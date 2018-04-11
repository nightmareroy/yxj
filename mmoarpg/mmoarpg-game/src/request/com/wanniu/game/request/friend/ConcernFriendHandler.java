package com.wanniu.game.request.friend;

import java.io.IOException;
import java.util.TreeMap;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.friend.FriendManager;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.FriendHandler.ConcernFriendRequest;
import pomelo.area.FriendHandler.ConcernFriendResponse;

@GClientEvent("area.friendHandler.concernFriendRequest")
public class ConcernFriendHandler extends PomeloRequest{

	@Override
	public PomeloResponse request() throws Exception {
		WNPlayer player = (WNPlayer)pak.getPlayer();
		ConcernFriendRequest msg = ConcernFriendRequest.parseFrom(pak.getRemaingBytes());
		String friendId = msg.getC2SFriendId();
		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				ConcernFriendResponse.Builder res = ConcernFriendResponse.newBuilder();
				FriendManager friendManager = player.getFriendManager();
				TreeMap<String,Object> rtData = friendManager.concernFriend(friendId,player);
				boolean result = (boolean)rtData.get("result");
				String info = (String)rtData.get("info");
			    if (result) {
			    	res.setS2CCode(OK);
			    	res.setS2CMsg(info);
			    }
			    else {
			    	res.setS2CCode(FAIL);
			    	res.setS2CMsg(info);
			    }
			    body.writeBytes(res.build().toByteArray());
			}
		};
	}

}
