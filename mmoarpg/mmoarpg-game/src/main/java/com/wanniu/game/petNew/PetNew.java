package com.wanniu.game.petNew;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wanniu.core.common.IntIntPair;
import com.wanniu.core.logfs.Out;
import com.wanniu.game.area.AreaDataConfig;
import com.wanniu.game.common.CommonUtil;
import com.wanniu.game.common.Const;
import com.wanniu.game.common.Const.GOODS_CHANGE_TYPE;
import com.wanniu.game.common.Const.PlayerBtlData;
import com.wanniu.game.common.Const.SkillType;
import com.wanniu.game.data.GameData;
import com.wanniu.game.data.PetExpLevelCO;
import com.wanniu.game.data.base.MapBase;
import com.wanniu.game.data.ext.BaseDataExt;
import com.wanniu.game.data.ext.MasterPropExt;
import com.wanniu.game.data.ext.MasterUpgradePropExt;
import com.wanniu.game.data.ext.PassiveSkillExt;
import com.wanniu.game.data.ext.PetSkillExt;
import com.wanniu.game.data.ext.PetUpgradeExt;
import com.wanniu.game.player.AttributeUtil;
import com.wanniu.game.player.BILogService;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.player.bi.LogReportService;
import com.wanniu.game.poes.PetNewPO;

import pomelo.Common.AttributeSimple;
import pomelo.area.PetNewHandler.PetDataInfo;
import pomelo.area.PetNewHandler.PetExpUpdatePush;
import pomelo.area.PetNewHandler.PetInfoUpdatePush;
import pomelo.area.PetNewHandler.SkillDataInfo;

public class PetNew {

	private static final int Tenthousand = 10000;
	public PetNewPO po;
	public BaseDataExt prop;
	private PetExpLevelCO curLevelExp;
	/**
	 * 宠物属性，由等级和阶级计算得出，同步给战斗服的
	 */
	public Map<PlayerBtlData, Integer> attr_all_pet;

	/**
	 * 同步给客户端的最终显示使用
	 */
	public Map<PlayerBtlData, Integer> attr_final_pet;
	/**
	 * 同步给客户端的最终显示使用(下一级)
	 */
	public Map<PlayerBtlData, Integer> attr_final_pet_next;
	/**
	 * 给主人的属性加成
	 */
	public Map<PlayerBtlData, Integer> attr_master;

	private int curMaxLv;
	private WNPlayer master;

	public PetNew(PetNewPO po, WNPlayer master) {
		this.po = po;
		this.master = master;
		prop = GameData.BaseDatas.get(po.id);
		initCurMaxLv();
		attr_all_pet = new HashMap<>();
		attr_final_pet = new HashMap<>();
		attr_final_pet_next = new HashMap<>();
		calAttr();
		calMasterAttr();
	}

	public int getCurMaxLv() {
		return curMaxLv;
	}

	public void initCurMaxLv() {
		curLevelExp = GameData.PetExpLevels.get(po.level);
		curMaxLv = GameData.PetConfigs.get("LevelLimit").intValue;
	}

	private void calAttr() {
		attr_all_pet.clear();
		attr_final_pet.clear();
		attr_all_pet.put(PlayerBtlData.Phy, CommonUtil.getGrowUpValue(prop.basePhyDamage, prop.phyGrowUp, po.level));
		attr_all_pet.put(PlayerBtlData.Mag, CommonUtil.getGrowUpValue(prop.baseMagDamage, prop.magGrowUp, po.level));
		attr_all_pet.put(PlayerBtlData.Hit, CommonUtil.getGrowUpValue(prop.initHit, prop.hitGrowUP, po.level));
		attr_all_pet.put(PlayerBtlData.Crit, CommonUtil.getGrowUpValue(prop.initCrit, prop.critGrowUP, po.level));
		attr_all_pet.put(PlayerBtlData.CritDamage, CommonUtil.getGrowUpValue(prop.initCritDamage, prop.critDamageGrowUp, po.level));
		// attr_all_pet.put(PlayerBtlData.RunSpeed, prop.moveSpeed);

		Map<PlayerBtlData, Integer> upLevelAttrs = null;
		List<PetUpgradeExt> list = GameData.findPetUpgrades((t) -> t.petID == po.id && t.targetUpLevel == po.upLevel);
		if (list.size() > 0) {
			upLevelAttrs = list.get(0).upLevelAttrs;
			AttributeUtil.addData2AllData(upLevelAttrs, attr_all_pet);
		}
		AttributeUtil.addData2AllData(calPassiveSkill(), attr_all_pet);

		AttributeUtil.addData2AllData(getSkillAttr(1), attr_all_pet);
		AttributeUtil.addData2AllData(attr_all_pet, attr_final_pet);

		this.calFinalData(attr_final_pet);// 属性转化

		po.fightPower = CommonUtil.calPetFightPower(attr_final_pet, po.id);
		po.fightPower += getSkillsPower();

		this.calNextAttr();
	}

	private void calNextAttr() {
		attr_final_pet_next.clear();

		Map<PlayerBtlData, Integer> attr_temp = new HashMap<>();
		attr_temp.put(PlayerBtlData.Phy, CommonUtil.getGrowUpValue(prop.basePhyDamage, prop.phyGrowUp, po.level));
		attr_temp.put(PlayerBtlData.Mag, CommonUtil.getGrowUpValue(prop.baseMagDamage, prop.magGrowUp, po.level));
		attr_temp.put(PlayerBtlData.Hit, CommonUtil.getGrowUpValue(prop.initHit, prop.hitGrowUP, po.level));
		attr_temp.put(PlayerBtlData.Crit, CommonUtil.getGrowUpValue(prop.initCrit, prop.critGrowUP, po.level));
		attr_temp.put(PlayerBtlData.CritDamage, CommonUtil.getGrowUpValue(prop.initCritDamage, prop.critDamageGrowUp, po.level));

		List<PetUpgradeExt> list = GameData.findPetUpgrades((t) -> t.petID == po.id && t.targetUpLevel == (po.upLevel + 1));
		if (list.size() > 0) {
			Map<PlayerBtlData, Integer> upLevelAttrs = list.get(0).upLevelAttrs;
			AttributeUtil.addData2AllData(upLevelAttrs, attr_temp);
		}
		AttributeUtil.addData2AllData(calPassiveSkill(), attr_temp);

		AttributeUtil.addData2AllData(getSkillAttr(1), attr_temp);

		AttributeUtil.addData2AllData(attr_temp, attr_final_pet_next);
		this.calFinalData(attr_final_pet_next);// 属性转化
	}

	private void calFinalData(Map<PlayerBtlData, Integer> attr) {
		/**
		 * 攻击、攻击百分比都要换算成物攻和魔攻
		 */
		// 攻击换算成物攻
		int baseValue = 0;
		if (attr.containsKey(PlayerBtlData.Phy)) {
			baseValue += attr.get(PlayerBtlData.Phy);
		}
		if (attr.containsKey(PlayerBtlData.Attack)) {
			baseValue += attr.get(PlayerBtlData.Attack);
		}
		attr.put(PlayerBtlData.Phy, baseValue);
		// 攻击百分比换算成物攻百分比
		int basePer = 0;
		if (attr.containsKey(PlayerBtlData.PhyPer)) {
			basePer += attr.get(PlayerBtlData.PhyPer);
		}
		if (attr.containsKey(PlayerBtlData.AttackPer)) {
			basePer += attr.get(PlayerBtlData.AttackPer);
		}
		attr.put(PlayerBtlData.PhyPer, basePer);
		// 攻击换算成魔攻
		baseValue = 0;
		if (attr.containsKey(PlayerBtlData.Mag)) {
			baseValue += attr.get(PlayerBtlData.Mag);
		}
		if (attr.containsKey(PlayerBtlData.Attack)) {
			baseValue += attr.get(PlayerBtlData.Attack);
		}
		attr.put(PlayerBtlData.Mag, baseValue);
		// 攻击百分比换算成魔攻百分比
		basePer = 0;
		if (attr.containsKey(PlayerBtlData.MagPer)) {
			basePer += attr.get(PlayerBtlData.MagPer);
		}
		if (attr.containsKey(PlayerBtlData.AttackPer)) {
			basePer += attr.get(PlayerBtlData.AttackPer);
		}
		attr.put(PlayerBtlData.MagPer, basePer);

		// 把物攻百分比计算成最后的物攻 (物攻+攻击)×(物攻百分比+攻击百分比)
		if (attr.containsKey(PlayerBtlData.Phy) && attr.containsKey(PlayerBtlData.PhyPer)) {
			int value = attr.get(PlayerBtlData.Phy);
			attr.put(PlayerBtlData.Phy, value + value * attr.get(PlayerBtlData.PhyPer) / Tenthousand);
		}
		// 把魔攻百分比计算成最后的魔攻
		if (attr.containsKey(PlayerBtlData.Mag) && attr.containsKey(PlayerBtlData.MagPer)) {
			int value = attr.get(PlayerBtlData.Mag);
			attr.put(PlayerBtlData.Mag, value + value * attr.get(PlayerBtlData.MagPer) / Tenthousand);
		}
		// 把命中百分比计算成最后的命中
		if (attr.containsKey(PlayerBtlData.Hit) && attr.containsKey(PlayerBtlData.HitPer)) {
			int value = attr.get(PlayerBtlData.Hit);
			attr.put(PlayerBtlData.Hit, value + value * attr.get(PlayerBtlData.HitPer) / Tenthousand);
		}
		// 把暴击百分比计算成最后的暴击
		if (attr.containsKey(PlayerBtlData.Crit) && attr.containsKey(PlayerBtlData.CritPer)) {
			int value = attr.get(PlayerBtlData.Crit);
			attr.put(PlayerBtlData.Crit, value + value * attr.get(PlayerBtlData.CritPer) / Tenthousand);
		}
	}

	/**
	 * 计算主人属性加成
	 */
	private void calMasterAttr() {
		attr_master = new HashMap<>();
		MasterPropExt prop = GameData.MasterProps.get(po.id);
		for (PlayerBtlData pbd : prop.attr_master.keySet()) {
			attr_master.put(pbd, CommonUtil.getGrowUpValue(prop.attr_master.get(pbd), prop.attr_grow.get(pbd), po.level));
		}

		// 再加一个阶数给人的加成值...
		List<MasterUpgradePropExt> props = GameData.findMasterUpgradeProps(v -> v.petID == po.id && v.upLevel == po.upLevel);
		if (props.size() == 1) {
			MasterUpgradePropExt template = props.get(0);
			AttributeUtil.addData2AllData(template.attr_master, attr_master);
		} else {
			Out.warn("宠物升阶对人物加成配置异常. petId=", po.id, ",upLevel=", po.upLevel);
		}

		AttributeUtil.addData2AllData(calMasterPassiveSkill(), attr_master);

		AttributeUtil.addData2AllData(getSkillAttr(2), attr_all_pet);
	}

	/**
	 * 计算被动技能对宠物的属性加成
	 * 
	 * @return
	 */
	private Map<PlayerBtlData, Integer> calPassiveSkill() {
		Map<PlayerBtlData, Integer> map = new HashMap<>();
		for (PetSkill skill : po.skills.values()) {
			if (skill.level > 0) {
				PetSkillExt skillProp = GameData.PetSkills.get(skill.id);
				if (skillProp == null) {
					Out.error("can't find prop by skillId:", skill.id);
					continue;
				}
				if (skillProp.skillType == SkillType.PASSIVE.getValue()) {
					for (PlayerBtlData pbd : skillProp.attributeValues.keySet()) {
						Map<Integer, Integer> map_attr_level = skillProp.attributeValues.get(pbd);
						Integer value = map_attr_level.get(skill.level);
						if (value != null)
							map.put(pbd, value);
					}
				}
			}
		}
		return map;
	}

	/**
	 * 计算被动技能对宠物的属性加成
	 * 
	 * @return
	 */
	private Map<PlayerBtlData, Integer> calMasterPassiveSkill() {
		Map<PlayerBtlData, Integer> map = new HashMap<>();
		for (PetSkill skill : po.skills.values()) {
			if (skill.level > 0) {
				PetSkillExt skillProp = GameData.PetSkills.get(skill.id);
				if (skillProp.skillType == SkillType.PET_PASSIVE.getValue()) {
					for (PlayerBtlData pbd : skillProp.attributeValues.keySet()) {
						Map<Integer, Integer> map_attr_level = skillProp.attributeValues.get(pbd);
						Integer value = map_attr_level.get(skill.level);
						if (value != null)
							map.put(pbd, value);
					}
				}
			}
		}
		return map;
	}

	/**
	 * 同步给客户端的数据
	 * 
	 * @return
	 */
	public PetDataInfo.Builder toJson4PayLoad() {
		PetDataInfo.Builder data = PetDataInfo.newBuilder();
		data.setId(po.id);
		data.setName(po.name);
		data.setExp(po.exp);
		data.setLevel(po.level);
		data.setUpLevel(po.upLevel);
		data.setFightPower(po.fightPower);
		ArrayList<AttributeSimple> list_attrs_pet = new ArrayList<>();
		for (PlayerBtlData pbd : attr_final_pet.keySet()) {
			AttributeSimple.Builder asb = AttributeSimple.newBuilder();
			asb.setId(pbd.id);
			asb.setValue(attr_final_pet.get(pbd));
			list_attrs_pet.add(asb.build());
		}
		data.addAllAttrsFinal(list_attrs_pet);

		ArrayList<AttributeSimple> list_attrs_pet_next = new ArrayList<>();
		for (PlayerBtlData pbd : attr_final_pet_next.keySet()) {
			AttributeSimple.Builder asb = AttributeSimple.newBuilder();
			asb.setId(pbd.id);
			asb.setValue(attr_final_pet_next.get(pbd));
			list_attrs_pet_next.add(asb.build());
		}
		data.addAllNextAttrsFinal(list_attrs_pet_next);

		ArrayList<SkillDataInfo> list_skill = new ArrayList<>();
		for (IntIntPair iip : prop.getInitSkills()) {
			PetSkill skill = po.skills.get(iip.first);
			if (skill != null) {
				SkillDataInfo.Builder sb = SkillDataInfo.newBuilder();
				sb.setId(skill.id);
				sb.setLevel(skill.level);
				sb.setPos(skill.pos);
				sb.setInborn(skill.inborn);
				list_skill.add(sb.build());
			}
		}
		data.addAllSkills(list_skill);
		return data;
	}

	public int addExp(int exp, boolean synchBattleServer) {
		if (po.level >= getCurMaxLv()) {
			return -1;
		}
		po.exp += exp;
		boolean upgrade = false;
		if (po.exp >= curLevelExp.experience) {
			upgrade = upgrade(synchBattleServer);
			if (synchBattleServer && master != null) {
				this.pushInfoUpdate();
			}
		} else {
			if (synchBattleServer && master != null) {
				this.pushExpUpdate();
			}
		}

		if (upgrade) {
			this.master.achievementManager.onGetPetLevel(po.id, po.level);
		}
		return upgrade ? 1 : 0;
	}

	public void pushInfoUpdate() {
		PetInfoUpdatePush.Builder data = PetInfoUpdatePush.newBuilder();
		data.setS2CPet(this.toJson4PayLoad());
		master.receive("area.petNewPush.petInfoUpdatePush", data.build());
	}

	public void pushExpUpdate() {
		PetExpUpdatePush.Builder data = PetExpUpdatePush.newBuilder();
		data.setS2CPetId(po.id);
		data.setS2CCurExp(po.exp);
		master.receive("area.petNewPush.petExpUpdatePush", data.build());
	}

	/**
	 * 到下一级所需要的经验
	 * 
	 * @param pet
	 * @return
	 */
	public long getNextLevelneedExp() {
		if (!this.canAddExp()) {
			return 0l;
		}
		// int maxLv = curMaxLv;
		PetExpLevelCO prop = GameData.PetExpLevels.get(po.level);
		return prop.experience - po.exp;

	}

	/**
	 * 根据经验获取相应等级
	 * 
	 * @return {exp, level}
	 */
	public static long[] getLevelByExp(long exp, int nowLevel, int curMaxLv) {
		int level = nowLevel;
		long curExp = exp;
		for (int i = nowLevel; i < curMaxLv; i++) {
			PetExpLevelCO prop = GameData.PetExpLevels.get(i);
			int needExp = prop.experience;
			if (curExp < needExp) {
				break;
			}
			curExp = curExp - needExp;
			level++;
			if (level == GameData.PetConfigs.get("LevelLimit").intValue) { // 如果到最高等级了就不加经验
				curExp = 0;
				break;
			}
			if (level == curMaxLv) { // 如果是到了当前阶级等级上限
				PetExpLevelCO _prop = GameData.PetExpLevels.get(level);
				if (curExp > _prop.experience) {
					curExp = _prop.experience;
				}
			}
		}

		return new long[] { curExp, level };
	}

	public String getBattlerServerPetData() {
		JSONObject json = new JSONObject();
		json.put("petBase", this.getBattlerServerPetBase());
		json.put("petEffect", this.getBattlerServerPetEffect());
		json.put("petSkill", this.getBattlerServerPetSkill());
		json.put("petMode", this.getPkDataToBattleJson());
		String str = json.toString();
		return str;
	}

	public int getPkDataToBattleJson() {
		MapBase sceneProp = AreaDataConfig.getInstance().get(this.master.getAreaId());
		if (sceneProp != null) {
			if (sceneProp.changePetAI == 0) {
				return sceneProp.petAI;
			}
		}
		return PetManager.getPkModel();
	}

	public boolean upgrade(boolean synchBattleServer) {
		int oldLevel = po.level;
		long oldExp = po.exp;
		boolean flag_upgrade = false;
		long[] par = getLevelByExp(po.exp, po.level, getCurMaxLv());
		int curLevel = (int) par[1];
		if (po.level != curLevel)
			flag_upgrade = true;
		po.exp = par[0];
		po.level = curLevel;

		// 上报
		LogReportService.getInstance().ansycReportPetUpgrade(master, po.id, po.name, po.upLevel, po.level, po.exp);
		BILogService.getInstance().ansycReportPetCultivate(master.getPlayer(), oldLevel, po.level, oldExp, po.exp, po.id);

		initCurMaxLv();
		calAttr();
		calMasterAttr();
		if (synchBattleServer) {
			master.petNewManager.refreshMasterAttr();
			master.onPetPropChange();
		}
		if (master.petNewManager.petsPO.fightPetId == po.id && synchBattleServer) {
			master.getXmdsManager().refreshPlayerPetDataChange(this.master.getId(), PetOperatorType.Reset.getValue(), this.getBattlerServerPetData());
		}
		return flag_upgrade;
	}

	/**
	 * 升阶，突破
	 * 
	 * @return 0成功，-1已经最大阶级了,-2等级不够,-3材料不够
	 */
	public int upgradeUplevel() {
		int nextUplvl = po.upLevel + 1;
		if (nextUplvl > GameData.PetConfigs.get("Upgrade.LevelLimit").intValue) {
			return -1;
		}
		PetUpgradeExt prop_next = GameData.findPetUpgrades((t) -> t.petID == po.id && t.targetUpLevel == nextUplvl).get(0);
		if (po.level < prop_next.reqLevel) {
			return -2;
		}
		String mateCode = prop_next.mateCode;
		int mateCount = prop_next.mateCount;
		if (!master.bag.discardItem(mateCode, mateCount, GOODS_CHANGE_TYPE.pet)) {
			return -3;
		}

		po.upLevel++;
		// 上报
		LogReportService.getInstance().ansycReportPetUpgrade(master, po.id, po.name, po.upLevel, po.level, po.exp);
		BILogService.getInstance().ansycReportPetCultivate(master.getPlayer(), po.upLevel, mateCode, mateCount, po.id);

		// 判断是否有开启的技能
		boolean newSkill = false;
		int openSkillID = prop_next.openSkillID;
		if (openSkillID != 0) {
			PetSkill skill = po.skills.get(openSkillID);
			if (skill != null) {
				skill.level = 1;
				newSkill = true;
			}
		}
		initCurMaxLv();
		calAttr();
		calMasterAttr();
		master.petNewManager.refreshMasterAttr();
		master.onPetPropChange();
		if (master.petNewManager.petsPO.fightPetId == po.id) {
			master.getXmdsManager().refreshPlayerPetDataChange(this.master.getId(), PetOperatorType.Reset.getValue(), this.getBattlerServerPetData());
		}
		JSONArray arr = new JSONArray();
		List<Map<String, Integer>> list = getBattlerServerPetSkill();
		for (Map<String, Integer> map : list) {
			arr.add(map);
		}
		if (newSkill && master.petNewManager.petsPO.fightPetId == po.id) {
			master.getXmdsManager().refreshPlayerPetSkillChange(this.master.getId(), 0, arr.toJSONString());
		}

		// 成就
		this.master.achievementManager.onPetUpGrade(0, po.upLevel);
		return 0;
	}

	public boolean canAddExp() {
		return po.level < getCurMaxLv();
	}

	public JSONObject getBattlerServerPetBase() {
		JSONObject data = new JSONObject();
		data.put("Model", this.prop.model);
		data.put("ModelPercent", this.prop.modelPercent);
		// if (this.getTransformLevel() > 0) {
		// data.put("ModelStar",
		// this.prop.getModelStar(this.getTransformLevel()));
		// data.put("ModelStarPercent",
		// this.prop.getModelStarPercent(this.getTransformLevel()));
		// data.put("ModelStarScenePercent",
		// this.prop.getModelStarScenePercent(this.getTransformLevel()));
		// } else {
		data.put("ModelStar", "");
		data.put("ModelStarPercent", 0);
		data.put("ModelStarScenePercent", 0);
		// }
		data.put("name", po.name);
		data.put("level", po.level);
		data.put("Qcolor", this.prop.qcolor);
		data.put("templateId", this.po.id);
		data.put("Icon", this.prop.icon);
		data.put("upGradeLevel", this.po.upLevel);
		return data;
	}

	public JSONObject getBattlerServerPetEffect() {
		JSONObject data = new JSONObject();
		for (PlayerBtlData pbd : attr_all_pet.keySet()) {
			data.put(pbd.name(), attr_all_pet.get(pbd));
		}
		data.put("MoveSpeed", prop.moveSpeed);

		data.put(PlayerBtlData.MaxHP.name(), 999999999);
		data.put("HP", 999999999);

		return data;
	}

	public List<Map<String, Integer>> getBattlerServerPetSkill() {
		List<Map<String, Integer>> data = new ArrayList<>();
		for (PetSkill skill : po.skills.values()) {
			int type = getPetSkillBySkillId(skill.id).skillType;
			if (type == Const.SkillType.BATTLE_PASSIVE.getValue() || type == Const.SkillType.ACTIVE.getValue() || type == Const.SkillType.NORMAL.getValue()) {
				if (skill.level > 0) {
					Map<String, Integer> e = new HashMap<>();
					e.put("id", skill.id);
					e.put("level", skill.level);
					e.put("talentLevel", skill.level);
					e.put("type", type);
					e.put("skillTime", 0);
					data.add(e);
				}

			}
		}
		return data;
	}

	public static PetSkillExt getPetSkillBySkillId(int skillId) {
		return GameData.PetSkills.get(skillId);
	}

	private int getSkillsPower() {
		int power = 0;
		for (PetSkill skill : po.skills.values()) {
			PetSkillExt skillProp = GameData.PetSkills.get(skill.id);
			power += skillProp.getSkillPower(skill.level);
		}
		return power;
	}

	/**
	 * 计算被动技能给目标增加的属性
	 * 
	 * @param targetType 1.宠物自己 2.宠物主人
	 * @return
	 */
	private Map<PlayerBtlData, Integer> getSkillAttr(int targetType) {
		Map<PlayerBtlData, Integer> map = new HashMap<>();
		for (PetSkill ps : po.passiveSkills.values()) {
			PassiveSkillExt prop = GameData.PassiveSkills.get(ps.id);
			if (prop.skillType == 0 && prop.target == targetType) {
				// 0：被动技能，服务器计算，被动生效，掌握后即给人物增加属性（也可以是宠物被动技能，对宠物自身加成）
				if (prop.ValueAttribute1 != null) {
					int value = 0;
					if (prop.ValueSetMap1 != null && prop.ValueSetMap1.containsKey(ps.level)) {
						value = prop.ValueSetMap1.get(ps.level);
					} else {
						Out.error("麻痹啊，天赋技能", ps.id, "对应等级无数据");
					}
					map.put(prop.ValueAttribute1, value);
				}
				if (prop.ValueAttribute2 != null) {
					int value = 0;
					if (prop.ValueSetMap2 != null && prop.ValueSetMap2.containsKey(ps.level)) {
						value = prop.ValueSetMap2.get(ps.level);
					} else {
						Out.error("麻痹啊，天赋技能", ps.id, "对应等级无数据");
					}
					map.put(prop.ValueAttribute2, value);
				}
				if (prop.ValueAttribute3 != null) {
					int value = 0;
					if (prop.ValueSetMap3 != null && prop.ValueSetMap3.containsKey(ps.level)) {
						value = prop.ValueSetMap3.get(ps.level);
					} else {
						Out.error("麻痹啊，天赋技能", ps.id, "对应等级无数据");
					}
					map.put(prop.ValueAttribute3, value);
				}
			}
		}
		return map;
	}
}
