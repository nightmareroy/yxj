package com.wanniu.core.game;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import com.wanniu.core.logfs.Out;

/**
 * 系统排程业务工厂 - 单任务
 * @author agui
 */
public final class JobFactory {
	
	private JobFactory(){}

	/** 排程业务 */
	private static ScheduledExecutorService 				jobExec;
	
	static{
		jobExec = Executors.newSingleThreadScheduledExecutor(new PoolFactory("排程业务"));
	}
	
	/**
	 * @param runnable
	 * @param initialDelay
	 * @param delay
	 * @param unit
	 */
	public static ScheduledFuture<?> addScheduleJob(Runnable runnable, long initialDelay, long delay, TimeUnit unit){
		try {
			return jobExec.scheduleWithFixedDelay(runnable,  Math.max(1L, initialDelay), delay, unit);
		} catch (Exception e) {
			Out.error(e);
		}
		return null;
	}
	
	/**
	 * 添加计划任务，以秒为单位(执行后指定延时)
	 * @param runnable
	 * @param initialDelay
	 * @param delay
	 */
	public static ScheduledFuture<?> addScheduleJob(Runnable runnable,long initialDelay, long delay){
		return addScheduleJob(runnable, initialDelay, delay, TimeUnit.SECONDS);
	}
	
	/**
	 * 添加计划任务，以秒为单位(指定延时)
	 * @param runnable
	 * @param initialDelay
	 * @param period
	 */
	public static ScheduledFuture<?> addFixedRateJob(Runnable runnable, long initialDelay, int period){
		try {
			return jobExec.scheduleAtFixedRate(runnable, Math.max(1L, initialDelay), period, TimeUnit.MILLISECONDS);
		} catch (Exception e) {
			Out.error(e);
		}
		return null;
	}
	
	/**
	 * 添加延期任务，以毫秒为单位（如：过期技能的过期处理）
	 */
	public static ScheduledFuture<?> addDelayJob(Runnable runnable, long delay){
		return addDelayJob(runnable, delay, TimeUnit.MILLISECONDS);
	}
	
	public static ScheduledFuture<?> addDelayJob(Runnable runnable, long delay, TimeUnit unit){
		return jobExec.schedule(runnable, delay, unit);
	}
	
	/**
	 * 
	 * 功能描述：添加指定执行次数的任务，以毫秒为单位
	 * @param runnable 
	 * @param initialDelay 初始执行时间
	 * @param delay 间隔执行时间
	 * @param count 执行次数
	 * @return
	 */
	public static ScheduledFuture<?> addFixJob(final Runnable runnable, long initialDelay, final long delay, final int count){
		return addDelayJob(new Runnable() {
			int total = count;
			@Override
			public void run() {
				runnable.run();
				if(--total > 0) {
					addFixJob(runnable, delay, delay, total);
				}
			}
		}, initialDelay);
	}
	
}
