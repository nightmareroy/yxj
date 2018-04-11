package com.wanniu.game.guild.dao;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.wanniu.core.db.GCache;
import com.wanniu.core.db.ModifyDataType;
import com.wanniu.game.common.ConstsTR;
import com.wanniu.game.common.Utils;
import com.wanniu.game.guild.GuildCommonUtil;
import com.wanniu.game.poes.GuildMemberPO;
import com.wanniu.redis.GameDao;

public class GuildMemberDao {

	public static final Map<String, GuildMemberPO> PlayerMemberMap = new ConcurrentHashMap<>();
	public static final Map<String, Set<String>> GuildMemberMap = new ConcurrentHashMap<>();

	public static void init() {
		ArrayList<GuildMemberPO> members = GuildCommonUtil.hgetAll(ConstsTR.guildMemberTR, GuildMemberPO.class);
		for (GuildMemberPO member : members) {
			addGuildMember(member);
		}
	}

	public static void addGuildMember(GuildMemberPO member) {
		PlayerMemberMap.put(member.playerId, member);
		Set<String> players = GuildMemberMap.get(member.guildId);
		if (players == null) {
			players = new HashSet<>();
			GuildMemberMap.put(member.guildId, players);
		}
		players.add(member.playerId);
	}

	/**
	 * 获取公会成员信息
	 */
	public static GuildMemberPO getGuildMember(String playerId) {
		return PlayerMemberMap.get(playerId);
	}

	public static Set<String> getGuildMemberIdList(String guildId) {
		return GuildMemberMap.get(guildId);
	}

	public static int getGuildMemberCount(String guildId) {
		Set<String> members = GuildMemberMap.get(guildId);
		return members != null ? members.size() : 0;
	}

	public static void updateGuildMember(GuildMemberPO member) {
		if (!PlayerMemberMap.containsKey(member.playerId)) {
			addGuildMember(member);
		}
		GCache.hset(ConstsTR.guildMemberTR.value, member.playerId, Utils.serialize(member));
		GameDao.updateToDB(ConstsTR.guildMemberTR, member.playerId, ModifyDataType.MAP);
	}

	public static void removeGuildMember(String playerId) {
		GuildMemberPO member = PlayerMemberMap.remove(playerId);
		if (member != null) {
			Set<String> members = GuildMemberMap.get(member.guildId);
			if (members != null) {
				members.remove(playerId);
			}
			GCache.hremove(ConstsTR.guildMemberTR.value, member.playerId);
			GameDao.delToDB(ConstsTR.guildMemberTR, playerId);
		}
	}

}
