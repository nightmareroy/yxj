package com.wanniu.core.tcp.client;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.wanniu.core.GConfig;
import com.wanniu.core.logfs.Out;
import com.wanniu.core.tcp.GBootstrap;
import com.wanniu.core.tcp.protocol.Packet;
import com.wanniu.core.tcp.protocol.RequestMessage;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * 客户端引导
 * 
 * @author agui
 */
public class ClientBootstrap extends GBootstrap {

	static class RequestLock {
		private Lock lock;
		private Condition condition;
		private Packet pak;

		RequestLock(Lock lock, Condition condition) {
			this.lock = lock;
			this.condition = condition;
		}

	}

	private static final int REQUEST_AWAIN = GConfig.getInstance().getInt("game.request.await", 15000);

	/** 客户端响应信号通知键生成器 */
	private static AtomicLong UUID = new AtomicLong(System.currentTimeMillis());
	private static Map<Long, RequestLock> LOCKS = new ConcurrentHashMap<>();

	private Bootstrap boots;
	protected Channel session;
	
	private static final EventLoopGroup m_loop = new NioEventLoopGroup();

	public ClientBootstrap(ChannelInitializer<SocketChannel> factory) {
		this(factory, m_loop);
	}
	
	public ClientBootstrap(ChannelInitializer<SocketChannel> factory, EventLoopGroup loop) {
		boots = new Bootstrap();
		boots.channel(NioSocketChannel.class);
		boots.option(ChannelOption.TCP_NODELAY, true);
		boots.option(ChannelOption.SO_KEEPALIVE, true);
		boots.handler(factory);
		boots.group(loop);
	}

	public Channel connect(String host, int port) {
		try {
			ChannelFuture future = boots.connect(new InetSocketAddress(host, port));
			future.awaitUninterruptibly(10, TimeUnit.SECONDS);

			if (!future.isSuccess()) {
				if (future.cause() != null) {
					Out.error(future.cause().getMessage());
				}
				return null;
			}
			session = future.channel();
			return future.channel();
		} catch (Exception e) {
			Out.error(e);
			return null;
		}
	}

	public void quit(Channel session) {
		if (this.session == session) {
			this.session = null;
		}
	}

	public static class RequestException extends RuntimeException {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public RequestException(String msg) {
			super(msg);
		}

		public RequestException(Throwable ex) {
			super(ex);
		}

	}

	public Packet request(RequestMessage req) {
		if (session == null) {
			throw new RequestException("session is null!!!");
		}
		long reqId = UUID.incrementAndGet();
		Lock lock = new ReentrantLock();
		lock.lock();
		try {
			reqId = req.setReqId(reqId);
			Condition condition = lock.newCondition();
			RequestLock look = new RequestLock(lock, condition);
			LOCKS.put(reqId, look);
			session.writeAndFlush(req.getContent());
			condition.await(REQUEST_AWAIN, TimeUnit.MILLISECONDS);
			LOCKS.remove(reqId);
			if (look.pak == null) {
				throw new RequestException("session is close!!!");
			}
			return look.pak;
		} catch (Exception e) {
			Out.error(e);
		} finally {
			lock.unlock();
		}
		return null;
	}

	public void response(long reqId, Packet pak) {
		RequestLock look = LOCKS.get(reqId);
		if (look != null) {
			look.lock.lock();
			try {
				look.pak = pak;
				look.condition.signal();
			} catch (Exception e) {
				Out.error(e);
			} finally {
				look.lock.unlock();
			}
		} else {
			Out.warn(reqId, " request timeout!!!");
		}
	}

}
