package com.wanniu.game.recent;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RecentChatMsg {
	public Date recentChatTime;// 最近一次聊天时间
	public Boolean isRead;// 是否已读取
	public List<ChatMsg> msgLs;// 聊天记录

	public RecentChatMsg() {
		recentChatTime = new Date(0);
		msgLs = new ArrayList<ChatMsg>();
	}

}
