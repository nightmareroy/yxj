package com.wanniu.game.poes;

import java.util.ArrayList;
import java.util.List;

import com.wanniu.core.game.entity.GEntity;
import com.wanniu.game.DBTable;
import com.wanniu.game.common.Table;
import com.wanniu.game.player.GlobalConfig;

@DBTable(Table.player_hookset)
public class HookSetPO extends GEntity {

	public int hpPercent; // 生命低于%
	public int mpPercent; // 法力低于% (废弃)
	public String hpItemCode; // 生命药剂
	public String mpItemCode; // 法力药剂 (废弃)
	public int pkSet; // 防PK设置 0-自动反击 1-自动逃走
	public List<Integer> meltQcolor; // 熔炼装备颜色

	public int autoBuyHpItem;
	public int autoBuyMpItem;
	public int fieldMaphook; // 野外全图挂机 0 不挂 1 挂
	public int areaMaphook; // 其他场景全图挂机 0 不挂 1 挂

	public HookSetPO() {
		
	}
	
	public HookSetPO(String playerId) {
		hpPercent = GlobalConfig.Auto_HP_Percent;
		hpItemCode = GlobalConfig.Auto_HP_Item;
		pkSet = GlobalConfig.Auto_PK_Reaction;
		meltQcolor = new ArrayList<Integer>();
		meltQcolor.add(GlobalConfig.Auto_Eqip_Qcolor);
		autoBuyHpItem = 1;
		fieldMaphook = 0;
		areaMaphook = 0;
	}

}