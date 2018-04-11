package com.wanniu.game.request.map;

import java.io.IOException;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.area.Area;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.MapHandler.GetAliveMonsterLineInfoResponse;

/**
 * @author Yangzz
 *
 */
@GClientEvent("area.mapHandler.getAliveMonsterLineInfoRequest")
public class GetAliveMonsterLineInfoHandler extends PomeloRequest {

	public PomeloResponse request() throws Exception {

		WNPlayer player = (WNPlayer) pak.getPlayer();
//		GetAliveMonsterLineInfoRequest req = GetAliveMonsterLineInfoRequest.parseFrom(pak.getRemaingBytes());
		Area area = player.getArea();
		
		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				GetAliveMonsterLineInfoResponse.Builder res = GetAliveMonsterLineInfoResponse.newBuilder();
				res.setS2CCode(OK);
				res.addAllS2CMonsterInfos(area.aliveBoss);
				body.writeBytes(res.build().toByteArray());
				return;

			}
		};
	}
}