package com.wanniu.game.data; 

import com.wanniu.game.data.ext.AccumulateExt;
import com.wanniu.game.data.ext.TurnRewardExt;
import com.wanniu.game.data.ext.BlessLevelExt;
import com.wanniu.game.data.ext.TeamTargetExt;
import com.wanniu.game.data.ext.UpLevelExpExt;
import com.wanniu.game.data.ext.BlessBuffExt;
import com.wanniu.game.data.ext.DayRewardExt;
import com.wanniu.game.data.ext.PurpleEquipExt;
import com.wanniu.game.data.ext.RankRewardExt;
import com.wanniu.game.data.ext.ScheduleExt;
import com.wanniu.game.data.ext.SmritiExt;
import com.wanniu.game.data.ext.MonsterRefreshExt;
import com.wanniu.game.data.ext.MasterPropExt;
import com.wanniu.game.data.ext.VitBonusExt;
import com.wanniu.game.data.ext.BlackWordExt;
import com.wanniu.game.data.ext.ActivityConfigExt;
import com.wanniu.game.data.ext.PetUpgradeExt;
import com.wanniu.game.data.ext.GuildFortExt;
import com.wanniu.game.data.ext.MedalListExt;
import com.wanniu.game.data.ext.RankListExt;
import com.wanniu.game.data.ext.TeamPropExt;
import com.wanniu.game.data.ext.CharacterExt;
import com.wanniu.game.data.ext.SevTaskRewardExt;
import com.wanniu.game.data.ext.DropListExt;
import com.wanniu.game.data.ext.SoloRankSeasonRewardExt;
import com.wanniu.game.data.ext.WingsListExt;
import com.wanniu.game.data.ext.DrawExt;
import com.wanniu.game.data.ext.EquipSockExt;
import com.wanniu.game.data.ext.AffixExt;
import com.wanniu.game.data.ext.UniqueEquipExt;
import com.wanniu.game.data.ext.BlueEquipExt;
import com.wanniu.game.data.ext.FirstPayExt;
import com.wanniu.game.data.ext.TreasureClassExt;
import com.wanniu.game.data.ext.SuitEquipExt;
import com.wanniu.game.data.ext.PetSkillExt;
import com.wanniu.game.data.ext.NormalSignExt;
import com.wanniu.game.data.ext.EnchantExt;
import com.wanniu.game.data.ext.RefineExt;
import com.wanniu.game.data.ext.PersonalRankExt;
import com.wanniu.game.data.ext.WayTreasureExt;
import com.wanniu.game.data.ext.AchievementConfigExt;
import com.wanniu.game.data.ext.AttributeExt;
import com.wanniu.game.data.ext.GuildSettingExt;
import com.wanniu.game.data.ext.GTechnologyExt;
import com.wanniu.game.data.ext.DailyPayExt;
import com.wanniu.game.data.ext.ItemIdConfigExt;
import com.wanniu.game.data.ext.BattleRoyaleRankSeasonRewardExt;
import com.wanniu.game.data.ext.ArmourAttributeExt;
import com.wanniu.game.data.ext.FashSuitConfigExt;
import com.wanniu.game.data.ext.BattleRoyaleRankExt;
import com.wanniu.game.data.ext.LuxurySignExt;
import com.wanniu.game.data.ext.PetConfigExt;
import com.wanniu.game.data.ext.ReBornExt;
import com.wanniu.game.data.ext.GBuffExt;
import com.wanniu.game.data.ext.FashionExt;
import com.wanniu.game.data.ext.SeniorReBuildExt;
import com.wanniu.game.data.ext.SkillDataExt;
import com.wanniu.game.data.ext.ActivityExt;
import com.wanniu.game.data.ext.PetAssociateExt;
import com.wanniu.game.data.ext.PassiveSkillExt;
import com.wanniu.game.data.ext.RideEquipExt;
import com.wanniu.game.data.ext.SoloRankExt;
import com.wanniu.game.data.ext.CombineExt;
import com.wanniu.game.data.ext.NpcListExt;
import com.wanniu.game.data.ext.SuitListExt;
import com.wanniu.game.data.ext.ShopMallItemsExt;
import com.wanniu.game.data.ext.ExchangeMallExt;
import com.wanniu.game.data.ext.MasterUpgradePropExt;
import com.wanniu.game.data.ext.NoticeSendExt;
import com.wanniu.game.data.ext.JJCRewardExt;
import com.wanniu.game.data.ext.ReBuildExt;
import com.wanniu.game.data.ext.OlGiftExt;
import com.wanniu.game.data.ext.GShopExt;
import com.wanniu.game.data.ext.LegendEquipExt;
import com.wanniu.game.data.ext.AchievementExt;
import com.wanniu.game.data.ext.EnchantBonusExt;
import com.wanniu.game.data.ext.RecoveryExt;
import com.wanniu.game.data.ext.InspireLevelExt;
import com.wanniu.game.data.ext.RideListExt;
import com.wanniu.game.data.ext.BlesslibaoExt;
import com.wanniu.game.data.ext.SuitConfigExt;
import com.wanniu.game.data.ext.PayRewardExt;
import com.wanniu.game.data.ext.SkillValueExt;
import com.wanniu.game.data.ext.RandomBoxExt;
import com.wanniu.game.data.ext.BloodSuitListExt;
import com.wanniu.game.data.ext.EquipMakeExt;
import com.wanniu.game.data.ext.MasteryPropExt;
import com.wanniu.game.data.ext.ItemTypeConfigExt;
import com.wanniu.game.data.ext.TotalPayExt;
import com.wanniu.game.data.ext.BaseDataExt;
import com.wanniu.game.data.ext.TransportExt;
import com.wanniu.game.data.ext.NormalEquipExt;
import com.wanniu.game.data.ext.SkinListExt;
import com.wanniu.game.data.ext.TalentEffectExt;
import com.wanniu.game.data.ext.ArmourPlusExt;
import com.wanniu.game.data.ext.KaiGuangExt;
import java.util.Map; 
import java.util.HashMap; 

import java.util.function.Predicate; 

import java.util.List; 

import java.util.ArrayList; 

/** 游戏-策划配置 */
public final class GameData { 

	public static Map<Integer, DayRewardExt> DayRewards = new HashMap<Integer, DayRewardExt>();
	public static List<DayRewardExt> findDayRewards(Predicate<DayRewardExt> pre) {
		List<DayRewardExt> results = new ArrayList<>();
		for(DayRewardExt t : DayRewards.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, PersonalRankExt> PersonalRanks = new HashMap<Integer, PersonalRankExt>();
	public static List<PersonalRankExt> findPersonalRanks(Predicate<PersonalRankExt> pre) {
		List<PersonalRankExt> results = new ArrayList<>();
		for(PersonalRankExt t : PersonalRanks.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, AchievementExt> Achievements = new HashMap<Integer, AchievementExt>();
	public static List<AchievementExt> findAchievements(Predicate<AchievementExt> pre) {
		List<AchievementExt> results = new ArrayList<>();
		for(AchievementExt t : Achievements.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, AchievementConfigExt> AchievementConfigs = new HashMap<Integer, AchievementConfigExt>();
	public static List<AchievementConfigExt> findAchievementConfigs(Predicate<AchievementConfigExt> pre) {
		List<AchievementConfigExt> results = new ArrayList<>();
		for(AchievementConfigExt t : AchievementConfigs.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, ArmourAttributeExt> ArmourAttributes = new HashMap<Integer, ArmourAttributeExt>();
	public static List<ArmourAttributeExt> findArmourAttributes(Predicate<ArmourAttributeExt> pre) {
		List<ArmourAttributeExt> results = new ArrayList<>();
		for(ArmourAttributeExt t : ArmourAttributes.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, ArmourPlusExt> ArmourPluss = new HashMap<Integer, ArmourPlusExt>();
	public static List<ArmourPlusExt> findArmourPluss(Predicate<ArmourPlusExt> pre) {
		List<ArmourPlusExt> results = new ArrayList<>();
		for(ArmourPlusExt t : ArmourPluss.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, AchievementSeniorCO> AchievementSeniors = new HashMap<Integer, AchievementSeniorCO>();
	public static List<AchievementSeniorCO> findAchievementSeniors(Predicate<AchievementSeniorCO> pre) {
		List<AchievementSeniorCO> results = new ArrayList<>();
		for(AchievementSeniorCO t : AchievementSeniors.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, AchievementAwardCO> AchievementAwards = new HashMap<Integer, AchievementAwardCO>();
	public static List<AchievementAwardCO> findAchievementAwards(Predicate<AchievementAwardCO> pre) {
		List<AchievementAwardCO> results = new ArrayList<>();
		for(AchievementAwardCO t : AchievementAwards.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, AchievementConfigSeniorCO> AchievementConfigSeniors = new HashMap<Integer, AchievementConfigSeniorCO>();
	public static List<AchievementConfigSeniorCO> findAchievementConfigSeniors(Predicate<AchievementConfigSeniorCO> pre) {
		List<AchievementConfigSeniorCO> results = new ArrayList<>();
		for(AchievementConfigSeniorCO t : AchievementConfigSeniors.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, AchievewordsCO> Achievewordss = new HashMap<Integer, AchievewordsCO>();
	public static List<AchievewordsCO> findAchievewordss(Predicate<AchievewordsCO> pre) {
		List<AchievewordsCO> results = new ArrayList<>();
		for(AchievewordsCO t : Achievewordss.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, ActivityExt> Activitys = new HashMap<Integer, ActivityExt>();
	public static List<ActivityExt> findActivitys(Predicate<ActivityExt> pre) {
		List<ActivityExt> results = new ArrayList<>();
		for(ActivityExt t : Activitys.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, ActivityConfigExt> ActivityConfigs = new HashMap<Integer, ActivityConfigExt>();
	public static List<ActivityConfigExt> findActivityConfigs(Predicate<ActivityConfigExt> pre) {
		List<ActivityConfigExt> results = new ArrayList<>();
		for(ActivityConfigExt t : ActivityConfigs.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, DiceCostCO> DiceCosts = new HashMap<Integer, DiceCostCO>();
	public static List<DiceCostCO> findDiceCosts(Predicate<DiceCostCO> pre) {
		List<DiceCostCO> results = new ArrayList<>();
		for(DiceCostCO t : DiceCosts.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, DiceFreeCO> DiceFrees = new HashMap<Integer, DiceFreeCO>();
	public static List<DiceFreeCO> findDiceFrees(Predicate<DiceFreeCO> pre) {
		List<DiceFreeCO> results = new ArrayList<>();
		for(DiceFreeCO t : DiceFrees.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, DiceRewardCO> DiceRewards = new HashMap<Integer, DiceRewardCO>();
	public static List<DiceRewardCO> findDiceRewards(Predicate<DiceRewardCO> pre) {
		List<DiceRewardCO> results = new ArrayList<>();
		for(DiceRewardCO t : DiceRewards.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, DiceTurnRewardCO> DiceTurnRewards = new HashMap<Integer, DiceTurnRewardCO>();
	public static List<DiceTurnRewardCO> findDiceTurnRewards(Predicate<DiceTurnRewardCO> pre) {
		List<DiceTurnRewardCO> results = new ArrayList<>();
		for(DiceTurnRewardCO t : DiceTurnRewards.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, FestivalGiftCO> FestivalGifts = new HashMap<Integer, FestivalGiftCO>();
	public static List<FestivalGiftCO> findFestivalGifts(Predicate<FestivalGiftCO> pre) {
		List<FestivalGiftCO> results = new ArrayList<>();
		for(FestivalGiftCO t : FestivalGifts.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, LimitTimeGiftCO> LimitTimeGifts = new HashMap<Integer, LimitTimeGiftCO>();
	public static List<LimitTimeGiftCO> findLimitTimeGifts(Predicate<LimitTimeGiftCO> pre) {
		List<LimitTimeGiftCO> results = new ArrayList<>();
		for(LimitTimeGiftCO t : LimitTimeGifts.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, AddRechargeLimitCO> AddRechargeLimits = new HashMap<Integer, AddRechargeLimitCO>();
	public static List<AddRechargeLimitCO> findAddRechargeLimits(Predicate<AddRechargeLimitCO> pre) {
		List<AddRechargeLimitCO> results = new ArrayList<>();
		for(AddRechargeLimitCO t : AddRechargeLimits.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, AdventureItemCO> AdventureItems = new HashMap<Integer, AdventureItemCO>();
	public static List<AdventureItemCO> findAdventureItems(Predicate<AdventureItemCO> pre) {
		List<AdventureItemCO> results = new ArrayList<>();
		for(AdventureItemCO t : AdventureItems.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, AdventureItemAddCO> AdventureItemAdds = new HashMap<Integer, AdventureItemAddCO>();
	public static List<AdventureItemAddCO> findAdventureItemAdds(Predicate<AdventureItemAddCO> pre) {
		List<AdventureItemAddCO> results = new ArrayList<>();
		for(AdventureItemAddCO t : AdventureItemAdds.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, ForgedRandomCO> ForgedRandoms = new HashMap<Integer, ForgedRandomCO>();
	public static List<ForgedRandomCO> findForgedRandoms(Predicate<ForgedRandomCO> pre) {
		List<ForgedRandomCO> results = new ArrayList<>();
		for(ForgedRandomCO t : ForgedRandoms.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, ForgedRandomAddCO> ForgedRandomAdds = new HashMap<Integer, ForgedRandomAddCO>();
	public static List<ForgedRandomAddCO> findForgedRandomAdds(Predicate<ForgedRandomAddCO> pre) {
		List<ForgedRandomAddCO> results = new ArrayList<>();
		for(ForgedRandomAddCO t : ForgedRandomAdds.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, GroupRandomCO> GroupRandoms = new HashMap<Integer, GroupRandomCO>();
	public static List<GroupRandomCO> findGroupRandoms(Predicate<GroupRandomCO> pre) {
		List<GroupRandomCO> results = new ArrayList<>();
		for(GroupRandomCO t : GroupRandoms.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, GroupRandomAddCO> GroupRandomAdds = new HashMap<Integer, GroupRandomAddCO>();
	public static List<GroupRandomAddCO> findGroupRandomAdds(Predicate<GroupRandomAddCO> pre) {
		List<GroupRandomAddCO> results = new ArrayList<>();
		for(GroupRandomAddCO t : GroupRandomAdds.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, BombboxCO> Bombboxs = new HashMap<Integer, BombboxCO>();
	public static List<BombboxCO> findBombboxs(Predicate<BombboxCO> pre) {
		List<BombboxCO> results = new ArrayList<>();
		for(BombboxCO t : Bombboxs.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, PpresentCO> Ppresents = new HashMap<Integer, PpresentCO>();
	public static List<PpresentCO> findPpresents(Predicate<PpresentCO> pre) {
		List<PpresentCO> results = new ArrayList<>();
		for(PpresentCO t : Ppresents.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, RankpreCO> Rankpres = new HashMap<Integer, RankpreCO>();
	public static List<RankpreCO> findRankpres(Predicate<RankpreCO> pre) {
		List<RankpreCO> results = new ArrayList<>();
		for(RankpreCO t : Rankpres.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, DropListExt> DropLists = new HashMap<Integer, DropListExt>();
	public static List<DropListExt> findDropLists(Predicate<DropListExt> pre) {
		List<DropListExt> results = new ArrayList<>();
		for(DropListExt t : DropLists.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<String, MonsterConfigCO> MonsterConfigs = new HashMap<String, MonsterConfigCO>();
	public static List<MonsterConfigCO> findMonsterConfigs(Predicate<MonsterConfigCO> pre) {
		List<MonsterConfigCO> results = new ArrayList<>();
		for(MonsterConfigCO t : MonsterConfigs.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, DrawExt> Draws = new HashMap<Integer, DrawExt>();
	public static List<DrawExt> findDraws(Predicate<DrawExt> pre) {
		List<DrawExt> results = new ArrayList<>();
		for(DrawExt t : Draws.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, NoticeCO> Notices = new HashMap<Integer, NoticeCO>();
	public static List<NoticeCO> findNotices(Predicate<NoticeCO> pre) {
		List<NoticeCO> results = new ArrayList<>();
		for(NoticeCO t : Notices.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, NoticeSendExt> NoticeSends = new HashMap<Integer, NoticeSendExt>();
	public static List<NoticeSendExt> findNoticeSends(Predicate<NoticeSendExt> pre) {
		List<NoticeSendExt> results = new ArrayList<>();
		for(NoticeSendExt t : NoticeSends.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, OffLineExpCO> OffLineExps = new HashMap<Integer, OffLineExpCO>();
	public static List<OffLineExpCO> findOffLineExps(Predicate<OffLineExpCO> pre) {
		List<OffLineExpCO> results = new ArrayList<>();
		for(OffLineExpCO t : OffLineExps.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, OlGiftExt> OlGifts = new HashMap<Integer, OlGiftExt>();
	public static List<OlGiftExt> findOlGifts(Predicate<OlGiftExt> pre) {
		List<OlGiftExt> results = new ArrayList<>();
		for(OlGiftExt t : OlGifts.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, GetLandCO> GetLands = new HashMap<Integer, GetLandCO>();
	public static List<GetLandCO> findGetLands(Predicate<GetLandCO> pre) {
		List<GetLandCO> results = new ArrayList<>();
		for(GetLandCO t : GetLands.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, PlantingCO> Plantings = new HashMap<Integer, PlantingCO>();
	public static List<PlantingCO> findPlantings(Predicate<PlantingCO> pre) {
		List<PlantingCO> results = new ArrayList<>();
		for(PlantingCO t : Plantings.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, PlantLevelCO> PlantLevels = new HashMap<Integer, PlantLevelCO>();
	public static List<PlantLevelCO> findPlantLevels(Predicate<PlantLevelCO> pre) {
		List<PlantLevelCO> results = new ArrayList<>();
		for(PlantLevelCO t : PlantLevels.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, PlantRecordCO> PlantRecords = new HashMap<Integer, PlantRecordCO>();
	public static List<PlantRecordCO> findPlantRecords(Predicate<PlantRecordCO> pre) {
		List<PlantRecordCO> results = new ArrayList<>();
		for(PlantRecordCO t : PlantRecords.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, PlantShopCO> PlantShops = new HashMap<Integer, PlantShopCO>();
	public static List<PlantShopCO> findPlantShops(Predicate<PlantShopCO> pre) {
		List<PlantShopCO> results = new ArrayList<>();
		for(PlantShopCO t : PlantShops.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, RecoveryExt> Recoverys = new HashMap<Integer, RecoveryExt>();
	public static List<RecoveryExt> findRecoverys(Predicate<RecoveryExt> pre) {
		List<RecoveryExt> results = new ArrayList<>();
		for(RecoveryExt t : Recoverys.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, KingCO> Kings = new HashMap<Integer, KingCO>();
	public static List<KingCO> findKings(Predicate<KingCO> pre) {
		List<KingCO> results = new ArrayList<>();
		for(KingCO t : Kings.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<String, RevelryCO> Revelrys = new HashMap<String, RevelryCO>();
	public static List<RevelryCO> findRevelrys(Predicate<RevelryCO> pre) {
		List<RevelryCO> results = new ArrayList<>();
		for(RevelryCO t : Revelrys.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, RevelryConfigCO> RevelryConfigs = new HashMap<Integer, RevelryConfigCO>();
	public static List<RevelryConfigCO> findRevelryConfigs(Predicate<RevelryConfigCO> pre) {
		List<RevelryConfigCO> results = new ArrayList<>();
		for(RevelryConfigCO t : RevelryConfigs.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, StartSerRechargeCO> StartSerRecharges = new HashMap<Integer, StartSerRechargeCO>();
	public static List<StartSerRechargeCO> findStartSerRecharges(Predicate<StartSerRechargeCO> pre) {
		List<StartSerRechargeCO> results = new ArrayList<>();
		for(StartSerRechargeCO t : StartSerRecharges.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, OutPutTypeCO> OutPutTypes = new HashMap<Integer, OutPutTypeCO>();
	public static List<OutPutTypeCO> findOutPutTypes(Predicate<OutPutTypeCO> pre) {
		List<OutPutTypeCO> results = new ArrayList<>();
		for(OutPutTypeCO t : OutPutTypes.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, RecomPlayCO> RecomPlays = new HashMap<Integer, RecomPlayCO>();
	public static List<RecomPlayCO> findRecomPlays(Predicate<RecomPlayCO> pre) {
		List<RecomPlayCO> results = new ArrayList<>();
		for(RecomPlayCO t : RecomPlays.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, ScheduleExt> Schedules = new HashMap<Integer, ScheduleExt>();
	public static List<ScheduleExt> findSchedules(Predicate<ScheduleExt> pre) {
		List<ScheduleExt> results = new ArrayList<>();
		for(ScheduleExt t : Schedules.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, TaskCycleTypeCO> TaskCycleTypes = new HashMap<Integer, TaskCycleTypeCO>();
	public static List<TaskCycleTypeCO> findTaskCycleTypes(Predicate<TaskCycleTypeCO> pre) {
		List<TaskCycleTypeCO> results = new ArrayList<>();
		for(TaskCycleTypeCO t : TaskCycleTypes.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, VitBonusExt> VitBonuss = new HashMap<Integer, VitBonusExt>();
	public static List<VitBonusExt> findVitBonuss(Predicate<VitBonusExt> pre) {
		List<VitBonusExt> results = new ArrayList<>();
		for(VitBonusExt t : VitBonuss.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, SevDayTaskCO> SevDayTasks = new HashMap<Integer, SevDayTaskCO>();
	public static List<SevDayTaskCO> findSevDayTasks(Predicate<SevDayTaskCO> pre) {
		List<SevDayTaskCO> results = new ArrayList<>();
		for(SevDayTaskCO t : SevDayTasks.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, SevTaskInsCO> SevTaskInss = new HashMap<Integer, SevTaskInsCO>();
	public static List<SevTaskInsCO> findSevTaskInss(Predicate<SevTaskInsCO> pre) {
		List<SevTaskInsCO> results = new ArrayList<>();
		for(SevTaskInsCO t : SevTaskInss.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, SevTaskRewardExt> SevTaskRewards = new HashMap<Integer, SevTaskRewardExt>();
	public static List<SevTaskRewardExt> findSevTaskRewards(Predicate<SevTaskRewardExt> pre) {
		List<SevTaskRewardExt> results = new ArrayList<>();
		for(SevTaskRewardExt t : SevTaskRewards.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, SevenLoginCO> SevenLogins = new HashMap<Integer, SevenLoginCO>();
	public static List<SevenLoginCO> findSevenLogins(Predicate<SevenLoginCO> pre) {
		List<SevenLoginCO> results = new ArrayList<>();
		for(SevenLoginCO t : SevenLogins.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Object, SingleCzCO> SingleCzs = new HashMap<Object, SingleCzCO>();
	public static List<SingleCzCO> findSingleCzs(Predicate<SingleCzCO> pre) {
		List<SingleCzCO> results = new ArrayList<>();
		for(SingleCzCO t : SingleCzs.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, SuperPackageCO> SuperPackages = new HashMap<Integer, SuperPackageCO>();
	public static List<SuperPackageCO> findSuperPackages(Predicate<SuperPackageCO> pre) {
		List<SuperPackageCO> results = new ArrayList<>();
		for(SuperPackageCO t : SuperPackages.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, GroupProbCO> GroupProbs = new HashMap<Integer, GroupProbCO>();
	public static List<GroupProbCO> findGroupProbs(Predicate<GroupProbCO> pre) {
		List<GroupProbCO> results = new ArrayList<>();
		for(GroupProbCO t : GroupProbs.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, PseudoRandomCO> PseudoRandoms = new HashMap<Integer, PseudoRandomCO>();
	public static List<PseudoRandomCO> findPseudoRandoms(Predicate<PseudoRandomCO> pre) {
		List<PseudoRandomCO> results = new ArrayList<>();
		for(PseudoRandomCO t : PseudoRandoms.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, TreasuresItemCO> TreasuresItems = new HashMap<Integer, TreasuresItemCO>();
	public static List<TreasuresItemCO> findTreasuresItems(Predicate<TreasuresItemCO> pre) {
		List<TreasuresItemCO> results = new ArrayList<>();
		for(TreasuresItemCO t : TreasuresItems.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, TreasuresShowCO> TreasuresShows = new HashMap<Integer, TreasuresShowCO>();
	public static List<TreasuresShowCO> findTreasuresShows(Predicate<TreasuresShowCO> pre) {
		List<TreasuresShowCO> results = new ArrayList<>();
		for(TreasuresShowCO t : TreasuresShows.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, WelfareCO> Welfares = new HashMap<Integer, WelfareCO>();
	public static List<WelfareCO> findWelfares(Predicate<WelfareCO> pre) {
		List<WelfareCO> results = new ArrayList<>();
		for(WelfareCO t : Welfares.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, TurnRewardExt> TurnRewards = new HashMap<Integer, TurnRewardExt>();
	public static List<TurnRewardExt> findTurnRewards(Predicate<TurnRewardExt> pre) {
		List<TurnRewardExt> results = new ArrayList<>();
		for(TurnRewardExt t : TurnRewards.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, ZillionaireCageCO> ZillionaireCages = new HashMap<Integer, ZillionaireCageCO>();
	public static List<ZillionaireCageCO> findZillionaireCages(Predicate<ZillionaireCageCO> pre) {
		List<ZillionaireCageCO> results = new ArrayList<>();
		for(ZillionaireCageCO t : ZillionaireCages.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, ZillionaireFreeCO> ZillionaireFrees = new HashMap<Integer, ZillionaireFreeCO>();
	public static List<ZillionaireFreeCO> findZillionaireFrees(Predicate<ZillionaireFreeCO> pre) {
		List<ZillionaireFreeCO> results = new ArrayList<>();
		for(ZillionaireFreeCO t : ZillionaireFrees.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, BattleRoyaleRankExt> BattleRoyaleRanks = new HashMap<Integer, BattleRoyaleRankExt>();
	public static List<BattleRoyaleRankExt> findBattleRoyaleRanks(Predicate<BattleRoyaleRankExt> pre) {
		List<BattleRoyaleRankExt> results = new ArrayList<>();
		for(BattleRoyaleRankExt t : BattleRoyaleRanks.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, BattleRoyaleRankSeasonRewardExt> BattleRoyaleRankSeasonRewards = new HashMap<Integer, BattleRoyaleRankSeasonRewardExt>();
	public static List<BattleRoyaleRankSeasonRewardExt> findBattleRoyaleRankSeasonRewards(Predicate<BattleRoyaleRankSeasonRewardExt> pre) {
		List<BattleRoyaleRankSeasonRewardExt> results = new ArrayList<>();
		for(BattleRoyaleRankSeasonRewardExt t : BattleRoyaleRankSeasonRewards.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, BeStrConfigCO> BeStrConfigs = new HashMap<Integer, BeStrConfigCO>();
	public static List<BeStrConfigCO> findBeStrConfigs(Predicate<BeStrConfigCO> pre) {
		List<BeStrConfigCO> results = new ArrayList<>();
		for(BeStrConfigCO t : BeStrConfigs.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, BeStrListCO> BeStrLists = new HashMap<Integer, BeStrListCO>();
	public static List<BeStrListCO> findBeStrLists(Predicate<BeStrListCO> pre) {
		List<BeStrListCO> results = new ArrayList<>();
		for(BeStrListCO t : BeStrLists.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, BloodListCO> BloodLists = new HashMap<Integer, BloodListCO>();
	public static List<BloodListCO> findBloodLists(Predicate<BloodListCO> pre) {
		List<BloodListCO> results = new ArrayList<>();
		for(BloodListCO t : BloodLists.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, BloodLVUpCO> BloodLVUps = new HashMap<Integer, BloodLVUpCO>();
	public static List<BloodLVUpCO> findBloodLVUps(Predicate<BloodLVUpCO> pre) {
		List<BloodLVUpCO> results = new ArrayList<>();
		for(BloodLVUpCO t : BloodLVUps.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, BloodLVLimitCO> BloodLVLimits = new HashMap<Integer, BloodLVLimitCO>();
	public static List<BloodLVLimitCO> findBloodLVLimits(Predicate<BloodLVLimitCO> pre) {
		List<BloodLVLimitCO> results = new ArrayList<>();
		for(BloodLVLimitCO t : BloodLVLimits.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, BloodProCO> BloodPros = new HashMap<Integer, BloodProCO>();
	public static List<BloodProCO> findBloodPros(Predicate<BloodProCO> pre) {
		List<BloodProCO> results = new ArrayList<>();
		for(BloodProCO t : BloodPros.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, BloodProNumCO> BloodProNums = new HashMap<Integer, BloodProNumCO>();
	public static List<BloodProNumCO> findBloodProNums(Predicate<BloodProNumCO> pre) {
		List<BloodProNumCO> results = new ArrayList<>();
		for(BloodProNumCO t : BloodProNums.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, BloodSuitConfigCO> BloodSuitConfigs = new HashMap<Integer, BloodSuitConfigCO>();
	public static List<BloodSuitConfigCO> findBloodSuitConfigs(Predicate<BloodSuitConfigCO> pre) {
		List<BloodSuitConfigCO> results = new ArrayList<>();
		for(BloodSuitConfigCO t : BloodSuitConfigs.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, BloodSuitListExt> BloodSuitLists = new HashMap<Integer, BloodSuitListExt>();
	public static List<BloodSuitListExt> findBloodSuitLists(Predicate<BloodSuitListExt> pre) {
		List<BloodSuitListExt> results = new ArrayList<>();
		for(BloodSuitListExt t : BloodSuitLists.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, BloodTipsCO> BloodTipss = new HashMap<Integer, BloodTipsCO>();
	public static List<BloodTipsCO> findBloodTipss(Predicate<BloodTipsCO> pre) {
		List<BloodTipsCO> results = new ArrayList<>();
		for(BloodTipsCO t : BloodTipss.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<String, BlackWordExt> BlackWords = new HashMap<String, BlackWordExt>();
	public static List<BlackWordExt> findBlackWords(Predicate<BlackWordExt> pre) {
		List<BlackWordExt> results = new ArrayList<>();
		for(BlackWordExt t : BlackWords.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, ActionCO> Actions = new HashMap<Integer, ActionCO>();
	public static List<ActionCO> findActions(Predicate<ActionCO> pre) {
		List<ActionCO> results = new ArrayList<>();
		for(ActionCO t : Actions.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, ChatSettingCO> ChatSettings = new HashMap<Integer, ChatSettingCO>();
	public static List<ChatSettingCO> findChatSettings(Predicate<ChatSettingCO> pre) {
		List<ChatSettingCO> results = new ArrayList<>();
		for(ChatSettingCO t : ChatSettings.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, DefMsgCO> DefMsgs = new HashMap<Integer, DefMsgCO>();
	public static List<DefMsgCO> findDefMsgs(Predicate<DefMsgCO> pre) {
		List<DefMsgCO> results = new ArrayList<>();
		for(DefMsgCO t : DefMsgs.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, EmoteCO> Emotes = new HashMap<Integer, EmoteCO>();
	public static List<EmoteCO> findEmotes(Predicate<EmoteCO> pre) {
		List<EmoteCO> results = new ArrayList<>();
		for(EmoteCO t : Emotes.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, InteractionCO> Interactions = new HashMap<Integer, InteractionCO>();
	public static List<InteractionCO> findInteractions(Predicate<InteractionCO> pre) {
		List<InteractionCO> results = new ArrayList<>();
		for(InteractionCO t : Interactions.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, ShowMsgCO> ShowMsgs = new HashMap<Integer, ShowMsgCO>();
	public static List<ShowMsgCO> findShowMsgs(Predicate<ShowMsgCO> pre) {
		List<ShowMsgCO> results = new ArrayList<>();
		for(ShowMsgCO t : ShowMsgs.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, DungeonMapCostCO> DungeonMapCosts = new HashMap<Integer, DungeonMapCostCO>();
	public static List<DungeonMapCostCO> findDungeonMapCosts(Predicate<DungeonMapCostCO> pre) {
		List<DungeonMapCostCO> results = new ArrayList<>();
		for(DungeonMapCostCO t : DungeonMapCosts.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, RechargeCostCO> RechargeCosts = new HashMap<Integer, RechargeCostCO>();
	public static List<RechargeCostCO> findRechargeCosts(Predicate<RechargeCostCO> pre) {
		List<RechargeCostCO> results = new ArrayList<>();
		for(RechargeCostCO t : RechargeCosts.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, ResRewardCO> ResRewards = new HashMap<Integer, ResRewardCO>();
	public static List<ResRewardCO> findResRewards(Predicate<ResRewardCO> pre) {
		List<ResRewardCO> results = new ArrayList<>();
		for(ResRewardCO t : ResRewards.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, SaleReviewTimeCO> SaleReviewTimes = new HashMap<Integer, SaleReviewTimeCO>();
	public static List<SaleReviewTimeCO> findSaleReviewTimes(Predicate<SaleReviewTimeCO> pre) {
		List<SaleReviewTimeCO> results = new ArrayList<>();
		for(SaleReviewTimeCO t : SaleReviewTimes.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, SoloTimesCostCO> SoloTimesCosts = new HashMap<Integer, SoloTimesCostCO>();
	public static List<SoloTimesCostCO> findSoloTimesCosts(Predicate<SoloTimesCostCO> pre) {
		List<SoloTimesCostCO> results = new ArrayList<>();
		for(SoloTimesCostCO t : SoloTimesCosts.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, EffectsConfigCO> EffectsConfigs = new HashMap<Integer, EffectsConfigCO>();
	public static List<EffectsConfigCO> findEffectsConfigs(Predicate<EffectsConfigCO> pre) {
		List<EffectsConfigCO> results = new ArrayList<>();
		for(EffectsConfigCO t : EffectsConfigs.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<String, DebugParametersCO> DebugParameterss = new HashMap<String, DebugParametersCO>();
	public static List<DebugParametersCO> findDebugParameterss(Predicate<DebugParametersCO> pre) {
		List<DebugParametersCO> results = new ArrayList<>();
		for(DebugParametersCO t : DebugParameterss.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, MoneyCO> Moneys = new HashMap<Integer, MoneyCO>();
	public static List<MoneyCO> findMoneys(Predicate<MoneyCO> pre) {
		List<MoneyCO> results = new ArrayList<>();
		for(MoneyCO t : Moneys.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<String, ParametersCO> Parameterss = new HashMap<String, ParametersCO>();
	public static List<ParametersCO> findParameterss(Predicate<ParametersCO> pre) {
		List<ParametersCO> results = new ArrayList<>();
		for(ParametersCO t : Parameterss.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<String, ServerConfigCO> ServerConfigs = new HashMap<String, ServerConfigCO>();
	public static List<ServerConfigCO> findServerConfigs(Predicate<ServerConfigCO> pre) {
		List<ServerConfigCO> results = new ArrayList<>();
		for(ServerConfigCO t : ServerConfigs.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, SystemConfigCO> SystemConfigs = new HashMap<Integer, SystemConfigCO>();
	public static List<SystemConfigCO> findSystemConfigs(Predicate<SystemConfigCO> pre) {
		List<SystemConfigCO> results = new ArrayList<>();
		for(SystemConfigCO t : SystemConfigs.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, CurrencyUICO> CurrencyUIs = new HashMap<Integer, CurrencyUICO>();
	public static List<CurrencyUICO> findCurrencyUIs(Predicate<CurrencyUICO> pre) {
		List<CurrencyUICO> results = new ArrayList<>();
		for(CurrencyUICO t : CurrencyUIs.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, ResEventCO> ResEvents = new HashMap<Integer, ResEventCO>();
	public static List<ResEventCO> findResEvents(Predicate<ResEventCO> pre) {
		List<ResEventCO> results = new ArrayList<>();
		for(ResEventCO t : ResEvents.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<String, EventCO> Events = new HashMap<String, EventCO>();
	public static List<EventCO> findEvents(Predicate<EventCO> pre) {
		List<EventCO> results = new ArrayList<>();
		for(EventCO t : Events.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, FateCO> Fates = new HashMap<Integer, FateCO>();
	public static List<FateCO> findFates(Predicate<FateCO> pre) {
		List<FateCO> results = new ArrayList<>();
		for(FateCO t : Fates.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<String, FunctionsCO> Functionss = new HashMap<String, FunctionsCO>();
	public static List<FunctionsCO> findFunctionss(Predicate<FunctionsCO> pre) {
		List<FunctionsCO> results = new ArrayList<>();
		for(FunctionsCO t : Functionss.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, FunGoToCO> FunGoTos = new HashMap<Integer, FunGoToCO>();
	public static List<FunGoToCO> findFunGoTos(Predicate<FunGoToCO> pre) {
		List<FunGoToCO> results = new ArrayList<>();
		for(FunGoToCO t : FunGoTos.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, GuideCO> Guides = new HashMap<Integer, GuideCO>();
	public static List<GuideCO> findGuides(Predicate<GuideCO> pre) {
		List<GuideCO> results = new ArrayList<>();
		for(GuideCO t : Guides.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, OpenLvCO> OpenLvs = new HashMap<Integer, OpenLvCO>();
	public static List<OpenLvCO> findOpenLvs(Predicate<OpenLvCO> pre) {
		List<OpenLvCO> results = new ArrayList<>();
		for(OpenLvCO t : OpenLvs.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, GiftCO> Gifts = new HashMap<Integer, GiftCO>();
	public static List<GiftCO> findGifts(Predicate<GiftCO> pre) {
		List<GiftCO> results = new ArrayList<>();
		for(GiftCO t : Gifts.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, BlessBuffExt> BlessBuffs = new HashMap<Integer, BlessBuffExt>();
	public static List<BlessBuffExt> findBlessBuffs(Predicate<BlessBuffExt> pre) {
		List<BlessBuffExt> results = new ArrayList<>();
		for(BlessBuffExt t : BlessBuffs.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, BlessItemCO> BlessItems = new HashMap<Integer, BlessItemCO>();
	public static List<BlessItemCO> findBlessItems(Predicate<BlessItemCO> pre) {
		List<BlessItemCO> results = new ArrayList<>();
		for(BlessItemCO t : BlessItems.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, BlessLevelExt> BlessLevels = new HashMap<Integer, BlessLevelExt>();
	public static List<BlessLevelExt> findBlessLevels(Predicate<BlessLevelExt> pre) {
		List<BlessLevelExt> results = new ArrayList<>();
		for(BlessLevelExt t : BlessLevels.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, BlesslibaoExt> Blesslibaos = new HashMap<Integer, BlesslibaoExt>();
	public static List<BlesslibaoExt> findBlesslibaos(Predicate<BlesslibaoExt> pre) {
		List<BlesslibaoExt> results = new ArrayList<>();
		for(BlesslibaoExt t : Blesslibaos.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, AuctionRecordCO> AuctionRecords = new HashMap<Integer, AuctionRecordCO>();
	public static List<AuctionRecordCO> findAuctionRecords(Predicate<AuctionRecordCO> pre) {
		List<AuctionRecordCO> results = new ArrayList<>();
		for(AuctionRecordCO t : AuctionRecords.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, InspireLevelExt> InspireLevels = new HashMap<Integer, InspireLevelExt>();
	public static List<InspireLevelExt> findInspireLevels(Predicate<InspireLevelExt> pre) {
		List<InspireLevelExt> results = new ArrayList<>();
		for(InspireLevelExt t : InspireLevels.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, RankRewardExt> RankRewards = new HashMap<Integer, RankRewardExt>();
	public static List<RankRewardExt> findRankRewards(Predicate<RankRewardExt> pre) {
		List<RankRewardExt> results = new ArrayList<>();
		for(RankRewardExt t : RankRewards.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, RewardListCO> RewardLists = new HashMap<Integer, RewardListCO>();
	public static List<RewardListCO> findRewardLists(Predicate<RewardListCO> pre) {
		List<RewardListCO> results = new ArrayList<>();
		for(RewardListCO t : RewardLists.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<String, GDungeonCO> GDungeons = new HashMap<String, GDungeonCO>();
	public static List<GDungeonCO> findGDungeons(Predicate<GDungeonCO> pre) {
		List<GDungeonCO> results = new ArrayList<>();
		for(GDungeonCO t : GDungeons.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, GDungeonMapCO> GDungeonMaps = new HashMap<Integer, GDungeonMapCO>();
	public static List<GDungeonMapCO> findGDungeonMaps(Predicate<GDungeonMapCO> pre) {
		List<GDungeonMapCO> results = new ArrayList<>();
		for(GDungeonMapCO t : GDungeonMaps.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, GDungeonRankCO> GDungeonRanks = new HashMap<Integer, GDungeonRankCO>();
	public static List<GDungeonRankCO> findGDungeonRanks(Predicate<GDungeonRankCO> pre) {
		List<GDungeonRankCO> results = new ArrayList<>();
		for(GDungeonRankCO t : GDungeonRanks.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, GuildFortExt> GuildForts = new HashMap<Integer, GuildFortExt>();
	public static List<GuildFortExt> findGuildForts(Predicate<GuildFortExt> pre) {
		List<GuildFortExt> results = new ArrayList<>();
		for(GuildFortExt t : GuildForts.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, GShopExt> GShops = new HashMap<Integer, GShopExt>();
	public static List<GShopExt> findGShops(Predicate<GShopExt> pre) {
		List<GShopExt> results = new ArrayList<>();
		for(GShopExt t : GShops.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, GBuffExt> GBuffs = new HashMap<Integer, GBuffExt>();
	public static List<GBuffExt> findGBuffs(Predicate<GBuffExt> pre) {
		List<GBuffExt> results = new ArrayList<>();
		for(GBuffExt t : GBuffs.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, GTechnologyExt> GTechnologys = new HashMap<Integer, GTechnologyExt>();
	public static List<GTechnologyExt> findGTechnologys(Predicate<GTechnologyExt> pre) {
		List<GTechnologyExt> results = new ArrayList<>();
		for(GTechnologyExt t : GTechnologys.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, GTechnologyItemCO> GTechnologyItems = new HashMap<Integer, GTechnologyItemCO>();
	public static List<GTechnologyItemCO> findGTechnologyItems(Predicate<GTechnologyItemCO> pre) {
		List<GTechnologyItemCO> results = new ArrayList<>();
		for(GTechnologyItemCO t : GTechnologyItems.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, GTechnologyLevelCO> GTechnologyLevels = new HashMap<Integer, GTechnologyLevelCO>();
	public static List<GTechnologyLevelCO> findGTechnologyLevels(Predicate<GTechnologyLevelCO> pre) {
		List<GTechnologyLevelCO> results = new ArrayList<>();
		for(GTechnologyLevelCO t : GTechnologyLevels.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, GuildBuildingCO> GuildBuildings = new HashMap<Integer, GuildBuildingCO>();
	public static List<GuildBuildingCO> findGuildBuildings(Predicate<GuildBuildingCO> pre) {
		List<GuildBuildingCO> results = new ArrayList<>();
		for(GuildBuildingCO t : GuildBuildings.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, GuildConditionCO> GuildConditions = new HashMap<Integer, GuildConditionCO>();
	public static List<GuildConditionCO> findGuildConditions(Predicate<GuildConditionCO> pre) {
		List<GuildConditionCO> results = new ArrayList<>();
		for(GuildConditionCO t : GuildConditions.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<String, GuildContributeCO> GuildContributes = new HashMap<String, GuildContributeCO>();
	public static List<GuildContributeCO> findGuildContributes(Predicate<GuildContributeCO> pre) {
		List<GuildContributeCO> results = new ArrayList<>();
		for(GuildContributeCO t : GuildContributes.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, GuildLevelCO> GuildLevels = new HashMap<Integer, GuildLevelCO>();
	public static List<GuildLevelCO> findGuildLevels(Predicate<GuildLevelCO> pre) {
		List<GuildLevelCO> results = new ArrayList<>();
		for(GuildLevelCO t : GuildLevels.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, GuildPositionCO> GuildPositions = new HashMap<Integer, GuildPositionCO>();
	public static List<GuildPositionCO> findGuildPositions(Predicate<GuildPositionCO> pre) {
		List<GuildPositionCO> results = new ArrayList<>();
		for(GuildPositionCO t : GuildPositions.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, GuildRecordCO> GuildRecords = new HashMap<Integer, GuildRecordCO>();
	public static List<GuildRecordCO> findGuildRecords(Predicate<GuildRecordCO> pre) {
		List<GuildRecordCO> results = new ArrayList<>();
		for(GuildRecordCO t : GuildRecords.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, GuildSettingExt> GuildSettings = new HashMap<Integer, GuildSettingExt>();
	public static List<GuildSettingExt> findGuildSettings(Predicate<GuildSettingExt> pre) {
		List<GuildSettingExt> results = new ArrayList<>();
		for(GuildSettingExt t : GuildSettings.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, WareHouseConditionCO> WareHouseConditions = new HashMap<Integer, WareHouseConditionCO>();
	public static List<WareHouseConditionCO> findWareHouseConditions(Predicate<WareHouseConditionCO> pre) {
		List<WareHouseConditionCO> results = new ArrayList<>();
		for(WareHouseConditionCO t : WareHouseConditions.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, WareHouseCondition2CO> WareHouseCondition2s = new HashMap<Integer, WareHouseCondition2CO>();
	public static List<WareHouseCondition2CO> findWareHouseCondition2s(Predicate<WareHouseCondition2CO> pre) {
		List<WareHouseCondition2CO> results = new ArrayList<>();
		for(WareHouseCondition2CO t : WareHouseCondition2s.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, WareHouseLevelCO> WareHouseLevels = new HashMap<Integer, WareHouseLevelCO>();
	public static List<WareHouseLevelCO> findWareHouseLevels(Predicate<WareHouseLevelCO> pre) {
		List<WareHouseLevelCO> results = new ArrayList<>();
		for(WareHouseLevelCO t : WareHouseLevels.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, WareHouseValueCO> WareHouseValues = new HashMap<Integer, WareHouseValueCO>();
	public static List<WareHouseValueCO> findWareHouseValues(Predicate<WareHouseValueCO> pre) {
		List<WareHouseValueCO> results = new ArrayList<>();
		for(WareHouseValueCO t : WareHouseValues.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, ExpReduceCO> ExpReduces = new HashMap<Integer, ExpReduceCO>();
	public static List<ExpReduceCO> findExpReduces(Predicate<ExpReduceCO> pre) {
		List<ExpReduceCO> results = new ArrayList<>();
		for(ExpReduceCO t : ExpReduces.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, GoldReduceCO> GoldReduces = new HashMap<Integer, GoldReduceCO>();
	public static List<GoldReduceCO> findGoldReduces(Predicate<GoldReduceCO> pre) {
		List<GoldReduceCO> results = new ArrayList<>();
		for(GoldReduceCO t : GoldReduces.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, ResLimitCO> ResLimits = new HashMap<Integer, ResLimitCO>();
	public static List<ResLimitCO> findResLimits(Predicate<ResLimitCO> pre) {
		List<ResLimitCO> results = new ArrayList<>();
		for(ResLimitCO t : ResLimits.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, SectionCO> Sections = new HashMap<Integer, SectionCO>();
	public static List<SectionCO> findSections(Predicate<SectionCO> pre) {
		List<SectionCO> results = new ArrayList<>();
		for(SectionCO t : Sections.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, CombineExt> Combines = new HashMap<Integer, CombineExt>();
	public static List<CombineExt> findCombines(Predicate<CombineExt> pre) {
		List<CombineExt> results = new ArrayList<>();
		for(CombineExt t : Combines.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, CombineTypeCO> CombineTypes = new HashMap<Integer, CombineTypeCO>();
	public static List<CombineTypeCO> findCombineTypes(Predicate<CombineTypeCO> pre) {
		List<CombineTypeCO> results = new ArrayList<>();
		for(CombineTypeCO t : CombineTypes.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, EnchantExt> Enchants = new HashMap<Integer, EnchantExt>();
	public static List<EnchantExt> findEnchants(Predicate<EnchantExt> pre) {
		List<EnchantExt> results = new ArrayList<>();
		for(EnchantExt t : Enchants.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, EnchantBonusExt> EnchantBonuss = new HashMap<Integer, EnchantBonusExt>();
	public static List<EnchantBonusExt> findEnchantBonuss(Predicate<EnchantBonusExt> pre) {
		List<EnchantBonusExt> results = new ArrayList<>();
		for(EnchantBonusExt t : EnchantBonuss.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, EquipMakeExt> EquipMakes = new HashMap<Integer, EquipMakeExt>();
	public static List<EquipMakeExt> findEquipMakes(Predicate<EquipMakeExt> pre) {
		List<EquipMakeExt> results = new ArrayList<>();
		for(EquipMakeExt t : EquipMakes.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, EquipMakeIndexCO> EquipMakeIndexs = new HashMap<Integer, EquipMakeIndexCO>();
	public static List<EquipMakeIndexCO> findEquipMakeIndexs(Predicate<EquipMakeIndexCO> pre) {
		List<EquipMakeIndexCO> results = new ArrayList<>();
		for(EquipMakeIndexCO t : EquipMakeIndexs.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, EquipSockExt> EquipSocks = new HashMap<Integer, EquipSockExt>();
	public static List<EquipSockExt> findEquipSocks(Predicate<EquipSockExt> pre) {
		List<EquipSockExt> results = new ArrayList<>();
		for(EquipSockExt t : EquipSocks.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, KaiGuangExt> KaiGuangs = new HashMap<Integer, KaiGuangExt>();
	public static List<KaiGuangExt> findKaiGuangs(Predicate<KaiGuangExt> pre) {
		List<KaiGuangExt> results = new ArrayList<>();
		for(KaiGuangExt t : KaiGuangs.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, MeltConfigCO> MeltConfigs = new HashMap<Integer, MeltConfigCO>();
	public static List<MeltConfigCO> findMeltConfigs(Predicate<MeltConfigCO> pre) {
		List<MeltConfigCO> results = new ArrayList<>();
		for(MeltConfigCO t : MeltConfigs.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, ReBornExt> ReBorns = new HashMap<Integer, ReBornExt>();
	public static List<ReBornExt> findReBorns(Predicate<ReBornExt> pre) {
		List<ReBornExt> results = new ArrayList<>();
		for(ReBornExt t : ReBorns.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, ReBuildExt> ReBuilds = new HashMap<Integer, ReBuildExt>();
	public static List<ReBuildExt> findReBuilds(Predicate<ReBuildExt> pre) {
		List<ReBuildExt> results = new ArrayList<>();
		for(ReBuildExt t : ReBuilds.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, ReBuildCfCO> ReBuildCfs = new HashMap<Integer, ReBuildCfCO>();
	public static List<ReBuildCfCO> findReBuildCfs(Predicate<ReBuildCfCO> pre) {
		List<ReBuildCfCO> results = new ArrayList<>();
		for(ReBuildCfCO t : ReBuildCfs.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, ReBuildStarCO> ReBuildStars = new HashMap<Integer, ReBuildStarCO>();
	public static List<ReBuildStarCO> findReBuildStars(Predicate<ReBuildStarCO> pre) {
		List<ReBuildStarCO> results = new ArrayList<>();
		for(ReBuildStarCO t : ReBuildStars.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, RefineExt> Refines = new HashMap<Integer, RefineExt>();
	public static List<RefineExt> findRefines(Predicate<RefineExt> pre) {
		List<RefineExt> results = new ArrayList<>();
		for(RefineExt t : Refines.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, SeniorReBuildExt> SeniorReBuilds = new HashMap<Integer, SeniorReBuildExt>();
	public static List<SeniorReBuildExt> findSeniorReBuilds(Predicate<SeniorReBuildExt> pre) {
		List<SeniorReBuildExt> results = new ArrayList<>();
		for(SeniorReBuildExt t : SeniorReBuilds.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, SmritiExt> Smritis = new HashMap<Integer, SmritiExt>();
	public static List<SmritiExt> findSmritis(Predicate<SmritiExt> pre) {
		List<SmritiExt> results = new ArrayList<>();
		for(SmritiExt t : Smritis.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, SuitConfigExt> SuitConfigs = new HashMap<Integer, SuitConfigExt>();
	public static List<SuitConfigExt> findSuitConfigs(Predicate<SuitConfigExt> pre) {
		List<SuitConfigExt> results = new ArrayList<>();
		for(SuitConfigExt t : SuitConfigs.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, SuitListExt> SuitLists = new HashMap<Integer, SuitListExt>();
	public static List<SuitListExt> findSuitLists(Predicate<SuitListExt> pre) {
		List<SuitListExt> results = new ArrayList<>();
		for(SuitListExt t : SuitLists.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, EquipdituConfigCO> EquipdituConfigs = new HashMap<Integer, EquipdituConfigCO>();
	public static List<EquipdituConfigCO> findEquipdituConfigs(Predicate<EquipdituConfigCO> pre) {
		List<EquipdituConfigCO> results = new ArrayList<>();
		for(EquipdituConfigCO t : EquipdituConfigs.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<String, BlueEquipExt> BlueEquips = new HashMap<String, BlueEquipExt>();
	public static List<BlueEquipExt> findBlueEquips(Predicate<BlueEquipExt> pre) {
		List<BlueEquipExt> results = new ArrayList<>();
		for(BlueEquipExt t : BlueEquips.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<String, LegendEquipExt> LegendEquips = new HashMap<String, LegendEquipExt>();
	public static List<LegendEquipExt> findLegendEquips(Predicate<LegendEquipExt> pre) {
		List<LegendEquipExt> results = new ArrayList<>();
		for(LegendEquipExt t : LegendEquips.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<String, NormalEquipExt> NormalEquips = new HashMap<String, NormalEquipExt>();
	public static List<NormalEquipExt> findNormalEquips(Predicate<NormalEquipExt> pre) {
		List<NormalEquipExt> results = new ArrayList<>();
		for(NormalEquipExt t : NormalEquips.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<String, PurpleEquipExt> PurpleEquips = new HashMap<String, PurpleEquipExt>();
	public static List<PurpleEquipExt> findPurpleEquips(Predicate<PurpleEquipExt> pre) {
		List<PurpleEquipExt> results = new ArrayList<>();
		for(PurpleEquipExt t : PurpleEquips.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<String, RideEquipExt> RideEquips = new HashMap<String, RideEquipExt>();
	public static List<RideEquipExt> findRideEquips(Predicate<RideEquipExt> pre) {
		List<RideEquipExt> results = new ArrayList<>();
		for(RideEquipExt t : RideEquips.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<String, SuitEquipExt> SuitEquips = new HashMap<String, SuitEquipExt>();
	public static List<SuitEquipExt> findSuitEquips(Predicate<SuitEquipExt> pre) {
		List<SuitEquipExt> results = new ArrayList<>();
		for(SuitEquipExt t : SuitEquips.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<String, UniqueEquipExt> UniqueEquips = new HashMap<String, UniqueEquipExt>();
	public static List<UniqueEquipExt> findUniqueEquips(Predicate<UniqueEquipExt> pre) {
		List<UniqueEquipExt> results = new ArrayList<>();
		for(UniqueEquipExt t : UniqueEquips.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<String, FashionExt> Fashions = new HashMap<String, FashionExt>();
	public static List<FashionExt> findFashions(Predicate<FashionExt> pre) {
		List<FashionExt> results = new ArrayList<>();
		for(FashionExt t : Fashions.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, FashSuitConfigExt> FashSuitConfigs = new HashMap<Integer, FashSuitConfigExt>();
	public static List<FashSuitConfigExt> findFashSuitConfigs(Predicate<FashSuitConfigExt> pre) {
		List<FashSuitConfigExt> results = new ArrayList<>();
		for(FashSuitConfigExt t : FashSuitConfigs.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<String, ActiveCO> Actives = new HashMap<String, ActiveCO>();
	public static List<ActiveCO> findActives(Predicate<ActiveCO> pre) {
		List<ActiveCO> results = new ArrayList<>();
		for(ActiveCO t : Actives.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<String, ChestCO> Chests = new HashMap<String, ChestCO>();
	public static List<ChestCO> findChests(Predicate<ChestCO> pre) {
		List<ChestCO> results = new ArrayList<>();
		for(ChestCO t : Chests.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<String, FashionItemCO> FashionItems = new HashMap<String, FashionItemCO>();
	public static List<FashionItemCO> findFashionItems(Predicate<FashionItemCO> pre) {
		List<FashionItemCO> results = new ArrayList<>();
		for(FashionItemCO t : FashionItems.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<String, JewelCO> Jewels = new HashMap<String, JewelCO>();
	public static List<JewelCO> findJewels(Predicate<JewelCO> pre) {
		List<JewelCO> results = new ArrayList<>();
		for(JewelCO t : Jewels.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<String, MateCO> Mates = new HashMap<String, MateCO>();
	public static List<MateCO> findMates(Predicate<MateCO> pre) {
		List<MateCO> results = new ArrayList<>();
		for(MateCO t : Mates.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<String, MiscCO> Miscs = new HashMap<String, MiscCO>();
	public static List<MiscCO> findMiscs(Predicate<MiscCO> pre) {
		List<MiscCO> results = new ArrayList<>();
		for(MiscCO t : Miscs.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<String, PetItemCO> PetItems = new HashMap<String, PetItemCO>();
	public static List<PetItemCO> findPetItems(Predicate<PetItemCO> pre) {
		List<PetItemCO> results = new ArrayList<>();
		for(PetItemCO t : PetItems.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<String, PotionCO> Potions = new HashMap<String, PotionCO>();
	public static List<PotionCO> findPotions(Predicate<PotionCO> pre) {
		List<PotionCO> results = new ArrayList<>();
		for(PotionCO t : Potions.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<String, QuestCO> Quests = new HashMap<String, QuestCO>();
	public static List<QuestCO> findQuests(Predicate<QuestCO> pre) {
		List<QuestCO> results = new ArrayList<>();
		for(QuestCO t : Quests.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<String, RankCO> Ranks = new HashMap<String, RankCO>();
	public static List<RankCO> findRanks(Predicate<RankCO> pre) {
		List<RankCO> results = new ArrayList<>();
		for(RankCO t : Ranks.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<String, RideItemCO> RideItems = new HashMap<String, RideItemCO>();
	public static List<RideItemCO> findRideItems(Predicate<RideItemCO> pre) {
		List<RideItemCO> results = new ArrayList<>();
		for(RideItemCO t : RideItems.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<String, VirtualCO> Virtuals = new HashMap<String, VirtualCO>();
	public static List<VirtualCO> findVirtuals(Predicate<VirtualCO> pre) {
		List<VirtualCO> results = new ArrayList<>();
		for(VirtualCO t : Virtuals.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<String, ItemIdConfigExt> ItemIdConfigs = new HashMap<String, ItemIdConfigExt>();
	public static List<ItemIdConfigExt> findItemIdConfigs(Predicate<ItemIdConfigExt> pre) {
		List<ItemIdConfigExt> results = new ArrayList<>();
		for(ItemIdConfigExt t : ItemIdConfigs.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, ItemTypeConfigExt> ItemTypeConfigs = new HashMap<Integer, ItemTypeConfigExt>();
	public static List<ItemTypeConfigExt> findItemTypeConfigs(Predicate<ItemTypeConfigExt> pre) {
		List<ItemTypeConfigExt> results = new ArrayList<>();
		for(ItemTypeConfigExt t : ItemTypeConfigs.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, AffixExt> Affixs = new HashMap<Integer, AffixExt>();
	public static List<AffixExt> findAffixs(Predicate<AffixExt> pre) {
		List<AffixExt> results = new ArrayList<>();
		for(AffixExt t : Affixs.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, MagicRingCO> MagicRings = new HashMap<Integer, MagicRingCO>();
	public static List<MagicRingCO> findMagicRings(Predicate<MagicRingCO> pre) {
		List<MagicRingCO> results = new ArrayList<>();
		for(MagicRingCO t : MagicRings.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, MasteryCO> Masterys = new HashMap<Integer, MasteryCO>();
	public static List<MasteryCO> findMasterys(Predicate<MasteryCO> pre) {
		List<MasteryCO> results = new ArrayList<>();
		for(MasteryCO t : Masterys.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, MasteryPropExt> MasteryProps = new HashMap<Integer, MasteryPropExt>();
	public static List<MasteryPropExt> findMasteryProps(Predicate<MasteryPropExt> pre) {
		List<MasteryPropExt> results = new ArrayList<>();
		for(MasteryPropExt t : MasteryProps.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, PickItemCO> PickItems = new HashMap<Integer, PickItemCO>();
	public static List<PickItemCO> findPickItems(Predicate<PickItemCO> pre) {
		List<PickItemCO> results = new ArrayList<>();
		for(PickItemCO t : PickItems.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, RandomBoxExt> RandomBoxs = new HashMap<Integer, RandomBoxExt>();
	public static List<RandomBoxExt> findRandomBoxs(Predicate<RandomBoxExt> pre) {
		List<RandomBoxExt> results = new ArrayList<>();
		for(RandomBoxExt t : RandomBoxs.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<String, PKLostBagItemTypeCO> PKLostBagItemTypes = new HashMap<String, PKLostBagItemTypeCO>();
	public static List<PKLostBagItemTypeCO> findPKLostBagItemTypes(Predicate<PKLostBagItemTypeCO> pre) {
		List<PKLostBagItemTypeCO> results = new ArrayList<>();
		for(PKLostBagItemTypeCO t : PKLostBagItemTypes.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<String, TreasureClassExt> TreasureClasss = new HashMap<String, TreasureClassExt>();
	public static List<TreasureClassExt> findTreasureClasss(Predicate<TreasureClassExt> pre) {
		List<TreasureClassExt> results = new ArrayList<>();
		for(TreasureClassExt t : TreasureClasss.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<String, AfterFilterCO> AfterFilters = new HashMap<String, AfterFilterCO>();
	public static List<AfterFilterCO> findAfterFilters(Predicate<AfterFilterCO> pre) {
		List<AfterFilterCO> results = new ArrayList<>();
		for(AfterFilterCO t : AfterFilters.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<String, BeforeFilterCO> BeforeFilters = new HashMap<String, BeforeFilterCO>();
	public static List<BeforeFilterCO> findBeforeFilters(Predicate<BeforeFilterCO> pre) {
		List<BeforeFilterCO> results = new ArrayList<>();
		for(BeforeFilterCO t : BeforeFilters.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<String, StoreFilterCO> StoreFilters = new HashMap<String, StoreFilterCO>();
	public static List<StoreFilterCO> findStoreFilters(Predicate<StoreFilterCO> pre) {
		List<StoreFilterCO> results = new ArrayList<>();
		for(StoreFilterCO t : StoreFilters.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, StoreMenuCO> StoreMenus = new HashMap<Integer, StoreMenuCO>();
	public static List<StoreMenuCO> findStoreMenus(Predicate<StoreMenuCO> pre) {
		List<StoreMenuCO> results = new ArrayList<>();
		for(StoreMenuCO t : StoreMenus.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, AthleticShopCO> AthleticShops = new HashMap<Integer, AthleticShopCO>();
	public static List<AthleticShopCO> findAthleticShops(Predicate<AthleticShopCO> pre) {
		List<AthleticShopCO> results = new ArrayList<>();
		for(AthleticShopCO t : AthleticShops.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, FateShopCO> FateShops = new HashMap<Integer, FateShopCO>();
	public static List<FateShopCO> findFateShops(Predicate<FateShopCO> pre) {
		List<FateShopCO> results = new ArrayList<>();
		for(FateShopCO t : FateShops.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, GuildShopCO> GuildShops = new HashMap<Integer, GuildShopCO>();
	public static List<GuildShopCO> findGuildShops(Predicate<GuildShopCO> pre) {
		List<GuildShopCO> results = new ArrayList<>();
		for(GuildShopCO t : GuildShops.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, MallShopCO> MallShops = new HashMap<Integer, MallShopCO>();
	public static List<MallShopCO> findMallShops(Predicate<MallShopCO> pre) {
		List<MallShopCO> results = new ArrayList<>();
		for(MallShopCO t : MallShops.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, ShopLabCO> ShopLabs = new HashMap<Integer, ShopLabCO>();
	public static List<ShopLabCO> findShopLabs(Predicate<ShopLabCO> pre) {
		List<ShopLabCO> results = new ArrayList<>();
		for(ShopLabCO t : ShopLabs.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, SundryShopCO> SundryShops = new HashMap<Integer, SundryShopCO>();
	public static List<SundryShopCO> findSundryShops(Predicate<SundryShopCO> pre) {
		List<SundryShopCO> results = new ArrayList<>();
		for(SundryShopCO t : SundryShops.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, MenuUISaleCO> MenuUISales = new HashMap<Integer, MenuUISaleCO>();
	public static List<MenuUISaleCO> findMenuUISales(Predicate<MenuUISaleCO> pre) {
		List<MenuUISaleCO> results = new ArrayList<>();
		for(MenuUISaleCO t : MenuUISales.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, NpcSaleCO> NpcSales = new HashMap<Integer, NpcSaleCO>();
	public static List<NpcSaleCO> findNpcSales(Predicate<NpcSaleCO> pre) {
		List<NpcSaleCO> results = new ArrayList<>();
		for(NpcSaleCO t : NpcSales.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, ExchangeMallExt> ExchangeMalls = new HashMap<Integer, ExchangeMallExt>();
	public static List<ExchangeMallExt> findExchangeMalls(Predicate<ExchangeMallExt> pre) {
		List<ExchangeMallExt> results = new ArrayList<>();
		for(ExchangeMallExt t : ExchangeMalls.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, ShopMallConfigCO> ShopMallConfigs = new HashMap<Integer, ShopMallConfigCO>();
	public static List<ShopMallConfigCO> findShopMallConfigs(Predicate<ShopMallConfigCO> pre) {
		List<ShopMallConfigCO> results = new ArrayList<>();
		for(ShopMallConfigCO t : ShopMallConfigs.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, ShopMallIconCO> ShopMallIcons = new HashMap<Integer, ShopMallIconCO>();
	public static List<ShopMallIconCO> findShopMallIcons(Predicate<ShopMallIconCO> pre) {
		List<ShopMallIconCO> results = new ArrayList<>();
		for(ShopMallIconCO t : ShopMallIcons.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, ShopMallItemsExt> ShopMallItemss = new HashMap<Integer, ShopMallItemsExt>();
	public static List<ShopMallItemsExt> findShopMallItemss(Predicate<ShopMallItemsExt> pre) {
		List<ShopMallItemsExt> results = new ArrayList<>();
		for(ShopMallItemsExt t : ShopMallItemss.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, JJCRewardExt> JJCRewards = new HashMap<Integer, JJCRewardExt>();
	public static List<JJCRewardExt> findJJCRewards(Predicate<JJCRewardExt> pre) {
		List<JJCRewardExt> results = new ArrayList<>();
		for(JJCRewardExt t : JJCRewards.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, MailOperateCO> MailOperates = new HashMap<Integer, MailOperateCO>();
	public static List<MailOperateCO> findMailOperates(Predicate<MailOperateCO> pre) {
		List<MailOperateCO> results = new ArrayList<>();
		for(MailOperateCO t : MailOperates.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<String, MailSystemCO> MailSystems = new HashMap<String, MailSystemCO>();
	public static List<MailSystemCO> findMailSystems(Predicate<MailSystemCO> pre) {
		List<MailSystemCO> results = new ArrayList<>();
		for(MailSystemCO t : MailSystems.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, DungeonMonsterCO> DungeonMonsters = new HashMap<Integer, DungeonMonsterCO>();
	public static List<DungeonMonsterCO> findDungeonMonsters(Predicate<DungeonMonsterCO> pre) {
		List<DungeonMonsterCO> results = new ArrayList<>();
		for(DungeonMonsterCO t : DungeonMonsters.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, DungeonMapCO> DungeonMaps = new HashMap<Integer, DungeonMapCO>();
	public static List<DungeonMapCO> findDungeonMaps(Predicate<DungeonMapCO> pre) {
		List<DungeonMapCO> results = new ArrayList<>();
		for(DungeonMapCO t : DungeonMaps.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, GameMapCO> GameMaps = new HashMap<Integer, GameMapCO>();
	public static List<GameMapCO> findGameMaps(Predicate<GameMapCO> pre) {
		List<GameMapCO> results = new ArrayList<>();
		for(GameMapCO t : GameMaps.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, NormalMapCO> NormalMaps = new HashMap<Integer, NormalMapCO>();
	public static List<NormalMapCO> findNormalMaps(Predicate<NormalMapCO> pre) {
		List<NormalMapCO> results = new ArrayList<>();
		for(NormalMapCO t : NormalMaps.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, ResurrectionCO> Resurrections = new HashMap<Integer, ResurrectionCO>();
	public static List<ResurrectionCO> findResurrections(Predicate<ResurrectionCO> pre) {
		List<ResurrectionCO> results = new ArrayList<>();
		for(ResurrectionCO t : Resurrections.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, WorldZoneCO> WorldZones = new HashMap<Integer, WorldZoneCO>();
	public static List<WorldZoneCO> findWorldZones(Predicate<WorldZoneCO> pre) {
		List<WorldZoneCO> results = new ArrayList<>();
		for(WorldZoneCO t : WorldZones.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, MedalListExt> MedalLists = new HashMap<Integer, MedalListExt>();
	public static List<MedalListExt> findMedalLists(Predicate<MedalListExt> pre) {
		List<MedalListExt> results = new ArrayList<>();
		for(MedalListExt t : MedalLists.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, DemonTowerCO> DemonTowers = new HashMap<Integer, DemonTowerCO>();
	public static List<DemonTowerCO> findDemonTowers(Predicate<DemonTowerCO> pre) {
		List<DemonTowerCO> results = new ArrayList<>();
		for(DemonTowerCO t : DemonTowers.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, Dungeon_EliteCO> Dungeon_Elites = new HashMap<Integer, Dungeon_EliteCO>();
	public static List<Dungeon_EliteCO> findDungeon_Elites(Predicate<Dungeon_EliteCO> pre) {
		List<Dungeon_EliteCO> results = new ArrayList<>();
		for(Dungeon_EliteCO t : Dungeon_Elites.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, Dungeon_HeroCO> Dungeon_Heros = new HashMap<Integer, Dungeon_HeroCO>();
	public static List<Dungeon_HeroCO> findDungeon_Heros(Predicate<Dungeon_HeroCO> pre) {
		List<Dungeon_HeroCO> results = new ArrayList<>();
		for(Dungeon_HeroCO t : Dungeon_Heros.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, Dungeon_NormalCO> Dungeon_Normals = new HashMap<Integer, Dungeon_NormalCO>();
	public static List<Dungeon_NormalCO> findDungeon_Normals(Predicate<Dungeon_NormalCO> pre) {
		List<Dungeon_NormalCO> results = new ArrayList<>();
		for(Dungeon_NormalCO t : Dungeon_Normals.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, DungeonCO> Dungeons = new HashMap<Integer, DungeonCO>();
	public static List<DungeonCO> findDungeons(Predicate<DungeonCO> pre) {
		List<DungeonCO> results = new ArrayList<>();
		for(DungeonCO t : Dungeons.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, Normal_WorldCO> Normal_Worlds = new HashMap<Integer, Normal_WorldCO>();
	public static List<Normal_WorldCO> findNormal_Worlds(Predicate<Normal_WorldCO> pre) {
		List<Normal_WorldCO> results = new ArrayList<>();
		for(Normal_WorldCO t : Normal_Worlds.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, DungeonMonRatioCO> DungeonMonRatios = new HashMap<Integer, DungeonMonRatioCO>();
	public static List<DungeonMonRatioCO> findDungeonMonRatios(Predicate<DungeonMonRatioCO> pre) {
		List<DungeonMonRatioCO> results = new ArrayList<>();
		for(DungeonMonRatioCO t : DungeonMonRatios.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, GuildBossRatioCO> GuildBossRatios = new HashMap<Integer, GuildBossRatioCO>();
	public static List<GuildBossRatioCO> findGuildBossRatios(Predicate<GuildBossRatioCO> pre) {
		List<GuildBossRatioCO> results = new ArrayList<>();
		for(GuildBossRatioCO t : GuildBossRatios.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<String, MonDataConfigCO> MonDataConfigs = new HashMap<String, MonDataConfigCO>();
	public static List<MonDataConfigCO> findMonDataConfigs(Predicate<MonDataConfigCO> pre) {
		List<MonDataConfigCO> results = new ArrayList<>();
		for(MonDataConfigCO t : MonDataConfigs.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, MultipleMonCO> MultipleMons = new HashMap<Integer, MultipleMonCO>();
	public static List<MultipleMonCO> findMultipleMons(Predicate<MultipleMonCO> pre) {
		List<MultipleMonCO> results = new ArrayList<>();
		for(MultipleMonCO t : MultipleMons.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, OrigDataCO> OrigDatas = new HashMap<Integer, OrigDataCO>();
	public static List<OrigDataCO> findOrigDatas(Predicate<OrigDataCO> pre) {
		List<OrigDataCO> results = new ArrayList<>();
		for(OrigDataCO t : OrigDatas.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, SingleMonCO> SingleMons = new HashMap<Integer, SingleMonCO>();
	public static List<SingleMonCO> findSingleMons(Predicate<SingleMonCO> pre) {
		List<SingleMonCO> results = new ArrayList<>();
		for(SingleMonCO t : SingleMons.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, TowerMonRatioCO> TowerMonRatios = new HashMap<Integer, TowerMonRatioCO>();
	public static List<TowerMonRatioCO> findTowerMonRatios(Predicate<TowerMonRatioCO> pre) {
		List<TowerMonRatioCO> results = new ArrayList<>();
		for(TowerMonRatioCO t : TowerMonRatios.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, UserDefineConfigCO> UserDefineConfigs = new HashMap<Integer, UserDefineConfigCO>();
	public static List<UserDefineConfigCO> findUserDefineConfigs(Predicate<UserDefineConfigCO> pre) {
		List<UserDefineConfigCO> results = new ArrayList<>();
		for(UserDefineConfigCO t : UserDefineConfigs.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, MonsterRefreshExt> MonsterRefreshs = new HashMap<Integer, MonsterRefreshExt>();
	public static List<MonsterRefreshExt> findMonsterRefreshs(Predicate<MonsterRefreshExt> pre) {
		List<MonsterRefreshExt> results = new ArrayList<>();
		for(MonsterRefreshExt t : MonsterRefreshs.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, TypeNameCO> TypeNames = new HashMap<Integer, TypeNameCO>();
	public static List<TypeNameCO> findTypeNames(Predicate<TypeNameCO> pre) {
		List<TypeNameCO> results = new ArrayList<>();
		for(TypeNameCO t : TypeNames.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, NpcListExt> NpcLists = new HashMap<Integer, NpcListExt>();
	public static List<NpcListExt> findNpcLists(Predicate<NpcListExt> pre) {
		List<NpcListExt> results = new ArrayList<>();
		for(NpcListExt t : NpcLists.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, SkillSlotCO> SkillSlots = new HashMap<Integer, SkillSlotCO>();
	public static List<SkillSlotCO> findSkillSlots(Predicate<SkillSlotCO> pre) {
		List<SkillSlotCO> results = new ArrayList<>();
		for(SkillSlotCO t : SkillSlots.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, CardCO> Cards = new HashMap<Integer, CardCO>();
	public static List<CardCO> findCards(Predicate<CardCO> pre) {
		List<CardCO> results = new ArrayList<>();
		for(CardCO t : Cards.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, PayCO> Pays = new HashMap<Integer, PayCO>();
	public static List<PayCO> findPays(Predicate<PayCO> pre) {
		List<PayCO> results = new ArrayList<>();
		for(PayCO t : Pays.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, PayRewardExt> PayRewards = new HashMap<Integer, PayRewardExt>();
	public static List<PayRewardExt> findPayRewards(Predicate<PayRewardExt> pre) {
		List<PayRewardExt> results = new ArrayList<>();
		for(PayRewardExt t : PayRewards.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, DailyPayExt> DailyPays = new HashMap<Integer, DailyPayExt>();
	public static List<DailyPayExt> findDailyPays(Predicate<DailyPayExt> pre) {
		List<DailyPayExt> results = new ArrayList<>();
		for(DailyPayExt t : DailyPays.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, FirstPayExt> FirstPays = new HashMap<Integer, FirstPayExt>();
	public static List<FirstPayExt> findFirstPays(Predicate<FirstPayExt> pre) {
		List<FirstPayExt> results = new ArrayList<>();
		for(FirstPayExt t : FirstPays.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, TotalPayExt> TotalPays = new HashMap<Integer, TotalPayExt>();
	public static List<TotalPayExt> findTotalPays(Predicate<TotalPayExt> pre) {
		List<TotalPayExt> results = new ArrayList<>();
		for(TotalPayExt t : TotalPays.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, BaseDataExt> BaseDatas = new HashMap<Integer, BaseDataExt>();
	public static List<BaseDataExt> findBaseDatas(Predicate<BaseDataExt> pre) {
		List<BaseDataExt> results = new ArrayList<>();
		for(BaseDataExt t : BaseDatas.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, MasterPropExt> MasterProps = new HashMap<Integer, MasterPropExt>();
	public static List<MasterPropExt> findMasterProps(Predicate<MasterPropExt> pre) {
		List<MasterPropExt> results = new ArrayList<>();
		for(MasterPropExt t : MasterProps.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, MasterUpgradePropExt> MasterUpgradeProps = new HashMap<Integer, MasterUpgradePropExt>();
	public static List<MasterUpgradePropExt> findMasterUpgradeProps(Predicate<MasterUpgradePropExt> pre) {
		List<MasterUpgradePropExt> results = new ArrayList<>();
		for(MasterUpgradePropExt t : MasterUpgradeProps.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, PetAssociateExt> PetAssociates = new HashMap<Integer, PetAssociateExt>();
	public static List<PetAssociateExt> findPetAssociates(Predicate<PetAssociateExt> pre) {
		List<PetAssociateExt> results = new ArrayList<>();
		for(PetAssociateExt t : PetAssociates.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<String, PetConfigExt> PetConfigs = new HashMap<String, PetConfigExt>();
	public static List<PetConfigExt> findPetConfigs(Predicate<PetConfigExt> pre) {
		List<PetConfigExt> results = new ArrayList<>();
		for(PetConfigExt t : PetConfigs.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, PetExpLevelCO> PetExpLevels = new HashMap<Integer, PetExpLevelCO>();
	public static List<PetExpLevelCO> findPetExpLevels(Predicate<PetExpLevelCO> pre) {
		List<PetExpLevelCO> results = new ArrayList<>();
		for(PetExpLevelCO t : PetExpLevels.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, PetUpgradeExt> PetUpgrades = new HashMap<Integer, PetUpgradeExt>();
	public static List<PetUpgradeExt> findPetUpgrades(Predicate<PetUpgradeExt> pre) {
		List<PetUpgradeExt> results = new ArrayList<>();
		for(PetUpgradeExt t : PetUpgrades.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, BranchLineCO> BranchLines = new HashMap<Integer, BranchLineCO>();
	public static List<BranchLineCO> findBranchLines(Predicate<BranchLineCO> pre) {
		List<BranchLineCO> results = new ArrayList<>();
		for(BranchLineCO t : BranchLines.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, CircleCO> Circles = new HashMap<Integer, CircleCO>();
	public static List<CircleCO> findCircles(Predicate<CircleCO> pre) {
		List<CircleCO> results = new ArrayList<>();
		for(CircleCO t : Circles.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, CircleChoiceCO> CircleChoices = new HashMap<Integer, CircleChoiceCO>();
	public static List<CircleChoiceCO> findCircleChoices(Predicate<CircleChoiceCO> pre) {
		List<CircleChoiceCO> results = new ArrayList<>();
		for(CircleChoiceCO t : CircleChoices.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, CircleRewardCO> CircleRewards = new HashMap<Integer, CircleRewardCO>();
	public static List<CircleRewardCO> findCircleRewards(Predicate<CircleRewardCO> pre) {
		List<CircleRewardCO> results = new ArrayList<>();
		for(CircleRewardCO t : CircleRewards.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, CircleSceneCO> CircleScenes = new HashMap<Integer, CircleSceneCO>();
	public static List<CircleSceneCO> findCircleScenes(Predicate<CircleSceneCO> pre) {
		List<CircleSceneCO> results = new ArrayList<>();
		for(CircleSceneCO t : CircleScenes.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, DailyCO> Dailys = new HashMap<Integer, DailyCO>();
	public static List<DailyCO> findDailys(Predicate<DailyCO> pre) {
		List<DailyCO> results = new ArrayList<>();
		for(DailyCO t : Dailys.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, DailyReduceCO> DailyReduces = new HashMap<Integer, DailyReduceCO>();
	public static List<DailyReduceCO> findDailyReduces(Predicate<DailyReduceCO> pre) {
		List<DailyReduceCO> results = new ArrayList<>();
		for(DailyReduceCO t : DailyReduces.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, DailyRewardCO> DailyRewards = new HashMap<Integer, DailyRewardCO>();
	public static List<DailyRewardCO> findDailyRewards(Predicate<DailyRewardCO> pre) {
		List<DailyRewardCO> results = new ArrayList<>();
		for(DailyRewardCO t : DailyRewards.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, MainLineCO> MainLines = new HashMap<Integer, MainLineCO>();
	public static List<MainLineCO> findMainLines(Predicate<MainLineCO> pre) {
		List<MainLineCO> results = new ArrayList<>();
		for(MainLineCO t : MainLines.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, TaskChestCO> TaskChests = new HashMap<Integer, TaskChestCO>();
	public static List<TaskChestCO> findTaskChests(Predicate<TaskChestCO> pre) {
		List<TaskChestCO> results = new ArrayList<>();
		for(TaskChestCO t : TaskChests.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, TreasureCO> Treasures = new HashMap<Integer, TreasureCO>();
	public static List<TreasureCO> findTreasures(Predicate<TreasureCO> pre) {
		List<TreasureCO> results = new ArrayList<>();
		for(TreasureCO t : Treasures.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, WayTreasureExt> WayTreasures = new HashMap<Integer, WayTreasureExt>();
	public static List<WayTreasureExt> findWayTreasures(Predicate<WayTreasureExt> pre) {
		List<WayTreasureExt> results = new ArrayList<>();
		for(WayTreasureExt t : WayTreasures.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, ConsumeCO> Consumes = new HashMap<Integer, ConsumeCO>();
	public static List<ConsumeCO> findConsumes(Predicate<ConsumeCO> pre) {
		List<ConsumeCO> results = new ArrayList<>();
		for(ConsumeCO t : Consumes.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, GetRedMoneyCO> GetRedMoneys = new HashMap<Integer, GetRedMoneyCO>();
	public static List<GetRedMoneyCO> findGetRedMoneys(Predicate<GetRedMoneyCO> pre) {
		List<GetRedMoneyCO> results = new ArrayList<>();
		for(GetRedMoneyCO t : GetRedMoneys.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, RedPackageCO> RedPackages = new HashMap<Integer, RedPackageCO>();
	public static List<RedPackageCO> findRedPackages(Predicate<RedPackageCO> pre) {
		List<RedPackageCO> results = new ArrayList<>();
		for(RedPackageCO t : RedPackages.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, RideListExt> RideLists = new HashMap<Integer, RideListExt>();
	public static List<RideListExt> findRideLists(Predicate<RideListExt> pre) {
		List<RideListExt> results = new ArrayList<>();
		for(RideListExt t : RideLists.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, SkinListExt> SkinLists = new HashMap<Integer, SkinListExt>();
	public static List<SkinListExt> findSkinLists(Predicate<SkinListExt> pre) {
		List<SkinListExt> results = new ArrayList<>();
		for(SkinListExt t : SkinLists.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, AttributeExt> Attributes = new HashMap<Integer, AttributeExt>();
	public static List<AttributeExt> findAttributes(Predicate<AttributeExt> pre) {
		List<AttributeExt> results = new ArrayList<>();
		for(AttributeExt t : Attributes.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, BuffSkillCO> BuffSkills = new HashMap<Integer, BuffSkillCO>();
	public static List<BuffSkillCO> findBuffSkills(Predicate<BuffSkillCO> pre) {
		List<BuffSkillCO> results = new ArrayList<>();
		for(BuffSkillCO t : BuffSkills.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, CharacterExt> Characters = new HashMap<Integer, CharacterExt>();
	public static List<CharacterExt> findCharacters(Predicate<CharacterExt> pre) {
		List<CharacterExt> results = new ArrayList<>();
		for(CharacterExt t : Characters.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, CharacterLevelCO> CharacterLevels = new HashMap<Integer, CharacterLevelCO>();
	public static List<CharacterLevelCO> findCharacterLevels(Predicate<CharacterLevelCO> pre) {
		List<CharacterLevelCO> results = new ArrayList<>();
		for(CharacterLevelCO t : CharacterLevels.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, RankListExt> RankLists = new HashMap<Integer, RankListExt>();
	public static List<RankListExt> findRankLists(Predicate<RankListExt> pre) {
		List<RankListExt> results = new ArrayList<>();
		for(RankListExt t : RankLists.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, RankSortCO> RankSorts = new HashMap<Integer, RankSortCO>();
	public static List<RankSortCO> findRankSorts(Predicate<RankSortCO> pre) {
		List<RankSortCO> results = new ArrayList<>();
		for(RankSortCO t : RankSorts.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, UpLevelEventCO> UpLevelEvents = new HashMap<Integer, UpLevelEventCO>();
	public static List<UpLevelEventCO> findUpLevelEvents(Predicate<UpLevelEventCO> pre) {
		List<UpLevelEventCO> results = new ArrayList<>();
		for(UpLevelEventCO t : UpLevelEvents.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, UpLevelExpExt> UpLevelExps = new HashMap<Integer, UpLevelExpExt>();
	public static List<UpLevelExpExt> findUpLevelExps(Predicate<UpLevelExpExt> pre) {
		List<UpLevelExpExt> results = new ArrayList<>();
		for(UpLevelExpExt t : UpLevelExps.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<String, PrefixCO> Prefixs = new HashMap<String, PrefixCO>();
	public static List<PrefixCO> findPrefixs(Predicate<PrefixCO> pre) {
		List<PrefixCO> results = new ArrayList<>();
		for(PrefixCO t : Prefixs.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<String, SuffixCO> Suffixs = new HashMap<String, SuffixCO>();
	public static List<SuffixCO> findSuffixs(Predicate<SuffixCO> pre) {
		List<SuffixCO> results = new ArrayList<>();
		for(SuffixCO t : Suffixs.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, FarmLandCO> FarmLands = new HashMap<Integer, FarmLandCO>();
	public static List<FarmLandCO> findFarmLands(Predicate<FarmLandCO> pre) {
		List<FarmLandCO> results = new ArrayList<>();
		for(FarmLandCO t : FarmLands.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, FarmRecordCO> FarmRecords = new HashMap<Integer, FarmRecordCO>();
	public static List<FarmRecordCO> findFarmRecords(Predicate<FarmRecordCO> pre) {
		List<FarmRecordCO> results = new ArrayList<>();
		for(FarmRecordCO t : FarmRecords.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, SeedCO> Seeds = new HashMap<Integer, SeedCO>();
	public static List<SeedCO> findSeeds(Predicate<SeedCO> pre) {
		List<SeedCO> results = new ArrayList<>();
		for(SeedCO t : Seeds.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, SeedTimeCO> SeedTimes = new HashMap<Integer, SeedTimeCO>();
	public static List<SeedTimeCO> findSeedTimes(Predicate<SeedTimeCO> pre) {
		List<SeedTimeCO> results = new ArrayList<>();
		for(SeedTimeCO t : SeedTimes.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, AccumulateExt> Accumulates = new HashMap<Integer, AccumulateExt>();
	public static List<AccumulateExt> findAccumulates(Predicate<AccumulateExt> pre) {
		List<AccumulateExt> results = new ArrayList<>();
		for(AccumulateExt t : Accumulates.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, LuxurySignExt> LuxurySigns = new HashMap<Integer, LuxurySignExt>();
	public static List<LuxurySignExt> findLuxurySigns(Predicate<LuxurySignExt> pre) {
		List<LuxurySignExt> results = new ArrayList<>();
		for(LuxurySignExt t : LuxurySigns.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, NormalSignExt> NormalSigns = new HashMap<Integer, NormalSignExt>();
	public static List<NormalSignExt> findNormalSigns(Predicate<NormalSignExt> pre) {
		List<NormalSignExt> results = new ArrayList<>();
		for(NormalSignExt t : NormalSigns.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, MonsterSkillValueCO> MonsterSkillValues = new HashMap<Integer, MonsterSkillValueCO>();
	public static List<MonsterSkillValueCO> findMonsterSkillValues(Predicate<MonsterSkillValueCO> pre) {
		List<MonsterSkillValueCO> results = new ArrayList<>();
		for(MonsterSkillValueCO t : MonsterSkillValues.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, PassiveSkillExt> PassiveSkills = new HashMap<Integer, PassiveSkillExt>();
	public static List<PassiveSkillExt> findPassiveSkills(Predicate<PassiveSkillExt> pre) {
		List<PassiveSkillExt> results = new ArrayList<>();
		for(PassiveSkillExt t : PassiveSkills.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, PetSkillExt> PetSkills = new HashMap<Integer, PetSkillExt>();
	public static List<PetSkillExt> findPetSkills(Predicate<PetSkillExt> pre) {
		List<PetSkillExt> results = new ArrayList<>();
		for(PetSkillExt t : PetSkills.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, SkillDataExt> SkillDatas = new HashMap<Integer, SkillDataExt>();
	public static List<SkillDataExt> findSkillDatas(Predicate<SkillDataExt> pre) {
		List<SkillDataExt> results = new ArrayList<>();
		for(SkillDataExt t : SkillDatas.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, SkillValueExt> SkillValues = new HashMap<Integer, SkillValueExt>();
	public static List<SkillValueExt> findSkillValues(Predicate<SkillValueExt> pre) {
		List<SkillValueExt> results = new ArrayList<>();
		for(SkillValueExt t : SkillValues.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, TalentGroupCO> TalentGroups = new HashMap<Integer, TalentGroupCO>();
	public static List<TalentGroupCO> findTalentGroups(Predicate<TalentGroupCO> pre) {
		List<TalentGroupCO> results = new ArrayList<>();
		for(TalentGroupCO t : TalentGroups.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<String, AllyConfigCO> AllyConfigs = new HashMap<String, AllyConfigCO>();
	public static List<AllyConfigCO> findAllyConfigs(Predicate<AllyConfigCO> pre) {
		List<AllyConfigCO> results = new ArrayList<>();
		for(AllyConfigCO t : AllyConfigs.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, TeamPropExt> TeamProps = new HashMap<Integer, TeamPropExt>();
	public static List<TeamPropExt> findTeamProps(Predicate<TeamPropExt> pre) {
		List<TeamPropExt> results = new ArrayList<>();
		for(TeamPropExt t : TeamProps.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<String, AllyNameCO> AllyNames = new HashMap<String, AllyNameCO>();
	public static List<AllyNameCO> findAllyNames(Predicate<AllyNameCO> pre) {
		List<AllyNameCO> results = new ArrayList<>();
		for(AllyNameCO t : AllyNames.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, SocialFriendCO> SocialFriends = new HashMap<Integer, SocialFriendCO>();
	public static List<SocialFriendCO> findSocialFriends(Predicate<SocialFriendCO> pre) {
		List<SocialFriendCO> results = new ArrayList<>();
		for(SocialFriendCO t : SocialFriends.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, SocialMessageCO> SocialMessages = new HashMap<Integer, SocialMessageCO>();
	public static List<SocialMessageCO> findSocialMessages(Predicate<SocialMessageCO> pre) {
		List<SocialMessageCO> results = new ArrayList<>();
		for(SocialMessageCO t : SocialMessages.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, SShopCO> SShops = new HashMap<Integer, SShopCO>();
	public static List<SShopCO> findSShops(Predicate<SShopCO> pre) {
		List<SShopCO> results = new ArrayList<>();
		for(SShopCO t : SShops.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, TeamTargetExt> TeamTargets = new HashMap<Integer, TeamTargetExt>();
	public static List<TeamTargetExt> findTeamTargets(Predicate<TeamTargetExt> pre) {
		List<TeamTargetExt> results = new ArrayList<>();
		for(TeamTargetExt t : TeamTargets.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, SoloMonsterCO> SoloMonsters = new HashMap<Integer, SoloMonsterCO>();
	public static List<SoloMonsterCO> findSoloMonsters(Predicate<SoloMonsterCO> pre) {
		List<SoloMonsterCO> results = new ArrayList<>();
		for(SoloMonsterCO t : SoloMonsters.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, SoloNewsCO> SoloNewss = new HashMap<Integer, SoloNewsCO>();
	public static List<SoloNewsCO> findSoloNewss(Predicate<SoloNewsCO> pre) {
		List<SoloNewsCO> results = new ArrayList<>();
		for(SoloNewsCO t : SoloNewss.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, SoloRankExt> SoloRanks = new HashMap<Integer, SoloRankExt>();
	public static List<SoloRankExt> findSoloRanks(Predicate<SoloRankExt> pre) {
		List<SoloRankExt> results = new ArrayList<>();
		for(SoloRankExt t : SoloRanks.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, SoloRankSeasonRewardExt> SoloRankSeasonRewards = new HashMap<Integer, SoloRankSeasonRewardExt>();
	public static List<SoloRankSeasonRewardExt> findSoloRankSeasonRewards(Predicate<SoloRankSeasonRewardExt> pre) {
		List<SoloRankSeasonRewardExt> results = new ArrayList<>();
		for(SoloRankSeasonRewardExt t : SoloRankSeasonRewards.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, TransportExt> Transports = new HashMap<Integer, TransportExt>();
	public static List<TransportExt> findTransports(Predicate<TransportExt> pre) {
		List<TransportExt> results = new ArrayList<>();
		for(TransportExt t : Transports.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, VipCO> Vips = new HashMap<Integer, VipCO>();
	public static List<VipCO> findVips(Predicate<VipCO> pre) {
		List<VipCO> results = new ArrayList<>();
		for(VipCO t : Vips.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}

	public static Map<Integer, WingsListExt> WingsLists = new HashMap<Integer, WingsListExt>();
	public static List<WingsListExt> findWingsLists(Predicate<WingsListExt> pre) {
		List<WingsListExt> results = new ArrayList<>();
		for(WingsListExt t : WingsLists.values()) {
			if(pre.test(t)) {
				results.add(t);
			}
		}
		return results;
	}


}