package com.wanniu.game.request.achievement;

import java.io.IOException;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.common.Const;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.AchievementHandler.AchievementGetTypeElementRequest;
import pomelo.area.AchievementHandler.AchievementGetTypeElementResponse;

/**
 * 请求成就项对应的成就
 * 
 * @author wfy
 *
 */
@GClientEvent("area.achievementHandler.achievementGetTypeElementRequest")
public class AchievementGetTypeElementHandler extends PomeloRequest {

	public PomeloResponse request() throws Exception {
		AchievementGetTypeElementRequest req = AchievementGetTypeElementRequest.parseFrom(pak.getRemaingBytes());
		WNPlayer player = (WNPlayer) pak.getPlayer();
		int id = req.getC2SId(); // 章节ID
		return new PomeloResponse() {

			@Override
			protected void write() throws IOException {
				AchievementGetTypeElementResponse.Builder res = AchievementGetTypeElementResponse.newBuilder();
				if (!player.functionOpenManager.isOpen(Const.FunctionType.ACHIEVEMENT.getValue())) {
					res.setS2CCode(Const.CODE.FAIL);
					res.setS2CMsg(LangService.getValue("FUNC_SET_PLAYED_NOT_OPEN"));
					body.writeBytes(res.build().toByteArray());
					return;
				}
				player.achievementManager.toJson4PayloadbyTypeId(id, res);
				res.setS2CCode(Const.CODE.OK);
				body.writeBytes(res.build().toByteArray());
			}

		};
	}
}