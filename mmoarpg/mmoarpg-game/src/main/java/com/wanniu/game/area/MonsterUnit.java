package com.wanniu.game.area;

public class MonsterUnit {

	public String uuid;

	/** 怪物模板ID */
	public int id;

	public int force;

	/** 刷新点名字 */
	public String flag;

	public boolean autoGuard;
	/** 同一个Area是否只能同时有一个怪存在*/
	public boolean unique = false;
	
	public int x;

	public int y;

	/** 是否是任务 共享怪 */
	public int shareType;
	
	/** 动态怪物等级，战斗服根据此计算怪物的动态数值	 */
	public int level;
	
	
}
