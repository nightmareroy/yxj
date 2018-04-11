package com.wanniu.game.request.entry;

import java.io.IOException;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;

import pomelo.gate.GateHandler.QueryEntryResponse;

@GClientEvent("gate.gateHandler.queryEntryRequest")
public class QueryEntryHandler extends PomeloRequest {

	public PomeloResponse request() throws Exception {
//		pak.getPlayer();
//		QueryEntryRequest req = QueryEntryRequest.parseFrom(pak.getRemaingBytes());
//		String uid = req.getC2SUid();
//		String sign = req.getC2SSign();
//		String time = req.getC2STime();
		
//		send(new ChatPush());
		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				QueryEntryResponse.Builder res = QueryEntryResponse.newBuilder();
				res.setS2CCode(OK);
				
				// TODO 仅仅返回ip+端口的话，这个请求客户端去掉，直接读取服务器列表
//				res.setS2CPubHost(GConfig.getInstance().getGamePubHost());
//				res.setS2CPort(GConfig.getInstance().getGamePort()); // 3015 3900 3010
				res.setS2CToken("succeed!");
				
				body.writeBytes(res.build().toByteArray());
			}
		};
	}

}
