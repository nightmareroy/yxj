package com.wanniu.game.request.consignment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.core.logfs.Out;
import com.wanniu.game.common.Const;
import com.wanniu.game.common.Const.BiLogType;
import com.wanniu.game.common.Const.GOODS_CHANGE_TYPE;
import com.wanniu.game.common.msg.ErrorResponse;
import com.wanniu.game.common.msg.MessageUtil;
import com.wanniu.game.consignmentShop.ConsignmentLineService;
import com.wanniu.game.consignmentShop.ConsignmentUtil;
import com.wanniu.game.data.base.DEquipBase;
import com.wanniu.game.data.base.DItemEquipBase;
import com.wanniu.game.item.ItemConfig;
import com.wanniu.game.item.ItemUtil;
import com.wanniu.game.item.po.ItemSpeData;
import com.wanniu.game.mail.MailUtil;
import com.wanniu.game.mail.SysMailConst;
import com.wanniu.game.mail.WNMail;
import com.wanniu.game.mail.data.MailData.Attachment;
import com.wanniu.game.mail.data.MailSysData;
import com.wanniu.game.player.BILogService;
import com.wanniu.game.player.PlayerUtil;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.player.bi.LogReportService;
import com.wanniu.game.poes.ConsignmentItemsPO;

import pomelo.Common.KeyValueStruct;
import pomelo.area.ConsignmentLineHandler.BuyConsignmentRequest;
import pomelo.area.ConsignmentLineHandler.BuyConsignmentResponse;

/**
 * 购买拍卖道具.
 *
 * @author 小流氓(176543888@qq.com)
 */
@GClientEvent("area.consignmentLineHandler.buyConsignmentRequest")
public class BuyConsignmentHandler extends PomeloRequest {

	@Override
	public PomeloResponse request() throws Exception {
		WNPlayer player = (WNPlayer) pak.getPlayer();
		BuyConsignmentRequest msg = BuyConsignmentRequest.parseFrom(pak.getRemaingBytes());
		String id = msg.getC2SId();
		int globalZone = msg.getC2SGlobal();

		// 等级判定
		int needLevel = ConsignmentUtil.getConsignmentLevel();
		if (needLevel > player.getLevel()) {
			return new ErrorResponse(LangService.getValue("CONSIGNMENT_OPEN_LEVEL").replace("{needLevel}", String.valueOf(needLevel)));
		}

		ConsignmentItemsPO item = ConsignmentLineService.getInstance().getById(id);
		if (item == null) {
			return new ErrorResponse(LangService.getValue("CONSIGNMENT_ITEM_CANNOT_BUY"));
		}

		// 不能购买自己的道具
		if (item.consignmentPlayerId.equals(player.getId())) {
			return new ErrorResponse(LangService.getValue("CONSIGNMENT_CANNOT_BUY_SELF_ITEM"));
		}

		// 判定元宝
		if (!player.moneyManager.enoughDiamond(item.consignmentPrice)) {
			return new ErrorResponse(LangService.getValue("DIAMAND_NOT_ENOUGH"));
		}

		// 变化道具
		List<KeyValueStruct> changeItems = new ArrayList<>();
		KeyValueStruct.Builder items = KeyValueStruct.newBuilder();
		items.setKey(item.db.code);
		items.setValue(String.valueOf(item.db.groupCount));
		changeItems.add(items.build());
		player.moneyManager.costDiamond(item.consignmentPrice, Const.GOODS_CHANGE_TYPE.CONSIGNMENT_BUY, changeItems);

		DItemEquipBase itemBase = ItemUtil.getPropByCode(item.db.code);
		String itemName = MessageUtil.itemColorName(itemBase.qcolor, itemBase.name);

		{// 出售成功
			if (!id.equals(ConsignmentUtil.sysItemId)) {
				Out.info("拍卖道具出售成功 playerId=", item.consignmentPlayerId, ",name=", item.consignmentPlayerName, ",itemId=", item.id, ",code=", item.db.code, ",count=", item.db.groupCount, ",price=", item.consignmentPrice);
				// 寄卖成功的邮件
				int salePrice = item.consignmentPrice - ConsignmentUtil.commissionPrice(globalZone, item.consignmentPrice);
				if (salePrice < 0) {
					salePrice = 0;
				}
				MailSysData mailSaleData = new MailSysData(SysMailConst.CONSIGNMENT_SALE);
				mailSaleData.attachments = new ArrayList<>();
				Attachment att = new Attachment();
				att.itemCode = "diamond";
				att.itemNum = salePrice;
				mailSaleData.attachments.add(att);
				mailSaleData.replace = new HashMap<>();
				mailSaleData.replace.put("storeItem", itemName);
				mailSaleData.replace.put("price", String.valueOf(item.consignmentPrice));
				mailSaleData.replace.put("realprice", String.valueOf(salePrice));
				MailUtil.getInstance().sendMailToOnePlayer(item.consignmentPlayerId, mailSaleData, GOODS_CHANGE_TYPE.CONSIGNMENT_SELL);

				ConsignmentLineService.getInstance().remove(item.id);
			} else {
				player.consignmentManager.signBuyFirstConsignItem();
			}
		}

		{// 购买成功的邮件
			Out.info("拍卖道具购买成功 playerId=", player.getId(), ",name=", player.getName(), ",itemId=", item.id, ",code=", item.db.code, ",count=", item.db.groupCount, ",price=", item.consignmentPrice);
			MailSysData mailData = new MailSysData(SysMailConst.CONSIGNMENT_BUY);
			mailData.replace = new HashMap<>();
			mailData.replace.put("storeItem", itemName);
			mailData.entityItems = new ArrayList<>();
			mailData.entityItems.add(item.db);

			WNMail mail = MailUtil.getInstance().createMail(mailData, player.getId(), GOODS_CHANGE_TYPE.CONSIGNMENT_BUY);
			MailUtil.getInstance().sendMail(player, mail);
			// FIXME 中文.........
			PlayerUtil.sendSysMessageToPlayer("购买成功，请到邮箱中查收！", player.getId(), null);
		}

		LogReportService.getInstance().ansycReportConsignment(player, item.consignmentPlayerId, item.consignmentPlayerName, item.db.code, item.db.groupCount);

		return new PomeloResponse() {

			@Override
			protected void write() throws IOException {
				BuyConsignmentResponse.Builder res = BuyConsignmentResponse.newBuilder();
				res.setS2CCode(OK);
				body.writeBytes(res.build().toByteArray());
			}
		};
	}
}