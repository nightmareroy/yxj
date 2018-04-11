package com.wanniu.game.data; 

public class RebuildConfigCO { 

	/** 锁定条目数 */
	public int lockNum;
	/** 额外消耗材料 */
	public String mateCode;
	/** 消耗材料数量 */
	public int mateNum;

	/** 主键 */
	public int getKey() {
		return this.lockNum; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}