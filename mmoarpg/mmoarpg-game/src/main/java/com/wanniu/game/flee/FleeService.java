package com.wanniu.game.flee;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import com.wanniu.core.game.JobFactory;
import com.wanniu.core.logfs.Out;
import com.wanniu.core.util.DateUtil;
import com.wanniu.game.GWorld;
import com.wanniu.game.common.Const;
import com.wanniu.game.common.ConstsTR;
import com.wanniu.game.data.GameData;
import com.wanniu.game.data.ext.BattleRoyaleRankExt;
import com.wanniu.game.data.ext.BattleRoyaleRankSeasonRewardExt;
import com.wanniu.game.leaderBoard.LeaderBoardDetail;
import com.wanniu.game.mail.MailUtil;
import com.wanniu.game.mail.SysMailConst;
import com.wanniu.game.mail.data.MailData.Attachment;
import com.wanniu.game.mail.data.MailSysData;
import com.wanniu.game.player.GlobalConfig;
import com.wanniu.game.poes.FleeSystemPO;
import com.wanniu.game.rank.RankType;
import com.wanniu.redis.GameDao;

/**
 * 大逃杀
 * 
 * @author lxm
 *
 */
public class FleeService {
	private FleeSystemPO fleeSystemPO;

	private static FleeService instance;

	public long sumMatchingTime;// 匹配对手总等待时间
	public int matchedNumber;// 匹配成功的人数

	/**
	 * 正在匹配的玩家
	 */
	private List<String> matchPlayer = new ArrayList<>();

	/**
	 * 赛季最短周期
	 */
	private final static long MIN_SEASON_MILLISEC = TimeUnit.DAYS.toMillis(3);

	public static FleeService getInstance() {
		if (instance == null) {
			instance = new FleeService();
		}
		return instance;
	}

	private FleeService() {
		initFleeSystem();
		// 发放奖励
		long initialDelay = fleeSystemPO.seasonEndTime.getTime() - System.currentTimeMillis();
		JobFactory.addFixedRateJob(new Runnable() {

			@Override
			public void run() {
				try {
					List<LeaderBoardDetail> datas = RankType.FLEE.getHandler().getRankDetail(GWorld.__SERVER_ID, 0, -1);
					int rank = 0;
					for (LeaderBoardDetail detail : datas) {
						rank++;
						Map<Integer, BattleRoyaleRankSeasonRewardExt> personalRanks = GameData.BattleRoyaleRankSeasonRewards;
						BattleRoyaleRankSeasonRewardExt ext = null;
						for (BattleRoyaleRankSeasonRewardExt temp : personalRanks.values()) {
							if (rank >= temp.startRank && rank <= temp.stopRank) {
								ext = temp;
								break;
							}
						}
						if (ext == null) {
							continue;
						}
						Map<String, Integer> rankRewards = ext.rankRewards;
						MailSysData mailData = new MailSysData(SysMailConst.FLEE_REWARD_LAST);
						Map<String, String> replace = new HashMap<>();
						replace.put("rank", String.valueOf(rank));
						mailData.replace = replace;
						mailData.attachments = new ArrayList<>();

						for (Entry<String, Integer> attach : rankRewards.entrySet()) {
							Attachment item = new Attachment();
							item.itemCode = attach.getKey();
							item.itemNum = attach.getValue();
							mailData.attachments.add(item);
						}
						MailUtil.getInstance().sendMailToOnePlayer(detail.memberId, mailData, Const.GOODS_CHANGE_TYPE.flee);
					}
					RankType.FLEE.getHandler().delRank(GWorld.__SERVER_ID);
					fleeSystemPO.seasonEndTime = calcSoloSeasonTime();
					GameDao.update(String.valueOf(GWorld.__SERVER_ID), ConstsTR.fleeSystemTR, fleeSystemPO);
				} catch (Exception e) {
					Out.error(e);
				}
			}
		}, initialDelay, TimeUnit.DAYS.toMillis(7));

	}

	/**
	 * 初始化系统配置
	 */
	private void initFleeSystem() {
		FleeSystemPO syspo = GameDao.get(String.valueOf(GWorld.__SERVER_ID), ConstsTR.fleeSystemTR, FleeSystemPO.class);
		if (syspo != null) {
			this.fleeSystemPO = syspo;
		} else {
			this.fleeSystemPO = new FleeSystemPO(String.valueOf(GWorld.__SERVER_ID));
			this.fleeSystemPO.seasonEndTime = calcSoloSeasonTime();
			GameDao.update(String.valueOf(GWorld.__SERVER_ID), ConstsTR.fleeSystemTR, this.fleeSystemPO);
		}
	}

	/**
	 * 计算赛季结束时间
	 * 
	 * @return
	 */
	private Date calcSoloSeasonTime() {
		Date endDate = calcSeasonEndTime(GlobalConfig.Flee_SeasonDay, GlobalConfig.Flee_SeasonWeekday);
		if (endDate.getTime() - System.currentTimeMillis() < MIN_SEASON_MILLISEC) {// 小于最短赛季天数就直接再加一周时间
			endDate = calcSeasonEndTime(GlobalConfig.Flee_SeasonDay + 7, GlobalConfig.Flee_SeasonWeekday);
		}
		return endDate;
	}

	/**
	 * 返回赛季结束日期的5点钟时间
	 * 
	 * @param term 赛季周期天数，期望几日后结束
	 * @param endWeekDay 期望几日后的周几结束 1~6对应周一~周六 任何大于6都当0处理
	 * @return
	 */
	private Date calcSeasonEndTime(int term, int endWeekDay) {
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
	 * 获取赛季结束时间
	 * 
	 * @return
	 */
	public Date getSeasonEndDate() {
		return fleeSystemPO.seasonEndTime;
	}

	/**
	 * 获取玩家大逃杀排名
	 * 
	 * @param playerId
	 * @return
	 */
	public int getPlayerRank(String playerId) {
		return (int) RankType.FLEE.getHandler().getRank(GWorld.__SERVER_ID, playerId);
	}

	public List<String> getMatchPlayer() {
		return this.matchPlayer;
	}

	/**
	 * 根据积分获取段位
	 * 
	 * @param score
	 * @return
	 */
	public int getGradeIdByScore(int score) {
		int max = 1;
		for (BattleRoyaleRankExt b : GameData.BattleRoyaleRanks.values()) {
			if (b.iD > max && score >= b.rankScore) {
				max = b.iD;
			}
		}
		return max;
	}

	/**
	 * 判断大逃杀是否在开放时间段内
	 * 
	 * @return
	 */
	public boolean isInOpenTime() {
		Calendar c = Calendar.getInstance();
		int weekDay = c.get(Calendar.DAY_OF_WEEK) - 1;
		if (!GlobalConfig.Flee_Weekly_OpenTime.contains(String.valueOf(weekDay))) {
			return false;
		}
		String[] openTime = GlobalConfig.Flee_Daily_OpenTime.split(",");
		Date nowTime = new Date();
		if (nowTime.after(DateUtil.format(openTime[0])) && nowTime.before(DateUtil.format(openTime[1]))) {
			return true;
		}

		return false;
	}

	/**
	 * 获取平均匹配等待时间秒数
	 * 
	 * @return
	 */
	public int getAvgMatchingTime() {
		if (this.matchedNumber <= 0) {
			return GlobalConfig.Flee_MATCH_TIME;
		}
		int second = (int) (this.sumMatchingTime / this.matchedNumber) / 1000;
		if (second > GlobalConfig.Flee_MATCH_TIME) {
			second = GlobalConfig.Flee_MATCH_TIME;
		}
		return second;
	}

	/**
	 * 更新匹配时间和匹配人数
	 * 
	 * @param matchedTime
	 */
	public void updateMatchingTime(long matchedTime) {
		this.matchedNumber += 1;
		this.sumMatchingTime += matchedTime;
	}
}
