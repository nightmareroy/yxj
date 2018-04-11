package com.wanniu.game.chat.command.reset;

import com.wanniu.game.chat.command.AbsCommand;
import com.wanniu.game.chat.command.Command;

/**
 * 重置系列.
 * 
 * @author 小流氓(zhoumingkai@qeng.cn)
 */
@Command({ "@gm reset help", "@gm reset ?" })
public class ResetHelpCommand extends AbsCommand {

	@Override
	public String help() {
		return "\n" //
				+ "@gm reset rmb <value>		重置充值元宝命令\n" //
				+ "@gm reset money <value>		重置绑定元宝命令\n"//
				+ "@gm reset gold <value>		重置银两命令\n"//
				+ "@gm reset recovery [day]		重置资源找回命令\n"//
				+ "@gm reset sweep 				重置扫荡次数命令\n"//
				+ "@gm reset chest [value]		重置宝箱计数命令\n"//
				+ "@gm reset task				重置今日任务命令\n"//

		//
		;
	}
}