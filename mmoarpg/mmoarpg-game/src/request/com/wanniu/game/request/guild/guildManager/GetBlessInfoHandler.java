package com.wanniu.game.request.guild.guildManager;

import java.io.IOException;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.guild.guildBless.GuildBlessCenter;
import com.wanniu.game.player.WNPlayer;

import io.netty.util.internal.StringUtil;
import pomelo.guild.GuildManagerHandler.GetBlessInfoResponse;
import pomelo.guild.GuildManagerHandler.GuildBlessInfo;

@GClientEvent("guild.guildManagerHandler.getBlessInfoRequest")
public class GetBlessInfoHandler extends PomeloRequest {

	public PomeloResponse request() throws Exception {
		WNPlayer player = (WNPlayer) pak.getPlayer();
		// GetBlessInfoRequest req =
		// GetBlessInfoRequest.parseFrom(pak.getRemaingBytes());
		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				GetBlessInfoResponse.Builder res = GetBlessInfoResponse.newBuilder();

				GuildBlessCenter blessManager = GuildBlessCenter.getInstance();
				if (StringUtil.isNullOrEmpty(player.getId()) || null == blessManager) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("SOMETHING_ERR"));
					body.writeBytes(res.build().toByteArray());
					return;
				}

				GuildBlessInfo blessData = blessManager.getBlessInfoByPlayerId(player.getId());
				res.setS2CCode(OK);
				res.setS2CBlessInfo(blessData);
				// Out.error("GuildBlessInfo:->>>>",res);
				body.writeBytes(res.build().toByteArray());
			}
		};
	}
}
