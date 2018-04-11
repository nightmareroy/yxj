package com.wanniu.game.guild.guidDepot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import com.wanniu.core.util.DateUtil;
import com.wanniu.game.common.Const;
import com.wanniu.game.data.GuildBuildingCO;
import com.wanniu.game.data.WareHouseLevelCO;
import com.wanniu.game.data.ext.GuildSettingExt;
import com.wanniu.game.guild.GuildMsg;
import com.wanniu.game.guild.GuildMsg.DepotRefreshGuildMsg;
import com.wanniu.game.guild.GuildService;
import com.wanniu.game.guild.GuildUtil;
import com.wanniu.game.guild.RecordInfo;
import com.wanniu.game.guild.RoleInfo;
import com.wanniu.game.item.NormalItem;
import com.wanniu.game.item.po.PlayerItemPO;
import com.wanniu.game.player.GlobalConfig;
import com.wanniu.game.poes.GuildDepotPO;

import pomelo.area.GuildDepotHandler.DepotLevelInfo;

public class GuildDepot {
	public GuildDepotPO depotData;
	public EquipBag bag;
	public int deleteCount;

	public GuildDepot(GuildDepotPO depotData) {
		this.depotData = depotData;
		this.bag = new EquipBag(depotData.bag);

		WareHouseLevelCO levelProp = GuildUtil.getDepotLevelProp(this.depotData.level);
		this.bag.bagTotalCount = levelProp.spece;
	}

	public static GuildDepot createEmptyDepot(String guildId, int logicServerId) {
		GuildDepotPO depotData = new GuildDepotPO();
		GuildBuildingCO buildProp = GuildUtil.getGuildBuildingProp(Const.GuildBuilding.DEPOT.getValue());
		depotData.id = guildId;
		depotData.logicServerId = logicServerId;
		depotData.level = buildProp.minLv;
		depotData.createTime = new Date();
		int maxUpLevel = GlobalConfig.Role_Upgrade_LevelLimit;
		// 初始条件
		GuildSettingExt settingProp = GuildUtil.getGuildSettingExtProp();

		GuildDepotCondition condition = new GuildDepotCondition();
		GuildCond useCond = new GuildCond();
		useCond.level = settingProp.joinLv;
		useCond.upLevel = 0;
		useCond.job = Const.GuildJob.MEMBER.getValue();
		condition.useCond = useCond;

		GuildCond minCond = new GuildCond();
		minCond.level = settingProp.warehouseMinLv;
		minCond.upLevel = 0;
		minCond.qColor = settingProp.warehouseMinQ;
		condition.minCond = minCond;

		GuildCond maxCond = new GuildCond();
		maxCond.level = 100;
		maxCond.upLevel = maxUpLevel;
		maxCond.qColor = 5;
		condition.maxCond = maxCond;

		WareHouseLevelCO levelProp = GuildUtil.getDepotLevelProp(1);
		depotData.bag.bagGridCount = levelProp.spece;
		depotData.bag.bagGrids = new HashMap<Integer, PlayerItemPO>();
		depotData.bag.bagTotalCount = levelProp.spece;

		depotData.condition = condition;
		depotData.news = new ArrayList<GuildRecordData>();
		depotData.deleteCount = 0;

		return new GuildDepot(depotData);
	}

	public int getDeleteCount() {
		if (null == this.depotData.refreshTime || DateUtil.canRefreshData(Const.REFRSH_NEW_DAY_TIME, this.depotData.refreshTime)) {
			this.deleteCount = 0;
			this.depotData.refreshTime = new Date();
		}
		return this.deleteCount;
	}

	public GuildDepotPO toJson4Serialize() {
		GuildDepotPO data = new GuildDepotPO();
		data.id = this.depotData.id;
		data.logicServerId = this.depotData.logicServerId;
		data.level = this.depotData.level;
		data.createTime = this.depotData.createTime;
		data.condition = this.depotData.condition;
		data.news = this.depotData.news;
		data.bag = this.bag.toJson4Serialize();
		data.deleteCount = this.getDeleteCount();
		data.refreshTime = this.depotData.refreshTime;
		return data;
	}

	public GuildDepotPO getDepotInfo() {
		GuildDepotPO data = new GuildDepotPO();
		GuildSettingExt settingProp = GuildUtil.getGuildSettingExtProp();
		data.level = this.depotData.level;
		data.condition.useCond = this.depotData.condition.useCond;
		data.condition.minCond = this.depotData.condition.minCond;
		data.condition.maxCond = this.depotData.condition.maxCond;
		data.deleteCount = this.getDeleteCount();
		data.deleteCountMax = settingProp.warehouseDel;
		return data;
	}

	public ArrayList<RecordInfo> getRecordList(int page) {
		ArrayList<RecordInfo> list = new ArrayList<RecordInfo>();
		int perPageNum = 50;
		int startIndex = (page - 1) * perPageNum;
		int endIndex = startIndex + perPageNum;
		for (int i = startIndex; i < this.depotData.news.size() && i < endIndex; ++i) {
			GuildRecordData record = this.depotData.news.get(i);
			RecordInfo tempInfo = new RecordInfo();
			tempInfo.time = DateUtil.format(record.time, "MM-dd HH:mm:ss");
			if (null != record.role1 && record.role1.pro > 0) {
				RoleInfo role1 = new RoleInfo();
				role1.pro = record.role1.pro;
				role1.name = record.role1.name;
				tempInfo.role1 = role1;
			}

			if (null != record.role2 && record.role2.pro > 0) {
				RoleInfo role2 = new RoleInfo();
				role2.pro = record.role2.pro;
				role2.name = record.role2.name;
				tempInfo.role2 = role2;
			}

			if (null != record.result.v2 && !record.result.v2.isEmpty()) {
				tempInfo.resultStr = record.result.v2;
			}

			if (null != record.item && !record.item.name.isEmpty()) {
				tempInfo.item = record.item;
			}

			tempInfo.recordType = record.type;
			list.add(tempInfo);
		}
		return list;
	}

	public boolean testEmptyGridLarge(int num) {
		int actNum = num > 0 ? num : 1;
		return (this.bag.emptyGridNum() >= actNum);
	}

	public void saveToRedis() {
		GuildDepotPO data = this.toJson4Serialize();
		GuildUtil.updateGuildDepot(data);
	}

	public int addEquip(NormalItem equip, String playerId) {
		int addIndex = this.bag.addItem(equip);
		if (addIndex < 0) {
			return addIndex;
		}
		// 通知bagIndex所在索引增加道具
		this.saveToRedis();
		DepotRefreshGuildMsg msgData = new DepotRefreshGuildMsg();
		msgData.bagIndex = addIndex;
		GuildMsg msg = new GuildMsg(Const.NotifyType.DEPOT_DEPOSIT_PUSH.getValue(), msgData);
		GuildService.notifyAllMemberRefreshGuild(this.depotData.id, msg, playerId);
		return addIndex;
	}

	// isDelete true为删除，false或不填，则不是删除
	public boolean removeEquip(int bagIndex, String playerId, boolean isDelete) {
		int removeIndex = this.bag.removeItemByPos(bagIndex);
		if (removeIndex < 0) {
			return false;
		}

		if (isDelete) {
			this.deleteCount += 1;
		}

		// 通知bagIndex所在索引增加道具
		this.saveToRedis();
		DepotRefreshGuildMsg msgData = new DepotRefreshGuildMsg();
		msgData.bagIndex = removeIndex;
		GuildMsg msg = new GuildMsg(Const.NotifyType.DEPOT_REMOVE_PUSH.getValue(), msgData);
		GuildService.notifyAllMemberRefreshGuild(this.depotData.id, msg, playerId);
		return true;
	}

	public NormalItem getEquip(int index) {
		return this.bag.getItem(index);
	}

	public boolean setCondition(GuildDepotCondition cond, String playerId) {
		if (!GuildUtil.checkCondition(cond)) {
			return false;
		}

		this.depotData.condition.useCond = cond.useCond;
		this.depotData.condition.minCond = cond.minCond;
		this.depotData.condition.maxCond = cond.maxCond;

		this.saveToRedis();
		DepotRefreshGuildMsg msgData = new DepotRefreshGuildMsg();
		msgData.condition = this.depotData.condition;
		GuildMsg msg = new GuildMsg(Const.NotifyType.DEPOT_CONDITION_PUSH.getValue(), msgData);
		GuildService.notifyAllMemberRefreshGuild(this.depotData.id, msg, playerId);
		return true;
	}

	public DepotLevelInfo getNotifyInfo() {
		DepotLevelInfo.Builder data = DepotLevelInfo.newBuilder();
		data.setLevel(this.depotData.level);
		data.setBagGridCount(this.bag.bagGridCount);
		data.setBagTotalCount(this.bag.bagTotalCount);
		return data.build();
	}

	public void upgradeLevel(String playerId) {
		this.depotData.level += 1;
		// 通知仓库升级
		WareHouseLevelCO levelProp = GuildUtil.getDepotLevelProp(this.depotData.level);
		this.bag.bagGridCount = levelProp.spece;
		this.bag.bagTotalCount = levelProp.spece;
		this.saveToRedis();

		DepotRefreshGuildMsg msgData = new DepotRefreshGuildMsg();
		msgData.levelInfo = this.getNotifyInfo();
		GuildMsg msg = new GuildMsg(Const.NotifyType.DEPOT_UPGRADE_PUSH.getValue(), msgData);

		GuildService.notifyAllMemberRefreshGuild(this.depotData.id, msg, playerId);
	}

	public void addRecord(GuildRecordData record, boolean save) {
		record.time = new Date();
		GuildSettingExt settingProp = GuildUtil.getGuildSettingExtProp();
		this.depotData.news.add(0, record);
		int len = this.depotData.news.size();
		while (len > settingProp.recording) {
			this.depotData.news.remove(len - 1);
		}

		if (save) {
			this.saveToRedis();
		}
	}
}
