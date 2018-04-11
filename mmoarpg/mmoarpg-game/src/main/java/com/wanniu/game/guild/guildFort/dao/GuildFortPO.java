package com.wanniu.game.guild.guildFort.dao;

import java.util.ArrayList;
import java.util.List;

public class GuildFortPO {
	public int fortId;
	public String occupyGuildId = null;

	public List<GuildFortBidderPO> bidders = new ArrayList<>();// list of bid contender
}
