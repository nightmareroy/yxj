package com.wanniu.core.gm.message;

import java.io.IOException;

import com.wanniu.core.GConfig;
import com.wanniu.core.GGame;
import com.wanniu.core.gm.GMType;
import com.wanniu.core.tcp.protocol.Message;

/**
 * 加入到GM服务
 * 
 * @author agui
 */
public class GMJoinMessage extends Message {

	@Override
	protected void write() throws IOException {
		body.writeString(GConfig.getInstance().get("server.gm.auth"));
		body.writeInt(GGame.__SERVER_ID);
		body.writeShort(GGame.getInstance().getOnlineCount());
		body.writeString(GConfig.getInstance().getGamePubHost());
		body.writeInt(GConfig.getInstance().getGamePort());
		body.writeString(GConfig.getInstance().get("game.name"));
		body.writeInt(GConfig.getInstance().getInt("game.areaId", 0));
		body.writeInt(GGame.__APP_ID);
	}

	@Override
	public short getType() {
		return GMType.JOIN;
	}
}