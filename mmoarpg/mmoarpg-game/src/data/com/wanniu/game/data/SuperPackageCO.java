package com.wanniu.game.data; 

public class SuperPackageCO { 

	/** 编号 */
	public int iD;
	/** 奖励礼包名称 */
	public String packageName;
	/** 礼包代码 */
	public String packageCode;
	/** 礼包数量 */
	public int packageNum;
	/** 礼包图标 */
	public String packageIcon;
	/** 礼包角标 */
	public int packageScript;
	/** 礼包价格 */
	public int packagePrice;
	/** 钻石数量 */
	public int diamondNum;

	/** 主键 */
	public int getKey() {
		return this.iD; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}