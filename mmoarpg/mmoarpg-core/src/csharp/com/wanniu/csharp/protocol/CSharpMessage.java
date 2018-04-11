package com.wanniu.csharp.protocol;

import java.io.IOException;

import com.wanniu.core.game.entity.GPlayer;
import com.wanniu.core.game.protocol.PomeloPacket;
import com.wanniu.core.tcp.BufferUtil;
import com.wanniu.core.tcp.protocol.Header;
import com.wanniu.core.tcp.protocol.Message;

import io.netty.buffer.ByteBuf;

/**
 * pomelo消息结构
 * 
 * @author agui
 */
public class CSharpMessage extends Message {

	private static final int HEAD_SIZE = 6;
	
	private ByteBuf pakBody;
	
	private String csharpServerId;
	
	public String getServerId() {
		return this.csharpServerId;
	}
	
	public CSharpMessage() {
	}

	public CSharpMessage(PomeloPacket pak) {
		super(null);
		this.pakBody = pak.getBody();
		CSharpHeader header = this.getHeader();
		pakBody.skipBytes(2);
		header.setLength(pakBody.readableBytes());
		GPlayer player = pak.getPlayer();
		header.setUid(player.getId());
		csharpServerId = player.getBattleServerId();
	}
	
	protected Header newHeader() {
		return new CSharpHeader();
	}

	public CSharpHeader getHeader() {
		return (CSharpHeader) this.header;
	}

	protected ByteBuf newContent() {
		CSharpHeader header = this.getHeader();
		return BufferUtil.getAutoBuffer(HEAD_SIZE + header.getUidLength() + header.getBodyLength());
	}

	protected void encodeHeader() {
		CSharpHeader header = this.getHeader();
		header.encode(content);
	}

	protected void encodeBody() {
		if (pakBody != null) {
			content.writeBytes(pakBody);
		} else {
			super.encodeBody();
		}
	}
	
	public short getType() {
		return 0;
	}

	@Override
	protected void write() throws IOException {

	}

}
