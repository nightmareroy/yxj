package com.wanniu.game.request.friend;

import java.io.IOException;

import com.alibaba.fastjson.JSONObject;
import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.common.Const;
import com.wanniu.game.friend.FriendManager;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.FriendHandler.FriendDeleteRequest;
import pomelo.area.FriendHandler.FriendDeleteResponse;

@GClientEvent("area.friendHandler.friendDeleteRequest")
public class FriendDeleteHandler extends PomeloRequest{

	@Override
	public PomeloResponse request() throws Exception {
		WNPlayer player = (WNPlayer)pak.getPlayer();
		FriendDeleteRequest msg = FriendDeleteRequest.parseFrom(pak.getRemaingBytes());
		String friendId = msg.getC2SFriendId();
		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				FriendDeleteResponse.Builder res = FriendDeleteResponse.newBuilder();
				FriendManager friendManager = player.getFriendManager();
				JSONObject ret = friendManager.deleteFriend(friendId);
			    if(ret.getIntValue("code") == Const.CODE.OK){
			    	res.setS2CCode(OK);
			    	res.setS2CMsg(LangService.getValue("FRIEND_DEL_SUCCESS"));
			    }
			    else if(ret.getIntValue("code") == Const.CODE.FAIL){
			    	res.setS2CCode(FAIL);
			    	res.setS2CMsg(LangService.getValue("FRIEND_NOT_EXIST"));
			    }
			    body.writeBytes(res.build().toByteArray());
			}
		};
	}

}
