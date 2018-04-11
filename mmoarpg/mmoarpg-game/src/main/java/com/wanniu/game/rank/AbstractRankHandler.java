package com.wanniu.game.rank;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.wanniu.core.db.GCache;
import com.wanniu.core.logfs.Out;
import com.wanniu.game.GWorld;
import com.wanniu.game.common.Const;
import com.wanniu.game.leaderBoard.LeaderBoardDetail;
import com.wanniu.game.leaderBoard.LeaderBoardProto;
import com.wanniu.game.player.GlobalConfig;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.poes.GuildPO;

import pomelo.area.LeaderBoardHandler.LeaderBoardData;
import pomelo.revelry.ActivityRevelryHandler.RevelryRankInfo;
import redis.clients.jedis.Tuple;

public abstract class AbstractRankHandler {
	// 定个小目标，2025年...
	protected static final long target;
	static {
		Calendar calendar = Calendar.getInstance();
		try {
			calendar.setTime(new SimpleDateFormat("yyyy-MM-dd").parse("2025-01-01"));
		} catch (ParseException e) {}
		target = calendar.getTimeInMillis();
	}

	/**
	 * @return 获取当前赛季编号.
	 */
	public int getCurrentSeason() {
		return getLastSeason() + 1;
	}

	/**
	 * @return 获取上一赛季编号.
	 */
	public int getLastSeason() {
		return 0;
	}

	/**
	 * 处理排名变更逻辑.
	 * 
	 * @param player 玩家对象.
	 * @param value
	 */
	public abstract void handle(WNPlayer player, Object... value);

	/**
	 * 获取一个带有赛季Key
	 * 
	 * @param logicServerId 服务器编号
	 * @param season 赛季
	 * @return 带有赛季Key
	 */
	protected abstract String getRedisKey(int logicServerId, int season);

	/**
	 * 获取一个不带有赛季Key
	 * 
	 * @param logicServerId 服务器编号
	 * @return 排名Key
	 */
	protected String getRedisKey(int logicServerId) {
		return this.getRedisKey(logicServerId, 0);
	}

	public abstract String getSelfId(WNPlayer player);

	public abstract String getSelfName(WNPlayer player);

	public LeaderBoardProto getRankData(WNPlayer player, int season) {
		return this.getRankData(GWorld.__SERVER_ID, season, getSelfId(player));
	}

	/**
	 * 更新排行到Redis.
	 * <p>
	 * 相同分数要做一个特殊处理，谁先到这个值，谁就第一.
	 * 
	 * @param key 排行Key
	 * @param score 分数值
	 * @param memberId 成员
	 */
	protected void updateRank(String key, long score, String memberId) {
		GCache.zadd(key, Double.parseDouble(score + "." + (target - System.currentTimeMillis()) / 1000), memberId);
	}

	public void handle(GuildPO guild) {}

	public abstract LeaderBoardData genBuilderInfo(String memberId, long score, int rank);

	/**
	 * 获取指定排名信息.
	 * 
	 * @param serverId 服务器编号
	 * @param start 开始值
	 * @param end 结束值
	 * @return 排名信息
	 */
	public List<LeaderBoardDetail> getRankDetail(int serverId, int start, int end) {
		return getRankDetail(serverId, 0, start, end);
	}

	/**
	 * 获取指定赛季排名信息.
	 * 
	 * @param serverId 服务器编号
	 * @param season 第几赛季
	 * @param start 开始值
	 * @param end 结束值
	 * @return 排名信息
	 */
	public List<LeaderBoardDetail> getRankDetail(int serverId, int season, int start, int end) {
		String key = getRedisKey(serverId, season);
		Set<Tuple> tuples = GCache.zrevrangeWithScores(key, start, end);
		List<LeaderBoardDetail> result = new ArrayList<LeaderBoardDetail>(tuples.size());
		int rank = 1;
		for (Tuple tuple : tuples) {
			LeaderBoardDetail detail = new LeaderBoardDetail();
			detail.rank = rank++;
			detail.score = tuple.getScore();
			detail.memberId = tuple.getElement();
			result.add(detail);
		}
		return result;
	}

	/**
	 * 获取指定赛季中第几名.
	 * 
	 * @param serverId 服务器编号
	 * @param season 第几赛季
	 * @param playerId 玩家ID
	 * @return 获取指定赛季中第几名.
	 */
	public long getSeasonRank(int serverId, int season, String playerId) {
		String key = getRedisKey(serverId, season);
		Long rank = GCache.zrevrank(key, playerId);
		return rank == null ? 0 : rank + 1;
	}

	public long getRank(int logicServerId, String playerId) {
		return getSeasonRank(logicServerId, 0, playerId);
	}

	public void delRank(int serverId) {
		GCache.del(getRedisKey(serverId));
	}

	public String getFirstRankMemberId(int serverId) {
		Set<String> members = GCache.zrevrange(getRedisKey(serverId), 0, 1);
		return members.isEmpty() ? "" : members.iterator().next();
	}

	public void asyncUpdateRank(int serverId, String playerId, int score) {
		String key = this.getRedisKey(serverId);
		this.updateRank(key, score, playerId);
	}

	public void delRankMember(int serverId, String memberId) {
		GCache.zrem(getRedisKey(serverId), memberId);
	}

	public LeaderBoardProto getRankData(int serverId, int seasonType, String selfId) {
		int season = 0;
		if (Const.RankSeasonType.LAST_SEASON.value == seasonType) {
			season = this.getLastSeason();
		} else {
			season = this.getCurrentSeason();
		}
		String key = this.getRedisKey(serverId, season);

		LeaderBoardProto result = new LeaderBoardProto();
		int rank = 1;
		for (Tuple tuple : GCache.zrevrangeWithScores(key, 0, GlobalConfig.RankList_Limit - 1)) {
			String memberId = tuple.getElement();
			int score = (int) tuple.getScore();
			try {
				LeaderBoardData build = genBuilderInfo(memberId, score, rank);
				if (build == null) {
					continue;
				}
				rank++;

				result.s2c_lists.add(build);

				if (selfId.equals(memberId)) {
					result.s2c_myData = build;
				}
			} catch (Exception e) {
				Out.warn("排行榜中的展示信息构建失败，忽略此人 playerId=", memberId);
			}
		}
		return result;
	}

	public double getSeasonScore(int logicServerId, int season, String selfId) {
		Double score = GCache.zscore(this.getRedisKey(logicServerId, season), selfId);
		return score == null ? 0 : score;
	}

	// 开服冲榜使用的..............
	public SimpleRankData getSelfRankInfo(boolean isGameOver, String tabID, WNPlayer player) {
		String memberId = this.getSelfId(player);
		int season = this.getCurrentSeason();
		String key = getRedisKey(GWorld.__SERVER_ID, season);

		// 冲榜结束了，需要补上活动编号
		if (isGameOver) {
			key = new StringBuilder(key.length() + 1 + tabID.length()).append(key).append("-").append(tabID).toString();
		}

		Long rank = GCache.zrevrank(key, memberId);
		rank = rank == null ? 0 : rank + 1;

		int score = 0;
		if (rank > 0) {
			Double scorex = GCache.zscore(key, memberId);
			score = (int) (scorex == null ? 0 : scorex);
		}
		SimpleRankData detail = new SimpleRankData();
		detail.setId(memberId);
		detail.setRank(rank.intValue());
		detail.setScore(score);
		return detail;
	}

	/**
	 * 获取冲榜里简易的排行信息
	 */
	public Map<Integer, SimpleRankData> getSimpleRankData(boolean isGameOver, String tabID, int minRank, int maxRank) {
		int season = this.getCurrentSeason();
		String key = getRedisKey(GWorld.__SERVER_ID, season);
		// 冲榜结束了，需要补上活动编号
		if (isGameOver) {
			key = new StringBuilder(key.length() + 1 + tabID.length()).append(key).append("-").append(tabID).toString();
		}

		Set<Tuple> tuples = GCache.zrevrangeWithScores(key, minRank, maxRank);
		Map<Integer, SimpleRankData> result = new HashMap<>();
		int rank = 1;
		for (Tuple tuple : tuples) {
			SimpleRankData detail = new SimpleRankData();
			detail.setId(tuple.getElement());
			detail.setRank(rank++);
			detail.setScore((int) tuple.getScore());
			result.put(detail.getRank(), detail);
		}
		return result;
	}

	/**
	 * 复制排行到另一个Key中.
	 */
	public Map<Integer, SimpleRankData> copyRankToKey(String tabID) {
		int season = this.getCurrentSeason();
		String key = getRedisKey(GWorld.__SERVER_ID, season);
		Set<Tuple> tuples = GCache.zrevrangeWithScores(key, 0, 9999);

		Map<Integer, SimpleRankData> result = new HashMap<>();
		Map<String, Double> scoreMembers = new HashMap<>();
		int rank = 1;
		for (Tuple tuple : tuples) {
			scoreMembers.put(tuple.getElement(), tuple.getScore());

			SimpleRankData detail = new SimpleRankData();
			detail.setId(tuple.getElement());
			detail.setRank(rank++);
			detail.setScore((int) tuple.getScore());

			result.put(detail.getRank(), detail);
		}

		String newKey = key + "-" + tabID;
		GCache.zadd(newKey, scoreMembers);
		// 添加一个超时
		GCache.expire(newKey, 1 * 30 * 24 * 60 * 60);
		return result;
	}

	/**
	 * 构建冲榜活动中排行信息,由子类实现.
	 */
	public abstract void buildRevelryRankInfo(RevelryRankInfo.Builder info, SimpleRankData rankData);
}