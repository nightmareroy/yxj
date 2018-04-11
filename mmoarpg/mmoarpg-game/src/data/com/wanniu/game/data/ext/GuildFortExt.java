package com.wanniu.game.data.ext;

import java.util.HashMap;
import java.util.Map;

import com.wanniu.game.data.GuildFortCO;

public class GuildFortExt extends GuildFortCO {
	/** 胜利方奖励物品 key:itemCode value:itemNum*/
	public Map<String,Integer> winnerReward = new HashMap<>();
	/** 失败方奖励物品 key:itemCode value:itemNum*/
	public Map<String,Integer> loserReward = new HashMap<>();
	/** 占领方每日奖励物品 key:itemCode value:itemNum*/
	public Map<String,Integer> dailyReward = new HashMap<>();
	
	public void initProperty() {
		String[] items = super.victoryResources.trim().split(";");
		for(String item : items){
			String[] str = item.trim().split(":");
			winnerReward.put(str[0], Integer.parseInt(str[1]));
		}
		
		items = super.failResources.trim().split(";");
		for(String item : items){
			String[] str = item.trim().split(":");
			loserReward.put(str[0], Integer.parseInt(str[1]));
		}
		
		items = super.dayResources.trim().split(";");
		for(String item : items){
			String[] str = item.trim().split(":");
			dailyReward.put(str[0], Integer.parseInt(str[1]));
		}
	}

}
