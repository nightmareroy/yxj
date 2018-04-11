package com.wanniu.game.data; 

public class TypeNameCO { 

	/** 集合页签 */
	public int type;
	/** 页签名称 */
	public String name;
	/** 最低进入等级 */
	public int minLv;
	/** 最高进入等级 */
	public int maxLv;

	/** 主键 */
	public int getKey() {
		return this.type; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}