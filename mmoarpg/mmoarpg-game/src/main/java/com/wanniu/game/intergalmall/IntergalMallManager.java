package com.wanniu.game.intergalmall;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.wanniu.core.logfs.Out;
import com.wanniu.game.common.Const;
import com.wanniu.game.common.ConstsTR;
import com.wanniu.game.data.GameData;
import com.wanniu.game.data.ShopLabCO;
import com.wanniu.game.data.base.IntergalShopBase;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.poes.IntergalMallPO;
import com.wanniu.redis.GameDao;

import pomelo.area.IntergalMallHandler.IntergalMallItem;
import pomelo.area.IntergalMallHandler.IntergalMallTab;

/**
 * 积分商城
 * 
 * @author Yangzz
 *
 */
public class IntergalMallManager {

	public WNPlayer player;

	public IntergalMallPO intergalMallPO;

	public IntergalMallManager(WNPlayer player) {
		this.player = player;

		intergalMallPO = GameDao.get(ConstsTR.intergalMallTR.value, player.getId(), IntergalMallPO.class);
		if (intergalMallPO == null) {
			intergalMallPO = new IntergalMallPO();
			intergalMallPO.hasBuyItem = new HashMap<>();
		}
	}

	/**
	 * 
	 * @param shopType 所有分类
	 */
	public List<IntergalMallTab> getIntergalMallItemList(int shopType) {
		List<IntergalMallTab> list = new ArrayList<>();

		for (ShopLabCO tabCO : GameData.ShopLabs.values()) {
			if (shopType != -1 && shopType != tabCO.id)
				continue;

			IntergalMallTab.Builder mallTab = IntergalMallTab.newBuilder();
			mallTab.setTabId(tabCO.id);
			if (tabCO.id == Const.IntergalMallType.MallShop) {
				mallTab.setCurrencyNum(this.player.moneyManager.getConsumePoint());
			} else if (tabCO.id == Const.IntergalMallType.FateShop) {
				if (this.player.allBlobData.xianYuan != null) {
					mallTab.setCurrencyNum(this.player.moneyManager.getXianYuan());
				}
			} else if (tabCO.id == Const.IntergalMallType.AthleticShop) {
				mallTab.setCurrencyNum(this.player.soloManager.getSolopoint());
			} else if (tabCO.id == Const.IntergalMallType.GuildShop) {
				mallTab.setCurrencyNum(this.player.guildManager.getContribution());
			} else if (tabCO.id == Const.IntergalMallType.SundryShop) {
				mallTab.setCurrencyNum(this.player.moneyManager.getGold());
			}

			Map<Integer, IntergalShopBase> items = IntergalMallConfig.getInstance().shopItems.get(tabCO.id);
			for (IntergalShopBase prop : items.values()) {
				// 无效
				if (prop.isShow == 0)
					continue;
				// 未开始
				if (prop.periodStartDate != null && prop.periodStartDate.getTime() > System.currentTimeMillis()) {
					continue;
				}
				// 已结束
				if (prop.periodEndDate != null && prop.periodEndDate.getTime() < System.currentTimeMillis()) {
					continue;
				}

				IntergalMallItem.Builder item = IntergalMallItem.newBuilder();
				item.setId(prop.iD);
				item.setCode(prop.itemCode);
				// 剩余次数
				if (prop.buyTimes == -1) {
					item.setLastcount(-1);
				} else {
					// 个人剩余限购次数
					// 已经购买的次数
					Map<Integer, Integer> shopHasBuyMap = intergalMallPO.hasBuyItem.get(tabCO.id);
					if (shopHasBuyMap == null) {
						shopHasBuyMap = new HashMap<>();
						intergalMallPO.hasBuyItem.put(tabCO.id, shopHasBuyMap);
					}
					int hasBuyNum = shopHasBuyMap.containsKey(prop.iD) ? shopHasBuyMap.get(prop.iD) : 0;
					if (hasBuyNum > 0) {
						Out.debug(prop.iD, "---------------------------------------");
					}
					
					int vip = player.baseDataManager.getVip();
					int add = 0;
					if (vip > 0) {
						add = GameData.Cards.get(vip).prv7;
					}
					
					int lastBuyNum = prop.buyTimes - hasBuyNum + add;

					// // 全球限购次数
					// int hasBuyNumGlobal = 0;
					// int lastBuyNumGlobal = 0;
					// if (prop.serveBuyTimes > 0) {
					// hasBuyNumGlobal =
					// IntergalMallGlobalService.getInstance().getGlobalNum(shopType, prop.iD);
					// if (hasBuyNumGlobal > 0) {
					// lastBuyNumGlobal = prop.serveBuyTimes - hasBuyNumGlobal;
					// }
					// // 取小的那个 剩余次数
					// if (lastBuyNum < lastBuyNumGlobal) {
					// item.setLastcount(lastBuyNum);
					// } else {
					// item.setLastcount(lastBuyNumGlobal);
					// }
					// } else {
					// item.setLastcount(lastBuyNum);
					// }
					item.setLastcount(lastBuyNum);
				}
				// 倒计时
				if (prop.countDown == 1) {
					item.setCountdown(prop.periodEndDate.getTime());
				}
				mallTab.addItems(item);
			}
			list.add(mallTab.build());
		}

		return list;
	}

	/**
	 * 每日0点重置购买次数
	 */
	public void refreshNewDay() {
		this.intergalMallPO.hasBuyItem.clear();
	}

}
