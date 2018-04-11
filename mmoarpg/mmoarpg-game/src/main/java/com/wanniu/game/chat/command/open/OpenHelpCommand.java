package com.wanniu.game.chat.command.open;

import com.wanniu.game.chat.command.AbsCommand;
import com.wanniu.game.chat.command.Command;

/**
 * 开通系列帮助.
 *
 * @author 小流氓(zhoumingkai@qeng.cn)
 */
@Command({ "@gm open ?", "@gm open help" })
public class OpenHelpCommand extends AbsCommand {

	@Override
	public String help() {
		return "@gm open [月卡|尊享卡]";
	}
}