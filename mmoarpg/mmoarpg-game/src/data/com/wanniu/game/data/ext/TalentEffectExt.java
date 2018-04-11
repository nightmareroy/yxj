package com.wanniu.game.data.ext;

import java.util.HashMap;
import java.util.Map;

import com.wanniu.core.logfs.Out;
import com.wanniu.core.util.StringUtil;
import com.wanniu.game.data.TalentEffectCO;

public class TalentEffectExt extends TalentEffectCO {
	/**
	 * 技能战斗力,index表示等级，value表示战斗力的值
	 */
	public int[] power_arr;

	public Map<Integer, Integer> replaceSkillMap;

	@Override
	public void initProperty() {
		if (StringUtil.isNotEmpty(power)) {
			String[] str_arr = power.split(";");
			int len = str_arr.length;
			power_arr = new int[len + 1];
			power_arr[0] = 0;
			for (int i = 0; i < len; i++) {
				String str = str_arr[i];
				String[] a_str = str.split(":");
				int lvl = Integer.parseInt(a_str[0]);
				if (lvl != i + 1) {
					Out.error("天赋的战力有问题,talentID=" , this.talentID);
				}
				power_arr[Integer.parseInt(a_str[0])] = Integer.parseInt(a_str[1]);
			}
		}

		if (talentType == 2) {
			replaceSkillMap = new HashMap<Integer, Integer>();
			String[] str_arr = replaceSkill.split(";");
			int len = str_arr.length;
			for (int i = 0; i < len; i++) {
				String str = str_arr[i];
				String[] a_str = str.split(":");
				int lvl = Integer.parseInt(a_str[0]);
				if (lvl != i + 1) {
					Out.error("天赋脚本的替换的技能,talentID=" , this.talentID);
				}
				replaceSkillMap.put(lvl, Integer.parseInt(a_str[1]));
			}
		}
	}

	/**
	 * 获取技能战力
	 * 
	 * @param lvl
	 *            技能等级
	 * @return
	 */
	public int getSkillPower(int lvl) {
		if (lvl < 0)
			return 0;
		if (power_arr != null) {
			// wuyonghui(吴永辉) 07-06 17:43:28
			// 如果等级超过这个数组了，取最后一个
			if (lvl > power_arr.length - 1) {
				return power_arr[power_arr.length - 1];
			}
			return power_arr[lvl];
		}
		return 0;
	}

	public int getReplaceSkillId(int talentLv) {
		if (replaceSkillMap != null && replaceSkillMap.containsKey(talentLv))
			return replaceSkillMap.get(talentLv);
		return 0;
	}
}
