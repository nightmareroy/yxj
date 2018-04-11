package com.wanniu.game.request.friend;

import java.io.IOException;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.friend.ChouRenManager;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.FriendHandler.FriendDeleteChouRenRequest;
import pomelo.area.FriendHandler.FriendDeleteChouRenResponse;

@GClientEvent("area.friendHandler.friendDeleteChouRenRequest")
public class FriendDeleteChouRenHandler extends PomeloRequest{

	@Override
	public PomeloResponse request() throws Exception {
		WNPlayer player = (WNPlayer)pak.getPlayer();
		FriendDeleteChouRenRequest msg = FriendDeleteChouRenRequest.parseFrom(pak.getRemaingBytes());
		String chouRenId = msg.getC2SChouRenId();
		ChouRenManager chouRenManager = player.getChouRenManager();
		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				FriendDeleteChouRenResponse.Builder res = FriendDeleteChouRenResponse.newBuilder();
				boolean rtFlag = chouRenManager.deleteChouRenById(chouRenId);
				if(rtFlag){
					res.setS2CCode(OK);
					res.setS2CMsg(LangService.getValue("FRIEND_ADD_SUCCESS"));
				}
				else{
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("FRIEND_ADD_FAIL"));
				}
				body.writeBytes(res.build().toByteArray());
			}
		};
	}

	
	
}
