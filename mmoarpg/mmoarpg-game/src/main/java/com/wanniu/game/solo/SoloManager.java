package com.wanniu.game.solo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import com.wanniu.core.game.JobFactory;
import com.wanniu.core.game.LangService;
import com.wanniu.core.logfs.Out;
import com.wanniu.core.util.DateUtil;
import com.wanniu.game.GWorld;
import com.wanniu.game.area.Area;
import com.wanniu.game.area.AreaUtil;
import com.wanniu.game.common.Const;
import com.wanniu.game.common.Const.GOODS_CHANGE_TYPE;
import com.wanniu.game.common.Const.TaskType;
import com.wanniu.game.common.ConstsTR;
import com.wanniu.game.common.HackerException;
import com.wanniu.game.common.Utils;
import com.wanniu.game.common.msg.WNNotifyManager;
import com.wanniu.game.data.GameData;
import com.wanniu.game.data.GameMapCO;
import com.wanniu.game.data.SoloMonsterCO;
import com.wanniu.game.data.SoloRankCO;
import com.wanniu.game.data.base.MapBase;
import com.wanniu.game.data.ext.SoloRankExt;
import com.wanniu.game.five2Five.Five2FiveService;
import com.wanniu.game.item.ItemUtil;
import com.wanniu.game.item.NormalItem;
import com.wanniu.game.item.VirtualItemType;
import com.wanniu.game.mail.MailUtil;
import com.wanniu.game.mail.SysMailConst;
import com.wanniu.game.mail.data.MailData;
import com.wanniu.game.mail.data.MailData.Attachment;
import com.wanniu.game.mail.data.MailSysData;
import com.wanniu.game.player.GlobalConfig;
import com.wanniu.game.player.PlayerUtil;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.player.bi.LogReportService;
import com.wanniu.game.poes.SoloDataPO;
import com.wanniu.game.poes.SoloDataPO.RankStatus;
import com.wanniu.game.solo.SoloService.OpenInfo;
import com.wanniu.game.solo.po.BattleRecordPO;
import com.wanniu.game.solo.vo.ResultVO;
import com.wanniu.game.solo.vo.ResultVO.KEY;
import com.wanniu.game.team.TeamData.TeamMemberData;
import com.wanniu.game.team.TeamService;
import com.wanniu.redis.PlayerPOManager;

import pomelo.Common.OpenTimeInfo;
import pomelo.area.PlayerHandler.SuperScriptType;
import pomelo.area.SoloHandler.BattleRecord;
import pomelo.area.SoloHandler.BattleRecordResponse;
import pomelo.area.SoloHandler.CancelMatchPush;
import pomelo.area.SoloHandler.DailyRewardItem;
import pomelo.area.SoloHandler.DrawDailyRewardResponse;
import pomelo.area.SoloHandler.DrawRankRewardResponse;
import pomelo.area.SoloHandler.GameResult;
import pomelo.area.SoloHandler.LeftSoloTimePush;
import pomelo.area.SoloHandler.MyInfo;
import pomelo.area.SoloHandler.NewsInfo;
import pomelo.area.SoloHandler.NewsInfoResponse;
import pomelo.area.SoloHandler.OnSoloMatchedPush;
import pomelo.area.SoloHandler.RankReward;
import pomelo.area.SoloHandler.RewardInfoResponse;
import pomelo.area.SoloHandler.SoloDailyReward;
import pomelo.area.SoloHandler.SoloInfoResponse;

public class SoloManager {
	private static enum REWARD_TYPE {
		DAILY_REWARD(1), WEEKLY_REWARD(2), RANK_REWARD(3);

		private int value;

		private REWARD_TYPE(int value) {
			this.value = value;
		}

	};

	private long startJoinTime;// 开始报名时间（不存档）
	private WNPlayer player;
	private SoloMonsterCO robot;
	private SoloNewsHandler newsHandler;
	public SoloDataPO soloData;
	private boolean busy;

	public void setBusy(boolean busy) {
		this.busy = busy;
	}

	/**
	 * @return 返回当前是否处于忙态，以确定是否该销毁player
	 */
	public boolean isBusy() {
		return busy;
	}

	public SoloManager(WNPlayer player) {
		this.player = player;
		this.soloData = loadSoloData(player.getId());
		this.startJoinTime = 0; // 开始报名时间（不存档）

		newsHandler = new SoloNewsHandler(this.player);
	}

	private SoloDataPO loadSoloData(String playerId) {
		SoloDataPO soloDataDb = PlayerPOManager.findPO(ConstsTR.player_solo_dataTR, playerId, SoloDataPO.class);
		if (soloDataDb == null) {
			soloDataDb = new SoloDataPO(playerId);
			PlayerPOManager.put(ConstsTR.player_solo_dataTR, playerId, soloDataDb);
		}
		return soloDataDb;
	}

	/**
	 * @return 当前资历积分
	 */
	public int getScore() {
		return this.soloData.score;
	}

	/**
	 * 
	 * @param score 可能加，也可能是减
	 */
	public void addScore(int score) {
		int oldScore = this.soloData.score;
		this.soloData.score += score;
		if (this.soloData.score < 0) {
			this.soloData.score = 0;
		}
		int oldRankId = this.soloData.rankId;
		// 积分变动后重新计算下段位id
		this.soloData.rankId = SoloService.getInstance().calcRankId(this.soloData.score);
		if (this.soloData.rankId != oldRankId) {// 段位发生变化
			this.soloData.rankGotTime = new Date();
			if (this.soloData.rankId > oldRankId) {// 段位晋级发送传言
				this.newsHandler.onRankIdChanged(this.soloData.rankId);
			}
		}
		this.calcRankReward(oldRankId);

		if (oldScore != this.soloData.score) {// 更新排行
			SoloService.getInstance().refreshSoloScoreToLeaderBoard(player, this.soloData.score);
		}
	}

	/**
	 * 增加宗师币
	 */
	public void addSolopoint(int num, GOODS_CHANGE_TYPE origin) {
		if (num == 0) {// 正常逻辑
			return;
		}
		if (num < 0) {
			throw new HackerException("增加宗师币时参数小于0.");
		}

		int before = soloData.soloPoint;
		// 溢出判定
		if (0L + soloData.soloPoint + num > Integer.MAX_VALUE) {
			soloData.soloPoint = Integer.MAX_VALUE;
		} else {
			soloData.soloPoint += num;
		}
		int after = soloData.soloPoint;

		Out.info("add solopoint. id=", player.getId(), ",name=", player.getName(), ",before=", before, ",value=", num, ",after=", after, ",origin=", origin.value);
		LogReportService.getInstance().ansycReportMoneyFlow(player.getPlayer(), VirtualItemType.SOLOPOINT, before, LogReportService.OPERATE_ADD, num, after, origin.value);

		player.pushDynamicData(Utils.ofMap("solopoint", soloData.soloPoint));
	}

	/**
	 * 消耗宗师币
	 */
	public boolean costSolopoint(int num, GOODS_CHANGE_TYPE origin) {
		if (num == 0) {
			return true;
		}

		if (!enoughSolopoint(num)) {
			return false;
		}

		int before = soloData.soloPoint;
		soloData.soloPoint -= num;// 扣钱
		int after = soloData.soloPoint;
		Out.info("cost solopoint. id=", player.getId(), ",name=", player.getName(), ",before=", before, ",value=", num, ",after=", after, ",origin=", origin.value);
		LogReportService.getInstance().ansycReportMoneyFlow(player.getPlayer(), VirtualItemType.SOLOPOINT, before, LogReportService.OPERATE_COST, num, after, origin.value);

		player.pushDynamicData(Utils.ofMap("solopoint", this.soloData.soloPoint));
		return true;
	}

	public boolean enoughSolopoint(int num) {
		if (num < 0) {
			throw new HackerException("判定玩家身上的宗师币是否足够时参数小于0.");
		}
		return getSolopoint() >= num;
	}

	/**
	 * 获取当前宗师币
	 */
	public int getSolopoint() {
		return this.soloData.soloPoint;
	};

	// Functions for Handler //////////////////////////////////////////////////

	// 处理单挑界面
	public void handleSoloInfo(SoloInfoResponse.Builder res) {
		MapBase sceneProp = AreaUtil.getAreaProp(GlobalConfig.Solo_MapID);
		if (sceneProp == null) {
			Out.error("there is no data of MapID: ", GlobalConfig.Solo_MapID, " in sceneProps");
			res.setS2CCode(Const.CODE.FAIL);
			res.setS2CMsg(LangService.getValue("SOMETHING_ERR"));
			return;
		}
		this.checkTerm();
		this.calcDailyChest();

		MyInfo.Builder myInfo = MyInfo.newBuilder();

		myInfo.setRank(getCurRank());
		myInfo.setScore(this.soloData.score);
		myInfo.setMaxContWinTimes(this.soloData.maxContWinTimes);
		myInfo.setMaxContLoseTimes(this.soloData.maxContLoseTimes);
		myInfo.setBattleTimes(this.soloData.battleTimes);
		myInfo.setWinTotalTimes(this.soloData.winTotalTimes);
		myInfo.setLoseTotalTimes(this.soloData.loseTotalTimes);
		myInfo.setSeasonEndTime(String.valueOf(SoloService.getInstance().getSeasonEndTime()));
		myInfo.setMyToken(this.getSolopoint());
		myInfo.setTodayToken(this.soloData.soloPointToday);
		myInfo.setMaxToken(GlobalConfig.Solo_MasterCoinGainLimit);
		myInfo.setAvgMatchTime(SoloMatcher.getInstance().getAvgMatchingTime());
		myInfo.setStartJoinTime(0);
		if (isInMatching()) {
			myInfo.setStartJoinTime((int) Math.floor(this.startJoinTime / 1000));
		}

		res.setS2CCode(Const.CODE.OK);
		res.setS2CMyInfo(myInfo.build());

		updateRedPoint();
	}

	/**
	 * 处理传闻请求
	 * 
	 * @param res
	 */
	public void handleNewsInfo(NewsInfoResponse.Builder res) {
		List<NewsInfo> msgs = new ArrayList<NewsInfo>();

		Map<String, List<String>> allNews = SoloNewsHandler.getAllSoloNews(GWorld.__SERVER_ID);
		for (String dateStrKey : allNews.keySet()) {
			NewsInfo.Builder newsInfo = NewsInfo.newBuilder();
			newsInfo.setDate(dateStrKey);
			newsInfo.addAllMessage(allNews.get(dateStrKey));
			msgs.add(newsInfo.build());
		}

		res.setS2CCode(Const.CODE.OK);
		res.addAllS2CSoloMessages(msgs);
	}

	/**
	 * 处理战斗记录请求
	 * 
	 * @param res
	 */
	public void handleBattleRecord(BattleRecordResponse.Builder res) {
		res.setS2CCode(Const.CODE.OK);
		res.setS2CCanglang(this.soloData.getWinRate(Const.Profession.canglang));
		res.setS2CYujian(this.soloData.getWinRate(Const.Profession.yujian));
		res.setS2CYixian(this.soloData.getWinRate(Const.Profession.yixian));
		res.setS2CShenjian(this.soloData.getWinRate(Const.Profession.shenjian));
		res.setS2CLinghu(this.soloData.getWinRate(Const.Profession.linghu));
		List<BattleRecord> records = new ArrayList<>();
		for (int i = this.soloData.battleRecords.size() - 1; i >= 0; i--) {
			BattleRecordPO br = this.soloData.battleRecords.get(i);
			BattleRecord.Builder brBuilder = BattleRecord.newBuilder();

			brBuilder.setResult(br.result);
			brBuilder.setBattleTime(String.valueOf(br.battleTime.getTime()));
			brBuilder.setScore(br.score);
			brBuilder.setScoreChange(br.scoreChange);
			brBuilder.setVsName(br.vsName);
			brBuilder.setVsGuildName(br.vsGuildName);
			brBuilder.setVsPro(br.vsPro);
			brBuilder.setVsScore(br.vsScore);
			records.add(brBuilder.build());
		}

		res.addAllS2CBatttleList(records);
	}

	/**
	 * @return 返回当前排名
	 */
	private int getCurRank() {
		return (int) SoloService.getInstance().getRank(this.player.getId());
	}

	// 处理奖励界面
	public void handleRewardInfo(RewardInfoResponse.Builder res) {
		List<RankReward> rankRewards = new ArrayList<>();
		for (RankStatus s : this.soloData.rankRewards.values()) {
			RankReward.Builder rb = RankReward.newBuilder();
			rb.setRankId(s.getRankId());
			rb.setStatus(s.getStatus());
			rankRewards.add(rb.build());
		}

		res.setS2CCode(Const.CODE.OK);
		res.addAllS2CRankRewards(rankRewards);
		res.setS2CCurrRank(getCurRank());// 当前排名

		// 每日奖励
		List<SoloDailyReward> dailyRewards = new ArrayList<>();
		for (RankStatus s : this.soloData.dailyRewards) {
			SoloDailyReward.Builder drb = SoloDailyReward.newBuilder();
			SoloRankCO prop = GameData.SoloRanks.get(soloData.rankId);
			if (prop != null) {// 每日奖励预览处理
				List<NormalItem> dailyReward = ItemUtil.createItemsByTcCode(prop.chestReward);
				for (NormalItem item : dailyReward) {
					DailyRewardItem.Builder drib = DailyRewardItem.newBuilder();
					drib.setItemCode(item.itemCode());
					drib.setItemNum(item.getNum());
					drb.addItems(drib.build());
				}
			}
			drb.setRankId(s.getRankId());
			drb.setStatus(s.getStatus());
			dailyRewards.add(drb.build());
		}
		res.addAllS2CDailyRewards(dailyRewards);
		res.setS2CDailyBattleTimes(this.soloData.dailyBattleTimes);
		return;
	};

	private String getGiftTypeName(REWARD_TYPE type) {
		return LangService.getValue("SOLO_GIFTTYPE" + type.value);
	}

	// 处理领取段位奖励
	public void handleDrawRankReward(int rankId, DrawRankRewardResponse.Builder res) {
		SoloRankExt prop = GameData.SoloRanks.get(rankId);
		RankStatus myRankReward = this.soloData.rankRewards.get(rankId);
		if (prop == null || myRankReward == null) {
			res.setS2CCode(Const.CODE.FAIL);
			res.setS2CMsg(LangService.getValue("SOLO_REWARD_NOT_EXIST"));
			return;
		}

		int status = myRankReward.getStatus();
		if (status == 2) {// 已领取
			res.setS2CCode(Const.CODE.FAIL);
			res.setS2CMsg(LangService.getValue("SOLO_REWARD_HAS_DRAWED"));
			return;
		}
		if (status != 1) {// 不可领取
			res.setS2CCode(Const.CODE.FAIL);
			res.setS2CMsg(LangService.getValue("SOLO_REWARD_CANNOT_DRAW"));
			return;
		}

		List<NormalItem> rankReward = ItemUtil.createItemsByItemCode(prop.rankRewards);

		if (!this.player.getWnBag().testAddEntityItems(rankReward, true)) {
			res.setS2CCode(Const.CODE.FAIL);
			res.setS2CMsg(LangService.getValue("BAG_NOT_ENOUGH_POS"));

			return;
		}

		this.player.getWnBag().addEntityItems(rankReward, Const.GOODS_CHANGE_TYPE.solo);
		myRankReward.setStatus(2);// 标识为已领取

		this.newsHandler.onGetGift(this.getGiftTypeName(REWARD_TYPE.RANK_REWARD), rankReward);

		this.player.taskManager.dealTaskEvent(TaskType.SINCOM, "1", 1);

		this.updateRedPoint();
		res.setS2CCode(Const.CODE.OK);
	}

	// 处理领取每日奖励
	public void handleDrawDailyReward(int index, DrawDailyRewardResponse.Builder res) {
		if (index > this.soloData.dailyRewards.size()) {
			res.setS2CCode(Const.CODE.FAIL);
			res.setS2CMsg(LangService.getValue("SOLO_REWARD_NOT_EXIST"));
			return;
		}
		index = index - 1; // 客户端的索引从1开始
		RankStatus rankStatus = this.soloData.dailyRewards.get(index);
		// SoloRankCO prop = GameData.SoloRanks.get(rankStatus.getRankId());
		SoloRankCO prop = GameData.SoloRanks.get(soloData.rankId);

		if (prop == null || rankStatus.getStatus() != 1) {// 找不到奖励对照表或者是状态是不可领取的
			res.setS2CCode(Const.CODE.FAIL);
			res.setS2CMsg(LangService.getValue("SOLO_REWARD_NOT_EXIST"));
			return;
		}

		List<NormalItem> dailyReward = ItemUtil.createItemsByTcCode(prop.chestReward);
		if (!this.player.getWnBag().testAddEntityItems(dailyReward, true)) {
			res.setS2CCode(Const.CODE.FAIL);
			res.setS2CMsg(LangService.getValue("BAG_NOT_ENOUGH_POS"));
			return;
		}
		List<DailyRewardItem> items = new ArrayList<>();
		for (NormalItem item : dailyReward) {
			DailyRewardItem.Builder drib = DailyRewardItem.newBuilder();
			drib.setItemCode(item.itemCode());
			drib.setItemNum(item.getNum());
			items.add(drib.build());
		}
		res.addAllS2CDailyRewardItems(items);

		this.player.getWnBag().addEntityItems(dailyReward, Const.GOODS_CHANGE_TYPE.solo);
		this.soloData.dailyRewards.remove(index);
		this.soloData.dailyReceived += 1;
		// this.soloData.dailyBattleTimes -= GlobalConfig.Solo_PKForChest;

		this.newsHandler.onGetGift(this.getGiftTypeName(REWARD_TYPE.DAILY_REWARD), dailyReward);

		this.player.taskManager.dealTaskEvent(TaskType.SINCOM, "1", 1);
		this.updateRedPoint();
		res.setS2CCode(Const.CODE.OK);
		return;
	}

	// 处理报名参战
	public ResultVO handleJoinSolo() {
		ResultVO result = new ResultVO(true);

		GameMapCO sceneProp = GameData.GameMaps.get(GlobalConfig.Solo_MapID);
		if (sceneProp == null) {
			Out.error("there is no data of MapID: ", GlobalConfig.Solo_MapID, " in sceneProps", null);
			result.result = false;
			result.info = "问道大会地图不存在";
			return result;
		}

		if (this.isBusy() || this.isInMatching()) {
			result.set(KEY.AVG_MATCHTIME, SoloMatcher.getInstance().getAvgMatchingTime());
			result.set(KEY.START_JOINTIME, (int) Math.floor(this.startJoinTime / 1000));
			return result;
		}

		// 在5V5里面
		if (Five2FiveService.getInstance().applyMatchTime(player.getId()) != null) {
			result.result = false;
			result.info = LangService.getValue("FIVE_2_FIVE_BUSY");
			return result;
		}

		if (!this.soloData.havePlayed) {
			return this.firstJoinSolo(sceneProp);
		}
		this.checkTerm();

		if (!SoloService.getInstance().isInOpenTime()) {
			result.result = false;
			result.info = LangService.getValue("SOLO_NOT_IN_OPEN_TIME");
			return result;
		}

		WNPlayer matchedPlayer = SoloMatcher.getInstance().findMatchedPlayer(this.soloData.score, this.player.getId());
		if (matchedPlayer != null
		// && PlayerUtil.isOnline(matchedPlayer.getId())
		) {// 匹配对手成功
			SoloMatcher.addWaiter(this.player, matchedPlayer);
			result.set(ResultVO.KEY.AVG_MATCHTIME, -1); // 匹配成功，不需要等待了
			result.set(ResultVO.KEY.START_JOINTIME, (int) Math.floor(this.startJoinTime / 1000));
		} else {
			// 加入到匹配列表
			SoloMatcher.getInstance().addToMatchingList(this.soloData.score, this.player);
			this.startJoinTime = System.currentTimeMillis();

			result.set(ResultVO.KEY.AVG_MATCHTIME, SoloMatcher.getInstance().getAvgMatchingTime());
			result.set(ResultVO.KEY.START_JOINTIME, (int) Math.floor(this.startJoinTime / 1000));
		}

		return result;
	};

	private AtomicBoolean inFight = new AtomicBoolean(false);

	/**
	 * 首次进入问道大会，直接匹配机器人
	 * 
	 * @param sceneProp
	 * @return
	 */
	private ResultVO firstJoinSolo(GameMapCO sceneProp) {
		ResultVO result = new ResultVO(true);
		this.player.dailyActivityMgr.onEvent(Const.DailyType.SOLO, "0", 1);
		setBusy(true);// 一旦匹配成功就算设置为忙态
		// SoloMatcher.getInstance().updateMatchingTime(getMatchedTime());
		this.robot = SoloUtil.getSoloMonsterPropByPro(this.player.getPro());
		OnSoloMatchedPush.Builder msg = OnSoloMatchedPush.newBuilder();
		msg.setS2CCode(Const.CODE.OK);
		msg.setS2CVsPlayerName(robot.monName);
		msg.setS2CVsPlayerPro(robot.proID);
		msg.setS2CVsPlayerLevel(robot.monLevel);
		msg.addAllS2CVsPlayerAvatars(PlayerUtil.getBattleServerAvatar(robot.proID));
		msg.setS2CWaitResponseTimeSec(GlobalConfig.Solo_EnterTime);
		msg.setS2CIsRobot(1);
		this.player.receive("area.soloPush.onSoloMatchedPush", msg.build());
		JobFactory.addDelayJob(new Runnable() {
			@Override
			public void run() {
				if (inFight.compareAndSet(false, true)) {
					enterSoloRobotArea();
				}
			}
		}, GlobalConfig.Solo_EnterTime, TimeUnit.SECONDS);

		result.set(ResultVO.KEY.AVG_MATCHTIME, -1);
		result.set(ResultVO.KEY.START_JOINTIME, (int) Math.floor(this.startJoinTime / 1000));

		return result;
	};

	private boolean enterSoloRobotArea() {
		this.onEnterSoloArea();
		Map<String, Object> userData = Utils.ofMap("isAddRobot", true, "pro", player.getPro());
		Area area = AreaUtil.createAreaAndDispatch(player, Arrays.asList(player.getId()), player.getLogicServerId(), GlobalConfig.Solo_MapID, userData);
		if (area == null || this.robot == null) {
			return false;
		}

		SoloArea soloArea = (SoloArea) area;
		soloArea.addRobot(this.robot);
		if (!PlayerUtil.isOnline(player.getId())) {
			player.getXmdsManager().playerReady(player.getId());
			area.onPlayerAutoBattle(player, true);
		}

		return true;
	}

	private ResultVO firstJoinSoloBattle() {
		ResultVO result = new ResultVO(true);
		if (inFight.compareAndSet(false, true)) {
			if (this.enterSoloRobotArea()) {
				result.result = true;
				// result.set(ResultVO.KEY.AVG_MATCHTIME, -1);
				// result.set(ResultVO.KEY.START_JOINTIME, (int)
				// Math.floor(this.startJoinTime / 1000));
			} else {
				result.result = false;
				result.info = "进入问道大会场景异常";
			}
			Out.debug("firstJoinSolo result:", result);
		}

		return result;
	}

	/**
	 * 匹配对手成功确认参战
	 * 
	 * @return
	 */
	public ResultVO handleJoinSoloBattle() {
		ResultVO result = new ResultVO(true);

		if (!this.soloData.havePlayed) {
			return this.firstJoinSoloBattle();
		}

		if (!SoloService.getInstance().isInOpenTime()) {
			result.result = false;
			result.info = LangService.getValue("SOLO_NOT_IN_OPEN_TIME");
			return result;
		}

		if (!SoloMatcher.getInstance().joinBattle(this.player)) {// 没有匹配成功的对手
			result.result = false;
			result.info = LangService.getValue("SOLO_NO_ENEMY");
			return result;
		}

		return result;
	}

	/**
	 * 处理退出报名。在参与其他活动例如打副本、切场景等等的时候都要调研此方法
	 */
	public void handleQuitSolo() {
		this.quitMatching(true);
	}

	// 处理查询奖励
	public ResultVO handleQueryReward() {
		ResultVO result = new ResultVO(true);
		result.set(ResultVO.KEY.HAS_REWARD, this.hasReward());

		return result;
	};

	// 处理离开单挑场景
	public ResultVO handleLeaveSoloArea() {
		this.setBusy(false);
		ResultVO data = new ResultVO(true, "");

		int historyAreaId = this.player.getPlayerTempData().historyAreaId;
		if (historyAreaId <= 0) {
			data.result = false;
			data.info = LangService.getValue("AREA_ID_NULL");
			return data;
		}

		Area area = AreaUtil.dispatchByAreaId(this.player, historyAreaId, this.player.getPlayerTempData().historyX, this.player.getPlayerTempData().historyY);
		if (area != null) {
			return data;
		} else {
			data.result = false;
			data.info = LangService.getValue("SOMETHING_ERR");
			return data;
		}

	};

	// Functions for Handler //////////////////////////////////////////////////

	/**
	 * WNPlayer每日时钟触发
	 */
	public void refreshNewDay() {
		this.resetData();

		Out.debug("==========>> SoloManager.refreshNewDay() on ", new Date());
	};

	public void resetData() {
		if (!player.functionOpenManager.isOpen(Const.FunctionType.SOLO.getValue())) {
			return;
		}

		this.soloData.dailyReceived = 0;
		// if (this.soloData.dailyReceived < 0) {
		// this.soloData.dailyReceived = 0;
		// }
		// 每日刷一个宝箱
		if (soloData.dailyRewards.size() < 2) {
			RankStatus s = new RankStatus();
			s.setRankId(getRankId());
			s.setStatus(0);
			this.soloData.dailyRewards.add(s);
		}
		// for (RankStatus r : soloData.dailyRewards) {
		// if (r.status == 2) {
		// r.status = 0;
		// }
		// }
		this.soloData.dailyBattleTimes = 0;

		this.soloData.soloPointToday = 0;
		this.checkTerm();
		this.soloData.dailyResetTime = new Date();

		// this.update();

		Out.debug("==========>> SoloManager.resetData() on ", new Date());
	};

	/**
	 * 检查赛季是否已经重置，如果已经重置，会重置积分和段位、奖励
	 */
	private void checkTerm() {
		if (this.soloData.term < SoloService.getInstance().getTerm()) {// 已经过期了
			// this.soloData.dailyRewards = new ArrayList<>();
			// this.soloData.dailyBattleTimes = 0;
			this.soloData.dailyRewardRound = 0;

			this.resetRankId();// 重置段位及相关的奖励
			this.calcRankReward(1);// 重新计算新赛季的奖励
			this.soloData.term = SoloService.getInstance().getTerm();

			this.soloData.contWinTimes = 0;
			this.soloData.extrWinTimes = 0;
			this.soloData.maxContWinTimes = 0;
			this.soloData.contLoseTimes = 0;
			this.soloData.maxContLoseTimes = 0;

			this.soloData.battleTimes = 0;
			this.soloData.winTotalTimes = 0;
			this.soloData.loseTotalTimes = 0;
			this.soloData.winTimes_canglang = 0;
			this.soloData.winTimes_yujian = 0;
			this.soloData.winTimes_yixian = 0;
			this.soloData.winTimes_shenjian = 0;
			this.soloData.winTimes_linghu = 0;
			this.soloData.battleTimes_canglang = 0;
			this.soloData.battleTimes_yujian = 0;
			this.soloData.battleTimes_yixian = 0;
			this.soloData.battleTimes_shenjian = 0;
			this.soloData.battleTimes_linghu = 0;
			this.soloData.battleRecords = new ArrayList<>();

			SoloService.getInstance().refreshSoloScoreToLeaderBoard(player, this.soloData.score);
		}
	}

	/**
	 * @return 返回段位名称
	 */
	private String getRankName() {
		SoloRankCO rank = GameData.SoloRanks.get(getRankId());
		if (rank != null) {
			return rank.rankName;
		}

		return String.valueOf(getRankId());
	}

	/**
	 * @return 返回段位id
	 */
	private int getRankId() {
		return this.soloData.rankId;
	}

	/**
	 * 邮件发送上赛季未领取的段位奖励后，重置奖励
	 */
	private void resetRankReward() {
		ArrayList<Attachment> list = new ArrayList<>();
		MailSysData mailData = new MailSysData(SysMailConst.SOLO_REWARD_LAST);
		mailData.replace = new HashMap<>();
		mailData.replace.put("rank", getRankName());
		for (RankStatus rew : this.soloData.rankRewards.values()) {
			if (rew.getStatus() == 1) {// 有奖励可领取
				SoloRankExt prop = GameData.SoloRanks.get(rew.getRankId());
				if (prop == null) {
					continue;
				}
				for (String itemCode : prop.rankRewards.keySet()) {
					MailData.Attachment attach = new MailData.Attachment();
					attach.itemCode = itemCode;
					attach.itemNum = prop.rankRewards.get(itemCode);
					list.add(attach);
				}
			}
		}
		if (list.size() > 0) {// 有未领取的奖励才发邮件
			mailData.attachments = list;
			MailUtil.getInstance().sendMailToOnePlayer(this.player.getId(), mailData, GOODS_CHANGE_TYPE.solo);
		}
		this.soloData.rankRewards = new HashMap<>();
	}

	private void resetRankId() {// 上赛季段位对折，再赋予对应的段位积分要求
		int newRankId = this.soloData.rankId / 2;
		if (newRankId == 0) {
			newRankId = 1;
		}
		if (newRankId > 10) {
			Out.error("resetRankId failed. something error occurred in :", this.getClass());
		}
		int newScore = GameData.SoloRanks.get(newRankId).rankScore;
		this.soloData.rankId = newRankId;
		this.soloData.score = newScore;
		this.resetRankReward();
	}

	public boolean isInMatching() {
		return SoloMatcher.getInstance().isInMatchingList(this.player.getId());
	};

	/**
	 * 退出匹配
	 * 
	 * @param isSilent
	 */
	public void quitMatching(boolean isSilent) {
		if (this.isInMatching()) {
			SoloMatcher.getInstance().delToMatchingList(this.player.getId());
			if (!isSilent) {
				PlayerUtil.sendSysMessageToPlayer(LangService.getValue("SOLO_MATCHING_HAVE_QUIT"), this.player.getId(), Const.TipsType.NORMAL);
				CancelMatchPush.Builder matchPush = CancelMatchPush.newBuilder();
				matchPush.setS2CCode(Const.CODE.OK);
				player.receive("area.soloPush.cancelMatchPush", matchPush.build());
			}
		}
	}

	/**
	 * 一场战斗后计算是否可以激活每日宝箱
	 */
	private boolean calcDailyChestOnBattled() {
		// 根据可领取宝箱个数计算需要的总战斗场次
		int times = 0;
		for (RankStatus s : this.soloData.dailyRewards) {
			if (s.getStatus() == 0) {
				times += GlobalConfig.Solo_PKForChest;
			}
		}
		if (times != 0) {
			this.soloData.dailyBattleTimes++;
			if (this.soloData.dailyBattleTimes >= GlobalConfig.Solo_PKForChest) {
				for (int i = 0; i < this.soloData.dailyRewards.size(); i++) {
					RankStatus s = this.soloData.dailyRewards.get(i);
					if (s.getStatus() == 0) {// 未激活
						s.setRankId(getRankId());
						s.setStatus(1);
						break;
					}
				}
				WNNotifyManager.getInstance().pushNewReward(this.player);
				this.soloData.dailyBattleTimes = 0;
				return true;
			}
		}
		return false;
	}

	/**
	 * @return 返回已经激活的日常宝箱数量
	 */
	// private int getActivatedChestNum() {
	// int num = 0;
	// for (RankStatus s : this.soloData.dailyRewards) {
	// if (s.getStatus() != 0) {
	// num++;
	// }
	// }
	//
	// return num;
	// }

	/**
	 * 计算日常宝箱奖励
	 */
	private void calcDailyChest() {
		// if (this.soloData.dailyRewards.size() >=
		// GlobalConfig.Solo_ChestMaxCount
		// || this.soloData.dailyReceived >= GlobalConfig.Solo_ChestMaxCount) {
		// return;
		// }

		// int round = GlobalConfig.Solo_ChestMaxCount -
		// this.soloData.dailyRewards.size();// 计算最多还能产生几个箱子

		// 首次进入刷一个宝箱
		if (!soloData.havePlayed && soloData.dailyRewards.isEmpty()) {
			RankStatus s = new RankStatus();
			s.setRankId(getRankId());
			s.setStatus(0);
			this.soloData.dailyRewards.add(s);
		}
	}

	/**
	 * 根据当前最新段位计算段位奖励
	 * 
	 * @param oldRankId 段位变更前的段位，以判断是降级还是晋级
	 */
	private void calcRankReward(int oldRankId) {
		RankStatus rankReward = this.soloData.rankRewards.get(getRankId());
		if (rankReward == null) {
			if (this.soloData.rankRewards.size() == 0) {// 首次完成一次挑战给段位奖励
				for (int i = 1; i <= getRankId(); i++) {// 段位从1开始遍历
					RankStatus rb = new RankStatus();
					rb.setRankId(i);
					rb.setStatus(1);// 设置为奖励可领取
					this.soloData.rankRewards.put(rb.rankId, rb);
				}
			} else {// 非首次就是更高的段位
				RankStatus rb = new RankStatus();
				rb.setRankId(getRankId());
				rb.setStatus(1);// 设置为奖励可领取
				this.soloData.rankRewards.put(rb.rankId, rb);
			}
			// WNNotifyManager.getInstance().pushNewReward(this.player);
			updateRedPoint();
		} else {// 曾经达到过的段位奖励都可以领取，不管是否降级
			// if(oldRankId>this.soloData.rankId){//段位降级
			// RankStatus oldStatus = this.soloData.rankRewards.get(oldRankId);
			// if(oldStatus!=null && oldStatus.getStatus()!=2){//未领取过
			// oldStatus.setStatus(0);//设置为不可领取
			// }else{
			// Out.debug("Logic error in calcRankIdReward: "+oldStatus.rankId
			// +"="+oldStatus.status);
			// }
			// }
			if (rankReward.getStatus() != 2) {// 曾经达到过段位，但是当时没领取奖励发生降级
				rankReward.setStatus(1);
				// WNNotifyManager.getInstance().pushNewReward(this.player);
				updateRedPoint();
			}
		}
	}

	/**
	 * 赚取的宗师币
	 * 
	 * @param result
	 */
	private int winSoloPoint(int result) {
		int point = 0;
		if (this.soloData.soloPointToday < GlobalConfig.Solo_MasterCoinGainLimit) {
			point = GlobalConfig.Solo_MasterCoinGainLimit - this.soloData.soloPointToday;// 计算离上限的差值
		}
		if (result == 1) {
			if (point < GlobalConfig.Solo_WinGainMasterCoin) {
				return point;
			}
			return GlobalConfig.Solo_WinGainMasterCoin;
		} else {
			if (point < GlobalConfig.Solo_LoseGainMasterCoin) {
				return point;
			}
			return GlobalConfig.Solo_LoseGainMasterCoin;
		}
	}


	/**
	 * @param result	1-胜 2-负 3-平
	 * @param vsPlayer 
	 * @param addScore winner score
	 * @param subScore loser score
	 */
	public void onGameEnd(int result, WNPlayer vsPlayer, int addScore, int subScore) {
		this.setBusy(false);// 战斗终了

		Out.debug("onGameEnd playerId:", this.player.getId(), ", result:", result, ", vsPlayerId:", vsPlayer);
		this.soloData.havePlayed = true; // 设置玩过状态

		// =================对手信息==============================
		int vsScore = 0;
		String vsName = "";
		String vsGuildName = "";
		int vsPro = 0;
		if (vsPlayer != null) {
			vsScore = vsPlayer.soloManager.getScore();
			vsName = vsPlayer.getName();
			vsGuildName = vsPlayer.guildManager.getGuildName();
			vsPro = vsPlayer.getPro();
		} else {// 机器人
			SoloMonsterCO monsterProp = GameData.SoloMonsters.get(this.player.getPro());
			vsName = monsterProp.monName;
			vsPro = monsterProp.proID;
		}
		// =====================================================

		int oldRank = getCurRank();
		int oldWinTimes = this.soloData.contWinTimes;
		int oldScore = this.soloData.score;

		if (result == 1) {
			this.addScore(addScore);// 赢了加积分

			this.soloData.contWinTimes++;
			this.soloData.extrWinTimes++;
			this.soloData.contLoseTimes = 0;
			// 成就
			this.player.achievementManager.onWinSolo(this.soloData.rankId);
		} else if (result == 2) {
			this.addScore(subScore);// 输了
			this.soloData.contLoseTimes++;
			if (this.soloData.contLoseTimes > this.soloData.maxContLoseTimes) {// 更新最高连败
				this.soloData.maxContLoseTimes = this.soloData.contLoseTimes;
			}
			this.soloData.contWinTimes = 0;
			this.soloData.extrWinTimes = 0;
		}
		// 积分真实变化
		int scoreChange = this.soloData.score - oldScore;

		int addPoint = winSoloPoint(result);// 计算获取的宗师币
		this.addSolopoint(addPoint, GOODS_CHANGE_TYPE.solo);
		this.soloData.soloPointToday += addPoint;

		// 连胜处理----------------------------------------
		if (this.soloData.contWinTimes > oldWinTimes) {
			this.newsHandler.onStraightWin(this.soloData.contWinTimes);
			if (this.soloData.contWinTimes > this.soloData.maxContWinTimes) {// 更新历史最高连胜
				this.soloData.maxContWinTimes = this.soloData.contWinTimes;
			}
		} else if (this.soloData.contWinTimes > oldWinTimes) {
			this.newsHandler.onStraightWinStopped(oldWinTimes, (vsPlayer != null ? vsPlayer.getName() : ""));
		}
		SoloRankCO prop = GameData.SoloRanks.get(this.soloData.rankId);
		if (prop != null) {
			if (this.soloData.extrWinTimes >= prop.extraRequire) {// 额外奖励
				this.soloData.extrWinTimes = 0;
				this.player.achievementManager.onWinSolo(this.soloData.rankId);
			}
		}

		boolean isAddBox = this.calcDailyChestOnBattled();// 计算每日宝箱奖励
		// this.update();

		BattleRecordPO battleRec = new BattleRecordPO();
		battleRec.result = result;
		battleRec.battleTime = new Date();
		battleRec.score = this.soloData.score;
		battleRec.scoreChange = scoreChange;
		battleRec.vsName = vsName;
		battleRec.vsGuildName = vsGuildName;
		battleRec.vsPro = vsPro;
		battleRec.vsScore = vsScore;
		this.soloData.recordBattle(battleRec);

		// 推送给客户端
		GameResult.Builder gb = GameResult.newBuilder();
		gb.setResult(result);
		gb.setNewScore(scoreChange);
		gb.setCurrScore(this.soloData.score);
		gb.setTokenChange(addPoint);

		int currRank = getCurRank();
		if (oldRank == 0) {
			gb.setRankChange(currRank);
		} else {
			gb.setRankChange(oldRank - currRank);
		}
		gb.setCurrentRank(currRank);
		int gameOverTime = GameData.GameMaps.get(GlobalConfig.Solo_MapID).timeCount;
		WNNotifyManager.getInstance().pushGameEnd(this.player, gb.build(), gameOverTime, isAddBox);
	}

	private void cancelFollow() {
		// 取消跟随
		Map<String, TeamMemberData> members = player.getTeamManager().getTeamMembers();
		if (members != null) {
			if (player.getTeamManager().isTeamLeader()) {
				for (TeamMemberData member : members.values()) {
					if (member.isFollow()) {
						TeamService.followLeader(member.getPlayer(), false);
					}
				}
			} else {
				TeamService.followLeader(player, false);
			}
		}
	}

	public void onEnterSoloArea() {
		cancelFollow();
		this.soloData.lastJoinedTime = new Date();

		this.startJoinTime = 0;

		this.player.taskManager.dealTaskEvent(TaskType.JOIN_SOLO, 1);

		// this.pushLeftSoloTimeToClient();
	}

	/**
	 * @return 匹配成功所耗费的时间
	 */
	public long getMatchedTime() {
		return System.currentTimeMillis() - this.startJoinTime;
	}

	private int hasReward() {
		if (this.soloData.dailyRewards.size() > 0) {
			return 1;
		}

		for (RankStatus r : this.soloData.rankRewards.values()) {
			if (r.getStatus() == 1) {
				return 1;
			}
		}

		return 0;
	}

	/**
	 * 更新奖励红点
	 */
	private void updateRedPoint() {
		List<SuperScriptType> list = new ArrayList<>();
		list.addAll(getSuperScript());
		this.player.updateSuperScriptList(list);
	}

	/**
	 * 获取红点信息
	 * 
	 * @return
	 */
	public List<SuperScriptType> getSuperScript() {
		List<SuperScriptType> list = new ArrayList<>();
		if (!this.player.functionOpenManager.isOpen(Const.FunctionType.SOLO.getValue())) {
			return list;
		}
		SuperScriptType.Builder data = SuperScriptType.newBuilder();
		data.setType(Const.SUPERSCRIPT_TYPE.SOLO.getValue());
		data.setNumber(SoloService.getInstance().isInOpenTime() ? 1 : 0);

		SuperScriptType.Builder data2 = SuperScriptType.newBuilder();
		data2.setType(Const.SUPERSCRIPT_TYPE.SOLO_REWARD.getValue());
		data2.setNumber(this.getRewardCount());

		list.add(data.build());
		list.add(data2.build());
		return list;
	}

	/**
	 * 玩家离线处理
	 */
	public void onPlayerOffline() {
		// this.quitMatching(false);
		SoloMatcher.getInstance().playerOffline(this.player.getId());
	}

	/**
	 * @return 返回所有可领取奖励的个数，以通知红点提醒
	 */
	private int getRewardCount() {
		int number = 0;
		if (this.soloData.dailyRewards.size() > 0) {
			for (RankStatus rank : soloData.dailyRewards) {
				if (rank.getStatus() == 1) {
					number += 1;
				}
			}
		}

		if (this.soloData.rankRewards != null) {
			Collection<RankStatus> ranks = this.soloData.rankRewards.values();
			for (RankStatus rank : ranks) {
				if (rank.getStatus() == 1) {
					number += 1;
				}
			}
		}

		return number;
	}

	/**
	 * @param date
	 * @return 返回当天0点距离date的毫秒数
	 */
	private int getTimeSec(Date date) {
		long zeroTime = DateUtil.getZeroToday().getTime();
		return (int) Math.floor(date.getTime() - zeroTime) / 1000;
	}

	public void pushLeftSoloTimeToClient() {
		LeftSoloTimePush.Builder data = LeftSoloTimePush.newBuilder();
		data.setS2CCode(Const.CODE.OK);

		List<OpenInfo> openList = SoloService.getInstance().getOpenInfoList();
		List<OpenTimeInfo> openTimeInfos = new ArrayList<>();
		for (OpenInfo oi : openList) {
			int openSec = getTimeSec(oi.openTime);
			int closeSec = getTimeSec(oi.closeTime);
			OpenTimeInfo.Builder bs = OpenTimeInfo.newBuilder();
			bs.setOpenTime(openSec);
			bs.setCloseTime(closeSec);

			openTimeInfos.add(bs.build());
		}

		data.addAllOpenList(openTimeInfos);
		player.receive("area.soloPush.leftSoloTimePush", data.build());
	}
}
