package com.wanniu.game.data; 

public class GuildPositionCO { 

	/** 职位ID */
	public int positionID;
	/** 职位名称 */
	public String position;
	/** 职位颜色 */
	public int positionColor;
	/** 人数 */
	public int setNumber;
	/** 修改公告 */
	public int right1;
	/** 公会改名 */
	public int right2;
	/** 审核人员 */
	public int right3;
	/** 移除成员 */
	public int right4;
	/** 准入条件 */
	public int right5;
	/** 公会仓库 */
	public int right6;
	/** 公会Q群 */
	public int right7;
	/** 公会科技 */
	public int right8;
	/** 公会加成 */
	public int right9;
	/** 公会副本 */
	public int right10;
	/** 邀请入会 */
	public int right11;
	/** 仙盟升级 */
	public int right12;

	/** 主键 */
	public int getKey() {
		return this.positionID; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}