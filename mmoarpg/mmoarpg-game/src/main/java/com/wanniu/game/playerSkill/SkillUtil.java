package com.wanniu.game.playerSkill;

import java.util.ArrayList;
import java.util.Map;

import com.wanniu.core.logfs.Out;
import com.wanniu.game.data.GameData;
import com.wanniu.game.data.ext.SkillDataExt;
import com.wanniu.game.data.ext.SkillValueExt;
import com.wanniu.game.data.ext.SkillValueExt.SkillValueSetData;

public class SkillUtil {

	public static SkillDataExt getProp(int skillId) {
		// List<SkillDataExt> list = GameData.findSkillDatas((t) -> t.skillID ==
		// skillId);
		// if (list.size() > 0)
		// return list.get(0);
		return GameData.SkillDatas.get(skillId);
		// return null;
	}

	public static ArrayList<String> getDesData(int skillId, int skillLevel) {
		SkillValueExt exProp = GameData.SkillValues.get(skillId);
		Out.debug("this.exProp: ", exProp);
		ArrayList<String> desData = new ArrayList<>();
		// $1
		float dmgRate = (exProp.dmgRate + (skillLevel - 1) * exProp.dmgRatePerLvl) / 10000f;
		desData.add("" + dmgRate);
		// $2
		int exdDmg = 0;
		if (exProp.exdDmgSetData.containsKey(skillLevel + "")) {
			exdDmg = exProp.exdDmgSetData.get(skillLevel + "");
		}

		desData.add("" + (exdDmg > 0 ? exdDmg : 0));
		// $3
		int chance = exProp.chance / 100;
		desData.add("" + chance);
		// $4
		int buffTime = 0;
		if (exProp.BuffTimeData.containsKey(skillLevel)) {
			buffTime = exProp.BuffTimeData.get(skillLevel) / 1000;
		}
		// int buffTime = exProp.BuffTimeData.get(skillLevel) > 0
		// ?exProp.BuffTimeData.get(skillLevel)/1000:0;
		desData.add("" + buffTime);
		// $5---$7
		for (int j = 1; j < 4; ++j) {
			Map<String, SkillValueSetData> valueSetData = exProp.valueSetData;
			SkillValueSetData valuedata = valueSetData.get("valueSetData" + j);
			if (valuedata != null && valuedata.valueSet != null) {
				Map<String, Integer> valueSet = valuedata.valueSet;
				Out.debug(valueSet);
				try {
					if (valueSet != null && valueSet.get("" + skillLevel) > 0) {

						float relative = 1;

						int ValueAttribute = valuedata.valueAttribute; // this.exProp["ValueAttribute" + j];//
																		// this.exProp.valueAttributes[j].valueAttribute;

						if (ValueAttribute == 1) {
							relative = 100;
						} else if (ValueAttribute == 2) {
							relative = 1000;
						} else if (ValueAttribute == 3) {
							relative = 10000;
						}

						desData.add("" + valueSet.get("" + skillLevel) / relative);
					} else {
						desData.add("" + 0);
					}
				} catch (Exception e) {
					Out.error(e);
				}

			} else {
				desData.add("" + 0);
			}
		}
		// Out.debug(skillDB.id + "skill getDesData: "+ skillLevel +"--- ", desData);
		return desData;
	}
}
