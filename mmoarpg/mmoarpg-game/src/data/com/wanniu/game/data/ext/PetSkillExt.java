package com.wanniu.game.data.ext;

import java.util.HashMap;
import java.util.Map;

import com.wanniu.core.logfs.Out;
import com.wanniu.core.util.ClassUtil;
import com.wanniu.core.util.StringUtil;
import com.wanniu.game.common.Const.PlayerBtlData;
import com.wanniu.game.common.Utils;
import com.wanniu.game.data.PetSkillCO;

/**
 * @since 2017/2/7 15:18:42
 * @author auto generate
 */
public class PetSkillExt extends PetSkillCO {

	public Map<PlayerBtlData, Map<Integer, Integer>> attributeValues = new HashMap<>();
	/**
	 * 技能战斗力,index表示等级，value表示战斗力的值
	 */
	public int[] power_arr;
	
	public void initProperty() {

		for (int i = 1; i < 4; i++) {

			String strValueSet = "valueSet";
			if (i > 1) {
				strValueSet += i;
			}

			Map<Integer, Integer> attributeValue = new HashMap<>();
			try {
				String[] valueSet = ClassUtil.getProperty(this, strValueSet).toString().split(";");

				for (String attribute : valueSet) {

					String[] as = attribute.split(":");

					if (as.length > 1) {

						int level = Integer.parseInt(as[0]);

						String value = as[1];

						String[] tmp = value.split("-");
						int _value = 0;
						if (tmp.length > 1) {

							_value = Utils.random(Integer.parseInt(tmp[0]), Integer.parseInt(tmp[1]));
						} else {
							_value = Integer.parseInt(value);
						}

						attributeValue.put(level, _value);

					}
				}

				String attributeName = (String) ClassUtil.getProperty(this, "valueAttributeName" + i);

				if (StringUtil.isNotEmpty(attributeName)) {
					if(PlayerBtlData.getEByKey(attributeName)==null){
						continue;
					}
					attributeValues.put(PlayerBtlData.getEByKey(attributeName), attributeValue);
				}
			} catch (Exception e) {
				Out.error(e);
			}
		}
		
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
					Out.error("宠物技能脚本的战力有问题,skillid=" + this.skillID);
				}
				power_arr[Integer.parseInt(a_str[0])] = Integer.parseInt(a_str[1]);
			}
		}
		
	}

	/**
	 * 获取技能战力
	 * @param lvl 技能等级
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
}