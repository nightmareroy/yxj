package com.wanniu.game.poes;

import java.util.HashMap;
import java.util.Map;

import com.wanniu.core.game.entity.GEntity;
import com.wanniu.game.DBTable;
import com.wanniu.game.common.Table;

@DBTable(Table.player_pets)
public class PlayerPetsNewPO extends GEntity {

	public Map<Integer,PetNewPO> pets = new HashMap<Integer, PetNewPO>();
	public int fightPetId;
	//0:主动,1:被动,2:跟随 默认的1
	public int pkModel = 1;
	
	public PlayerPetsNewPO() {
		
	}
}
