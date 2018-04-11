package com.wanniu.game.data; 

public class NormalSignCO { 

	/** 排序 */
	public int orderId;
	/** 轮数 */
	public int round;
	/** ID */
	public int id;
	/** 名称 */
	public String name;
	/** 下一轮 */
	public int nextRound;
	/** 名称备注 */
	public String nameRemarks;
	/** 奖励内容 */
	public String item1code;
	/** 数量 */
	public int item1count;
	/** VIP需求 */
	public int vip;
	/** VIP描述 */
	public String describe;
	/** 任务角标 */
	public int script;

	/** 主键 */
	public int getKey() {
		return this.orderId; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}