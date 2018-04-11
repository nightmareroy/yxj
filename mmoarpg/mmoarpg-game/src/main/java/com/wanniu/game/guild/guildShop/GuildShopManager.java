package com.wanniu.game.guild.guildShop;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.wanniu.game.common.Const;
import com.wanniu.game.common.Utils;
import com.wanniu.game.data.GShopCO;
import com.wanniu.game.data.ext.GShopExt;
import com.wanniu.game.guild.GuildResult;
import com.wanniu.game.guild.GuildService;
import com.wanniu.game.guild.GuildUtil;
import com.wanniu.game.item.ItemUtil;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.GuildShopHandler.ExchangeItem;
import pomelo.area.GuildShopHandler.ShopCondition;
import pomelo.area.GuildShopHandler.ShopInfo;
import pomelo.area.GuildShopHandler.ShopMoneyInfo;
import pomelo.item.ItemOuterClass.MiniItem;

public class GuildShopManager {
	public WNPlayer player;
	public ArrayList<Integer> boughtList;
	public ArrayList<Integer> goods;

	public GuildShopManager(WNPlayer player) {
		this.player = player;
		boughtList = new ArrayList<Integer>();
		goods = new ArrayList<Integer>();
	}

	public JSONObject toJson4Serialize() {
		JSONObject data = new JSONObject();
		data.put("boughtList ", this.boughtList);
		return data;
	}

	public void refreshNewDay(boolean isNewDay) {
		if (isNewDay && null != this.boughtList) {
			this.boughtList.clear();
		}
	}

	public void refreshGuildTodayGoods() {
		GuildResult ret = GuildService.getGuildTodayGoodsList(this.player);
		if (null != ret && null != this.goods) {
			this.goods = ret.goods;
		}
	}

	public void resetPublicData() {
		if (null != this.goods) {
			this.goods.clear();
		}
	}

	public ShopInfo toJson4PayLoad() {
		ShopInfo.Builder shopInfo = ShopInfo.newBuilder();
		if (null == this.goods || this.goods.size() == 0) {
			return shopInfo.build();
		}

		List<GShopExt> itemPropList = GuildUtil.getShopPropList();
		List<ExchangeItem> todayList = new ArrayList<ExchangeItem>();
		List<ExchangeItem> nextList = new ArrayList<ExchangeItem>();
		for (int i = 0; i < itemPropList.size(); ++i) {
			GShopExt prop = itemPropList.get(i);
			int goodId = prop.itemID;
			MiniItem.Builder miniData = ItemUtil.getMiniItemData(prop.itemCode, prop.itemCount, Const.ForceType.getE(prop.isBind));
			if (null == miniData) {
				continue;
			}

			ExchangeItem.Builder good = ExchangeItem.newBuilder();
			good.setId(goodId);
			good.setItemShowName(prop.itemShowName);
			good.setItem(miniData.build());
			good.setIsBind(miniData.getBindType());
			good.setItemDes(prop.itemDes);
			// 价钱
			good.addAllNeedMoney(prop.moneyReqList);
			// 兑换条件
			good.addAllCondition(this.getConditions(prop));
			good.setMeetCondition((this.getConditionStatus(prop) == 0) ? 1 : 0);
			// 状态
			good.setState(Const.EVENT_GIFT_STATE.NOT_RECEIVE.getValue()); // 0
			if (this.goods.indexOf(goodId) != -1) {
				good.setState(Const.EVENT_GIFT_STATE.CAN_RECEIVE.getValue()); // 1
				if (this.boughtList.indexOf(goodId) != -1) {
					good.setState(Const.EVENT_GIFT_STATE.RECEIVED.getValue()); // 2
				}
				todayList.add(good.build());
			} else {
				nextList.add(good.build());
			}
		}

		// 筛选未刷新商品
		nextList.sort((a, b) -> {
			if (a.getItem().getQColor() != b.getItem().getQColor()) {
				return b.getItem().getQColor() - a.getItem().getQColor();
			} else if (a.getId() != b.getId()) {
				return a.getId() - b.getId();
			} else {
				return 0;
			}
		});

		nextList.subList(11, nextList.size() - 1); // 更多物品之先生 3 + 8件

		// 时间
		long miniSeconds = Const.Time.Day.getValue() + Utils.getZeroDate().getTime() - System.currentTimeMillis(); // 毫秒
		shopInfo.addAllTodayItems(todayList);
		shopInfo.addAllNextItems(nextList);
		shopInfo.setRefreshTime(0, (int) Math.ceil(miniSeconds / 1000)); // 秒
		return shopInfo.build();
	}

	public ShopCondition newCondition(int type, int num) {
		ShopCondition.Builder data = ShopCondition.newBuilder();
		data.setType(type);
		data.setNumber(num);
		return data.build();
	}

	public List<ShopCondition> getConditions(GShopCO prop) {
		List<ShopCondition> data = new ArrayList<ShopCondition>();
		if (prop.levelReq > 0) {
			data.add(newCondition(1, prop.levelReq));
		}

		if (prop.upReq > 0) {
			data.add(newCondition(2, prop.upReq));
		}

		if (prop.vipReq > 0) {
			data.add(newCondition(3, prop.vipReq));
		}

		if (prop.raceReq > 0) {
			data.add(newCondition(4, prop.raceReq));
		}
		if (prop.raceClass > 0) {
			data.add(newCondition(5, prop.raceClass));
		}

		return data;
	}

	public int getConditionStatus(GShopCO prop) {
		List<ShopCondition> conditions = this.getConditions(prop);
		for (int i = 0; i < conditions.size(); ++i) {
			ShopCondition condition = conditions.get(i);
			int type = condition.getType();
			int number = condition.getNumber();
			if (type == 1 && this.player.getPlayer().level < number) {
				return 1;
			}
			if (type == 2 && this.player.getPlayer().upLevel < number) {
				return 2;
			}
			// if (type == 3 && this.player.baseDataManager.getVip() < number) {
			// return 3;
			// }
			if (type == 4) {

			}
			if (type == 5) {

			}
			if (type == 6) {

			}
			if (type == 7) {

			}
		}
		return 0;
	}
}