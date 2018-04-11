package com.wanniu.game.request.friend;

import java.io.IOException;
import java.util.List;
import java.util.TreeMap;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.common.Const.TaskType;
import com.wanniu.game.friend.FriendManager;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.FriendHandler.FriendAllApplyRequest;
import pomelo.area.FriendHandler.FriendAllApplyResponse;

@GClientEvent("area.friendHandler.friendAllApplyRequest")
public class FriendAllApplyHandler extends PomeloRequest{

	@Override
	public PomeloResponse request() throws Exception {
		WNPlayer player = (WNPlayer)pak.getPlayer();
		FriendAllApplyRequest msg = FriendAllApplyRequest.parseFrom(pak.getRemaingBytes());
		List<String> toPlayerIds = msg.getC2SToPlayerIdsList();
		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				FriendAllApplyResponse.Builder res = FriendAllApplyResponse.newBuilder();
				FriendManager friendManager = player.getFriendManager();
				TreeMap<String,Object> rtData = friendManager.friendAllApply(toPlayerIds,player);
				boolean result = (boolean)rtData.get("result");
				String info = (String)rtData.get("info");
			    if (result) {
			        player.getPlayerTasks().dealTaskEvent(TaskType.ADD_FRIEND, toPlayerIds.size());
			        res.setS2CCode(OK);
			        res.setS2CMsg(info);
			    }
			    else {
			    	res.setS2CCode(FAIL);
			        res.setS2CMsg(info);
			    }
			    body.writeBytes(res.build().toByteArray());
			}
		};
	}

}
