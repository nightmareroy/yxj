package com.wanniu.game.player;

public class PlayerService {

	public void afterPlayerChangeName(WNPlayer player) {
		player.consignmentManager.afterPlayerChangeName();// logic-default线程
		player.refreshBattlerServerBasic();// 通知战斗服
		player.rankManager.onChangeName();
		player.guildManager.onChangeName();//
	}

	private PlayerService() {}

	private static class PlayerManagerHolder {
		public final static PlayerService INSTANCE = new PlayerService();
	}

	public static PlayerService getInstance() {
		return PlayerManagerHolder.INSTANCE;
	}
}
