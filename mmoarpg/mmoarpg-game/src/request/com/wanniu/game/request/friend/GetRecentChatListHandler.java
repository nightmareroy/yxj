package com.wanniu.game.request.friend;

import java.io.IOException;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.recent.RecentChatMgr;

import pomelo.area.FriendHandler.GetRecentChatListResponse;

/**
 * 获取最近联系人列表
 * 
 * @author jjr
 *
 */
@GClientEvent("area.friendHandler.getRecentChatListRequest")
public class GetRecentChatListHandler extends PomeloRequest {

	@Override
	public PomeloResponse request() throws Exception {
		WNPlayer player = (WNPlayer) pak.getPlayer();
		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				GetRecentChatListResponse.Builder res = GetRecentChatListResponse.newBuilder();
				RecentChatMgr recentMgr = player.getRecentChatMgr();
				res.setS2CCode(OK);
				res.addAllS2CLs(recentMgr.getRecentLs());
				body.writeBytes(res.build().toByteArray());
			}
		};
	}

}
