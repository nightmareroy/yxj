package com.wanniu.game.guild;

public class GuildRandomItem {
	public int id;
	public int weight;
	public int minNum;
	public int maxNum;

	public int getPropertyValue(String key) {
		if (key.equals("id")) {
			return id;
		} else if (key.equals("weight")) {
			return weight;
		} else if (key.equals("minNum")) {
			return minNum;
		} else if (key.equals("maxNum")) {
			return maxNum;
		}
		return 0;
	}
}
