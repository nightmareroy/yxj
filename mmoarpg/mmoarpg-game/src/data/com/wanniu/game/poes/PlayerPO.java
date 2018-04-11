package com.wanniu.game.poes;

import java.util.Date;

import com.wanniu.core.game.entity.GEntity;
import com.wanniu.game.DBField;
import com.wanniu.game.DBTable;
import com.wanniu.game.common.Table;

/**
 * 角色数据库实体类
 * 
 * @author yangzhuzhi
 */
@DBTable(Table.player)
public final class PlayerPO extends GEntity {
	@DBField(isPKey = true, fieldType = "char", size = 36)
	public String id;
	@DBField(size = 256)
	public String uid;
	public int logicServerId;
	@DBField(size = 64)
	public String name;
	public int isDelete;
	public int level;
	public long exp;
	public int prestige;
	/** 职业ID */
	public int pro;
	// public int sp;
	/** 银两 */
	public int gold;
	/** 元宝 */
	public int diamond;
	/** 绑元 */
	public int ticket;
	// public int energy;
	public int friendly;
	/** 商城消费积分 */
	public int consumePoint;

	public int charm;
	public int pawnGold;
	public int guildpoint;
	public int treasurePoint;
	public int fightPower;

	public int maxFightPower;// 历史最大值

	public int upOrder;
	/**
	 * 境界编号
	 */
	public int upLevel;

	// /** 境界编号 */
	// public int classID;
	// /** 境界阶数 */
	// public int classUPLevel;
	/** 修为 */
	public int classExp;
	/**
	 * VIP类型，请勿直接访问该字段，可以通过PlayerBaseDataManager的getVip()获取
	 */
	public int vip;
	public Date vipEndTime;

	public int isAcceptAutoTeam;
	public Date createTime;
	public Date loginTime;
	public Date logoutTime;
	public Date refreshTime; // 每日重置时间
	public Date forbidTalkTime;// 禁言恢复时间
	public Date freezeTime;// 冻结恢复时间
	public String forbidTalkReason;// 禁言原因
	public String freezeReason;// 冻结原因
	public int totalCostDiamond;

	public String fightingPetId;

	public Date lvChangeTime; // 等级变化时间
	public Date fightChangeTime; // 战力变化时间

	public boolean openMount;

	@DBField(include = false)
	public int needExp;

	/** 今日收入银两 */
	public int todayGold;
	/** 今日收入元宝 */
	public int todayDiamond;
	/** 今日收入绑元 */
	public int todayTicket;

	public String channel;// 渠道
	public String subchannel;// 子渠道
	public String subchannelUID;// 子渠道UID
	public String mac; // 最近一次登录MAC地址
	public String os; // 系统类型
	public String ip;// 最后登录IP
	public int exchangCount = 0;// 兑换属性次数

	public PlayerPO() {}
}