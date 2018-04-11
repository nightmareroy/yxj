package com.wanniu.game.data; 

public class EquipSockCO { 

	/** 对应编号 */
	public int typeID;
	/** 物品类型代码 */
	public String itemType;
	/** 可镶嵌宝石数量 */
	public int sockNum;
	/** 插槽1开放等级 */
	public int sock1OpenLvl;
	/** 插槽2开放等级 */
	public int sock2OpenLvl;
	/** 插槽3开放等级 */
	public int sock3OpenLvl;
	/** 插槽4开放等级 */
	public int sock4OpenLvl;
	/** 插槽5开放等级 */
	public int sock5OpenLvl;
	/** 可镶嵌宝石代码列表 */
	public String gemTypeList;

	/** 主键 */
	public int getKey() {
		return this.typeID; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}