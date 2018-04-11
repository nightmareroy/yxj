package com.wanniu.game.request.guild.guildManager;

import java.io.IOException;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.guild.GuildUtil;
import com.wanniu.game.guild.guidDepot.GuildDepot;
import com.wanniu.game.guild.guidDepot.GuildDepotCenter;
import com.wanniu.game.guild.guildBless.GuildBless;
import com.wanniu.game.guild.guildBless.GuildBlessCenter;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.poes.GuildMemberPO;

import io.netty.util.internal.StringUtil;
import pomelo.guild.GuildManagerHandler.BuildingLevel;
import pomelo.guild.GuildManagerHandler.GetBuildingLevelResponse;

@GClientEvent("guild.guildManagerHandler.getBuildingLevelRequest")
public class GetBuildingLevelHandler extends PomeloRequest {

	public PomeloResponse request() throws Exception {
		WNPlayer player = (WNPlayer) pak.getPlayer();
		// GetBuildingLevelRequest req =
		// GetBuildingLevelRequest.parseFrom(pak.getRemaingBytes());
		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				GetBuildingLevelResponse.Builder res = GetBuildingLevelResponse.newBuilder();

				GuildBlessCenter blessManager = GuildBlessCenter.getInstance();
				GuildDepotCenter depotManager = GuildDepotCenter.getInstance();
				if (StringUtil.isNullOrEmpty(player.getId()) || null == blessManager || null == depotManager) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("SOMETHING_ERR"));
					body.writeBytes(res.build().toByteArray());
					return;
				}

				BuildingLevel.Builder data = BuildingLevel.newBuilder();
				GuildMemberPO myInfo = GuildUtil.getGuildMember(player.getId());
				if (null != myInfo) {
					GuildBless bless = blessManager.getBless(myInfo.guildId);
					if (null != bless) {
						data.setBlessLevel(bless.level);
						data.setTechLevel(bless.tech.level);
					}

					GuildDepot depot = depotManager.getDepot(myInfo.guildId);
					if (null != depot) {
						data.setDepotLevel(depot.depotData.level);
					}
				}

				res.setS2CCode(OK);
				res.setS2CLevelInfo(data.build());
				body.writeBytes(res.build().toByteArray());
			}
		};
	}
}
