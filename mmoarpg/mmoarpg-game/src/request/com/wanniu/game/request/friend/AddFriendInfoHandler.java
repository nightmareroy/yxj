package com.wanniu.game.request.friend;

import java.io.IOException;
import java.util.ArrayList;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.friend.FriendManager;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.FriendHandler.AddFriendInfoResponse;
import pomelo.area.FriendHandler.PlayerInfo;

@GClientEvent("area.friendHandler.addFriendInfoRequest")
public class AddFriendInfoHandler extends PomeloRequest {

	@Override
	public PomeloResponse request() throws Exception {
		WNPlayer player = (WNPlayer) pak.getPlayer();
		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				AddFriendInfoResponse.Builder res = AddFriendInfoResponse.newBuilder();
				FriendManager friendManager = player.getFriendManager();
				ArrayList<PlayerInfo> rtData = friendManager.addFriendInfo(player.getLevel());
				res.setS2CCode(OK);
				res.addAllData(rtData);
				body.writeBytes(res.build().toByteArray());
			}
		};
	}

}
