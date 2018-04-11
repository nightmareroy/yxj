package com.wanniu.game.poes;

import java.util.Date;
import java.util.Map;

import com.wanniu.core.game.entity.GEntity;
import com.wanniu.game.DBTable;
import com.wanniu.game.common.Table;

@DBTable(Table.player_sign)
public final class AttendancePO extends GEntity {
	public int stage;
	public Date lastSignTime;
	public Date lastLuxuryTime;
	public int luxuryState;
	public Map<Integer, Integer> signMap;
	/** 存储Id和receive */
	public Map<Integer, Integer> cumulativeMap;

	public AttendancePO() {
		
	}
}
