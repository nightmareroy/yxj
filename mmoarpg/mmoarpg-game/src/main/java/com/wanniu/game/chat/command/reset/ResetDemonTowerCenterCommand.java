package com.wanniu.game.chat.command.reset;

import com.wanniu.game.activity.DemonTowerService;
import com.wanniu.game.chat.command.AbsCommand;
import com.wanniu.game.chat.command.Command;
import com.wanniu.game.player.WNPlayer;

/**
 * 重置镇妖塔中心数据.
 * 
 * @author liyue
 */
@Command("@gm reset demontowercenter")
public class ResetDemonTowerCenterCommand extends AbsCommand {

	@Override
	public String help() {
		return "重置镇妖塔中心数据";
	}

	@Override
	protected String exec(WNPlayer player, String... args) {
		DemonTowerService.getInstance().clearAllData();
		return "已成功重置镇妖塔中心数据";
	}
}