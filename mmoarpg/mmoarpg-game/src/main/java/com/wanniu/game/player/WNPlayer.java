package com.wanniu.game.player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.time.DateUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.google.protobuf.GeneratedMessage;
import com.wanniu.core.db.GCache;
import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GPlayer;
import com.wanniu.core.logfs.Out;
import com.wanniu.core.util.StringUtil;
import com.wanniu.csharp.CSharpClient;
import com.wanniu.game.GWorld;
import com.wanniu.game.achievement.AchievementManager;
import com.wanniu.game.activity.ActivityManager;
import com.wanniu.game.activity.DemonTowerManager;
import com.wanniu.game.activity.RechargeActivityService;
import com.wanniu.game.area.Area;
import com.wanniu.game.area.Area.Actor;
import com.wanniu.game.area.AreaData;
import com.wanniu.game.area.AreaDataConfig;
import com.wanniu.game.area.AreaUtil;
import com.wanniu.game.area.DropManager;
import com.wanniu.game.area.PlayerRemote;
import com.wanniu.game.area.SceneProgressManager;
import com.wanniu.game.arena.ArenaManager;
import com.wanniu.game.attendance.PlayerAttendance;
import com.wanniu.game.auction.AuctionManager;
import com.wanniu.game.bag.WNBag;
import com.wanniu.game.bag.WNBag.TradeMessageItemData;
import com.wanniu.game.blood.BloodManager;
import com.wanniu.game.buffer.BufferManager;
import com.wanniu.game.common.CommonUtil;
import com.wanniu.game.common.Const;
import com.wanniu.game.common.Const.BORN_TYPE;
import com.wanniu.game.common.Const.EventType;
import com.wanniu.game.common.Const.GOODS_CHANGE_TYPE;
import com.wanniu.game.common.Const.KickReason;
import com.wanniu.game.common.Const.MESSAGE_TYPE;
import com.wanniu.game.common.Const.ManagerType;
import com.wanniu.game.common.Const.PlayerBtlData;
import com.wanniu.game.common.Const.PlayerEventType;
import com.wanniu.game.common.Const.SCENE_TYPE;
import com.wanniu.game.common.Const.SUPERSCRIPT_TYPE;
import com.wanniu.game.common.ConstsTR;
import com.wanniu.game.common.ModuleManager;
import com.wanniu.game.common.Utils;
import com.wanniu.game.common.msg.MessagePush;
import com.wanniu.game.common.msg.MessageUtil;
import com.wanniu.game.common.msg.WNNotifyManager;
import com.wanniu.game.consignmentShop.ConsignmentManager;
import com.wanniu.game.consignmentShop.ConsignmentUtil;
import com.wanniu.game.daily.DailyActivityMgr;
import com.wanniu.game.data.CharacterCO;
import com.wanniu.game.data.CharacterLevelCO;
import com.wanniu.game.data.EventCO;
import com.wanniu.game.data.GameData;
import com.wanniu.game.data.base.MapBase;
import com.wanniu.game.equip.EquipManager;
//import com.wanniu.game.farm.FarmCenter;
import com.wanniu.game.farm.FarmMgr;
import com.wanniu.game.fashion.FashionManager;
import com.wanniu.game.fightLevel.FightLevelManager;
import com.wanniu.game.five2Five.Five2FiveManager;
import com.wanniu.game.flee.FleeManager;
import com.wanniu.game.friend.ChouRenManager;
import com.wanniu.game.friend.FriendManager;
import com.wanniu.game.friend.FriendsCenter;
import com.wanniu.game.functionOpen.FunctionOpenManager;
import com.wanniu.game.guild.GuildManager;
import com.wanniu.game.guild.dao.FindPlayerGuildDao;
import com.wanniu.game.guild.guildBoss.GuildBossAreaHurtRankManager;
import com.wanniu.game.guild.guildBoss.GuildBossManager;
import com.wanniu.game.guild.guildFort.GuildFortManager;
import com.wanniu.game.hookSet.HookSetManager;
import com.wanniu.game.illusion.IllusionManager;
import com.wanniu.game.interact.PlayerInteract;
import com.wanniu.game.intergalmall.IntergalMallManager;
import com.wanniu.game.item.ItemUtil;
import com.wanniu.game.leaderBoard.LeaderBoardManager;
import com.wanniu.game.mail.MailCenter;
import com.wanniu.game.mail.MailManager;
import com.wanniu.game.message.MessageData;
import com.wanniu.game.message.PlayerMessages;
import com.wanniu.game.money.MoneyManager;
import com.wanniu.game.mount.MountCenter;
import com.wanniu.game.mount.MountManager;
import com.wanniu.game.onlineGift.OnlineGiftManager;
import com.wanniu.game.petNew.PetCenter;
import com.wanniu.game.petNew.PetManager;
import com.wanniu.game.petNew.PetNew;
import com.wanniu.game.player.po.AllBlobPO;
import com.wanniu.game.player.po.AvatarObj;
import com.wanniu.game.playerData.PlayerBaseDataManager;
import com.wanniu.game.playerData.PlayerBtlDataManager;
import com.wanniu.game.playerSkill.SkillManager;
import com.wanniu.game.playerSkillKey.SkillKeyManager;
import com.wanniu.game.poes.ActivityDataPO;
import com.wanniu.game.poes.AttendancePO;
import com.wanniu.game.poes.BagsPO;
import com.wanniu.game.poes.DailyActivityPO;
import com.wanniu.game.poes.FightLevelsPO;
import com.wanniu.game.poes.FunctionOpenPO;
import com.wanniu.game.poes.GuildMemberPO;
import com.wanniu.game.poes.IllusionPO;
import com.wanniu.game.poes.MonsterDropPO;
import com.wanniu.game.poes.MountPO;
import com.wanniu.game.poes.OnlineDataPO;
import com.wanniu.game.poes.PlayerAttachPO;
import com.wanniu.game.poes.PlayerBasePO;
import com.wanniu.game.poes.PlayerConsignmentItemsPO;
import com.wanniu.game.poes.PlayerPKDataPO;
import com.wanniu.game.poes.PlayerPO;
import com.wanniu.game.poes.PlayerPetsNewPO;
import com.wanniu.game.poes.PlayerTempPO;
import com.wanniu.game.poes.ShopMallPO;
import com.wanniu.game.poes.SkillsPO;
import com.wanniu.game.poes.TitlePO;
import com.wanniu.game.prepaid.PrepaidManager;
import com.wanniu.game.rank.RankManager;
import com.wanniu.game.rank.RankType;
import com.wanniu.game.rank.TitleManager;
import com.wanniu.game.recent.RecentChatCenter;
import com.wanniu.game.recent.RecentChatMgr;
import com.wanniu.game.request.task.TaskMessages;
import com.wanniu.game.revelry.RevelryManager;
import com.wanniu.game.rich.RichManager;
import com.wanniu.game.sale.SaleManager;
import com.wanniu.game.sevengoal.SevenGoalManager;
import com.wanniu.game.sevengoal.SevenGoalManager.SevenGoalTaskType;
import com.wanniu.game.shopMall.ShopMallManager;
import com.wanniu.game.solo.SoloManager;
import com.wanniu.game.sysSet.SysSetInfo;
import com.wanniu.game.task.PlayerTasks;
import com.wanniu.game.task.TaskEvent;
import com.wanniu.game.task.TaskQueue;
import com.wanniu.game.team.TeamData;
import com.wanniu.game.team.TeamData.TeamMemberData;
import com.wanniu.game.team.TeamManager;
import com.wanniu.game.team.TeamService;
import com.wanniu.game.util.RobotUtil;
import com.wanniu.game.vip.VipManager;
import com.wanniu.game.xianyuan.XianYuanService;
import com.wanniu.redis.GameDao;
import com.wanniu.redis.GlobalDao;
import com.wanniu.redis.PlayerPOManager;

import Pomelo.ZoneManagerPrx;
import Xmds.GetPlayerData;
import Xmds.RefreshPlayerPropertyChange;
import Xmds.SkillDataICE;
import Xmds.XmdsManagerPrx;
import pomelo.Common.KeyValueStruct;
import pomelo.Common.PropertyStruct;
import pomelo.area.FunctionHandler.FunctionGoToPush;
import pomelo.area.FunctionHandler.TipsParam;
import pomelo.area.PlayerHandler.ClientConfigPush;
import pomelo.area.PlayerHandler.KickPlayerPush;
import pomelo.area.PlayerHandler.SuperScriptPush;
import pomelo.area.PlayerHandler.SuperScriptType;
import pomelo.item.ItemOuterClass.MiniItem;
import pomelo.player.PlayerOuterClass.LookUpPlayer;

/**
 * 玩家数据对象
 * 
 * @author Yangzz
 *
 */
public class WNPlayer extends GPlayer {

	public Map<ManagerType, ModuleManager> allManagers = new HashMap<>();

	public AllBlobPO allBlobData;

	public PlayerPO player;

	/** 玩家扩展数据 */
	public PlayerBasePO playerBasePO;
	/** 玩家附属数据 */
	public PlayerAttachPO playerAttachPO;

	public PlayerTempPO playerTempData;

	/** 职业信息 */
	public CharacterCO basicProp;

	/** 玩家背包对象 */
	public WNBag bag;

	/** 出售物品回收站 */
	public WNBag recycle;

	/** 仓库 */
	public WNBag wareHouse;

	public PlayerBaseDataManager baseDataManager;

	public PlayerBtlDataManager btlDataManager;

	/** 玩家装备背包 */
	public EquipManager equipManager;

	public FashionManager fashionManager;
	
	public BloodManager bloodManager;

	/** 玩家在线礼物对象 */
	public OnlineGiftManager onlineGiftManager;

	public MailManager mailManager;

	public FightLevelManager fightLevelManager;

	public DropManager dropManager;

	public SysSetInfo sysSetManager;

	public VipManager vipManager;
	public PkRuleManager pkRuleManager;
	public BufferManager bufferManager;
	public SceneProgressManager sceneProgressManager;
	public PlayerAttendance playerAttendance;
	public ShopMallManager shopMallManager;
	public IntergalMallManager intergalMallManager;

	/** 工会BOSS伤害管理排行榜 */
	public GuildBossAreaHurtRankManager guildBossAreaHurtRankManager;
	/** 成就管理对象 */
	public AchievementManager achievementManager;
	/** 竞技场管理对象 */
	public ArenaManager arenaManager;
	/** 工会BOSS管理对象 */
	public GuildBossManager guildBossManager;
	
	/** 工会据点管理对象 */
	public GuildFortManager guildFortManager;
	/** 单挑王管理对象 */
	public SoloManager soloManager;
	/** 世界等级/膜拜 */
	public LeaderBoardManager leaderBoardManager;
	/** 5v5 */
	public Five2FiveManager five2FiveManager;
	public AuctionManager auctionManager;
	public HookSetManager hookSetManager;
	public TitleManager titleManager;
	public MountManager mountManager;
	public FunctionOpenManager functionOpenManager;
	public PlayerTasks taskManager;
	public PlayerMessages messageManager;

	public PetManager petNewManager;
	public SkillManager skillManager;
	public SkillKeyManager skillKeyManager;
	public DailyActivityMgr dailyActivityMgr;
	public FarmMgr farmMgr; // 果园

	public ConsignmentManager consignmentManager;
	public SaleManager saleManager;
	public PrepaidManager prepaidManager;
	public ActivityManager activityManager;
	// public PayGiftManager payGiftManager;
	public DemonTowerManager demonTowerManager;
	public FriendManager friendManager;
	public ChouRenManager chouRenManager;
	public IllusionManager illusionManager;

	public GuildManager guildManager;

	public TeamManager teamManager;

	public FleeManager fleeManager;
	public MoneyManager moneyManager;
	public RankManager rankManager;
	
	public RichManager richManager;
	public SevenGoalManager sevenGoalManager;

	// public int demonTowerCount;
	// public int demonTowerLevel=1;
	// public int demonTowerSweepCountLeft=1;
	// public boolean demonTowerIsSweeping=false;

	/*********************************************************************/
	public int bornType = Const.BORN_TYPE.NORMAL.value;
	public int enterState = Const.ENTER_STATE.online.value;

	public Map<Integer, Long> chatTime;

	public Area area;
	private int force;// 阵营
	/** 每日是否登陆过 */
	private boolean todayLogined;

	/*********************************************************************/

	public WNPlayer(AllBlobPO allBlobData) {
		this._init(allBlobData);
	}

	public WNPlayer(PlayerPO player) {
		this.player = player;
	}

	public WNPlayer() {

	}

	public void receive(String route, GeneratedMessage msg) {
		write(new MessagePush(route, msg));
	}

	public HookSetManager getHookSet() {
		if (hookSetManager == null) {
			hookSetManager = new HookSetManager(this, this.allBlobData.hookSetData);
		}
		return hookSetManager;
	}

	@Override
	public String getId() {
		return player.id;
	}

	public int getLogicServerId() {
		return player.logicServerId;
	}

	public int getAcrossServerId() {
		return GWorld.__ACROSS_SERVER_ID;
	}

	public String getInstanceId() {
		return area.instanceId;
	}

	public int getAreaId() {
		return area.areaId;
	}

	public int getLineIndex() {
		return area.lineIndex;
	}

	public int getSceneType() {
		return area.sceneType;
	}

	public String getSceneName() {
		return area.getSceneName();
	}

	@Override
	public String getUid() {
		return player.uid;
	}

	@Override
	public String getName() {
		return player.name;
	}

	public int getLevel() {
		return player.level;
	}

	public long getExp() {
		return player.exp;
	}

	public int getPro() {
		return player.pro;
	}

	public int getUpLevel() {
		return player.upLevel;
	}

	public int getUpOrder() {
		return player.upOrder;
	}

	public int getFightPower() {
		return player.fightPower;
	}

	public PlayerPO getPlayer() {
		return player;
	}

	public PlayerBasePO getPlayerAttach() {
		return playerBasePO;
	}

	public PlayerTempPO getPlayerTempData() {
		return playerTempData;
	}

	public void setPlayerTempData(PlayerTempPO playerTempData) {
		this.playerTempData = playerTempData;
	}

	public WNBag getWnBag() {
		return bag;
	}

	public void setWnBag(WNBag wnBag) {
		this.bag = wnBag;
	}

	public PlayerTasks getPlayerTasks() {
		return taskManager;
	}

	public void setPlayerTasks(PlayerTasks playerTasks) {
		this.taskManager = playerTasks;
	}

	public FriendManager getFriendManager() {
		return friendManager;
	}

	public FarmMgr getFarmMgr() {
		return farmMgr;
	}

	public RecentChatMgr getRecentChatMgr() {
		return RecentChatCenter.getInstance().getRecentChatMgr(getId());
	}

	public void setFriendManager(FriendManager friendManager) {
		this.friendManager = friendManager;
	}

	public ChouRenManager getChouRenManager() {
		return chouRenManager;
	}

	public void setChouRenManager(ChouRenManager chouRenManager) {
		this.chouRenManager = chouRenManager;
	}

	public TeamManager getTeamManager() {
		return teamManager;
	}

	public void setTeamManager(TeamManager teamManager) {
		this.teamManager = teamManager;
	}

	public IntergalMallManager getIntergalMallManager() {
		if (this.intergalMallManager == null) {
			this.intergalMallManager = new IntergalMallManager(this);
		}
		return this.intergalMallManager;
	}

	@Override
	public void sync() {
		update();
		PlayerPOManager.sync(this.getId());
	}

	public Date getLogoutTime() {
		return this.player.logoutTime;
	}

	public void setLogoutTime(Date date) {
		baseDataManager.setLogoutTime(date);
	}

	@Override
	public void onLogout(boolean self) {
		Out.debug("---------------------logout begin----------------------- uid : ", getUid());
		long startTime = System.nanoTime();

		Area area = getArea();
		if (area != null) {
			try {
				if (area.isNormal() && area.hasPlayer(getId())) {
					GetPlayerData result = area.getPlayerData(getId());
					if (result != null) {
						syncNowData(area.areaId, area.instanceId, result);
					}
				}
			} catch (Exception e) {
				Out.error(e);
			} finally {
				try {
					recycle.removeAllItems();
					PlayerRemote.syncPlayerDataOffline(this, area);
					area.onPlayerLogout(this);
				} catch (Exception e) {
					Out.error(e);
				}
			}
		}

		TeamService.onLogout(this);

		GWorld.getInstance().ansycExec(() -> {
			JSONObject players = new JSONObject();
			List<String> list_ids = PlayerDao.getPlayerIdsByUid(getUid(), getLogicServerId());
			for (String playerId : list_ids) {
				PlayerPO baseData = PlayerPOManager.findPO(ConstsTR.playerTR, playerId, PlayerPO.class);
				if (baseData != null) {
					JSONObject player = new JSONObject();
					player.put("lv", baseData.level);
					player.put("name", baseData.name);
					player.put("pro", baseData.pro);
					player.put("time", baseData.logoutTime.getTime());
					players.put(playerId, player);
				}
			}
			GlobalDao.hset(String.valueOf(getLogicServerId()), getUid(), players.toJSONString());

			if (players.size() != list_ids.size()) {
				list_ids.clear();
				for (String playerId : players.keySet()) {
					list_ids.add(playerId);
				}
				PlayerDao.updatePlayerIds(getUid(), getLogicServerId(), list_ids);
			}

			PlayerUtil.addLoginServer(getUid(), getLogicServerId(), players.size());

		});

		float useTime = (System.nanoTime() - startTime) / 100_0000F;
		Out.info("角色退出游戏 uid=", this.getUid(), ",playerId=", this.getId(), ",name=", this.getName(), ",useTime=", useTime, " ms");
		BILogService.getInstance().ansycReportLogout(this);
	}

	/**
	 * 客户端资源加载完成通知 给客户端推送的数据要在这里
	 */
	public void onReady() {
		this.functionOpenManager.checkFunctoinAward();
	}

	/**
	 * 角色登录成功，加载完所有数据后，将角色相关的信息主动推送到客户端
	 * 
	 * @param player
	 */
	public void onLogin() {
		// TODO 这里加载玩家数据，有些模块可能有依赖，注意加载顺序

		this.baseDataManager.setLoginTime(new Date());

		// 推送数据
		WNNotifyManager.getInstance().pushPlayerBattleData(this);

		// 5点重置数据
		this.refreshNewDay();

		//////////// 登陆刷新数据////////////
		// 更新功能开放
		this.functionOpenManager.init();
		// 刷新 升级 等 成就
		this.achievementManager.playerLevelChange(this.player.level);

		this.teamManager.loginFlag = this.teamManager.isInTeam();
		this.onlineGiftManager.onLogin();

		// 上线如果没有队伍，移除一条龙任务
		this.taskManager.onLogin();

		//
		this.activityManager.onLogin();

		this.dailyActivityMgr.onLogin();

		this.shopMallManager.onLogin();

		// 仙盟模块登录
		this.guildManager.onLogin();

		this.equipManager.onLogin();

		for (ModuleManager manager : allManagers.values()) {
			manager.onPlayerEvent(PlayerEventType.AFTER_LOGIN);
		}

		// 每日首次登陆推送积分商店红点
		if (!todayLogined) {
			todayLogined = true;
			this.updateSuperScript(SUPERSCRIPT_TYPE.INTERGAL_MALL, 1);
		}

		// 冲榜
		RevelryManager.getInstance().onLogin(this);
		
		// 连续充值活动
		RechargeActivityService.getInstance().onLogin(this);
	}

	private String battleServerId = GWorld.__CS_NODE.getNodeId();

	public void setBattleServerId(String battleServerId) {
		if (!this.battleServerId.equals(battleServerId)) {
			this.battleServerId = battleServerId;
			bindBattleServer(battleServerId);
		}
	}

	@Override
	public String getBattleServerId() {
		return battleServerId;
	}

	public JSONObject toJSON4EnterScene(Area area) {
		if (!AreaUtil.canRideMount(this.area.areaId)) {
			this.mountManager.unMountData();
		}
		JSONObject data = new JSONObject();
		data.put("effects", this._getBattlerServerEffect());
		data.put("skills", this.skillManager.toJson4BattleServer());
		data.put("tasks", this._getBattleServerTask());
		data.put("flags", this._getBattleServerTaskFlag());
		data.put("playerEntered", area.hasPlayerEntered);
		data.put("avatars", PlayerUtil.getBattlerServerAvatarObj(this));
		data.put("basic", this._getBattlerServerBasic());
		data.put("connectServerId", this.getBattleServerId());
		data.put("uid", this.getId()/* TODO this.getUid() */);
		data.put("unitTemplateID", this.player.pro);
		data.put("force", this.force);
		data.put("robot", isRobot());
		if (// (this.getSceneType() != Const.SCENE_TYPE.NORMAL.getValue()) &&
		this.enterState == Const.ENTER_STATE.changeArea.value) {
			this.recoverHPAndMP();
		}
		Map<String, Number> tempData = new HashMap<>();
		if (this.playerTempData.hp == 0) {
			float x = 0, y = 0, direction = 0;
			if (area.areaId == playerTempData.historyAreaId) {
				x = playerTempData.historyX;
				y = playerTempData.historyY;
				direction = playerTempData.historyDirection;
			}
			tempData.put("x", x);
			tempData.put("y", y);
			tempData.put("direction", direction);
			tempData.put("hp", this.btlDataManager.finalInflus.get(Const.PlayerBtlData.MaxHP));
			tempData.put("mp", this.playerTempData.mp);
		} else {
			tempData.put("x", this.playerTempData.x);
			tempData.put("y", this.playerTempData.y);
			tempData.put("direction", this.playerTempData.direction);
			tempData.put("hp", this.playerTempData.hp);
			tempData.put("mp", this.playerTempData.mp);
		}
		data.put("tempData", tempData);

		data.put("pkInfo", this.pkRuleManager.getPkDataToBattleJson());
		Map<String, Object> petBase = this.petNewManager.getBattlerServerPetBase();
		data.put("petBase", petBase);
		if (petBase.get("Model").equals("")) {
			data.put("addTestPetData", 0);
		} else {
			data.put("addTestPetData", 1);
			data.put("petEffect", this.petNewManager.getBattlerServerPetEffect());
			data.put("petSkill", this.petNewManager.getBattlerServerPetSkill());
			data.put("petMode", this.petNewManager.getPkDataToBattleJson());
		}
		Map<String, Integer> sceneData = new HashMap<String, Integer>();
		sceneData.put("allowAutoGuard", 0);
		MapBase sceneProp = AreaDataConfig.getInstance().get(area.areaId);
		int autoFight = sceneProp.autoFight;
		if (autoFight == 0) {
			sceneData.put("allowAutoGuard", 1);
		} else if (autoFight == 1 || autoFight == 3) {
			sceneData.put("allowAutoGuard", 0);
		} else if (autoFight == 2) {
			if ((area.getSceneType() == Const.SCENE_TYPE.FIGHT_LEVEL.getValue() || area.getSceneType() == Const.SCENE_TYPE.LOOP.getValue()) && area.prop.dungeonTab != 4 // 秘境
			// && !this.fightLevelManager.isFinishedFightLevel(area.areaId)
			) {
				sceneData.put("allowAutoGuard", 1);
			}
		}

		data.put("sceneData", sceneData);
		Out.debug("enterscenedata:", data);
		return data;
	}

	private void registManager(ModuleManager manager) {
		if (manager.getManagerType() == null) {
			Out.error("manager.getManagerType()为空");
		}
		allManagers.put(manager.getManagerType(), manager);
	}

	///////////////////////////////////////////////////////////////////////////////////////////////////////

	// /**
	// * init
	// *
	// * @private
	// */
	private void _init(AllBlobPO allBlobData) {
		this.allBlobData = allBlobData;
		this.player = allBlobData.player;
		this.basicProp = GameData.Characters.get(this.player.pro);

		this.playerBasePO = allBlobData.playerBase;
		this.playerTempData = allBlobData.playerTemp;
		this.playerAttachPO = allBlobData.playerAttachPO;

		this.baseDataManager = new PlayerBaseDataManager(this, player);
		registManager(baseDataManager);

		SkillsPO skillDb = PlayerPOManager.findPO(ConstsTR.skillTR, getId(), SkillsPO.class);
		this.skillManager = new SkillManager(this, skillDb);
		registManager(skillManager);
		this.skillKeyManager = new SkillKeyManager(this, skillDb);
		registManager(skillKeyManager);
		this.sysSetManager = new SysSetInfo(this);
		this.hookSetManager = new HookSetManager(this, allBlobData.hookSetData);
		this.vipManager = new VipManager(this, playerAttachPO.vipData);
		registManager(vipManager);

		this.messageManager = new PlayerMessages(this);
		this.sceneProgressManager = new SceneProgressManager(playerAttachPO.sceneProgress);
		this.chouRenManager = new ChouRenManager(this, allBlobData.chouRens);

		this.achievementManager = new AchievementManager(this, allBlobData.achievements);
		this.guildBossAreaHurtRankManager = new GuildBossAreaHurtRankManager(this);
		TitlePO titlePo = PlayerPOManager.findPO(ConstsTR.playerTitleTR, getId(), TitlePO.class);// FindRankDao.getRankByPlayerId(this.getId());
		this.titleManager = new TitleManager(this, titlePo);
		PlayerPetsNewPO petsPo = PetCenter.getInstance().findPet(getId());
		this.petNewManager = new PetManager(this, petsPo);
		registManager(petNewManager);

		this.soloManager = new SoloManager(this);
		this.guildManager = new GuildManager(this, FindPlayerGuildDao.getPlayerGuildPOById(player.id));
		this.saleManager = new SaleManager(this);
		this.arenaManager = new ArenaManager(this);
		this.guildBossManager = new GuildBossManager(this);
		this.guildFortManager = new GuildFortManager(this);
		registManager(guildBossManager);
		this.leaderBoardManager = new LeaderBoardManager(this);
		// 创建背包对象
		BagsPO bags = PlayerPOManager.findPO(ConstsTR.bagTR, this.getId(), BagsPO.class);
		bag = new WNBag(this, Const.BAG_TYPE.BAG, bags.bagData, bags);
		this.recycle = new WNBag(this, Const.BAG_TYPE.RECYCLE, bags.recycleData, bags);
		this.wareHouse = new WNBag(this, Const.BAG_TYPE.WAREHOUSE, bags.wareHouseData, bags);

		// 创建装备背包对象
		if (this.getPlayerAttach().equipGrids == null) {
			this.getPlayerAttach().equipGrids = new HashMap<>();
		}
		this.equipManager = new EquipManager(this, playerBasePO.equipGrids, playerBasePO.strengthPos);

		this.fashionManager = new FashionManager(this);
		registManager(this.fashionManager);
		
		this.bloodManager = new BloodManager(this);
		registManager(this.bloodManager);

		FunctionOpenPO functionOpen = PlayerPOManager.findPO(ConstsTR.player_func_openTR, this.getId(), FunctionOpenPO.class);// FindFuncOpenDao.getFunctionOpenDBByPlayerId(this.getId());
		this.functionOpenManager = new FunctionOpenManager(this, functionOpen);

		this.pkRuleManager = new PkRuleManager(this, PlayerPOManager.findPO(ConstsTR.pkRuleTR, this.getId(), PlayerPKDataPO.class));

		this.dailyActivityMgr = new DailyActivityMgr(this.player.id, PlayerPOManager.findPO(ConstsTR.player_dailyTR, this.player.id, DailyActivityPO.class));

		this.bufferManager = new BufferManager(this);

		this.mailManager = MailCenter.getInstance().findPlayerMails(getId());
		registManager(mailManager);
		AttendancePO attendanceDb = PlayerPOManager.findPO(ConstsTR.player_signTR, this.getId(), AttendancePO.class);
		this.playerAttendance = new PlayerAttendance(this, attendanceDb);

		ShopMallPO shopMallDB = PlayerPOManager.findPO(ConstsTR.shopMallTR, this.getId(), ShopMallPO.class);
		this.shopMallManager = new ShopMallManager(this, shopMallDB);

		MountPO mountPo = player.openMount ? MountCenter.getInstance().findMount(getId()) : null;
		mountManager = new MountManager(this, mountPo);
		registManager(mountManager);

		PlayerConsignmentItemsPO consignmentDB = PlayerPOManager.findPO(ConstsTR.player_consignmentTR, getId(), PlayerConsignmentItemsPO.class);
		this.consignmentManager = new ConsignmentManager(this, consignmentDB);

		this.teamManager = new TeamManager(this);
		// 任务必须放到组队后面
		this.setPlayerTasks(new PlayerTasks(this, allBlobData.tasks));

		this.prepaidManager = new PrepaidManager(getId());
		registManager(prepaidManager);

		ActivityDataPO adp = PlayerPOManager.findPO(ConstsTR.activityTR, getPlayer().id, ActivityDataPO.class);
		this.activityManager = new ActivityManager(this, adp);
		registManager(this.activityManager);

		auctionManager = new AuctionManager(this);
		registManager(auctionManager);

		this.demonTowerManager = new DemonTowerManager(this);

		// 在线礼包，在 activityManager 之后调用
		OnlineDataPO onlineData = PlayerPOManager.findPO(ConstsTR.onlineGiftTR, getId(), OnlineDataPO.class);
		this.onlineGiftManager = new OnlineGiftManager(this, onlineData);

		this.fightLevelManager = new FightLevelManager(this, PlayerPOManager.findPO(ConstsTR.player_fightlevelTR, getId(), FightLevelsPO.class));// FindFightLevelDao.getDataById(this.getId())

		this.dropManager = new DropManager(this, PlayerPOManager.findPO(ConstsTR.monster_drop_infoTR, getId(), MonsterDropPO.class));

		this.friendManager = FriendsCenter.getInstance().getFriendsMgr(getId());
		this.illusionManager = new IllusionManager(this, GameDao.get(player.id, ConstsTR.player_illusion, IllusionPO.class));
		this.five2FiveManager = new Five2FiveManager(this);
		registManager(five2FiveManager);

		btlDataManager = new PlayerBtlDataManager(this);
		registManager(btlDataManager);

		fleeManager = new FleeManager(this);
		this.moneyManager = new MoneyManager(this);

		// 果园 必须放到friendManager之后
		// this.farmMgr = new
		// FarmMgr(this.player.id,PlayerPOManager.findPO(ConstsTR.player_farmTR,
		// this.getId(),
		// FarmPO.class));
		this.farmMgr = new FarmMgr(this);

		calFightPower();

		// 这个初始化要放在计算战斗与属性后面...
		this.rankManager = new RankManager(this);
		
		this.richManager = new RichManager(this);
		registManager(this.richManager);
		
		this.sevenGoalManager = new SevenGoalManager(this);
		registManager(this.sevenGoalManager);
		this.init();
	};

	private void init() {
		demonTowerManager.init();
		
		this.richManager.init();
		
		this.dailyActivityMgr.init(richManager,sevenGoalManager);
		
		this.petNewManager.init();
	}

	public void refreshNewDay() {
		Date now = new Date();
		if (DateUtils.isSameDay(DateUtils.addHours(now, -5), DateUtils.addHours(player.refreshTime, -5))) {
			return;
		}

		Out.info("refreshNewDay playerId:", this.getId(), ",", player.name);
		this.player.refreshTime = now;

		this.todayLogined = false;
		// this.demonTowerCount = 0;
		// 清空每日 拾取超时 已发送邮件的数量
		this.playerTempData.sendMailItemNum = 0;
		this.moneyManager.refreshNewDay();

		// 任务刷新
		this.taskManager.refreshNewDay();
		// 签到系统刷新 TODO
		this.playerAttendance.refreshNewDay();
		// 重置在线礼包
		this.onlineGiftManager.refreshNewDay();
		// 每日仇人悬赏刷新
		this.chouRenManager.refreshNewDay();
		// //称号系统
		this.titleManager.refreshNewDay();
		this.messageManager.refreshNewDay();
		// this.payGiftManager.refreshNewDay();
		//
		// this.friendManager.refreshNewDay();
		// 竞技场
		this.arenaManager.refreshNewDay();
		// 单挑问道大会系统
		this.soloManager.refreshNewDay();
		this.shopMallManager.refreshNewDay();
		// this.limitTimeActivityManager.refreshNewDay();

		// 重置幻境
		this.illusionManager.refreshNewDay();

		// 活动(必需在重置日常活动前面，需要里面的数据做进度)
		this.activityManager.refreshNewDay();
		// 重置日常活动
		this.dailyActivityMgr.refreshNewDay();

		// 重置积分商城
		if (this.intergalMallManager != null) {
			this.intergalMallManager.refreshNewDay();
		}

		// // 仙盟刷新
		this.guildManager.refreshNewDay();

		// 寄卖行
		this.consignmentManager.refreshNewDay();

		this.fightLevelManager.refreshNewDay();

		// 怪物击杀次数刷新
		this.dropManager.refreshNewDay();

		// 果园每日刷新
		this.farmMgr.refreshNewDay();

		// 镇妖塔扫荡
		demonTowerManager.UpdateSweepCount();
		demonTowerManager.refreshNewDay();
		

		if (this.reliveManager != null)
			this.reliveManager.refreshNewDay();

		for (ModuleManager manager : this.allManagers.values()) {
			manager.onPlayerEvent(PlayerEventType.REFRESH_NEWDAY);
		}

		XianYuanService.getInstance().refreshNewDay(allBlobData.xianYuan);

		playerAttachPO.KillBossCount = 0;
//		playerAttachPO.fetchRedPacketCount = 0;
		
		equipManager.refreshNewDay();
		
		richManager.checkData();
	};

	/**
	 * bornData相关初始化
	 */
	public void initBornData() {
		if (this.bornType == Const.BORN_TYPE.HISTORY.value) {
			this.playerTempData.areaId = this.playerTempData.historyAreaId;
			this.playerTempData.x = this.playerTempData.historyX;
			this.playerTempData.y = this.playerTempData.historyY;
		} else if (this.bornType == Const.BORN_TYPE.BORN.value) {
			this.playerTempData.areaId = this.playerTempData.bornAreaId;
			this.playerTempData.x = this.playerTempData.bornX;
			this.playerTempData.y = this.playerTempData.bornY;
		}
	};

	/**
	 * 初始化角色属性
	 */
	public void initAndCalAllInflu(Collection<String> allyIds) {
		this.initBornData();
		CharacterCO basicData = GameData.Characters.get(this.player.pro);
		if (basicData == null) {
			String msg = "there is no data of pro: " + this.player.pro + " in characterProps ";
			throw new RuntimeException(msg);
		}

		this.basicProp = basicData;

		CharacterLevelCO levelData = GameData.CharacterLevels.get(this.player.level);
		if (levelData == null) {
			Out.error("there is no data of level: ", this.player.level, " in characterLevelProps ");
		}

		this.player.needExp = levelData.experience;

		this.calFightPower();
	};

	public boolean setClientCustomConfig(String key, String value, boolean push) {
		if (this.playerAttachPO.config == null) {
			this.playerAttachPO.config = new HashMap<>();
		}
		int len = this.playerAttachPO.config.size();
		// 限制
		if (len >= 300) {
			return false;
		}
		if (!StringUtil.isEmpty(value)) {
			this.playerAttachPO.config.put(key, value);
		} else {
			this.playerAttachPO.config.remove(key);
		}
		if (push) {
			ClientConfigPush.Builder data = ClientConfigPush.newBuilder();
			data.setS2CKey(key);
			data.setS2CValue(value);
			receive("area.playerPush.clientConfigPush", data.build());
		}
		return true;
	};

	/**
	 * 计算玩家战力
	 */
	public void calFightPower() {
		int old = this.player.fightPower;
		this.player.fightPower = CommonUtil.calPlayerFightPower(this.btlDataManager.fightPowerInflus, player.pro);
		// this.player.fightPower +=
		// this.equipManager.getSameAttsExtFightPower();//加上穿着装备所有扩展相同属性额外加成的战力
		this.player.fightPower += skillManager.getSkillsPower();

		if (this.player.fightPower > this.player.maxFightPower) {
			this.player.maxFightPower = this.player.fightPower;
			Out.info("战力提升 playerId=", getId(), ",name=", getName(), ", fightPower=", player.fightPower);
		}
		
		if (rankManager != null) {
			rankManager.onEvent(RankType.FIGHTPOWER, player.fightPower);
		}
		if (sevenGoalManager != null) {
			sevenGoalManager.processGoal(SevenGoalTaskType.FIGHTPOWER_TO, player.fightPower);
		}
		
		this.activityManager.updateDeskRedPoint();// 战力红点
		if (old < this.player.fightPower) {
			// BI
			BILogService.getInstance().recordNum(this, Const.BiLogType.FightPower, this.player.fightPower - old, null);
		}
		this.achievementManager.playerPowerChange(this.player.fightPower);
		this.baseDataManager.updateFightTime(new Date());
	};

	/**
	 * 获取角色基本信息
	 */
	public Map<String, Object> _getBattlerServerBasic() {
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("name", this.getName());
		data.put("camp", Const.AreaCamp.Neutral.value);
		data.put("guildId", this.guildManager.getGuildId());
		data.put("guildName", this.guildManager.getGuildName());
		data.put("guildIcon", this.guildManager.getGuildIcon());
		data.put("pro", this.player.pro);
		data.put("serverId", this.getLogicServerId());
		data.put("titleId", this.titleManager.getTitleId());
		data.put("level", this.player.level);
		data.put("vip", this.player.vip);
		data.put("upLevel", this.player.upLevel);
		data.put("beReward", 0); // 无悬赏
		return data;
	};

	/**
	 * 获取角色任务数据
	 * 
	 * @private
	 */
	public JSONArray _getBattleServerTask() {
		return this.taskManager.toJson4BattleServer();
	};

	public List<Object[]> _getBattleServerTaskFlag() {
		return this.sceneProgressManager.toJson4BattleServer();
	};

	/**
	 * 获取战斗服effect数据
	 */
	public Map<String, Number> _getBattlerServerEffect() {
		return btlDataManager._getBattlerServerEffect();
	};

	/**
	 * 刷新战斗服基本属性
	 */
	public void refreshBattlerServerBasic() {
		String basic = JSON.toJSONString(this._getBattlerServerBasic());
		getXmdsManager().refreshPlayerBasicData(getId(), basic);
	};

	/**
	 * 刷新战斗服背包剩余数量属性
	 */
	public void refreshPlayerRemainBagCountData(int remainCount) {
		getXmdsManager().refreshPlayerRemainBagCountData(this.getId(), remainCount);
	}

	/**
	 * 刷新战斗服队伍背包剩余数量属性
	 */
	public void refreshPlayerRemainTeamBagCountData(int remainCount) {
		if (remainCount <= 3) {
			getXmdsManager().refreshPlayerRemainTeamBagCountData(this.getId(), remainCount);
		}
	};

	/**
	 * 刷新战斗服属性影响值
	 */
	public void refreshBattlerServerEffect(boolean isHpMpValid) {
		Map<String, Number> effects = this._getBattlerServerEffect();
		if (!isHpMpValid) {
			effects.put("HP", -1);
			effects.put("MP", -1);
		}
		String str = JSON.toJSONString(effects);
		getXmdsManager().refreshPlayerBattleEffect(this.getId(), str);
	};

	/**
	 * 刷新战斗服技能信息
	 */
	public void refreshBattlerServerSkill(int type, List<Integer> skillIds) {
		if (!area.hasPlayer(getId()))
			return;
		String _skillData = getXmdsManager().getPlayerSkillCDTime(this.getId());
		List<SkillDataICE> skillData = JSON.parseObject(_skillData, new TypeReference<List<SkillDataICE>>() {});
		this.skillManager.syncBattleSkillTime(skillData);
		List<com.wanniu.game.playerSkill.SkillManager.SkillInfo> data = null;
		if (type == Const.SkillHandleType.ALL.getValue()) {
			data = this.skillManager.toJson4BattleServer();
		} else if (type == Const.SkillHandleType.CHANGE.getValue() || type == Const.SkillHandleType.ADD.getValue() || type == Const.SkillHandleType.DELETE.getValue()) {
			data = this.skillManager.toJson4UpdateBattleServer(type, skillIds);
		}
		if (data != null && data.size() > 0) {
			getXmdsManager().refreshPlayerSkill(this.getId(), type, JSON.toJSONString(data));
		} else {
			Out.debug("send to battle server skill change data is null,it is not influence");
		}
	};

	/**
	 * 刷新战斗服avatar信息
	 */
	public void refreshBattlerServerAvatar() {
		List<AvatarObj> list = PlayerUtil.getBattlerServerAvatarObj(this);
		Map<String, List<AvatarObj>> avatars = new HashMap<>();
		avatars.put("avatars", list);
		String avatars_str = JSON.toJSONString(avatars);
		Out.debug("refreshBattlerServerAvatar:", avatars_str);
		getXmdsManager().refreshPlayerAvatar(this.getId(), avatars_str);
	};

	/**
	 * 角色关联属性变更 传入参数 playerId :角色id changeType : 0:hp, 1:mp, 2:hpAndMp, 3:npc
	 * valueType : 0:value 1:percent value : num(int) itemCode : itemCode itemNum :
	 * itemNum
	 */
	public void refreshPlayerPropertyChange(RefreshPlayerPropertyChange data) {
		Out.debug("refreshPlayerPropertyChange :", data);
		getXmdsManager().refreshPlayerPropertyChange(this.getId(), JSON.toJSONString(data));
	};

	public void refreshPlayerPetPropertyChange(RefreshPlayerPropertyChange data) {
		Out.debug("refreshPlayerPetPropertyChange :", data);
		getXmdsManager().refreshPlayerPetPropertyChange(this.getId(), JSON.toJSONString(data));
	};

	public void changePlayerPkValue(int value) {
		getXmdsManager().refreshPlayerPKValue(this.getId(), value);
	};

	/**
	 * 设置阵营
	 */
	public void setForce(int force) {
		this.force = force;
	}

	public int getForce() {
		return this.force;
	}

	/**
	 * 同步场景出生数据
	 */
	public void syncBornData(float bornX, float bornY, int bornAreaId) {
		this.playerTempData.bornX = bornX;
		this.playerTempData.bornY = bornY;
		this.playerTempData.bornAreaId = bornAreaId;
	};

	/**
	 * 同步场景历史数据
	 */
	public void syncHistoryData(int areaId, String instanceId, GetPlayerData data) {
		this.playerTempData.historyAreaId = areaId;
		this.playerTempData.historyX = data.x;
		this.playerTempData.historyY = data.y;
		this.playerTempData.historyDirection = data.direction;
		Out.debug(this.getName(), ">>>>>>>>>>>>>history areaId:", areaId, ">>>historyX:", data.x, ">>>historyY:", data.y);

		this.pkRuleManager.setHistoryPkModel();
	};

	/**
	 * 同步场景临时数据
	 */
	public void syncNowData(int areaId, String instanceId, GetPlayerData data) {
		this.playerTempData.x = data.x;
		this.playerTempData.y = data.y;
		this.playerTempData.direction = data.direction;
		this.playerTempData.hp = data.hp;
		this.playerTempData.mp = data.mp;
		this.playerTempData.areaId = areaId;
		// this.playerTempData.instanceId = instanceId;
		this.pkRuleManager.pkData.pkModel = data.pkMode;
		this.pkRuleManager.pkData.pkValue = data.pkValue;
		this.pkRuleManager.pkData.pkLevel = data.pkLevel;

		this.skillManager.syncBattleSkillTime(Arrays.asList(data.skillData));
	};

	private long lastChangeTime;

	/**
	 * @return 上次切场景的时间戳毫秒数
	 */
	public long getLastChangeAreaTime() {
		return lastChangeTime;
	}

	/**
	 * 设置场景信息
	 */
	public void setArea(Area area) {

		if (this.area != null && GWorld.APP_TIME - lastChangeTime < AreaUtil.MIN_CHANGE_AREA_INTERVAL_MILL) {
			Out.warn(getName(), " : ", this.area.getSceneName(), "-", this.area.instanceId, " to ", area.getSceneName(), "-", area.instanceId, " use ", GWorld.APP_TIME - lastChangeTime, "ms use ", JSON.toJSONString(this.playerTempData));

		}
		this.area = area;
		setBattleServerId(area.getServerId());
		// this.playerTempData.areaId = area.areaId;
		// this.playerTempData.instanceId = area.instanceId;
		// this.playerTempData.lineIndex = area.lineIndex;
		// this.playerTempData.sceneType = area.sceneType;
		Out.debug("set player area data: ", area);
		lastChangeTime = GWorld.APP_TIME;
	};

	/** 增加声望 */
	public void addPrestige(int prestige) {
		addPrestige(prestige, null);
	}

	/** 增加声望 */
	public void addPrestige(int prestige, Const.GOODS_CHANGE_TYPE from) {
		baseDataManager.addPrestige(prestige, from);
		// if (prestige >= 0) {
		// this.player.prestige = this.player.prestige + prestige;
		// if (this.player.prestige >= Const.NUMBER_MAX.INT) {
		// this.player.prestige = Const.NUMBER_MAX.INT;
		// }
		// if (from != Const.GOODS_CHANGE_TYPE.monsterdrop) {
		// this.customTip(Const.CUSTOMTIPTYPE.PRESTIGE, prestige);
		// }
		// this.medal.onAddPrestige();
		// this.update();
		// }
	};

	public void onUpgrade() {

		for (ModuleManager manager : allManagers.values()) {
			manager.onPlayerEvent(PlayerEventType.UPGRADE);
		}

		// 功能开放模块放到最前面，后面的模块依赖它
		this.functionOpenManager.onUpgradeLevelOrVip();

		// btlDataManager.onUpgrade();
		// TODO 后面是其他模块的升级

		equipManager.OnPlayerLevelUp();
		// skillManager.onPlayerUpgrade();

		// 自动开启天赋页
		this.skillManager.updateSuperScript();
		// 自动学习技能
		// this.skillManager.autoLearnSkill();
		this.teamManager.onPlayerUpgrade();
		// this.soloManager.updateSoloRankToRedis(); //排行只存储PlayerId
		// petNewManager.onPlayerUpgrade();
		this.achievementManager.playerLevelChange(this.player.level);
		WNNotifyManager.getInstance().levelChange(this, this.player.level);
		this.activityManager.updateDeskRedPoint();

		// btlDataManager.onPlayerUpgrade();
		calFightPower();
		// 升级后满血
		playerTempData.hp = btlDataManager.finalInflus.get(PlayerBtlData.MaxHP);
		this.pushAndRefreshEffect(true);
		this.refreshBattlerServerBasic();
		this.pushDynamicData(Utils.ofMap("level", this.player.level, "exp", this.player.exp, "needExp", this.player.needExp, "fightPower", this.player.fightPower));

		// 刷新穿装备红点
		this.equipManager.updateEquipScript(null);
		// 刷新强化红点
		this.equipManager.updateStrengthScript(null);
		// 刷新打造红点
		this.equipManager.updateMakeScript(null);

		this.baseDataManager.updateLvChangeTime(new Date());

		if (getLevel() >= 10) {
			sync();
			RobotUtil.cloneRobot(this);
		}
		
		sevenGoalManager.processGoal(SevenGoalTaskType.LEVEL_TO,getLevel());
	}

	public void addExp(int exp, Const.GOODS_CHANGE_TYPE from) {
		this.addExp(exp, from, 0);
	}

	public void addUpExp(int value, Const.GOODS_CHANGE_TYPE from) {
		baseDataManager.addClassExp(value, from);
	}

	/**
	 * 添加经验
	 */
	public void addExp(int exp, Const.GOODS_CHANGE_TYPE from, int teamExp) {
		baseDataManager.addExp(exp, from, teamExp);
	}

	/**
	 * 恢复血魔
	 */
	public void recoverHPAndMP() {
		this.playerTempData.hp = btlDataManager.finalInflus.get(PlayerBtlData.MaxHP);
	};

	public void customTip(Const.CUSTOMTIPTYPE type, int num) {
		String strType = "";
		switch (type) {
		case GOLD:
			strType = "GAIN_GOLDS";
			break;
		case TICKET:
			strType = "GAIN_CASH";
			break;
		case DIAMOND:
			strType = "GAIN_DIAMOND";
			break;
		case EXP:
			strType = "GAIN_EXP";
			break;
		case PRESTIGE:
			strType = "GAIN_PRESTIGE";
			break;
		default:
			break;
		}
		if (StringUtil.isEmpty(strType)) {
			return;
		}
		String addStr = LangService.format(strType, num);
		this.sendSysTip(addStr, Const.TipsType.NEWTYPE);
	};

	/**
	 * 添加仙盟副本积分
	 */
	public boolean addGuildPoint(int num) {
		return baseDataManager.addGuildPoint(num);
	};

	/**
	 * 设置pk模式
	 */
	public void setPetPkModel(int model) {
		this.petNewManager.changePetPkModel(model);
	};


	/**
	 * 设置是否接受自动组队
	 */
	public void setIsAcceptAutoTeam(int isAcceptAutoTeam) {
		this.player.isAcceptAutoTeam = isAcceptAutoTeam;
	};

	/**
	 * 设置进场景状态
	 */
	public void setEnterState(int enterState) {
		this.enterState = enterState;
	};

	/**
	 * 设置出生类型
	 */
	public void setBornType(BORN_TYPE bornType) {
		this.bornType = bornType.value;
	};

	/**
	 * 重新设置出生数值
	 */
	public void setBornType(BORN_TYPE bornType, int areaId) {
		this.bornType = bornType.value;
		Out.debug("setBornType bornType:", bornType, " tempData:", playerTempData.areaId);
		if (this.bornType == Const.BORN_TYPE.HISTORY.value) {
			this.playerTempData.areaId = playerTempData.historyAreaId;
			this.playerTempData.x = playerTempData.historyX;
			this.playerTempData.y = playerTempData.historyY;
		} else if (this.bornType == Const.BORN_TYPE.BORN.value) {
			playerTempData.bornAreaId = playerTempData.areaId = areaId;
			playerTempData.x = playerTempData.bornX = 0;
			playerTempData.y = playerTempData.bornY = 0;
		}
	};

	// /**
	// * 处理客户端消息
	// * return 务必返回此类消息是否还继续有效
	// * */
	public boolean onMessage(int operate, MessageData message) {
		boolean result = true;
		Out.debug("player onMessage:", message);
		MessageUtil.deleteSendedPlayerMessage(message);
		if (message.messageType == Const.MESSAGE_TYPE.mail_receive.getValue()) {
			result = this.mailManager.onMessage(operate, message);
		} else if (message.messageType == Const.MESSAGE_TYPE.daily_task_times.getValue()) {
			result = TaskMessages.onMessage(this, MESSAGE_TYPE.daily_task_times, operate, message);
		} else if (message.messageType == Const.MESSAGE_TYPE.loop_task_addfriend.getValue()) {
			result = TaskMessages.onMessage(this, MESSAGE_TYPE.loop_task_addfriend, operate, message);
		} else if (message.messageType == Const.MESSAGE_TYPE.loop_task_member_leave.getValue()) {
			result = TaskMessages.onMessage(this, MESSAGE_TYPE.loop_task_member_leave, operate, message);
		} else if (message.messageType == Const.MESSAGE_TYPE.loop_task_times.getValue()) {
			result = TaskMessages.onMessage(this, MESSAGE_TYPE.loop_task_times, operate, message);
		} else if (message.messageType == Const.MESSAGE_TYPE.consignment_publish.getValue()) {
			result = ConsignmentUtil.onMessage(this, MESSAGE_TYPE.consignment_publish, operate, message);
		}
		this.messageManager.deleteReceivedMessage(message.messageType, message.id);
		return result;
	}

	public boolean onSaveRebirth(TaskEvent event) {
		if (event.type == EventType.rebirth.getValue()) {

			Area area = getArea();
			if (!area.canRebirth(this.getId())) {
				return true;
			}
			String name = ((Object[]) event.params)[1].toString();
			WNNotifyManager.getInstance().pushRebirth(this, name);
			return true;
		}
		return false;
	};

	/**
	 * 战斗服场景事件处理
	 */
	public void onBatterServerSceneEvent(String eventId) {
		Out.debug("onBatterServerSceneEvent eventId:", eventId);
		EventCO eventProp = GameData.Events.get(eventId);
		if (eventProp != null && eventProp.eventType == 1) {
			int toAreaId = Integer.parseInt(eventProp.eventData1);
			AreaUtil.enterArea(this, toAreaId, 0, 0);
		}
	}

	public void onEvent(TaskEvent event) {
		event.player = this;
		TaskQueue.put(event);
		// event.run();
	}

	/**
	 * 序列化仙盟成员信息
	 */
	public GuildMemberPO toJSON4GuildMember() {
		GuildMemberPO data = new GuildMemberPO();
		data.playerId = this.getId();
		data.name = this.getName();
		data.pro = this.getPlayer().pro;
		return data;
	};

	/**
	 * 向客户端发送effect更新数据
	 */
	public void pushEffectData() {
		Map<String, Object> data = new HashMap<>();
		data.put("fightPower", this.player.fightPower);
		WNNotifyManager.getInstance().pushEffectData(this, data);
	};

	public void pushDynamicData(String key, Object value) {
		WNNotifyManager.getInstance().pushPlayerDynamic(this, Utils.ofMap(key, value));
	}

	/**
	 * 需要来源的玩家货币数据推送
	 */
	public void pushDynamicData(String key, Object value, Const.GOODS_CHANGE_TYPE origin) {
		pushDynamicData(key, value, origin, null);
	}

	/**
	 * 需要来源和道具变更的数据推送
	 */
	public void pushDynamicData(String key, Object value, Const.GOODS_CHANGE_TYPE origin, List<KeyValueStruct> itemChange) {
		PropertyStruct.Builder data = PropertyStruct.newBuilder();
		data.setKey(key);
		data.setValue(String.valueOf(value));
		if (origin != null) {
			data.setSource(origin.getValue());
		}
		data.setType(1);
		if (itemChange != null) {
			data.addAllItems(itemChange);
		}
		WNNotifyManager.getInstance().pushPlayerDynamic(this, data.build());
	}

	/**
	 * 推送字段变更信息
	 * 
	 * @param k, v
	 */
	public void pushDynamicData(Map<String, Object> atts) {
		WNNotifyManager.getInstance().pushPlayerDynamic(this, atts);
	}

	/**
	 * 向客户端及战斗服同步角色effect数据
	 */
	public void pushAndRefreshEffect(boolean isHpMpValid) {
		this.refreshBattlerServerEffect(isHpMpValid);
		this.pushEffectData();
	};

	/** 重载方法 */
	public void sendSysTip(String content) {
		sendSysTip(content, Const.TipsType.NORMAL);
	}

	/**
	 * 发送系统提示
	 */
	public void sendSysTip(String content, Const.TipsType type) {
		MessageUtil.sendSysTip(this, content, type);
	};

	/**
	 * 上线通知所有角标的信息
	 */
	public void nofitySuperScript() {
		SuperScriptPush.Builder data = SuperScriptPush.newBuilder();

		List<SuperScriptType> s2c_data = new ArrayList<>();
		for (ModuleManager manager : allManagers.values()) {
			List<SuperScriptType> list = manager.getSuperScript();
			if (list != null) {
				s2c_data.addAll(list);
			}
		}

		// 以下是没有实现ModuleManager接口的模块，需要手动添加
		s2c_data.addAll(this.mailManager.getSuperScript());
		s2c_data.addAll(this.soloManager.getSuperScript());
		s2c_data.addAll(this.arenaManager.getSuperScript());
		s2c_data.addAll(this.shopMallManager.getSuperScript());
		s2c_data.addAll(this.achievementManager.getSuperScript());
		s2c_data.addAll(this.mountManager.getSuperScript());
		s2c_data.addAll(this.equipManager.getSuperScript());

		data.addAllS2CData(s2c_data);

		receive("area.playerPush.onSuperScriptPush", data.build());
	};

	/**
	 * 获得道具，货币后主动更新的红点信息
	 */
	public List<SuperScriptType> getItemChangeScript() {
		List<SuperScriptType> s2c_data = new ArrayList<>();
		// 各个模块相应的接口 TODO
		// s2c_data = s2c_data.concat(this.mount.getSuperScript());
		// s2c_data = s2c_data.concat(this.wingManager.getSuperScript());
		// s2c_data = s2c_data.concat(this.skillManager.getSuperScript());
		// s2c_data = s2c_data.concat(this.medal.getSuperScript());
		// s2c_data = s2c_data.concat(this.masteryManager.getSuperScript());
		// s2c_data = s2c_data.concat(this.petManager.getSuperScript());
		// s2c_data = s2c_data.concat(this.equipCraftManager.getSuperScript());
		// s2c_data = s2c_data.concat(this.activityManager.getSuperScript());
		// s2c_data = s2c_data.concat(this.treasureManager.getSuperScript());

		Out.debug("itemChangeScript data:", s2c_data);
		return s2c_data;
	};

	/**
	 * 更新角标信息
	 * 
	 * @param type Const.SUPERSCRIPT_TYPE枚举类型
	 * @param number 0:不显示 1:不带数字的红点 2～n:显示数字
	 */
	public void updateSuperScript(Const.SUPERSCRIPT_TYPE type, int number) {
		SuperScriptPush.Builder data = SuperScriptPush.newBuilder();

		SuperScriptType.Builder scriptType = SuperScriptType.newBuilder();
		scriptType.setType(type.getValue());
		scriptType.setNumber(number);
		data.addS2CData(scriptType);

		receive("area.playerPush.onSuperScriptPush", data.build());
	};

	/**
	 * 更新多个角标信息
	 * 
	 * @param scriptList eg:[{type: type, number: number}]; type
	 *            Const.SUPERSCRIPT_TYPE枚举类型 number 0:不显示 1:不带数字的红点 2～n:显示数字
	 */
	public void updateSuperScriptList(List<SuperScriptType> scriptList) {
		if (scriptList == null || scriptList.isEmpty()) {
			return;
		}
		SuperScriptPush.Builder data = SuperScriptPush.newBuilder();
		data.addAllS2CData(scriptList);
		receive("area.playerPush.onSuperScriptPush", data.build());
	};

	/**
	 * 金票不足打开商店界面
	 */
	public void puchFuncGoToTicketNotEnough() {
		this.onFunctionGoTo(Const.FUNCTION_GOTO_TYPE.TICKET_NOT_ENOUGH, null, null, null);
	};

	public void puchFuncGoToPickItem() {
		this.onFunctionGoTo(Const.FUNCTION_GOTO_TYPE.PICK_ITEM, null, null, null);
	};

	/**
	 * 界面跳转接口
	 */
	public void onFunctionGoTo(Const.FUNCTION_GOTO_TYPE funGoId, String itemCode, String id, TipsParam tipsParam) {
		FunctionGoToPush.Builder args = FunctionGoToPush.newBuilder();
		args.setS2CFunGoId(funGoId.getValue());
		if (!StringUtil.isEmpty(itemCode)) {
			args.setS2CItemCode(itemCode);
		}
		if (!StringUtil.isEmpty(id)) {
			args.setS2CId(id);
		}
		if (tipsParam != null) {
			args.setS2CParam(tipsParam);
		}

		WNNotifyManager.getInstance().pushFunctionGoTo(this, args);
	};

	/**
	 * 向客户端推送角色相关数据
	 */
	public void onEndEnterScene() {
		this.mountManager.pushToClientMountsFlag();
		getXmdsManager().playerReady(getId());

		Area area = getArea();
		Actor actor = area.getActor(getId());
		if (actor != null) {
			if (actor.ready && !inPvP())
				return;
			actor.ready = true;
		}

		// this.soloManager.pushLeftSoloTimeToClient();
		// this.bufferManager.pushLocalBuffToClient();
		// this.crossManager.pushCrossTreasureOpenInfo();
		// this.limitTimeActivityManager.refreshNewDay();

		TeamService.changeTeamArea(this);
		area.onEndEnterScene(this);

		RobotUtil.onRobotReplyHP(this);
		Out.debug(battleServerId, " onEndEnterScene : ", area);
	};

	/**
	 * 钓鱼
	 */
	public void onFishItem(List<MiniItem> item) {
		WNNotifyManager.getInstance().pushFishItem(this, item);
	};

	public void sendLeaveWord() {

	};

	/**
	 * 
	 * @param templateId 任务模板ID
	 * @param npcId
	 */
	public boolean canTalkWithNpc(int templateId, int npcId) {
		// if (npcId == 0) {
		// return true;
		// }
		//
		// String _data = getXmdsManager().canTalkWithNpc(this.getId(), npcId);
		// Out.debug("canTalkWithNpc:", _data);
		// CanTalkWithNpcResult data = JSON.parseObject(_data,
		// CanTalkWithNpcResult.class);
		//
		// if (!data.canTalk) {
		// return TaskUtils.canTalkWithNpc(templateId, npcId);
		// }
		// return data.canTalk;
		return true;
	};

	public LookUpPlayer getFightData() {
		LookUpPlayer.Builder data = LookUpPlayer.newBuilder();
		data.setName(player.name);
		data.setLevel(player.level);
		data.setUpLevel(player.upLevel);
		data.setUpOrder(player.upOrder);
		data.setPro(player.pro);
		data.setFightPower(player.fightPower);
		data.addAllAttrs(btlDataManager._getPlayerAttr());
		return data.build();
	};

	public void pushChatSystemMessage(Const.SYS_CHAT_TYPE type, String value1, Object value2, String value3) {
		this.pushChatSystemMessage(type, value1, value2, value3, null);
	}

	public void pushChatSystemMessage(Const.SYS_CHAT_TYPE type, String value1, Object value2, String value3, GOODS_CHANGE_TYPE from) {
		String str = "";
		if (type == Const.SYS_CHAT_TYPE.ITEM) {
			String strQt = ItemUtil.getColorItemNameByQcolor((int) value2, value1);
			if (from != null && from == GOODS_CHANGE_TYPE.shop) {
				str = LangService.format("ITEM_GET_BUY", value3, strQt);
			} else {
				str = LangService.format("ITEM_GET", value3, strQt);
			}
		} else if (type == Const.SYS_CHAT_TYPE.EXP) {
			if (value2 != null && StringUtil.isNotEmpty(String.valueOf(value2))) {
				str = LangService.format("GAIN_EXP_CHAT_WORLDLEVEL", value1, value2);
			} else {
				str = LangService.format("GAIN_EXP_CHAT", value1);
			}
		} else if (type == Const.SYS_CHAT_TYPE.CLASS_EXP) {
			str = LangService.format("GAIN_CLASS_EXP_CHAT", value1);
		} else if (type == Const.SYS_CHAT_TYPE.TEAM_EXP) {
			str = LangService.format("GAIN_TEAM_EXP", value1, value2);
		} else if (type == Const.SYS_CHAT_TYPE.COIN) {
			str = LangService.format("GAIN_GOLDS_CHAT", value1);
		} else if (type == Const.SYS_CHAT_TYPE.TRADE) {
			if (value2 != null) {
				String str1 = "";
				@SuppressWarnings("unchecked")
				List<TradeMessageItemData> list = (List<TradeMessageItemData>) value2;
				for (TradeMessageItemData item : list) {
					str1 += ItemUtil.getColorItemNameByQcolor(item.qt, item.name) + "*" + item.num + ", ";
				}
				if (str1 != "") {
					if (value1 != null) {
						str = LangService.getValue("TRADE_GET");
					} else {
						str = LangService.getValue("TRADE_LOST");
					}
					str1 = str1.substring(0, str1.length() - 2);
					str = str.replace("{tradeItem}", str1);
				}
			}
		}

		if (!StringUtil.isEmpty(str)) {
			MessageUtil.sendSysChat(this, str);
		}
	}

	/**
	 * 对象序列化到数据库
	 */
	public void update() {
		//
		this.bag.update();
		this.wareHouse.update();
		GameDao.update(getId(), ConstsTR.playerBtlData.value, btlDataManager.finalInflus);

		if (this.reliveManager != null) {
			GameDao.update(ConstsTR.DAILY_RELIVE.value, getId(), this.reliveManager);
		}
		if (this.intergalMallManager != null) {
			GameDao.update(ConstsTR.intergalMallTR.value, getId(), intergalMallManager.intergalMallPO);
		}
		GameDao.update(player.id, ConstsTR.player_illusion, illusionManager.illusionPO);
	}

	public ZoneManagerPrx getZoneManager() {
		return CSharpClient.getZoneManager(getBattleServerId());
	}

	public XmdsManagerPrx getXmdsManager() {
		return CSharpClient.getXmdsManager(getBattleServerId());
	}

	public Area getArea() {
		return area;
	}

	public boolean inPvP() {
		return getSceneType() == SCENE_TYPE.ARENA.getValue() || getSceneType() == SCENE_TYPE.FIVE2FIVE.getValue() || getSceneType() == SCENE_TYPE.SIN_COM.getValue();
	}

	/**
	 * 玩家进阶了
	 */
	public void onClassUpgrade() {
		for (ModuleManager manager : allManagers.values()) {
			manager.onPlayerEvent(PlayerEventType.CLASS_UPGRADE);
		}

		this.refreshBattlerServerEffect(false);
		updateFightPowerPoint();
	}

	/**
	 * 坐骑升阶升星换皮肤触发
	 */
	public void onMountPropChange() {
		btlDataManager.onMountPropChange();
		this.refreshBattlerServerEffect(false);
		updateFightPowerPoint();
	}

	/**
	 * 兑换属性了.
	 */
	public void onExchangeProparty() {
		for (ModuleManager manager : allManagers.values()) {
			manager.onPlayerEvent(PlayerEventType.EXCHANGE_PROPARTY);
		}

		this.refreshBattlerServerEffect(false);
		updateFightPowerPoint();
	}

	public void onPlayerSkillUpgrade() {
		updateFightPowerPoint();
	}

	public void onTalentPassiveSkillUpgrade() {
		for (ModuleManager manager : allManagers.values()) {
			manager.onPlayerEvent(PlayerEventType.UPGRADE_TALENT_PASSIVE_SKILL);
		}
		this.refreshBattlerServerEffect(false);
		updateFightPowerPoint();
	}

	public void onEquipChange(GOODS_CHANGE_TYPE from) {
		for (ModuleManager manager : allManagers.values()) {
			manager.onPlayerEvent(PlayerEventType.EQUIPMENT_CHANGE);
		}
		this.refreshBattlerServerEffect(false);
		updateFightPowerPoint(from);
	}

	/**
	 * 时装
	 */
	public void onFashionChange() {
		for (ModuleManager manager : allManagers.values()) {
			manager.onPlayerEvent(PlayerEventType.FASHION_CHANGE);
		}
		this.refreshBattlerServerEffect(false);
		updateFightPowerPoint(GOODS_CHANGE_TYPE.Fashion);
	}
	
	/**
	 * 血脉
	 */
	public void onBloodChange() {
		for (ModuleManager manager : allManagers.values()) {
			manager.onPlayerEvent(PlayerEventType.BLOOD);
		}
		this.refreshBattlerServerEffect(false);
		updateFightPowerPoint(GOODS_CHANGE_TYPE.blood);
	}

	/**
	 * 元始圣甲
	 */
	public void onArmourActive() {
		for (ModuleManager manager : allManagers.values()) {
			manager.onPlayerEvent(PlayerEventType.ARMOUR_ACTIVE);
		}
		this.refreshBattlerServerEffect(false);
		updateFightPowerPoint();
	}

	public void onPetPropChange() {
		for (ModuleManager manager : allManagers.values()) {
			manager.onPlayerEvent(PlayerEventType.PET_PROP_CHANGE);
		}
		this.refreshBattlerServerEffect(false);
		updateFightPowerPoint();
	}

	public void onGuildBlessChange() {
		for (ModuleManager manager : allManagers.values()) {
			manager.onPlayerEvent(PlayerEventType.GUILD_BLESS_CHANGE);
		}
		this.refreshBattlerServerEffect(false);
		updateFightPowerPoint();
	}

	/**
	 * 刷新玩家仙盟修行属性
	 */
	public void updatePlayerGuildTechAttrs() {
		for (ModuleManager manager : allManagers.values()) {
			manager.onPlayerEvent(PlayerEventType.GUILD_TECH_CHANGE);
		}
		this.refreshBattlerServerEffect(false);
		updateFightPowerPoint();
	}

	private void updateFightPowerPoint() {
		updateFightPowerPoint(GOODS_CHANGE_TYPE.def);
	}

	private void updateFightPowerPoint(GOODS_CHANGE_TYPE from) {
		calFightPower();
		this.pushDynamicData("fightPower", this.player.fightPower, from);
	}

	/**
	 * 刷新任务称号属性
	 */
	public void updateTitleAttrs() {
		for (ModuleManager manager : allManagers.values()) {
			manager.onPlayerEvent(PlayerEventType.TITLE_CHANGE);
		}
		this.refreshBattlerServerEffect(false);
	}

	private PlayerInteract interactManager;

	public PlayerInteract getInteractManager() {
		if (interactManager == null) {
			interactManager = new PlayerInteract(this);
		}
		return interactManager;
	}

	private ReliveManager reliveManager;

	public ReliveManager getReliveManager() {
		if (reliveManager == null) {
			String dailyRelive = GCache.hget(ConstsTR.DAILY_RELIVE.value, getId());
			reliveManager = StringUtil.isNotEmpty(dailyRelive) ? JSON.parseObject(dailyRelive, ReliveManager.class) : new ReliveManager();
		}
		return reliveManager;
	}

	public void free() {
		update();
		PlayerPOManager.clearOfflinePO(this.getId());
	}

	/**
	 * 玩家充值
	 */
	public void onPay() {
		for (ModuleManager manager : allManagers.values()) {
			manager.onPlayerEvent(PlayerEventType.PAY);
		}
	}

	/**
	 * GM T下线
	 */
	public void kick(KickReason reason) {
		try {// 该玩家状态不是sessionClosed，是异常状态，返回错误
			KickPlayerPush.Builder data = KickPlayerPush.newBuilder();
			data.setS2CReasonType(reason.value);
			session.write(new MessagePush("area.playerPush.kickPlayerPush", data.build()).getContent()).await(2000);
		} catch (InterruptedException e) {}
		session.close();
		doLogout(false);
	}

	public boolean isRobot() {
		return PlayerUtil.isRobot(player);
	}

	public boolean isProxy() {
		return false;
	}

	public boolean isRomote() {
		if (teamManager.acrossTargetId > 0) {
			return true;
		}
		TeamData team = teamManager.getTeam();
		return team != null && !team.local;
	}

	public void changeArea(AreaData areaData) {
		AreaUtil.changeArea(this, areaData);
	}

	public PetNew getFightingPet() {
		return petNewManager.getFightingPet();
	}

	public Map<String, WNPlayer> teamMembers;

	public Set<String> getTeamMembers() {
		if (teamMembers != null)
			return teamMembers.keySet();
		Map<String, TeamMemberData> teamMembers = teamManager.getTeamMembers();
		if (teamMembers != null) {
			return teamMembers.keySet();
		}
		return null;
	}

	public void finishFightLevel(int currHard, int templateID) {}

	public void onProxyEvent(int type, ProxyEventCB event) {}

	public int getGuildExdExp() {
		return guildManager.calAllInfluence().containsKey("ExdExp") ? guildManager.calAllInfluence().get("ExdExp") : 0;
	}

	public int getGuildExdGold() {
		return guildManager.calAllInfluence().containsKey("ExdGold") ? guildManager.calAllInfluence().get("ExdGold") : 0;
	}

	public int getBtlExdGold() {
		return btlDataManager.allInflus.containsKey(PlayerBtlData.ExdGold) ? btlDataManager.allInflus.get(PlayerBtlData.ExdGold) : 0;
	}

	public int processXianYuanGet(int from) {
		int addNum = XianYuanService.getInstance().processXianYuanGet(from, allBlobData.xianYuan);
		if (addNum > 0) {
			this.moneyManager.addXianYuan(addNum, from);
		}
		return addNum;
	}
}
