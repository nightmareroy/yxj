package com.wanniu.core.db.message;

import java.io.IOException;

import com.wanniu.core.db.DBType;
import com.wanniu.core.tcp.protocol.RequestMessage;

/**
 * @author agui
 *
 */
public class DBPingMessage extends RequestMessage {
	
	@Override
	protected void write() throws IOException {
		
	}

	@Override
	public short getType() {
		return DBType.PING;
	}
	
}
