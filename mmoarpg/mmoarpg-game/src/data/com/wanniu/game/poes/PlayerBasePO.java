package com.wanniu.game.poes;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.wanniu.core.game.entity.GEntity;
import com.wanniu.game.DBTable;
import com.wanniu.game.common.Const;
import com.wanniu.game.common.Table;
import com.wanniu.game.item.po.PlayerItemPO;

/**
 * 玩家扩展数据 Yangzz
 */
@DBTable(Table.player_base)
public class PlayerBasePO extends GEntity {

	/** 装备格子物品 */
	public Map<Integer, PlayerItemPO> equipGrids; 
	
	/** 强化格子对应的等级 */
	public Map<Integer, EquipStrengthPos> strengthPos;
	
	/**变身模型Avatar*/
	public String model;
	
	/**变身后的速度*/
	public float speed;
	
	/** 时装 */
	//已激活的时装列表
	public List<Object[]> fashions_get;//数组第二个为false表示未点击查看过
	//穿戴中的时装 列表,id对应到Const.FASHION_TYPE
	public Map<Integer,String> fashions_equiped;
	
//	public int fashion_get_spot;//获取时装红点
	
	public boolean openRebornToday;
	public boolean openRebuildToday;
	public boolean openKaiguangToday;
	
	public PlayerBasePO() {
		fashions_get=new LinkedList<>();
		fashions_equiped=new HashMap<>();
		for (Const.FASHION_TYPE t : Const.FASHION_TYPE.values()) {
			fashions_equiped.put(t.value, null);
		}
		openRebornToday=false;
		openRebuildToday=false;
		openKaiguangToday=false;
		
	}
	/*********************************************************************/
	
	/**装备格子信息: 强化等级、镶嵌信息*/
	public static class EquipStrengthPos {
		public int enSection; // 强化段位
		public int enLevel; // 强化等级
		/**镶嵌的宝石,索引从1开始*/
		public Map<Integer, String> gems;// 镶嵌的宝石,索引从1开始
		public int socks;//开放的镶嵌孔数量
	}
	
}
