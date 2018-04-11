package com.wanniu.game.data; 

public class OlGiftCO { 

	/** 礼包ID */
	public int giftId;
	/** 类型 */
	public int type;
	/** 礼包名称 */
	public String name;
	/** 等级下限 */
	public int lvDown;
	/** 等级上限 */
	public int lvUp;
	/** 阶级下限 */
	public int downOrder;
	/** 阶级上限 */
	public int upOrder;
	/** 登陆时长 */
	public int time;
	/** 奖励物品 */
	public String reward;
	/** 数量 */
	public int number;

	/** 主键 */
	public int getKey() {
		return this.giftId; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}