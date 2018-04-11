package com.wanniu.game.request.guild.guildManager;

import java.io.IOException;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.core.logfs.Out;
import com.wanniu.game.guild.GuildCommonUtil;
import com.wanniu.game.guild.guidDepot.GuildDepotCenter;
import com.wanniu.game.guild.guidDepot.PlayerGuildDepot;
import com.wanniu.game.player.WNPlayer;

import pomelo.guild.GuildManagerHandler;
import pomelo.guild.GuildManagerHandler.BagInfo;
import pomelo.guild.GuildManagerHandler.DepotInfo;
import pomelo.guild.GuildManagerHandler.GetDepotInfoResponse;

@GClientEvent("guild.guildManagerHandler.getDepotInfoRequest")
public class GetDepotInfoHandler extends PomeloRequest {

	public PomeloResponse request() throws Exception {
		WNPlayer player = (WNPlayer) pak.getPlayer();
		// GetDepotInfoRequest req =
		// GetDepotInfoRequest.parseFrom(pak.getRemaingBytes());
		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				GetDepotInfoResponse.Builder res = GetDepotInfoResponse.newBuilder();

				PlayerGuildDepot depotData = GuildDepotCenter.getInstance().getDepotDataByPlayerId(player);
				if (null == depotData) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("SOMETHING_ERR"));
					body.writeBytes(res.build().toByteArray());
					return;
				}

				DepotInfo.Builder depotInfo = DepotInfo.newBuilder();
				if (null != depotData.depotInfo) {
					depotInfo.setLevel(depotData.depotInfo.level);
					GuildManagerHandler.DepotCondition depCond = GuildCommonUtil.toManagerCond(depotData.depotInfo.condition);
					if (null != depCond) {
						depotInfo.setDepotCond(depCond);
					} else {
						Out.error("depCond is null:", depCond);
					}

					depotInfo.setDeleteCount(depotData.depotInfo.deleteCount);
					depotInfo.setDeleteCountMax(depotData.depotInfo.deleteCountMax);
				}

				BagInfo.Builder depotBag = BagInfo.newBuilder();
				if (null != depotData.bagInfo) {
					depotBag.setBagGridCount(depotData.bagInfo.getBagGridCount());
					depotBag.setBagTotalCount(depotData.bagInfo.getBagTotalCount());
					depotBag.addAllBagGrids(depotData.bagInfo.getBagGridsList());
					depotBag.addAllBagDetails(depotData.detailInfo);
				}

				res.setS2CCode(OK);
				res.setS2CDepotInfo(depotInfo.build());
				res.setS2CDepotBag(depotBag.build());
				body.writeBytes(res.build().toByteArray());
			}
		};
	}
}
