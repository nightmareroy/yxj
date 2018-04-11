package com.wanniu.redis;

import java.util.List;
import java.util.Set;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.wanniu.core.db.GCache;
import com.wanniu.core.db.ModifyDataType;
import com.wanniu.core.db.ModifyOperateType;
import com.wanniu.core.db.ModifyVo;
import com.wanniu.core.db.QueryVo;
import com.wanniu.core.db.connet.DBClient;
import com.wanniu.core.db.message.DBModifyMessage;
import com.wanniu.core.logfs.Out;
import com.wanniu.core.util.StringUtil;
import com.wanniu.game.common.ConstsTR;
import com.wanniu.game.common.Utils;

public class GameDao {

	public static void update(String key, ConstsTR field, Object po) {
		update(key, field.value, po);
	}

	public static void update(String key, String field, Object po) {
		GCache.hset(key, field, Utils.serialize(po));
	}

	public static <T> T get(String key, String field, TypeReference<T> ref) {
		String data = GCache.hget(key, field);
		T po = null;
		if (StringUtil.isNotEmpty(data)) {
			return JSON.parseObject(data, ref);
		}
		return po;
	}

	public static <T> T get(String key, ConstsTR field, Class<T> clazz) {
		return get(key, field.value, clazz);
	}

	public static <T> T get(String key, String field, Class<T> clazz) {
		String data = GCache.hget(key, field);
		T po = null;
		if (StringUtil.isNotEmpty(data)) {
			Out.debug(data);
			Out.debug(clazz);
			return JSON.parseObject(data, clazz);
		}
		return po;
	}

	public static void updateToDB(ConstsTR tr, String key, ModifyDataType dataType) {
		ModifyVo mv = new ModifyVo(tr.value, key, ModifyOperateType.UPDATE, dataType);
		DBClient.getInstance().add(new DBModifyMessage(mv));
	}

	public static void delToDB(ConstsTR tr, String key) {
		ModifyVo mv = new ModifyVo(tr.value, key, ModifyOperateType.DELETE);
		DBClient.getInstance().add(new DBModifyMessage(mv));
	}

	public static <T> T getFromDB(ConstsTR tr, String key, Class<T> clazz) {
		QueryVo queryVo = new QueryVo(tr.value, key);
		List<T> result = DBClient.getInstance().query(queryVo, clazz);
		if (result.size() > 0) {
			return result.get(0);
		}
		return null;
	}

	public static <T> List<T> findFromDB(ConstsTR tr, String key, Class<T> clazz) {
		QueryVo queryVo = new QueryVo(tr.value, key);
		return DBClient.getInstance().query(queryVo, clazz);
	}

	public static boolean putName(String name, String playerId) {
		long code = GCache.hsetnx(ConstsTR.NAME_MODULE.value, name, playerId);
		return (code > 0) ? true : false;
	}

	public static void freeName(String name) {
		GCache.hremove(ConstsTR.NAME_MODULE.value, name);
	}

	public static final Set<String> hkeys(String key) {
		return GCache.hkeys(key);
	}

	/**
	 * 根据名字获取ID
	 */
	public static String getIdByName(String name) {
		return GCache.hget(ConstsTR.NAME_MODULE.value, name);
	}
}