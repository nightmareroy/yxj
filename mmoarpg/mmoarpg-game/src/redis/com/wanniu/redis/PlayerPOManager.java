package com.wanniu.redis;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.wanniu.core.db.QueryVo;
import com.wanniu.core.db.connet.DBClient;
import com.wanniu.core.game.JobFactory;
import com.wanniu.core.game.entity.GEntity;
import com.wanniu.core.logfs.Out;
import com.wanniu.game.common.ConstsTR;
//import com.wanniu.game.farm.FarmCenter;
import com.wanniu.game.friend.FriendsCenter;
import com.wanniu.game.mail.MailCenter;
import com.wanniu.game.player.PlayerUtil;

/**
 * 全局的PO缓存管理
 * 
 * @author Yangzz
 *
 */
public class PlayerPOManager {

	/** 最多缓存数量 (po的数量, 一个玩家包含几十个PO) */
	public static int MAX_COUNT = 50 * 2000;
	/** 对象过期时间(1小时) */
	public static long EXPIRE = 1000 * 60 * 60 * 1;
	/** po 缓存集合 <playerId, <tr, T>> */
	public static Map<String, Map<String, GEntity>> pos = new ConcurrentHashMap<>();

	static {
		// 启动缓存清理定时器
		JobFactory.addScheduleJob(() -> {
			clearOfflinePO();
		}, EXPIRE, EXPIRE);
	}
	
	private static Map<String, GEntity> getPlayerPOMap(String playerId) {
		Map<String, GEntity> playerPOMap = pos.get(playerId);
		if (playerPOMap == null) {
			playerPOMap = new ConcurrentHashMap<>();
			pos.put(playerId, playerPOMap);
		}
		return playerPOMap;
	}

	/**
	 * 获取缓存对象
	 */
	@SuppressWarnings("unchecked")
	public static <T extends GEntity> T findPO(ConstsTR tr, String playerId, Class<T> clazz) {
		GEntity po = null;
		Map<String, GEntity> playerPOMap = getPlayerPOMap(playerId);

		String key = tr.value;
		if (playerPOMap.containsKey(key)) {
			po = playerPOMap.get(key);
		} else {
			po = GameDao.get(playerId, tr, clazz);
			if (po != null) {
				playerPOMap.put(key, po);
			} else {
				po = DBClient.getInstance().get(new QueryVo(tr.value, playerId), clazz);
				if (po != null) {
					playerPOMap.put(key, po);
				}
			}
		}

		// 缓存数量超过阀值,移除离线玩家(防止瞬间集中登陆的情况)
		int size = pos.size();
		if (pos.size() > MAX_COUNT) {
			Out.warn("po count is: " , size);
			clearOfflinePO();
		}

		return (T) po;
	}

	/**
	 * 清理离线对象
	 */
	public static void clearOfflinePO() {
		for (String rid : pos.keySet()) {
			if (!PlayerUtil.isLocal(rid)) {
				MailCenter.getInstance().onPlayerDisponse(rid);
				FriendsCenter.getInstance().onPlayerDisponse(rid);
//				FarmCenter.getInstance().onPlayerDisponse(rid);
				update(rid, pos.remove(rid));
			}
		}
	}
	
	public static void onCloseGame() {
		clearOfflinePO();
	}

	public static void clearOfflinePO(String playerId) {
		if (pos.containsKey(playerId)) {
			MailCenter.getInstance().onPlayerDisponse(playerId);
			FriendsCenter.getInstance().onPlayerDisponse(playerId);
//			FarmCenter.getInstance().onPlayerDisponse(playerId);
			update(playerId, pos.remove(playerId));
			DBClient.getInstance().onPlayerleave(playerId);
		}
	}

	/**
	 * 同步缓存到redis
	 * @param playerId
	 */
	public static void sync(String playerId) {
		update(playerId, pos.get(playerId));
	}
	
	/**
	 * 更新缓存到redis
	 * @param playerPos 玩家所有PO对象集合 
	 */
	private static void update(String playerId, Map<String, GEntity> playerPos) {
		if (playerPos == null) {
			return;
		}
		for (String tr : playerPos.keySet()) {
			GEntity entity = playerPos.get(tr);
			GameDao.update(playerId, tr, entity);
		}
	}

	/**
	 * 将po对象增加到全局缓存中 
	 */
	public static void put(ConstsTR tr, String playerId, GEntity po) {
		Map<String, GEntity> playerPoMap = getPlayerPOMap(playerId);
		playerPoMap.put(tr.value, po);
	}

}
