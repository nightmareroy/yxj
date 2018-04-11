package com.wanniu.game.chat.command.reset;

import com.wanniu.game.chat.command.AbsCommand;
import com.wanniu.game.chat.command.Command;
import com.wanniu.game.common.Const.GOODS_CHANGE_TYPE;
import com.wanniu.game.player.WNPlayer;

/**
 * 重置充值元宝命令.
 * 
 * @author 小流氓(zhoumingkai@qeng.cn)
 */
@Command("@gm reset rmb")
public class ResetRmbCommand extends AbsCommand {

	@Override
	public String help() {
		return "@gm reset rmb <value>	重置充值元宝命令";
	}

	@Override
	protected String exec(WNPlayer player, String... args) {
		Integer value = 0;
		if (args.length > 3) {
			value = Integer.parseInt(args[3]);
		}

		int diamond = player.moneyManager.getDiamond();
		if (diamond > value) {
			player.moneyManager.costDiamond(diamond - value, GOODS_CHANGE_TYPE.gm);
		} else {
			player.moneyManager.addDiamond(value - diamond, GOODS_CHANGE_TYPE.gm);
		}
		return "已成功重置" + value + "充值元宝";
	}
}