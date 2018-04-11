package com.wanniu.game.rank.handler;

import java.util.List;

import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.poes.PlayerRankInfoPO;
import com.wanniu.game.rank.AbstractPlayerRankHandler;
import com.wanniu.game.rank.RankType;

/**
 * 5V5排行榜。
 *
 * @author 小流氓(176543888@qq.com)
 */
public class Five2FiveRankHandler extends AbstractPlayerRankHandler {

	@Override
	protected String getRedisKey(int logicServerId, int season) {
		return RankType.PVP_5V5.getRedisKey(logicServerId, season);
	}

	@Override
	public void handle(WNPlayer player, Object... value) {}

	@Override
	protected void buildRankInfo(List<String> contents, PlayerRankInfoPO player, long score) {
		contents.add(String.valueOf(player.getLevel()));// 4：等级
		contents.add(String.valueOf(player.getFightPower()));// 5：战斗力
		contents.add(String.valueOf(score));// 6：5V5
		contents.add(player.getGuildName());//7：公会名
	}
}