package com.wanniu.game.chat;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.wanniu.core.logfs.Out;
import com.wanniu.core.util.ClassUtils;
import com.wanniu.game.GWorld;
import com.wanniu.game.chat.command.Command;
import com.wanniu.game.chat.command.GmCommand;

/**
 * GM命令管理类.
 * 
 * @author 小流氓(zhoumingkai@qeng.cn)
 */
public class GmCommandManager {
	// 所有命令...
	private static final Map<String, GmCommand> commands = new HashMap<>();

	public static void init() {
		if (GWorld.DEBUG) {
			ClassUtils.scanPackage(GmCommand.class.getPackage().getName(), klass -> {
				if (GmCommand.class.isAssignableFrom(klass)) {
					Command command = klass.getAnnotation(Command.class);
					if (command != null) {
						for (String v : command.value()) {
							commands.put(v, (GmCommand) ClassUtils.newInstance(klass));
						}
						Out.debug("GM命令注册：", Arrays.toString(command.value()), "->", klass.getSimpleName());
					}
				}
			});
		}
	}

	public static GmCommand find(String[] content) {
		// @gm help \t帮助\n
		StringBuilder sb = new StringBuilder(64);
		if (content.length > 1 && ("?".equals(content[1]) || "help".equalsIgnoreCase(content[1]))) {
			sb.append(content[0].toLowerCase()).append(" ");
			sb.append(content[1].toLowerCase());
		} else if (content.length > 2) {
			sb.append(content[0].toLowerCase()).append(" ");
			sb.append(content[1].toLowerCase()).append(" ");
			sb.append(content[2].toLowerCase());
		}
		return commands.get(sb.toString());
	}
}