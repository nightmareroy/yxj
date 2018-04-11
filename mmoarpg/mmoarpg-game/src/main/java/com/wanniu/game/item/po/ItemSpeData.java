package com.wanniu.game.item.po;

import java.io.Serializable;
import java.util.Map;

import com.wanniu.game.equip.RepeatKeyMap;

/**
 * 物品扩展数据
 * 
 * @author Yangzz
 *
 */
public class ItemSpeData implements Serializable {

	private static final long serialVersionUID = 1L;

	public int worth;

	/** 基础属性 值随机（>=min <=max） */
	public Map<String, Integer> baseAtts;

	/** 重铸出来的额外属性加成(两个或两个以上的属性加成就会触发) */
	public Map<String, Integer> extAttsAdd;

	/**
	 * 随机扩展属性(词条随机) type:EquipType,tcLevel = Level or Level = 0, Pro == null ||
	 * p.pro in Pro, color ProX 取出多条，根据 Rare 随机取合适的
	 */
	public RepeatKeyMap<Integer, Integer> extAtts;

	/**
	 * 1条传奇属性
	 */
	public Map<Integer, Integer> legendAtts;

	/** 洗练、重铸、精炼 未保存的临时属性 */
	public Map<String, Integer> tempBaseAtts;
	public RepeatKeyMap<Integer, Integer> tempExtAtts;
	public RepeatKeyMap<Integer, Integer> tempExtAtts_senior;
	/** 重铸出来的临时的额外属性加成(两个或两个以上的属性加成就会触发) */
	public Map<String, Integer> tempExtAttsAdd;
	/** 临时 开光传奇属性 */
	public Map<Integer, Integer> tempUniqueAtts;

	public static class ExtObj {
		public int value;
		public int affixId;

		public ExtObj(int value, int affixId) {
			this.value = value;
			this.affixId = affixId;
		}
	}
}
