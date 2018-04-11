package com.wanniu.handler;

import com.wanniu.GGlobal;
import com.wanniu.gm.request.GClientEvent;
import com.wanniu.tcp.protocol.NetHandler;
import com.wanniu.tcp.protocol.Packet;
import com.wanniu.tcp.protocol.Prefix;

import cn.qeng.gm.api.rpc.RpcManager;
import cn.qeng.gm.api.rpc.RpcResponse;

@GClientEvent(0xfa)
public class GMResponseHandler extends NetHandler {

	@Override
	public void execute(Packet packet) {
		long key = packet.getLong();
		RpcResponse response = RpcManager.removeRpc(key);
		if (response != null) {
			int sid = packet.getInt();
			byte type = packet.getByte();
			if (type == 0) {
				byte code = packet.getByte();
				response.putStatus(sid, code);
			} else {
				byte[] body = packet.getBytes(Prefix.INT);
				response.setResult(new String(body, GGlobal.UTF_8));
			}
			response.getCounter().countDown();
		}
	}
}