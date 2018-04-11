package com.wanniu.game.friend;

import java.util.HashMap;
import java.util.Map;

import com.wanniu.game.common.ConstsTR;
import com.wanniu.game.poes.PlayerFriendsPO;
import com.wanniu.redis.PlayerPOManager;

/**
 * 好友管理中心
 * 
 * @author jjr
 *
 */
public class FriendsCenter {
	private static FriendsCenter instance;
	private Map<String, FriendManager> friendsMgr; // 所有好友数据

	public static synchronized FriendsCenter getInstance() {
		if (null == instance) {
			instance = new FriendsCenter();
		}
		return instance;
	}

	private FriendsCenter() {
		friendsMgr = new HashMap<String, FriendManager>();
	}

	public Map<String, FriendManager> getAllFriendMgr() {
		return friendsMgr;
	}

	public void onPlayerDisponse(String playerId) {
		friendsMgr.remove(playerId);
	}

	/**
	 * 获取好友管理
	 * 
	 * @param playerId
	 * @return
	 */
	public FriendManager getFriendsMgr(String playerId) {
		if (friendsMgr.containsKey(playerId)) {
			return friendsMgr.get(playerId);
		}

		PlayerFriendsPO po = PlayerPOManager.findPO(ConstsTR.player_friendsTR, playerId, PlayerFriendsPO.class);

		FriendManager friendMgr = new FriendManager(playerId, po);
		friendsMgr.put(playerId, friendMgr);
		return friendMgr;
	}

	/**
	 * 同意添加好友申请
	 * 
	 * @param selfId 自己id
	 * @param requestId 请求id
	 */
	public Map<String, Object> friendAgreeApply(String selfId, String requestId) {
		FriendManager friendMgr = getFriendsMgr(selfId);
		return friendMgr.friendAgreeApply(requestId);
	}
}
