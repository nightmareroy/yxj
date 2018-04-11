package com.wanniu.game.achievement;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.wanniu.core.game.LangService;
import com.wanniu.core.logfs.Out;
import com.wanniu.core.util.StringUtil;
import com.wanniu.game.achievement.po.BaseInfo;
import com.wanniu.game.common.Const;
import com.wanniu.game.common.Const.ACHIEVEMENT_CONDITION_TYPE;
import com.wanniu.game.common.Const.EventType;
import com.wanniu.game.common.Const.GOODS_CHANGE_TYPE;
import com.wanniu.game.common.Const.PlayerBtlData;
import com.wanniu.game.common.Const.TaskKind;
import com.wanniu.game.common.ConstsTR;
import com.wanniu.game.data.AchievementCO;
import com.wanniu.game.data.GameData;
import com.wanniu.game.data.base.DItemBase;
import com.wanniu.game.data.ext.AchievementConfigExt;
import com.wanniu.game.data.ext.AchievementExt;
import com.wanniu.game.data.ext.ArmourAttributeExt;
import com.wanniu.game.data.ext.ArmourPlusExt;
import com.wanniu.game.item.ItemUtil;
import com.wanniu.game.item.NormalItem;
import com.wanniu.game.mail.SysMailConst;
import com.wanniu.game.player.AttributeUtil;
import com.wanniu.game.player.BILogService;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.poes.AchievementDataPO;
import com.wanniu.game.poes.AchievementDataPO.AchievePO;
import com.wanniu.game.poes.AchievementDataPO.HolyArmour;
import com.wanniu.game.poes.PlayerBasePO.EquipStrengthPos;
import com.wanniu.game.task.TaskEvent;
import com.wanniu.redis.PlayerPOManager;

import pomelo.area.AchievementHandler.Achievement;
import pomelo.area.AchievementHandler.AchievementGetTypeElementResponse;
import pomelo.area.PlayerHandler.SuperScriptType;
import pomelo.item.ItemOuterClass.MiniItem;

public class AchievementManager {
	private WNPlayer player;
	public AchievementDataPO achievementDataPO;
	public Map<Integer, AchievePO> achievementRecordMap;

	private AchievementServiceNew achievementService;

	protected AchievementManager() {

	}

	public AchievementManager(WNPlayer player, AchievementDataPO achievementData) {
		this.player = player;
		this.achievementDataPO = achievementData;
		if (this.achievementDataPO == null) {
			this.achievementDataPO = new AchievementDataPO();
			PlayerPOManager.put(ConstsTR.achievementTR, player.getId(), this.achievementDataPO);
		}

		this.achievementRecordMap = this.achievementDataPO.achievements;

		achievementService = AchievementServiceNew.getInstance();
	};

	public Map<Integer, AchievePO> getAchievementRecords() {
		return this.achievementRecordMap;
	}

	/**
	 * 请求对于项的成就
	 * 
	 * @param chapterId 章节ID
	 * @returns {*}
	 */
	public void toJson4PayloadbyTypeId(int chapterId, AchievementGetTypeElementResponse.Builder res) {
		List<AchievementExt> achievements = GameData.findAchievements(t -> {
			return t.chapterID == chapterId;
		});
		AchievementConfigExt configExt = GameData.AchievementConfigs.get(chapterId);
		int recordCount = 0;
		for (AchievementExt achievementRelation : achievements) {
			Achievement.Builder builder = Achievement.newBuilder();
			AchievePO achieve = achievementRecordMap.get(achievementRelation.id);
			if (achieve != null) {
				builder.setId(achieve.id);
				builder.setScheduleCurr(achieve.scheduleCurr);
				builder.setStatus(0);
				if (achieve.awardState == 1) {
					// 0:未达成 1:已达成,未领取 2:已领取
					recordCount++;
					builder.setStatus(2);
					builder.setScheduleCurr(achievementRelation.targetNum);
				}
				else if (achievementService.isComplete(achieve, achievementRelation)) {
					recordCount++;
					// 0:未达成 1:已达成,未领取 2:已领取
					builder.setStatus(1);
				}

				
			} else {
				builder.setId(achievementRelation.id);
				builder.setScheduleCurr(0);
				// 0:未达成 1:已达成,未领取 2:已领取
				builder.setStatus(0);
			}
			res.addS2CAchievements(builder);
		}
		res.setS2CRewardCount(recordCount);
		int status = 0; // 0:未达成 1:已达成,未领取 2:已领取
		if (this.achievementDataPO.receivedAwards.contains(chapterId)) {
			status = 2;
		} else if (achievements.size() > 0 && recordCount >= achievements.size()) {
			status = 1;
		}
		res.setS2CRewardStatus(status);

		// 成就项是否开放
		List<Integer> list_opend_chapter = new ArrayList<>();
		for (AchievementConfigExt config : GameData.AchievementConfigs.values()) {
			if (player.getLevel() < config.lv) {
				continue;
			}
			if (config.quest != 0 && !player.taskManager.finishedNormalTasks.containsKey(config.quest)) {
				continue;
			}
			list_opend_chapter.add(config.typeId);
		}
		res.addAllS2COpenedChapter(list_opend_chapter);

		// 宝箱预览
		List<MiniItem> list_chest = new ArrayList<>();
		for (String itemCode : configExt.awards.keySet()) {
			list_chest.add(ItemUtil.getMiniItemData(itemCode, configExt.awards.get(itemCode)).build());
		}
		res.addAllS2CChestView(list_chest);
	};

	/**
	 * 领取奖励
	 * 
	 * @param type 0:累计奖励 1:单条成就奖励
	 * @param awardId
	 * @returns {number}
	 */
	public BaseInfo getAward(int type, int awardId) {
		BaseInfo data = new BaseInfo();
		data.code = Const.CODE.OK;

		// 检查条件
		if (player == null) {
			data.code = Const.CODE.FAIL;
			data.msg = LangService.getValue("SOMETHING_ERR");
			return data;
		}

		// 累计奖励
		if (type == 0) {
			AchievementConfigExt configExt = GameData.AchievementConfigs.get(awardId);
			// 章节不存在
			if (configExt == null) {
				data.code = Const.CODE.FAIL;
				data.msg = LangService.getValue("SOMETHING_ERR");
				return data;
			}

			// 当前已领取
			if (this.achievementDataPO.receivedAwards.contains(awardId)) {
				data.code = Const.CODE.FAIL;
				data.msg = LangService.getValue("SIGN_HAVE_RECEIVED");
				return data;
			}

			// 未达成当前章节所有成就
			List<AchievementExt> list_achieves = GameData.findAchievements(t -> {
				return t.chapterID == awardId;
			});
			AchievePO achivePO = null;
			for (AchievementExt ext : list_achieves) {
				achivePO = this.achievementRecordMap.get(awardId);
				if (achivePO == null) {
					break;
				}
				if(achivePO.awardState==1) {
					break;
				}
				if (!achievementService.isComplete(achivePO, ext)) {
					break;
				}
			}

			// 更新为已领取状态
			this.achievementDataPO.receivedAwards.add(awardId);

			// 发放奖励
			List<NormalItem> list_items = ItemUtil.createItemsByItemCode(configExt.awards);
			player.bag.addCodeItemMail(list_items, null, GOODS_CHANGE_TYPE.achieve, SysMailConst.BAG_FULL_COMMON);
		} else if (type == 1) { // 单条成就奖励
			AchievementExt achievementExt = GameData.Achievements.get(awardId);
			AchievePO achivePO = this.achievementRecordMap.get(awardId);
			// 成就不存在
			if (achievementExt == null || achivePO == null) {
				data.code = Const.CODE.FAIL;
				data.msg = LangService.getValue("SOMETHING_ERR");
				return data;
			}

			// 当前已领取
			if (achivePO.awardState == 1) {
				data.code = Const.CODE.FAIL;
				data.msg = LangService.getValue("SIGN_HAVE_RECEIVED");
				return data;
			}

			// 成就未达成
			if (!achievementService.isComplete(achivePO, achievementExt)) {
				data.code = Const.CODE.FAIL;
				data.msg = LangService.getValue("SIGN_HAVE_RECEIVED");
				return data;
			}

			// 更新为已领取状态
			achivePO.awardState = 1;

			// 发放奖励
			// player.bag.addCodeItem(achievementExt.awardKey, achievementExt.awardValue,
			// null, GOODS_CHANGE_TYPE.achieve, null);
			player.bag.addCodeItemMail(achievementExt.awardKey, achievementExt.awardValue, null, GOODS_CHANGE_TYPE.achieve, SysMailConst.BAG_FULL_COMMON);
		} else {
			data.code = Const.CODE.FAIL;
			data.msg = LangService.getValue("SOMETHING_ERR");
			return data;
		}

		return data;
	};

	/****************************************************************************************************************
	 * 成就完成事件 其他模块调用接口 begin
	 *****************************************************************************************************************/
	/**
	 * 幻境挂机时间
	 */
	public void onIllusionTimeChange(int minutes) {
		List<AchievementExt> achievementArray = achievementService.findByConditionType(ACHIEVEMENT_CONDITION_TYPE.ILLUSION_TIME);
		if (achievementArray.size() > 0) {
			achievementService._onConditionChange(minutes, achievementArray, player, true);
		}
	}

	/**
	 * 击杀野外BOSS达到指定数量
	 */
	public void onKillBoss(int num) {
		List<AchievementExt> achievementArray = achievementService.findByConditionType(ACHIEVEMENT_CONDITION_TYPE.KILL_BOSS);
		if (achievementArray.size() > 0) {
			achievementService._onConditionChange(num, achievementArray, player, true);
		}
	}

	/**
	 * 通关指定层数的镇妖塔
	 */
	public void onPassDemonTower(int floor, boolean win) {
		if (win) {
			List<AchievementExt> achievementArray = achievementService.findByConditionType(ACHIEVEMENT_CONDITION_TYPE.PASS_DEMONTOWER);
			if (achievementArray.size() > 0) {
				achievementService._onConditionChange(floor, achievementArray, player, false);
			}
		}
		List<AchievementExt> achievementArray = achievementService.findByConditionType(ACHIEVEMENT_CONDITION_TYPE.DEMONTOWER_TIMES);
		if (achievementArray.size() > 0) {
			achievementService._onConditionChange(1, achievementArray, player, true);
		}
	}

	/**
	 * 世界频道喊话达到指定次数
	 */
	public void onWorldSpeakTimes() {
		List<AchievementExt> achievementArray = achievementService.findByConditionType(ACHIEVEMENT_CONDITION_TYPE.WORLD_SPEAK_TIME);
		if (achievementArray.size() > 0) {
			achievementService._onConditionChange(1, achievementArray, player, true);
		}
	}

	/**
	 * 镶嵌宝石总等级达到指定数值
	 */
	public void onGemFillTotalLevel() {
		List<AchievementExt> achievementArray = achievementService.findByConditionType(ACHIEVEMENT_CONDITION_TYPE.GEM_FILL_TOTAL_LEVEL);
		int level = 0;
		for (EquipStrengthPos pos : player.equipManager.strengthPos.values()) {
			for (String code : pos.gems.values()) {
				if (StringUtil.isEmpty(code))
					continue;
				DItemBase prop = ItemUtil.getUnEquipPropByCode(code);
				level += prop.levelReq;
			}
		}
		if (achievementArray.size() > 0) {
			achievementService._onConditionChange(level, achievementArray, player, false);
		}
	}

	/**
	 * 指定数量的部位强化达到指定等级
	 */
	public void onEquipPosStrengthLevel(int pos, int level) {
		List<AchievementExt> achievementArray = achievementService.findByConditionType(ACHIEVEMENT_CONDITION_TYPE.EQUIP_POS_LEVEL);

		List<AchievementExt> tmpArray = new ArrayList<>();
		if (achievementArray.size() > 0) {
			for (AchievementExt achievement : achievementArray) {
				if (Integer.parseInt(achievement.targetID) <= level) {
					tmpArray.add(achievement);
				}
			}
		}

		if (tmpArray.size() > 0) {
			achievementService._onConditionChange(pos, tmpArray, player, true);
		}
	}

	/**
	 * 装备打造次数达到指定数值
	 */
	public void onEquipMake() {
		List<AchievementExt> achievementArray = achievementService.findByConditionType(ACHIEVEMENT_CONDITION_TYPE.EQUIP_MAKE_TIMES);
		if (achievementArray.size() > 0) {
			achievementService._onConditionChange(1, achievementArray, player, true);
		}
	}

	/**
	 * 装备洗练次数达到指定数值
	 */
	public void onEquipReborn() {
		List<AchievementExt> achievementArray = achievementService.findByConditionType(ACHIEVEMENT_CONDITION_TYPE.EQUIP_REBORN_TIMES);
		if (achievementArray.size() > 0) {
			achievementService._onConditionChange(1, achievementArray, player, true);
		}
	}

	/**
	 * 装备精炼次数达到指定数值
	 */
	public void onEquipRefine() {
		List<AchievementExt> achievementArray = achievementService.findByConditionType(ACHIEVEMENT_CONDITION_TYPE.EQUIP_REFINE_TIMES);
		if (achievementArray.size() > 0) {
			achievementService._onConditionChange(1, achievementArray, player, true);
		}
	}

	/**
	 * 装备重铸次数达到指定数值
	 */
	public void onEquipRebuild() {
		List<AchievementExt> achievementArray = achievementService.findByConditionType(ACHIEVEMENT_CONDITION_TYPE.EQUIP_REBUILD_TIMES);
		if (achievementArray.size() > 0) {
			achievementService._onConditionChange(1, achievementArray, player, true);
		}
	}

	/**
	 * 累计参加试练大赛的次数达到指定数值 5v5=试练大赛
	 */
	public void onPassFiveVsFive() {
		List<AchievementExt> achievementArray = achievementService.findByConditionType(ACHIEVEMENT_CONDITION_TYPE.FIVE_VS_FIVE_TIMES);
		if (achievementArray.size() > 0) {
			achievementService._onConditionChange(1, achievementArray, player, true);
		}
	}

	/**
	 * 累计参加五岳一战的次数达到指定数值 竞技场=大乱斗=五岳一战
	 */
	public void onArenaBattle() {
		List<AchievementExt> achievementArray = achievementService.findByConditionType(ACHIEVEMENT_CONDITION_TYPE.AREANA_TIMES);
		if (achievementArray.size() > 0) {
			achievementService._onConditionChange(1, achievementArray, player, true);
		}
	}

	/**
	 * 累计参加问道大会的次数达到指定数值 单挑王=问道大会
	 */
	public void onSoloBattle() {
		List<AchievementExt> achievementArray = achievementService.findByConditionType(ACHIEVEMENT_CONDITION_TYPE.SOLO_TIMES);
		if (achievementArray.size() > 0) {
			achievementService._onConditionChange(1, achievementArray, player, true);
		}
	}

	/**
	 * 累计参加镇妖塔次数达到指定数值
	 */
	public void onWorldLevelTimes() {
		List<AchievementExt> achievementArray = achievementService.findByConditionType(ACHIEVEMENT_CONDITION_TYPE.WORLD_LEVEL_TIMES);
		if (achievementArray.size() > 0) {
			achievementService._onConditionChange(1, achievementArray, player, true);
		}
	}

	/**
	 * 获得坐骑数量达到指定数值
	 */
	public void onMountGot() {
		List<AchievementExt> achievementArray = achievementService.findByConditionType(ACHIEVEMENT_CONDITION_TYPE.MOUNT_COUNT);
		if (achievementArray.size() > 0) {
			achievementService._onConditionChange(1, achievementArray, player, true);
		}
	}

	/**
	 * 累计获取仙缘值达到指定数值
	 */
	public void onXianyuanChange(int num) {
		List<AchievementExt> achievementArray = achievementService.findByConditionType(ACHIEVEMENT_CONDITION_TYPE.XIANYUAN_COUNT);
		if (achievementArray.size() > 0) {
			achievementService._onConditionChange(num, achievementArray, player, true);
		}
	}

	/**
	 * 角色等级改变
	 * 
	 * @param newLevel
	 */
	public void playerLevelChange(int newLevel) {
		if (achievementService.achievementLevelArray.size() > 0) {
			achievementService._onConditionChange(newLevel, achievementService.achievementLevelArray, player, false);
		}

		this.updateSuperScript();
	};

	/**
	 * 角色升阶
	 * 
	 * @param newRank
	 */
	public void playerRankChange(int newRank) {
		List<AchievementExt> achievementArray = achievementService.achievementUpLevelArray;
		if (achievementArray.size() > 0) {
			achievementService._onConditionChange(newRank, achievementArray, player, false);
		}
	};

	/**
	 * 角色战力
	 * 
	 * @param newPower
	 */
	public void playerPowerChange(int newPower) {
		List<AchievementExt> achievementArray = achievementService.achievementPower;
		if (achievementArray.size() > 0) {
			achievementService._onConditionChange(newPower, achievementArray, player, false);
		}
	};

	/**
	 * 击杀怪物
	 * 
	 * @param npcId
	 */
	public void killNpc(String npcId) {
		List<AchievementExt> achievementArray = achievementService.findByConditionTypeAndTargetIdInt(ACHIEVEMENT_CONDITION_TYPE.KILL_NPC, Integer.parseInt(npcId));

		if (achievementArray.size() > 0) {
			achievementService._onConditionChange(1, achievementArray, player, true);
		}
	};

	/**
	 * 装备强化
	 * 
	 * @param pos,level
	 */
	public void equipEnhance(int pos, int level) {
		List<AchievementExt> achievementArray = achievementService.findByConditionTypeAndTargetIdInt(ACHIEVEMENT_CONDITION_TYPE.EQUIPMENT_ENHANCE, pos);
		if (achievementArray.size() > 0) {
			achievementService._onConditionChange(level, achievementArray, player, false);
		}

		List<AchievementExt> achievementArray_every_pos = achievementService.findByConditionTypeAndTargetIdInt(ACHIEVEMENT_CONDITION_TYPE.EQUIPMENT_ENHANCE, 0);

		if (achievementArray_every_pos.size() > 0) {
			achievementService._onConditionChange(level, achievementArray_every_pos, player, false);
		}
	};

	public void onPlaceArrived(int achievementId) {
		List<AchievementExt> achievementArray = achievementService.findAchievementsByIdAndConditionType(achievementId, ACHIEVEMENT_CONDITION_TYPE.PLACE_ARRIVED);

		if (achievementArray.size() > 0) {
			achievementService._onConditionChange(1, achievementArray, player, true);
		}
	};

	public void onFinishTask(int taskId) {
		List<AchievementExt> achievementArray = achievementService.findByConditionTypeAndTargetIdInt(ACHIEVEMENT_CONDITION_TYPE.FINISH_TASK, taskId);

		if (achievementArray.size() > 0) {
			achievementService._onConditionChange(1, achievementArray, player, false);
		}

		for (AchievementConfigExt config : GameData.AchievementConfigs.values()) {
			if (config.quest == taskId) {
				this.updateSuperScript();
				break;
			}
		}
	};

	public void onFinishTaskNum(int kind) {
		Const.ACHIEVEMENT_CONDITION_TYPE conditionType = ACHIEVEMENT_CONDITION_TYPE.DEFAULT;

		if (kind == TaskKind.DAILY) {
			conditionType = ACHIEVEMENT_CONDITION_TYPE.FINISH_DAILY_TASK;
		} else if (kind == TaskKind.LOOP) {
			conditionType = ACHIEVEMENT_CONDITION_TYPE.FINISH_LOOP_TASK;
		} else if (kind == TaskKind.MAIN || kind == TaskKind.BRANCH) {
			conditionType = ACHIEVEMENT_CONDITION_TYPE.FINISH_TASK_NUM;
		}

		List<AchievementExt> achievementArray = achievementService.findByConditionType(conditionType);

		if (achievementArray.size() > 0) {
			achievementService._onConditionChange(1, achievementArray, player, true);
		}
	};

	public void onGetGold(int num) {
		List<AchievementExt> achievementArray = achievementService.findByConditionType(ACHIEVEMENT_CONDITION_TYPE.GET_GOLD);

		if (achievementArray.size() > 0) {
			achievementService._onConditionChange(num, achievementArray, player, true);
		}
	};

	public void onGetDiamondInConsignment(int num) {
		List<AchievementExt> achievementArray = achievementService.findByConditionType(ACHIEVEMENT_CONDITION_TYPE.GET_DIAMOND_IN_CONSIGNMENT);

		if (achievementArray.size() > 0) {
			achievementService._onConditionChange(num, achievementArray, player, true);
		}
	};

	public void onGetMagicRing(String ringId) {
		List<AchievementExt> achievementArray = new ArrayList<>();
		Collection<AchievementExt> datas = GameData.Achievements.values();

		for (AchievementCO d : datas) {
			AchievementExt data = (AchievementExt) d;
			if (ACHIEVEMENT_CONDITION_TYPE.GET_MAGIC_RING.value != data.conditionType)
				continue;
			// TODO 判断条件可能不对
			if (!data.targetID.equals(ringId))
				continue;

			achievementArray.add(data);
		}

		if (achievementArray.size() > 0) {
			achievementService._onConditionChange(1, achievementArray, player, false);
		}
	};

	public void onGetMedal(String medalId) {
		List<AchievementExt> achievementArray = new ArrayList<>();
		Collection<AchievementExt> datas = GameData.Achievements.values();

		for (AchievementCO d : datas) {
			AchievementExt data = (AchievementExt) d;
			if (ACHIEVEMENT_CONDITION_TYPE.GET_MEDAL.value != data.conditionType)
				continue;

			// TODO 判断条件可能不对
			if (!data.targetID.equals(medalId))
				continue;

			achievementArray.add(data);
		}

		if (achievementArray.size() > 0) {
			achievementService._onConditionChange(1, achievementArray, player, false);
		}
	};

	public void onGetNecklace(String medalId) {
		List<AchievementExt> achievementArray = new ArrayList<>();
		Collection<AchievementExt> datas = GameData.Achievements.values();

		for (AchievementCO d : datas) {
			AchievementExt data = (AchievementExt) d;
			if (ACHIEVEMENT_CONDITION_TYPE.GET_NECKLACE.value != data.conditionType)
				continue;

			// TODO 判断条件可能不对
			if (!data.targetID.equals(medalId))
				continue;

			achievementArray.add(data);
		}

		if (achievementArray.size() > 0) {
			achievementService._onConditionChange(1, achievementArray, player, false);
		}
	};

	/**
	 * 获得指定ID的装备
	 */
	public void onGetEquipment(String code) {
		List<AchievementExt> achievementArray = achievementService.findByConditionTypeAndTargetIdString(ACHIEVEMENT_CONDITION_TYPE.GET_EQUIPMENT, code);

		if (achievementArray.size() > 0) {
			achievementService._onConditionChange(1, achievementArray, player, true);
		}
	};

	public void onEquipEnchant() {
		List<AchievementExt> achievementArray = achievementService.findByConditionType(ACHIEVEMENT_CONDITION_TYPE.EQUIPMENT_ENCHANT);

		if (achievementArray.size() > 0) {
			achievementService._onConditionChange(1, achievementArray, player, true);
		}
	};

	public void onGetPet(int color) {
		List<AchievementExt> achievementArray = achievementService.findByConditionType(ACHIEVEMENT_CONDITION_TYPE.GET_PET);

		if (achievementArray.size() > 0) {
			achievementService._onConditionChange(1, achievementArray, player, true);
		}

		// achievementDatas = achievementServiceNew.onGetPetColor(this.player, color);
		List<AchievementExt> achievementArray_petColor = new ArrayList<>();

		List<AchievementExt> datas = achievementService.findByConditionType(ACHIEVEMENT_CONDITION_TYPE.GET_QUALITY_PET);

		for (AchievementExt data : datas) {
			if (Integer.parseInt(data.targetID) > color)
				continue;

			achievementArray_petColor.add(data);
		}

		if (achievementArray_petColor.size() > 0) {
			achievementService._onConditionChange(1, achievementArray_petColor, player, true);
		}
	};

	public void onGetPetLevel(int petId, int level) {
		List<AchievementExt> achievementArray = achievementService.findByConditionType(ACHIEVEMENT_CONDITION_TYPE.PET_LEVEL);
		List<AchievementExt> tmpArray = new ArrayList<>();
		if (achievementArray.size() > 0) {
			for (AchievementExt achievement : achievementArray) {
				if (Integer.parseInt(achievement.targetID) <= level) {
					tmpArray.add(achievement);
				}
			}

			achievementService._onConditionChange(petId, tmpArray, player, false);
		}
	};

	public void onPetUpGrade(int olderLevel, int level) {
		List<AchievementExt> achievementArray = achievementService.findByConditionType(ACHIEVEMENT_CONDITION_TYPE.PET_UPGRADE_LEVEL);
		List<AchievementExt> tmpArray = new ArrayList<>();
		if (achievementArray.size() > 0) {
			for (AchievementExt achievement : achievementArray) {
				if (Integer.parseInt(achievement.targetID) > olderLevel && Integer.parseInt(achievement.targetID) <= level) {
					tmpArray.add(achievement);
				}
			}

			achievementService._onConditionChange(1, tmpArray, player, false);
		}
	};

	public void onPetTransformLevel(int olderLevel, int level) {
		List<AchievementExt> achievementArray = achievementService.findByConditionType(ACHIEVEMENT_CONDITION_TYPE.PET_TRANSFORM_LEVEL);
		List<AchievementExt> tmpArray = new ArrayList<>();
		if (achievementArray.size() > 0) {
			for (AchievementExt achievement : achievementArray) {
				if (Integer.parseInt(achievement.targetID) > olderLevel && Integer.parseInt(achievement.targetID) <= level) {
					tmpArray.add(achievement);
				}
			}

			achievementService._onConditionChange(1, tmpArray, player, true);
		}
	};

	public void onHorseLevelChange(int level) {
		List<AchievementExt> achievementArray = achievementService.findByConditionType(ACHIEVEMENT_CONDITION_TYPE.RIDE_DEVELOPMENT);

		if (achievementArray.size() > 0) {
			achievementService._onConditionChange(level, achievementArray, player, false);
		}
	};

	public void onWingLevelChange(int id) {
		List<AchievementExt> achievementArray = achievementService.findByConditionTypeAndTargetIdInt(ACHIEVEMENT_CONDITION_TYPE.WING_LEVEL, id);

		if (achievementArray.size() > 0) {
			achievementService._onConditionChange(1, achievementArray, player, false);
		}
	};

	// public void onFillGem(int num) {
	// List<AchievementExt> achievementArray =
	// achievementService.findByConditionType(ACHIEVEMENT_CONDITION_TYPE.ACHIEVEMENT_CONDITION_FILL_GEM);
	//
	// if (achievementArray.size() > 0) {
	// achievementService._onConditionChange(num, achievementArray, player, true);
	// }
	// };

	public void onFishing() {
		List<AchievementExt> achievementArray = achievementService.findByConditionType(ACHIEVEMENT_CONDITION_TYPE.FISH_ITEM);

		if (achievementArray.size() > 0) {
			achievementService._onConditionChange(1, achievementArray, player, true);
		}
	};

	public void onSkillLevelChange(int level) {
		List<AchievementExt> achievementArray = achievementService.findByConditionType(ACHIEVEMENT_CONDITION_TYPE.SKILL_LEVEL);

		if (achievementArray.size() > 0) {
			achievementService._onConditionChange(level, achievementArray, player, false);
		}
	};

	public void onPassedDungeon(int id) {
		List<AchievementExt> achievementArray = achievementService.findByConditionTypeAndTargetIdInt(ACHIEVEMENT_CONDITION_TYPE.DUNGEON_PASSED, id);

		if (achievementArray.size() > 0) {
			achievementService._onConditionChange(1, achievementArray, player, true);
		}
	};

	public void onWinSolo(int rankId) {
		List<AchievementExt> achievementArray = achievementService.findByConditionType(ACHIEVEMENT_CONDITION_TYPE.SOLO_WIN);

		if (achievementArray.size() > 0) {
			achievementService._onConditionChange(1, achievementArray, player, true);
		}

		// achievementDatas = achievementServiceNew.onSoloRank(this.player, rankId);
		List<AchievementExt> achievementArray_soloRank = achievementService.findByConditionTypeAndTargetIdInt(ACHIEVEMENT_CONDITION_TYPE.SOLO_RANK, rankId);

		if (achievementArray_soloRank.size() > 0) {
			achievementService._onConditionChange(1, achievementArray_soloRank, player, true);
		}
	};

	public void onArenaKill() {
		List<AchievementExt> achievementArray = achievementService.findByConditionType(ACHIEVEMENT_CONDITION_TYPE.ARENA_KILL_PLAYER);

		if (achievementArray.size() > 0) {
			achievementService._onConditionChange(1, achievementArray, player, true);
		}
	};

	public void onArenaScore(int score) {
		List<AchievementExt> achievementArray = achievementService.findByConditionType(ACHIEVEMENT_CONDITION_TYPE.ARENA_SCORE);

		if (achievementArray.size() > 0) {
			achievementService._onConditionChange(score, achievementArray, player, false);
		}
	};

	public void onGetAllyGold(int gold) {
		List<AchievementExt> achievementArray = achievementService.findByConditionType(ACHIEVEMENT_CONDITION_TYPE.ALLY_GOLD);

		if (achievementArray.size() > 0) {
			achievementService._onConditionChange(gold, achievementArray, player, true);
		}
	};

	public void onGetAllyKillCount(int count) {
		List<AchievementExt> achievementArray = achievementService.findByConditionType(ACHIEVEMENT_CONDITION_TYPE.ALLY_KILL_PLAYER);

		if (achievementArray.size() > 0) {
			achievementService._onConditionChange(count, achievementArray, player, false);
		}
	};

	public void onFriendNumber(int friendNum) {
		List<AchievementExt> achievementArray = achievementService.findByConditionType(ACHIEVEMENT_CONDITION_TYPE.FRIENDS_NUM);

		if (achievementArray.size() > 0) {
			achievementService._onConditionChange(friendNum, achievementArray, player, true);
		}
	};

	public void onGemCombine(String code, int num) {
		List<AchievementExt> achievementArray = achievementService.findByConditionTypeAndTargetIdString(ACHIEVEMENT_CONDITION_TYPE.GEM_COMBINE, code);

		if (achievementArray.size() > 0) {
			achievementService._onConditionChange(num, achievementArray, player, true);
		}
	};

	/**
	 * 杀怪处理
	 */
	public void onTaskEvent(TaskEvent event) {
		int eventName = event.type;
		if (eventName == EventType.killMonster.getValue()) {
			this.killNpc(event.params[0].toString());
		}
	};

	/**
	 * 通用事件处理
	 */
	public void onEvent(ACHIEVEMENT_CONDITION_TYPE condition_type, Object... params) {
		List<AchievementExt> achievementArray = null;
		int num = 0;
		switch (condition_type) {
		case GEM_COMBINE:
			achievementArray = achievementService.findByConditionTypeAndTargetIdString(ACHIEVEMENT_CONDITION_TYPE.GEM_COMBINE, (String) params[0]);
			break;
		default:
			break;
		}

		if (achievementArray.size() > 0) {
			achievementService._onConditionChange(num, achievementArray, player, true);
		}
	}

	/****************************************************************************************************************
	 * 其他模块调用接口 end
	 *****************************************************************************************************************/

	/**
	 * 红点角标
	 */
	public List<SuperScriptType> getSuperScript() {
		List<SuperScriptType> list = new ArrayList<>();
		int number = 0;
		/** 章节红点 */
		Map<Integer, Integer> chapters = new HashMap<>();
		for (int typeId : GameData.AchievementConfigs.keySet()) {
			chapters.put(typeId, 0);
		}
		// 成就 功能未开启
		// if
		// (!this.player.functionOpenManager.isOpen(Const.FunctionType.ACHIEVEMENT.getValue()))
		// {
		// SuperScriptType.Builder data = SuperScriptType.newBuilder();
		// data.setType(Const.SUPERSCRIPT_TYPE.GROWUP_TARGET.getValue());
		// data.setNumber(number);
		// list.add(data.build());
		// return list;
		// }

		// 计算未领取的单条成就数
		for (AchievePO achievePO : this.achievementDataPO.achievements.values()) {
			AchievementExt prop = GameData.Achievements.get(achievePO.id);
			if (prop == null) {
				Out.error(AchievementManager.class, achievePO.id);
				continue;
			}
			// 章节未开启
			AchievementConfigExt config = GameData.AchievementConfigs.get(prop.chapterID);
			// if (config.quest != 0 &&
			// !player.taskManager.finishedNormalTasks.containsKey(config.quest)) {
			// continue;
			// }
			if (!AchievementServiceNew.GetChapterOpened(player.getId(), prop.chapterID)) {
				continue;
			}
			if (achievePO.scheduleCurr >= prop.targetNum) {
				if (achievePO.awardState == 0) {
					number += 1;

					// 章节红点
					if (chapters.containsKey(config.typeId)) {
						chapters.put(config.typeId, chapters.get(config.typeId) + 1);
					} else {
						chapters.put(config.typeId, 1);
					}
				}
			}
		}
		// 计算未领取的累计成就奖励
		for (AchievementConfigExt config : GameData.AchievementConfigs.values()) {
			// if (player.getLevel() < config.lv) {
			// continue;
			// }
			// if (config.quest != 0 &&
			// !player.taskManager.finishedNormalTasks.containsKey(config.quest)) {
			// continue;
			// }
			if (!AchievementServiceNew.GetChapterOpened(player.getId(), config.typeId)) {
				continue;
			}
			if (this.achievementDataPO.receivedAwards.contains(config.typeId)) {
				continue;
			}
			List<AchievementExt> list_achieve = GameData.findAchievements(t -> {
				return t.chapterID == config.typeId;
			});
			if (list_achieve.size() == 0) {
				continue;
			}

			// 章节完成的成就数
			int recordCount = 0;
			for (AchievementExt achievementProp : list_achieve) {
				AchievePO achieve = achievementRecordMap.get(achievementProp.id);

				if (achieve == null) {
					continue;
				}
				if(achieve.awardState==1) {
					recordCount++;
				}
				else if (achievementService.isComplete(achieve, achievementProp)) {
					recordCount++;
				}
			}

			if (recordCount == list_achieve.size()) {
				number += 1;

				// 章节红点
				if (chapters.containsKey(config.typeId)) {
					chapters.put(config.typeId, chapters.get(config.typeId) + 1);
				} else {
					chapters.put(config.typeId, 1);
				}
			}
		}

		SuperScriptType.Builder data = SuperScriptType.newBuilder();
		data.setType(Const.SUPERSCRIPT_TYPE.GROWUP_TARGET.getValue());
		data.setNumber(number);
		list.add(data.build());

		// 章节红点
		for (int chapterId : chapters.keySet()) {
			SuperScriptType.Builder sc = SuperScriptType.newBuilder();
			sc.setType(Const.SUPERSCRIPT_TYPE.GROWUP_TARGET.getValue() + (chapterId / 10));
			sc.setNumber(chapters.get(chapterId));
			list.add(sc.build());
		}

		// 已完成成就总数
		Map<Integer, int[]> progressMap = AchievementServiceNew.GetChapterProgress(player.getId());
		int progress = 0;
		for (int[] ar : progressMap.values()) {
			progress += ar[0];
		}
		SuperScriptType.Builder progressData = SuperScriptType.newBuilder();
		progressData.setType(Const.SUPERSCRIPT_TYPE.GROWUP_TOTAL.getValue());
		progressData.setNumber(progress);
		list.add(progressData.build());
		return list;
	};

	public void updateSuperScript() {
		player.updateSuperScriptList(this.getSuperScript());
	};

	/**
	 * 计算时装属性
	 */
	public Map<PlayerBtlData, Integer> calAllInfluence() {
		Map<PlayerBtlData, Integer> data = new HashMap<>();

		int haveCount = 0;
		for (HolyArmour holyArmour : achievementDataPO.holyArmourMap.values()) {
			if (holyArmour.states == 3) {
				ArmourAttributeExt armourAttributeExt = GameData.ArmourAttributes.get(holyArmour.id);
				AttributeUtil.addData2AllData(armourAttributeExt.atts, data);
				haveCount++;
			}
		}
		if (haveCount > 0) {
			final int x = haveCount;
			for (ArmourPlusExt armourPlusExt : GameData.findArmourPluss(v -> x >= v.activateNum)) {
				AttributeUtil.addData2AllData(armourPlusExt.atts, data);
			}
		}
		return data;
	}

	/**
	 * 激活元始圣甲
	 */
	public boolean activateHolyArmour(int id) {
		HolyArmour armour = player.achievementManager.achievementDataPO.holyArmourMap.get(id);
		if (armour.states != 2) {
			return false;
		}
		armour.states = 3;
		this.player.btlDataManager.data_holy_armour = calAllInfluence();
		this.player.btlDataManager.calFinalData();
		this.player.onArmourActive();
		this.player.refreshBattlerServerAvatar();

		// BI日志...
		Map<Integer, String> data = new HashMap<>();
		for (HolyArmour holyArmour : achievementDataPO.holyArmourMap.values()) {
			if (holyArmour.states == 3) {
				ArmourAttributeExt armourAttributeExt = GameData.ArmourAttributes.get(holyArmour.id);
				if (armourAttributeExt != null) {
					data.put(holyArmour.id, armourAttributeExt.name);
				}
			}
		}
		BILogService.getInstance().ansycReportHolyArmour(player.getPlayer(), data);
		
		Out.info("玩家：",player.getId(),"激活了元始圣甲，部位id:",id);
		return true;
	};

	public void FinishChapterAchievement(int chapterId) {
		Map<Integer, AchievementDataPO.AchievePO> achievementRecords = player.achievementManager.getAchievementRecords();

		List<AchievementExt> achievementArray = GameData.findAchievements((t) -> {
			return t.chapterID == chapterId;
		});

		for (AchievementExt achievement : achievementArray) {
			// AchievementConfigExt config =
			// GameData.AchievementConfigs.get(achievement.chapterID);

			AchievementDataPO.AchievePO achievementRecord = achievementRecords.get(achievement.id);
			if (achievementRecord == null) {
				achievementRecord = new AchievePO();
				achievementRecord.id = achievement.id;
				achievementRecord.scheduleCurr = 0;
				achievementRecords.put(achievement.id, achievementRecord);
			}
			achievementRecord.scheduleCurr = achievement.targetNum;

		}

		player.achievementManager.updateSuperScript();
	}

}
