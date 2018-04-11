package com.wanniu.game.request.fightLevel;

import java.io.IOException;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.core.logfs.Out;
import com.wanniu.game.area.Area;
import com.wanniu.game.common.msg.ErrorResponse;
import com.wanniu.game.fightLevel.FightLevelManager;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.FightLevelHandler.EnterDungeonRequest;
import pomelo.area.FightLevelHandler.EnterDungeonResponse;

/**
 * 进入副本
 * @author agui
 */
@GClientEvent("area.fightLevelHandler.enterDungeonRequest")
public class EnterDungeonHandler extends FightLevelLine {

	public PomeloResponse request(WNPlayer player) throws Exception {

		EnterDungeonRequest req = EnterDungeonRequest.parseFrom(pak.getRemaingBytes());

		int dungeonId = req.getC2SDungeonId();
		Out.debug(this.getClass().getName(), " : ", dungeonId);
		
		Area area = player.getArea();
		if (area != null) {
			if (dungeonId == area.areaId) {
				return new ErrorResponse(LangService.getValue("MAP_IN_MPA"));
			}
		}
		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				EnterDungeonResponse.Builder res = EnterDungeonResponse.newBuilder();

				FightLevelManager fightLevelManager = player.fightLevelManager;

				String data = fightLevelManager.enterDungeonReq(player, dungeonId);
				if (data == null) {
					res.setS2CCode(OK);
					body.writeBytes(res.build().toByteArray());
				} else {
					res.setS2CCode(FAIL);
					res.setS2CMsg(data);
					body.writeBytes(res.build().toByteArray());
				}

			}
		};
	}

}