package com.wanniu.game.guild.guidDepot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.wanniu.core.game.JobFactory;
import com.wanniu.game.common.Const;
import com.wanniu.game.guild.GuildServiceCenter;
import com.wanniu.game.guild.GuildUtil;
import com.wanniu.game.guild.RecordInfo;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.poes.GuildDepotPO;
import com.wanniu.game.poes.GuildMemberPO;

import pomelo.item.ItemOuterClass.ItemDetail;

public class GuildDepotCenter {

	private static GuildDepotCenter instance;

	public static GuildDepotCenter getInstance() {
		if (instance == null) {
			instance = new GuildDepotCenter();
		}

		return instance;
	}

	public Map<String, GuildDepot> depotMap;

	private GuildDepotCenter() {
		depotMap = new HashMap<String, GuildDepot>();
		init();
	}

	public void init() {
		initFromRedis();
		// 定时器 后面调试评估用这个定时器存储数据的消耗，待优化
		JobFactory.addDelayJob(new Runnable() {
			public void run() {
				saveAllDepot();
			}
		}, Const.Time.Minute.getValue());
	}

	public GuildDepot getDepot(String guildId) {
		return depotMap.get(guildId);
	}

	public void initFromRedis() {
		ArrayList<GuildDepotPO> depotList = GuildUtil.getGuildDepotList();
		for (GuildDepotPO depotData : depotList) {
			GuildDepot depot = new GuildDepot(depotData);
			depotMap.put(depot.depotData.id, depot);
		}
	}

	public void createDepot(String guildId, int logicServerId) {
		GuildDepot depot = getDepot(guildId);
		if (null == depot) {
			depot = GuildDepot.createEmptyDepot(guildId, logicServerId);
			depotMap.put(depot.depotData.id, depot);
			// 存储
			saveDepot(guildId);
		}
	}

	public void saveAllDepot() {
		for (String key : depotMap.keySet()) {
			saveDepot(key);
		}
	}

	public void saveDepot(String id) {
		GuildDepot depot = getDepot(id);
		if (null == depot) {
			return;
		}
		GuildDepotPO depotData = depot.toJson4Serialize();
		GuildUtil.updateGuildDepot(depotData);
	}

	public PlayerGuildDepot getDepotDataByPlayerId(WNPlayer player) {
		PlayerGuildDepot data = new PlayerGuildDepot();
		GuildMemberPO myInfo = GuildServiceCenter.getInstance().getGuildMember(player.getId());
		if (null == myInfo) {
			return null;
		}

		GuildDepot depot = getDepot(myInfo.guildId);
		if (null == depot) {
			return data;
		}

		data.depotInfo = depot.getDepotInfo();
		data.bagInfo = depot.bag.toJson4Payload();
		data.detailInfo = depot.bag.getAllEquipDetails4PayLoad(player.playerBasePO);
		return data;
	}

	public ArrayList<RecordInfo> getDepotRecordByPlayerId(String playerId, int page) {
		ArrayList<RecordInfo> data = new ArrayList<RecordInfo>();
		GuildMemberPO myInfo = GuildServiceCenter.getInstance().getGuildMember(playerId);
		if (null == myInfo) {
			return null;
		}

		GuildDepot depot = getDepot(myInfo.guildId);
		if (null == depot) {
			return data;
		}

		data = depot.getRecordList(page);
		return data;
	}

	public ArrayList<ItemDetail> getDepotDetailsByPlayerId(WNPlayer player) {
		ArrayList<ItemDetail> data = new ArrayList<ItemDetail>();
		GuildMemberPO myInfo = GuildServiceCenter.getInstance().getGuildMember(player.getId());
		if (null == myInfo) {
			return null;
		}
		GuildDepot depot = getDepot(myInfo.guildId);
		if (null == depot) {
			return null;
		}
		data = depot.bag.getAllEquipDetails4PayLoad(player.playerBasePO);
		return data;
	}

	public GuildDepotOneGrid getDepotOneGridInfoByPlayerId(WNPlayer player, int bagIndex) {
		GuildDepotOneGrid data = new GuildDepotOneGrid();
		GuildMemberPO myInfo = GuildServiceCenter.getInstance().getGuildMember(player.getId());
		if (null == myInfo) {
			return null;
		}

		GuildDepot depot = getDepot(myInfo.guildId);
		if (null == depot) {
			return null;
		}

		data = depot.bag.getGridAndDetailByIndex(player.playerBasePO, bagIndex);

		return data;
	}

}
