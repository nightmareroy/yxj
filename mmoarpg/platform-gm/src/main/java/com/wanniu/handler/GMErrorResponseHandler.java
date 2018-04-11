package com.wanniu.handler;

import com.wanniu.CDKeyLock;
import com.wanniu.ServerContext;
import com.wanniu.gm.request.GClientEvent;
import com.wanniu.tcp.protocol.NetHandler;
import com.wanniu.tcp.protocol.Packet;

import cn.qeng.gm.api.rpc.RpcManager;
import cn.qeng.gm.api.rpc.RpcResponse;

@GClientEvent(0xfb)
public class GMErrorResponseHandler extends NetHandler {

	@Override
	public void execute(Packet packet) {
		long key = packet.getLong();
		RpcResponse response = RpcManager.removeRpc(key);
		if (response != null) {
			int sid = packet.getInt();
			response.putStatus(sid, Byte.MIN_VALUE);
			response.setResult(null);
			response.getCounter().countDown();
			return;
		}
		CDKeyLock cdklock = ServerContext.CDKEYLOCKS.get(key);
		if (cdklock != null) {
			cdklock.getLock().tryLock();
			try {
				// cdklock.setData(packet.getBytes(Prefix.INT));
				cdklock.getCondition().signal();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				cdklock.getLock().unlock();
			}
			return;
		}
	}

}
