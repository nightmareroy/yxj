package com.wanniu.core.db.message;

import java.io.IOException;

import com.wanniu.core.db.DBType;
import com.wanniu.core.db.QueryVo;
import com.wanniu.core.tcp.protocol.RequestMessage;

/**
 * @author agui
 *
 */
public class DBQueryMessage extends RequestMessage {

	private QueryVo query;

	public DBQueryMessage(QueryVo query) {
		this.query = query;
	}

	@Override
	protected void write() throws IOException {
		body.writeLong(reqId);
		body.writeString(query.getQueryTR());
		body.writeByte(query.type);
		body.writeString(query.getConVal());
	}

	@Override
	public short getType() {
		return DBType.QUERY;
	}

}
