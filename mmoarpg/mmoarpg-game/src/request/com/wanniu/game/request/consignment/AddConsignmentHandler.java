package com.wanniu.game.request.consignment;

import java.io.IOException;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.core.logfs.Out;
import com.wanniu.game.common.Const;
import com.wanniu.game.common.msg.ErrorResponse;
import com.wanniu.game.consignmentShop.ConsignmentLineService;
import com.wanniu.game.consignmentShop.ConsignmentUtil;
import com.wanniu.game.data.base.DItemEquipBase;
import com.wanniu.game.item.ItemConfig;
import com.wanniu.game.item.NormalItem;
import com.wanniu.game.player.GlobalConfig;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.poes.ConsignmentItemsPO;

import pomelo.area.ConsignmentLineHandler.AddConsignmentRequest;
import pomelo.area.ConsignmentLineHandler.AddConsignmentResponse;

/**
 * 拍卖行上架入口.
 *
 * @author 小流氓(zhoumingkai@qeng.cn)
 */
@GClientEvent("area.consignmentLineHandler.addConsignmentRequest")
public class AddConsignmentHandler extends PomeloRequest {

	@Override
	public PomeloResponse request() throws Exception {
		AddConsignmentRequest msg = AddConsignmentRequest.parseFrom(pak.getRemaingBytes());

		int index = msg.getC2SIndex();
		int num = msg.getC2SNumber();
		int salePrice = msg.getC2SPrice();
		String itemId = msg.getC2SId();// 重新上架，会有此值.
		WNPlayer player = (WNPlayer) pak.getPlayer();

		int needLevel = ConsignmentUtil.getConsignmentLevel();
		if (needLevel > player.getLevel()) {
			return new ErrorResponse(LangService.getValue("CONSIGNMENT_OPEN_LEVEL").replace("{needLevel}", String.valueOf(needLevel)));
		}

		if (num < 1) {
			return new ErrorResponse(LangService.getValue("CONSIGNMENT_ITEM_NUM_LESS_ONE"));
		}

		NormalItem item = player.getWnBag().getItem(index);
		if (item == null) {
			return new ErrorResponse(LangService.getValue("ITEM_NULL"));
		}
		
		
		if(!item.isEquip()&&item.prop.salePrice>0)
		{
			if (salePrice < item.prop.salePrice*GlobalConfig.Consignment_Advertisement_minPercent * num/100) {// CONSIGNMENT_PRICE_LESS=当前售价低于最低售价，请调整后重新上架
				return new ErrorResponse(LangService.getValue("CONSIGNMENT_PRICE_LESS"));
			}
	
			if (salePrice > item.prop.salePrice*GlobalConfig.Consignment_Advertisement_maxPercent * num/100) {// CONSIGNMENT_PRICE_MORE=当前售价超出最高售价，请调整后重新上架
				return new ErrorResponse(LangService.getValue("CONSIGNMENT_PRICE_MORE"));
			}
		}
		else
		{
			// 单价最低不能少于2个元宝...
			if (salePrice < 2 * num) {// CONSIGNMENT_PRICE_LESS=当前售价低于最低售价，请调整后重新上架
				return new ErrorResponse(LangService.getValue("CONSIGNMENT_PRICE_LESS"));
			}
	
			if (salePrice > 1000_0000) {// CONSIGNMENT_PRICE_MORE=当前售价超出最高售价，请调整后重新上架
				return new ErrorResponse(LangService.getValue("CONSIGNMENT_PRICE_MORE"));
			}
		}

		// 处理重新上架功能...
		if (StringUtils.isNotEmpty(itemId)) {
			return this.handleReshelf(player, itemId, salePrice);
		}

		

		// 绑定物品不能寄卖
		if (item.isBinding()) {
			return new ErrorResponse(LangService.getValue("CONSIGNMENT_BIND_CANNOT"));
		}

		// 不可寄卖物品
		if (!item.canAuction()) {
			return new ErrorResponse(LangService.getValue("CONSIGNMENT_ITEM_CANNOT_SALE"));
		}

		// 数量上限判定
		if (item.itemDb.groupCount < num) {
			return new ErrorResponse(LangService.getValue("ITEM_NOT_ENOUGH"));
		}

		// 寄卖上限
		List<ConsignmentItemsPO> myList = ConsignmentLineService.getInstance().findByPlayerId(player.getId());
		if (myList.size() >= ConsignmentUtil.sellNum(player)) {
			player.onFunctionGoTo(Const.FUNCTION_GOTO_TYPE.CONSIGNMENT, null, null, null);
			return new ErrorResponse();
		}

		// 手续费..
		int depositCoin = ConsignmentUtil.depositPrice(item.prop.price * num);
		if (!player.moneyManager.enoughGold(depositCoin)) {
			return new ErrorResponse(LangService.getValue("CONSIGNMENT_GOLD_NOT_ENOUGH"));
		}

		int lateMinutes = ConsignmentUtil.getLateMinutes(salePrice);
		ConsignmentItemsPO data = item.toJSON4ConsignmentLine(salePrice, player.getName(), player.getPro(), player.getId(), ConsignmentUtil.sellTime(), num, lateMinutes);
		boolean ret = ConsignmentLineService.getInstance().insert(data);
		if (!ret) {
			return new ErrorResponse(LangService.getValue("CommonUtil_ITEM_IS_IN_SALE"));
		}
		Out.info("上架拍卖道具 playerId=", player.getId(), ",itemId=", data.id, ",code=", data.db.code, ",count=", num, ",price=", salePrice);
		player.getWnBag().discardItemByPos(index, num, false, Const.GOODS_CHANGE_TYPE.CONSIGNMENT_ADD);
		// 扣除银币
		player.moneyManager.costGold(depositCoin, Const.GOODS_CHANGE_TYPE.CONSIGNMENT_ADD);

		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				AddConsignmentResponse.Builder res = AddConsignmentResponse.newBuilder();
				res.setS2CCode(OK);
				body.writeBytes(res.build().toByteArray());
			}
		};
	}

	private PomeloResponse handleReshelf(WNPlayer player, String itemId, int salePrice) {
		// 物品...
		ConsignmentItemsPO item = ConsignmentLineService.getInstance().getById(itemId);
		if (item == null) {
			return new ErrorResponse(LangService.getValue("ITEM_NULL"));
		}

		// 模板
		DItemEquipBase prop = ItemConfig.getInstance().getItemProp(item.db.code);
		if (prop == null) {
			return new ErrorResponse(LangService.getValue("ITEM_NULL"));
		}

		// 手续费..
		int depositCoin = ConsignmentUtil.depositPrice(prop.price * item.db.groupCount);
		if (!player.moneyManager.costGold(depositCoin, Const.GOODS_CHANGE_TYPE.CONSIGNMENT_ADD)) {
			return new ErrorResponse(LangService.getValue("CONSIGNMENT_GOLD_NOT_ENOUGH"));
		}
		Out.info("重新上架拍卖道具 playerId=", player.getId(), ",itemId=", item.id, ",code=", item.db.code, ",count=", item.groupCount, ",price=", salePrice);

		int lateMinutes = ConsignmentUtil.getLateMinutes(salePrice);
		item.consignmentPrice = salePrice;
		item.consignmentTime = System.currentTimeMillis() + ConsignmentUtil.sellTime() + lateMinutes * Const.Time.Minute.getValue();
		item.lateMinutes = lateMinutes;

		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				AddConsignmentResponse.Builder res = AddConsignmentResponse.newBuilder();
				res.setS2CCode(OK);
				body.writeBytes(res.build().toByteArray());
			}
		};
	}
}