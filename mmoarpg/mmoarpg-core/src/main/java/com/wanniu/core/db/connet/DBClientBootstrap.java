package com.wanniu.core.db.connet;

import com.wanniu.core.tcp.client.ClientBootstrap;
import com.wanniu.core.tcp.protocol.GCodecFactory;

import io.netty.channel.ChannelHandler;

/**
 * @author agui
 *
 */
public class DBClientBootstrap extends ClientBootstrap{

	/**
	 * @param factory
	 */
	public DBClientBootstrap(ChannelHandler handler) {
		super(new GCodecFactory(handler));
	}

}
