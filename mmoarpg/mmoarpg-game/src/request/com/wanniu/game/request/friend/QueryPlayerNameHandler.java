package com.wanniu.game.request.friend;

import java.io.IOException;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.friend.FriendManager;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.FriendHandler.QueryPlayerNameRequest;
import pomelo.area.FriendHandler.QueryPlayerNameResponse;

@GClientEvent("area.friendHandler.queryPlayerNameRequest")
public class QueryPlayerNameHandler extends PomeloRequest{

	@Override
	public PomeloResponse request() throws Exception {
		WNPlayer player = (WNPlayer)pak.getPlayer();
		QueryPlayerNameRequest msg = QueryPlayerNameRequest.parseFrom(pak.getRemaingBytes());
		String strName = msg.getC2SStrName();
		return new PomeloResponse() {	
			@Override
			protected void write() throws IOException {
				FriendManager friendManager = player.getFriendManager();
				QueryPlayerNameResponse.Builder rtData  = friendManager.queryPlayerName(strName);
			    body.writeBytes(rtData.build().toByteArray());
			}
		};
	}

}
