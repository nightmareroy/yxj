package com.wanniu.game.rank.handler;

import java.util.List;

import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.poes.PlayerRankInfoPO;
import com.wanniu.game.rank.AbstractPlayerRankHandler;
import com.wanniu.game.rank.RankType;

/**
 * 大逃杀排行榜。
 *
 * @author 小流氓(176543888@qq.com)
 */
public class FleeRankHandler extends AbstractPlayerRankHandler {

	@Override
	protected String getRedisKey(int logicServerId, int season) {
		return RankType.FLEE.getRedisKey(logicServerId, season);
	}

	@Override
	public void handle(WNPlayer player, Object... value) {}

	@Override
	protected void buildRankInfo(List<String> contents, PlayerRankInfoPO player, long score) {
		contents.add(String.valueOf(player.getLevel()));// 4：等级
		contents.add(String.valueOf(player.getFightPower()));// 5：战斗力
		contents.add(String.valueOf(score));// 6： 大逃杀
		contents.add(player.getGuildName());//7：公会名
	}
}