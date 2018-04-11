package com.wanniu.tcp;

import io.netty.channel.Channel;

/**
 * 网络处理事件接口
 * @author agui
 */
public interface NetEvent {

	/** 绑定会话 */
	void bind(Channel session);

	/** 发送ping包 */
	void ping();

	/**关闭连接回调  */
	void close();

}
