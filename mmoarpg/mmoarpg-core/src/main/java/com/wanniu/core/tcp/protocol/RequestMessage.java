package com.wanniu.core.tcp.protocol;

/**
 * 请求消息基类
 * @author agui
 */
public abstract class RequestMessage extends Message {
	
	protected long reqId;	// 请求的标识，需要透传响应（推荐在message的body第一个属性）
	
	public long setReqId(long reqId) {
		this.reqId = reqId;
		return this.reqId;
	}

}
