package com.wanniu.game.data; 

public class ShopLabCO { 

	/** 控件编号 */
	public int id;
	/** 控件名称 */
	public String controlName;
	/** 页签名称 */
	public String btnText;
	/** 货币产出简介 */
	public String desc;
	/** 对应货币 */
	public String icon;
	/** 途径功能ID */
	public String waysID;

	/** 主键 */
	public int getKey() {
		return this.id; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}