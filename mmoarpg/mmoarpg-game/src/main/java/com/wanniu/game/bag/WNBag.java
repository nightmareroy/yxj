package com.wanniu.game.bag;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.wanniu.core.game.LangService;
import com.wanniu.core.logfs.Out;
import com.wanniu.core.util.RandomUtil;
import com.wanniu.game.GWorld;
import com.wanniu.game.common.Const;
import com.wanniu.game.common.Const.EventType;
import com.wanniu.game.common.Const.ForceType;
import com.wanniu.game.common.Const.GOODS_CHANGE_TYPE;
import com.wanniu.game.common.Const.TaskType;
import com.wanniu.game.common.CurrencyData;
import com.wanniu.game.common.msg.MessageUtil;
import com.wanniu.game.common.msg.WNNotifyManager;
import com.wanniu.game.data.GameData;
import com.wanniu.game.data.base.DItemBase;
import com.wanniu.game.data.base.DItemEquipBase;
import com.wanniu.game.equip.NormalEquip;
import com.wanniu.game.item.ItemConfig;
import com.wanniu.game.item.ItemUtil;
import com.wanniu.game.item.NormalItem;
import com.wanniu.game.item.po.DetailItemNum;
import com.wanniu.game.item.po.PlayerItemPO;
import com.wanniu.game.mail.MailUtil;
import com.wanniu.game.mail.data.MailData.Attachment;
import com.wanniu.game.mail.data.MailSysData;
import com.wanniu.game.player.GlobalConfig;
import com.wanniu.game.player.PlayerUtil;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.player.bi.LogReportService;
import com.wanniu.game.poes.BagsPO;
import com.wanniu.game.task.TaskEvent;

import pomelo.area.BagHandler.BagGridNumPush;
import pomelo.item.ItemOuterClass.Grid;
import pomelo.item.ItemOuterClass.ItemDetail;
import pomelo.player.PlayerOuterClass.Store;

/**
 * 背包处理
 * 
 * @author Yangzz
 *
 */
public class WNBag {

	public static class TradeMessageItemData {
		public String name;
		public int qt;
		public int num;
	}

	private WNPlayer player;
	public BagsPO bags;
	public BagPO bagPO;
	public Map<Integer, NormalItem> bagGrids;
	public int usedGridCount;
	private int bagTotalCount;
	private Const.BAG_TYPE type;
	private ItemConfig itemManager = ItemConfig.getInstance();
	public long bagGridPackUpTime = 0; // 背包整理时间

	protected WNBag() {

	}

	/**
	 * @param type 背包类型
	 */
	public WNBag(WNPlayer player, Const.BAG_TYPE type, BagPO bagPO, BagsPO bags) {
		this.player = player;
		this.type = type;
		this.bags = bags;

		this._init(bagPO);
	}

	private void _init(BagPO bagPO) {
		this.bagPO = bagPO;
		this.bagGrids = new ConcurrentHashMap<>();
		this.usedGridCount = 0;

		// 测试环境清理不存在的道具
		if (GWorld.DEBUG) {
			Iterator<Integer> iter = bagPO.bagGrids.keySet().iterator();
			while (iter.hasNext()) {
				int index = iter.next();
				if (ItemConfig.getInstance().getItemProp(bagPO.bagGrids.get(index).code) == null) {
					iter.remove();
				}
			}
		}
		for (Integer index : bagPO.bagGrids.keySet()) {
			NormalItem item = ItemUtil.createItemByDbOpts(bagPO.bagGrids.get(index));
			if (item != null) {
				bagGrids.put(index, item);
				this.usedGridCount++;
			}
		}
		this.bagTotalCount = BagUtil.getTotalCount(this.type);
	}

	/**
	 * 获取单个背包格子
	 */
	public NormalItem getGridItemCode(int gridNndex) {
		return bagGrids.get(gridNndex);
	}

	public void setBagGrid(int gridIndex, NormalItem item) {
		bagGrids.put(gridIndex, item);
	}

	public void addBagGrid(int gridIndex, NormalItem item) {
		bagGrids.put(gridIndex, item);
	}

	public NormalItem getItem(int gridIndex) {
		return this.bagGrids.get(gridIndex);
	};

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * 随机获得一个 非盟军币的未绑定道具 数据
	 * 
	 * @param needRemove 找到后是否需要从背包中删除找到的格子数据
	 * @returns {*} 道具数据
	 */
	public NormalItem randomGetItem() {
		List<Integer> emptyIndex = new ArrayList<>();

		for (int i = 1; i <= this.bagPO.bagGridCount; i++) {
			NormalItem item = this.bagGrids.get(i);
			if (item != null && !item.isBinding()) {
				if (GameData.PKLostBagItemTypes.containsKey(item.itemCode())) {
					emptyIndex.add(i);
				}
			}
		}
		if (emptyIndex.size() > 0) {
			int emptyPos = RandomUtil.getInt(0, emptyIndex.size() - 1);
			int randomPos = emptyIndex.get(emptyPos);
			int randGoldPercent = RandomUtil.getInt(GlobalConfig.PK_DropGoldMin, GlobalConfig.PK_DropGoldMax);
			return discardItemByPosPerent(randomPos, randGoldPercent, Const.GOODS_CHANGE_TYPE.hitUser);
		}
		return null;
	}

	/**
	 * 随机获得一个道具包含在codes里的道具(乱序)
	 * 
	 *
	 */
	public Object[] randomGetItem(Set<String> codes, int maxReduceCount) {
		if (this.bagPO.bagGridCount < 1) {
			return null;
		}
		Object[] obj = null;
		List<NormalItem> l = getNormalItemOrder(codes, this.bagPO.bagGridCount);
		if (l != null && !l.isEmpty()) {
			NormalItem dpItem = l.get(0);
			int groupCount = dpItem.itemDb.groupCount;
			int min = Math.min(maxReduceCount, groupCount);
			int trueCount = RandomUtil.getInt(min);
			if (discardItem(dpItem.itemDb.code, trueCount, Const.GOODS_CHANGE_TYPE.hitUser)) {
				obj = new Object[2];
				obj[0] = dpItem.itemDb.code;
				obj[1] = trueCount;
			}
		}
		return obj;
	}

	private List<NormalItem> getNormalItemOrder(Set<String> codes, int count) {
		List<NormalItem> list = new ArrayList<>();
		for (int i = 1; i <= count; i++) {
			NormalItem item = this.bagGrids.get(i);
			if (item != null && codes.contains(item.prop.code)) {
				list.add(item);
			}
		}
		if (list.size() > 1) {
			Collections.shuffle(list);
		}
		return list;
	}

	public Store toJson4Payload() {
		Store.Builder data = Store.newBuilder();
		data.addAllBagGrids(this.getGrids4PayLoad());
		data.setBagGridCount(this.bagPO.bagGridCount);
		data.setBagTotalCount(this.bagTotalCount);
		data.setType(this.type.getValue());
		data.setGridPrice(BagUtil.getGridPrice(this.type));
		return data.build();
	};

	public void _addUsedGridCount(int num) {
		if (num != 0) {
			this.usedGridCount += num;
			this._gridNumChangePush();
		}
	};

	public void _gridNumChangePush() {
		_gridNumChangePush(false);
	}

	/**
	 * @param force 强制给战斗服推送背包格子剩余数量
	 */
	public void _gridNumChangePush(boolean force) {
		if (this.type == Const.BAG_TYPE.BAG) {
			int emptyNum = emptyGridNum();
			if (emptyNum <= 3 || force) {
				player.refreshPlayerRemainBagCountData(emptyNum);
			}
		}
	};

	/**
	 * 开启背包格子
	 * 
	 * @param num
	 * @returns {boolean} false：失败 true:成功
	 */
	public boolean openGrid(int num) {

		if (num + this.bagPO.bagGridCount > this.bagTotalCount) {
			return false;
		}

		// 开格子
		int oldGridCount = this.bagPO.bagGridCount;
		this.bagPO.bagGridCount += num;
		List<Integer> list_gridIndexs = new ArrayList<>();
		for (int i = oldGridCount + 1; i <= this.bagPO.bagGridCount; ++i) {
			list_gridIndexs.add(i);
		}

		this._updateAndPush(list_gridIndexs, false);
		if (this.type == Const.BAG_TYPE.BAG) {
			int emptyNum = emptyGridNum();
			player.refreshPlayerRemainBagCountData(emptyNum);
		}
		return true;
	}

	/**
	 * 事件处理
	 */
	public void onEvent(TaskEvent event) {
		Out.debug(getClass(), "Bag onEvent ", event.type);
		if (event.type == EventType.consumeItem.getValue()) {
			String objId = (String) event.params[0];
			int objNum = (int) event.params[1];
			if (objId.equals("diamond")) {
				this.player.moneyManager.costDiamond(objNum, Const.GOODS_CHANGE_TYPE.consume);
				// this.player.pushDynamicData("diamond",
				// player.player.diamond);
			} else {
				this.itemUseById(objId, objNum);
			}
		}
	};

	// 背包格子道具的数据结构
	public List<Grid> getGrids4PayLoad() {
		List<Grid> data = new ArrayList<>();
		for (int index = 1; index <= this.bagPO.bagGridCount; index++) {
			NormalItem item = this.getItem(index);
			if (item != null) {
				data.add(this.getGrid4PayLoad(index).build());
			}
		}
		return data;
	};

	public Grid.Builder getGrid4PayLoad(int index) {
		Grid.Builder grid = Grid.newBuilder();
		grid.setGridIndex(index);
		NormalItem item = this.getItem(index);
		if (item != null) {
			grid.setItem(item.toJSON4GridPayload());
		}
		return grid;
	};

	/**
	 * 获取背包所有道具详情
	 * 
	 * @returns {Array}
	 */
	public List<ItemDetail> getAllEquipDetails4PayLoad() {
		List<ItemDetail> data = new ArrayList<>();
		for (int index = 1; index <= this.bagPO.bagGridCount; index++) {
			NormalItem item = this.getItem(index);
			if (item != null) {
				data.add(item.getItemDetail(player.playerBasePO).build());
			}
		}
		return data;
	};

	/**
	 * 通过模版ID查询相关道具的总数量
	 * 
	 * @param code
	 * @returns {number}
	 */
	public int findItemNumByCode(String code) {
		int num = 0;
		for (int i = 1; i <= this.bagPO.bagGridCount; i++) {
			NormalItem item = this.bagGrids.get(i);
			if (item != null && code.equals(item.itemDb.code)) {
				num += item.itemDb.groupCount;
			}
		}
		return num;
	};

	/**
	 * 通过模版ID查询相关道具的总数量,绑定数量，未绑定数量
	 * 
	 * @param code
	 * @returns {{totalNum: number, bindNum: number, unBindNum: number}}
	 */
	public DetailItemNum findDetailItemNumByCode(String code) {
		DetailItemNum data = new DetailItemNum();
		for (int i = 1; i <= this.bagPO.bagGridCount; i++) {
			NormalItem item = this.bagGrids.get(i);
			if (item != null && code.equals(item.itemDb.code)) {
				data.totalNum += item.itemDb.groupCount;
				if (item.isBinding()) {
					data.bindNum += item.itemDb.groupCount;
				} else {
					data.unBindNum += item.itemDb.groupCount;
				}
			}
		}
		return data;
	}

	/**
	 * 查找背包内所有匹配模版ID的格子
	 * 
	 * @param code * @param bind 绑定过滤条件
	 * @returns {{}}
	 */
	public List<NormalItem> findGridsByCode(String code, boolean bind) {
		// if(bind == undefined){
		// bind = false;
		// }
		List<NormalItem> grids = new ArrayList<>();
		for (int i = 1; i <= this.bagPO.bagGridCount; i++) {
			NormalItem item = this.bagGrids.get(i);
			if (item != null && code.equals(item.itemDb.code) && item.isBinding() == bind) {
				grids.set(i, item);
			}
		}
		return grids;
	}

	/**
	 * 查找背包内所有匹配模版ID的,且未叠加满的格子,用于添加道具叠加时用
	 * 
	 * @param code * @param bind 绑定过滤条件
	 * @returns {{}}
	 */
	public Map<Integer, NormalItem> findNotFullGridsByCode(String code, boolean bind) {
		Map<Integer, NormalItem> grids = new HashMap<>();
		for (int i = 1; i <= this.bagPO.bagGridCount; i++) {

			NormalItem item = this.bagGrids.get(i);
			if (item != null && code.equals(item.itemDb.code) && item.isBinding() == bind && (item.leftGroup() > 0)) {
				grids.put(i, item);
			}
		}
		return grids;
	};

	/**
	 * 查找背包内所有匹配模版ID的格子索引和叠加数量
	 * 
	 * @param code
	 * @returns {{}}
	 */
	public Map<Integer, Integer> findItemPosAndCountByCode(String code) {
		Map<Integer, Integer> posCount = new HashMap<>();
		for (int i = 1; i <= this.bagPO.bagGridCount; i++) {
			NormalItem item = this.bagGrids.get(i);
			if (item != null && code.equals(item.itemDb.code)) {
				posCount.put(i, item.itemDb.groupCount);
			}
		}
		return posCount;
	};

	/**
	 * 返回符合模版ID的第一个道具
	 * 
	 * @param code
	 * @returns {null}
	 */
	public NormalItem findFirstItemByCode(String code) {
		for (int i = 1; i <= this.bagPO.bagGridCount; i++) {
			NormalItem item = this.bagGrids.get(i);
			if (item != null && code.equals(item.itemDb.code)) {
				return item;
			}
		}
		return null;
	};

	/**
	 * 通过code找pos
	 * 
	 * @param code
	 * @returns {*}
	 */
	@Deprecated
	public int findPosByCode(String code) {
		for (int i = 1; i <= this.bagPO.bagGridCount; i++) {
			NormalItem item = this.bagGrids.get(i);
			if (item != null && code.equals(item.itemDb.code)) {
				return i;
			}
		}
		return 0;
	};

	/**
	 * 返回符合唯一id的道具
	 * 
	 * @param id
	 * @returns {*}
	 */
	public NormalItem findItemById(String id) {
		for (int i = 1; i <= this.bagPO.bagGridCount; i++) {
			NormalItem item = this.bagGrids.get(i);
			if (item != null && id.equals(item.itemDb.id)) {
				return item;
			}
		}
		return null;
	};

	/**
	 * 通过id找pos
	 * 
	 * @param id
	 * @returns {*}
	 */
	public int findPosById(String id) {
		for (int i = 1; i <= this.bagPO.bagGridCount; i++) {
			NormalItem item = this.bagGrids.get(i);
			if (item != null && id.equals(item.itemDb.id)) {
				// return {pos:i, item:item};
				return i;
			}
		}
		return -1;
	};

	/**
	 * 空余的格子数量
	 * 
	 * @returns {number}
	 */
	public int emptyGridNum() {
		return this.bagPO.bagGridCount - this.usedGridCount;
	};

	/**
	 * 查找num数量的背包空格子
	 * 
	 * @param num,数量, 未传此值默认返回所有
	 * @returns {Array}
	 */
	public List<Integer> findEmptyGrids(int num) {
		List<Integer> emptyIndex = new ArrayList<>();
		if (num <= 0) {
			return emptyIndex;
		}
		for (int i = 1; i <= this.bagPO.bagGridCount; i++) {
			NormalItem item = this.bagGrids.get(i);
			if (item == null) {
				emptyIndex.add(i);
			}
			if (emptyIndex.size() == num) {
				break;
			}
		}
		return emptyIndex;
	};

	public boolean testEmptyGridLarge(int actNum) {
		return testEmptyGridLarge(actNum, false);
	}

	/**
	 * 检查空余格子数是否大于等于num
	 * 
	 * @param num 检查数量
	 * @param isSilient true:安静模式，不弹框 默认：弹框
	 * @returns {boolean}
	 */
	public boolean testEmptyGridLarge(int actNum, boolean isSilient) {
		if (this.emptyGridNum() >= actNum) {
			return true;
		} else {
			if (!isSilient) {
				WNNotifyManager.getInstance().gridNotEnough(player);
			}
			return false;
		}
	}

	// 发送道具系统提示
	public void sendItemTips(WNPlayer player, String code, int number) {
		DItemEquipBase prop = itemManager.getItemProp(code);
		if (prop != null) {
			MessageUtil.sendItemTip(player, prop.name, prop.qcolor, number);
		}
	};

	public boolean addItemToPos(int gridIndex, NormalItem item, Const.GOODS_CHANGE_TYPE from) {
		if (item != null) {
			if (this._isVirtualItem(item, from)) {
				return true;
			}
			NormalItem oldItem = this.getItem(gridIndex);
			if (oldItem == null) {
				this.bagGrids.put(gridIndex, item);
				this._addUsedGridCount(1);
				List<Integer> gridIndexs = new ArrayList<>();
				gridIndexs.add(gridIndex);
				this._updateAndPush(gridIndexs, false, from);
				WNNotifyManager.getInstance().updateScript(player);
				Out.info("增加道具成功,角色id=", this.player.getId(), ",背包类型为:", this.type.getValue(), ",道具code=", item.itemDb.code, ",id=", item.itemDb.id, ",数量=", item.itemDb.groupCount, ",位置=", gridIndex, ",来源为:", (from == null ? "未知" : from.value));
				return true;
			}
		}
		return false;
	};

	public boolean isMagicRingTaskOpen(WNPlayer player) {
		int taskId = GlobalConfig.MagicRing_MissionID;
		if (taskId == 0) {
			return true;
		}
		if (player.taskManager.isTaskDoingOrFinish(taskId)) {
			return true;
		}
		return false;
	};

	/**
	 * 判断并添加虚拟道具
	 * 
	 * @param item 实体道具
	 * @param from 原始数量
	 * @returns {boolean}
	 * @private
	 */
	public boolean _isVirtualItem(NormalItem item, Const.GOODS_CHANGE_TYPE from) {
		if (item != null) {
			if (itemManager.getSecondType(item.prop.type) == Const.ItemSecondType.virtual.getValue()) {
				int num = item.getWorth();
				String code = item.itemDb.code;
				if (code.equals("gold")) {
					player.moneyManager.addGold(num, from);
				} else if (code.equals(Const.ITEM_CODE.TICKET.value)) {
					player.moneyManager.addTicket(num, from);
				} else if (code.equals("diamond")) {
					player.moneyManager.addDiamond(num, from);
				} else if (code.equals("fate")) {
					player.moneyManager.addXianYuan(num, from);
				} else if (code.equals("exp")) {
					player.addExp(num, from);
				} else if (code.equals("upexp")) {
					player.baseDataManager.addClassExp(num, from);
				} else if (code.equals("sp")) {
					// player.addSp(num);
				} else if (code.equals("prestige")) {
					player.addPrestige(num);
					player.pushDynamicData("prestige", player.player.prestige);
				} else if (code.equals("ringpres")) {

				}
				// else if(code.equals("power")){
				// player.addEnergy(num);
				// }
				else if (code.equals("solopoint")) {
					player.soloManager.addSolopoint(num, from);
				} else if (code.equals("consumepoint")) {
					player.moneyManager.addConsumePoint(num, from);
				} else if (code.equals("friendly")) {
					player.baseDataManager.addFriendly(num);
					player.pushDynamicData("friendly", player.player.friendly);
				} else if (code.indexOf("rank") == 0) {
					int rankId = Integer.parseInt(code.replace("rank", ""));
					player.titleManager.onAwardRank(rankId);
				} else if (code.indexOf("guildpoint") == 0) {
					player.addGuildPoint(num);
					player.pushDynamicData("guildpoint", player.player.guildpoint);
				} else if (code.equals(Const.ITEM_CODE.TREASURE_POINT.value)) {
					player.baseDataManager.addTreasurePoint(num);
				}

				return true;
			}
			// else if(item.prop.itemSecondType ==
			// Const.ItemSecondType.mastery.getValue()){
			// if(!isMagicRingTaskOpen(player)){
			// FSLog.info(getClass(), "魔戒任务没有接取");
			// return true;
			// }
			// player.countItemManger.add(item.itemDb.code,
			// item.itemDb.groupCount);
			// sendItemTips(player, item.itemDb.code, item.itemDb.groupCount);
			//
			// return true;
			// }
			else if (item.isVirtQuest()) {
				return true;
			}
		}

		return false;
	}

	/**
	 * 判断是否有足够的相应物品 return {flag : flag, items : sortItems}
	 */
	private Map<String, Object> _hasEnoughItem(String code, int num, boolean unbindFist) {
		boolean flag = false;
		int sum = 0;
		Map<Integer, NormalItem> itemAll = new HashMap<>();
		for (int i = 1; i <= this.bagPO.bagGridCount; i++) {
			NormalItem item = this.getItem(i);
			if (item != null && code.equals(item.itemDb.code)) {
				itemAll.put(i, item); // items.add(new Object[] {i,
										// item.itemDb.groupCount,
										// item.isBinding() });
				sum = sum + item.itemDb.groupCount;
			}
		}

		if (sum >= num) {
			flag = true;
		}

		// unbindFist为false时，绑定的物品在前面，为true时绑定的放在后面
		List<Entry<Integer, NormalItem>> list = new ArrayList<>(itemAll.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<Integer, NormalItem>>() {

			@Override
			public int compare(Map.Entry<Integer, NormalItem> item1, Map.Entry<Integer, NormalItem> item2) {
				int bind1 = item1.getValue().itemDb.isBind; // ((boolean)item1[2])
															// ? 1 : 0;
				int bind2 = item2.getValue().itemDb.isBind; // ((boolean)item2[2])
															// ? 1 : 0;
				if (bind1 != bind2) {
					if (unbindFist) { // 非绑放前面，即升序
						return bind1 < bind2 ? -1 : 1;
					} else {
						return bind1 < bind2 ? 1 : -1;
					}
				}
				return 0;
			}
		});
		Out.debug(getClass(), "unbindFist:", unbindFist, ",haveEnoughItem:", itemAll);

		Map<Integer, Integer> items = new HashMap<>();
		for (int index : itemAll.keySet()) {
			items.put(index, itemAll.get(index).itemDb.groupCount);
		}

		Map<String, Object> result = new HashMap<>();
		result.put("flag", flag);
		result.put("items", items);
		return result;
	};

	/**
	 * 在指定的格子列表中 扣除指定数量的道具
	 * 
	 * @param items {index：格子位置 count：格子扣除数量}
	 * @param sum 需扣除的总数量
	 */
	public List<Integer> discardEnoughItem(Map<Integer, Integer> items, int sum, Const.GOODS_CHANGE_TYPE costDes) {
		List<Integer> indexs = new ArrayList<>();

		for (int pos : items.keySet()) {
			int num = items.get(pos); // item.count;
			NormalItem realItem = this.getItem(pos);
			if (num > sum) {
				num = sum;
				sum = 0;
			} else {
				sum = sum - num;
			}

			if (realItem != null) {
				if (realItem.deleteGroup(num)) {
					if (realItem.isInvalid()) {
						this._delete(pos);
					}
					indexs.add(pos);

					Out.info("扣除道具成功,角色id=", this.player.getId(), ",背包类型为:", this.type.getValue(), ",道具code=", realItem.itemDb.code, ",id=", realItem.itemDb.id, ",数量=", realItem.itemDb.groupCount, ",位置=", pos, ",来源为:", (costDes == null ? "未知" : costDes.value));
					if (type == Const.BAG_TYPE.BAG) {
						LogReportService.getInstance().ansycReportItemFlow(player.getPlayer(), LogReportService.OPERATE_COST, realItem.itemDb.code, num, realItem.itemDb.isBind == 1, costDes, realItem.getName());
					}
				}
			}
			if (sum == 0) {
				break;
			}
		}
		return indexs;
	};

	/**
	 * 从背包中扣除道具
	 * 
	 * @param code 道具code
	 * @param num 扣除数量
	 * @param costDes 消耗来源类型，参考枚举Const.GOODS_CHANGE_TYPE
	 * @param currencyList BI日志 道具变更日志的类型和数量列表
	 * @param unBindFist true：优先扣除未绑定道具 false：先扣除绑定道具
	 * @returns {*|data.flag}
	 */
	public boolean discardItem(String code, int num, Const.GOODS_CHANGE_TYPE costDes, List<CurrencyData> currencyList, boolean unBindFist, boolean isNotPush) {
		boolean isPush = !isNotPush;
		if (currencyList == null) {
			CurrencyData currency = new CurrencyData(Const.CurrencyType.NONE.getValue(), 0);
			currencyList = new ArrayList<>();
			currencyList.add(currency);
		}

		Map<String, Object> data = this._hasEnoughItem(code, num, unBindFist);
		boolean flag = (boolean) data.get("flag");
		Map<Integer, Integer> items = (Map<Integer, Integer>) data.get("items");
		if (flag) {
			List<Integer> indexs = this.discardEnoughItem(items, num, costDes);
			if (isPush) {
				this._updateAndPush(indexs, true, costDes);
			}
			WNNotifyManager.getInstance().updateScript(player);

			// 删除道具，推送公会捐献红点
			player.guildManager.bagDelItem(code);
		}
		return flag;
	}

	public boolean discardItem(String code, int num, Const.GOODS_CHANGE_TYPE costDes) {
		return this.discardItem(code, num, costDes, null, false, false);
	}

	/** 重载方法 */
	public void discardItemByPos(int pos, int delNum, Const.GOODS_CHANGE_TYPE costDes) {
		discardItemByPos(pos, delNum, false, costDes);
	}

	/**
	 * 按个数的万份比扣除
	 */
	public NormalItem discardItemByPosPerent(int pos, int percent, Const.GOODS_CHANGE_TYPE costDes) {
		NormalItem item = this.getItem(pos);
		if (percent == 0 || item.itemDb.groupCount <= 0) {
			return null;
		}
		float fc = (1.0f * item.itemDb.groupCount / 10000) * percent;
		int cost = Math.round(fc);
		cost = cost <= 0 ? 1 : cost;
		discardItemByPos(pos, cost, costDes);

		// 必需使用此方法....
		NormalItem drop = ItemUtil.createItemByOpts(item.itemDb.copy(), item.prop);
		drop.itemDb.groupCount = cost;
		return drop;
	}

	/**
	 * 按背包格子位置扣除道具
	 * 
	 * @param pos 格子位置
	 * @param num 数量
	 * @param all 是否是删除所有
	 * @param costDes 消耗来源类型，参考枚举Const.GOODS_CHANGE_TYPE
	 */
	public void discardItemByPos(int pos, int delNum, boolean all, Const.GOODS_CHANGE_TYPE costDes) {

		NormalItem item = this.getItem(pos);
		if (item != null) {
			if (all) {
				delNum = item.itemDb.groupCount;
			}

			Map<Integer, Integer> currency = new HashMap<>();
			currency.put(Const.CurrencyType.NONE.getValue(), 0);
			if (item.deleteGroup(delNum)) {
				if (item.isInvalid()) {
					this._delete(pos);
				}
				List<Integer> list = new ArrayList<>();
				list.add(pos);
				this._updateAndPush(list, true, costDes);
				WNNotifyManager.getInstance().updateScript(player);

				Out.info("扣除道具成功,角色id=", this.player.getId(), ",背包类型为:", this.type.getValue(), ",道具code=", item.itemDb.code, ",id=", item.itemDb.id, ",数量=", item.itemDb.groupCount, ",位置=", pos, ",来源为:", (costDes == null ? "未知" : costDes.value));
				if (type == Const.BAG_TYPE.BAG) {
					LogReportService.getInstance().ansycReportItemFlow(player.getPlayer(), LogReportService.OPERATE_COST, item.itemDb.code, delNum, item.itemDb.isBind == 1, costDes, item.getName());
				}
			}
		}
	}

	/**
	 * 按位置丢弃多个物品，如果该位置丢弃全部数量，则不对物品做任何操作。
	 * 
	 * @param posnums [{pos:int32,num:int32},{pos:int32,num:int32},...] costDes
	 *            消耗来源类型，参考枚举Const.GOODS_CHANGE_TYPE
	 */
	public void discardItemsByPos(List<Map<String, Object>> posnums, Const.GOODS_CHANGE_TYPE source) {
		List<Integer> gridIndexs = new ArrayList<Integer>();
		List<TradeMessageItemData> sysMessageItems = new ArrayList<>();

		for (int i = 0; i < posnums.size(); i++) {
			int pos = (int) posnums.get(i).get("pos");
			int num = (int) posnums.get(i).get("num");
			Map<Integer, Integer> currency = (Map<Integer, Integer>) posnums.get(i).get("currency");
			if (currency == null) {
				currency = new HashMap<>();
				currency.put(Const.CurrencyType.NONE.getValue(), 0);
			}
			int costDes = (int) posnums.get(i).get("costDes");
			NormalItem item = this.getItem(pos);

			if (costDes == Const.GOODS_CHANGE_TYPE.trade.getValue()) {
				TradeMessageItemData data = new TradeMessageItemData();
				data.name = item.getName();
				data.qt = item.prop.qcolor;
				data.num = num;
				sysMessageItems.add(data);
			}
			if (item != null && num <= item.itemDb.groupCount) {
				if (num < item.itemDb.groupCount) {
					item.deleteGroup(num);
				} else {
					this._delete(pos);
				}
				gridIndexs.add(pos);
				Out.info("扣除道具成功,角色id=", this.player.getId(), ",背包类型为:", this.type.getValue(), ",道具code=", item.itemDb.code, ",id=", item.itemDb.id, ",数量=", item.itemDb.groupCount, ",位置=", pos, ",来源为:", (source == null ? "未知" : source.value));
				if (type == Const.BAG_TYPE.BAG) {
					LogReportService.getInstance().ansycReportItemFlow(player.getPlayer(), LogReportService.OPERATE_COST, item.itemDb.code, item.itemDb.groupCount, item.itemDb.isBind == 1, source, item.getName());
				}
			}
		}

		if (gridIndexs.size() > 0) {
			this._updateAndPush(gridIndexs, true, source);
			WNNotifyManager.getInstance().updateScript(player);
		}
		if (sysMessageItems.size() > 0) {
			this.player.pushChatSystemMessage(Const.SYS_CHAT_TYPE.TRADE, "0", sysMessageItems, null);
		}

	};

	/**
	 * 扣除一组物品，如果背包中任何一种物品数量不足，则都不扣除。
	 * 
	 * @param code e.g:"xuan1:1;xuan2:1;xuan3:1"
	 * 
	 */
	public boolean discardItemsByCode(String code, Const.GOODS_CHANGE_TYPE costDes) {
		List<SimpleItemInfo> simpleItemInfos = ItemUtil.parseString(code);
		for (SimpleItemInfo simpleItemInfo : simpleItemInfos) {
			int totalNum = this.findItemNumByCode(simpleItemInfo.itemCode);
			if (totalNum < simpleItemInfo.itemNum) {
				return false;
			}
		}
		for (SimpleItemInfo simpleItemInfo : simpleItemInfos) {
			this.discardItem(simpleItemInfo.itemCode, simpleItemInfo.itemNum, costDes);

		}
		return true;
	}

	/**
	 * 按位置丢弃物品并不对物品做任何操作
	 * 
	 * @param pos 格子位置
	 * @param flag 是否记录BI日志
	 * @param costDes 消耗来源类型，参考枚举Const.GOODS_CHANGE_TYPE
	 */
	public void removeItemByPos(int pos, boolean flag, Const.GOODS_CHANGE_TYPE costDes) {
		NormalItem item = this.getItem(pos);
		if (item != null) {
			this._delete(pos);
			List<Integer> list_pos = new ArrayList<>();
			list_pos.add(pos);
			this._updateAndPush(list_pos, true, costDes);
			WNNotifyManager.getInstance().updateScript(player);
			Out.info("扣除道具成功,角色id=", this.player.getId(), ",背包类型为:", this.type.getValue(), ",道具code=", item.itemDb.code, ",id=", item.itemDb.id, ",数量=", item.itemDb.groupCount, ",位置=", pos, ",来源为:", (costDes == null ? "未知" : costDes.value));
			if (type == Const.BAG_TYPE.BAG) {
				LogReportService.getInstance().ansycReportItemFlow(player.getPlayer(), LogReportService.OPERATE_COST, item.itemDb.code, item.itemDb.groupCount, item.itemDb.isBind == 1, costDes, item.getName());
			}
		}
	};

	/**
	 * 丢弃所有物品
	 */
	public void removeAllItems() {
		List<Integer> gridIndexs = new ArrayList<>();

		for (int i = 1; i <= this.bagPO.bagGridCount; i++) {
			NormalItem item = this.getItem(i);
			if (item != null) {
				this._delete(i);
				gridIndexs.add(i);
			}
		}

		if (gridIndexs.size() > 0) {
			this._updateAndPush(gridIndexs, true, GOODS_CHANGE_TYPE.clear_when_logout);
			WNNotifyManager.getInstance().updateScript(player);
			Out.info("下线时清除所有回购背包的物品:playerId=", this.player.getId());
		}
	};

	/**
	 * * 整理背包判断时间是否满足CD条件
	 * 
	 * @returns {boolean}
	 */
	public boolean canPackUp() {
		long now = System.currentTimeMillis();
		if ((now - bagGridPackUpTime) < Const.Bag.BAG_GRID_PACKUP_TIME) {
			return false;
		}
		return true;
	};

	/**
	 * 将item合并到temp中，如果产生新分组，则将叠满的组放在newData中,不满的仍然房子temp中
	 * 
	 * @param item 实体道具
	 * @param temp 道具map
	 * @param newData 新分组
	 */
	public void merge(NormalItem item, Map<String, NormalItem> temp, List<NormalItem> newData) {

		if (temp.get(item.itemDb.code) == null) {
			temp.put(item.itemDb.code, item);
		} else {
			if (temp.get(item.itemDb.code).itemDb.groupCount + item.itemDb.groupCount >= item.prop.groupCount) {
				int tmpCount = item.itemDb.groupCount;
				// item.itemDb.groupCount = item.prop.groupCount;
				item.setNum(item.prop.groupCount);
				newData.add(item);
				// temp.get(item.itemDb.code).itemDb.groupCount =
				// temp.get(item.itemDb.code).itemDb.groupCount + tmpCount -
				// item.prop.groupCount;
				temp.get(item.itemDb.code).setNum(temp.get(item.itemDb.code).itemDb.groupCount + tmpCount - item.prop.groupCount);
				if (temp.get(item.itemDb.code).itemDb.groupCount == 0) {
					temp.remove(item.itemDb.code);
				}
			} else {
				// temp.get(item.itemDb.code).itemDb.groupCount +=
				// item.itemDb.groupCount;
				temp.get(item.itemDb.code).addGroupNum(item.itemDb.groupCount);
			}
		}

	};

	/**
	 * 整理背包时的堆叠数量处理
	 * 
	 * @returns {Array}
	 * @private
	 */
	public List<NormalItem> _packUpStackNum() {
		List<NormalItem> newData = new ArrayList<>();

		Map<String, NormalItem> temp = new HashMap<>();
		Map<String, NormalItem> tempBind = new HashMap<>();

		for (int index = 1; index <= this.bagPO.bagGridCount; index++) {
			NormalItem item = this.getItem(index);
			if (item == null) {
				continue;
			}
			// 到堆叠上限
			if (item.itemDb.groupCount != item.prop.groupCount) {
				if (item.isBinding()) {
					ItemUtil.mergeItems(item, tempBind, newData);
				} else {
					ItemUtil.mergeItems(item, temp, newData);
				}
			} else {
				newData.add(item);
			}
		}

		for (NormalItem opts : temp.values()) {
			newData.add(opts);
		}

		for (NormalItem opts : tempBind.values()) {
			newData.add(opts);
		}

		return newData;
	};

	/**
	 * 通过道具code使用道具
	 * 
	 * @param id 道具code
	 * @param num 数量
	 * @returns {boolean}
	 */
	public boolean itemUseById(String id, int num) {
		// 判断个数
		if (num > this.findItemNumByCode(id)) {
			Out.warn(this.player.getId(), "在使用物品的时候失败了!个数不足:id=", id, ",要扣的数量为:", num);
			return false;
		}
		NormalItem item = this.findFirstItemByCode(id);
		// 道具是否可使用
		if (item == null || !item.canUse()) {
			Out.warn(this.player.getId(), "在使用物品的时候失败了!是不可使用的类型:id=", id, ",要扣的数量为:", num);
			return false;
		}

		// 消耗
		if (!this.discardItem(id, num, Const.GOODS_CHANGE_TYPE.use, null, false, false)) {
			Out.warn(this.player.getId(), "在使用物品的时候失败了!个数不足2:id=", id, ",要扣的数量为:", num);
			return false;
		}

		return true;
	};

	/**
	 * 通过格子位置使用道具
	 * 
	 * @param pos 格子位置
	 * @param num 数量
	 * @returns {boolean}
	 */
	// public boolean itemUseByPos(int pos, int num) {
	// NormalItem item = this.getItem(pos);
	//
	// // todo 道具是否可使用
	// if ((item == null) || !item.canUse()) {
	// Out.debug(getClass(), "不可使用类型");
	// return false;
	// }
	// // 消耗
	// if (!item.deleteGroup(num)) {
	// Out.debug(getClass(), "个数不足");
	// return false;
	// }
	// if (item.isInvalid()) {
	// this._delete(pos);
	// }
	// List<Integer> list_pos = new ArrayList<>();
	// list_pos.add(pos);
	// this._updateAndPush(list_pos, true);
	//
	// return true;
	// };

	/**
	 * 判断数量是否足够
	 * 
	 * @param code 道具code
	 * @param num 数量
	 * @returns {boolean}
	 */
	public boolean isItemNumEnough(String code, int num) {
		if (this.findItemNumByCode(code) >= num) {
			return true;
		}
		return false;
	};

	/**
	 * 更新道具状态
	 * 
	 * @param index 格子位置
	 * @constructor
	 */
	public void ItemStatusUpdate(int index) {
		NormalItem item = this.getItem(index);
		if (item != null) {
			item.itemDb.isNew = 0;
			this.bagGrids.put(index, item);
		}
	};

	// public void updateAndPush(List<Integer> gridIndexs, boolean refresh){
	// this._updateAndPush(gridIndexs, refresh);
	// };

	public void _updateAndPush(List<Integer> gridIndexs, boolean refresh, GOODS_CHANGE_TYPE source) {
		this._pushDynamicData(gridIndexs, refresh, source);
	};

	public void _updateAndPush(List<Integer> gridIndexs, boolean refresh) {
		this._pushDynamicData(gridIndexs, refresh, null);
	};

	/**
	 *
	 * @param gridIndexs
	 * @param noChange 没有改变
	 * @private
	 */
	public void _pushDynamicData(List<Integer> gridIndexs, boolean refresh, GOODS_CHANGE_TYPE source) {
		if (this.type == Const.BAG_TYPE.BAG) {
			WNNotifyManager.getInstance().pushBagItemDynamic(player, gridIndexs, refresh, source);
		} else if (this.type == Const.BAG_TYPE.WAREHOUSE) {
			WNNotifyManager.getInstance().pushBagItemDynamicWareHouse(player, gridIndexs, refresh);
		} else if (this.type == Const.BAG_TYPE.RECYCLE) {
			WNNotifyManager.getInstance().pushBagItemDynamicRecycle(player, gridIndexs, refresh);
		}
	};

	public void _pushNewItem(String code, int num, GOODS_CHANGE_TYPE from) {
		WNNotifyManager.getInstance().pushBagNewItem(player, code, num, from);
	}

	public boolean identify() {
		return false; // 已取消鉴定,改为精炼
	};

	/**
	 * 根据格子索引找到删除道具
	 * 
	 * @param pos
	 * @private
	 */
	public void _delete(int pos) {
		this.bagGrids.remove(pos); // this.bagGrids.put(pos, null);
		this._addUsedGridCount(-1);
	};

	/**
	 * 设计道具cd
	 * 
	 * @param secondType
	 * @param pos
	 */
	public void setCD(int secondType, int pos) {
		List<Integer> grids = new ArrayList<>();
		for (int i = 1; i <= this.bagPO.bagGridCount; i++) {
			NormalItem item = this.bagGrids.get(i);
			if (item != null) {
				if (itemManager.getSecondType(item.prop.type) == secondType) {
					item.setCD();
					if (i != pos) {
						grids.add(i);
					}
				}
			}
		}

		this._updateAndPush(grids, false);
	};

	/**
	 * 获取所有种类中最高等级的一种宝石
	 * 
	 * @returns {Array}
	 */
	public List<NormalItem> getAllTypeGem() {
		Map<Integer, NormalItem> data = new HashMap<>();
		for (int i = 1; i <= this.bagPO.bagGridCount; i++) {
			NormalItem item = this.getItem(i);// .getBagGrid(i);
			DItemBase prop = (DItemBase) item.prop;
			if (item != null && item.prop.itemSecondType == Const.ItemSecondType.gem.getValue()) {
				if (item.getLevel() > this.player.getLevel()) {
					continue;
				}
				NormalItem old = data.get(prop.par);
				if (old != null) {
					if (old.prop.price < item.prop.price) {
						data.put(prop.par, item);
					}
				} else {
					data.put(prop.par, item);
				}
			}
		}

		Out.debug(getClass(), "getAllTypeGem:", data);
		List<NormalItem> res = new ArrayList<>();
		for (NormalItem v : data.values()) {
			res.add(v);
		}

		res.sort(new Comparator<NormalItem>() {

			@Override
			public int compare(NormalItem a, NormalItem b) {
				return a.prop.price < b.prop.price ? 1 : -1;
			}
		});

		return res;
	};

	/**
	 * 根据装备类型和职业 获取背包中的装备
	 * 
	 * @param equipType
	 * @return
	 */
	public List<NormalEquip> findEquipByType(int equipType, int pro, int level) {
		List<NormalEquip> items = new ArrayList<>();
		for (int i = 1; i <= this.bagPO.bagGridCount; i++) {
			NormalItem item = this.bagGrids.get(i);
			if (item == null) {
				continue;
			}
			if (!item.isEquip()) {
				continue;
			}
			if (item.prop.Pro != 0 && item.prop.Pro != pro) {
				continue;
			}
			if (item.prop.levelReq > level) {
				continue;
			}
			NormalEquip equip = (NormalEquip) item;
			if (equip.prop.itemTypeId == equipType) {

				items.add(equip);
			}
		}
		return items;
	}

	/**
	 * 根据道具类型查找道具
	 */
	public List<NormalItem> findItemByType(String type) {
		List<NormalItem> items = new ArrayList<>();
		for (int i = 1; i <= this.bagPO.bagGridCount; i++) {
			NormalItem item = this.bagGrids.get(i);
			if (item != null && item.prop.type.equals(type)) {
				items.add(item);
			}
		}
		return items;
	};

	/**
	 * 根据道具子类型查找道具
	 */
	public List<NormalItem> findItemBySecondType(Const.ItemSecondType secondType) {
		List<NormalItem> items = new ArrayList<>();
		for (int i = 1; i <= this.bagPO.bagGridCount; i++) {
			NormalItem item = this.bagGrids.get(i);
			if (item != null && item.prop.itemSecondType == secondType.getValue()) {
				items.add(item);
			}
		}
		return items;
	};

	/**
	 * 根据道具prop字段查找道具
	 */
	public List<NormalItem> findItemByProp(String prop) {
		List<NormalItem> items = new ArrayList<>();
		for (int i = 1; i <= this.bagPO.bagGridCount; i++) {
			NormalItem item = this.bagGrids.get(i);
			if (item == null) {
				continue;
			}
			if (item.isEquip()) {
				continue;
			}

			DItemBase template = (DItemBase) item.prop;
			if (template != null && template.prop.equals(prop)) {
				items.add(item);
			}
		}
		return items;
	};

	/**
	 * 通过id（uuid)扣除道具
	 * 
	 * @param id
	 * @param num
	 */
	public void gmDeleteItemById(String id, int num) {
		for (int i = 1; i <= this.bagPO.bagGridCount; i++) {
			NormalItem item = this.bagGrids.get(i);
			if (item != null && item.itemDb.id.equalsIgnoreCase(id)) {
				num = Math.min(num, item.itemDb.groupCount);// 数量超过时删全部
				this.discardItemByPos(i, num, false, Const.GOODS_CHANGE_TYPE.gm);
				List<Integer> grids = new ArrayList<>();
				grids.add(i);
				this._updateAndPush(grids, true, GOODS_CHANGE_TYPE.gm);
				break;
			}
		}
	};

	public static class SimpleItemInfo {
		public String itemCode;
		public int itemNum;
		public ForceType forceType;
		public int type;
		public int index;
		public int pos;
	}

	/**
	 * 内部计算添加道具合并后需要的空格子数量 注：强制绑定类型forceType优先使用数组itemList内部的,
	 * 如forceType参数未提供，内部某对象也不存在该字段，则该道具绑定规则走默认配置
	 * 
	 * @param itemList [{itemCode:"mou1", itemNum:10, forceType:1}]
	 * @param forceType 强制绑定类型，参考Const.ForceType类型
	 * @returns {number}
	 * @private
	 */
	public int _calcNeedGridNumByCode(List<SimpleItemInfo> itemList, Const.ForceType forceType) {
		int needGridNum = 0;
		// 按绑定类型分组统计数量
		Map<String, Integer> bindGroup = new HashMap<>();
		Map<String, Integer> unBindGroup = new HashMap<>();
		for (int i = 0; i < itemList.size(); i++) {
			SimpleItemInfo itemInfo = itemList.get(i);

			if (ItemUtil.isVirtualItem(itemInfo.itemCode)) {
				continue;
			}
			Const.ForceType finalForceType = itemInfo.forceType != null ? itemInfo.forceType : forceType; // itemInfo.hasOwnProperty("forceType")
																											// ?
																											// itemInfo.forceType
																											// :
																											// forceType;
			DItemEquipBase prop = ItemUtil.getPropByCode(itemInfo.itemCode);
			if (prop == null) {
				return 9999;
			}
			int finalBindType = ItemUtil.getPropBindType(prop, finalForceType);
			boolean isBind = finalBindType == 1;

			Map<String, Integer> group = isBind ? bindGroup : unBindGroup;
			if (group.get(itemInfo.itemCode) != null) {
				// group[itemInfo.itemCode] += itemInfo.itemNum;
				group.put(itemInfo.itemCode, group.get(itemInfo.itemCode) + itemInfo.itemNum);
			} else {
				// group[itemInfo.itemCode] = itemInfo.itemNum;
				group.put(itemInfo.itemCode, itemInfo.itemNum);
			}
		}
		// 将分组合并到一个数组中
		List<Object[]> items = new ArrayList<>();
		Iterator<String> keys_bind = bindGroup.keySet().iterator();
		while (keys_bind.hasNext()) {
			String itemCode = keys_bind.next();
			int itemNum = bindGroup.get(itemCode);
			items.add(new Object[] { itemCode, itemNum, true });
		}
		Iterator<String> keys_unbind = unBindGroup.keySet().iterator();
		while (keys_unbind.hasNext()) {
			String itemCode = keys_unbind.next();
			int itemNum = unBindGroup.get(itemCode);
			items.add(new Object[] { itemCode, itemNum, false });
		}
		// 统计合并到背包后需要的格子数量
		if (items.size() == 0) {
			return needGridNum;
		}
		for (int i = 0; i < items.size(); i++) {
			Object[] itemInfo = items.get(i);
			String itemCode = (String) itemInfo[0];
			int calcNum = (int) itemInfo[1];
			boolean isBind = (boolean) itemInfo[2];

			DItemEquipBase prop = ItemUtil.getPropByCode(itemCode);
			Map<Integer, NormalItem> grids = this.findNotFullGridsByCode(itemCode, isBind);
			Iterator<Integer> keys = grids.keySet().iterator();
			while (keys.hasNext()) {
				int gridIndex = keys.next();

				NormalItem item = getItem(gridIndex);
				int leftNum = item.leftGroup();
				if (leftNum <= 0) {
					continue;
				}
				int addNum = (leftNum < calcNum) ? leftNum : calcNum;
				if (addNum > 0) {
					calcNum = calcNum - addNum;
				}
				if (calcNum == 0) {
					break;
				}
			}
			if (calcNum > 0) {
				needGridNum += (calcNum + prop.groupCount - 1) / prop.groupCount;
			}
		}
		return needGridNum;
	};

	/** 重载方法 */
	public boolean testAddCodeItem(String itemCode, int itemNum) {
		return testAddCodeItem(itemCode, itemNum, null, false);
	}

	public boolean testAddCodeItem(String itemCode, int itemNum, Const.ForceType forceType) {
		return this.testAddCodeItem(itemCode, itemNum, forceType, false);
	}

	/**
	 * 多种道具进背包空间检测
	 * 
	 * @param itemCode 道具code
	 * @param itemNum 道具数量
	 * @param forceType 强制绑定类型，参考Const.ForceType类型
	 * @param isNotNotify 格子不够是否 不弹框
	 * @returns {boolean}
	 */
	public boolean testAddCodeItem(String itemCode, int itemNum, Const.ForceType forceType, boolean isNotNotify) {
		List<SimpleItemInfo> list = new ArrayList<>();
		SimpleItemInfo map = new SimpleItemInfo();
		map.itemCode = itemCode;
		map.itemNum = itemNum;
		map.forceType = forceType;
		list.add(map);
		return this.testAddCodeItems(list, forceType, isNotNotify);
	};

	/**
	 * * @param itemList [{itemCode:"mou1", itemNum:10, forceType:1}] forceType:
	 * 该道具的强制绑定类型
	 */
	public boolean testAddCodeItems(List<SimpleItemInfo> itemList) {
		return testAddCodeItems(itemList, null, false);
	}

	/**
	 * 多种道具进背包空间检测 注：强制绑定类型forceType优先使用itemList内部的forceType字段
	 * 
	 * @param itemList [{itemCode:"mou1", itemNum:10, forceType:1}] forceType:
	 *            该道具的强制绑定类型
	 * @param forceType 全局强制绑定类型，参考Const.ForceType类型
	 * @param isNotNotify 格子不够是否 不弹框
	 * @returns {boolean}
	 */
	public boolean testAddCodeItems(List<SimpleItemInfo> itemList, Const.ForceType forceType, boolean isNotNotify) {
		int needGrids = this._calcNeedGridNumByCode(itemList, forceType);
		if (itemList.size() > 0) {
			Out.debug(getClass(), "testAddCodeItems ", itemList.get(0).itemCode, " needGrids:", needGrids);
		} else {
			Out.debug(getClass(), "testAddCodeItems needGrids:", needGrids);
		}

		if (this.emptyGridNum() < needGrids) {
			if (!isNotNotify) {
				WNNotifyManager.getInstance().gridNotEnough(player);
			}
			return false;
		}
		return true;
	}

	/**
	 * 单个实体道具进背包空间检测
	 * 
	 * @param item 实体道具
	 * @param isNotNotify 格子不够是否 不弹框
	 * @returns {boolean}
	 */
	public boolean testAddEntityItem(NormalItem item, boolean isNotNotify) {
		List<NormalItem> list_items = new ArrayList<NormalItem>();
		list_items.add(item);
		return this.testAddEntityItems(list_items, isNotNotify);
	};

	/**
	 * 通过PO对象检测背包空间
	 */
	public boolean testAddEntityItemsPO(List<PlayerItemPO> items, boolean isNotNotify) {
		List<NormalItem> list = new ArrayList<>();
		for (PlayerItemPO po : items) {
			list.add(ItemUtil.createItemByDbOpts(po));
		}
		return testAddEntityItems(list, isNotNotify);
	}

	/**
	 * 多个实体道具进背包空间检测
	 * 
	 * @param items 实体道具数组
	 * @param isNotNotify 格子不够是否 不弹框
	 * @returns {boolean}
	 */
	public boolean testAddEntityItems(List<NormalItem> items, boolean isNotNotify) {
		List<SimpleItemInfo> itemList = new ArrayList<>();
		for (int i = 0; i < items.size(); i++) {
			NormalItem item = items.get(i);
			// 通过绑定状态获取强制绑定类型
			Const.ForceType forceType = item.isBinding() ? Const.ForceType.BIND : Const.ForceType.UN_BIND;
			SimpleItemInfo map = new SimpleItemInfo();
			map.itemCode = item.itemDb.code;
			map.itemNum = item.itemDb.groupCount;
			map.forceType = forceType;
			itemList.add(map);
		}
		return this.testAddCodeItems(itemList, null, isNotNotify);
	};

	/**
	 * 当背包空间不足的时候直接发邮件
	 */
	public boolean addCodeItemMail(List<NormalItem> items, Const.ForceType forceType, Const.GOODS_CHANGE_TYPE fromDes, String mailKey) {
		if (this.testAddEntityItems(items, false)) {
			this.addEntityItems(items, fromDes);
		} else {
			MailSysData mailData = new MailSysData(mailKey);
			List<Attachment> list_attach = new ArrayList<>();
			for (NormalItem item : items) {
				// 虚拟道具不用走邮件
				if (item.isVirtual()) {
					this.addEntityItem(item, fromDes);
				} else {
					Attachment attachment = new Attachment();
					attachment.itemCode = item.itemCode();
					attachment.itemNum = item.getNum();
					list_attach.add(attachment);
				}
			}
			if (list_attach.size() > 0) {
				mailData.attachments = list_attach;
				MailUtil.getInstance().sendMailToOnePlayer(player.getId(), mailData, fromDes);
			}
		}
		return false;
	}

	/**
	 * 当背包空间不足的时候直接发邮件
	 */
	public boolean addCodeItemMail(String itemCode, int number, Const.ForceType forceType, Const.GOODS_CHANGE_TYPE fromDes, String mailKey) {
		if (this.testAddCodeItem(itemCode, number, forceType)) {
			this.addCodeItem(itemCode, number, forceType, fromDes);
		} else {
			MailSysData mailData = new MailSysData(mailKey);
			mailData.attachments = new ArrayList<>();
			Attachment attachment = new Attachment();
			attachment.itemCode = itemCode;
			attachment.itemNum = number;
			mailData.attachments.add(attachment);
			MailUtil.getInstance().sendMailToOnePlayer(player.getId(), mailData, fromDes);
		}
		return false;
	}

	/**
	 * 当背包空间不足的时候直接发邮件
	 */
	public void addEntityItemMail(NormalItem item, Const.GOODS_CHANGE_TYPE fromDes, String mailKey) {
		ForceType forceType = item.getBind() == 1 ? ForceType.BIND : ForceType.UN_BIND;
		if (this.testAddCodeItem(item.itemCode(), item.getNum(), forceType)) {
			this.addEntityItem(item, fromDes);
		} else {
			MailSysData mailData = new MailSysData(mailKey);
			mailData.entityItems = new ArrayList<PlayerItemPO>();
			mailData.entityItems.add(item.itemDb);
			MailUtil.getInstance().sendMailToOnePlayer(player.getId(), mailData, fromDes);
		}
	}

	public void addCodeItem(String code, int number, Const.ForceType forceType, Const.GOODS_CHANGE_TYPE fromDes) {
		this.addCodeItem(code, number, forceType, fromDes, null, false, false);
	}

	public void addCodeItem(String code, int number, Const.ForceType forceType, Const.GOODS_CHANGE_TYPE fromDes, Map<Integer, Object> currencyList) {
		this.addCodeItem(code, number, forceType, fromDes, currencyList, false, false);
	}

	/**
	 * 通过code添加道具
	 * 
	 * @param code 道具code
	 * @param number 数量
	 * @param forceType 强制绑定类型，参考Const.ForceType类型
	 * @param fromDes 来源类型,GOODS_CHANGE_TYPE 和 GOODS_CHANGE_TYPE
	 * @param currencyList bi日志结构，eg:{type: Const.CurrencyType.COIN, value : number}
	 * @param isNotPushNewItem 是否通知前端获得新物品
	 * @param isNotSendFullMsg 如果添加后包裹格子满了，是否通知前端背包已满
	 * @returns {*}
	 */
	public void addCodeItem(String code, int number, Const.ForceType forceType, Const.GOODS_CHANGE_TYPE fromDes, Map<Integer, Object> currencyList, boolean isNotPushNewItem, boolean isNotSendFullMsg) {
		List<SimpleItemInfo> codeItems = new ArrayList<>();
		SimpleItemInfo itemInfo = new SimpleItemInfo();
		itemInfo.itemCode = code;
		itemInfo.itemNum = number;
		itemInfo.forceType = forceType;
		codeItems.add(itemInfo);
		this.addCodeItems(codeItems, fromDes, currencyList, isNotPushNewItem, isNotSendFullMsg);
	};

	/** 重载方法 */
	public void addCodeItems(List<SimpleItemInfo> codeItems, Const.GOODS_CHANGE_TYPE fromDes) {
		this.addCodeItems(codeItems, fromDes, null, false, false);
	}

	/**
	 * 通过code添加多种道具 注：添加多个道具，强制绑定类型优先使用items数组中对象的forceType
	 * 如果数组中的对象没有该字段，则使用参数forceType
	 * 
	 * @param codeItems 多个code道具数组，格式为[{itemCode:"mou1", itemNum:10, forceType:0,
	 *            expandParas}] 注：items参数中，对象的expandParas字段为创建时需要的额外参数,可以不提供该字段
	 * @param forceType 强制绑定类型，参考Const.ForceType类型
	 * @param fromDes 来源类型,GOODS_CHANGE_TYPE 和 GOODS_CHANGE_TYPE
	 * @param currencyList bi日志结构，eg:{type: Const.CurrencyType.COIN, value : number}
	 * @param isNotPushNewItem 是否通知前端获得新物品
	 * @param isNotSendFullMsg 如果添加后包裹格子满了，是否通知前端背包已满
	 * @returns {*}
	 */
	public void addCodeItems(List<SimpleItemInfo> codeItems, Const.GOODS_CHANGE_TYPE fromDes, Map<Integer, Object> currencyList, boolean isNotPushNewItem, boolean isNotSendFullMsg) {
		List<NormalItem> items = new ArrayList<>();
		for (int i = 0; i < codeItems.size(); i++) {
			SimpleItemInfo itemInfo = codeItems.get(i);
			Const.ForceType realForceType = itemInfo.forceType;
			List<NormalItem> itemList = ItemUtil.createItemsByItemCode(itemInfo.itemCode, itemInfo.itemNum);
			for (int j = 0; j < itemList.size(); j++) {
				NormalItem item = itemList.get(j);
				int bindType = ItemUtil.getPropBindType(item.prop, realForceType);
				item.setBind(bindType);
			}
			items.addAll(itemList);
		}
		this.addEntityItems(items, fromDes, currencyList, isNotPushNewItem, isNotSendFullMsg, true);
	};

	public void addEntityItem(NormalItem item, Const.GOODS_CHANGE_TYPE fromDes) {
		addEntityItem(item, fromDes, null, false, false);
	}

	/**
	 * 添加单个实体道具
	 * 
	 * @param item 实体道具
	 * @param forceType 强制绑定类型，参考Const.ForceType类型
	 * @param fromDes 来源类型,GOODS_CHANGE_TYPE 和 GOODS_CHANGE_TYPE
	 * @param currencyList bi日志结构，eg:{type: Const.CurrencyType.COIN, value : number}
	 * @param isNotPushNewItem 是否通知前端获得新物品
	 * @param isNotSendFullMsg 如果添加后包裹格子满了，是否通知前端背包已满
	 * @returns {*}
	 */
	public void addEntityItem(NormalItem item, Const.GOODS_CHANGE_TYPE fromDes, Map<Integer, Object> currencyList, boolean isNotPushNewItem, boolean isNotSendFullMsg) {
		List<NormalItem> items = new ArrayList<NormalItem>();
		items.add(item);
		this.addEntityItems(items, fromDes, currencyList, isNotPushNewItem, isNotSendFullMsg, false);
	};

	public void addEntityItems(List<NormalItem> items, Const.GOODS_CHANGE_TYPE fromDes, Map<Integer, Object> currencyList) {
		addEntityItems(items, fromDes, currencyList, false, false, false);
	}

	/**
	 * 添加PO对象到背包
	 */
	public void addEntityItemsPO(List<PlayerItemPO> items, Const.GOODS_CHANGE_TYPE fromDes) {
		List<NormalItem> list = new ArrayList<>();
		for (PlayerItemPO po : items) {
			list.add(ItemUtil.createItemByDbOpts(po));
		}
		addEntityItems(list, fromDes);
	}

	public void addEntityItems(List<NormalItem> items, Const.GOODS_CHANGE_TYPE fromDes) {
		addEntityItems(items, fromDes, null, false, false, false);
	}

	/**
	 * 添加多个实体道具
	 * 
	 * @param items 实体道具数组
	 * @param forceType 强制绑定类型，参考Const.ForceType类型
	 * @param fromDes 来源类型,GOODS_CHANGE_TYPE 和 GOODS_CHANGE_TYPE
	 * @param currencyList bi日志结构，eg:{type: Const.CurrencyType.COIN, value : number}
	 * @param isNotPushNewItem 是否通知前端获得新物品
	 * @param isNotSendFullMsg 如果添加后包裹格子满了，是否通知前端背包已满
	 * @param isCanChangeParam 是否可以修改item参数的信息(道具叠加会修改数量) false:不可以修改参数 true:可以修改参数
	 * @returns {*}
	 */
	private void addEntityItems(List<NormalItem> items, Const.GOODS_CHANGE_TYPE fromDes, Map<Integer, Object> currencyList, boolean isNotPushNewItem, boolean isNotSendFullMsg, boolean isCanChangeParam) {
		if (items == null || items.size() <= 0 || player.isRobot()) {
			return;
		}

		List<NormalItem> actualItems = new ArrayList<>(); // 实体道具
		Map<String, Integer> actualCounts = new HashMap<>(); // 分类统计数量，用于通知前端<code,
																// number>
		for (int i = 0; i < items.size(); i++) {
			NormalItem item = items.get(i);
			// 成就
			player.achievementManager.onGetEquipment(item.itemDb.code);
			// 强化红点
			player.equipManager.updateStrengthScript(item.itemDb.code);
			// 打造红点
			player.equipManager.updateMakeScript(item.itemDb.code);
			// 镶嵌红点
			player.equipManager.updateFillGemScript(item.itemDb.code);
			// 穿装备红点
			if (item.isEquip()) {
				try {
					player.equipManager.updateEquipScript((NormalEquip) item);
				} catch (ClassCastException e) {
					Out.error("NormalEquip cast exception, id=" + item.getId() + " code=" + item.itemDb.code + " name=" + item.getName());
					throw e;
				}
			}

			if (this._isVirtualItem(item, fromDes)) {
				continue;
			}
			if (!isCanChangeParam && item.prop.groupCount > 1) {// 不可以修改items参数(叠加会修改数量),且该道具可以叠加
				NormalItem cloneItem = ItemUtil.createItemByDbOpts(item.cloneItemDB()); // item..toJson4Serialize()
				actualItems.add(cloneItem);
			} else {
				actualItems.add(item);
			}

			if (actualCounts.get(item.itemDb.code) != null) {
				actualCounts.put(item.itemDb.code, actualCounts.get(item.itemDb.code) + item.itemDb.groupCount);
			} else {
				actualCounts.put(item.itemDb.code, item.itemDb.groupCount);
			}
			// 任务
			this.player.taskManager.dealTaskEvent(TaskType.GET_ITEM, item.itemDb.code, item.itemDb.groupCount);
		}
		if (actualItems.size() == 0) {
			return;
		}

		List<Integer> gridIndexs = new ArrayList<>(); // 需要变更的格子索引
		List<NormalItem> createArray = new ArrayList<>(); // 最终需要添加到新格子中的实体道具
		List<HashMap<String, NormalItem>> notFullCache = new ArrayList<>();// {bind:{},
																			// unBind:{}};
																			// //缓存未叠满的实体道具
		notFullCache.add(new HashMap<>());
		notFullCache.add(new HashMap<>());
		List<HashMap<String, Boolean>> bagFullCheck = new ArrayList<>();// {bind:{},unBind:{}};
																		// //判断code在背包中是否已叠加满，已叠加满的直接添加到缓存中
		bagFullCheck.add(new HashMap<>());
		bagFullCheck.add(new HashMap<>());

		for (int i = 0; i < actualItems.size(); i++) {
			NormalItem newItem = actualItems.get(i);
			boolean isBind = newItem.isBinding();
			String code = newItem.itemDb.code;

			if (type == Const.BAG_TYPE.BAG) {
				LogReportService.getInstance().ansycReportItemFlow(player.getPlayer(), LogReportService.OPERATE_ADD, code, newItem.itemDb.groupCount, isBind, fromDes, newItem.getName());
			}

			Map<String, Boolean> bagCheck = isBind ? bagFullCheck.get(0) : bagFullCheck.get(1);
			Map<String, NormalItem> cache = isBind ? notFullCache.get(0) : notFullCache.get(1);

			if (bagCheck.get(code) == null || !bagCheck.get(code)) {
				Map<Integer, NormalItem> grids = this.findNotFullGridsByCode(newItem.itemDb.code, isBind);
				Iterator<Integer> keys = grids.keySet().iterator();
				while (keys.hasNext()) {
					int pos = keys.next();
					NormalItem item = grids.get(pos);
					int leftNum = item.prop.groupCount - item.itemDb.groupCount;
					if (leftNum <= 0) {// 该格子叠加满了
						continue;
					}
					int addNum = newItem.itemDb.groupCount <= leftNum ? newItem.itemDb.groupCount : leftNum;
					newItem.addGroupNum(-addNum);
					item.addGroupNum(addNum);
					if (!gridIndexs.contains(pos)) { // 该格子发生了变更
						gridIndexs.add(pos);
						Out.info("增加道具成功,角色id=", this.player.getId(), ",背包类型为:", this.type.getValue(), ",道具code=", item.itemDb.code, ",id=", item.itemDb.id, ",数量=", addNum, "更新后数量为:", item.itemDb.groupCount, ",位置=", pos, ",来源为:", (fromDes == null ? "未知" : fromDes.value));
					}
					if (newItem.itemDb.groupCount <= 0) { // 全部都叠加到背包中了
						break;
					}
				}
			}
			// 合并后若还有剩余,则合并到缓存的分组中
			if (newItem.itemDb.groupCount > 0) {
				bagCheck.put(code, true); // bagCheck[code] = true;
											// //没有合并玩，说明背包中该道具已叠加满
				int maxGroupCount = newItem.prop.groupCount; // 最大叠加数量
				if (newItem.itemDb.groupCount >= maxGroupCount) { // 满组的，直接占一个格子
					createArray.add(newItem);
				} else if (cache.get(code) == null) {
					cache.put(code, newItem);
				} else {
					NormalItem cacheItem = cache.get(code); // 缓存未叠加满的相同道具
					int totalCount = cacheItem.itemDb.groupCount + newItem.itemDb.groupCount;
					if (totalCount > maxGroupCount) { // 先叠加中，然后将满的缓存道具放到createArray中，将未叠加的放在缓存中
						cacheItem.setNum(maxGroupCount);
						newItem.setNum(totalCount - maxGroupCount);
						// 更新缓存
						createArray.add(cacheItem);
						cache.put(code, newItem);
					} else {// 先叠加中，然后将满的缓存道具放到createArray中
						cacheItem.setNum(totalCount);
						if (cacheItem.itemDb.groupCount == maxGroupCount) {
							createArray.add(cacheItem);
							cache.remove(code);
						}
					}
				}
			}
		}
		// 将未叠加满的分组放在createArray中
		for (Map<String, NormalItem> cache : notFullCache) {
			for (NormalItem newItem : cache.values()) {
				if (newItem != null) {
					createArray.add(newItem);
				}
			}
		}

		// 添加到空格子里
		List<Integer> emptyIndexs = this.findEmptyGrids(createArray.size());
		if (emptyIndexs.size() < createArray.size()) {// 添加物品不成功，造成物品丢弃，临时添加日志追踪
			StringBuilder sb = new StringBuilder("addEntityItems error, playerId=" + this.player.getId() + " emptyIndexsSize:" + emptyIndexs.size() + " createArraySize:" + createArray.size());
			sb.append("\r\nlostItems: ");
			for (NormalItem lostItem : createArray) {
				sb.append("code").append(lostItem.itemCode()).append(",num").append(lostItem.getNum()).append(";");
			}
			Out.error(sb.toString());
			return;
		}
		for (int i = 0; i < createArray.size(); i++) {
			NormalItem item = createArray.get(i);
			this.bagGrids.put(emptyIndexs.get(i), createArray.get(i));
			gridIndexs.add(emptyIndexs.get(i));
			Out.info("增加道具成功,角色id=", this.player.getId(), ",背包类型为:", this.type.getValue(), ",道具code=", item.itemDb.code, ",id=", item.itemDb.id, ",数量=", item.itemDb.groupCount, "更新后数量为:", item.itemDb.groupCount, ",位置=", emptyIndexs.get(i), ",来源为:", (fromDes == null ? "未知" : fromDes.value));
		}
		this._addUsedGridCount(createArray.size());

		// 通知客户端
		if (gridIndexs.size() > 0) {
			this._updateAndPush(gridIndexs, true, fromDes);
		}
		if (!isNotPushNewItem) { // 需要通知客户端道具添加
			Iterator<String> keys = actualCounts.keySet().iterator();
			while (keys.hasNext()) {
				String code = keys.next();
				int actualCount = actualCounts.get(code);
				this._pushNewItem(code, actualCount, fromDes);
			}

		}
		if (this.emptyGridNum() <= 0 && !isNotSendFullMsg) {
			PlayerUtil.sendSysMessageToPlayer(LangService.getValue("BAG_BECOME_FULL_NOTICE"), this.player.getId(), Const.TipsType.BLACK);
		}
		WNNotifyManager.getInstance().updateScript(player);

		// 推送公会捐献红点
		player.guildManager.bagAddItems(items);
	};

	public void addBagGridCount(int num) {
		int oldCount = this.bagPO.bagGridCount;
		this.bagPO.bagGridCount += num;
		List<Integer> gridIndexs = new ArrayList<>();
		for (int i = 1; i <= num; i++) {
			gridIndexs.add(oldCount + i);
		}
		this._updateAndPush(gridIndexs, false);
		this._gridNumChangePush();

		// 通知客户端界面格子变更
		BagGridNumPush.Builder gridNumChange = BagGridNumPush.newBuilder();
		gridNumChange.setS2CType(this.type.getValue());
		gridNumChange.setGridNum(this.bagPO.bagGridCount);
		player.receive("area.bagPush.bagGridNumPush", gridNumChange.build());
	}

	/**
	 * 保存到数据库
	 */
	public void update() {
		Map<Integer, PlayerItemPO> grids = new HashMap<>();
		for (Integer pos : this.bagGrids.keySet()) {
			if (this.bagGrids.get(pos) != null) {
				grids.put(pos, bagGrids.get(pos).itemDb);
			}
		}
		this.bagPO.bagGrids = grids;
	}

}
