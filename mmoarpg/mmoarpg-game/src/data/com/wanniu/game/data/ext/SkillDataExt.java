package com.wanniu.game.data.ext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.wanniu.core.logfs.Out;
import com.wanniu.core.util.StringUtil;
import com.wanniu.game.common.Const;
import com.wanniu.game.data.SkillDataCO;

public class SkillDataExt extends SkillDataCO {

	public int pro_;

	public List<Integer> lvReqData;

	public Map<Integer, Integer> costReqData;

	public List<CostItemReqData> costItemReqData;
	
	/**
	 * 技能战斗力,index表示等级，value表示战斗力的值
	 */
	public int[] power_arr;
	
	/** 属性构造 */
	@Override
	public void initProperty() {
		pro_ = Const.PlayerPro.Value(super.pro);

		String lvReqStr = this.upReqLevel;
		lvReqData = new ArrayList<>();
		if (StringUtil.isNotEmpty(lvReqStr)) {
			String[] a_lvReqStr = lvReqStr.split(";");
			int len = a_lvReqStr.length;
			for (int i = 0; i < len; i++) {
				lvReqData.add(Integer.parseInt(a_lvReqStr[i]));
			}
		}
		lvReqData.add(-1);

		String costReqStr = this.upCostGold;
		costReqData = new HashMap<>();
		if (StringUtil.isNotEmpty(costReqStr)) {
			String[] a_costReqStr = costReqStr.split(";");
			int len = a_costReqStr.length;	
			for (int i = 0; i < len; i++) {
				String str = a_costReqStr[i];
				String[] a_str = str.split(":");
				if (a_str != null && a_str.length >= 2) {
					costReqData.put(Integer.parseInt(a_str[0]) , Integer.parseInt(a_str[1]));
//					if (i == len - 1) {
//						costReqData.put(Integer.parseInt(a_str[0]), -1);
//					}
				}
			}
		}

		String costItemReqStr = this.upCostItem;
		costItemReqData = new ArrayList<>();
		if (StringUtil.isNotEmpty(costItemReqStr)) {
			String[] a_costItemReqStr = costItemReqStr.split(";");
			int len = a_costItemReqStr.length;
			for (int i = 0; i < len; i++) {
				String str = a_costItemReqStr[i];
				String[] a_str = str.split(":");
				if (a_str != null && a_str.length >= 2) {
					costItemReqData.add(new CostItemReqData(i, a_str[0], Integer.parseInt(a_str[1])));
					if (i == len - 1) {
						costItemReqData.add(new CostItemReqData(i, a_str[0], -1));
					}
				}
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
					Out.error("技能脚本的战力有问题,skillid=" , this.skillID);
				}
				power_arr[Integer.parseInt(a_str[0])] = Integer.parseInt(a_str[1]);
			}
		}

	}

	public class CostItemReqData {
		public int lv;
		public String itemCode;
		public int num;

		public CostItemReqData(int lv, String itemCode, int num) {
			this.lv = lv;
			this.itemCode = itemCode;
			this.num = num;
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