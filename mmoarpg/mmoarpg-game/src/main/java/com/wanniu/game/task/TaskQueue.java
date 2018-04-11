package com.wanniu.game.task;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import com.wanniu.core.game.PoolFactory;

/**
 * 任务队列
 * 
 * @author agui
 */
public class TaskQueue {

	/** 任务执行器 */
	private static Executor TaskExecutor = Executors.newSingleThreadExecutor(new PoolFactory("任务处理器"));

	private TaskQueue() {}

	/**
	 * 触发任务事件
	 */
	public static void put(TaskEvent event) {
		TaskExecutor.execute(event);
	}

}