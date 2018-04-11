package com.wanniu.game.playerSkill;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.wanniu.core.common.StringInt;
import com.wanniu.core.logfs.Out;
import com.wanniu.game.common.Const;
import com.wanniu.game.common.Const.GOODS_CHANGE_TYPE;
import com.wanniu.game.common.Const.ManagerType;
import com.wanniu.game.common.Const.PlayerBtlData;
import com.wanniu.game.common.Const.PlayerEventType;
import com.wanniu.game.common.ModuleManager;
import com.wanniu.game.common.msg.WNNotifyManager;
import com.wanniu.game.data.GameData;
import com.wanniu.game.data.base.DItemEquipBase;
import com.wanniu.game.data.ext.CharacterExt;
import com.wanniu.game.data.ext.CharacterExt.InitSkill;
import com.wanniu.game.data.ext.PassiveSkillExt;
import com.wanniu.game.data.ext.SkillDataExt;
import com.wanniu.game.data.ext.SkillValueExt;
import com.wanniu.game.data.ext.TalentEffectExt;
import com.wanniu.game.item.ItemUtil;
import com.wanniu.game.player.BILogService;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.playerSkill.po.SkillDB;
import com.wanniu.game.poes.SkillsPO;

import Xmds.SkillDataICE;
import pomelo.area.PlayerHandler.SuperScriptType;
import pomelo.area.SkillHandler.GetAllSkillResponse;
import pomelo.area.SkillHandler.ReqItem;
import pomelo.area.SkillHandler.SkillBasic;
import pomelo.area.SkillHandler.SkillDetail;
import pomelo.area.SkillHandler.TalentDetail;
import pomelo.area.SkillKeysHandler.SaveSkillKeysRequest;
import pomelo.area.SkillKeysHandler.SkillKey;

public class SkillManager extends ModuleManager {

	public WNPlayer player;
	public SkillsPO player_skills;

	private static SkillComparator comparator = new SkillComparator();
	private static SkillLevelComparator comparator_lv = new SkillLevelComparator();
	public Map<PlayerBtlData, Integer> data_skill_attr;

	public SkillManager(WNPlayer player, SkillsPO skills) {
		this.player = player;
		this.player_skills = skills;
		refreshPassiveSkillData();

	}

	public static SkillsPO initNewPlayerSkills(int pro, int initLevel) {
		CharacterExt prop = GameData.findCharacters((t) -> t.pro == pro).get(0);
		List<InitSkill> initSkills = prop.initSkills;
		Map<Integer, SkillDB> skills_list = new HashMap<>();
		int openCount = 0;
		for (int i = 0; i < initSkills.size(); i++) {
			InitSkill cfg = initSkills.get(i);
			SkillDB skill = new SkillDB(cfg.level, cfg.id, 0, 0, 0, i);
			SkillDataExt scf = GameData.SkillDatas.get(cfg.id);
			int needLvl = scf.lvReqData.get(0);
			if (initLevel >= needLvl) {
				skill.flag = 1;
				openCount++;
			}
			skills_list.put(cfg.id, skill);
			// System.out.println("skillId= "+cfg.id+" flag = "+skill.flag);
		}
		SkillsPO player_skills = new SkillsPO();
		player_skills.skills = skills_list;
		player_skills.openCount = openCount;
		player_skills.talentSkills = new HashMap<>();
		// 以下为测试代码
		// {
		// player_skills.talentSkills = new HashMap<>();
		// SkillDB s = new SkillDB();
		// s.id = 802020;
		// s.lv = 1;
		// player_skills.talentSkills.put(s.id, s);
		// }
		return player_skills;
	}

	/**
	 * 人物升级，判断是否有技能解锁
	 */
	public void onPlayerUpgrade() {
		List<SkillDataExt> props = GameData.findSkillDatas((t) -> t.pro_ == player.player.pro && t.learnSkill == 1 && t.lvReqData.size() > 0 && t.lvReqData.get(0) <= player.baseDataManager.baseData.level);

		List<Integer> list = new ArrayList<>();
		for (SkillDataExt prop : props) {
			SkillDB skill = getSkill(prop.skillID);
			if (skill != null) {
				if (skill.flag != 1) {
					skill.flag = 1;
					skill.lv = 1;
					list.add(prop.skillID);
				}
			}
		}
		if (list.size() > 0) {
			this.player.refreshBattlerServerSkill(Const.SkillHandleType.ADD.getValue(), list);
			WNNotifyManager.getInstance().pushSkillUpdate(player, list);
			WNNotifyManager.getInstance().pushSkillKeysUpdate(player, player.skillKeyManager.toJson4Payload());
		}

	}

	public SkillDB getSkill(int skillId) {
		return player_skills.skills.get(skillId);
	}

	public void upgradeSkill(SkillDB skill) {
		int skill_next_lv = skill.lv + 1;
		SkillDataExt prop = GameData.SkillDatas.get(skill.id);
		int cost = 0;
		if (prop.costReqData.containsKey(skill_next_lv))
			cost = prop.costReqData.get(skill_next_lv);
		player.moneyManager.costGold(cost, GOODS_CHANGE_TYPE.skill);
		skill.lv = skill_next_lv;
		Out.info("技能升级 playerId=", player.getId(), ",skillId=", skill.id, ",level=", skill_next_lv);
		// 成就
		this.player.achievementManager.onSkillLevelChange(skill.lv);

		BILogService.getInstance().ansycReportSkillUpgrade(player.getPlayer(), skill.id, prop.skillName, skill.lv);
	}

	public boolean upgradeOneSkill(int skillId) {
		SkillDB skill = getSkill(skillId);
		CheckSkillData result = this.checkSkillReq(skill);
		if (result.result == 0) {
			upgradeSkill(skill);
		}

		List<Integer> list = new ArrayList<>();
		list.add(skillId);
		WNNotifyManager.getInstance().pushSkillUpdate(player, list);
		this.player.refreshBattlerServerSkill(Const.SkillHandleType.CHANGE.getValue(), list);
		player.onPlayerSkillUpgrade();
		return true;
	}

	/**
	 * 递归升级 一键升级按钮的逻辑： 先把能升级的技能找出来，按当前等级从低到高排序 取等级最低的技能，升一级，然后重新排序
	 * 循环以上两步，直到可升级技能列表为空
	 * 
	 * @param map
	 */
	private void sortAndUpgrade(Map<Integer, Object> map, int deep) {
		if (--deep < 0) {
			Out.error("sortAndUpgrade 死锁", map);
			return;
		}

		List<SkillDB> list = new ArrayList<>();
		// 找出可以升级的技能
		for (SkillDB skill : player_skills.skills.values()) {
			if (checkSkillReq(skill).result == 0)
				list.add(skill);
		}
		if (list.size() < 1)
			return;
		list.sort(comparator_lv);
		SkillDB skill = list.get(0);
		upgradeSkill(skill);
		map.put(skill.id, null);
		sortAndUpgrade(map, deep);
	}

	public SkillDB getSkillByPos(int pos) {
		for (SkillDB skill : player_skills.skills.values()) {
			if (skill.pos == pos)
				return skill;
		}
		return null;
	}

	/**
	 * 技能按键位置调整
	 * 
	 * @param skillId
	 * @param pos
	 * @return
	 */
	public int changeSkillsPos(SaveSkillKeysRequest req) {
		List<SkillKey> list = req.getS2CSkillKeysList();
		// if (list.size() > player_skills.openCount) {
		// return -5;
		// }
		// 我这里的pos是从0开始的，不知道和客户端是否一样，所以这里以后还要再校验一遍
		for (SkillKey sk : list) {
			int skillId = sk.getSkillId();
			int pos = sk.getKeyPos();
			if (skillId == 0) { // 卸技能
				SkillDB skill = getSkillByPos(pos);
				if (skill != null)
					if (skill.pos == 0)
						return -3; // 普攻不能卸
			} else {
				SkillDB skill = getSkill(skillId);
				if (skill == null)
					return -4;
				// 0号位置的技能不允许换技能
				if (sk.getKeyPos() > 5 || sk.getKeyPos() == 0)
					return -3;

				// 检查有没有重复的pos和skillId
				int flag_pos = 0, flag_skillId = 0;
				for (SkillKey _sk : list) {
					if (_sk.getKeyPos() == sk.getKeyPos())
						flag_pos++;
					if (_sk.getSkillId() == skillId)
						flag_skillId++;
				}
				if (flag_pos > 1 || flag_skillId > 1) {
					return -1;
				}
			}

		}

		for (SkillKey sk : list) {
			int skillId = sk.getSkillId();
			int pos = sk.getKeyPos();
			if (skillId == 0) { // 卸技能
				SkillDB skill = getSkillByPos(pos);
				if (skill != null)
					skill.pos = 0;
			} else {
				SkillDB skill = getSkill(sk.getSkillId());
				skill.pos = sk.getKeyPos();
			}

		}
		return 0;
	}

	public final List<SkillInfo> toJson4BattleServer() {
		List<SkillInfo> skills = new ArrayList<>();

		for (SkillDB skill : player_skills.skills.values()) {
			if (skill.flag == 1) {
				SkillInfo data = new SkillInfo();
				SkillDataExt prop = getProp(skill.id);
				data.type = prop.skillType;
				data.id = skill.id;
				data.level = skill.lv;
				// data.flag = skill.flag;
				data.talentLevel = 1;
				data.skillTime = skill.skillTime;
				data.cdTime = 0;
				skills.add(data);
			}
		}
		return skills;
	}

	public static SkillDataExt getProp(int skillId) {
		return GameData.SkillDatas.get(skillId);
	}

	public final void syncBattleSkillTime(List<SkillDataICE> skillTimeArray) {
		for (SkillDataICE skillTimeData : skillTimeArray) {
			SkillDB skill = getSkill(skillTimeData.skillId);
			if (skill != null) {
				skill.skillTime = skillTimeData.skillTime;
			} else {
				Out.debug("syncBattleSkillTime error-- id is: ", skillTimeData.skillId);
			}
		}

	}

	/**
	 * 玩家等级改变，可触发技能升级条件
	 */
	public final void onLevelChange() {
		this.updateSuperScript();
	}

	public void updateSuperScript() {
		// TODO Auto-generated method stub

	}

	public final GetAllSkillResponse.Builder toJson4Payload() {
		GetAllSkillResponse.Builder result = GetAllSkillResponse.newBuilder();
		ArrayList<SkillBasic> data = new ArrayList<>();
		result.setHubLock(false);
		for (SkillDB skill : player_skills.skills.values()) {
			data.add(toJSON4BasicPayload(skill, false));
		}
		data.sort(comparator);
		result.addAllSkillList(data);
		return result;
	}

	public final ArrayList<SkillBasic> getSkillsBasicList() {
		ArrayList<SkillBasic> data = new ArrayList<>();
		for (SkillDB skill : player_skills.skills.values()) {
			data.add(toJSON4BasicPayload(skill, false));
		}
		data.sort(comparator);
		return data;
	}

	public final SkillBasic getSkillBasicUpdate4PayLoad(int skillId) {
		SkillDB skill = this.getSkill(skillId);
		SkillBasic basicJson = null;
		basicJson = toJSON4BasicPayload(skill, true);
		return basicJson;
	}

	public final SkillBasic toJSON4BasicPayload(SkillDB skill, boolean isRefresh) {
		SkillBasic.Builder data = SkillBasic.newBuilder();
		SkillDataExt prop = SkillUtil.getProp(skill.id);
		data.setPos(prop.skillIndex);
		data.setLevel(skill.lv);
		data.setExtlv(skill.extLv);
		data.setFlag(skill.flag);
		data.setSkillId(skill.id);
		data.setName(prop.skillName);
		data.setPic(prop.skillIcon);
		if (prop.skillType == Const.SkillType.EFFECT_PASSIVE.getValue()) {
			data.setType(Const.SkillType.BATTLE_PASSIVE.getValue());
		} else {
			data.setType(prop.skillType);
		}
		data.setMaxLevel(prop.maxLevel);
		int nextLevel = 0;
		if (skill.lv < prop.lvReqData.size()) {
			nextLevel = prop.lvReqData.get(skill.lv);
		}
		data.setUpgradeNeedLevel(nextLevel);
		data.setDetailNeedRefresh((!isRefresh) ? false : true);

		ArrayList<Integer> superScripts = new ArrayList<>();
		int isCanUp = 0;
		int isEnSkill1CanUp = 0;
		int isEnSkill2CanUp = 0;
		//
		CheckSkillData result = this.checkSkillReq(skill);
		isCanUp = result.result == 0 ? 1 : 0;
		superScripts.add(isCanUp);
		superScripts.add(isEnSkill1CanUp);
		superScripts.add(isEnSkill2CanUp);

		int nextReqCost = 0;
		if (prop.costReqData.containsKey(skill.lv + 1))
			nextReqCost = prop.costReqData.get(skill.lv + 1);
		data.setCost(nextReqCost);
		// SkillData talent1 = player.playerSkillManager.getSkill(this.enSkillID_1);
		// if(talent1!=null){
		// isEnSkill1CanUp = talent1.checkAndSetUpgradeState();
		// }
		// SkillData talent2 = player.playerSkillManager.getSkill(this.enSkillID_2);
		// if(talent2!= null){
		// isEnSkill2CanUp = talent2.checkAndSetUpgradeState();
		// }
		// superScripts.add(isCanUp);
		// superScripts.add(isEnSkill1CanUp);
		// superScripts.add(isEnSkill2CanUp);
		data.addAllCanUpgrade(superScripts);
		return data.build();
	}

	public final SkillDetail getSkillDetail4PayLoad(int skillId) {
		SkillDB skill = getSkill(skillId);
		if (skill == null)
			return null;
		SkillDataExt prop = SkillUtil.getProp(skillId);
		SkillDetail.Builder data = SkillDetail.newBuilder();
		SkillValueExt exProp = GameData.SkillValues.get(skillId);
		data.setColddown(exProp.cDTime);
		// int manaCostPre = (int)Math.floor(player.allInflus.get("MaxMP") *
		// exProp.costManaPer /10000);
		// int curManaCost = exProp.costManaSetData.get(getUpCorrectLevel(skill));
		// if(curManaCost > 0){
		// data.setCurManaCost(curManaCost + manaCostPre);
		// }else{
		// data.setCurManaCost(manaCostPre);
		// }
		data.setCurManaCost(0);
		// 开始组织描述需要用的数据
		// 当前等级的
		ArrayList<String> curDesData = SkillUtil.getDesData(skillId, getUpCorrectLevel(skill) + skill.extLv);
		// StringBuffer sb = new StringBuffer();
		// for(String s:curDesData){
		// sb.append(s).append(",");
		// }
		// Out.red(sb.toString());
		data.addAllCurDesData(curDesData);
		int nextReqCost = 0;
		if (prop.costReqData.containsKey(skill.lv + 1))
			nextReqCost = prop.costReqData.get(skill.lv + 1);
		ArrayList<ReqItem> items = new ArrayList<>();
		// 组织金币
		String iCode = "gold";
		DItemEquipBase itemData = ItemUtil.getPropByCode(iCode);
		int curItemNum = player.getPlayer().gold;
		int iNum = nextReqCost;

		if (itemData != null) {
			ReqItem.Builder item = ReqItem.newBuilder();
			item.setItemCode(iCode);
			item.setItemQua(itemData.qcolor);
			item.setItemIcon(itemData.icon);
			item.setItemReqNum(iNum);
			item.setItemCurNum(curItemNum);
			items.add(item.build());
		}
		data.addAllReqItems(items);

		ArrayList<TalentDetail> TalentList = new ArrayList<>();
		// if (this.enSkillID_1 != 0){
		// SkillData skill = player.playerSkillManager.getSkill(this.enSkillID_1);
		// if (skill != null){
		// TalentList.add(skill.toJSON4DetailPayloadTalent(player));
		// }
		// }
		// if (this.enSkillID_2 != 0){
		// SkillData skill = player.playerSkillManager.getSkill(this.enSkillID_2);
		// if (skill != null){
		// TalentList.add(skill.toJSON4DetailPayloadTalent(player));
		// }
		// }
		data.addAllTalentList(TalentList);

		// int canUpgrade = this.checkUpgradeState();
		int canUpgrade = 0;
		data.setCanUpgrade(canUpgrade);
		return data.build();

	}

	public final CheckSkillData checkSkillReq(SkillDB skill) {
		int curLv = skill.lv;
		SkillDataExt prop = SkillUtil.getProp(skill.id);

		// 当前等级是否已达最高等级
		if (curLv >= prop.maxLevel) {
			CheckSkillData data = new CheckSkillData(-1);
			return data;
		}

		// var reqPreSkillId = this.prop.PreSkillID;
		// var reqPreSkillLv = this.prop.PreSkillLevel;
		// var reqPreSkill = player.skillManager.getSkill(reqPreSkillId);
		// if(reqPreSkill && reqPreSkill.lv < reqPreSkillLv){
		// return {result:-4};
		// }

		// 一 等级需求 lvReqData为数组[50,60], 当前等级做index索引得到需求等级
		int reqPlayerLevel = 0;
		if (curLv >= prop.lvReqData.size()) {
			reqPlayerLevel = 0;
		} else {
			reqPlayerLevel = prop.lvReqData.get(curLv);
		}
		if (reqPlayerLevel > player.getLevel()) {
			CheckSkillData data = new CheckSkillData(-2);
			return data;
		}

		// 二 金币需求
		int reqCost = 0;
		if (prop.costReqData.containsKey(curLv + 1)) {
			reqCost = prop.costReqData.get(curLv + 1);
		}
		if (!player.moneyManager.enoughGold(reqCost)) {
			CheckSkillData data = new CheckSkillData(-3);
			return data;
		}

		//// 第3检测是否SP满足
		// var reqSpCost = this.prop.costReqSpData[curLv + 1];
		// if(reqSpCost !== undefined && reqSpCost > player.sp){
		// return {result:-5};
		// }

		// 三 道具需求
		StringInt item = null;
		// CostItemReqData tempItem = this.prop.costItemReqData.get(curLv);
		// if (tempItem != null) {
		// String iCode = tempItem.itemCode;
		// int iNum = tempItem.num;
		// int curItemNum = player.getWnBag().findItemNumByCode(iCode);
		// if (iNum > 0) {
		// if (curItemNum >= iNum) {
		// item = new StringInt();
		// item.strValue = iCode;
		// item.intValue = iNum;
		// } else {
		// CheckSkillData data = new CheckSkillData(-6);
		// return data;
		// }
		// }
		// }
		CheckSkillData data = new CheckSkillData(0);
		data.reqCost = reqCost;
		data.reqCostItem = item;
		return data;
	}

	public final int getUpCorrectLevel(SkillDB skill) {
		return skill.lv <= 0 ? 1 : skill.lv;
	}

	private static class SkillComparator implements Comparator<SkillBasic> {

		@Override
		public int compare(SkillBasic skillA, SkillBasic skillB) {
			return skillA.getPos() - skillB.getPos();
		}

	}

	private static class SkillLevelComparator implements Comparator<SkillDB> {
		@Override
		public int compare(SkillDB skillA, SkillDB skillB) {
			return skillA.lv - skillB.lv;
		}

	}

	/**
	 * 改方法被调用的时候 不需要去额外加被动技能 只管生成对应的战斗服务器需要的数据
	 * 
	 * @param player
	 * @param skillIds
	 * @returns {*}
	 */
	public final List<SkillInfo> toJson4UpdateBattleServer(int type, List<Integer> skillIds) {
		if (skillIds == null || skillIds.size() <= 0) {
			return null;
		}
		ArrayList<SkillInfo> skills = new ArrayList<>();
		for (int skillId : skillIds) {
			SkillDB skill = getSkill(skillId);
			if (skill != null && skill.flag == 1) {
				SkillInfo skilldata = this._getBattlerServerSkillData(type, skill);
				Out.debug("toJson4UpdateBattleServer _getBattlerServerSkillData: ", skill.id, "--", skilldata);
				if (skilldata != null) {
					skills.add(skilldata);
				}
			}
		}
		return skills;
	}

	private final SkillInfo _getBattlerServerSkillData(int type, SkillDB skill) {
		SkillDataExt prop = SkillUtil.getProp(skill.id);
		if (type == Const.SkillHandleType.DELETE.getValue()) {
			SkillInfo info = new SkillInfo();
			info.id = skill.id;
			info.level = skill.lv;
			info.talentLevel = skill.lv;
			info.type = prop.skillType;
			info.skillTime = skill.skillTime;
			// info.cdTime = skill.reduceTime;
			info.cdTime = 0;
			return info;
		}
		SkillInfo info = new SkillInfo();
		if (prop.skillType == Const.SkillType.ACTIVE.getValue()) {
			// int enSkillid = this.player.playerSkillManager.getAdvancedSkillId(skill.id);
			int skillLv = skill.lv;
			int skillType = prop.skillType;
			/*
			 * if(skill.isNormal()){ skillType = Const.SkillType.ACTIVE; }
			 */
			// 如果不满足武器要求，则天赋技能不生效
			boolean isMeetWeapon = false;
			// int advanceLv = skillLv;
			// if(enSkillid != skill.id){
			// SkillData enskill = this.player.playerSkillManager.getSkill(enSkillid);
			// if(enskill != null){
			// advanceLv = enskill.skillDB.lv;
			// isMeetWeapon = enskill.isMeetWeapon();
			// }
			// }
			if (isMeetWeapon) {
				// info.id = enSkillid;
				// info.level = skillLv;
				// info.talentLevel = advanceLv;
				// info.type = skillType;
				// info.skillTime = skill.skillDB.skillTime;
				// info.cdTime = skill.reduceTime;
			} else {
				info.id = skill.id;
				info.level = skillLv;
				info.talentLevel = skillLv;
				info.type = skillType;
				info.skillTime = skill.skillTime;
				// info.cdTime = skill.reduceTime;
				info.cdTime = 0;
			}
		}
		// else if(prop.skillType == Const.SkillType.BATTLE_PASSIVE.getValue()){
		// info.id = this.player.playerSkillManager.getAdvancedSkillId(skill.id);
		// info.level = skill.lv;
		// info.talentLevel = skill.lv;
		// info.type = prop.skillType;
		// info.skillTime = skill.skillTime;
		//// info.cdTime = skill.reduceTime;
		// info.cdTime = 0;
		// }
		else {
			return null;
		}
		return info;
	}

	@Override
	public void onPlayerEvent(PlayerEventType eventType) {
		switch (eventType) {
		case UPGRADE:
			onPlayerUpgrade();
			break;
		case AFTER_LOGIN:

			break;
		default:
			break;
		}

	}

	@Override
	public List<SuperScriptType> getSuperScript() {
		List<SuperScriptType> list = new ArrayList<>();
		SuperScriptType.Builder data = SuperScriptType.newBuilder();
		data.setType(Const.SUPERSCRIPT_TYPE.SKILL.getValue());
		data.setNumber(0);
		for (SkillDB skill : player_skills.skills.values()) {

			if (this.checkSkillReq(skill).result == 0) {
				data.setNumber(1);
				break;
			}
		}
		list.add(data.build());
		return list;
	}

	@Override
	public ManagerType getManagerType() {
		return ManagerType.SKILL;
	}

	/**
	 * 判断技能是否可以升级，不考虑金币因素
	 * 
	 * @param skill
	 * @return
	 */
	public boolean canUpgradeSkill(SkillDB skill) {
		int curLv = skill.lv;
		SkillDataExt prop = SkillUtil.getProp(skill.id);

		// 当前等级是否已达最高等级
		if (curLv >= prop.maxLevel) {
			return false;
		}
		int reqPlayerLevel = 0;
		if (curLv >= prop.lvReqData.size()) {
			reqPlayerLevel = 0;
		} else {
			reqPlayerLevel = prop.lvReqData.get(curLv);
		}
		if (reqPlayerLevel > player.getLevel()) {
			return false;
		}
		return true;
	}

	/**
	 * 一键升级 一键升级按钮的逻辑： 先把能升级的技能找出来，按当前等级从低到高排序 取等级最低的技能，升一级，然后重新排序
	 * 循环以上两步，直到可升级技能列表为空
	 * 
	 * @return 0表示有技能升级成功 ，-1表示没有技能可以升级，-2表示没有足够的钱
	 */
	public int upgradeSkillOneKey2() {
		boolean flag = false;
		for (SkillDB skill : player_skills.skills.values()) {
			if (canUpgradeSkill(skill)) {
				flag = true;
				break;
			}
		}
		if (!flag)
			return -1;

		Map<Integer, Object> map = new HashMap<>();
		sortAndUpgrade(map, 150);
		List<Integer> list_id = new ArrayList<>();
		for (Integer id : map.keySet())
			list_id.add(id);

		if (list_id.size() > 0) {
			WNNotifyManager.getInstance().pushSkillUpdate(player, list_id);
			this.player.refreshBattlerServerSkill(Const.SkillHandleType.CHANGE.getValue(), list_id);
			player.onPlayerSkillUpgrade();
			player.pushDynamicData("gold", player.player.gold);
			return 0;
		} else
			return -2;
	}

	public static class CheckSkillData {
		public int result;
		public int reqCost;
		public StringInt reqCostItem;
		public int reqSpCost;

		public CheckSkillData(int result) {
			this.result = result;
		}
	}

	public static class SkillInfo {
		public int id;
		public int level;
		public int talentLevel;
		public int type;
		public long skillTime;
		public int cdTime;
		public int flag;
	}

	public int getSkillsPower() {
		int power = 0;
		for (SkillDB skill : player_skills.skills.values()) {
			// if(!skill.isTalent){
			SkillDataExt skillData = SkillUtil.getProp(skill.id);
			power = skillData.getSkillPower(skill.lv) + power;
			// }
			// else{
			// TalentEffectExt prop = GameData.TalentEffects.get(skill.id);
			// power = prop.getSkillPower(skill.lv) + power;
			// }
		}
		return power;
	}

	/**
	 * 计算被动技能给人物增加的属性
	 */
	public void refreshPassiveSkillData() {
		data_skill_attr = new ConcurrentHashMap<>();
		Map<Integer, SkillDB> map = player_skills.talentSkills;
		if (map == null)
			return;
		for (SkillDB s : map.values()) {
			PassiveSkillExt prop = GameData.PassiveSkills.get(s.id);
			if (prop.skillType == 0) {
				// 0：被动技能，服务器计算，被动生效，掌握后即给人物增加属性（也可以是宠物被动技能，对宠物自身加成）
				if (prop.ValueAttribute1 != null) {
					int value = 0;
					if (prop.ValueSetMap1 != null && prop.ValueSetMap1.containsKey(s.lv)) {
						value = prop.ValueSetMap1.get(s.lv);
					} else {
						Out.error("麻痹啊，天赋技能", s.id, "对应等级无数据");
					}
					data_skill_attr.put(prop.ValueAttribute1, value);
				}
				if (prop.ValueAttribute2 != null) {
					int value = 0;
					if (prop.ValueSetMap2 != null && prop.ValueSetMap2.containsKey(s.lv)) {
						value = prop.ValueSetMap2.get(s.lv);
					} else {
						Out.error("麻痹啊，天赋技能", s.id, "对应等级无数据");
					}
					data_skill_attr.put(prop.ValueAttribute2, value);
				}
				if (prop.ValueAttribute3 != null) {
					int value = 0;
					if (prop.ValueSetMap3 != null && prop.ValueSetMap3.containsKey(s.lv)) {
						value = prop.ValueSetMap3.get(s.lv);
					} else {
						Out.error("麻痹啊，天赋技能", s.id, "对应等级无数据");
					}
					data_skill_attr.put(prop.ValueAttribute3, value);
				}
			}
		}
	}

	public void addTalentPoint(int value) {
		player_skills.talentPoint += value;
	}

	// /**
	// * 升级天赋
	// * @param talentSkillId
	// * @return
	// */
	// public String upgradeTalent(int talentSkillId) {
	// TalentEffectExt prop = GameData.TalentEffects.get(talentSkillId);
	// if (prop == null) {
	// return LangService.getValue("SOMETHING_ERR");
	// }
	// int TalentGroupId = prop.talentGroup;
	// TalentGroupCO tg = GameData.TalentGroups.get(TalentGroupId);
	// if (tg == null || tg.groupJob != player.player.pro) {
	// return LangService.getValue("SOMETHING_ERR");
	// }
	// if (player_skills.talentPoint < 1) {
	// return "天赋点不足";
	// }
	// SkillDB s = player_skills.talentSkills.get(talentSkillId);
	// // 先找以前有没有升过级
	// if (s != null) {
	// if (s.lv < prop.maxLevel) {
	// s.lv++;
	// return null;
	// } else {
	// return "改天赋已经最高等级了";
	// }
	// } else {
	// s = new SkillDB();
	// s.lv = 1;
	// s.id = talentSkillId;
	// player_skills.talentSkills.put(s.id, s);
	// addNewTalentSkill(prop);
	// return null;
	// }
	// }

	/**
	 * 激活新的天赋
	 */
	private void addNewTalentSkill(TalentEffectExt prop) {
		if (prop.talentType == 0) {
			refreshPassiveSkillData();
			player.onTalentPassiveSkillUpgrade();
		} else if (prop.talentType == 1) {
			// 获得新的主动技能

		} else if (prop.talentType == 2) {
			SkillDB s = player_skills.skills.get(prop.beforeSkill);
			if (s != null) {
				int replaceSkillId = prop.getReplaceSkillId(1);
				if (replaceSkillId != 0)
					s.replaceSkillId = replaceSkillId;
			}
		}
	}

}
