package com.wanniu.game.poes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.alibaba.fastjson.annotation.JSONField;
import com.wanniu.core.game.entity.GEntity;
import com.wanniu.game.DBTable;
import com.wanniu.game.common.Table;
import com.wanniu.game.item.po.PlayerItemPO;

@DBTable(Table.player_fightlevel)
public class FightLevelsPO extends GEntity {
	// 今天完成的有收益副本的计数
	public Map<Integer, Integer> todays;
	// 今天购买的副本计数
	public Map<Integer, Integer> buys = new HashMap<Integer, Integer>();

	public Map<Integer, Integer> finishes;

	/** <areaId,<bossId,times>> */
	public Map<Integer, Map<Integer, Integer>> dropedBossMap = new ConcurrentHashMap<>();

	/** 资源副本当日 已挑战次数和购买次数 */
	public Map<Integer, ResourceDungeonPO> resourceDungeon;
	// 资源副本双倍奖励存储
	public RDDoubleRewardPO doubleReward;

	public FightLevelsPO() {

	}

	/**
	 * 资源副本当日已挑战次数和已购买次数
	 */
	public static class ResourceDungeonPO {
		public int usedTimes;

		public int buyTimes;

		// 进入状态，不需要存储.
		@JSONField(deserialize = false, serialize = false)
		public boolean entering = false;
	}

	public static class RDDoubleRewardPO {
		public Map<String, Integer> doubleVirtualItems;
		public List<PlayerItemPO> doubleItems;
	}
}
