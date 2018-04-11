package com.wanniu.core.http;

import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.wanniu.core.logfs.Out;

import java.util.Set;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.multipart.Attribute;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import io.netty.handler.codec.rtsp.RtspHeaders.Values;

public class HttpDecoderAndEncoder {

	/**
	 * 请求解析
	 * 
	 * @param ctx
	 * @param req
	 */
	public static void Request(ChannelHandlerContext ctx, FullHttpRequest req) {

	}

	/**
	 * 解析请求参数
	 * 
	 * @return 包含所有请求参数的键值对, 如果没有参数, 则返回空Map
	 *
	 * @throws BaseCheckedException
	 * @throws IOException
	 */
	public static Map<String, String> parse(FullHttpRequest fullReq) throws Exception {
		HttpMethod method = fullReq.getMethod();

		Map<String, String> parmMap = new HashMap<String, String>();

		if (HttpMethod.GET == method) {
			// 是GET请求
			QueryStringDecoder decoder = new QueryStringDecoder(fullReq.getUri());
			Set<Entry<String, List<String>>> set=decoder.parameters().entrySet();
			for(Entry<String, List<String>> entry: set){
				parmMap.put(entry.getKey(), entry.getValue().get(0));
			}
		} else if (HttpMethod.POST == method) {
			// 是POST请求
			HttpPostRequestDecoder decoder = new HttpPostRequestDecoder(fullReq);
			decoder.offer(fullReq);

			List<InterfaceHttpData> parmList = decoder.getBodyHttpDatas();

			for (InterfaceHttpData parm : parmList) {

				Attribute data = (Attribute) parm;
				parmMap.put(data.getName(), data.getValue());
			}

		} else {
			// 不支持其它方法
			throw new Exception(""); // 这是个自定义的异常, 可删掉这一行
		}

		return parmMap;
	}

	/**
	 * 响应Http请求
	 * 
	 * @param ctx
	 * @param req
	 * @param msg
	 */
	public static void Response(ChannelHandlerContext ctx, FullHttpRequest req, String msg) {
		Response(ctx, req, msg, OK);
	}

	/**
	 * 响应Http请求
	 * 
	 * @param ctx
	 * @param req
	 * @param res
	 */
	public static void Response(ChannelHandlerContext ctx, FullHttpRequest req, String msg, HttpResponseStatus status) {
		try {

			FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK,
					Unpooled.wrappedBuffer(msg.getBytes("UTF-8")));
			response.headers().set("Content-Type", "text/plain");
			response.headers().set("Content-Length", response.content().readableBytes());
			response.headers().set("Access-Control-Allow-Origin", "*");
			if (HttpHeaders.isKeepAlive(req)) {
				response.headers().set("Connection", Values.KEEP_ALIVE);
			}
			ctx.write(response);
			ctx.flush();

		} catch (Exception e) {
			Out.error("HttpDecoderAndEncoder Response", e);
		}

	}

}
