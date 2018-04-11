package com.wanniu.game.sysSet;

public enum SysSetFlag {

	recvMailSet(1 << 0), teamInviteSet(1 << 1), recvStrangerMsgSet(1 << 2), recvAddFriendSet(1 << 3);

	private int value;

	private SysSetFlag(int value) {
		this.value = value;
	}

	public int getValue() {
		return this.value;
	}
}
