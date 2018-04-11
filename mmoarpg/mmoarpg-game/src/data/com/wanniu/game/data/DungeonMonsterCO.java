package com.wanniu.game.data; 

public class DungeonMonsterCO { 

	/** 职业编号 */
	public int proID;
	/** 职业名称 */
	public String proName;
	/** 怪物编号 */
	public int monID1;
	/** 怪物名字 */
	public String monName1;
	/** 路点ID */
	public int startPoint1;
	/** 怪物编号 */
	public int monID2;
	/** 怪物名字 */
	public String monName2;
	/** 路点ID */
	public int startPoint2;
	/** 怪物编号 */
	public int monID3;
	/** 怪物名字 */
	public String monName3;
	/** 路点ID */
	public int startPoint3;
	/** 怪物编号 */
	public int monID4;
	/** 怪物名字 */
	public String monName4;
	/** 路点ID */
	public int startPoint4;

	/** 主键 */
	public int getKey() {
		return this.proID; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}