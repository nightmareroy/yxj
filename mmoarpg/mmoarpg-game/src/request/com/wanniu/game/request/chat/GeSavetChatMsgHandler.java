package com.wanniu.game.request.chat;

import java.io.IOException;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;

import pomelo.chat.ChatHandler.GetSaveChatMsgRequest;
import pomelo.chat.ChatHandler.GetSaveChatMsgResponse;

@GClientEvent("chat.chatHandler.getSaveChatMsgRequest")
public class GeSavetChatMsgHandler extends PomeloRequest {

	public PomeloResponse request() throws Exception {
		GetSaveChatMsgRequest req = GetSaveChatMsgRequest.parseFrom(pak.getRemaingBytes());
		int scope = req.getC2SScope();
		String uid = req.getC2SUid();
		int index = req.getC2SIndex();
		// TODO
		
		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				GetSaveChatMsgResponse.Builder res = GetSaveChatMsgResponse.newBuilder();
				res.setS2CCode(OK);
				
				body.writeBytes(res.build().toByteArray());
			}
		};
	}

}
