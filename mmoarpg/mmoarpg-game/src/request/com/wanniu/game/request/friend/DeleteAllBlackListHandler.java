package com.wanniu.game.request.friend;

import java.io.IOException;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.friend.FriendManager;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.FriendHandler.DeleteAllBlackListResponse;

@GClientEvent("area.friendHandler.deleteAllBlackListRequest")
public class DeleteAllBlackListHandler extends PomeloRequest{

	@Override
	public PomeloResponse request() throws Exception {
		WNPlayer player = (WNPlayer)pak.getPlayer();
		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				DeleteAllBlackListResponse.Builder res = DeleteAllBlackListResponse.newBuilder();
				FriendManager friendManager = player.getFriendManager();
				friendManager.deleteBlackList();
				res.setS2CCode(OK);
				res.setS2CMsg(LangService.getValue("FRIEND_DEL_SUCCESS"));
				body.writeBytes(res.build().toByteArray());
			}
		};
	}

}
