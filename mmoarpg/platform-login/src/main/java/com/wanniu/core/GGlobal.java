package com.wanniu.core;

import java.nio.ByteOrder;

import io.netty.util.AttributeKey;


/**
 * 游戏中的常量定义
 * @author agui
 */
public interface GGlobal extends GFile, GConst {
	
	/** 用户连接的渠道编号标记 */
	AttributeKey<Integer> 			_KEY_CHANNEL_ID 				= AttributeKey.valueOf("_KEY_CHANNEL_ID");
	
	/** 用户连接的验证标记 */
	AttributeKey<String> 			_KEY_TOKEN 						= AttributeKey.valueOf("_KEY_TOKEN");

	/** 连接中的角色账号ID */
	AttributeKey<String> 			_KEY_USER_ID 					= AttributeKey.valueOf("_KEY_USER_ID");
	
	/**通讯协议默认字节序*/
	ByteOrder __BYTE_ORDER 				= ByteOrder.LITTLE_ENDIAN;
	
	/**默认上行字节缓存容量*/
	int __BUFFER_CAPACITY 				= GConfig.getInstance().getInt("tcp.buffer.capacity", 256);
	/**默认下行字节缓存容量*/
	int __BODY_CAPACITY 				= GConfig.getInstance().getInt("tcp.body.capacity", 512);
	/**请求包最大包体长度*/
	int __REQUEST_MAX_LEN 				= GConfig.getInstance().getInt("tcp.request.maxlen", 1024 * 10);
	/**返回包最大包体长度*/
	int __RESPONSE_MAX_LEN 				= GConfig.getInstance().getInt("tcp.response.maxlen", 1024 * 512);

	/** TCP空闲时间标志 */
	int __TPC_IDLE_TIME 				=  GConfig.getInstance().getInt("tcp.time.idle", 60000);

	/** 客户端无消息超时时间 */
	int __CLIENT_TIMEOUT 				= GConfig.getInstance().getInt("tcp.time.out", __TPC_IDLE_TIME);

	/** 战斗服务线程的调度时间 */
	long __BATTLE_YIELD_TIME 			= GConfig.getInstance().getInt("game.battle.yield", 10);

}
