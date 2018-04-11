package com.wanniu.game.rank.handler;

import java.util.Collections;
import java.util.List;

import com.wanniu.game.GWorld;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.poes.PlayerRankInfoPO;
import com.wanniu.game.rank.AbstractPlayerRankHandler;
import com.wanniu.game.rank.RankType;

import pomelo.Common.Avatar;

/**
 * 坐骑排行榜.
 *
 * @author 小流氓(176543888@qq.com)
 */
public class MountRankHandler extends AbstractPlayerRankHandler {

	@Override
	protected String getRedisKey(int logicServerId, int season) {
		return RankType.Mount.getRedisKey(logicServerId, season);
	}

	@Override
	public void handle(WNPlayer player, Object... value) {
		// 修正个人信息
		PlayerRankInfoPO info = player.rankManager.getRankPO();
		int oldFightPower = info.getMountFightPower();
		info.setMountFightPower((int) value[0]);
		info.setMountSkinId((int) value[1]);

		// 更新排名
		if (oldFightPower != info.getMountFightPower()) {
			String key = this.getRedisKey(GWorld.__SERVER_ID);
			this.updateRank(key, info.getMountFightPower(), player.getId());
		}
	}

	@Override
	protected void buildRankInfo(List<String> contents, PlayerRankInfoPO player, long score) {
		contents.add(String.valueOf(player.getLevel()));// 4：等级
		contents.add(String.valueOf(score));// 5：坐骑战斗力
		contents.add(String.valueOf(player.getMountSkinId()));// 6:坐骑皮肤
		contents.add(player.getGuildName());//7：公会名
	}

	@Override
	protected List<Avatar> buildAvatarsInfo(PlayerRankInfoPO player) {
		return Collections.emptyList();
	}
}