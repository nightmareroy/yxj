package com.wanniu.handler;

import com.wanniu.CDKeyLock;
import com.wanniu.ServerContext;
import com.wanniu.gm.request.GClientEvent;
import com.wanniu.tcp.protocol.NetHandler;
import com.wanniu.tcp.protocol.Packet;
import com.wanniu.tcp.protocol.Prefix;

@GClientEvent(0xfc)
public class CDKeyResponseHandler extends NetHandler {

	@Override
	public void execute(Packet packet) {
		long key = packet.getLong();
		CDKeyLock lock = ServerContext.CDKEYLOCKS.get(key);
		if (lock != null) {
			lock.getLock().tryLock();
			try {
				lock.setOk(packet.getBoolean());
				lock.setData(packet.getBytes(Prefix.INT));
				lock.getCondition().signal();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				lock.getLock().unlock();
			}
		}
	}

}
