package com.wanniu.game.data; 

public class EquipMakeCO { 

	/** 编号 */
	public int iD;
	/** 专属职业 */
	public String pro;
	/** 装备等级 */
	public int equipLevel;
	/** 目标物品代码 */
	public String targetCode;
	/** 需要金钱 */
	public int costMoney;
	/** 材料种类数 */
	public int mateCount;
	/** 需要材料名称1 */
	public String reqMateName1;
	/** 需要材料代码 1 */
	public String reqMateCode1;
	/** 需要材料数量1 */
	public int reqMateCount1;
	/** 需要材料名称2 */
	public String reqMateName2;
	/** 需要材料代码2 */
	public String reqMateCode2;
	/** 需要材料数量2 */
	public int reqMateCount2;
	/** 需要材料名称3 */
	public String reqMateName3;
	/** 需要材料代码3 */
	public String reqMateCode3;
	/** 需要材料数量3 */
	public int reqMateCount3;

	/** 主键 */
	public int getKey() {
		return this.iD; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}