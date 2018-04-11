package com.wanniu.gm;

import java.io.IOException;

import com.wanniu.core.tcp.protocol.Message;

public abstract class GMResponse extends Message {

	protected long key;
	private short type;

	public GMResponse(int type) {
		this.type = (short) type;
	}
	
	public GMResponse(long key, int type) {
		this.key = key;
		this.type = (short) type;
	}
	
	public void setKey(long key) {
		this.key = key;
	}
	
	@Override
	protected void write() throws IOException {
		body.writeLong(key);
		response();
	}

	protected abstract void response() throws IOException;
	
	@Override
	public short getType() {
		return type;
	}
	
}
