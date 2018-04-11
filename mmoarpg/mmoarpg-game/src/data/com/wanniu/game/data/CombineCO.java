package com.wanniu.game.data; 

public class CombineCO { 

	/** 编号 */
	public int destID;
	/** 合成目标物代码 */
	public String destCode;
	/** 所需物品1代码 */
	public String srcCode1;
	/** 所需物品1数量 */
	public int srcCount1;
	/** 所需物品2代码 */
	public String srcCode2;
	/** 所需物品2数量 */
	public int srcCount2;
	/** 所需物品3代码 */
	public String srcCode3;
	/** 所需物品3数量 */
	public int srcCount3;
	/** 金币消耗 */
	public int costGold;
	/** 是否播走马灯公告 */
	public int isNotice;

	/** 主键 */
	public int getKey() {
		return this.destID; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}