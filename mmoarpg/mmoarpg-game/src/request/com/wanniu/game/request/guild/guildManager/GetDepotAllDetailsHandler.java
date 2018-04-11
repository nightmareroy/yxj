package com.wanniu.game.request.guild.guildManager;

import java.io.IOException;
import java.util.ArrayList;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.guild.guidDepot.GuildDepotCenter;
import com.wanniu.game.player.WNPlayer;

import io.netty.util.internal.StringUtil;
import pomelo.guild.GuildManagerHandler.GetDepotAllDetailsResponse;
import pomelo.item.ItemOuterClass.ItemDetail;

@GClientEvent("guild.guildManagerHandler.getDepotAllDetailsRequest")
public class GetDepotAllDetailsHandler extends PomeloRequest {

	public PomeloResponse request() throws Exception {
		WNPlayer player = (WNPlayer) pak.getPlayer();
		// GetDepotAllDetailsRequest req =
		// GetDepotAllDetailsRequest.parseFrom(pak.getRemaingBytes());
		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				GetDepotAllDetailsResponse.Builder res = GetDepotAllDetailsResponse.newBuilder();

				GuildDepotCenter depotManager = GuildDepotCenter.getInstance();
				if (StringUtil.isNullOrEmpty(player.getId()) || null == depotManager) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("SOMETHING_ERR"));
					body.writeBytes(res.build().toByteArray());
					return;
				}
				ArrayList<ItemDetail> list = depotManager.getDepotDetailsByPlayerId(player);
				res.setS2CCode(OK);
				res.addAllS2CBagDetails(list);
				body.writeBytes(res.build().toByteArray());
			}
		};
	}
}
