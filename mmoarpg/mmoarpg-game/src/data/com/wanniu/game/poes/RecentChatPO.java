package com.wanniu.game.poes;

import java.util.HashMap;
import java.util.Map;

import com.wanniu.core.game.entity.GEntity;
import com.wanniu.game.DBTable;
import com.wanniu.game.common.Table;
import com.wanniu.game.recent.RecentChatMsg;

@DBTable(Table.player_recent_chat)
public class RecentChatPO extends GEntity {
	public Map<String, RecentChatMsg> msg; // 与好友聊天记录

	public RecentChatPO() {
		msg = new HashMap<String, RecentChatMsg>();
	}
}
