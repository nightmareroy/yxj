/*
 * Copyright © 2017 qeng.cn All Rights Reserved.
 * 
 * 感谢您加入清源科技，不用多久，您就会升职加薪、当上总经理、出任CEO、迎娶白富美、从此走上人生巅峰
 * 除非符合本公司的商业许可协议，否则不得使用或传播此源码，您可以下载许可协议文件：
 * 
 * 		http://www.noark.xyz/qeng/LICENSE
 *
 * 1、未经许可，任何公司及个人不得以任何方式或理由来修改、使用或传播此源码;
 * 2、禁止在本源码或其他相关源码的基础上发展任何派生版本、修改版本或第三方版本;
 * 3、无论你对源代码做出任何修改和优化，版权都归清源科技所有，我们将保留所有权利;
 * 4、凡侵犯清源科技相关版权或著作权等知识产权者，必依法追究其法律责任，特此郑重法律声明！
 */
package com.wanniu.game.rank.handler;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.wanniu.core.db.GCache;
import com.wanniu.core.util.StringUtil;
import com.wanniu.game.GWorld;
import com.wanniu.game.guild.GuildServiceCenter;
import com.wanniu.game.guild.guildBoss.GuildBossAreaHurtRankCenter;
import com.wanniu.game.guild.guildBoss.GuildBossAreaHurtRankCenter.GuildStaticRankBean;
import com.wanniu.game.leaderBoard.LeaderBoardProto;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.poes.GuildPO;
import com.wanniu.game.rank.AbstractGuildRankHandler;
import com.wanniu.game.rank.RankType;

import pomelo.area.LeaderBoardHandler.LeaderBoardData;

/**
 * 
 * 工会BOSS昨日工会世界排名
 * 
 * @author Feiling(feiling@qeng.cn)
 */
public class GuildBossPreRankGuildHandler extends AbstractGuildRankHandler {
	public static GuildBossPreRankGuildHandler instance = new GuildBossPreRankGuildHandler();

	/**
	 * 直接把排好序的扔上去
	 * 
	 * @param worldRanks
	 */
	public void putStaticData(List<GuildStaticRankBean> worldRanks) {
		String strVL = (worldRanks == null || worldRanks.isEmpty()) ? "[]" : JSONObject.toJSONString(worldRanks);
		String key = getRedisKey(GWorld.__SERVER_ID);
		GCache.put(key, strVL);
	}

	/**
	 * 生成客户端协议字段
	 */

	public LeaderBoardData genBuilderInfo(String memberId, long score, int rank, GuildStaticRankBean bean) {
		LeaderBoardData.Builder builder = LeaderBoardData.newBuilder();

		GuildPO guild = GuildServiceCenter.getInstance().getGuild(memberId);
		if (null == guild) {
			return null;
		}

		List<String> contents = new ArrayList<String>(7);
		contents.add(String.valueOf(rank));// 0：排名
		contents.add(memberId);// 1：公会ID
		contents.add(guild.icon);// 2：公会图标
		contents.add(guild.name);// 3：公会名称
		contents.add(String.valueOf(guild.level));// 4：公会等级
		contents.add(String.valueOf(bean.getTotalHurt()));// 5:总伤害
		contents.add(String.valueOf(bean.getSeconds()));// 6:总的时间花费
		builder.addAllContents(contents);
		return builder.build();
	}

	@Override
	public LeaderBoardProto getRankData(int serverId, int seasonType, String selfId) {
		String key = getRedisKey(GWorld.__SERVER_ID);// 这里不用上面的参数是因为这里必须强制获取当前服的ID,合服的时候要把老服删掉,用新服得
		String vl = GCache.get(key);
		int rank = 1;
		LeaderBoardProto result = new LeaderBoardProto();
		if (vl != null) {
			List<GuildStaticRankBean> worldRanks = JSONObject.parseArray(vl, GuildStaticRankBean.class);
			for (GuildStaticRankBean bean : worldRanks) {
				LeaderBoardData build = genBuilderInfo(bean.getGuildId(), bean.getTotalHurt(), rank, bean);
				if (build == null) {
					continue;
				}
				rank++;
				result.s2c_lists.add(build);

				if (selfId.equals(bean.getGuildId())) {
					result.s2c_myData = build;
				}
			}
		}
		return result;
	}

	@Override
	public void delRankMember(int serverId, String memberId) {
		String key = getRedisKey(serverId);
		String vl = GCache.get(key);
		boolean hasChanged = false;
		if (!StringUtil.isEmpty(vl)) {
			List<GuildStaticRankBean> worldRanks = JSONObject.parseArray(vl, GuildStaticRankBean.class);
			for (GuildStaticRankBean bean : worldRanks) {
				if (bean.getGuildId().equals(memberId)) {
					worldRanks.remove(bean);
					hasChanged = true;
					break;
				}
			}
			if (hasChanged) {
				putStaticData(worldRanks);
			}
		}
	}

	@Override
	public void handle(WNPlayer player, Object... value) {

	}

	@Override
	protected String getRedisKey(int logicServerId, int season) {
		return RankType.GUILD_BOSS_GUILD.getRedisKey(logicServerId, season) + "/" + "preday";
	}

	private GuildBossPreRankGuildHandler() {}

	public static GuildBossPreRankGuildHandler getInstance() {
		return instance;
	}

}
