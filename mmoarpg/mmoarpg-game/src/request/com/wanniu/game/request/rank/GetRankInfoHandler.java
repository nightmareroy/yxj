package com.wanniu.game.request.rank;

import java.io.IOException;
import java.util.ArrayList;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.entity.GPlayer;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.rank.TitleManager;

import pomelo.area.RankHandler.AwardRank;
import pomelo.area.RankHandler.GetRankInfoResponse;

@GClientEvent("area.rankHandler.getRankInfoRequest")
public class GetRankInfoHandler extends PomeloRequest {

	@Override
	public PomeloResponse request() throws Exception {
		GPlayer player = pak.getPlayer();
		WNPlayer wPlayer = (WNPlayer) player;
		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				GetRankInfoResponse.Builder res = GetRankInfoResponse.newBuilder();
				TitleManager titleManager = wPlayer.titleManager;
				int id = titleManager.getSelectedRankId();
				// int fightPower = playerRank.calFightPower();
				res.setS2CCode(OK);
				res.setS2CSelectedRankId(id);
				// res.setS2CFightPower(fightPower); // 客户端废弃字段
				ArrayList<AwardRank.Builder> list = titleManager.getRankInfo();
				for (AwardRank.Builder builder : list) {
					res.addS2CAwardRanks(builder.build());
				}
				body.writeBytes(res.build().toByteArray());
			}
		};
	}

}
