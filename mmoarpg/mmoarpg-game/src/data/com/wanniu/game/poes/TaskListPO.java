package com.wanniu.game.poes;

import java.util.Map;

import com.wanniu.core.game.entity.GEntity;
import com.wanniu.game.DBTable;
import com.wanniu.game.common.Table;
import com.wanniu.game.task.LoopResult;
import com.wanniu.game.task.po.TaskPO;

@DBTable(Table.player_tasks)
public final class TaskListPO extends GEntity {

	public Map<Integer, TaskPO> normalTasks;

	public Map<Integer, Integer> finishedNormalTasks;

	public Map<Integer, TaskPO> dailyTasks;

	public Map<Integer, Integer> finishedDailyTasks;
	
	public Map<Integer, TaskPO> treasureTasks;
	
	public Map<Integer, Integer> finishedTreasureTasks;
	
	/**当日完成的一条龙任务数*/
	public int todayLoopTaskCount;
	/** 当然完成的师门任务数 */
	public int todayDailyTaskCount;
	/** 一条龙任务是否已经被打断过了(玩家首次玩一条龙10次就中断) */
	public boolean loopBreaked;
	/**皓月镜每5轮结算参数*/
	public LoopResult loopResult;
	
	public TaskListPO() {
		
	}
	
}
