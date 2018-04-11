package com.wanniu.game.data; 

public class WayTreasureCO { 

	/** 编号 */
	public int iD;
	/** 场景ID */
	public String doScene;
	/** 路点 */
	public String doPoint;
	/** 路点刷新的怪物ID */
	public String monsterID;
	/** 路点刷新的怪物名称 */
	public String monster;
	/** 藏宝图颜色 */
	public int colour;
	/** 挖宝次序 */
	public int order;

	/** 主键 */
	public int getKey() {
		return this.iD; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}