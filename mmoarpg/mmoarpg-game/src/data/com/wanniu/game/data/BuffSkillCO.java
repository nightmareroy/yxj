package com.wanniu.game.data; 

public class BuffSkillCO { 

	/** 状态ID */
	public int buffID;
	/** 效果名称 */
	public String effectName;
	/** 效果描述 */
	public String effectDesc;
	/** 触发概率 */
	public int par;
	/** 内置CD */
	public int coldTime;
	/** 持续时长 */
	public int time;
	/** 最小值 */
	public int min;
	/** 最大值 */
	public int max;
	/** 值是否百分比格式化 */
	public int isFormat;
	/** 最多可叠加次数 */
	public int repeat;
	/** 图标 */
	public String buffIcon;

	/** 主键 */
	public int getKey() {
		return this.buffID; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}