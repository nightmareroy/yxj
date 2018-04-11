package com.wanniu.game.chat.command.test;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import com.wanniu.core.util.FileUtil;
import com.wanniu.game.chat.command.AbsCommand;
import com.wanniu.game.chat.command.Command;
import com.wanniu.game.common.Const.GOODS_CHANGE_TYPE;
import com.wanniu.game.data.base.DEquipBase;
import com.wanniu.game.data.base.DItemBase;
import com.wanniu.game.item.ItemConfig;
import com.wanniu.game.player.WNPlayer;

/**
 * 生成后台所需游戏区的功能命令.
 * 
 * @author 小流氓(zhoumingkai@qeng.cn)
 */
@Command("@gm test automation")
public class TestAutomationCommand extends AbsCommand {

	@Override
	public String help() {
		return "@gm test automation		生成后台所需的命令";
	}

	@Override
	protected String exec(WNPlayer player, String... args) {
		this.genItemTemplateLog();
		this.genFuncTemplateLog();

		return "OK";
	}

	private void genFuncTemplateLog() {
		Set<Integer> test = new HashSet<>();
		StringBuilder sb = new StringBuilder(2048);
		for (GOODS_CHANGE_TYPE type : GOODS_CHANGE_TYPE.values()) {
			if (test.contains(type.value)) {
				throw new RuntimeException("来源有重复的ID：" + type.value);
			}
			test.add(type.value);
			sb.append("i18n.func.code." + type.value + "=" + type.getDesc() + "\n");
		}
		FileUtil.write(new File("E:\\mmoarpg\\platform-gm\\src\\main\\resources\\template-func.properties"), sb.toString());
	}

	private void genItemTemplateLog() {
		StringBuilder sb = new StringBuilder(2048);
		for (DItemBase it : ItemConfig.getInstance().getItemTemplates().values()) {
			sb.append("i18n.item." + it.code + "=" + it.name + "\n");
		}
		for (DEquipBase it : ItemConfig.getInstance().getEquipTemplates().values()) {
			sb.append("i18n.item." + it.code + "=" + it.name + "\n");
		}
		FileUtil.write(new File("E:\\mmoarpg\\platform-gm\\src\\main\\resources\\template-item.properties"), sb.toString());
	}
}