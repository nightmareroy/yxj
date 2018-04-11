package com.wanniu;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

/**
 * 定时任务执行器
 */
public class GmScheduledExecutor {
	public static ScheduledExecutorService executor = Executors.newScheduledThreadPool(5);
	/**
	 * 正在执行的Scheduled，用来控制任务的取消
	 */
	public static Map<String, ScheduledFuture<?>> futures = new ConcurrentHashMap<>();
}
