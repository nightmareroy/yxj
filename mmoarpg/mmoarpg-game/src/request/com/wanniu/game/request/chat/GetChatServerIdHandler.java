package com.wanniu.game.request.chat;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.common.msg.ErrorResponse;

@GClientEvent("chat.chatHandler.getChatServerIdRequest")
public class GetChatServerIdHandler extends PomeloRequest {

	public PomeloResponse request() throws Exception {
		return new ErrorResponse("404");
//		GetChatServerIdRequest req = GetChatServerIdRequest.parseFrom(pak.getRemaingBytes());
//		
//		// TODO
//		
//		return new PomeloResponse() {
//			@Override
//			protected void write() throws IOException {
//				GetChatServerIdResponse.Builder res = GetChatServerIdResponse.newBuilder();
//				res.setS2CCode(OK);
//				
//				res.setS2CServerId("1");
//				res.setS2CClientHttp("http://www.baidu.com");
//				
//				
//				body.writeBytes(res.build().toByteArray());
//			}
//		};
	}

}
