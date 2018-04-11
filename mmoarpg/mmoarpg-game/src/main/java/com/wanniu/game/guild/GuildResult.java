package com.wanniu.game.guild;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.wanniu.game.common.Const;
import com.wanniu.game.guild.guidDepot.GuildDepotCondition;
import com.wanniu.game.guild.guildTech.GuildTechData;
import com.wanniu.game.item.po.PlayerItemPO;

import pomelo.area.GuildHandler.MemberInfo;
import pomelo.item.ItemOuterClass.Grid;

//为公会返回结果设置
public class GuildResult {
	public int result;
	public String des;
	public GuildResultData data;
	public String id;
	public boolean joined;
	public String memberId;
	public Grid bagGrid;
	public String name;
	public int leftKickNum;
	public int needLevel;
	public int isNew;
	public PlayerItemPO itemData;
	public int deleteCount;
	public int addReadFund;
	public int addRealExp;
	public GuildDepotCondition newCondition;
	public GuildTechData techData;
	public int depositCount;
	public ArrayList<Integer> goods;
	public String newNotice;
	public int state;
	public String cdInfo;

	public GuildResult() {

	}

	public static abstract class GuildResultData {

	}

	public static class UpgradeGuildLvData extends GuildResultData {
		public int preLevel;
		public long preExp;
		public long exp;
		public int level;
		public int costExp;
	}

	public static class TransferGuildPresidentData extends GuildResultData {
		public String preId;
		public String preName;
		public String nowId;
		public String nowName;
		public int newJob;

		public TransferGuildPresidentData() {
			preId = "";
			preName = "";
			nowId = "";
			nowName = "";
		}
	}

	public static class DepotUpgradeLevelData extends GuildResultData {
		public int costGoldNum;
		public int newLevel;
		public long fund;
		public String nowName;
		public String id;
		public String name;
		public int preLevel;

		public DepotUpgradeLevelData() {
			nowName = "";
			id = "";
			name = "";
		}
	}

	public static class PlayerOnlineRefreshGuild extends GuildResultData {
		public int isInGuild;
		public ArrayList<Integer> goods;
		public GuildTechData techData;
		public Date refreshTime;
		public int[] finishStateArr;
		public int throwAwardState;

		public PlayerOnlineRefreshGuild() {
			goods = new ArrayList<Integer>();
			techData = new GuildTechData();
			refreshTime = new Date(0);
			finishStateArr = new int[Const.NUMBER_MAX.GUILD_FINISHED_MAX];
		}
	}

	public static class GuildBlessActionData extends GuildResultData {
		public int blessValue;
		public int id;
		public int finishNum;
		public int buffTime;
		public List<Integer> buffIds;
		public int[] finishState;
		public int blessCount;
		public List<Integer> receiveState;

		public GuildBlessActionData() {
			finishState = new int[Const.NUMBER_MAX.GUILD_FINISHED_MAX];
			receiveState = new ArrayList<Integer>();
			buffIds = new ArrayList<Integer>();
		}
	}

	public static class GuildGiftAndBuffData extends GuildResultData {
		public Map<String, Integer> itemCode;
		public ArrayList<Integer> buffIds;
		public List<Integer> receiveState;

		public GuildGiftAndBuffData() {
			itemCode = new HashMap<String, Integer>();
			buffIds = new ArrayList<Integer>();
			receiveState = new ArrayList<Integer>();
		}
	}

	public static class UpgradeLevel extends GuildResultData {
		public int level;
		public int needGold;
		public long fund;
		public String id;
		public String name;
		public int preLevel;
		public int buffLevel;

		public UpgradeLevel() {
			id = "";
			name = "";
		}
	}

	public static class JoinGuild extends GuildResultData {
		public int needUpLevel;
		public int needLevel;
		public String cdInfo;

		public JoinGuild() {
			cdInfo = "";
		}
	}

	public static class MyGuildMember extends GuildResultData {
		public int leftKickNum;
		public List<MemberInfo> list;

		public MyGuildMember() {
			list = new ArrayList<MemberInfo>();
		}
	}

	public static class GuildExchangeGoods extends GuildResultData {
		public int id;
		public int state;
		public int moneyType;
		public int condType;
	}

}
