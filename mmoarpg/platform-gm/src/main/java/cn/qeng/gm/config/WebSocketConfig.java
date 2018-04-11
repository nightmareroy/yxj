/*
 * Copyright © 2015 www.noark.xyz All Rights Reserved.
 * 
 * 感谢您选择Noark框架，希望我们的努力能为您提供一个简单、易用、稳定的服务器端框架 ！
 * 除非符合Noark许可协议，否则不得使用该文件，您可以下载许可协议文件：
 * 
 * 		http://www.noark.xyz/LICENSE
 *
 * 1.未经许可，任何公司及个人不得以任何方式或理由对本框架进行修改、使用和传播;
 * 2.禁止在本项目或任何子项目的基础上发展任何派生版本、修改版本或第三方版本;
 * 3.无论你对源代码做出任何修改和改进，版权都归Noark研发团队所有，我们保留所有权利;
 * 4.凡侵犯Noark版权等知识产权的，必依法追究其法律责任，特此郑重法律声明！
 */
package cn.qeng.gm.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import cn.qeng.gm.websocket.WebSocketHander;
import cn.qeng.gm.websocket.WebSocketHandshakeInterceptor;
import cn.qeng.gm.websocket.WebSocketPackageHander;

/**
 * WebSocket配置类.
 *
 * @author 小流氓(176543888@qq.com)
 */
@Configuration
@EnableWebSocket // 开启websocket
public class WebSocketConfig implements WebSocketConfigurer {

	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		WebSocketHandshakeInterceptor interceptor = new WebSocketHandshakeInterceptor();
		{// 聊天监控WEBsocket
			WebSocketHander hander = new WebSocketHander();
			// 支持websocket的访问链接
			registry.addHandler(hander, "/chat/monitoring").addInterceptors(interceptor).setAllowedOrigins("*");
			// 不支持websocket的访问链接
			registry.addHandler(hander, "/sockjs/chat/monitoring").addInterceptors(interceptor).withSockJS();
		}
		{
			WebSocketPackageHander hander = new WebSocketPackageHander();
			// 支持websocket的访问链接
			registry.addHandler(hander, "/package/updateing").addInterceptors(interceptor).setAllowedOrigins("*");
			// 不支持websocket的访问链接
			registry.addHandler(hander, "/sockjs/package/updateing").addInterceptors(interceptor).withSockJS();
		}
	}
}