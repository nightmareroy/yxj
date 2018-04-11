package com.wanniu.game.data; 

public class AccumulateCO { 

	/** 排序 */
	public int orderId;
	/** 轮数 */
	public int round;
	/** ID */
	public int id;
	/** 天数 */
	public int days;
	/** 名称备注 */
	public String name;
	/** 礼包描述 */
	public String describe;
	/** 展示道具 */
	public String iconcode;
	/** 奖励内容 */
	public String item1code;
	/** 数量 */
	public int item1count;
	/** 奖励内容 */
	public String item2code;
	/** 数量 */
	public int item2count;
	/** 奖励内容 */
	public String item3code;
	/** 数量 */
	public int item3count;
	/** 奖励内容 */
	public String item4code;
	/** 数量 */
	public int item4count;

	/** 主键 */
	public int getKey() {
		return this.orderId; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}