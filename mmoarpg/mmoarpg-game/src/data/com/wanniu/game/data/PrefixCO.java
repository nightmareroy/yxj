package com.wanniu.game.data; 

public class PrefixCO { 

	/** 前缀 */
	public String prefix;
	/** 性别 */
	public int sex;

	/** 主键 */
	public String getKey() {
		return this.prefix; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}