package com.wanniu.game.chat.command.add;

import com.wanniu.game.chat.command.AbsCommand;
import com.wanniu.game.chat.command.Command;
import com.wanniu.game.common.Const.GOODS_CHANGE_TYPE;
import com.wanniu.game.player.WNPlayer;

/**
 * 添加充值元宝命令.
 * 
 * @author 小流氓(zhoumingkai@qeng.cn)
 */
@Command("@gm add rmb")
public class AddRmbCommand extends AbsCommand {

	@Override
	public String help() {
		return "@gm add rmb <value>	添加充值元宝命令";
	}

	@Override
	protected String exec(WNPlayer player, String... args) {
		Integer value = 1;
		if (args.length > 3) {
			value = Integer.parseInt(args[3]);
		}
		player.moneyManager.addDiamond(value, GOODS_CHANGE_TYPE.gm);
		return "已成功添加" + value + "充值元宝";
	}
}