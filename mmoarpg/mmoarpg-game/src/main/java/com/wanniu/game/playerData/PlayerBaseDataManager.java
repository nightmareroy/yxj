package com.wanniu.game.playerData;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.core.logfs.Out;
import com.wanniu.core.util.StringUtil;
import com.wanniu.game.GWorld;
import com.wanniu.game.common.CommonUtil;
import com.wanniu.game.common.Const;
import com.wanniu.game.common.Const.DungeonHardModel;
import com.wanniu.game.common.Const.GOODS_CHANGE_TYPE;
import com.wanniu.game.common.Const.ManagerType;
import com.wanniu.game.common.Const.PlayerEventType;
import com.wanniu.game.common.Const.TaskType;
import com.wanniu.game.common.Const.VipType;
import com.wanniu.game.common.ConstsTR;
import com.wanniu.game.common.ModuleManager;
import com.wanniu.game.common.Utils;
import com.wanniu.game.common.msg.ErrorResponse;
import com.wanniu.game.data.CardCO;
import com.wanniu.game.data.CharacterLevelCO;
import com.wanniu.game.data.DungeonMapCO;
import com.wanniu.game.data.GameData;
import com.wanniu.game.data.UpLevelEventCO;
import com.wanniu.game.data.ext.UpLevelExpExt;
import com.wanniu.game.player.BILogService;
import com.wanniu.game.player.GlobalConfig;
import com.wanniu.game.player.PlayerUtil;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.player.bi.LogReportService;
import com.wanniu.game.poes.PlayerPO;
import com.wanniu.game.poes.PlayerRankInfoPO;
import com.wanniu.game.rank.RankType;
import com.wanniu.redis.PlayerPOManager;

import pomelo.area.PlayerHandler.SuperScriptType;

public class PlayerBaseDataManager extends ModuleManager {

	public WNPlayer player;
	public PlayerPO baseData;
	/**
	 * 由境界决定的角色等级上限
	 */
	private int curMaxLv;
	private CharacterLevelCO curLevelData;

	public PlayerBaseDataManager(WNPlayer player, PlayerPO baseData) {
		super();
		this.player = player;
		this.baseData = baseData;
		player.player = baseData;
		curLevelData = GameData.CharacterLevels.get(this.baseData.level);
		curMaxLv = PlayerUtil.initCurMaxLv(this.baseData);
	}

	/**
	 * 添加经验
	 */
	public void addExp(long exp, Const.GOODS_CHANGE_TYPE from, int teamExp) {
		if (exp <= 0)
			return;

		// 机器人等级限制
		if (PlayerUtil.isRobot(this.baseData)) {
			if (this.baseData.level >= GWorld.ROBOT_MAX_LEVEL) {
				return;
			}
		}
		// 大神福利(世界等级第一)加成
		long expAdd = 0;
		if ((from == GOODS_CHANGE_TYPE.monsterdrop || from == GOODS_CHANGE_TYPE.task) && player.getLevel() >= GlobalConfig.WorldExp_ReqLevel) {
			String playerId = RankType.LEVEL.getHandler().getFirstRankMemberId(GWorld.__SERVER_ID);
			if (StringUtil.isNotEmpty(playerId)) {
				PlayerPO result = PlayerPOManager.findPO(ConstsTR.playerTR, playerId, PlayerPO.class);
				expAdd = (long) Math.ceil(player.leaderBoardManager.getExpAdd(result) / 100f * exp);
			}
			exp += expAdd;
		}

		int preLevel = this.baseData.level;
		long preExp = this.baseData.exp;
		// 是否被境界的最高等级限制
		if (baseData.level == curMaxLv && this.baseData.exp >= curLevelData.experience) {
			// 如果已经被境界的最高等级限制了，就要对增加的经验做修改，我去年买了个表
			/*
			 * 当玩家被阶级的等级上限限制而无法升级时，会将获得的溢出经验累积起来，但是会有递减惩罚。 溢出经验累积惩罚规则：
			 * 惩罚经验=-5%+（当前溢出经验能够升到多少级-当前阶级的等级上限）X15%。惩罚值控制区间为0%-100%，超过了界限， 就等于界限值。
			 * 实际获得经验=原获得经验X（1-惩罚百分比）
			 * 
			 */
			int canUpgradeLv = PlayerUtil.calExpLv(this.baseData.exp, this.baseData.level);
			// double punishRate = -GlobalConfig.Exp_RoleRank_Basic / 10000 +
			// (canUpgradeLv - curMaxLv) *
			// GlobalConfig.Exp_RoleRank_PenaltyCoefficient / 10000;
			// int realAddExp = (int) (exp * (1 - punishRate));
			int punishRate = (-GlobalConfig.Exp_RoleRank_Basic + (canUpgradeLv - curMaxLv) * GlobalConfig.Exp_RoleRank_PenaltyCoefficient);
			if (punishRate > 10000) {
				punishRate = 10000;
			}
			long realAddExp = (long) (exp * (10000 - punishRate)) / 10000;
			expAdd = (long) (expAdd * (10000 - punishRate)) / 10000;
			this.baseData.exp = this.baseData.exp + realAddExp;
			if (this.baseData.exp >= Const.NUMBER_MAX.INT) {
				this.baseData.exp = Const.NUMBER_MAX.INT;
			}
		} else {
			// 没有限制的话就直接加经验，然后升级
			this.baseData.exp = this.baseData.exp + exp;
			if (this.baseData.exp >= Const.NUMBER_MAX.INT) {
				this.baseData.exp = Const.NUMBER_MAX.INT;
			}
			if (this.baseData.exp >= curLevelData.experience && baseData.level < curMaxLv) {
				this._upgrade(preExp);

				// 升级的时候服务器自己飘字(客户端只比较了两次经验，升级之后经验可能会减少)
				long expGot = player.player.exp - preExp;
				for (int lv = preLevel; lv < this.baseData.level; lv++) {
					expGot += GameData.CharacterLevels.get(lv).experience;
				}
				String addStr = LangService.getValue("GAIN_EXP1");
				addStr = addStr.replace("%s", String.valueOf(expGot));
				player.sendSysTip(addStr, Const.TipsType.LEFTDWON);
				if (expAdd > 0) {
					player.pushChatSystemMessage(Const.SYS_CHAT_TYPE.EXP, String.valueOf(expGot), expAdd, null);
				} else {
					player.pushChatSystemMessage(Const.SYS_CHAT_TYPE.EXP, String.valueOf(expGot), null, null);
				}
			}
		}

		if (teamExp > 0) {
			player.pushChatSystemMessage(Const.SYS_CHAT_TYPE.TEAM_EXP, String.valueOf(exp), String.valueOf(teamExp), null);
		}

		if (expAdd > 0 && this.baseData.exp - preExp > 0) {
			player.pushDynamicData("exp", player.player.exp + ":" + expAdd);
		} else {
			player.pushDynamicData("exp", player.player.exp);
		}

		BILogService.getInstance().recordNum(player, Const.BiLogType.Exp, exp, from);
	}

	public void _upgrade(long preExp) {
		long[] finalData = PlayerUtil.getLevelByExp(this.baseData.exp, this.baseData.level, curMaxLv);
		int finalLevel = (int) finalData[1];
		this.upgrade(finalLevel, finalData[0]);
	}

	/**
	 * 升级.
	 * 
	 * @param level 目标等级.
	 * @param exp 目标经验
	 */
	public void upgrade(int level, long exp) {
		// 机器人等级限制
		if (PlayerUtil.isRobot(this.baseData)) {
			level = Math.min(GWorld.ROBOT_MAX_LEVEL, level);
		}

		if (this.baseData.level < level) {
			this.baseData.level = level;
			this.baseData.exp = exp;
			Out.info("角色升级 playerId=", baseData.id, ",name=", baseData.name, ",level=", baseData.level);
			if (!player.isRobot()) {
				player.rankManager.onEvent(RankType.LEVEL, player.getLevel(), player.getUpOrder());

				LogReportService.getInstance().ansycReportUpgrade(player);
				BILogService.getInstance().ansycReportPlayerData(player.getSession(), player.getPlayer(), true);
			}
		}

		CharacterLevelCO levelData = GameData.CharacterLevels.get(this.baseData.level);
		player.player.needExp = levelData.experience;
		curLevelData = GameData.CharacterLevels.get(this.baseData.level);
		player.onUpgrade();
		player.pushDynamicData(Utils.ofMap("level", player.player.level, "needExp", player.player.needExp));
	}

	/**
	 * 向客户端推送可突破提示图标
	 */
	public void sendIconMsgType() {
		String result = upgradeClass(true);
		if (result == null) {
			CommonUtil.sendIconMsgType(Const.MESSAGE_TYPE.upLevel_up, this.player.getId());
			updateSuperScript();
		}
	}

	public void addClassExp(int exp, GOODS_CHANGE_TYPE from) {
		// 大神福利(世界等级第一)加成
		long expAdd = 0;
		if (player.getLevel() >= GlobalConfig.WorldExp_ReqLevel && Const.GOODS_CHANGE_TYPE.use != from) {
			String playerId = RankType.LEVEL.getHandler().getFirstRankMemberId(GWorld.__SERVER_ID);
			if (StringUtil.isNotEmpty(playerId)) {
				PlayerPO result = PlayerPOManager.findPO(ConstsTR.playerTR, playerId, PlayerPO.class);
				expAdd = (long) Math.ceil(player.leaderBoardManager.getExpAdd(result) / 100f * exp);
			}
			exp += expAdd;
		}

		baseData.classExp += exp;
		Out.info("add classexp. playerId=", player.getId(), ",classexp=", baseData.classExp, ",value=", exp, ",from=", from == null ? 0 : from.value);

		player.pushDynamicData("classExp", player.player.classExp);
		sendIconMsgType();
		// player.pushChatSystemMessage(Const.SYS_CHAT_TYPE.CLASS_EXP,
		// String.valueOf(exp), null, null);
	}

	public String upgradeClass() {
		return upgradeClass(false);
	}

	public UpLevelExpExt getNextUpLevelExp() {
		int upOrderId_next = baseData.upOrder + 1;
		UpLevelExpExt prop_next = GameData.UpLevelExps.get(upOrderId_next);
		return prop_next;
	}

	/**
	 * 进阶
	 * 
	 * @param justCheck 如果为真，就只做条件检查，不会去升级
	 * @return null成功或者可以突破，其他值表示条件不满足
	 */
	public String upgradeClass(boolean justCheck) {
		if (baseData.upOrder >= PlayerUtil.maxUpOrder)
			return LangService.getValue("UPGRADE_MAX_LEVEL");
		UpLevelExpExt prop_next = getNextUpLevelExp();
		int classID_next = prop_next.classID;

		if (prop_next.reqClassExp > baseData.classExp)
			return LangService.getValue("UPGRADE_NEED_UPLEVELEXP");
		if (prop_next.reqLevel > baseData.level) {
			// 如果这是最高级，提示就要换一下
			if (player.getLevel() >= GlobalConfig.Role_LevelLimit) {
				return LangService.getValue("UPGRADE_MAX_LEVEL");
			}
			return LangService.getValue("UPGRADE_NEED_LEVEL").replace("{x}", "" + prop_next.reqLevel);
		}
		// 这里还要判断一下UpLevelEvent（进阶条件，要什么完成屌毛的任务，WQNMLGB）
		if (StringUtil.isNotEmpty(prop_next.reqEvents)) {
			String result = checkClassEvent(prop_next);
			if (result != null)
				return result;
		}
		if (justCheck)
			return null;

		baseData.classExp -= prop_next.reqClassExp;
		baseData.upOrder++;
		baseData.upLevel = classID_next;
		player.pushDynamicData(Utils.ofMap("classExp", player.player.classExp, "upOrder", player.player.upOrder));
		curMaxLv = PlayerUtil.initCurMaxLv(this.baseData);
		player.onClassUpgrade();

		Out.info("玩家进阶成功 playerId=", player.getId(), ", name=", player.getName(), ", upOrder=", baseData.upOrder);

		int oldLevel = baseData.level;
		if (this.baseData.exp >= curLevelData.experience && baseData.level < curMaxLv) {
			long preExp = this.baseData.exp;
			this._upgrade(preExp);
		}
		if (baseData.level == oldLevel && player.rankManager != null) {
			// 临时屏蔽此参数变更
			// player.rankManager.onEvent(RankType.LEVEL, baseData.level, baseData.upOrder);
			// 修正个人信息
			PlayerRankInfoPO info = player.rankManager.getRankPO();
			if (info != null) {
				info.setUpOrder(baseData.upOrder);
			}
		}

		// 更新任务状态
		this.player.taskManager.dealTaskEvent(TaskType.ROLE_UPGRADE, "", baseData.upLevel);
		// player.getPlayerTasks().onUpLevelChange(upLevel);

		player.achievementManager.playerRankChange(baseData.upLevel);

		return null;
	}

	/**
	 * 检查通关条件
	 * 
	 * @param prop_next
	 * @return null表示通过
	 */
	public String checkClassEvent(UpLevelExpExt prop_next) {
		if (StringUtil.isNotEmpty(prop_next.reqEvents)) {
			int reqEvents = Integer.parseInt(prop_next.reqEvents);
			UpLevelEventCO event = GameData.UpLevelEvents.get(reqEvents);
			if (event == null)
				return null;
			int eventPar = event.eventPar;
			DungeonMapCO dungeonMap = GameData.DungeonMaps.get(eventPar);
			if (dungeonMap == null) {
				return null;
			}
			int templateID = dungeonMap.templateID;
			int hardModel = dungeonMap.hardModel;
			int hard = player.fightLevelManager.getCurrHard(templateID);
			if (hard > hardModel)
				return null;
			else {
				DungeonHardModel dhm = DungeonHardModel.getE(hardModel);
				if (dhm == null)
					return null;
				StringBuffer hardModel_str = new StringBuffer("<f color='");
				hardModel_str.append(dhm.color);
				hardModel_str.append("'>");
				hardModel_str.append(dhm.desc).append("</f>");

				StringBuffer dungeonName = new StringBuffer("<f color='");
				dungeonName.append(dhm.color);
				dungeonName.append("'>");
				dungeonName.append(dungeonMap.name).append("</f>");
				return LangService.getValue("UPGRADE_NEED_OPTION").replace("{HardModel}", hardModel_str.toString()).replace("{Dungeon}", dungeonName.toString());
			}
		}
		return null;
	}

	/** 增加声望 */
	public void addPrestige(int prestige, Const.GOODS_CHANGE_TYPE from) {
		if (prestige >= 0) {
			this.baseData.prestige = this.baseData.prestige + prestige;
			if (this.baseData.prestige >= Const.NUMBER_MAX.INT) {
				this.baseData.prestige = Const.NUMBER_MAX.INT;
			}
			// if (from != Const.GOODS_CHANGE_TYPE.monsterdrop) {
			// this.customTip(Const.CUSTOMTIPTYPE.PRESTIGE, prestige);
			// }
			// this.medal.onAddPrestige();
			// this.update();
		}
	};

	/**
	 * 获得友情度
	 */
	public boolean addFriendly(int num) {
		if (num > 0) {
			this.baseData.friendly = this.baseData.friendly + num;
			if (this.baseData.friendly >= Const.NUMBER_MAX.INT) {
				this.baseData.friendly = Const.NUMBER_MAX.INT;
			}
			return true;
		}
		return false;
	};

	/**
	 * 消耗友情度
	 */
	public boolean costFriendly(int num) {
		if (num <= 0 || this.baseData.friendly < num) {
			return false;
		} else {
			this.baseData.friendly = this.baseData.friendly - num;
			return true;
		}
	};

	public boolean enoughFriendly(int num) {
		return this.baseData.friendly >= num;
	};

	/**
	 * 获取仓库贡献值
	 * 
	 * @returns {*}
	 */
	public int getPawnGold() {
		return baseData.pawnGold;
	}

	/**
	 * 判断是否有足够的仓库贡献值
	 */
	public boolean enoughPawnGold(int num) {
		return (this.baseData.pawnGold >= num);
	}

	/**
	 * 添加仓库贡献值
	 */
	public boolean addPawnGold(int num) {
		if (num > 0) {
			baseData.pawnGold += num;
			if (baseData.pawnGold >= Const.NUMBER_MAX.INT) {
				baseData.pawnGold = Const.NUMBER_MAX.INT;
			}
			return true;
		}
		return false;
	}

	/**
	 * 消耗仓库贡献值
	 */
	public boolean costPawnGold(int num) {
		if (num < 0 || baseData.pawnGold < num) {
			return false;
		} else {
			baseData.pawnGold -= num;
			return true;
		}
	};

	/**
	 * 判断是否有足够的君王宝藏积分
	 */
	public boolean enoughTreasurePoint(int num) {
		return (this.baseData.treasurePoint >= num);
	};

	/**
	 * 添加君王宝藏积分
	 */
	public boolean addTreasurePoint(int num) {
		if (num > 0) {
			this.baseData.treasurePoint = this.baseData.treasurePoint + num;
			if (this.baseData.treasurePoint >= Const.NUMBER_MAX.INT) {
				this.baseData.treasurePoint = Const.NUMBER_MAX.INT;
			}
			return true;
		}
		return false;
	};

	/**
	 * 消耗君王宝藏积分
	 */
	public boolean costTreasurePoint(int num) {
		if (num < 0 || this.baseData.treasurePoint < num) {
			return false;
		} else {
			this.baseData.treasurePoint = this.baseData.treasurePoint - num;
			return true;
		}
	};

	/**
	 * 添加公会副本积分
	 */
	public boolean addGuildPoint(int num) {
		if (num > 0) {
			this.baseData.guildpoint = this.baseData.guildpoint + num;
			if (this.baseData.guildpoint >= Const.NUMBER_MAX.INT) {
				this.baseData.guildpoint = Const.NUMBER_MAX.INT;
			}
			return true;
		}
		return false;
	};

	/**
	 * 设置登录时间
	 */
	public void setLoginTime(Date loginTime) {
		this.baseData.loginTime = loginTime;
	};

	/**
	 * 设置登出时间
	 */
	public void setLogoutTime(Date logoutTime) {
		this.baseData.logoutTime = logoutTime;
	};

	/**
	 * 获取声望值
	 */
	public int getPrestige() {
		return this.baseData.prestige;
	};

	// public void modifyVip(int vip, Date vipEndTime){
	// baseData.vip = vip;
	// if(baseData.vipEndTime!=null &&
	// baseData.vipEndTime.after(Calendar.getInstance().getTime())){
	// Calendar c = Calendar.getInstance();
	// c.setTime(baseData.vipEndTime);
	//
	// }
	// baseData.vipEndTime = vipEndTime;
	// }

	/**
	 * 更改vip类型，特别要注意的是SB策划和运营竟然允许既有月卡又有终身卡，所以有新怪胎产物：双卡，真的tmd一群脑残
	 * 
	 * @param vip
	 * @param lastTime
	 */
	public void modifyVip(int vip, int lastTime) {
		// 如果同事拥有月卡和终身卡，就变异成为怪胎双卡,SB策划运营
		final VipType vt = VipType.getE(vip);
		// 调用一下，清除过期
		getVip();
		if (vt == null) {
			return;
		}

		// 开通的是月卡...
		if (vt == Const.VipType.month) {
			if (baseData.vip == Const.VipType.forever.value || baseData.vip == Const.VipType.sb_double.value) {
				baseData.vip = Const.VipType.sb_double.value;
			} else {
				baseData.vip = vt.value;
			}
		}
		// 开通的是尊享卡
		else if (vt == Const.VipType.forever) {
			if (baseData.vip == Const.VipType.month.value || baseData.vip == Const.VipType.sb_double.value) {
				baseData.vip = Const.VipType.sb_double.value;
			} else {
				baseData.vip = vt.value;
			}
		}

		// 如果是月卡，重新计算月卡结束时间...
		if (vt == Const.VipType.month) {
			CardCO cardProp = GameData.Cards.get(vip);
			Calendar c = Calendar.getInstance();
			if (baseData.vipEndTime != null && baseData.vipEndTime.after(Calendar.getInstance().getTime())) {
				c.setTime(baseData.vipEndTime);
				c.add(Calendar.DAY_OF_MONTH, cardProp.lastTime);
				baseData.vipEndTime = c.getTime();
			} else {
				c.add(Calendar.DAY_OF_MONTH, cardProp.lastTime);
				baseData.vipEndTime = c.getTime();
			}
		}
	}

	public int getVip() {
		// 月卡要判断是否过期
		if (baseData.vip == VipType.month.value || baseData.vip == VipType.sb_double.value) {
			if (baseData.vipEndTime.before(Calendar.getInstance().getTime())) {
				baseData.vipEndTime = null;
				if (baseData.vip == VipType.month.value)
					baseData.vip = Const.VipType.none.value;
				else
					baseData.vip = Const.VipType.forever.value;
			}
		}
		return this.baseData.vip;
	}

	@Override
	public void onPlayerEvent(PlayerEventType eventType) {
		switch (eventType) {
		case UPGRADE:
			sendIconMsgType();
			break;

		default:
			break;
		}

	}

	@Override
	public ManagerType getManagerType() {
		return ManagerType.BASE_DATA;
	}

	public List<SuperScriptType> getSuperScript() {
		UpLevelExpExt prop_next = getNextUpLevelExp();
		String flag = checkClassEvent(prop_next);
		List<SuperScriptType> list = new ArrayList<>();
		SuperScriptType.Builder data = SuperScriptType.newBuilder();

		data.setType(Const.SUPERSCRIPT_TYPE.UPLEVEL.getValue());
		data.setNumber(flag == null ? 1 : 0);

		list.add(data.build());
		return list;
	}

	/** 更新角标 */
	public final void updateSuperScript() {
		if (player != null) {
			player.updateSuperScriptList(getSuperScript());
		}
	}

	public void updateLvChangeTime(Date time) {
		baseData.lvChangeTime = time;
	}

	public void updateFightTime(Date time) {
		baseData.fightChangeTime = time;
	}

	public Date getLvChangeTime() {
		return baseData.lvChangeTime;
	}

	public Date getFightChangeTime() {
		return baseData.fightChangeTime;
	}

	// 兑换属性
	public PomeloResponse exchange(int type) {
		// 满级才可以兑换...
		if (player.getLevel() < GlobalConfig.Exchange_OpenLV) {
			Out.warn("未满级也想兑换属性吗？playerId=", player.getId());
			return new ErrorResponse(LangService.getValue("PARAM_ERROR"));
		}

		switch (type) {
		case 1:// 1=经验兑换
			return this.exchangeByExp();
		case 2:// 2=修为兑换
			return this.exchangeByUpexp();
		case 3:// 3=银两兑换
			return this.exchangeByGold();
		default:
			return new ErrorResponse(LangService.getValue("PARAM_ERROR"));
		}
	}

	private PomeloResponse exchangeByGold() {
		final int needGold = GlobalConfig.Exchange_Gold;

		if (!player.moneyManager.costGold(needGold, Const.GOODS_CHANGE_TYPE.EXCHANGE_PROPARTY)) {
			return new ErrorResponse(LangService.getValue("NOT_ENOUGH_GOLD_LEARN"));
		}

		player.getPlayer().exchangCount++;

		Out.info("银两兑换属性成功. playerId=", player.getId(), ",count=", player.getPlayer().exchangCount);

		// 刷新属性
		player.onExchangeProparty();

		// 正常逻辑返回null
		return null;
	}

	private PomeloResponse exchangeByUpexp() {
		final int needUpExp = GlobalConfig.Exchange_UpExp;
		if (player.getPlayer().classExp < needUpExp) {
			return new ErrorResponse(LangService.getValue("PLAER_EXCHANGE_UPEXP_NOT_ENOUGH"));
		}

		player.getPlayer().classExp -= needUpExp;
		player.getPlayer().exchangCount++;
		Out.info("修为兑换属性成功. playerId=", player.getId(), ",count=", player.getPlayer().exchangCount);
		player.pushDynamicData("classExp", player.player.classExp);

		// 刷新属性
		player.onExchangeProparty();

		// 正常逻辑返回null
		return null;
	}

	private PomeloResponse exchangeByExp() {
		final int needExp = GlobalConfig.Exchange_Exp;
		if (player.getExp() < needExp) {
			return new ErrorResponse(LangService.getValue("PLAER_EXCHANGE_EXP_NOT_ENOUGH"));
		}

		player.getPlayer().exp -= needExp;
		player.getPlayer().exchangCount++;
		Out.info("经验兑换属性成功. playerId=", player.getId(), ",count=", player.getPlayer().exchangCount);
		player.pushDynamicData("exp", player.player.exp);

		// 刷新属性
		player.onExchangeProparty();

		// 正常逻辑返回null
		return null;
	}
}