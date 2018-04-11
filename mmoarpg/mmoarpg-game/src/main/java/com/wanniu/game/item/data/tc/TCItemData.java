package com.wanniu.game.item.data.tc;

import java.util.List;

/**
 * TC掉落单个物品
 * 
 * @author Yangzz
 *
 */
public class TCItemData {

	/** 装备类型掉落 */
	public static final int TC_EQUIP_TYPE = 1;
	/** itemCode#num掉落 */
	public static final int TC_ITEMCODE = 2;
	/** 嵌套TC */
	public static final int TC_INNER_TC = 3;

	/** 1:装备类型掉落 2:itemCode#num掉落 3:嵌套TC */
	public int tcType;

	public String code;

	public int num;

	public int minNum;

	public int maxNum;

	public int rare;

	public List<Integer> expandParas;

	public TCItemData() {

	}

	public TCItemData(String code, int num, int rare, List<Integer> expandParas, int minNum, int maxNum) {
		this.code = code;
		this.num = num;
		this.rare = rare;
		this.expandParas = expandParas;

		this.minNum = minNum;
		this.maxNum = maxNum;
	}
}
