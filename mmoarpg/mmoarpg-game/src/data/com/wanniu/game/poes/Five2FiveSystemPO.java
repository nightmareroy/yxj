package com.wanniu.game.poes;

import java.util.Date;
import java.util.Map;

import com.wanniu.core.game.entity.GEntity;
import com.wanniu.game.DBField;

/**
 * 5v5系统信息
 * 
 * @author wanghaitao
 *
 */
public class Five2FiveSystemPO extends GEntity {
	@DBField(isPKey = true, fieldType = "varchar", size = 50)
	public String id;

	public int logicServerId;

	public long teamTotalMatchSuccessCostTime;

	public long singleTotalMatchSuccessCostTime;

	public int totalMatchSuccessSingle;

	public int totalMatchSuccessTeam;
	
	/**赛季结束时间*/
	public Date seasonRefreshTime = new Date();

	/** 还未领取排名奖励的玩家 <playerId,rank> */
	public Map<String, Integer> hasNoReciveRankRewardPlayer;

	public Five2FiveSystemPO() {

	}
}
