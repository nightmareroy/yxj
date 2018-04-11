package com.wanniu.game.request.friend;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.achievement.AchievementServiceNew;
import com.wanniu.game.common.Const.ACHIEVEMENT_CONDITION_TYPE;
import com.wanniu.game.common.Const.TaskType;
import com.wanniu.game.data.ext.AchievementExt;
import com.wanniu.game.friend.FriendsCenter;
import com.wanniu.game.player.PlayerUtil;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.FriendHandler.FriendAgreeApplyRequest;
import pomelo.area.FriendHandler.FriendAgreeApplyResponse;

@GClientEvent("area.friendHandler.friendAgreeApplyRequest")
public class FriendAgreeApplyHandler extends PomeloRequest{

	@Override
	public PomeloResponse request() throws Exception {
		WNPlayer player = (WNPlayer)pak.getPlayer();
		FriendAgreeApplyRequest msg = FriendAgreeApplyRequest.parseFrom(pak.getRemaingBytes());
		String requestId = msg.getC2SRequestId();
		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				FriendAgreeApplyResponse.Builder res = FriendAgreeApplyResponse.newBuilder();
				Map<String,Object> rtData = FriendsCenter.getInstance().friendAgreeApply(player.getId(),requestId);
				boolean result = (boolean)rtData.get("result");
			    if(result){
			        player.getPlayerTasks().dealTaskEvent(TaskType.ADD_FRIEND, 1);
			        res.setS2CCode(OK);
			        res.setS2CMsg(LangService.getValue("FRIEND_ADD_SUCCESS"));
			        
			        AchievementServiceNew achievementService = AchievementServiceNew.getInstance();
			        // 成就
			        player.achievementManager.onFriendNumber(1);
			        if(PlayerUtil.isOnline(requestId)) {
			        	PlayerUtil.getOnlinePlayer(requestId).achievementManager.onFriendNumber(1);
			        } else {		        	
			        	List<AchievementExt> achievementArray = achievementService.findByConditionType(ACHIEVEMENT_CONDITION_TYPE.FRIENDS_NUM);
			    		if (achievementArray.size() > 0) {
			    			AchievementServiceNew.OnFriendAchieveOfOfflinePlayer(1, achievementArray, requestId);
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
