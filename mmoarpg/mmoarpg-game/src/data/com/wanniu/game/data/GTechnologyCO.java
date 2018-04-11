package com.wanniu.game.data; 

public class GTechnologyCO { 

	/** 序号 */
	public int order;
	/** 技能名称 */
	public int techID;
	/** 技能名称 */
	public String techName;
	/** 等级 */
	public int techLevel;
	/** 增加属性1 */
	public String techAttribute1;
	/** 属性数值1 */
	public int techValue1;
	/** 增加属性2 */
	public String techAttribute2;
	/** 属性数值2 */
	public int techValue2;
	/** 描述说明 */
	public String techDes;
	/** 升级需要帮贡 */
	public int points;
	/** 升级需要金币 */
	public int gold;
	/** 推荐职业 */
	public String recommend;
	/** 技能图标 */
	public String icon;

	/** 主键 */
	public int getKey() {
		return this.order; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}