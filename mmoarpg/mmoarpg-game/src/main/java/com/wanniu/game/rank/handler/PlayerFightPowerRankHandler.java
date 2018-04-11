package com.wanniu.game.rank.handler;

import java.util.List;

import com.wanniu.core.logfs.Out;
import com.wanniu.game.GWorld;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.poes.PlayerRankInfoPO;
import com.wanniu.game.rank.AbstractPlayerRankHandler;
import com.wanniu.game.rank.RankType;

/**
 * 战斗力排行榜.
 *
 * @author 小流氓(176543888@qq.com)
 */
public class PlayerFightPowerRankHandler extends AbstractPlayerRankHandler {

	@Override
	protected String getRedisKey(int logicServerId, int season) {
		return RankType.FIGHTPOWER.getRedisKey(logicServerId, season);
	}

	@Override
	public void handle(WNPlayer player, Object... value) {
		// 修正个人信息
		PlayerRankInfoPO info = player.rankManager.getRankPO();
		info.setFightPower((int) value[0]);// 战斗力...

		// 更新排名
		String key = this.getRedisKey(GWorld.__SERVER_ID);
		this.updateRank(key, info.getFightPower(), player.getId());

		// 更新子榜排名
		switch (player.getPro()) {
		case 1:
			this.updateRank(RankType.FIGHTPOWER_1.getRedisKey(GWorld.__SERVER_ID, 0), info.getFightPower(), player.getId());
			break;
		case 3:
			this.updateRank(RankType.FIGHTPOWER_3.getRedisKey(GWorld.__SERVER_ID, 0), info.getFightPower(), player.getId());
			break;
		case 5:
			this.updateRank(RankType.FIGHTPOWER_5.getRedisKey(GWorld.__SERVER_ID, 0), info.getFightPower(), player.getId());
			break;
		default:
			Out.warn("未实现的职业排行榜. pro=", player.getPro());
			break;
		}
	}

	@Override
	protected void buildRankInfo(List<String> contents, PlayerRankInfoPO player, long score) {
		contents.add(String.valueOf(player.getLevel()));// 4：等级
		contents.add(String.valueOf(score));// 5：战斗力
		contents.add(player.getGuildName());// 公会名
	}
}