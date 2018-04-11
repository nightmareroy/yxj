package com.wanniu.game.data; 

public class RewardListCO { 

	/** 编号 */
	public int iD;
	/** 名称 */
	public String name;
	/** 代码 */
	public String code;
	/** 竞拍底价 */
	public int auctionMin;
	/** 一口价 */
	public int auctionMax;
	/** 加价额 */
	public int addPrice;

	/** 主键 */
	public int getKey() {
		return this.iD; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}