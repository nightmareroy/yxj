package com.wanniu.game.poes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.wanniu.core.game.entity.GEntity;
import com.wanniu.game.DBTable;
import com.wanniu.game.common.Table;
import com.wanniu.game.data.ArmourAttributeCO;
import com.wanniu.game.data.GameData;

/**
 * @author WFY (player_achieves)
 * "achievements": {
        "awards": [
        	{
                "id": 97, 
                "count": 0, 
                "type": 72
            }, ...N], 
        "achievements": [
            {
                "id": 1001, 
                "scheduleCurr": 126057
            }, ...N  ]
    },
 */
@DBTable(Table.player_achieves)
public class AchievementDataPO extends GEntity {

	public Map<Integer, AchievementDataPO.AchievePO> achievements = new HashMap<>();
	// 已领取的成就奖励
	public List<Integer> receivedAwards = new ArrayList<>();
	
	//好友成就中已完成的id列表
	public List<Integer> finishedFriendAchievementList=new ArrayList<>();
	
	//元始圣甲列表
	public Map<Integer, HolyArmour> holyArmourMap=new HashMap<>();
	
	public AchievementDataPO() {
		for (ArmourAttributeCO armourAttributeCO : GameData.ArmourAttributes.values()) {
			HolyArmour armour=new HolyArmour(armourAttributeCO.iD);
			holyArmourMap.put(armour.id, armour);
		}
	}
	
	public static class AchievePO{
		public int id;
		public int scheduleCurr;
		public List<String> data; // 进度数据eg: 强化10个部位 1,2,..9,10
		public int awardState; // 0:未领取 1:已领取
	}
	
	public static class HolyArmour{
		public int id;
		public int states;//激活状态  1:未激活 2：可激活 3：已激活
		public HolyArmour(){

		}
		public HolyArmour(int id){
			this.id=id;
			this.states=1;
		}
	}
}
