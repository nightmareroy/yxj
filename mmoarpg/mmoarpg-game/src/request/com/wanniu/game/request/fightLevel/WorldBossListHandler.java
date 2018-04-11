package com.wanniu.game.request.fightLevel;

import java.io.IOException;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.FightLevelHandler.WorldBossListResponse;

/**
 * 世界首领列表
 * 
 * @author agui
 */
@GClientEvent("area.fightLevelHandler.worldBossListRequest")
public class WorldBossListHandler extends FightLevelLine {

	public PomeloResponse request(WNPlayer player) throws Exception {

		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				WorldBossListResponse.Builder res = WorldBossListResponse.newBuilder();

//				FightLevelManager fightLevelManager = player.fightLevelManager;


				res.setS2CCode(OK);
//				List<MapInfo> data = fightLevelManager.worldBossList(player);
//				res.addAllMapInfos(data);
				body.writeBytes(res.build().toByteArray());

			}
		};
	}
}