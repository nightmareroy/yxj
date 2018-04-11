package com.wanniu.game.daoyou.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.wanniu.core.db.GCache;
import com.wanniu.core.db.ModifyDataType;
import com.wanniu.core.logfs.Out;
import com.wanniu.game.common.ConstsTR;
import com.wanniu.game.common.Utils;
import com.wanniu.game.poes.DaoYouMemberPO;
import com.wanniu.game.poes.DaoYouPO;
import com.wanniu.redis.GameDao;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

/**
 * @author wanghaitao
 *
 */
public class DaoYouDao {
	/**
	 * 获取服上的所有道友
	 * 
	 * @return
	 */
	public static List<DaoYouPO> getAllDaoYouList() {
		Map<String, String> dataMap = GCache.hgetAll(ConstsTR.daoYouTR.value);
		if (dataMap == null || dataMap.isEmpty()) {
			List<DaoYouPO> daoYouList = GameDao.findFromDB(ConstsTR.daoYouTR, null, DaoYouPO.class);
			dataMap = new HashMap<>();
			for (int i = 0; i < daoYouList.size(); i++) {
				DaoYouPO dy = daoYouList.get(i);
				dataMap.put(dy.id, Utils.serialize(dy));
			}
			if (dataMap.size() > 0) {
				GCache.hmset(ConstsTR.daoYouTR.value, dataMap);
			}
			return daoYouList;
		}
		List<DaoYouPO> list = new ArrayList<>();
		if (dataMap != null && !dataMap.isEmpty()) {
			for (String data : dataMap.values()) {
				DaoYouPO daoYouPO = Utils.deserialize(data, DaoYouPO.class);
				if (daoYouPO != null) {
					list.add(daoYouPO);
				}
			}
		}
		return list;
	}

	/**
	 * 更新道友
	 * 
	 * @param data
	 */
	public static void updateDaoYou(DaoYouPO data) {
		try {
			GCache.hset(ConstsTR.daoYouTR.value, data.id, Utils.serialize(data));
			GameDao.updateToDB(ConstsTR.daoYouTR, data.id, ModifyDataType.MAP);
		} catch (Exception e) {
			Out.error(e);
		}
	}

	/**
	 * 获取服上所有道友成员
	 * 
	 * @return
	 */
	public static List<DaoYouMemberPO> getAllDaoYouMember() {
		Map<String, String> dataMap = GCache.hgetAll(ConstsTR.daoYouMemberTR.value);
		if (dataMap == null || dataMap.isEmpty()) {
			List<DaoYouMemberPO> members = GameDao.findFromDB(ConstsTR.daoYouMemberTR, null, DaoYouMemberPO.class);
			dataMap = new HashMap<>();
			for (int i = 0; i < members.size(); i++) {
				DaoYouMemberPO mem = members.get(i);
				dataMap.put(mem.playerId, Utils.serialize(mem));
			}
			if (dataMap.size() > 0) {
				GCache.hmset(ConstsTR.daoYouMemberTR.value, dataMap);
			}
			return members;
		}
		List<DaoYouMemberPO> list = new ArrayList<DaoYouMemberPO>();
		if (dataMap != null && !dataMap.isEmpty()) {
			for (String data : dataMap.values()) {
				DaoYouMemberPO daoYouMemPo = Utils.deserialize(data, DaoYouMemberPO.class);
				if (daoYouMemPo != null) {
					list.add(daoYouMemPo);
				}
			}
		}
		return list;
	}

	/**
	 * 修改道友成员
	 * 
	 * @param data
	 */
	public static void updateDaoYouMember(DaoYouMemberPO data) {
		try {
			GCache.hset(ConstsTR.daoYouMemberTR.value, data.playerId, Utils.serialize(data));
			GameDao.updateToDB(ConstsTR.daoYouMemberTR, data.playerId, ModifyDataType.MAP);
		} catch (Exception err) {
			Out.error(err);
		}
	}

	/**
	 * 移除道友
	 * 
	 * @param data
	 */
	public static void removeDaoYou(DaoYouPO data) {
		String daoYouId = data.id;
		Jedis redis = GCache.getRedis();
		try {
			Transaction multi = redis.multi();
			multi.hdel(ConstsTR.daoYouTR.value, daoYouId);
			multi.exec();

			GameDao.delToDB(ConstsTR.daoYouTR, daoYouId);
		} catch (Exception e) {
			Out.error(e);
		} finally {
			GCache.release(redis);
		}
	}

	/**
	 * 移除道友成员
	 * 
	 * @param data
	 */
	public static void removeDaoYouMember(DaoYouMemberPO data) {
		String daoYouMemId = data.playerId;
		Jedis redis = GCache.getRedis();
		try {
			Transaction multi = redis.multi();
			multi.hdel(ConstsTR.daoYouMemberTR.value, daoYouMemId);
			multi.exec();

			GameDao.delToDB(ConstsTR.daoYouMemberTR, daoYouMemId);
		} catch (Exception e) {
			Out.error(e);
		} finally {
			GCache.release(redis);
		}
	}
}
