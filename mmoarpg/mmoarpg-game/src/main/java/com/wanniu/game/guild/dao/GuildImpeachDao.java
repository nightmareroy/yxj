package com.wanniu.game.guild.dao;

import java.util.ArrayList;

import com.wanniu.core.db.GCache;
import com.wanniu.game.common.ConstsTR;
import com.wanniu.game.common.Utils;
import com.wanniu.game.guild.GuildCommonUtil;
import com.wanniu.game.guild.guildImpeach.GuildImpeachData;

public class GuildImpeachDao {
	public static GuildImpeachData getGuildImpeach(String id) {
		return GuildCommonUtil.hget(ConstsTR.guildImpeachTR, id, GuildImpeachData.class);
	}

	public static ArrayList<GuildImpeachData> getImpeachList() {
		return GuildCommonUtil.hgetAll(ConstsTR.guildImpeachTR, GuildImpeachData.class);
	}

	public static void updateGuildImpeach(GuildImpeachData data) {
		updateImpeachToRedis(data);
	}

	public void removeGuildImpeach(String id) {
		GuildImpeachData data = getGuildImpeach(id);
		if (null != data) {
			removeGuildImpeachByData(data);
		}
	}

	public static void removeGuildImpeachByData(GuildImpeachData data) {
		removeImpeachToRedis(data);
	}

	public static void updateImpeachToRedis(GuildImpeachData data) {
		GCache.hset(ConstsTR.guildImpeachTR.value, data.id, Utils.serialize(data));
	}

	public static void removeImpeachToRedis(GuildImpeachData data) {
		GCache.hremove(ConstsTR.guildImpeachTR.value, data.id);
	}
}
