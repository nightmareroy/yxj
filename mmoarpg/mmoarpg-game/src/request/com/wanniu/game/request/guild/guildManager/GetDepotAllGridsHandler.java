package com.wanniu.game.request.guild.guildManager;

import java.io.IOException;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.guild.guidDepot.GuildDepotCenter;
import com.wanniu.game.guild.guidDepot.PlayerGuildDepot;
import com.wanniu.game.player.WNPlayer;

import io.netty.util.internal.StringUtil;
import pomelo.guild.GuildManagerHandler.GetDepotAllGridsResponse;

@GClientEvent("guild.guildManagerHandler.getDepotAllGridsRequest")
public class GetDepotAllGridsHandler extends PomeloRequest {

	public PomeloResponse request() throws Exception {
		WNPlayer player = (WNPlayer) pak.getPlayer();
		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				GetDepotAllGridsResponse.Builder res = GetDepotAllGridsResponse.newBuilder();

				GuildDepotCenter depotManager = GuildDepotCenter.getInstance();
				if (StringUtil.isNullOrEmpty(player.getId()) || null == depotManager) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("SOMETHING_ERR"));
					body.writeBytes(res.build().toByteArray());
					return;
				}
				PlayerGuildDepot data = depotManager.getDepotDataByPlayerId(player);
				res.setS2CCode(OK);
				res.setS2CBagInfo(data.bagInfo);
				body.writeBytes(res.build().toByteArray());
			}
		};
	}
}