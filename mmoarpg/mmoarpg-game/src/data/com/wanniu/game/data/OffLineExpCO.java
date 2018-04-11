package com.wanniu.game.data; 

public class OffLineExpCO { 

	/** 编号 */
	public int id;
	/** 玩家等级 */
	public int roleLevel;
	/** 离线每10分钟获得经验 */
	public int expRatio;

	/** 主键 */
	public int getKey() {
		return this.id; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}