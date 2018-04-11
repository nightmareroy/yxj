package com.wanniu.game.shopMall;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.wanniu.core.logfs.Out;
import com.wanniu.game.data.ExchangeMallCO;
import com.wanniu.game.data.GameData;
import com.wanniu.game.data.ShopMallConfigCO;
import com.wanniu.game.data.ext.ExchangeMallExt;
import com.wanniu.game.data.ext.ShopMallItemsExt;

public class ShopMallConfig {

	private static ShopMallConfig instance;
	private static List<ShopMallItemsExt> shopMallItems = new ArrayList<>();

	public static ShopMallConfig getInstance() {
		if (instance == null) {
			instance = new ShopMallConfig();

			for (ShopMallItemsExt item : GameData.ShopMallItemss.values()) {
				shopMallItems.add(item);
			}
			Collections.sort(shopMallItems, new Comparator<ShopMallItemsExt>() {
				@Override
				public int compare(ShopMallItemsExt o1, ShopMallItemsExt o2) {
//					return Integer.parseInt(o1.iD) < Integer.parseInt(o2.iD) ? -1 : 1;
					return o1.sort-o2.sort;
				}

			});

		}
		return instance;
	}

//	public final ArrayList<ShopMallConfigCO> findShowMallConfigByItemType(int type) {
//		ArrayList<ShopMallConfigCO> list = new ArrayList<>();
//		for (ShopMallConfigCO config : GameData.ShopMallConfigs.values()) {
//			if (config.itemType == type) {
//				list.add(config);
//			}
//		}
//		return list;
//	}

//	public final List<ShopMallItemsExt> findShopMallPropsByConsumeTypeAndItemType(int itemType) {
//		List<ShopMallItemsExt> list = GameData.findShopMallItemss(t->t.itemType==itemType);
//		
//		return list;
//	}

	public final ShopMallItemsExt fingShowMallItemByID(String id) {
		for (ShopMallItemsExt item : shopMallItems) {
			if (item.iD.equals(id)) {
				return item;
			}
		}
//		Out.error("can`t find ShowMallItem id = ", id);
		return null;
	}
	
	public final ExchangeMallExt findExchangeMallItemByID(String id) {
		for (ExchangeMallExt exchangeMallExt : GameData.ExchangeMalls.values()) {
			if (exchangeMallExt.iD.equals(id)) {
				return exchangeMallExt;
			}
		}
//		Out.error("can`t find ExchangeMallItem id = ", id);
		return null;
	}

}
