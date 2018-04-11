package com.wanniu.game.data; 

public class WorldZoneCO { 

	/** 编号 */
	public int mapID;
	/** 区域名称 */
	public String mapName;
	/** 所属阵营 */
	public int whichCamp;
	/** 是否有子地图 */
	public int chirdmap;
	/** 父级地图编号 */
	public int followMapID;
	/** 快捷传送排列顺序 */
	public int mapList;
	/** 地图标签 */
	public String mapTag;

	/** 主键 */
	public int getKey() {
		return this.mapID; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}