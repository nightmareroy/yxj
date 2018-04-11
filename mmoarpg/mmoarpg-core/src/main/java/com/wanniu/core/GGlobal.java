package com.wanniu.core;

import java.nio.ByteOrder;

import com.wanniu.core.game.entity.GPlayer;

import cn.qeng.common.login.TokenInfo;
import io.netty.util.AttributeKey;

/**
 * 游戏中的常量定义
 * 
 * @author agui
 */
public interface GGlobal extends GFile, GConst {

	/** 玩家标识（存放角色ID - int） */
	AttributeKey<GPlayer> __KEY_PLAYER = AttributeKey.valueOf("__KEY_PLAYER");

	/** 已登录认证 */
	AttributeKey<Boolean> __KEY_SECURITY = AttributeKey.valueOf("__KEY_SECURITY");

	/** 用户连接的验证标记 */
	AttributeKey<String> __KEY_TOKEN = AttributeKey.valueOf("__KEY_TOKEN");

	/** 连接中的角色账号ID */
	AttributeKey<String> __KEY_USER_ID = AttributeKey.valueOf("__KEY_USER_ID");

	/** 标识用户角色数 */
	AttributeKey<Integer> __KEY_ROLE_COUNT = AttributeKey.valueOf("__KEY_ROLE_COUNT");

	/** 逻辑服务器ID */
	AttributeKey<Integer> __KEY_LOGIC_SERVERID = AttributeKey.valueOf("__KEY_LOGIC_SERVERID");

	/** 连接超时 */
	AttributeKey<Boolean> __KEY_SESSION_TIMEOUT = AttributeKey.valueOf("__KEY_SESSION_TIMEOUT");
	/** 登录Token */
	AttributeKey<TokenInfo> __KEY_TOKEN_INFO = AttributeKey.valueOf("__KEY_TOKEN_INFO");

	/** 远程回调 */
	byte __HEAD_CALLBACK_REMOTE = Byte.MAX_VALUE;
	/** 默认的远程回调 */
	byte __HEAD_CALLBACK_DEFAULT = -1;

	/** 通讯协议默认字节序 */
	ByteOrder __BYTE_ORDER = ByteOrder.LITTLE_ENDIAN;

	/** 默认上行字节缓存容量 */
	int __BUFFER_CAPACITY = GConfig.getInstance().getInt("tcp.buffer.capacity", 256);
	/** 默认下行字节缓存容量 */
	int __BODY_CAPACITY = GConfig.getInstance().getInt("tcp.body.capacity", 512);
	/** 请求包最大包体长度 */
	int __REQUEST_MAX_LEN = GConfig.getInstance().getInt("tcp.request.maxlen", 1024 * 10);
	/** 返回包最大包体长度 */
	int __RESPONSE_MAX_LEN = GConfig.getInstance().getInt("tcp.response.maxlen", 1024 * 512);

	/** TCP空闲时间标志 */
	int __TPC_IDLE_TIME = GConfig.getInstance().getInt("tcp.time.idle", 60000);

	/** 客户端无消息超时时间 */
	int __CLIENT_TIMEOUT = GConfig.getInstance().getInt("tcp.time.out", __TPC_IDLE_TIME);

	/** 战斗服务线程的调度时间 */
	long __BATTLE_YIELD_TIME = GConfig.getInstance().getInt("game.battle.yield", 10);

}
