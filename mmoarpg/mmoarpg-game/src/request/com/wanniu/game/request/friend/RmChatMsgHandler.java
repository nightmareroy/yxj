package com.wanniu.game.request.friend;

import java.io.IOException;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.core.logfs.Out;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.recent.RecentChatMgr;

import pomelo.area.FriendHandler.RmChatMsgRequest;
import pomelo.area.FriendHandler.RmChatMsgResponse;

@GClientEvent("area.friendHandler.rmChatMsgRequest")
public class RmChatMsgHandler extends PomeloRequest {
	public PomeloResponse request() throws Exception {
		WNPlayer player = (WNPlayer) pak.getPlayer();
		RmChatMsgRequest req = RmChatMsgRequest.parseFrom(pak.getRemaingBytes());
		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				RmChatMsgResponse.Builder res = RmChatMsgResponse.newBuilder();
				try {
					RecentChatMgr recentMgr = player.getRecentChatMgr();
					recentMgr.removeRecentMsg(req.getC2SFriendId());
					res.setS2CCode(OK);
					body.writeBytes(res.build().toByteArray());
				} catch (Exception e) {
					Out.error(e);
					res.setS2CCode(FAIL);
					body.writeBytes(res.build().toByteArray());
				}
			}
		};
	}
}