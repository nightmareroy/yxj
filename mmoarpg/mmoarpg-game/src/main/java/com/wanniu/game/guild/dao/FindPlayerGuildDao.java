package com.wanniu.game.guild.dao;

import com.wanniu.game.common.ConstsTR;
import com.wanniu.game.poes.PlayerGuildPO;
import com.wanniu.redis.PlayerPOManager;

public class FindPlayerGuildDao {

	public static final PlayerGuildPO getPlayerGuildPOById(String playerId) {
		PlayerGuildPO redisPo = PlayerPOManager.findPO(ConstsTR.playerGuildTR, playerId, PlayerGuildPO.class);
		if (null != redisPo) {
			return redisPo;
		}

		PlayerGuildPO po = new PlayerGuildPO();
		PlayerPOManager.put(ConstsTR.playerGuildTR, playerId, po);

		return po;
	}

}
