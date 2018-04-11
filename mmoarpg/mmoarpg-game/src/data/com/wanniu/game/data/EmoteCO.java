package com.wanniu.game.data; 

public class EmoteCO { 

	/** 序号 */
	public int id;
	/** 表情代码 */
	public String e_code;
	/** 表情名称 */
	public String e_text;

	/** 主键 */
	public int getKey() {
		return this.id; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}