package com.wanniu.game.data; 

public class FestivalGiftCO { 

	/** 序号 */
	public int id;
	/** 节日名称 */
	public String name;
	/** 描述文字 */
	public String describe;
	/** 展示图标 */
	public String showIcon;
	/** 物品奖励 */
	public String rewardItem;
	/** 开始时间 */
	public String openTime;
	/** 结束时间 */
	public String closeTime;

	/** 主键 */
	public int getKey() {
		return this.id; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}