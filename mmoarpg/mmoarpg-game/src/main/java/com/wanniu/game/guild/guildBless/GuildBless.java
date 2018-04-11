package com.wanniu.game.guild.guildBless;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.wanniu.core.logfs.Out;
import com.wanniu.core.util.DateUtil;
import com.wanniu.game.common.Const;
import com.wanniu.game.common.Const.PlayerBtlData;
import com.wanniu.game.common.Utils;
import com.wanniu.game.data.BlessItemCO;
import com.wanniu.game.data.GShopCO;
import com.wanniu.game.data.GameData;
import com.wanniu.game.data.GuildBuildingCO;
import com.wanniu.game.data.base.DItemEquipBase;
import com.wanniu.game.data.ext.BlessLevelExt;
import com.wanniu.game.data.ext.BlesslibaoExt;
import com.wanniu.game.data.ext.GShopExt;
import com.wanniu.game.data.ext.GuildSettingExt;
import com.wanniu.game.guild.GuildCommonUtil;
import com.wanniu.game.guild.GuildMsg;
import com.wanniu.game.guild.GuildMsg.BlessRefreshGuildMsg;
import com.wanniu.game.guild.GuildMsg.TechRefreshGuildMsg;
import com.wanniu.game.guild.GuildRandomItem;
import com.wanniu.game.guild.GuildResult;
import com.wanniu.game.guild.GuildResult.GuildBlessActionData;
import com.wanniu.game.guild.GuildResult.GuildGiftAndBuffData;
import com.wanniu.game.guild.GuildService;
import com.wanniu.game.guild.GuildUtil;
import com.wanniu.game.guild.RecordInfo;
import com.wanniu.game.guild.RoleInfo;
import com.wanniu.game.guild.guidDepot.GuildRecordData;
import com.wanniu.game.guild.guildTech.GuildTech;
import com.wanniu.game.guild.guildTech.GuildTechData;
import com.wanniu.game.guild.po.GuildBlessPO;
import com.wanniu.game.guild.po.GuildBlessPO.GuildBlessAllBlob;
import com.wanniu.game.guild.po.GuildBlessPO.GuildBlessItem;
import com.wanniu.game.item.ItemUtil;
import com.wanniu.game.player.WNPlayer;

import io.netty.util.internal.StringUtil;
import pomelo.guild.GuildManagerHandler;
import pomelo.guild.GuildManagerHandler.GuildBlessInfo;
import pomelo.item.ItemOuterClass.MiniItem;

public class GuildBless {
	public GuildBlessPO blessData;
	public String id;
	public int logicServerId;
	public int level;
	public Date createTime;
	public int blessValue;
	public int blessValueMax; // 祈福礼包进度最大值
	public ArrayList<GuildRecordData> news;
	public Date refreshTime;
	public Map<Integer, GuildBlessItem> blessItems;
	public Map<PlayerBtlData, Integer> bufsAttr;
	public int blessLevel;
	public ArrayList<Integer> goods;
	public GuildTechData techData;
	public int throwAwardState;
	public GuildTech tech;
	public List<Map<String, Integer>> giftLs; // 祈福分段礼包奖励
	public float[] blessProcess = { 0.3f, 0.6f, 1 }; // 祈福礼包分段完成度刻度，按完成30%，60%，100%发奖励

	/**
	 * 给定具有权值的对象数组，根据对象的权值随机获得数组下标
	 * 
	 * @param objArr 对象数组,必需保护weight字段 [{id:'apple', weight:'30'}]
	 * @param weightKey 权值对应的索引键
	 * @returns {number} 成功返回数组下标索引，是吧返回-1
	 */
	public int randIndexByWeight(List<GuildRandomItem> objArr, String weightKey) {
		if (objArr.size() == 0) {
			return -1;
		}
		String key = "weight";
		if (!StringUtil.isNullOrEmpty(weightKey)) {
			key = weightKey;
		}

		// 计算总权值
		int totalWeight = 0;
		for (int i = 0; i < objArr.size(); ++i) {
			GuildRandomItem randomItem = objArr.get(i);
			if (null == randomItem)
				continue;
			totalWeight += randomItem.getPropertyValue(key);
		}

		int randNum = Utils.random(1, totalWeight);
		int totalNum = 0;
		for (int i = 0; i < objArr.size(); ++i) {
			GuildRandomItem randomItem = objArr.get(i);
			if (null == randomItem)
				continue;
			totalNum += randomItem.getPropertyValue(key);
			if (randNum <= totalNum) {
				return i;
			}
		}
		return -1;
	}

	// 从数组中随机出num个不重复值,最大数量等于数组大小
	public <T> ArrayList<T> randomSomeFromArray(List<T> buffList, int num) {
		ArrayList<T> resultArray = new ArrayList<T>();
		if (null == buffList) {
			return resultArray;
		}

		ArrayList<T> cacheArr = new ArrayList<T>();
		for (int i = 0; i < buffList.size(); ++i) {
			cacheArr.add(buffList.get(i));
		}

		int randCount = num <= cacheArr.size() ? num : cacheArr.size();
		for (int i = 0; i < randCount; ++i) {
			int index = Utils.random(0, cacheArr.size() - 1);
			resultArray.add(cacheArr.get(index));
			cacheArr.remove(index);
		}
		return resultArray;
	}

	// 从map中随机出num个不重复值,最大数量等于数组大小
	public Map<String, Integer> randomSomeFromMap(Map<String, Integer> buffMap, int num) {
		Map<String, Integer> _map = new HashMap<String, Integer>();
		ArrayList<String> cacheArr = new ArrayList<String>();
		for (String key : buffMap.keySet()) {
			cacheArr.add(key);
		}

		int randCount = num <= cacheArr.size() ? num : cacheArr.size();
		for (int i = 0; i < randCount; ++i) {
			int index = Utils.random(0, cacheArr.size() - 1);
			String key = cacheArr.get(index);
			_map.put(key, buffMap.get(key));
			cacheArr.remove(index);
		}

		return _map;
	}

	public GuildBless(GuildBlessPO blessData, int logicServerId) {
		this();
		this.blessData = blessData;
		this.id = blessData.id; // 公会id
		this.logicServerId = blessData.logicServerId;
		this.level = blessData.level;
		this.createTime = blessData.createTime;

		// 二进制数据
		GuildBlessAllBlob blobData = blessData.allBlobData;
		this.blessValue = blobData.blessValue;
		this.blessValueMax = blobData.blessValueMax;
		this.news = blobData.news; // 动态日志
		this.refreshTime = blobData.refreshTime; // 刷新时间
		this.blessItems = blobData.blessItems;
		this.blessLevel = blobData.blessLevel; // 上次刷新时刻的祈福等级
		this.giftLs = blessData.gifts;
		this.refreshBlessProcess();

		// 商店物品
		this.goods = blobData.goods;
		if (null != this.goods && this.goods.size() == 0) {
			this.randomShopGoods();
		}
		// 科技
		if (null == blobData.techData) {
			this.tech = new GuildTech(this.id);
		} else {
			this.tech = new GuildTech(blobData.techData);
		}
	}

	public GuildBless() {
		this.giftLs = new ArrayList<>();
		this.blessItems = new HashMap<Integer, GuildBlessItem>();
		this.blessData = new GuildBlessPO();
		resetBlessAwardState();
	}

	public GuildBless(String guildId, int logicServerId) {
		this();
		this.createDefaultBless(guildId, logicServerId);
	}

	public void resetBlessAwardState() {
		for (int i = 0; i < this.blessData.allBlobData.finishStateArr.length; i++) {
			this.blessData.allBlobData.finishStateArr[i] = Const.EVENT_GIFT_STATE.NOT_RECEIVE.getValue();
		}
	}

	public void createDefaultBless(String guildId, int logicServerId) {
		GuildBuildingCO buildProp = GuildUtil.getGuildBuildingProp(Const.GuildBuilding.BLESS.getValue());
		this.id = guildId; // 公会id
		this.logicServerId = logicServerId;
		this.level = buildProp.minLv;
		this.createTime = new Date();

		this.blessValue = 0;
		this.blessValueMax = 0;
		this.refreshTime = new Date();
		this.blessData.allBlobData.refreshTime = this.refreshTime;
		this.news = new ArrayList<GuildRecordData>();
		this.blessItems = new HashMap<Integer, GuildBlessItem>();
		this.blessLevel = this.level;
		this.resetBlessAwardState();
		this.randomBlessItemAndProcess();// 随机生成祈福物品
		// 商店
		this.randomShopGoods();
		// 科技
		this.tech = new GuildTech(guildId);
	}

	public void refreshBlessProcess() {
		this.blessValueMax = 0;
		if (null == this.blessItems)
			return;

		for (GuildBlessItem item : this.blessItems.values()) {
			this.blessValueMax += item.needNum;
		}
	}

	public GuildBlessPO toJson4Serialize() {
		GuildBlessPO data = new GuildBlessPO();
		data.id = this.id;
		data.logicServerId = this.logicServerId;
		data.level = this.level;
		data.createTime = this.createTime;

		GuildBlessAllBlob blobData = new GuildBlessAllBlob();
		blobData.blessValue = this.blessValue;
		blobData.blessValueMax = this.blessValueMax;
		blobData.news = this.news;
		blobData.refreshTime = this.refreshTime;
		blobData.blessItems = this.blessItems;
		blobData.blessLevel = this.blessLevel;
		blobData.finishStateArr = this.blessData.allBlobData.finishStateArr;
		// 商店
		blobData.goods = this.goods;
		// 科技
		blobData.techData = this.tech.toJson4Serialize();

		data.allBlobData = blobData;
		data.gifts = this.giftLs;
		return data;
	}

	public void randomBlessItemAndProcess() {
		randomBlessItem();
		// 重新计算进度
		this.blessValue = 0;
		this.refreshBlessProcess();
	}

	public void randomBlessItem() {
		BlessLevelExt levelProp = GuildUtil.getBlessPropByLevel(this.level);
		List<BlessItemCO> itemPropList = GuildUtil.getBlessItemListByLevel(this.level);
		int kindNum = (levelProp.itemKind < itemPropList.size()) ? levelProp.itemKind : itemPropList.size();

		List<GuildRandomItem> baseItems = new ArrayList<GuildRandomItem>();
		Map<Integer, GuildBlessItem> resultItems = new HashMap<Integer, GuildBlessItem>();
		for (int i = 0; i < itemPropList.size(); ++i) {
			BlessItemCO prop = itemPropList.get(i);
			GuildRandomItem randomItem = new GuildRandomItem();
			randomItem.id = prop.iD;
			randomItem.weight = prop.pro;
			randomItem.minNum = Math.min(prop.minNeed, prop.maxNeed);
			randomItem.maxNum = Math.max(prop.minNeed, prop.maxNeed);
			baseItems.add(randomItem);
		}

		for (int i = 0; i < kindNum; ++i) {
			int index = randIndexByWeight(baseItems, null);
			if (-1 == index) {
				break;// 配置错误，随机失败
			}

			GuildRandomItem itemMap = baseItems.get(index);

			if (null != itemMap) {
				int minX = itemMap.minNum;
				int maxX = itemMap.maxNum;
				int needNum = Utils.random(minX, maxX);// 每日需要量
				GuildBlessItem blessItem = new GuildBlessItem();
				blessItem.id = itemMap.id;
				blessItem.finishNum = 0;
				blessItem.needNum = needNum;
				resultItems.put(blessItem.id, blessItem);
				baseItems.remove(index); // 已随出的东西从随机列表中删除，避免重复随机
			}
		}

		this.blessItems = resultItems;

		this.blessLevel = this.level;

		BlesslibaoExt giftProp = GameData.Blesslibaos.get(this.level);
		this.giftLs.clear();
		// 随机分段礼包奖励
		this.giftLs.add(randomSomeFromMap(giftProp.itemCode30, giftProp.blessBuffNum));
		this.giftLs.add(randomSomeFromMap(giftProp.itemCode60, giftProp.blessBuffNum));
		this.giftLs.add(randomSomeFromMap(giftProp.itemCode100, giftProp.blessBuffNum));
		blessData.gifts = this.giftLs;
	}

	// 刷新商店物品
	public void randomShopGoods() {
		List<GShopExt> itemPropList = GuildUtil.getShopPropList();
		List<GuildRandomItem> baseItems = new ArrayList<GuildRandomItem>();
		for (int i = 0; i < itemPropList.size(); ++i) {
			GShopCO prop = itemPropList.get(i);
			GuildRandomItem item = new GuildRandomItem();
			item.id = prop.itemID;
			item.weight = prop.pro;
			baseItems.add(item);
		}

		ArrayList<Integer> goodsIdArray = new ArrayList<Integer>();
		for (int i = 0; i < 8; ++i) {
			int index = randIndexByWeight(baseItems, null);
			if (index == -1) {
				break;// 配置错误，随机失败
			}
			goodsIdArray.add(baseItems.get(index).id);
			baseItems.remove(index); // 已随出的东西从随机列表中删除，避免重复随机
		}

		goodsIdArray.sort((a, b) -> { // 排序
			return a - b;
		});
		this.goods = goodsIdArray;
	}

	/**
	 * 获取今日祈福分段奖励状态数组 0：未完成 1：可领取 2：已领取
	 * 
	 * @return [1,1]
	 */
	public int[] calFinishState() {
		if (this.blessProcess.length != this.blessData.allBlobData.finishStateArr.length) {
			Out.error("blessProcess.length != this.finishStateArr.length");
			return null;
		}

		for (int i = 0; i < this.blessData.allBlobData.finishStateArr.length; i++) {
			if (Const.EVENT_GIFT_STATE.NOT_RECEIVE.getValue() == this.blessData.allBlobData.finishStateArr[i]) {
				if (this.blessValue >= Math.floor(this.blessValueMax * this.blessProcess[i])) {
					this.blessData.allBlobData.finishStateArr[i] = Const.EVENT_GIFT_STATE.CAN_RECEIVE.getValue();
				}
			}
		}

		return this.blessData.allBlobData.finishStateArr;
	}

	public void checkRefreshNewDay(boolean isPush) {
		this.randomBlessItemAndProcess(); // 刷新祈福道具
		this.randomShopGoods(); // 刷新商店物品
		this.tech.refreshNewDay(false);
		this.refreshTime = new Date();
		resetBlessAwardState(); // 刷新领取礼包状态
		if (isPush) {
			TechRefreshGuildMsg msgData = new TechRefreshGuildMsg();
			msgData.techData.blobData.products = this.tech.products;
			GuildMsg msg = new GuildMsg(Const.NotifyType.BLESS_NEW_DAY_PUSH.getValue(), msgData);
			GuildService.notifyAllMemberRefreshGuild(this.id, msg, null);
		}
	}

	public void addRecord(GuildRecordData record) {
		record.time = new Date();
		GuildSettingExt settingProp = GuildUtil.getGuildSettingExtProp();
		this.news.add(0, record);
		int len = this.news.size();
		if (len > settingProp.recording) {
			this.news.remove(len - 1);
		}
		this.saveToRedis();
	}

	public void saveToRedis() {
		GuildBlessPO data = this.toJson4Serialize();
		GuildUtil.updateGuildBless(data);
	}

	public void saveToMysql() {
		this.saveToRedis();
	}

	public GuildGiftAndBuffData getGiftAndBuffInfo(int index) {
		GuildGiftAndBuffData data = new GuildGiftAndBuffData();
		Map<String, Integer> giftMap = this.giftLs.get(index);
		if (null != giftMap) {
			data.itemCode = giftMap;
		}
		return data;
	}

	public GuildBlessInfo toJson4PayLoad() {
		BlessLevelExt levelProp = GuildUtil.getBlessPropByLevel(this.blessLevel);
		GuildBlessInfo.Builder data = GuildBlessInfo.newBuilder();
		data.setLevel(this.level);
		data.setBlessValue(this.blessValue);
		data.setBlessValueMax(this.blessValueMax);
		data.addAllFinishState(GuildCommonUtil.toList(this.blessData.allBlobData.finishStateArr));
		data.setBlessCountMax(levelProp.blessTime);

		// 祈福道具
		for (GuildBlessItem item : this.blessItems.values()) {
			GuildManagerHandler.BlessItem.Builder tempInfo = GuildManagerHandler.BlessItem.newBuilder();
			BlessItemCO prop = GuildUtil.getBlessItemById(item.id);
			tempInfo.setId(item.id);
			MiniItem.Builder tmpItem = ItemUtil.getMiniItemData(prop.itemID, item.needNum);
			if (null != tmpItem) {
				tempInfo.setItem(tmpItem);
			} else {
				Out.error("GuildBless toJson4PayLoad config is null:", prop.itemID);
			}
			tempInfo.setFinishNum(item.finishNum);
			data.addItemList(tempInfo);
		}

		return data.build();
	}

	public List<RecordInfo> getRecordList(int page) {
		List<RecordInfo> list = new ArrayList<RecordInfo>();
		int perPageNum = 50;
		int startIndex = (page - 1) * perPageNum;
		int endIndex = startIndex + perPageNum;
		for (int i = startIndex; i < this.news.size() && i < endIndex; ++i) {
			GuildRecordData record = this.news.get(i);
			RecordInfo tempInfo = new RecordInfo();
			tempInfo.time = DateUtil.format(record.time, "MM-dd HH:mm:ss");
			if (null != record.role1 && record.role1.pro > 0) {
				RoleInfo info1 = new RoleInfo();
				info1.pro = record.role1.pro;
				info1.name = record.role1.name;
				tempInfo.role1 = info1;
			}
			if (null != record.role2 && record.role2.pro > 0) {
				RoleInfo info2 = new RoleInfo();
				info2.pro = record.role2.pro;
				info2.name = record.role2.name;
				tempInfo.role1 = info2;
			}

			tempInfo.resultNum = record.result.v1;
			if (!StringUtil.isNullOrEmpty(record.result.v2)) {
				tempInfo.resultStr = record.result.v2;
			}

			if (null != record.item && !StringUtil.isNullOrEmpty(record.item.name)) {
				tempInfo.item = record.item;
			}

			tempInfo.recordType = record.type;
			list.add(tempInfo);
		}
		return list;
	}

	public boolean isTodayValidBlessId(int id) {
		GuildBlessItem blessItem = this.blessItems.get(id);
		if (null == blessItem || blessItem.needNum <= 0) {
			return false;
		}
		return true;
	}

	public boolean isChange(int[] oldState, int[] newState) {
		boolean isChange = false;
		for (int i = 0; i < oldState.length; i++) {
			if (Const.EVENT_GIFT_STATE.NOT_RECEIVE.getValue() == oldState[i] && oldState[i] != newState[i]) {
				isChange = true;
			}
		}
		return isChange;
	}

	public GuildResult blessAction(int id, int times, WNPlayer player) {
		GuildResult ret = new GuildResult();
		int[] oldState = new int[Const.NUMBER_MAX.GUILD_FINISHED_MAX];
		for (int i = 0; i < this.blessData.allBlobData.finishStateArr.length; i++) {
			oldState[i] = this.blessData.allBlobData.finishStateArr[i];
		}

		this.blessItems.get(id).finishNum += times;
		this.blessValue += times;

		int[] newState = this.calFinishState();
		// 检查完成状态变更
		if (this.isChange(oldState, newState)) {// 完成状态改变，通知所有成员
			BlessRefreshGuildMsg msgData = new BlessRefreshGuildMsg();
			msgData.blessValue = this.blessValue;
			msgData.finishStateArr = newState;
			GuildMsg msg = new GuildMsg(Const.NotifyType.BLESS_FINISH_PUSH.getValue(), msgData);
			GuildService.notifyAllMemberRefreshGuild(this.id, msg, player.getId());
		}

		BlessItemCO blessItemProp = GuildUtil.getBlessItemById(id);
		DItemEquipBase itemProp = ItemUtil.getPropByCode(blessItemProp.itemID);

		GuildRecordData record = new GuildRecordData();
		record.type = Const.GuildRecord.BLESS_USE_ITEM.getValue();
		record.role1.pro = player.getPro();
		record.role1.name = player.getName();
		record.result.v2 = Integer.toString(times);
		record.item.qColor = itemProp.qcolor;
		record.item.name = itemProp.name;
		this.addRecord(record);

		GuildBlessActionData data = new GuildBlessActionData();
		data.blessValue = this.blessValue;
		data.id = id;
		data.finishNum = this.blessItems.get(id).finishNum;
		data.finishState = newState;

		// 随机一条buff
		BlessLevelExt levelProp = GuildUtil.getBlessPropByLevel(this.level);
		data.buffTime = levelProp.bufftime * 60;
		data.buffIds = randomSomeFromArray(levelProp.buffList, levelProp.blessBuffNum);
		ret.result = 0;
		ret.data = data;
		return ret;
	}

	public void upgradeLevel(String playerId) {
		this.level += 1;
		this.saveToRedis();
	}
}
