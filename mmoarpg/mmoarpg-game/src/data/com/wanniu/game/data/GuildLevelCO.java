package com.wanniu.game.data; 

public class GuildLevelCO { 

	/** 等级 */
	public int position;
	/** 消耗威望 */
	public int exp;
	/** 需要资金 */
	public int funds;
	/** 需要金币 */
	public int gold;
	/** 需要祈福 */
	public int building1;
	/** 需要科技 */
	public int building2;
	/** 需要仓库 */
	public int building3;
	/** 公会人数上限 */
	public int member;
	/** 公会威望上限 */
	public int maxExpDay;
	/** 公会资金上限 */
	public int maxFundsDay;

	/** 主键 */
	public int getKey() {
		return this.position; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}