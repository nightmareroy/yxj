package com.wanniu.game.equip;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.wanniu.core.common.IntIntPair;
import com.wanniu.game.bag.WNBag.SimpleItemInfo;
import com.wanniu.game.data.GameData;
import com.wanniu.game.data.MeltConfigCO;
import com.wanniu.game.data.ext.CombineExt;
import com.wanniu.game.data.ext.EnchantExt;
import com.wanniu.game.data.ext.EquipMakeExt;
import com.wanniu.game.data.ext.SuitConfigExt;
import com.wanniu.game.data.ext.SuitListExt;
import com.wanniu.game.item.ItemUtil;
import com.wanniu.game.player.WNPlayer;

import pomelo.item.ItemOuterClass.Combine;
import pomelo.item.ItemOuterClass.MiniItem;

/**
 * 装备加工处理
 * 
 * @author Yangzz
 *
 */
public class EquipCraftConfig {

	private static EquipCraftConfig instance;

	public static EquipCraftConfig getInstance() {
		if (instance == null) {
			instance = new EquipCraftConfig();
		}
		return instance;
	}

	// private List<SuiteLevelCO> list_suiteLevels;
	/** 获取精炼属性所有分组 */
	private List<Integer> refineMagicGroupIds = null;

	private EquipCraftConfig() {
		// list_suiteLevels = new ArrayList<>(GameData.SuiteLevels.values());
		// list_refineSuccess = new ArrayList<>(GameData.RefineSuccesss.values());
		refineMagicGroupIds = new ArrayList<>();
		/** 精炼属性所有分组 */
		// for (RefineMagicCO magic : GameData.RefineMagics.values()) {
		// int groupId = magic.groupID;
		// if (refineMagicGroupIds.indexOf(groupId) != -1) {
		// continue;
		// }
		// refineMagicGroupIds.add(groupId);
		// }
	}

	/****************************************
	 * 合成相关配置
	 *******************************************/
	/**
	 * 获取合成所需的配置
	 */
	public CombineExt getCombineProp(int destId) {
		return GameData.Combines.get(destId);
	}

	/**
	 * 道具合成公式
	 * 
	 * @param code
	 * @returns {*}
	 */
	public Combine getCombineFormula(CombineExt prop) {
		Combine.Builder data = Combine.newBuilder();
		data.setProduct(ItemUtil.getMiniItemData(prop.destCode, 1, null));

		List<MiniItem> materials = new ArrayList<>();
		for (SimpleItemInfo componet : prop.material) {

			MiniItem tmp = ItemUtil.getMiniItemData(componet.itemCode, componet.itemNum, null).build();
			if (tmp != null) {
				materials.add(tmp);
			}
		}
		data.addAllMaterials(materials);
		data.setGold(prop.costGold);

		return data.build();
	};

	// public int getCombineNum (bag, prop){
	// var arr = [];
	// prop.material.forEach(function(v){
	// var num = Math.floor(bag.findItemNumByCode(v.itemCode)/ v.itemNum);
	// arr.push(num);
	// });
	// var min = Math.min.apply(null, arr);
	// return min;
	// };
	/********************************
	 * 合成相关配置结束
	 ***************************************************/

	/**
	 * 获取等级对应的强化配置 段位、等级
	 */
	public EnchantExt getEnchantConfig(int enClass, int enLevel) {
		List<EnchantExt> list = GameData.findEnchants(t -> {
			return t.enClass == enClass && t.enLevel == enLevel;
		});
		return list.size() > 0 ? list.get(0) : null;
	}

	/**
	 * 获取最高强化等级
	 */
	public int getMaxStrengthLevel() {
		return GameData.Enchants.values().size();
	}

	// /**
	// * 根据成色 获取附魔配置
	// */
	// public List<MagicalCO> getMagicalPropsByColor(int color) {
	// List<MagicalCO> list = new ArrayList<>();
	// Collection<MagicalCO> collection = GameData.Magicals.values();
	// for (MagicalCO cfg : collection) {
	// if (cfg.qcolor == color) {
	// list.add(cfg);
	// }
	// }
	// return list;
	// }

	/**
	 * 根据ID获取附魔配置
	 * 
	 * @param id
	 * @return
	 */
	// public MagicalCO getMagicalPropByID(int id) {
	// return GameData.Magicals.get(id);
	// }
	//
	// /**
	// * 根据成色获取精炼配置
	// */
	// public RefineCO getRefinePropByColor(int color) {
	// return GameData.Refines.get(color);
	// }

	// /**
	// * 根据属性ID获取精炼魔法属性配置
	// */
	// public RefineMagicCO getRefineMagicPropById(int propID) {
	// return GameData.RefineMagics.get(propID);
	// }
	//
	// /**
	// * 根据属性分组获取精炼魔法属性配置
	// */
	// public List<RefineMagicCO> getRefineMagicPropListByGroup(int groupID) {
	// List<RefineMagicCO> list = new ArrayList<>();
	// Collection<RefineMagicCO> collection = GameData.RefineMagics.values();
	// for (RefineMagicCO config : collection) {
	// if (config.groupID == groupID) {
	// list.add(config);
	// }
	// }
	// return list;
	// }

	/**
	 * 获取精炼属性所有分组
	 */
	public List<Integer> getRefineMagicGroupIds() {
		return refineMagicGroupIds;
	}

	/**
	 * 获取装备制作配置
	 */
	public EquipMakeExt getEquipMakePropByCode(String targetCode) {
		List<EquipMakeExt> list = GameData.findEquipMakes(t -> {
			return t.targetCode.equals(targetCode);
		});
		if (list.size() > 0) {
			return list.get(0);
		}
		return null;
	}

	// /**
	// * 获取装备升级配置
	// */
	// public EquipLevelUpCO getEquipLevelUpPropByCode(String code) {
	// if (StringUtil.isEmpty(code)) {
	// return null;
	// }
	// for (EquipLevelUpCO el : GameData.EquipLevelUps.values()) {
	// if (code.equals(el.code)) {
	// return el;
	// }
	// }
	// return null;
	// }
	//
	// /**
	// * 获取套装等级 进阶配置
	// */
	// public List<SuiteLevelCO> getSuitLevelProps() {
	// return list_suiteLevels;
	// }

	/**
	 * 获取熔炼配置
	 */
	public MeltConfigCO getMeltProp(int meltLevel, int quality) {
		Collection<MeltConfigCO> col = GameData.MeltConfigs.values();
		for (MeltConfigCO cfg : col) {
			if (cfg.meltLevel == meltLevel && cfg.equipQColor == quality) {
				return cfg;
			}
		}
		return null;
	}

	/**
	 * 返回 套装ID_件数 的集合
	 * 
	 * @param player
	 * @return List<suitID, counts>
	 */
	public List<IntIntPair> getValidSuits(WNPlayer player) {
		List<IntIntPair> results = new ArrayList<>();
		for (SuitListExt config : GameData.SuitLists.values()) {
			if (config.isValid == 0) {
				continue;
			}
			if (player.player.level < config.level) {
				continue;
			}
			List<String> suitCodes = config.getContaintsCode(player.equipManager.equips);
			if (suitCodes.size() == 0) {
				continue;
			}
			results.add(new IntIntPair(config.suitID, suitCodes.size()));
		}
		return results;
	}

	/**
	 * 根据 获取套装属性
	 * 
	 * @param configs
	 * @return
	 */
	public Map<String, Integer> getSuitAtts(List<IntIntPair> configs) {
		Map<String, Integer> results = new HashMap<>();
		for (SuitConfigExt config : GameData.SuitConfigs.values()) {
			for (IntIntPair idCount : configs) {
				if (config.suitID == idCount.first && config.partReqCount <= idCount.second) {
					if(results.containsKey(config._prop)) {
						results.put(config._prop, results.get(config._prop)+config.min);
					}else {
						results.put(config._prop, config.min);
					}
				}
			}
		}
		return results;
	}
}
