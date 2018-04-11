package com.wanniu.game.request.revelry;

import java.io.IOException;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.core.logfs.Out;
import com.wanniu.game.common.Const.ForceType;
import com.wanniu.game.common.Const.GOODS_CHANGE_TYPE;
import com.wanniu.game.common.msg.ErrorResponse;
import com.wanniu.game.data.GameData;
import com.wanniu.game.data.KingCO;
import com.wanniu.game.player.WNPlayer;

import pomelo.revelry.ActivityRevelryHandler.RevelryExchangeRequest;
import pomelo.revelry.ActivityRevelryHandler.RevelryExchangeResponse;

/**
 * 冲榜兑换协议.
 *
 * @author 小流氓(176543888@qq.com)
 */
@GClientEvent("revelry.activityRevelryHandler.revelryExchangeRequest")
public class RevelryExchangeHandler extends PomeloRequest {

	public PomeloResponse request() throws Exception {

		RevelryExchangeRequest req = RevelryExchangeRequest.parseFrom(pak.getRemaingBytes());

		int tabId = req.getId();
		KingCO template = GameData.Kings.get(tabId);
		if (template == null) {
			return new ErrorResponse(LangService.getValue("PARAM_ERROR"));
		}

		int count = req.getNum();
		if (count <= 0 || count > 9999) {
			return new ErrorResponse(LangService.getValue("PARAM_ERROR"));
		}

		WNPlayer player = (WNPlayer) pak.getPlayer();

		// 需求材料是否够
		int needNum = template.num2 * count;
		if (!player.getWnBag().isItemNumEnough(template.item2code, needNum)) {
			return new ErrorResponse(LangService.getValue("NOT_ENOUGH_ITEM"));
		}

		// 背包空间
		int addNum = template.num1 * count;
		if (!player.getWnBag().testAddCodeItem(template.item1code, addNum)) {
			return new ErrorResponse(LangService.getValue("BAG_FULL"));
		}

		Out.info("冲榜兑换物品，playerId=", player.getId(), ",name=", player.getName(), ",tabId=", tabId, ",num=", count);
		// 扣掉材料
		player.getWnBag().discardItem(template.item2code, needNum, GOODS_CHANGE_TYPE.REVELRY_EXCHANGE);
		// 发送奖励
		player.getWnBag().addCodeItem(template.item1code, addNum, ForceType.DEFAULT, GOODS_CHANGE_TYPE.REVELRY_EXCHANGE);

		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				RevelryExchangeResponse.Builder result = RevelryExchangeResponse.newBuilder();
				result.setS2CCode(OK);
				body.writeBytes(result.build().toByteArray());
			}
		};
	}
}