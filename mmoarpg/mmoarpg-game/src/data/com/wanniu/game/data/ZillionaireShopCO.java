package com.wanniu.game.data; 

public class ZillionaireShopCO { 

	/** 排序 */
	public int sort;
	/** 编号 */
	public int iD;
	/** 道具ID */
	public String shopID;
	/** 道具名称 */
	public String name;
	/** 获得数量 */
	public int nUM;
	/** 兑换条件 */
	public String exchangeNeed;
	/** 获得后是否绑定 */
	public int isBind;
	/** 每天可兑换次数 */
	public int exchangeTimes;

	/** 主键 */
	public int getKey() {
		return this.sort; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}