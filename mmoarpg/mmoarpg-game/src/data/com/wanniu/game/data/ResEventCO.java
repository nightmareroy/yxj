package com.wanniu.game.data; 

public class ResEventCO { 

	/** 序号 */
	public int iD;
	/** 副本编号 */
	public int scenesID;
	/** 副本类型 */
	public int type;
	/** 参数 */
	public String parm;
	/** 资源路径 */
	public String resourcePath;
	/** 备注 */
	public String note;

	/** 主键 */
	public int getKey() {
		return this.iD; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}