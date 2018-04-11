package com.wanniu.game.guild.guildFort.dao;

public class GuildFortBidderPO {
	public String guildId = "";
	public int fund = 0;
	public GuildFortBidderPO() {
		
	}
	
	public GuildFortBidderPO(String guildId,int fund) {
		this.guildId = guildId;
		this.fund = fund;
	}
}
