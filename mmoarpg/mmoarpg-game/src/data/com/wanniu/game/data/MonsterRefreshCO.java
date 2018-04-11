package com.wanniu.game.data; 

public class MonsterRefreshCO { 

	/** 序列ID */
	public int iD;
	/** 场景ID */
	public int mapID;
	/** 怪物路点 */
	public String monPoint;
	/** 地图名称 */
	public String mapName;
	/** 刷新点名称 */
	public String refreshPoint;
	/** BOSS起始刷新时间 */
	public String rebornBeginTime;
	/** BOSS结束刷新时间 */
	public String rebornEndTime;
	/** 刷新间隔 */
	public int coolDownTime;
	/** 怪物ID */
	public int monsterID;
	/** 是否广播 */
	public int msgSend;
	/** 初始模型 */
	public String model;
	/** 模型缩放倍数 */
	public float modelPercent;
	/** 模型高度偏移 */
	public float modelY;
	/** 击杀掉落预览 */
	public String dropPre;
	/** 参与掉落预览及奖励 */
	public String partakeDropPre;
	/** BOSS描述 */
	public String bossDesc;
	/** 背景贴图 */
	public String mapPicture;
	/** BOSS贴图 */
	public String bossPicture;
	/** 开放日期 */
	public String opentime;
	/** 集合页签 */
	public int type;
	/** 功能定位 */
	public int useType;
	/** 排序 */
	public int sort;

	/** 主键 */
	public int getKey() {
		return this.iD; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}