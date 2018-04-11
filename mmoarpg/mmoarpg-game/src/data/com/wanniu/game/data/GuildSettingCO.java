package com.wanniu.game.data; 

public class GuildSettingCO { 

	/** 入会等级 */
	public int joinLv;
	/** 创建消耗 */
	public int cost;
	/** 退会冷却 */
	public int selfOut;
	/** 被踢冷却 */
	public int kickOut;
	/** 踢人冷却 */
	public int fireOut;
	/** 踢人次数 */
	public int fireNum;
	/** 申请失效时长 */
	public int applicationFail;
	/** 弹劾基础条件 */
	public int impeach;
	/** 弹劾持续时长 */
	public int impeachTime;
	/** 弹劾人数 */
	public int impeachNo;
	/** 公告字数 */
	public int announcement;
	/** 默认公告 */
	public String defaultNote;
	/** 动态条目 */
	public int recording;
	/** 申请限制 */
	public int applyMax;
	/** 公会名称 */
	public int nameMaxLen;
	/** 最长字数 */
	public int maxLen;
	/** 改名道具 */
	public String changeName;
	/** 改名消耗 */
	public int changeNameCost;
	/** 改名冷却 */
	public int changeNameCD;
	/** 公会仓库存入次数 */
	public int warehousePutIn;
	/** 公会仓库删除次数 */
	public int warehouseDel;
	/** 公会仓库存入等级 */
	public int warehouseMinLv;
	/** 公会仓库存入品质 */
	public int warehouseMinQ;
	/** 公会科技物品每日刷新时间 */
	public float gTechItemRefreshTime;
	/** 仙盟BOSS开启 */
	public String gBossOpenTime;
	/** 仙盟BOSS倒计时时间 */
	public String gBossShowTime;
	/** 仙盟BOSS结束时间 */
	public String gBossCloseTime;

	/** 主键 */
	public int getKey() {
		return this.joinLv; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}