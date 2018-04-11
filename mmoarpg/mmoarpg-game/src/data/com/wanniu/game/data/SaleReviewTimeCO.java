package com.wanniu.game.data; 

public class SaleReviewTimeCO { 

	/** 上架元宝最小值 */
	public int minDiamond;
	/** 上架元宝最大值 */
	public int maxDiamond;
	/** 审核时间最小值 */
	public int minTime;
	/** 审核时间最大值 */
	public int maxTime;

	/** 主键 */
	public int getKey() {
		return this.minDiamond; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}