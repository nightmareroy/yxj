package com.wanniu.game.chat.command.test;

import com.wanniu.game.chat.command.AbsCommand;
import com.wanniu.game.chat.command.Command;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.poes.AchievementDataPO.HolyArmour;

/**
 * 测试激活元始圣甲命令.
 * 
 * @author 小流氓(zhoumingkai@qeng.cn)
 */
@Command("@gm test armour")
public class TestArmourCommand extends AbsCommand {

	@Override
	public String help() {
		return "@gm test armour <id>	 测试激活元始圣甲命令";
	}

	@Override
	protected String exec(WNPlayer player, String... args) {
		Integer holyArmourId = Integer.parseInt(args[3]);
		HolyArmour holyArmour = player.achievementManager.achievementDataPO.holyArmourMap.get(holyArmourId);
		if (holyArmour == null) {
			return "ID未找到";
		}
		if (holyArmour.states == 1) {
			holyArmour.states = 2;
		}
		player.achievementManager.activateHolyArmour(holyArmourId);
		return "OK";
	}
}