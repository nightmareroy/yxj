package com.wanniu.game.request.friend;

import java.io.IOException;
import java.util.TreeMap;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.friend.ChouRenManager;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.FriendHandler.FriendAddChouRenRequest;
import pomelo.area.FriendHandler.FriendAddChouRenResponse;

@GClientEvent("area.friendHandler.friendAddChouRenRequest")
public class FriendAddChouRenHandler extends PomeloRequest{

	@Override
	public PomeloResponse request() throws Exception {
		WNPlayer player = (WNPlayer)pak.getPlayer();
		ChouRenManager chouRenManager = player.getChouRenManager();
		FriendAddChouRenRequest msg = FriendAddChouRenRequest.parseFrom(pak.getRemaingBytes());
		String chouRenId = msg.getC2SChouRenId();
		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				TreeMap<String,Object> rtData = chouRenManager.add2ChouRenList(chouRenId);
				boolean result = (boolean)rtData.get("result");
				FriendAddChouRenResponse.Builder res = FriendAddChouRenResponse.newBuilder();
				String info = (String)rtData.get("info");
				if(result){
					res.setS2CCode(OK);
				}
				else{
					res.setS2CCode(FAIL);
				}
				res.setS2CMsg(info);
				body.writeBytes(res.build().toByteArray());
			}
		};
	}

}
