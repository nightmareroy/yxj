package com.wanniu.core.tcp.protocol;

import com.wanniu.core.GGlobal;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.entity.GPlayer;
import com.wanniu.core.logfs.Out;

import io.netty.channel.Channel;

/**
 * 描述：网络报文处理基类
 * 
 * @author agui
 */
public abstract class NetHandler {

	public NetHandler() {
		GClientEvent handler = this.getClass().getAnnotation(GClientEvent.class);
		if (handler != null) {
			this.type = handler.type();
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

	/** 直接写入，用于非广播消息 */
	public void write(Message msg) {
		if (__session__ != null) {
			if (!__session__.isActive()) {
				GPlayer pl = __session__.attr(GGlobal.__KEY_PLAYER).get();
				String pId = pl == null ? "" : pl.getId();
				Out.warn("send msg warning noActivity,pid=", pId);
			} else if (!__session__.isWritable()) {
				GPlayer pl = __session__.attr(GGlobal.__KEY_PLAYER).get();
				String pId = pl == null ? "" : pl.getId();
				Out.warn("send msg warning noWriteable,pid=", pId);
			} else {
				__session__.writeAndFlush(msg, __session__.voidPromise());
			}
		}
	}

	/**
	 * 处理
	 * 
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
