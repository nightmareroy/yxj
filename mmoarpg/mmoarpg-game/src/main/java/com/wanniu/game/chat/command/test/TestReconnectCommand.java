package com.wanniu.game.chat.command.test;

import com.wanniu.game.chat.command.AbsCommand;
import com.wanniu.game.chat.command.Command;
import com.wanniu.game.player.WNPlayer;

/**
 * 测试重连命令.
 * 
 * @author 小流氓(zhoumingkai@qeng.cn)
 */
@Command("@gm test reconnect")
public class TestReconnectCommand extends AbsCommand {

	@Override
	public String help() {
		return "@gm test reconnect	测试重连命令";
	}

	@Override
	protected String exec(WNPlayer player, String... args) {
		player.getSession().close();
		return "";
	}
}