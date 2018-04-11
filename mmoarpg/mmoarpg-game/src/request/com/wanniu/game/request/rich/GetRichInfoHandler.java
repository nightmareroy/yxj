package com.wanniu.game.request.rich;

import java.io.IOException;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.rich.RichManager;


import pomelo.rich.RichHandler.GetRichInfoResponse;

/**
 * 获取大富翁详情
 * 
 * @author liyue
 */
@GClientEvent("rich.richHandler.getRichInfoRequest")
public class GetRichInfoHandler extends PomeloRequest {

	@Override
	public PomeloResponse request() throws Exception {
		WNPlayer player = (WNPlayer) pak.getPlayer();
		GetRichInfoResponse.Builder res=player.richManager.getRichInfo(player.getId());
		
		
		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				body.writeBytes(res.build().toByteArray());

			}
		};
	}

}
