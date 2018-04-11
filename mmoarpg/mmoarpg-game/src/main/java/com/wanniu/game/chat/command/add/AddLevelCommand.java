package com.wanniu.game.chat.command.add;

import com.wanniu.game.chat.command.AbsCommand;
import com.wanniu.game.chat.command.Command;
import com.wanniu.game.player.GlobalConfig;
import com.wanniu.game.player.WNPlayer;

/**
 * 添加等级命令.
 * 
 * @author 小流氓(zhoumingkai@qeng.cn)
 */
@Command("@gm add level")
public class AddLevelCommand extends AbsCommand {

	@Override
	public String help() {
		return "@gm add level <value>	添加等级命令";
	}

	@Override
	protected String exec(WNPlayer player, String... args) {
		Integer value = 1;
		if (args.length > 3) {
			value = Integer.parseInt(args[3]);
		}
		player.baseDataManager.upgrade(Math.min(GlobalConfig.Role_LevelLimit, player.getLevel() + value), 0);
		return "升级成功";
	}
}