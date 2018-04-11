package com.wanniu.game.guild.guidDepot;

public class GuildDepotCondition {
	public GuildCond useCond;
	public GuildCond minCond;
	public GuildCond maxCond;

	public GuildDepotCondition() {
		useCond = new GuildCond();
		minCond = new GuildCond();
		maxCond = new GuildCond();
	}
}
