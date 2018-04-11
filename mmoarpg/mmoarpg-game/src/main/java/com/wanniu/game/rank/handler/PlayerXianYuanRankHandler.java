package com.wanniu.game.rank.handler;

import java.util.List;

import com.wanniu.game.GWorld;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.poes.PlayerRankInfoPO;
import com.wanniu.game.rank.AbstractPlayerRankHandler;
import com.wanniu.game.rank.RankType;

/**
 * 仙缘榜.
 *
 * @author 小流氓(176543888@qq.com)
 */
public class PlayerXianYuanRankHandler extends AbstractPlayerRankHandler {

	@Override
	protected String getRedisKey(int logicServerId, int season) {
		return RankType.XIANYUAN.getRedisKey(logicServerId, season);
	}

	@Override
	public void handle(WNPlayer player, Object... value) {
		// 修正个人信息
		PlayerRankInfoPO info = player.rankManager.getRankPO();
		info.setXianyuan((int) value[0]);// 仙缘值

		// 更新排名
		String key = this.getRedisKey(GWorld.__SERVER_ID);
		this.updateRank(key, info.getXianyuan(), player.getId());
	}

	@Override
	protected void buildRankInfo(List<String> contents, PlayerRankInfoPO player, long score) {
		contents.add(String.valueOf(player.getLevel()));// 4：等级
		contents.add(String.valueOf(player.getFightPower()));// 5：战斗力
		contents.add(String.valueOf(score));// 6：仙缘值
		contents.add(player.getGuildName());//7：公会名
	}
}