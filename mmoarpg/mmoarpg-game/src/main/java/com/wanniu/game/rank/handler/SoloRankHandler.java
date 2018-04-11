package com.wanniu.game.rank.handler;

import java.util.List;

import com.wanniu.game.GWorld;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.poes.PlayerRankInfoPO;
import com.wanniu.game.rank.AbstractPlayerRankHandler;
import com.wanniu.game.rank.RankType;
import com.wanniu.game.solo.SoloService;

/**
 * 问道排行榜。
 *
 * @author 小流氓(176543888@qq.com)
 */
public class SoloRankHandler extends AbstractPlayerRankHandler {

	@Override
	protected String getRedisKey(int logicServerId, int season) {
		return RankType.SOLO_SCORE.getRedisKey(logicServerId, season);
	}

	@Override
	public void handle(WNPlayer player, Object... value) {
		int term = (int) value[0];// 赛季
		int source = (int) value[1];// 问道

		// 更新排名
		String key = this.getRedisKey(GWorld.__SERVER_ID, term);
		this.updateRank(key, source, player.getId());
	}

	@Override
	protected void buildRankInfo(List<String> contents, PlayerRankInfoPO player, long score) {
		contents.add(String.valueOf(player.getLevel()));// 4：等级
		contents.add(String.valueOf(player.getFightPower()));// 5：战斗力
		contents.add(String.valueOf(score));// 6： 问道
		contents.add(player.getGuildName());//7：公会名
	}

	@Override
	public int getCurrentSeason() {
		return SoloService.getInstance().getTerm();
	}

	@Override
	public int getLastSeason() {
		int term = this.getCurrentSeason() - 1;
		return term < 0 ? 0 : term;
	}
}