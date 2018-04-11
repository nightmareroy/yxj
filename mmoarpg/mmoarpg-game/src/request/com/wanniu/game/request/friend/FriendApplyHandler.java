package com.wanniu.game.request.friend;

import java.io.IOException;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.common.Const.TaskType;
import com.wanniu.game.friend.FriendManager;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.FriendHandler.FriendApplyRequest;
import pomelo.area.FriendHandler.FriendApplyResponse;

@GClientEvent("area.friendHandler.friendApplyRequest")
public class FriendApplyHandler extends PomeloRequest{

	@Override
	public PomeloResponse request() throws Exception {
		WNPlayer player = (WNPlayer)pak.getPlayer();
		FriendApplyRequest msg = FriendApplyRequest.parseFrom(pak.getRemaingBytes());
		String toPlayerId = msg.getC2SToPlayerId();
		return new PomeloResponse() {	
			@Override
			protected void write() throws IOException {
				FriendApplyResponse.Builder res = FriendApplyResponse.newBuilder();
				FriendManager friendManager = player.getFriendManager();
				String result = friendManager.friendApply(toPlayerId,player);
		        if (result == null) {
		            player.getPlayerTasks().dealTaskEvent(TaskType.ADD_FRIEND, 1);
		            res.setS2CCode(OK);
		            res.setS2CMsg(LangService.getValue("FRIEND_SEND_MESSAGE"));
		        }
		        else {
		        	res.setS2CCode(FAIL);
		            res.setS2CMsg(result);
		        }
		        body.writeBytes(res.build().toByteArray());
			}
		};
	}

}
