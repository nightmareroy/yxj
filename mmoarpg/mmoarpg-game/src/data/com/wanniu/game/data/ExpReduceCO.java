package com.wanniu.game.data; 

public class ExpReduceCO { 

	/** 序号 */
	public int iD;
	/** 最低等级 */
	public int minLv;
	/** 最高等级 */
	public int maxLv;
	/** 20%衰减比例 */
	public int rate1;
	/** 40%衰减比例 */
	public int rate2;
	/** 60%衰减比例 */
	public int rate3;
	/** 80%衰减比例 */
	public int rate4;

	/** 主键 */
	public int getKey() {
		return this.iD; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}