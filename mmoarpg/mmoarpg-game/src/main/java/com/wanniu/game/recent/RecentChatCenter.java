package com.wanniu.game.recent;

import java.util.HashMap;
import java.util.Map;

import com.wanniu.game.common.ConstsTR;
import com.wanniu.game.poes.RecentChatPO;
import com.wanniu.redis.PlayerPOManager;

/**
 * 最近联系人管理中心
 * 
 * @author jjr
 *
 */
public class RecentChatCenter {
	private static RecentChatCenter instance;
	private Map<String, RecentChatMgr> recentChatMgrs; // 所有好友数据

	private RecentChatCenter() {
		recentChatMgrs = new HashMap<String, RecentChatMgr>();
	}

	public static RecentChatCenter getInstance() {
		if (null == instance) {
			instance = new RecentChatCenter();
		}
		return instance;
	}

	public RecentChatMgr getRecentChatMgr(String playerId) {
		if (recentChatMgrs.containsKey(playerId)) {
			return recentChatMgrs.get(playerId);
		}

		RecentChatPO po = PlayerPOManager.findPO(ConstsTR.playerRecentChatTR, playerId, RecentChatPO.class);
		RecentChatMgr recentChatMgr = new RecentChatMgr(playerId, po);
		recentChatMgrs.put(playerId, recentChatMgr);
		return recentChatMgr;
	}
}
