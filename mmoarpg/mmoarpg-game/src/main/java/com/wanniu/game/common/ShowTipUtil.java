package com.wanniu.game.common;

import java.util.ArrayList;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GPlayer;
import com.wanniu.game.GWorld;
import com.wanniu.game.player.PlayerUtil;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.PlayerHandler.CommonProperty;
import pomelo.area.PlayerHandler.CommonPropertyPush;
import pomelo.chat.ChatHandler.TipPush;

public class ShowTipUtil {

	/**
	 * 系统提示（TipsType.ROLL：为上方跑马灯消息）
	 */
	public static void sendSysTip(WNPlayer player, String msg, Const.TipsType type) {
		TipPush.Builder data = TipPush.newBuilder();
		data.setS2CMsg(msg);
		if (type == null) {
			type = Const.TipsType.NORMAL;
		}
		data.setS2CType(type.getValue());

		player.receive("chat.chatPush.tipPush", data.build());
	};
	
	
	/**
	 * 道具系统提示
	 */
	public static void sendItemTip(WNPlayer player, String itemName, int color, int itemCount) {
		String key = getColor(color);
		String name = LangService.getValue(key);// tips[key];
		name = name.replace("{a}", itemName);
		String str = LangService.getValue("ITEM_GET");
		str = str.replace("{itemName}", name);
		str = str.replace("{itemNm}", String.valueOf(itemCount));
		sendSysTip(player, str, Const.TipsType.NORMAL);
	};

	/**
	 * 战力系统提示
	 */
	public static void sendFightTip(String playerId, int fightPower) {
		String str = LangService.getValue("FIGHT_POWER_ADD");
		str = str.replace("{fightPower}", String.valueOf(fightPower));

		WNPlayer player = GWorld.getInstance().getPlayer(playerId);
		if (player != null) {
			sendSysTip(player, str, Const.TipsType.NORMAL);
		}
	};

	public static String getColor(int quality) {
		String color = "WHITE";
		if (quality == Const.ItemQuality.WHITE.getValue()) {
			color = "WHITE";
		} else if (quality == Const.ItemQuality.GREEN.getValue()) {
			color = "GREEN";
		} else if (quality == Const.ItemQuality.BLUE.getValue()) {
			color = "BLUE";
		} else if (quality == Const.ItemQuality.PURPLE.getValue()) {
			color = "PURPLE";
		} else if (quality == Const.ItemQuality.ORANGE.getValue()) {
			color = "ORANGE";
		} else if (quality == Const.ItemQuality.RED.getValue()) {
			color = "RED";
		}
		return color;
	}

	public static String getColorLink(int quality) {
		String color = "WHITE_LINK";
		if (quality == Const.ItemQuality.WHITE.getValue()) {
			color = "WHITE_LINK";
		} else if (quality == Const.ItemQuality.GREEN.getValue()) {
			color = "GREEN_LINK";
		} else if (quality == Const.ItemQuality.BLUE.getValue()) {
			color = "BLUE_LINK";
		} else if (quality == Const.ItemQuality.PURPLE.getValue()) {
			color = "PURPLE_LINK";
		} else if (quality == Const.ItemQuality.ORANGE.getValue()) {
			color = "ORANGE_LINK";
		} else if (quality == Const.ItemQuality.RED.getValue()) {
			color = "RED_LINK";
		}
		return color;
	}

	public static String getMonsterName(String name, int qColor) {
		String key = getColor(qColor);
		String str = LangService.getValue(key);
		str = str.replace("{a}", name);
		return str;
	};

	public static void sendSysAttrMessageNew(WNPlayer player, int type, ArrayList<CommonProperty> propertys) {
		CommonPropertyPush.Builder push = CommonPropertyPush.newBuilder();
		push.setS2CType(type);
		push.addAllS2CProperty(propertys);
		player.receive("area.playerPush.commonPropertyPush", push.build());
	}

	public static String getPlayerNameColor(String name, int pro) {
		return LangService.format("NAME_COLOR", name);
	}

	public static String itemColorName(int qColor, String name) {
		String key = getColor(qColor);
		String colorStr = LangService.getValue(key);
		String nameStr = colorStr.replace("{a}", name);
		return nameStr;
	};
}
