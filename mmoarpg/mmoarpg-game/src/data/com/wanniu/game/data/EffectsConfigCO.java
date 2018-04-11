package com.wanniu.game.data; 

public class EffectsConfigCO { 

	/** 编号 */
	public int iD;
	/** 特效名称 */
	public String name;
	/** 特效文件名 */
	public String paramName;
	/** 特效路径 */
	public String path;
	/** 放大倍数 */
	public int scaling;

	/** 主键 */
	public int getKey() {
		return this.iD; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}