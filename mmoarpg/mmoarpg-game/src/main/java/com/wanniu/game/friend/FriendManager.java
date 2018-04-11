package com.wanniu.game.friend;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GPlayer;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.logfs.Out;
import com.wanniu.core.util.DateUtil;
import com.wanniu.game.area.Area;
import com.wanniu.game.common.CommonUtil;
import com.wanniu.game.common.Const;
import com.wanniu.game.common.Const.TaskType;
import com.wanniu.game.common.Const.TipsType;
import com.wanniu.game.common.ConstsTR;
import com.wanniu.game.common.msg.MessageUtil;
import com.wanniu.game.daoyou.DaoYouService;
import com.wanniu.game.data.SShopCO;
import com.wanniu.game.data.SocialFriendCO;
import com.wanniu.game.functionOpen.FunctionOpenUtil;
import com.wanniu.game.guild.GuildUtil;
import com.wanniu.game.message.MessageData;
import com.wanniu.game.player.GlobalConfig;
import com.wanniu.game.player.PlayerUtil;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.poes.GuildMemberPO;
import com.wanniu.game.poes.GuildPO;
import com.wanniu.game.poes.PlayerAttachPO;
import com.wanniu.game.poes.PlayerFriendsPO;
import com.wanniu.game.poes.PlayerPO;
import com.wanniu.game.poes.SevenGoalPO;
import com.wanniu.game.sevengoal.SevenGoalManager.SevenGoalTaskType;
import com.wanniu.game.social.SocialFriendProps;
import com.wanniu.game.sysSet.SysSetFlag;
import com.wanniu.redis.PlayerPOManager;

import pomelo.Common.Avatar;
import pomelo.area.FriendHandler.FriendMessageListResponse;
import pomelo.area.FriendHandler.FriendShopCondition;
import pomelo.area.FriendHandler.GetSocialInfoResponse;
import pomelo.area.FriendHandler.PlayerInfo;
import pomelo.area.FriendHandler.Position;
import pomelo.area.FriendHandler.QueryPlayerNameResponse;
import pomelo.chat.ChatHandler.OnChatPush;

public class FriendManager {
	private static BlackListComparator blackListComparator = new BlackListComparator();
	private static FriendMessageComparator friendMessageComparator = new FriendMessageComparator();
	private static FriendInfoComparator friendInfoComparator = new FriendInfoComparator();
	private static FriendComparator friendComparator = new FriendComparator();
	private String playerId;
	private PlayerPO player;
	public PlayerFriendsPO po;

	public static class ApplyFriendData {
		public String id;
		public long time;

		// id : friendId,time : now.getTime()
		public ApplyFriendData() {

		}

		public ApplyFriendData(String id, long time) {
			this.id = id;
			this.time = time;
		}
	}

	public static class RecordInfo {
		public String friendId;
		public Date concernTime;
	}

	public FriendManager() {

	}

	public FriendManager(String playerId, PlayerFriendsPO data) {
		this.setPlayerId(playerId);
		this.po = data;
		this.player = PlayerUtil.getPlayerBaseData(playerId);

		if (this.po == null) {
			this.po = new PlayerFriendsPO();
			PlayerPOManager.put(ConstsTR.player_friendsTR, playerId, this.po);
		}
	}

	public List<PlayerInfo> getAllFriends() {
		List<PlayerInfo> friends = new ArrayList<>();
		for (Map.Entry<String, FriendData> node : this.po.friends.entrySet()) {
			FriendData data = node.getValue();
			PlayerInfo friend = data.friendToJson4PayLoad(this.po.recordInfos.get(data.friendId));
			if (friend != null) {
				friends.add(friend);
			}
		}

		friends.sort(friendComparator);
		return friends;
	}

	public final String friendApply(String toPlayerId, WNPlayer player) {
		boolean bOpen = PlayerUtil.isPlayerOpenedFunction(this.playerId, Const.FunctionType.FRIEND.getValue());

		if (!bOpen) {
			return FunctionOpenUtil.getTipsByName(Const.FunctionType.FRIEND.getValue());
		}
		bOpen = PlayerUtil.isPlayerOpenedFunction(toPlayerId, Const.FunctionType.FRIEND.getValue());
		if (!bOpen) {
			return LangService.getValue("FUNC_SET_TARGET_NOT_OPEN");
		}
		if (this.po.blackList.containsKey(toPlayerId)) {
			return LangService.getValue("FRIEND_TARGET_IN_BLACK_LIST");
		}

		PlayerPO toPlayer = PlayerUtil.getPlayerBaseData(toPlayerId);
		if (toPlayer == null) {
			return LangService.getValue("CROSS_SERVER_AUTH_LIMIT_ADDFRIEND");
		}

		// 暂时直接从reids获取，后期直接从Player缓存里获取
		PlayerAttachPO playerAttachPO = PlayerPOManager.findPO(ConstsTR.playerAttachTR, toPlayerId, PlayerAttachPO.class);
		if (null != playerAttachPO) {
			if ((playerAttachPO.sysSet & SysSetFlag.recvAddFriendSet.getValue()) == 0) {
				return LangService.getValue("FRIEND_FORBIDDEN_APPLY");
			}
		}

		if (this.hasApplyFriend(toPlayerId)) {
			return LangService.getValue("FRIEND_HAVE_ALREADY_APPLY");
		}

		FriendManager toPlayerFriendManager = FriendsCenter.getInstance().getFriendsMgr(toPlayerId);
		if (toPlayerFriendManager.isInBlackList(this.playerId)) {
			return LangService.getValue("FRIEND_IN_BLACK_LIST");
		}

		int num = this.applyFriendLength();
		if (num >= GlobalConfig.Social_InviteFriendMax) {
			return LangService.getValue("FRIEND_APPLY_FULL");
		}

		// 自己的判断
		if (toPlayerId.equals(this.playerId)) {// todo 写到const
			return LangService.getValue("FRIEND_TARGET PLAYER_YOUSELF");
		}
		int friendNumLimit = GlobalConfig.Social_MaxFriendNum;

		if (this.po.friends.size() >= friendNumLimit) {// todo 写到const
			return LangService.getValue("FRIEND_LIST_FULL");
		}
		if (this.po.friends.containsKey(toPlayerId)) {
			return LangService.getValue("FRIEND_HAVE_BEEN_FRIENDS");
		}
		if (toPlayerFriendManager.po.friends.size() >= friendNumLimit) {
			return LangService.getValue("FRIEND_TARGET_LIST_FULL");
		}

		if (isInFriendMessage(toPlayerId)) {
			return LangService.getValue("FRIEND_HAVE_IN_YOU_APPLY");
		}

		this.addApplyFriend(toPlayerId);
		this.addFriendMessageToFriend(player, toPlayerId);
		return null;
	}

	// 全部请求好友
	public final TreeMap<String, Object> friendAllApply(List<String> toPlayerIds, WNPlayer player) {

		TreeMap<String, Object> rtData = new TreeMap<>();
		rtData.put("result", true);
		rtData.put("info", LangService.getValue("FRIEND_SEND_MESSAGE"));

		boolean bOpen = PlayerUtil.isPlayerOpenedFunction(this.playerId, Const.FunctionType.FRIEND.getValue());

		if (!bOpen) {
			rtData.put("result", false);
			rtData.put("info", FunctionOpenUtil.getTipsByName(Const.FunctionType.FRIEND.getValue()));
			return rtData;
		}

		int friendNumLimit = GlobalConfig.Social_MaxFriendNum;

		if (po.friends.size() >= friendNumLimit) {
			rtData.put("result", false);
			rtData.put("info", LangService.getValue("FRIEND_LIST_FULL"));// 好友已满
			return rtData;
		}

		int num = this.applyFriendLength();
		if (num >= GlobalConfig.Social_InviteFriendMax) {
			rtData.put("result", false);
			rtData.put("info", LangService.getValue("FRIEND_APPLY_FULL"));// 好友已满
			return rtData;
		}

		boolean bNeedApply = false;
		bOpen = false;

		for (String playerId : toPlayerIds) {
			boolean result = PlayerUtil.isPlayerOpenedFunction(playerId, Const.FunctionType.FRIEND.getValue());
			if (result) {
				bOpen = true;
				if (!this.hasApplyFriend(playerId)) {
					bNeedApply = true;
					break;
				}
			}
		}

		if (!bOpen) {
			rtData.put("result", false);
			rtData.put("info", LangService.getValue("FUNC_SET_TARGET_NOT_OPEN"));
			return rtData;
		}

		if (!bNeedApply) {
			rtData.put("result", false);
			rtData.put("info", LangService.getValue("FRIEND_HAVE_ALREADY_APPLY"));// 已经邀请了
			return rtData;
		}

		for (String playerId : toPlayerIds) {
			this.friendApply(playerId, player);
		}
		return rtData;
	}

	public final void addFriendMessageToFriend(WNPlayer player, String friendId) {
		FriendManager friendMgr = FriendsCenter.getInstance().getFriendsMgr(friendId); // 获取好友的管理对象
		friendMgr.addFriendMessage(this.playerId, Const.FriendMessageType.TYPE_INVITE.getValue());

		Map<String, String> strMsg = new HashMap<>(1);
		strMsg.put("playerName", player.getName());
		MessageData message = MessageUtil.createMessage(Const.MESSAGE_TYPE.friend_invite.getValue(), this.playerId, null, strMsg);
		if (player.messageManager.addSendedMessage(message)) {
			MessageUtil.sendMessageToPlayer(message, friendId);
		}
	}

	public TreeMap<String, Object> friendAllAgreeApply(List<String> requestIds, WNPlayer player) {

		TreeMap<String, Object> rtData = new TreeMap<>();
		rtData.put("result", true);
		// 自己好友是否满
		if (po.friends.size() >= GlobalConfig.Social_MaxFriendNum) {
			rtData.put("result", false);
			rtData.put("info", LangService.getValue("FRIEND_LIST_FULL"));// 好友已满
			return rtData;
		}
		for (String s : requestIds) {
			this.friendAgreeApply(s);
		}
		return rtData;
	}

	/**
	 * 同意好友申请
	 * 
	 * @param requestId 申请人id
	 * @return
	 */
	public Map<String, Object> friendAgreeApply(String requestId) {
		Map<String, Object> rtData = new HashMap<String, Object>();
		rtData.put("result", true);

		if (requestId.equals(this.playerId)) {// todo 写到const
			rtData.put("result", false);
			rtData.put("info", LangService.getValue("FRIEND_TARGET"));// 不能是自己
			return rtData;
		}
		// 自己好友是否满
		if (po.friends.size() >= GlobalConfig.Social_MaxFriendNum) {
			this.removeFriendMessage(requestId, Const.FriendMessageType.TYPE_INVITE.getValue());
			rtData.put("result", false);
			rtData.put("info", LangService.getValue("FRIEND_LIST_FULL"));// 好友已满
			return rtData;
		}

		if (this.po.friends.containsKey(requestId)) {
			this.removeFriendMessage(requestId, Const.FriendMessageType.TYPE_INVITE.getValue());
			rtData.put("result", false);
			rtData.put("info", LangService.getValue("FRIEND_HAVE_BEEN_FRIENDS"));// 已经是好友
			return rtData;
		}

		FriendManager friendMgr = FriendsCenter.getInstance().getFriendsMgr(requestId); // 获取好友的管理对象
		if (friendMgr.po.friends.size() >= GlobalConfig.Social_MaxFriendNum) {
			this.removeFriendMessage(requestId, Const.FriendMessageType.TYPE_INVITE.getValue());
			rtData.put("result", false);
			rtData.put("info", LangService.getValue("FRIEND_TARGET_LIST_FULL"));// 对方的好友列表已满
			return rtData;
		}

		if (friendMgr.po.blackList.containsKey(this.playerId)) {
			rtData.put("result", false);
			rtData.put("info", LangService.getValue("FRIEND_IN_BLACK_LIST"));// 黑名单无法添加好友
			return rtData;
		}

		// 自己添加好友处理
		this.addFriend(requestId);
		this.removeApplyFriend(requestId);
		this.removeFriendMessage(requestId, Const.FriendMessageType.TYPE_INVITE.getValue());

		// 好友相应添加我为好友处理
		friendMgr.addFriend(this.playerId);
		friendMgr.removeFriendMessage(this.playerId, Const.FriendMessageType.TYPE_INVITE.getValue());
		friendMgr.removeApplyFriend(player.id);

		// TODO chatRemote
		// try{
		// pomelo.app.rpc.chat.chatRemote.addFriend({}, player.id, requestId,
		// function(code){
		// Out.debug("chat.chatRemote.addFrined "+code);
		// });
		// }catch (err){
		// FSLog.error("chatRemote addFriend ero")
		// }

		WNPlayer player = PlayerUtil.findPlayer(this.playerId);
		String msg = LangService.getValue("FRIEND_INVITATION_SUCCESS");
		if (null != player) {
			msg = msg.replace("{friendName}", player.getName());
		}

		Out.debug("fromPlayer.sendSysMessage ", msg);
		PlayerUtil.sendSysMessageToPlayer(msg, requestId, null);
		
		player.sevenGoalManager.processGoal(SevenGoalTaskType.ADD_FRIEND);
		WNPlayer friend=PlayerUtil.getOnlinePlayer(requestId);
		if(friend!=null) {
			friend.sevenGoalManager.processGoal(SevenGoalTaskType.ADD_FRIEND);
		}
		else {
			SevenGoalPO sevenGoalPO=PlayerPOManager.findPO(ConstsTR.SevenGoal, requestId, SevenGoalPO.class);
			sevenGoalPO.processAddFriend();
		}
		return rtData;
	}

	/**
	 * 添加好友
	 * 
	 * @param requestId 好友id
	 */
	public final void addFriend(String requestId) {
		// 不能添加自己为好友
		if (this.playerId.equals(requestId)) {
			return;
		}

		// 已经在好友列表里面
		if (this.po.friends.containsKey(requestId)) {
			return;
		}

		FriendData friend = new FriendData();
		friend.friendId = requestId;
		friend.friendLv = 0;
		friend.createTimeStamp = new Date();
		friend.addFriendLvTime = new Date(0);
		friend.addFriendLvToday = 0;

		Out.debug("add a new Friend: ", friend);
		this.po.friends.put(friend.friendId, friend);
		// this.friendsNum++;

		// ================跟Player绑定的相应处理后续===================
		// this.player.getPlayerTasks().dealTaskEvent(TaskType.FRIEND_NUM, 1);
		// this.player.achievementManager.onFriendNumber();

	}

	public final Position getFriendPosition(String friendId) {
		Position.Builder currentPos = Position.newBuilder();
		WNPlayer friendPlayer = PlayerUtil.findPlayer(friendId);
		if (null != friendPlayer) {
			Area area = friendPlayer.getArea();
			if (null != area) {
				currentPos.setAreaName(area.getSceneName());
				currentPos.setAreaId(area.areaId);
			}
		} else {
			currentPos.setAreaName("");
			currentPos.setTargetX(0);
			currentPos.setTargetY(0);
			currentPos.setAreaId(0);
		}

		return currentPos.build();
	}

	public final boolean isFriend(String friendId) {
		for (Map.Entry<String, FriendData> node : this.po.friends.entrySet()) {
			if (node.getValue().friendId.equals(friendId)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 所有好友的id
	 * 
	 * @returns {Array}
	 */
	public Set<String> getAllFriendId() {
		return this.po.friends.keySet();
	}

	public final List<String> getAllOnlineFriendId() {
		ArrayList<String> data = new ArrayList<>();
		for (Map.Entry<String, FriendData> node : this.po.friends.entrySet()) {
			if (PlayerUtil.isOnline(node.getKey())) {
				data.add(node.getKey());
			}
		}
		return data;
	}

	public final int getFriendsNum() {
		return this.po.friends.size();
	}

	public final ArrayList<PlayerInfo> addFriendInfo(int selfLevel) {

		ArrayList<PlayerInfo> friendInfos = new ArrayList<>();
		ArrayList<PlayerInfo> otherfriendInfos = new ArrayList<>();

		int recommendNumLimit = GlobalConfig.Social_RecommendNum;
		int levelRange = GlobalConfig.Social_LevelRange;

		List<GPlayer> players = new ArrayList<GPlayer>();
		for (GPlayer player : PlayerUtil.getAllOnlinePlayer()) {
			boolean isOpen = PlayerUtil.isPlayerOpenedFunction(player.getId(), Const.FunctionType.FRIEND.getValue());
			if (isOpen)
				players.add(player);
		}

		Random r = new Random();
		while (players.size() > 0) {
			int idx = r.nextInt(players.size());
			WNPlayer friendPlayer = (WNPlayer) players.get(idx);
			if (null == friendPlayer) {
				players.remove(idx);
				continue;
			}

			String playerId = friendPlayer.getId();
			if (null == friendPlayer || playerId.equals(this.playerId) // 屏蔽自己
					|| this.po.friends.containsKey(playerId) // 已是好友
					|| this.po.blackList.containsKey(playerId) // 已在黑名单
					|| this.hasApplyFriend(playerId) // 已经向对方发送申请
					|| this.isInFriendMessage(playerId) // 对方已在自己申请列表
			) {
				players.remove(idx);
				continue;
			}

			if (friendPlayer.getLogicServerId() != this.player.logicServerId) {
				players.remove(idx);
				continue;
			}

			PlayerInfo.Builder friendInfo = PlayerInfo.newBuilder();
			friendInfo.setId(playerId);
			friendInfo.setGuildId(friendPlayer.guildManager.getGuildId());
			friendInfo.setGuildName(friendPlayer.guildManager.getGuildName());
			friendInfo.setName(friendPlayer.getName());
			friendInfo.setLevel(friendPlayer.getLevel());
			friendInfo.setPro(friendPlayer.getPro());
			friendInfo.setStageLevel(friendPlayer.player.upLevel);
			friendInfo.setVip(0);
			friendInfo.setFightPower(CommonUtil.calFightPower(friendPlayer.btlDataManager.allInflus));

			List<Avatar> avatars = PlayerUtil.getBattlerServerAvatar(playerId);
			friendInfo.addAllAvatars(avatars);
			if (Math.abs(friendInfo.getLevel() - selfLevel) < levelRange) {
				friendInfos.add(friendInfo.build());
				if (friendInfos.size() >= recommendNumLimit) {
					players.remove(idx);
					break;
				}
			} else {
				if (otherfriendInfos.size() < recommendNumLimit) {
					otherfriendInfos.add(friendInfo.build());
				}
			}
			players.remove(idx);
		}

		if (friendInfos.size() < recommendNumLimit) {// 等级限制内不足时随机加满
			while (otherfriendInfos.size() > 0) {
				int idx = r.nextInt(otherfriendInfos.size());
				friendInfos.add(otherfriendInfos.get(idx));
				if (friendInfos.size() >= recommendNumLimit) {
					otherfriendInfos.remove(idx);
					break;
				}
				otherfriendInfos.remove(idx);
			}
		}
		friendInfos.sort(friendInfoComparator);
		return friendInfos;
	}

	public PlayerInfo.Builder getPlayerBaseData(String playerId) {
		PlayerInfo.Builder datas = PlayerInfo.newBuilder();
		Position.Builder currentPos = Position.newBuilder();
		boolean isOnline = PlayerUtil.isOnline(playerId);
		datas.setIsOnline(isOnline ? 1 : 0);

		if (isOnline) {
			WNPlayer friendPlayer = PlayerUtil.findPlayer(playerId);
			if (null == friendPlayer) {
				return datas;
			}

			datas.setGuildId(friendPlayer.guildManager.getGuildId());
			datas.setGuildName(friendPlayer.guildManager.getGuildName());

			datas.setName(friendPlayer.getName());
			datas.setLevel(friendPlayer.getLevel());
			datas.setPro(friendPlayer.getPro());
			datas.setStageLevel(friendPlayer.player.upLevel);
			datas.setVip(0);
			datas.setFightPower(CommonUtil.calFightPower(friendPlayer.btlDataManager.allInflus));

			if (DaoYouService.getInstance().getDaoYou(friendPlayer.getId()) != null) {
				datas.setHasAlly(1);
			}
			Area area = friendPlayer.getArea();
			if (area != null) {
				currentPos.setAreaName(area.getSceneName());
				currentPos.setAreaId(area.areaId);
			}
		} else {
			PlayerPO friendPlayer = PlayerUtil.getPlayerBaseData(playerId);

			if (null == friendPlayer) {
				return datas;
			}

			datas.setName(friendPlayer.name);
			datas.setLevel(friendPlayer.level);
			datas.setPro(friendPlayer.pro);
			datas.setStageLevel(friendPlayer.upLevel);
			datas.setVip(0);
			datas.setGuildId("");
			datas.setGuildName("");

			GuildMemberPO myInfo = GuildUtil.getGuildMember(playerId);
			if (null != myInfo) {
				GuildPO myGuild = GuildUtil.getGuild(myInfo.guildId);
				if (null != myGuild) {
					datas.setGuildId(myGuild.id);
					datas.setGuildName(myGuild.name);
				}
			}

			datas.setFightPower(friendPlayer.fightPower);
		}

		datas.setId(playerId);
		datas.setCurrentPos(currentPos.build());
		return datas;
	}

	public QueryPlayerNameResponse.Builder queryPlayerName(String strName) {

		QueryPlayerNameResponse.Builder data = QueryPlayerNameResponse.newBuilder();

		int playerNum = 0;

		int recommendNumLimit = GlobalConfig.Social_RecommendNum;
		ArrayList<PlayerInfo> list = new ArrayList<>();
		Collection<GPlayer> players = PlayerUtil.getAllOnlinePlayer();
		for (GPlayer player : players) {
			WNPlayer friendPlayer = (WNPlayer) player;
			// 排除玩家自己
			if (this.playerId.equals(friendPlayer.getId())) {
				continue;
			}
			PlayerInfo.Builder playerName = PlayerInfo.newBuilder();
			playerName.setId(friendPlayer.getId());
			playerName.setGuildId(friendPlayer.guildManager.getGuildId());
			playerName.setGuildName(friendPlayer.guildManager.getGuildName());
			playerName.setName(friendPlayer.getName());
			playerName.setLevel(friendPlayer.getLevel());
			playerName.setPro(friendPlayer.getPro());
			playerName.setStageLevel(friendPlayer.player.upLevel);
			playerName.setVip(0);
			playerName.setFightPower(CommonUtil.calFightPower(friendPlayer.btlDataManager.allInflus));
			List<Avatar> avatars = PlayerUtil.getBattlerServerAvatar(friendPlayer.getId());
			playerName.addAllAvatars(avatars);
			if (playerName.getName().equals(strName)) {
				list.add(playerName.build());
				playerNum++;
			}
			if (playerNum >= recommendNumLimit)
				break;
		}
		if (list.size() == 0) {
			data.setS2CMsg(LangService.getValue("FRIEND_FIND_NONE"));
			data.setS2CCode(PomeloRequest.FAIL);
		} else {
			data.setS2CCode(PomeloRequest.OK);
		}
		data.addAllData(list);
		return data;
	}

	public final void onPlayerEnter(Date logoutTime) {
		Date nowTime = new Date();
		int days = (int) Math.floor((nowTime.getTime() - logoutTime.getTime()) / Const.DAY_BY_MILLISECOND);

		int decreaseFriendLv = days * GlobalConfig.Social_FavorReduce;

		for (Map.Entry<String, FriendData> node : this.po.friends.entrySet()) {
			FriendData data = node.getValue();
			if (days > 0) {
				data.friendLv -= decreaseFriendLv;

				if (data.friendLv < 0) {
					data.friendLv = 0;
				}
			}

			String strData = MessageUtil.getPlayerNameColor(this.player.name, this.player.pro);

			String msg = LangService.getValue("FRIEND_ONLINE");

			msg = msg.replace("{playerName}", strData);
			WNPlayer player = PlayerUtil.getOnlinePlayer(this.playerId);
			if (null != player)
				MessageUtil.sendSysChat(player, msg, TipsType.NORMAL);
		}

	}

	/**
	 * 自己下线，通知好友下线
	 */
	public final void onPlayerOffline() {
		WNPlayer selfPlayer = PlayerUtil.getOnlinePlayer(this.playerId);
		if (null == selfPlayer)
			return;

		for (Map.Entry<String, FriendData> node : this.po.friends.entrySet()) {
			String strData = MessageUtil.getPlayerNameColor(this.player.name, this.player.pro);
			String msg = LangService.getValue("FRIEND_OFFLINE");
			msg = msg.replace("{playerName}", strData);
			OnChatPush.Builder chatMsg = MessageUtil.createChatMsg(selfPlayer, msg, Const.CHAT_SCOPE.SYSTEM, TipsType.NORMAL);
			WNPlayer player = PlayerUtil.findPlayer(node.getKey());
			if (null != player) {
				player.receive("chat.chatPush.onChatPush", chatMsg.build());
			}
		}
	}

	public final TreeMap<String, Object> concernFriend(String friendId, WNPlayer player) {
		TreeMap<String, Object> rtData = new TreeMap<>();
		rtData.put("result", true);
		rtData.put("info", "");
		if (!this.po.friends.containsKey(friendId)) {
			rtData.put("result", false);
			rtData.put("info", LangService.getValue("FRIEND_TARGET_HAVE_DELETE"));
			return rtData;
		}
		if (!PlayerUtil.isOnline(friendId)) {
			rtData.put("result", false);
			rtData.put("info", LangService.getValue("PLAYER_NOT_ONLINE"));// 玩家不在线
			return rtData;
		}

		if (!this.po.recordInfos.containsKey(friendId)) {
			RecordInfo info = new RecordInfo();
			info.friendId = friendId;
			info.concernTime = new Date(0);
			this.po.recordInfos.put(friendId, info);
		}
		Date concernTime = this.po.recordInfos.get(friendId).concernTime;

		Date currDate = new Date();
		if (DateUtil.isSameDay(concernTime, currDate)) {
			rtData.put("result", false);
			rtData.put("info", LangService.getValue("FRIEND_ALREAD_CONCERNED"));// 每天只能对一个好友关注一次
			return rtData;
		}
		RecordInfo info = this.po.recordInfos.get(friendId);
		info.concernTime = currDate;
		this.po.recordInfos.put(friendId, info);

		SocialFriendCO socialFriendProp = SocialFriendProps.findByMSocialAction(1);
		// 亲密度
		this.addFriendLv(friendId, socialFriendProp.favorNum);

		socialFriendProp = SocialFriendProps.findByMSocialAction(2);

		int friendLv = socialFriendProp.favorNum;

		FriendManager friendMgr = FriendsCenter.getInstance().getFriendsMgr(friendId); // 获取好友的管理对象
		friendMgr.addFriendMessage(this.playerId, Const.FriendMessageType.TYPE_CONCERN.getValue());
		friendMgr.addFriendShipPoint();
		friendMgr.addFriendLv(this.playerId, friendLv);

		WNPlayer selfPlayer = PlayerUtil.getOnlinePlayer(this.playerId);
		// 增益BUFF
		if (this.friendConcernTimesToday() == GlobalConfig.Social_FocusNum) {

			selfPlayer.initAndCalAllInflu(null);
			selfPlayer.pushAndRefreshEffect(false);
		}

		player.getPlayerTasks().dealTaskEvent(TaskType.CONCERN_FRIEND, 1);

		return rtData;

	}

	public final void addFriendLv(String friendId, int addFriendLv) {
		if (!this.po.friends.containsKey(friendId)) {
			return;
		}
		FriendData friendData = this.po.friends.get(friendId);
		if (addFriendLv < 0) {
			friendData.friendLv += addFriendLv;

			if (friendData.friendLv < 0) {
				friendData.friendLv = 0;
			}
			this.po.friends.put(friendId, friendData);

			return;
		}

		Date currDate = new Date();
		if (!DateUtil.isSameDay(friendData.addFriendLvTime, currDate)) {
			friendData.addFriendLvToday = 0;
			friendData.addFriendLvTime = currDate;
		}

		int favorNumMax = GlobalConfig.Social_FavorNumMax;
		int favorNumDailyMax = GlobalConfig.Social_FavorNumDailyMax;

		if (friendData.friendLv >= favorNumMax || friendData.addFriendLvToday >= favorNumDailyMax) {
			return;
		}

		friendData.friendLv += addFriendLv;
		friendData.addFriendLvToday += addFriendLv;

		if (friendData.friendLv > favorNumMax) {
			friendData.addFriendLvToday -= (friendData.friendLv - favorNumMax);

			friendData.friendLv = favorNumMax;
		}

		if (friendData.addFriendLvToday > favorNumDailyMax) {
			friendData.friendLv -= (friendData.addFriendLvToday - favorNumDailyMax);

			friendData.addFriendLvToday = favorNumDailyMax;
		}
		this.po.friends.put(friendId, friendData);

	}

	public final int friendConcernTimesToday() {

		int concernTimes = 0;

		Date currDate = new Date();
		for (Map.Entry<String, FriendData> node : this.po.friends.entrySet()) {
			String id = node.getKey();
			if (!this.po.recordInfos.containsKey(id)) {
				continue;
			}
			RecordInfo recordInfo = this.po.recordInfos.get(id);
			if (DateUtil.isSameDay(recordInfo.concernTime, currDate)) {
				concernTimes++;
			}
		}
		return concernTimes;
	}

	public final Map<String, Integer> calAllInfluence() {

		Map<String, Integer> influs = new HashMap<>();

		if (this.friendConcernTimesToday() >= GlobalConfig.Social_FocusNum) {

			String values = GlobalConfig.Social_FocusBuff;
			String[] nodeValues = values.split(";");
			for (int i = 0; i < nodeValues.length; i++) {
				String[] buffValue = nodeValues[i].split(",");
				int value = 0;
				if (influs.containsKey(buffValue[0])) {
					value = influs.get(buffValue[0]);
				}
				value += Integer.parseInt(buffValue[1]);
				influs.put(buffValue[0], value);
			}
		}
		return influs;
	}

	public final void addFriendShipPoint() {
		Date currDate = new Date();
		if (!DateUtil.isSameDay(this.po.addPointTime, currDate)) {
			this.po.addPointToday = 0;
			this.po.addPointTime = currDate;
		}

		int friendshipNum = SocialFriendProps.findByMSocialAction(2).friendshipNum;
		int friendshipNumDailyMax = GlobalConfig.Social_FriendshipNumDailyMax;
		WNPlayer selfPlayer = PlayerUtil.getOnlinePlayer(this.playerId);
		if (this.po.addPointToday < friendshipNumDailyMax - friendshipNum) {
			this.po.addPointToday += friendshipNum;
			selfPlayer.baseDataManager.addFriendly(friendshipNum);
			selfPlayer.pushDynamicData("friendly", selfPlayer.player.friendly);

		} else if (this.po.addPointToday < friendshipNumDailyMax) {
			selfPlayer.baseDataManager.addFriendly(friendshipNumDailyMax - this.po.addPointToday);
			selfPlayer.pushDynamicData("friendly", selfPlayer.player.friendly);

			this.po.addPointToday = friendshipNumDailyMax;

		}
	}

	public final int applyFriendLength() {
		int number = 0;
		Date now = new Date();
		for (Map.Entry<String, ApplyFriendData> node : this.po.applyFriendIds.entrySet()) {
			ApplyFriendData data = node.getValue();
			if (now.getTime() - data.time < Const.DAY_BY_MILLISECOND) {

				number++;// 有效
			}
		}
		return number;
	}

	public final boolean hasApplyFriend(String friendId) {
		ApplyFriendData data = this.po.applyFriendIds.get(friendId);
		if (data == null) {
			return false;
		}
		Date now = new Date();
		if (now.getTime() - data.time < Const.DAY_BY_MILLISECOND) {
			return true;// 有效
		}
		return false;
	}

	public final void addApplyFriend(String friendId) {
		Date now = new Date();
		ApplyFriendData data = this.po.applyFriendIds.get(friendId);
		if (data != null) {
			if (now.getTime() - data.time < Const.DAY_BY_MILLISECOND) {
				return;// 有效
			} else {
				data.time = now.getTime();
			}
		} else {
			data = new ApplyFriendData(friendId, now.getTime());
			this.po.applyFriendIds.put(friendId, data);
		}

	}

	public final void removeApplyFriend(String friendId) {
		ApplyFriendData data = this.po.applyFriendIds.get(friendId);
		if (data != null) {
			this.po.applyFriendIds.remove(friendId);

		}
	}

	public final FriendMessageListResponse.Builder friendMessageList() {

		FriendMessageListResponse.Builder data = FriendMessageListResponse.newBuilder();

		ArrayList<PlayerInfo> friendMessageInfos = new ArrayList<>();

		Out.debug("friendMessageList :", this.po.friendMessage);

		for (MessageDate message : this.po.friendMessage) {
			PlayerInfo.Builder info = this.getPlayerBaseData(message.playerId);
			info.setType(message.type);
			info.setId(message.playerId);
			info.setTime(JSON.toJSONString(message.time));
			info.setAddFriendShipPoint(SocialFriendProps.findByMSocialAction(2).friendshipNum);

			friendMessageInfos.add(info.build());
		}
		friendMessageInfos.sort(friendMessageComparator);
		data.addAllFriendMessageInfos(friendMessageInfos);
		data.setMessageNumMax(GlobalConfig.Social_MaxFriendMessageNum);
		data.setFriendlyCode("friendly");
		return data;
	}

	public final void deleteFriendMessage() {
		this.po.friendMessage.clear();

	}

	public final List<PlayerInfo> getAllBlackList() {
		List<PlayerInfo> blackList = new ArrayList<>();
		for (Map.Entry<String, BlackActor> node : this.po.blackList.entrySet()) {
			String id = node.getKey();
			PlayerInfo.Builder info = this.getPlayerBaseData(id);
			info.setId(id);
			info.setCreateTime(JSON.toJSONString(node.getValue().createTime));
			blackList.add(info.build());
		}
		blackList.sort(blackListComparator);
		return blackList;
	}

	public final boolean deleteBlackListById(String blackListId) {
		if (!this.po.blackList.containsKey(blackListId)) {
			return false;
		}
		this.po.blackList.remove(blackListId);
		// this.blackListNum--;

		return true;
	}

	public final void deleteBlackList() {
		this.po.blackList.clear();
		// this.blackListNum = 0;

	}

	public final String addBlackList(String blackListId) {

		boolean bOpen = PlayerUtil.isPlayerOpenedFunction(this.playerId, Const.FunctionType.FRIEND.getValue());

		if (!bOpen) {
			return FunctionOpenUtil.getTipsByName(Const.FunctionType.FRIEND.getValue());
		}

		bOpen = PlayerUtil.isPlayerOpenedFunction(blackListId, Const.FunctionType.FRIEND.getValue());

		if (!bOpen) {
			return LangService.getValue("FUNC_SET_TARGET_NOT_OPEN");
		}

		if (this.po.blackList.size() >= GlobalConfig.Social_MaxBlacklistNum) {
			return LangService.getValue("FRIEND_BLACK_LIST_FULL");
		}

		if (this.po.blackList.containsKey(blackListId)) {
			return LangService.getValue("FRIEND_HAVE_IN_BLACK_LIST");
		}

		BlackActor blackList = new BlackActor();
		blackList.id = blackListId;
		blackList.createTime = new Date();
		this.po.blackList.put(blackListId, blackList);
		// this.blackListNum++;

		this.deleteFriend(blackListId);
		return null;
	}

	public final boolean isInBlackList(String playerId) {
		return (this.po.blackList.containsKey(playerId));
	}

	public final void killOtherOnce(String playerId) {
		int FavorNum = SocialFriendProps.findByMSocialAction(3).favorNum;
		this.addFriendLv(playerId, FavorNum);
	}

	public final void beKilledOnce(String playerId) {// playerId:杀死自己的玩家的id
		int FavorNum = SocialFriendProps.findByMSocialAction(4).favorNum;
		this.addFriendLv(playerId, FavorNum);
	}

	public final GetSocialInfoResponse.Builder getSocialInfo(String playerId) {
		GetSocialInfoResponse.Builder data = GetSocialInfoResponse.newBuilder();
		Date currDate = new Date();
		if (!DateUtil.isSameDay(this.po.addPointTime, currDate)) {
			data.setAddPointToday(0);
		} else {
			data.setAddPointToday(this.po.addPointToday);
		}
		data.setFriendShipPoint(this.player.friendly);
		data.setAddPointTodayMax(GlobalConfig.Social_FriendshipNumDailyMax);
		return data;
	}

	// 拒绝申请
	public final TreeMap<String, Object> friendRefuseApply(String requestId, WNPlayer player) {

		TreeMap<String, Object> rtData = new TreeMap<>();
		rtData.put("result", true);

		this.removeFriendMessage(requestId, Const.FriendMessageType.TYPE_INVITE.getValue());
		this.removeApplyFriend(requestId);

		FriendManager friendMgr = FriendsCenter.getInstance().getFriendsMgr(requestId);
		friendMgr.removeFriendMessage(this.playerId, Const.FriendMessageType.TYPE_INVITE.getValue());
		friendMgr.removeApplyFriend(this.playerId);

		String msg = LangService.getValue("FRIEND_INVITATION_REDUSE");
		msg = msg.replace("{inviterName}", player.getName());
		PlayerUtil.sendSysMessageToPlayer(msg, requestId, null);
		return rtData;
	}

	public final ArrayList<FriendShopCondition> getConditions(SShopCO prop) {
		ArrayList<FriendShopCondition> data = new ArrayList<>();
		FriendShopCondition.Builder level = FriendShopCondition.newBuilder();
		level.setType(1);
		level.setNumber(prop.levelReq);
		data.add(level.build());
		FriendShopCondition.Builder up = FriendShopCondition.newBuilder();
		up.setType(2);
		up.setNumber(prop.upReq);
		data.add(up.build());
		FriendShopCondition.Builder vip = FriendShopCondition.newBuilder();
		vip.setType(3);
		vip.setNumber(prop.vipReq);
		data.add(vip.build());
		FriendShopCondition.Builder race = FriendShopCondition.newBuilder();
		race.setType(4);
		race.setNumber(prop.raceReq);
		data.add(race.build());
		FriendShopCondition.Builder raceClass = FriendShopCondition.newBuilder();
		raceClass.setType(5);
		raceClass.setNumber(prop.raceClass);
		data.add(raceClass.build());
		return data;
	}

	public final String getConditionStatus(SShopCO prop) {
		ArrayList<FriendShopCondition> conditions = this.getConditions(prop);
		for (int i = 0; i < conditions.size(); ++i) {
			FriendShopCondition condition = conditions.get(i);
			int type = condition.getType();
			int number = condition.getNumber();
			if (type == 1 && this.player.level < number) {
				return LangService.getValue("EXCHANGE_LEVEL_NOT_REACH");
			}
			if (type == 2 && this.player.upLevel < number) {
				return LangService.getValue("EXCHANGE_STAGE_NOT_REACH");
			}
			// if (type == 3 && this.player.vip < number) {
			// return LangService.getValue("EXCHANGE_VIP_NOT_REACH");
			// }
			if (type == 4) {}
			if (type == 5) {}
			if (type == 6) {}
			if (type == 7) {}
		}
		return null;
	}

	public void removeFriendMessage(String playerId, int type) {
		for (MessageDate messageData : this.po.friendMessage) {
			if (messageData.type == type && messageData.playerId.equals(playerId)) {
				this.po.friendMessage.remove(messageData);

				return;
			}
		}
	}

	public void addFriendMessage(String playerId, int type) {

		if (this.po.friendMessage.size() >= GlobalConfig.Social_MaxFriendMessageNum) {
			this.po.friendMessage.remove(this.po.friendMessage.size() - 1);
		}

		MessageDate messageData = new MessageDate();
		messageData.type = type;
		messageData.playerId = playerId;
		messageData.time = new Date();
		this.po.friendMessage.add(messageData);

	}

	// 对方是否在自己好友申请列表中
	public boolean isInFriendMessage(String playerId) {
		boolean isIn = false;
		for (int i = 0; i < this.po.friendMessage.size(); i++) {
			MessageDate elem = this.po.friendMessage.get(i);
			if (elem.playerId.equals(playerId)) {
				isIn = true;
				break;
			}
		}
		return isIn;
	}

	// 删除好友
	public void removeFriend(String id) {
		if (this.po.friends.containsKey(id)) {
			this.po.friends.remove(id);
		}
	}

	// 删除好友
	public JSONObject deleteFriend(String friendId) {
		JSONObject ret = new JSONObject();
		// 没有这个好友
		if (!this.po.friends.containsKey(friendId)) {
			ret.put("code", Const.CODE.FAIL);
			ret.put("des", LangService.getValue("FRIEND_NOT_EXIST"));
			return ret;
		}
		// 自己删除好友
		this.removeFriend(friendId);
		// 好友删除自己
		FriendManager friendMgr = FriendsCenter.getInstance().getFriendsMgr(friendId);
		friendMgr.removeFriend(this.playerId);
		ret.put("code", Const.CODE.OK);
		ret.put("des", LangService.getValue("FRIEND_DEL_SUCESS"));
		return ret;
	}

	public String getPlayerId() {
		return playerId;
	}

	public void setPlayerId(String playerId) {
		this.playerId = playerId;
	}

	private static class BlackListComparator implements Comparator<PlayerInfo> {
		@Override
		public int compare(PlayerInfo data1, PlayerInfo data2) {
			return data1.getCreateTime().compareTo(data2.getCreateTime());
		}
	}

	private static class FriendMessageComparator implements Comparator<PlayerInfo> {
		@Override
		public int compare(PlayerInfo data1, PlayerInfo data2) {
			return data1.getTime().compareTo(data2.getTime());
		}
	}

	private static class FriendInfoComparator implements Comparator<PlayerInfo> {
		@Override
		public int compare(PlayerInfo data1, PlayerInfo data2) {
			return data1.getLevel() < data2.getLevel() ? 1 : -1;
		}
	}

	private static class FriendComparator implements Comparator<PlayerInfo> {
		@Override
		public int compare(PlayerInfo data1, PlayerInfo data2) {
			if (data1.getIsOnline() != data2.getIsOnline()) {
				return data1.getIsOnline() < data2.getIsOnline() ? 1 : -1;
			}
			if (data1.getFriendLv() != data2.getFriendLv()) {
				return data1.getFriendLv() < data2.getFriendLv() ? 1 : -1;
			}
			if (data1.getVip() != data2.getVip()) {
				return data1.getVip() < data2.getVip() ? 1 : -1;
			}

			if (data1.getStageLevel() != data2.getStageLevel()) {
				return data1.getStageLevel() < data2.getStageLevel() ? 1 : -1;
			}

			if (data1.getLevel() == data2.getLevel()) {
				return 0;
			}

			return data1.getLevel() <= data2.getLevel() ? 1 : -1;
		}
	}
}
