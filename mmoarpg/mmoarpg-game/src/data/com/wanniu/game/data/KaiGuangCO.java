package com.wanniu.game.data; 

public class KaiGuangCO { 

	/** 开光编号 */
	public int iD;
	/** 装备等级 */
	public int level;
	/** 所需材料代码1 */
	public String mateCode1;
	/** 材料数量1 */
	public int mateCount1;
	/** 所需材料代码2 */
	public String mateCode2;
	/** 材料数量2 */
	public int mateCount2;
	/** 消耗金钱 */
	public int costGold;

	/** 主键 */
	public int getKey() {
		return this.iD; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}