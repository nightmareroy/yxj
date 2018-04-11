package com.wanniu.game.poes;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.wanniu.core.game.entity.GEntity;
import com.wanniu.game.DBTable;
import com.wanniu.game.common.Table;

@DBTable(Table.player_shop_mall)
public final class ShopMallPO extends GEntity {
	public Map<Integer, Map<Integer, Boolean>> seenTab;
	public Map<String, Integer> dayMallItemNums;
	/** 一周购买过的物品 */
	public Map<String, Integer> weekMallItemNums;
	
	/** 一天兑换过的物品 */
	public Map<String, Integer> dayMallExchangeItemNums;
	/** 一周兑换过的物品 */
	public Map<String, Integer> weekMallExchangeItemNums;
	/** 总共兑换过的物品 */
	public Map<String, Integer> totalMallExchangeItemNums;
	
	/** 重置时间 */
	public Date resetTime;

	public ShopMallPO() {
		this.dayMallItemNums = new HashMap<String, Integer>();
		this.weekMallItemNums = new HashMap<String, Integer>();
		this.dayMallExchangeItemNums = new HashMap<String, Integer>();
		this.weekMallExchangeItemNums = new HashMap<String, Integer>();
		this.totalMallExchangeItemNums = new HashMap<String, Integer>();
	}

	public ShopMallPO(Map<Integer, Map<Integer, Boolean>> seenTab) {
		this();
		this.seenTab = seenTab;
		
	}

}
