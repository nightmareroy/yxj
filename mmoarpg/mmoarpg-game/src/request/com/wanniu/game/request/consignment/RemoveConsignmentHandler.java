package com.wanniu.game.request.consignment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.TreeMap;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.core.logfs.Out;
import com.wanniu.game.common.Const.GOODS_CHANGE_TYPE;
import com.wanniu.game.common.msg.MessageUtil;
import com.wanniu.game.consignmentShop.ConsignmentLineService;
import com.wanniu.game.data.base.DItemEquipBase;
import com.wanniu.game.item.ItemUtil;
import com.wanniu.game.mail.MailUtil;
import com.wanniu.game.mail.SysMailConst;
import com.wanniu.game.mail.WNMail;
import com.wanniu.game.mail.data.MailSysData;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.poes.ConsignmentItemsPO;

import pomelo.area.ConsignmentLineHandler.RemoveConsignmentRequest;
import pomelo.area.ConsignmentLineHandler.RemoveConsignmentResponse;

@GClientEvent("area.consignmentLineHandler.removeConsignmentRequest")
public class RemoveConsignmentHandler extends PomeloRequest {

	@Override
	public PomeloResponse request() throws Exception {
		WNPlayer player = (WNPlayer) pak.getPlayer();
		RemoveConsignmentRequest msg = RemoveConsignmentRequest.parseFrom(pak.getRemaingBytes());
		String id = msg.getC2SId();
		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				RemoveConsignmentResponse.Builder res = RemoveConsignmentResponse.newBuilder();
				ConsignmentItemsPO item = ConsignmentLineService.getInstance().getById(id);
				if (item == null || !player.getId().equals(item.consignmentPlayerId)) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("ITEM_NULL"));
					body.writeBytes(res.build().toByteArray());
					return;
				}

				boolean ret = ConsignmentLineService.getInstance().remove(id);
				if (!ret) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("CONSIGNMENT_ITEM_CANNOT_BUY"));
					body.writeBytes(res.build().toByteArray());
					return;
				}

				Out.info("回收拍卖道具 playerId=", player.getId(), ",itemId=", id, ",code=", item.db.code, ",count=", item.groupCount);

				DItemEquipBase itemBase = ItemUtil.getPropByCode(item.db.code);
				String itemName = MessageUtil.itemColorName(itemBase.qcolor, itemBase.name);
				MailSysData mailData = new MailSysData(SysMailConst.CONSIGNMENT_WITHDRAW);
				mailData.replace = new TreeMap<>();
				mailData.replace.put("storeItem", itemName);
				mailData.entityItems = new ArrayList<>();
				mailData.entityItems.add(item.db);
				WNMail mail = MailUtil.getInstance().createMail(mailData, player.getId(), GOODS_CHANGE_TYPE.CONSIGNMENT_REMOVE);
				MailUtil.getInstance().sendMail(player, mail);

				res.setS2CCode(OK);
				body.writeBytes(res.build().toByteArray());
			}
		};
	}
}
