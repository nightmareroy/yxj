package com.wanniu.core.proxy.message;

import java.io.IOException;

import com.alibaba.fastjson.JSON;
import com.wanniu.core.GConfig;
import com.wanniu.core.GGame;
import com.wanniu.core.proxy.ProxyType;
import com.wanniu.core.tcp.protocol.Message;

/**
 * 加入到proxy服务
 * @author agui
 */
public class ProxyJoinMessage extends Message {

	public static String SECRT = GConfig.getInstance().get("server.proxy.auth");

	@Override
	protected void write() throws IOException {
		body.writeString(SECRT);
		body.writeInt(GGame.__SERVER_ID);
		body.writeInt(GConfig.getInstance().getInt("game.areaId", 0));
		body.writeShort(GGame.getInstance().getOnlineCount());
		body.writeString(JSON.toJSONString(GGame.__CS_NODE));
	}

	@Override
	public short getType() {

		return ProxyType.JOIN;
	}

}
