package com.wanniu.game.guild.guidDepot;

import java.util.HashMap;
import java.util.Map;

import com.wanniu.game.item.po.PlayerItemPO;

public class GuildBagItem {
	public int bagGridCount;
	public Map<Integer, PlayerItemPO> bagGrids;
	public int bagTotalCount;

	public GuildBagItem() {
		bagGrids = new HashMap<Integer, PlayerItemPO>();
	}
}
