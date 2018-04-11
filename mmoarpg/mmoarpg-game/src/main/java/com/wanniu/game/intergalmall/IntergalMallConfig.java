package com.wanniu.game.intergalmall;

import java.util.HashMap;
import java.util.Map;

import com.wanniu.game.data.GameData;
import com.wanniu.game.data.base.IntergalShopBase;

/**
 * 积分商城
 * 
 * @author Yangzz
 *
 */
public class IntergalMallConfig {

	public Map<Integer, Map<Integer, IntergalShopBase>> shopItems = new HashMap<>();

	private static IntergalMallConfig intergalMallConfig = null;

	private IntergalMallConfig() {
		shopItems.put(1, new HashMap<>());
		shopItems.put(2, new HashMap<>());
		shopItems.put(3, new HashMap<>());
		shopItems.put(4, new HashMap<>());
		shopItems.put(5, new HashMap<>());
		shopItems.get(1).putAll(GameData.SundryShops);
		shopItems.get(2).putAll(GameData.MallShops);
		shopItems.get(3).putAll(GameData.FateShops);
		shopItems.get(4).putAll(GameData.AthleticShops);
		shopItems.get(5).putAll(GameData.GuildShops);
	}

	public static IntergalMallConfig getInstance() {
		if (intergalMallConfig == null) {
			intergalMallConfig = new IntergalMallConfig();
		}
		return intergalMallConfig;
	}

	public IntergalShopBase getIntergalMallProp(int shopType, int itemId) {
		Map<Integer, IntergalShopBase> items = shopItems.get(shopType);
		for (IntergalShopBase prop : items.values()) {
			if (prop.iD == itemId) {
				return prop;
			}
		}
		return null;
	}
}
