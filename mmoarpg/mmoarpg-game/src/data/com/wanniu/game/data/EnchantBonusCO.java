package com.wanniu.game.data; 

public class EnchantBonusCO { 

	/** 强化编号 */
	public int iD;
	/** 类型 */
	public String type;
	/** 强化段位 */
	public int enClass;
	/** 奖励属性 */
	public String prop;
	/** 参数 */
	public int par;
	/** 最小值 */
	public int min;
	/** 最大值 */
	public int max;

	/** 主键 */
	public int getKey() {
		return this.iD; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}