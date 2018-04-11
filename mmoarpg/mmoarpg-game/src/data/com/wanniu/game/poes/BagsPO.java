package com.wanniu.game.poes;

import com.wanniu.core.game.entity.GEntity;
import com.wanniu.game.DBField;
import com.wanniu.game.DBTable;
import com.wanniu.game.bag.BagPO;
import com.wanniu.game.common.Table;

/**
 * player_bags[背包、仓库]
 * 
 * @author Yangzz
 *
 */
@DBTable(Table.player_bags)
public class BagsPO extends GEntity {


	@DBField(fieldType = "mediumblob")
	public BagPO bagData;

	@DBField(fieldType = "mediumblob")
	public BagPO wareHouseData;

	@DBField(fieldType = "mediumblob")
	public BagPO recycleData;

	public BagsPO() {
	}

	public BagsPO(BagPO bagPO, BagPO wareHouseData, BagPO recycleData) {
		this.bagData = bagPO;
		this.wareHouseData = wareHouseData;
		this.recycleData = recycleData;
	}
}
