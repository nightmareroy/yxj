package com.wanniu.game.rank.handler;

import com.wanniu.game.rank.RankType;

/**
 * 战斗力排行榜.
 *
 * @author 小流氓(176543888@qq.com)
 */
public class PlayerFightPower_1RankHandler extends PlayerFightPowerRankHandler {

	@Override
	protected String getRedisKey(int logicServerId, int season) {
		return RankType.FIGHTPOWER_1.getRedisKey(logicServerId, season);
	}
}