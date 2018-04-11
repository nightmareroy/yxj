package com.wanniu.game.request.entry;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

import com.wanniu.core.GGlobal;
import com.wanniu.core.game.JobFactory;
import com.wanniu.core.game.protocol.PomeloPush;
import com.wanniu.core.logfs.Out;
import com.wanniu.core.tcp.protocol.Packet;
import com.wanniu.game.GWorld;

import io.netty.channel.Channel;
import io.netty.util.AttributeKey;
import pomelo.connector.EntryHandler.LoginQueuePush;

/**
 * 
 * @author agui
 *
 */
public class LoginQueue {

	private static final AttributeKey<Long> __KEY_QUEUE_TIME = AttributeKey.valueOf("__KEY.QUEUE.TIME");
	
	public final static List<Channel> EntryQueue = new CopyOnWriteArrayList<>();
	public final static Map<String, Channel> GotoBindQueue = new ConcurrentHashMap<>();

	static class QueueMessage extends PomeloPush {
		private int num;

		QueueMessage(int num) {
			this.num = num;
		}

		@Override
		protected void write() throws IOException {
			LoginQueuePush.Builder push = LoginQueuePush.newBuilder();
			push.setNum(num);
			push.setTime(OL_AVG_TIME * num);
			body.writeBytes(push.build().toByteArray());
		}

		@Override
		public String getRoute() {
			return "connector.entryPush.loginQueuePush";
		}
	}

	private static int OL_AVG_TIME = 60;
	private static boolean initQueueSch = false;
	private static final AtomicLong TOTAL_COUNT = new AtomicLong();
	private static final AtomicLong TOTAL_TIME = new AtomicLong();

	private static int getOnlineCount( ) {
		return GWorld.getInstance().getOnlineCount() + GotoBindQueue.size();
	}

	public static void syncQueueInfo() {
		if (!initQueueSch) {
			initQueueSch = true;
			JobFactory.addScheduleJob(() -> {
				if (!EntryQueue.isEmpty()) {
					Out.info("sync queque info...", EntryQueue.size());
					int canLoginCount = GWorld.__PLAYER_LIMIT - getOnlineCount();
					if (canLoginCount > 0) {
						Channel channel = EntryQueue.get(0);
						GotoBindQueue.put(channel.attr(GGlobal.__KEY_USER_ID).get(), channel);
						Out.debug(channel, ".................................goto bind....................................", canLoginCount);
						removeEntryQueue(channel);
						channel.writeAndFlush(new QueueMessage(0));
						for (int i = 0; i < EntryQueue.size(); i++) {
							EntryQueue.get(i).writeAndFlush(new QueueMessage(i + 1));
						}
					}
				}
			}, 1000, 1000);
		}
	}

	public static boolean checkQueue(Packet pak) {
//		GWorld.__PLAYER_LIMIT = 1;
		Channel channel = pak.getSession();
		if (EntryQueue.size() > 0 || getOnlineCount() >= GWorld.__PLAYER_LIMIT) {
			String uid = pak.getAttr(GGlobal.__KEY_USER_ID);
			if (!GotoBindQueue.containsKey(uid)) {
				int index = EntryQueue.indexOf(channel);
				if (index < 0) {
					index = EntryQueue.size();
					EntryQueue.add(channel);
					channel.attr(__KEY_QUEUE_TIME).set(System.currentTimeMillis());
					syncQueueInfo();
				}
				channel.writeAndFlush(new QueueMessage(index + 1).getContent());
				return false;
			}
		}
		addBindQueue(channel);
		return true;
	}

	public static void remove(Channel channel) {
		String uid = channel.attr(GGlobal.__KEY_USER_ID).get();
		if (uid != null && !removeEntryQueue(channel)) {
			if (GotoBindQueue.containsKey(uid)) {
				JobFactory.addDelayJob(() -> {
					if (channel == GotoBindQueue.get(uid)) {
						Out.debug("remove bind queue...");
						GotoBindQueue.remove(uid);
					} else {
						Out.debug("current binding queue...");
					}
				}, 10000);
			}
		}
	}

	public static boolean removeEntryQueue(Channel channel) {
		if (EntryQueue.remove(channel)) {
			Long queueTime = channel.attr(__KEY_QUEUE_TIME).get();
			long count = TOTAL_COUNT.incrementAndGet();
			long total = TOTAL_TIME.get();
			TOTAL_TIME.compareAndSet(total, total + System.currentTimeMillis() - queueTime);
			OL_AVG_TIME = Math.max(10, (int) ((TOTAL_TIME.get() / count) / 1000));
			Out.debug("OL_AVG_TIME : ", OL_AVG_TIME);
			for (int i = 0; i < EntryQueue.size(); i++) {
				EntryQueue.get(i).writeAndFlush(new QueueMessage(i + 1));
			}
			return true;
		}
		return false;
	}

	public static void addBindQueue(Channel channel) {
		String uid = channel.attr(GGlobal.__KEY_USER_ID).get();
		if (uid != null) {
			if (GotoBindQueue.put(uid, channel) == channel) {
				Out.warn("bug???...应该是客户端排队连接已优化了.......................................");
			}
		}
	}

	public static boolean removeBindQueue(Channel channel) {
		String uid = channel.attr(GGlobal.__KEY_USER_ID).get();
		if (uid != null && GotoBindQueue.containsKey(uid)) {
			GotoBindQueue.remove(uid);
			return true;
		}
		return false;
	}

}
