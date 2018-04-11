package com.wanniu.game.chat.command.reset;

import com.wanniu.game.chat.command.AbsCommand;
import com.wanniu.game.chat.command.Command;
import com.wanniu.game.player.WNPlayer;

/**
 * 重置名字命令.
 * 
 * @author 小流氓(zhoumingkai@qeng.cn)
 */
@Command("@gm reset name")
public class ResetNameCommand extends AbsCommand {

	@Override
	public String help() {
		return "@gm reset name <pname>	 重置角色名字命令";
	}

	@Override
	protected String exec(WNPlayer player, String... args) {
		// TODO 费玲，考虑一下

		return "已成功重置角色名字...";
	}
}