package com.wanniu.game.poes;

import java.util.List;
import java.util.Map;

import com.wanniu.core.game.entity.GEntity;
import com.wanniu.game.DBTable;
import com.wanniu.game.common.Table;

@DBTable(Table.player_func_open)
public class FunctionOpenPO extends GEntity {

	public Map<String,Integer> openMap;//已开启功能  {"Wing":1,"Solo":0}
	public Map<String,Integer> playMap;//点开过的功能
	/**已开放和已领取的功能奖励<guideId */
	public List<Integer> functionAwards;
	
	public FunctionOpenPO() {
		
	}
}
