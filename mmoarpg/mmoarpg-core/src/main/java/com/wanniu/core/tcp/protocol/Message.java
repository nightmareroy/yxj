package com.wanniu.core.tcp.protocol;

import java.io.IOException;

import com.wanniu.core.logfs.Out;
import com.wanniu.core.tcp.BufferUtil;

import io.netty.buffer.ByteBuf;

/**
 * 客户端消息基类(单播消息)
 * @author agui
 */
public abstract class Message {
	
	/** 包头 */
	protected Header header;
	
	/** 包体 */
	protected Body body;

	/**
	 * 消息内容
	 */
	protected ByteBuf content;

	public Message() {
		this(new Body());
	}

	/**
	 * @param initCapacity body的初始字节数
	 */
	public Message(Body body) {
		this.header = newHeader();
		this.body = body;
	}

	/**
	 * 包头构造
	 */
	protected Header newHeader() {
		return new Header();
	}

	/**
	 * 数据包
	 */
	protected ByteBuf newContent() {
		return BufferUtil.getAutoBuffer(body.getBuffer().readableBytes() + Header.SIZE);
	}
	
	/**
	 * 包头编码
	 */
	protected void encodeHeader() {
		header.setType(getType());
		header.setLength(body.getBuffer().readableBytes());
		header.encode(content);
	}

	/**
	 * 包体体编码
	 */
	protected void encodeBody() {
		content.writeBytes(body.getBuffer());
	}
	
	/**
	 * 获取消息内容
	 * 
	 * @return
	 */
	public ByteBuf getContent() {
		try {
			if (null == content) {

				write();

				content = newContent();
				encodeHeader();
				encodeBody();

				body = null;
				header = null;
			}

			return content;
		} catch (Exception e) {
			Out.error("Message getContent", e);
			return null;
		}
	}

	/**
	 * 将消息内容写入(包体)输出管道
	 * 
	 * @throws IOException
	 */
	protected abstract void write() throws IOException;

	/**
	 * 下行消息编号
	 * 
	 * @return
	 * @throws Exception
	 */
	public abstract short getType();

}
