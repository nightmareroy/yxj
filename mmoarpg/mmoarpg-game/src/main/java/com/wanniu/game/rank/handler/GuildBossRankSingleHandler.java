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

import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.wanniu.core.db.GCache;
import com.wanniu.core.util.StringUtil;
import com.wanniu.game.GWorld;
import com.wanniu.game.guild.guildBoss.GuildBossCenter;
import com.wanniu.game.guild.guildBoss.GuildBossService;
import com.wanniu.game.guild.guildBoss.RankBean;
import com.wanniu.game.leaderBoard.LeaderBoardProto;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.poes.PlayerRankInfoPO;
import com.wanniu.game.rank.AbstractPlayerRankHandler;
import com.wanniu.game.rank.RankType;

import pomelo.area.LeaderBoardHandler.LeaderBoardData;

/**
 * 
 * 工会BOSS个人世界排名
 * 
 * @author Feiling(feiling@qeng.cn)
 */
public class GuildBossRankSingleHandler extends AbstractPlayerRankHandler {

	public static GuildBossRankSingleHandler instance = new GuildBossRankSingleHandler();

	@Override
	public String getRedisKey(int logicServerId, int season) {
		return RankType.GUILD_BOSS_SINGLE.getRedisKey(logicServerId, season) + "/" + "today";
	}

	@Override
	public void buildRankInfo(List<String> contents, PlayerRankInfoPO player, long score) {
		contents.add(String.valueOf(player.getLevel()));// 4：等级
		contents.add(String.valueOf(player.getFightPower()));// 5：战斗力
		contents.add(player.getGuildName());// 6：工会名字
		contents.add(String.valueOf(score));// 7：伤害
	}

	@Override
	public LeaderBoardProto getRankData(int serverId, int seasonType, String selfId) {
		LeaderBoardProto result = new LeaderBoardProto();
		List<RankBean> worldRanks = null;
		if (GuildBossCenter.getInstance().isOpen()) {// 如果正在打获取实时的数据
			worldRanks = GuildBossService.getInstance().getWorldSingleRank();
		} else {
			String key = getRedisKey(GWorld.__SERVER_ID);//这里不用上面的参数是因为这里必须强制获取当前服的ID,合服的时候要把老服删掉,用新服得
			String vl = GCache.get(key);
			if (vl != null) {
				worldRanks = JSONObject.parseArray(vl, RankBean.class);
			}
		}
		if (worldRanks != null && !worldRanks.isEmpty()) {
			int rank = 1;
			for (RankBean bean : worldRanks) {
				LeaderBoardData build = genBuilderInfo(bean.getId(), bean.getHurt(), rank);
				if (build == null) {
					continue;
				}
				rank++;
				result.s2c_lists.add(build);

				if (selfId.equals(bean.getId())) {
					result.s2c_myData = build;
				}
			}
		}
		return result;
	}

	public void delRankMember(int serverId, String memberId) {
		boolean hasChanged = false;
		List<RankBean> worldRanks = getDataList();
		if (worldRanks != null) {
			for (RankBean bean : worldRanks) {
				if (bean.getId().equals(memberId)) {
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

	public List<RankBean> getDataList() {
		String key = getRedisKey(GWorld.__SERVER_ID);
		String vl = GCache.get(key);
		if (!StringUtil.isEmpty(vl)) {
			return JSONObject.parseArray(vl, RankBean.class);
		}
		return null;
	}

	/**
	 * 直接把排好序的扔上去
	 * 
	 * @param worldRanks
	 */
	public void putStaticData(List<RankBean> worldRanks) {
		String key = getRedisKey(GWorld.__SERVER_ID);
		String strVL = (worldRanks == null || worldRanks.isEmpty()) ? "[]" : JSONObject.toJSONString(worldRanks);
		GCache.put(key, strVL);
	}

	@Override
	public void handle(WNPlayer player, Object... value) {

	}

	private GuildBossRankSingleHandler() {}

	public static GuildBossRankSingleHandler getInstance() {
		return instance;
	}
}
