package com.wanniu.game.chat.command.reset;

import com.wanniu.game.chat.command.AbsCommand;
import com.wanniu.game.chat.command.Command;
import com.wanniu.game.common.Const.GOODS_CHANGE_TYPE;
import com.wanniu.game.player.WNPlayer;

/**
 * 重置仙缘值命令.
 * 
 * @author liyue
 */
@Command("@gm reset fate")
public class ResetFateCommand extends AbsCommand {

	@Override
	public String help() {
		return "@gm reset fate <value>	重置仙缘命令";
	}

	@Override
	protected String exec(WNPlayer player, String... args) {
		Integer value = 0;
		if (args.length > 3) {
			value = Integer.parseInt(args[3]);
		}

		int xianyuan = player.moneyManager.getXianYuan();
		if (xianyuan > value) {
			player.moneyManager.costXianYuan(xianyuan - value, GOODS_CHANGE_TYPE.gm);
		} else {
			player.moneyManager.addXianYuan(value - xianyuan, GOODS_CHANGE_TYPE.gm);
		}
		return "已成功重置" + value + "仙缘";
	}
}