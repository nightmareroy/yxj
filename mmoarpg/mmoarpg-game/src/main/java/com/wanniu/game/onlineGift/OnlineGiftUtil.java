package com.wanniu.game.onlineGift;

import java.util.List;

import com.wanniu.game.data.ext.OlGiftExt;

public class OnlineGiftUtil {

	public static List<OlGiftExt> getPropList() {
		return OlGiftConfig.getInstance().getPropList();
	};

	public static OlGiftExt getPropById(int giftId) {
		return OlGiftConfig.getInstance().getPropById(giftId);
	};

	public static List<OlGiftExt> getPropListByLevel(int giftType, int upLevel, int level) {
		return OlGiftConfig.getInstance().getPropListByLevel(giftType, upLevel, level);
	};

	public static OlGiftExt getPropByLevelAndTime(int upLevel, int level, int time) {
		return OlGiftConfig.getInstance().getPropByLevelAndTime(upLevel, level, time);
	};

}
