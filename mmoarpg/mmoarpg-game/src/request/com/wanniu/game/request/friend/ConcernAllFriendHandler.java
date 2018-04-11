package com.wanniu.game.request.friend;

import java.io.IOException;
import java.util.List;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.friend.FriendManager;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.FriendHandler.ConcernAllFriendRequest;
import pomelo.area.FriendHandler.ConcernAllFriendResponse;

@GClientEvent("area.friendHandler.concernAllFriendRequest")
public class ConcernAllFriendHandler extends PomeloRequest{

	@Override
	public PomeloResponse request() throws Exception {
		WNPlayer player = (WNPlayer)pak.getPlayer();
		ConcernAllFriendRequest msg = ConcernAllFriendRequest.parseFrom(pak.getRemaingBytes());
		List<String> friendIds = msg.getC2SFriendIdsList();
		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				ConcernAllFriendResponse.Builder res = ConcernAllFriendResponse.newBuilder();
				FriendManager friendManager = player.getFriendManager();
				for(String s : friendIds ){
			        friendManager.concernFriend(s,player);
			    }
				res.setS2CCode(OK);
				res.setS2CMsg("");
				body.writeBytes(res.build().toByteArray());
			}
		};
	}

}
