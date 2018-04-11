package com.wanniu.game.rank.handler;

import java.util.ArrayList;
import java.util.List;

import com.wanniu.game.GWorld;
import com.wanniu.game.guild.GuildServiceCenter;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.poes.GuildPO;
import com.wanniu.game.rank.AbstractGuildRankHandler;
import com.wanniu.game.rank.RankType;

import pomelo.area.LeaderBoardHandler.LeaderBoardData;

/**
 * 据点战历史胜利场次排行榜
 * @author fangyue
 */
public class GuildFortRankHandler extends AbstractGuildRankHandler {

	@Override
	protected String getRedisKey(int logicServerId, int season) {
		return RankType.GUILD_FORT.getRedisKey(logicServerId, season);
	}

	/* (non-Javadoc)
	 * @see com.wanniu.game.rank.AbstractRankHandler#handle(com.wanniu.game.player.WNPlayer, java.lang.Object[])
	 */
	public void handle(WNPlayer player, Object... value) {		
		String guildId = (String) value[0];
		long winTimes = (int)value[1];
		
		String key = this.getRedisKey(GWorld.__SERVER_ID);
		this.updateRank(key, winTimes, guildId);
	}

	/**
	 * 生成客户端协议字段
	 */
	@Override
	public LeaderBoardData genBuilderInfo(String memberId, long score, int rank) {
		LeaderBoardData.Builder builder = LeaderBoardData.newBuilder();

		GuildPO guild = GuildServiceCenter.getInstance().getGuild(memberId);
		if (null == guild) {
			return null;
		}

		List<String> contents = new ArrayList<String>(5);
		contents.add(String.valueOf(rank));// 0：排名
		contents.add(memberId);// 1：公会ID
		contents.add(guild.icon);// 2：公会图标
		contents.add(guild.name);// 3：公会名称
		contents.add(String.valueOf(guild.level));// 4：公会等级
		contents.add(String.valueOf(score));//5：历史胜利场次
		builder.addAllContents(contents);
		return builder.build();
	}
	
	
	@Override
	public void handle(GuildPO guild) {
		// 更新排名
		String key = this.getRedisKey(GWorld.__SERVER_ID);
		long winTimes = guild.fortInfo.winTimes;
		this.updateRank(key, winTimes, guild.id);
	}
}