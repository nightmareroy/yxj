package com.wanniu.handler;

import com.wanniu.pay.request.GClientEvent;
import com.wanniu.tcp.protocol.NetHandler;
import com.wanniu.tcp.protocol.Packet;

import cn.qeng.paycenter.api.rpc.RpcManager;
import cn.qeng.paycenter.api.rpc.RpcResponse;

/**
 * 充值通知成功回调
 * 
 * @author lxm
 */
@GClientEvent(0xf5)
public class PayNotifySuccessHandler extends NetHandler {

	@Override
	public void execute(Packet packet) {
		final long key = packet.getLong();
		String result = packet.getString();
		RpcResponse response = RpcManager.removeRpc(key);
		if (response != null) {
			response.setResult(result);
			response.getCounter().countDown();
		}
	}
}
