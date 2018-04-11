package com.wanniu.game.poes;

import java.util.Date;
import java.util.Map;

import com.wanniu.core.game.entity.GEntity;
import com.wanniu.game.DBField;

/**
 * 道友成员信息
 * 
 * @author wanghaitao
 *
 */
public class DaoYouMemberPO extends GEntity {
	@DBField(isPKey = true, fieldType = "varchar", size = 50)
	/** 玩家ID */
	public String playerId;

	/** 道友ID */
	public String daoYouId;

	/** 总发放返利数 */
	public int totalSendRebate;

	/** 今日发放返利数<playerName,reciveNumber> */
	public Map<String, Integer> todaySendRebate;

	/** 总收到返利数 */
	public int totalReciveRebate;

	/** 今日收到返利数 */
	public int todayReciveRebate;

	/** 加入道友时间 */
	public Date joinTime;

	/** 创建时间 */
	public Date createTime;

	/** 更新时间 */
	public Date updateTime;

	public DaoYouMemberPO() {

	}

}
