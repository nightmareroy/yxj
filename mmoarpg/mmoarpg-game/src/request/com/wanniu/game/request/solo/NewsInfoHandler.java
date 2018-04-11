package com.wanniu.game.request.solo;

import java.io.IOException;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.SoloHandler.NewsInfoResponse;

/**
 * 传言信息请求
 * 
 * @author wfy
 *
 */
@GClientEvent("area.soloHandler.newsInfoRequest")
public class NewsInfoHandler extends SoloRequestFilter {

	public PomeloResponse request(WNPlayer player) throws Exception {
		
		return new PomeloResponse() {
			
			@Override
			protected void write() throws IOException {
				NewsInfoResponse.Builder res = NewsInfoResponse.newBuilder();
			    player.soloManager.handleNewsInfo(res);
				body.writeBytes(res.build().toByteArray());
			}
			
		};
	}
}