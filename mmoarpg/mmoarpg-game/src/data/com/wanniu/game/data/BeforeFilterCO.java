package com.wanniu.game.data; 

public class BeforeFilterCO { 

	/** 编号 */
	public String id;
	/** TC代码 */
	public String tcCode;
	/** 动态等级值 */
	public String dynamicLv;
	/** 替换TC */
	public String tcForLv;

	/** 主键 */
	public String getKey() {
		return this.id; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}