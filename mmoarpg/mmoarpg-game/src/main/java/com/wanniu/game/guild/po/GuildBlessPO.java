package com.wanniu.game.guild.po;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.wanniu.game.common.Const;
import com.wanniu.game.guild.guidDepot.GuildRecordData;
import com.wanniu.game.guild.guildTech.GuildTechData;

public class GuildBlessPO {
	public static class GuildBlessItem {
		public int id;
		public int finishNum;
		public int needNum;
	}

	public static class GuildBlessAllBlob {
		public int blessValue;
		public int blessValueMax;
		public ArrayList<GuildRecordData> news;
		public Date refreshTime;
		public Map<Integer, GuildBlessItem> blessItems;
		public int blessLevel;
		public ArrayList<Integer> goods;
		public GuildTechData techData;
		public int[] finishStateArr;
		public int throwAwardState;

		public GuildBlessAllBlob() {
			news = new ArrayList<GuildRecordData>();
			refreshTime = new Date(0);
			blessItems = new HashMap<Integer, GuildBlessItem>();
			goods = new ArrayList<Integer>();
			techData = new GuildTechData();
			finishStateArr = new int[Const.NUMBER_MAX.GUILD_FINISHED_MAX];
			for (int i = 0; i < finishStateArr.length; i++) {
				this.finishStateArr[i] = 0;
			}
		}
	}

	public String id;
	public int logicServerId;
	public int level;
	public Date createTime;
	public List<Map<String, Integer>> gifts; // 祈福分段礼包奖励
	public GuildBlessAllBlob allBlobData;

	public GuildBlessPO() {
		id = "";
		createTime = new Date(0);
		allBlobData = new GuildBlessAllBlob();
		gifts = new ArrayList<>();
	}
}
