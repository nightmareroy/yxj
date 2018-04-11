package com.wanniu.game.guild.dao;

import java.util.ArrayList;

import com.wanniu.core.db.GCache;
import com.wanniu.game.common.ConstsTR;
import com.wanniu.game.common.Utils;
import com.wanniu.game.guild.GuildCommonUtil;
import com.wanniu.game.poes.GuildDepotPO;

public class GuildDepotDao {
	/**
	 * 公会仓库数据
	 */
	public static GuildDepotPO getDepot(String id) {
		// 从redis缓存中读取数据
		return GuildCommonUtil.hget(ConstsTR.guildDepotTR, id, GuildDepotPO.class);
	}

	public static ArrayList<GuildDepotPO> getDepotList() {
		// 从redis缓存中读取数据
		return GuildCommonUtil.hgetAll(ConstsTR.guildDepotTR, GuildDepotPO.class);
	}

	public static void updateDepot(GuildDepotPO data) {
		GCache.hset(ConstsTR.guildDepotTR.value, data.id, Utils.serialize(data));
	}

	public static void removeDepotToRedis(GuildDepotPO data) {
		GCache.hremove(ConstsTR.guildDepotTR.value, data.id);
	}

}
