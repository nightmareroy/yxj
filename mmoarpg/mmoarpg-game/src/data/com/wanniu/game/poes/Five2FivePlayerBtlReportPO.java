package com.wanniu.game.poes;

import java.util.Date;
import java.util.Map;

import com.wanniu.core.game.entity.GEntity;
import com.wanniu.game.DBField;
import com.wanniu.game.five2Five.Five2FivePlayerResultInfoVo;

/**
 * 玩家5v5战报信息
 * 
 * @author wanghaitao
 *
 */
public class Five2FivePlayerBtlReportPO extends GEntity {
	@DBField(isPKey = true, fieldType = "varchar", size = 50)
	public String id;

	/** 玩家ID */
	public String playerId;

	/** 状态 (1胜利、2失败、3平局) */
	public int status;

	/** 积分变化 */
	public int scoreChange;

	/** A组的战斗信息<playerId,resultInfoVo> */
	public Map<String, Five2FivePlayerResultInfoVo> resultInfoA;
	
	/** B组的战斗信息<playerId,resultInfoVo> */
	public Map<String, Five2FivePlayerResultInfoVo> resultInfoB;

	/** 创建时间 */
	public Date createTime;
	
	public Five2FivePlayerBtlReportPO() {
		
	}
}
