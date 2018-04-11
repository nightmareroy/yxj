package com.wanniu.game.request.friend;

import java.io.IOException;
import java.util.List;
import java.util.TreeMap;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.common.Const.TaskType;
import com.wanniu.game.friend.FriendManager;
import com.wanniu.game.player.PlayerUtil;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.FriendHandler.FriendAllAgreeApplyRequest;
import pomelo.area.FriendHandler.FriendAllAgreeApplyResponse;

@GClientEvent("area.friendHandler.friendAllAgreeApplyRequest")
public class FriendAllAgreeApplyHandler extends PomeloRequest{

	@Override
	public PomeloResponse request() throws Exception {
		WNPlayer player = (WNPlayer)pak.getPlayer();
		FriendAllAgreeApplyRequest msg = FriendAllAgreeApplyRequest.parseFrom(pak.getRemaingBytes());
		List<String> requestIds = msg.getC2SRequestIdsList();
		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				FriendAllAgreeApplyResponse.Builder res = FriendAllAgreeApplyResponse.newBuilder();
				FriendManager friendManager = player.getFriendManager();
				TreeMap<String,Object> rtData = friendManager.friendAllAgreeApply(requestIds,player);
				boolean result = (boolean)rtData.get("result");
			    if(result){
			        player.getPlayerTasks().dealTaskEvent(TaskType.ADD_FRIEND, requestIds.size());
			        res.setS2CCode(OK);
			        res.setS2CMsg(LangService.getValue("FRIEND_ADD_SUCCESS"));
			        
			     // 成就
			        player.achievementManager.onFriendNumber(requestIds.size());
			        for (String requestId : requestIds) {
				        if(PlayerUtil.isOnline(requestId)) {
				        	PlayerUtil.getOnlinePlayer(requestId).achievementManager.onFriendNumber(1);
				        } else {
				        	// TODO 对方不在线
				        }
			        }
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
