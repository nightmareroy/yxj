package com.wanniu.game.data; 

public class BlessLevelCO { 

	/** 等级 */
	public int blessLevel;
	/** 需要资金 */
	public int funds;
	/** 需要金币 */
	public int gold;
	/** 需要公会等级 */
	public int gLevel;
	/** 道具种类 */
	public int itemKind;
	/** 可能BUFF */
	public String blessBuff;
	/** BUFF条目数 */
	public int blessBuffNum;
	/** 每次增加仙盟资金 */
	public int addGuildFunds;
	/** 每次增加仙盟贡献 */
	public int addGuildPoints;
	/** 可祈福次数 */
	public int blessTime;
	/** BUFF持续时间 */
	public int bufftime;

	/** 主键 */
	public int getKey() {
		return this.blessLevel; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}