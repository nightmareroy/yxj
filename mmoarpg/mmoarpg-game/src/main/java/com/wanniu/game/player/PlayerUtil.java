package com.wanniu.game.player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wanniu.core.GGame;
import com.wanniu.core.XLang;
import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GPlayer;
import com.wanniu.core.logfs.Out;
import com.wanniu.core.login.AuthServer;
import com.wanniu.core.tcp.protocol.Packet;
import com.wanniu.core.util.DateUtil;
import com.wanniu.core.util.StringUtil;
import com.wanniu.game.GWorld;
import com.wanniu.game.area.AreaUtil;
import com.wanniu.game.attendance.PlayerAttendance.GiftState;
import com.wanniu.game.bag.BagPO;
import com.wanniu.game.common.Const;
import com.wanniu.game.common.Const.EquipType;
import com.wanniu.game.common.Const.ONLINE_GIFT_TYPE;
import com.wanniu.game.common.Const.TaskType;
import com.wanniu.game.common.ConstsTR;
import com.wanniu.game.common.Utils;
import com.wanniu.game.data.BranchLineCO;
import com.wanniu.game.data.CharacterCO;
import com.wanniu.game.data.CharacterLevelCO;
import com.wanniu.game.data.GameData;
import com.wanniu.game.data.MainLineCO;
import com.wanniu.game.data.PrefixCO;
import com.wanniu.game.data.SuffixCO;
import com.wanniu.game.data.base.DItemEquipBase;
import com.wanniu.game.data.ext.CharacterExt;
import com.wanniu.game.data.ext.CharacterExt.InitItem;
import com.wanniu.game.data.ext.EquipSockExt;
import com.wanniu.game.data.ext.OlGiftExt;
import com.wanniu.game.data.ext.TransportExt;
import com.wanniu.game.data.ext.UpLevelExpExt;
import com.wanniu.game.equip.EquipUtil;
import com.wanniu.game.equip.NormalEquip;
import com.wanniu.game.fashion.FashionUtil;
import com.wanniu.game.five2Five.Five2FiveService;
import com.wanniu.game.functionOpen.FunctionOpenUtil;
import com.wanniu.game.guild.GuildService;
import com.wanniu.game.guild.GuildServiceCenter;
import com.wanniu.game.item.ItemConfig;
import com.wanniu.game.item.ItemUtil;
import com.wanniu.game.item.NormalItem;
import com.wanniu.game.item.po.MedalPO;
import com.wanniu.game.mount.MountCenter;
import com.wanniu.game.mount.MountUtil;
import com.wanniu.game.onlineGift.OlGiftConfig;
import com.wanniu.game.player.po.AllBlobPO;
import com.wanniu.game.player.po.AvatarObj;
import com.wanniu.game.playerSkill.SkillManager;
import com.wanniu.game.poes.AchievementDataPO;
import com.wanniu.game.poes.ActivityDataPO;
import com.wanniu.game.poes.ArenaDataPO;
import com.wanniu.game.poes.AttendancePO;
import com.wanniu.game.poes.BagsPO;
import com.wanniu.game.poes.DailyActivityPO;
import com.wanniu.game.poes.FightLevelsPO;
import com.wanniu.game.poes.FunctionOpenPO;
import com.wanniu.game.poes.GuildPO;
import com.wanniu.game.poes.HookSetPO;
import com.wanniu.game.poes.LeaderBoardPlayerPO;
import com.wanniu.game.poes.MountPO;
import com.wanniu.game.poes.OnlineDataPO;
import com.wanniu.game.poes.PlayerAttachPO;
import com.wanniu.game.poes.PlayerBasePO;
import com.wanniu.game.poes.PlayerBasePO.EquipStrengthPos;
import com.wanniu.game.poes.PlayerChouRenPO;
import com.wanniu.game.poes.PlayerConsignmentItemsPO;
import com.wanniu.game.poes.PlayerFriendsPO;
import com.wanniu.game.poes.PlayerMailDataPO;
import com.wanniu.game.poes.PlayerPKDataPO;
import com.wanniu.game.poes.PlayerPO;
import com.wanniu.game.poes.PlayerPetsNewPO;
import com.wanniu.game.poes.PlayerTempPO;
import com.wanniu.game.poes.ShopMallPO;
import com.wanniu.game.poes.SkillsPO;
import com.wanniu.game.poes.SoloDataPO;
import com.wanniu.game.poes.TaskListPO;
import com.wanniu.game.poes.TitlePO;
import com.wanniu.game.poes.VipPO;
import com.wanniu.game.sysSet.SysSetFlag;
import com.wanniu.game.task.LoopResult;
import com.wanniu.game.task.TaskData;
import com.wanniu.game.task.TaskUtils;
import com.wanniu.game.xianyuan.XianYuanService;
import com.wanniu.redis.PlayerPOManager;

import io.netty.channel.Channel;
import pomelo.Common.Avatar;
import pomelo.area.TeamHandler.Player;
import pomelo.item.ItemOuterClass.MiniItem;
import pomelo.player.PlayerOuterClass.PlayerBasic;

/**
 * 角色工具类
 * 
 * @author Yangzz
 *
 */
public class PlayerUtil {

	public static int maxClassID = 0;
	public static int maxUpOrder = 0;

	static {
		Map<Integer, UpLevelExpExt> map = GameData.UpLevelExps;
		for (UpLevelExpExt prop : map.values()) {
			if (prop.classID > maxClassID && prop.isValid == 1)
				maxClassID = prop.classID;
			if (prop.upOrder > maxUpOrder && prop.isValid == 1)
				maxUpOrder = prop.upOrder;
		}
		// maxClassID
	}

	/**
	 * 获取某在线玩家场景坐标信息
	 */
	public static JSONObject getPlayerPosition(WNPlayer player) {
		String res = player.getXmdsManager().getPlayerData(player.getId(), false);
		return StringUtil.isEmpty(res) ? Utils.toJSON("x", 0, "y", 0) : JSON.parseObject(res);
	};

	/**
	 * 判断角色是否在线
	 */
	public final static boolean isOnline(String playerId) {
		return GWorld.getInstance().isOnline(playerId);
	}

	/**
	 * 判断玩家对象是否还存在
	 */
	public static boolean isLocal(String rid) {
		return GWorld.getInstance().isLocal(rid);
	}

	/**
	 * 将系统消息发送给对应玩家 若玩家在本服务器，则直接推送，否则通过中心服务器对消息进行中转
	 */
	public static String sendSysMessageToPlayer(String message, String playerId) {
		return sendSysMessageToPlayer(message, playerId, Const.TipsType.NORMAL);
	}

	public static String sendSysMessageToPlayer(String message, String playerId, Const.TipsType type) {
		WNPlayer player = GWorld.getInstance().getPlayer(playerId);
		if (player != null) {
			player.sendSysTip(message, type);
		} else {
			// if(isOnline(playerId)){
			// CommonUtil.sendMsgToCenterServer([playerId],Const.PlayerMessage.SYSMESSAGE,
			// {message:message,type:type});
			// }else{
			// return playerId;
			// }
		}
		return null;
	}

	/**
	 * 处理角色message
	 */
	public static final String handleGMChat(WNPlayer player, Map<String, Object> _data) {
		String operate = (String) _data.get("operate");
		if (operate.equals("add")) {
			String itemCode = (String) _data.get("itemCode");
			if (ItemConfig.getInstance().getItemPropByName(itemCode) != null) {
				itemCode = ItemConfig.getInstance().getItemPropByName(itemCode).code;
			}
			int num = (int) _data.get("num");
			if (player.bag.testAddCodeItem(itemCode, num)) {
				player.bag.addCodeItem(itemCode, num, Const.ForceType.DEFAULT, Const.GOODS_CHANGE_TYPE.gm, null);
			}
		} else if (operate.equals("addRank")) {
			int id = (int) _data.get("id");
			player.titleManager.onAwardRank(id);
		} else if (operate.equals("addPetExp")) {
			player.petNewManager.addExp(Integer.parseInt((String) _data.get("petId")), Integer.parseInt((String) _data.get("num")));
		} else if (operate.equalsIgnoreCase("finishTask")) {
			player.taskManager.gmFinishTask((int) _data.get("id"));
		} else if (operate.equalsIgnoreCase("acceptTask")) {
			player.taskManager.gmAcceptTask((int) _data.get("id"));
		} else if (operate.equals("finishTaskTarget")) {
			player.taskManager.gmfinishTaskTarget((int) _data.get("id"));
		} else if (operate.equalsIgnoreCase("newTask")) {
			player.taskManager.gmNewTask((int) _data.get("id"));
		} else if (operate.equalsIgnoreCase("discardTask")) {
			player.taskManager.gmDiscardTaskByID((int) _data.get("id"));
		} else if (operate.equals("prepaid")) {
			player.prepaidManager.onPrepaidChargeByMoney((int) _data.get("money"));
		} else if (operate.equals("openFunc")) {
			player.functionOpenManager.gmOpenFunction((int) _data.get("functionID"));
		} else if (operate.equals("guildAdd")) {
			if (_data.get("moneyName").equals("contribution")) {
				player.guildManager.addContribution((int) _data.get("num"), Const.GOODS_CHANGE_TYPE.gm);
			} else {
				GuildService.gmAddGuildMoney(player.getId(), (String) _data.get("moneyName"), (int) _data.get("num"));
			}
		} else if (operate.equals("tp")) {
			AreaUtil.dispatchByAreaId(player, (int) _data.get("mapID"), null);
		} else if (operate.equals("guildFundAdd")) {
			GuildPO guildPo = player.guildManager.guild;
			if (null != guildPo) {
				int _num = (int) _data.get("num");
				guildPo.fund += _num;
				guildPo.sumFund += _num;
				GuildServiceCenter.getInstance().refreshGuildTopInfo(guildPo.id); // 刷新排行榜
				GuildServiceCenter.getInstance().saveGuild(guildPo);
			}
		} else if (operate.equals("guildExpAdd")) {
			GuildPO guildPo = player.guildManager.guild;
			if (null != guildPo) {
				guildPo.exp += (int) _data.get("num");
				GuildServiceCenter.getInstance().saveGuild(guildPo);
			}
		}
		return null;
	}

	/**
	 * 根据玩家ID获取玩家不包含离线玩家
	 */
	public static WNPlayer findPlayer(String playerId) {
		if (StringUtil.isEmpty(playerId)) {
			Out.error("playerId is nil");
			new Exception().printStackTrace();
			return null;
		}
		WNPlayer player = getOnlinePlayer(playerId);
		if (player != null) {
			return player;
		}

		// AllBlobPO allBlobData = PlayerDao.getAllBlobData(playerId);
		// player = new WNPlayer(allBlobData);

		return player;
	}

	/**
	 * 获取在线玩家
	 */
	public static WNPlayer getOnlinePlayer(String playerId) {
		if (StringUtil.isEmpty(playerId)) {
			Out.error("playerId is null");
			new Exception().printStackTrace();
			return null;
		}
		return GWorld.getInstance().getPlayer(playerId);
	};

	public static WNPlayer getOnlinePlayerByUid(String uid) {
		return (WNPlayer) GGame.getInstance().getPlayerByUid(uid);
	};

	public static PlayerPO getPlayerBaseData(String playerId) {
		WNPlayer player = PlayerUtil.findPlayer(playerId);

		if (null != player) {
			return player.getPlayer();
		}

		try {
			return PlayerDao.getPlayerDataById(playerId);
		} catch (Exception e) {
			Out.error(e);
			return null;
		}
	}

	/// **
	// * 判断玩家是否开放某功能
	// * @param playerId 玩家id
	// * @param funcName 功能类型名称(Const.FunctionType)
	// */
	public static boolean isPlayerOpenedFunction(String playerId, String funcName) {
		int defState = FunctionOpenUtil.getDefaultOpenState(funcName);
		if (defState == -1) {
			return false; // 系统配置关闭
		}
		if (defState == 1) { // 系统配置默认开启
			return true;
		}
		FunctionOpenPO db = PlayerPOManager.findPO(ConstsTR.player_func_openTR, playerId, FunctionOpenPO.class);
		if (db == null) {
			return false;
		} else {
			if (db.openMap == null || !db.openMap.containsKey(funcName)) {
				return false;
			}
		}
		return true;
	};

	//
	public static Player.Builder transToJson4TeamMemberSimple(WNPlayer player) {
		PlayerPO playerData = player.getPlayer();
		Player.Builder data = Player.newBuilder();
		data.setId(playerData.id);
		data.setName(playerData.name);
		data.setPro(playerData.pro);
		data.setLevel(playerData.level);
		data.setUpLevel(playerData.upLevel);
		String guildName = player.guildManager.getGuildName();
		data.setGuildName(StringUtil.isEmpty(guildName) ? LangService.getValue("TEAM_NO_GUILD") : guildName);
		return data;
	};
	//

	public static PlayerBasic transToJson4BasicByBlob(AllBlobPO playerData) {
		PlayerBasic.Builder data = PlayerBasic.newBuilder();
		data.setId(playerData.player.id);
		data.setName(playerData.player.name);
		data.setLevel(playerData.player.level);
		data.setPro(playerData.player.pro);
		data.setUpLevel(playerData.player.upLevel);

		if (playerData.playerBase.equipGrids == null) {
			playerData.playerBase.equipGrids = new HashMap<>();
		}

		data.addAllAvatars(getBattlerServerAvatar4Login(playerData.player.pro, playerData.playerBase));
		return data.build();
	};

	public static PlayerBasic transToJson4Basic(WNPlayer playerData) {
		PlayerBasic.Builder data = PlayerBasic.newBuilder();
		data.setId(playerData.player.id);
		data.setName(playerData.player.name);
		data.setLevel(playerData.player.level);
		data.setPro(playerData.player.pro);
		data.setUpLevel(playerData.player.upLevel);

		if (playerData.playerBasePO.equipGrids == null) {
			playerData.playerBasePO.equipGrids = new HashMap<>();
		}

		data.addAllAvatars(getBattlerServerAvatar4Login(playerData.player.pro, playerData.playerBasePO));
		return data.build();
	};

	public static PlayerBasic playerBasicData(PlayerPO playerData) {
		Out.debug(PlayerUtil.class, "playerBasicData:::", playerData.id);
		PlayerBasic.Builder data = PlayerBasic.newBuilder();
		data.setId(playerData.id);
		data.setName(playerData.name);
		data.setLevel(playerData.level);
		data.setPro(playerData.pro);
		data.setUpLevel(playerData.upLevel);
		return data.build();
	};

	/**
	 * 根据职业获取Avatar协议
	 */
	public static List<Avatar> getBattleServerAvatar(int pro) {
		CharacterCO basicProp = GameData.Characters.get(pro);
		List<Avatar> avatars = new ArrayList<>();
		Avatar.Builder avatar = Avatar.newBuilder();
		avatar.setEffectType(0);
		avatar.setTag(Const.AVATAR_TYPE.AVATAR_BODY.value);
		avatar.setFileName(basicProp.model);
		avatars.add(avatar.build());
		return avatars;
	}

	/**
	 * 获取avatar信息
	 */
	public static List<Avatar> getBattlerServerAvatar(String playerId) {
		List<Avatar> avatars = new ArrayList<>();
		PlayerPO playerPO = PlayerPOManager.findPO(ConstsTR.playerTR, playerId, PlayerPO.class);
		if (playerPO == null) {
			Out.error("getBattlerServerAvatarxxxxxxxxxxxxxxx-->>", playerId);
			return avatars;
		}
		PlayerBasePO playerBasePO = PlayerPOManager.findPO(ConstsTR.playerBaseTR, playerId, PlayerBasePO.class);
		// WingDataPO wingPO = PlayerPOManager.findPO(ConstsTR.wingTR, playerId,
		// WingDataPO.class);
		MountPO mountPO = playerPO.openMount ? MountCenter.getInstance().findMount(playerId) : null;

		List<Avatar> equipAvatars = EquipUtil.getAvatarData(playerPO.pro, playerBasePO, false);
		avatars.addAll(equipAvatars);
		avatars.addAll(MountUtil.getCurMountAvatarInfo(mountPO));
		// 时装
		List<Avatar> fashionAvatars = FashionUtil.getAvatarData(playerBasePO);
		Map<Integer, Avatar> targetMap = new HashMap<>();
		for (Avatar avatar : avatars) {
			targetMap.put(avatar.getTag(), avatar);
		}
		for (Avatar fashionAvatar : fashionAvatars) {
			int tag = fashionAvatar.getTag();
			if (targetMap.containsKey(tag)) {
				// 需要替换effectId,所以新建一个builder
				Avatar.Builder avatarBuilderNew = Avatar.newBuilder();
				avatarBuilderNew.setTag(tag);
				avatarBuilderNew.setFileName(fashionAvatar.getFileName());
				avatarBuilderNew.setEffectType(targetMap.get(tag).getEffectType());
				targetMap.put(tag, avatarBuilderNew.build());
			} else {
				targetMap.put(fashionAvatar.getTag(), fashionAvatar);
			}
		}
		List<Avatar> targetList = new LinkedList<>();
		for (Avatar avatar : targetMap.values()) {
			targetList.add(avatar);
		}
		return targetList;
	};

	/**
	 * 获取avatar信息
	 * 
	 * @param changeModel 是否使用变身avatar
	 */
	public static List<Avatar> getBattlerServerAvatar(WNPlayer source, boolean changeModel) {
		List<Avatar> avatars = new ArrayList<>();

		// 任务 变身模型,屏蔽 武器，坐骑
		if (StringUtil.isNotEmpty(source.playerBasePO.model) && changeModel) {
			Avatar.Builder avatar = Avatar.newBuilder();
			avatar.setEffectType(0);
			avatar.setTag(Const.AVATAR_TYPE.AVATAR_BODY.value);
			avatar.setFileName(source.playerBasePO.model);
			avatars.add(avatar.build());
			return avatars;
		}

		// 装备
		List<Avatar> equipAvatars = EquipUtil.getAvatarData(source.player.pro, source.playerBasePO, changeModel);
		avatars.addAll(equipAvatars);
		// 坐骑
		if (!changeModel || StringUtil.isEmpty(source.playerBasePO.model)) {
			avatars.addAll(MountUtil.getCurMountAvatarInfo(source.mountManager.mount));
		}
		// 时装
		List<Avatar> fashionAvatars = FashionUtil.getAvatarData(source.playerBasePO);
		Map<Integer, Avatar> targetMap = new HashMap<>();
		for (Avatar avatar : avatars) {
			targetMap.put(avatar.getTag(), avatar);
		}
		for (Avatar fashionAvatar : fashionAvatars) {
			int tag = fashionAvatar.getTag();
			if (targetMap.containsKey(tag)) {
				// 需要替换effectId,所以新建一个builder
				Avatar.Builder avatarBuilderNew = Avatar.newBuilder();
				avatarBuilderNew.setTag(tag);
				avatarBuilderNew.setFileName(fashionAvatar.getFileName());
				avatarBuilderNew.setEffectType(targetMap.get(tag).getEffectType());
				targetMap.put(tag, avatarBuilderNew.build());
			} else {
				targetMap.put(fashionAvatar.getTag(), fashionAvatar);
			}
		}
		List<Avatar> targetList = new LinkedList<>();
		for (Avatar avatar : targetMap.values()) {
			targetList.add(avatar);
		}
		return targetList;
	};

	public static List<Avatar> getBattlerServerAvatar(PlayerPO player) {
		List<Avatar> avatars = new ArrayList<>();
		PlayerBasePO playerBasePO = PlayerPOManager.findPO(ConstsTR.playerBaseTR, player.id, PlayerBasePO.class);
		List<Avatar> equipAvatars = EquipUtil.getAvatarData(player.pro, playerBasePO, false);
		avatars.addAll(equipAvatars);
		// 时装
		List<Avatar> fashionAvatars = FashionUtil.getAvatarData(playerBasePO);
		Map<Integer, Avatar> targetMap = new HashMap<>();
		for (Avatar avatar : avatars) {
			targetMap.put(avatar.getTag(), avatar);
		}
		for (Avatar fashionAvatar : fashionAvatars) {
			int tag = fashionAvatar.getTag();
			if (targetMap.containsKey(tag)) {
				// 需要替换effectId,所以新建一个builder
				Avatar.Builder avatarBuilderNew = Avatar.newBuilder();
				avatarBuilderNew.setTag(tag);
				avatarBuilderNew.setFileName(fashionAvatar.getFileName());
				avatarBuilderNew.setEffectType(targetMap.get(tag).getEffectType());
				targetMap.put(tag, avatarBuilderNew.build());
			} else {
				targetMap.put(fashionAvatar.getTag(), fashionAvatar);
			}
		}
		List<Avatar> targetList = new LinkedList<>();
		for (Avatar avatar : targetMap.values()) {
			targetList.add(avatar);
		}
		return targetList;
	};

	/**
	 * 获取avatar信息
	 */
	public static List<Avatar> getBattlerServerAvatar4Login(int pro, PlayerBasePO playerBasePO) {
		List<Avatar> avatars = new ArrayList<>();

		List<Avatar> equipAvatars = EquipUtil.getAvatarData(pro, playerBasePO, false);
		avatars.addAll(equipAvatars);
		// 时装
		List<Avatar> fashionAvatars = FashionUtil.getAvatarData(playerBasePO);
		Map<Integer, Avatar> targetMap = new HashMap<>();
		for (Avatar avatar : avatars) {
			targetMap.put(avatar.getTag(), avatar);
		}
		for (Avatar fashionAvatar : fashionAvatars) {
			int tag = fashionAvatar.getTag();
			if (targetMap.containsKey(tag)) {
				// 需要替换effectId,所以新建一个builder
				Avatar.Builder avatarBuilderNew = Avatar.newBuilder();
				avatarBuilderNew.setTag(tag);
				avatarBuilderNew.setFileName(fashionAvatar.getFileName());
				avatarBuilderNew.setEffectType(targetMap.get(tag).getEffectType());
				targetMap.put(tag, avatarBuilderNew.build());
			} else {
				targetMap.put(fashionAvatar.getTag(), fashionAvatar);
			}
		}
		List<Avatar> targetList = new LinkedList<>();
		for (Avatar avatar : targetMap.values()) {
			targetList.add(avatar);
		}
		return targetList;
	};

	public static List<AvatarObj> getBattlerServerAvatarObj(WNPlayer source) {
		List<Avatar> list = getBattlerServerAvatar(source, true);
		List<AvatarObj> list_new = new ArrayList<>();
		for (Avatar avatar : list) {
			list_new.add(new AvatarObj(avatar.getTag(), avatar.getFileName(), avatar.getEffectType()));
		}
		return list_new;
	}

	/**
	 * 获取某在线玩家场景坐标信息
	 */
	public static JSONObject getPlayerNowPosition(String playerId) {
		WNPlayer player = getOnlinePlayer(playerId);
		if (player != null) {
			return getPlayerPosition(player);
		}
		return null;
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
			CharacterLevelCO prop = GameData.CharacterLevels.get(i);
			int needExp = prop.experience;
			if (curExp < needExp) {
				break;
			}
			curExp = curExp - needExp;
			level++;
			if (level == GlobalConfig.Role_LevelLimit) { // 如果到最高等级了就不加经验
				// curExp = 0;
				break;
			}
		}

		return new long[] { curExp, level };
	}

	/**
	 * 计算经验能够升到多少级
	 * 
	 * @param exp
	 * @param nowLevel
	 * @return
	 */
	public static int calExpLv(long exp, int nowLevel) {
		int level = nowLevel;
		long curExp = exp;
		for (int i = nowLevel; i <= GlobalConfig.Role_LevelLimit; i++) {
			CharacterLevelCO prop = GameData.CharacterLevels.get(i);
			int needExp = prop.experience;
			if (curExp < needExp) {
				break;
			}
			curExp = curExp - needExp;
			level++;
		}
		return level;
	}

	/**
	 * 根据transportId进行玩家传送操作
	 */
	public static boolean transPortById(WNPlayer player, int id) {

		TransportExt transportProp = (TransportExt) GameData.Transports.get(id);
		if (transportProp == null) {
			return false;
		}
		boolean flag = false;
		int transMod = transportProp.transMod;
		String modValue = transportProp.modValue;
		if (transMod == Const.TRANSPORT_CONDITION.NOTHING.value) {
			flag = true;
		} else if (transMod == Const.TRANSPORT_CONDITION.ACCEPT_TASK.value) {
			if (player.taskManager.isTaskDoing(Integer.parseInt(modValue))) {
				flag = true;
			}
		} else if (transMod == Const.TRANSPORT_CONDITION.FINISH_TASK.value) {
			if (player.taskManager.isFinishTask(Integer.parseInt(modValue))) {
				flag = true;
			}
		} else if (transMod == Const.TRANSPORT_CONDITION.HAS_ITEM.value) {
			if (player.getWnBag().findItemNumByCode(modValue) > 0) {
				flag = true;
			}
		} else if (transMod == Const.TRANSPORT_CONDITION.CONSUME_ITEM.value) {
			int itemNumber = transportProp.needNumber;
			if (player.getWnBag().findItemNumByCode(modValue) > itemNumber) {
				player.getWnBag().discardItem(modValue, 0, null); // TODO
																	// 这里js没有传
																	// num
				flag = true;
			}
		}

		if (flag) {
			int areaId = transportProp.targetMap;
			Map<String, Integer> targetPoint = transportProp.targetPoint_;
			if (targetPoint != null && targetPoint.size() > 0) {
				AreaUtil.enterArea(player, areaId, targetPoint.get("targetX"), targetPoint.get("targetY"));
			} else {
				AreaUtil.enterArea(player, areaId, 0, 0);
			}
			return true;
		}
		return false;
	};

	public static boolean isRobot(PlayerPO player) {
		return GWorld.ROBOT && player.uid.startsWith("robot") && !player.uid.endsWith("hoolai");
	}

	/**
	 * 
	 */
	public static AllBlobPO createPlayer(String playerId, String uid, String name, int pro, int logicServerId) {
		AllBlobPO allBlobData = new AllBlobPO();
		PlayerPO player = createPlayerPO(playerId, uid, name, pro, logicServerId);
		allBlobData.player = player;
		PlayerPOManager.put(ConstsTR.playerTR, playerId, player);

		allBlobData.playerBase = createBaseData(playerId);

		allBlobData.playerTemp = createTempData(playerId);

		PlayerAttachPO playerAttachPO = new PlayerAttachPO();
		allBlobData.playerAttachPO = playerAttachPO;
		playerAttachPO.firstKillMonsterIds = new ArrayList<>();

		createShopMall(playerId);

		allBlobData.tasks = createTasks(playerId, Const.PLAYER.initLevel);

		BagsPO bagsPO = new BagsPO(createBag(), createWareHouse(), createRecycle());
		PlayerPOManager.put(ConstsTR.bagTR, playerId, bagsPO);

		SkillsPO skills = createSkills(pro, Const.PLAYER.initLevel);
		PlayerPOManager.put(ConstsTR.skillTR, playerId, skills);

		playerAttachPO.sysSet = createSysSetManager();
		// 挂机设置
		allBlobData.hookSetData = createHookSetManager(playerId);

		createActivityManager(playerId);

		PlayerPOManager.put(ConstsTR.player_fightlevelTR, playerId, new FightLevelsPO());

		createAttendance(playerId);

		createTitle(playerId);

		createConsignment(playerId);

		createOnlineData(playerId, ONLINE_GIFT_TYPE.FIRST_DAY, player.upLevel, player.level);

		PlayerPOManager.put(ConstsTR.mailTR, playerId, new PlayerMailDataPO());

		PlayerPOManager.put(ConstsTR.player_dailyTR, playerId, new DailyActivityPO());

		PlayerPOManager.put(ConstsTR.player_func_openTR, playerId, new FunctionOpenPO());

		PlayerPOManager.put(ConstsTR.pkRuleTR, playerId, new PlayerPKDataPO());

		PlayerPOManager.put(ConstsTR.player_arena_dataTR, playerId, new ArenaDataPO());

		PlayerPOManager.put(ConstsTR.player_leaderboardTR, playerId, new LeaderBoardPlayerPO());

		PlayerPOManager.put(ConstsTR.player_friendsTR, playerId, new PlayerFriendsPO());

		playerAttachPO.sceneProgress = new HashMap<>();
		playerAttachPO.vipData = new VipPO();

		allBlobData.chouRens = new PlayerChouRenPO();
		PlayerPOManager.put(ConstsTR.player_chourenTR, playerId, allBlobData.chouRens);

		allBlobData.achievements = new AchievementDataPO();
		allBlobData.achievements.achievements = new HashMap<>(); // {awards:[],conditions:[]};
		playerAttachPO.medal = new MedalPO();
		PlayerPOManager.put(ConstsTR.achievementTR, playerId, allBlobData.achievements);

		PlayerPOManager.put(ConstsTR.playerPetTR, playerId, new PlayerPetsNewPO());

		PlayerPOManager.put(ConstsTR.player_solo_dataTR, playerId, new SoloDataPO(playerId));

		allBlobData.five2FivePo = Five2FiveService.getInstance().createFive2FivePO(playerId);
		PlayerPOManager.put(ConstsTR.five2FiveTR, playerId, allBlobData.five2FivePo);

		// 仙缘
		allBlobData.xianYuan = XianYuanService.getInstance().createXianYuan(playerId);
		PlayerPOManager.put(ConstsTR.xianYuanTR, playerId, allBlobData.xianYuan);

		CharacterExt characterProp = GameData.Characters.get(pro);
		// 初始携带道具
		List<InitItem> initItems = characterProp.initItems;
		int index = 1;
		for (int i = 0; i < initItems.size(); i++) {
			InitItem itemData = initItems.get(i);
			DItemEquipBase itemProp = ItemUtil.getPropByCode(itemData.itemCode);
			if (itemProp != null) {
				List<NormalItem> items = ItemUtil.createItemsByItemCode(itemData.itemCode, itemData.itemNum);
				for (int j = 0; j < items.size(); j++) {
					if (index <= bagsPO.bagData.bagGridCount) {
						NormalItem item = items.get(j);
						bagsPO.bagData.bagGrids.put(index, item.itemDb);
						index = index + 1;
					}
				}
			} else {
				Out.warn("角色初始化添加物品失败 player pro:", pro, "itemCode:", itemData.itemCode);
			}
		}

		// 初始携带装备
		allBlobData.playerBase.equipGrids = new HashMap<>();
		List<String> equips = isRobot(player) ? Arrays.asList("lweap1-test", "lclot1-test") : characterProp.initEquips;
		for (String code : equips) {
			// 创建装备并穿戴
			NormalEquip equip = (NormalEquip) ItemUtil.createItemsByItemCode(code, 1).get(0);
			equip.setBind(1);
			allBlobData.playerBase.equipGrids.put(equip.prop.itemSecondType, equip.itemDb);
		}

		PlayerPOManager.put(ConstsTR.bagTR, playerId, bagsPO);
		PlayerPOManager.put(ConstsTR.playerAttachTR, playerId, playerAttachPO);
		PlayerPOManager.put(ConstsTR.playerBaseTR, playerId, allBlobData.playerBase);

		// 统一保存到redis
		PlayerPOManager.sync(playerId);
		return allBlobData;
	}

	public static OnlineDataPO createOnlineData(String playerId, ONLINE_GIFT_TYPE giftType, int upLevel, int level) {
		OnlineDataPO onlineData = new OnlineDataPO();
		PlayerPOManager.put(ConstsTR.onlineGiftTR, playerId, onlineData);
		List<OlGiftExt> propList = OlGiftConfig.getInstance().getPropListByLevel(giftType.getValue(), upLevel, level);
		for (int i = 0; i < propList.size(); i++) {
			onlineData.rewardState.put(propList.get(i).giftId, GiftState.NO_RECEIVE.getValue());
		}
		return onlineData;
	}

	public static void createConsignment(String playerId) {
		PlayerConsignmentItemsPO po = new PlayerConsignmentItemsPO();
		PlayerPOManager.put(ConstsTR.player_consignmentTR, playerId, po);
	}

	public static void createTitle(String playerId) {
		TitlePO titlePO = new TitlePO();
		titlePO.awardRanks = new HashMap<>();
		titlePO.selectedRankId = 0;
		PlayerPOManager.put(ConstsTR.playerTitleTR, playerId, titlePO);
	}

	public static PlayerPO createPlayerPO(String playerId, String uid, String name, int pro, int logicServerId) {
		PlayerPO player = new PlayerPO();
		player.id = playerId;
		player.uid = uid;
		player.name = name;
		player.logicServerId = logicServerId;
		player.isDelete = 0;
		player.level = Const.PLAYER.initLevel;
		player.exp = Const.PLAYER.initExp;
		player.prestige = Const.PLAYER.initPrestige;
		player.pro = pro;
		// player.sp = Const.PLAYER.initSp;
		player.gold = Const.PLAYER.initGold;
		player.ticket = Const.PLAYER.initTicket;
		player.diamond = Const.PLAYER.initDiamond;
		player.totalCostDiamond = 0;
		// player.energy = Const.PLAYER.initEnergy;
		player.friendly = Const.PLAYER.initFriendly;
		// player.solopoint = Const.PLAYER.initSolopoint;
		player.consumePoint = 0;
		player.charm = Const.PLAYER.initCharm;
		player.pawnGold = 0;
		player.guildpoint = 0;
		player.treasurePoint = 0;
		player.vip = Const.VipType.none.value;
		Date date = new Date();
		player.isAcceptAutoTeam = GlobalConfig.TeamAutoInvite;
		player.createTime = date;
		player.loginTime = date;
		player.logoutTime = date;
		player.refreshTime = date;
		player.fightPower = 0;
		// player.guildId = "";
		// player.guildName = "";
		// player.guildJob = 0;
		// player.guildIcon = "";

		return player;
	}

	public static PlayerTempPO createTempData(String playerId) {
		PlayerTempPO tempData = new PlayerTempPO();
		tempData.historyAreaId = GlobalConfig.motherland;
		tempData.historyX = 0;
		tempData.historyY = 0;
		tempData.bornAreaId = GlobalConfig.motherland;
		tempData.bornX = 0;
		tempData.bornY = 0;
		tempData.x = 0;
		tempData.y = 0;
		tempData.hp = Const.PLAYER.initHp;
		tempData.mp = Const.PLAYER.initMp;
		tempData.areaId = GlobalConfig.motherland;

		PlayerPOManager.put(ConstsTR.playerTempTR, playerId, tempData);

		return tempData;
	};

	/**
	 * 创建玩家附属
	 */
	public static PlayerBasePO createBaseData(String playerId) {
		PlayerBasePO playerBasePO = new PlayerBasePO();
		playerBasePO.equipGrids = new HashMap<>();
		playerBasePO.strengthPos = new HashMap<>();
		// playerBasePO.fashions = new HashMap<>();
		int gridsCount = EquipType.values().length;
		for (int i = 1; i <= gridsCount; i++) {
			EquipSockExt sockCO = GameData.EquipSocks.get(i);
			playerBasePO.equipGrids.put(i, null);
			EquipStrengthPos pos = new EquipStrengthPos();
			pos.enSection = 0;
			pos.enLevel = 0;
			pos.socks = 0;
			pos.gems = new HashMap<>();
			for (int j = 1; j <= sockCO.sockOpenLevel.size(); j++) {
				// 初始给5个格子赋上空值
				// pos.gems.put(j, "");

				// 计算初始登记开发的孔数
				if (Const.PLAYER.initLevel >= sockCO.sockOpenLevel.get(j)) {
					pos.socks += 1;
				}
			}
			playerBasePO.strengthPos.put(i, pos);
		}
		return playerBasePO;
	}

	private static ShopMallPO createShopMall(String playerId) {
		Map<Integer, Map<Integer, Boolean>> seenTab = new HashMap<>();
		seenTab.put(Const.SHOP_MALL_CONSUME_TYPE.DIAMOND.getValue(), new HashMap<Integer, Boolean>());
		seenTab.put(Const.SHOP_MALL_CONSUME_TYPE.TICKET.getValue(), new HashMap<Integer, Boolean>());

		ShopMallPO shopMall = new ShopMallPO(seenTab);

		PlayerPOManager.put(ConstsTR.shopMallTR, playerId, shopMall);

		return shopMall;
	};

	private static ActivityDataPO createActivityManager(String playerId) {
		ActivityDataPO activityData = new ActivityDataPO();

		activityData.activityRewardRecorder = new HashMap<>();

		activityData.activityInfo = new HashMap<>();

		activityData.luckyAwardContainer = new ArrayList<>();
		activityData.drawedContainer = new HashMap<>();
		activityData.refreshTime = new Date();
		activityData.buffTimes = 1;

		PlayerPOManager.put(ConstsTR.activityTR, playerId, activityData);

		return activityData;
	};

	private static int createSysSetManager() {
		int ret = 1;
		ret |= SysSetFlag.recvMailSet.getValue();
		ret |= SysSetFlag.teamInviteSet.getValue();
		ret |= SysSetFlag.recvStrangerMsgSet.getValue();
		ret |= SysSetFlag.recvAddFriendSet.getValue();
		return ret;
	};

	public static HookSetPO createHookSetManager(String playerId) {
		HookSetPO hootSet = new HookSetPO(playerId);
		hootSet.hpPercent = GlobalConfig.Auto_HP_Percent;
		hootSet.mpPercent = GlobalConfig.Auto_MP_Percent;
		hootSet.hpItemCode = GlobalConfig.Auto_HP_Item;
		hootSet.mpItemCode = GlobalConfig.Auto_MP_Item;
		hootSet.pkSet = GlobalConfig.Auto_PK_Reaction;
		hootSet.meltQcolor.add(GlobalConfig.Auto_Eqip_Qcolor);
		hootSet.autoBuyHpItem = 1;
		hootSet.autoBuyMpItem = 0;
		hootSet.fieldMaphook = 0;
		hootSet.areaMaphook = 0;

		PlayerPOManager.put(ConstsTR.hookSetTR, playerId, hootSet);

		return hootSet;
	};

	public static AttendancePO createAttendance(String playerId) {
		AttendancePO sign = new AttendancePO();

		sign.stage = 1;
		sign.lastSignTime = DateUtil.getZeroDate();
		sign.signMap = new HashMap<>();
		sign.cumulativeMap = new HashMap<>();
		sign.lastLuxuryTime = DateUtil.getZeroDate();
		sign.luxuryState = GiftState.NO_RECEIVE.getValue();

		PlayerPOManager.put(ConstsTR.player_signTR, playerId, sign);
		return sign;
	}

	public static TaskListPO createTasks(String playerId, int level) {
		TaskListPO tasks = new TaskListPO();
		tasks.normalTasks = new HashMap<>();
		tasks.dailyTasks = new HashMap<>();
		tasks.treasureTasks = new HashMap<>();
		tasks.finishedNormalTasks = new HashMap<>();
		tasks.finishedDailyTasks = new HashMap<>();
		tasks.finishedTreasureTasks = new HashMap<>();
		tasks.loopResult = new LoopResult();
		// 第一个主线
		for (MainLineCO main : GameData.MainLines.values()) {
			if (main.before.equals("0")) {
				// 已接任务
				TaskData task = TaskUtils.createTask(main);
				tasks.normalTasks.put(task.db.templateId, task.toJson4Serialize());
				break;
			}
		}
		// 是否有支线可以接
		for (BranchLineCO branch : GameData.BranchLines.values()) {
			if (branch.before.equals("0") && branch.level <= level) {
				// 已接任务
				TaskData task = TaskUtils.createTask(branch);
				tasks.normalTasks.put(task.db.templateId, task.toJson4Serialize());
			}
		}

		PlayerPOManager.put(ConstsTR.taskTR, playerId, tasks);
		return tasks;
	};

	private static SkillsPO createSkills(int pro, int initLevel) {
		SkillsPO skills = SkillManager.initNewPlayerSkills(pro, initLevel);
		skills.skillKeys = createSkillKeys(pro);
		return skills;
	};

	private static Map<Integer, Integer> createSkillKeys(int pro) {
		Map<Integer, Integer> allSkillKeys = new HashMap<>();
		for (int j = 0; j < 11; j++) {
			allSkillKeys.put(j, -1);
		}
		Out.debug(PlayerUtil.class, "createSkillKeys: ", allSkillKeys);
		return allSkillKeys;
	};

	public static BagPO createBag() {
		BagPO data = new BagPO();
		data.bagGrids = new HashMap<>();
		data.bagGridCount = GlobalConfig.Package_DefaultSize;
		return data;
	};

	public static BagPO createWareHouse() {
		BagPO data = new BagPO();
		data.bagGrids = new HashMap<>();
		data.bagGridCount = GlobalConfig.PersonalWarehouse_DefaultSize;
		return data;
	};

	public static BagPO createRecycle() {
		BagPO data = new BagPO();
		data.bagGrids = new HashMap<>();
		data.bagGridCount = GlobalConfig.Package_MaxSize;
		return data;
	}

	public static String getRandomName(int pro) {
		String firstName = getFirstName();
		if (firstName.length() <= 0) {
			return null;
		}

		String lastName = getLastName(pro);

		if (lastName.length() <= 0) {
			return null;
		}

		// 越南中间需要添加一个空格
		if (GWorld.__SERVER_LANG == XLang.VN) {
			return firstName + " " + lastName;
		}

		return firstName + lastName;
	};

	public static String getFirstName() {
		List<PrefixCO> first_list = PlayerConfig.getInstance().randomname_prefix;
		if (first_list.size() <= 0) {
			return "";
		}
		int rndIndex = Utils.random(0, first_list.size() - 1);
		return first_list.get(rndIndex).prefix;
	};

	private static int _getSexByPro(int pro) {
		if (pro == Const.PlayerPro.CANG_LANG.value || pro == Const.PlayerPro.YI_XIAN.value || pro == Const.PlayerPro.SHEN_JIAN.value || pro == Const.PlayerPro.COMMON.value) {
			return 1;
		}
		return 0;
	};

	public static String getLastName(int pro) {
		List<SuffixCO> lastWomanList = PlayerConfig.getInstance().randomname_suffix.get(0);
		List<SuffixCO> lastManList = PlayerConfig.getInstance().randomname_suffix.get(1);

		int sex = _getSexByPro(pro);
		List<SuffixCO> last_list = null;
		if (sex == 0) {
			last_list = lastWomanList;
		} else {
			last_list = lastManList;
		}

		if (last_list == null || last_list.size() <= 0) {
			return "";
		}

		int rndIndex = Utils.random(0, last_list.size() - 1);
		return last_list.get(rndIndex).suffix;
	};

	public static void onFishItem(WNPlayer player, String tc, String extendTc) {
		Out.debug(PlayerUtil.class, "onFishItem tc:", tc, "  extendTc:", extendTc);
		List<NormalItem> items = ItemUtil.createItemsByTcCode(tc);
		Out.debug(PlayerUtil.class, "onFishItem:" + tc);
		List<NormalItem> extendItems = null;
		if (StringUtil.isNotEmpty(extendTc)) {
			extendItems = ItemUtil.createItemsByTcCode(extendTc);
			items.addAll(extendItems);
		}

		Out.debug(PlayerUtil.class, "onFishItem 2222:", items.size());
		if (items.size() <= 0) {
			player.sendSysTip(LangService.getValue("FISH_FAILED"), Const.TipsType.BLACK);
			return;
		}

		// if(items.length > 1){
		// sendSysMessageToPlayer("生成了多个道具", player.id);
		// return;
		// }

		// 检查背包格子数
		if (!player.bag.testEmptyGridLarge(ItemUtil.getPackUpItemsNum(items))) {
			// sendSysMessageToPlayer(tips.BAG_NOT_ENOUGH_POS, player.id);
			return;
		}

		if (extendItems != null) {
			for (NormalItem dropItem : extendItems) {
				player.taskManager.dealTaskEvent(TaskType.FISH, dropItem.itemDb.code, dropItem.itemDb.groupCount);
			}
		}

		player.bag.addEntityItems(items, Const.GOODS_CHANGE_TYPE.fish, null);
		List<MiniItem> miniItem = new ArrayList<>();
		// var biItems = []; TODO
		for (NormalItem v : items) {
			miniItem.add(ItemUtil.getMiniItemData(v.itemDb.code, v.itemDb.groupCount, Const.ForceType.getE(v.getBind())).build());
			// biItems.add({itemCode: v.itemDb.code, itemNum:
			// v.itemDb.groupCount});
		}
		player.onFishItem(miniItem);
	};

	/**
	 * 根据职业获取角色带颜色名字
	 * 
	 * @params pro 职业 playerName 名字
	 *
	 */
	public static String getColorPlayerNameByPro(int pro, String playerName) {
		// if (pro == Const.PlayerPro.CANG_LANG.value) {
		// playerName =
		// LangService.getValue("PLAYER_NAME_CANG_LANG").replace("{playerName}",
		// playerName);
		// } else if (pro == Const.PlayerPro.YU_JIAN.value) {
		// playerName =
		// LangService.getValue("PLAYER_NAME_YU_JIAN").replace("{playerName}",
		// playerName);
		// } else if (pro == Const.PlayerPro.YI_XIAN.value) {
		// playerName =
		// LangService.getValue("PLAYER_NAME_YI_XIAN").replace("{playerName}",
		// playerName);
		// } else if (pro == Const.PlayerPro.LI_NHU.value) {
		// playerName =
		// LangService.getValue("PLAYER_NAME_LI_NHU").replace("{playerName}",
		// playerName);
		// } else if (pro == Const.PlayerPro.SHEN_JIAN.value) {
		// playerName =
		// LangService.getValue("PLAYER_NAME_SHEN_JIAN").replace("{playerName}",
		// playerName);
		// }
		return playerName;
	};

	/// **
	// * 获取进阶名
	// * @params upLevel 阶级
	// * pro 职业
	// * */
	public static final String getUpLevelName(int upLevel, int pro) {
		String name = "";
		UpLevelExpExt prop = PlayerConfig.getInstance().findupLevelExpPropsByUpLevelAndPro(upLevel, pro);
		if (prop == null) {
			return name;
		}
		int qColor = prop.qcolor;

		String colorInfo = LangService.getValue("WHITE");
		if (qColor == 1) {
			colorInfo = LangService.getValue("GREEN");
		} else if (qColor == 2) {
			colorInfo = LangService.getValue("BLUE");
		} else if (qColor == 3) {
			colorInfo = LangService.getValue("PURPLE");
		} else if (qColor == 4) {
			colorInfo = LangService.getValue("ORANGE");
		} else if (qColor == 5) {
			colorInfo = LangService.getValue("RED");
		}
		colorInfo = colorInfo.replace("{a}", prop.uPName);
		return colorInfo;
	}

	//
	public static String getFullColorName(WNPlayer player) {
		StringBuilder name = new StringBuilder(getColorPlayerNameByPro(player.getPro(), player.getName()));
		name.append("(");
		String whiteColorInfo = LangService.getValue("WHITE");
		String pro = "";
		CharacterCO character = GameData.Characters.get(player.getPro());
		if (character != null) {
			pro = character.proName + ", ";
		}
		name.append(whiteColorInfo.replace("{a}", pro + player.getLevel() + "级"));
		// name.append(getUpLevelName(player.getPlayer().upLevel, player.getPro()));
		name.append(")");
		return name.toString();
	};

	public static int initCurMaxLv(PlayerPO baseData) {
		return GlobalConfig.Role_LevelLimit;
	}

	/**
	 * 获得所有的在线玩家
	 * 
	 * @returns {*}
	 */

	public static Collection<GPlayer> getAllOnlinePlayer() {
		return GWorld.getInstance().getOnlinePlayers().values();
	};

	public static void bi(Class<?> clazz, Const.BiLogType logType, WNPlayer player, Object... args) {
		if (player == null) {
			Out.error(logType.desc, " player is null");
			return;
		}
		bi(clazz, logType, player.player, args);
	}

	/**
	 * 记录BI日志
	 */
	public static void bi(Class<?> clazz, Const.BiLogType logType, PlayerPO player, Object... args) {
		if (!GWorld.ROBOT) {
			StringBuilder builder = new StringBuilder();
			builder.append(player.uid).append(", ").append(player.id).append(", ");
			builder.append(player.name).append(", ").append(player.logicServerId).append(", ");
			builder.append(player.pro).append(", ").append(player.level).append(",");
			builder.append(player.upLevel).append(", ").append(player.vip).append(", ");
			builder.append(player.fightPower).append(", ").append(player.gold).append(", ");
			builder.append(player.diamond).append(", ").append(player.ticket);
			for (Object arg : args) {
				builder.append(", ").append(arg);
			}
			Out.info(clazz.getName(), "::", logType.desc, "::, ", builder);
		}
	}

	public static void logWarnIfPlayerNull(Packet packet) {
		if (packet == null) {
			Out.warn("发现PlayerNUll.啥信息都没!");
			return;
		}
		Channel c = packet.getSession();
		if (c == null) {
			Out.warn("发现PlayerNUll.只有包信息!");
			return;
		}
		Out.warn("发现PlayerNUll.", c.remoteAddress());
	}

	public static void addLoginServer(String uid, int sid, int count) {
		String playerServers = AuthServer.get(AuthServer.K_PLAYER_SERVERS + uid);
		JSONArray loginServers = null;
		if (StringUtil.isNotEmpty(playerServers)) {
			loginServers = JSON.parseArray(playerServers);
		} else {
			loginServers = new JSONArray();
		}
		for (int i = loginServers.size() - 1; i >= 0; i--) {
			JSONObject json = loginServers.getJSONObject(i);
			if (json.getIntValue("sid") == sid) {
				if (json.getIntValue("count") == count) {
					return;
				}
				if (count == 0) {
					loginServers.remove(i);
				} else {
					json.put("count", count);
				}
				AuthServer.put(AuthServer.K_PLAYER_SERVERS + uid, loginServers.toJSONString());
				return;
			}
		}
		JSONObject json = new JSONObject();
		json.put("sid", sid); // 逻辑服ID
		json.put("count", count); // 角色数
		loginServers.add(json);
		AuthServer.put(AuthServer.K_PLAYER_SERVERS + uid, loginServers.toJSONString());
	}

}
