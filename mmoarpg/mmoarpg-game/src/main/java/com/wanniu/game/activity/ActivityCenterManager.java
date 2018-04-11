package com.wanniu.game.activity;

import java.util.List;

import com.wanniu.core.db.GCache;
import com.wanniu.core.util.StringUtil;
import com.wanniu.game.common.Const;
import com.wanniu.game.common.ConstsTR;
import com.wanniu.game.data.ActivityCO;
import com.wanniu.game.data.GameData;
import com.wanniu.game.data.ext.ActivityExt;

public class ActivityCenterManager {
	private static ActivityCenterManager instance;

	public static synchronized ActivityCenterManager getIntance() {
		if (instance == null)
			instance = new ActivityCenterManager();
		return instance;
	}

	private ActivityCenterManager() {
		// TODO
		// var logicServerBuy = {};;;;
		// var gameDBClient = pomelo.app.get('gameDBClient');
		// for(var acrossServerId in gameDBClient){
		// if(gameDBClient[acrossServerId]){
		// var funds = gameDBClient[acrossServerId].models.buyFunds;
		// var fundsRecord = await(funds.findAsync({}));
		// fundsRecord.forEach(function(record){
		// if(logicServerBuy[record.logicServerId]){
		// logicServerBuy[record.logicServerId]++;
		// }else{
		// logicServerBuy[record.logicServerId] =
		// getJoinNum(record.logicServerId) + 1;
		// }
		// });
		// }
		// }
		//
		// for(var serverId in logicServerBuy){
		// fundsDao.addServerFundsHash(serverId, logicServerBuy[serverId]);
		// }
	}

	public int getFundsNum(int serverId) {
		String data = GCache.hget(Integer.toString(serverId), ConstsTR.fundsTR.value);
		int num = 0;
		if (StringUtil.isEmpty(data)) {
			num = getJoinNum(serverId);
			GCache.hset(Integer.toString(serverId), ConstsTR.fundsTR.value, String.valueOf(num));
		} else {
			num = Integer.parseInt(data);
		}
		return num;
	}

	/*
	 * ActivityCenterManager.getFundsNum = function(serverId){ var num =
	 * fundsDao.getServerFundsHash(serverId); if(num === 0){ num =
	 * getJoinNum(serverId); if(num !== 0){ fundsDao.addServerFundsHash(serverId,
	 * num); } } return Number(num); };
	 */

	public void addFundRecord(int serverId) {
		GCache.hincr(Integer.toString(serverId), ConstsTR.fundsTR.value, 1);
	}

	/*
	 * ActivityCenterManager.addFundRecord = function(serverId){ var num =
	 * fundsDao.getServerFundsHash(serverId); num++;
	 * fundsDao.addServerFundsHash(serverId, num); };
	 */

	public int getJoinNum(int serverId) {
		int num = 0;
		List<ActivityExt> props = GameData.findActivitys((t) -> t.activityTab == Const.ActivityRewardType.FOUNDATION.getValue());
		ActivityExt prop = null;
		for (ActivityCO p : props) {
			prop = (ActivityExt) p;
			break;
		}
		if (prop != null) {
			num = prop.joinNum;
		}
		return num;
	}
}
