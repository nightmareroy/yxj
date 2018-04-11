package com.wanniu.game.guild;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ScheduledFuture;
import java.util.function.Predicate;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.wanniu.core.game.JobFactory;
import com.wanniu.core.game.LangService;
import com.wanniu.core.logfs.Out;
import com.wanniu.core.util.DateUtil;
import com.wanniu.game.GWorld;
import com.wanniu.game.area.Area;
import com.wanniu.game.common.Const;
import com.wanniu.game.common.Const.GOODS_CHANGE_TYPE;
import com.wanniu.game.common.Const.TaskType;
import com.wanniu.game.common.Const.TipsType;
import com.wanniu.game.common.ConstsTR;
import com.wanniu.game.common.HackerException;
import com.wanniu.game.common.msg.MessageUtil;
import com.wanniu.game.data.BlessItemCO;
import com.wanniu.game.data.BlessLevelCO;
import com.wanniu.game.data.GameData;
import com.wanniu.game.data.GuildContributeCO;
import com.wanniu.game.data.GuildLevelCO;
import com.wanniu.game.data.GuildPositionCO;
import com.wanniu.game.data.WareHouseValueCO;
import com.wanniu.game.data.ext.GuildSettingExt;
import com.wanniu.game.guild.GuildMsg.BlessRefreshGuildMsg;
import com.wanniu.game.guild.GuildMsg.DepotRefreshGuildMsg;
import com.wanniu.game.guild.GuildMsg.DungeonPassGuildMsg;
import com.wanniu.game.guild.GuildMsg.DungeonPlayerNumGuildMsg;
import com.wanniu.game.guild.GuildMsg.JoinGuildBlessMsg;
import com.wanniu.game.guild.GuildMsg.OnChatGuildMsg;
import com.wanniu.game.guild.GuildMsg.RefreshGuildMsg;
import com.wanniu.game.guild.GuildMsg.TechRefreshGuildMsg;
import com.wanniu.game.guild.GuildResult.DepotUpgradeLevelData;
import com.wanniu.game.guild.GuildResult.GuildBlessActionData;
import com.wanniu.game.guild.GuildResult.GuildGiftAndBuffData;
import com.wanniu.game.guild.GuildResult.JoinGuild;
import com.wanniu.game.guild.GuildResult.MyGuildMember;
import com.wanniu.game.guild.GuildResult.PlayerOnlineRefreshGuild;
import com.wanniu.game.guild.GuildResult.UpgradeLevel;
import com.wanniu.game.guild.dao.FindPlayerGuildDao;
import com.wanniu.game.guild.dao.GuildDao;
import com.wanniu.game.guild.guidDepot.GuildCond;
import com.wanniu.game.guild.guidDepot.GuildDepotCondition;
import com.wanniu.game.guild.guidDepot.GuildRecordData;
import com.wanniu.game.guild.guildBless.GuildBless;
import com.wanniu.game.guild.guildBless.GuildBlessCenter;
import com.wanniu.game.guild.guildDungeon.GuildDungeonResult;
import com.wanniu.game.guild.guildShop.GuildShopManager;
import com.wanniu.game.guild.guildTech.GuildTechManager;
import com.wanniu.game.guild.po.GuildBlessPO;
import com.wanniu.game.item.ItemUtil;
import com.wanniu.game.item.NormalItem;
import com.wanniu.game.item.VirtualItemType;
import com.wanniu.game.item.po.PlayerItemPO;
import com.wanniu.game.mail.SysMailConst;
import com.wanniu.game.player.AttributeUtil;
import com.wanniu.game.player.GlobalConfig;
import com.wanniu.game.player.PlayerUtil;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.player.bi.LogReportService;
import com.wanniu.game.player.po.MiscData;
import com.wanniu.game.poes.GuildApplyPO;
import com.wanniu.game.poes.GuildMemberPO;
import com.wanniu.game.poes.GuildPO;
import com.wanniu.game.poes.PlayerGuildPO;
import com.wanniu.game.poes.PlayerPO;
import com.wanniu.game.util.BlackWordUtil;
import com.wanniu.redis.GameDao;
import com.wanniu.redis.PlayerPOManager;

import io.netty.util.internal.StringUtil;
import pomelo.area.GuildBlessHandler;
import pomelo.area.GuildBlessHandler.BlessRefreshPush;
import pomelo.area.GuildBlessHandler.MyBlessInfo;
import pomelo.area.GuildDepotHandler.DepotRefreshPush;
import pomelo.area.GuildHandler.ApplyInfo;
import pomelo.area.GuildHandler.ContributeTimesInfo;
import pomelo.area.GuildHandler.GuildBaseInfo;
import pomelo.area.GuildHandler.GuildDungeonOpenPush;
import pomelo.area.GuildHandler.GuildDungeonPassPush;
import pomelo.area.GuildHandler.GuildDungeonPlayerNumPush;
import pomelo.area.GuildHandler.GuildInfo;
import pomelo.area.GuildHandler.GuildInvitePush;
import pomelo.area.GuildHandler.GuildRefreshPush;
import pomelo.area.GuildHandler.MemberInfo;
import pomelo.area.GuildHandler.MyContributeInfo;
import pomelo.area.GuildHandler.MyGuildInfo;
import pomelo.area.GuildHandler.OfficeName;
import pomelo.area.GuildHandler.RecordInfo;
import pomelo.area.GuildShopHandler.ShopRefreshPush;
import pomelo.area.GuildTechHandler.GuildTechRefreshPush;
import pomelo.area.PlayerHandler.SuperScriptType;
import pomelo.item.ItemOuterClass.MiniItem;

public class GuildManager {

	public GuildPO guild;

	public GuildMemberPO member;

	public PlayerGuildPO guildData;

	public GuildShopManager guildShopManager;

	public GuildTechManager guildTechManager;

	public int throwAwardState;
	private WNPlayer player;
	public ScheduledFuture<?> timer = null;

	public GuildManager(WNPlayer player, PlayerGuildPO guildData) {
		this.player = player;
		if (null == guildData) {
			this.guildData = new PlayerGuildPO();
			this.guildData.refreshTime = new Date();
			PlayerPOManager.put(ConstsTR.playerGuildTR, player.getId(), this.guildData);
		} else {
			this.guildData = guildData;
		}

		init();
	}

	public void cancelTimer() {
		if (null != timer) {
			timer.cancel(true);
			timer = null;
		}
	}

	public void clearBuffTime() {
		guildData.buffTime = 0;
		clearBlessBuff();
		changeBlessBuff();
		cancelTimer();
	}

	public void startTimer() {
		cancelTimer();
		if (guildData.buffTime > 0) {
			timer = JobFactory.addScheduleJob(new Runnable() {
				@Override
				public void run() {
					guildData.buffTime--;
					if (guildData.buffTime <= 0) {
						clearBuffTime();
					}
				}
			}, 1, Const.Time.Second.getValue());
		} else {
			clearBuffTime();
		}
	}

	// 刷新buff时间
	public void updateBuffTime() {
		// 离线时间
		Date nowTime = new Date();
		Date logoutTime = this.player.getPlayer().logoutTime;
		long leaveTime = nowTime.getTime() - logoutTime.getTime();
		this.guildData.buffTime -= leaveTime * 0.001;
	}

	public void init() {
		this.guildShopManager = new GuildShopManager(player);
		this.guildTechManager = new GuildTechManager(player);
		syncGuildInfo(false);
	}

	public void updateGuildTimer() {
		startTimer();
		updateBuffTime();
	}

	public void syncGuildInfo(boolean isNotifyBattleServer) {
		GuildMemberPO myInfo = GuildUtil.getGuildMember(player.getId());
		if (myInfo != null) {
			GuildPO myGuild = GuildUtil.getGuild(myInfo.guildId);
			if (null != myGuild) {
				this.member = myInfo;
				this.guild = myGuild;
				if (isNotifyBattleServer) {
					this.player.refreshBattlerServerBasic();
				}
				return;
			}
		}
	}

	// 角色登陆
	public void onLogin() {
		updateGuildTimer();
		this.playerOnlineRefreshGuildData(); // 上线通知公会，并拉取公会数据
		this.refreshGuildInfo(false);
		if (StringUtil.isNullOrEmpty(getGuildId())) { // 没有公会，如果是公会pk模式，重置为和平模式
			this.player.pkRuleManager.onExitGuild();
		} else {
			// 有公会，每次上线检测身上有没有加入公会任务，有则完成
			this.player.taskManager.dealTaskEvent(TaskType.ADD_GUILD, "1", 1);
		}

		pushRedPoint();
	}

	/**
	 * 检测获取道具是否需要推送公会捐献红点
	 * 
	 * @param items
	 */
	public void bagAddItems(List<NormalItem> items) {
		if (!this.player.functionOpenManager.isOpen(Const.FunctionType.GUILD.getValue()) || !this.isInGuild()) {
			return;
		}

		boolean _isNeedPush = true;
		for (int i = 0; i < items.size(); i++) {
			String _code = items.get(i).itemDb.code;
			List<GuildContributeCO> ls = GameData.findGuildContributes(new Predicate<GuildContributeCO>() {
				@Override
				public boolean test(GuildContributeCO co) {
					return co.costItem == _code;
				}
			});

			if (ls.size() > 0) {
				_isNeedPush = false;
				break;
			}
		}

		if (_isNeedPush) {
			pushRedPoint();
		}
	}

	public void bagDelItem(String code) {
		if (!this.player.functionOpenManager.isOpen(Const.FunctionType.GUILD.getValue()) || !this.isInGuild()) {
			return;
		}

		List<GuildContributeCO> ls = GameData.findGuildContributes(new Predicate<GuildContributeCO>() {
			@Override
			public boolean test(GuildContributeCO co) {
				return co.costItem == code;
			}
		});

		if (ls.size() > 0) {
			pushRedPoint();
		}
	}

	/**
	 * 判断公会祈福礼包是否推送红点
	 * 
	 * @return
	 */
	public boolean isCanRecBlessGift() {
		boolean canPush = false;
		for (int i = 0; i < this.guildData.blessRecState.length; i++) {
			if (this.guildData.blessRecState[i] == Const.EVENT_GIFT_STATE.CAN_RECEIVE.getValue()) {
				canPush = true;
				break;
			}
		}
		return canPush;
	}

	public void pushRedPoint() {
		if (!this.player.functionOpenManager.isOpen(Const.FunctionType.GUILD.getValue()) || !this.isInGuild()) {
			return;
		}

		boolean isPushRedPoint = false;
		if (checkIsCanContribute() || isCanRecBlessGift() || this.player.guildBossManager.needUpdateRedPoint() || this.player.auctionManager.canGuildPush() || this.player.auctionManager.needUpdateRedPoint() || this.player.guildFortManager.needUpdateRedPoint()) {
			isPushRedPoint = true;
		}

		List<SuperScriptType> list = new ArrayList<SuperScriptType>();
		SuperScriptType.Builder data = SuperScriptType.newBuilder();
		data.setType(Const.SUPERSCRIPT_TYPE.GUILD.getValue());
		if (isPushRedPoint) {
			data.setNumber(1);
		} else {
			data.setNumber(0);
		}

		list.add(data.build());
		this.player.updateSuperScriptList(list);
	}

	// 角色登出
	public void onLogout() {
		update();
		cancelTimer();
	}

	public JSONObject toJson4MoneyPayLoad() {
		JSONObject data = new JSONObject();
		data.put("depositCount", this.guildData.depositCount);
		GuildSettingExt settingProp = GuildUtil.getGuildSettingExtProp();
		data.put("depositCountMax", settingProp.warehousePutIn);
		return data;
	}

	/**
	 * 刷新公会数据
	 */
	public void refreshNewDay() {
		// 捐献数据
		this.guildData.contributeTimesMap.clear();
		// 仓库数据
		this.guildData.depositCount = 0;
		//
		this.guildData.blessCount = 0;

		for (int i = 0; i < this.guildData.blessRecState.length; i++) {
			this.guildData.blessRecState[i] = Const.EVENT_GIFT_STATE.NOT_RECEIVE.getValue();
		}

		this.guildData.buffIds.clear();
		// 商店刷新
		guildShopManager.refreshNewDay(true);
		// 科技刷新
		guildTechManager.refreshNewDay(true);
		// 重置刷新时间
		this.guildData.refreshTime = new Date();

		pushRedPoint();
	}

	/**
	 * 清除祈福buff
	 */
	public void clearBlessBuff() {
		this.guildData.buffIds.clear();
		this.guildData.buffTime = 0;
	}

	/**
	 * 刷新祈福buff
	 */
	public void changeBlessBuff() {
		// 重新计算属性
		this.player.onGuildBlessChange();
	}

	public Map<String, Integer> calAllInfluence() {
		Map<String, Integer> buffAttrs = new HashMap<String, Integer>();
		if (this.isInGuild()) {
			buffAttrs = GuildUtil.getBlessBuffAttrs(this.guildData.buffIds);
		}
		return buffAttrs;
	}

	public void pushAndRefreshGuildEffect() {
		this.player.updatePlayerGuildTechAttrs();
	}

	public void resetGuildInfo(boolean isNotifyBattleServer) {
		// 这些数据不需要序列化,上线，变更时刷新
		this.guild = null;
		this.member = null;
		if (isNotifyBattleServer) {
			this.clearBlessBuff();
			this.changeBlessBuff();
			this.player.refreshBattlerServerBasic();
		}
	}

	public String getGuildName() {
		return guild != null ? guild.name : "";
	}

	public void refreshGuildInfo(boolean isNotifyBattleServer) {
		GuildUtil.refreshGuildJobPush(player);
		syncGuildInfo(isNotifyBattleServer);
	}

	public GuildPO getGuildInfo() {
		return guild == null ? new GuildPO() : guild;
	}

	public boolean isInGuild() {
		return this.guild != null;
	}

	public int getContributeTimes(int type) {
		if (this.guildData.contributeTimesMap.containsKey(type)) {
			return this.guildData.contributeTimesMap.get(type);
		}
		return 0;
	}

	public int getJob() {
		return member != null ? member.job : 0;
	}

	public String getJobName() {
		String jobName = getGuildInfo().officeNames.get(getJob());
		return jobName == null ? "" : jobName;
	}

	public boolean isPresident() {
		return Const.GuildJob.PRESIDENT.getValue() == getJob();
	}

	public boolean isVicePresident() {
		return Const.GuildJob.VICE_PRESIDENT.getValue() == getJob();
	}

	public void addContributeTimes(int type, int times) {
		if (this.guildData.contributeTimesMap.containsKey(type)) {
			int count = this.guildData.contributeTimesMap.get(type);
			count += times;
			this.guildData.contributeTimesMap.put(type, count);
		} else {
			this.guildData.contributeTimesMap.put(type, times);
		}
	}

	/**
	 * 读取某个公会总贡献
	 * 
	 * @param guildId 公会id
	 * @returns {*|number}
	 */
	public int getTotalContribution(String guildId) {
		if (this.guildData.totalContributionMap.containsKey(guildId))
			return this.guildData.totalContributionMap.get(guildId);
		else
			return 0;
	}

	/**
	 * 设置某个公会的总贡献
	 * 
	 * @param guildId
	 * @param value
	 */
	public void addTotalContribution(String guildId, int value) {
		if (this.guildData.totalContributionMap.containsKey(guildId)) {
			int con = this.guildData.totalContributionMap.get(guildId);
			con += value;
			this.guildData.totalContributionMap.put(guildId, con);
		} else {
			this.guildData.totalContributionMap.put(guildId, value);
		}
	}

	/**
	 * 添加公会贡献.
	 * 
	 * @param num 贡献值
	 * @param origin 来源
	 */
	public void addContribution(int num, GOODS_CHANGE_TYPE origin) {
		if (num == 0) {// 正常逻辑
			return;
		}
		if (num < 0) {
			throw new HackerException("增加公会贡献时参数小于0.");
		}
		int before = guildData.contribution;
		// 溢出判定
		if (0L + guildData.contribution + num > Integer.MAX_VALUE) {
			guildData.contribution = Integer.MAX_VALUE;
		} else {
			guildData.contribution += num;
		}
		int after = guildData.contribution;
		Out.info("add contribution. id=", player.getId(), ",name=", player.getName(), ",before=", before, ",value=", num, ",after=", after, ",origin=", origin.value);
		LogReportService.getInstance().ansycReportMoneyFlow(player.getPlayer(), VirtualItemType.GUILDPOINT, before, LogReportService.OPERATE_ADD, num, after, origin.value);
	}

	/**
	 * @return 获取当前的公会贡献值.
	 */
	public int getContribution() {
		return this.guildData.contribution;
	}

	/**
	 * 判定玩家身上的公会贡献是否足够.
	 * 
	 * @param num 需要消耗的公会贡献值
	 * @return 如果足够返回true，否则返回false.
	 */
	public boolean enoughContribution(int num) {
		if (num < 0) {
			throw new HackerException("判定玩家身上的公会贡献是否足够时参数小于0.");
		}
		return getContribution() >= num;
	}

	/**
	 * 消耗公会贡献接口.
	 * <p>
	 * 
	 * @param num 需要消耗的公会贡献值
	 * @param origin 消耗来源(必填) 参考{@link Const.GOODS_CHANGE_TYPE}
	 * @return 消耗成功返回true,否则返回false.
	 */
	public boolean costContribution(int num, Const.GOODS_CHANGE_TYPE origin) {
		if (num == 0) {
			return true;
		}
		if (!enoughContribution(num)) {
			return false;
		}

		int before = guildData.contribution;
		guildData.contribution -= num;// 扣钱
		int after = guildData.contribution;
		Out.info("cost contribution. id=", player.getId(), ",name=", player.getName(), ",before=", before, ",value=", num, ",after=", after, ",origin=", origin.value);
		LogReportService.getInstance().ansycReportMoneyFlow(player.getPlayer(), VirtualItemType.GUILDPOINT, before, LogReportService.OPERATE_COST, num, after, origin.value);

		// 推送更新协议给客户端
		return true;
	}

	public GuildResult createGuild(JSONObject params) {
		GuildResult ret = new GuildResult();
		GuildSettingExt prop = GuildUtil.getGuildSettingExtProp();
		if (null == prop) {
			ret.result = -1; // 配置错误
			return ret;
		}

		boolean isInGuild = GuildUtil.isInGuild(this.player.getId());
		if (isInGuild) {
			ret.result = -2; // 已入会
			return ret;
		}

		if (this.player.getLevel() < prop.joinLv) {
			ret.result = -3; // 等级不足
			return ret;
		}

		Date now = new Date();
		long lastSelfExitPassedTime = now.getTime() - this.guildData.lastSelfExitTime.getTime();
		if (lastSelfExitPassedTime < prop.selfOutMs) {
			long needMs = prop.selfOutMs - lastSelfExitPassedTime;
			String cdInfo = GuildCommonUtil.leftTimeTips(needMs);
			ret.result = -10;
			ret.cdInfo = cdInfo; // 主动退会冷却中
			return ret;
		}

		if (this.player.getPlayer().diamond < prop.cost) {
			ret.result = -4; // 钻石不足
			return ret;
		}

		if (params.getString("name").length() < 3) {
			ret.result = -6; // 名字太短
			return ret;
		}

		if (params.getString("name").length() > 6) {
			ret.result = -7; // 名字太长
			return ret;
		}

		if (BlackWordUtil.isIncludeSpecialChar(params.getString("name"))) {
			ret.result = -8; // 特殊字符
			return ret;
		}

		if (BlackWordUtil.isIncludeBlackString(params.getString("name"))) {
			ret.result = -9; // 非法字符
			return ret;
		}

		ret = GuildService.createGuild(this.player, params);

		if (ret.result == 0) {// 创建成功
			syncGuildInfo(false);
			this.player.moneyManager.costDiamond(prop.cost, Const.GOODS_CHANGE_TYPE.guild_create);
			this.refreshGuildInfo(true);
			this.pushAndRefreshGuildEffect();
			this.player.taskManager.dealTaskEvent(TaskType.ADD_GUILD, "1", 1);
		}
		return ret;
	}

	public GuildResult joinGuild(String guildId) {
		GuildResult ret = new GuildResult();
		JoinGuild data = new JoinGuild();
		GuildSettingExt prop = GuildUtil.getGuildSettingExtProp();
		if (null == prop) {
			ret.result = -1; // 配置错误
			return ret;
		}
		boolean isInGuild = GuildUtil.isInGuild(this.player.getId());
		if (isInGuild) {
			ret.result = -2; // 已入会
			return ret;
		}

		GuildPO guild = GuildUtil.getGuild(guildId);
		if (null == guild) {
			ret.result = -3;
			return ret; // 公会不存在
		}

		if (guild.entryUpLevel > 0) {
			if (this.player.getPlayer().upLevel < guild.entryUpLevel) {
				data.needUpLevel = guild.entryUpLevel;
				ret.data = data; // 进阶不足
				ret.result = -4;
				return ret;
			}
		} else {
			int needLevel = Math.max(prop.joinLv, guild.entryLevel);
			if (this.player.getLevel() < needLevel) {
				ret.result = -5;
				ret.needLevel = needLevel; // 等级不足
				ret.data = data;
				return ret;
			}
		}

		Date now = new Date();
		long lastSelfExitPassedTime = now.getTime() - this.guildData.lastSelfExitTime.getTime();
		lastSelfExitPassedTime = lastSelfExitPassedTime > 0 ? lastSelfExitPassedTime : prop.selfOutMs;
		if (lastSelfExitPassedTime < prop.selfOutMs) {
			long needMs = prop.selfOutMs - lastSelfExitPassedTime;
			String cdInfo = GuildCommonUtil.leftTimeTips(needMs);
			ret.result = -6;
			data.cdInfo = cdInfo; // 主动退会冷却中
			ret.data = data;
			return ret;
		}
		if (!GWorld.sids.contains(this.player.getLogicServerId())) {
			ret.result = -7;
			return ret;
		}

		ret = GuildService.joinGuild(this.player, guildId);

		if (ret.result == 0) {// 成功
			if (ret.joined) {// 加入成功,会通知
				// this.player.biServerManager.guildMemberChange(
				// 1a
				// ,{id: guild.id, name:guild.name}
				// ,{id: args.player.id, name: args.player.name}
				// ,GuildUtil.getGuildMemberCount(guild.id)
				// );
			} else {// 申请成功

			}
		}

		return ret;
	}

	public GuildResult joinGuildByPlayerId(String playerId) {
		GuildResult ret = new GuildResult();
		boolean isInGuild = GuildUtil.isInGuild(this.player.getId());
		if (isInGuild) {
			ret.result = -2; // 已入会
			return ret;
		}
		GuildMemberPO memInfo = GuildUtil.getGuildMember(playerId);
		if (null == memInfo || null == memInfo.playerId) {
			ret.result = -20; // 公会不存在
			return ret;
		}

		ret = this.joinGuild(memInfo.guildId);
		return ret;
	}

	public GuildResult invitePlayerJoinGuild(String playerId) {
		GuildResult ret = new GuildResult();
		GuildMemberPO myInfo = GuildUtil.getGuildMember(this.player.getId());
		if (null == myInfo) {
			ret.result = -1;// 本人还未加入公会
			return ret;
		}

		GuildPO myGuild = GuildUtil.getGuild(myInfo.guildId);
		if (null == myGuild) {
			ret.result = -1; // 本人还未加入公会
			return ret;
		}
		GuildPositionCO jobProp = GuildUtil.getGuildJobPropByJobId(myInfo.job);
		if (jobProp.right11 == 0) {
			ret.result = -2; // 没有邀请权限
			return ret;
		}
		boolean isInGuild = GuildUtil.isInGuild(playerId);
		if (isInGuild) {
			ret.result = -3;// 对方已入会
			return ret;
		}
		if (!PlayerUtil.isOnline(playerId)) {
			ret.result = -4; // 对方不在线
			return ret;
		}

		WNPlayer player = PlayerUtil.getOnlinePlayer(playerId);
		if (null != player && !GWorld.sids.contains(player.getLogicServerId())) {
			ret.result = -7;
			return ret;
		}
		// 邀请数据
		GuildInvitePush.Builder inviteInfo = GuildInvitePush.newBuilder();
		inviteInfo.setS2CCode(Const.CODE.OK);
		inviteInfo.setPlayerId(this.player.getId());
		inviteInfo.setPlayerPro(this.player.getPro());
		inviteInfo.setPlayerName(this.player.getName());
		inviteInfo.setGuildId(myGuild.id);
		inviteInfo.setGuildLevel(myGuild.level);
		inviteInfo.setGuildName(myGuild.name);

		if (null != player) {
			player.guildManager.pushInviteJoinGuild(inviteInfo);
		}

		ret.result = 0;
		return ret;
	}

	public void pushInviteJoinGuild(GuildInvitePush.Builder inviteInfo) {
		player.receive("area.guildPush.guildInvitePush", inviteInfo.build());
	}

	public GuildResult dealApply(String applyId, int operate) {
		GuildResult ret = new GuildResult();
		if (operate < 2 && null == applyId) {
			ret.result = -20;// 参数错误
			return ret;
		}
		GuildMemberPO myInfo = GuildUtil.getGuildMember(this.player.getId());
		if (null == myInfo) {
			ret.result = -1;
			return ret;
		}

		GuildPositionCO jobProp = GuildUtil.getGuildJobPropByJobId(myInfo.job);
		if (jobProp.right3 == 0) {
			ret.result = -2;
			return ret;
		}

		ret = GuildService.dealApply(this.player, applyId, operate);

		if (ret.result == 0) {// 审核成功

		}

		return ret;
	}

	public GuildResult setGuildInfo(GuildSetData params) {
		GuildResult ret = new GuildResult();
		GuildMemberPO myInfo = GuildUtil.getGuildMember(this.player.getId());
		if (null == myInfo) {
			ret.result = -2;
			return ret;
		}

		GuildSettingExt prop = GuildUtil.getGuildSettingExtProp();
		GuildSetData newParams = new GuildSetData();
		if (params.entryLevel > 0) {
			if (params.entryLevel < prop.joinLv) {
				ret.result = -4;
				return ret;
			}
			newParams.entryLevel = params.entryLevel;
		}
		if (params.entryUpLevel > 0 || params.entryUpLevel == 0) {
			newParams.entryUpLevel = params.entryUpLevel;
		}

		if (params.guildMode > 0) {
			if (params.guildMode == Const.GuildMode.AUTO_MODE.getValue() || params.guildMode == Const.GuildMode.CHECK_MODE.getValue()) {
				newParams.guildMode = params.guildMode;
			}
		}

		return GuildService.setGuildInfo(this.player, newParams);
	}

	public GuildResult setGuildQQGroup(String qqGroup) {
		GuildResult ret = new GuildResult();
		if (qqGroup.length() > 10) {
			ret.result = -1;// 参数错误
			return ret;
		}
		if (!BlackWordUtil.isNumberString(qqGroup)) {
			ret.result = -2;// 不是数字
			return ret;
		}
		GuildMemberPO myInfo = GuildUtil.getGuildMember(this.player.getId());
		if (null == myInfo) {
			ret.result = -3;// 不是公会成员
			return ret;
		}
		return GuildService.setGuildQQGroup(this.player, qqGroup);
	}

	public List<GuildInfo> getGuildList(String name) {
		List<GuildInfo> data = new ArrayList<GuildInfo>();

		List<GuildPO> guildList = GuildUtil.getGuildList(this.player.getLogicServerId(), name);
		List<GuildApplyPO> applies = GuildUtil.getPlayerApplyIdList(this.player.getId());

		for (GuildPO guild : guildList) {
			GuildLevelCO levelProp = GuildUtil.getGuildLevelPropByLevel(guild.level);
			GuildBaseInfo.Builder baseInfo = GuildBaseInfo.newBuilder();
			baseInfo.setGuildId(guild.id);
			baseInfo.setName(guild.name);
			baseInfo.setLevel(guild.level);
			baseInfo.setPresidentId(guild.presidentId);
			baseInfo.setPresidentName(guild.presidentName);
			baseInfo.setEntryLevel(guild.entryLevel);
			baseInfo.setEntryUpLevel(guild.entryUpLevel);
			baseInfo.setGuildMode(guild.guildMode);
			baseInfo.setGuildIcon(guild.icon);
			baseInfo.setMemberNum(GuildUtil.getGuildMemberCount(guild.id));
			baseInfo.setMemberMax(levelProp.member);
			PlayerPO president = PlayerUtil.getPlayerBaseData(guild.presidentId);
			if (null == president) { // 数据异常，会长都找不到了
				continue;
			}
			baseInfo.setPresidentPro(president.pro);
			baseInfo.setPresidentLevel(president.level);
			baseInfo.setCreateTime(guild.createTime.toString());

			GuildInfo.Builder guildInfo = GuildInfo.newBuilder();
			guildInfo.setBaseInfo(baseInfo);

			boolean haveApply = false;
			for (GuildApplyPO guildApplyPO : applies) {
				if (guildApplyPO.guildId.equals(guild.id)) {
					haveApply = true;
					break;
				}
			}
			if (haveApply) {
				guildInfo.setApplyState(1);
			}
			data.add(guildInfo.build());
		}

		data.sort(new Comparator<GuildInfo>() {
			@Override
			public int compare(GuildInfo guildA, GuildInfo guildB) {
				GuildBaseInfo baseA = guildA.toBuilder().getBaseInfo();
				GuildBaseInfo baseB = guildB.toBuilder().getBaseInfo();
				if (baseA.getLevel() != baseB.getLevel()) {
					return baseA.getLevel() < baseB.getLevel() ? 1 : -1;
				} else {
					return baseB.getCreateTime().compareTo(baseA.getCreateTime());// 时间字符串比较(待调试)
				}

			}
		});

		return data;
	}

	public MyContributeInfo getMyContributeInfo(String guildId) {
		MyContributeInfo.Builder myContributeInfo = MyContributeInfo.newBuilder();
		int myContribute = 0;
		// if (this.guildData.totalContributionMap.containsKey(guildId)) {
		// myContribute = this.getContribution();
		// }
		myContribute = this.getContribution();
		myContributeInfo.setCurrentContribute(myContribute);
		myContributeInfo.setTotalContribute(this.getTotalContribution(guildId));
		myContributeInfo.addAllTimesList(this.getContributeTimesList());
		return myContributeInfo.build();
	}

	public List<ContributeTimesInfo> getContributeTimesList() {
		List<ContributeTimesInfo> timesList = new ArrayList<ContributeTimesInfo>();
		Map<String, GuildContributeCO> propMap = GuildUtil.getGuildContributePropMap();
		for (String key : propMap.keySet()) {
			GuildContributeCO prop = propMap.get(key);
			ContributeTimesInfo.Builder tempInfo = ContributeTimesInfo.newBuilder();
			tempInfo.setType(prop.type);
			tempInfo.setTimes(this.getContributeTimes(prop.type));
			tempInfo.setMaxTimes(prop.time);
			timesList.add(tempInfo.build());
		}

		return timesList;
	}

	public MyGuildInfo getMyGuildInfo() {
		MyGuildInfo.Builder data = MyGuildInfo.newBuilder();
		GuildMemberPO myInfo = GuildUtil.getGuildMember(this.player.getId());
		if (null == myInfo) {
			return null;
		}
		GuildPO guild = GuildUtil.getGuild(myInfo.guildId);
		if (null == guild) {
			return null;
		}

		GuildLevelCO levelProp = GuildUtil.getGuildLevelPropByLevel(guild.level);
		if (null == levelProp) {
			return null;
		}

		GuildBaseInfo.Builder baseInfo = GuildBaseInfo.newBuilder();
		baseInfo.setGuildId(guild.id);
		baseInfo.setName(guild.name);
		baseInfo.setLevel(guild.level);
		baseInfo.setPresidentId(guild.presidentId);
		baseInfo.setPresidentName(guild.presidentName);
		baseInfo.setEntryLevel(guild.entryLevel);
		baseInfo.setEntryUpLevel(guild.entryUpLevel);
		baseInfo.setGuildMode(guild.guildMode);
		baseInfo.setGuildIcon(guild.icon);
		baseInfo.setMemberNum(GuildUtil.getGuildMemberCount(guild.id));
		baseInfo.setMemberMax(levelProp.member);
		PlayerPO president = PlayerUtil.getPlayerBaseData(guild.presidentId);
		baseInfo.setPresidentPro(1);
		baseInfo.setPresidentLevel(1);
		if (null == president) { // 数据异常，会长都找不到了
			Out.debug("getMyGuildInfo getPlayerBaseData player not exit,guildId:", guild.id, ", playerId:", guild.presidentId);
		} else {
			baseInfo.setPresidentPro(president.pro);
			baseInfo.setPresidentLevel(president.level);
		}

		data.setBaseInfo(baseInfo.build());
		data.setNotice(guild.notice);
		data.setFund((int) guild.fund);
		data.setExp((int) guild.exp);
		data.setQqGroup(guild.qqGroup);
		data.setMyInfo(this.getMyContributeInfo(guild.id));
		Date now = new Date();
		int passedDay = (int) Math.floor((now.getTime() - guild.changeNameTime.getTime()) / Const.Time.Day.getValue());
		passedDay = Math.min(passedDay, 7);
		data.setChangeNamePassedDay(passedDay);

		List<OfficeName> officeNames = new ArrayList<OfficeName>();
		for (Integer key : guild.officeNames.keySet()) {
			OfficeName.Builder jobInfo = OfficeName.newBuilder();
			jobInfo.setJob(key);
			jobInfo.setName(guild.officeNames.get(key));
			officeNames.add(jobInfo.build());
		}
		data.addAllOfficeNames(officeNames);
		return data.build();
	}

	public void onChangeName() {
		GuildMemberPO myInfo = GuildUtil.getGuildMember(this.player.getId());
		if (null == myInfo) {
			return;
		}
		myInfo.name = this.player.getName();
		GuildUtil.updateGuildMember(myInfo);
		GuildPO guild = GuildUtil.getGuild(myInfo.guildId);
		if (null == guild) {
			return;
		}
		if (guild.presidentId.equals(this.player.getId())) {
			guild.presidentName = this.player.getName();
			GuildUtil.updateGuild(guild);
		}
	}

	public GuildResult getMyGuildMemberList() {
		GuildResult ret = new GuildResult();
		MyGuildMember data = new MyGuildMember();
		GuildMemberPO myInfo = GuildUtil.getGuildMember(this.player.getId());
		if (null == myInfo) {
			ret.result = -1; // 自己不是工会成员
			return ret;
		}

		GuildPO guild = GuildUtil.getGuild(myInfo.guildId);
		if (null == guild) {
			ret.result = -1; // 未找到公会
			return ret;
		}

		// 读取踢人次数
		GuildSettingExt settingProp = GuildUtil.getGuildSettingExtProp();
		int kickCount = guild.kickCount;
		if (DateUtil.canRefreshData(Const.REFRSH_NEW_DAY_TIME, guild.kickTime)) {
			kickCount = 0;
		}
		int leftKickNum = settingProp.fireNum - kickCount;
		data.leftKickNum = leftKickNum > 0 ? leftKickNum : 0;
		// 读取成员列表
		List<GuildMemberPO> memberList = GuildUtil.getGuildMemberList(myInfo.guildId);
		Map<Integer, String> defaultNames = GuildUtil.getJobNameMap();
		MemberInfo.Builder president = MemberInfo.newBuilder();
		MemberInfo.Builder me = MemberInfo.newBuilder();
		for (GuildMemberPO member : memberList) {
			PlayerPO playerBase = PlayerUtil.getPlayerBaseData(member.playerId);
			if (null == playerBase) {
				continue;
			}

			MemberInfo.Builder tempInfo = MemberInfo.newBuilder();
			tempInfo.setGuildName(guild.name);
			tempInfo.setPlayerId(member.playerId);
			tempInfo.setJob(member.job);
			tempInfo.setJoinTime(member.createTime.toString());
			if (null != guild.officeNames.get(member.job)) {
				tempInfo.setJobName(guild.officeNames.get(member.job));
			} else {
				tempInfo.setJobName(defaultNames.get(member.job));
			}

			tempInfo.setName(playerBase.name);
			tempInfo.setPro(playerBase.pro);
			tempInfo.setLevel(playerBase.level);
			tempInfo.setUpLevel(playerBase.upOrder);

			PlayerGuildPO redisPo = FindPlayerGuildDao.getPlayerGuildPOById(member.playerId);
			if (null == redisPo) {
				continue;
			}

			Map<String, Integer> totalContrMap = redisPo.totalContributionMap;
			int contribution = 0;
			int totalContribute = 0;
			if (null != totalContrMap && totalContrMap.size() > 0) {
				if (totalContrMap.containsKey(guild.id)) {
					totalContribute = totalContrMap.get(guild.id);
					// contribution = redisPo.contribution;
				} else {
					totalContribute = 0;
					// contribution = 0;
				}
			}
			contribution = redisPo.contribution;
			tempInfo.setCurrentContribute(contribution);
			tempInfo.setTotalContribute(totalContribute);
			boolean isOnline = PlayerUtil.isOnline(member.playerId);
			tempInfo.setOnlineState(isOnline ? 1 : 0);
			tempInfo.setLastActiveTime((int) Math.ceil(playerBase.logoutTime.getTime() / Const.Time.Second.getValue()));

			if (tempInfo.getJob() == Const.GuildJob.PRESIDENT.getValue()) {
				president = tempInfo;
				if (tempInfo.getPlayerId().equals(this.player.getId())) { // 我是会长
					me = tempInfo;
				}
			} else if (tempInfo.getPlayerId().equals(this.player.getId())) {// 我不是会长
				me = tempInfo;
			} else {
				data.list.add(tempInfo.build());
			}
		}

		data.list.sort((o1, o2) -> {
			MemberInfo.Builder memberA = o1.toBuilder();
			MemberInfo.Builder memberB = o1.toBuilder();
			if (memberA.getOnlineState() != memberB.getOnlineState()) {
				return memberA.getOnlineState() < memberB.getOnlineState() ? 1 : -1;
			} else if (memberA.getJob() != memberB.getJob()) {
				return memberA.getJob() < memberB.getJob() ? -1 : 1;
			} else if (memberA.getUpLevel() != memberB.getUpLevel()) {
				return memberA.getUpLevel() < memberB.getUpLevel() ? 1 : -1;
			} else if (memberA.getLevel() != memberB.getLevel()) {
				return memberA.getLevel() < memberB.getLevel() ? 1 : -1;
			} else {
				return memberB.getJoinTime().compareTo(memberA.getJoinTime()); // 字符串比较
			}
		});

		if (!president.getPlayerId().equals(me.getPlayerId())) {
			data.list.add(0, me.build());
		}

		data.list.add(0, president.build());
		ret.result = 0;
		ret.data = data;
		return ret;
	}

	public List<ApplyInfo> getMyGuildApplyList() {
		List<ApplyInfo> data = new ArrayList<ApplyInfo>();
		GuildMemberPO myInfo = GuildUtil.getGuildMember(this.player.getId());
		if (null == myInfo) {
			return data;
		}
		GuildPO guild = GuildUtil.getGuild(myInfo.guildId);
		if (null == guild) {
			return data;
		}
		List<GuildApplyPO> applyList = GuildUtil.getGuildApplyList(myInfo.guildId);
		for (GuildApplyPO apply : applyList) {
			PlayerPO playerBase = PlayerUtil.getPlayerBaseData(apply.playerId);
			if (null == playerBase) {// 找不到玩家数据
				continue;
			}
			ApplyInfo.Builder tempInfo = ApplyInfo.newBuilder();
			tempInfo.setApplyId(apply.id);
			tempInfo.setPlayerId(apply.playerId);
			tempInfo.setPro(playerBase.pro);
			tempInfo.setName(playerBase.name);
			tempInfo.setLevel(playerBase.level);
			tempInfo.setFightPower(playerBase.fightPower);
			tempInfo.setUpLevel(this.player.player.upLevel);
			tempInfo.setCreateTime(apply.createTime.toString());
			data.add(tempInfo.build());
		}

		data.sort((applyA, applyB) -> {
			if (applyA.getUpLevel() != applyB.getUpLevel()) {
				return applyA.getUpLevel() < applyB.getUpLevel() ? 1 : -1;
			} else if (applyA.getLevel() != applyB.getLevel()) {
				return applyA.getLevel() < applyB.getLevel() ? 1 : -1;
			} else if (applyA.getFightPower() != applyB.getFightPower()) {
				return applyA.getFightPower() < applyB.getFightPower() ? 1 : -1;
			}
			return applyB.getCreateTime().compareTo(applyA.getCreateTime());
		});
		return data;
	}

	public GuildResult exitGuild() {
		GuildResult ret = GuildService.exitGuild(this.player);
		if (ret.result == 0) {
			this.guildData.lastSelfExitTime = new Date();
			Set<String> ids = new HashSet<String>();
			ids.add(this.player.getId());
			GuildMsg msg = new GuildMsg(Const.NotifyType.GUILD_EXIT_PUSH.getValue(), null);
			GuildService.notifySomePlayerRefreshGuild(ids, msg, null);
			this.player.pkRuleManager.onExitGuild();
			this.update();
		}
		return ret;
	}

	public GuildResult kickMember(String kickId) {
		GuildResult ret = GuildService.kickMember(this.player, kickId);
		return ret;
	}

	public GuildResult upgradeGuildLevel() {
		GuildResult ret = new GuildResult();
		GuildMemberPO myInfo = GuildUtil.getGuildMember(this.player.getId());
		if (null == myInfo) {
			ret.result = -1;
			ret.des = "不是公会成员";
			return ret;
		}
		GuildPO myGuild = GuildUtil.getGuild(myInfo.guildId);
		if (null == myGuild) {
			ret.result = -1;
			ret.des = "不是公会成员";
			return ret;
		}

		GuildPositionCO jobProp = GuildUtil.getGuildJobPropByJobId(myInfo.job);
		if (jobProp.right12 == 0) {
			ret.result = -2;
			ret.des = "没有权限";
			return ret;
		}

		GuildSettingExt settingProp = GuildUtil.getGuildSettingExtProp();
		GuildLevelCO levelProp = GuildUtil.getGuildLevelPropByLevel(myGuild.level);
		if (null == settingProp || null == levelProp) {
			ret.result = -3;
			ret.des = "配置错误";
			return ret;
		}
		if (!this.player.moneyManager.enoughGold(levelProp.gold)) {
			ret.result = -4;
			ret.des = "银两不足";
			return ret;
		}

		ret = GuildService.upgradeGuildLevel(this.player);

		if (ret.result == 0) {
			this.player.moneyManager.costGold(levelProp.gold, Const.GOODS_CHANGE_TYPE.guild_upgrade_level);
			// TODO BI数据，待续。。。
			// this.player.biServerManager.guildLevel(
			// myGuild.id
			// , myGuild.name
			// ,{1:result.biInfo.preLevel, 2: result.biInfo.preExp}
			// ,{1: result.biInfo.level, 2: result.biInfo.exp}
			// , result.biInfo.costExp
			// );
		}
		return ret;
	}

	public GuildResult changeGuildNotice(String notice) {
		GuildResult ret = new GuildResult();
		GuildMemberPO myInfo = GuildUtil.getGuildMember(this.player.getId());
		if (null == myInfo) {
			ret.result = -1;// 不在公会中
			return ret;
		}
		GuildPO myGuild = GuildUtil.getGuild(myInfo.guildId);
		if (null == myGuild) {
			ret.result = -1;
			ret.des = "不是公会成员";
			return ret;
		}

		if (null == notice) {
			ret.result = -3;
			return ret;
		}

		GuildSettingExt settingProp = GuildUtil.getGuildSettingExtProp();
		int maxLen = settingProp.announcement > 0 ? settingProp.announcement : 100;
		if (notice.length() > maxLen) {
			ret.result = -4; // 公告太长
			return ret;
		}

		String replacedNotice = BlackWordUtil.replaceBlackString(notice);
		ret = GuildService.changeGuildNotice(this.player, replacedNotice);
		return ret;
	}

	public GuildResult changeGuildName(String name) {
		GuildResult ret = new GuildResult();
		GuildMemberPO myInfo = GuildUtil.getGuildMember(this.player.getId());
		if (null == myInfo) {
			ret.result = -1;// 不在公会中
			return ret;
		}
		GuildPO myGuild = GuildUtil.getGuild(myInfo.guildId);
		if (null == myGuild) {
			ret.result = -1;// 不在公会中
			return ret;
		}

		GuildPositionCO jobProp = GuildUtil.getGuildJobPropByJobId(myInfo.job);
		if (jobProp.right2 == 0) {
			ret.result = -2;
			return ret;
		}
		Date now = new Date();
		GuildSettingExt settingProp = GuildUtil.getGuildSettingExtProp();
		int dayCD = settingProp.changeNameCD;
		long cdOverTime = myGuild.changeNameTime.getTime() + (dayCD * Const.Time.Day.getValue());
		if (now.getTime() < cdOverTime) {
			ret.result = 2; // 更名cd判断提前
			return ret;
		}
		if (null == name || name.length() < 3) {
			ret.result = -4;// 名字太短
			return ret;
		}
		if (name.length() > settingProp.nameMaxLen) {
			ret.result = -5;// 名字太长
			return ret;
		}
		if (BlackWordUtil.isIncludeSpecialChar(name)) {
			ret.result = -8;// 特殊字符
			return ret;
		}
		if (BlackWordUtil.isIncludeBlackString(name)) {
			ret.result = -9;// 非法字符
			return ret;
		}

		GuildSettingExt settintProp = GuildUtil.getGuildSettingExtProp();
		String costCode = settintProp.changeName;
		int costNum = settintProp.changeNameCost;
		int haveNum = this.player.bag.findItemNumByCode(costCode);
		if (haveNum < costNum) {
			ret.result = -6;// 材料不足
			return ret;
		}

		ret = GuildService.changeGuildName(this.player, name);
		if (ret.result == 0) {
			this.player.bag.discardItem(costCode, costNum, Const.GOODS_CHANGE_TYPE.guildchangename, null, false, false);
			this.update();
		}
		return ret;
	}

	public GuildResult changeOfficeName(List<OfficeName> officeNames) {
		GuildResult ret = new GuildResult();
		GuildMemberPO myInfo = GuildUtil.getGuildMember(this.player.getId());
		if (null == myInfo) {
			ret.result = -1;// 不在公会中
			return ret;
		}
		GuildPO myGuild = GuildUtil.getGuild(myInfo.guildId);
		if (null == myGuild) {
			ret.result = -1;// 不在公会中
			return ret;
		}

		if (myInfo.job != Const.GuildJob.PRESIDENT.getValue()) {
			ret.result = -2;
			return ret;
		}

		GuildSettingExt settintProp = GuildUtil.getGuildSettingExtProp();
		for (OfficeName office : officeNames) {
			if (office.getJob() < Const.GuildJob.PRESIDENT.getValue() || office.getJob() > Const.GuildJob.MEMBER.getValue()) {
				ret.result = -3;// 职位不存在
				return ret;
			} else if (office.getName().isEmpty()) {
				ret.result = -4;// 职位名称不能为空
				return ret;
			} else if (office.getName().length() > settintProp.maxLen) {
				ret.result = -5;// 名称太长
				return ret;
			} else if (BlackWordUtil.isIncludeSpecialChar(office.getName())) {
				ret.result = -8;
				return ret;
			} else if (BlackWordUtil.isIncludeBlackString(office.getName())) {
				ret.result = -9;
				return ret;
			}
		}

		ret = GuildService.changeOfficeName(this.player, officeNames);
		if (ret.result == 0) {}

		return ret;
	}

	/**
	 * 检查捐献红点
	 * 
	 * @return
	 */
	public boolean checkIsCanContribute() {
		boolean ret = false;

		for (int i = 1; i <= 2; i++) {
			GuildContributeCO contributeProp = GuildUtil.getGuildContributePropByType(i);
			String costCode = contributeProp.costItem;
			int costNum = contributeProp.costAmount;

			if (this.getContributeTimes(i) < contributeProp.time) {
				if (1 == i) {
					if (this.player.moneyManager.enoughGold(costNum)) {
						ret = true;
						break;
					}
				} else {
					int haveNum = this.player.bag.findItemNumByCode(costCode);
					if (haveNum >= costNum) {
						ret = true;
						break;
					}
				}
			}
		}
		return ret;
	}

	public GuildResult contributeToGuild(int type, int times) {
		GuildResult ret = new GuildResult();
		GuildMemberPO myInfo = GuildUtil.getGuildMember(this.player.getId());
		if (null == myInfo) {
			ret.result = -1;// 不在公会中
			return ret;
		}
		GuildPO myGuild = GuildUtil.getGuild(myInfo.guildId);
		if (null == myGuild) {
			ret.result = -1;// 不在公会中
			return ret;
		}

		GuildContributeCO contributeProp = GuildUtil.getGuildContributePropByType(type);
		if (null == contributeProp || times == 0) {
			ret.result = -2;
			return ret;
		}
		String costCode = contributeProp.costItem;
		int costNum = contributeProp.costAmount * times;
		if (type == 1) {
			if (!this.player.moneyManager.enoughGold(costNum)) {
				ret.result = -3;
				return ret;// 银两不足
			}
		} else {
			int haveNum = this.player.bag.findItemNumByCode(costCode);
			if (haveNum < costNum) {
				ret.result = -4;
				return ret;
			}
		}
		if (this.getContributeTimes(type) + times > contributeProp.time) {
			ret.result = -5; // 次数不足
			return ret;
		}

		ret = GuildService.contributeToGuild(this.player, type, times);

		if (ret.result == 0) {// 捐献成功
			if (type == 1) {
				this.player.moneyManager.costGold(costNum, Const.GOODS_CHANGE_TYPE.guild_donate);
			} else {
				this.player.bag.discardItem(costCode, costNum, Const.GOODS_CHANGE_TYPE.guild_donate);
			}
			int totalPoints = contributeProp.guildPoints * times;
			this.addContribution(totalPoints, Const.GOODS_CHANGE_TYPE.guild_donate);
			this.addTotalContribution(myGuild.id, totalPoints);
			this.addContributeTimes(type, times);

			this.player.taskManager.dealTaskEvent(TaskType.GUILD_DONATE, "1", 1);
			pushRedPoint();
			// TODO BI 数据，待续。。。
			// this.player.biServerManager.guildDonate(
			// type
			// , {id: result.biInfo.id,name: result.biInfo.name}
			// , {id: args.player.id, name: args.player.name}
			// ,costNum
			// );

			getMiscData().guildDonateToday = 1;

			this.update();
		}
		return ret;
	}

	public MiscData getMiscData() {
		if (null == this.player.playerAttachPO.miscData) {
			this.player.playerAttachPO.miscData = new MiscData();

		}
		return this.player.playerAttachPO.miscData;
	}

	public GuildResult setMemberJob(String memberId, int job) {
		GuildResult ret = new GuildResult();
		if (null == memberId || memberId.isEmpty() || job > Const.GuildJob.MEMBER.getValue()) {
			ret.result = -20;// 参数错误
			return ret;
		}
		if (memberId == this.player.getId()) {
			ret.result = -21; // 自己
			return ret;
		}

		GuildMemberPO myInfo = GuildUtil.getGuildMember(this.player.getId());
		if (null == myInfo) {
			ret.result = -1;// 不在公会中
			return ret;
		}
		GuildPO myGuild = GuildUtil.getGuild(myInfo.guildId);
		if (null == myGuild) {
			ret.result = -1;// 不在公会中
			return ret;
		}
		GuildMemberPO memberInfo = GuildUtil.getGuildMember(memberId);
		if (null == memberInfo || !memberInfo.guildId.equals(myGuild.id)) {
			ret.result = -2;// 对方不是公会成员
			return ret;
		}
		if (myInfo.job > Const.GuildJob.ELDER.getValue()) {
			ret.result = -3;
			return ret;
		}
		if (myInfo.job >= memberInfo.job || myInfo.job >= job) {
			ret.result = -4;// 没有权限设置比自己同等和低等级职位的成员
			return ret;
		}

		ret = GuildService.setMemberJob(this.player, memberId, job);
		if (ret.result == 0) {}

		return ret;
	}

	public GuildResult transferGuildPresident(String memberId) {
		GuildResult ret = new GuildResult();
		GuildMemberPO myInfo = GuildUtil.getGuildMember(this.player.getId());
		if (null == myInfo) {
			ret.result = -1;// 不在公会中
			return ret;
		}

		GuildPO myGuild = GuildUtil.getGuild(myInfo.guildId);
		if (null == myGuild) {
			ret.result = -1;// 不在公会中
			return ret;
		}
		if (myInfo.job != Const.GuildJob.PRESIDENT.getValue()) {
			ret.result = -2;// 不是会长
			return ret;
		}

		GuildMemberPO memberInfo = GuildUtil.getGuildMember(memberId);
		if (null == memberInfo || !memberInfo.guildId.equals(myGuild.id)) {
			ret.result = -3;// 对方不是公会成员
			return ret;
		}

		PlayerPO newPresident = PlayerUtil.getPlayerBaseData(memberId);
		if (null == newPresident) {
			ret.result = -20;
			return ret;
		}

		ret = GuildService.transferGuildPresident(this.player.getId(), memberId);
		return ret;
	}

	public List<RecordInfo> getGuildRecordList(int page) {
		List<RecordInfo> list = new ArrayList<RecordInfo>();
		GuildMemberPO myInfo = GuildUtil.getGuildMember(this.player.getId());
		if (null == myInfo) {
			return list;// 不在公会中
		}
		GuildPO myGuild = GuildUtil.getGuild(myInfo.guildId);
		if (null == myGuild) {
			return list;// 不在公会中
		}
		if (page == 0) {
			return list;
		}
		List<GuildRecordData> records = GuildDao.getGuildNews(myGuild.id);
		int perPageNum = 50;
		int startIndex = (page - 1) * perPageNum;
		int endIndex = startIndex + perPageNum;
		for (int i = startIndex; i < records.size() && i < endIndex; ++i) {
			GuildRecordData record = records.get(i);
			Date recordTime = record.time;
			RecordInfo.Builder tempInfo = RecordInfo.newBuilder();
			tempInfo.setTime(DateUtil.format(recordTime, "MM-dd HH:mm:ss"));
			if (null != record.role1 && 0 != record.role1.pro) {
				tempInfo.setRole1(GuildCommonUtil.convertRoleInfo(record.role1));
			}
			if (null != record.role2 && record.role2.pro != 0) {
				tempInfo.setRole2(GuildCommonUtil.convertRoleInfo(record.role2));
			}
			if (null != record.result) {
				tempInfo.setResultNum(record.result.v1);

				if (null != record.result.v2 && !record.result.v2.isEmpty()) {
					tempInfo.setResultStr(record.result.v2);
				}
			}
			if (null != record.build && !record.build.isEmpty()) {
				tempInfo.setBuild(record.build);
			}

			tempInfo.setRecordType(record.type);
			list.add(tempInfo.build());
		}
		return list;
	}

	public GuildResult impeachGuildPresident() {
		GuildResult ret = new GuildResult();
		GuildMemberPO myInfo = GuildUtil.getGuildMember(this.player.getId());
		if (null == myInfo) {
			ret.result = -1;
			ret.des = "不是公会成员";
			return ret;
		}

		GuildPO myGuild = GuildUtil.getGuild(myInfo.guildId);
		if (null == myGuild) {
			ret.result = -1;
			ret.des = "不是公会成员";
			return ret;
		}
		if (myInfo.job == Const.GuildJob.PRESIDENT.getValue()) {
			ret.result = -2;
			ret.des = "不能弹劾自己";
			return ret;
		}
		boolean isPresidentOnline = PlayerUtil.isOnline(myGuild.presidentId);
		if (isPresidentOnline) {
			ret.result = -3;
			ret.des = "会长在线";
			return ret;
		}
		PlayerPO president = PlayerUtil.getPlayerBaseData(myGuild.presidentId);
		if (null == president) {
			ret.result = -4;
			ret.des = "会长不存在，系统错误";
			return ret;
		}
		Date logoutTime = president.logoutTime;
		Date nowTime = new Date();
		GuildSettingExt settingProp = GuildUtil.getGuildSettingExtProp();
		if (null == settingProp) {
			ret.result = -5;
			ret.des = "配置不存在，系统错误";
			return ret;
		}
		if (nowTime.getTime() - logoutTime.getTime() < settingProp.impeachMs) {
			ret.result = -6;
			ret.des = LangService.getValue("GUILD_PRESIDENT_OFFTIME_NOT_ENOUGH").replace("{day}", String.valueOf(settingProp.impeach));
			return ret;
		}

		ret = GuildService.impeachGuildPresident(this.player, guild.presidentId, logoutTime);

		if (ret.result == 0) {
			// 发起弹劾成功
		}
		return ret;
	}

	// 任务完成事件
	public void onTaskEvent() {
		this.player.taskManager.dealTaskEvent(TaskType.ADD_GUILD, "1", 1);
	}

	public void playerOnlineRefreshGuildData() {
		GuildResult resData = GuildService.playerOnlineRefreshGuild(this.player);
		PlayerOnlineRefreshGuild data = (PlayerOnlineRefreshGuild) resData.data;
		this.changeGuildData(data, false);
	}

	public void changeGuildData(GuildMsg msg, boolean isPush) {
		JoinGuildBlessMsg joinBless = (JoinGuildBlessMsg) msg.data;
		GuildBlessPO data = joinBless.blessData;

		if (null != data && null != data.allBlobData) {
			if (null != data.allBlobData.goods) {
				guildShopManager.goods = data.allBlobData.goods;
			}
			if (null != data.allBlobData.techData) {
				guildTechManager.refreshTechData(data.allBlobData.techData);
			}

			// 刷新修行和修行增益等级
			guildTechManager.refreshLevel();

			int[] arr = data.allBlobData.finishStateArr;
			for (int i = 0; i < arr.length; i++) {
				if (Const.EVENT_GIFT_STATE.NOT_RECEIVE.getValue() == this.guildData.blessRecState[i])
					this.guildData.blessRecState[i] = arr[i];
			}
			if (data.allBlobData.throwAwardState > 0) {
				this.throwAwardState = data.allBlobData.throwAwardState;
			}
		}

		if (isPush) {
			this.pushAndRefreshGuildEffect();
			this.changeBlessBuff();
		}
	}

	public void changeGuildData(PlayerOnlineRefreshGuild data, boolean isPush) {
		if (null == data) {
			return;
		}

		if (null != data.goods) {
			guildShopManager.goods = data.goods;
		}
		if (null != data.techData) {
			guildTechManager.refreshTechData(data.techData);
		}

		guildTechManager.refreshLevel();

		int[] arr = data.finishStateArr;
		for (int i = 0; i < arr.length; i++) {
			if (Const.EVENT_GIFT_STATE.NOT_RECEIVE.getValue() == this.guildData.blessRecState[i])
				this.guildData.blessRecState[i] = arr[i];
		}

		if (data.throwAwardState > 0) {
			this.throwAwardState = data.throwAwardState;
		}

		if (isPush) {
			this.pushAndRefreshGuildEffect();
			this.changeBlessBuff();
		}
	}

	public void resetGuildPublicData(boolean isPush) {
		this.guildData.buffIds.clear();
		for (int i = 0; i < this.guildData.blessRecState.length; i++) {
			this.guildData.blessRecState[i] = 0;
		}
		guildTechManager.resetPublicData();
		guildShopManager.resetPublicData();
		if (isPush) {
			this.pushAndRefreshGuildEffect();
			this.changeBlessBuff();
		}
		resetGuildInfo(true);
	}

	public void onNotifyRefreshGuild(GuildMsg msg) {
		int refreshType = msg.notifyType;
		if (refreshType == Const.NotifyType.GUILD_REFRESH.getValue()) {
			this.refreshGuildInfo(false);
		} else if (refreshType == Const.NotifyType.GUILD_JOIN_PUSH.getValue()) {
			this.refreshGuildInfo(true);
			this.changeGuildData(msg, true);
			this.pushToClientRefreshGuild(msg);
			if (this.guild != null) {
				this.player.taskManager.dealTaskEvent(TaskType.ADD_GUILD, "1", 1);
			}
		} else if (refreshType == Const.NotifyType.GUILD_EXIT_PUSH.getValue()) {
			this.resetGuildPublicData(true);
			this.refreshGuildInfo(true);
			this.player.pkRuleManager.onExitGuild();
			this.pushToClientRefreshGuild(msg);
		} else if (refreshType == Const.NotifyType.GUILD_JOB_CHANGE.getValue()) {
			this.refreshGuildInfo(true);
			this.pushToClientRefreshGuild(msg);
		} else if (refreshType == Const.NotifyType.GUILD_CHANGE_NAME.getValue()) {
			this.refreshGuildInfo(true);
			this.pushToClientRefreshGuild(msg);
		} else if (refreshType == Const.NotifyType.DEPOT_DEPOSIT_PUSH.getValue() || refreshType == Const.NotifyType.DEPOT_REMOVE_PUSH.getValue() || refreshType == Const.NotifyType.DEPOT_UPGRADE_PUSH.getValue() || refreshType == Const.NotifyType.DEPOT_CONDITION_PUSH.getValue()) {
			DepotRefreshGuildMsg msgData = (DepotRefreshGuildMsg) msg.data;
			DepotRefreshPush.Builder data = DepotRefreshPush.newBuilder();
			data.setS2CCode(Const.CODE.OK);
			data.setType(msgData.type);
			data.setBagIndex(msgData.bagIndex);
			data.setLevelInfo(msgData.levelInfo);
			data.setCondition(GuildCommonUtil.toHandlerDepot(msgData.condition));
			player.receive("area.guildDepotPush.depotRefreshPush", data.build());
		}
		// 祈福通知
		else if (refreshType == Const.NotifyType.BLESS_FINISH_PUSH.getValue()) {
			BlessRefreshPush.Builder blessData = BlessRefreshPush.newBuilder();
			blessData.setS2CCode(Const.CODE.OK);

			BlessRefreshGuildMsg blessMsgData = (BlessRefreshGuildMsg) msg.data;
			blessData.setType(refreshType - Const.NotifyType.BLESS_PUSH_START.getValue() + 1);
			if (blessMsgData.finishStateArr.length > 0) {
				blessData.addAllFinishState(GuildCommonUtil.toList(blessMsgData.finishStateArr));
				blessData.setBlessValue(blessMsgData.blessValue);
				// 设置缓存
				for (int i = 0; i < blessMsgData.finishStateArr.length; i++) {
					if (Const.EVENT_GIFT_STATE.NOT_RECEIVE.getValue() == this.guildData.blessRecState[i])
						this.guildData.blessRecState[i] = blessMsgData.finishStateArr[i];
				}
			}
		} else if (refreshType == Const.NotifyType.BLESS_NEW_DAY_PUSH.getValue()) {
			// 公会捐献
			this.pushToClientRefreshGuild(msg);

			// 祈福刷新
			BlessRefreshPush.Builder blessData = BlessRefreshPush.newBuilder();
			blessData.setS2CCode(Const.CODE.OK);
			blessData.setType(Const.NotifyType.BLESS_NEW_DAY_PUSH.getValue() - Const.NotifyType.BLESS_PUSH_START.getValue() + 1);
			player.receive("area.guildBlessPush.blessRefreshPush", blessData.build());

			// 商店刷新，交给祈福管理
			ShopRefreshPush.Builder shopData = ShopRefreshPush.newBuilder();
			shopData.setS2CCode(Const.CODE.OK);
			shopData.setType(Const.NotifyType.SHOP_NEW_DAY_PUSH.getValue() - Const.NotifyType.SHOP_PUSH_START.getValue() + 1);
			player.receive("area.guildShopPush.shopRefreshPush", shopData.build());

			// 科技刷新
			GuildTechRefreshPush.Builder techData = GuildTechRefreshPush.newBuilder();
			// TODO
			// guildTechManager.refreshTechData(refreshData);
			techData.setType(1);
			player.receive("area.guildTechPush.guildTechRefreshPush", techData.build());
			// 推送属性变化
			this.pushAndRefreshGuildEffect();
			this.clearBlessBuff();
			this.changeBlessBuff();
		} else if (refreshType == Const.NotifyType.TECH_LEVEL_PUSH.getValue()) {
			TechRefreshGuildMsg msgData = (TechRefreshGuildMsg) msg.data;
			boolean changeInfluence = guildTechManager.refreshTechData(msgData.techData);
			if (changeInfluence) {
				this.pushAndRefreshGuildEffect();
				this.changeBlessBuff();
			}

			GuildTechRefreshPush.Builder data = GuildTechRefreshPush.newBuilder();
			data.setS2CCode(200);
			data.setType(refreshType - Const.NotifyType.TECH_PUSH_START.getValue() + 1);
			data.setLevel(msgData.techData.blobData.level);
			player.receive("area.guildTechPush.guildTechRefreshPush", data.build());

		} else if (refreshType == Const.NotifyType.TECH_BUFF_LEVEL_PUSH.getValue()) {
			// 科技刷新
			TechRefreshGuildMsg msgData = (TechRefreshGuildMsg) msg.data;
			boolean changeInfluence = guildTechManager.refreshTechData(msgData.techData);
			if (changeInfluence) {
				this.pushAndRefreshGuildEffect();
				this.changeBlessBuff();
			}

			GuildTechRefreshPush.Builder data = GuildTechRefreshPush.newBuilder();
			data.setS2CCode(Const.CODE.OK);
			data.setType(1);
			player.receive("area.guildTechPush.guildTechRefreshPush", data.build());
		} else if (refreshType == Const.NotifyType.GUILD_DUNGEON_OPEN.getValue()) {
			Area area = this.player.getArea();

			GuildDungeonOpenPush.Builder data = GuildDungeonOpenPush.newBuilder();
			if (null != area && (area.isNormal() || area.sceneType == Const.SCENE_TYPE.WORLD_BOSS.getValue())) {
				data.setS2CCode(Const.CODE.OK);
				player.receive("area.guildPush.guildDungeonOpenPush", data.build());
			}
		} else if (refreshType == Const.NotifyType.GUILD_DUNGEON_PASS.getValue()) {
			DungeonPassGuildMsg msgData = (DungeonPassGuildMsg) msg.data;
			GuildDungeonPassPush.Builder data = GuildDungeonPassPush.newBuilder();
			data.setDungeonCount(msgData.dungeonCount);
			player.receive("area.guildPush.guildDungeonPassPush", data.build());
		} else if (refreshType == Const.NotifyType.GUILD_DUNGEON_PLAYER_NUM.getValue()) {
			DungeonPlayerNumGuildMsg msgData = (DungeonPlayerNumGuildMsg) msg.data;
			GuildDungeonPlayerNumPush.Builder data = GuildDungeonPlayerNumPush.newBuilder();
			data.setS2CCode(Const.CODE.OK);
			data.setPlayerNum(msgData.playerNum);
			player.receive("area.guildPush.guildDungeonPlayerNumPush", data.build());
		} else if (refreshType == Const.NotifyType.GUILD_DUNGEON_OPEN_CHAT.getValue()) {
			OnChatGuildMsg msgData = (OnChatGuildMsg) msg.data;
			WNPlayer player = PlayerUtil.getOnlinePlayer(msgData.playerId);
			if (null == player) {
				// logger.error('onNotifyRefreshGuild getPlayerBaseData player
				// not exit, playerId:' + memberId);
				return;
			}
			MessageUtil.sendChatMsgAsyn(player, LangService.getValue("GDUNGEON_OPEN_WORDS"), Const.CHAT_SCOPE.GUILD, TipsType.NORMAL);
		}
	}

	public void pushToClientRefreshGuild(GuildMsg msg) {
		int refreshType = msg.notifyType;

		GuildRefreshPush.Builder data = GuildRefreshPush.newBuilder();
		data.setS2CCode(Const.CODE.OK);

		if (refreshType <= 0) {
			return;
		} else if (refreshType == Const.NotifyType.GUILD_JOIN_PUSH.getValue()) {
			data.setIsIn(1);
		} else if (refreshType == Const.NotifyType.GUILD_EXIT_PUSH.getValue()) {
			data.setIsOut(1);
		} else if (refreshType == Const.NotifyType.GUILD_JOB_CHANGE.getValue()) {
			RefreshGuildMsg msgData = (RefreshGuildMsg) msg.data;
			data.setJob(msgData.job);
			data.setJobName(msgData.jobName);
		} else if (refreshType == Const.NotifyType.GUILD_CHANGE_NAME.getValue()) {
			RefreshGuildMsg msgData = (RefreshGuildMsg) msg.data;
			data.setGuildName(msgData.guildName);
		} else if (refreshType == Const.NotifyType.BLESS_NEW_DAY_PUSH.getValue()) {
			data.addAllTimesList(this.getContributeTimesList());
		}

		player.receive("area.guildPush.guildRefreshPush", data.build());
	}

	public GuildResult depositEquipToDepot(int bagIndex) {
		GuildResult ret = new GuildResult();
		if (bagIndex < 0) {
			ret.result = -1;
			ret.des = "参数错误";
			return ret;
		}

		NormalItem item = this.player.bag.getItem(bagIndex);
		if (null == item) {
			ret.result = -2;
			ret.des = "该包裹格子没有装备错误";
			return ret;
		}
		if (!item.isEquip()) {
			ret.result = -4;
			ret.des = "不是装备";
			return ret;
		}
		if (item.isBinding()) {
			ret.result = -3;
			ret.des = "道具已绑定";
			return ret;
		}
		if (!item.canDepotGuild()) {
			ret.result = -5;
			ret.des = "该道具不能存入公会仓库";
			return ret;
		}

		GuildSettingExt settingProp = GuildUtil.getGuildSettingExtProp();
		if (this.guildData.depositCount >= settingProp.warehousePutIn) {
			ret.result = -6;
			ret.des = "今日存入次数已用完";
			return ret;
		}

		if (item.getLevel() < settingProp.warehouseMinLv) {
			ret.result = -7;
			ret.des = "装备等级太低";
			return ret;
		}

		if (item.getQLevel() < settingProp.warehouseMinQ) {
			ret.result = -8;
			ret.des = "装备品质太低";
			return ret;
		}

		WareHouseValueCO depositProp = GuildUtil.getDepotDepositValueProp(item.getLevel(), item.getQLevel());
		if (null == depositProp) {
			ret.result = -9;
			ret.des = "未找到该品质对应的配置";
			return ret;
		}

		int addNum = depositProp.wareHouseValue;
		PlayerItemPO itemData = item.cloneItemDB();
		ret = GuildService.depotDepositEquip(this.player, itemData);

		if (ret.result == 0) {
			this.player.bag.removeItemByPos(bagIndex, false, GOODS_CHANGE_TYPE.guild_store);
			this.player.baseDataManager.addPawnGold(addNum);
			this.player.pushDynamicData("pawnGold", this.player.player.pawnGold);
			this.guildData.depositCount = this.guildData.depositCount + 1;
			ret.depositCount = this.guildData.depositCount;
			this.update();
		}
		return ret;
	}

	public void update() {
		// 刷新写入数据库
		GameDao.update(this.player.getId(), ConstsTR.playerGuildTR, this.guildData);
	}

	public GuildResult takeOutEquipFromDepot(int depotIndex) {
		GuildResult ret = new GuildResult();
		if (depotIndex <= 0) {
			ret.result = -1;
			ret.des = "参数错误";
			return ret;
		}

		if (!this.player.bag.testEmptyGridLarge(1)) {
			ret.result = -2;
			ret.des = "背包格子不够";
			return ret;
		}

		int havePawnGold = this.player.baseDataManager.getPawnGold();
		ret = GuildService.depotTakeOutEquip(this.player, depotIndex, havePawnGold);
		if (ret.result == 0) {
			PlayerItemPO itemData = ret.itemData;
			NormalItem item = ItemUtil.createItemByDbOpts(itemData);
			WareHouseValueCO depositProp = GuildUtil.getDepotDepositValueProp(item.getLevel(), item.getQLevel());
			int costNum = depositProp.wareHouseCost;
			this.player.bag.addEntityItem(item, Const.GOODS_CHANGE_TYPE.guild_store, null, true, false);
			this.player.baseDataManager.costPawnGold(costNum);
			this.player.pushDynamicData("pawnGold", this.player.player.pawnGold);

			this.update();
		}
		return ret;
	}

	public GuildResult setDepotCondition(GuildDepotCondition condition) {
		GuildResult ret = new GuildResult();
		GuildCond useCond = condition.useCond;
		if (0 == useCond.job || useCond.job > Const.GuildJob.MEMBER.getValue() || (0 == useCond.level && 0 == useCond.upLevel)) {
			ret.result = -1;
			ret.des = "参数错误";
			return ret;
		}

		if (!GuildUtil.checkCondition(condition)) {
			ret.result = -2;
			ret.des = "装备品质区间不合理";
			return ret;
		}

		ret = GuildService.depotSetCondition(this.player, condition);
		if (ret.result == 0) {}
		return ret;
	}

	public GuildResult deleteEquipFromDepot(int depotIndex) {
		GuildResult ret = new GuildResult();
		if (depotIndex == 0) {
			ret.result = -1;
			ret.des = "参数错误";
			return ret;
		}

		ret = GuildService.depotDeleteEquip(this.player, depotIndex);
		if (ret.result == 0) {}
		return ret;
	}

	public GuildResult upgradeDepotLevel() {
		GuildResult ret = new GuildResult();
		int myGold = this.player.moneyManager.getGold();
		ret = GuildService.depotUpgradeLevel(this.player, myGold);
		DepotUpgradeLevelData data = (DepotUpgradeLevelData) ret.data;
		if (ret.result == 0) {
			if (data.costGoldNum > 0) {
				this.player.moneyManager.costGold(data.costGoldNum, Const.GOODS_CHANGE_TYPE.guild_upgrade_depot_level);
				this.update();
			}
		}
		return ret;
	}

	/**
	 * 获取个人祈福信息
	 * 
	 * @return
	 */
	public MyBlessInfo getMyBlessInfo() {
		MyBlessInfo.Builder data = MyBlessInfo.newBuilder();
		data.setBlessCount(this.guildData.blessCount);
		// 属性加成
		List<Map<String, Integer>> buffAttrs = GuildUtil.getBlessBuffAttrsList(this.guildData.buffIds);
		if (null != buffAttrs) {
			data.addAllBlessAttrs(AttributeUtil.getAttributeBaseByArray(buffAttrs));
		}
		data.setBuffTime(this.guildData.buffTime);
		data.addAllReceiveState(GuildCommonUtil.toList(this.guildData.blessRecState));
		GuildBlessPO blessPO = GuildBlessCenter.getInstance().getBlessData(getGuildId());
		// 祈福奖励道具
		List<GuildBlessHandler.BlessItem> itemList = new ArrayList<>();
		for (int i = 0; i < blessPO.gifts.size(); i++) {
			Map<String, Integer> ls = blessPO.gifts.get(i);
			GuildBlessHandler.BlessItem.Builder tempInfo = GuildBlessHandler.BlessItem.newBuilder();
			List<MiniItem> items = new ArrayList<MiniItem>();
			for (String key : ls.keySet()) {
				MiniItem.Builder tmpItem = ItemUtil.getMiniItemData(key, 1);
				if (null == tmpItem) {
					Out.error("GuildBless toJson4PayLoad config is null:", key);
					continue;
				}
				tmpItem.setGroupCount(ls.get(key));
				items.add(tmpItem.build());
			}
			tempInfo.addAllItem(items);
			itemList.add(tempInfo.build());
		}
		data.addAllItemList(itemList);
		return data.build();
	}

	public GuildResult blessAction(int id, int times) {
		GuildResult ret = new GuildResult();
		BlessItemCO blessItemProp = GuildUtil.getBlessItemById(id);
		if (null == blessItemProp) {
			ret.result = -1;
			ret.des = "参数错误";
			return ret;
		}
		String itemCode = blessItemProp.itemID;
		int haveItemNum = this.player.bag.findItemNumByCode(itemCode);
		if (haveItemNum < times) {
			ret.result = -2;
			ret.des = "材料不足";
			return ret;
		}

		ret = GuildService.blessAction(this.player, id, this.guildData.blessCount, times);

		if (ret.result == 0) {
			this.guildData.blessCount += times;
			this.player.bag.discardItem(itemCode, times, Const.GOODS_CHANGE_TYPE.guild_bless, null, false, false);
			getMiscData().guildBlessToday = 1;
			GuildBlessActionData data = (GuildBlessActionData) ret.data;
			data.blessCount = this.guildData.blessCount;
			// 清除以前的公会buff
			this.guildData.buffIds.clear();
			this.guildData.buffTime = data.buffTime;
			this.guildData.buffIds = data.buffIds;
			for (int i = 0; i < data.finishState.length; i++) {
				if (Const.EVENT_GIFT_STATE.NOT_RECEIVE.getValue() == this.guildData.blessRecState[i])
					this.guildData.blessRecState[i] = data.finishState[i]; // 刷新领取状态
			}

			{// 再添加一些资源
				GuildBless bless = GuildBlessCenter.getInstance().getBless(guild.id);
				if (null != bless) {
					BlessLevelCO levelProp = GuildUtil.getBlessPropByLevel(bless.blessLevel);
					if (levelProp != null) {

						// 仙盟的
						guild.fund += levelProp.addGuildFunds;
						guild.sumFund += levelProp.addGuildFunds;
						Out.info("添加仙盟基金 guildId=", guild.id, ", fund=", levelProp.addGuildFunds);
						GuildServiceCenter.getInstance().saveGuild(guild);

						// 个人的
						player.guildManager.addContribution(levelProp.addGuildPoints, Const.GOODS_CHANGE_TYPE.guild_bless);
						player.guildManager.addTotalContribution(guild.id, levelProp.addGuildPoints);
					}
				}
			}

			pushRedPoint();
			startTimer();
			this.changeBlessBuff();// 祈福增加buff
			this.update();
		}
		return ret;
	}

	public GuildResult receiveBlessGift(int index) {
		GuildResult ret = new GuildResult();
		if (index < 0 && index >= 2) {
			ret.result = -10;
			ret.des = "参数错误";
			return ret;
		}

		if (this.guildData.blessRecState[index] == Const.EVENT_GIFT_STATE.RECEIVED.getValue()) {
			ret.result = -1;
			ret.des = "已领取";
			return ret;
		}

		if (!this.player.bag.testEmptyGridLarge(1)) {
			ret.result = -2;
			ret.des = "背包格子不够";
			return ret;
		}

		ret = GuildService.receiveBlessGift(this.player, index);
		if (ret.result == 0) {
			// 给奖励
			GuildGiftAndBuffData gift = (GuildGiftAndBuffData) ret.data;
			List<NormalItem> list_items = ItemUtil.createItemsByItemCode(gift.itemCode);
			player.bag.addCodeItemMail(list_items, null, GOODS_CHANGE_TYPE.guild_bless_award, SysMailConst.BAG_FULL_COMMON);
			this.guildData.blessRecState[index] = Const.EVENT_GIFT_STATE.RECEIVED.getValue();
			gift.receiveState = GuildCommonUtil.toList(this.guildData.blessRecState);
			this.update();
			pushRedPoint();
		}
		return ret;
	}

	public GuildResult upgradeBlessLevel() {
		GuildResult ret = new GuildResult();
		int haveGold = this.player.moneyManager.getGold();
		ret = GuildService.upgradeBlessLevel(this.player, haveGold);
		UpgradeLevel data = (UpgradeLevel) ret.data;
		if (ret.result == 0) {
			if (data.needGold > 0) {
				this.player.moneyManager.costGold(data.needGold, Const.GOODS_CHANGE_TYPE.guild_upgrade_bless_level);
				this.update();
			}
		}
		return ret;
	}

	public GuildDungeonResult joinGuildDungeon(Area area, int type) {
		GuildDungeonResult data = new GuildDungeonResult();
		data.type = 0;
		if (!area.isNormal() && area.getSceneType() != Const.SCENE_TYPE.GUILD_DUNGEON.getValue() && area.getSceneType() != Const.SCENE_TYPE.WORLD_BOSS.getValue()) {
			data.result = false;
			data.info = LangService.getValue("DUNGEON_ALREAD_IN_DUNGEON");
			return data;
		}

		if (!getGuildId().equals(this.guildData.joinDungeonGuildId) && DateUtil.isSameDay(new Date(), this.guildData.joinDungeonTime)) {
			data.result = false;
			data.info = LangService.getValue("GDUNGEON_NO_TIMES");
			return data;
		}

		GuildService.joinGuildDungeon(this.player.getId(), this.player.getLevel());

		if (LangService.getValue("GDUNGEON_ENTER_ERROR").equals(data.info)) {
			if (type == 0) {
				data.result = true;
				data.type = 1;
			}
			return data;
		}

		if (!data.result) {
			return data;
		}

		if (area.getSceneType() == Const.SCENE_TYPE.GUILD_DUNGEON.getValue() && area.areaId == data.dungeonId) {

			data.result = false;
			data.info = LangService.getValue("DUNGEON_ALREAD_IN_DUNGEON");
			return data;
		}

		// TODO 公会副本 Dispatch 相关功能，待续
		// if(null != data.instanceId && !data.instanceId.isEmpty()){
		// var findInstanceAsync =
		// pomelo.app.rpc.manager.serverRemote.findInstanceAsync;
		// var ret = await(findInstanceAsync({}, {id :
		// data.instanceId},{$orderBy:{createTime:1}}));
		//
		// if( ret.length > 0 ) {
		// this.player.taskManager.dealTaskEvent(TaskType.JOIN_GUILD_INSTANCE,
		// "", 1);
		// AreaUtil.dispatchByInstanceId(
		// this.player,
		// {
		// areaId: data.dungeonId,
		// instanceId : data.instanceId
		// }
		// );
		// return data;
		// }
		// }
		//
		// this.player.taskManager.dealTaskEvent(TaskType.JOIN_GUILD_INSTANCE,
		// "", 1);
		// areaUtil.createAreaAndDispatch(this.player.logicServerId,
		// [this.player.uid], {areaId:data.dungeonId},
		// null,[this.player.id],{enterType :
		// Const.DungeonEnterType.TYPE_NORMAL,guildId :
		// data.guildId,dungeonCount:data.dungeonCount,maxCountDungeonId:data.maxCountDungeonId});
		//
		// pomelo.app.rpc.guild.GuildService.clearDungeonState({},
		// this.player.id,
		// function(code){});

		return data;
	}

	public GuildDungeonResult leaveDungeon(WNPlayer player) {
		GuildDungeonResult data = new GuildDungeonResult();
		data.result = true;
		data.info = "";
		// TODO 离开公会副本 待续。。。
		// var historyAreaId=player.historyAreaId;
		// if(!historyAreaId){
		// data.result=false;data.info=strList.AREA_ID_NULL;
		// return data;
		// }
		//
		// var result = areaUtil.dispatchByAreaId(player,
		// {areaId:historyAreaId,targetX:player.historyX,targetY:player.historyY});
		// if(result.result){
		// return data;
		// }
		// else{
		// data.result=false;
		// data.info= strList.SOMETHING_ERR;
		// return data;}

		return data;
	}

	public List<SuperScriptType> getSuperScript() {
		List<SuperScriptType> list = new ArrayList<SuperScriptType>();
		SuperScriptType.Builder data = SuperScriptType.newBuilder();
		data.setType(Const.SUPERSCRIPT_TYPE.GUILD.getValue());
		data.setNumber(0);
		if (!this.player.functionOpenManager.isOpen(Const.FunctionType.GUILD.getValue())) {
			list.add(data.build());
			return list;
		}
		if (!this.isInGuild()) {
			list.add(data.build());
			return list;
		}
		GuildPositionCO jobProp = GuildUtil.getGuildJobPropByJobId(member.job);
		if (null == jobProp) {
			list.add(data.build());
			return list;
		}
		if (jobProp.right3 > 0) {
			int applyCount = GuildUtil.getGuildApplyCount(getGuildId());
			if (applyCount > 0) {
				int tmp = data.getNumber();
				data.setNumber(tmp + 1); // 有申请需要审核
			}
		}

		if (null != this.guildData.blessRecState) {
			for (int i = 0; i < this.guildData.blessRecState.length; i++) {
				if (this.guildData.blessRecState.length <= i) {
					continue;
				}
				if (this.guildData.blessRecState[i] != Const.EVENT_GIFT_STATE.RECEIVED.getValue()) {
					int tmp = data.getNumber();
					data.setNumber(tmp + 1); // 有祈福奖励未领取
				}
			}
		}

		if (this.throwAwardState > 0) {
			int tmp = data.getNumber();
			data.setNumber(tmp + 1); // 可掷点
		}

		return list;

	}

	public void setJoinDungeonGuildId(String guildId) {
		if (!this.guildData.joinDungeonGuildId.equals(guildId)) {
			this.guildData.joinDungeonGuildId = guildId;
		}

		Date now = new Date();
		if (!DateUtil.isSameDay(now, this.guildData.joinDungeonTime)) {
			this.guildData.joinDungeonTime = now;
		}
	}

	public String getGuildId() {
		return guild != null ? guild.id : "";
	}

	public String getGuildIcon() {
		return guild != null ? guild.icon : "";
	}

	public void setGuildJobInfo(String guildId, String guildName, int guildJob, String guildIcon) {
		if (null == guild)
			guild = new GuildPO();

		if (null == member)
			member = new GuildMemberPO();

		guild.id = guildId;
		guild.name = guildName;
		guild.icon = guildIcon;
		guild.job = guildJob;
		member.job = guildJob;
	}

	/**
	 * 计算仙盟竞拍分红收益值.
	 */
	public int calAuctionBonus() {
		if (StringUtils.isEmpty(getGuildId())) {
			return 0;
		}
		Set<String> ids = GuildUtil.getGuildMemberIdList(getGuildId());
		if (ids == null || ids.isEmpty()) {
			return 0;
		}
		// 总收入，除以人数
		return Math.min(GlobalConfig.Auction_MaxBonus, guild.auctionBonus / ids.size());
	}

	public void addAuctionBonus(int value) {
		if (guild == null) {
			return;
		}

		// 扣个税，系统分成...
		value = value * GlobalConfig.Auction_GuildTaxation / 100;

		int before = guild.auctionBonus;
		guild.auctionBonus += value;
		int after = guild.auctionBonus;
		Out.info("add auction bonus. id=", player.getId(), ",name=", player.getName(), ",before=", before, ",value=", value, ",after=", after);
	}
}