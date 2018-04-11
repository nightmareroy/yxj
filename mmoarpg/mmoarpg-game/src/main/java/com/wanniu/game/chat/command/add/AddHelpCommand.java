package com.wanniu.game.chat.command.add;

import com.wanniu.game.chat.command.AbsCommand;
import com.wanniu.game.chat.command.Command;

/**
 * 添加帮助命令.
 * 
 * @author 小流氓(zhoumingkai@qeng.cn)
 */
@Command({ "@gm add help", "@gm add ?" })
public class AddHelpCommand extends AbsCommand {

	@Override
	public String help() {
		return "\n" //
				+ "@gm add rmb <value>			添加充值元宝命令\n" //
				+ "@gm add money <value>		添加绑定元宝命令\n"//
				+ "@gm add gold <value>			添加银两命令\n"//
				+ "@gm add monster <id>			添加怪物命令\n"//
				+ "@gm add item <code> [num]	添加道具命令\n"//
				+ "@gm add petexp <petId> [exp]	添加宠物经验命令\n"//
				+ "@gm add auction 				添加竞拍物品命令\n"//
				+ "@gm add level <value>		添加等级命令\n"//

		//
		;
	}
}