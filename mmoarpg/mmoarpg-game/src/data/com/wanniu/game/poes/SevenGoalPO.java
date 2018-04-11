package com.wanniu.game.poes;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.wanniu.core.game.entity.GEntity;
import com.wanniu.game.DBTable;
import com.wanniu.game.common.Table;
import com.wanniu.game.data.GameData;
import com.wanniu.game.data.SevDayActivityCO;
import com.wanniu.game.data.SevDayTaskCO;
import com.wanniu.game.data.SevTaskRewardCO;
import com.wanniu.game.data.TurnRewardCO;
import com.wanniu.game.data.ext.SevTaskRewardExt;
import com.wanniu.game.sevengoal.DayInfo;
import com.wanniu.game.sevengoal.TaskInfo;
import com.wanniu.game.sevengoal.SevenGoalManager.SevenGoalTaskType;



@DBTable(Table.player_seven_goal)
public class SevenGoalPO extends GEntity {

	public Map<Integer, DayInfo> dayInfoMap;
	
	public int currentTurn;
	
	public SevenGoalPO() {
		reset(-1);
		
	}
	
	public void reset(int newTurn) {
		dayInfoMap=new HashMap<>();
		for (SevDayTaskCO sevDayTaskCO : GameData.SevDayTasks.values()) {
			DayInfo dayInfo = dayInfoMap.get(sevDayTaskCO.date);
			if(dayInfo==null) {
				dayInfo = new DayInfo();
				dayInfo.dayId=sevDayTaskCO.date;
				dayInfoMap.put(dayInfo.dayId, dayInfo);
			}
			
			TaskInfo taskInfo = new TaskInfo();// dayInfo.taskMap.get(key)
			taskInfo.taskId=sevDayTaskCO.iD;
			dayInfo.taskMap.put(taskInfo.taskId, taskInfo);
		}
		
		currentTurn=newTurn;
	}
	
	public void processAddFriend() {
		for (DayInfo dayInfo : dayInfoMap.values()) {
			for (TaskInfo taskInfo : dayInfo.taskMap.values()) {
				SevDayTaskCO sevDayTaskCO = GameData.SevDayTasks.get(taskInfo.taskId);
				SevenGoalTaskType sevenGoalTaskType=SevenGoalTaskType.getType(sevDayTaskCO.style);
				if(sevenGoalTaskType==SevenGoalTaskType.ADD_FRIEND) {
					taskInfo.finishedNum=Math.min(taskInfo.finishedNum+1, sevDayTaskCO.targetNum) ;
				}
			}
		}
	}
	
	public void processPayCount() {
		for (DayInfo dayInfo : dayInfoMap.values()) {
			for (TaskInfo taskInfo : dayInfo.taskMap.values()) {
				SevDayTaskCO sevDayTaskCO = GameData.SevDayTasks.get(taskInfo.taskId);
				SevenGoalTaskType sevenGoalTaskType=SevenGoalTaskType.getType(sevDayTaskCO.style);
				if(sevenGoalTaskType==SevenGoalTaskType.PAY_COUNT) {
					taskInfo.finishedNum=Math.min(taskInfo.finishedNum+1, sevDayTaskCO.targetNum) ;
				}
			}
		}
	}
}
