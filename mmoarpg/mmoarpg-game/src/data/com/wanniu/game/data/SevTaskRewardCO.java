package com.wanniu.game.data; 

public class SevTaskRewardCO { 

	/** 天数编号 */
	public int iD;
	/** 天数奖励 */
	public String reward;
	/** 是否绑定 */
	public int isBinding;
	/** 日期图片 */
	public String datePicture;

	/** 主键 */
	public int getKey() {
		return this.iD; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}