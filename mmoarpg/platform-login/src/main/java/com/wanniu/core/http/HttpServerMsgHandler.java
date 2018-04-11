package com.wanniu.core.http;

import java.util.Map;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;

public interface HttpServerMsgHandler {
	
	public String getPath();
	
	public void doPost(ChannelHandlerContext ctx, FullHttpRequest request, Map<String, String> parmMap);
	
	public void doGet(ChannelHandlerContext ctx, FullHttpRequest request, Map<String, String> parmMap);
}
