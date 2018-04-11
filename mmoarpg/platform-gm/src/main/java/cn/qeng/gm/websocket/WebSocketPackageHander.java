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
package cn.qeng.gm.websocket;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import com.alibaba.fastjson.JSON;

/**
 * WebSocket处理类.
 *
 * @author 小流氓(176543888@qq.com)
 */
public class WebSocketPackageHander implements WebSocketHandler {
	private static final Logger logger = LogManager.getLogger(WebSocketPackageHander.class);
	public static final ConcurrentHashMap<String, WebSocketSession> socketSessionMap = new ConcurrentHashMap<>();

	// 初次链接成功执行
	@Override
	public void afterConnectionEstablished(WebSocketSession webSocketSession) throws Exception {
		socketSessionMap.put(webSocketSession.getId(), webSocketSession);
		logger.debug("链接成功......");
		Map<String, String> config = new HashMap<>();
		config.put("level", "INFO");
		config.put("log", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss:SSS")) + "- 请开始你的表演...");
		webSocketSession.sendMessage(new TextMessage(JSON.toJSONBytes(config)));
	}

	// 接受消息处理消息
	@Override
	public void handleMessage(WebSocketSession socketSession, WebSocketMessage<?> webSocketMessage) throws Exception {
		String msg = (String) webSocketMessage.getPayload();
		logger.info("receive msg:" + msg);
	}

	@Override
	public void handleTransportError(WebSocketSession webSocketSession, Throwable throwable) throws Exception {
		socketSessionMap.remove(webSocketSession.getId());
		if (webSocketSession.isOpen()) {
			webSocketSession.close();
		}
		logger.debug("链接出错，关闭链接......");
	}

	@Override
	public void afterConnectionClosed(WebSocketSession webSocketSession, CloseStatus closeStatus) throws Exception {
		socketSessionMap.remove(webSocketSession.getId());
		logger.debug("链接关闭......" + closeStatus.toString());
	}

	@Override
	public boolean supportsPartialMessages() {
		return false;
	}
}