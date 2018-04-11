package com.wanniu.game.rank.handler;

import java.util.List;

import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.poes.PlayerRankInfoPO;
import com.wanniu.game.rank.AbstractPlayerRankHandler;
import com.wanniu.game.rank.RankType;

/**
 * 道友排行榜。
 *
 * @author 小流氓(176543888@qq.com)
 */
public class DaoYouRankHandler extends AbstractPlayerRankHandler {
	private static final DaoYouRankHandler instance = new DaoYouRankHandler();

	public static DaoYouRankHandler getInstance() {
		return instance;
	}

	@Override
	protected String getRedisKey(int logicServerId, int season) {
		return RankType.DAOYOU.getRedisKey(logicServerId, season);
	}

	@Override
	public void handle(WNPlayer player, Object... value) {}

	@Override
	protected void buildRankInfo(List<String> contents, PlayerRankInfoPO player, long score) {
		contents.add(String.valueOf(player.getLevel()));// 4：等级
		contents.add(String.valueOf(player.getFightPower()));// 5：战斗力
		contents.add(String.valueOf(score));// 6：5V5
	}

	public void asyncUpdateRank(int serverId, String memberId, int score) {
		this.updateRank(this.getRedisKey(serverId), score, memberId);
	}
}