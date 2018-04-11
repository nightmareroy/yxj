package com.wanniu.game.equip;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.wanniu.core.common.IntIntPair;
import com.wanniu.core.logfs.Out;
import com.wanniu.core.util.DateUtil;
import com.wanniu.core.util.RandomUtil;
import com.wanniu.core.util.StringUtil;
import com.wanniu.game.GWorld;
import com.wanniu.game.common.Const;
import com.wanniu.game.common.Const.AffixType;
import com.wanniu.game.common.Const.EquipType;
import com.wanniu.game.common.Const.GOODS_CHANGE_TYPE;
import com.wanniu.game.common.Const.TaskType;
import com.wanniu.game.common.ConstsTR;
import com.wanniu.game.common.Utils;
import com.wanniu.game.common.msg.WNNotifyManager;
import com.wanniu.game.data.EquipSockCO;
import com.wanniu.game.data.GameData;
import com.wanniu.game.data.base.DEquipBase;
import com.wanniu.game.data.base.DItemBase;
import com.wanniu.game.data.base.FourProp;
import com.wanniu.game.data.ext.AffixExt;
import com.wanniu.game.data.ext.EnchantBonusExt;
import com.wanniu.game.data.ext.EnchantExt;
import com.wanniu.game.data.ext.EquipMakeExt;
import com.wanniu.game.data.ext.ReBornExt;
import com.wanniu.game.data.ext.ReBuildExt;
import com.wanniu.game.data.ext.RefineExt;
import com.wanniu.game.data.ext.SeniorReBuildExt;
import com.wanniu.game.data.ext.SmritiExt;
import com.wanniu.game.data.ext.SuitListExt;
import com.wanniu.game.equip.RepeatKeyMap.Pair;
import com.wanniu.game.item.ItemConfig;
import com.wanniu.game.item.ItemUtil;
import com.wanniu.game.item.NormalItem;
import com.wanniu.game.item.data.AttsObj;
import com.wanniu.game.item.po.PlayerItemPO;
import com.wanniu.game.player.AttributeUtil;
import com.wanniu.game.player.BILogService;
import com.wanniu.game.player.GlobalConfig;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.poes.PlayerBasePO.EquipStrengthPos;
import com.wanniu.game.poes.RedPointPO;
import com.wanniu.game.rank.RankType;
import com.wanniu.game.sevengoal.SevenGoalManager.SevenGoalTaskType;
import com.wanniu.game.task.TaskUtils;
import com.wanniu.redis.PlayerPOManager;

import pomelo.Common.AttributeBase;
import pomelo.area.PlayerHandler.SuperScriptPush;
import pomelo.area.PlayerHandler.SuperScriptType;
import pomelo.item.ItemOuterClass.EquipGridStrengthInfo;
import pomelo.item.ItemOuterClass.Grid;
import pomelo.item.ItemOuterClass.ItemDetail;
import pomelo.player.PlayerOuterClass.Equipments;

/**
 * 装备
 * 
 * @author Yangzz
 *
 */
public class EquipManager {

	public WNPlayer player;

	public Map<Integer, NormalEquip> equips;

	public Map<Integer, EquipStrengthPos> strengthPos;

	/** 当前强化等级下的 effectType */
	public int effectTypeId;

	/** avatar 是否刷新 */
	public boolean isEffectTypeFlush;

	/** 最大强化等级 */
	public int maxStrengthLevel;

	/** 红点状态缓存 */
	public Map<Integer, Integer> redpoints;

	public EquipManager(WNPlayer player, Map<Integer, PlayerItemPO> equipDatas, Map<Integer, EquipStrengthPos> strengthPos) { // , List<BagGrid>
																																// equipData
		this.player = player;
		this.strengthPos = strengthPos;
		this.redpoints = new ConcurrentHashMap<>();

		_init(equipDatas);
	}

	private void _init(Map<Integer, PlayerItemPO> equipDatas) {
		equips = new ConcurrentHashMap<>();

		Iterator<Integer> keys = equipDatas.keySet().iterator();
		while (keys.hasNext()) {
			int gridIndex = keys.next();
			if (equipDatas.get(gridIndex) == null)
				continue;
			NormalEquip equip = (NormalEquip) ItemUtil.createItemByDbOpts(equipDatas.get(gridIndex));
			if (equip != null) {
				equips.put(gridIndex, equip);
			}
		}

		// this.strengthProp = this.getStrengthConfig(this.speData.enLevel);
		// //当前强化等级下的 effectType avatar展示使用
		// this.effectTypeId = EquipUtil.getEffectTypeId(this.speData.enLevel);
		// //avatar 是否刷新
		// this.isEffectTypeFlush = false;
		// 最大强化等级
		this.maxStrengthLevel = EquipCraftConfig.getInstance().getMaxStrengthLevel();

	}

	/**
	 * 根据装备索引获取装备
	 */
	public NormalEquip getEquipment(int pos) {
		return this.equips.get(pos);
	};

	/**
	 * 根据装备 Id查找装备
	 */
	public int getEquipmentById(String id) {
		for (int pos : this.equips.keySet()) {
			NormalEquip equip = getEquipment(pos);
			if (equip != null && equip.itemDb.id.equals(id)) {
				return pos;
			}
		}
		return 0;
	};

	public NormalEquip getEquipById(String id) {
		for (int pos : this.equips.keySet()) {
			NormalEquip equip = this.equips.get(pos);
			if (equip.itemDb.id.equals(id)) {
				return equip;
			}
		}
		return null;
	};

	public List<SmritiExt> findSmritiCO(int lv) {
		List<SmritiExt> list = GameData.findSmritis((t) -> {
			return t.level == lv;
		});
		return list;
	}

	/**
	 * 获取单个格子 协议信息
	 */
	public EquipGridStrengthInfo getStrenghInfo(int pos) {
		EquipStrengthPos info = strengthPos.get(pos);
		EquipGridStrengthInfo.Builder data = EquipGridStrengthInfo.newBuilder();
		data.setPos(pos);
		data.setEnSection(info.enSection);
		data.setEnLevel(info.enLevel);
		data.addAllJewelAtts(EquipUtil.toJson4Gem(info));
		data.setSocks(info.socks);
		return data.build();
	}

	/**
	 * 装备格子强化等级信息
	 */
	public List<EquipGridStrengthInfo> toJson4StrengthPos() {
		List<EquipGridStrengthInfo> list = new ArrayList<>();
		if (strengthPos == null)
			return list;
		for (int pos : strengthPos.keySet()) {
			list.add(getStrenghInfo(pos));
		}
		return list;
	}

	public Equipments toJson4Payload() {
		Equipments.Builder data = Equipments.newBuilder();
		List<Grid> equips = new ArrayList<>();
		for (int pos : this.equips.keySet()) {
			equips.add(this.getEquip4PayLoad(pos));
		}
		data.addAllEquips(equips);
		return data.build();
	};

	public List<ItemDetail> getAllEquipDetails4PayLoad() {
		List<ItemDetail> data = new ArrayList<>();
		for (NormalEquip equip : this.equips.values()) {
			data.add(equip.getItemDetail(player.playerBasePO).build());
		}
		return data;
	};

	public Grid getEquip4PayLoad(int pos) {
		Grid.Builder equipGrid = Grid.newBuilder();
		equipGrid.setGridIndex(pos);
		NormalItem equip = this.equips.get(pos);
		if (equip != null) {
			equipGrid.setItem(equip.toJSON4GridPayload());
		}
		return equipGrid.build();
	};

	/**
	 * 判断是否是avatar部位 * @param pos
	 * 
	 * @returns {boolean}
	 */
	public boolean isAvatarPart(int pos) {
		boolean mainBool = (pos == Const.EquipType.MAIN_HAND.getValue());
		boolean clothBool = (pos == Const.EquipType.CLOTH.getValue());
		return mainBool || clothBool;
	}

	/**
	 * 穿装备
	 * 
	 * @param equipment
	 * @returns {boolean}
	 */
	public boolean equip(NormalEquip equipment) {
		if (equipment != null) {
			NormalEquip oldequip = this.equips.get(equipment.getPosition());

			this.equips.put(equipment.getPosition(), equipment);
			// 检测绑定
			if (equipment.getBind() == 2) {
				equipment.setBind(1);
				this._updateAndPush(new int[] { equipment.getPosition() }, true);
			} else {
				this._updateAndPush(new int[] { equipment.getPosition() }, false);
			}

			if (this.isAvatarPart(equipment.getPosition()) && StringUtil.isNotEmpty(equipment.prop.avatarId)) {
				if (oldequip == null) {
					if (equipment.getPosition() == Const.EquipType.CLOTH.getValue()) {
						if (!this.player.basicProp.model.equals(String.valueOf(equipment.prop.avatarId))) {
							this.player.refreshBattlerServerAvatar();
						}
					} else if (equipment.getPosition() == Const.EquipType.MAIN_HAND.getValue()) {
						if (!this.player.basicProp.weaponmodel.equals(String.valueOf(equipment.prop.avatarId))) {
							this.player.refreshBattlerServerAvatar();
						}
					}
				} else {
					if (oldequip.prop.avatarId != equipment.prop.avatarId) {
						this.player.refreshBattlerServerAvatar();
					}

				}
			}
			this.equipSync(GOODS_CHANGE_TYPE.equip);

			EquipStrengthPos strengthLevel = strengthPos.get(equipment.getPosition());
			if (strengthLevel.enSection > 0 || strengthLevel.enLevel > 0) {
				// 强化等级修改为 【强化段位*最大等级 + 等级】
				this.player.taskManager.dealTaskEvent(TaskType.TRAIN_EQUIP, String.valueOf(equipment.getPosition()), strengthLevel.enSection * (GlobalConfig.EquipmentCraft_Enchant_MaxenLevel + 1) + strengthLevel.enLevel);
				TaskUtils.dealTrainEquipAllTask(player);
			}
			this.player.taskManager.dealTaskEvent(TaskType.TAKE_EQUIP_Qt, equipment.getQLevel() + "|" + equipment.getQColor() + "|" + equipment.getPosition(), 1);

			// BI
			this.ansycReportEquipChange(equipment, 1, equipment.getPosition());

			return true;
		}
		return false;
	};

	// 1=穿, 2=脱
	private void ansycReportEquipChange(NormalEquip equip, int type, int position) {
		// 不是套装就不要上报了...
		if (equip.getQColor() < 4) {
			return;
		}
		try {
			Map<String, Object> data = new HashMap<>();
			data.put("equipCode", equip.prop.code);
			data.put("equipName", equip.prop.name);

			for (SuitListExt config : GameData.SuitLists.values()) {
				if (config.isValid == 0) {
					continue;
				}
				if (player.getLevel() < config.level) {
					continue;
				}

				if (config.partCodes.contains(equip.prop.code)) {
					data.put("suitID", config.suitID);
					data.put("suitName", config.suitName);
					break;
				}
			}

			BILogService.getInstance().ansycReportEquipChange(player.getPlayer(), type, position, data);
		} catch (Exception e) {
			Out.warn("ansycReportEquipChange", e);
		}
	}

	/**
	 * 脱装备
	 * 
	 * @param gridIndex
	 * @returns {boolean}
	 */
	public boolean unEquip(int position) {
		if (this.equips.size() == 0)
			return false;

		NormalEquip oldequip = this.equips.get(position);

		this.equips.remove(position);

		this._updateAndPush(new int[] { position }, false);
		if (this.isAvatarPart(position)) {
			DEquipBase prop = oldequip.prop;
			if (oldequip.getPosition() == Const.EquipType.CLOTH.getValue()) {
				if (!this.player.basicProp.model.equals(String.valueOf(prop.avatarId))) {
					this.player.refreshBattlerServerAvatar();
				}
			} else if (oldequip.getPosition() == Const.EquipType.MAIN_HAND.getValue()) {
				if (!this.player.basicProp.weaponmodel.equals(String.valueOf(prop.avatarId))) {
					this.player.refreshBattlerServerAvatar();
				}
			}
		}
		this.equipSync();

		// BI
		this.ansycReportEquipChange(oldequip, 2, oldequip.getPosition());

		return true;
	};

	/**
	 * 从人物身上掉落装备
	 * 
	 * @param position
	 * @returns {boolean}
	 * @constructor
	 */
	public boolean DropEquipAndReturn(int position) {
		if (this.unEquip(position)) {
			return true;
		}
		return false;
	};

	/**
	 * 镶嵌宝石
	 * 
	 * @param pos
	 * @param index
	 * @param code
	 * @returns {boolean}
	 */
	public boolean fillGem(int pos, int[] index, String code, WNPlayer player) {
		EquipStrengthPos posInfo = this.strengthPos.get(pos);
		if (posInfo != null) {
			if (EquipUtil.fillGem(posInfo, index, code, player)) {
				this._updateAndPushPOS(new int[] { pos }, true);

				this.equipSync();
				this.player.getPlayerTasks().dealTaskEvent(TaskType.FILL_GEM, code, 1);

				updateGemLevelRank();
				// 成就
				// player.achievementManager.onFillGem(1);
				player.achievementManager.onGemFillTotalLevel();
				// 红点
				updateFillGemScript(null);
				return true;
			}
		}
		return false;
	};

	public void updateGemLevelRank() {
		int level = 0;
		for (EquipStrengthPos pos : player.equipManager.strengthPos.values()) {
			for (String code : pos.gems.values()) {
				if (StringUtil.isEmpty(code))
					continue;
				DItemBase prop = ItemUtil.getUnEquipPropByCode(code);
				level += prop.levelReq;
			}
		}

		player.rankManager.onEvent(RankType.GemLevel, level);
	}

	/**
	 * 卸载单个宝石
	 * 
	 * @param pos
	 * @param index
	 * @param code
	 * @returns {boolean}
	 */
	public boolean unfillGem(int pos, int index) {
		EquipStrengthPos posInfo = this.strengthPos.get(pos);
		if (posInfo != null) {
			String code = posInfo.gems.get(index);
			if (StringUtil.isEmpty(code)) {
				return false;
			}

			posInfo.gems.remove(index);

			NormalItem item = ItemUtil.createItemsByItemCode(code, 1).get(0);
			player.bag.addEntityItem(item, Const.GOODS_CHANGE_TYPE.equipmosaic, null, false, false);

			this._updateAndPushPOS(new int[] { pos }, true);
			this.equipSync();
			// 红点 在加物品里面调用了
			// updateFillGemScript(code);
			updateGemLevelRank();
			return true;
		}
		return false;
	};

	public void _updateAndPushPOS(int[] gridIndexs, boolean refresh) {
		this.update();
		WNNotifyManager.getInstance().pushEquipmentPOS(player, gridIndexs, refresh);
		WNNotifyManager.getInstance().pushEquipmentDynamic(player, gridIndexs, refresh);
	}

	public void _updateAndPush(int[] gridIndexs, boolean refresh) {
		_updateAndPush(gridIndexs, refresh, true);
	};

	/**
	 * @param isDressed: 是否是穿戴在身上的
	 */
	public void _updateAndPush(int[] gridIndexs, boolean refresh, boolean isDressed) {
		this.update();
		if (isDressed) {
			WNNotifyManager.getInstance().pushEquipmentDynamic(player, gridIndexs, refresh);
		} else {
			List<Integer> list = new ArrayList<>();
			for (int pos : gridIndexs) {
				list.add(pos);
			}
			WNNotifyManager.getInstance().pushBagItemDynamic(player, list, refresh);
		}
	}

	public Map<String, Integer> calAllInfluence() {
		Map<String, Integer> data = new ConcurrentHashMap<>();
		for (int position : this.strengthPos.keySet()) {
			// 装备相关
			NormalEquip equip = this.getEquipment(position);

			// 基础属性，扩展属性，独有属性
			if (equip != null) {
				if (equip.speData.extAtts == null) { // 固定属性装备
					Utils.deepCopy(data, equip.prop.fixedAtts);
				} else {
					Utils.deepCopyAffix(data, equip.speData.extAtts, equip.getQColor());
				}
				if (equip.speData.extAttsAdd != null) {// 重铸带来的额外属性
					Utils.deepCopy(data, equip.speData.extAttsAdd);
				}

				Utils.deepCopyAffix(data, equip.speData.legendAtts, equip.getQColor());
			}
			// 格子相关
			EquipStrengthPos strengthInfo = strengthPos.get(position);
			if (strengthInfo == null || (strengthInfo.enSection == 0 && strengthInfo.enLevel == 0)) {
				if (equip != null) {
					Utils.deepCopy(data, equip.speData.baseAtts);
				}
			} else {
				// 强化属性
				if (equip != null) {
					EnchantExt enchantExt = GameData.Enchants.get(strengthInfo.enSection * 100 + strengthInfo.enLevel);
					if (equip.speData.baseAtts != null && enchantExt != null) {
						for (String key : equip.speData.baseAtts.keySet()) {
							int value = equip.speData.baseAtts.get(key);
							value = value * (10000 + enchantExt.propPer) / 10000;
							if (data.containsKey(key)) {
								data.put(key, data.get(key) + value);
							} else {
								data.put(key, value);
							}
						}
					}
				}

				// 加上强化段位额外 奖励(没有装备也需要)
				if (strengthInfo.enSection > 0) {
					for (int section = 1; section <= strengthInfo.enSection; section++) {
						for (EnchantBonusExt enchantBonusCO : GameData.EnchantBonuss.values()) {
							if (enchantBonusCO._type == position && enchantBonusCO.enClass == section) {
								if (data.containsKey(enchantBonusCO._prop)) {
									data.put(enchantBonusCO._prop, data.get(enchantBonusCO._prop) + enchantBonusCO.min);
								} else {
									data.put(enchantBonusCO._prop, enchantBonusCO.min);
								}
								break;
							}
						}
					}
				}
			}
		}

		// 宝石属性
		for (int position : this.strengthPos.keySet()) {
			EquipStrengthPos strengthInfo = this.strengthPos.get(position);
			for (int index : strengthInfo.gems.keySet()) {
				String code = strengthInfo.gems.get(index);
				DItemBase prop = (DItemBase) ItemConfig.getInstance().getItemProp(code);
				String key = AttributeUtil.getKeyByName(prop.prop);
				if (StringUtil.isEmpty(key)) {
					Out.error(EquipUtil.class, "Equip fillGem config error code ", code, ",index=", index);
					continue;
				}

				String gemProp = AttributeUtil.getKeyByName(prop.prop);
				int gemValue = prop.min;
				if (data.containsKey(gemProp)) {
					data.put(gemProp, data.get(gemProp) + gemValue);
				} else {
					data.put(gemProp, gemValue);
				}
			}
		}

		// 套装加成属性
		List<IntIntPair> list_suits = EquipCraftConfig.getInstance().getValidSuits(player);
		Map<String, Integer> suitAttr = EquipCraftConfig.getInstance().getSuitAtts(list_suits);
		Utils.deepCopy(data, suitAttr);

		return data;
	};

	public void sendNotice(WNPlayer player, NormalEquip equip) {
		// chenshaozhi(陈绍治) 03-14 10:04:47
		// 这个先去掉吧，先不做广播
		// yangzhuzhi(杨助志) 03-14 10:05:08
		// OK
		// chenshaozhi(陈绍治) 03-14 10:04:58
		// 这里有问题，颜色判定已经改了
		// int needLevel = GlobalConfig.Equipment_Speaker_StrengthenLevel;
		// IntIntPair strengthLevel = strengthPos.get(equip.getPosition());
		// if (strengthLevel >= needLevel) {
		// int rand = Utils.random(1, 3);
		// String content = LangService.getValue("EQUIPMENT_STRENGTHEN_SPEAKER"
		// + rand);
		// content = content.replace("{playerName}",
		// MessageUtil.getPlayerNameColor(player.getName(), player.getPro()));
		// content = content.replace("{EquipmentName}",
		// MessageUtil.itemColorName(equip.getQColor(), equip.getName()));
		// content = content.replace("{EquipmentLevel}",
		// String.valueOf(strengthLevel));
		// try {
		// // TODO chatRemote
		// // pomelo.app.rpc.chat.chatRemote.sendWorldContent({},
		// // player.logicServerId,
		// // content, Const.CHAT_SCOPE.SYSTEM, function(code){
		// // Out.debug(getClass(), "interact.sendMessage " + code);
		// // });
		// } catch (Exception e) {
		// FSLog.error(getClass(), e);
		// }
		// }
	};

	/**
	 * 强化后置数据获取
	 * 
	 * @return {res: true, succPer: 0};
	 */
	public Object[] equipStrengthen(int pos) {
		NormalEquip equip = this.getEquipment(pos);
		Object[] result = new Object[] { true, 0 };
		// if (equip == null) {
		// result[0] = false;
		// return result;
		// }
		// result = equip.strength();
		// 装备等级修改为提升 格子等级
		EquipStrengthPos pair = strengthPos.get(pos);
		// if(pair.enSection<GlobalConfig.EquipmentCraft_Enchant_MaxEnClass) {
		// if (pair.enLevel == GlobalConfig.EquipmentCraft_Enchant_MaxenLevel) {
		// pair.enSection += 1;
		// pair.enLevel = 0;
		// } else {
		// pair.enLevel += 1;
		// }
		// }

		EnchantExt enchantExt = EquipUtil.getStrengthConfig(pair.enSection, pair.enLevel);
		if (enchantExt == null) {
			Out.error("无法获取强化配置！");
			return result;
		}

		// 这是一种老的等级计算方案，只是为了BI上报兼容
		int oldLevel = pair.enSection * 100 + pair.enLevel;

		pair.enSection = enchantExt.enClass;
		pair.enLevel = enchantExt.enLevel;
		Out.info("强化部位 playerId=", player.getId(), ",pos=", pos, ",level=", pair.enSection * 10 + pair.enLevel);

		// 获得奖励属性[excel配置了minValue,maxValue, csz说取最小值]

		// 是否刷新 强化后外形效果
		int tmpEffectTypeId = EquipUtil.getEffectTypeId(pair.enSection, pair.enLevel);
		if (this.effectTypeId != tmpEffectTypeId) {
			this.isEffectTypeFlush = true;
		} else {
			this.isEffectTypeFlush = false;
		}
		this.effectTypeId = tmpEffectTypeId;

		if ((boolean) result[0]) {
			sendNotice(this.player, equip);
			// 强化成功
			this._updateAndPushPOS(new int[] { pos }, true);

			if (this.isAvatarPart(pos)) {
				if (isEffectTypeFlush) {
					this.player.refreshBattlerServerAvatar();
				}
			}
			this.equipSync();
			int newLevel = pair.enSection * (GlobalConfig.EquipmentCraft_Enchant_MaxenLevel + 1) + pair.enLevel;
			this.player.taskManager.dealTaskEvent(Const.TaskType.TRAIN_EQUIP, String.valueOf(pos), newLevel);
			TaskUtils.dealTrainEquipAllTask(player);
			this.player.achievementManager.equipEnhance(pos, newLevel);
			this.player.achievementManager.onEquipPosStrengthLevel(pos, newLevel);
			// 刷新红点
			updateStrengthScript(null);

			player.sevenGoalManager.processGoal(SevenGoalTaskType.EQUIP_STRENTHEN_COUNT, getTotalStrenthenLv());

			BILogService.getInstance().ansycReportStrengthenCultivate(player.getPlayer(), pos, oldLevel, pair.enSection * 100 + pair.enLevel, enchantExt.mates);
			return result;
		}

		return result;
	};

	private void equipSync() {
		this.equipSync(GOODS_CHANGE_TYPE.def);
	}

	/**
	 * 由装备的穿脱的属性变化以及处理
	 */
	private void equipSync(GOODS_CHANGE_TYPE from) {
		this.player.btlDataManager.data_equip = calAllInfluence();
		this.player.onEquipChange(from);
	};

	/**
	 * 装备的武器的数量
	 * 
	 * @returns {number}
	 */
	public int weaponNum() {
		int num = 0;
		if (this.equips.get(Const.EquipType.MAIN_HAND.getValue()) != null) {
			num++;
		}

		// if (this.equips.get(Const.EquipType.enLevel_HAND.getValue()) != null)
		// {
		// num++;
		// }

		return num;
	};

	/**
	 * 是否有双手武器
	 * 
	 * @returns {boolean}
	 */
	public boolean haveBothHandWeapon() {
		NormalEquip mainH = this.equips.get(Const.EquipType.MAIN_HAND.getValue());
		if (mainH != null && mainH.prop.isBothHand == 1) {
			return true;
		}

		// NormalEquip secondH =
		// this.equips.get(Const.EquipType.enLevel_HAND.getValue());
		// if (secondH != null && secondH.prop.isBothHand == 1) {
		// return true;
		// }

		return false;
	};

	/**
	 * 是否有单手武器
	 * 
	 * @returns {boolean}
	 */
	public boolean haveSingleHandWeapon() {
		NormalEquip mainH = this.equips.get(Const.EquipType.MAIN_HAND.getValue());
		if (mainH != null && mainH.prop.isBothHand == 0) {
			return true;
		}

		// NormalEquip secondH =
		// this.equips.get(Const.EquipType.enLevel_HAND.getValue());
		// if (secondH != null && secondH.prop.isBothHand == 0) {
		// return true;
		// }

		return false;
	};

	// public void getProCount(pro){
	// var count = 0;
	// for(var pos in this.equips){
	// var equip = this.equips[pos];
	// if(equip.getPro() == pro){
	// count++;
	// }
	// }
	// return count;
	// };
	//
	// /**
	// * gmt 专用接口
	// */
	// public void gmGetAllEquipDetail(){
	// var data = [];
	// for(var pos in this.equips){
	// var equip = this.equips[pos];
	// //data.push(equip.getGmItemDetail());
	// var itemData = {};
	// itemData.prop_id = equip.code;
	// itemData.prop_name = equip.getName();
	// itemData.is_wear = "1";
	// itemData.prop_num = equip.groupCount;
	// itemData.pos = pos;
	// itemData.prop_uid = equip.id;
	// itemData.prop_info = equip.gmtGetInfluence();
	// data.push(itemData);
	// }
	//
	// logger.info("gmGetAllEquipDetail", data);
	// return data;
	// };
	//
	// /**
	// * gm 删除装备
	// * @param id
	// */
	// public void gmDeleteEquip(pos, id) {
	// logger.info("gmDeleteEquip:", id);
	// var equip = this.equips[pos];
	// if(equip.id == id){
	// if (this.unEquip(pos)){
	// return true;
	// }
	// }
	// return false;
	// };
	//

	public static class ResultEquipNumAndQt {
		public boolean isEnoughNum;
		public int value;
	}

	public ResultEquipNumAndQt equipNumAndQt(int num, int qt) {
		ResultEquipNumAndQt result = new ResultEquipNumAndQt();
		result.isEnoughNum = false;
		result.value = 0;
		int equpNum = 0;
		for (int pos : this.equips.keySet()) {
			NormalEquip equip = this.equips.get(pos);
			if (qt != 0) {
				if (equip.getQColor() >= qt) {
					equpNum++;
				}
			} else {
				equpNum++;
			}
		}
		if (equpNum >= num) {
			result.isEnoughNum = true;
		}
		result.value = equpNum;
		return result;
	};

	public static class EquipAndLevelData {
		// /**最高强化段位*/
		// public int maxSection;
		/** 最高强化等级 */
		public int maxLevel;

		public Map<Integer, EquipStrengthPos> equips;
	}

	public EquipAndLevelData getAllEquipAndLevel() {
		EquipAndLevelData arr = new EquipAndLevelData();
		arr.equips = new HashMap<>();
		arr.maxLevel = 0;
		for (Map.Entry<Integer, EquipStrengthPos> node : this.strengthPos.entrySet()) {
			EquipStrengthPos sectionAndLevel = node.getValue();
			arr.equips.put(node.getKey(), node.getValue());

			int maxLevel = sectionAndLevel.enSection * (GlobalConfig.EquipmentCraft_Enchant_MaxenLevel + 1) + sectionAndLevel.enLevel;
			if (arr.maxLevel < maxLevel) {
				arr.maxLevel = maxLevel;
			}
		}
		return arr;
	}

	/**
	 * 所有的格子强化达到指定等级的数量
	 */
	public int hasAllStrenghLevel(int lv) {
		int validNum = 0;
		for (int i = 1; i <= Const.EquipType.CHARM.getValue(); ++i) {
			EquipStrengthPos pair = this.strengthPos.get(i);
			if (pair == null) {
				continue;
			}
			if (pair.enLevel >= lv) {
				validNum++;
			}
		}
		return validNum;
	}

	/**
	 * 洗练 从脚本中重新生成3个基础属性的值，这些属性的名称是固定的，不会变，就改变它的随机值
	 */
	public void reborn(NormalEquip equip, int pos, boolean isDressed) {
		if (equip.itemDb.isNew == 1) {
			equip.itemDb.isNew = 0;
		}
		DEquipBase finalProp = equip.prop;
		if (StringUtil.isNotEmpty(equip.prop.baseCode)) {
			finalProp = ItemConfig.getInstance().getEquipProp(equip.prop.baseCode);
		}
		// 3条主属性
		equip.speData.tempBaseAtts = new HashMap<>();
		for (String attrName : finalProp.baseAtts.keySet()) {
			FourProp pair = finalProp.baseAtts.get(attrName);
			if (pair == null) {
				continue;
			}
			equip.speData.tempBaseAtts.put(pair.prop, RandomUtil.getInt(pair.min, pair.max));
		}
		if (isDressed) {
			_updateAndPush(new int[] { pos }, true);
			// equipSync();
		} else {
			_updateAndPush(new int[] { pos }, true, false);
		}

		// 更新任务状态
		this.player.taskManager.dealTaskEvent(TaskType.EQUIP_REBORN, "", 1);
		// 成就
		this.player.achievementManager.onEquipReborn();
	}

	/**
	 * 保存洗练属性
	 */
	public void saveReborn(NormalEquip equip, int pos, boolean isDressed) {
		if (equip.speData.tempBaseAtts == null) {
			return;
		}
		if (equip.itemDb.isNew == 1) {
			equip.itemDb.isNew = 0;
		}
		equip.speData.baseAtts = equip.speData.tempBaseAtts;
		equip.speData.tempBaseAtts = null;

		if (isDressed) {
			_updateAndPush(new int[] { pos }, true);
			equipSync(GOODS_CHANGE_TYPE.saveReborn);
		} else {
			_updateAndPush(new int[] { pos }, true, false);
		}
	}

	private boolean contain(List<Integer> val, int index) {
		for (int i : val) {
			if (i == index) {
				return true;
			}
		}

		return false;
	}

	/**
	 * 检查重铸锁定的条目是否合法
	 * 
	 * @param lockIndexs
	 * @param attrSize
	 * @return
	 */
	public boolean checkRebuildLocks(List<Integer> lockIndexs, int attrSize) {
		int maxLockSize = this.getCanLockNum(attrSize);

		if (maxLockSize < 0 || lockIndexs.size() > maxLockSize) {
			return false;
		}

		for (int lockIndex : lockIndexs) {
			if (lockIndex < 1 || lockIndex > attrSize) {// 客户端的下标从1开始
				return false;
			}
		}

		return true;
	}

	/**
	 * 根据装备扩展属性的词条数目返回最多能重铸锁定的条目数
	 * 
	 * @param attrSize
	 * @return
	 */
	private int getCanLockNum(int attrSize) {
		int maxLockSize = 0;

		switch (attrSize) {
		case 3:
			maxLockSize = GlobalConfig.Equipment_ReBuild_MaxNumtoLock3;
			break;
		case 4:
			maxLockSize = GlobalConfig.Equipment_ReBuild_MaxNumtoLock4;
			break;
		case 5:
			maxLockSize = GlobalConfig.Equipment_ReBuild_MaxNumtoLock5;
			break;
		}

		return maxLockSize;
	}

	/**
	 * 重铸 重铸就是把所有扩展随机属性重新生成，完全等同于重新执行一遍上文提到的【生成物品属性】的第3步开始的以下步骤
	 */
	public void rebuild(NormalEquip equip, int pos, boolean isDressed, List<Integer> lockedAttIdList) {
		if (equip.itemDb.isNew == 1) {
			equip.itemDb.isNew = 0;
		}
		equip.speData.tempExtAtts = new RepeatKeyMap<>();
		equip.speData.tempExtAttsAdd = null;

		int index = 1;
		// 临时属性中保存已锁定的属性
		for (RepeatKeyMap.Pair<Integer, Integer> rp : equip.speData.extAtts.entrySet()) {
			if (contain(lockedAttIdList, index)) {// 有锁定的话就copy保存下来，没锁定的就创建一个key，value都为0的Pair
				equip.speData.tempExtAtts.put(rp.k, rp.v);
			} else {
				equip.speData.tempExtAtts.put(new RepeatKeyMap.Pair<Integer, Integer>(0, 0));
			}
			index++;
		}

		DEquipBase finalProp = equip.prop;
		if (StringUtil.isNotEmpty(equip.prop.baseCode)) {
			finalProp = ItemConfig.getInstance().getEquipProp(equip.prop.baseCode);
		}

		ItemUtil.initRebuildExtAtts(equip.speData.tempExtAtts, finalProp, equip.speData.extAtts == null ? equip.prop.fixedAtts.size() : equip.speData.extAtts.size(), lockedAttIdList.size());
		Map<String, Integer> exarAttr = ItemUtil.getSameAttsExtAttributes(equip.speData.tempExtAtts, equip.prop.type, equip.prop.code);// 额外赠送的属性
		if (exarAttr != null) {
			equip.speData.tempExtAttsAdd = exarAttr;
		}

		if (isDressed) {
			_updateAndPush(new int[] { pos }, true);
		} else {
			_updateAndPush(new int[] { pos }, true, false);
		}

		// 更新任务状态
		this.player.taskManager.dealTaskEvent(TaskType.EQUIP_REBUILD, "", 1);
		this.player.achievementManager.onEquipRebuild();

	}

	/**
	 * 高级重铸 不更改物品属性，只刷新物品数值
	 */
	public void seniorRebuild(NormalEquip equip, int pos, boolean isDressed) {
		if (equip.itemDb.isNew == 1) {
			equip.itemDb.isNew = 0;
		}

		equip.speData.tempExtAtts_senior = new RepeatKeyMap<>();

		DEquipBase finalProp = equip.prop;
		if (StringUtil.isNotEmpty(equip.prop.baseCode)) {
			finalProp = ItemConfig.getInstance().getEquipProp(equip.prop.baseCode);
		}

		ItemUtil.seniorInitExtAtts(equip.speData.extAtts, equip.speData.tempExtAtts_senior, finalProp);

		if (isDressed) {
			_updateAndPush(new int[] { pos }, true);
			equipSync();
		} else {
			_updateAndPush(new int[] { pos }, true, false);
		}

		// 更新任务状态
		this.player.taskManager.dealTaskEvent(TaskType.EQUIP_REBUILD, "", 1);
		this.player.achievementManager.onEquipRebuild();
	}

	/**
	 * 保存重铸属性
	 */
	public void saveRebuild(NormalEquip equip, int pos, boolean isDressed) {
		if (equip.speData.tempExtAtts == null) {
			return;
		}
		if (equip.itemDb.isNew == 1) {
			equip.itemDb.isNew = 0;
		}
		equip.speData.extAtts = equip.speData.tempExtAtts;
		equip.speData.extAttsAdd = equip.speData.tempExtAttsAdd;
		equip.speData.tempExtAttsAdd = null;
		equip.speData.tempExtAtts = null;
		// 保存低级重铸时，需要把高级重铸的临时信息清除掉
		equip.speData.tempExtAtts_senior = null;

		// 重铸清除 选中的精炼属性
		equip.speData.tempUniqueAtts = null;

		if (isDressed) {
			_updateAndPush(new int[] { pos }, true);
			equipSync(GOODS_CHANGE_TYPE.saveReborn);
		} else {
			_updateAndPush(new int[] { pos }, true, false);
		}
	}

	public void smritiEquip(NormalEquip leftEquip, int leftPos, boolean leftIsDressed, NormalEquip rightEquip, int rightPos, boolean rightIsDressed) {
		rightEquip.speData.extAtts.clear();
		copyAtts(leftEquip, rightEquip, leftEquip.speData.extAtts, rightEquip.speData.extAtts);
		rightEquip.speData.extAttsAdd = leftEquip.speData.extAttsAdd;

		// 左边装备的重铸属性清空
		leftEquip.speData.extAtts.clear();
		leftEquip.speData.extAttsAdd = null;

		if (leftEquip.itemDb.isNew == 1) {
			leftEquip.itemDb.isNew = 0;
		}

		if (rightEquip.itemDb.isNew == 1) {
			rightEquip.itemDb.isNew = 0;
		}

		ItemUtil.initExtAtts(leftEquip.speData.extAtts, leftEquip.prop, 0, 0);

		_updateAndPush(new int[] { leftPos }, true, leftIsDressed);
		_updateAndPush(new int[] { rightPos }, true, rightIsDressed);

		if (leftIsDressed || rightIsDressed) {
			equipSync(GOODS_CHANGE_TYPE.smriti_equip);
		}
	}

	private void copyAtts(NormalEquip leftEquip, NormalEquip rightEquip, RepeatKeyMap<Integer, Integer> source, RepeatKeyMap<Integer, Integer> dist) {
		if (source != null && !source.isEmpty()) {
			List<Pair<Integer, Integer>> list = source.getValues();
			for (Pair<Integer, Integer> k : list) {
				AffixExt affix = GameData.Affixs.get(k.k);
				if (affix == null) {
					Out.warn("key=" + k.k + " val=" + k.v);
					continue;
				}
				AffixExt finalAffix = ItemUtil.getFixOneAtts(rightEquip.prop.tcLevel, rightEquip.prop.type, rightEquip.prop.pro, AffixType.normal, affix.attName);
				if (finalAffix == null) {
					dist.put(k.k, k.v);
					Out.error("smritiEquip can't find dist attr。src:" + leftEquip.getId() + "," + k.k + "," + k.v + " dist:" + rightEquip.getId());
					continue;
				}
				FourProp rdProp = finalAffix.props.get(rightEquip.prop.qcolor);
				if (rdProp == null) {
					dist.put(finalAffix.iD, k.v);
					Out.error("smritiEquip can't find dist qcolor。src:" + leftEquip.getId() + "," + k.k + "," + k.v + " dist:" + rightEquip.getId());
					continue;
				}

				int finalVal = k.v;
				if (finalVal > rdProp.max) {
					finalVal = rdProp.max;
					Out.error("smritiEquip exceed max value。src:" + leftEquip.getId() + "," + k.k + "," + k.v + " dist:" + rightEquip.getId() + "," + k.k + "," + rdProp.max);
				}
				int min = ItemUtil.calcRebuildPropMin(rdProp.min);
				if (finalVal < min) {
					finalVal = min;
					Out.info("smritiEquip less min value。src:" + leftEquip.getId() + "," + k.k + "," + k.v + " dist:" + rightEquip.getId() + "," + k.k + "," + min);
				}
				dist.put(finalAffix.iD, finalVal);
			}
		}
	}

	/**
	 * 保存高级重铸属性
	 */
	public void saveSeniorRebuild(NormalEquip equip, int pos, boolean isDressed) {
		if (equip.speData.tempExtAtts_senior == null) {
			return;
		}
		if (equip.itemDb.isNew == 1) {
			equip.itemDb.isNew = 0;
		}
		equip.speData.extAtts = equip.speData.tempExtAtts_senior;
		// 保存高级重铸时，保留低级重铸的临时信息，只清除高级重铸临时信息
		equip.speData.tempExtAtts_senior = null;

		// 重铸清除 选中的精炼属性
		equip.speData.tempUniqueAtts = null;

		if (isDressed) {
			_updateAndPush(new int[] { pos }, true);
			equipSync(GOODS_CHANGE_TYPE.saveReborn);
		} else {
			_updateAndPush(new int[] { pos }, true, false);
		}
	}

	/**
	 * 精炼
	 * 
	 * @param attrKey 选中的属性名字 重铸就是把所有扩展随机属性重新生成，完全等同于重新执行一遍上文提到的【生成物品属性】的第3步开始的以下步骤
	 */
	public void refineLegend(NormalEquip equip, int pos, boolean isDressed) {
		// 没有传奇属性
		if (equip.itemDb.speData.legendAtts == null) {
			return;
		}

		if (equip.itemDb.isNew == 1) {
			equip.itemDb.isNew = 0;
		}
		// 已经随机到的词条，下次随机不再出现
		List<Integer> usedGroupId = new ArrayList<>();
		// if (equip.itemDb.speDataObj.legendAtts != null) {
		// usedGroupId.add(ItemConfig.getInstance().getAffixGroupID(equip.itemDb.speDataObj.legendAtts.key));
		// }

		// 固定属性装备,则取prop.baseCode作为加工的值
		DEquipBase finalProp = equip.prop;
		if (StringUtil.isNotEmpty(equip.prop.baseCode)) {
			finalProp = ItemConfig.getInstance().getEquipProp(equip.prop.baseCode);
		}

		AffixExt affxExt = ItemUtil.initOneAtts(finalProp, usedGroupId, AffixType.legend);

		FourProp rdProp = affxExt.props.get(equip.prop.qcolor);
		if (rdProp == null) {
			return;
		}
		// 生成临时属性
		equip.speData.tempUniqueAtts = new HashMap<>();
		equip.speData.tempUniqueAtts.put(affxExt.iD, RandomUtil.getInt(rdProp.min, rdProp.max));

		if (isDressed) {
			_updateAndPush(new int[] { pos }, true);
			equipSync();
		} else {
			_updateAndPush(new int[] { pos }, true, false);
		}

		// 更新任务状态
		this.player.taskManager.dealTaskEvent(TaskType.EQUIP_REFINE, "", 1);
		this.player.achievementManager.onEquipRefine();
	}

	/**
	 * 保存精炼属性
	 */
	public void saveRefineLegend(NormalEquip equip, int pos, boolean isDressed) {
		if (equip.speData.tempUniqueAtts == null) {
			return;
		}
		if (equip.itemDb.isNew == 1) {
			equip.itemDb.isNew = 0;
		}

		// 从属性集合中移除老的属性
		equip.itemDb.speData.legendAtts = equip.speData.tempUniqueAtts;
		equip.speData.tempUniqueAtts = null;

		if (isDressed) {
			_updateAndPush(new int[] { pos }, true);
			equipSync();
		} else {
			_updateAndPush(new int[] { pos }, true, false);
		}
	}

	public List<AttributeBase> getRefineExtProp(NormalEquip equip) {
		List<AttributeBase> result = new ArrayList<>();
		DEquipBase extProp = equip.prop;
		if (StringUtil.isNotEmpty(equip.prop.baseCode)) {
			extProp = ItemConfig.getInstance().getEquipProp(equip.prop.baseCode);
		}

		List<AttsObj> extAtts = new ArrayList<>();
		ItemUtil.initAllExtAtts(extAtts, extProp);

		for (AttsObj att : extAtts) {
			result.add(EquipUtil.getAttributeBase(att));
		}
		return result;
	}

	public List<SuperScriptType> getSuperScript() {
		List<SuperScriptType> list = new ArrayList<>();
		list.addAll(getWorkSuperScript());
		list.addAll(getStrengthSuperScript());
		list.addAll(getGemFillSuperScript(null));
		list.addAll(getEquipSuperScript(null));
		return list;
	}

	public void pushScripts() {
		SuperScriptPush.Builder data = SuperScriptPush.newBuilder();
		List<SuperScriptType> list = getWorkSuperScript();
		if (list != null && !list.isEmpty()) {
			data.addAllS2CData(list);
			player.receive("area.playerPush.onSuperScriptPush", data.build());
		}
	}

	// 打造红点
	public int getMakePoint() {
		if (!player.functionOpenManager.isOpen(Const.FunctionType.Make.getValue())) {
			return 0;
		}
		int number = 0;
		for (EquipMakeExt make : GameData.EquipMakes.values()) {
			if (player.player.gold < make.costMoney) {
				continue;
			}
			if (StringUtil.isNotEmpty(make.reqMateCode1)) {
				if (player.bag.findItemNumByCode(make.reqMateCode1) < make.reqMateCount1) {
					continue;
				}
			}
			if (StringUtil.isNotEmpty(make.reqMateCode2)) {
				if (player.bag.findItemNumByCode(make.reqMateCode2) < make.reqMateCount2) {
					continue;
				}
			}
			if (StringUtil.isNotEmpty(make.reqMateCode3)) {
				if (player.bag.findItemNumByCode(make.reqMateCode3) < make.reqMateCount3) {
					continue;
				}
			}
			if (player.getLevel() / 10 * 10 != make.equipLevel) {
				continue;
			}
			number = 1;
			break;
		}
		return number;
	}

	// 洗练红点
	public int getRebornPoint() {
		if (!player.functionOpenManager.isOpen(Const.FunctionType.Reborn.getValue())) {
			return 0;
		}
		if (player.playerBasePO.openRebornToday) {
			return 0;
		}
		int number = 0;
		for (ReBornExt reBornExt : GameData.ReBorns.values()) {
			if (player.player.gold < reBornExt.costGold) {
				continue;
			}
			if (StringUtil.isNotEmpty(reBornExt.mateCode1)) {
				if (player.bag.findItemNumByCode(reBornExt.mateCode1) < reBornExt.mateCount1) {
					continue;
				}
			}
			if (StringUtil.isNotEmpty(reBornExt.mateCode2)) {
				if (player.bag.findItemNumByCode(reBornExt.mateCode2) < reBornExt.mateCount2) {
					continue;
				}
			}

			if (player.getLevel() / 10 * 10 != reBornExt.level) {
				continue;
			}
			number = 1;
			break;
		}
		return number;
	}

	// 重铸高级重铸红点
	public int getRebuildPoint() {
		if (!player.functionOpenManager.isOpen(Const.FunctionType.Rebuild.getValue())) {
			return 0;
		}
		if (player.playerBasePO.openRebuildToday) {
			return 0;
		}
		int number = 0;
		for (ReBuildExt reBuildExt : GameData.ReBuilds.values()) {
			if (player.player.gold < reBuildExt.costGold) {
				continue;
			}
			if (StringUtil.isNotEmpty(reBuildExt.mateCode1)) {
				if (player.bag.findItemNumByCode(reBuildExt.mateCode1) < reBuildExt.mateCount1) {
					continue;
				}
			}
			if (StringUtil.isNotEmpty(reBuildExt.mateCode2)) {
				if (player.bag.findItemNumByCode(reBuildExt.mateCode2) < reBuildExt.mateCount2) {
					continue;
				}
			}
			if (StringUtil.isNotEmpty(reBuildExt.mateCode3)) {
				if (player.bag.findItemNumByCode(reBuildExt.mateCode3) < reBuildExt.mateCount3) {
					continue;
				}
			}

			if (player.getLevel() / 10 * 10 != reBuildExt.level) {
				continue;
			}
			number = 1;
			break;
		}
		if (number > 0) {
			return number;
		}
		for (SeniorReBuildExt seniorReBuildExt : GameData.SeniorReBuilds.values()) {
			if (player.player.gold < seniorReBuildExt.costGold) {
				continue;
			}
			if (StringUtil.isNotEmpty(seniorReBuildExt.mateCode1)) {
				if (player.bag.findItemNumByCode(seniorReBuildExt.mateCode1) < seniorReBuildExt.mateCount1) {
					continue;
				}
			}
			if (StringUtil.isNotEmpty(seniorReBuildExt.mateCode2)) {
				if (player.bag.findItemNumByCode(seniorReBuildExt.mateCode2) < seniorReBuildExt.mateCount2) {
					continue;
				}
			}

			if (StringUtil.isNotEmpty(seniorReBuildExt.mateCode3)) {
				if (player.bag.findItemNumByCode(seniorReBuildExt.mateCode3) < seniorReBuildExt.mateCount3) {
					continue;
				}
			}

			if (player.getLevel() / 10 * 10 != seniorReBuildExt.level) {
				continue;
			}
			number = 1;
			break;
		}
		return number;
	}

	// 开光红点
	public int getKaiguangPoint() {
		if (!player.functionOpenManager.isOpen(Const.FunctionType.REFINE.getValue())) {
			return 0;
		}
		if (player.playerBasePO.openKaiguangToday) {

			return 0;
		}
		int number = 0;
		for (RefineExt reFineExt : GameData.Refines.values()) {
			if (player.player.gold < reFineExt.costGold) {
				// Out.error(reFineExt.iD,"-------1");
				continue;
			}
			if (StringUtil.isNotEmpty(reFineExt.mateCode1)) {
				if (player.bag.findItemNumByCode(reFineExt.mateCode1) < reFineExt.mateCount1) {
					// Out.error(reFineExt.iD,"-------2");
					continue;
				}
			}
			if (StringUtil.isNotEmpty(reFineExt.mateCode2)) {
				if (player.bag.findItemNumByCode(reFineExt.mateCode2) < reFineExt.mateCount2) {
					// Out.error(reFineExt.iD,"-------3");
					continue;
				}
			}

			if (player.getLevel() / 10 * 10 != reFineExt.level) {
				// Out.error(reFineExt.iD,"-------4");
				continue;
			}
			number = 1;
			// Out.error(reFineExt.iD,"-------5");
			break;
		}
		return number;
	}

	/**
	 * 计算装备加工红点
	 */
	public List<SuperScriptType> getWorkSuperScript() {
		List<SuperScriptType> list = new ArrayList<>();
		// if (!player.functionOpenManager.isOpen(Const.FunctionType.Make.getValue())) {
		// return list;
		// }

		// RedPointPO redPoint = getAndCheckUpdateRedPointPO(player.getId());

		// 打造
		int number1 = getMakePoint();
		SuperScriptType.Builder data1 = SuperScriptType.newBuilder();
		data1.setType(Const.SUPERSCRIPT_TYPE.MAKE.getValue());
		data1.setNumber(number1);
		list.add(data1.build());

		// 洗脸
		int number2 = getRebornPoint();
		SuperScriptType.Builder data2 = SuperScriptType.newBuilder();
		data2.setType(Const.SUPERSCRIPT_TYPE.REBORN.getValue());
		data2.setNumber(number2);
		list.add(data2.build());

		// 重铸
		int number3 = getRebuildPoint();
		SuperScriptType.Builder data3 = SuperScriptType.newBuilder();
		data3.setType(Const.SUPERSCRIPT_TYPE.REBUILD.getValue());
		data3.setNumber(number3);
		list.add(data3.build());

		// 开光
		int number4 = getKaiguangPoint();
		SuperScriptType.Builder data4 = SuperScriptType.newBuilder();
		data4.setType(Const.SUPERSCRIPT_TYPE.KAIGUANG.getValue());
		data4.setNumber(number4);
		list.add(data4.build());

		// 加工
		SuperScriptType.Builder data = SuperScriptType.newBuilder();
		data.setType(Const.SUPERSCRIPT_TYPE.WORKING.getValue());
		data.setNumber(number1 + number2 + number3 + number4);
		list.add(data.build());

		// for (SuperScriptType superScriptType : list) {
		// Out.error(superScriptType.getType(),"-------------",superScriptType.getNumber());
		// }

		return list;
	}

	public RedPointBean findRedPointBean(List<RedPointBean> list, int code) {
		if (list == null || list.isEmpty()) {
			return null;
		}
		for (RedPointBean bean : list) {
			if (bean.id == code) {
				return bean;
			}
		}
		return null;
	}

	/**
	 * 计算装备强化红点
	 */
	public List<SuperScriptType> getStrengthSuperScript() {
		List<SuperScriptType> list = new ArrayList<>();
		if (!player.functionOpenManager.isOpen(Const.FunctionType.STRENGTHEN.getValue())) {
			return list;
		}

		int number = 0;
		// 强化红点
		for (int pos : this.strengthPos.keySet()) {
			EquipStrengthPos posInfo = this.strengthPos.get(pos);
			int level = posInfo.enSection * 100 + posInfo.enLevel;
			if (level < EquipUtil.maxStrengthLevel) {
				int nextLv = level + 1;
				if (posInfo.enLevel >= 9) {
					nextLv = (posInfo.enSection + 1) * 100;
				}
				EnchantExt enchant = GameData.Enchants.get(nextLv);
				if (enchant != null && player.bag.isItemNumEnough(enchant.mateCode1, enchant.mateCount1) && player.bag.isItemNumEnough(enchant.mateCode2, enchant.mateCount2) && player.player.gold >= enchant.costGold) {
					number = 1;
					break;
				}
			}
		}
		SuperScriptType.Builder data = SuperScriptType.newBuilder();
		data.setType(Const.SUPERSCRIPT_TYPE.EQUIP_STRENGTH.getValue());
		data.setNumber(number);
		list.add(data.build());
		return list;
	}

	/**
	 * 计算宝石镶嵌红点
	 */
	public List<SuperScriptType> getGemFillSuperScript(String templateCode) {
		long start = System.currentTimeMillis();
		List<SuperScriptType> list = new ArrayList<>();

		if (!player.functionOpenManager.isOpen(Const.FunctionType.SetNew.getValue())) {
			return list;
		}

		int number = 0;

		if (StringUtil.isNotEmpty(templateCode)) {
			// 1.空格子,并且有对应的宝石
			boolean flag = false;
			List<Integer> codePoses = ItemConfig.getInstance().findPosByGem(templateCode);
			for (int pos : codePoses) {
				EquipStrengthPos posInfo = this.strengthPos.get(pos);
				for (int i = 1; i <= posInfo.socks; i++) {
					String code = posInfo.gems.get(i);
					if (StringUtil.isEmpty(code)) {
						number = 1;
						flag = true;
						break;
					}
				}
				if (flag) {
					break;
				}
			}

			if (number == 0) {
				DItemBase baseProp = (DItemBase) ItemConfig.getInstance().getItemProp(templateCode);
				List<Integer> poses = ItemConfig.getInstance().findPosByGem(templateCode);
				// 宝石类型，宝石最小等级
				Map<Integer, Integer> gemLevels = new HashMap<>();
				// 镶嵌红点
				for (int pos : this.strengthPos.keySet()) {
					if (!poses.contains(pos)) {
						continue;
					}
					EquipStrengthPos posInfo = this.strengthPos.get(pos);
					for (String code : posInfo.gems.values()) {
						if (StringUtil.isEmpty(code)) {
							continue;
						}
						DItemBase prop = (DItemBase) ItemConfig.getInstance().getItemProp(code);
						if (!gemLevels.containsKey(prop.par) || gemLevels.get(prop.par) > prop.levelReq) {
							gemLevels.put(prop.par, prop.levelReq);
						}
					}
				}

				int level = gemLevels.containsKey(baseProp.par) ? gemLevels.get(baseProp.par) : 0;
				if (baseProp.levelReq > level) {
					number = 1;
				}
			}
		} else {
			// 1.空格子,并且有对应的宝石
			boolean flag = false;
			for (int pos : this.strengthPos.keySet()) {
				EquipStrengthPos posInfo = this.strengthPos.get(pos);
				for (int i = 1; i <= posInfo.socks; i++) {
					String code = posInfo.gems.get(i);
					if (StringUtil.isNotEmpty(code)) {
						continue;
					}
					List<String> typeList = GameData.EquipSocks.get(pos).typeList;
					for (String _code : typeList) {
						if (player.bag.findFirstItemByCode(_code) != null) {
							number = 1;
							flag = true;
							break;
						}
					}
					if (flag) {
						break;
					}
				}
				if (flag) {
					break;
				}
			}

			if (number == 0) {
				// 2.装备部位 对应的各种宝石等级
				Map<Integer, Integer> posCodeLevelBag = new HashMap<>();
				List<NormalItem> list_bag = player.bag.findItemByType(Const.ItemSecondType.gem.getKey());
				if (list_bag.size() == 0) {
					number = 0;
				} else {
					for (NormalItem bagItem : list_bag) {
						List<Integer> list_gem_pos = ItemConfig.getInstance().findPosByGem(bagItem.itemCode());
						for (int bagPos : list_gem_pos) {
							if (posCodeLevelBag.get(bagPos) == null || posCodeLevelBag.get(bagPos) < bagItem.prop.levelReq) {
								posCodeLevelBag.put(bagPos, bagItem.prop.levelReq);
							}
						}
					}

					// 装备部位 对应的各种宝石最低等级
					Map<Integer, Integer> posCodeLevel = new HashMap<>();
					// 镶嵌红点
					for (int pos : this.strengthPos.keySet()) {
						EquipStrengthPos posInfo = this.strengthPos.get(pos);
						for (String code : posInfo.gems.values()) {
							if (StringUtil.isEmpty(code)) {
								continue;
							}
							DItemBase prop = (DItemBase) ItemConfig.getInstance().getItemProp(code);
							if (posCodeLevel.get(pos) == null || posCodeLevel.get(pos) > prop.levelReq) {
								posCodeLevel.put(pos, prop.levelReq);
							}
						}
					}
					flag = false;
					for (int bagPos : posCodeLevelBag.keySet()) {
						int levelBag = posCodeLevelBag.get(bagPos);
						for (int pos : posCodeLevel.keySet()) {
							if (bagPos == pos) {
								if (levelBag > posCodeLevel.get(pos)) {
									number = 1;
									flag = true;
									break;
								}
							}
						}
						if (flag) {
							break;
						}
					}

				}
			}
		}
		long cost = System.currentTimeMillis() - start;
		if (cost > 10) {
			Out.info("getGemFillSuperScript cost ", cost);
		}

		SuperScriptType.Builder data = SuperScriptType.newBuilder();
		data.setType(Const.SUPERSCRIPT_TYPE.EQUIP_FILL_GEM.getValue());
		data.setNumber(number);
		list.add(data.build());
		return list;
	}

	/**
	 * 背包中有更高品质装备显示 红点
	 */
	public List<SuperScriptType> getEquipSuperScript(NormalEquip equip) {
		List<SuperScriptType> list = new ArrayList<>();

		int number = 0;
		// 当前装备比身上的装备评分高
		if (equip != null) {
			if (equip.getLevel() <= this.player.getLevel() && (equip.prop.Pro == this.player.getPro() || equip.prop.Pro == 0)) {
				NormalEquip playerEquip = this.equips.get(equip.getPosition());
				if (playerEquip == null) {
					number = 1;
				} else {
					if (playerEquip.getEquipScore(player.playerBasePO) < equip.getEquipScore(player.playerBasePO)) {
						number = 1;
					}
				}
			}
		} else {
			boolean flag = false;
			for (EquipType ePos : EquipType.values()) {
				int pos = ePos.getValue();
				List<NormalEquip> list_all = player.bag.findEquipByType(pos, this.player.getPro(), player.getLevel());
				if (list_all.size() > 0) {
					NormalEquip playerEquip = this.equips.containsKey(pos) ? this.equips.get(pos) : null;
					if (playerEquip == null) {
						number = 1;
						flag = true;
					} else {
						for (NormalEquip bagEquip : list_all) {
							if ((playerEquip.getEquipScore(player.playerBasePO) < bagEquip.getEquipScore(player.playerBasePO))) {
								number = 1;
								flag = true;
								break;
							}
						}
					}
				}
				if (flag) {
					break;
				}
			}
		}
		SuperScriptType.Builder data = SuperScriptType.newBuilder();
		data.setType(Const.SUPERSCRIPT_TYPE.EQUIP_EQUIP.getValue());
		data.setNumber(number);
		list.add(data.build());
		return list;
	}

	private void removeSameScript(List<SuperScriptType> list) {
		if (list == null || list.size() == 0) {
			return;
		}
		Iterator<SuperScriptType> iter = list.iterator();
		while (iter.hasNext()) {
			SuperScriptType script = iter.next();
			int oldNum = redpoints.containsKey(script.getType()) ? redpoints.get(script.getType()) : 0;
			if (script.getNumber() > 0) {
				if (oldNum > 0) {
					iter.remove();
				} else {
					redpoints.put(script.getType(), script.getNumber());
				}
			} else {
				if (oldNum <= 0) {
					iter.remove();
				} else {
					redpoints.put(script.getType(), script.getNumber());
				}
			}
		}
	}

	public RedPointPO getAndCheckUpdateRedPointPO(String playerId) {
		RedPointPO redPointPO = PlayerPOManager.findPO(ConstsTR.redpointTR, playerId, RedPointPO.class);
		if (redPointPO == null) {
			synchronized (this.player) {
				redPointPO = PlayerPOManager.findPO(ConstsTR.redpointTR, playerId, RedPointPO.class);
				if (redPointPO == null) {
					redPointPO = new RedPointPO();
					PlayerPOManager.put(ConstsTR.redpointTR, playerId, redPointPO);
				}
			}
		}
		checkupdateRedpoint(redPointPO);
		return redPointPO;
	}

	public void checkupdateRedpoint(RedPointPO redPointPO) {
		if (redPointPO.list != null) {
			Date now = new Date();
			for (RedPointBean bean : redPointPO.list) {
				boolean isSameDay = DateUtil.isSameDay(bean.date, now);
				if (!isSameDay) {
					bean.date = now;
					bean.point = 0;
				}
			}
		}
	}

	/**
	 * 刷新打造红点
	 */
	public void updateMakeScript(String code) {
		if (StringUtil.isEmpty(code) || EquipUtil.makeMaterial.contains(code)) {
			GWorld.getInstance().ansycExec(() -> {
				List<SuperScriptType> list = this.getWorkSuperScript();
				// removeSameScript(list);
				player.updateSuperScriptList(list);
			});
		}
	}

	/**
	 * 刷新强化红点
	 */
	public void updateStrengthScript(String code) {
		if (StringUtil.isEmpty(code) || EquipUtil.strengthMaterial.contains(code)) {
			GWorld.getInstance().ansycExec(() -> {
				List<SuperScriptType> list = this.getStrengthSuperScript();
				removeSameScript(list);
				player.updateSuperScriptList(list);
			});
		}
	}

	/**
	 * 刷新镶嵌红点
	 */
	public void updateFillGemScript(String code) {
		if (StringUtil.isEmpty(code) || GameData.Jewels.containsKey(code)) {
			GWorld.getInstance().ansycExec(() -> {
				List<SuperScriptType> list = this.getGemFillSuperScript(code);
				removeSameScript(list);
				player.updateSuperScriptList(list);
			});
		}
	}

	/**
	 * 刷新穿装备红点
	 */
	public void updateEquipScript(NormalEquip equip) {
		GWorld.getInstance().ansycExec(() -> {
			List<SuperScriptType> list = this.getEquipSuperScript(equip);
			player.updateSuperScriptList(list);
		});
	}

	public void updateSuperScript() {
		GWorld.getInstance().ansycExec(() -> {
			List<SuperScriptType> list = this.getSuperScript();
			player.updateSuperScriptList(list);
		});
	};

	public void onLogin() {
		redpoints.clear();
	}

	/**
	 * 玩家升级事件
	 */
	public void OnPlayerLevelUp() {
		// 开放装备的宝石 镶嵌槽
		int level = player.player.level;
		for (int pos : this.strengthPos.keySet()) {
			EquipSockCO sockCO = GameData.EquipSocks.get(pos);
			if (sockCO == null)
				continue;

			EquipStrengthPos posInfo = this.strengthPos.get(pos);
			int sockNum = 0;
			if (level >= sockCO.sock1OpenLvl) {
				sockNum += 1;
			}
			if (level >= sockCO.sock2OpenLvl) {
				sockNum += 1;
			}
			if (level >= sockCO.sock3OpenLvl) {
				sockNum += 1;
			}
			if (level >= sockCO.sock4OpenLvl) {
				sockNum += 1;
			}
			if (level >= sockCO.sock5OpenLvl) {
				sockNum += 1;
			}
			posInfo.socks = sockNum;
		}

		int len = Const.EquipType.values().length;
		int[] grids = new int[len];
		for (int i = 0; i < len; i++) {
			grids[i] = i + 1;
		}
		_updateAndPushPOS(grids, false);
		// 红点
		updateFillGemScript(null);
	}

	private void update() {
		player.playerBasePO.equipGrids = new HashMap<>();
		for (int pos : this.equips.keySet()) {
			if (equips.get(pos) != null) {
				player.playerBasePO.equipGrids.put(pos, equips.get(pos).itemDb);
			}
		}
	}

	public int getTotalStrenthenLv() {
		int totalLv = 0;
		for (EquipStrengthPos equipStrengthPos : strengthPos.values()) {
			totalLv += equipStrengthPos.enSection * 10 + equipStrengthPos.enLevel;
		}
		return totalLv;
	}

	public void refreshNewDay() {
		player.playerBasePO.openRebornToday = false;
		player.playerBasePO.openRebuildToday = false;
		player.playerBasePO.openKaiguangToday = false;
	}
}
