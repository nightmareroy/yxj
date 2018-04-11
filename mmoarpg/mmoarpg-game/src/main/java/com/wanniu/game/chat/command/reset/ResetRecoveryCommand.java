package com.wanniu.game.chat.command.reset;

import com.wanniu.game.chat.command.AbsCommand;
import com.wanniu.game.chat.command.Command;
import com.wanniu.game.player.WNPlayer;

/**
 * 重置资源找回命令.
 * 
 * @author 小流氓(zhoumingkai@qeng.cn)
 */
@Command("@gm reset recovery")
public class ResetRecoveryCommand extends AbsCommand {

	@Override
	public String help() {
		return "@gm reset recovery [day]	重置资源找回命令";
	}

	@Override
	protected String exec(WNPlayer player, String... args) {
		Integer day = 1;
		if (args.length > 3) {
			day = Math.min(Integer.parseInt(args[3]), 3);
		}
		player.activityManager.gmRecovered(day);
		return "资源找回已修正到" + day + "天前...";
	}
}