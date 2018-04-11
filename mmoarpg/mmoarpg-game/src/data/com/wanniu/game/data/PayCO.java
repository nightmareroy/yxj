package com.wanniu.game.data; 

public class PayCO { 

	/** 套餐ID */
	public int iD;
	/** 套餐图标 */
	public String packageIcon;
	/** APP后台编号 */
	public String appProductId;
	/** 套餐名称 */
	public String packageName;
	/** 套餐描述 */
	public String packageDesc;
	/** 首充描述 */
	public String packageDescFirst;
	/** 货币类型 */
	public String payMoneyType;
	/** 货币金额 */
	public int payMoneyAmount;
	/** 充值元宝 */
	public int payDiamond;
	/** 首购赠元宝 */
	public int firstDiamond;
	/** 非首购赠元宝 */
	public int nonFirstDiamond;
	/** 首充绑元 */
	public int payCashFirst;
	/** 充值标签 */
	public int payTag;

	/** 主键 */
	public int getKey() {
		return this.iD; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}