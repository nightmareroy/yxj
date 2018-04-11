package com.wanniu.game.data; 

public class TransportCO { 

	/** 传送ID */
	public int transportID;
	/** 传送条件 */
	public int transMod;
	/** 条件值 */
	public String modValue;
	/** 数量值 */
	public int needNumber;
	/** 使用次数 */
	public int count;
	/** 目标地图 */
	public int targetMap;
	/** 目标坐标 */
	public String targetPoint;
	/** 无法传送提示 */
	public String failTips;

	/** 主键 */
	public int getKey() {
		return this.transportID; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}