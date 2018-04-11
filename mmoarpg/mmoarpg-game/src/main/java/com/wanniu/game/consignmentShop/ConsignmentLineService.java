package com.wanniu.game.consignmentShop;

import java.util.ArrayList;
import java.util.List;

import com.wanniu.core.logfs.Out;
import com.wanniu.game.common.Const;
import com.wanniu.game.data.base.DItemEquipBase;
import com.wanniu.game.item.ItemUtil;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.poes.ConsignmentItemsPO;

import pomelo.area.ConsignmentLineHandler.ConsignmentListResponse;

public class ConsignmentLineService {

	public static class ConsignmentQueryParam {
		public int pro;
		public int qColor;
		public int order;
		public int itemSecondType;
		public int page;
		public String itemType;
		public int level;
	}

	private static ConsignmentLineService instance;

	public static ConsignmentLineService getInstance() {
		if (instance == null) {
			synchronized (ConsignmentLineService.class) {
				if (instance == null) {
					instance = new ConsignmentLineService();
				}
			}
		}
		return instance;
	}

	public static final int ONE_PAGE = 20;
	public static final int MAX_PAGE = 999;
	public static final int MAX_SEARCH = 100;
	private ConsignmentDataManager dataManager;

	private ConsignmentLineService() {
		this.dataManager = new ConsignmentDataManager();
		// JobFactory.addScheduleJob(new Runnable() {
		// @Override
		// public void run() {
		// async();
		// }
		// }, 5 * 1000, 5 * 1000);
	}

	public boolean add(ConsignmentItemsPO item) {
		return dataManager.insert(item);
	}

	/**
	 * 向寄卖行添加物品
	 * 
	 * @param item
	 * @returns {*}
	 */
	public boolean insert(ConsignmentItemsPO item) {
		Out.debug("ConsignmentService insert:", "item:", item);
		boolean ret = dataManager.insert(item);
		if (ret) {
			// this.insertDB(logicServerId, item);
			Out.debug("寄卖行 新增物品 id:", item.id);
		} else {
			Out.error("寄卖行新增物品不成功：", item.id);
		}

		return ret;
	}

	/**
	 * 删除寄卖行的物品
	 * 
	 * @param id
	 * @returns {*}
	 */
	public boolean remove(String id) {
		// this.removeDB(logicServerId, id);
		boolean ret = dataManager.remove(id);
		if (ret) {
			// this.removeDB(logicServerId, id);
			Out.debug("寄卖行 主动删除物品 id:", id);
		} else {
			Out.error("寄卖行 要删除的物品不存在:", id);
		}
		return ret;
	}

	public ConsignmentItemsPO getById(String id) {
		return dataManager.findById(id);
	}

	/**
	 * 获取玩家寄卖的所有物品
	 */
	public List<ConsignmentItemsPO> findByPlayerId(String playerId) {
		return dataManager.findByPlayerId(playerId);
	}

	/**
	 * 保存寄卖行物品到redis
	 * 
	 * @param items
	 */
	public void onCloseGame() {
		this.dataManager.saveConsignmentItems();
	}

	public final ConsignmentListResponse.Builder query(WNPlayer player, int logicServerId, ConsignmentQueryParam opts) {
		int pro = opts.pro;
		int qColor = opts.qColor;
		int order = opts.order;
		int itemSecondType = opts.itemSecondType;
		String itemType = opts.itemType;
		int level = opts.level;
		int page = opts.page;

		if (page > MAX_PAGE) {
			page = MAX_PAGE;
		}

		ConsignmentListResponse.Builder ret = ConsignmentListResponse.newBuilder();
		List<pomelo.item.ItemOuterClass.ConsignmentItem> data = new ArrayList<>();
		// ret.data = [];

		List<ConsignmentItemsPO> result = null;

		if (pro == -1 && qColor == -1 && itemSecondType == -1 && itemType.length() == 0 && (level == 0 || level == -1)) {
			// 默认全部请求
			result = dataManager.data(order);
		} else {
			String type = ConsignmentDataManager.getCacheType(itemSecondType, pro, qColor, order, itemType, level);
			if (dataManager.isNeedRefresh(type)) {
				dataManager.refreshCache(itemSecondType, pro, qColor, order, itemType, level);
			}
			result = dataManager.getCache(type);
			if (result == null) {
				dataManager.refreshCache(itemSecondType, pro, qColor, order, itemType, level);
				result = dataManager.getCache(type);
			}
		}
		ret.setS2CTotalPage((result.size() + ONE_PAGE - 1) / ONE_PAGE);
		int end = page * ONE_PAGE;
		int begin = end - ONE_PAGE;
		if (begin < result.size()) {
			for (int i = begin; i < end && i < result.size(); ++i) {
				ConsignmentItemsPO item = result.get(i);
				// String consignmentPlayerName = item.consignmentPlayerName;
				// int consignmentPlayerPro = item.consignmentPlayerPro;
				//
				// NormalItem normalItem = ItemUtil.createItemByDbOpts(item.db);
				// pomelo.item.ItemOuterClass.ConsignmentItem.Builder d =
				// pomelo.item.ItemOuterClass.ConsignmentItem.newBuilder();
				// d.setDetail(normalItem.getItemDetail(player.playerBasePO));
				// d.setGroupCount(item.groupCount);
				// d.setPublishTimes(item.publishTimes);
				// d.setConsignmentPrice(item.consignmentPrice);
				// d.setConsignmentTime(String.valueOf(item.consignmentTime));
				// d.setConsignmentPlayerName(consignmentPlayerName);
				// d.setConsignmentPlayerPro(consignmentPlayerPro);
				// d.setConsignmentPlayerId(item.consignmentPlayerId);
				data.add(ConsignmentUtil.buildConsignmentItem(player, item));
			}
			ret.addAllS2CData(data);
		}
		return ret;
	}

	/**
	 * 某服务器搜索
	 * 
	 * @param logicServerId
	 * @param condition
	 */
	public final ArrayList<pomelo.item.ItemOuterClass.ConsignmentItem> search(WNPlayer player, int logicServerId, String condition) {
		List<ConsignmentItemsPO> result = dataManager.data(Const.ConsignmentOrderType.TIME_DES.getValue());

		ArrayList<pomelo.item.ItemOuterClass.ConsignmentItem> ret = new ArrayList<>();
		for (int i = 0, sum = 0; i < result.size() && sum < MAX_SEARCH; i++) {
			ConsignmentItemsPO item = result.get(i);
			DItemEquipBase itemBase = ItemUtil.getPropByCode(item.db.code);
			if (itemBase.name.indexOf(condition) != -1) {

				// String consignmentPlayerName = item.consignmentPlayerName;
				// NormalItem normalItem = ItemUtil.createItemByDbOpts(item.db);
				// pomelo.item.ItemOuterClass.ConsignmentItem.Builder d =
				// pomelo.item.ItemOuterClass.ConsignmentItem.newBuilder();
				// d.setDetail(normalItem.getItemDetail(player.playerBasePO));
				// d.setGroupCount(item.groupCount);
				// d.setPublishTimes(item.publishTimes);
				// d.setConsignmentPrice(item.consignmentPrice);
				// d.setConsignmentTime(String.valueOf(item.consignmentTime));
				// d.setConsignmentPlayerName(consignmentPlayerName);
				// d.setConsignmentPlayerPro(item.consignmentPlayerPro);
				// d.setConsignmentPlayerId(item.consignmentPlayerId);
				ret.add(ConsignmentUtil.buildConsignmentItem(player, item));
				sum++;
			}
		}

		Out.debug("ConsignmentService search:", ret);
		return ret;
	}

	// public final void async() {
	// int logicServerId = 0;
	// List<ConsignmentItemsPO> datas = dataManager.findOverTime(new
	// Date().getTime());
	// for (ConsignmentItemsPO v : datas) {
	// // 从全局缓存中移除
	// boolean ret = dataManager.remove(v.id);
	// if (ret) {
	// Out.debug("consignmentTime logicServerId:" + logicServerId + " id:" + v.id);
	//
	// ConsignmentUtil.timeOutMail(v);
	//
	// Out.info("寄卖行 系统到期下架物品 id:", v.id +"," + v.db.code + ", playerId=" +
	// v.consignmentPlayerId);
	// } else {
	// Out.error("寄卖行 找不到下架的物品:" + v.id);
	// }
	// }
	// }

}
