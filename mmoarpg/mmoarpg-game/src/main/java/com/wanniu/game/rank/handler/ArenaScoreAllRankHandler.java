package com.wanniu.game.rank.handler;

import java.util.List;

import com.wanniu.game.arena.ArenaService;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.poes.PlayerRankInfoPO;
import com.wanniu.game.rank.AbstractPlayerRankHandler;
import com.wanniu.game.rank.RankType;

/**
 * 五岳一战每日积分排行排行榜。
 *
 * @author 小流氓(176543888@qq.com)
 */
public class ArenaScoreAllRankHandler extends AbstractPlayerRankHandler {
	private static final ArenaScoreAllRankHandler instance = new ArenaScoreAllRankHandler();

	public static ArenaScoreAllRankHandler getInstance() {
		return instance;
	}

	@Override
	protected String getRedisKey(int logicServerId, int season) {
		return RankType.ARENA_SCOREALL.getRedisKey(logicServerId, season);
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
		String key = this.getRedisKey(serverId, this.getCurrentSeason());
		this.updateRank(key, score, playerId);
	}

	@Override
	public int getCurrentSeason() {
		return ArenaService.getInstance().getTerm();
	}

	@Override
	public int getLastSeason() {
		int term = this.getCurrentSeason() - 1;
		return term < 0 ? 0 : term;
	}
}