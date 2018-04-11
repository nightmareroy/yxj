package com.wanniu.game.chat.command.test;

import com.wanniu.csharp.CSharpClient;
import com.wanniu.game.chat.command.AbsCommand;
import com.wanniu.game.chat.command.Command;
import com.wanniu.game.player.WNPlayer;

/**
 * 测试断开战斗服命令.
 * 
 * @author 小流氓(zhoumingkai@qeng.cn)
 */
@Command("@gm test battle")
public class TestBattleCommand extends AbsCommand {

	@Override
	public String help() {
		return "@gm test battle	 测试断开战斗服命令";
	}

	@Override
	protected String exec(WNPlayer player, String... args) {
		CSharpClient.getInstance().gmTestClose();
		return "";
	}
}