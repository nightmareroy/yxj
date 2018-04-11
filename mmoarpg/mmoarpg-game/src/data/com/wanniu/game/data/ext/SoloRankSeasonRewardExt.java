package com.wanniu.game.data.ext;

import java.util.HashMap;
import java.util.Map;

import com.wanniu.game.data.SoloRankSeasonRewardCO;

public class SoloRankSeasonRewardExt extends SoloRankSeasonRewardCO { 

	/** 奖励物品 key:itemCode value:itemNum*/
	public Map<String,Integer> rankRewards = new HashMap<>();


	/** 构造属性 */
	public void initProperty() { 
		String[] items = super.rankReward.trim().split(";");
		for(String item : items){
			String[] str = item.trim().split(":");
			rankRewards.put(str[0], Integer.parseInt(str[1]));
		}
	}


}