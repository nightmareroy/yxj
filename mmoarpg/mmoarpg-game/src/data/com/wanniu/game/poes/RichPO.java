package com.wanniu.game.poes;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.wanniu.core.game.entity.GEntity;
import com.wanniu.game.DBTable;
import com.wanniu.game.common.Table;
import com.wanniu.game.data.GameData;
import com.wanniu.game.data.TurnRewardCO;



@DBTable(Table.player_rich)
public class RichPO extends GEntity {
//	public int finishedTurn;
	public int currentStep;
	public int freeCount;
	
	public int currentTurn;
	
	public Map<Integer,Integer> turnStatesMap;//回合奖励状态  0未完成 1已完成 2已领取
	
	public RichPO() {
		reset(-1);
	}
	
	public void reset(int newTurn) {
		turnStatesMap=new HashMap<>();
		for (TurnRewardCO turnRewardCO : GameData.TurnRewards.values()) {
			turnStatesMap.put(turnRewardCO.sort, 0);
		}
		currentStep=1;
		freeCount=0;
		
		currentTurn=newTurn;
	}
}
