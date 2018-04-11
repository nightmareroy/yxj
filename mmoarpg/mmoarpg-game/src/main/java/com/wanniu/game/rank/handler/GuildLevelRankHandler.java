package com.wanniu.game.rank.handler;

import com.wanniu.game.GWorld;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.poes.GuildPO;
import com.wanniu.game.rank.AbstractGuildRankHandler;
import com.wanniu.game.rank.RankType;

/**
 * 公会等级排行榜.
 *
 * @author 小流氓(176543888@qq.com)
 */
public class GuildLevelRankHandler extends AbstractGuildRankHandler {

	@Override
	protected String getRedisKey(int logicServerId, int season) {
		return RankType.GUILD_LEVEL.getRedisKey(logicServerId, season);
	}

	@Override
	public void handle(WNPlayer player, Object... value) {}

	@Override
	public void handle(GuildPO guild) {
		// 更新排名
		String key = this.getRedisKey(GWorld.__SERVER_ID);
		// 公会等级，成员总战力，再最后时间
		long sumFund = guild.sumFund > 1_0000_0000L ? 9999_9999L : guild.sumFund;
		this.updateRank(key, guild.level * 1_0000_0000L + sumFund, guild.id);
	}
}