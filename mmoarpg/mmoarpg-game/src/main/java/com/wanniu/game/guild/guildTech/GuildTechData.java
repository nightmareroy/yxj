package com.wanniu.game.guild.guildTech;

import java.util.HashMap;
import java.util.Map;

public class GuildTechData {

	public static class GuildTechBlob {
		public int level;
		public int buffLevel;
		public int refreshLevel;
		public Map<String, Integer> products;

		public GuildTechBlob() {
			products = new HashMap<String, Integer>();
		}
	}

	public String id;
	public GuildTechBlob blobData;

	public GuildTechData() {
		blobData = new GuildTechBlob();
	}
}
