package com.wanniu.game.poes;

import java.util.Date;

import com.wanniu.core.game.entity.GEntity;
import com.wanniu.game.DBField;
import com.wanniu.game.item.po.PlayerItemPO;

/**
 * consignment_items
 * @author Yangzz
 *
 */
public class ConsignmentItemsPO extends GEntity {
	@DBField(isPKey = true, fieldType = "varchar", size = 50)
	public String id;
	
	public int itemType;
	public int pro;
	public int itemSecondType;
	public int level;
	public int consignmentPrice;
	public long consignmentTime;
	/**延迟上架时间(分钟)*/
	public int lateMinutes;
	public String consignmentPlayerName;
	public int consignmentPlayerPro;
	public String consignmentPlayerId;
	public int groupCount;
	/**宣传次数*/
	public int publishTimes;
	public PlayerItemPO db;

	public ConsignmentItemsPO() {
		
	}
}
