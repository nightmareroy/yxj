package com.wanniu.game.request.fightLevel;

import java.io.IOException;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.FightLevelHandler.GetBossInfoResponse;

/**
 * Boss信息
 * 
 * @author Yangzz
 *
 */
@GClientEvent("area.fightLevelHandler.getBossInfoRequest")
public class GetBossInfoHandler extends FightLevelLine {

	public PomeloResponse request(WNPlayer player) throws Exception {

//		GetBossInfoRequest req = GetBossInfoRequest.parseFrom(pak.getRemaingBytes());

		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				GetBossInfoResponse.Builder res = GetBossInfoResponse.newBuilder();

//				Area area = player.getArea();
//
//				FightLevelManager fightLevelManager = player.fightLevelManager;


				res.setS2CCode(OK);
//				List<BossInfo> data = fightLevelManager.getBossInfo(player, area.areaId);
//				res.addAllS2CBossInfos(data);
				body.writeBytes(res.build().toByteArray());

			}
		};
	}
}