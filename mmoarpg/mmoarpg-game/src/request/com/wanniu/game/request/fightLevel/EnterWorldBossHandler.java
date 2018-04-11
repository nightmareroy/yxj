package com.wanniu.game.request.fightLevel;

import java.io.IOException;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.area.Area;
import com.wanniu.game.area.AreaUtil;
import com.wanniu.game.player.GlobalConfig;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.FightLevelHandler.EnterWorldBossRequest;
import pomelo.area.FightLevelHandler.EnterWorldBossResponse;

/**
 * 进入世界首领
 * 
 * @author Yangzz
 *
 */
@GClientEvent("area.fightLevelHandler.enterWorldBossRequest")
public class EnterWorldBossHandler extends FightLevelLine {

	public PomeloResponse request(WNPlayer player) throws Exception {

		EnterWorldBossRequest req = EnterWorldBossRequest.parseFrom(pak.getRemaingBytes());
		int areaId = req.getS2CAreaId();

		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				EnterWorldBossResponse.Builder res = EnterWorldBossResponse.newBuilder();

				Area area = player.getArea();

				if (area.areaId == areaId) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("MAP_IN_MPA"));
					body.writeBytes(res.build().toByteArray());
					return;
				}
				if (AreaUtil.needCreateArea(area.areaId)) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("DUNGEON_ALREAD_IN_DUNGEON"));
					body.writeBytes(res.build().toByteArray());
					return;
				}

				if (areaId == GlobalConfig.World_Boss_NewScene) {

					int newMapLevel = GlobalConfig.World_Boss_NewScene_Close;

					int questId = GlobalConfig.World_Boss_NweScene_Quest;

					if (player.player.level >= newMapLevel && player.taskManager.isCompleteTaskByID(questId)) {

						res.setS2CCode(FAIL);
						res.setS2CMsg(LangService.getValue("SOMETHING_ERR"));
						body.writeBytes(res.build().toByteArray());
					}
				}

				Area newArea = AreaUtil.enterArea(player, areaId);
				if (newArea != null) {
					res.setS2CCode(OK);
				} else {
					res.setS2CCode(FAIL);
					res.setS2CMsg("");
					body.writeBytes(res.build().toByteArray());
				}

				body.writeBytes(res.build().toByteArray());

			}
		};
	}
}