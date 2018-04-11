package com.wanniu.game.playerData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.wanniu.core.logfs.Out;
import com.wanniu.core.util.StringUtil;
import com.wanniu.game.GWorld;
import com.wanniu.game.common.CommonUtil;
import com.wanniu.game.common.Const;
import com.wanniu.game.common.Const.ManagerType;
import com.wanniu.game.common.Const.PlayerBtlData;
import com.wanniu.game.common.Const.PlayerEventType;
import com.wanniu.game.common.ModuleManager;
import com.wanniu.game.daoyou.DaoYouCenter;
import com.wanniu.game.daoyou.DaoYouService;
import com.wanniu.game.data.GameData;
import com.wanniu.game.data.ext.CharacterExt;
import com.wanniu.game.data.ext.UpLevelExpExt;
import com.wanniu.game.player.AttributeUtil;
import com.wanniu.game.player.GlobalConfig;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.poes.DaoYouPO;
import com.wanniu.game.rank.RankType;

import pomelo.Common.AttributeSimple;

/**
 * @author fangyue
 *
 */
public class PlayerBtlDataManager extends ModuleManager {

	// refreshPlayerBasicData

	private static final int Tenthousand = 10000;

	public Map<PlayerBtlData, Integer> data_pro_lvl = new ConcurrentHashMap<>();

	public Map<PlayerBtlData, Integer> data_class = new ConcurrentHashMap<>();

	public Map<String, Integer> data_equip = new ConcurrentHashMap<>();

	/** 时装 */
	public Map<PlayerBtlData, Integer> data_fashion = new ConcurrentHashMap<>();

	public Map<PlayerBtlData, Integer> data_mount = new ConcurrentHashMap<>();

	public Map<PlayerBtlData, Integer> data_dao_you = new ConcurrentHashMap<>();

	public Map<PlayerBtlData, Integer> data_guild_bless = new ConcurrentHashMap<>();

	// 元始圣甲
	public Map<PlayerBtlData, Integer> data_holy_armour = new ConcurrentHashMap<>();
	// 兑换属性
	public Map<PlayerBtlData, Integer> data_exchange_property = new ConcurrentHashMap<>();
	//血脉
	public Map<PlayerBtlData, Integer> data_blood = new ConcurrentHashMap<>();

	/**
	 * 所有的模块带来的属性累计在这个集合里面
	 */
	public Map<PlayerBtlData, Integer> allInflus = new ConcurrentHashMap<>();

	/**
	 * 这个是拿allInflus，然后做了百分比加成
	 */
	public Map<PlayerBtlData, Integer> finalInflus = new ConcurrentHashMap<>();

	/**
	 * 用于计算战力的属性集合.
	 */
	public Map<PlayerBtlData, Integer> fightPowerInflus = new HashMap<>();

	private WNPlayer player;

	public PlayerBtlDataManager(WNPlayer player) {
		this.player = player;
		init();

	}

	private void init() {
		calProLvlData();
		calClassData();
		// TODO 后面还有装备，技能等系统带来的属性变化
		data_mount = player.mountManager.data_mount_final;
		calDaoYouData();
		data_equip = player.equipManager.calAllInfluence();
		data_fashion = player.fashionManager.calAllInfluence();
		data_holy_armour = player.achievementManager.calAllInfluence();
		data_blood = player.bloodManager.calAllInfluence();
		resetCalExchangeProparty();

		// 最后计算总战力
		calFinalData();
	}

	public void resetCalExchangeProparty() {
		int count = player.getPlayer().exchangCount;
		if (count > 0) {
			Map<PlayerBtlData, Integer> exchange_property = new ConcurrentHashMap<>();
			exchange_property.put(PlayerBtlData.MaxHP, GlobalConfig.Exchange_AddBlood * count);
			exchange_property.put(PlayerBtlData.Phy, GlobalConfig.Exchange_AddPhyAttack * count);
			exchange_property.put(PlayerBtlData.Ac, GlobalConfig.Exchange_AddPhyDefense * count);
			exchange_property.put(PlayerBtlData.Mag, GlobalConfig.Exchange_AddMagAttack * count);
			exchange_property.put(PlayerBtlData.Resist, GlobalConfig.Exchange_AddMagDefense * count);
			this.data_exchange_property = exchange_property;
		}
	}

	private void calClassData() {
		if (player.player.upOrder == 0) {
			data_class = new HashMap<>();
		} else {
			UpLevelExpExt prop = GameData.findUpLevelExps((t) -> t.upOrder == player.player.upOrder).get(0);
			data_class = prop.attrs;
		}

	}

	private void onEquipChange() {
		calFinalData();
	}


	
	private void calModuleData() {
		allInflus.clear();
		finalInflus.clear();
		AttributeUtil.addData2AllData(data_pro_lvl, allInflus);
		AttributeUtil.addData2AllData(data_class, allInflus);

		AttributeUtil.addData2AllDataByKey(data_equip, allInflus);
		// Map<String, Integer> mm = player.equipManager.calAllInfluence();
		// AttributeUtil.addData2AllDataByKey(mm, allInflus);
		AttributeUtil.addData2AllData(data_fashion, allInflus);
		AttributeUtil.addData2AllData(data_blood, allInflus);

		AttributeUtil.addData2AllData(player.skillManager.data_skill_attr, allInflus);
		AttributeUtil.addData2AllData(data_mount, allInflus);
		AttributeUtil.addData2AllData(player.petNewManager.masterAttr, allInflus);
		AttributeUtil.addData2AllData(player.petNewManager.masterAttrOnOutFight, allInflus);
		
		AttributeUtil.addData2AllData(data_dao_you, allInflus);
		AttributeUtil.addData2AllData(data_holy_armour, allInflus);
		// 兑换属性
		AttributeUtil.addData2AllData(data_exchange_property, allInflus);

		// 公会修行属性加成
		AttributeUtil.addData2AllDataByKey(player.guildManager.guildTechManager.calAllInfluence(), allInflus);
		// 称号属性加成
		AttributeUtil.addData2AllDataByKey(player.titleManager.calAllInfluence(), allInflus);

		// 机器人需要添加随机属性
		if (player.allBlobData.robotAttr != null) {
			AttributeUtil.addData2AllData(player.allBlobData.robotAttr, allInflus);
		}
	}
	
	/**
	 * 不参与战斗力计算的属性加在这方法的最下面，例如时效性buff类的属性
	 */
	private void calBuffData() {
		// 复制一份全属性，用来计算战斗力的...
		this.fightPowerInflus = new HashMap<>(allInflus);
		// 公会祈福属性加成
		AttributeUtil.addData2AllDataByKey(player.guildManager.calAllInfluence(), allInflus);
		// 工会BOSS里面的鼓舞加成(只针对工会BOSS场景里的生效)
		AttributeUtil.addData2AllDataByKey(player.guildBossManager.calAllInfluence(), allInflus);
	}
	
	private void calFinalData(int oldMaxHp,int oldPhy,int oldMag) {
		
		// 物攻 (物攻+攻击)×(物攻百分比+攻击百分比)
		int baseValue = 0;
		if (allInflus.containsKey(PlayerBtlData.Phy)) {
			baseValue = allInflus.get(PlayerBtlData.Phy);
		}
		if (allInflus.containsKey(PlayerBtlData.Attack)) {
			baseValue += allInflus.get(PlayerBtlData.Attack);
		}
		allInflus.put(PlayerBtlData.Phy, baseValue);
		int basePer = 0;
		if (allInflus.containsKey(PlayerBtlData.PhyPer)) {
			basePer += allInflus.get(PlayerBtlData.PhyPer);
		}
		if (allInflus.containsKey(PlayerBtlData.AttackPer)) {
			basePer += allInflus.get(PlayerBtlData.AttackPer);
		}
		allInflus.put(PlayerBtlData.PhyPer, basePer);
		// 魔攻
		baseValue = 0;
		if (allInflus.containsKey(PlayerBtlData.Mag)) {
			baseValue = allInflus.get(PlayerBtlData.Mag);
		}
		if (allInflus.containsKey(PlayerBtlData.Attack)) {
			baseValue += allInflus.get(PlayerBtlData.Attack);
		}
		allInflus.put(PlayerBtlData.Mag, baseValue);
		basePer = 0;
		if (allInflus.containsKey(PlayerBtlData.MagPer)) {
			basePer += allInflus.get(PlayerBtlData.MagPer);
		}
		if (allInflus.containsKey(PlayerBtlData.AttackPer)) {
			basePer += allInflus.get(PlayerBtlData.AttackPer);
		}
		allInflus.put(PlayerBtlData.MagPer, basePer);
		// 物防
		baseValue = 0;
		if (allInflus.containsKey(PlayerBtlData.Ac)) {
			baseValue = allInflus.get(PlayerBtlData.Ac);
		}
		if (allInflus.containsKey(PlayerBtlData.Def)) {
			baseValue += allInflus.get(PlayerBtlData.Def);
		}
		allInflus.put(PlayerBtlData.Ac, baseValue);
		basePer = 0;
		if (allInflus.containsKey(PlayerBtlData.AcPer)) {
			basePer += allInflus.get(PlayerBtlData.AcPer);
		}
		if (allInflus.containsKey(PlayerBtlData.DefPer)) {
			basePer += allInflus.get(PlayerBtlData.DefPer);
		}
		allInflus.put(PlayerBtlData.AcPer, basePer);
		// 魔防
		baseValue = 0;
		if (allInflus.containsKey(PlayerBtlData.Resist)) {
			baseValue = allInflus.get(PlayerBtlData.Resist);
		}
		if (allInflus.containsKey(PlayerBtlData.Def)) {
			baseValue += allInflus.get(PlayerBtlData.Def);
		}
		allInflus.put(PlayerBtlData.Resist, baseValue);
		basePer = 0;
		if (allInflus.containsKey(PlayerBtlData.ResistPer)) {
			basePer += allInflus.get(PlayerBtlData.ResistPer);
		}
		if (allInflus.containsKey(PlayerBtlData.DefPer)) {
			basePer += allInflus.get(PlayerBtlData.DefPer);
		}
		allInflus.put(PlayerBtlData.ResistPer, basePer);
		allInflus.remove(PlayerBtlData.Attack);
		allInflus.remove(PlayerBtlData.AttackPer);
		allInflus.remove(PlayerBtlData.Def);
		allInflus.remove(PlayerBtlData.DefPer);

		// 把所有属性复制到final
		AttributeUtil.addData2AllData(allInflus, finalInflus);

		// 生命
		int curMaxHp = 0;
		if (finalInflus.containsKey(PlayerBtlData.HPPer) && finalInflus.containsKey(PlayerBtlData.MaxHP)) {
			curMaxHp = finalInflus.get(PlayerBtlData.MaxHP);
			curMaxHp = curMaxHp + curMaxHp * finalInflus.get(PlayerBtlData.HPPer) / Tenthousand;
			finalInflus.put(PlayerBtlData.MaxHP, curMaxHp);
		}
		// 物攻 (物攻+攻击)×(物攻百分比+攻击百分比)
		if (finalInflus.containsKey(PlayerBtlData.Phy) && finalInflus.containsKey(PlayerBtlData.PhyPer)) {
			int value = finalInflus.get(PlayerBtlData.Phy);
			finalInflus.put(PlayerBtlData.Phy, value + value * finalInflus.get(PlayerBtlData.PhyPer) / Tenthousand);
		}
		// 魔攻
		if (finalInflus.containsKey(PlayerBtlData.Mag) && finalInflus.containsKey(PlayerBtlData.MagPer)) {
			int value = finalInflus.get(PlayerBtlData.Mag);
			finalInflus.put(PlayerBtlData.Mag, value + value * finalInflus.get(PlayerBtlData.MagPer) / Tenthousand);
		}
		// 命中
		if (finalInflus.containsKey(PlayerBtlData.Hit) && finalInflus.containsKey(PlayerBtlData.HitPer)) {
			int value = finalInflus.get(PlayerBtlData.Hit);
			finalInflus.put(PlayerBtlData.Hit, value + value * finalInflus.get(PlayerBtlData.HitPer) / Tenthousand);
		}
		// 闪避
		if (finalInflus.containsKey(PlayerBtlData.Dodge) && finalInflus.containsKey(PlayerBtlData.DodgePer)) {
			int value = finalInflus.get(PlayerBtlData.Dodge);
			finalInflus.put(PlayerBtlData.Dodge, value + value * finalInflus.get(PlayerBtlData.DodgePer) / Tenthousand);
		}
		// 暴击
		if (finalInflus.containsKey(PlayerBtlData.Crit) && finalInflus.containsKey(PlayerBtlData.CritPer)) {
			int value = finalInflus.get(PlayerBtlData.Crit);
			finalInflus.put(PlayerBtlData.Crit, value + value * finalInflus.get(PlayerBtlData.CritPer) / Tenthousand);
		}
		// 抗暴
		if (finalInflus.containsKey(PlayerBtlData.ResCrit) && finalInflus.containsKey(PlayerBtlData.ResCritPer)) {
			int value = finalInflus.get(PlayerBtlData.ResCrit);
			finalInflus.put(PlayerBtlData.ResCrit, value + value * finalInflus.get(PlayerBtlData.ResCritPer) / Tenthousand);
		}

		// 物防
		if (finalInflus.containsKey(PlayerBtlData.Ac) && finalInflus.containsKey(PlayerBtlData.AcPer)) {
			int value = finalInflus.get(PlayerBtlData.Ac);
			finalInflus.put(PlayerBtlData.Ac, value + value * finalInflus.get(PlayerBtlData.AcPer) / Tenthousand);
		}
		// 魔防
		if (finalInflus.containsKey(PlayerBtlData.Resist) && finalInflus.containsKey(PlayerBtlData.ResistPer)) {
			int value = finalInflus.get(PlayerBtlData.Resist);
			finalInflus.put(PlayerBtlData.Resist, value + value * finalInflus.get(PlayerBtlData.ResistPer) / Tenthousand);
		}

		int nowMaxHp = finalInflus.getOrDefault(PlayerBtlData.MaxHP, 0);
		int nowPhy = finalInflus.getOrDefault(PlayerBtlData.Phy, 0);
		int nowMag = finalInflus.getOrDefault(PlayerBtlData.Mag, 0);
		if (oldMaxHp != nowMaxHp && player.rankManager != null) {
			player.rankManager.onEvent(RankType.HP, nowMaxHp);
		}
		if (oldPhy != nowPhy && player.rankManager != null) {
			player.rankManager.onEvent(RankType.PHY, nowPhy);
		}
		if (oldMag != nowMag && player.rankManager != null) {
			player.rankManager.onEvent(RankType.MAGIC, nowMag);
		}
		if(GWorld.DEBUG) {			
			StringBuilder sb = new StringBuilder("\r\n==========================player:").append(player.getName()).append(" finalInflus begin=====================\r\n");
			for(PlayerBtlData key : finalInflus.keySet()) {
				sb.append(key.toString() + "\t" +key.chName + "=" + finalInflus.get(key)).append("\r\n");
			}
			sb.append("==========================player finalInflus end=================================\r\n");
			Out.error(sb.toString());
		}
	}
	

	/**
	 * 根据给定的扩展属性重新计算人物总属性
	 * 如果是buff类属性将只影响属性变化，而不会影响战斗力变化
	 * @param influs the attributes to increase
	 * @param isBuffAttr whether the influs are buffer or not
	 */
	public void calFinalData( Map<String, Integer> influs, boolean isBuffAttr) {
		int oldMaxHp = finalInflus.getOrDefault(PlayerBtlData.MaxHP, 0);
		int oldPhy = finalInflus.getOrDefault(PlayerBtlData.Phy, 0);
		int oldMag = finalInflus.getOrDefault(PlayerBtlData.Mag, 0);
		this.calModuleData();
		if(isBuffAttr) {//如果是buff类属性，加在计算战力之后，否则加在计算战力之前
			calBuffData();
			AttributeUtil.addData2AllDataByKey(influs, allInflus);
		}else {
			AttributeUtil.addData2AllDataByKey(influs, allInflus);
			calBuffData();
		}
		
		calFinalData(oldMaxHp, oldPhy, oldMag);
	}
	
	/**
	 * 计算所有属性
	 */
	public void calFinalData() {
		int oldMaxHp = finalInflus.getOrDefault(PlayerBtlData.MaxHP, 0);
		int oldPhy = finalInflus.getOrDefault(PlayerBtlData.Phy, 0);
		int oldMag = finalInflus.getOrDefault(PlayerBtlData.Mag, 0);
		//下面几个方法的调用顺序很重要
		calModuleData();
		calBuffData();
		calFinalData(oldMaxHp, oldPhy, oldMag);
	}

	/**
	 * 升级
	 */
	private void onPlayerUpgrade() {
		calProLvlData();
		calFinalData();
	}

	/**
	 * 升阶
	 */
	private void onClassLvlUp() {
		calClassData();
		calFinalData();
	}

	/**
	 * 兑换属性
	 */
	public void onExchangeProparty() {
		this.resetCalExchangeProparty();
		calFinalData();
	}

	/**
	 * 计算角色的职业和等级带来的属性
	 */
	private void calProLvlData() {
		data_pro_lvl.clear();
		CharacterExt character_prop = GameData.findCharacters((t) -> t.pro == player.player.pro).get(0);
		data_pro_lvl.put(PlayerBtlData.MaxHP, CommonUtil.getGrowUpValue(character_prop.initHP, character_prop.hPGrowUp, player.player.level));
		data_pro_lvl.put(PlayerBtlData.Phy, CommonUtil.getGrowUpValue(character_prop.basePhyDamage, character_prop.phyGrowUp, player.player.level));
		data_pro_lvl.put(PlayerBtlData.Mag, CommonUtil.getGrowUpValue(character_prop.baseMagDamage, character_prop.magGrowUp, player.player.level));
		data_pro_lvl.put(PlayerBtlData.Hit, CommonUtil.getGrowUpValue(character_prop.initHit, character_prop.hitGrowUP, player.player.level));
		data_pro_lvl.put(PlayerBtlData.Dodge, CommonUtil.getGrowUpValue(character_prop.initDodge, character_prop.dodgeGrowUP, player.player.level));
		data_pro_lvl.put(PlayerBtlData.Crit, CommonUtil.getGrowUpValue(character_prop.initCrit, character_prop.critGrowUP, player.player.level));
		data_pro_lvl.put(PlayerBtlData.ResCrit, CommonUtil.getGrowUpValue(character_prop.initResCrit, character_prop.resCritGrowUP, player.player.level));
		data_pro_lvl.put(PlayerBtlData.Ac, CommonUtil.getGrowUpValue(character_prop.initAc, character_prop.acGrowUp, player.player.level));
		data_pro_lvl.put(PlayerBtlData.Resist, CommonUtil.getGrowUpValue(character_prop.initResist, character_prop.resistGrowUp, player.player.level));
		data_pro_lvl.put(PlayerBtlData.HPRecover, character_prop.baseHPRegen);
		data_pro_lvl.put(PlayerBtlData.HealEffect, character_prop.healEffect);
		data_pro_lvl.put(PlayerBtlData.HealedEffect, character_prop.healedEffect);
		data_pro_lvl.put(PlayerBtlData.CritDamage, character_prop.critDamage);

		// 职业天赋
		if (StringUtil.isNotEmpty(character_prop.giftProp1)) {
			PlayerBtlData gift = PlayerBtlData.getE(character_prop.giftProp1);
			if (gift != null)
				putBtlData(gift, character_prop.giftValue1, data_pro_lvl);
		}
		if (!StringUtil.isNotEmpty(character_prop.giftProp2)) {
			PlayerBtlData gift = PlayerBtlData.getE(character_prop.giftProp2);
			if (gift != null)
				putBtlData(gift, character_prop.giftValue2, data_pro_lvl);
		}
		if (!StringUtil.isNotEmpty(character_prop.giftProp3)) {
			PlayerBtlData gift = PlayerBtlData.getE(character_prop.giftProp3);
			if (gift != null)
				putBtlData(gift, character_prop.giftValue3, data_pro_lvl);
		}

	}

	/**
	 * 添加一个属性到属性集合里面
	 * 
	 * @param e
	 * @param value
	 * @param map
	 */
	private static void putBtlData(PlayerBtlData e, int value, Map<PlayerBtlData, Integer> map) {
		if (map.containsKey(e)) {
			int value_data = map.get(e);
			map.put(e, value + value_data);
		} else
			map.put(e, value);
	}

	/**
	 * 发送给场景服的人物数据
	 * 
	 * @return
	 */
	public Map<String, Number> _getBattlerServerEffect() {
		Map<String, Number> map = new HashMap<>();
		for (PlayerBtlData pbd : PlayerBtlData.values()) {
			if (allInflus.containsKey(pbd)) {
				map.put(pbd.name(), allInflus.get(pbd));
			} else {
				map.put(pbd.name(), 0);
			}
		}
		map.put("HP", player.playerTempData.hp);
		float moveSpeed = Const.PLAYER.initSpeed * (1 + map.get(PlayerBtlData.RunSpeed.name()).floatValue() / Tenthousand);
		if (this.player.playerBasePO.speed != 0) {
			moveSpeed = this.player.playerBasePO.speed;
		}
		map.put("MoveSpeed", moveSpeed);
		int bagRemainCount = player.bag.emptyGridNum();
		map.put("bagRemainCount", bagRemainCount);
		return map;
	}

	public List<AttributeSimple> _getPlayerAttr() {
		List<AttributeSimple> list = new ArrayList<>();
		for (PlayerBtlData pbd : finalInflus.keySet()) {
			AttributeSimple.Builder asb = AttributeSimple.newBuilder();
			asb.setId(pbd.id);
			asb.setValue(finalInflus.get(pbd));
			list.add(asb.build());
		}
		return list;
	}

	public int getPlayerBtlPropValue(PlayerBtlData pbd) {
		if (finalInflus.containsKey(pbd)) {
			return finalInflus.get(pbd);
		}
		return 0;
	}

	public void onMountPropChange() {
		data_mount = player.mountManager.data_mount_final;
		calFinalData();

	}

	private void onPetPropChange() {
		calFinalData();

	}

	public void onDaoYouChange() {
		calDaoYouData();
		calFinalData();
	}

	public void onGuildBossInpire() {
		calFinalData();
	}

	public void calDaoYouData() {
		DaoYouPO dyp = DaoYouService.getInstance().getDaoYou(player.getId());
		if (dyp == null) {
			data_dao_you.clear();
			return;
		}
		String daoYouId = dyp.id;
		Map<PlayerBtlData, Integer> tempDataDaoYou = DaoYouCenter.getInstance().getDaoYouBtl(daoYouId);
		if (tempDataDaoYou == null) {
			return;
		}
		data_dao_you = tempDataDaoYou;
	}

	@Override
	public void onPlayerEvent(PlayerEventType eventType) {
		switch (eventType) {
		case UPGRADE:
			onPlayerUpgrade();
			break;
		case CLASS_UPGRADE:
			onClassLvlUp();
			break;
		case EQUIPMENT_CHANGE:
			onEquipChange();
			break;
		case PET_PROP_CHANGE:
			onPetPropChange();
			break;
		case GUILD_BLESS_CHANGE:
		case GUILD_TECH_CHANGE:
		case TITLE_CHANGE:
		case UPGRADE_TALENT_PASSIVE_SKILL:
			calFinalData();
			break;
		case EXCHANGE_PROPARTY:
			onExchangeProparty();
			break;
		default:
			break;
		}
	}

	@Override
	public ManagerType getManagerType() {
		return ManagerType.BTL_DATA;
	}
}
