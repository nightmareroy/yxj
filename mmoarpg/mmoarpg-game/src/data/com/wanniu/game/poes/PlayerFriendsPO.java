package com.wanniu.game.poes;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.wanniu.core.game.entity.GEntity;
import com.wanniu.game.DBTable;
import com.wanniu.game.common.Table;
import com.wanniu.game.friend.BlackActor;
import com.wanniu.game.friend.FriendData;
import com.wanniu.game.friend.FriendManager.ApplyFriendData;
import com.wanniu.game.friend.FriendManager.RecordInfo;
import com.wanniu.game.friend.MessageDate;

@DBTable(Table.player_friends)
public class PlayerFriendsPO extends GEntity {

	// public String friends;
	public Map<String, FriendData> friends;

	public Date addPointTime;

	public int addPointToday;

	public Date friendShipTime;

	public Map<String, ApplyFriendData> applyFriendIds;    // 自己向哪些玩家发送了申请

	public Map<String, BlackActor> blackList;

	public List<MessageDate> friendMessage;    // 别人向自己申请的信息

	public Map<String, RecordInfo> recordInfos;

	public PlayerFriendsPO() {
		friends = new HashMap<String, FriendData>();
		addPointTime = new Date(0);
		friendShipTime = new Date(0);
		applyFriendIds = new HashMap<String, ApplyFriendData>();
		blackList = new HashMap<String, BlackActor>();
		friendMessage = new ArrayList<MessageDate>();
		recordInfos = new HashMap<String, RecordInfo>();
	}

}
