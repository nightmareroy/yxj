package com.wanniu.game.data; 

public class GiftCO { 

	/** 礼包ID */
	public int giftId;
	/** 身份码 */
	public String type;
	/** 渠道 */
	public String channel;
	/** 大渠道 */
	public int rootChannel;
	/** 使用次数 */
	public int times;
	/** VIP等级 */
	public int vip;
	/** 等级 */
	public int level;
	/** 阶级 */
	public int upOrder;
	/** 重复使用次数 */
	public int isRestated;
	/** 生效时间 */
	public String openTime;
	/** 结束时间 */
	public String endTime;
	/** 奖励物品TC */
	public String tcReward;
	/** 奖励物品1 */
	public String reward1;
	/** 数量1 */
	public int number1;
	/** 奖励物品2 */
	public String reward2;
	/** 数量2 */
	public int number2;
	/** 奖励物品3 */
	public String reward3;
	/** 数量3 */
	public int number3;
	/** 奖励物品4 */
	public String reward4;
	/** 数量4 */
	public int number4;
	/** 奖励物品5 */
	public String reward5;
	/** 数量5 */
	public int number5;
	/** 奖励物品6 */
	public String reward6;
	/** 数量6 */
	public int number6;
	/** 奖励物品7 */
	public String reward7;
	/** 数量7 */
	public int number7;
	/** 奖励物品8 */
	public String reward8;
	/** 数量8 */
	public int number8;

	/** 主键 */
	public int getKey() {
		return this.giftId; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}