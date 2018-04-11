package com.wanniu.game.player;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.wanniu.core.util.StringUtil;
import com.wanniu.game.common.ConstsTR;
import com.wanniu.game.player.po.AllBlobPO;
import com.wanniu.game.poes.AchievementDataPO;
import com.wanniu.game.poes.HookSetPO;
import com.wanniu.game.poes.PlayerAttachPO;
import com.wanniu.game.poes.PlayerBasePO;
import com.wanniu.game.poes.PlayerChouRenPO;
import com.wanniu.game.poes.PlayerPO;
import com.wanniu.game.poes.PlayerTempPO;
import com.wanniu.game.poes.TaskListPO;
import com.wanniu.game.poes.XianYuanPO;
import com.wanniu.redis.GlobalDao;
import com.wanniu.redis.PlayerPOManager;

/**
 * 玩家对象Reids存储类
 * 
 * @author Yangzz
 *
 */
public class PlayerDao {

	/**
	 * 插入新角色
	 */
	public static void insertPlayerId(AllBlobPO allBlob) {
		int logicServerId = allBlob.player.logicServerId;
		List<String> rids = getPlayerIdsByUid(allBlob.player.uid, logicServerId);
		rids.add(allBlob.player.id);
		String key = ConstsTR.playerIdsTR + "/" + allBlob.player.uid;
		GlobalDao.hset(String.valueOf(logicServerId), key, JSON.toJSONString(rids));
	}

	/**
	 * 插入新角色
	 */
	public static void insertPlayerId(PlayerPO po, AllBlobPO newBlob) {
		int logicServerId = newBlob.player.logicServerId;
		List<String> rids = getPlayerIdsByUid(newBlob.player.uid, logicServerId);
		rids.add(po.id);
		String key = ConstsTR.playerIdsTR + "/" + newBlob.player.uid;
		GlobalDao.hset(String.valueOf(logicServerId), key, JSON.toJSONString(rids));
	}

	public static List<String> getPlayerIdsByUid(String uid, int logicServerId) {
		String key = ConstsTR.playerIdsTR + "/" + uid;
		String existsRids = GlobalDao.hget(String.valueOf(logicServerId), key);
		List<String> rids = null;
		if (StringUtil.isNotEmpty(existsRids)) {
			rids = JSON.parseObject(existsRids, new TypeReference<List<String>>() {});
		}
		return rids == null ? new ArrayList<>() : rids;
	}

	public static void updatePlayerIds(String uid, int logicServerId, List<String> rids) {
		String key = ConstsTR.playerIdsTR + "/" + uid;
		GlobalDao.hset(String.valueOf(logicServerId), key, JSON.toJSONString(rids));
	}

	public static AllBlobPO getAllBlobData(String playerId) {
		PlayerPO player = PlayerPOManager.findPO(ConstsTR.playerTR, playerId, PlayerPO.class);

		PlayerBasePO playerBase = PlayerPOManager.findPO(ConstsTR.playerBaseTR, playerId, PlayerBasePO.class);

		PlayerTempPO playerTemp = PlayerPOManager.findPO(ConstsTR.playerTempTR, playerId, PlayerTempPO.class);

		AllBlobPO allBlobData = new AllBlobPO(player, playerBase, playerTemp);
		allBlobData.playerAttachPO = PlayerPOManager.findPO(ConstsTR.playerAttachTR, playerId, PlayerAttachPO.class);
		allBlobData.tasks = PlayerPOManager.findPO(ConstsTR.taskTR, playerId, TaskListPO.class);
		allBlobData.achievements = PlayerPOManager.findPO(ConstsTR.achievementTR, playerId, AchievementDataPO.class);
		allBlobData.hookSetData = PlayerPOManager.findPO(ConstsTR.hookSetTR, playerId, HookSetPO.class);
		allBlobData.chouRens = PlayerPOManager.findPO(ConstsTR.player_chourenTR, playerId, PlayerChouRenPO.class);
		allBlobData.xianYuan = PlayerPOManager.findPO(ConstsTR.xianYuanTR, playerId, XianYuanPO.class);

		return allBlobData;
	}

	/**
	 * 通过id查找角色数据
	 */
	public static PlayerPO getPlayerDataById(String playerId) throws Exception {
		// 如果缓存里面有,从缓存获取数据
		PlayerPO player = PlayerPOManager.findPO(ConstsTR.playerTR, playerId, PlayerPO.class);
		if (null != player && player.isDelete == 0) {
			return player;
		}
		return player;
	};

	/**
	 * 更新分区id
	 */
	public static boolean putName(String name, String playerId) {
		long code = GlobalDao.hsetnx(ConstsTR.NAME_MODULE.value, name, playerId);
		return (code > 0) ? true : false;
	}

	/**
	 * 是否存在该名字的角色
	 */
	public static boolean existsName(String name) {
		return GlobalDao.hexists(ConstsTR.NAME_MODULE.value, name);
	}

	public static void freeName(String name) {
		GlobalDao.hremove(ConstsTR.NAME_MODULE.value, name);
	}

	/**
	 * 根据名字获取ID
	 */
	public static String getIdByName(String name) {
		return GlobalDao.hget(ConstsTR.NAME_MODULE.value, name);
	}
}
