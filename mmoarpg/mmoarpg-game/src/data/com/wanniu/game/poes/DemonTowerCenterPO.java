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

@DBTable(Table.player_demon_tower_center)
public final class DemonTowerCenterPO extends GEntity {

	public List<String> firstPlayerIds;
	public List<Date> firstPlayerDates;
	
	public List<String> fastPlayerIds;
	public List<Long> fastPlayerTimes;
//	public List<Boolean> leastPlayerRewardGot;
	
	public DemonTowerCenterPO() {
		firstPlayerIds=new LinkedList<>();
		firstPlayerDates=new LinkedList<>();
		fastPlayerIds=new LinkedList<>();
		fastPlayerTimes=new LinkedList<>();
//		leastPlayerRewardGot=new LinkedList<>();
		
		for (DropListExt dropListExt : GameData.DropLists.values()) {
			firstPlayerIds.add(null);
			firstPlayerDates.add(null);
			fastPlayerIds.add(null);
			fastPlayerTimes.add(null);
//			leastPlayerRewardGot.add(false);
		}
	}
}