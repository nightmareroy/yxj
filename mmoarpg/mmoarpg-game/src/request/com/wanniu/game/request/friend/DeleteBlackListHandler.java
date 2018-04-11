package com.wanniu.game.request.friend;

import java.io.IOException;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.friend.FriendManager;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.FriendHandler.DeleteBlackListRequest;
import pomelo.area.FriendHandler.DeleteBlackListResponse;

@GClientEvent("area.friendHandler.deleteBlackListRequest")
public class DeleteBlackListHandler extends PomeloRequest{

	@Override
	public PomeloResponse request() throws Exception {
		WNPlayer player = (WNPlayer)pak.getPlayer();
		DeleteBlackListRequest msg = DeleteBlackListRequest.parseFrom(pak.getRemaingBytes());
		String blackListId = msg.getC2SBlackListId();
		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				DeleteBlackListResponse.Builder res = DeleteBlackListResponse.newBuilder();
				FriendManager friendManager = player.getFriendManager();
				boolean rtFlag = friendManager.deleteBlackListById(blackListId);
				if(rtFlag){
					res.setS2CCode(OK);
					res.setS2CMsg(LangService.getValue("FRIEND_DEL_SUCCESS"));
				}
				else{
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("FRIEND_DEL_FAIL"));
				}
				body.writeBytes(res.build().toByteArray());
			}
		};
	}

}
