package com.wanniu.game.player;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.wanniu.core.GConfig;
import com.wanniu.core.game.JobFactory;
import com.wanniu.core.logfs.Out;
import com.wanniu.core.util.DataUtil;
import com.wanniu.core.util.RandomUtil;
import com.wanniu.core.util.StringUtil;
import com.wanniu.game.GWorld;
import com.wanniu.game.common.Const.SCENE_TYPE;
import com.wanniu.game.data.DebugParametersCO;
import com.wanniu.game.data.DungeonMapCO;
import com.wanniu.game.data.GameData;
import com.wanniu.game.data.OpenLvCO;
import com.wanniu.game.data.ParametersCO;
import com.wanniu.game.data.TeamTargetCO;
import com.wanniu.game.guild.guildFort.GuildFortCenter;
import com.wanniu.game.guild.guildFort.GuildFortService;
import com.wanniu.game.item.ItemConfig;
import com.wanniu.game.monster.GuildBossRatioConfig;
import com.wanniu.game.monster.MonsterConfig;
import com.wanniu.game.task.TaskUtils;

/**
 * 游戏参数配置管理
 * 
 * @author Yangzz
 *
 */
public class GlobalConfig {

	//================================ParamMain auto generate start================================//
	public static int Role_LevelLimit;
	public static int Role_ReqOfferupLevel;
	public static int Role_Upgrade_LevelLimit;
	public static int Exp_WorldRatio;
	public static int Exp_Monster_LevelLimit;
	public static int WorldExp_ReqLevel;
	public static int WorldExp_MaxLevelValue;
	public static int WorldExp_MinExpRatio;
	public static int WorldExp_MaxExpRatio;
	public static int WorldExp_RatioPerLevel;
	public static int WorldExp_UpToLevel;
	public static String WorldExp_Winner_ItemList;
	public static String WorldExp_Bonus_AccountTime;
	public static String WorldExp_SuperChest_ItemCode;
	public static String WorldExp_NormalChest_ItemCode;
	public static int WorldExp_SuperChest_Chance;
	public static int WorldExp_DiamondAdmire_Price;
	public static int WorldExp_Admire_MaxCount;
	public static int WorldExp_DiamondAdmire_AddGold;
	public static int WorldExp_Admire_AddGold;
	public static int resurgence;
	public static int allResurgence;
	public static String HornCode;
	public static int HornCount;
	public static int Room_Reconnection;
	public static int Exp_RoleRank_Basic;
	public static int Exp_RoleRank_PenaltyCoefficient;
	public static int OffLineExp_ReqLevel;
	public static int OffLineExp_MaxTime;
	public static int OffLineExp_TimeUnit;
	public static int OffLineExp_2Bonus_ReqVIPLevel;
	public static int OffLineExp_3Bonus_CostDiamond;
	public static int OffLineExp_4Bonus_CostDiamond;
	public static int OffLineExp_3Bonus_ReturnCash;
	public static int OffLineExp_4Bonus_ReturnCash;
	public static int LostBonus_ReqLevel;
	public static int LostBonus_FreeRate;
	public static int LostBonus_TotalRate;
	public static String LostBonus_Exp_CostDiamond;
	public static String LostBonus_Ride_CostDiamond;
	public static String LostBonus_Wing_CostDiamond;
	public static int Package_MaxSize;
	public static int Package_DefaultSize;
	public static int Package_PricePer;
	public static int Dungeon_Overtime_Wait;
	public static int Dungeon_SendToChat_Time;
	public static int Dungeon_SendToFriends_Time;
	public static int Dungeon_DropList_Count;
	public static int Dungeon_WaitFriends_Time;
	public static int Dungeon_TeamInChat_Time;
	public static int Dungeon_DropList_ItemQcolor;
	public static int Dungeon_DeadBack_Time;
	public static int Dungeon_Close_Room;
	public static int Dungeon_Close_TeamRoom;
	public static int Dungeon_Sweep_VIP;
	public static String World_Boss_OpenTime;
	public static int World_Boss_Pick_ItemQcolor;
	public static int World_Boss_Scene;
	public static int World_Boss_VipScene;
	public static int World_Boss_NewScene;
	public static int World_Boss_NewScene_Close;
	public static int World_Boss_NweScene_Quest;
	public static int World_Boss_DPS_ListMin;
	public static int World_Boss_DPS_ListMax;
	public static String World_Boss_Player_Point;
	public static int motherland;
	public static int Ride_InitUpLevel;
	public static int Ride_InitStar;
	public static int Ride_MaxUpLevel;
	public static int mountMaxStar;
	public static int Ride_DefaultSkinID;
	public static String Equipment_IdentfyItem_Code;
	public static int Equipment_IdentfyGreen_Count;
	public static int Equipment_IdentfyBlue_Count;
	public static int Equipment_IdentfyPurple_Count;
	public static int Equipment_IdentfyLegend_Count;
	public static int Equipment_IdentfyPercent_Count;
	public static String Equipment_RandomDrop_BindChance;
	public static int Equipment_Speaker_StrengthenLevel;
	public static int Equipment_Effect_Qcolor;
	public static int Equipment_Effect_StrengthenLevel;
	public static int EquipmentCraft_Enchant_MaxEnClass;
	public static int EquipmentCraft_Enchant_MaxenLevel;
	public static int Equipment_MinAffixChance;
	public static int Equipment_SameAffixChance2;
	public static int Equipment_SameAffixChance3;
	public static int Equipment_SameAffixChance4;
	public static int Equipment_SameAffixChance5;
	public static int Equipment_ReBuild_AddAffixChance;
	public static int Equipment_ReBuild_MaxNumtoLock3;
	public static int Equipment_ReBuild_MaxNumtoLock4;
	public static int Equipment_ReBuild_MaxNumtoLock5;
	public static int Equipment_SeniorReBuild_MaxRatio;
	public static int Equipment_Refine_RepeatPropCount;
	public static String Equipment_Refine_ResetItemCode;
	public static String Equipment_Refine_ResetItemCount;
	public static int Equipment_Refine_ConfirmQColor;
	public static int Equipment_PropNum_FloorRate;
	public static int Equipment_LvUp_QColor;
	public static int Equipment_Upgrade_QColor;
	public static int Item_Qcolor_Combine_Notice;
	public static int Equipment_UpLevel_LevelUp_Notice;
	public static int Equipment_Qcolor_Upgrade_Notice;
	public static int Equipment_Qcolor_Make_Notice;
	public static String Magical_MateCodes;
	public static String Magical_MateCounts;
	public static String Magical_MatePrice;
	public static int Suite_Complete_Time;
	public static String Item_Attention;
	public static int skillTalentlFirst;
	public static int skillTalentlSecond;
	public static int itemdrop_lock_freezeTime;
	public static int itemdrop_lock_protectTime;
	public static int itemdrop_lock_lifeTime;
	public static int NormalTeamMinNum;
	public static int NormalTeamMaxNum;
	public static int TeamOfflineMaxTime;
	public static int TeamSharedScope;
	public static int TeamExperience2;
	public static int TeamExperience3;
	public static int TeamExperience4;
	public static int TeamExperience5;
	public static int TeamViewMAX;
	public static int TeamGoMapLeftTime;
	public static int TeamRefuseWaitTime;
	public static String TeamMapTypeGO;
	public static int TeamAutoInvite;
	public static String TeamSummonItem;
	public static int PersonalWarehouse_MaxSize;
	public static int PersonalWarehouse_DefaultSize;
	public static int PersonalWarehouse_PricePer;
	public static int AccountWarehouse_MaxSize;
	public static int AccountWarehouse_DefaultSize;
	public static int AccountWarehouse_PricePer;
	public static int Social_MaxFriendNum;
	public static int Social_MaxEnemyNum;
	public static int Social_MaxBlacklistNum;
	public static int Social_MaxFriendMessageNum;
	public static int Social_FavorReduce;
	public static int Social_FocusNum;
	public static int Social_RecommendNum;
	public static int Social_LevelRange;
	public static int Social_FavorNumMax;
	public static int Social_FavorNumDailyMax;
	public static int Social_KillNumMax;
	public static int Social_KillNumMin;
	public static int Social_InviteFriendMax;
	public static int Social_FriendshipNumDailyMax;
	public static String Social_FocusBuff;
	public static int Social_FriendRefresh;
	public static int Planting_FriendMaxStealNum;
	public static int Planting_StealProp;
	public static int Planting_PlantStealNum;
	public static int Planting_SeedStageProp;
	public static int Planting_GrowStageProp;
	public static int Planting_MatureStageProp;
	public static int Planting_ProtectionTime;
	public static int Planting_DeductionProp;
	public static int Planting_ReduceTime;
	public static int Planting_MaxRecord;
	public static int NpcShop_ItemPrice_CutRate;
	public static int PK_LevelReq;
	public static int PK_DropGoldMin;
	public static int PK_DropGoldMax;
	public static int PK_DropSimilarMin;
	public static int PK_DropSimilarMax;
	public static int PK_PKValue_Max;
	public static int PK_OnLine_Time;
	public static int PK_OnLine_ReducePKValue;
	public static int PK_AddPKValue_White_Attack;
	public static int PK_AddPKValue_White_Killed;
	public static int PK_Killed_LostBagItem_Chance;
	public static int PK_Killed_LostBagItem_MaxCount;
	public static int PK_Killed_LostEquip_PKValue;
	public static int PK_Killed_LostEquip_Denominator;
	public static int PK_Killed_LostEquip_MaxRate;
	public static String PK_Killed_LostEquip_ExceptType;
	public static int PK_Killed_LostEquip_HurtDmgTimeOut;
	public static int PK_Killed_LostBagItem_AllDrop;
	public static int PK_Killed_LostBagItem_PartDropMin;
	public static int PK_Killed_LostBagItem_PartDropMax;
	public static int MATCH_WAIT_TIME;
	public static int MATCH_TIME;
	public static String MATCH_SCENE;
	public static int Solo_SeasonDay;
	public static int Solo_SeasonWeekday;
	public static int Solo_EnterTime;
	public static int Solo_ReadyTime;
	public static int Solo_PKTime;
	public static int Solo_MATCH_TIME;
	public static int Solo_MatchScoreRange;
	public static int Solo_MatchWaitTime;
	public static int Solo_MatchRangeIncrease;
	public static int Solo_MatchOfflineTime;
	public static int Solo_MasterCoinGainLimit;
	public static int Solo_WinGainMasterCoin;
	public static int Solo_LoseGainMasterCoin;
	public static int Solo_ChestMaxCount;
	public static int Solo_PKForChest;
	public static int Solo_WinGetPoint;
	public static int Solo_SoloNews_Number;
	public static int Solo_MapID;
	public static String Solo_Weekly_OpenTime;
	public static String Solo_Daily_OpenTime;
	public static int Solo_ReportCount;
	public static int Solo_ReportTime;
	public static int Path_AutoRide_Distance;
	public static int Revive_SOS_Time;
	public static int Trade_diamond_Max;
	public static int Trade_RequestTime;
	public static int Trade_Item_Max;
	public static int Trade_Level;
	public static int JJC_RebirthTime;
	public static int JJC_RankPlayerNum;
	public static int JJC_RankNumMax;
	public static int JJC_MatchTime;
	public static int JJC_KillScore;
	public static String JJC_BigItemCode;
	public static String JJC_MiddleItemCode;
	public static String JJC_SmallItemCode;
	public static String JJC_Weekly_OpenTime;
	public static String JJC_Daily_OpenTime;
	public static String JJC_Daily_Award;
	public static int JJC_SeasonDay;
	public static int JJC_SeasonWeekday;
	public static int JJC_EnterCount;
	public static int Dis_BagNumber_max;
	public static int Dis_Roll_quality;
	public static int Auto_HP_Percent;
	public static int Auto_MP_Percent;
	public static String Auto_HP_Item;
	public static String Auto_MP_Item;
	public static int Auto_PK_Reaction;
	public static int Auto_Eqip_Qcolor;
	public static int Auto_HP_Buy;
	public static int Auto_MP_Buy;
	public static int Shop_OnSaleTime_Diamond;
	public static int Shop_ShelfTime_Diamond;
	public static int Shop_OnSaleTime_Ticket;
	public static int Shop_ShelfTime_Ticket;
	public static int Shop_OnSaleTime_Exchange;
	public static int Shop_ShelfTime_Exchange;
	public static String Shop_Need_Gold;
	public static int Consignment_SellTime;
	public static int Consignment_Level;
	public static int Consignment_SellNum;
	public static int Consignment_Fee;
	public static int Consignment_Commission;
	public static int Consignment_MinFee;
	public static int Consignment_MinCommission;
	public static int Consignment_MinPrice;
	public static int Consignment_MaxPrice;
	public static String Consignment_DefaultItem;
	public static int Consignment_DefaultItem_QuestID;
	public static int Consignment_Commission_serverEnter;
	public static int Consignment_Anonymous_VIP;
	public static String Consignment_Anonymous_Show;
	public static int Consignment_Advertisement_goldNum;
	public static int Consignment_Advertisement_gold;
	public static int Consignment_Advertisement_diamond;
	public static int Consignment_Advertisement_minPercent;
	public static int Consignment_Advertisement_maxPercent;
	public static int MagicRing_MissionID;
	public static int Activity_LuckDraw;
	public static int Activity_LuckDraw_Mail;
	public static int Activity_LuckDrwa_Cost;
	public static int Activity_LuckDrwa_CumulativeCost;
	public static int Activity_LuckDrwa_Reset;
	public static int Activity_Fund_Buy;
	public static int Activity_Fund_Vip;
	public static int Ally_PersonNum;
	public static int Ally_MessageNum;
	public static int Ally_BloodBook_DailyNum;
	public static int Ally_Blessing_EffectNum;
	public static int Ally_Blessing_DailyNum;
	public static int Quest_Soul_DailyLimit;
	public static int Quest_Soul_Refresh_CostDiamond;
	public static int CROSS_SERVER_ENTER_LEVEL;
	public static int CROSS_SERVER_ENTER_UPLEVEL;
	public static int CROSS_SERVER_ENTER_SCENE;
	public static String CROSS_SERVER_TREASURE_OPEN;
	public static String CROSS_SERVER_TREASURE_SEND_OPEN;
	public static String CROSS_SERVER_TREASURE_SEND_CLOSE;
	public static int CROSS_SERVER_TREASURE_COOLDOWN;
	public static String CROSS_SERVER_TREASURE_REFRESH;
	public static int CROSS_SERVER_TREASURE_MAX;
	public static int AllyWar_SingleRankNum;
	public static int AllyWar_AllyRankNum;
	public static int AllyWar_AllyTeamNum;
	public static int AllyWar_Time;
	public static int AllyWar_ShopListNum;
	public static String AllyWar_ShopListRefreshTime;
	public static int AllyGold_DropMinNum;
	public static int AllyGold_DropMaxNum;
	public static int AllyWar_AdvanceTime;
	public static String AllyWar_Rule;
	public static String AllyGold_Code;
	public static String AllyChange_Npe;
	public static int AllyWar_RebirthTime;
	public static int Wings_MonsterMinLv;
	public static String Quest_FishGear_Code;
	public static int Transfer_VIP_Min;
	public static String Transfer_SceneType;
	public static String Sign_Key;
	public static String Vendor_Key;
	public static int RewardPK_ReleaseMinLv;
	public static int RewardPK_BeOfferedMinLv;
	public static int RewardPK_MaxNum;
	public static int RewardPK_BeOffereMaxNum;
	public static String RewardPK_Item;
	public static int RewardPK_ItemNum;
	public static String RewardPK_DropItem;
	public static int RewardPK_DropItem_Prob;
	public static int RewardPK_Time;
	public static String Chat_Default_Color;
	public static int RankList_Limit;
	public static int RankList_Save_Limit;
	public static int Pet_GetExp_Prob;
	public static String Call_Boss_TaskID;
	public static int Quest_rewardTimes1;
	public static int Quest_rewardTimes2;
	public static float Loop_Hard_Rate;
	public static int Daily_completeMax;
	public static int Loop_completeTips;
	public static int Leave_Notice_Quality;
	public static String Treasures_Item;
	public static int Treasures_ItemNum;
	public static int Treasures_Diamond;
	public static int Treasures_point;
	public static int Treasures_BagMax;
	public static int Treasures_BagEveryone;
	public static String Treasures_Exchange;
	public static String LengendEquip_Exchange;
	public static int MillItemMax;
	public static int HUD_TopMenu_Close;
	public static String Map_Type;
	public static String Monster_NoDrop_IDList;
	public static int Vip_DailyReward_Time;
	public static String Bombbox_Time;
	public static int Bombbox_Frequency;
	public static String OpenLv_Animation_SceneID;
	public static int Group_Cooldown;
	public static int Group_Integral;
	public static int Group_Lose;
	public static int Group_RewardTimes;
	public static int Group_Time;
	public static int Group_Point;
	public static int Group_PlusPoint;
	public static int Group_MaxPoint;
	public static int Group_MatchingTime;
	public static int Group_Kill;
	public static int Group_Dps;
	public static int Group_Treatment;
	public static int Group_Punishment;
	public static int Group_ReadyTime;
	public static int Group_Record;
	public static int Group_Map;
	public static int Group_Team;
	public static int Group_AverageTime;
	public static int Group_Resurrection;
	public static String Group_Weekly_OpenTime;
	public static String Group_Daily_OpenTime;
	public static int Fate_Dungeon;
	public static int Fate_Break;
	public static int Fate_Resource;
	public static int Fate_Loop;
	public static int Fate_Plant;
	public static float Fate_Resource_Rate;
	public static int Guide_Time;
	public static int Flee_SeasonDay;
	public static int Flee_SeasonWeekday;
	public static int Flee_EnterTime;
	public static int Flee_ReadyTime;
	public static int Flee_PKTime;
	public static int Flee_MATCH_TIME;
	public static int Flee_MatchOfflineTime;
	public static int Flee_WinGetPoint;
	public static int Flee_OneToFive_GetPoint;
	public static int Flee_SixToTen_GetPoint;
	public static int Flee_RankBase_GetPoint;
	public static int Flee_RankBase_BaseGetPoint;
	public static int Flee_MapID;
	public static String Flee_Weekly_OpenTime;
	public static String Flee_Daily_OpenTime;
	public static int Flee_ReportCount;
	public static int Lords_MaxNumEveryday;
	public static int Mysterious_MaxNumEveryday;
	public static String Mysterious_RewardShow;
	public static String Mysterious_GetRewardNum;
	public static String Mysterious_Reward;
	public static String Mysterious_MaxNumVcard1;
	public static String Mysterious_DropItems;
	public static int Mysterious_DropItemsPro;
	public static int Mysterious_DropItemsMaxNum;
	public static String GuildBoss_Weekly_OpenTime;
	public static String GuildBoss_Daily_OpenTime;
	public static int GuildBoss_MapID;
	public static String GuildBoss_Reward;
	public static int GuildBoss_GrowLevel;
	public static int GuildBoss_InspireMaxNum1;
	public static int GuildBoss_InspireMaxNum2;
	public static int GuildBoss_InspireMaxNum3;
	public static int GuildBoss_LeaveTime;
	public static int Auction_GuildShowTime;
	public static int Auction_GuildBiddingTime;
	public static int Auction_WorldShowTime;
	public static int Auction_WorldBiddingTime;
	public static int Auction_GuildTaxation;
	public static int Auction_LastAddTimes;
	public static int Auction_PerAddTimes;
	public static int Auction_MaxBonus;
	public static int Exchange_OpenLV;
	public static int Exchange_Exp;
	public static int Exchange_UpExp;
	public static int Exchange_Gold;
	public static int Exchange_AddBlood;
	public static int Exchange_AddPhyAttack;
	public static int Exchange_AddMagAttack;
	public static int Exchange_AddPhyDefense;
	public static int Exchange_AddMagDefense;
	public static int Combine_Exp;
	public static int Combine_UpExp;
	public static String Combine_Items;
	public static int Combine_MaxDay;
	public static int Red_DiamondNumber_Down;
	public static int Red_DiamondNumber_Up;
	public static int Red_DiamondNumber_Add;
	public static int Red_LootLevel;
	public static int Red_SendLevel;
	public static int Red_Prop;
	public static int Red_HongbaoNumber_Down;
	public static int Red_HongbaoNumber_Up;
	public static int Red_HongbaoTime;
	public static int Red_HongbaoRatio;
	public static String Zillionaire_Open_Time;
	public static int Zillionaire_Continued_Time;
	public static int Zillionaire_Yuanbao_One;
	public static int Zillionaire_FreeUp;
	public static int Zillionaire_CurrentTurn;
	public static String GuildFort_PickPoint;
	public static int GuildFort_PickAddDefense;
	public static String GuildFort_KillMonPoint;
	public static int GuildFort_KillMonAddAttack;
	public static String GuildFort_BidStartTime;
	public static String GuildFort_BidEndTime;
	public static String GuildFort_BattleStartTime;
	public static String GuildFort_BattleEndTime;
	public static int GuildFort_PreStart;
	public static int GuildFort_BackRate;
	public static int GuildFort_KillPoint;
	public static int GuildFort_DestroyPoint;
	public static int GuildFort_TotalFlag;
	public static int GuildFort_MinWinPoint;
	public static int GuildFort_MaxWinPoint;
	public static int GuildFort_BattleRecordNum;
	public static int GuildFort_RoundDays;
	public static int GuildFort_MinBetMoney;
	public static int GuildFort_countDown;
	public static String AddRecharge_Time_Begin;
	public static String AddRecharge_Time_End;
	public static String AddRecharge_Reward;
	public static String SevenGoal_Begin;
	public static int SevenGoal_Continued;
	public static int SevenGoal_CurrentTurn;
	public static String DoubleGet_Begin;
	public static String DoubleGet_End;
	public static int DemonTower_LimitTime;
	public static String Red_Time_Slot;
	public static int Red_Time_Space;
	public static int Red_Time_Number;
	public static int God_Red_Money;
//================================ParamMain auto generate end================================//

	public static int Mail_Attach_Size = 10;

	public static int Box_Refresh_Time = 120000;

	public static int Team_Min_Level = 10;

	public static int Robot_Level = 25;

	public static int[] Equipment_RandomDrop_BindChances;
	public static int[] TeamMapTypeGOs;
	public static int[] Monster_NoDrop_IDLists;
	public static int[] DemonTowerMapIds;

	public static int LevelRatio;
	public static float FloorRatio;
	public static float PropRatio;
	public static int ResetNum;
	public static int SweepTime;
	public static int SweepPrice;

	public static List<Integer> PK_EXCEPT_TYPES = new ArrayList<>();
	public static Map<String, Integer> mysteriousRewardInfo = new HashMap<>();
	public static Map<String,Integer>  mysteriousMaxNumVcardInfo = new HashMap<>();
	public static Set<String> pkDrop = new HashSet<>();
	
	public static Map<Integer, Integer> guildFortPickPoint = new HashMap<>();
	public static Map<Integer,Integer>  guildFortKillMonPoint = new HashMap<>();

	
	private static void initParameters(GlobalConfig config) {
		for (ParametersCO param : GameData.Parameterss.values()) {
			try {
				Field field = config.getClass().getField(param.paramName.trim().replace(".", "_"));
				String typeName = field.getType().getSimpleName();
				if ("int".equals(typeName)) {
					field.set(config, Integer.parseInt(param.paramValue));
				} else if ("float".equals(typeName)) {
					field.set(config, Float.parseFloat(param.paramValue));
				} else {
					field.set(config, param.paramValue);
				}
			} catch (Exception e) {
				Out.error(param.paramName, "\t", param.paramType, "\t", param.paramValue);
				Out.error(e);
			}
		}
	}
	
	private static void initDebugParameters(GlobalConfig config) {
		for (DebugParametersCO param : GameData.DebugParameterss.values()) {
			try {
				Field field = config.getClass().getField(param.paramName.trim().replace(".", "_"));
				String typeName = field.getType().getSimpleName();
				if ("int".equals(typeName)) {
					field.set(config, Integer.parseInt(param.paramValue));
				} else if ("float".equals(typeName)) {
					field.set(config, Float.parseFloat(param.paramValue));
				} else {
					field.set(config, param.paramValue);
				}
			} catch (Exception e) {
				Out.error(param.paramName, "\t", param.paramType, "\t", param.paramValue);
				Out.error(e);
			}
		}
	}
	
	/**
	 * 加载策划脚本
	 */
	public static void init() {
		GlobalConfig config = new GlobalConfig();
		if(GWorld.DEBUG){
			initDebugParameters(config);
			File debugConfigFile = new File(GConfig.getInstance().get("dir.game.data")+"\\config\\GameConfig\\DebugParameters.json");			
			JobFactory.addScheduleJob(new Runnable() {
				long modifiedTime = debugConfigFile.lastModified();
				@Override
				public void run() {	
					long newModifiedTime = debugConfigFile.lastModified();
					if(newModifiedTime>modifiedTime) {
						modifiedTime = newModifiedTime;
						Out.error( debugConfigFile.getAbsolutePath()," has modified, begin reload.......");
						DataUtil.reloadData(debugConfigFile);
						initDebugParameters(config);
						GuildFortService.getInstance().reload();//TODO Only for test GuildFort
						Out.error( debugConfigFile.getAbsolutePath()," reloaded!!!");
					}else {
						GuildFortCenter.getInstance().test();
					}
				}
			}, 10*1000, 10 * 1000);
		}else{
			initParameters(config);
		}
		
		{
			String[] arr = Equipment_RandomDrop_BindChance.split(",");
			Equipment_RandomDrop_BindChances = new int[arr.length];
			copyIntArray(arr, Equipment_RandomDrop_BindChances);
		}
		{
			String[] arr = TeamMapTypeGO.split(",");
			TeamMapTypeGOs = new int[arr.length];
			copyIntArray(arr, TeamMapTypeGOs);
		}
		{
			String[] arr = Monster_NoDrop_IDList.split(",");
			Monster_NoDrop_IDLists = new int[arr.length];
			copyIntArray(arr, Monster_NoDrop_IDLists);
		}
		// 秘境宝箱
		{
			String[] rs = Mysterious_Reward.split(",");
			if (rs != null) {
				for (int i = 0; i < rs.length; i++) {
					mysteriousRewardInfo.put(rs[i], i + 1);
				}
			}
			
			String [] rs2 = Mysterious_MaxNumVcard1.split(",");
			if(rs2 != null) {
				for (int i = 0; i < rs2.length; i++) {
					String [] t2 = rs2[i].split(":");
					mysteriousMaxNumVcardInfo.put(t2[0],Integer.valueOf(t2[1]));
				}
			}
			//秘境杀人掉落物品
			String [] rs3 = Mysterious_DropItems.split(",");
			if(rs3 != null) {
				for (int i = 0; i < rs3.length; i++) {
					String code = rs3[i];
					pkDrop.add(code);
				}
			}
		}
		
		// Guild Fort
		{		
			String [] rs = GuildFort_PickPoint.split(";");
			if(rs != null) {
				for (int i = 0; i < rs.length; i++) {
					String [] t = rs[i].split(":");
					guildFortPickPoint.put(Integer.valueOf(t[0]),Integer.valueOf(t[1]));
				}
			}
			
			String [] rs1 = GuildFort_KillMonPoint.split(";");
			if(rs1 != null) {
				for (int i = 0; i < rs1.length; i++) {
					String [] t = rs1[i].split(":");
					guildFortKillMonPoint.put(Integer.valueOf(t[0]),Integer.valueOf(t[1]));
				}
			}
		}
		
		MonsterConfig.getInstance().loadScript();
		ItemConfig.getInstance().loadScript();
		GuildBossRatioConfig.loadScript();
		TaskUtils.init();

		List<DungeonMapCO> towers = GameData.findDungeonMaps(p -> {
			return p.type == SCENE_TYPE.DEMON_TOWER.getValue();
		});
		DemonTowerMapIds = new int[towers.size()];
		for (int i = 0; i < DemonTowerMapIds.length; i++) {
			DemonTowerMapIds[i] = towers.get(i).mapID;
		}

		GameData.MonsterConfigs.values().forEach(p -> {
			switch (p.param) {
			case "LevelRatio":
				LevelRatio = (int) p.paramValue;
				break;
			case "FloorRatio":
				FloorRatio = p.paramValue;
				break;
			case "PropRatio":
				PropRatio = p.paramValue;
				break;
			case "ResetNum":
				ResetNum = (int) p.paramValue;
				break;
			case "SweepTime":
				SweepTime = (int) p.paramValue;
				break;
			case "SweepPrice":
				SweepPrice = (int) p.paramValue;
				break;
			}
		});

		String[] exceptTypeStrs = GlobalConfig.PK_Killed_LostEquip_ExceptType.split(",");
		for (String exceptTypeStr : exceptTypeStrs) {
			if (StringUtil.isNotEmpty(exceptTypeStr)) {
				PK_EXCEPT_TYPES.add(Integer.parseInt(exceptTypeStr));
			}
		}

		// 队伍开放等级
		OpenLvCO open = GameData.OpenLvs.get(16);
		if (open != null) {
			Team_Min_Level = open.openLv;
		}
		TeamTargetCO target = GameData.TeamTargets.get(1010);
		if (target != null) {
			Robot_Level = target.openLv;
		}
	}
	
	/**
	 * 乱序
	 * @return
	 */
	public static Set<String> getPKDropSet(){	
		return pkDrop;
	}

	public static Integer getMysteriousLv(String code) {
		return mysteriousRewardInfo.get(code);
	}
	
	public static Integer getItemCount(String code) {
		return mysteriousMaxNumVcardInfo.get(code);
	}

	private static void copyIntArray(String[] from, int[] to) {
		for (int i = 0; i < from.length; i++) {
			to[i] = Integer.parseInt(from[i]);
		}
	}

	public static int getBindByQcolor(int qcolor) {
		if (Equipment_RandomDrop_BindChances != null && qcolor < Equipment_RandomDrop_BindChances.length) {
			int v = Equipment_RandomDrop_BindChances[qcolor];
			int randValue = RandomUtil.random(100);
			if (randValue <= v) {
				return 1;
			}
		}
		return 0;
	}

}

