package com.wanniu.game.request.guild.guildManager;

import java.io.IOException;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.guild.guidDepot.GuildDepotCenter;
import com.wanniu.game.guild.guidDepot.GuildDepotOneGrid;
import com.wanniu.game.player.WNPlayer;

import io.netty.util.internal.StringUtil;
import pomelo.guild.GuildManagerHandler.GetDepotOneGridInfoRequest;
import pomelo.guild.GuildManagerHandler.GetDepotOneGridInfoResponse;

@GClientEvent("guild.guildManagerHandler.getDepotOneGridInfoRequest")
public class GetDepotOneGridInfoHandler extends PomeloRequest {

	public PomeloResponse request() throws Exception {
		WNPlayer player = (WNPlayer) pak.getPlayer();
		GetDepotOneGridInfoRequest req = GetDepotOneGridInfoRequest.parseFrom(pak.getRemaingBytes());
		int bagIndex = req.getBagIndex();
		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				GetDepotOneGridInfoResponse.Builder res = GetDepotOneGridInfoResponse.newBuilder();
				GuildDepotCenter depotManager = GuildDepotCenter.getInstance();
				if (StringUtil.isNullOrEmpty(player.getId()) || null == depotManager) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("SOMETHING_ERR"));
					body.writeBytes(res.build().toByteArray());
					return;
				}

				GuildDepotOneGrid data = depotManager.getDepotOneGridInfoByPlayerId(player, bagIndex);
				res.setS2CCode(OK);
				res.setS2CBagGrid(data.grid);
				res.setS2CBagDetail(data.detail);
				body.writeBytes(res.build().toByteArray());
			}
		};
	}
}