package com.wanniu.game.poes;

import java.util.HashMap;
import java.util.Map;

import com.wanniu.core.game.entity.GEntity;
import com.wanniu.game.DBTable;
import com.wanniu.game.common.Table;

@DBTable(Table.player_online_data)
public class OnlineDataPO extends GEntity {

	public long sumTime;
	public Map<Integer, Integer> rewardState; // 记录奖励领取状态NO_RECEIVE(0),
												// CAN_RECEIVE(1), RECEIVED(2)

	public OnlineDataPO() {
		rewardState = new HashMap<Integer, Integer>();
	}
}
