package com.wanniu.game.shopMall;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.wanniu.core.game.LangService;
import com.wanniu.core.logfs.Out;
import com.wanniu.core.util.StringUtil;
import com.wanniu.game.GWorld;
import com.wanniu.game.common.Const;
import com.wanniu.game.common.Const.GOODS_CHANGE_TYPE;
import com.wanniu.game.common.ConstsTR;
import com.wanniu.game.data.GameData;
import com.wanniu.game.data.ShopMallConfigCO;
import com.wanniu.game.data.base.DItemEquipBase;
import com.wanniu.game.data.ext.ExchangeMallExt;
import com.wanniu.game.data.ext.ShopMallItemsExt;
import com.wanniu.game.item.ItemConfig;
import com.wanniu.game.mail.MailUtil;
import com.wanniu.game.mail.data.MailData.Attachment;
import com.wanniu.game.mail.data.MailPlayerData;
import com.wanniu.game.player.GlobalConfig;
import com.wanniu.game.player.PlayerUtil;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.player.bi.LogReportService;
import com.wanniu.game.poes.PlayerPO;
import com.wanniu.game.poes.ShopMallPO;
import com.wanniu.redis.PlayerPOManager;

import pomelo.Common.KeyValueStruct;
import pomelo.area.PlayerHandler.SuperScriptType;
import pomelo.area.ShopMallHandler.MallItem;
import pomelo.area.ShopMallHandler.MallTab;

public class ShopMallManager {

	public static ShopMallItemData createShopMallItemData(String jsonString) {
		ShopMallItemData data = JSON.parseObject(jsonString, ShopMallItemData.class);
		return data;
	}

	public class ShopMallData {
		public String itemType;
		public int moneyType;
		public int isOpen;
		public String name;
		public int isLimit;
	}

	public class ShopMallResult {
		public boolean result;
		public String msg;
		public int totalNum;

		public ShopMallResult(boolean result, String msg) {
			this.result = result;
			this.msg = msg;
		}
	}

	private WNPlayer player;
	public ShopMallPO db;

	public ShopMallManager(WNPlayer player, ShopMallPO db) {
		this.player = player;
		this.db = db;
		if (this.db == null) {
			this.db = new ShopMallPO();
			this.db.dayMallItemNums = new HashMap<>();
			this.db.weekMallItemNums = new HashMap<>();
			this.db.seenTab = new HashMap<>();

			this.refreshNewDay();
			this.refreshNewWeek();
			PlayerPOManager.put(ConstsTR.shopMallTR, this.player.getId(), this.db);
		}
	}

	public final ShopMallResult buyMallItem(String itemId, int count, String playerId, int bDiamond) {
		ShopMallResult result = new ShopMallResult(true, null);

		ShopMallConfigCO shopMallConfigCO = null;

		ShopMallItemsExt mallItemProp = ShopMallConfig.getInstance().fingShowMallItemByID(itemId);
		ExchangeMallExt exchangeMallExt = ShopMallConfig.getInstance().findExchangeMallItemByID(itemId);
		Date now = new Date();
		Date endTime = null;// mallItemProp.endTime;

		// mallItemProp和exchangeMallExt只有一个不为null
		if (mallItemProp == null && exchangeMallExt == null) {
			return new ShopMallResult(false, LangService.getValue("ITEM_NULL"));
		}

		if (mallItemProp != null) {
			// 每日限购, 每周限购
			if (mallItemProp.buyTimes > 0 || mallItemProp.weekBuyTimes > 0) {
				int remainNum = this.getSelfMallItemRemainNum(mallItemProp.iD);
				if ((remainNum > 0 && remainNum < count) || remainNum == 0) {
					return new ShopMallResult(false, LangService.getValue("SHOPMALL_LIMIT_ITEM_OVER"));
				}
			}

			shopMallConfigCO = GameData.ShopMallConfigs.get(mallItemProp.itemType);
			endTime = mallItemProp.endTime;
		}
		if (exchangeMallExt != null) {
			// 每日限购, 每周限购,总共限购
			if (exchangeMallExt.exchangeTimes > 0 || exchangeMallExt.weekExchangeTimes > 0 || exchangeMallExt.totalTimes>0) {
				int remainNum = this.getSelfMallExchangeItemRemainNum(exchangeMallExt.iD);
				if ((remainNum > 0 && remainNum < count) || remainNum == 0) {
					return new ShopMallResult(false, LangService.getValue("SHOPMALL_LIMIT_ITEM_OVER"));
				}
			}

			shopMallConfigCO = GameData.ShopMallConfigs.get(exchangeMallExt.itemType);
			endTime = exchangeMallExt.endTime;
		}

		if (!this.isValidOfMallTab(shopMallConfigCO.itemType)) {
			return new ShopMallResult(false, LangService.getValue("SHOPMALL_ITEM_OPEN_NOT"));
		}

		if (endTime != null && now.getTime() > endTime.getTime()) {
			return new ShopMallResult(false, LangService.getValue("SHOPMALL_LIMIT_ITEM_OVER"));
		}
		// playerId玩家是否是好友
		if (StringUtil.isNotEmpty(playerId) && !this.player.friendManager.isFriend(playerId)) {
			return new ShopMallResult(false, LangService.getValue("FRIEND_FIND_NONE"));
		}
		// if (this.player.friendManager.isFriend(playerId) &&
		// mallItemProp.isBind == 1) {
		// return new
		// ShopMallResult(false,LangService.getValue("SHOPMALL_BIND_ITEM_CAN_NOT_GIVE"));
		// }

		int itemNum = -1;
		if (mallItemProp != null) {
			int priceSingle = mallItemProp.price2;
			if (priceSingle <= 0) {
				priceSingle = mallItemProp.price;
			}
			int costNum = priceSingle * count;
			itemNum = mallItemProp.num * count;

			if (shopMallConfigCO.consumeType == Const.SHOP_MALL_CONSUME_TYPE.DIAMOND.getValue()) {
				// 判断玩家consomePoint是否足够
				if (!this.player.moneyManager.enoughDiamond(costNum)) {
					return new ShopMallResult(false, LangService.getValue("DIAMAND_NOT_ENOUGH"));
				}
			} else if (shopMallConfigCO.consumeType == Const.SHOP_MALL_CONSUME_TYPE.TICKET.getValue()) {
				// 判断玩家Ticket是否足够
				if (!this.player.moneyManager.enoughTicketAndDiamond(costNum)) {
					return new ShopMallResult(false, LangService.getValue("DIAMAND_NOT_ENOUGH"));
				}
			}

			int consumePoint = mallItemProp.points * count;
			// 添加玩家consume
			this.player.moneyManager.addConsumePoint(consumePoint, Const.GOODS_CHANGE_TYPE.shop);

			List<KeyValueStruct> itemChange = new ArrayList<>();
			KeyValueStruct.Builder it = KeyValueStruct.newBuilder();
			it.setKey(mallItemProp.itemCode);
			it.setValue(String.valueOf(itemNum));
			itemChange.add(it.build());

			// 更新玩家数据
			if (shopMallConfigCO.consumeType == Const.SHOP_MALL_CONSUME_TYPE.DIAMOND.getValue()) {
				this.player.moneyManager.costDiamond(costNum, Const.GOODS_CHANGE_TYPE.shop, itemChange);
			} else if (shopMallConfigCO.consumeType == Const.SHOP_MALL_CONSUME_TYPE.TICKET.getValue()) {
				this.player.moneyManager.costTicketAndDiamond(costNum, Const.GOODS_CHANGE_TYPE.shop, itemChange);
			}

			// 增加物品
			if (StringUtil.isEmpty(playerId)) {
				int forceType = mallItemProp.isBind;
				if (!this.player.getWnBag().testAddCodeItem(mallItemProp.itemCode, itemNum, Const.ForceType.getE(forceType))) {
					return new ShopMallResult(false, LangService.getValue("BAG_FULL"));
				}
				Out.info("商城购买 playerId=", player.getId(), ",itemId=", mallItemProp.itemCode, ",count=", itemNum);
				Map<Integer, Object> currencyList = new HashMap<>();
				currencyList.put(mallItemProp.consumeType, priceSingle);
				this.player.getWnBag().addCodeItem(mallItemProp.itemCode, itemNum, Const.ForceType.getE(forceType), Const.GOODS_CHANGE_TYPE.shop, currencyList);
			} else {
				MailPlayerData mailData = new MailPlayerData();
				mailData.mailSender = this.player.getName();
				mailData.mailSenderId = this.player.getId();
				mailData.mailTitle = LangService.getValue("SHOPMALL_SEND_FRIEND_ITEM_TITLE");
				mailData.mailText = LangService.getValue("SHOPMALL_SEND_FRIEND_ITEM").replace("{playerName}", this.player.getName()).replace("{itemNum}", String.valueOf(itemNum)).replace("{itemName}", mallItemProp.name);
				mailData.mailRead = 1;
				mailData.mailIcon = this.player.getPlayer().pro;
				ArrayList<Attachment> atts = new ArrayList<>();
				Attachment att = new Attachment();
				att.itemCode = mallItemProp.itemCode;
				att.itemNum = itemNum;
				atts.add(att);
				mailData.attachments = atts;
				MailUtil.getInstance().sendMailToOnePlayer(playerId, mailData, GOODS_CHANGE_TYPE.shop);
				// 获取好友脚本属性 给好友发送消息
				int friendshipNum = GameData.SocialFriends.get(7).friendshipNum;// .socialFriendProps.find({MSocialAction:
																				// 7})[0].FriendshipNum;
				String messageText = GameData.SocialFriends.get(7).messageText;// .socialFriendProps.find({MSocialAction:
																				// 7})[0].MessageText;

				this.player.baseDataManager.addFriendly(friendshipNum);
				this.player.pushDynamicData("friendly", player.player.friendly);
				PlayerPO friend = PlayerPOManager.findPO(ConstsTR.playerTR, playerId, PlayerPO.class);
				PlayerUtil.sendSysMessageToPlayer(messageText.replace("{playerName}", friend.name).replace("itemName", mallItemProp.name), this.player.getId());
			}

			result.totalNum = player.bag.findItemNumByCode(mallItemProp.itemCode);

			if (mallItemProp.buyTimes > 0 || mallItemProp.weekBuyTimes > 0) {
				this.addSelfMallItemNum(mallItemProp.iD, count);
			}

			// 上报购买信息
			LogReportService.getInstance().ansycReportShop(player, mallItemProp.itemCode, itemNum, shopMallConfigCO.consumeType, costNum);
		}
		if (exchangeMallExt != null) {
			itemNum = exchangeMallExt.num * count;

			for (Map.Entry<String, Integer> entry : exchangeMallExt.exchangeNeedMap.entrySet()) {
				if (player.bag.findItemNumByCode(entry.getKey()) < entry.getValue() * count) {
					return new ShopMallResult(false, "");
				}
			}

			int forceType = exchangeMallExt.isBind;
			if (!this.player.getWnBag().testAddCodeItem(exchangeMallExt.itemCode, itemNum, Const.ForceType.getE(forceType))) {
				return new ShopMallResult(false, LangService.getValue("BAG_FULL"));
			}

			for (Map.Entry<String, Integer> entry : exchangeMallExt.exchangeNeedMap.entrySet()) {
				player.bag.discardItem(entry.getKey(), entry.getValue() * count, Const.GOODS_CHANGE_TYPE.shop);
			}

			Out.info("商城兑换 playerId=", player.getId(), ",itemId=", exchangeMallExt.itemCode, ",count=", count);

			this.player.getWnBag().addCodeItem(exchangeMallExt.itemCode, itemNum, Const.ForceType.getE(forceType), Const.GOODS_CHANGE_TYPE.shop);

			result.totalNum = player.bag.findItemNumByCode(exchangeMallExt.itemCode);

			if (exchangeMallExt.exchangeTimes > 0 || exchangeMallExt.weekExchangeTimes > 0) {
				this.addSelfMallExchangeItemNum(exchangeMallExt.iD, count);
			}
		}

		return result;
	}

	public final ArrayList<MallItem.Builder> getMallItemList(int itemType) {
		ArrayList<MallItem.Builder> items = new ArrayList<>();
		Date now = new Date();
		if (!this.isValidOfMallTab(itemType)) {
			return items;
		}
		this.seeTab(itemType);
		// List<ShopMallItemsExt> mallItems =
		// ShopMallConfig.getInstance().findShopMallPropsByConsumeTypeAndItemType(itemType);
		List<ShopMallItemsExt> mallItems = GameData.findShopMallItemss(t -> t.itemType == itemType);
		List<ExchangeMallExt> exchangeMallExts = GameData.findExchangeMalls(t -> t.itemType == itemType);
		if (mallItems != null) {
			for (ShopMallItemsExt mallItem : mallItems) {
				if (mallItem.serveLimit == Const.SHOP_MALL_SERVER_LIMIT.SELF.getValue()) {
					// 199测试商城只在debug模式下显示
					if (!GWorld.DEBUG && mallItem.itemType == 199) {
						continue;
					}

					int remainNum = this.getSelfMallItemRemainNum(mallItem.iD);
					boolean flag = true;
					if (mallItem.isShow == 0) {
						flag = false;
					}
					if (remainNum == 0 && mallItem.isUseOut == 1) {
						flag = false;
					}
					long endTime = 0;
					Date endDate = mallItem.endTime;
					if (endDate != null) {
						if (now.getTime() > endDate.getTime()) {
							flag = false;
						} else {
							endTime = endDate.getTime();
						}
					}

					if (flag) {
						DItemEquipBase itemProp = ItemConfig.getInstance().getItemProp(mallItem.itemCode);
						if (itemProp != null) {
							MallItem.Builder data = MallItem.newBuilder();
							data.setId(mallItem.iD);
							data.setCode(mallItem.itemCode);
							data.setGroupCount(mallItem.num);
							data.setOriginPrice(mallItem.price);
							int nowPrice = mallItem.price2 <= 0 ? mallItem.price : mallItem.price2;
							data.setNowPrice(nowPrice);
							data.setDisCount(mallItem.series);
							data.setEndTime((int) (endTime / 1000));
							data.setRemainNum(remainNum);
							data.setConsumeScore(mallItem.points);
							data.setCanSend(1);
							switch (mallItem.isBind) {
							case 1:
								data.setBindType(1);
								data.setCanSend(0);
								break;
							case 2:
								data.setBindType(0);
								break;
							default:
								data.setBindType(itemProp.bindType);
								break;
							}
							items.add(data);
						} else {
							Out.error("there is no shop item prop, code : ", mallItem.itemCode);
						}

					}
				} else {
					// todo..
				}
			}
		}

		if (exchangeMallExts != null) {
			for (ExchangeMallExt mallItem : exchangeMallExts) {
				// 199测试商城只在debug模式下显示
				if (!GWorld.DEBUG && mallItem.itemType == 199) {
					continue;
				}

				int remainNum = this.getSelfMallExchangeItemRemainNum(mallItem.iD);
				boolean flag = true;
				if (mallItem.isShow == 0) {
					flag = false;
				}
				if (remainNum == 0 && mallItem.isUseOut == 1) {
					flag = false;
				}
				long endTime = 0;
				Date endDate = mallItem.endTime;
				if (endDate != null) {
					if (now.getTime() > endDate.getTime()) {
						flag = false;
					} else {
						endTime = endDate.getTime();
					}
				}

				if (flag) {
					DItemEquipBase itemProp = ItemConfig.getInstance().getItemProp(mallItem.itemCode);
					if (itemProp != null) {
						MallItem.Builder data = MallItem.newBuilder();
						data.setId(mallItem.iD);
						data.setCode(mallItem.itemCode);
						data.setGroupCount(mallItem.num);
						data.setOriginPrice(0);
						data.setNowPrice(0);
						data.setDisCount(0);
						data.setEndTime((int) (endTime / 1000));
						data.setRemainNum(remainNum);
						data.setConsumeScore(0);
						data.setCanSend(1);
						switch (mallItem.isBind) {
						case 1:
							data.setBindType(1);
							data.setCanSend(0);
							break;
						case 2:
							data.setBindType(0);
							break;
						default:
							data.setBindType(itemProp.bindType);
							break;
						}
						items.add(data);
					} else {
						Out.error("there is no shop item prop, code : ", mallItem.itemCode);
					}

				}
			}
		}
		return items;
	}

	public final List<MallTab.Builder> getMallTabs() {
		List<MallTab.Builder> tabs = new ArrayList<>();
		for (ShopMallConfigCO shopMallTabsProp : GameData.ShopMallConfigs.values()) {
			if (shopMallTabsProp.isOpened == 1 || GWorld.DEBUG) {
				// 199测试商城只在debug模式下显示
				if (!GWorld.DEBUG && shopMallTabsProp.itemType == 199) {
					continue;
				}
				MallTab.Builder data = MallTab.newBuilder();
				data.setMoneyType(shopMallTabsProp.consumeType);
				data.setItemType(shopMallTabsProp.itemType);
				if (this.isValidOfMallLimitItemTab(shopMallTabsProp.itemType)) {
					data.setIsOpen(1);
				} else {
					data.setIsOpen(0);
				}
				data.setScriptNum(this.getSuperScriptNum(shopMallTabsProp.itemType));
				data.setName(shopMallTabsProp.labelName);

				if (shopMallTabsProp.itemType == Const.SHOP_MALL_ITEM_TYPE.DIAMOND_LIMIT.getValue() || shopMallTabsProp.itemType == Const.SHOP_MALL_ITEM_TYPE.TICKET_LIMIT.getValue() || shopMallTabsProp.itemType == Const.SHOP_MALL_ITEM_TYPE.ITEM_LIMIT.getValue()) {
					data.setIsLimit(1);
				} else {
					data.setIsLimit(0);
				}
				data.setLastNumText(shopMallTabsProp.remainNum);
				tabs.add(data);
			}
		}
		return tabs;
	}

	public final void refreshNewDay() {
		if (this.db.seenTab == null) {
			this.db.seenTab = new HashMap<>();
		}
		if (this.db.dayMallItemNums == null) {
			this.db.dayMallItemNums = new HashMap<>();
		}

		if (this.db.seenTab.containsKey(Const.SHOP_MALL_CONSUME_TYPE.DIAMOND.getValue())) {
			this.db.seenTab.get(Const.SHOP_MALL_CONSUME_TYPE.DIAMOND.getValue()).clear();
		} else {
			this.db.seenTab.put(Const.SHOP_MALL_CONSUME_TYPE.DIAMOND.getValue(), new HashMap<>());
		}
		if (this.db.seenTab.containsKey(Const.SHOP_MALL_CONSUME_TYPE.TICKET.getValue())) {
			this.db.seenTab.get(Const.SHOP_MALL_CONSUME_TYPE.TICKET.getValue()).clear();
		} else {
			this.db.seenTab.put(Const.SHOP_MALL_CONSUME_TYPE.TICKET.getValue(), new HashMap<>());
		}
		this.db.dayMallItemNums.clear();
		this.db.dayMallExchangeItemNums.clear();
	}

	/**
	 * 商城每周限购重置
	 */
	public void refreshNewWeek() {
		if (this.db.weekMallItemNums == null) {
			this.db.weekMallItemNums = new HashMap<>();
		}

		this.db.weekMallItemNums.clear();
		this.db.weekMallExchangeItemNums.clear();
		this.db.resetTime = new Date();
	}

	public void onLogin() {
		// 重置之前下线的玩家，登陆时候处理重置
		Date now = new Date();
		Date monday = ShopMallService.getInstance().getResetTime();
		if (now.getTime() > monday.getTime() && (this.db.resetTime == null || this.db.resetTime.getTime() < monday.getTime())) {
			refreshNewWeek();
		}
	}

	private final void seeTab(int itemType) {
		ShopMallConfigCO shopMallConfigCO = GameData.ShopMallConfigs.get(itemType);
		if (shopMallConfigCO == null) {
			return;
		}
		if (this.db.seenTab.containsKey(shopMallConfigCO.consumeType)) {
			Map<Integer, Boolean> node = this.db.seenTab.get(shopMallConfigCO.consumeType);
			node.put(itemType, true);
		} else {
			Map<Integer, Boolean> node = new HashMap<>();
			node.put(itemType, true);
			this.db.seenTab.put(shopMallConfigCO.consumeType, node);
		}
	}

	private final void addSelfMallItemNum(String id, int count) {
		int num = 0;
		if (this.db.dayMallItemNums.containsKey(id)) {
			num = this.db.dayMallItemNums.get(id);
		} else {
			this.db.dayMallItemNums.put(id, num);
		}
		num = num + count;
		this.db.dayMallItemNums.put(id, num);

		int weekNum = 0;
		if (db.weekMallItemNums == null) {
			db.weekMallItemNums = new HashMap<>();
		}
		if (this.db.weekMallItemNums.containsKey(id)) {
			weekNum = this.db.weekMallItemNums.get(id);
		} else {
			this.db.weekMallItemNums.put(id, weekNum);
		}
		weekNum += count;
		this.db.weekMallItemNums.put(id, weekNum);
	}

	private final void addSelfMallExchangeItemNum(String id, int count) {
		int num = 0;
		if (this.db.dayMallExchangeItemNums.containsKey(id)) {
			num = this.db.dayMallExchangeItemNums.get(id);
		} else {
			this.db.dayMallExchangeItemNums.put(id, num);
		}
		num = num + count;
		this.db.dayMallExchangeItemNums.put(id, num);

		int weekNum = 0;
		if (this.db.weekMallExchangeItemNums.containsKey(id)) {
			weekNum = this.db.weekMallExchangeItemNums.get(id);
		} else {
			this.db.weekMallExchangeItemNums.put(id, weekNum);
		}
		weekNum += count;
		this.db.weekMallExchangeItemNums.put(id, weekNum);
		
		int totalNum = 0;
		if (this.db.totalMallExchangeItemNums.containsKey(id)) {
			totalNum = this.db.totalMallExchangeItemNums.get(id);
		} else {
			this.db.totalMallExchangeItemNums.put(id, totalNum);
		}
		totalNum += count;
		this.db.totalMallExchangeItemNums.put(id, totalNum);
	}

	private final int getSelfMallItemRemainNum(String id) {
		int useNum = 0;
		if (this.db.dayMallItemNums.containsKey(id)) {
			useNum = this.db.dayMallItemNums.get(id);
		} else {
			this.db.dayMallItemNums.put(id, useNum);
		}

		ShopMallItemsExt mallItemProp = ShopMallConfig.getInstance().fingShowMallItemByID(id);

		int remainNum = -1;
		int dayTime = mallItemProp.buyTimes;
		int weekTime = mallItemProp.weekBuyTimes;
		// 检测每日限购
		if (dayTime > 0) {
			int vip = player.baseDataManager.getVip();
			int add = 0;
			if (vip > 0) {
				add = GameData.Cards.get(vip).prv7;
			}
			remainNum = mallItemProp.buyTimes + add - useNum;
			remainNum = remainNum >= 0 ? remainNum : 0;
		}
		// 检测每周限购
		if (weekTime > 0) {
			useNum = 0;
			if (this.db.weekMallItemNums.containsKey(id)) {
				useNum = this.db.weekMallItemNums.get(id);
			} else {
				this.db.weekMallItemNums.put(id, useNum);
			}
			remainNum = mallItemProp.weekBuyTimes - useNum;
			remainNum = remainNum > 0 ? remainNum : 0;
		}

		return remainNum;
	}

	private final int getSelfMallExchangeItemRemainNum(String id) {
		
		

		ExchangeMallExt exchangeMallExt = ShopMallConfig.getInstance().findExchangeMallItemByID(id);

		int remainNum = -1;

		int dayTime = exchangeMallExt.exchangeTimes;
		int weekTime = exchangeMallExt.weekExchangeTimes;
		int totalTime = exchangeMallExt.totalTimes;

		// 检测每日限购
		if (dayTime > 0) {
//			int vip = player.baseDataManager.getVip();
//			int add = 0;
//			if (vip > 0) {
//				add = GameData.Cards.get(vip).prv7;
//			}
			
			int dayUseNum = 0;
			if (this.db.dayMallExchangeItemNums.containsKey(id)) {
				dayUseNum = this.db.dayMallExchangeItemNums.get(id);
			} else {
				this.db.dayMallExchangeItemNums.put(id, dayUseNum);
			}
			
			remainNum = exchangeMallExt.exchangeTimes - dayUseNum;
			remainNum = remainNum >= 0 ? remainNum : 0;
		}
		// 检测每周限购
		if (weekTime > 0) {		
			int weekUseNum = 0;
			if (this.db.weekMallExchangeItemNums.containsKey(id)) {
				weekUseNum = this.db.weekMallExchangeItemNums.get(id);
			} else {
				this.db.weekMallExchangeItemNums.put(id, weekUseNum);
			}
			
			remainNum = exchangeMallExt.weekExchangeTimes - weekUseNum;
			remainNum = remainNum > 0 ? remainNum : 0;
		}
		// 检测总共限购
		if (totalTime > 0) {		
			int weekUseNum = 0;
			if (this.db.totalMallExchangeItemNums.containsKey(id)) {
				weekUseNum = this.db.totalMallExchangeItemNums.get(id);
			} else {
				this.db.totalMallExchangeItemNums.put(id, weekUseNum);
			}
			
			remainNum = exchangeMallExt.totalTimes - weekUseNum;
			remainNum = remainNum > 0 ? remainNum : 0;
		}

		return remainNum;
	}

	private final boolean isValidOfMallTab(int itemType) {
		boolean result = this.isOpenOfMallTab(itemType);
		if (!result) {
			return false;
		}
		if (itemType == Const.SHOP_MALL_ITEM_TYPE.DIAMOND_LIMIT.getValue() || itemType == Const.SHOP_MALL_ITEM_TYPE.TICKET_LIMIT.getValue() || itemType == Const.SHOP_MALL_ITEM_TYPE.ITEM_LIMIT.getValue()) {
			if (!this.isValidOfMallLimitItemTab(itemType)) {
				return false;
			}
		}
		return true;
	}

	public final boolean isOpenOfMallTab(int itemType) {
		ShopMallConfigCO shopMallConfigCO = GameData.ShopMallConfigs.get(itemType);
		if (shopMallConfigCO == null) {
			return false;
		}

		if (shopMallConfigCO.isOpened == 0 && !GWorld.DEBUG) {
			return false;
		}
		return true;
	}

	private final boolean isValidOfMallLimitItemTab(int itemType) {
		Date now = new Date();
		Calendar c = Calendar.getInstance();
		c.setTime(now);
		int nowHour = c.get(Calendar.HOUR_OF_DAY);
		ShopMallConfigCO shopMallConfigCO = GameData.ShopMallConfigs.get(itemType);
		if (shopMallConfigCO == null) {
			return false;
		}
		if (shopMallConfigCO.consumeType == Const.SHOP_MALL_CONSUME_TYPE.DIAMOND.getValue() && itemType == Const.SHOP_MALL_ITEM_TYPE.DIAMOND_LIMIT.getValue()) {
			int onSaleTime = GlobalConfig.Shop_OnSaleTime_Diamond;
			int shelfTime = GlobalConfig.Shop_ShelfTime_Diamond;
			if (nowHour < onSaleTime || nowHour >= shelfTime) {
				return false;
			}
		} else if (shopMallConfigCO.consumeType == Const.SHOP_MALL_CONSUME_TYPE.TICKET.getValue() && itemType == Const.SHOP_MALL_ITEM_TYPE.TICKET_LIMIT.getValue()) {
			int onSaleTime = GlobalConfig.Shop_OnSaleTime_Ticket;
			int shelfTime = GlobalConfig.Shop_ShelfTime_Ticket;
			if (nowHour < onSaleTime || nowHour >= shelfTime) {
				return false;
			}
		} else if (shopMallConfigCO.consumeType == Const.SHOP_MALL_CONSUME_TYPE.ITEMCHANGE.getValue() && itemType == Const.SHOP_MALL_ITEM_TYPE.ITEM_LIMIT.getValue()) {
			int onSaleTime = GlobalConfig.Shop_OnSaleTime_Exchange;
			int shelfTime = GlobalConfig.Shop_ShelfTime_Exchange;
			if (nowHour < onSaleTime || nowHour >= shelfTime) {
				return false;
			}
		}
		return true;
	}

	public final List<SuperScriptType> getSuperScript() {
		List<SuperScriptType> list = new ArrayList<>();
		SuperScriptType.Builder script = SuperScriptType.newBuilder();
		script.setType(Const.SUPERSCRIPT_TYPE.SHOPMALL.getValue());
		script.setNumber(0);

		// 功能开启
		if (this.player.functionOpenManager.isOpen(Const.FunctionType.MALL.getValue())) {
			script.setNumber(this.getSuperScriptAllNum());
		}
		list.add(script.build());
		return list;
	}

	private final int getSuperScriptAllNum() {
		int result = 0;
		for (ShopMallConfigCO shopMallTabsProp : GameData.ShopMallConfigs.values()) {
			int moneyType = shopMallTabsProp.consumeType;
			int itemType = shopMallTabsProp.itemType;
			if (shopMallTabsProp.isOpened == 1) {
				int num = this.getSuperScriptNum(itemType);
				if (num > 0) {
					result = num;
					break;
				}
			}
		}
		return result;
	}

	private final int getSuperScriptNum(int itemType) {
		int result = 0;
		if (itemType != Const.SHOP_MALL_ITEM_TYPE.DIAMOND_LIMIT.getValue() && itemType != Const.SHOP_MALL_ITEM_TYPE.TICKET_LIMIT.getValue()) {
			// List<ShopMallItemsExt> mallItems =
			// ShopMallConfig.getInstance().findShopMallPropsByConsumeTypeAndItemType(itemType);
			List<ShopMallItemsExt> mallItems = GameData.findShopMallItemss(t -> t.itemType == itemType);
			for (ShopMallItemsExt mallItem : mallItems) {
				if (mallItem.serveLimit == Const.SHOP_MALL_SERVER_LIMIT.SELF.getValue()) {
					// 限购商品红点取消
					// if(!this.isSeenTab(moneyType, itemType)){
					// if(mallItem.Series === -2){
					// result = 1;
					// break;
					// }
					// }
				} else {
					// todo..
				}
			}
		} else {
			// 限时商品红点取消
			// if(this.isValidOfMallTab(moneyType, itemType)){
			// if(!this.isSeenTab(moneyType, itemType)) {
			// result = 1;
			// }
			// }
		}
		return result;
	}

}
