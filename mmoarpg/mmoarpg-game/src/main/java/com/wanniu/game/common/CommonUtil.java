package com.wanniu.game.common;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.wanniu.core.logfs.Out;
import com.wanniu.core.util.StringUtil;
import com.wanniu.game.bag.WNBag.SimpleItemInfo;
import com.wanniu.game.common.Const.PlayerBtlData;
import com.wanniu.game.common.msg.MessageUtil;
import com.wanniu.game.data.GameData;
import com.wanniu.game.data.base.FourProp;
import com.wanniu.game.data.ext.AffixExt;
import com.wanniu.game.data.ext.AttributeExt;
import com.wanniu.game.message.MessageData;
import com.wanniu.game.util.BlackWordUtil;

import pomelo.Common.AttributeSimple;
import pomelo.Common.PropertyStruct;

public class CommonUtil {

	/**
	 * 类型转换
	 */
	public static PropertyStruct.Builder transferDataType(String key, Object value) {
		PropertyStruct.Builder data = PropertyStruct.newBuilder();
		data.setKey(key);
		data.setValue(String.valueOf(value));

		if (value instanceof Integer) {
			data.setType(1);
		} else {
			data.setType(2);
		}
		return data;
	}

	/**
	 * 计算战力
	 */
	public static int calFightPower(Map<PlayerBtlData, Integer> allInflus) {
		float fightPower = 0;
		Map<Integer, AttributeExt> map = GameData.Attributes;
		for (AttributeExt att : map.values()) {
			if (att.isEffect == 1) {
				if (allInflus.containsKey(att.btlProp)) {
					int value = allInflus.get(att.btlProp);
					if (value > 0) {
						// System.out.println("=="+att.btlProp.chName+":"+value);
						fightPower += Math.abs(value)  * att.scoreRatio;
					}
				}
			}
		}

		return (int) fightPower;
	};

	/**
	 * 计算战力
	 */
	public static int calPlayerFightPower(Map<PlayerBtlData, Integer> allInflus, int pro) {
		float fightPower = 0;
		Map<Integer, AttributeExt> map = GameData.Attributes;
		for (AttributeExt att : map.values()) {
			if (att.isEffect == 1) {
				if (att.btlProp == null) {
					Out.error("calPlayerFightPower :: ", att.attName);
					continue;
				}
				if (allInflus.containsKey(att.btlProp)) {
					int value = allInflus.get(att.btlProp);
					if (att.btlProp == PlayerBtlData.CritDamage) {
						value = value - GameData.Characters.get(pro).critDamage;
					}
					fightPower += Math.abs(value)  * att.scoreRatio;
				}
			}
		}
		return (int) fightPower;
	};

	/**
	 * 计算某一条属性的战斗力
	 * 
	 * @param qcolor
	 * @return
	 */
	public static int calOneAttributeFightScroreByStr(String key, int value) {
		PlayerBtlData data = PlayerBtlData.valueOf(key);
		if (data == null) {
			Out.warn("发现属性不存在2:", key);
			return 0;
		}
		AttributeExt att = GameData.Attributes.get(data.id);
		if (att == null) {
			Out.warn("发现属性不存在1:", key);
			return 0;
		}
		return (int) (Math.abs(value)  * att.scoreRatio);
	}

	/**
	 * 计算某一条属性的战斗力
	 * 
	 * @param qcolor
	 * @return
	 */
	public static int calOneAttributeFightScroreById(int key, int value, int qColor) {
		AffixExt affix = GameData.Affixs.get(key);
		if (affix == null) {
			Out.warn("发现属性不存在3:", key);
			return 0;
		}
		FourProp pair = affix.props.get(qColor);
		if (pair == null) {
			Out.warn("发现属性不存在4:", key, ",", qColor);
			return 0;
		}
		return calOneAttributeFightScroreByStr(pair.prop, value);
	}

	/**
	 * 计算战力
	 */
	public static int calPetFightPower(Map<PlayerBtlData, Integer> allInflus, int petId) {
		float fightPower = 0;
		Map<Integer, AttributeExt> map = GameData.Attributes;
		for (AttributeExt att : map.values()) {
			if (att.isEffect == 1) {
				if (allInflus.containsKey(att.btlProp)) {
					int value = allInflus.get(att.btlProp);
					if (att.btlProp == PlayerBtlData.CritDamage) {
						value = value - GameData.BaseDatas.get(petId).initCritDamage;
					}
					if (value > 0) {
						// System.out.println("=="+att.btlProp.chName+":"+value);
						fightPower += Math.abs(value)  * att.scoreRatio;
					}
				}
			}
		}
		return (int) fightPower;
	};

	public static int calFightPowerByData(Map<String, Integer> influs) {
		// Map<String, Integer> finalInflus = transInfluesData(influs);
		// Map<String, Integer> allInflus = calFinalInflus(finalInflus);
		// return calFightPower(allInflus);
		return 0;
	}

	/**
	 * 连服操作获取相应逻辑服id
	 */
	public static int getUnRealLogicServerId(int logicServerId, int acrossServerId, boolean isAcross) {
		if (isAcross) {
			return acrossServerId;
		}
		return logicServerId;
	};

	/**
	 * 属性成长系数，算法为：=ROUND(初始值*ROUND(成长系数^(等级-1),4),0)
	 * 
	 * @param initValue
	 * @param growUpValue
	 * @param lvl
	 * @return
	 */
	public static int getGrowUpValue(int initValue, float growUpValue, int lvl) {
		return (int) Math.round(initValue * Math.pow(growUpValue, (lvl - 1)));
	}

	public static boolean isLegalString(String name) {
		final String pattern = "^[\u4e00-\u9fa5_a-zA-Z0-9]+$";
		if (!name.matches(pattern)) {
			return false;
		}
		if (BlackWordUtil.isIncludeBlackString(name)) {
			return false;
		}

		return true;
	}

	/**
	 * 获取星期几
	 * 
	 * @return 1,2,3,4,5,6,7
	 */
	public static int getWeek() {
		Date date = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
		if (0 == w) {
			w = 7;
		}
		return w;
	}

	/**
	 * 道具字段解析
	 * 
	 * @param arr
	 * @param itemStr "asst1:16;gold1k:16;mstar:16;cash100:3"
	 */
	public static List<SimpleItemInfo> parseItems(String itemStr) {
		List<SimpleItemInfo> items = new ArrayList<SimpleItemInfo>();
		if (StringUtil.isEmpty(itemStr)) {
			return items;
		}
		String[] timeStr = itemStr.split(";");
		for (int i = 0; i < timeStr.length; i++) {
			String elemStr = timeStr[i];
			if (StringUtil.isEmpty(elemStr)) {
				continue;
			}

			String[] tmp = elemStr.split(":");

			if (tmp.length != 2) {
				continue;
			}

			SimpleItemInfo item = new SimpleItemInfo();
			item.itemCode = tmp[0];
			item.itemNum = Integer.parseInt(tmp[1]);
			items.add(item);
		}
		return items;
	}

	/**
	 * 向客户端推送提示图标
	 */
	public static void sendIconMsgType(Const.MESSAGE_TYPE msgType, String playerId) {
		MessageData message = MessageUtil.createMessage(msgType.getValue(), playerId, null);
		MessageUtil.sendMessageToPlayer(message, playerId);
	}

	public static void printAttrMap(Map<PlayerBtlData, Integer> map) {
		StringBuffer sb = new StringBuffer("");
		for (PlayerBtlData pbd : map.keySet()) {
			sb.append(pbd.chName + ":" + map.get(pbd)).append("  ");
		}
		System.out.println(sb);
	}

	public static void printAttrList(List<AttributeSimple> list) {
		StringBuffer sb = new StringBuffer("");
		for (AttributeSimple attr : list) {
			int id = attr.getId();
			PlayerBtlData pbd = PlayerBtlData.getE(id);
			sb.append(pbd.chName + ":" + attr.getValue()).append("  ");
		}
		System.out.println(sb);
	}

}
