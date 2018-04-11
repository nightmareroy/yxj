package com.wanniu.game.data.ext;

import java.util.HashMap;
import java.util.Map;

import com.wanniu.core.util.StringUtil;
import com.wanniu.game.common.Const.PlayerBtlData;
import com.wanniu.game.data.PassiveSkillCO;

public class PassiveSkillExt extends PassiveSkillCO{
	public PlayerBtlData ValueAttribute1;
	public Map<Integer, Integer> ValueSetMap1;
	public PlayerBtlData ValueAttribute2;
	public Map<Integer, Integer> ValueSetMap2;
	public PlayerBtlData ValueAttribute3;
	public Map<Integer, Integer> ValueSetMap3;
	
	public void initProperty() {
		if(StringUtil.isNotEmpty(valueAttributeName1)){
			ValueAttribute1 = PlayerBtlData.getEByKey(valueAttributeName1);
			ValueSetMap1 = new HashMap<Integer, Integer>();
			String[] ss = valueSet.split(";");
			int len = ss.length;
			for (int i = 0; i < len; i++) {
				String str = ss[i];
				String[] a_str = str.split(":");
				if (a_str != null && a_str.length >= 2) {
					ValueSetMap1.put(Integer.parseInt(a_str[0]), Integer.parseInt(a_str[1]));
				}
			}
		}
		if(StringUtil.isNotEmpty(valueAttributeName2)){
			ValueAttribute2 = PlayerBtlData.getEByKey(valueAttributeName2);
			ValueSetMap2 = new HashMap<Integer, Integer>();
			String[] ss = valueSet2.split(";");
			int len = ss.length;
			for (int i = 0; i < len; i++) {
				String str = ss[i];
				String[] a_str = str.split(":");
				if (a_str != null && a_str.length >= 2) {
					ValueSetMap2.put(Integer.parseInt(a_str[0]), Integer.parseInt(a_str[1]));
				}
			}
		}
		if(StringUtil.isNotEmpty(valueAttributeName3)){
			ValueAttribute3 = PlayerBtlData.getEByKey(valueAttributeName3);
			ValueSetMap3 = new HashMap<Integer, Integer>();
			String[] ss = valueSet3.split(";");
			int len = ss.length;
			for (int i = 0; i < len; i++) {
				String str = ss[i];
				String[] a_str = str.split(":");
				if (a_str != null && a_str.length >= 2) {
					ValueSetMap3.put(Integer.parseInt(a_str[0]), Integer.parseInt(a_str[1]));
				}
			}
		}
	}
}
