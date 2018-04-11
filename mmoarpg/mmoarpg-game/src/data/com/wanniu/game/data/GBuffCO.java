package com.wanniu.game.data; 

public class GBuffCO { 

	/** 等级 */
	public int buffLevel;
	/** 增加属性1 */
	public String buffAttribute1;
	/** 属性数值1 */
	public int buffValue1;
	/** 增加属性2 */
	public String buffAttribute2;
	/** 属性数值2 */
	public int buffValue2;
	/** 增加属性3 */
	public String buffAttribute3;
	/** 属性数值3 */
	public int buffValue3;
	/** 增加属性4 */
	public String buffAttribute4;
	/** 属性数值4 */
	public int buffValue4;
	/** 增加属性5 */
	public String buffAttribute5;
	/** 属性数值5 */
	public int buffValue5;
	/** 升级需要公会资金 */
	public int funds;

	/** 主键 */
	public int getKey() {
		return this.buffLevel; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}