package com.wanniu.game.chat.command;

import com.wanniu.game.chat.GMChatUtil.GMChatResult;
import com.wanniu.game.player.WNPlayer;

/**
 * 抽象的处理方案.
 * 
 * @author 小流氓(zhoumingkai@qeng.cn)
 */
public abstract class AbsCommand implements GmCommand {

	@Override
	public GMChatResult call(WNPlayer player, String... args) {
		if (args.length > 3 && ("?".equals(args[3]) || "help".equalsIgnoreCase(args[3]))) {
			return new GMChatResult(true, this.help());
		}
		String result = null;
		try {
			result = this.exec(player, args);
		} catch (Exception e) {
			result = this.help() + "执行时发生了异常情况\n异常信息：" + e.getMessage() + "\n";
		}
		return new GMChatResult(true, result);
	}

	protected String exec(WNPlayer player, String... args) {
		return this.help();
	}
}