package com.wanniu.game.guild;

import java.util.ArrayList;

import com.wanniu.game.common.Const;
import com.wanniu.game.guild.guidDepot.GuildDepotCondition;
import com.wanniu.game.guild.guildTech.GuildTechData;
import com.wanniu.game.guild.po.GuildBlessPO;

import pomelo.area.GuildDepotHandler.DepotLevelInfo;
import pomelo.area.GuildHandler.ContributeTimesInfo;

public class GuildMsg {
	public int notifyType;
	public GuildMsgData data; // push 协议数据

	public GuildMsg(int notifyType, GuildMsgData data) {
		this.notifyType = notifyType;
		this.data = data;
	}

	// =====================GuildMsgData begin======================
	public static abstract class GuildMsgData {

	}

	public static class RefreshGuildMsg extends GuildMsgData {
		public int isIn; // 进入公会
		public int isOut; // 退出公会
		public int job;
		public String jobName;
		public String guildName;
		public ArrayList<ContributeTimesInfo> timesList;

		public RefreshGuildMsg() {
			jobName = "";
			guildName = "";
			timesList = new ArrayList<ContributeTimesInfo>();
		}
	}

	public static class JoinGuildBlessMsg extends RefreshGuildMsg {
		public GuildBlessPO blessData;

		public JoinGuildBlessMsg() {
			blessData = new GuildBlessPO();
		}
	}

	public static class DepotRefreshGuildMsg extends GuildMsgData {
		public int type;
		public int bagIndex;
		public DepotLevelInfo levelInfo;
		public GuildDepotCondition condition;

		public DepotRefreshGuildMsg() {
			levelInfo = DepotLevelInfo.newBuilder().build();
			condition = new GuildDepotCondition();
		}
	}

	public static class BlessRefreshGuildMsg extends GuildMsgData {
		public int type;
		public int blessValue;
		public int[] finishStateArr;

		public BlessRefreshGuildMsg() {
			finishStateArr = new int[Const.NUMBER_MAX.GUILD_FINISHED_MAX];
		}
	}

	public static class ShopRefreshGuildMsg extends GuildMsgData {

	}

	public static class TechRefreshGuildMsg extends GuildMsgData {
		public GuildTechData techData;

		public TechRefreshGuildMsg() {
			techData = new GuildTechData();
		}
	}

	public static class DungeonPassGuildMsg extends GuildMsgData {
		public int dungeonCount;
	}

	public static class DungeonPlayerNumGuildMsg extends GuildMsgData {
		public int playerNum;
	}

	public static class OnChatGuildMsg extends GuildMsgData {
		public String playerId;
	}

	// ======================GuildMsgData end=====================
}
