package com.wanniu.game.data; 

public class MoneyCO { 

	/** 货币类型 */
	public int type;
	/** 货币代码 */
	public String code;
	/** 货币名称 */
	public String name;
	/** 是否跳转 */
	public int isJump;
	/** 跳转标识 */
	public String jumpTo;
	/** 简介 */
	public String desc;
	/** 来源 */
	public String source;
	/** 来源开启条件 */
	public String sourceOpen;
	/** 图标 */
	public String icon;

	/** 主键 */
	public int getKey() {
		return this.type; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}