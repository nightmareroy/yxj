package com.wanniu.game.five2Five.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.wanniu.core.db.GCache;
import com.wanniu.core.logfs.Out;
import com.wanniu.core.util.StringUtil;
import com.wanniu.game.GWorld;
import com.wanniu.game.common.ConstsTR;
import com.wanniu.game.common.Utils;
import com.wanniu.game.five2Five.Five2FivePlayerResultInfoVo;
import com.wanniu.game.poes.Five2FivePO;
import com.wanniu.game.poes.Five2FivePlayerBtlReportPO;
import com.wanniu.game.poes.Five2FiveSystemPO;
import com.wanniu.redis.GameDao;
import com.wanniu.redis.PlayerPOManager;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

/**
 * @author wanghaitao
 *
 */
public class Five2FiveDao {
	/**
	 * 更新5v5系統信息
	 * 
	 * @param systemPo
	 */
	public static void updateFive2FiveSystem(Five2FiveSystemPO systemPo) {
		GameDao.update(String.valueOf(GWorld.__SERVER_ID), ConstsTR.fivie2FiveSystemTR, systemPo);
	}

	/**
	 * 获取5v5系统信息
	 * 
	 * @param logicServerId
	 */
	public static Five2FiveSystemPO getFive2FivePO(String logicServerId) {
		return GameDao.get(String.valueOf(GWorld.__SERVER_ID), ConstsTR.fivie2FiveSystemTR, Five2FiveSystemPO.class);
	}

	/**
	 * 更新玩家5v5战报信息
	 * 
	 * @param btlPo
	 */
	public static void updatePlayerFive2FiveBtlReportPO(Five2FivePlayerBtlReportPO btlPo) {
		Jedis redis = GCache.getRedis();
		try {
			Transaction multi = redis.multi();
			multi.hset(getBtlReportTR(btlPo.playerId), btlPo.id, Utils.serialize(btlPo));
			multi.exec();
		} catch (Exception e) {
			Out.error(e);
		} finally {
			GCache.release(redis);
		}
	}

	/**
	 * 删除玩家战报
	 * 
	 * @param btlPo
	 */
	public static void delPlayerFive2FiveBtlReportPO(Five2FivePlayerBtlReportPO btlPo) {
		Jedis redis = GCache.getRedis();
		try {
			Transaction multi = redis.multi();
			multi.hdel(getBtlReportTR(btlPo.playerId), btlPo.id);
			multi.exec();
		} catch (Exception e) {
			Out.error(e);
		} finally {
			GCache.release(redis);
		}
	}

	/**
	 * 获取玩家5v5信息
	 * 
	 * @param playerId
	 * @return
	 */
	public static Five2FivePO getPlayerFive2FivePO(String playerId) {
		return PlayerPOManager.findPO(ConstsTR.five2FiveTR, playerId, Five2FivePO.class);
	}

	/**
	 * 获取玩家战报信息
	 * 
	 * @param playerId
	 * @return
	 */
	public static List<Five2FivePlayerBtlReportPO> getPlayerFive2FiveBtlReportPO(String playerId) {
		List<Five2FivePlayerBtlReportPO> list = new ArrayList<Five2FivePlayerBtlReportPO>();
		Map<String, String> dataMap = GCache.hgetAll(getBtlReportTR(playerId));
		if (dataMap != null && !dataMap.isEmpty()) {
			for (String data : dataMap.values()) {
				Five2FivePlayerBtlReportPO btlReportPo = Utils.deserialize(data, Five2FivePlayerBtlReportPO.class);
				if (btlReportPo != null) {
					list.add(btlReportPo);
				}
			}
		}
		Collections.sort(list, new Comparator<Five2FivePlayerBtlReportPO>() {

			@Override
			public int compare(Five2FivePlayerBtlReportPO o1, Five2FivePlayerBtlReportPO o2) {
				long i = o2.createTime.getTime() - o1.createTime.getTime();
				if (i > 0) {
					return 1;
				} else if (i < 0) {
					return -1;
				}
				return 0;
			}
		});
		return list;
	}

	/**
	 * 存储分享的比赛结果(24小时)
	 * 
	 * @param instanceId
	 * @param resultInfos
	 */
	public static void updateShardBtlReport(String instanceId, List<Five2FivePlayerResultInfoVo> resultInfos) {
		String resultInfoJson = Utils.serialize(resultInfos);
		int expired = 24 * 60 * 60;
		GCache.put(getShardBtlReportTR(instanceId), resultInfoJson, expired);
	}

	/**
	 * 获取存储分享的比赛结果
	 * 
	 * @param instanceId
	 * @param resultInfos
	 */
	public static List<Five2FivePlayerResultInfoVo> getShardBtlReport(String instanceId) {
		String data = GCache.get(getShardBtlReportTR(instanceId));
		List<Five2FivePlayerResultInfoVo> resultInfos = new ArrayList<>();
		if (StringUtil.isNotEmpty(data)) {
			resultInfos = JSON.parseObject(data, new TypeReference<List<Five2FivePlayerResultInfoVo>>() {});
		}
		return resultInfos;
	}

	/**
	 * 获取玩家战报TR
	 * 
	 * @param playerId
	 * @return
	 */
	private static String getBtlReportTR(String playerId) {
		return ConstsTR.five2FiveBtlTR.value + "/" + playerId;
	}

	/**
	 * 获取玩家分享战报TR
	 * 
	 * @param instanceId
	 * @return
	 */
	private static String getShardBtlReportTR(String instanceId) {
		return ConstsTR.five2FiveShardBtlReportTR.value + "/" + instanceId;
	}

}
