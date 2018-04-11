package com.wanniu.login.announce;

import java.util.Map;

import com.wanniu.core.http.HttpDecoderAndEncoder;
import com.wanniu.core.http.HttpServerMsgHandler;
import com.wanniu.core.logfs.Out;
import com.wanniu.login.AnnounceServer;

import cn.qeng.common.gm.po.AnnouncementPO;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;

/**
 * 获取客户端公告
 * 
 * @author Yangzz
 *
 */
public class AnnounceMsgHandler implements HttpServerMsgHandler {

	private String path;

	public AnnounceMsgHandler(String path) {
		this.path = path;
	}

	@Override
	public String getPath() {
		return path;
	}

	@Override
	public void doGet(ChannelHandlerContext ctx, FullHttpRequest request, Map<String, String> parmMap) {
		doPost(ctx, request, parmMap);
	}

	@Override
	public void doPost(ChannelHandlerContext ctx, FullHttpRequest request, Map<String, String> parmMap) {
		AnnouncementPO announce = AnnounceServer.announce;
		if (announce == null) {
			Out.error("未获取到公告");
			HttpDecoderAndEncoder.Response(ctx, request, "");
		} else {
			Out.debug("公告：" + announce.getContent());
			HttpDecoderAndEncoder.Response(ctx, request, announce.getContent());
		}
	}

}
