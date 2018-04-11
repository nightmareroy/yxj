package com.wanniu.game.chat.command;

import com.wanniu.game.chat.GMChatUtil.GMChatResult;
import com.wanniu.game.player.WNPlayer;

public interface GmCommand {
	/**
	 * @return 帮助提示
	 */
	public String help();

	/**
	 * 执行逻辑
	 */
	public GMChatResult call(WNPlayer player, String... args);
}