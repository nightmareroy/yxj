package com.wanniu.game.data.ext;

import java.util.HashMap;
import java.util.Map;

import com.wanniu.game.data.BattleRoyaleRankSeasonRewardCO;

public class BattleRoyaleRankSeasonRewardExt extends BattleRoyaleRankSeasonRewardCO {

	/** 排名奖励 */
	public Map<String, Integer> rankRewards = new HashMap<>();

	/** 构造属性 */
	public void initProperty() {
		String[] items = super.rankReward.trim().split(";");
		for (String item : items) {
			String[] str = item.trim().split(":");
			rankRewards.put(str[0], Integer.parseInt(str[1]));
		}
	}

}
