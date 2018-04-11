package com.wanniu.game.request.prepaid;

import java.io.IOException;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.PrepaidHandler.PrepaidFirstResponse;

/**
 * 获取首充奖励信息
 * 
 * @author lxm
 *
 */
@GClientEvent("area.prepaidHandler.prepaidFirstAwardRequest")
public class PrepaidFirstAwardHandler extends PomeloRequest {

	@Override
	public PomeloResponse request() throws Exception {
		WNPlayer player = (WNPlayer) pak.getPlayer();
		return new PomeloResponse() {

			@Override
			protected void write() throws IOException {
				PrepaidFirstResponse res = player.prepaidManager.getPrepaidFirstAward();
				body.writeBytes(res.toByteArray());
			}
		};
	}

}
