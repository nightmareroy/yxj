package com.wanniu.game.data; 

public class BloodSuitListCO { 

	/** 套装编号 */
	public int suitID2;
	/** 分类ID */
	public String sortID1;
	/** 品质 */
	public int quality;
	/** 分类名称 */
	public String sortName;
	/** 套装名称 */
	public String suitName;
	/** 套装图标 */
	public String icon1;
	/** 部件数量 */
	public int partCount;
	/** 是否有效 */
	public int isValid;
	/** 套装描述 */
	public String suitDesc;
	/** 部件物品ID */
	public String partCodeList;
	/** 推荐职业 */
	public String occupation;
	/** 套装角标 */
	public int bloodType;

	/** 主键 */
	public int getKey() {
		return this.suitID2; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}