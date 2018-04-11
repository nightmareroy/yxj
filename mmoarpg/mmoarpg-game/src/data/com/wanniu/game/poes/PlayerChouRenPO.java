package com.wanniu.game.poes;

import java.util.HashMap;
import java.util.Map;

import com.wanniu.core.game.entity.GEntity;
import com.wanniu.game.DBTable;
import com.wanniu.game.common.Table;
import com.wanniu.game.friend.ChouRenData;

@DBTable(Table.player_chouren)
public class PlayerChouRenPO extends GEntity{

	public Map<String, ChouRenData> chouRens;
	
	public PlayerChouRenPO(){
		chouRens = new HashMap<String,ChouRenData>();
	}
}
