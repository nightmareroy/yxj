package com.wanniu.game.request.bag;

import java.io.IOException;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.bag.BagUtil;
import com.wanniu.game.bag.WNBag;
import com.wanniu.game.common.Const;
import com.wanniu.game.common.Const.GOODS_CHANGE_TYPE;
import com.wanniu.game.common.msg.ErrorResponse;
import com.wanniu.game.item.ItemUtil;
import com.wanniu.game.item.NormalItem;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.BagHandler.TransferItemRequest;
import pomelo.area.BagHandler.TransferItemResponse;

/**
 * 转移物品
 * 
 * @author Yangzz
 *
 */
@GClientEvent("area.bagHandler.transferItemRequest")
public class TransferItemHandler extends PomeloRequest {

	public PomeloResponse request() throws Exception {

		WNPlayer player = (WNPlayer) pak.getPlayer();

		TransferItemRequest req = TransferItemRequest.parseFrom(pak.getRemaingBytes());
		int fromType = req.getC2SFromType();
		int fromIndex = req.getC2SFromIndex();
		int toType = req.getC2SToType();
		int num = req.getC2SNum();

		if (fromType == toType) {
			return new ErrorResponse(LangService.getValue("PARAM_ERROR"));
		}

		WNBag fromStore = BagUtil.getStoreByType(player, fromType);
		WNBag toStore = BagUtil.getStoreByType(player, toType);
		if (fromStore == null || toStore == null) {
			return new ErrorResponse(LangService.getValue("PARAM_ERROR"));
		}

		NormalItem item = fromStore.getItem(fromIndex);
		if (item == null) {
			return new ErrorResponse(LangService.getValue("ITEM_NULL"));
		}

		if (toType == Const.BAG_TYPE.WAREHOUSE.getValue()) {
			if (!item.canDepotRole()) {
				return new ErrorResponse(LangService.getValue("ITEM_NOT_STORE"));
			}
		}

		if (item.itemDb.groupCount < num || num <= 0) {
			return new ErrorResponse(LangService.getValue("PARAM_ERROR"));
		}

		Const.ForceType forceType = item.isBinding() ? Const.ForceType.BIND : Const.ForceType.UN_BIND;
		if (!toStore.testAddCodeItem(item.itemDb.code, num, forceType, true)) {
			String str = "";
			if (toType == Const.BAG_TYPE.BAG.getValue()) {
				str = LangService.getValue("BAG_NOT_ENOUGH_POS");
			} else if (toType == Const.BAG_TYPE.WAREHOUSE.getValue()) {
				str = LangService.getValue("WAREHOUSE_SPACE_NOT_ENOUGH");
			}
			return new ErrorResponse(LangService.getValue(str));
		}

		if (item.itemDb.groupCount != num) {
			// 要新建物品
			fromStore.discardItemByPos(fromIndex, num, false, GOODS_CHANGE_TYPE.move);
			NormalItem newItem = ItemUtil.createItemsByItemCode(item.itemDb.code, num).get(0);
			newItem.itemDb.isNew = 0;
			newItem.setBind(item.getBind());
			toStore.addEntityItem(newItem, GOODS_CHANGE_TYPE.move, null, true, true);
		} else {
			item.itemDb.isNew = 0;
			fromStore.removeItemByPos(fromIndex, false, GOODS_CHANGE_TYPE.move);
			toStore.addEntityItem(item, GOODS_CHANGE_TYPE.move, null, true, true);
		}

		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				TransferItemResponse.Builder res = TransferItemResponse.newBuilder();
				res.setS2CCode(OK);
				body.writeBytes(res.build().toByteArray());
			}
		};
	}
}