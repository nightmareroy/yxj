package com.wanniu.game.poes;



import java.util.HashMap;
import java.util.Map;

import com.wanniu.core.game.entity.GEntity;
import com.wanniu.game.DBField;
import com.wanniu.game.DBTable;
import com.wanniu.game.common.Table;
import com.wanniu.game.item.po.PlayerItemPO;

@DBTable(Table.player_blood)
public final class BloodPO extends GEntity {
	@DBField(isPKey = true, fieldType = "varchar", size = 50)
	public String playerId;

	public Map<Integer, Integer> equipedMap;//key:sortId  value:bloodId

	public BloodPO() {
		playerId=null;
		equipedMap=new HashMap<>();
	}
	public BloodPO(String playerId) {
		this();
		this.playerId=playerId;
		
		
		
		
	}
}