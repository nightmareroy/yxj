package com.wanniu.game.poes;

import com.wanniu.core.game.entity.GEntity;
import com.wanniu.game.DBTable;
import com.wanniu.game.common.Table;

@DBTable(Table.player_pk_data)
public class PlayerPKDataPO extends GEntity {
	
	public int pkModel;
	public int historyPkModel;
	public int pkValue;
    public int pkLevel;
    
    public PlayerPKDataPO() {
    	
    }
}
