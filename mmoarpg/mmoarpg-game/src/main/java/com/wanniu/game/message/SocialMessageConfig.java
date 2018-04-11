package com.wanniu.game.message;

import java.util.ArrayList;

import com.wanniu.game.data.GameData;
import com.wanniu.game.data.SocialMessageCO;

public class SocialMessageConfig {

	private static SocialMessageConfig instance;

	public static SocialMessageConfig getInstance() {
		if (instance == null) {
			instance = new SocialMessageConfig();
		}
		return instance;
	}

	private SocialMessageConfig() {
		for (SocialMessageCO data : GameData.SocialMessages.values()) {
			listMessage.add(data);
		}
	}

	private ArrayList<SocialMessageCO> listMessage = new ArrayList<>();

	public final SocialMessageCO findMessageById(int id) {
		for (SocialMessageCO data : listMessage) {
			if (data.iD == id) {
				return data;
			}
		}
		return null;
	}

	public final SocialMessageCO findMessageByMessageType(int type) {
		for (SocialMessageCO data : listMessage) {
			if (data.messageType == type) {
				return data;
			}
		}
		return null;
	}
}
