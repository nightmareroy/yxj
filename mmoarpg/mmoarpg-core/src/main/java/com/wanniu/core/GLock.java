package com.wanniu.core;

import java.util.concurrent.CountDownLatch;

/**
 * 自定义锁
 * @author agui
 */
public class GLock {

	/** 执行统计 */
	private CountDownLatch counter;
	/** 返回的json对象 */
	private String json;

	public GLock(CountDownLatch counter) {
		this.counter = counter;
	}

	public CountDownLatch getCounter() {
		return counter;
	}

	public void countDown() {
		counter.countDown();
	}

	public String getJson() {
		return json;
	}

	public void setJson(String json) {
		this.json = json;
	}

}
