package com.wanniu.core.db.message;

import java.io.IOException;

import com.wanniu.core.GConfig;
import com.wanniu.core.GGame;
import com.wanniu.core.db.DBType;
import com.wanniu.core.tcp.protocol.RequestMessage;

/**
 * @author agui
 *
 */
public class DBJoinMessage extends RequestMessage {

	@Override
	protected void write() throws IOException {
		body.writeLong(reqId);
		body.writeInt(GGame.__SERVER_ID);
		
		String redisHost = GConfig.getInstance().get("server.redis.host", "127.0.0.1");
		int redisPort = GConfig.getInstance().getInt("server.redis.port", 6379);
		String pwd = GConfig.getInstance().get("server.redis.password");
		int db = GConfig.getInstance().getInt("server.redis.db", 0);

		body.writeString(redisHost);
		body.writeShort(redisPort);
		body.writeString(pwd);
		body.writeByte(db);

		body.writeString(GConfig.getInstance().get("game.dsname", "game-ds"));
		body.writeString(GConfig.getInstance().get("game.dbname", "game_db"));
		
	}

	@Override
	public short getType() {
		return DBType.JOIN;
	}

}
