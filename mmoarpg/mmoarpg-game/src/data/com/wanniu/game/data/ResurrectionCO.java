package com.wanniu.game.data; 

public class ResurrectionCO { 

	/** 编号 */
	public int mapID;
	/** 地图名称 */
	public String mapName;
	/** 安全复活 */
	public int safeResurrect;
	/** 回城复活 */
	public int backResurrect;
	/** 原地复活 */
	public int immResurrect;
	/** 复活次数 */
	public int resurrectNum;
	/** 是否躺尸 */
	public int lieDown;
	/** 自动复活倒计时 */
	public int autoResurrectTime;
	/** 复活冷却时间 */
	public int resurrectCD;
	/** 是否不可免费复活 */
	public int freeResurrect;
	/** 复活消耗 */
	public int cost;

	/** 主键 */
	public int getKey() {
		return this.mapID; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}