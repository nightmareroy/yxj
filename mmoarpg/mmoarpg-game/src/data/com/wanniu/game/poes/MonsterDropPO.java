package com.wanniu.game.poes;

import com.wanniu.core.game.entity.GEntity;
import com.wanniu.game.DBTable;
import com.wanniu.game.common.Table;

@DBTable(Table.monster_drop_info)
public class MonsterDropPO extends GEntity {

	/** <areaId,<bossId,times>> */
	private int todayCount;

	public MonsterDropPO() {

	}

	public int getTodayCount() {
		return todayCount;
	}

	public void setTodayCount(int todayCount) {
		this.todayCount = todayCount;
	}

	public void clear() {
		todayCount = 0;
	}

	public void addBossCount() {
		todayCount++;
	}
}
