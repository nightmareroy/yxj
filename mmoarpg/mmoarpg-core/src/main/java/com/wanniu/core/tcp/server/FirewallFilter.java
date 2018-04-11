package com.wanniu.core.tcp.server;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.wanniu.core.GConfig;
import com.wanniu.core.logfs.Out;

import io.netty.channel.Channel;
import io.netty.util.AttributeKey;

/**
 * 功能描述：网络请求防火墙功能
 * @author agui
 */
public final class FirewallFilter {

	private final static AttributeKey<Boolean> __WHITE_KEY__ = AttributeKey.valueOf("__WHITE_KEY__");
	private static final Map<Channel, Integer> __READS__ = new ConcurrentHashMap<Channel, Integer>();
	private static final Map<Channel, Long> __LAST_READ_TIMES = new ConcurrentHashMap<Channel, Long>();
	
	public static final long __ALLOWED_COUNT__ = GConfig.getInstance().getInt("client.message.total", 1000000);
	public static final long __SKIP_TIME__ = GConfig.getInstance().getInt("client.message.skiptime", 100);
	public static final long __SKIP_COUNT__ = GConfig.getInstance().getInt("client.message.skipcount", 50);
	
	public FirewallFilter() { }

	public void channelCreated(Channel channel) {
		if (checkConnectionOk(channel)) {
			__LAST_READ_TIMES.put(channel, System.currentTimeMillis());
			__READS__.put(channel, 0);
		} else {
			channel.close();
			Out.error(channel.remoteAddress() , "在黑名单中，无法创建");
		}
	}

	public boolean messageReceived(Channel channel) {
		if (checkReadOk(channel)) {
			return true;
		}
		Out.error(channel.remoteAddress() , "读取异常快");
		return false;
	}

	public void channelClosed(Channel channel) {
		__READS__.remove(channel);
		__LAST_READ_TIMES.remove(channel);
	}
	
	private boolean checkReadOk(Channel channel) {
		int readCount = __READS__.get(channel);
		long lastReadTime = __LAST_READ_TIMES.get(channel);
		long nowReadTime = System.currentTimeMillis();
		if (nowReadTime - lastReadTime < __SKIP_TIME__){
			if(channel.attr(__WHITE_KEY__).get() != null) return true;
			if(++readCount > __SKIP_COUNT__) {
				IPBlacks.getInstance().black(channel, "过快,");
				IPBlacks.getInstance().addIp(channel);
				return false;
			}
//			else if (attrs.getReadMessages() > __ALLOWED_COUNT__) {
//				XIPBlacks.getInstance().black(channel, "过量,");
//				return false;
//			}
			else {
				__READS__.put(channel, readCount);
				return true;
			}
		}
		__LAST_READ_TIMES.put(channel, nowReadTime);
		__READS__.put(channel, 0);
		return true;
	}

	private boolean checkConnectionOk(Channel channel) {
		SocketAddress remoteAddress = channel.remoteAddress();
		if (remoteAddress instanceof InetSocketAddress) {
			InetSocketAddress addr = (InetSocketAddress) remoteAddress;
			String host = addr.getAddress().getHostAddress();
			
			if(IPWhites.getInstance().check(host)){
				channel.attr(__WHITE_KEY__).set(Boolean.TRUE);
				return true;
			}  
			if(IPBlacks.getInstance().contains(host)){
				return false;
			} 
			return true;
		}
		return false;
	}

}
