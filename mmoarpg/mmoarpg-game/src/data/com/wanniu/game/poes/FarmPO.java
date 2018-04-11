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
import com.wanniu.game.data.GetLandCO;
import com.wanniu.game.farm.FarmMgr;
import com.wanniu.game.farm.FarmMgr.Block;
import com.wanniu.game.farm.FarmMgr.Product;
//import com.wanniu.game.farm.FarmMgr.FarmInfo;
import com.wanniu.game.farm.FarmMgr.RecordInfo;
import com.wanniu.game.farm.FarmMgr.Seed;

@DBTable(Table.player_farm)
public final class FarmPO extends GEntity {
	@DBField(isPKey = true, fieldType = "varchar", size = 50)
	public String playerId;
//	public String playerName;
//	public int lv;
//	public boolean opened;
	public int exp;
	public int lv;
	
	public Map<Integer,Block> blockMap;// 果园田块信息
//	public Map<Integer, Seed> seedMap;
//	public Map<Integer, Product> productMap;
	public LinkedList<RecordInfo> recordLs; // 果园日志
//	public int curSeedNum;// 当前种植数量
//	public Date lastExpGetDate;//上次获取仙缘值的日期
//	public int xianyuan_today;//本日获取的仙缘值
	
//	public int xianyuan_total;//累计获得的仙缘值
	
	public int stealCountToday;//本日偷取好友次数
	
	public Map<Integer, Integer> shopToday;//今日购买
	

	public FarmPO() {
		this.blockMap=new HashMap<Integer,Block>();
		for (GetLandCO getLandCO : GameData.GetLands.values()) {
			Block block=new Block(getLandCO.landNum);
			blockMap.put(getLandCO.landNum, block);
		}
//		this.seedMap=new HashMap<Integer,Seed>();
		this.recordLs=new LinkedList<RecordInfo>();
//		this.lastExpGetDate=new Date();
//		this.xianyuan_today=0;
//		this.xianyuan_total=0;
		this.shopToday=new HashMap<>();
		
	}
	public FarmPO(String playerId) {
		this();
		this.playerId=playerId;
		
		
		
		
	}
}