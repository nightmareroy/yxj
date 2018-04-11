package com.wanniu.game.poes;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.wanniu.core.game.entity.GEntity;
import com.wanniu.game.DBTable;
import com.wanniu.game.activity.ActivityManager.RewardRecord;
import com.wanniu.game.activity.po.LuckyAward;
import com.wanniu.game.bag.WNBag.SimpleItemInfo;
import com.wanniu.game.common.Const;
import com.wanniu.game.common.Table;
import com.wanniu.game.data.ActiveCO;
import com.wanniu.game.data.GameData;
import com.wanniu.game.data.LimitTimeGiftCO;
import com.wanniu.game.data.ext.ActivityConfigExt;
import com.wanniu.game.data.ext.ActivityConfigExt.ActivityConfigItem;
import com.wanniu.game.data.ext.ActivityExt;

@DBTable(Table.player_activity)
public class ActivityDataPO extends GEntity {

	public ArrayList<LuckyAward> luckyAwardContainer;

	public Date refreshTime;
	public int buffTimes;
	/** 活动领取记录 */
	public HashMap<Integer, RewardRecord> activityRewardRecorder;
	//super package领取记录，因为不在activityConfig表中，所以另起一个map，避免id冲突
	public HashMap<Integer, RewardRecord> superPackageRecorder;
	//每日充值领取记录
	public HashMap<Integer, RewardRecord> dailyRechargeRecorder;
	
	/** 活动购买记录 */
	public HashMap<Integer, HashMap<Integer, Integer>> activityInfo;
	public HashMap<Integer, SimpleItemInfo> drawedContainer;
	
	
	
	
	//每日充值
	public boolean daily_recharge_have_entered;
//	public int rechargeToday;
//	public Date lastUpdateDate;
//	public Map<Integer,Boolean> dailyRechargeGetRewardMap;
	// 七日登录
//	public int sevenday_round = 1;
//	public int sevenday_day = 1;
//	public boolean sevenday_received = false;
	public List<Integer> sevendayList;
	
	//超值礼包
	public boolean super_pakage_have_enterd;
	
	//每日抽奖
	public int daily_draw_free_time;
	public int daily_draw_free_time_add;//新增的新春抽奖
//	public Date daily_draw_free_time_update_time;
//	public int daily_draw_exploreTicketCountLeft;
	public Map<Integer, Integer> daily_draw_forgerandom_map;
	public Map<Integer, Integer> daily_draw_forgerandom_map_add;//新增的新春抽奖
	// 资源找回
	public HashMap<Integer, Integer> recovery;
	public HashMap<Integer, Integer> recoveryHistory;
	
	//限时礼包
	public Map<Integer, Date> timeLimitGiftTriggeredTimeMap;
	public Map<Integer, Integer> timeLimitGiftTriggeredIdMap;
	public Map<Integer, Integer> timeLimitGiftPushMap;
	public Map<Integer, Integer> timeLimitGiftBuyMap;


	public ActivityDataPO() {
		luckyAwardContainer = new ArrayList<LuckyAward>();
		refreshTime = new Date(0);
		activityRewardRecorder = new HashMap<Integer, RewardRecord>();
		dailyRechargeRecorder = new HashMap<Integer, RewardRecord>();
		superPackageRecorder = new HashMap<Integer, RewardRecord>();
		activityInfo = new HashMap<Integer, HashMap<Integer, Integer>>();
		drawedContainer = new HashMap<Integer, SimpleItemInfo>();
		recovery = new HashMap<>();
		recoveryHistory = new HashMap<>();
		
		daily_recharge_have_entered=false; 
//		daily_draw_free_time=1;
		super_pakage_have_enterd=false;
//		Date now=new Date();
//		Calendar calendar=Calendar.getInstance();
//		calendar.setTime(now);
//		int h=calendar.get(Calendar.HOUR_OF_DAY);
//		calendar.set(Calendar.HOUR_OF_DAY, 5);
//		if(h>5)
//			calendar.add(Calendar.DAY_OF_MONTH, 1);
//		daily_draw_free_time_update_time=calendar.getTime();

		daily_draw_forgerandom_map=new HashMap<>();
		daily_draw_forgerandom_map_add=new HashMap<>();
		
		sevendayList=new LinkedList<>();
		for (int i=0;i<7;i++) {
			sevendayList.add(i, 0);
		}
		sevendayList.set(0, 1);
		
		timeLimitGiftTriggeredTimeMap=new HashMap<>();
		timeLimitGiftTriggeredIdMap=new HashMap<>();
		timeLimitGiftPushMap=new HashMap<>();
		timeLimitGiftBuyMap=new HashMap<>();
		for (LimitTimeGiftCO limitTimeGiftCO : GameData.LimitTimeGifts.values()) {
			timeLimitGiftPushMap.put(limitTimeGiftCO.id, 0);
			if(!timeLimitGiftTriggeredTimeMap.containsKey(limitTimeGiftCO.condition))
			{
				timeLimitGiftTriggeredTimeMap.put(limitTimeGiftCO.condition, null);
			}
			if(!timeLimitGiftTriggeredIdMap.containsKey(limitTimeGiftCO.condition))
			{
				timeLimitGiftTriggeredIdMap.put(limitTimeGiftCO.condition, -1);
			}
			timeLimitGiftBuyMap.put(limitTimeGiftCO.id, 0);
		}
		
		
//		rechargeToday=0;
//		lastUpdateDate=new Date();
//		dailyRechargeGetRewardMap=new HashMap<>();
//		
//		
//		List<ActivityConfigExt> activityConfigExts=GameData.findActivityConfigs((t) -> t.type == Const.ActivityRewardType.DAILY_RECHARGE.getValue());
//		for (ActivityConfigExt activityConfigExt : activityConfigExts) {
//			getRewardMap.put(activityConfigExt.parameter1, false);
//		}
	}
}
