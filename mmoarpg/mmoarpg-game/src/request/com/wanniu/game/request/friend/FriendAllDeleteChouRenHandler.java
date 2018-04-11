package com.wanniu.game.request.friend;

import java.io.IOException;
import java.util.List;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.friend.ChouRenManager;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.FriendHandler.FriendAllDeleteChouRenRequest;
import pomelo.area.FriendHandler.FriendAllDeleteChouRenResponse;

@GClientEvent("area.friendHandler.friendAllDeleteChouRenRequest")
public class FriendAllDeleteChouRenHandler extends PomeloRequest{

	@Override
	public PomeloResponse request() throws Exception {
		WNPlayer player = (WNPlayer)pak.getPlayer();
		FriendAllDeleteChouRenRequest msg = FriendAllDeleteChouRenRequest.parseFrom(pak.getRemaingBytes());
		List<String> chouRenIds = msg.getC2SChouRenIdsList();
		ChouRenManager chouRenManager = player.getChouRenManager();
		return new PomeloResponse() {
			
			@Override
			protected void write() throws IOException {
				for(String s : chouRenIds){
					chouRenManager.deleteChouRenById(s);
				}
				FriendAllDeleteChouRenResponse.Builder res = FriendAllDeleteChouRenResponse.newBuilder();
				res.setS2CCode(OK);
				res.setS2CMsg(LangService.getValue("FRIEND_DEL_SUCCESS"));
			}
		};
	}

}
