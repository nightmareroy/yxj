package com.wanniu.game.guild.guildImpeach;

import java.util.ArrayList;
import java.util.Date;

public class GuildImpeachData {
	public static class Sponsor {
		public String id;
		public int pro;
		public String name;
	}

	public String id;
	public int logicServerId;
	public Date logoutTime;
	public Date createTime;
	public ArrayList<String> playerIds;
	public Sponsor sponsor;

	public GuildImpeachData() {
		logoutTime = new Date(0);
		createTime = new Date(0);
		playerIds = new ArrayList<String>();
		sponsor = new Sponsor();
	}
}
