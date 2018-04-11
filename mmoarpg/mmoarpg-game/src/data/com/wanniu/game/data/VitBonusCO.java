package com.wanniu.game.data; 

public class VitBonusCO { 

	/** 编号 */
	public int iD;
	/** 奖励礼包名称 */
	public String chestName;
	/** 礼包代码 */
	public String chestCode;
	/** 需要活跃度 */
	public int reqVit;
	/** 邮件id */
	public int mailId;

	/** 主键 */
	public int getKey() {
		return this.iD; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}