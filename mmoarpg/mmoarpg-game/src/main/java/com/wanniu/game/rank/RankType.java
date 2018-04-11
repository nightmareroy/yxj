package com.wanniu.game.rank;

import com.wanniu.game.rank.handler.ArenaScoreAllRankHandler;
import com.wanniu.game.rank.handler.ArenaScoreRankHandler;
import com.wanniu.game.rank.handler.DaoYouRankHandler;
import com.wanniu.game.rank.handler.Five2FiveRankHandler;
import com.wanniu.game.rank.handler.FleeRankHandler;
import com.wanniu.game.rank.handler.GemRankHandler;
import com.wanniu.game.rank.handler.GuildBossPreRankGuildHandler;
import com.wanniu.game.rank.handler.GuildBossPreRankSingleHandler;
import com.wanniu.game.rank.handler.GuildBossRankGuildHandler;
import com.wanniu.game.rank.handler.GuildBossRankSingleHandler;
import com.wanniu.game.rank.handler.GuildFortRankHandler;
import com.wanniu.game.rank.handler.GuildLevelRankHandler;
import com.wanniu.game.rank.handler.MountRankHandler;
import com.wanniu.game.rank.handler.PetRankHandler;
import com.wanniu.game.rank.handler.PlayerDemonTowerRankHandler;
import com.wanniu.game.rank.handler.PlayerFightPowerRankHandler;
import com.wanniu.game.rank.handler.PlayerFightPower_1RankHandler;
import com.wanniu.game.rank.handler.PlayerFightPower_3RankHandler;
import com.wanniu.game.rank.handler.PlayerFightPower_5RankHandler;
import com.wanniu.game.rank.handler.PlayerHpRankHandler;
import com.wanniu.game.rank.handler.PlayerLevelRankHandler;
import com.wanniu.game.rank.handler.PlayerMagRankHandler;
import com.wanniu.game.rank.handler.PlayerPhyRankHandler;
import com.wanniu.game.rank.handler.PlayerXianYuanRankHandler;
import com.wanniu.game.rank.handler.SoloRankHandler;

public enum RankType {
	FIGHTPOWER(101, new PlayerFightPowerRankHandler()), // 战力本服榜
	FIGHTPOWER_1(102, new PlayerFightPower_1RankHandler()), // 战力苍狼榜
	// FIGHTPOWER_2(103, new PlayerFightPowerRankHandler()), // 战力御剑榜
	FIGHTPOWER_3(104, new PlayerFightPower_3RankHandler()), // 战力逸仙榜
	// FIGHTPOWER_4(105, new PlayerFightPowerRankHandler()), // 战力神箭榜
	FIGHTPOWER_5(106, new PlayerFightPower_5RankHandler()), // 战力灵狐榜

	LEVEL(200, new PlayerLevelRankHandler()), // 本服等级榜,

	GUILD_LEVEL(300, new GuildLevelRankHandler()), // 仙盟等级榜
	GUILD_FORT(400,new GuildFortRankHandler()), // 仙盟据点战胜利场次排行榜

	Mount(500, new MountRankHandler()), // 本服坐骑榜
	PET(600, new PetRankHandler()), // 本服宠物榜
	XIANYUAN(700, new PlayerXianYuanRankHandler()), // 仙缘榜
	HP(800, new PlayerHpRankHandler()), // 生命榜
	PHY(801, new PlayerPhyRankHandler()), // 物攻榜
	MAGIC(802, new PlayerMagRankHandler()), // 魔攻榜
	FLEE(900, new FleeRankHandler()), // 大逃杀

	PVP_5V5(1003, new Five2FiveRankHandler()), // 试炼大赛
	GemLevel(1100, new GemRankHandler()), // 宝石

	DAOYOU(2005, DaoYouRankHandler.getInstance()), // 道友排行榜

	// 赛季排行榜 必须后缀 _seasonRankTR
	SOLO_SCORE(2006, new SoloRankHandler()) {
		@Override
		public String getRedisKey(int serverId, int season) {
			return new StringBuilder(32).append("rank/").append(serverId).append("/").append(this.name()).append("-").append(season).toString();
		}
	}, // 问道大会资历榜
	ARENA_SCORE(2007, ArenaScoreRankHandler.getInstance()), // 竞技场（五岳一战）每日积分排行
	ARENA_SCOREALL(2009, ArenaScoreAllRankHandler.getInstance()) {
		@Override
		public String getRedisKey(int serverId, int season) {
			return new StringBuilder(32).append("rank/").append(serverId).append("/").append(this.name()).append("-").append(season).toString();
		}
	}, // 五岳一战总积分排行
	DEMON_TOWER(2100, new PlayerDemonTowerRankHandler()), // 镇妖塔
	GUILD_BOSS_SINGLE(2200, GuildBossRankSingleHandler.getInstance()), // 工会BOSS个人伤害排行
	GUILD_BOSS_PRE_SINGLE(2201, GuildBossPreRankSingleHandler.getInstance()), // 工会BOSS昨日个人伤害排行
	GUILD_BOSS_GUILD(2300, GuildBossRankGuildHandler.getInstance()), // 工会BOSS工会伤害排行
	GUILD_BOSS_PRE_GUILD(2301, GuildBossPreRankGuildHandler.getInstance()), // 工会BOSS昨日伤害排行
	;

	private final int value;
	private final AbstractRankHandler handler;

	private RankType(int value) {
		this(value, null);
	}

	private RankType(int value, AbstractRankHandler handler) {
		this.value = value;
		this.handler = handler;
	}

	/**
	 * 获取排行榜在Redis中的Key值
	 * 
	 * @param serverId 服务器ID
	 * @return Redis中的Key
	 */
	public String getRedisKey(int serverId, int season) {
		return new StringBuilder(32).append("rank/").append(serverId).append("/").append(this.name()).toString();
	}

	public int getValue() {
		return value;
	}

	public AbstractRankHandler getHandler() {
		return handler;
	}

	public static RankType valueOf(int value) {
		for (RankType e : RankType.values()) {
			if (e.value == value) {
				return e;
			}
		}
		return null;
	}
}