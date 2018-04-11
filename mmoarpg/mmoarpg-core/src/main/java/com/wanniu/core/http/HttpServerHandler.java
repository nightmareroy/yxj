package com.wanniu.core.http;


import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;

@Sharable
public class HttpServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

	private Map<String, HttpServerMsgHandler> httphandlersChain=new HashMap<String, HttpServerMsgHandler>();
	
	
	public HttpServerHandler(){
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
		URI uri = new URI(request.getUri());
//        System.err.println("request uri==" + uri.getPath());
		String path=uri.getPath();
		HttpServerMsgHandler handler = httphandlersChain.get(path);
		Map<String, String> parmMap = HttpDecoderAndEncoder.parse(request);
		if(handler!=null){
			if (request.getMethod().equals(HttpMethod.GET)) {
				handler.doGet(ctx, request, parmMap);
			} else if (request.getMethod().equals(HttpMethod.POST)||request.getMethod().equals(HttpMethod.OPTIONS)) {
				handler.doPost(ctx, request, parmMap);
			}
		}
		else
			ctx.close();
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		ctx.flush();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		ctx.close();
	}

	public void addHandler(HttpServerMsgHandler handler){
		httphandlersChain.put(handler.getPath(), handler);
	}
	
}
