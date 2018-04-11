package com.wanniu.game.chat.command.reset;

import com.wanniu.game.chat.command.AbsCommand;
import com.wanniu.game.chat.command.Command;
import com.wanniu.game.common.Const.GOODS_CHANGE_TYPE;
import com.wanniu.game.player.WNPlayer;

/**
 * 重置绑定元宝命令.
 * 
 * @author 小流氓(zhoumingkai@qeng.cn)
 */
@Command("@gm reset money")
public class ResetMoneyCommand extends AbsCommand {

	@Override
	public String help() {
		return "@gm reset money <value> 重置绑定元宝命令";
	}

	@Override
	protected String exec(WNPlayer player, String... args) {
		Integer value = 0;
		if (args.length > 3) {
			value = Integer.parseInt(args[3]);
		}
		int ticket = player.moneyManager.getTicket();
		if (ticket > value) {
			player.moneyManager.costTicket(ticket - value, GOODS_CHANGE_TYPE.gm);
		} else {
			player.moneyManager.addTicket(value - ticket, GOODS_CHANGE_TYPE.gm);
		}
		return "已成功重置" + value + "绑定元宝";
	}
}