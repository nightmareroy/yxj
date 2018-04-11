package com.wanniu.game.request.friend;

import java.io.IOException;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.friend.FriendManager;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.FriendHandler.DeleteAllFriendMessageResponse;

@GClientEvent("area.friendHandler.deleteAllFriendMessageRequest")
public class DeleteAllFriendMessageHandler extends PomeloRequest{

	@Override
	public PomeloResponse request() throws Exception {
		WNPlayer player = (WNPlayer)pak.getPlayer();
		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				FriendManager friendManager = player.getFriendManager();
				DeleteAllFriendMessageResponse.Builder res = DeleteAllFriendMessageResponse.newBuilder();
				friendManager.deleteFriendMessage();
				res.setS2CCode(OK);
				res.setS2CMsg(LangService.getValue("FRIEND_DEL_SUCCESS"));
			}
		};
	}

}
