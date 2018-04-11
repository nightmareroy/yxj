package com.wanniu.game.data.ext;

import java.util.HashMap;
import java.util.Map;

import com.wanniu.game.data.BattleRoyaleRankCO;

public class BattleRoyaleRankExt extends BattleRoyaleRankCO { 
	/** 段位奖励 */
	public Map<String,Integer> gradeRewards = new HashMap<>();

	/** 构造属性 */
	public void initProperty() { 
		String[] items = super.rankReward.trim().split(";");
		for(String item : items){
			String[] str = item.trim().split(":");
			gradeRewards.put(str[0], Integer.parseInt(str[1]));
		}
	}

}