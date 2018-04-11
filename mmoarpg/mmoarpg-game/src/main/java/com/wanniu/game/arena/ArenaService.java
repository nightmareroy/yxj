package com.wanniu.game.arena;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import com.wanniu.core.game.JobFactory;
import com.wanniu.core.game.entity.GPlayer;
import com.wanniu.core.logfs.Out;
import com.wanniu.core.util.DateUtil;
import com.wanniu.game.GWorld;
import com.wanniu.game.area.AreaUtil;
import com.wanniu.game.arena.po.ArenaSystemPO;
import com.wanniu.game.common.ConstsTR;
import com.wanniu.game.data.base.MapBase;
import com.wanniu.game.player.GlobalConfig;
import com.wanniu.game.player.PlayerUtil;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.rank.RankType;
import com.wanniu.game.rank.handler.ArenaScoreAllRankHandler;
import com.wanniu.game.rank.handler.ArenaScoreRankHandler;
import com.wanniu.redis.GameDao;

public class ArenaService {
	/**
	 * 赛季最短周期
	 */
	private final static long MIN_SEASON_MILLISEC = TimeUnit.DAYS.toMillis(3);

	/* 竞技场地图场景id编号约定常量 */
	public static final int ARENA_MAP_ID = 70002;
	private ArenaSystemPO arenaSystem;

	private MapBase arenaMap;

	private Date beginTime;
	private Date endTime;
	private Date awardTime;// 单日发奖时间

	public MapBase getArenaMap() {
		return arenaMap;
	}

	private static ArenaService instance;

	public static ArenaService getInstance() {
		if (instance == null) {
			instance = new ArenaService();
		}
		return instance;
	}

	private ArenaService() {

		init();
	}

	private void init() {
		initOpenTime();
		initArenaSystem();

		long delay = DateUtil.getFiveDelay();
		Out.info("arenaService init timer delay:", delay);
		JobFactory.addScheduleJob(new Runnable() {// 每日重置任务
			@Override
			public void run() {
				Out.info("arenaService scheduleJob...");
				initOpenTime();
			}
		}, delay, TimeUnit.DAYS.toMillis(1));

		// 每日开始时间重置单日排行榜任务
		delay = DateUtil.getTaskDelay(beginTime);
		delay -= TimeUnit.MINUTES.toMillis(5);// 提前5分钟
		JobFactory.addScheduleJob(new Runnable() {
			@Override
			public void run() {
				if (checkIsOpenDay()) {
					RankType.ARENA_SCORE.getHandler().delRank(GWorld.__SERVER_ID);
				}
			}

		}, delay, TimeUnit.DAYS.toMillis(1));

		long seasonDelay = this.arenaSystem.seasonResetTime.getTime() - System.currentTimeMillis();
		JobFactory.addScheduleJob(new Runnable() {
			@Override
			public void run() {
				Out.info("SoloService reset soloSeason ...");
				resetSeason();// 本赛季结束，重置赛季
			}
		}, seasonDelay, GlobalConfig.JJC_SeasonDay * TimeUnit.DAYS.toMillis(1));

		// 开始和结束时间刷新红点
		JobFactory.addScheduleJob(new Runnable() {

			@Override
			public void run() {
				for (GPlayer p : PlayerUtil.getAllOnlinePlayer()) {
					WNPlayer wp = (WNPlayer) p;
					wp.updateSuperScriptList(wp.arenaManager.getSuperScript());
				}
			}
		}, DateUtil.getTaskDelay(beginTime), TimeUnit.DAYS.toMillis(1));

		JobFactory.addScheduleJob(new Runnable() {

			@Override
			public void run() {
				for (GPlayer p : PlayerUtil.getAllOnlinePlayer()) {
					WNPlayer wp = (WNPlayer) p;
					wp.updateSuperScriptList(wp.arenaManager.getSuperScript());
				}
			}
		}, DateUtil.getTaskDelay(endTime), TimeUnit.DAYS.toMillis(1));
	}

	// private final static int MIN_DAILY_TERMS = 7;

	/**
	 * 重置赛季
	 */
	private void resetSeason() {
		Out.info("reset term in ArenaService.resetSeason...");
		this.arenaSystem.term += 1;
		this.arenaSystem.seasonResetTime = calcSoloSeasonTime();
		updateArenaSystemToRedis();
	}

	/**
	 * 计算赛季结束时间
	 * 
	 * @return
	 */
	private Date calcSoloSeasonTime() {
		Date endDate = calcSeasonEndTime(GlobalConfig.JJC_SeasonDay, GlobalConfig.JJC_SeasonWeekday);
		if (endDate.getTime() - System.currentTimeMillis() < MIN_SEASON_MILLISEC) {// 小于最短赛季天数就直接再加一周时间
			endDate = calcSeasonEndTime(GlobalConfig.JJC_SeasonDay + 7, GlobalConfig.JJC_SeasonWeekday);
		}
		return endDate;
	}
	
	/**
	 * 获取赛季结束时间
	 * 
	 * @return
	 */
	public Date getSeasonEndTime()
	{
		return this.arenaSystem.seasonResetTime;
	}

	/**
	 * 返回赛季结束日期的5点钟时间
	 * 
	 * @param term 赛季周期天数，期望几日后结束
	 * @param endWeekDay 期望几日后的周几结束 1~6对应周一~周六 任何大于6都当0处理
	 * @return
	 */
	private static Date calcSeasonEndTime(int term, int endWeekDay) {
		if (endWeekDay >= 7) {
			endWeekDay = 0;
		}
		Date endDate = DateUtil.getFiveTimeOfDay(DateUtil.getDateAfter(term));
		Calendar endC = Calendar.getInstance();
		endC.setTime(endDate);
		endC.set(Calendar.DAY_OF_WEEK, endWeekDay + 1);

		return endC.getTime();
	}

	/**
	 * 获取当前赛季
	 * 
	 * @return
	 */
	public int getTerm() {
		return this.arenaSystem.term;
	}

	/**
	 * 根据GameMap配置重新计算功能开放的起始时间和结束时间
	 */
	private void initOpenTime() {
		this.arenaMap = AreaUtil.getAreaProp(ARENA_MAP_ID);
		String[] openTimes = GlobalConfig.JJC_Daily_OpenTime.split(";")[0].split(",");
		this.beginTime = DateUtil.format(openTimes[0]);
		this.endTime = DateUtil.format(openTimes[1]);
		this.awardTime = DateUtil.format(GlobalConfig.JJC_Daily_Award);
	}

	/**
	 * 初始化系统配置
	 */
	private void initArenaSystem() {
		ArenaSystemPO syspo = GameDao.get(String.valueOf(GWorld.__SERVER_ID), ConstsTR.arenaSystemTR, ArenaSystemPO.class);
		if (syspo != null) {
			this.arenaSystem = syspo;
		} else {
			this.arenaSystem = new ArenaSystemPO(String.valueOf(GWorld.__SERVER_ID));
			this.arenaSystem.seasonResetTime = calcSoloSeasonTime();
			this.updateArenaSystemToRedis();
		}
	}

	/**
	 * @return 功能开放是时间戳
	 */
	public long getBeginTime() {
		return this.beginTime.getTime();
	}

	/**
	 * @return 功能结束的时间戳
	 */
	public long getEndTime() {
		return this.endTime.getTime();
	}

	/**
	 * 判断是否在可参与时段
	 */
	public boolean isInOpenTime() {
		if (!checkIsOpenDay()) {
			return false;
		}
		Date nowTime = new Date();
		if (nowTime.after(beginTime) && nowTime.before(endTime)) {
			return true;
		}
		return false;
	}

	/**
	 * 判断是否在开放日期内
	 * 
	 * @return
	 */
	public boolean checkIsOpenDay() {
		// 竞技场按周开放
		Calendar c = Calendar.getInstance();
		int weekDay = c.get(Calendar.DAY_OF_WEEK) - 1;
		return GlobalConfig.JJC_Weekly_OpenTime.contains(String.valueOf(weekDay));
	}

	/**
	 * 更新系统配置到redis
	 */
	private void updateArenaSystemToRedis() {
		GameDao.update(String.valueOf(GWorld.__SERVER_ID), ConstsTR.arenaSystemTR, this.arenaSystem);
	}

	private double getDoubleScoreByMills(int score) {
		long time = arenaSystem.seasonResetTime.getTime();
		String s = score + "." + (time - System.currentTimeMillis());
		return Double.parseDouble(s);
	}

	/**
	 * 保存单日分数排行榜
	 */
	public void refreshScoreRank(String playerId, int score) {
		ArenaScoreRankHandler.getInstance().asyncUpdateRank(GWorld.__SERVER_ID, playerId, score);
	}

	/**
	 * 获取指定playerId的上赛季排名
	 * 
	 * @param playerId
	 * @return
	 */
	public int getLastAllScoreRank(String playerId) {
		int term = this.getTerm();
		if (term <= 0) {
			return 0;
		}
		return (int) RankType.ARENA_SCOREALL.getHandler().getSeasonRank(GWorld.__SERVER_ID, term - 1, playerId);
	}

	/**
	 * 获取指定playerId的当前赛季排名
	 * 
	 * @param playerId
	 * @return
	 */
	public int getCurrentAllScoreRank(String playerId) {
		return (int) RankType.ARENA_SCOREALL.getHandler().getSeasonRank(GWorld.__SERVER_ID, this.getTerm(), playerId);
	}

	/**
	 * 获取指定playerId的当前赛季积分
	 * 
	 * @param playerId
	 * @return
	 */
	public int getCurrentAllScoreScore(String playerId) {
		return PlayerUtil.getOnlinePlayer(playerId).arenaManager.getCurrentTotalScore();
	}

	/**
	 * 刷新竞技场最高连杀信息到赛季排行榜
	 */
	public void refreshMonthScoreRank(String playerId, int scoreMonth) {
		ArenaScoreAllRankHandler.getInstance().asyncUpdateRank(GWorld.__SERVER_ID, playerId, scoreMonth);
	};

	/**
	 * 当前时间是否可领取单日奖励
	 * 
	 * @return
	 */
	public boolean canDrawDayAward() {
		// if (!checkIsOpenDay()) {
		// return true;
		// }
		// Date date = new Date();
		// if (date.after(awardTime) || date.before(beginTime)) {
		// return true;
		// }
		return false;
	}
}
