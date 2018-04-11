package com.wanniu.game.data.ext;

import java.util.HashMap;
import java.util.Map;

import com.wanniu.game.data.DayRewardCO;

/**
 * @author wanghaitao
 *
 */
public class DayRewardExt extends DayRewardCO{
	/** 每日奖励 */
	public Map<String,Integer> dayRewards = new HashMap<>();

	/** 构造属性 */
	public void initProperty() { 
		String[] items = super.rankReward.trim().split(";");
		for(String item : items){
			String[] str = item.trim().split(":");
			dayRewards.put(str[0], Integer.parseInt(str[1]));
		}
	}
}
