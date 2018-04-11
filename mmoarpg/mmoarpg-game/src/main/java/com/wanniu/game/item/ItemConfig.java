package com.wanniu.game.item;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.wanniu.core.logfs.Out;
import com.wanniu.core.util.StringUtil;
import com.wanniu.game.data.BeforeFilterCO;
import com.wanniu.game.data.GameData;
import com.wanniu.game.data.MiscCO;
import com.wanniu.game.data.PlantingCO;
import com.wanniu.game.data.base.DEquipBase;
import com.wanniu.game.data.base.DItemBase;
import com.wanniu.game.data.base.DItemEquipBase;
import com.wanniu.game.data.ext.EquipSockExt;
import com.wanniu.game.data.ext.ItemIdConfigExt;
import com.wanniu.game.data.ext.ItemTypeConfigExt;
import com.wanniu.game.data.ext.ReBuildExt;
import com.wanniu.game.data.ext.TreasureClassExt;

public class ItemConfig {
	/** Key为模板ID的道具集合 */
	private Map<String, DItemBase> itemTemplates = new HashMap<>();

	/** 装备集合 */
	private Map<String, DEquipBase> equipTemplates = new HashMap<>();

	/** 宝石对应的可镶嵌部位 */
	private Map<String, List<Integer>> gemPos = new HashMap<>();

	/** sourceTC+level, BeforeFilterCO */
	public Map<String, BeforeFilterCO> tcs = null;

	// 重铸材料
	public Map<Integer, Map<Integer, ReBuildExt>> rebuildMap = null;

	// 果园
	// 种子物品的基础属性的map
	public static Map<String, MiscCO> seedMiscMap = null;
	// 果实物品的基础属性的map
	public static Map<String, MiscCO> productMiscMap = null;
	// 种子以及果实的特殊属性的map
	public static Map<String, PlantingCO> plantingMap = null;

	public static ItemConfig getInstance() {
		return Holder.instance;
	}

	private ItemConfig() {}

	private static final class Holder {
		public static final ItemConfig instance = new ItemConfig();
	}

	/**
	 * 加载策划脚本
	 */
	public void loadScript() {
		// 加载物品数据
		itemTemplates.putAll(GameData.Jewels);
		itemTemplates.putAll(GameData.RideItems);
		itemTemplates.putAll(GameData.PetItems);
		itemTemplates.putAll(GameData.Chests);
		itemTemplates.putAll(GameData.Potions);
		itemTemplates.putAll(GameData.Mates);
		itemTemplates.putAll(GameData.Miscs);
		itemTemplates.putAll(GameData.Virtuals);
		itemTemplates.putAll(GameData.Ranks);
		itemTemplates.putAll(GameData.Quests);
		itemTemplates.putAll(GameData.Actives);
		itemTemplates.putAll(GameData.FashionItems);

		// 加载装备数据
		equipTemplates.putAll(GameData.NormalEquips);
		equipTemplates.putAll(GameData.BlueEquips);
		equipTemplates.putAll(GameData.PurpleEquips);
		equipTemplates.putAll(GameData.LegendEquips);
		equipTemplates.putAll(GameData.SuitEquips);
		equipTemplates.putAll(GameData.RideEquips);
		equipTemplates.putAll(GameData.UniqueEquips);

		// 加载宝石对应的可镶嵌部位
		for (EquipSockExt sock : GameData.EquipSocks.values()) {
			for (String gem : sock.typeList) {
				List<Integer> poses = gemPos.get(gem);
				if (poses == null) {
					poses = new ArrayList<>();
					gemPos.put(gem, poses);
				}

				if (!poses.contains(sock.typeID)) {
					poses.add(sock.typeID);
				}
			}
		}

		// tc前置过滤
		tcs = new HashMap<>();
		for (BeforeFilterCO filter : GameData.BeforeFilters.values()) {
			if (filter.dynamicLv.indexOf("~") != -1) {
				String[] lvZones = filter.dynamicLv.split(",");
				for (String lvZone : lvZones) {
					String[] lvs = lvZone.split("~");
					if (lvs.length == 2) {// 区间的每个等级都创建一个key
						int min = Integer.parseInt(lvs[0]);
						int max = Integer.parseInt(lvs[1]);
						for (int i = min; i <= max; i++) {
							tcs.put(filter.tcCode + i, filter);
						}
					} else {
						Out.error("BeforeFilterCO.dynamicLv 格式错误：~号分隔必须有2个level数字");
					}

				}
			} else {
				tcs.put(filter.tcCode + filter.dynamicLv, filter);
			}
		}

		// 重铸前置过滤
		rebuildMap = new HashMap<>();
		for (ReBuildExt reBuildExt : GameData.ReBuilds.values()) {
			if (!rebuildMap.containsKey(reBuildExt.level))
				rebuildMap.put(reBuildExt.level, new HashMap<>());
			if (!rebuildMap.get(reBuildExt.level).containsKey(reBuildExt.lockNum))
				rebuildMap.get(reBuildExt.level).put(reBuildExt.lockNum, reBuildExt);
		}

		// 果园相关前置过滤
		seedMiscMap = new HashMap<>();
		for (MiscCO miscCO : GameData.Miscs.values()) {
			if (miscCO.code.length() == 6) {
				String strs = miscCO.code.substring(0, 4);
				if (strs.equals("seed"))
					seedMiscMap.put(miscCO.code, miscCO);
			}
		}
		productMiscMap = new HashMap<>();
		for (MiscCO miscCO : GameData.Miscs.values()) {
			if (miscCO.code.length() == 9) {
				String strs = miscCO.code.substring(0, 7);
				if (strs.equals("product"))
					productMiscMap.put(miscCO.code, miscCO);
			}
		}
		plantingMap = new HashMap<>();
		for (PlantingCO plantingCO : GameData.Plantings.values()) {
			plantingMap.put(plantingCO.code, plantingCO);
		}
	}

	public Map<String, DItemBase> getItemTemplates() {
		return itemTemplates;
	}

	public Map<String, DEquipBase> getEquipTemplates() {
		return equipTemplates;
	}

	public DEquipBase getEquipProp(String templateCode) {
		DEquipBase result = this.equipTemplates.get(templateCode);
		if (result == null) {
			Out.error(String.format("Item equipTemplate [%s] is not found.", templateCode));
		}
		return result;
	}

	public DItemEquipBase getItemPropByName(String name) {
		for (DItemEquipBase result : this.itemTemplates.values()) {
			if (result.name.equals(name)) {
				return result;
			}
		}
		for (DItemEquipBase result : this.equipTemplates.values()) {
			if (result.name.equals(name)) {
				return result;
			}
		}
		return null;
	}

	/**
	 * 根据模版编号获取道具脚本对象数据
	 *
	 * @param templateCode
	 * @return
	 */
	public DItemEquipBase getItemProp(String templateCode) {
		DItemEquipBase result = this.itemTemplates.get(templateCode);
		if (result == null) {
			result = this.equipTemplates.get(templateCode);
		}
		if (result == null) {
			Out.error(this.getClass(), String.format("Item template [%s] is not found.", templateCode));
		}
		return result;
	}

	/**
	 * 获取物品模板
	 */
	public List<DEquipBase> getEquipProps(int qColor, int tcLevel, int itemSecondType) {
		List<DEquipBase> list = new ArrayList<>();
		Collection<DEquipBase> collection = this.equipTemplates.values();
		for (DEquipBase template : collection) {
			if (template.qcolor == qColor && template.tcLevel == tcLevel && getSecondType(template.type) == itemSecondType && template.isValid != 0) {
				list.add(template);
			}
		}
		return list;
	}

	/**
	 * 根据类型获取 类型/ID配置
	 */
	public ItemIdConfigExt getIdConfig(String itemType) {
		return GameData.ItemIdConfigs.get(itemType);
	}

	/**
	 * 根据子类型获取 一级类型配置
	 */
	public ItemTypeConfigExt getTypeConfig(String subType) {
		for (ItemTypeConfigExt cfg : GameData.ItemTypeConfigs.values()) {
			for (String component : cfg.subTypes) {
				if (!StringUtil.isEmpty(component) && component.equalsIgnoreCase(subType)) {
					return cfg;
				}
			}
		}
		return null;
	}

	/**
	 * 获取装备一级类型
	 */
	public int getFirstType(String type) {
		return getTypeConfig(type).iD;
	}

	/**
	 * 获取装备的二级类型
	 */
	public int getSecondType(String type) {
		return getIdConfig(type).typeID;
	}

	/**
	 * 获取TC配置
	 */
	public TreasureClassExt getTcProp(String tcCode) {
		return GameData.TreasureClasss.get(tcCode);
	}

	/**
	 * 根据Prop获取物品模板
	 */
	public DItemBase findUnEquipPropsByProp(String prop) {
		Collection<DItemBase> items = itemTemplates.values();
		for (DItemBase item : items) {
			if (item.prop.equals(prop)) {
				return item;
			}
		}
		return null;
	}

	/**
	 * 根据type获取物品模板
	 */
	public List<DItemBase> findUnEquipPropsByType(String type) {
		Collection<DItemBase> items = itemTemplates.values();
		List<DItemBase> ret = new ArrayList<>();
		for (DItemBase item : items) {
			if (item.type.equals(type)) {
				ret.add(item);
			}
		}
		return ret;
	}

	/**
	 * 根据宝石获取宝石对应的孔
	 */
	public List<Integer> findPosByGem(String gem) {
		return gemPos.get(gem);
	}

	/**
	 * 根据等级获取真正执行的tc 皓月镜的个人、镇妖塔的结算掉落、资源副本的四种掉落 在执行掉落时，使用个人等级
	 */
	public String getRealTC(String tc, int level) {
		if (level <= 0) {
			return tc;
		}
		BeforeFilterCO lvTC = tcs.get(tc + level);
		if (lvTC == null) {
			return tc;
		}

		return lvTC.tcForLv;
	}

	// /**
	// * 根据职业和部位获取时装配置
	// */
	// public FashionExt findFashionByProType (int pro, int type) {
	// for (FashionExt fashion : GameData.Fashions.values()) {
	// if ((fashion.Pro == 0 || fashion.Pro == pro) && fashion.Type == type) {
	// return fashion;
	// }
	// }
	// return null;
	// }
}
