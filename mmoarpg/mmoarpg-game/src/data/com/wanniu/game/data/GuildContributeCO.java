package com.wanniu.game.data; 

public class GuildContributeCO { 

	/** 捐献名称 */
	public String contribute;
	/** 类型 */
	public int type;
	/** 消耗 */
	public String costItem;
	/** 消耗数量 */
	public int costAmount;
	/** 获得贡献 */
	public int guildPoints;
	/** 增加威望 */
	public int guildExp;
	/** 增加资金 */
	public int guildFunds;
	/** 次数 */
	public int time;

	/** 主键 */
	public String getKey() {
		return this.contribute; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}