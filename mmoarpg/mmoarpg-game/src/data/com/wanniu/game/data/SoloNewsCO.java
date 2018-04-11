package com.wanniu.game.data; 

public class SoloNewsCO { 

	/** 编号ID */
	public int iD;
	/** 传闻类型 */
	public int newsType;
	/** 传闻参数值 */
	public int newsPar;
	/** 传闻内容 */
	public String newsContent;

	/** 主键 */
	public int getKey() {
		return this.iD; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}