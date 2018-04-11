package com.wanniu.tcp.protocol;


import com.wanniu.pay.request.GClientEvent;

import io.netty.channel.Channel;

/**
 * 描述：网络报文处理基类
 * @author agui
 */
public abstract class NetHandler {

	public NetHandler() {
		GClientEvent handler = this.getClass().getAnnotation(GClientEvent.class);
		if (handler != null) {
			this.type = handler.value();
		}
	}
	
	private Channel __session__;

	/** 协议编号|类型 */
	private short type;

	/** 获取协议编号|类型 */
	public short getType() {
		return type;
	}

	/** 绑定会话 */
	public void bindSession(Channel session) {
		this.__session__ = session;
	}

	/** 发送给当前处理的连接 */
	public void send(Message msg) {
		if (__session__ != null) {
			__session__.writeAndFlush(msg);
		}
	}

	/** 直接写入，用于非广播消息 */
	public void write(Message msg) {
		if (__session__ != null) {
			__session__.writeAndFlush(msg.getContent());
		}
	}

	/**
	 * 处理
	 * @param pak
	 *            待处理包，封装了请求类型以及请求内容
	 * @return 处理结果成功与否
	 */
	public abstract void execute(Packet pak);

	public boolean isGateHandler() {
		return false;
	}

	public int getRunIndex() {
		return 0;
	}

}
