package com.wanniu.game.request.friend;

import java.io.IOException;
import java.util.List;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.recent.RecentChatMgr;

import pomelo.area.FriendHandler.ChatMsg;
import pomelo.area.FriendHandler.GetChatMsgRequest;
import pomelo.area.FriendHandler.GetChatMsgResponse;

@GClientEvent("area.friendHandler.getChatMsgRequest")
public class GetChatMsgHandler extends PomeloRequest {

	@Override
	public PomeloResponse request() throws Exception {
		WNPlayer player = (WNPlayer) pak.getPlayer();
		GetChatMsgRequest req = GetChatMsgRequest.parseFrom(pak.getRemaingBytes());
		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				GetChatMsgResponse.Builder res = GetChatMsgResponse.newBuilder();
				RecentChatMgr recentChatMgr = player.getRecentChatMgr();
				List<ChatMsg> ls = recentChatMgr.getRecentMsg(req.getC2SFriendId());
				res.setS2CCode(OK);
				res.addAllS2CMsgLs(ls);
				body.writeBytes(res.build().toByteArray());
			}
		};
	}

}
