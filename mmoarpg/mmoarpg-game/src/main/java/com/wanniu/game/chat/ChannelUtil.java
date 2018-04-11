package com.wanniu.game.chat;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.wanniu.core.db.GCache;
import com.wanniu.game.common.ConstsTR;
import com.wanniu.game.item.NormalItem;
import com.wanniu.game.item.po.PlayerItemPO;
import com.wanniu.game.player.WNPlayer;

/**
 * 频道 工具
 * 
 * @author agui
 *
 */
public class ChannelUtil {

	/**
	 * 获得物品信息的字符串
	 */
	public static String setItemInfo(NormalItem item) {
		Map<String, Object> o = new HashMap<>();
		o.put("MsgType", 1);
		o.put("Id", item.itemDb.id);
		o.put("Name", item.prop.name);
		o.put("Quality", item.prop.qcolor);
		o.put("TemplateId", item.itemDb.code);

		if (item.isEquip()) {
			o.put("needQuery", 1); // 需要查询
			cacheChatItem(item.itemDb);
		} else {
			o.put("needQuery", 0);
		}

		return JSON.toJSONString(o);
	};

	public static String setAreaPosInfo(int mapId, int areaId, String instanceId, int x, int y) {
		Map<String, Object> o = new HashMap<>();
		o.put("MsgType", 6);
		Map<String, Object> data = new HashMap<>();
		data.put("mapId", mapId);
		data.put("areaId", areaId);
		data.put("instanceId", instanceId);
		data.put("targetX", x);
		data.put("targetY", y);
		o.put("data", data);

		return JSON.toJSONString(o);
	};

	public static String setPlayerInfo(WNPlayer player) {
		Map<String, Object> o = new HashMap<>();
		o.put("MsgType", 3);
		o.put("s2c_playerId", player.getId());
		o.put("s2c_name", player.getName());
		o.put("s2c_level", player.getLevel());
		o.put("s2c_pro", player.getPro());

		return JSON.toJSONString(o);
	};

	private static final String ITEM_FLAG = "<a {\"Id\":\"";
	private static final String POSITION_FLAG = "<";

	private static void cacheChatItem(PlayerItemPO item) {
		if (item != null) {
			GCache.put(ConstsTR.chat_item_tr.value + "/" + item.id, JSON.toJSONString(item), 600);
		}
	}

	public static boolean extractChatItem(WNPlayer player, String content) {
		boolean flag = false;
		for (int i = content.indexOf(ITEM_FLAG, 0); i >= 0;) {
			int sIndex = i + ITEM_FLAG.length();
			int eIndex = content.indexOf("\"", sIndex + 1);
			if (eIndex > sIndex) {
				String uuid = content.substring(i + ITEM_FLAG.length(), eIndex);
				i = content.indexOf(ITEM_FLAG, eIndex);
				NormalItem item = player.getWnBag().findItemById(uuid);
				if (item == null) {
					item = player.equipManager.getEquipById(uuid);
				}
				if (item != null) {
					cacheChatItem(item.itemDb);
					flag = true;
				}
			} else {
				break;
			}
		}
		if (!flag) {// 只要包含<都要忽略，太多没想到的地方...
			flag = content.indexOf(POSITION_FLAG, 0) >= 0;
		}
		return flag;
	}

	/**
	 * 获取物品在聊天界面的超链接格式
	 */
	public static String getChatLinkItem(NormalItem item) {
		if (item == null) {
			return "";
		}
		StringBuffer sb = new StringBuffer("|<a ");

		ChatLinkItem cli = new ChatLinkItem();
		cli.Id = item.getId();
		cli.MsgType = 1;
		// cli.Name = "[<color='orange'>" + item.getName() + "</color>]";
		cli.Name = "[" + item.getName() + "]";
		cli.Quality = item.getQLevel();
		cli.TemplateId = item.getTemplate().code;
		cli.needQuery = 1;

		sb.append(JSON.toJSONString(cli));

		sb.append("></a>|");

		cacheChatItem(item.itemDb);
		return sb.toString();
	}
}
