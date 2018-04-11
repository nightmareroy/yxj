package com.wanniu.game.request.fightLevel;

import java.io.IOException;
import java.util.List;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.area.Area;
import com.wanniu.game.fightLevel.FightLevelManager;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.FightLevelHandler.GetBossDamageRankResponse;
import pomelo.area.FightLevelHandler.RankInfo;

/**
 * Boss伤害排名
 * 
 * @author Yangzz
 *
 */
@GClientEvent("area.fightLevelHandler.getBossDamageRankRequest")
public class GetBossDamageRankHandler extends FightLevelLine {

	public PomeloResponse request(WNPlayer player) throws Exception {

//		GetBossDamageRankRequest req = GetBossDamageRankRequest.parseFrom(pak.getRemaingBytes());

		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				GetBossDamageRankResponse.Builder res = GetBossDamageRankResponse.newBuilder();

				Area area = player.getArea();

				FightLevelManager fightLevelManager = player.fightLevelManager;

			    GetBossDamageRankResult data = fightLevelManager.getBossDamageRank(player,area);

//			    s2c_rankInfos: data.rankInfos,s2c_ownDamage:data.ownDamage,s2c_ownRank:data.ownRank});

				res.setS2CCode(OK);
				res.addAllS2CRankInfos(data.rankInfos);
				res.setS2COwnDamage(data.ownDamage);
				res.setS2COwnRank(data.ownRank);
				
				body.writeBytes(res.build().toByteArray());

			}
		};
	}

public static class GetBossDamageRankResult {
	public List<RankInfo> rankInfos;
	public int ownDamage;
	public int ownRank;
	
	public GetBossDamageRankResult(List<RankInfo> rankInfos, int ownDamage, int ownRank) {
		this.rankInfos = rankInfos;
		this.ownDamage = ownDamage;
		this.ownRank = ownRank;
	}
}
}