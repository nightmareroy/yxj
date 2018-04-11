package com.wanniu.game.rank.handler;

import java.util.List;

import com.wanniu.core.db.GCache;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.poes.PlayerRankInfoPO;
import com.wanniu.game.rank.AbstractPlayerRankHandler;
import com.wanniu.game.rank.RankType;

/**
 * 五岳一战每日积分排行排行榜。
 *
 * @author 小流氓(176543888@qq.com)
 */
public class ArenaScoreRankHandler extends AbstractPlayerRankHandler {
	private static final ArenaScoreRankHandler instance = new ArenaScoreRankHandler();

	public static ArenaScoreRankHandler getInstance() {
		return instance;
	}

	@Override
	protected String getRedisKey(int logicServerId, int season) {
		return RankType.ARENA_SCORE.getRedisKey(logicServerId, season);
	}

	@Override
	public void handle(WNPlayer player, Object... value) {}

	@Override
	protected void buildRankInfo(List<String> contents, PlayerRankInfoPO player, long score) {
		contents.add(String.valueOf(player.getLevel()));// 4：等级
		contents.add(String.valueOf(player.getFightPower()));// 5：战斗力
		contents.add(String.valueOf(score));// 6： 问道
	}

	public void asyncUpdateRank(int serverId, String playerId, int score) {
		// 小于排行榜中值不更新
		String key = this.getRedisKey(serverId);
		Double oldScore = GCache.zscore(key, playerId);
		if (oldScore != null && oldScore > score) {
			return;
		}
		this.updateRank(key, score, playerId);
	}
}