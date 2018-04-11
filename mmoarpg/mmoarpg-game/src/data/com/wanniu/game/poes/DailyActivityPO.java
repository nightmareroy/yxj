package com.wanniu.game.poes;

import java.util.HashMap;
import java.util.Map;

import com.wanniu.core.game.entity.GEntity;
import com.wanniu.game.DBTable;
import com.wanniu.game.common.Table;

@DBTable(Table.player_daily)
public final class DailyActivityPO extends GEntity {
	
	public Map<Integer,DailyInfo> activeMap;	  // 活动map
	public Map<Integer,DailyRewardInfo> rewards;  // 活跃度奖励
	public int totalDegree;  // 总活跃度
	
	public DailyActivityPO(){
		activeMap = new HashMap<Integer,DailyInfo>();
		rewards = new HashMap<Integer,DailyRewardInfo>();
	}
	
	public static class DailyInfo{
		public int id;              // 活动id
		public int process;			// 活动进度
	}
	
	public static class DailyRewardInfo{
		public int id;				// 奖励id
		public int state;			// 领取状态 0 ：未达到  1：可领取 2：已领取
	}
}