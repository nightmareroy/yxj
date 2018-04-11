package com.wanniu.csharp.message;

import com.wanniu.csharp.protocol.CSharpHeader;
import com.wanniu.csharp.protocol.CSharpMessage;

/**
 * 
 * @author agui
 */
public class CSharpPingMessage extends CSharpMessage {

	public CSharpPingMessage() {
		CSharpHeader header = getHeader();
		header.setUid("ping");
	}

}
