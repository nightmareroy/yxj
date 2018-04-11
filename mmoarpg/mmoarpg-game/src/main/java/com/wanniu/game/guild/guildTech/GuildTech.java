package com.wanniu.game.guild.guildTech;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.wanniu.game.common.Const;
import com.wanniu.game.common.Utils;
import com.wanniu.game.data.GTechnologyItemCO;
import com.wanniu.game.data.GTechnologyLevelCO;
import com.wanniu.game.data.GuildBuildingCO;
import com.wanniu.game.guild.GuildMsg;
import com.wanniu.game.guild.GuildMsg.TechRefreshGuildMsg;
import com.wanniu.game.guild.GuildService;
import com.wanniu.game.guild.GuildUtil;
import com.wanniu.game.guild.guildTech.GuildTechData.GuildTechBlob;
import com.wanniu.game.player.WNPlayer;

public class GuildTech {
	/**
	 * 给定具有权值的对象数组，根据对象的权值随机获得数组下标
	 * 
	 * @param objArr 对象数组,必需保护weight字段 [{id:'apple', weight:'30'}]
	 * @param weightKey 权值对应的索引键
	 * @returns {number} 成功返回数组下标索引，失败返回-1
	 */
	public int randIndexByWeight(ArrayList<Map<String, Integer>> objArr, String weightKey) {
		if (objArr.size() == 0) {
			return -1;
		}

		String key = "weight";
		if (null != weightKey) {
			key = weightKey;
		}
		// 计算总权值
		int totalWeight = 0;
		for (int i = 0; i < objArr.size(); ++i) {
			totalWeight += objArr.get(i).get(key);
		}
		int randNum = Utils.random(1, totalWeight);
		int totalNum = 0;
		for (int i = 0; i < objArr.size(); ++i) {
			totalNum += objArr.get(i).get(key);
			if (randNum <= totalNum) {
				return i;
			}
		}
		return -1;
	};

	public String id;
	public int level;
	public int buffLevel;
	public int refreshLevel;
	public Map<String, Integer> products;

	public GuildTech(GuildTechData techData) {
		this.id = techData.id;
		GuildTechBlob blobData = techData.blobData;
		this.level = blobData.level; // 科技等级
		this.buffLevel = blobData.buffLevel; // 增益等级
		this.refreshLevel = blobData.refreshLevel; // 刷新时等级
		this.products = blobData.products; // {"1": 3} key:ID, value:num
		if (null != this.products && this.products.size() > 0) {
			this.randomProduce();
		}
	}

	public GuildTech(String guildId) {
		this.createDefaultTech(guildId);
	}

	public void createDefaultTech(String guildId) {
		GuildBuildingCO buildProp = GuildUtil.getGuildBuildingProp(Const.GuildBuilding.TECH.getValue());
		this.id = guildId;
		this.level = buildProp.minLv; // 科技等级
		this.buffLevel = 0; // 增益等级
		this.refreshLevel = this.level;
		this.products = new HashMap<String, Integer>(); // {"1": 3} key:ID,
														// value:num
		this.randomProduce();
	};

	public void randomProduce() {
		this.refreshLevel = this.level;
		if (null != this.products) {
			this.products.clear();
		}
		Map<Integer, GTechnologyItemCO> producePropList = GuildUtil.getTechProducePropList();
		GTechnologyLevelCO levelProp = GuildUtil.getTechLevelPropByLevel(this.refreshLevel);

		ArrayList<Map<String, Integer>> produceList = new ArrayList<Map<String, Integer>>();
		for (GTechnologyItemCO prop : producePropList.values()) {
			Map<String, Integer> item = new HashMap<String, Integer>();
			item.put("iD", prop.iD);
			item.put("pro", prop.pro);
			item.put("count", prop.count);
			produceList.add(item);
		}

		int kindNum = (levelProp.techItemDayCount < produceList.size()) ? levelProp.techItemDayCount : produceList.size();
		for (int i = 0; i < kindNum; ++i) {
			int index = randIndexByWeight(produceList, "pro");
			if (index == -1) {
				break;// 配置错误，随机失败
			}
			Map<String, Integer> item = produceList.get(index);
			this.products.put(Integer.toString(item.get("iD")), item.get("count"));
			produceList.remove(index);// 已随出的东西从随机列表中删除，避免重复随机
		}

	};

	public void refreshNewDay(boolean isPush) {
		this.randomProduce();
		if (isPush) {
			TechRefreshGuildMsg msgData = new TechRefreshGuildMsg();
			msgData.techData.blobData.products = this.products;
			GuildMsg msg = new GuildMsg(Const.NotifyType.TECH_NEW_DAY_PUSH.getValue(), msgData);
			GuildService.notifyAllMemberRefreshGuild(this.id, msg, null);
		}
	}

	public GuildTechData toJson4Serialize() {
		GuildTechData data = new GuildTechData();
		data.id = this.id;
		data.blobData = new GuildTechBlob();
		data.blobData.level = this.level;
		data.blobData.buffLevel = this.buffLevel;
		data.blobData.refreshLevel = this.refreshLevel;
		data.blobData.products = this.products;
		return data;
	};

	// 废弃,直接使用toJson4Serialize
	// public void toJson4Area(){
	// var data = {};
	// data.level = this.level;
	// data.buffLevel = this.buffLevel;
	// data.products = this.products;
	// return data;
	// };

	public void upgradeLevel(WNPlayer player) {
		this.level += 1;
		TechRefreshGuildMsg msgData = new TechRefreshGuildMsg();
		msgData.techData.blobData.level = this.level;
		GuildMsg msg = new GuildMsg(Const.NotifyType.TECH_LEVEL_PUSH.getValue(), msgData);
		GuildService.notifyAllMemberRefreshGuild(this.id, msg, player.getId());
	}

	public void upgradeBuffLevel(WNPlayer player) {
		this.buffLevel += 1;
		TechRefreshGuildMsg msgData = new TechRefreshGuildMsg();
		msgData.techData.blobData.buffLevel = this.buffLevel;
		GuildMsg msg = new GuildMsg(Const.NotifyType.TECH_BUFF_LEVEL_PUSH.getValue(), msgData);
		GuildService.notifyAllMemberRefreshGuild(this.id, msg, player.getId());
	};

}
