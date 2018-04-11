package com.wanniu.game.rank.handler;

import java.util.List;

import com.wanniu.game.GWorld;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.poes.PlayerRankInfoPO;
import com.wanniu.game.rank.AbstractPlayerRankHandler;
import com.wanniu.game.rank.RankType;

/**
 * 生命排行榜.
 *
 * @author 小流氓(176543888@qq.com)
 */
public class PlayerHpRankHandler extends AbstractPlayerRankHandler {

	@Override
	protected String getRedisKey(int logicServerId, int season) {
		return RankType.HP.getRedisKey(logicServerId, season);
	}

	@Override
	public void handle(WNPlayer player, Object... value) {
		// 修正个人信息
		PlayerRankInfoPO info = player.rankManager.getRankPO();
		info.setHp((int) value[0]);// HP

		// 更新排名
		String key = this.getRedisKey(GWorld.__SERVER_ID);
		this.updateRank(key, info.getHp(), player.getId());
	}

	@Override
	protected void buildRankInfo(List<String> contents, PlayerRankInfoPO player, long score) {
		contents.add(String.valueOf(player.getLevel()));// 4：等级
		contents.add(String.valueOf(player.getFightPower()));// 5：战斗力
		contents.add(String.valueOf(score));// 6：HP
		contents.add(player.getGuildName());//7：公会名
	}
}