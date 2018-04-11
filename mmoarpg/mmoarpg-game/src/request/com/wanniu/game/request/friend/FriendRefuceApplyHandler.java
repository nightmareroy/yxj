package com.wanniu.game.request.friend;

import java.io.IOException;
import java.util.TreeMap;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.friend.FriendManager;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.FriendHandler.FriendRefuceApplyRequest;
import pomelo.area.FriendHandler.FriendRefuceApplyResponse;

@GClientEvent("area.friendHandler.friendRefuceApplyRequest")
public class FriendRefuceApplyHandler extends PomeloRequest{

	@Override
	public PomeloResponse request() throws Exception {
		WNPlayer player = (WNPlayer)pak.getPlayer();
		FriendRefuceApplyRequest msg = FriendRefuceApplyRequest.parseFrom(pak.getRemaingBytes());
		String requestId = msg.getC2SRequestId();
		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				FriendRefuceApplyResponse.Builder res = FriendRefuceApplyResponse.newBuilder();
				FriendManager friendManager = player.getFriendManager();
				TreeMap<String,Object> rtData = friendManager.friendRefuseApply(requestId,player);
				boolean result = (boolean)rtData.get("result");
			    if(result){
			    	res.setS2CCode(OK);
			        res.setS2CMsg(LangService.getValue("FRIEND_REFUSE_SUCCESS"));
			    }
			    else{
			    	res.setS2CCode(FAIL);
			    	String info = (String)rtData.get("info");
			    	res.setS2CMsg(info);
			    }
			    body.writeBytes(res.build().toByteArray());
			}
		};
	}

}
