package com.wanniu.game.poes;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import com.wanniu.core.game.entity.GEntity;
import com.wanniu.game.DBField;
import com.wanniu.game.DBTable;
import com.wanniu.game.common.Table;
import com.wanniu.game.data.GameData;
import com.wanniu.game.data.ext.DropListExt;
import com.wanniu.game.farm.FarmMgr.Block;
//import com.wanniu.game.farm.FarmMgr.FarmInfo;
import com.wanniu.game.farm.FarmMgr.RecordInfo;
import com.wanniu.game.farm.FarmMgr.Seed;

@DBTable(Table.player_demon_tower)
public final class DemonTowerPO extends GEntity {
	@DBField(isPKey = true, fieldType = "varchar", size = 50)
	public String playerId;

	public int maxFloor;
	public int failedMapId;
	public int sweepCountLeft;
	
	public Date sweepEndTime;
	
	public Date firstTimeToPeak;//第一次打通当前层的时间
	
	
	public List<Long> leastTimeList;
	
//	public boolean isSweeping;

	public DemonTowerPO() {
		maxFloor=1;
		firstTimeToPeak=new Date();
		leastTimeList=new LinkedList<>();
		
	}
	public DemonTowerPO(String playerId,int currentFloor,int failedMapId,int sweepCountLeft) {
		this();
		this.playerId=playerId;
		this.maxFloor=currentFloor;
		this.failedMapId = 0;
		this.sweepCountLeft=sweepCountLeft;
//		this.isSweeping=isSweeping;
		this.sweepEndTime=null;

	}
}