package com.wanniu.game.equip;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.wanniu.core.logfs.Out;
import com.wanniu.core.util.StringUtil;
import com.wanniu.game.bag.WNBag.SimpleItemInfo;
import com.wanniu.game.common.Const;
import com.wanniu.game.common.Utils;
import com.wanniu.game.data.CharacterCO;
import com.wanniu.game.data.EnchantCO;
import com.wanniu.game.data.GameData;
import com.wanniu.game.data.base.DEquipBase;
import com.wanniu.game.data.base.DItemBase;
import com.wanniu.game.data.base.DItemEquipBase;
import com.wanniu.game.data.ext.EnchantExt;
import com.wanniu.game.data.ext.EquipMakeExt;
import com.wanniu.game.data.ext.ReBornExt;
import com.wanniu.game.data.ext.ReBuildExt;
import com.wanniu.game.data.ext.RefineExt;
import com.wanniu.game.data.ext.SeniorReBuildExt;
import com.wanniu.game.item.ItemConfig;
import com.wanniu.game.item.ItemUtil;
import com.wanniu.game.item.data.AttsObj;
import com.wanniu.game.item.po.PlayerItemPO;
import com.wanniu.game.player.AttributeUtil;
import com.wanniu.game.player.GlobalConfig;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.poes.PlayerBasePO;
import com.wanniu.game.poes.PlayerBasePO.EquipStrengthPos;

import pomelo.Common.AttributeBase;
import pomelo.Common.AttributeSimple;
import pomelo.Common.Avatar;
import pomelo.area.EquipHandler.EquipStrengthenData;
import pomelo.item.ItemOuterClass.EquipmentJewelAtt;
import pomelo.item.ItemOuterClass.ItemDetail;
import pomelo.item.ItemOuterClass.MiniItem;

public class EquipUtil {

	/** 最高强化等级 */
	public static int maxStrengthLevel;
	/** 强化材料 */
	public static List<String> strengthMaterial = new ArrayList<>();
	/** 打造材料 */
	public static List<String> makeMaterial = new ArrayList<>();
	static {
		for (EnchantExt enchant : GameData.Enchants.values()) {
			if (maxStrengthLevel < enchant.iD) {
				maxStrengthLevel = enchant.iD;
			}
			if (StringUtil.isNotEmpty(enchant.mateCode1) && !strengthMaterial.contains(enchant.mateCode1)) {
				strengthMaterial.add(enchant.mateCode1);
			}
			if (StringUtil.isNotEmpty(enchant.mateCode2) && !strengthMaterial.contains(enchant.mateCode2)) {
				strengthMaterial.add(enchant.mateCode2);
			}
		}

		for (EquipMakeExt make : GameData.EquipMakes.values()) {
			if (StringUtil.isNotEmpty(make.reqMateCode1) && !makeMaterial.contains(make.reqMateCode1)) {
				makeMaterial.add(make.reqMateCode1);
			}
			if (StringUtil.isNotEmpty(make.reqMateCode2) && !makeMaterial.contains(make.reqMateCode2)) {
				makeMaterial.add(make.reqMateCode2);
			}
			if (StringUtil.isNotEmpty(make.reqMateCode3) && !makeMaterial.contains(make.reqMateCode3)) {
				makeMaterial.add(make.reqMateCode3);
			}
		}
		
		for (RefineExt refineExt : GameData.Refines.values()) {
			if (StringUtil.isNotEmpty(refineExt.mateCode1) && !makeMaterial.contains(refineExt.mateCode1)) {
				makeMaterial.add(refineExt.mateCode1);
			}
			if (StringUtil.isNotEmpty(refineExt.mateCode2) && !makeMaterial.contains(refineExt.mateCode2)) {
				makeMaterial.add(refineExt.mateCode2);
			}
		}
		
		for (ReBuildExt reBuildExt : GameData.ReBuilds.values()) {
			if (StringUtil.isNotEmpty(reBuildExt.mateCode1) && !makeMaterial.contains(reBuildExt.mateCode1)) {
				makeMaterial.add(reBuildExt.mateCode1);
			}
			if (StringUtil.isNotEmpty(reBuildExt.mateCode2) && !makeMaterial.contains(reBuildExt.mateCode2)) {
				makeMaterial.add(reBuildExt.mateCode2);
			}
			if (StringUtil.isNotEmpty(reBuildExt.mateCode3) && !makeMaterial.contains(reBuildExt.mateCode3)) {
				makeMaterial.add(reBuildExt.mateCode3);
			}
		}
		
		for (SeniorReBuildExt seniorReBuildExt : GameData.SeniorReBuilds.values()) {
			if (StringUtil.isNotEmpty(seniorReBuildExt.mateCode1) && !makeMaterial.contains(seniorReBuildExt.mateCode1)) {
				makeMaterial.add(seniorReBuildExt.mateCode1);
			}
			if (StringUtil.isNotEmpty(seniorReBuildExt.mateCode2) && !makeMaterial.contains(seniorReBuildExt.mateCode2)) {
				makeMaterial.add(seniorReBuildExt.mateCode2);
			}
			if (StringUtil.isNotEmpty(seniorReBuildExt.mateCode3) && !makeMaterial.contains(seniorReBuildExt.mateCode3)) {
				makeMaterial.add(seniorReBuildExt.mateCode3);
			}
		}
		
		for (ReBornExt reBornExt : GameData.ReBorns.values()) {
			if (StringUtil.isNotEmpty(reBornExt.mateCode1) && !makeMaterial.contains(reBornExt.mateCode1)) {
				makeMaterial.add(reBornExt.mateCode1);
			}
			if (StringUtil.isNotEmpty(reBornExt.mateCode2) && !makeMaterial.contains(reBornExt.mateCode2)) {
				makeMaterial.add(reBornExt.mateCode2);
			}
		}
	}

	/**
	 * 宝石数据
	 * 
	 * @returns {Array}
	 */
	public static List<EquipmentJewelAtt> toJson4Gem(EquipStrengthPos posInfo) {
		List<EquipmentJewelAtt> data = new ArrayList<>();
		if (posInfo.gems == null)
			return data;
		for (int gem_index : posInfo.gems.keySet()) {
			// for(int gem_index = 1; gem_index <= posInfo.socks; gem_index ++) {
			String gemCode = posInfo.gems.get(gem_index);
			EquipmentJewelAtt.Builder gemData = EquipmentJewelAtt.newBuilder();
			gemData.setIndex(gem_index);
			if (StringUtil.isNotEmpty(gemCode)) {
				DItemBase prop = ItemUtil.getUnEquipPropByCode(gemCode);
				gemData.setId(AttributeUtil.getIdByName(prop.prop));
				gemData.setValue(prop.min);

				MiniItem.Builder mini = ItemUtil.getMiniItemData(gemCode, 1);
				if (prop != null && mini != null) {
					gemData.setGem(mini);
				}
			}

			data.add(gemData.build());
		}
		Out.debug(EquipUtil.class, " toJson4Gem ", data);
		return data;
	}

	// /**
	// * 获取宝石详情数据
	// * */
	// var getGemDetail (equip){
	// itemUtil = getItemUtil();
	// var data = [];
	// var jewel = equip.speData.jewelAtts;
	// for(var index in jewel){
	// var gemInfo = jewel[index];
	// if(gemInfo){
	// var gemData = {};
	// gemData.index = Number(index);
	// gemData.attrName = gemInfo.key;//attributeUtil.getNameByKey(gemInfo.key);
	// gemData.value = gemInfo.value;
	//
	// var prop = itemUtil.getMiniItemData(gemInfo.code, 1);
	// if(prop){
	// gemData.name = prop.name;
	// }
	// data.push(gemData);
	// }
	// }
	// Out.debug(EquipUtil.class,"getGemDetail ",data);
	// return data;
	// };

	/**
	 * 镶嵌宝石
	 * 
	 * @param index 待镶嵌孔的位置
	 * @param code
	 * @returns {boolean}
	 */
	public static boolean fillGem(EquipStrengthPos posInfo, int[] index, String code, WNPlayer player) {
		DItemBase prop = ItemUtil.getUnEquipPropByCode(code);
		if (prop == null) {
			Out.error("Equip fillGem config error code ", code);
			return false;
		}

		// if(posInfo.gems.get(index) != null){
		// // 该位置已经被镶嵌宝石
		// return false;
		// }
		//
		// // 宝石孔大于宝石数量
		// if(index > posInfo.socks || index < 1){
		// return false;
		// }

		if (posInfo.gems.size() >= posInfo.socks) {
			return false;
		}

		for (int i = 1; i <= posInfo.socks; i++) {
			if (!posInfo.gems.containsKey(i)) {
				posInfo.gems.put(i, code);
				index[0] = i;
				break;
			}
		}
		return true;
	};

	/**
	 * 强化相关信息
	 * 
	 * @returns {{}}
	 */
	public static EquipStrengthenData getStrengthInfo(WNPlayer player, int pos) {
		NormalEquip equip = player.equipManager.getEquipment(pos);
		EquipStrengthPos pair = player.equipManager.strengthPos.get(pos);
		EquipStrengthenData.Builder data = EquipStrengthenData.newBuilder();
		List<AttributeSimple> simpleAtts = new ArrayList<>();
		if (pair.enSection == GlobalConfig.EquipmentCraft_Enchant_MaxEnClass && pair.enLevel == GlobalConfig.EquipmentCraft_Enchant_MaxenLevel) {
			data.addAllSimpleAtts(simpleAtts);
		} else {
			List<MiniItem> list_cost_items = new ArrayList<>();
			EnchantExt prop = EquipUtil.getStrengthConfig(pair.enSection, pair.enLevel);
			if (prop != null) {
				for (String code : prop.mates.keySet()) {
					list_cost_items.add(ItemUtil.getMiniItemData(code, prop.mates.getIntValue(code), null).build());
				}
				data.addAllCostItem(list_cost_items);
				data.setNeedGoldNum(prop.costGold);
				if (equip != null) {
					Map<String, Integer> atts = new HashMap<>();
					Utils.deepCopy(atts, equip.itemDb.speData.baseAtts);
					if (equip.itemDb.speData.extAtts == null) {
						Utils.deepCopy(atts, equip.prop.fixedAtts);
					} else {// 重铸扩展属性改为可重复了
						Utils.deepCopyAffix(atts, equip.itemDb.speData.extAtts, equip.getQLevel());
					}
					data.addAllSimpleAtts(ItemUtil.getStrengthSimpleAtt(atts, pair.enSection, pair.enLevel));
				}
			} else {
				Out.error("pos = ", pos, ",enSection = ", pair.enSection, ", enLevel = ", pair.enLevel);
			}
		}
		return data.build();
	};

	/**
	 * 仅仅 获取装备avatar信息， 需要任务avatar，调用PlayerUtil.getBattleServerA...
	 * 
	 * @param changeModel 是否需要变身 NormalEquip tmpCloth =
	 *            this.equips.get(Const.EquipType.CLOTH.getvalue); NormalEquip
	 *            tmpRHand = this.getEquipment(Const.EquipType.MAIN_HAND.getvalue);
	 */
	public static List<Avatar> getAvatarData(int pro, PlayerBasePO playerBasePO, boolean changeModel) {
		CharacterCO basicProp = GameData.Characters.get(pro);
		Map<Integer, PlayerItemPO> equipDatas = playerBasePO.equipGrids;
		Map<Integer, EquipStrengthPos> strengthPos = playerBasePO.strengthPos;
		PlayerItemPO tmpCloth = equipDatas.get(Const.EquipType.CLOTH.getValue());
		PlayerItemPO tmpRHand = equipDatas.get(Const.EquipType.MAIN_HAND.getValue());

		List<Avatar> data = new ArrayList<>();
		Avatar.Builder avatar = Avatar.newBuilder();
		avatar.setEffectType(0);
		avatar.setTag(Const.AVATAR_TYPE.AVATAR_BODY.value);

		
		if (tmpCloth != null) {
			DEquipBase prop = (DEquipBase) ItemConfig.getInstance().getItemProp(tmpCloth.code);
			if (StringUtil.isNotEmpty(prop.avatarId)) {
				avatar.setFileName(String.valueOf(prop.avatarId));
			} else {
				avatar.setFileName(String.valueOf(basicProp.model));
			}
		} else {
			avatar.setFileName(String.valueOf(basicProp.model));
		}
		data.add(avatar.build());

		Avatar.Builder avatar_r = Avatar.newBuilder();
		avatar_r.setTag(Const.AVATAR_TYPE.R_HAND_WEAPON.value);
		String defaultRoleWeapon = basicProp.weaponmodel;
		if (tmpRHand != null) {
			DEquipBase prop = (DEquipBase) ItemConfig.getInstance().getItemProp(tmpRHand.code);
			String reDefaultWeapon;
			if (StringUtil.isEmpty(prop.avatarId)) {
				reDefaultWeapon = defaultRoleWeapon;
			} else {
				reDefaultWeapon = String.valueOf(prop.avatarId);
			}

			EquipStrengthPos pair = strengthPos.get(Const.EquipType.MAIN_HAND.getValue());
			if (basicProp.pro == Const.PlayerPro.YU_JIAN.value) {
				Avatar.Builder avatar_l = Avatar.newBuilder();
				avatar_l.setEffectType(getEffectTypeId(pair.enSection, pair.enLevel));
				avatar_l.setTag(Const.AVATAR_TYPE.L_HAND_WEAPON.value);
				avatar_l.setFileName(String.valueOf(reDefaultWeapon));
				data.add(avatar_l.build());

				avatar_r.setEffectType(getEffectTypeId(pair.enSection, pair.enLevel));
				avatar_r.setFileName(String.valueOf(reDefaultWeapon));
			} else {
				avatar_r.setEffectType(getEffectTypeId(pair.enSection, pair.enLevel));
				avatar_r.setFileName(String.valueOf(reDefaultWeapon));
			}
		} else {
			if (basicProp.pro == Const.PlayerPro.YU_JIAN.value) {
				Avatar.Builder avatar_l = Avatar.newBuilder();
				avatar_l.setEffectType(0);
				avatar_l.setTag(Const.AVATAR_TYPE.L_HAND_WEAPON.value);
				avatar_l.setFileName(String.valueOf(defaultRoleWeapon));
				data.add(avatar_l.build());

				avatar_r.setEffectType(0);
				avatar_r.setFileName(String.valueOf(defaultRoleWeapon));
			} else {
				avatar_r.setEffectType(0);
				avatar_r.setFileName(String.valueOf(defaultRoleWeapon));
			}
		}
		data.add(avatar_r.build());

		return data;
	}

	public static int getEffectTypeId(int enClass, int enLevel) {
		Out.debug("Equip getEffectTypeId: ", enClass, ",", enLevel);
		if (enClass == 0 && enLevel < 1) {
			return 0;
		}
		EnchantCO props = EquipCraftConfig.getInstance().getEnchantConfig(enClass, enLevel);
		if (props == null) {
			Out.error("equip getEffectTypeId null:", enLevel);
			return 0;
		}
		return props.effectType;
	};

	/**
	 * 强化配置
	 * 
	 * @param enLevel
	 * @returns {*}
	 */
	public static EnchantExt getStrengthConfig(int enClass, int enLevel) {
		if (enClass > GlobalConfig.EquipmentCraft_Enchant_MaxEnClass || enLevel > GlobalConfig.EquipmentCraft_Enchant_MaxenLevel) {
			return null;
		}
		if (enClass == GlobalConfig.EquipmentCraft_Enchant_MaxEnClass && enLevel == GlobalConfig.EquipmentCraft_Enchant_MaxenLevel) {
			return null;
		}
		if (enLevel == GlobalConfig.EquipmentCraft_Enchant_MaxenLevel) {
			enClass += 1;
			enLevel = 0;
		} else {
			enLevel += 1;
		}

		return EquipCraftConfig.getInstance().getEnchantConfig(enClass, enLevel);
	};

	/**
	 * 是否达到最高强化段位和等级
	 */
	public static boolean isMaxStrengthLevel(int enClass, int enLevel) {
		if (enClass == GlobalConfig.EquipmentCraft_Enchant_MaxEnClass && enLevel == GlobalConfig.EquipmentCraft_Enchant_MaxenLevel) {
			return true;
		}

		return false;
	};

	/**
	 * 是否已存在相同类型宝石
	 */
	public static boolean existSameTypeGem(int type, EquipStrengthPos posInfo) {
		for (String code : posInfo.gems.values()) {
			if (StringUtil.isNotEmpty(code)) {
				DItemEquipBase prop = ItemConfig.getInstance().getItemProp(code);
				if (type == ((DItemBase) prop).par) {
					return true;
				}
			}
		}

		return false;
	};

	/**
	 * 获取镶嵌的宝石数据列表
	 * 
	 * @param index 位置，不传或传0则获取所有
	 * @returns {itemCode: jewel.code, itemNum: 1, forceType: forceType}
	 */
	public static List<SimpleItemInfo> getGemList(EquipStrengthPos posInfo, int index) {
		List<SimpleItemInfo> itemList = new ArrayList<>();
		// if(index != 0){
		String jewel_code = posInfo.gems.get(index);
		if (StringUtil.isNotEmpty(jewel_code)) {
			Const.ForceType forceType = Const.ForceType.getE(Const.BindType.UN_BIND.getValue());
			SimpleItemInfo map = new SimpleItemInfo();
			map.itemCode = jewel_code;
			map.itemNum = 1;
			map.forceType = forceType;
			itemList.add(map);
		}
		// }else{
		// for(int i = 1; i <= this.speData.jewelHoleNum; ++i){
		// JewelAtts jewel = this.speData.jewelAtts.get(i);
		// if(jewel != null){
		// Const.BindType forceType = this.isBinding() ? Const.BindType.BIND :
		// Const.BindType.UN_BIND;
		// Map<String, Object> map = new HashMap<>();
		// map.put("itemCode", jewel.code);
		// map.put("itemNum", 1);
		// map.put("forceType", forceType);
		// itemList.add(map);
		// }
		// }
		// }

		return itemList;
	};

	/**
	 * 扩展属性 AttributeBase 生成
	 */
	public static List<AttributeBase> getAttsAttributeBase(List<AttsObj> atts) {
		List<AttributeBase> list = new ArrayList<>();
		if (atts == null) {
			return list;
		}
		for (AttsObj att : atts) {
			AttributeBase ab = getAttributeBase(att);
			list.add(ab);
		}
		return list;
	}

	public static AttributeBase getAttributeBase(AttsObj att) {
		AttributeBase.Builder ab = AttributeBase.newBuilder();

		ab.setId(AttributeUtil.getIdByKey(att.key));
		ab.setIsFormat(AttributeUtil.getFormatByKey(att.key));
		ab.setValue(att.value);

		ab.setMinValue(att.min);
		ab.setMaxValue(att.max);

		ab.setParam3(att.par);// par参数
		return ab.build();
	}

	/**
	 * 根据装备 数据库对象获取 网络数据
	 */
	public static List<ItemDetail> getAllEquipDetails4PayLoad(PlayerBasePO playerBasePO, Map<Integer, PlayerItemPO> equipGrids) {
		List<NormalEquip> list = new ArrayList<>();
		for (PlayerItemPO itemDb : equipGrids.values()) {
			list.add((NormalEquip) ItemUtil.createItemByDbOpts(itemDb));
		}
		List<ItemDetail> data = new ArrayList<>();
		for (NormalEquip equip : list) {
			data.add(equip.getItemDetail(playerBasePO).build());
		}
		return data;
	};
}
