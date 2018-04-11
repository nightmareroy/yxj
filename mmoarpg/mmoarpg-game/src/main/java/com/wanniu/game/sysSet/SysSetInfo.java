package com.wanniu.game.sysSet;

import com.wanniu.game.player.WNPlayer;

import pomelo.area.SysSetHandler.SetData;

public class SysSetInfo {

	private final WNPlayer player;

	public SysSetInfo(WNPlayer player) {
		this.player = player;
	}

	public boolean isPermission(SysSetFlag setFlag) {
		return (player.playerAttachPO.sysSet & setFlag.getValue()) != 0;
	}

	// 是否接收邮件
	public boolean canRecvMail() {
		return (player.playerAttachPO.sysSet & SysSetFlag.recvMailSet.getValue()) != 0;
	}

	// 是否允许组队邀请
	public boolean canTeamInvite() {
		return (player.playerAttachPO.sysSet & SysSetFlag.teamInviteSet.getValue()) != 0;
	}

	// 是否允许接收陌生人消息
	public boolean canRecvStrangerMsg() {
		return (player.playerAttachPO.sysSet & SysSetFlag.recvStrangerMsgSet.getValue()) != 0;
	}

	// 是否允许接收好友申请
	public boolean canRecvAddFriend() {
		return (player.playerAttachPO.sysSet & SysSetFlag.recvAddFriendSet.getValue()) != 0;
	}

	// 更改系统设置
	public void changeSet(final SetData setData) {
		int oldFlag = player.playerAttachPO.sysSet;
		int newFlag = converToFlag(setData);
		if (newFlag == oldFlag) {
			return;
		}
		player.playerAttachPO.sysSet = (newFlag);
		return;
	}

	public int converToFlag(final SetData setData) {
		int ret = 0;
		if (setData.getRecvMailSet() > 0) {
			ret |= SysSetFlag.recvMailSet.getValue();
		}
		if (setData.getTeamInviteSet() > 0) {
			ret |= SysSetFlag.teamInviteSet.getValue();
		}
		if (setData.getRecvStrangerMsgSet() > 0) {
			ret |= SysSetFlag.recvStrangerMsgSet.getValue();
		}
		if (setData.getRecvAddFriendSet() > 0) {
			ret |= SysSetFlag.recvAddFriendSet.getValue();
		}
		return ret;
	}

	public SetData getSetData() {
		SetData.Builder ret = SetData.newBuilder();
		ret.setRecvMailSet(this.canRecvMail() ? 1 : 0);
		ret.setTeamInviteSet(this.canTeamInvite() ? 1 : 0);
		ret.setRecvStrangerMsgSet(this.canRecvStrangerMsg() ? 1 : 0);
		ret.setRecvAddFriendSet(this.canRecvAddFriend() ? 1 : 0);
		return ret.build();
	}

	public pomelo.player.PlayerOuterClass.SetData toJson4Payload() {
		pomelo.player.PlayerOuterClass.SetData.Builder data = pomelo.player.PlayerOuterClass.SetData.newBuilder();
		data.setRecvMailSet(this.canRecvMail() ? 1 : 0);
		data.setTeamInviteSet(this.canTeamInvite() ? 1 : 0);
		data.setRecvStrangerMsgSet(this.canRecvStrangerMsg() ? 1 : 0);
		data.setRecvAddFriendSet(this.canRecvAddFriend() ? 1 : 0);
		return data.build();
	};
}
