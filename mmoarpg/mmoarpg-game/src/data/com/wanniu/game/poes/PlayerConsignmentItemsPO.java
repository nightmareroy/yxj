package com.wanniu.game.poes;

import com.wanniu.core.game.entity.GEntity;
import com.wanniu.game.DBTable;
import com.wanniu.game.common.Table;

@DBTable(Table.player_consignment)
public class PlayerConsignmentItemsPO extends GEntity {
	
//	public Map<String, ConsignmentItemData> local;
	
	public int buyFirstConsignItem;
	
	/**当日寄卖的数量*/
//	public int todaySellCount;

	public PlayerConsignmentItemsPO() {
		
	}
}
