package com.wanniu.core.pay.request;

import com.wanniu.core.pay.PayClient;
import com.wanniu.core.tcp.protocol.Message;
import com.wanniu.core.tcp.protocol.NetHandler;

/**
 * 充值报文处理基类
 * @author agui
 */
public abstract class PayHandler extends NetHandler {
	
	protected static final byte CALLBACK_DEFAULT = 0;
	
	public void send(Message msg) {
		PayClient.getInstance().add(msg);
	}

}
