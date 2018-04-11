package com.wanniu.game.request.onlineGift;

import java.io.IOException;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.OnlineGiftHandler.GetOnlineTimeResponse;
import pomelo.area.OnlineGiftHandler.OnlineGift;

/**
 * 获取在线时间
 * @author haog
 *
 */
@GClientEvent("area.onlineGiftHandler.getOnlineTimeRequest")
public class GetOnlineTimeHandler extends PomeloRequest {

	public PomeloResponse request() throws Exception {
		WNPlayer player = (WNPlayer) pak.getPlayer();
		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				GetOnlineTimeResponse.Builder res = GetOnlineTimeResponse.newBuilder();
				long onlineTime = player.onlineGiftManager.onlineData.sumTime;
			    res.setS2CCode(OK);
			    OnlineGift.Builder gift = OnlineGift.newBuilder();
			    gift.setOnlineTime((int)onlineTime);
				body.writeBytes(res.build().toByteArray());
			}
		};
	}
}
