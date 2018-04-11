package com.wanniu.game.data; 

public class GuildFortCO { 

	/** 据点编号 */
	public int iD;
	/** 据点名称 */
	public String name;
	/** 据点等级 */
	public int level;
	/** 胜方获得资源 */
	public String victoryResources;
	/** 败方获得资源 */
	public String failResources;
	/** 胜方每日资源 */
	public String dayResources;

	/** 主键 */
	public int getKey() {
		return this.iD; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}