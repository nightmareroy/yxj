package com.wanniu.game.request.solo;

import java.io.IOException;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.SoloHandler.SoloInfoResponse;

/**
 * 单挑信息请求
 * 
 * @author wfy
 *
 */
@GClientEvent("area.soloHandler.soloInfoRequest")
public class SoloInfoHandler extends SoloRequestFilter {

	public PomeloResponse request(WNPlayer player) throws Exception {
		
		
		return new PomeloResponse() {
			
			@Override
			protected void write() throws IOException {
				SoloInfoResponse.Builder res = SoloInfoResponse.newBuilder();
			    player.soloManager.handleSoloInfo(res);
				body.writeBytes(res.build().toByteArray());
			}
			
		};
	}
}