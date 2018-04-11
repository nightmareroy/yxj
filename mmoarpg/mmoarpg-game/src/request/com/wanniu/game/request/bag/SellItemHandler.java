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
import com.wanniu.game.item.ItemUtil;
import com.wanniu.game.item.NormalItem;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.BagHandler.SellItemRequest;
import pomelo.area.BagHandler.SellItemResponse;

/**
 * 出售物品
 * 
 * @author Yangzz
 *
 */
@GClientEvent("area.bagHandler.sellItemRequest")
public class SellItemHandler extends PomeloRequest {

	public PomeloResponse request() throws Exception {

		WNPlayer player = (WNPlayer) pak.getPlayer();
		WNBag wnBag = player.getWnBag();

		SellItemRequest req = SellItemRequest.parseFrom(pak.getRemaingBytes());
		int index = req.getC2SGridIndex();
		int num = req.getC2SNum();

		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				SellItemResponse.Builder res = SellItemResponse.newBuilder();
				// wnBag.sellItem(res, index, num);

				NormalItem item = player.bag.getItem(index);
				if (item == null) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("ITEM_NULL"));
					return;
				}

				if (!item.canSell()) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("ITEM_CAN_NOT_SELL"));
					return;
				}

				if (item.itemDb.groupCount < num || num <= 0) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("PARAM_ERROR"));
					return;
				}

				int price = BagUtil.getSellPrice(item.price(), num);

				int delNum = num;

				// 移动物品
				if (item.itemDb.groupCount != num) {
					// 要新建物品
					player.bag.discardItemByPos(index, num, GOODS_CHANGE_TYPE.clear_when_logout);
					NormalItem newItem = ItemUtil.createItemsByItemCode(item.itemDb.code, num).get(0);
					newItem.itemDb.isNew = 0;
					newItem.setBind(item.getBind());
					player.recycle.addEntityItem(newItem, GOODS_CHANGE_TYPE.clear_when_logout, null, true, true);
				} else {
					item.itemDb.isNew = 0;
					delNum = item.itemDb.groupCount;
					player.bag.removeItemByPos(index, true, Const.GOODS_CHANGE_TYPE.clear_when_logout);
					player.recycle.addEntityItem(item, GOODS_CHANGE_TYPE.clear_when_logout, null, true, true);
				}

				player.moneyManager.addGold(price, Const.GOODS_CHANGE_TYPE.clear_when_logout);
				if (item.prop.itemSecondType == Const.ItemSecondType.gem.getValue()) {
					// player.biServerManager.gemOperation(4, item.getName(),
					// item.getName(), delNum);TODO
				}
				res.setS2CCode(OK);
				body.writeBytes(res.build().toByteArray());
			}
		};
	}
}
