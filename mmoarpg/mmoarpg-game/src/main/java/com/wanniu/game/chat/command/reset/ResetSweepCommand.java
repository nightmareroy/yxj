package com.wanniu.game.chat.command.reset;

import com.wanniu.game.chat.command.AbsCommand;
import com.wanniu.game.chat.command.Command;
import com.wanniu.game.player.WNPlayer;

/**
 * 重置扫荡次数命令.
 * 
 * @author 小流氓(zhoumingkai@qeng.cn)
 */
@Command("@gm reset sweep")
public class ResetSweepCommand extends AbsCommand {

	@Override
	public String help() {
		return "@gm reset sweep 	重置扫荡次数命令";
	}

	@Override
	protected String exec(WNPlayer player, String... args) {
		player.demonTowerManager.UpdateSweepCount();
		return "已成功重置扫荡次数";
	}
}