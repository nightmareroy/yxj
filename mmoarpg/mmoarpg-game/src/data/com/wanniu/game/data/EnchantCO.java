package com.wanniu.game.data; 

public class EnchantCO { 

	/** 强化编号 */
	public int iD;
	/** 强化段位 */
	public int enClass;
	/** 强化等级 */
	public int enLevel;
	/** 基础属性提升万分比 */
	public int propPer;
	/** 消耗金钱 */
	public int costGold;
	/** 所需材料代码1 */
	public String mateCode1;
	/** 材料数量1 */
	public int mateCount1;
	/** 所需材料代码2 */
	public String mateCode2;
	/** 材料数量2 */
	public int mateCount2;
	/** 强化后外形效果类型 */
	public int effectType;

	/** 主键 */
	public int getKey() {
		return this.iD; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}