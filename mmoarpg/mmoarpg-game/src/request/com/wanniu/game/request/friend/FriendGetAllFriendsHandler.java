package com.wanniu.game.request.friend;

import java.io.IOException;
import java.util.List;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.friend.ChouRenManager;
import com.wanniu.game.friend.FriendManager;
import com.wanniu.game.player.GlobalConfig;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.FriendHandler.FriendGetAllFriendsResponse;
import pomelo.area.FriendHandler.PlayerInfo;

@GClientEvent("area.friendHandler.friendGetAllFriendsRequest")
public class FriendGetAllFriendsHandler extends PomeloRequest {

	@Override
	public PomeloResponse request() throws Exception {
		WNPlayer player = (WNPlayer) pak.getPlayer();
		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				FriendManager friendManager = player.getFriendManager();
				FriendGetAllFriendsResponse.Builder res = FriendGetAllFriendsResponse.newBuilder();
				List<PlayerInfo> friendList = friendManager.getAllFriends();
				int concernNumber = 0;
				for (int i = 0; i < friendList.size(); i++) {
					if (1 == friendList.get(i).getIsConcerned()) {
						concernNumber++;
					}
				}
				res.setS2CCode(OK);
				res.addAllFriends(friendList);
				res.setConcernNum(concernNumber);
				res.setAllConcernNum(GlobalConfig.Social_FocusNum);
				res.setFriendsNumMax(GlobalConfig.Social_MaxFriendNum);

				ChouRenManager chouRenManager = player.getChouRenManager();
				List<PlayerInfo> chouRenList = chouRenManager.getAllChouRens();
				res.addAllChouRens(chouRenList);
				res.setChouRensNumMax(GlobalConfig.Social_MaxEnemyNum);

				List<PlayerInfo> blackList = friendManager.getAllBlackList();
				res.addAllBlackList(blackList);
				res.setBlackListNumMax(GlobalConfig.Social_MaxBlacklistNum);
				body.writeBytes(res.build().toByteArray());
			}
		};
	}

}
