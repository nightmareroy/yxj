package com.wanniu.game.prepaid;

import com.wanniu.game.common.ConstsTR;
import com.wanniu.game.player.PlayerUtil;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.redis.GameDao;

/**
 * 充值中心.
 * 
 * @author 周明凯
 */
public class PrepaidCenter {
	private static PrepaidCenter instance;

	public static synchronized PrepaidCenter getInstance() {
		if (instance == null) {
			instance = new PrepaidCenter();
		}
		return instance;
	}

	public PrepaidManager findPrepaid(String playerId) {
		WNPlayer player = PlayerUtil.getOnlinePlayer(playerId);
		if (player == null) {
			return new PrepaidManager(playerId);
		}
		return player.prepaidManager;
	}

	/**
	 * 立刻存档.
	 */
	public void update(String playerId, PrepaidManager manager) {
		GameDao.update(playerId, ConstsTR.prepaidNewTR, manager.po);
	}
}