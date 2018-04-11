package com.wanniu.game.chat.command.reset;

import com.wanniu.game.chat.command.AbsCommand;
import com.wanniu.game.chat.command.Command;
import com.wanniu.game.player.WNPlayer;

/**
 * 重置今日任务
 * 
 * @author 李玥
 */
@Command("@gm reset task")
public class ResetTaskCommand extends AbsCommand {

	@Override
	public String help() {
		return "@gm reset task	重置今日任务";
	}

	@Override
	protected String exec(WNPlayer player, String... args) {
		player.taskManager.refreshNewDay();
		return "已成功重置今日任务";
	}
}