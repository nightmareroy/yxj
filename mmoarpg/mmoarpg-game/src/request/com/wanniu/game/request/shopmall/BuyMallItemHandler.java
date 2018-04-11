package com.wanniu.game.request.shopmall;

import java.io.IOException;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.entity.GPlayer;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.GWorld;
import com.wanniu.game.common.msg.ErrorResponse;
import com.wanniu.game.data.ext.ShopMallItemsExt;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.shopMall.ShopMallConfig;
import com.wanniu.game.shopMall.ShopMallManager.ShopMallResult;

import pomelo.area.ShopMallHandler.BuyMallItemRequest;
import pomelo.area.ShopMallHandler.BuyMallItemResponse;

@GClientEvent("area.shopMallHandler.buyMallItemRequest")
public class BuyMallItemHandler extends PomeloRequest {

	@Override
	public PomeloResponse request() throws Exception {
		GPlayer player = pak.getPlayer();
		BuyMallItemRequest req = BuyMallItemRequest.parseFrom(pak.getRemaingBytes());
		String itemId = req.getC2SItemId();
		int count = req.getC2SCount();
		String playerId = req.getC2SPlayerId();
		int bDiamond = req.getC2SBDiamond();

		if (itemId == null || count <= 0 || playerId == null || count > 9999) {
			return new ErrorResponse(LangService.getValue("PARAM_ERROR"));
		}

		// 199测试商城只在debug模式下才能购买
		ShopMallItemsExt mallItemProp = ShopMallConfig.getInstance().fingShowMallItemByID(itemId);
		if (!GWorld.DEBUG && mallItemProp != null && mallItemProp.itemType == 199) {
			return new ErrorResponse(LangService.getValue("PARAM_ERROR"));
		}

		WNPlayer wPlayer = (WNPlayer) player;
		ShopMallResult result = wPlayer.shopMallManager.buyMallItem(itemId, count, playerId, bDiamond);

		BuyMallItemResponse.Builder res = BuyMallItemResponse.newBuilder();

		if (result.result) {
			res.setTotalNum(result.totalNum);
			res.setS2CCode(OK);
		} else {
			if (!result.msg.equals(LangService.getValue("BAG_NOT_ENOUGH_POS"))) {

				return new ErrorResponse(result.msg);
			} else {
				return new ErrorResponse();
			}
		}

		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				body.writeBytes(res.build().toByteArray());
			}
		};
	}
}
