package com.wanniu.game.sevengoal;

import java.util.HashMap;
import java.util.Map;

public class DayInfo {
	public int dayId;
	public Map<Integer, TaskInfo> taskMap;
	public boolean fetched;
	
	public DayInfo() {
		dayId=0;
		taskMap=new HashMap<>();
		fetched=false;
	}
}
