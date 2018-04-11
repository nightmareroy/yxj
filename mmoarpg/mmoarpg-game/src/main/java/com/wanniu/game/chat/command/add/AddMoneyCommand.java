package com.wanniu.game.chat.command.add;

import com.wanniu.game.chat.command.AbsCommand;
import com.wanniu.game.chat.command.Command;
import com.wanniu.game.common.Const.GOODS_CHANGE_TYPE;
import com.wanniu.game.player.WNPlayer;

/**
 * 添加绑定元宝命令.
 * 
 * @author 小流氓(zhoumingkai@qeng.cn)
 */
@Command("@gm add money")
public class AddMoneyCommand extends AbsCommand {

	@Override
	public String help() {
		return "@gm add money <value>	添加绑定元宝命令";
	}

	@Override
	protected String exec(WNPlayer player, String... args) {
		Integer value = 1;
		if (args.length > 3) {
			value = Integer.parseInt(args[3]);
		}
		player.moneyManager.addTicket(value, GOODS_CHANGE_TYPE.gm);
		return "已成功添加" + value + "绑定元宝";
	}
}