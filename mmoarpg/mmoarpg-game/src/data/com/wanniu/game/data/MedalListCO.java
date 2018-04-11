package com.wanniu.game.data; 

public class MedalListCO { 

	/** 编号ID */
	public int iD;
	/** 名称 */
	public String name;
	/** 专属职业 */
	public String pro;
	/** 代码 */
	public String code;
	/** 下一级勋章代码 */
	public String nextCode;
	/** 需要金币 */
	public int costGold;
	/** 需要钻石 */
	public int costDiamond;
	/** 需要爵位ID */
	public int needTitleID;

	/** 主键 */
	public int getKey() {
		return this.iD; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}