package com.wanniu.game.request.guild.guildBless;

import java.io.IOException;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.GuildBlessHandler.GetMyBlessInfoResponse;
import pomelo.area.GuildBlessHandler.MyBlessInfo;

@GClientEvent("area.guildBlessHandler.getMyBlessInfoRequest")
public class GetMyBlessInfoHandler extends PomeloRequest {

	public PomeloResponse request() throws Exception {
		WNPlayer player = (WNPlayer) pak.getPlayer();
//		GetMyBlessInfoRequest req = GetMyBlessInfoRequest.parseFrom(pak.getRemaingBytes());
		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				GetMyBlessInfoResponse.Builder res = GetMyBlessInfoResponse.newBuilder();

					MyBlessInfo data = player.guildManager.getMyBlessInfo();
					 res.setS2CCode(OK);
					 res.setS2CBlessInfo(data);
				     body.writeBytes(res.build().toByteArray());
				     //Out.error("XXXXXXXXMyBlessInfo:->>>>>",res);
			}
		};
	}
}