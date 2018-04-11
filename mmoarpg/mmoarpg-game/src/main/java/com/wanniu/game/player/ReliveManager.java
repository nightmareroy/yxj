package com.wanniu.game.player;

import java.util.HashMap;
import java.util.Map;

import com.wanniu.game.data.GameData;
import com.wanniu.game.data.ResurrectionCO;

public class ReliveManager {

	public int payCost;
	public int payConfirm = 1; // 原地复活付费弹框
	public int nowReliveNum; // 原地复活
	public Map<Integer, Integer> reliveHistory = new HashMap<>();

	public void addRelive(int areaId) {
		reliveHistory.put(areaId, getReliveCount(areaId) + 1);
	}

	public int getReliveCount(int areaId) {
		return reliveHistory.containsKey(areaId) ? reliveHistory.get(areaId) : 0;
	}

	public int getReliveTotal(int areaId) {
		ResurrectionCO resurrection = GameData.Resurrections.get(areaId);
		return resurrection != null ? resurrection.resurrectNum : 0;
	}

	public void refreshNewDay() {
		nowReliveNum = 0;
		reliveHistory.clear();
	}

}
