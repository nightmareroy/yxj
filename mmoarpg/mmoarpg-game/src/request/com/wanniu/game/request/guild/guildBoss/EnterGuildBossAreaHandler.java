package com.wanniu.game.request.guild.guildBoss;

import java.io.IOException;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.common.Const;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.GuildBossHandler.EnterGuildBossAreaResponse;

/**
 * 进入工会BOSS场景(在工会那个线程下)
 * 
 * @author Feil
 *
 */
@GClientEvent("area.guildBossHandler.enterGuildBossAreaRequest")
public class EnterGuildBossAreaHandler extends PomeloRequest {
	public PomeloResponse request() throws Exception {
		WNPlayer player = (WNPlayer) pak.getPlayer();
		return new PomeloResponse() {
			EnterGuildBossAreaResponse.Builder res = EnterGuildBossAreaResponse.newBuilder();
			String msg = player.guildBossManager.handleEnterGuildBossArea();

			@Override
			protected void write() throws IOException {
				if (msg != null) {
					res.setS2CCode(Const.CODE.FAIL);
					res.setS2CMsg(msg);
				} else {
					res.setS2CCode(Const.CODE.OK);
				}
				body.writeBytes(res.build().toByteArray());

			}
		};
	}

}
