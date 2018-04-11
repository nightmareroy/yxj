package com.wanniu.game.petNew;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.alibaba.fastjson.JSONObject;
import com.wanniu.core.common.IntIntPair;
import com.wanniu.core.game.LangService;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.logfs.Out;
import com.wanniu.core.util.StringUtil;
import com.wanniu.game.area.AreaDataConfig;
import com.wanniu.game.area.AreaUtil;
import com.wanniu.game.common.CommonUtil;
import com.wanniu.game.common.Const;
import com.wanniu.game.common.Const.BiLogType;
import com.wanniu.game.common.Const.GOODS_CHANGE_TYPE;
import com.wanniu.game.common.Const.ManagerType;
import com.wanniu.game.common.Const.PlayerBtlData;
import com.wanniu.game.common.Const.PlayerEventType;
import com.wanniu.game.common.Const.TaskType;
import com.wanniu.game.common.ModuleManager;
import com.wanniu.game.common.msg.MessageUtil;
import com.wanniu.game.data.GameData;
import com.wanniu.game.data.PetAssociateCO;
import com.wanniu.game.data.PetItemCO;
import com.wanniu.game.data.base.MapBase;
import com.wanniu.game.data.ext.BaseDataExt;
import com.wanniu.game.data.ext.PetAssociateExt;
import com.wanniu.game.player.AttributeUtil;
import com.wanniu.game.player.BILogService;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.player.bi.LogReportService;
import com.wanniu.game.poes.PetNewPO;
import com.wanniu.game.poes.PlayerPetsNewPO;
import com.wanniu.game.rank.RankType;
import com.wanniu.game.sevengoal.SevenGoalManager.SevenGoalTaskType;
import com.wanniu.game.util.BlackWordUtil;

import pomelo.area.PetNewHandler.AddExpByItemResponse;
import pomelo.area.PetNewHandler.ChangePetNameNewResponse;
import pomelo.area.PetNewHandler.GetAllPetsInfoResponse;
import pomelo.area.PetNewHandler.GetPetInfoNewResponse;
import pomelo.area.PetNewHandler.OnNewPetDetailPush;
import pomelo.area.PetNewHandler.SummonPetResponse;
import pomelo.area.PetNewHandler.UpGradeUpLevelResponse;
import pomelo.area.PetNewHandler.UpgradeOneLevelResponse;
import pomelo.area.PetNewHandler.UpgradeToTopResponse;
import pomelo.area.PlayerHandler.SuperScriptType;

public class PetManager extends ModuleManager {
	public PlayerPetsNewPO petsPO;
	public WNPlayer player;
	public Map<Integer, PetNew> playerPets;
	public Map<PlayerBtlData, Integer> masterAttr = new ConcurrentHashMap<>();
	public Map<PlayerBtlData, Integer> masterAttrOnOutFight = new HashMap<>();
//	public Map<PlayerBtlData, Integer> masterAssociate = new HashMap<>();

	public PetManager(WNPlayer player, PlayerPetsNewPO petsPO) {
		this.player = player;
		this.petsPO = petsPO;
		playerPets = new HashMap<>();
	}
	
	public void init() {
		for (PetNewPO petPO : petsPO.pets.values()) {
			PetNew pet = new PetNew(petPO, player);
			playerPets.put(petPO.id, pet);
		}
		this.refreshMasterAttr();
	}

	/**
	 * 召唤宠物
	 * 
	 * @param id
	 * @return 0成功，-1未知错误，-2道具不够
	 */
	public SummonPetResponse.Builder summonPet(int id) {
		SummonPetResponse.Builder res = SummonPetResponse.newBuilder();
		Map<Integer, BaseDataExt> map = GameData.BaseDatas;
		// 找不到配置的宠物或者已经有宠物了
		if (!map.containsKey(id) || petsPO.pets.containsKey(id)) {
			res.setS2CCode(PomeloRequest.FAIL);
			res.setS2CMsg(LangService.getValue("SOMETHING_ERR"));
			Out.warn("宠物召唤失败,配表不存在或者已经有该宠物了!,roleId=", player.getId(), ",id=", id);
			return res;
		}
		BaseDataExt prop = map.get(id);
		String itemCode = prop.petItemCode;
		int itemCount = prop.itemCount;
		if (!player.bag.isItemNumEnough(itemCode, itemCount)) {
			res.setS2CCode(PomeloRequest.FAIL);
			res.setS2CMsg(LangService.getValue("NOT_ENOUGH_ITEM"));
			Out.warn("宠物召唤失败,道具不足!,roleId=", player.getId(), ",id=", id);
			return res;
		}

		player.bag.discardItem(itemCode, itemCount, GOODS_CHANGE_TYPE.pet);

		PetNew pet = createPet(id, prop);
		if (pet.po.id == petsPO.fightPetId)
			res.setS2CFight(1);
		else
			res.setS2CFight(0);
		res.setS2CCode(PomeloRequest.OK);
		Out.info("宠物召唤成功!,roleId=", player.getId(), ",id=", id);
		return res;
	}

	/**
	 * 使用物品召唤宠物
	 * 
	 * @param id
	 * @return
	 */
	public String summonPetByItem(int id) {
		if (playerPets.containsKey(id))
			return LangService.getValue("ALREADY_HAVE_PET");
		BaseDataExt prop = GameData.BaseDatas.get(id);
		if (prop == null)
			return LangService.getValue("SOMETHING_ERR");
		createPet(id, prop);
		return null;
	}

	public PetNew createPet(int id, BaseDataExt prop) {
		PetNewPO petPo = new PetNewPO();
		petPo.id = id;
		petPo.name = prop.petName;
		petPo.level = prop.initLevel;
		petPo.upLevel = 0;
		petPo.skills = new HashMap<>();
		int index = 0;
		for (IntIntPair iip : prop.getInitSkills()) {
			PetSkill skill = new PetSkill(iip.first, iip.second, index, 0);
			petPo.skills.put(iip.first, skill);
			index++;
		}
		petPo.passiveSkills = new HashMap<>();
		// 以下为测试代码
		// {
		// petPo.passiveSkills = new HashMap<>();
		// PetSkill skill = new PetSkill(802020, 1, 0, 0);
		// petPo.passiveSkills.put(802020, skill);
		// }
		petsPO.pets.put(petPo.id, petPo);
		PetNew pet = new PetNew(petPo, player);
		playerPets.put(id, pet);
		if (playerPets.size() == 1) {
			this.petOutFight(petPo.id, 1);
		}
		refreshMasterAttr();
		player.onPetPropChange();
		String key = MessageUtil.getColor(pet.prop.qcolor);
		if (key != null && key.length() > 0) {
			String color = LangService.getValue(key).replace("{a}", petPo.name);
			String str = LangService.getValue("GAIN_PET").replace("{petName}", color);
			this.player.sendSysTip(str, Const.TipsType.NORMAL);
		}
		OnNewPetDetailPush.Builder data = OnNewPetDetailPush.newBuilder();
		data.setS2CCode(Const.CODE.OK);
		data.setS2CMsg("");
		data.setPetInfo(pet.toJson4PayLoad());

		player.receive("area.petNewPush.onNewPetDetailPush", data.build());
		this.player.achievementManager.onGetPet(prop.qcolor);
		this.player.achievementManager.onGetPetLevel(petPo.id, petPo.level);

		LogReportService.getInstance().ansycReportPetSkin(player, id);
		BILogService.getInstance().ansycReportPetActivate(player.getPlayer(), id, prop.petName);
		return pet;
	}

	public GetAllPetsInfoResponse.Builder toJson4Payload() {
		GetAllPetsInfoResponse.Builder data = GetAllPetsInfoResponse.newBuilder();
		data.setS2CCode(PomeloRequest.OK);
		data.setS2CFightingPetId(petsPO.fightPetId);
		for (PetNew pet : playerPets.values()) {
			data.addS2CPetInfo(pet.toJson4PayLoad());
		}
		return data;
	}

	public void refreshMasterAttr() {
		masterAttr = new HashMap<>();
		masterAttrOnOutFight = new HashMap<>();
		for (PetNew pet : playerPets.values()) {
			AttributeUtil.addData2AllData(pet.attr_master, masterAttr);
			if (this.petsPO.fightPetId == pet.po.id) {// 按表格配置比例转换出战宠物自身属性到主人身上
				int rate = 0;

				switch (pet.prop.qcolor) {
				case 1:
					rate = GameData.PetConfigs.get("PetPro.Transform1").intValue;
					break;
				case 2:
					rate = GameData.PetConfigs.get("PetPro.Transform2").intValue;
					break;
				case 3:
					rate = GameData.PetConfigs.get("PetPro.Transform3").intValue;
					break;
				case 4:
					rate = GameData.PetConfigs.get("PetPro.Transform4").intValue;
					break;
				}
				if (rate == 0) {
					Out.error("Can't find qcolor by petid:" + pet.po.id + " qcolor:" + pet.prop.qcolor);
					continue;
				}

				for (PlayerBtlData btl : pet.attr_all_pet.keySet()) {
					if (btl != PlayerBtlData.CritDamage) {// 暴击伤害不转换到主人身上
						int val = pet.attr_all_pet.get(btl) * rate / 100;
						masterAttrOnOutFight.put(btl, val);
					}
				}
			}
		}
		AttributeUtil.addData2AllData(calAssociateInfluence(), masterAttr);

		// 刷新宠物，就是要更新排行榜...
		if (player.rankManager != null) {
			this.updateRank();
		}
	}

	private void updateRank() {
		PetNewPO petMax = null;
		int petFightPowerMax = 0;
		for (PetNewPO pet : petsPO.pets.values()) {
			int petFightPower = pet.fightPower;
			if (petFightPower > petFightPowerMax) {
				petFightPowerMax = petFightPower;
				petMax = pet;
			}
		}
		if (petMax != null && player.rankManager != null) {
			player.rankManager.onEvent(RankType.PET, petMax.id, petMax.name, petMax.fightPower);
		}
	}

	public String petOutFight(int petId, int type) {
		PetNew pet = playerPets.get(petId);
		if (pet == null) {
			return LangService.getValue("PET_NOT_EXIST");
		}
		if (type == 1) { // 出战
			if (petsPO.fightPetId == petId) {
				return LangService.getValue("ALREADY_BATTLE");
			}
			if (StringUtil.isEmpty(this.getFightingPetId())) {

				this.player.getXmdsManager().refreshPlayerPetDataChange(this.player.getId(), PetOperatorType.Add.getValue(), pet.getBattlerServerPetData());
			} else {
				this.player.getXmdsManager().refreshPlayerPetDataChange(this.player.getId(), PetOperatorType.Replace.getValue(), pet.getBattlerServerPetData());
			}
			petsPO.fightPetId = petId;
			this.player.sendSysTip(LangService.getValue("BATTLING"), Const.TipsType.NO_BG);

		} else if (type == 0) { // 休息
			if (StringUtil.isEmpty(this.getFightingPetId()) || petsPO.fightPetId != petId) {
				return LangService.getValue("ALREADY_REST");
			}
			petsPO.fightPetId = 0;
			MapBase prop = AreaUtil.getAreaProp(player.getAreaId());
			if (prop != null && 1 == prop.takePet) {
				try {
					this.player.getXmdsManager().refreshPlayerPetDataChange(this.player.getId(), PetOperatorType.Delete.getValue(), null);
				} catch (Exception e) {
					Out.error("syncNowPetData error", e);
				}
				this.player.sendSysTip(LangService.getValue("RESTING"), Const.TipsType.NO_BG);
			}
		} else {
			return LangService.getValue("PARAM_ERROR");
		}
		// 刷新出战宠物对主人的属性加成
		refreshMasterAttr();
		this.player.onPetPropChange();
		// this.player.initAndCalAllInflu(null);
		// this.player.pushAndRefreshEffect(false);
		player.player.fightingPetId = String.valueOf(petsPO.fightPetId);
		this.player.pushDynamicData("fightingPetId", player.player.fightingPetId);

		BILogService.getInstance().ansycReportPetBattle(player.getPlayer(), petId, pet.prop.petName, pet.po.level, pet.po.upLevel, type);
		return null;
	}

	/**
	 * @param id
	 * @param name
	 * @return 0成功，-1没有该宠物.-2名字长度，-3名字不合法，-4钱不够
	 */
	public ChangePetNameNewResponse.Builder changePetName(int id, String petName) {
		ChangePetNameNewResponse.Builder result = ChangePetNameNewResponse.newBuilder();
		result.setS2CCode(PomeloRequest.FAIL);
		PetNew pet = playerPets.get(id);
		if (pet == null) {
			result.setS2CMsg(LangService.getValue("SOMETHING_ERR"));
			return result;
		}
		// 判定名字是否合法
		if (petName == null || petName.length() <= 0) {
			result.setS2CMsg(LangService.getValue("PET_NAME_NULL"));
			return result;
		}
		if (petName.length() > 6) {
			result.setS2CMsg(LangService.getValue("PET_NAME_LONG"));
			return result;
		}
		if (!CommonUtil.isLegalString(petName) || BlackWordUtil.isIncludeBlackString(petName)) {
			result.setS2CMsg(LangService.getValue("ILLEGAL_CHARACTER"));
			return result;
		}
		if (!player.moneyManager.costDiamond(GameData.PetConfigs.get("Rename.Cost").intValue, GOODS_CHANGE_TYPE.petChangeName)) {
			result.setS2CMsg(LangService.getValue("NOT_ENOUGH_DIAMOND"));
			return result;
		}
		result.setS2CCode(PomeloRequest.OK);
		// player.pushDynamicData("diamond", player.player.diamond);
		pet.po.name = petName;

		this.updateRank();

		if (this.petsPO.fightPetId == pet.po.id) {
			this.player.getXmdsManager().refreshPlayerPetDataChange(this.player.getId(), PetOperatorType.Reset.getValue(), pet.getBattlerServerPetData());
		}
		result.setPetInfo(pet.toJson4PayLoad());
		return result;
	}

	/**
	 * @param id
	 * @param itemCode
	 * @param itemCount
	 * @return 0成功,-1没有该宠物,-2不能使用该物品,-3物品不足,-4已经升到顶了
	 */
	public AddExpByItemResponse.Builder addExpByItem(int id, String itemCode, int itemCount) {
		AddExpByItemResponse.Builder result = AddExpByItemResponse.newBuilder();
		result.setS2CCode(PomeloRequest.FAIL);
		PetNew pet = playerPets.get(id);
		if (pet == null) {
			result.setS2CMsg(LangService.getValue("SOMETHING_ERR"));
			Out.warn("宠物单次加经验失败,找不到该宠物!,roleId=", player.getId(), ",宠物id=", id, ",道具Id=", itemCode);
			return result;
		}
		if (!pet.prop.list_ExpCode.contains(itemCode)) {
			result.setS2CMsg(LangService.getValue("SOMETHING_ERR"));
			Out.warn("宠物单次加经验失败,配表找不到宠物使用的经验药水!,roleId=", player.getId(), ",宠物id=", id, ",道具Id=", itemCode);
			return result;
		}
		if (!player.bag.isItemNumEnough(itemCode, itemCount)) {
			result.setS2CMsg(LangService.getValue("NOT_ENOUGH_ITEM"));
			return result;
		}
		if (!pet.canAddExp()) {
			result.setS2CMsg(LangService.getValue("PET_MAX_LEVEL"));
			Out.warn("宠物单次加经验失败,等级超上限了!,roleId=", player.getId(), ",宠物id=", id, ",道具Id=", itemCode, ",当前等级为:", pet.po.level, ",当前可提升最大等级为:", pet.getCurMaxLv());
			return result;
		}
		result.setS2CCode(PomeloRequest.OK);
		PetItemCO prop_item = GameData.PetItems.get(itemCode);
		int exp = prop_item.min;
		int remain = itemCount;
		// 因为有等级限制，所以用一个检查一下是否可以继续使用
		while (pet.canAddExp() && remain > 0) {
			remain--;
			pet.addExp(exp, true);
		}
		player.bag.discardItem(itemCode, itemCount - remain, GOODS_CHANGE_TYPE.pet);
		result.setPetInfo(pet.toJson4PayLoad());

		// 更新任务
		player.taskManager.dealTaskEvent(TaskType.PET_TRAIN, String.valueOf(id), 1);
		Out.info("宠物单次加经验成功!,roleId=", player.getId(), ",宠物id=", id, ",道具Id=", itemCode, "，当前等级=", pet.po.level, ",当前经验为:", pet.po.exp);
		return result;
	}

	/**
	 * 请求升到顶
	 * 
	 * @param id
	 * @return 0成功,-1没有该宠物,-2已经升到顶了
	 */
	public UpgradeToTopResponse.Builder reqUpgrade2Top(int id) {
		UpgradeToTopResponse.Builder result = UpgradeToTopResponse.newBuilder();
		result.setS2CCode(PomeloRequest.FAIL);
		PetNew pet = playerPets.get(id);
		if (pet == null) {
			result.setS2CMsg(LangService.getValue("SOMETHING_ERR"));
			Out.warn("宠物升级最高等级失败,找不到该宠物!,roleId=", player.getId(), ",宠物id=", id);
			return result;
		}
		if (!pet.canAddExp()) {
			result.setS2CMsg(LangService.getValue("PET_MAX_LEVEL"));
			Out.warn("宠物升级最高等级失败,等级超上限了!,roleId=", player.getId(), ",宠物id=", id, ",当前等级为:", pet.po.level, ",当前可提升最大等级为:", pet.getCurMaxLv());
			return result;
		}
		boolean flag = false;
		for (String itemCode : pet.prop.list_ExpCode) {
			if (player.bag.findItemNumByCode(itemCode) > 0) {
				flag = true;
				break;
			}
		}
		if (!flag) {
			result.setS2CMsg(LangService.getValue("NOT_ENOUGH_ITEM"));
			return result;
		}
		result.setS2CCode(PomeloRequest.OK);
		boolean hasUpgrade = false;
		boolean hasItem = true;
		int loopCount = 0;
		while (pet.canAddExp() && hasItem && loopCount++ < 100) {
			boolean isItemFlag = false;
			long nextLvExp = pet.getNextLevelneedExp();
			for (String itemCode : pet.prop.list_ExpCode) {
				if (!pet.canAddExp())
					break;
				PetItemCO prop_item = GameData.PetItems.get(itemCode);
				int exp = prop_item.min;
				int itemCount = player.bag.findItemNumByCode(itemCode);
				if (itemCount <= 0) {
					continue;
				}
				isItemFlag = true;
				long canAddExp = 1l * itemCount * exp;
				int removeItemCount = itemCount;
				int result_addExp = 0;
				if (nextLvExp >= canAddExp) {
					result_addExp = pet.addExp((int) canAddExp, false);
				} else {
					long t1 = nextLvExp % exp;
					removeItemCount = t1 == 0 ? (int) (nextLvExp / exp) : (int) (nextLvExp / exp + 1);
					result_addExp = pet.addExp((int) nextLvExp, false);
				}
				if (result_addExp == 1) {
					hasUpgrade = true;
					nextLvExp = pet.getNextLevelneedExp();
				}
				player.bag.discardItem(itemCode, removeItemCount, GOODS_CHANGE_TYPE.pet);
			}
			hasItem = isItemFlag;
		}
		if (loopCount >= 100) {
			Out.warn("一键升顶时判断有异常情况,怀疑死循环,playerId=", this.player.getId());
		}
		// 如果升级了就同步战斗服，不要在pet的addExp里面同步，如果升级跨度大的话每升一级都会去同步战斗服，很耗时间，所以在升级完了之后只通知一次战斗服就行了
		if (hasUpgrade && this.petsPO.fightPetId == pet.po.id) {
			player.getXmdsManager().refreshPlayerPetDataChange(player.getId(), PetOperatorType.Reset.getValue(), pet.getBattlerServerPetData());
		}
		if (hasUpgrade) {
			refreshMasterAttr();
			player.onPetPropChange();
			pet.pushInfoUpdate();
		} else {
			pet.pushExpUpdate();
		}
		result.setPetInfo(pet.toJson4PayLoad());

		// 更新任务
		player.taskManager.dealTaskEvent(TaskType.PET_TRAIN, String.valueOf(id), 1);
		Out.info("宠物升级最高等级成功!,roleId=", player.getId(), ",宠物id=", id, "，当前等级=", pet.po.level, ",当前经验为:", pet.po.exp);
		return result;
	}

	/**
	 * 请求升一级
	 * 
	 * @param id
	 * @return 0成功,-1没有该宠物,-2已经升到顶了
	 */
	public UpgradeOneLevelResponse.Builder reqUpgradeOneLevel(int id) {
		UpgradeOneLevelResponse.Builder result = UpgradeOneLevelResponse.newBuilder();
		result.setS2CCode(PomeloRequest.FAIL);
		PetNew pet = playerPets.get(id);
		if (pet == null) {
			result.setS2CMsg(LangService.getValue("SOMETHING_ERR"));
			return result;
		}
		if (!pet.canAddExp()) {
			result.setS2CMsg(LangService.getValue("PET_MAX_LEVEL"));
			return result;
		}
		boolean flag = false;
		for (String itemCode : pet.prop.list_ExpCode) {
			if (player.bag.findItemNumByCode(itemCode) > 0) {
				flag = true;
				break;
			}
		}
		if (!flag) {
			result.setS2CMsg(LangService.getValue("NOT_ENOUGH_ITEM"));
			return result;
		}

		result.setS2CCode(PomeloRequest.OK);
		int targetLevel = pet.po.level + 1;
		boolean hasUpgrade = false;
		for (String itemCode : pet.prop.list_ExpCode) {
			if (!pet.canAddExp())
				break;
			PetItemCO prop_item = GameData.PetItems.get(itemCode);
			int exp = prop_item.min;
			int itemCount = player.bag.findItemNumByCode(itemCode);
			int remain = itemCount;
			int result_addExp = 0;
			while (pet.po.level < targetLevel && remain > 0) {
				remain--;
				result_addExp = pet.addExp(exp, false);
				if (!hasUpgrade && result_addExp == 1) {
					hasUpgrade = true;
				}
			}
			player.bag.discardItem(itemCode, itemCount - remain, GOODS_CHANGE_TYPE.pet);
		}
		result.setPetInfo(pet.toJson4PayLoad());

		// 升级后刷
		// 如果升级了就同步战斗服，不要在pet的addExp里面同步，如果升级跨度大的话每升一级都会去同步战斗服，很耗时间，所以在升级完了之后只通知一次战斗服就行了
		if (hasUpgrade && this.petsPO.fightPetId == pet.po.id) {
			player.getXmdsManager().refreshPlayerPetDataChange(player.getId(), PetOperatorType.Reset.getValue(), pet.getBattlerServerPetData());
		}
		if (hasUpgrade) {
			refreshMasterAttr();
			player.onPetPropChange();
			pet.pushInfoUpdate();
		} else {
			pet.pushExpUpdate();
		}

		// 更新任务
		player.taskManager.dealTaskEvent(TaskType.PET_TRAIN, String.valueOf(id), 1);
		return result;
	}

	public Map<String, Object> getBattlerServerPetBase() {
		if (petsPO.fightPetId != 0) {
			PetNew pet = playerPets.get(petsPO.fightPetId);
			return pet.getBattlerServerPetBase();
		} else {
			Map<String, Object> data = new HashMap<>();
			data.put("Model", "");
			data.put("ModelPercent", 0);
			data.put("ModelStar", "");
			data.put("ModelStarPercent", 0);
			data.put("name", "");
			data.put("level", 0);
			data.put("Qcolor", 0);
			data.put("templateId", 0);
			data.put("Icon", "");
			data.put("upGradeLevel", 0);
			return data;
		}
	}

	public Map<String, Object> getBattlerServerPetEffect() {
		Map<String, Object> data = new HashMap<>();
		if (petsPO.fightPetId != 0) {
			PetNew pet = playerPets.get(petsPO.fightPetId);
			// for (PlayerBtlData pbd : pet.attr_all_pet.keySet()) {
			// data.put(pbd.name(), pet.attr_all_pet.get(pbd));
			// data.put(PlayerBtlData.MaxHP.name(), 999999);
			// data.put("HP", 999999);
			// }
			JSONObject json = pet.getBattlerServerPetEffect();
			for (String key : json.keySet()) {
				data.put(key, json.get(key));
			}
		} else {
			data.put("Ac", 0);
			data.put("Crit", 0);
			data.put("Dodge", 0);
			data.put("Hit", 0);
			data.put("HP", 0);
			data.put("HPReborn", 0);
			data.put("IgnoreAc", 0);
			data.put("IgnoreAcPer", 0);
			data.put("IgnoreResist", 0);
			data.put("IgnoreResistPer", 0);
			data.put("IgnorMagDamage", 0);
			data.put("IgnorPhyDamage", 0);
			data.put("MaxHP", 0);
			data.put("MaxMag", 0);
			data.put("MaxMP", 0);
			data.put("MaxPhy", 0);
			data.put("MinMag", 0);
			data.put("MinPhy", 0);
			data.put("MoveSpeed", 0);
			data.put("MP", 0);
			data.put("MPReborn", 0);
			data.put("Rescrit", 0);
			data.put("Resist", 0);
			data.put("HealEffect", 0);
			data.put("HealedEffect", 0);
		}
		return data;

	}

	public List<Map<String, Integer>> getBattlerServerPetSkill() {

		List<Map<String, Integer>> data = new ArrayList<>();
		if (petsPO.fightPetId != 0) {
			PetNew pet = playerPets.get(petsPO.fightPetId);
			return pet.getBattlerServerPetSkill();
		} else
			return data;
	}

	/**
	 * 宠物战斗模式，0:主动,1:被动,2:跟随
	 * 
	 * @return
	 */
	public int getPkDataToBattleJson() {
		MapBase sceneProp = AreaDataConfig.getInstance().get(this.player.getAreaId());
		if (sceneProp != null) {
			if (sceneProp.changePetAI == 0) {
				return sceneProp.petAI;
			}
		}
		return getPkModel();
	}

	/**
	 * 宠物战斗模式，0:主动,1:被动,2:跟随
	 * 
	 * @return
	 */
	public static int getPkModel() {
		return 1;
	}

	public String getFightingPetId() {
		return this.petsPO.fightPetId == 0 ? "" : this.petsPO.fightPetId + "";
	}

	public List<SuperScriptType> getSuperScript() {
		// List<SuperScriptType> list = new ArrayList<>();
		// SuperScriptType.Builder data = SuperScriptType.newBuilder();
		// if
		// (!this.player.functionOpenManager.isOpen(Const.FunctionType.PET.getValue()))
		// {
		// data.setType(Const.SUPERSCRIPT_TYPE.PET.getValue());
		// data.setNumber(0);
		// } else {
		// data.setType(Const.SUPERSCRIPT_TYPE.PET.getValue());
		// data.setNumber(this.petCanGet());
		// }
		// list.add(data.build());
		// return list;
		return null;
	}

	public int petCanGet() {

		return petsPO.pkModel;
	}

	public PetNew getFightingPet() {
		return playerPets.get(petsPO.fightPetId);
	}

	public void addExp(String id, int exp) {
		addExp(StringUtil.isEmpty(id) ? 0 : Integer.parseInt(id), exp);
	}

	public void addExp(int id, int exp) {
		PetNew pet = playerPets.get(id);
		if (pet != null) {
			pet.addExp(exp, true);
		}
	}

	public String changePetPkModel(int reqModel) {
		MapBase sceneProp = AreaDataConfig.getInstance().get(this.player.getAreaId());
		if (sceneProp != null) {
			if (sceneProp.changePKtype == 0) {
				return LangService.getValue("AREA_CANNOT_CHANG_PKMODE");
			}
		}
		if (reqModel == getPkModel()) {
			return LangService.getValue("PARAM_ERROR");
		}
		petsPO.pkModel = reqModel;

		this.player.getXmdsManager().refreshPlayerPetFollowModeChange(this.player.getId(), reqModel);

		this.player.pushDynamicData("petPkModel", petsPO.pkModel);

		return null;
	}

	public UpGradeUpLevelResponse.Builder upgradeUplevel(int id) {
		UpGradeUpLevelResponse.Builder res = UpGradeUpLevelResponse.newBuilder();
		res.setS2CCode(PomeloRequest.FAIL);
		PetNew pet = playerPets.get(id);
		if (pet == null) {
			res.setS2CMsg(LangService.getValue("SOMETHING_ERR"));
			Out.warn("宠物突破失败,因为宠物不存在!,roleId=", player.getId(), ",宠物id=", id);
			return res;
		}
		int result = pet.upgradeUplevel();
		if (result == 0) {
			res.setS2CCode(PomeloRequest.OK);
			res.setPetInfo(pet.toJson4PayLoad());
		} else {
			Out.warn("宠物突破失败!,roleId=", player.getId(), ",宠物id=", id, "result=", result);
			res.setS2CMsg(LangService.getValue("SOMETHING_ERR"));
		}
		player.sevenGoalManager.processGoal(SevenGoalTaskType.PET_UPGRADE_UPLV,getMaxPetUpLv());
		Out.info("宠物突破成功!,roleId=", player.getId(), ",宠物id=", id, "当前阶为:", pet.po.upLevel);
		return res;
	}

	private void onPlayerUpgrade() {
		for (PetNew pet : playerPets.values()) {
			pet.initCurMaxLv();
		}
	}

	public GetPetInfoNewResponse.Builder getPetInfo(int id) {
		GetPetInfoNewResponse.Builder res = GetPetInfoNewResponse.newBuilder();
		PetNew pet = playerPets.get(id);
		if (pet == null) {
			res.setS2CCode(PomeloRequest.FAIL);
			res.setS2CMsg(LangService.getValue("SOMETHING_ERR"));
			return res;
		}
		res.setS2CCode(PomeloRequest.OK);
		res.setS2CPet(pet.toJson4PayLoad());
		return res;
	}
	
	public Map<PlayerBtlData, Integer> calAssociateInfluence()
	{
//		for (PetNewPO petNewPO : petsPO.pets.values()) {
//			
//		}
		Map<PlayerBtlData, Integer> data=new HashMap<>();
		for (PetAssociateExt petAssociateExt : GameData.PetAssociates.values()) {
			boolean actived=true;
			for (Map.Entry<Integer, Integer> entry : petAssociateExt.petIDMap.entrySet()) {
				PetNewPO activedPetNewPO=null;
				for (PetNewPO petNewPO : petsPO.pets.values()) {
					if(petNewPO.id==entry.getKey())
					{
						if(petNewPO.upLevel>=entry.getValue())
						{
							activedPetNewPO=petNewPO;
							break;
						}
					}
				}
				if(activedPetNewPO==null)
				{
					actived=false;
					continue;
				}		
			}
			
			if(actived==false)
			{
				continue;
			}
			
			for (Map.Entry<Integer, Integer> entry2 : petAssociateExt.addProMap.entrySet()) {
				PlayerBtlData key=PlayerBtlData.getE(entry2.getKey());
				int oldValue=0;
				if(data.containsKey(key)) {
					oldValue+=data.get(key);
					
				}
				data.put(key, entry2.getValue()+oldValue);
				
			}
		}
		return data;
	}
	
	public int getMaxPetUpLv() {
		int maxUpLv=0;
		for (PetNew petNew: playerPets.values()) {
			if(petNew.po.upLevel>maxUpLv) {
				maxUpLv = petNew.po.upLevel;
			}
		}
		return maxUpLv;
	}

	@Override
	public void onPlayerEvent(PlayerEventType eventType) {
		switch (eventType) {
		case UPGRADE:
			onPlayerUpgrade();
			break;

		default:
			break;
		}

	}

	@Override
	public ManagerType getManagerType() {
		return ManagerType.PET;
	}
}
