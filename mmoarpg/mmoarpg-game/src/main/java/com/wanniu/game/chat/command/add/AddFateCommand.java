package com.wanniu.game.chat.command.add;

import com.wanniu.game.chat.command.AbsCommand;
import com.wanniu.game.chat.command.Command;
import com.wanniu.game.common.Const.GOODS_CHANGE_TYPE;
import com.wanniu.game.player.WNPlayer;

/**
 * 添加仙缘值命令.
 * 
 * @author liyue
 */
@Command("@gm add fate")
public class AddFateCommand extends AbsCommand {

	@Override
	public String help() {
		return "@gm add fate <value>	添加仙缘值命令";
	}

	@Override
	protected String exec(WNPlayer player, String... args) {
		Integer value = 1;
		if (args.length > 3) {
			value = Integer.parseInt(args[3]);
		}
		player.moneyManager.addXianYuan(value, GOODS_CHANGE_TYPE.gm);
		return "已成功添加" + value + "仙缘";
	}
}