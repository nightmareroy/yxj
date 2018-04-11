package com.wanniu.game.chat.command;

@Command({ "@gm help", "@gm ?" })
public class HelpCommand extends AbsCommand {

	@Override
	public String help() {
		return "\n" //
				+ "@gm add ?      添加系列命令\n" //
				+ "@gm reset ?    重置系列命令\n"//
				+ "@gm open ?     开启系列命令\n"//

		//
		;
	}
}