package com.wanniu.game.chat;

import com.wanniu.game.data.ChatSettingCO;
import com.wanniu.game.data.GameData;

public class ChatConfig {

	private static ChatConfig instance;

	public static ChatConfig getInstance() {
		if (instance == null) {
			instance = new ChatConfig();
		}
		return instance;
	}

	private ChatConfig() {

	}

	/**
	 * 根据频道获取聊天配置
	 */
	public ChatSettingCO getChatSettingProp(int channelId) {
		for (ChatSettingCO setting : GameData.ChatSettings.values()) {
			if (setting.channelID == channelId) {
				return setting;
			}
		}
		return null;
	}

}
