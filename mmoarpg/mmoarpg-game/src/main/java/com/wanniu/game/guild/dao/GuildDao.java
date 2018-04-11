package com.wanniu.game.guild.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

import com.alibaba.fastjson.JSON;
import com.wanniu.core.GSystem;
import com.wanniu.core.db.GCache;
import com.wanniu.core.db.ModifyDataType;
import com.wanniu.core.logfs.Out;
import com.wanniu.core.util.StringUtil;
import com.wanniu.game.GWorld;
import com.wanniu.game.common.ConstsTR;
import com.wanniu.game.common.Utils;
import com.wanniu.game.guild.GuildCommonUtil;
import com.wanniu.game.guild.guidDepot.GuildAuctionLog;
import com.wanniu.game.guild.guidDepot.GuildRecordData;
import com.wanniu.game.guild.guildDungeon.GuildDungeonRecord;
import com.wanniu.game.guild.guildDungeon.GuildDungeonThrowInfo;
import com.wanniu.game.poes.GuildDungeonPO;
import com.wanniu.game.poes.GuildPO;
import com.wanniu.game.rank.RankType;
import com.wanniu.redis.GameDao;

import pomelo.guild.GuildManagerHandler.RankInfo;
import redis.clients.jedis.Jedis;

public class GuildDao {

	public static final Map<String, GuildPO> GuildMap = new ConcurrentHashMap<>();
	public static final Map<String, String> GuildNameMap = new ConcurrentHashMap<>();
	public static final Map<String, List<GuildRecordData>> GuildNewsMap = new ConcurrentHashMap<>();
	public static final Map<String, List<GuildAuctionLog>> GuildAuctionLogMap = new ConcurrentHashMap<>();

	public static ScheduledFuture<?> future = null;

	public static void init() {
		List<GuildPO> guilds = GuildCommonUtil.hgetAll(ConstsTR.guildTR, GuildPO.class);
		for (GuildPO guild : guilds) {
			if (!GWorld.sids.contains(guild.logicServerId)) {
				continue;// 逻辑服过滤
			}
			GuildMap.put(guild.id, guild);
			GuildNameMap.put(guild.name, guild.id);
		}
		future = GSystem.addFixedRateJob(() -> {
			checkModifyGuild();
		}, 30000, 30000);
	}

	public static void onCloseGame() {
		if (future != null) {
			future.cancel(true);
		}
		checkModifyGuild();
	}

	public static void checkModifyGuild() {
		for (GuildPO guild : GuildMap.values()) {
			if (guild.modify) {
				try {
					guild.modify = false;
					GCache.hset(ConstsTR.guildTR.value, guild.id, Utils.serialize(guild));
					GameDao.updateToDB(ConstsTR.guildTR, guild.id, ModifyDataType.MAP);
				} catch (Exception e) {
					Out.error(e);
				}
			}
		}
	}

	public static GuildPO getGuild(String id) {
		return GuildMap.get(id);
	}

	public static List<GuildPO> getGuildList(int logicServerId, String name) {
		List<GuildPO> list = new ArrayList<GuildPO>();
		for (GuildPO guild : GuildMap.values()) {
			if (!GWorld.sids.contains(guild.logicServerId)) {
				continue;// 逻辑服过滤
			}
			if (StringUtil.isNotEmpty(name) && guild.name.indexOf(name) == -1) {
				continue;// 名字过滤
			}
			list.add(guild);
		}
		return list;
	}
	
	public static Collection<GuildPO> getAllGuild(){
		return GuildMap.values();
	}

	public static void updateGuild(GuildPO data) {
		GuildMap.put(data.id, data);
		GuildNameMap.put(data.name, data.id);
		data.modify = true;
	}

	public static void removeGuild(GuildPO data) {
		GuildMap.remove(data.id);
		GuildNameMap.remove(data.name);
		GuildNewsMap.remove(data.id);
		GuildAuctionLogMap.remove(data.id);

		GCache.hremove(ConstsTR.guildTR.value, data.id);
		GCache.hremove(ConstsTR.guildNewsTR.value, data.id);
		GCache.hremove(ConstsTR.guildAuctionLogTR.value, data.id);
		GCache.hremove(ConstsTR.guildDepotTR.value, data.id);
		GCache.hremove(ConstsTR.guildBlessTR.value, data.id);
		GameDao.delToDB(ConstsTR.guildDepotTR, data.id); // 删除公会仓库
		GameDao.delToDB(ConstsTR.guildBlessTR, data.id); // 删除公会祈福
		GameDao.delToDB(ConstsTR.guildTR, data.id);

		// 删除排行榜公会信息
		RankType.GUILD_LEVEL.getHandler().delRankMember(GWorld.__SERVER_ID, data.id);
		RankType.GUILD_BOSS_GUILD.getHandler().delRankMember(GWorld.__SERVER_ID, data.id);
	}

	public static List<GuildRecordData> getGuildNews(String guildId) {
		List<GuildRecordData> news = GuildNewsMap.get(guildId);
		if (news == null) {
			String data = GCache.hget(ConstsTR.guildNewsTR.value, guildId);
			news = StringUtil.isNotEmpty(data) ? JSON.parseArray(data, GuildRecordData.class) : new ArrayList<>();
			GuildNewsMap.put(guildId, news);
		}
		return news;
	}

	public static void saveGuildNews(String guildId) {
		List<GuildRecordData> news = GuildNewsMap.get(guildId);
		if (news != null) {
			GCache.hset(ConstsTR.guildNewsTR.value, guildId, JSON.toJSONString(news));
		}
	}

	public static String getGuildIdByName(String name) {
		return GuildNameMap.get(name);
	}

	public static void removeGuildNameIndex(String name) {
		GuildNameMap.remove(name);
	}

	public static GuildDungeonPO getGuildDungeon(String id) {
		// 从redis缓存中读取数据
		GuildDungeonPO data = GuildCommonUtil.hget(ConstsTR.guildDungeonTR, id, GuildDungeonPO.class);
		if (null != data) {
			return data;
		}

		GuildDungeonPO dungeonInfo = new GuildDungeonPO();
		dungeonInfo.id = id;
		dungeonInfo.openTimesToday = 0;
		dungeonInfo.openState = 0;
		dungeonInfo.currPassedCount = 0;
		dungeonInfo.totalPassedCount = 0;
		dungeonInfo.dungeonPassedTime = new Date();
		dungeonInfo.openTime = new Date();
		dungeonInfo.dungeonRecord = new HashMap<Integer, GuildDungeonRecord>();
		dungeonInfo.instanceId = "";
		dungeonInfo.serverId = "";
		dungeonInfo.throwInfo = new ArrayList<GuildDungeonThrowInfo>();
		dungeonInfo.damagePlayer = new HashMap<Integer, ArrayList<String>>();
		dungeonInfo.damageRankInfo = new ArrayList<RankInfo>();
		dungeonInfo.healRankInfo = new ArrayList<RankInfo>();

		return dungeonInfo;
	}

	public static void updateGuildDungeon(GuildDungeonPO data) {
		GCache.hset(ConstsTR.guildDungeonTR.value, data.id, Utils.serialize(data));
	}

	public static void updateGuildDungeonToRedis(Jedis redis, GuildDungeonPO data) {
		redis.hset(ConstsTR.guildDungeonTR.value, data.id, Utils.serialize(data));
	}

	public ArrayList<GuildDungeonPO> getAllGuildDungeon() {
		try {
			return GuildCommonUtil.hgetAll(ConstsTR.guildDungeonTR, GuildDungeonPO.class);
		} catch (Exception e) {
			Out.error(e);
			return null;
		}

	}

	/**
	 * 获取所有仙盟竞拍的日志.
	 */
	public static List<GuildAuctionLog> getGuildAuctionLog(String guildId) {
		List<GuildAuctionLog> logs = GuildAuctionLogMap.get(guildId);
		if (logs == null) {
			String data = GCache.hget(ConstsTR.guildAuctionLogTR.value, guildId);
			logs = StringUtil.isNotEmpty(data) ? JSON.parseArray(data, GuildAuctionLog.class) : new ArrayList<>();
			GuildAuctionLogMap.put(guildId, logs);
		}
		return logs;
	}

	/**
	 * 保存所有仙盟竞拍的日志.
	 */
	public static void saveGuildAuctionLog(String guildId) {
		List<GuildAuctionLog> logs = GuildAuctionLogMap.get(guildId);
		if (logs != null) {
			GCache.hset(ConstsTR.guildAuctionLogTR.value, guildId, JSON.toJSONString(logs));
		}
	}
}