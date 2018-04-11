package com.wanniu.game.rank;

import com.wanniu.game.data.GameData;
import com.wanniu.game.data.ext.RankListExt;

public class RankConfig {

	private static RankConfig instance;

	public static RankConfig getInstance() {
		if (instance == null) {
			instance = new RankConfig();
		}
		return instance;
	}

	public final RankListExt findListRankPropByRankID(int rankId) {
		for (RankListExt RankCO : GameData.RankLists.values()) {
			if (RankCO.rankID == rankId) {
				return RankCO;
			}
		}
		return null;
	}

}
