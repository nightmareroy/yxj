package com.wanniu.csharp.message;

import java.io.IOException;

import com.wanniu.core.GGlobal;
import com.wanniu.csharp.protocol.CSharpHeader;
import com.wanniu.csharp.protocol.CSharpMessage;

/**
 * 加入到CSharp战斗服
 * @author agui
 */
public class CSharpJoinMessage extends CSharpMessage {

	private byte[] serverId;

	public CSharpJoinMessage(String serverId) {
		this.serverId = serverId.getBytes(GGlobal.UTF_8);
		CSharpHeader header = getHeader();
		header.setUid("connetorId");
		header.setLength(this.serverId.length);
	}

	@Override
	protected void write() throws IOException {
		body.writeBytes(serverId);
	}

}
