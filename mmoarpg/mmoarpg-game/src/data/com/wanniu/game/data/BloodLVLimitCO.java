package com.wanniu.game.data; 

public class BloodLVLimitCO { 

	/** 血脉品质 */
	public int bloodQColor;
	/** 等级上限 */
	public int expLimit;
	/** 升级百分比提升 */
	public float ratio;

	/** 主键 */
	public int getKey() {
		return this.bloodQColor; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}