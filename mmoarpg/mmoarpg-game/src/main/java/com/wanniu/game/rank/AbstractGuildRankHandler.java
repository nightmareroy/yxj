package com.wanniu.game.rank;

import java.util.ArrayList;
import java.util.List;

import com.wanniu.game.guild.GuildServiceCenter;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.poes.GuildPO;

import pomelo.area.LeaderBoardHandler.LeaderBoardData;
import pomelo.revelry.ActivityRevelryHandler.RevelryRankInfo;

/**
 * 公会抽象的处理类.
 *
 * @author 小流氓(176543888@qq.com)
 */
public abstract class AbstractGuildRankHandler extends AbstractRankHandler {

	@Override
	public String getSelfId(WNPlayer player) {
		return player.guildManager.getGuildId();
	}

	@Override
	public String getSelfName(WNPlayer player) {
		return player.guildManager.getGuildName();
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
		builder.addAllContents(contents);
		return builder.build();
	}

	@Override
	public void buildRevelryRankInfo(RevelryRankInfo.Builder info, SimpleRankData rankData) {
		GuildPO guild = GuildServiceCenter.getInstance().getGuild(rankData.getId());
		if (guild != null) {
			// 0:排名
			info.addContents(String.valueOf(rankData.getRank()));
			// 1:排行分数
			info.addContents(String.valueOf(guild.level));
			// 2:仙盟名称
			info.addContents(guild.name);
			// 3:仙盟图标
			info.addContents(guild.icon);
			// 4:仙盟等级
			info.addContents(String.valueOf(guild.level));
		}
	}
}