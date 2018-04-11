package com.wanniu.game.recent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.wanniu.core.util.DateUtil;
import com.wanniu.game.area.Area;
import com.wanniu.game.common.CommonUtil;
import com.wanniu.game.common.ConstsTR;
import com.wanniu.game.daoyou.DaoYouService;
import com.wanniu.game.guild.GuildUtil;
import com.wanniu.game.player.GlobalConfig;
import com.wanniu.game.player.PlayerUtil;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.poes.GuildMemberPO;
import com.wanniu.game.poes.GuildPO;
import com.wanniu.game.poes.PlayerPO;
import com.wanniu.game.poes.RecentChatPO;
import com.wanniu.redis.PlayerPOManager;

import pomelo.area.FriendHandler;
import pomelo.area.FriendHandler.PlayerInfo;
import pomelo.area.FriendHandler.Position;

/**
 * 最近联系人管理
 * 
 * @author jjr
 *
 */
public class RecentChatMgr {
	private RecentChatPO data;
	private final int MSG_MAX = 20; // 每个人的聊天记录条数

	public RecentChatMgr(String playerId, RecentChatPO po) {
		if (null != po) {
			this.data = po;
		} else {
			this.data = new RecentChatPO();
			PlayerPOManager.put(ConstsTR.playerRecentChatTR, playerId, this.data);
		}
	}

	/**
	 * 获得缓存数据
	 * 
	 * @return
	 */
	public RecentChatPO getData() {
		return data;
	}

	/**
	 * 玩家离线处理
	 */
	public void onPlayerOffline() {
		this.checkRecentLimit(); // 存库RECENT_PLAYRS_MAX人
	}

	/**
	 * 检测最近联系人上限
	 */
	public void checkRecentLimit() {
		if (this.data.msg.size() > GlobalConfig.Social_MaxFriendNum) {
			List<Entry<String, RecentChatMsg>> list = new ArrayList<Entry<String, RecentChatMsg>>(this.data.msg.entrySet());
			list.sort((o1, o2) -> {
				return o1.getValue().recentChatTime.getTime() < o2.getValue().recentChatTime.getTime() ? 1 : -1;
			});

			// 根据时间排序后，删除长时间没有聊天的人
			for (int i = GlobalConfig.Social_MaxFriendNum; i < this.data.msg.size(); i++) {
				this.data.msg.remove(list.get(i).getKey());
			}
		}
	}

	/**
	 * 获取好友信息
	 * 
	 * @param playerId
	 * @return
	 */
	public PlayerInfo getPlayerInfo(String playerId) {
		PlayerInfo.Builder datas = PlayerInfo.newBuilder();
		Position.Builder currentPos = Position.newBuilder();
		boolean isOnline = PlayerUtil.isOnline(playerId);
		datas.setIsOnline(isOnline ? 1 : 0);

		if (isOnline) {
			WNPlayer player = PlayerUtil.findPlayer(playerId);
			if (null == player) {
				return datas.build();
			}

			datas.setGuildId(player.guildManager.getGuildId());
			datas.setGuildName(player.guildManager.getGuildName());

			datas.setName(player.getName());
			datas.setLevel(player.getLevel());
			datas.setPro(player.getPro());
			datas.setStageLevel(player.player.upLevel);
			datas.setVip(0);
			datas.setFightPower(CommonUtil.calFightPower(player.btlDataManager.allInflus));

			if (DaoYouService.getInstance().getDaoYou(playerId) != null) {
				datas.setHasAlly(1);
			}
			Area area = player.getArea();
			if (area != null) {
				currentPos.setAreaName(area.getSceneName());
				currentPos.setAreaId(area.areaId);
			}
		} else {
			PlayerPO player = PlayerUtil.getPlayerBaseData(playerId);

			if (null == player) {
				return datas.build();
			}

			datas.setName(player.name);
			datas.setLevel(player.level);
			datas.setPro(player.pro);
			datas.setStageLevel(player.upLevel);
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

			datas.setFightPower(player.fightPower);
		}

		datas.setId(playerId);
		datas.setCurrentPos(currentPos.build());
		return datas.build();
	}

	/**
	 * 获取联系人信息列表
	 * 
	 * @return
	 */
	public List<PlayerInfo> getRecentLs() {
		List<PlayerInfo> ls = new ArrayList<PlayerInfo>();
		for (String key : this.data.msg.keySet()) {
			PlayerInfo playerInfo = this.getPlayerInfo(key);
			if (null != playerInfo) {
				ls.add(playerInfo);
			}
		}
		return ls;
	}

	/**
	 * 添加聊天记录到缓存
	 * 
	 * @param friendId 好友id
	 * @param msg 聊天信息
	 */
	public void addRecentMsg(String friendId, ChatMsg msg) {
		Map<String, RecentChatMsg> ls = this.data.msg;
		if (!ls.containsKey(friendId)) {
			RecentChatMsg recentMsg = new RecentChatMsg();
			recentMsg.recentChatTime = DateUtil.format(msg.time);
			recentMsg.msgLs.add(msg);
			ls.put(friendId, recentMsg);
		} else {
			// 满了把以前的聊天删除
			if (ls.get(friendId).msgLs.size() > MSG_MAX) {
				ls.get(friendId).msgLs.remove(0);
			}

			ls.get(friendId).recentChatTime = DateUtil.format(msg.time);
			ls.get(friendId).msgLs.add(msg);
		}

		// Out.info("OOOOOOOOOOOOOOOchat,Time:->>", msg.time, "ContentLs:->>",
		// ls.get(friendId).msgLs);
	}

	/**
	 * 获取某玩家聊天记录
	 * 
	 * @param playerId 玩家id
	 * @return 聊天数组
	 */
	public List<FriendHandler.ChatMsg> getRecentMsg(String playerId) {
		List<FriendHandler.ChatMsg> ls = new ArrayList<FriendHandler.ChatMsg>();
		if (this.data.msg.size() < 0 || !this.data.msg.containsKey(playerId)) {
			return ls;
		}

		for (int i = 0; i < this.data.msg.get(playerId).msgLs.size(); i++) {
			ChatMsg msg = this.data.msg.get(playerId).msgLs.get(i);
			FriendHandler.ChatMsg.Builder buildMsg = FriendHandler.ChatMsg.newBuilder();
			buildMsg.setS2CPlayerId(msg.playerId);
			buildMsg.setS2CContent(msg.content);
			buildMsg.setS2CTime(msg.time);
			buildMsg.setS2CAcceptRid(msg.acceptRid);
			ls.add(buildMsg.build());
		}

		return ls;
	}

	/**
	 * 清除某一玩家聊天记录
	 * 
	 * @param playerId 玩家id
	 */
	public void removeRecentMsg(String playerId) {
		if (this.data.msg.size() < 0 || !this.data.msg.containsKey(playerId)) {
			return;
		}

		this.data.msg.remove(playerId);
	}

	/**
	 * 清除所有聊天记录
	 */
	public void removeAllMsg() {
		this.data.msg.clear();
	}
}
