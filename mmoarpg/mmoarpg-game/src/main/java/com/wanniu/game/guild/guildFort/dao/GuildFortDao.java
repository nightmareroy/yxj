package com.wanniu.game.guild.guildFort.dao;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wanniu.core.util.StringUtil;
import com.wanniu.game.GWorld;
import com.wanniu.game.common.ConstsTR;
import com.wanniu.redis.GameDao;


public class GuildFortDao {
	public static void saveReports(List<GuildFortReportPO> list) {
		String str = JSONObject.toJSONString(list);
		GameDao.update(String.valueOf(GWorld.__SERVER_ID), ConstsTR.guildFortReportTR.value, str);
	}
	
	public static List<GuildFortReportPO> getReports(){
		String str = GameDao.get(String.valueOf(GWorld.__SERVER_ID),ConstsTR.guildFortReportTR.value,String.class);
		List<GuildFortReportPO> list = null;
		if (!StringUtil.isEmpty(str)) {
			list = JSON.parseArray(str, GuildFortReportPO.class);
		} else {
			list = new ArrayList<>();
		}
		
		return list;
	}
	
	
	private static String getKey(int fortId) {
		return ConstsTR.guildFortTR.value + "/" +  fortId;
	}
	
	public static void saveFort(GuildFortPO fort) {
		String key = getKey(fort.fortId);
		GameDao.update(String.valueOf(GWorld.__SERVER_ID), key, fort);
	}
	
	public static GuildFortPO getFort(int fortId) {
		String key = getKey(fortId);
		return  GameDao.get(String.valueOf(GWorld.__SERVER_ID),key,GuildFortPO.class);
	}
}
