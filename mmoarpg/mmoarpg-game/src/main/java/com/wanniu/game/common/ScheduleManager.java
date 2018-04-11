package com.wanniu.game.common;

import java.util.List;
import java.util.Map;

import com.wanniu.core.GGame;
import com.wanniu.core.db.GCache;
import com.wanniu.core.game.JobFactory;
import com.wanniu.core.game.entity.GPlayer;
import com.wanniu.core.logfs.Out;
import com.wanniu.core.util.DateUtil;
import com.wanniu.game.daily.DailyActivityMgr;
import com.wanniu.game.data.GameData;
import com.wanniu.game.data.ext.ScheduleExt;
import com.wanniu.game.data.ext.ScheduleExt.TimeCond;
import com.wanniu.game.guild.guildBless.GuildBlessCenter;
import com.wanniu.game.intergalmall.IntergalMallGlobalService;
import com.wanniu.game.player.WNPlayer;

/**
 * 计划任务
 * 
 * @author Yangzz
 *
 */
public class ScheduleManager {

	private static class Holder {
		private static ScheduleManager instance = new ScheduleManager();
	}

	public static ScheduleManager getInstance() {
		return Holder.instance;
	}

	private ScheduleManager() {
		// 启动5点刷新任务
		long initialDelay = DateUtil.getFiveDelay();

		Out.info("refreshNewDay will start after ", initialDelay);
		// 重置5点在线玩家数据
		JobFactory.addFixedRateJob(() -> {

			GameData.NoticeSends.values().forEach(notice -> {
				if (notice.isValid != 0) {
					notice.schedeule();
				}
			});

			// GCache.remove(ConstsTR.DAILY_DEMON_TOWER_COUNT.value);
			GCache.remove(ConstsTR.DAILY_RELIVE.value);
			GCache.remove(ConstsTR.intergalMallTR.value);
			Out.info("begin refreshNewDay...");

			Map<String, GPlayer> players = GGame.getInstance().getOnlinePlayers();
			WNPlayer player = null;
			for (GPlayer gplayer : players.values()) {
				player = (WNPlayer) gplayer;

				player.refreshNewDay();
			}

			// 重置商城全服购买次数
			IntergalMallGlobalService.getInstance().refreshNewDay();
			// 公会祈福重置
			GuildBlessCenter.getInstance().refreshNewDay();

			Out.info("end refreshNewDay...");
		}, initialDelay, Const.Time.Day.getValue());

		List<ScheduleExt> scheduleExts = GameData.findSchedules(t -> 
			t.schID == DailyActivityMgr.ScheduleType.SOLO.getValue() || 
			t.schID == DailyActivityMgr.ScheduleType.FIVE_MOUNTAIN.getValue() ||
			t.schID == DailyActivityMgr.ScheduleType.TRIAL.getValue() ||
			t.schID == DailyActivityMgr.ScheduleType.DEMON_INVADE.getValue() ||
			t.schID == DailyActivityMgr.ScheduleType.ILLUSION2.getValue()||
			t.schID == DailyActivityMgr.ScheduleType.GUILD_BOSS.getValue());
		for (ScheduleExt scheduleExt : scheduleExts) {
			List<TimeCond> timeConds = scheduleExt.periodInCalendarArray;
			for (int j = 0; j < timeConds.size(); j++) {
				TimeCond timeCond = timeConds.get(j);
				String startTime = new String(timeCond.beginTime);
				String endTime = new String(timeCond.endTime);
				if (startTime.indexOf(":") == startTime.lastIndexOf(":")) {
					startTime += ":00";
				}
				if (endTime.indexOf(":") == endTime.lastIndexOf(":")) {
					endTime += ":00";
				}
				long start = DateUtil.format(startTime).getTime();
				long end = DateUtil.format(endTime).getTime();
				JobFactory.addFixedRateJob(() -> {
					dailyActivityUpdate();
				}, DateUtil.getSomeDateDelay(start - 10 * 60 * 1000), Const.Time.Day.getValue());
				JobFactory.addFixedRateJob(() -> {
					dailyActivityUpdate();
				}, DateUtil.getSomeDateDelay(start), Const.Time.Day.getValue());
				JobFactory.addFixedRateJob(() -> {
					dailyActivityUpdate();
				}, DateUtil.getSomeDateDelay(end), Const.Time.Day.getValue());
			}

		}

	}

	private void dailyActivityUpdate() {
		Map<String, GPlayer> players = GGame.getInstance().getOnlinePlayers();
		WNPlayer player = null;
		for (GPlayer gplayer : players.values()) {
			player = (WNPlayer) gplayer;
			DailyActivityMgr dailyActivityMgr = player.dailyActivityMgr;
			dailyActivityMgr.updateSuperScript();
		}
	}

}
