package com.wanniu.game.chat.command.add;

import java.util.ArrayList;
import java.util.List;

import com.wanniu.game.area.MonsterUnit;
import com.wanniu.game.chat.command.AbsCommand;
import com.wanniu.game.chat.command.Command;
import com.wanniu.game.common.Const;
import com.wanniu.game.data.base.MonsterBase;
import com.wanniu.game.monster.MonsterConfig;
import com.wanniu.game.player.WNPlayer;

/**
 * 添加怪物命令.
 * 
 * @author 小流氓(zhoumingkai@qeng.cn)
 */
@Command("@gm add monster")
public class AddMonsterCommand extends AbsCommand {

	@Override
	public String help() {
		return "@gm add monster <id>	添加怪物命令";
	}

	@Override
	protected String exec(WNPlayer player, String... args) {
		player.getArea().syncPlayerHistoryData(player);

		Integer monsterId = Integer.parseInt(args[3]);
		MonsterBase prop = MonsterConfig.getInstance().get(monsterId);
		List<MonsterUnit> data = new ArrayList<>();
		MonsterUnit unit = new MonsterUnit();
		unit.id = monsterId;
		unit.x = (int) player.playerTempData.x;
		unit.y = (int) player.playerTempData.y;
		unit.force = Const.AreaForce.MONSTER.value;
		unit.autoGuard = true;
		unit.shareType = prop.shareType;
		data.add(unit);
		player.getArea().addUnitsToArea(data);
		return "";
	}
}