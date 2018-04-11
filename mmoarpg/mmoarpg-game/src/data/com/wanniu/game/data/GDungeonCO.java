package com.wanniu.game.data; 

public class GDungeonCO { 

	/** 副本刷新时间 */
	public String dungeonRefreshDate;
	/** 副本持续时间 */
	public int fightTime;
	/** 公会每日开启次数 */
	public int openTime;
	/** 通关后再次开启间隔 */
	public int openCD;
	/** 开启后准备时间 */
	public int openForReady;
	/** 掷点时间 */
	public int throwTime;

	/** 主键 */
	public String getKey() {
		return this.dungeonRefreshDate; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}