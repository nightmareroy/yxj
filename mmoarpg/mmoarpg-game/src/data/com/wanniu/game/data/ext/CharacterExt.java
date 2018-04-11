package com.wanniu.game.data.ext;

import java.util.ArrayList;
import java.util.List;

import com.wanniu.core.util.StringUtil;
import com.wanniu.game.data.CharacterCO;

public class CharacterExt extends CharacterCO {

	/**
	 * 初始携带道具
	 */
	public List<InitItem> initItems;

	/** 初始携带技能 */
	public List<InitSkill> initSkills;
	
	/** 初始装备 */
	public List<String> initEquips;

	/** 属性构造 */
	@Override
	public void initProperty() {

		initItems = new ArrayList<>();
		if (StringUtil.isNotEmpty(this.initItem)) {
			String[] itemStrs = this.initItem.split("\\|");
			for (String itemStr : itemStrs) {
				if (StringUtil.isNotEmpty(itemStr)) {
					String[] item = itemStr.split(":");
					initItems.add(new InitItem(item[0], Integer.valueOf(item[1])));
				}
			}
		}
		// 100010:1|100030:0|100020:0|100040:0|100050:0|102010:0|102050:0|100060:0|101010:0
		initSkills = new ArrayList<>();
		if (StringUtil.isNotEmpty(initSkill)) {
			String[] skillStrs = this.initSkill.split("\\|");
			int index = 0;
			for (String skillStr : skillStrs) {
				if (StringUtil.isNotEmpty(skillStr)) {
					String[] skill = skillStr.split(":");
					if (skill.length > 1) {
						// initSkills.put(Integer.parseInt(skill[0]), new
						// InitSkill(Integer.parseInt(skill[0]),
						// Integer.parseInt(skill[1]), index));
						initSkills.add(new InitSkill(Integer.parseInt(skill[0]), Integer.parseInt(skill[1]), index));
						index = index + 1;
					}
				}
			}
		}
		
		// 创角携带的装备
		initEquips = new ArrayList<>();
		if (StringUtil.isNotEmpty(this.initEquip)) {
			String[] equips = this.initEquip.split("\\|");
			for (String equip : equips) {
				if (StringUtil.isNotEmpty(equip)) {
					initEquips.add(equip);
				}
			}
		}
	}

	/** 初始携带道具 */
	public class InitItem {
		public String itemCode;
		public int itemNum;

		public InitItem(String itemCode, int itemNum) {
			this.itemCode = itemCode;
			this.itemNum = itemNum;
		}
	}

	/**
	 * 初始携带技能
	 * 
	 * @author c
	 *
	 */
	public class InitSkill {
		public int id;
		public int level;
		public int pos;

		public InitSkill(int id, int level, int pos) {
			this.id = id;
			this.level = level;
			this.pos = pos;
		}
	}
}