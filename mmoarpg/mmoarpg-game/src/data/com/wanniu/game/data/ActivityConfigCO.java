package com.wanniu.game.data; 

public class ActivityConfigCO { 

	/** 活动编号 */
	public int id;
	/** 活动类型 */
	public int type;
	/** 参数注释 */
	public String notes1;
	/** 参数1 */
	public int parameter1;
	/** 参数扩展1 */
	public String extend1;
	/** 参数注释 */
	public String notes2;
	/** 参数2 */
	public int parameter2;
	/** 奖励道具 */
	public String itemCode;
	/** 职业奖励：战士 */
	public String zSItemCode;
	/** 职业奖励：刺客 */
	public String cKItemCode;
	/** 职业奖励：法师 */
	public String fSItemCode;
	/** 职业奖励：猎人 */
	public String lRItemCode;
	/** 职业奖励：牧师 */
	public String mSItemCode;
	/** 邮件ID */
	public int mailID;

	/** 主键 */
	public int getKey() {
		return this.id; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}