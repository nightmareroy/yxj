package com.wanniu.game.solo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.wanniu.core.game.JobFactory;
import com.wanniu.core.game.entity.GPlayer;
import com.wanniu.core.logfs.Out;
import com.wanniu.core.util.DateUtil;
import com.wanniu.game.GWorld;
import com.wanniu.game.common.Const.GOODS_CHANGE_TYPE;
import com.wanniu.game.common.ConstsTR;
import com.wanniu.game.data.GameData;
import com.wanniu.game.data.SoloRankCO;
import com.wanniu.game.data.ext.SoloRankSeasonRewardExt;
import com.wanniu.game.leaderBoard.LeaderBoardDetail;
import com.wanniu.game.mail.MailUtil;
import com.wanniu.game.mail.SysMailConst;
import com.wanniu.game.mail.data.MailData;
import com.wanniu.game.mail.data.MailData.Attachment;
import com.wanniu.game.mail.data.MailSysData;
import com.wanniu.game.player.GlobalConfig;
import com.wanniu.game.player.PlayerUtil;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.rank.RankType;
import com.wanniu.game.solo.po.SoloSystemPO;
import com.wanniu.redis.GameDao;

public class SoloService {
	public static class OpenInfo {
		public String openTimeStr;
		public String closeTimeStr;
		public Date openTime;
		public Date closeTime;
	}

	/**
	 * 赛季最短周期
	 */
	private final static long MIN_SEASON_MILLISEC = TimeUnit.DAYS.toMillis(3);

	private SoloRankCO[] soloRanks;
	private List<OpenInfo> soloOpenInfoList;
	private SoloSystemPO soloSystem;

	private static SoloService soloService;

	public static SoloService getInstance() {
		if (soloService == null) {
			soloService = new SoloService();
		}
		return soloService;
	}

	private SoloService() {
		init();
	}

	/**
	 * 每日凌晨刷新任务
	 */
	private void init() {
		initSoloSystem();
		initSoloRanks();// 把资历和段位的对照表存放在一个有序列表里
		initOpenTimeList();// 初始化开放活动时间列表

		long delay = DateUtil.getFiveDelay();
		JobFactory.addScheduleJob(new Runnable() {
			@Override
			public void run() {
				Out.info("soloService scheduleJob...");
				resetOpenTime();
			}
		}, delay, 24 * 3600 * 1000);

		long seasonDelay = this.getSeasonEndTime() - System.currentTimeMillis();
		JobFactory.addScheduleJob(new Runnable() {
			@Override
			public void run() {
				Out.info("SoloService reset soloSeason ...");
				resetSeason();// 本赛季结束，重置赛季
			}
		}, seasonDelay, GlobalConfig.Solo_SeasonDay * 24 * 3600 * 1000);

		// 开始和结束时间刷新红点
		for (OpenInfo info : soloOpenInfoList) {
			JobFactory.addScheduleJob(new Runnable() {

				@Override
				public void run() {
					for (GPlayer p : PlayerUtil.getAllOnlinePlayer()) {
						WNPlayer wp = (WNPlayer) p;
						wp.updateSuperScriptList(wp.soloManager.getSuperScript());
					}
				}
			}, DateUtil.getTaskDelay(info.openTime), TimeUnit.DAYS.toMillis(1));

			JobFactory.addScheduleJob(new Runnable() {

				@Override
				public void run() {
					for (GPlayer p : PlayerUtil.getAllOnlinePlayer()) {
						WNPlayer wp = (WNPlayer) p;
						wp.updateSuperScriptList(wp.soloManager.getSuperScript());
						wp.soloManager.quitMatching(false);
					}
					SoloMatcher.getInstance().resetMatchingList();
				}
			}, DateUtil.getTaskDelay(info.closeTime), TimeUnit.DAYS.toMillis(1));
		}
	}

	/**
	 * 重置每日开放时间段
	 */
	private void resetOpenTime() {
		for (OpenInfo info : this.soloOpenInfoList) {
			info.openTime = DateUtil.format(info.openTimeStr);
			info.closeTime = DateUtil.format(info.closeTimeStr);
		}
		this.soloSystem.rounds++;

		this.updateSoloSystemToRedis();
	}

	/**
	 * 重置赛季
	 */
	private void resetSeason() {
		if (System.currentTimeMillis() < this.soloSystem.seasonEndTime.getTime()) {
			Out.error("SoloService resetSeason error.......");
			return;
		}
		sendSeasonReward();
		this.soloSystem.seasonEndTime = calcSoloSeasonTime();// 重新计算下一赛季时间
		this.soloSystem.term += 1;
		this.soloSystem.rounds = 1;

		this.updateSoloSystemToRedis();
	}

	/**
	 * @return 赛季结束时间
	 */
	public long getSeasonEndTime() {
		return this.soloSystem.seasonEndTime.getTime();
	}

	/**
	 * List eg: "10:00:00,11:00:00;19:00:00,20:00:00"
	 */
	private void initOpenTimeList() {
		this.soloOpenInfoList = new ArrayList<>();
		String[] rangeList = GlobalConfig.Solo_Daily_OpenTime.split(";");
		for (int i = 0; i < rangeList.length; ++i) {

			String[] timeList = rangeList[i].split(",");
			if (timeList.length >= 2) {
				OpenInfo oi = new OpenInfo();
				oi.openTimeStr = timeList[0];
				oi.openTime = DateUtil.format(timeList[0]);
				oi.closeTimeStr = timeList[1];
				oi.closeTime = DateUtil.format(timeList[1]);
				this.soloOpenInfoList.add(oi);
			} else {
				Out.error("Error in SoloManager.getOpenTimeList() ", timeList.length);
			}
		}
	};

	/**
	 * 判断问道大会是否在开放时间段内
	 * 
	 * @return
	 */
	public boolean isInOpenTime() {	
		Calendar c = Calendar.getInstance();
		int weekDay = c.get(Calendar.DAY_OF_WEEK) - 1;
		if (!GlobalConfig.Solo_Weekly_OpenTime.contains(String.valueOf(weekDay))) {
			return false;
		}
		Date nowTime = new Date();
		for (OpenInfo oi : this.soloOpenInfoList) {
			if (nowTime.after(oi.openTime) && nowTime.before(oi.closeTime)) {
				return true;
			}
		}

		return false;
	};

	/**
	 * 返回开放时间段列表
	 * 
	 * @return
	 */
	public List<OpenInfo> getOpenInfoList() {
		return this.soloOpenInfoList;
	}

	/**
	 * 把资历和段位的对照表存放在一个有序列表里
	 */
	private void initSoloRanks() {
		if (soloRanks == null || soloRanks.length < 1) {
			soloRanks = new SoloRankCO[GameData.SoloRanks.size()];
			int i = 0;
			for (SoloRankCO prop : GameData.SoloRanks.values()) {
				soloRanks[i] = prop;
				i++;
			}

			Arrays.sort(soloRanks, new Comparator<SoloRankCO>() {
				@Override
				public int compare(SoloRankCO o1, SoloRankCO o2) {
					return (o1.iD > o2.iD) ? 1 : -1;
				}
			});

			for (SoloRankCO prop : soloRanks) {
				Out.debug("==============solorankid after sort:", prop.iD);
			}
		}
	}

	private void initSoloSystem() {
		SoloSystemPO syspo = GameDao.get(String.valueOf(GWorld.__SERVER_ID), ConstsTR.soloSystemTR, SoloSystemPO.class);
		if (syspo != null) {
			this.soloSystem = syspo;
		} else {
			this.soloSystem = new SoloSystemPO(String.valueOf(GWorld.__SERVER_ID));
			this.soloSystem.seasonEndTime = calcSoloSeasonTime();
			this.updateSoloSystemToRedis();
		}
	}

	private Date calcSoloSeasonTime() {
		Date endDate = _calcSeasonEndTime(GlobalConfig.Solo_SeasonDay, GlobalConfig.Solo_SeasonWeekday);
		if (endDate.getTime() - System.currentTimeMillis() < MIN_SEASON_MILLISEC) {// 小于最短赛季天数就直接再加一周时间
			endDate = _calcSeasonEndTime(GlobalConfig.Solo_SeasonDay + 7, GlobalConfig.Solo_SeasonWeekday);
		}
		return endDate;
	}

	/**
	 * 返回赛季结束日期的23:59:59.999Date
	 * 
	 * @param term 赛季周期天数，期望几日后结束
	 * @param endWeekDay 期望几日后的周几结束 0~6对应周日~周六 任何大于6都当0处理
	 * @return
	 */
	private static Date _calcSeasonEndTime(int term, int endWeekDay) {
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
	 * 根据给定的资历积分查找对应的段位id
	 * 
	 * @param score
	 * @return
	 */
	public int calcRankId(int score) {
		int rankId = 0;
		for (SoloRankCO rank : soloRanks) {// 在正排序的列表里查找最高的rankId
			if (score >= rank.rankScore) {
				rankId = rank.iD;
			}
		}
		return rankId;
	}

	public int getTerm() {
		return this.soloSystem.term;
	}

	public int getRounds() {
		return this.soloSystem.rounds;
	}

	/**
	 * 新增一条传闻消息，超过上限就清除最老的消息
	 * 
	 * @param news
	 */
	public void addSoloNews(String news) {
		List<String> msgs = this.soloSystem.soloNewses;
		if (msgs.size() >= GlobalConfig.Solo_SoloNews_Number) {
			msgs.remove(0);
		}
		msgs.add(news);
		updateSoloSystemToRedis();
	}

	/**
	 * 获取所有的传言消息
	 * 
	 * @return
	 */
	public List<String> getAllSoloNews() {
		return this.soloSystem.soloNewses;
	}

	private void updateSoloSystemToRedis() {
		GameDao.update(String.valueOf(GWorld.__SERVER_ID), ConstsTR.soloSystemTR, this.soloSystem);
	}

	private final static int RANK_LIST_LIMIT = 500;
	private final static int MAX_REWARD_RANK = 10000;

	// 发送赛季奖励
	private void sendSeasonReward() {
		int start = 0;
		int end = start + RANK_LIST_LIMIT;
		int season = this.getTerm();

		List<LeaderBoardDetail> rankList = RankType.SOLO_SCORE.getHandler().getRankDetail(GWorld.__SERVER_ID, season, start, end);
		while (rankList.size() > 0) {
			Out.debug("rank list end limit :", end);
			for (LeaderBoardDetail board : rankList) {
				MailUtil.getInstance().sendMailToOnePlayer(board.memberId, generateRewardMail(board.rank), GOODS_CHANGE_TYPE.solo);
			}
			start += RANK_LIST_LIMIT;
			end += RANK_LIST_LIMIT;
			if (end > MAX_REWARD_RANK) {
				break;
			}
			rankList = RankType.SOLO_SCORE.getHandler().getRankDetail(GWorld.__SERVER_ID, season, start, end);
		}
	};

	/**
	 * 根据排名生成奖励邮件
	 * 
	 * @param rank
	 * @return
	 */
	private MailSysData generateRewardMail(long rank) {
		Collection<SoloRankSeasonRewardExt> props = GameData.SoloRankSeasonRewards.values();
		for (SoloRankSeasonRewardExt prop : props) {
			if (rank >= prop.startRank && rank <= prop.stopRank) {
				ArrayList<Attachment> list = new ArrayList<>();
				MailSysData mailData = new MailSysData(SysMailConst.SOLO_REWARD);
				mailData.replace = new HashMap<>();
				mailData.replace.put("rank", String.valueOf(rank));
				// mailData.replace.put("{score}",String.valueOf(board.score));
				for (String itemCode : prop.rankRewards.keySet()) {
					MailData.Attachment attach = new MailData.Attachment();
					attach.itemCode = itemCode;
					attach.itemNum = prop.rankRewards.get(itemCode);
					list.add(attach);
				}
				mailData.attachments = list;
				return mailData;
			}
		}
		return null;
	}

	/**
	 * 刷新资历到排行榜
	 * 
	 * @param playerId
	 * @param score
	 * @return 返回刷新后的排名
	 */
	public void refreshSoloScoreToLeaderBoard(WNPlayer player, int score) {
		player.rankManager.onEvent(RankType.SOLO_SCORE, this.getTerm(), score);
	}

	public long getRank(String playerId) {
		return RankType.SOLO_SCORE.getHandler().getSeasonRank(GWorld.__SERVER_ID, this.getTerm(), playerId);
	}
}
