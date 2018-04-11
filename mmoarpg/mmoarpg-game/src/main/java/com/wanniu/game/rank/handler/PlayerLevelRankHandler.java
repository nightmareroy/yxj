package com.wanniu.game.rank.handler;

import java.util.List;

import com.wanniu.game.GWorld;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.poes.PlayerRankInfoPO;
import com.wanniu.game.rank.AbstractPlayerRankHandler;
import com.wanniu.game.rank.RankType;

/**
 * 等级排行榜.
 *
 * @author 小流氓(176543888@qq.com)
 */
public class PlayerLevelRankHandler extends AbstractPlayerRankHandler {

	@Override
	protected String getRedisKey(int logicServerId, int season) {
		return RankType.LEVEL.getRedisKey(logicServerId, season);
	}

	@Override
	public void handle(WNPlayer player, Object... value) {
		// 修正个人信息
		PlayerRankInfoPO info = player.rankManager.getRankPO();
		info.setLevel((int) value[0]);// 等级
		info.setUpOrder((int) value[1]);

		// 更新排名rank/10001/FIGHTPOWER_5
		String key = this.getRedisKey(GWorld.__SERVER_ID);
		this.updateRank(key, info.getLevel(), player.getId());
	}

	@Override
	protected void buildRankInfo(List<String> contents, PlayerRankInfoPO player, long score) {
		contents.add(String.valueOf(score));// 4：等级
		contents.add(String.valueOf(player.getFightPower()));// 5：战斗力
		contents.add(String.valueOf(player.getUpOrder()));// 6：境界
		contents.add(player.getGuildName());//7：公会名
	}
}