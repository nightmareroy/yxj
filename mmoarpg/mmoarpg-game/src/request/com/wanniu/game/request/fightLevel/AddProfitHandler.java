package com.wanniu.game.request.fightLevel;

import java.io.IOException;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.common.Const;
import com.wanniu.game.common.Const.ITEM_CODE;
import com.wanniu.game.common.msg.ErrorResponse;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.FightLevelHandler.AddProfitRequest;
import pomelo.area.FightLevelHandler.AddProfitResponse;

/**
 * 增加副本收益次数.
 *
 * @author 小流氓(176543888@qq.com)
 */
@GClientEvent("area.fightLevelHandler.addProfitRequest")
public class AddProfitHandler extends FightLevelLine {

	public PomeloResponse request(WNPlayer player) throws Exception {
		AddProfitRequest request = AddProfitRequest.parseFrom(pak.getRemaingBytes());
		final int mapId = request.getMapId();

		// 扣材料
		if (!player.bag.discardItem(ITEM_CODE.DUNGEONPROFIT.value, 1, Const.GOODS_CHANGE_TYPE.use)) {
			return new ErrorResponse(LangService.getValue("ITEM_NOT_ENOUGH"));
		}

		// 添加收益次数
		player.fightLevelManager.addPrifit(mapId);

		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				AddProfitResponse.Builder res = AddProfitResponse.newBuilder();
				res.setS2CCode(OK);
				body.writeBytes(res.build().toByteArray());
			}
		};
	}
}