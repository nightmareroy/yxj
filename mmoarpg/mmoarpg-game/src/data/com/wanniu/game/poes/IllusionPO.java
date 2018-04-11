package com.wanniu.game.poes;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * 幻境存储对象
 * 
 * @author Yangzz
 */
public class IllusionPO {

	/** 今日获得的经验 */
	public int todayExp;

	/** 今日获得的修为 */
	public int todayClassExp;

	/** 今日获得的银两 */
	public int todayGold;

	public Map<Integer, Integer> boxs;

	public Map<String, Integer> items;

	public boolean hasBoxData() {
		return boxs != null && !boxs.isEmpty();
	}
	public boolean hasItemData() {
		return items != null && !items.isEmpty();
	}

	public void resetBoxData() {
		if (boxs != null) {
			boxs.clear();
		}
	}
	
	public void resetItemData() {
		if (items != null) {
			items.clear();
		}
	}

	public void putBox(int lv, int count) {
		if (boxs == null) {
			boxs = new HashMap<>();
		}
		Integer cur = boxs.get(lv);
		if (cur == null) {
			boxs.put(lv, count);
		} else {
			boxs.put(lv, count + cur);
		}
	}
	
	public void putItem(String code,int count) {
		if (items == null) {
			items = new HashMap<>();
		}
		Integer cur = items.get(code);
		if (cur == null) {
			items.put(code, count);
		} else {
			items.put(code, count + cur);
		}
	}
	
	public int calTotalItemNum(String code) {
		if(items == null || items.isEmpty()) {
			return 0;
		}
		Integer c = items.get(code);
		return c == null ? 0 : c;
	}

	public int calTotalNum() {
		if (boxs == null || boxs.isEmpty()) {
			return 0;
		}
		int total = 0;
		Collection<Integer> cols = boxs.values();
		for (Integer i : cols) {
			total += i;
		}
		return total;
	}

	public IllusionPO() {

	}
}
