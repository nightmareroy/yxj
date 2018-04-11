package com.wanniu.game.request.friend;

import java.io.IOException;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.friend.FriendManager;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.FriendHandler.AddBlackListRequest;
import pomelo.area.FriendHandler.AddBlackListResponse;

@GClientEvent("area.friendHandler.addBlackListRequest")
public class AddBlackListHandler extends PomeloRequest{

	@Override
	public PomeloResponse request() throws Exception {
		WNPlayer player = (WNPlayer)pak.getPlayer();
		AddBlackListRequest msg = AddBlackListRequest.parseFrom(pak.getRemaingBytes());
		String blackListId = msg.getC2SBlackListId();
		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				AddBlackListResponse.Builder res = AddBlackListResponse.newBuilder();
				FriendManager friendManager = player.getFriendManager();
				String info = friendManager.addBlackList(blackListId);
			    if(info == null){
			    	res.setS2CCode(OK);
			    	res.setS2CMsg(LangService.getValue("FRIEND_ADD_SUCCESS"));
			    }
			    else{
			    	res.setS2CCode(FAIL);
			    	res.setS2CMsg(info);
			    }
			    body.writeBytes(res.build().toByteArray());
			}
		};
	}

}
