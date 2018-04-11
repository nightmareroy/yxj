package com.wanniu.game.data; 

public class PetUpgradeCO { 

	/** 编号 */
	public int iD;
	/** 宠物编号 */
	public int petID;
	/** 目标阶数 */
	public int targetUpLevel;
	/** 突破所需宠物等级 */
	public int reqLevel;
	/** 突破需要材料代码 */
	public String mateCode;
	/** 需要材料数量 */
	public int mateCount;
	/** 突破后等级上限 */
	public int nextMaxLvl;
	/** 突破后解锁技能ID */
	public int openSkillID;
	/** 属性数量 */
	public int propCount;
	/** 属性1 */
	public String petProp1;
	/** 参数1 */
	public int petPar1;
	/** 最小值1 */
	public int petMin1;
	/** 最大值1 */
	public int petMax1;
	/** 属性2 */
	public String petProp2;
	/** 参数2 */
	public int petPar2;
	/** 最小值2 */
	public int petMin2;
	/** 最大值2 */
	public int petMax2;
	/** 属性3 */
	public String petProp3;
	/** 参数3 */
	public int petPar3;
	/** 最小值3 */
	public int petMin3;
	/** 最大值3 */
	public int petMax3;
	/** 属性4 */
	public String petProp4;
	/** 参数4 */
	public int petPar4;
	/** 最小值4 */
	public int petMin4;
	/** 最大值4 */
	public int petMax4;

	/** 主键 */
	public int getKey() {
		return this.iD; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}