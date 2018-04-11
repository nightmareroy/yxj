package com.wanniu.game.data.ext;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.wanniu.game.data.SuitListCO;
import com.wanniu.game.equip.NormalEquip;

/**
 * 套装集合
 * @author Yangzz
 *
 */
public class SuitListExt extends SuitListCO {

	public List<String> partCodes;

	@Override
	public void initProperty() {
		partCodes = new ArrayList<>();
		String[] pars = this.partCodeList.split(",");
		for(String code : pars) {
			partCodes.add(code);
		}
	}
	
	/**
	 * 获取 身上的装备有几件是套装
	 * @return 是套装的 id
	 */
	public List<String> getContaintsCode(Map<Integer, NormalEquip> equips) {
		List<String> list = new ArrayList<>();
		for(NormalEquip equip : equips.values()) {
			if(equip == null) {
				continue;
			}
			if(partCodes.contains(equip.itemDb.code)) {
				list.add(equip.itemDb.code);
			}
		}
		return list;
	}
}
