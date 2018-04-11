package com.wanniu.game.data.base; 

public abstract class SaleBase { 

	/** 编号ID */
	public int iD;
	/** 分类ID */
	public int typeID;
	/** 分类标签 */
	public String typeName;
	/** 编号ID */
	public int itemID;
	/** 物品显示名称 */
	public String itemShowName;
	/** 物品代码 */
	public String itemCode;
	/** 是否动态 */
	public int isDynamic;
	/** 购买后是否绑定 */
	public int isBind;
	/** 物品数量 */
	public int itemCount;
	/** 金币单价 */
	public int goldPrice;
	/** 金票单价 */
	public int cashPrice;
	/** 钻石单价 */
	public int diamondPrice;
	
	
	public int moneyType;
	public int needMoney;

	/** 主键 */
	public Integer getKey() {
		return this.iD; 
	}

	/** 构造前置属性 */
	public void beforeProperty() { }

	/** 构造属性 */
	public void initProperty() { 
		if (this.goldPrice > 0) {
	        this.moneyType = 1;
	        this.needMoney = this.goldPrice;
	    } else if (this.cashPrice > 0) {
	        this.moneyType = 2;
	        this.needMoney = this.cashPrice;
	    } else if (this.diamondPrice > 0) {
	        this.moneyType = 3;
	        this.needMoney = this.diamondPrice;
	    }
	}

}