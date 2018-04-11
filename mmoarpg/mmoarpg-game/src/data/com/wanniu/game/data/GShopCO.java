package com.wanniu.game.data; 

public class GShopCO { 

	/** 编号ID */
	public int itemID;
	/** 物品显示名称 */
	public String itemShowName;
	/** 物品代码 */
	public String itemCode;
	/** 物品数量 */
	public int itemCount;
	/** 是否有效 */
	public int isValid;
	/** 兑换后是否绑定 */
	public int isBind;
	/** 每天购买次数上限 */
	public int dayCount;
	/** 物品描述 */
	public String itemDes;
	/** 需要等级 */
	public int levelReq;
	/** 需要进阶等级 */
	public int upReq;
	/** 需要VIP等级 */
	public int vipReq;
	/** 需要阵营 */
	public int raceReq;
	/** 需要阵营声望等级 */
	public int raceClass;
	/** 货币类型1 */
	public int type1;
	/** 需要货币数值1 */
	public int value1;
	/** 货币类型2 */
	public int type2;
	/** 需要货币数值2 */
	public int value2;
	/** 货币类型3 */
	public int type3;
	/** 需要货币数值3 */
	public int value3;
	/** 权值 */
	public int pro;

	/** 主键 */
	public int getKey() {
		return this.itemID; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}