package com.wanniu.game.consignmentShop;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.alibaba.fastjson.JSON;
import com.wanniu.core.db.GCache;
import com.wanniu.core.db.ModifyDataType;
import com.wanniu.core.logfs.Out;
import com.wanniu.core.util.StringUtil;
import com.wanniu.game.common.Const;
import com.wanniu.game.common.ConstsTR;
import com.wanniu.game.item.ItemUtil;
import com.wanniu.game.poes.ConsignmentItemsPO;
import com.wanniu.redis.GameDao;

public class ConsignmentDataManager {

	private Date refreshTime;
	private Map<String, List<ConsignmentItemsPO>> cache;
	private Map<String, Date> cacheTime;
	public Map<String, ConsignmentItemsPO> items = new ConcurrentHashMap<>();

	public ConsignmentDataManager() {
		this.refreshTime = new Date();
		this.cache = new HashMap<>();
		this.cacheTime = new HashMap<>();

		ConstsTR tr = ConstsTR.consignment_itemsTR;
		long start = System.currentTimeMillis();
		Map<String, String> result = GCache.hgetAll(tr.value);
		Out.info("加载寄卖行耗时:", (System.currentTimeMillis() - start));
		if (result != null) {
			for (String id : result.keySet()) {
				ConsignmentItemsPO item = JSON.parseObject(result.get(id), ConsignmentItemsPO.class);
				if (item.consignmentTime <= System.currentTimeMillis()) {
					GCache.hremove(tr.value, id);
					GameDao.delToDB(tr, id);

					// 超时，邮件返还给玩家
					ConsignmentUtil.timeOutMail(item);
					continue;
				}
				items.put(id, item);
			}
		} else {
			List<ConsignmentItemsPO> list = GameDao.findFromDB(tr, null, ConsignmentItemsPO.class);
			for (ConsignmentItemsPO item : list) {
				if (item.consignmentTime <= System.currentTimeMillis()) {
					// GCache.hremove(tr.value, item.id);
					GameDao.delToDB(tr, item.id);

					// 超时，邮件返还给玩家
					ConsignmentUtil.timeOutMail(item);
					continue;
				}
				items.put(item.id, item);
			}
		}
	}

	public ConsignmentItemsPO findById(String id) {
		return this.items.get(id);
	}

	/**
	 * 获取玩家寄卖的所有物品
	 */
	public List<ConsignmentItemsPO> findByPlayerId(String playerId) {
		List<ConsignmentItemsPO> list = new ArrayList<>();
		for (ConsignmentItemsPO item : items.values()) {
			if (item.consignmentPlayerId.equals(playerId)) {
				list.add(item);
			}
		}
		return list;
	}

	public boolean insert(ConsignmentItemsPO item) {
		ConsignmentItemsPO cItem = this.items.get(item.id);
		if (cItem == null) {
			this.items.put(item.id, item);
			this.refreshTime = new Date();
			return true;
		}
		return false;
	}

	public boolean remove(String id) {
		ConsignmentItemsPO cItem = this.items.get(id);
		if (cItem == null) {
			return false;
		}
		this.items.remove(id);
		GCache.hremove(ConstsTR.consignment_itemsTR.value, id);
		GameDao.delToDB(ConstsTR.consignment_itemsTR, id);

		this.refreshTime = new Date();
		return true;
	}

	/**
	 * 保存寄卖行物品到redis
	 * 
	 * @param items
	 */
	public void saveConsignmentItems() {
		for (String id : this.items.keySet()) {
			// 保存到redis
			GameDao.update(ConstsTR.consignment_itemsTR.value, id, items.get(id));
			// 保存到数据库
			GameDao.updateToDB(ConstsTR.consignment_itemsTR, id, ModifyDataType.MAP);
		}
	}

	public boolean isNeedRefresh(String type) {
		Date cTime = this.cacheTime.get(type);
		if (cTime == null || cTime.getTime() <= this.refreshTime.getTime()) {
			return true;
		}

		return false;
	}

	/**
	 * 获得缓存的类型
	 * 
	 * @param pro
	 * @param qcolor
	 * @param order
	 * @returns {string}
	 */
	public static String getCacheType(int itemSecondType, int pro, int qcolor, int order, String itemType, int level) {
		return new String(itemSecondType + ":" + itemType + ":" + pro + qcolor + order + level);
	}

	public void refreshCache(int itemSecondType, int pro, int qcolor, int order, String itemType, int level) {
		Map<String, Object> query = new HashMap<>();
		if (itemSecondType != -1) {
			query.put("itemSeondType", itemSecondType);
		}
		if (pro != -1) {
			query.put("pro", pro);
		}

		if (qcolor != -1) {
			query.put("qColor", qcolor);
		}

		if (StringUtil.isNotEmpty(itemType)) {
			String[] types = itemType.split(",");
			ArrayList<Integer> list = new ArrayList<>();
			if (types.length == 1) {
				list.add(Integer.parseInt(types[0]));
			} else {
				for (String type : types) {
					list.add(Integer.parseInt(type));
				}
			}
			query.put("itemType", list);
		}

		if (level != 0 && level != -1) {
			query.put("levelReq", level);
		}

		Out.debug("query:", query);
		Out.debug("order:", order);
		List<ConsignmentItemsPO> data = this.find(query, order);
		String type = getCacheType(itemSecondType, pro, qcolor, order, itemType, level);
		this.cache.put(type, data);
		this.cacheTime.put(type, new Date());

		Out.debug("type " + type + " cache:", this.cache);
	}

	public List<ConsignmentItemsPO> getCache(String type) {
		List<ConsignmentItemsPO> list = this.cache.get(type);
		List<ConsignmentItemsPO> result = new ArrayList<>();
		long now = System.currentTimeMillis();
		for (ConsignmentItemsPO item : list) {
			// 延迟上架的过滤掉...
			if (item.consignmentTime - ConsignmentUtil.sellTime() > now) {
				continue;
			}
			// 过期了
			if (item.consignmentTime < now) {
				continue;
			}
			result.add(item);
		}
		return result;
	}

	/**
	 * @param option 排序规则
	 */
	public List<ConsignmentItemsPO> data(int order) {
		long now = System.currentTimeMillis();
		List<ConsignmentItemsPO> list = new ArrayList<>();
		for (Map.Entry<String, ConsignmentItemsPO> node : this.items.entrySet()) {
			ConsignmentItemsPO item = node.getValue();
			// 延迟上架的过滤掉...
			if (item.consignmentTime - ConsignmentUtil.sellTime() > now) {
				continue;
			}
			// 过期了
			if (item.consignmentTime < now) {
				continue;
			}
			list.add(node.getValue());
		}
		list.sort(comparator(order));
		return list;
	}

	/**
	 * 
	 * @param order Const.ConsignmentOrderType
	 */
	private Comparator<ConsignmentItemsPO> comparator(int order) {
		Comparator<ConsignmentItemsPO> comparator = new Comparator<ConsignmentItemsPO>() {
			@Override
			public int compare(ConsignmentItemsPO o1, ConsignmentItemsPO o2) {
				if (order == Const.ConsignmentOrderType.TIME_ASC.getValue()) {
					return (int) (o1.consignmentTime - o2.consignmentTime);
				} else if (order == Const.ConsignmentOrderType.TIME_DES.getValue()) {
					return (int) (o2.consignmentTime - o1.consignmentTime);
				} else if (order == Const.ConsignmentOrderType.PRICE_ASC.getValue()) {
					return (o1.consignmentPrice - o2.consignmentPrice);
				} else if (order == Const.ConsignmentOrderType.PRICE_DES.getValue()) {
					return (o2.consignmentPrice - o1.consignmentPrice);
				} else if (order == Const.ConsignmentOrderType.LEVEL_ASC.getValue()) {
					return (o1.level - o2.level);
				} else if (order == Const.ConsignmentOrderType.LEVEL_DES.getValue()) {
					return (o2.level - o1.level);
				}
				return 0;
			}
		};
		return comparator;
	}

	// public List<ConsignmentItemsPO> findOverTime(long time) {
	// List<ConsignmentItemsPO> retList = new ArrayList<>();
	// for (Map.Entry<String, ConsignmentItemsPO> node : this.items.entrySet()) {
	// ConsignmentItemsPO item = node.getValue();
	// if (item.consignmentTime <= time) {
	// retList.add(item);
	// }
	// }
	// return retList;
	// }

	private List<ConsignmentItemsPO> findByItemSeondType(int itemSecondType, List<ConsignmentItemsPO> list) {
		List<ConsignmentItemsPO> retList = new ArrayList<>();
		if (list == null) {
			for (Map.Entry<String, ConsignmentItemsPO> node : this.items.entrySet()) {
				ConsignmentItemsPO item = node.getValue();
				if (item.itemSecondType == itemSecondType) {
					retList.add(item);
				}
			}
		} else {
			for (ConsignmentItemsPO item : list) {
				if (item.itemSecondType == itemSecondType) {
					retList.add(item);
				}
			}
		}
		return retList;
	}

	private List<ConsignmentItemsPO> findByItemId(String id, List<ConsignmentItemsPO> list) {
		List<ConsignmentItemsPO> retList = new ArrayList<>();
		if (list == null) {
			for (Map.Entry<String, ConsignmentItemsPO> node : this.items.entrySet()) {
				ConsignmentItemsPO item = node.getValue();
				if (item.id.equals(id)) {
					retList.add(item);
				}
			}
		} else {
			for (ConsignmentItemsPO item : list) {
				if (item.id.equals(id)) {
					retList.add(item);
				}
			}
		}
		return retList;
	}

	private List<ConsignmentItemsPO> findByPro(int pro, List<ConsignmentItemsPO> list) {
		ArrayList<ConsignmentItemsPO> retList = new ArrayList<>();
		if (list == null) {
			for (Map.Entry<String, ConsignmentItemsPO> node : this.items.entrySet()) {
				ConsignmentItemsPO item = node.getValue();
				if (item.pro == pro) {
					retList.add(item);
				}
			}
		} else {
			for (ConsignmentItemsPO item : list) {
				if (item.pro == pro) {
					retList.add(item);
				}
			}
		}
		return retList;
	}

	private List<ConsignmentItemsPO> findByQColor(int qColor, List<ConsignmentItemsPO> list) {
		ArrayList<ConsignmentItemsPO> retList = new ArrayList<>();
		if (list == null) {
			for (Map.Entry<String, ConsignmentItemsPO> node : this.items.entrySet()) {
				ConsignmentItemsPO item = node.getValue();
				if (ItemUtil.getPropByCode(item.db.code).qcolor == qColor) {
					retList.add(item);
				}
			}
		} else {
			for (ConsignmentItemsPO item : list) {
				if (ItemUtil.getPropByCode(item.db.code).qcolor == qColor) {
					retList.add(item);
				}
			}
		}
		return retList;
	}

	private List<ConsignmentItemsPO> findByReqLevel(int level, List<ConsignmentItemsPO> list) {
		List<ConsignmentItemsPO> retList = new ArrayList<>();
		if (list == null) {
			for (Map.Entry<String, ConsignmentItemsPO> node : this.items.entrySet()) {
				ConsignmentItemsPO item = node.getValue();
				if (item.level == level) {
					retList.add(item);
				}
			}
		} else {
			for (ConsignmentItemsPO item : list) {
				if (ItemUtil.getPropByCode(item.db.code).levelReq == level) {
					retList.add(item);
				}
			}
		}
		return retList;
	}

	private List<ConsignmentItemsPO> findByItemType(List<Integer> itemType, List<ConsignmentItemsPO> list) {
		ArrayList<ConsignmentItemsPO> retList = new ArrayList<>();
		if (list == null) {
			for (Map.Entry<String, ConsignmentItemsPO> node : this.items.entrySet()) {
				ConsignmentItemsPO item = node.getValue();
				for (Integer value : itemType) {
					if (item.itemType == value) {
						retList.add(item);
					}
				}
			}
		} else {
			for (ConsignmentItemsPO item : list) {
				for (Integer value : itemType) {
					if (item.itemType == value) {
						retList.add(item);
					}
				}
			}
		}
		return retList;
	}

	private List<ConsignmentItemsPO> find(Map<String, Object> types, int order) {
		List<ConsignmentItemsPO> list = null;
		for (Map.Entry<String, Object> node : types.entrySet()) {
			String key = node.getKey();
			if (key.equals("id")) {
				String value = String.valueOf(node.getValue());
				list = findByItemId(value, list);
			}
			if (key.equals("itemSeondType")) {
				int value = (int) node.getValue();
				list = findByItemSeondType(value, list);
			} else if (key.equals("pro")) {
				int value = (int) node.getValue();
				list = findByPro(value, list);
			} else if (key.equals("qColor")) {
				int value = (int) node.getValue();
				list = findByQColor(value, list);
			} else if (key.equals("itemType")) {
				List<Integer> value = (List<Integer>) node.getValue();
				list = findByItemType(value, list);
			} else if (key.equals("levelReq")) {
				int value = (int) node.getValue();
				list = findByReqLevel(value, list);
			}
		}
		list.sort(comparator(order));
		return list;
	}
}
