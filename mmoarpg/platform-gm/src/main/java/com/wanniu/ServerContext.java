package com.wanniu;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/** 服务管理类 */
public class ServerContext {

	/** 客户端响应信号通知集合 */
	public static Map<Long, CDKeyLock> CDKEYLOCKS = new ConcurrentHashMap<Long, CDKeyLock>();

	/** 客户端响应信号通知键生成器 */
	public static AtomicLong KEYS = new AtomicLong();

}
