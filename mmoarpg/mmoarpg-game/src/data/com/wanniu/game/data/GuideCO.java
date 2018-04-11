package com.wanniu.game.data; 

public class GuideCO { 

	/** 功能编号 */
	public int iD;
	/** 目标引导名称 */
	public String guideName;
	/** 开放等级 */
	public int openLv;
	/** 关闭等级 */
	public int closeLv;
	/** 图标显示 */
	public String showIcon;
	/** 显示描述 */
	public String showTips;
	/** 奖励 */
	public String reward;
	/** 底图 */
	public String banner;

	/** 主键 */
	public int getKey() {
		return this.iD; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}