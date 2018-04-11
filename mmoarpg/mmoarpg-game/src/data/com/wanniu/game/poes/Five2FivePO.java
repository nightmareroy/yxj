package com.wanniu.game.poes;

import java.util.Date;

import com.wanniu.core.game.entity.GEntity;
import com.wanniu.game.DBTable;
import com.wanniu.game.common.Table;

/**
 * 玩家5v5信息
 * 
 * @author wanghaitao
 *
 */
@DBTable(Table.player_five2five)
public class Five2FivePO extends GEntity {

	/** 积分 */
	public int score;

	/** 胜利场数 */
	public int winCount;

	/** 平局场数 */
	public int tieCount;

	/** 失败场数 */
	public int failCount;

	/** MVP次数 */
	public int mvpCount;

	/** 当天战斗次数 */
	public int canReciveRewardCount;

	/** 最后一次挑战的时间 */
	public Date lastChallengeTime;

	/** 已经领取奖励的次数 */
	public int hasReciveRewardCount;

	/** 最后一次领取奖励的时间 */
	public Date lastReciveRewardTime;

	/** 创建时间 */
	public Date createTime;

	/** 赛季结束时间 */
	public Date updateTime;
	
	public Five2FivePO() {
		
	}
}
