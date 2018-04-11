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
	 * @param job
	 * @param initialDelay
	 * @param delay
	 * @param unit
	 */
	public static ScheduledFuture<?> addScheduleJob(final Runnable job, long initialDelay, long delay, TimeUnit unit) {
		return jobExec.scheduleWithFixedDelay(new Runnable() {
			@Override
			public void run() {
				try {
					job.run();
				} catch (Exception e) {
					Out.error(e);
				}
			}
		}, Math.max(1L, initialDelay), delay, unit);
	}

	/**
	 * 添加计划任务，以秒为单位(执行后指定延时)
	 * 
	 * @param job
	 * @param initialDelay
	 * @param delay
	 */
	public static ScheduledFuture<?> addScheduleJob(final Runnable job, long initialDelay, long delay) {
		return addScheduleJob(job, initialDelay, delay, TimeUnit.MILLISECONDS);
	}

	/**
	 * 添加计划任务，以毫秒为单位(指定延时)
	 * 
	 * @param job
	 * @param initialDelay
	 * @param period
	 */
	public static ScheduledFuture<?> addFixedRateJob(final Runnable job, long initialDelay, long period) {
		return jobExec.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				try {
					job.run();
				} catch (Exception e) {
					Out.error(e);
				}
			}
		}, Math.max(1L, initialDelay), period, TimeUnit.MILLISECONDS);
	}

	/**
	 * 添加延期任务，以毫秒为单位（如：过期技能的过期处理）
	 */
	public static ScheduledFuture<?> addDelayJob(Runnable job, long delay){
		return addDelayJob(job, delay, TimeUnit.MILLISECONDS);
	}
	
	public static ScheduledFuture<?> addDelayJob(final Runnable job, long delay, TimeUnit unit){
		return jobExec.schedule(new Runnable() {
			@Override
			public void run() {
				try {
					job.run();
				} catch (Exception e) {
					Out.error(e);
				}
			}
		}, delay, unit);
	}
	
	/**
	 * 
	 * 功能描述：添加指定执行次数的任务，以毫秒为单位
	 * @param job 
	 * @param initialDelay 初始执行时间
	 * @param delay 间隔执行时间
	 * @param count 执行次数
	 * @return
	 */
	public static ScheduledFuture<?> addFixJob(final Runnable job, long initialDelay, final long delay, final int count){
		return jobExec.schedule(new Runnable() {
			int total = count;
			@Override
			public void run() {
				try {
					job.run();
				} catch (Exception e) {
					Out.error(e);
				} finally {
					if (--total > 0) {
						addFixJob(job, delay, delay, total);
					}
				}
			}
		}, initialDelay, TimeUnit.MILLISECONDS);
	}
	
}
