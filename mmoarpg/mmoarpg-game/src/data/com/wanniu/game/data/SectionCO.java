package com.wanniu.game.data; 

public class SectionCO { 

	/** 序号 */
	public int iD;
	/** 最低进入等级 */
	public int minLv;
	/** 最高进入等级 */
	public int maxLv;
	/** 副本ID */
	public int dungeonID;
	/** 背景贴图 */
	public String mapPicture;
	/** BOSS编号 */
	public String bossID;
	/** 幻境名称 */
	public String name;

	/** 主键 */
	public int getKey() {
		return this.iD; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}