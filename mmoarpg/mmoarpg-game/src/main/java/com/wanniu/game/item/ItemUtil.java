package com.wanniu.game.item;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;

import com.wanniu.core.game.LangService;
import com.wanniu.core.logfs.Out;
import com.wanniu.core.util.RandomUtil;
import com.wanniu.core.util.StringUtil;
import com.wanniu.game.bag.WNBag.SimpleItemInfo;
import com.wanniu.game.common.Const;
import com.wanniu.game.common.Const.AffixType;
import com.wanniu.game.common.Const.PlayerBtlData;
import com.wanniu.game.common.Utils;
import com.wanniu.game.data.AfterFilterCO;
import com.wanniu.game.data.EnchantCO;
import com.wanniu.game.data.GameData;
import com.wanniu.game.data.MeltConfigCO;
import com.wanniu.game.data.ReBuildStarCO;
import com.wanniu.game.data.base.DEquipBase;
import com.wanniu.game.data.base.DItemBase;
import com.wanniu.game.data.base.DItemEquipBase;
import com.wanniu.game.data.base.FourProp;
import com.wanniu.game.data.ext.AffixExt;
import com.wanniu.game.data.ext.TreasureClassExt;
import com.wanniu.game.equip.EquipCraftConfig;
import com.wanniu.game.equip.EquipUtil;
import com.wanniu.game.equip.NormalEquip;
import com.wanniu.game.equip.RepeatKeyMap;
import com.wanniu.game.equip.RepeatKeyMap.Pair;
import com.wanniu.game.item.data.AttsObj;
import com.wanniu.game.item.data.tc.TCItemData;
import com.wanniu.game.item.po.ItemSpeData;
import com.wanniu.game.item.po.PlayerItemPO;
import com.wanniu.game.player.AttributeUtil;
import com.wanniu.game.player.GlobalConfig;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.request.bag.UseItemHandler.GetItemChanagePropertyResult;

import Xmds.RefreshPlayerPropertyChange;
import pomelo.Common.AttributeSimple;
import pomelo.area.EquipHandler.EquipPos;
import pomelo.item.ItemOuterClass.MiniItem;

/**
 * 物品工具类
 * 
 * @author Yangzz
 *
 */
public class ItemUtil {

	/**
	 * 根据物品类型判断是否是装备
	 */
	public static boolean isEquipByItemType(int itemType) {
		// int itemType = ItemConfig.getInstance().getTypeConfig(type).id;
		if (itemType == Const.ItemType.Weapon.getValue() || itemType == Const.ItemType.Armor.getValue() || itemType == Const.ItemType.Oranament.getValue() || itemType == Const.ItemType.RideEquip.getValue()) {
			return true;
		}
		return false;
	}

	/**
	 * 通过code获取相应的prop
	 * 
	 * @param code
	 * @returns {*}
	 */
	public static DItemEquipBase getPropByCode(String code) {
		if (StringUtils.isEmpty(code)) {
			return null;
		}
		// var prop = dataAccessor.equipProps.find({Code : code})[0];
		// if(!prop){
		// prop = dataAccessor.unEquipProps.find({Code : code})[0];
		// }
		DItemEquipBase prop = ItemConfig.getInstance().getItemProp(code);
		return prop;
	}

	/**
	 * 通过code获取非装备的prop
	 * 
	 * @param code
	 * @returns {*}
	 */
	public static DItemBase getUnEquipPropByCode(String code) {
		// var prop = dataAccessor.unEquipProps.find({Code : code})[0];
		return (DItemBase) ItemConfig.getInstance().getItemProp(code);
	};

	/**
	 * 是否是不进背包的道具
	 * 
	 * @param code
	 * @returns {boolean}
	 */
	public static boolean isVirtualItem(String code) {
		DItemEquipBase prop = ItemConfig.getInstance().getItemProp(code);
		if (prop == null) {
			return false;
		}

		int itemSecondType = ItemConfig.getInstance().getSecondType(prop.type);
		if (itemSecondType == Const.ItemSecondType.virtual.getValue()
				// || itemSecondType == Const.ItemSecondType.mastery.getValue()
//				|| itemSecondType == Const.ItemSecondType.virtQuest.getValue()
				) {
			return true;
		}

		return false;
	}

	public static List<AttributeSimple> getStrengthSimpleAtt(Map<String, Integer> data, int enClass, int enLevel) {
		List<AttributeSimple> newDataArray = new ArrayList<>();
		int maxClass = GlobalConfig.EquipmentCraft_Enchant_MaxEnClass;
		int maxLevel = GlobalConfig.EquipmentCraft_Enchant_MaxenLevel;
		// 单独处理MinPhy MaxPhy MinMag MaxMag
		for (Map.Entry<String, Integer> entry : data.entrySet()) {
			AttributeSimple.Builder tempData = AttributeSimple.newBuilder();
			if (entry.getKey().equals("MinPhy")) {
				tempData.setId(AttributeUtil.getIdByKey("Phy"));
				tempData.setValue(0);
				if (enClass < maxClass || (enClass == maxClass && enLevel < maxLevel)) {
					EnchantCO enchantProp = EquipUtil.getStrengthConfig(enClass, enLevel);
					tempData.setValue((int) (0.5 * (data.get(entry.getKey()) + data.get("MaxPhy")) * enchantProp.propPer / 10000));
				}
				newDataArray.add(tempData.build());
			}
			if (entry.getKey().equals("MinMag")) {
				tempData.setId(AttributeUtil.getIdByKey("Mag"));
				tempData.setValue(0);
				if (enClass < maxClass || (enClass == maxClass && enLevel < maxLevel)) {
					EnchantCO enchantProp = EquipUtil.getStrengthConfig(enClass, enLevel);
					tempData.setValue((int) (0.5 * (data.get(entry.getKey()) + data.get("MaxMag")) * enchantProp.propPer / 10000));
				}
				newDataArray.add(tempData.build());
			}
		}
		for (Map.Entry<String, Integer> entry : data.entrySet()) {
			if (!entry.getKey().equals("MinMag") && !entry.getKey().equals("MaxMag") && !entry.getKey().equals("MinPhy") && !entry.getKey().equals("MaxPhy")) {
				AttributeSimple.Builder newData = AttributeSimple.newBuilder();
				newData.setId(AttributeUtil.getIdByKey(entry.getKey()));
				newData.setValue(0);
				if (enClass < maxClass || (enClass == maxClass && enLevel < maxLevel)) {
					EnchantCO enchantProp = EquipUtil.getStrengthConfig(enClass, enLevel);
					newData.setValue((int) (data.get(entry.getKey()) * enchantProp.propPer / 10000));
				}
				newDataArray.add(newData.build());
			}
		}
		return newDataArray;
	};

	// public static List<AttributeSimple>
	// getStrengthSimpleAtt(RepeatKeyMap<String,Integer> data, int enClass, int
	// enLevel){
	// List<AttributeSimple> newDataArray = new ArrayList<>();
	// int maxClass = GlobalConfig.EquipmentCraft_Enchant_MaxEnClass;
	// int maxLevel = GlobalConfig.EquipmentCraft_Enchant_MaxenLevel;
	// //单独处理MinPhy MaxPhy MinMag MaxMag
	// for(RepeatKeyMap.Pair<String, Integer> entry : data.entrySet()){
	// AttributeSimple.Builder tempData = AttributeSimple.newBuilder();
	// if(entry.k.equals("MinPhy")) {
	// tempData.setId(AttributeUtil.getIdByKey("Phy"));
	// tempData.setValue(0);
	// if(enClass < maxClass || (enClass == maxClass && enLevel < maxLevel)){
	// EnchantCO enchantProp = EquipUtil.getStrengthConfig(enClass, enLevel);
	// tempData.setValue((int) (0.5*(entry.v +
	// data.get("MaxPhy"))*enchantProp.propPer/10000));
	// }
	// newDataArray.add(tempData.build());
	// }
	// if(entry.k.equals("MinMag")) {
	// tempData.setId(AttributeUtil.getIdByKey("Mag"));
	// tempData.setValue(0);
	// if(enClass < maxClass || (enClass == maxClass && enLevel < maxLevel)){
	// EnchantCO enchantProp = EquipUtil.getStrengthConfig(enClass, enLevel);
	// tempData.setValue((int)(0.5*(entry.v +
	// data.get("MaxMag"))*enchantProp.propPer/10000));
	// }
	// newDataArray.add(tempData.build());
	// }
	// }
	// for(RepeatKeyMap.Pair<String, Integer> entry : data.entrySet()){
	// if(!entry.k.equals("MinMag") &&!entry.k.equals("MaxMag")
	// &&!entry.k.equals("MinPhy") &&!entry.k.equals("MaxPhy")){
	// AttributeSimple.Builder newData = AttributeSimple.newBuilder();
	// newData.setId(AttributeUtil.getIdByKey(entry.k));
	// newData.setValue(0);
	// if(enClass < maxClass || (enClass == maxClass && enLevel < maxLevel)){
	// EnchantCO enchantProp = EquipUtil.getStrengthConfig(enClass, enLevel);
	// newData.setValue((int) (data.get(entry.k)*enchantProp.propPer/10000));
	// }
	// newDataArray.add(newData.build());
	// }
	// }
	// return newDataArray;
	// };

	/**
	 * 数据库item属性重新组装 从db加载 TODO 调用这个方法的地方，大多数已经创建好NormalItem对象了，应该是修改源对象属性，而不是重新出创建
	 * 
	 * @param opts
	 */
	public static NormalItem createItemByDbOpts(PlayerItemPO itemDb) {
		DItemEquipBase prop = ItemConfig.getInstance().getItemProp(itemDb.code);
		if (prop == null) {
			// playerId 为0 表示系统
			Out.error("createItemByDbOpts error,code have deleted: ", itemDb.code);
			// 表里面的数据被删了
			throw new RuntimeException();
		}
		// if(!isEquipByItemType(prop.itemType) &&
		// (ItemConfig.getInstance().getSecondType(prop.type) ==
		// Const.ItemSecondType.pkear.getValue())){
		// //耳朵道具不能直接通过模版获取
		// itemDb.speDataObj = createItemSpeData(prop, 0,
		// itemDb.speDataObj.expandParas);
		// }else if(!isEquipByItemType(prop.itemType) &&
		// (ItemConfig.getInstance().getSecondType(prop.type) !=
		// Const.ItemSecondType.virtual.getValue())){
		// 非装备类物品直接通过模版获取
		// itemDb.speDataObj = createItemSpeData(prop, 0, null);
		// }

		NormalItem item = createItemByOpts(itemDb, prop);
		return item;
	};

	public static NormalItem createItemByOpts(PlayerItemPO itemDb, DItemEquipBase prop) {
		// int itemType = ItemConfig.getInstance().getFirstType(prop.type);
		int itemSecondType = ItemConfig.getInstance().getSecondType(prop.type);
		NormalItem item = null;
		if (isEquipByItemType(prop.itemType)) {
			item = new NormalEquip(itemDb, prop);
		} else if (itemSecondType == Const.ItemSecondType.virtual.getValue()) {
			item = new VirtualItem(itemDb, prop);
		} else {
			item = new NormalItem(itemDb, prop);

		}
		return item;
	};

	/**
	 * 通过权值获得相应下表 params: rares[] 权值数组
	 */
	public static int getIndexByRare(List<Integer> rareArray) {
		int result = 0;
		if (rareArray == null || rareArray.size() == 0)
			return result;
		int rares = 0;
		for (int rare : rareArray) {
			rares = rares + rare;
		}
		if (rares == 0) {
			return result;
		}
		int ranRare = RandomUtil.getInt(0, rares);
		int maxRare = 0;
		for (int i = 0; i < rareArray.size(); i++) {
			int rare = rareArray.get(i);
			maxRare = maxRare + rare;
			if (rare > 0 && ranRare <= maxRare) {
				result = i;
				break;
			}
		}

		return result;
	};

	public static ItemSpeData createItemSpeData(DItemEquipBase prop, int worth) {
		ItemSpeData speData = new ItemSpeData();

		if (isEquipByItemType(prop.itemType)) {
			speData = initSpeData((DEquipBase) prop);
		} else if (ItemConfig.getInstance().getSecondType(prop.type) == Const.ItemSecondType.virtual.getValue()) {
			speData.worth = worth;
		}
		// else if (ItemConfig.getInstance().getSecondType(prop.type) ==
		// Const.ItemSecondType.pkear.getValue()) {
		// speData.expandParas = expandParas;
		// }

		return speData;
	}

	public static ItemSpeData initSpeData(DEquipBase prop) {
		ItemSpeData speData = new ItemSpeData();

		speData.baseAtts = new HashMap<>(); // 3条主属性
		if (StringUtil.isEmpty(prop.baseCode)) { // 随机属性装备
			speData.extAtts = new RepeatKeyMap<>(); // 6条扩展属性

			// 生成装备3条随机属性值+6条 随机词条
			initBaseAtts(speData.baseAtts, prop);
			initExtAtts(speData.extAtts, prop, 0, 0);

			Map<String, Integer> exarAttr = getSameAttsExtAttributes(speData.extAtts, prop.type, prop.code);// 额外赠送的属性
			if (exarAttr != null) {
				speData.extAttsAdd = exarAttr;
			}
			initLegendAtts(speData, prop);
		} else { // 固定属性装备
			// 3条主属性
			for (String attrName : prop.baseAtts.keySet()) {
				FourProp pair = prop.baseAtts.get(attrName);
				if(pair==null) {
					continue;
				}
				speData.baseAtts.put(pair.prop, RandomUtil.getInt(pair.min, pair.max)); // 固定属性取
			}
			// 6条 条扩展属性:扩展属性不存数据库，直接从模板里面取
			// DEquipBase baseProp = ItemConfig.getInstance().getEquipProp(prop.baseCode);
			// for(FourProp pair : baseProp.fixedAtts) {
			// speData.extAtts.add(new AttsObj(pair.prop, RandomUtil.getInt(pair.min,
			// pair.max), pair.par, pair.min, pair.max));
			// }
		}

		return speData;
	}

	/**
	 * 生成随机属性（3条主属性，6条扩展属性）
	 */
	public static void initBaseAtts(Map<String, Integer> baseAtts, DEquipBase prop) {
		for (String attrName : prop.baseAtts.keySet()) {
			FourProp pair = prop.baseAtts.get(attrName);
			if(pair==null) {
				continue;
			}
			baseAtts.put(pair.prop, RandomUtil.getInt(pair.min, pair.max));
		}
	}

	/**
	 * 6条扩展属性
	 * 
	 * @param lastCount:最低属性条目
	 * @param lockedCount:锁定的属性条目数量，非重铸时传入0即可
	 *            装备获得时，默认给出该件装备在表格中配置的最少的条目数，之后在重铸时会有一定几率在当前条目数上增+1条，最多加到条目数上限
	 */
	public static void initExtAtts(RepeatKeyMap<Integer, Integer> extAtts, DEquipBase prop, int lastCount, int lockedCount) {
		// 随机词条
		int rdIndex = RandomUtil.hit(new int[] { GlobalConfig.Equipment_MinAffixChance, 10000 - GlobalConfig.Equipment_MinAffixChance });
		int rdTimes = rdIndex == 0 ? prop.minAffixCount : prop.maxAffixCount;
		rdTimes = rdTimes > lastCount ? rdTimes : lastCount;

		// 一定几率在当前条目数上增+1条，最多加到条目数上限
		if (lastCount != 0 && rdTimes < prop.maxAffixCount) {
			rdIndex = RandomUtil.hit(new int[] { GlobalConfig.Equipment_ReBuild_AddAffixChance, 10000 - GlobalConfig.Equipment_ReBuild_AddAffixChance });
			rdTimes = rdIndex == 0 ? rdTimes + 1 : rdTimes;
		}

		// 减去已锁定的数目
		rdTimes -= lockedCount;

		List<Integer> usedGroupId = new ArrayList<>(); // 已经随机到的词条，下次随机不再出现
		for (int i = 0; i < rdTimes; i++) {
			AffixExt finalAffix = initRebuildOneAtts(prop, usedGroupId, AffixType.normal);
			if (finalAffix == null) {
				continue;
			}
			FourProp rdProp = finalAffix.props.get(prop.qcolor);
			if (rdProp != null) {
				extAtts.put(finalAffix.iD, RandomUtil.getInt(rdProp.min, rdProp.max));
			}
		}
		
	}

	/**
	 * 6条扩展属性
	 * 
	 * @param lastCount:最低属性条目
	 * @param lockedCount:锁定的属性条目数量，非重铸时传入0即可
	 *            装备获得时，默认给出该件装备在表格中配置的最少的条目数，之后在重铸时会有一定几率在当前条目数上增+1条，最多加到条目数上限
	 */
	public static void initRebuildExtAtts(RepeatKeyMap<Integer, Integer> tempExtAtts, DEquipBase prop, int lastCount, int lockedCount) {
		// 随机词条
		int rdIndex = RandomUtil.hit(new int[] { GlobalConfig.Equipment_MinAffixChance, 10000 - GlobalConfig.Equipment_MinAffixChance });
		int rdTimes = rdIndex == 0 ? prop.minAffixCount : prop.maxAffixCount;
		rdTimes = rdTimes > lastCount ? rdTimes : lastCount;

		// 一定几率在当前条目数上增+1条，最多加到条目数上限
		if (lastCount != 0 && rdTimes < prop.maxAffixCount) {
			rdIndex = RandomUtil.hit(new int[] { GlobalConfig.Equipment_ReBuild_AddAffixChance, 10000 - GlobalConfig.Equipment_ReBuild_AddAffixChance });
			rdTimes = rdIndex == 0 ? rdTimes + 1 : rdTimes;
		}

		// 减去已锁定的数目
		rdTimes -= lockedCount;
		if (rdTimes > 5) {
			Out.error("equip", prop.code, ",", prop.desc, "rebuild rdTimes error ", rdTimes);
		}

		List<Integer> usedGroupId = new ArrayList<>(); // 保存已经随机到的词条现
		StringBuilder sb = new StringBuilder(">>>>>>>>>usedGroupId:");
		// 临时属性中保存已锁定的属性
		for (RepeatKeyMap.Pair<Integer, Integer> rp : tempExtAtts.entrySet()) {
			if (rp.k > 0) {
				AffixExt affix = GameData.Affixs.get(rp.k);
				if(affix == null) {
					Out.warn("key="+rp.k + " val=" + rp.v);
					continue;
				}
				
				int gid = affix.groupID;
				usedGroupId.add(gid);
				sb.append(gid).append(",");
			}
		}
		Out.debug(sb);

		for (int i = 0; i < rdTimes; i++) {
			AffixExt finalAffix = initRebuildOneAtts(prop, usedGroupId, AffixType.normal);
			if (finalAffix == null) {
				continue;
			}
			FourProp rdProp = finalAffix.props.get(prop.qcolor);
			if (rdProp != null) {// 策划要求重铸的时候降低随机的重铸下限，取原下限的80%
				int val = RandomUtil.getInt(calcRebuildPropMin(rdProp.min), rdProp.max);
				tempExtAtts.putIfEmpty(finalAffix.iD, val);
				if(val<rdProp.min) {
					Out.info("成功生成一条属性低于下限>>>>>>>>>>" + finalAffix.iD + "," + finalAffix.props.get(prop.qcolor).prop + "," + val+ " min=" + rdProp.min);
				}else {
					Out.debug("成功生成一条属性>>>>>>>>>>" + finalAffix.iD + "," + finalAffix.props.get(prop.qcolor).prop + "," + val);
				}
			}
		}
	}

	/**
	 * 根据初始配置表的重铸词条属性下限值，计算实际的属性下限值
	 * 
	 * @param originalMin
	 * @return
	 */
	public static int calcRebuildPropMin(int originalMin) {
		return (int) originalMin * GlobalConfig.Equipment_PropNum_FloorRate / 100;
	}

	/**
	 * 6条扩展属性
	 * 
	 * @param lastCount:最低属性条目 刷新扩展属性的数值
	 */
	public static void seniorInitExtAtts(RepeatKeyMap<Integer, Integer> extAtts, RepeatKeyMap<Integer, Integer> tempExtAtts_senior, DEquipBase prop) {
		List<Pair<Integer, Integer>> list = extAtts.entrySet();
		for (Pair<Integer, Integer> p : list) {			
			AffixExt finalAffix = GameData.Affixs.get(p.k);
			if(finalAffix == null) {
				Out.warn("key="+p.k + " val=" + p.v);
				continue;
			}
			FourProp rdProp = finalAffix.props.get(prop.qcolor);	
			if(rdProp==null) {
				continue;
			}
			int max = rdProp.max;
			if (p.v < max) {
				int addPer = RandomUtil.getIndex(GlobalConfig.Equipment_SeniorReBuild_MaxRatio + 1);
				if (addPer <= 0) {
					tempExtAtts_senior.put(p.k, p.v);
					continue;
				}
				int add = Math.round(p.v * addPer * 1.0f / 100);
				if(add <= 0) {					
					tempExtAtts_senior.put(p.k, p.v);
					continue;
				}
				int after = add+p.v;
				after = after > max ? max : after;
				tempExtAtts_senior.put(p.k, after);
			}else {
				tempExtAtts_senior.put(p.k, p.v);
			}
		}
	}

	/**
	 * 1条传奇属性
	 */
	public static void initLegendAtts(ItemSpeData speData, DEquipBase prop) {
		// 随机词条
		List<Integer> usedGroupId = new ArrayList<>(); // 已经随机到的词条，下次随机不再出现
		AffixExt finalAffix = initOneAtts(prop, usedGroupId, AffixType.legend);
		if (finalAffix != null) {
			FourProp rdProp = finalAffix.props.get(prop.qcolor);
			if (rdProp != null) {
				speData.legendAtts = new HashMap<>();
				speData.legendAtts.put(finalAffix.iD, RandomUtil.getInt(rdProp.min, rdProp.max));
			}
		}
	}

	/**
	 * 1~2条独有属性
	 */
	public static void initUniqueAtts(List<AttsObj> uniqueAtts, DEquipBase prop) {
		for (FourProp pair : prop.uniqueAtts.values()) {
			uniqueAtts.add(new AttsObj(pair.prop, RandomUtil.getInt(pair.min, pair.max), pair.par, pair.min, pair.max));
		}
	}

	
	private static ReBuildStarCO findReBuildStarCObyNum(int num, String type) {
		List<ReBuildStarCO> list = GameData.findReBuildStars((t) -> {
			return t.type.equals(type) && t.enClass == num;
		});
		if (list != null && !list.isEmpty()) {
			return list.get(0);
		}
		return null;
	}

	private static ReBuildStarCO findReBuildStarCObyMax(List<ReBuildStarCO> lt) {
		if (lt == null || lt.isEmpty()) {
			return null;
		}
		ReBuildStarCO maxCO = null;
		for (ReBuildStarCO co : lt) {
			if (maxCO == null) {
				maxCO = co;
			} else {
				maxCO = co.enClass > maxCO.enClass ? co : maxCO;
			}
		}
		return maxCO;
	}
	
	private static List<ReBuildStarCO> findMaxStarCO(String type) {
		List<ReBuildStarCO> list = GameData.findReBuildStars((t) -> {
			return t.type.equals(type);
		});
		return list;
	}
	/**
	 * 扩展属性任意2条及以上相同属性类型都有额外的属性加成
	 * 
	 * @return
	 */
	public static Map<String, Integer> getSameAttsExtAttributes(RepeatKeyMap<Integer, Integer> attrs, String type, String code) {
		Map<Integer, Integer> sames = new HashMap<>();
		for (Integer key : attrs.keySet()) {// 找出相同属性类型并计算重复次数
			if (sames.containsKey(key)) {
				sames.put(key, sames.get(key) + 1);
			} else {
				sames.put(key, 1);
			}
		}

		Map<String, Integer> attr = null;
		for (Integer key : sames.keySet()) {
			int repeatTimes = sames.get(key);
			if (repeatTimes <= 1) {// 只有一条属性就忽略
				continue;
			}
			attr = attr == null ? new HashMap<>() : attr;
			ReBuildStarCO co = findReBuildStarCObyNum(repeatTimes, type);
			if (co == null) {
				Out.warn("重铸装备获取额外属性失败1,发现当前相同的条目数大于配表数量,当前数量为:" + repeatTimes,  "部位=" + type, "code=", code);
				List<ReBuildStarCO> lt = findMaxStarCO(type);
				co = findReBuildStarCObyMax(lt);
				if (co == null) {
					Out.warn("重铸装备获取额外属性失败1,发现不可预料的情况,难道是配表为空?,当前数量为:" + repeatTimes,"部位=" + type, "code=", code);
				}
			} else {
				PlayerBtlData data = PlayerBtlData.getE(co.prop);
				Integer vl = attr.get(data.toString());
				if (vl == null) {
					attr.put(data.toString(), co.proNum);
				} else {
					attr.put(data.toString(), co.proNum + vl);
				}
			}
		}
		return attr;
	}
	
	/**
	 * 初始化一条重铸 扩展属性
	 */
	public static AffixExt initRebuildOneAtts(DEquipBase prop, List<Integer> usedGroupId, AffixType affixType) {
		List<AffixExt> list_affixs = new ArrayList<>();
		List<Integer> rareArray = new ArrayList<>();

		StringBuilder sb = new StringBuilder("================");
		for (AffixExt affix : GameData.Affixs.values()) {
			if ((affix.equipType.equalsIgnoreCase("All") || affix.equipType.indexOf(prop.type) != -1) && (prop.tcLevel == affix.level || affix.level == 0) && (StringUtil.isEmpty(affix.pro) || affix.pro.indexOf(prop.pro) != -1)
			// && !usedGroupId.contains(affix.groupID)
					&& affixType.value == affix.affixType && affix.isValid == 1) {
				list_affixs.add(affix);
				sb.append(affix.groupID).append(",");
				rareArray.add(affix.rare);
			}
		}

		Out.debug(list_affixs.size(), ":::::", sb);
		if (list_affixs.size() > 0) {
			while (true) {
				int index = Utils.getIndexByRareByList(rareArray);
				AffixExt finalAffix = list_affixs.get(index);

				if (isValid(usedGroupId, finalAffix.groupID)) {
					usedGroupId.add(finalAffix.groupID);
					return finalAffix;
				} else {
					Out.debug(finalAffix.groupID);
				}
			}
		} else {
			Out.error("not six affix prop:", prop.code, ":", prop.name);
		}
		return null;
	}

	/**
	 * 获取一条固定的属性值
	 * 
	 * @param prop
	 * @param usedGroupId
	 * @param affixType
	 * @return
	 */
	public static AffixExt getFixOneAtts(int lv,String type,String pro, AffixType affixType, String attName) {
		AffixExt fixAffix = null;
		for (AffixExt affix : GameData.Affixs.values()) {
			if ((affix.equipType.equalsIgnoreCase("All") || affix.equipType.indexOf(type) != -1) && (lv == affix.level || affix.level == 0) && (StringUtil.isEmpty(affix.pro) || affix.pro.indexOf(pro) != -1)
					&& affixType.value == affix.affixType && affix.isValid == 1 && attName.equals(affix.attName)) {
				fixAffix = affix;
				break;
			}
		}
		return fixAffix;
	}

	/**
	 * 初始化一条 扩展属性
	 */
	public static AffixExt initOneAtts(DEquipBase prop, List<Integer> usedGroupId, AffixType affixType) {
		List<AffixExt> list_affixs = new ArrayList<>();
		List<Integer> rareArray = new ArrayList<>();
		for (AffixExt affix : GameData.Affixs.values()) {
			if ((affix.equipType.equalsIgnoreCase("All") || affix.equipType.indexOf(prop.type) != -1) && (prop.tcLevel == affix.level || affix.level == 0) && (StringUtil.isEmpty(affix.pro) || affix.pro.indexOf(prop.pro) != -1) && !usedGroupId.contains(affix.groupID) && affixType.value == affix.affixType && affix.isValid == 1) {
				list_affixs.add(affix);
				rareArray.add(affix.rare);
			}
		}
		if (list_affixs.size() > 0) {
			int index = Utils.getIndexByRareByList(rareArray);
			AffixExt finalAffix = list_affixs.get(index);
			usedGroupId.add(finalAffix.groupID);
			return finalAffix;
		} else {
			Out.error("not six affix prop:", prop.code, ":", prop.name);
		}
		return null;
	}

	/**
	 * 根据已经生成的groupID及刚生成的groupID判断已有多少条重复，并根据重复条数相应的概率来判断刚生成的groupID是否废弃
	 * 
	 * @param usedGroupId
	 * @param groupID
	 * @return
	 */
	private static boolean isValid(List<Integer> usedGroupId, int groupID) {
		int haved = 1;
		for (Integer i : usedGroupId) {
			if (i == groupID) {
				haved++;
			}
		}
		boolean result = false;
		switch (haved) {
		case 1:// 没有重复的
			result = true;
			break;
		case 2:
			result = Utils.randomPercent(GlobalConfig.Equipment_SameAffixChance2);
			break;
		case 3:
			result = Utils.randomPercent(GlobalConfig.Equipment_SameAffixChance3);
			break;
		case 4:
			result = Utils.randomPercent(GlobalConfig.Equipment_SameAffixChance4);
			break;
		case 5:
			result = Utils.randomPercent(GlobalConfig.Equipment_SameAffixChance5);
			break;
		}

		return result;
	}

	/**
	 * 获取装备可能随机到的所有属性条目
	 */
	public static void initAllExtAtts(List<AttsObj> extAtts, DEquipBase prop) {
		for (AffixExt affix : GameData.Affixs.values()) {
			if ((affix.equipType.equalsIgnoreCase("All") || affix.equipType.indexOf(prop.type) != -1) && (prop.tcLevel == affix.level || affix.level == 0) && (StringUtil.isEmpty(affix.pro) || affix.pro.indexOf(prop.pro) != -1) && affix.isValid == 1) {
				FourProp rdProp = affix.props.get(prop.qcolor);
				if (rdProp != null) {
					extAtts.add(new AttsObj(rdProp.prop, RandomUtil.getInt(rdProp.min, rdProp.max), rdProp.par, rdProp.min, rdProp.max));
				}
			}
		}
	}

	/**
	 * 重载一个生成奖励物品的便利方法
	 * 
	 * @param rewards map{key:ItemCode,value:ItemNum}
	 * @return
	 */
	public static List<NormalItem> createItemsByItemCode(Map<String, Integer> rewards) {
		List<NormalItem> returnItems = new ArrayList<>();
		for (String itemCode : rewards.keySet()) {
			List<NormalItem> items = createItemsByItemCode(itemCode, rewards.get(itemCode));
			returnItems.addAll(items);
		}
		return returnItems;
	}

	/**
	 * 通过code生成物品 params: code:code groupCount:数量
	 */
	public static List<NormalItem> createItemsByItemCode(String code, int groupCount) {
		// if(!expandParas){
		// expandParas = [];
		// }
		DItemEquipBase prop = ItemConfig.getInstance().getItemProp(code);
		if (prop == null) {
			return new ArrayList<>();
		}
		return createItemsByProp(prop, groupCount);
	};

	private static List<NormalItem> createItemsByProp(DItemEquipBase prop, int groupCount) {
		List<NormalItem> items = new ArrayList<NormalItem>();
		// int itemSecondType = ItemConfig.getInstance().getSecondType(prop.type);
		if (ItemConfig.getInstance().getSecondType(prop.type) == Const.ItemSecondType.virtual.getValue()) {
			PlayerItemPO itemDb = new PlayerItemPO();
			itemDb.id = UUID.randomUUID().toString();
			itemDb.code = prop.code;
			itemDb.groupCount = 1;
			itemDb.isNew = 1;
			itemDb.speData = createItemSpeData(prop, groupCount);
			itemDb.gotTime = new Date();
			itemDb.cdTime = 0; // 使用cd 0 表示没有cd
			itemDb.isBind = 0;

			NormalItem item = createItemByOpts(itemDb, prop);
			Out.debug("生成一个虚拟物品：", item);
			items.add(item);
			return items;
		}

		int remainCount = groupCount;
		while (remainCount > 0) {
			int count = remainCount;
			if (remainCount > prop.groupCount) {
				count = prop.groupCount;
			}
			remainCount = remainCount - count;

			PlayerItemPO itemDb = new PlayerItemPO();
			itemDb.id = UUID.randomUUID().toString();
			itemDb.code = prop.code;
			itemDb.groupCount = count;
			itemDb.isNew = 1;
			// if((itemSecondType == Const.ItemSecondType.pkear.getValue())) {
			// if(expandParas != null && expandParas instanceof ItemSpeData.ExpandParasObj)
			// {
			// itemDb.speDataObj = createItemSpeData(prop, 0, (ItemSpeData.ExpandParasObj)
			// expandParas);
			// }
			// } else {
			itemDb.speData = createItemSpeData(prop, 0);
			// }
			itemDb.gotTime = new Date();
			itemDb.cdTime = 0;
			itemDb.isBind = prop.bindType;

			NormalItem item = createItemByOpts(itemDb, prop);
			items.add(item);
		}
		return items;
	}

	/**
	 * 制作装备 params: color:成色 tcLevel:tcLevel itemSecondType:位置
	 */
	private static List<NormalItem> createEquipsByTcLevelAndType(int qColor, int tcLevel, int itemSecondType) {
		List<NormalItem> items = new ArrayList<NormalItem>();
		List<DEquipBase> props = ItemConfig.getInstance().getEquipProps(qColor, tcLevel, itemSecondType);
		if (props.size() <= 0) {
			Out.debug(ItemUtil.class, "创建装备失败 qColor:", qColor, " tcLevel:", tcLevel, " itemSecondType:", itemSecondType);
			return items;
		}

		List<Integer> rareArray = new ArrayList<>();
		props.forEach(prop -> {
			rareArray.add(prop.rare);
		});
		int index = getIndexByRare(rareArray);
		DEquipBase prop = props.get(index);
		return createItemsByProp(prop, 1);
	};

	/**
	 * 根据itemData创建道具 params: prop:tcProp itemData:itemData color:成色
	 * isNumOfRare:rare属性是否参与数量计算
	 */
	private static List<NormalItem> createItemsByItemData(TreasureClassExt prop, TCItemData itemData, int color, boolean isNumOfRare, List<Integer> colorRareArray, int level) {
		List<NormalItem> items = new ArrayList<>();
		String itemCode = itemData.code;
		int num = itemData.num;
		if (num == 0) {
			num = Utils.random(itemData.minNum, itemData.maxNum);
		}

		if (isNumOfRare) {
			num = num * itemData.rare;
		}
		// List<Integer> expandParas = itemData.expandParas;
		if (itemData.tcType == TCItemData.TC_EQUIP_TYPE) {
			// if(itemSecondType <= Const.ItemSecondType.HU_SHENG_FU.getValue()){
			int itemSecondType = Const.ItemSecondType.getV(itemCode);
			int tcLevel = prop.tcLevel;
			for (int i = 0; i < num; i++) {
				List<NormalItem> getItems = createEquipsByTcLevelAndType(color, tcLevel, itemSecondType);
				items.addAll(getItems);
			}
		} else {
			// 判定是否是TcCode
			TreasureClassExt tcProp = ItemConfig.getInstance().getTcProp(itemCode);
			if (tcProp != null && itemData.tcType == TCItemData.TC_INNER_TC) {// 嵌套TC
				List<NormalItem> getItems = null;
				// 判定是否成色覆盖
				// if(colorRareArray != null && colorRareArray.size() > 0){
				// getItems = createItemsByTcCode(itemCode, colorRareArray);
				// }else{
				// getItems = createItemsByTcCode(itemCode);
				// }
				getItems = createItemsByTcCode(itemCode, colorRareArray, level);
				items.addAll(getItems);
			} else {
				List<NormalItem> getItems = createItemsByItemCode(itemCode, num);
				items.addAll(getItems);
			}
		}
		return items;
	};

	/**
	 * @param code TCcode
	 * @return
	 */
	public static List<NormalItem> createItemsByTcCode(String code) {
		return createItemsByTcCode(code, null, 0);
	}

	/**
	 * @param code TCcode
	 * @param level player current level
	 * @return
	 */
	public static List<NormalItem> createItemsByRealTC(String code, int level) {
		return createItemsByTcCode(code, null, level);
	}

	/**
	 * 根据tcCode创建相应道具
	 */
	private static List<NormalItem> createItemsByTcCode(String code, List<Integer> colorRareArray, int level) {
		List<NormalItem> items = new ArrayList<>();
		if (StringUtil.isEmpty(code)) {
			return items;
		}
		if (level > 0) {// 根据人物等级重新计算TCcode
			code = ItemConfig.getInstance().getRealTC(code, level);
		}

		TreasureClassExt prop = ItemConfig.getInstance().getTcProp(code);
		if (prop == null) {
			return items;
		}

		// 掉落执行次数
		int picks = prop.picks;
		boolean isHasColor = true;

		if (colorRareArray == null || colorRareArray.size() == 0) {
			isHasColor = false;
		}
		if (picks > 0) {
			for (int i = 0; i < picks; i++) {
				List<Integer> rareArray = new ArrayList<>();
				rareArray.add(prop.noDrop);
				List<TCItemData> itemDatas = prop.items;
				for (TCItemData itemData : itemDatas) {
					rareArray.add(itemData.rare);
				}
				int index = getIndexByRare(rareArray);
				if (index > 0) {
					if (colorRareArray == null || colorRareArray.size() == 0) {
						colorRareArray = new ArrayList<>();
						colorRareArray.add(prop.white);
						colorRareArray.add(prop.blue);
						colorRareArray.add(prop.purple);
						colorRareArray.add(prop.legend);
						colorRareArray.add(prop.green);
					}

					int colorIndex = getIndexByRare(colorRareArray);
					int color = colorIndex; // colorIndex + 1;

					TCItemData itemData = itemDatas.get(index - 1);
					// 判定是否成色覆盖
					List<NormalItem> getItems = null;
					if (prop.overColor != 0) {
						getItems = createItemsByItemData(prop, itemData, color, false, colorRareArray, level);
					} else {
						getItems = createItemsByItemData(prop, itemData, color, false, null, level);
					}
					items.addAll(getItems);
				}
				if (!isHasColor) {
					colorRareArray = null;
				}
			}
		} else {
			picks = Math.abs(picks);
			for (int i = 0; i < picks; i++) {

				List<TCItemData> itemDatas = prop.items;
				TCItemData itemData = itemDatas.get(i);
				if (itemData != null) {
					if (colorRareArray == null || colorRareArray.size() == 0) {
						colorRareArray = new ArrayList<>();
						colorRareArray.add(prop.white);
						colorRareArray.add(prop.blue);
						colorRareArray.add(prop.purple);
						colorRareArray.add(prop.legend);
						colorRareArray.add(prop.green);
					}

					int colorIndex = getIndexByRare(colorRareArray);
					int color = colorIndex + 1;

					// 判定是否成色覆盖
					List<NormalItem> getItems = null;
					if (prop.overColor != 0) {
						getItems = createItemsByItemData(prop, itemData, color, true, colorRareArray, level);
					} else {
						getItems = createItemsByItemData(prop, itemData, color, true, null, level);
					}
					items.addAll(getItems);
				}
				if (!isHasColor) {
					colorRareArray = null;
				}
			}
		}

		// 执行TC后置过滤器 功能不完整被wfy注释掉了
		AfterFilterCO afterFilter = GameData.AfterFilters.get(code);
		if (afterFilter != null) {
			for (NormalItem item : items) {
				if (StringUtil.isEmpty(afterFilter.bindType)) {
					continue;
				}
				try {
					item.setBindFilter(Integer.parseInt(afterFilter.bindType));

					// 暂时开启这一条，测试一下有没有问题
					item.setBindFilter(Integer.parseInt(afterFilter.noAuction));
				} catch (Exception e) {
					Out.error("过滤表填错了");
				}
			}
		}
		return items;
	};

	public static List<NormalItem> getPackUpItems(List<NormalItem> items) {
		Map<String, NormalItem> temp = new HashMap<>();
		List<NormalItem> data = new ArrayList<>();
		// 先遍历一遍虚拟道具
		for (int i = 0; i < items.size(); i++) {
			NormalItem item = items.get(i);

			if (item.isVirtual()) {
				if (temp.get(item.itemDb.code) == null) {
					temp.put(item.itemDb.code, item);
				} else {
					((VirtualItem) temp.get(item.itemDb.code)).addWorth(item.getWorth());
				}
			} else {
				data.add(item);
			}
		}

		for (NormalItem item : temp.values()) {
			data.add(item);
		}

		temp = new HashMap<>();
		Map<String, NormalItem> tempBind = new HashMap<>();
		List<NormalItem> newData = new ArrayList<>();

		// 再合并非虚拟道具,绑定归绑定，非绑定归非绑定
		for (int i = 0; i < data.size(); i++) {
			NormalItem item = data.get(i);
			if (item == null) {
				continue;
			}
			// 到堆叠上限
			if (item.itemDb.groupCount != item.prop.groupCount) {
				if (item.isBinding()) {
					mergeItems(item, tempBind, newData);
				} else {
					mergeItems(item, temp, newData);
				}
			} else {
				newData.add(item);
			}
		}
		for (NormalItem item : temp.values()) {
			newData.add(item);
		}

		for (NormalItem item : tempBind.values()) {
			newData.add(item);
		}

		return newData;
	};

	public static void mergeItems(NormalItem item, Map<String, NormalItem> temp, List<NormalItem> newData) {
		if (temp.get(item.itemDb.code) == null) {
			temp.put(item.itemDb.code, item);
		} else {
			if (temp.get(item.itemDb.code).itemDb.groupCount + item.itemDb.groupCount >= item.prop.groupCount) {
				int tmpCount = item.itemDb.groupCount;
				// item.itemDb.groupCount = item.prop.groupCount;
				item.setNum(item.prop.groupCount);
				newData.add(item);
				// temp.get(item.itemDb.code).itemDb.groupCount =
				// temp.get(item.itemDb.code).itemDb.groupCount + tmpCount -
				// item.prop.groupCount;
				temp.get(item.itemDb.code).setNum(temp.get(item.itemDb.code).itemDb.groupCount + tmpCount - item.prop.groupCount);
				if (temp.get(item.itemDb.code).itemDb.groupCount == 0) {
					temp.remove(item.itemDb.code);
				}
			} else {
				// temp.get(item.itemDb.code).itemDb.groupCount += item.itemDb.groupCount;
				temp.get(item.itemDb.code).addGroupNum(item.itemDb.groupCount);
			}
		}
	};

	public static int getPackUpItemsNum(List<NormalItem> items) {
		int sum = 0;
		for (int i = 0; i < items.size(); ++i) {
			if (items.get(i).prop.itemSecondType != Const.ItemSecondType.virtual.getValue()
					// && items.get(i).prop.itemSecondType !=
					// Const.ItemSecondType.mastery.getValue()
					&& !items.get(i).isVirtQuest()) {
				sum++;
			}
		}
		return sum;
	}

	// public static int getPropBindType(AbsDItem prop, Const.ForceType forceType){
	// int bindType = prop.bindType;
	// if(forceType == null){
	// bindType = prop.bindType;
	// }else if(forceType == Const.ForceType.BIND){
	// bindType = Const.BindType.BIND.getValue();
	// }else if(forceType == Const.ForceType.UN_BIND){
	// if(isEquipByItemType(prop.itemType)){
	// bindType = Const.BindType.EQUIP_BIND.getValue();
	// }else{
	// bindType = Const.BindType.UN_BIND.getValue();
	// }
	// }
	// return bindType;
	// };

	/**
	 * 获取绑定类型
	 * 
	 * @param template
	 * @param forceType int
	 * @return
	 */
	public static int getPropBindType(DItemEquipBase template, Const.ForceType forceType) {
		int bindType = template.bindType;
		if (forceType == Const.ForceType.BIND) {
			bindType = Const.BindType.BIND.getValue();
		} else if (forceType == Const.ForceType.UN_BIND) {
			if (isEquipByItemType(template.itemType)) {
				bindType = Const.BindType.EQUIP_BIND.getValue();
			} else {
				bindType = Const.BindType.UN_BIND.getValue();
			}
		}
		return bindType;
	};

	public static MiniItem.Builder getMiniItemData(String code, int num) {
		return getMiniItemData(code, num, null);
	}

	public static MiniItem.Builder getMiniItemData(String code, int num, Const.ForceType forceBindType) {
		DItemEquipBase prop = getPropByCode(code);
		if (prop != null) {
			int bindType = getPropBindType(prop, forceBindType);
			MiniItem.Builder data = MiniItem.newBuilder();
			data.setCode(prop.code);
			data.setGroupCount(num);
			data.setIcon(prop.icon);
			data.setQColor(prop.qcolor);
			data.setName(prop.name);
			data.setStar(0);
			data.setBindType(bindType);

			return data;
		}

		return null;
	}

	// public static MiniItem getMiniItemDataFromConfig(String itemCode,int
	// itemCount,Const.ForceType forceBindType){
	// DItemEquipBase prop = ItemUtil.getPropByCode(itemCode);
	// if(prop != null) {
	// MiniItem.Builder builder = MiniItem.newBuilder();
	// int bindType = getPropBindType(prop, forceBindType);
	// //装换成 获取后状态
	// if(bindType == Const.BindType.BIND.getValue()){
	// bindType = Const.BindType.BIND_AFTER_GET.getValue();
	// } else if(bindType == Const.BindType.EQUIP_BIND.getValue()){
	// bindType = Const.BindType.EQUIP_BIND_AFTER_GET.getValue();
	// }
	// builder.setCode(prop.code);
	// builder.setGroupCount(itemCount);
	// builder.setIcon(prop.icon);
	// builder.setQColor(prop.qcolor);
	// builder.setName(prop.name);
	// builder.setStar(prop.star);
	// builder.setBindType(bindType);
	// return builder.build();
	// }
	// return null;
	// }

	// /************************************附魔相关函数 begin
	// ***************************************/
	// /**
	// * 根据装备位置获取附魔的详情(材料，图纸，钻石)
	// * @param pos, level, advanced
	// * @returns {Array}
	// */
	// public void getEnchantDetail (pos, level, advanced){
	// var props;
	// if(advanced != 0){
	// // 优先考虑进阶需求
	// props = dataAccessor.magicalProps.find({pos:pos, EquipUpLevel :
	// {$lte:advanced}});
	// }
	// else{
	// props = dataAccessor.magicalProps.find({pos:pos, EquipLevel : {$lte: level,
	// $gt:0}});
	// }
	//
	// Out.debug(ItemUtil.class, "getEnchantDetail ", props);
	// var data = [];
	// for(var i = 0; i < props.length; ++i){
	// var items = dataAccessor.unEquipProps.find({Type:"magical",Min:props[i].ID});
	// for(var j = 0; j < items.length; ++j){
	// var v = {
	// drawing : items[j].Code,
	// material : getMiniItemData(props[i].MateCode, props[i].MateCount),
	// diamond : Math.floor(props[i].Diamond/props[i].MateCount),
	// magicDes : props[i].PropMag,
	// drawName : items[j].Name
	// };
	// data.push(v);
	// }
	// }
	//
	// return data;
	// };

	/**
	 * 根据图纸获取附魔材料
	 * 
	 * @param draw
	 * @returns { itemCode: codesArray[prop.Qcolor], itemNum :
	 *          Number(countsArray[prop.Qcolor]), price :
	 *          Number(diamondsArray[prop.Qcolor]) }
	 */
	public static List<Object[]> getEnchantMaterial(DItemEquipBase prop) {
		List<Object[]> data = new ArrayList<>();
		String codes = GlobalConfig.Magical_MateCodes;
		String counts = GlobalConfig.Magical_MateCounts;
		String diamonds = GlobalConfig.Magical_MatePrice;

		if (StringUtil.isEmpty(codes) || StringUtil.isEmpty(counts) || StringUtil.isEmpty(diamonds)) {
			return data;
		}

		String[] codesArray = codes.split(",");
		String[] countsArray = counts.split(",");
		String[] diamondsArray = diamonds.split(",");

		data.add(new Object[] { codesArray[prop.qcolor], Integer.parseInt(countsArray[prop.qcolor]), Integer.parseInt(diamondsArray[prop.qcolor]) });

		return data;
	};

	// /**
	// * 根据图纸获取附魔的magicId
	// * @param draw
	// * @returns {number}
	// */
	// // public void getEnchantMagicId (draw){
	//// var prop = getUnEquipPropByCode(draw);
	//// if(!prop){
	//// return 0;
	//// }
	//// if(prop.Type != "magical"){
	//// return 0;
	//// }
	// //
	//// var props = dataAccessor.magicalProps.find({ID: prop.Min});
	//// if(props.length == 0){
	//// return 0;
	//// }
	//// prop = props[0];
	//// return Number(prop.ID);
	// // };
	//
	// /**
	// * 根据ID获得MagicalProp
	// * @param id
	// * @returns {*}
	// */
	// public void getMagicalPropById (id){
	// if(id == 0){
	// return null;
	// }
	//
	// var props = dataAccessor.magicalProps.find({ID : id});
	// if(props.length == 0){
	// return null;
	// }
	//
	// return props[0];
	// };
	// /************************************附魔相关函数 end
	// ***************************************/
	//
	// /************************************鉴定相关函数 begin
	// *************************************/
	// public void getIdentifyCost (quality){
	// var name = "Equipment.IdentfyGreen.Count";
	// if(quality == Const.ItemQuality.GREEN){
	// name = "Equipment.IdentfyGreen.Count";
	// } else if(quality == Const.ItemQuality.BLUE){
	// name = "Equipment.IdentfyBlue.Count";
	// } else if(quality == Const.ItemQuality.PURPLE){
	// name = "Equipment.IdentfyPurple.Count";
	// } else if(quality == Const.ItemQuality.ORANGE){
	// name = "Equipment.IdentfyLegend.Count";
	// } else if(quality == Const.ItemQuality.RED){
	// name = "Equipment.IdentfyLegend.Count"; //以后要改
	// }
	//
	// var itemNum = GameConfigManager.getInstance().get(name)||1;
	// var itemCode =
	// GameConfigManager.getInstance().get("Equipment.IdentfyItem.Code");
	//
	// return [{itemCode:itemCode, itemNum:itemNum}];
	// };
	//
	// /************************************鉴定相关函数 end
	// ***************************************/
	//
	// /************************************传承相关函数 begin
	// ***************************************/

	public static NormalItem getEquip(WNPlayer player, EquipPos EquipPos) {
		if (EquipPos.getBagOrBody() == Const.EquipPos.BODY.value) {
			return player.equipManager.getEquipment(EquipPos.getPosOrGrid());
		} else {
			return player.getWnBag().getItem(EquipPos.getPosOrGrid());
		}
	};

	// /************************************传承相关函数 end
	// ***************************************/
	//
	//
	// /************************************ 熔炼相关函数 begin
	// ***************************************/
	public static MeltConfigCO getMeltProp(int meltLevel, int quality) {
		return EquipCraftConfig.getInstance().getMeltProp(meltLevel, quality);
	};

	// /************************************ 熔炼相关函数 end
	// ***************************************/
	// /**
	// * 根据类型查找
	// * @param player
	// * @param type
	// * @returns {*}
	// */
	// public void getItemByType (player, type){
	// var items = player.bag.findItemByType(type);
	// if(items && items.length > 0){
	// return items;
	// }
	// return null;
	// }

	public static NormalItem getItemById(WNPlayer player, String id) {
		NormalItem item = player.bag.findItemById(id);
		if (item != null) {
			return item;
		}

		item = player.equipManager.getEquipById(id);
		if (item != null) {
			return item;
		}
		return null;
	};

	public static GetItemChanagePropertyResult getItemChanageProperty(NormalItem item) {
		DItemBase prop = (DItemBase) item.prop;
		return getItemChanageProperty(prop);
	}

	public static GetItemChanagePropertyResult getItemChanageProperty(DItemBase prop) {
		RefreshPlayerPropertyChange itemData = new RefreshPlayerPropertyChange();
		GetItemChanagePropertyResult data = new GetItemChanagePropertyResult(itemData, false);

		if (prop.itemSecondType == Const.ItemSecondType.hpot.getValue()) {
			itemData.changeType = Const.PropertyChangeType.HP.value;
		}
		// else if(item.prop.itemSecondType == Const.ItemSecondType.mpot.getValue()){
		// itemData.changeType = Const.PropertyChangeType.MP.value;
		// }
		// else if(item.prop.itemSecondType == Const.ItemSecondType.pthpot.getValue()){
		// itemData.changeType = Const.PropertyChangeType.HP.value;
		// data.bPet = true;
		// }
		// else if(item.prop.itemSecondType == Const.ItemSecondType.ptmpot.getValue()){
		// itemData.changeType = Const.PropertyChangeType.MP.value;
		// data.bPet = true;
		// }
		// else if(item.prop.itemSecondType == Const.ItemSecondType.ptrpot.getValue()){
		// itemData.changeType = Const.PropertyChangeType.HPAndMP.value;
		// data.bPet = true;
		// }
		else {
			itemData.changeType = Const.PropertyChangeType.HPAndMP.value;
		}

		int format = AttributeUtil.getFormatByName(prop.prop);
		itemData.valueType = format;

		itemData.value = prop.min;
		itemData.duration = prop.par;
		if (prop.par > 0) {
			itemData.timestamp = System.currentTimeMillis() + prop.par; // 恢复型药剂的到期时间
		} else {
			itemData.timestamp = 0;
		}

		data.itemData = itemData;

		return data;
	};

	public static String getColorItemNameByQcolor(int itemQcolor, String itemName) {
		if (itemQcolor == Const.ItemQuality.WHITE.getValue()) {
			itemName = LangService.getValue("ITEM_QCOLOR_NAME_WHITE").replace("{itemName}", itemName);
		} else if (itemQcolor == Const.ItemQuality.GREEN.getValue()) {
			itemName = LangService.getValue("ITEM_QCOLOR_NAME_GREEN").replace("{itemName}", itemName);
		} else if (itemQcolor == Const.ItemQuality.BLUE.getValue()) {
			itemName = LangService.getValue("ITEM_QCOLOR_NAME_BLUE").replace("{itemName}", itemName);
		} else if (itemQcolor == Const.ItemQuality.PURPLE.getValue()) {
			itemName = LangService.getValue("ITEM_QCOLOR_NAME_PURPLE").replace("{itemName}", itemName);
		} else if (itemQcolor == Const.ItemQuality.ORANGE.getValue()) {
			itemName = LangService.getValue("ITEM_QCOLOR_NAME_ORANGE").replace("{itemName}", itemName);
		} else if (itemQcolor == Const.ItemQuality.RED.getValue()) {
			itemName = LangService.getValue("ITEM_QCOLOR_NAME_RED").replace("{itemName}", itemName);
		}
		return itemName;
	}

	public static int getEquipScoreRatio(String code) {
		DItemEquipBase prop = getPropByCode(code);
		if (prop == null) {
			Out.error("there is no prop for Equip ", code);
			return 0;
		}
		if (!ItemUtil.isEquipByItemType(prop.itemType)) {
			return 0;
		}

		return 0;// getEquipScore(((DEquipBase) prop).baseAtts); TODO 评分修改为随机属性计算
	}

	public static int getEquipScore(Map<String, Integer> attrs) {
		int score = 0;
		for (Map.Entry<String, Integer> node : attrs.entrySet()) {
			score += (int) (AttributeUtil.getScoreRatioByKey(node.getKey()) * Math.abs(node.getValue()) );
		}
		return score;
	}

	//
	// public void getEquipScoreRatio (code){
	// var prop = getPropByCode(code);
	// if(!prop) {
	// FSLog.error(ItemUtil.class, "there is no prop for Equip " + code);
	// return 0;
	// }
	// if(!isEquipByItemType(prop.itemType)) {
	// return 0;
	// }
	//
	// return Equipment.getEquipScore(prop.baseAtts);
	// }
	//
	public static int getMaxStrengthLevel() {
		// TODO return Equipment.getMaxStrengthLevel();
		return 0;
	}
	// public void getMaxStrengthLevel (){
	// return Equipment.getMaxStrengthLevel();
	// }

	public static ArrayList<SimpleItemInfo> parseString(String itemCode) {
		ArrayList<SimpleItemInfo> simpleItemInfos = new ArrayList<>();
		if (StringUtil.isEmpty(itemCode)) {
			return simpleItemInfos;
		}
		String[] rewards = itemCode.split(";");
		for (String ss : rewards) {
			String[] rw = ss.split(":");
			if (rw.length == 2) {
				SimpleItemInfo item = new SimpleItemInfo();
				item.itemCode = rw[0];
				item.itemNum = Integer.parseInt(rw[1]);
				item.forceType = Const.ForceType.BIND;
				simpleItemInfos.add(item);
			}
		}
		return simpleItemInfos;
	}

	public static Map<String, Integer> parseString2Map(String itemCode) {
		if (StringUtils.isEmpty(itemCode)) {
			return Collections.emptyMap();
		}

		Map<String, Integer> awards = new HashMap<>();
		String[] items = StringUtils.split(itemCode, ";");
		for (String item : items) {
			String[] codenum = item.split(":");
			awards.put(codenum[0], Integer.parseInt(codenum[1]));
		}
		return awards;
	}
}