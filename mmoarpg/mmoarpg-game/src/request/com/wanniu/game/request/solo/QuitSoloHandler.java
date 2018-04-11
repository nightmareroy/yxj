package com.wanniu.game.request.solo;

import java.io.IOException;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.common.Const;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.SoloHandler.QuitSoloResponse;

/**
 * 单挑信息请求
 * 
 * @author wfy
 *
 */
@GClientEvent("area.soloHandler.quitSoloRequest")
public class QuitSoloHandler extends SoloRequestFilter {

	public PomeloResponse request(WNPlayer player) throws Exception {
		
		return new PomeloResponse() {
			
			@Override
			protected void write() throws IOException {

				QuitSoloResponse.Builder res = QuitSoloResponse.newBuilder();
			    player.soloManager.handleQuitSolo();
			    
		        res.setS2CCode(Const.CODE.OK);
				body.writeBytes(res.build().toByteArray());
			}
			
		};
	}
}