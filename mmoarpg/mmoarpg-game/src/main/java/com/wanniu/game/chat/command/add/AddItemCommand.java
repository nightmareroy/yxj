package com.wanniu.game.chat.command.add;

import com.wanniu.game.chat.command.AbsCommand;
import com.wanniu.game.chat.command.Command;
import com.wanniu.game.common.Const;
import com.wanniu.game.data.base.DItemEquipBase;
import com.wanniu.game.item.ItemConfig;
import com.wanniu.game.player.WNPlayer;

/**
 * 添加道具命令.
 * 
 * @author 小流氓(zhoumingkai@qeng.cn)
 */
@Command("@gm add item")
public class AddItemCommand extends AbsCommand {

	@Override
	public String help() {
		return "@gm add item <code> [num]	添加道具命令";
	}

	@Override
	protected String exec(WNPlayer player, String... args) {
		String itemCode = args[3];
		Integer value = 1;
		if (args.length > 4) {
			value = Integer.parseInt(args[4]);
		}

		// 物品编号不存在，尝试名字...
		if (ItemConfig.getInstance().getItemProp(itemCode) == null) {
			DItemEquipBase t = ItemConfig.getInstance().getItemPropByName(itemCode);
			if (t == null) {
				return "你输入的道具编号【" + itemCode + "】不存在";
			} else {
				itemCode = t.code;
			}
		}

		if (!player.bag.testAddCodeItem(itemCode, value)) {
			return "您的背包装不下啊...";
		}

		player.bag.addCodeItem(itemCode, value, Const.ForceType.UN_BIND, Const.GOODS_CHANGE_TYPE.gm);
		return "OK";
	}
}