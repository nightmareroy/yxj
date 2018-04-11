package com.wanniu.game.data; 

public class AfterFilterCO { 

	/** TC代码 */
	public String tcCode;
	/** 覆盖物品成色 */
	public String qcolor;
	/** 覆盖物品需求等级 */
	public String levelReq;
	/** 覆盖绑定规则 */
	public String bindType;
	/** 覆盖不可销毁 */
	public String noDestory;
	/** 覆盖不可出售 */
	public String noSell;
	/** 覆盖不可交易 */
	public String noTrade;
	/** 覆盖不可寄卖 */
	public String noAuction;
	/** 覆盖不可存入个人仓库 */
	public String noDepotRole;
	/** 覆盖不可存入账号仓库 */
	public String noDepotAcc;
	/** 覆盖不可存入公会仓库 */
	public String noDepotGuild;
	/** 覆盖不可熔炼 */
	public String noMelt;

	/** 主键 */
	public String getKey() {
		return this.tcCode; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}