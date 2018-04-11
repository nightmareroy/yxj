package com.wanniu.game.data; 

public class TalentGroupCO { 

	/** 天赋组ID */
	public int talentGroupID;
	/** 对应职业 */
	public int groupJob;
	/** 天赋系名称 */
	public String talentName;
	/** 描述 */
	public String talentDesc;
	/** 是否开放 */
	public int open;

	/** 主键 */
	public int getKey() {
		return this.talentGroupID; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}