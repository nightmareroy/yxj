package com.wanniu.game.request.fightLevel;

import java.io.IOException;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.FightLevelHandler.PalaceListResponse;

/**
 * 地宫列表
 * 
 * @author Yangzz
 *
 */
@GClientEvent("area.fightLevelHandler.palaceListRequest")
public class PalaceListHandler extends FightLevelLine {

	public PomeloResponse request(WNPlayer player) throws Exception {

//		PalaceListRequest req = PalaceListRequest.parseFrom(pak.getRemaingBytes());

		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				PalaceListResponse.Builder res = PalaceListResponse.newBuilder();

//				FightLevelManager fightLevelManager = player.fightLevelManager;


				res.setS2CCode(OK);
//				List<PalaceInfo> data = fightLevelManager.palaceList(player, req.getS2CType());
//				res.addAllS2CPalaceInfos(data);
				body.writeBytes(res.build().toByteArray());

			}
		};
	}
}