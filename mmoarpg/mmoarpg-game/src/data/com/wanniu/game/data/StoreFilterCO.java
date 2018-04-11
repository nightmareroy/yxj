package com.wanniu.game.data; 

public class StoreFilterCO { 

	/** 过滤名称 */
	public String filterName;
	/** 过滤类型 */
	public int filterType;
	/** 过滤代码 */
	public int filterCode;

	/** 主键 */
	public String getKey() {
		return this.filterName; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}