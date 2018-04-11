package com.wanniu.game.chat.command.reset;

import com.wanniu.game.chat.command.AbsCommand;
import com.wanniu.game.chat.command.Command;
import com.wanniu.game.common.Const.GOODS_CHANGE_TYPE;
import com.wanniu.game.player.WNPlayer;

/**
 * 重置银两命令.
 * 
 * @author 小流氓(zhoumingkai@qeng.cn)
 */
@Command("@gm reset gold")
public class ResetGoldCommand extends AbsCommand {

	@Override
	public String help() {
		return "@gm reset gold <value>	重置银两命令";
	}

	@Override
	protected String exec(WNPlayer player, String... args) {
		Integer value = 0;
		if (args.length > 3) {
			value = Integer.parseInt(args[3]);
		}

		int gold = player.moneyManager.getGold();
		if (gold > value) {
			player.moneyManager.costGold(gold - value, GOODS_CHANGE_TYPE.gm);
		} else {
			player.moneyManager.addGold(value - gold, GOODS_CHANGE_TYPE.gm);
		}
		return "已成功重置到" + value + "银两";
	}
}