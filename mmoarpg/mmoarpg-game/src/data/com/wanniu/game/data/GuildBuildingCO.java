package com.wanniu.game.data; 

public class GuildBuildingCO { 

	/** 建筑ID */
	public int buildingID;
	/** 建筑名称 */
	public String buildingName;
	/** 初始等级 */
	public int minLv;
	/** 最大等级 */
	public int maxLv;
	/** 图标 */
	public String icon;
	/** 二级名称 */
	public String buildingName2;

	/** 主键 */
	public int getKey() {
		return this.buildingID; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}