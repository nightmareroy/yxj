package com.wanniu.game.data; 

public class SoloMonsterCO { 

	/** 职业编号 */
	public int proID;
	/** 职业名称 */
	public String proName;
	/** 怪物编号 */
	public int monID;
	/** 怪物名字 */
	public String monName;
	/** 路点ID */
	public int startPoint;
	/** 怪物显示等级 */
	public int monLevel;
	/** 是否有效 */
	public int availably;

	/** 主键 */
	public int getKey() {
		return this.proID; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}