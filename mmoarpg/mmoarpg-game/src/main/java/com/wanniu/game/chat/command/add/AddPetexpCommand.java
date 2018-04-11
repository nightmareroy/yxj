package com.wanniu.game.chat.command.add;

import com.wanniu.game.chat.command.AbsCommand;
import com.wanniu.game.chat.command.Command;
import com.wanniu.game.player.WNPlayer;

/**
 * 添加宠物经验命令.
 * 
 * @author 小流氓(zhoumingkai@qeng.cn)
 */
@Command("@gm add petexp")
public class AddPetexpCommand extends AbsCommand {

	@Override
	public String help() {
		return "@gm add petexp <petId> <exp>	添加宠物经验命令";
	}

	@Override
	protected String exec(WNPlayer player, String... args) {
		Integer petId = 1;
		if (args.length > 3) {
			petId = Integer.parseInt(args[3]);
		}
		Integer exp = 1;
		if (args.length > 4) {
			exp = Integer.parseInt(args[4]);
		}

		player.petNewManager.addExp(petId, exp);
		return "已成功添加" + exp + "经验";
	}
}