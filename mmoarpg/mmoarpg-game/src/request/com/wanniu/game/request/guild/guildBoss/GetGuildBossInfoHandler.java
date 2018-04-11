package com.wanniu.game.request.guild.guildBoss;

import java.io.IOException;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.common.Const;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.GuildBossHandler.GetGuildBossInfoResponse;

/**
 * 进入工会BOSS面板
 * 
 * @author Feil
 *
 */
@GClientEvent("area.guildBossHandler.getGuildBossInfoRequest")
public class GetGuildBossInfoHandler extends PomeloRequest {
	public PomeloResponse request() throws Exception {
		WNPlayer player = (WNPlayer) pak.getPlayer();
		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				GetGuildBossInfoResponse.Builder res = GetGuildBossInfoResponse.newBuilder();
				String msg = player.guildBossManager.handlerGetBossInfo(res);
				if (msg != null) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(msg);
				} else {
					res.setS2CCode(Const.CODE.OK);
				}
				body.writeBytes(res.build().toByteArray());
			}
		};
	}
}
