package com.wanniu.core.gm.request;

import com.wanniu.core.gm.GMClient;
import com.wanniu.core.tcp.protocol.Message;
import com.wanniu.core.tcp.protocol.NetHandler;

/**
 * GM报文处理基类
 * @author agui
 */
public abstract class GMHandler extends NetHandler {
	
	protected static final byte CALLBACK_DEFAULT = 0;
	
	/** 发送到GM服务 */
	public void sendGM(Message msg) {
		GMClient.getInstance().add(msg);
	}

}
