package com.wanniu.game.poes;

import java.util.Date;

import com.wanniu.core.game.entity.GEntity;
import com.wanniu.game.DBTable;
import com.wanniu.game.common.Table;

/**
 * 世界等级/膜拜
 * 
 * @author Yangzz
 *
 */
@DBTable(Table.player_leaderboard)
public class LeaderBoardPlayerPO extends GEntity {

	public int worShipTimes;
	public Date worShipTime;
	public int worShipDiamondTimes;

	public LeaderBoardPlayerPO() {
		
	}
	
}
