package com.wanniu.game.task.po;

import java.util.HashMap;
import java.util.Map;

public class TaskPO {

	public int templateId;
	public int state;
	public int progress;
	public Map<String, String> battle_attributes = new HashMap<>();
	public int finishCount;
	public Map<Integer, Integer> extendData = new HashMap<>();

}
