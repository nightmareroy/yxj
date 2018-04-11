package com.wanniu.game.item.po;

import java.io.Serializable;
import java.util.Date;

/**
 * player_items 玩家物品实体类 Fri Jan 13 16:06:37 CST 2017 Yangzz
 */
public class PlayerItemPO implements Serializable {

	private static final long serialVersionUID = 1L;

	public String id;

	public String code;

	public int groupCount;

	public int isNew;

	/** 装备扩展数据 */
	public ItemSpeData speData = new ItemSpeData();

	public int isBind;
	

	/** 不可寄卖 */
	public int noAuction;
	
	/** 新增后置过滤字段   -1表示不适用，0表示否，1表示是 */
	public int isBindFilter;//绑定过滤
	public int noAuctionFilter;//不可寄卖过滤

	public long cdTime;

	public Date gotTime;

	
	

	public PlayerItemPO() {
		isBindFilter=-1;
		noAuctionFilter=-1;
				
	}

	public PlayerItemPO copy() {
		PlayerItemPO po = new PlayerItemPO();
		po.id = id;
		po.code = code;
		po.groupCount = groupCount;
		po.isNew = isNew;
		po.speData = speData;// 目前没这个需求拷贝(如果有需要的话这个也是要拷贝的)
		po.isBind = isBind;
		po.noAuction = noAuction;
		po.cdTime = cdTime;
		po.gotTime = gotTime;
		po.isBindFilter = isBindFilter;
		po.noAuctionFilter = noAuctionFilter;
		return po;
	}

}
