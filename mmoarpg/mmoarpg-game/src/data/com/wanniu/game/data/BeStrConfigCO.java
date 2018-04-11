package com.wanniu.game.data; 

public class BeStrConfigCO { 

	/** 途径ID */
	public int id;
	/** 从属页签 */
	public int type;
	/** 途径名称 */
	public String name;
	/** 途径描述 */
	public String des;
	/** 需求等级 */
	public int lvLimit;
	/** 功能ID */
	public String funId;
	/** 途径图标 */
	public String icon;

	/** 主键 */
	public int getKey() {
		return this.id; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}