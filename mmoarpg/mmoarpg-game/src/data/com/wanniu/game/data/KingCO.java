package com.wanniu.game.data; 

public class KingCO { 

	/** 标签页ID */
	public int tabID;
	/** 标签页名字 */
	public String tabName;
	/** 是否开放 */
	public int isOpen;
	/** 展示类型 */
	public int showType;
	/** 活动描述 */
	public String activityDesc;
	/** 活动banner图片 */
	public String banner;
	/** 模型文件 */
	public String avatarId;
	/** 模型缩放倍数 */
	public float modelPercent;
	/** 模型旋转偏移 */
	public float roteY;
	/** 模型高度偏移 */
	public float modelY;
	/** 模型远近偏移 */
	public float modelZ;
	/** 奖励1 */
	public String item1code;
	/** 数量1 */
	public int num1;
	/** 兑换消耗2 */
	public String item2code;
	/** 数量2 */
	public int num2;

	/** 主键 */
	public int getKey() {
		return this.tabID; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}