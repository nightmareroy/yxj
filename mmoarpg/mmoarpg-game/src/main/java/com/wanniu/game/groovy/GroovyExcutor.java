package com.wanniu.game.groovy;

import com.wanniu.core.groovy.IGameGroovy;
import com.wanniu.game.player.GlobalConfig;
import com.wanniu.game.player.PlayerUtil;
import com.wanniu.game.player.WNPlayer;

public class GroovyExcutor implements IGameGroovy {
	public String execute() {
		String id = "87ce937c-1b8d-4181-bbfa-06ad52dca268";
		WNPlayer player = PlayerUtil.getOnlinePlayer(id);
		if (player == null) {
			return "不在线";
		}
		player.baseDataManager.upgrade(Math.min(GlobalConfig.Role_LevelLimit, 39), 0);
		return "OK";
	}
}