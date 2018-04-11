package com.wanniu.game.request.friend;

import java.io.IOException;
import java.util.List;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.friend.FriendManager;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.FriendHandler.FriendAllRefuceApplyRequest;
import pomelo.area.FriendHandler.FriendAllRefuceApplyResponse;

@GClientEvent("area.friendHandler.friendAllRefuceApplyRequest")
public class FriendAllRefuceApplyHandler extends PomeloRequest{

	@Override
	public PomeloResponse request() throws Exception {
		WNPlayer player = (WNPlayer)pak.getPlayer();
		FriendAllRefuceApplyRequest msg = FriendAllRefuceApplyRequest.parseFrom(pak.getRemaingBytes());
		List<String> requestIds = msg.getC2SRequestIdsList();
		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				FriendAllRefuceApplyResponse.Builder res = FriendAllRefuceApplyResponse.newBuilder();
				FriendManager friendManager = player.getFriendManager();
				for( String s : requestIds ) {
			        friendManager.friendRefuseApply(s,player);
			    }
				res.setS2CCode(OK);
				res.setS2CMsg(LangService.getValue("FRIEND_REFUSE_SUCCESS"));
				body.writeBytes(res.build().toByteArray());
			}
		};
	}

}
