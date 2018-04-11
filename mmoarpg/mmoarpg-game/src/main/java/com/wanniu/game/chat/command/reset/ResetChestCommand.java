package com.wanniu.game.chat.command.reset;

import com.wanniu.game.chat.command.AbsCommand;
import com.wanniu.game.chat.command.Command;
import com.wanniu.game.player.WNPlayer;

/**
 * 重置今天收入的宝箱命令.
 * 
 * @author 小流氓(zhoumingkai@qeng.cn)
 */
@Command("@gm reset chest")
public class ResetChestCommand extends AbsCommand {

	@Override
	public String help() {
		return "@gm reset chest [value]	重置宝箱计数命令";
	}

	@Override
	protected String exec(WNPlayer player, String... args) {
		Integer value = 0;
		if (args.length > 3) {
			value = Integer.parseInt(args[3]);
		}
		player.illusionManager.illusionPO.putBox(1, value);
		return "已成功重置当前" + value + "宝箱计数";
	}
}