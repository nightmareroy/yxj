package com.wanniu.game.five2Five;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

import com.wanniu.core.game.JobFactory;
import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GPlayer;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.logfs.Out;
import com.wanniu.core.util.DateUtil;
import com.wanniu.game.GWorld;
import com.wanniu.game.area.Area;
import com.wanniu.game.area.AreaData;
import com.wanniu.game.area.AreaUtil;
import com.wanniu.game.common.Const;
import com.wanniu.game.common.Utils;
import com.wanniu.game.data.GameData;
import com.wanniu.game.data.GameMapCO;
import com.wanniu.game.data.ext.PersonalRankExt;
import com.wanniu.game.five2Five.dao.Five2FiveDao;
import com.wanniu.game.functionOpen.FunctionOpenUtil;
import com.wanniu.game.item.ItemUtil;
import com.wanniu.game.item.NormalItem;
import com.wanniu.game.leaderBoard.LeaderBoardDetail;
import com.wanniu.game.mail.MailUtil;
import com.wanniu.game.mail.SysMailConst;
import com.wanniu.game.mail.data.MailData.Attachment;
import com.wanniu.game.mail.data.MailSysData;
import com.wanniu.game.player.GlobalConfig;
import com.wanniu.game.player.PlayerUtil;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.poes.Five2FivePO;
import com.wanniu.game.poes.Five2FivePlayerBtlReportPO;
import com.wanniu.game.poes.Five2FiveSystemPO;
import com.wanniu.game.rank.RankType;
import com.wanniu.game.team.TeamData;
import com.wanniu.game.team.TeamData.TeamMemberData;
import com.wanniu.game.team.TeamManager;
import com.wanniu.game.team.TeamService;

import pomelo.five2five.Five2FiveHandler.Five2FiveLeaderCancelMatchPush;
import pomelo.five2five.Five2FiveHandler.Five2FiveMatchFailedPush;
import pomelo.five2five.Five2FiveHandler.Five2FiveMatchMemberInfo;
import pomelo.five2five.Five2FiveHandler.Five2FiveMatchMemberInfoPush;
import pomelo.five2five.Five2FiveHandler.Five2FiveMatchPoolChangePush;
import pomelo.five2five.Five2FiveHandler.Five2FiveMatchTime;
import pomelo.five2five.Five2FiveHandler.Five2FiveMemberChoicePush;
import pomelo.five2five.Five2FiveHandler.Five2FiveTeamChangePush;

/**
 * 5V5
 * 
 * @author wanghaitao
 *
 */
public class Five2FiveService {
	/**
	 * 赛季最短周期
	 */
	private final static long MIN_SEASON_MILLISEC = TimeUnit.DAYS.toMillis(3);

	/**
	 * 战斗结果
	 * 
	 * @author lxm
	 *
	 */
	public enum Five2FiveResult {
		DEFAULT, WIN, FAIL, TIE
	}

	private Five2FiveSystemPO five2FiveSystemPo;

	public static Five2FiveService getInstance() {
		if (instance == null) {
			synchronized (Five2FiveService.class) {
				if (instance == null) {
					instance = new Five2FiveService();
				}
			}
		}
		return instance;
	}

	private static Five2FiveService instance = null;

	private Five2FiveService() {
		init();
	}

	private void init() {
		// 5v5匹配线程
		JobFactory.addDelayJob(new Five2FiveMatchTeamThread(), 3000, TimeUnit.MILLISECONDS);

		five2FiveSystemPo = Five2FiveDao.getFive2FivePO(String.valueOf(GWorld.__SERVER_ID));
		if (five2FiveSystemPo == null) {
			five2FiveSystemPo = new Five2FiveSystemPO();
			five2FiveSystemPo.logicServerId = GWorld.__SERVER_ID;
			five2FiveSystemPo.id = UUID.randomUUID().toString();
			five2FiveSystemPo.seasonRefreshTime = calcSoloSeasonTime();
			updateFive2FiveSystem(five2FiveSystemPo);
		}

		// 发放奖励
		long initialDelay = five2FiveSystemPo.seasonRefreshTime.getTime() - System.currentTimeMillis();
		JobFactory.addFixedRateJob(new Runnable() {

			@Override
			public void run() {
				try {
					Out.info("开始发放5v5周奖励");
					List<LeaderBoardDetail> datas = RankType.PVP_5V5.getHandler().getRankDetail(five2FiveSystemPo.logicServerId, 0, -1);
					int rank = 1;
					for (LeaderBoardDetail detail : datas) {
						String playerId = detail.memberId;
						WNPlayer player = PlayerUtil.getOnlinePlayer(playerId);
						if (player != null) {
							Map<Integer, PersonalRankExt> personalRanks = GameData.PersonalRanks;
							PersonalRankExt maxExt = null;
							for (PersonalRankExt temp : personalRanks.values()) {
								if (maxExt == null || (temp.startRank > maxExt.startRank && temp.stopRank > maxExt.stopRank)) {
									maxExt = temp;
								}
							}
							PersonalRankExt rankExt = null;
							for (PersonalRankExt rankReward : personalRanks.values()) {
								int startRank = rankReward.startRank;
								int endRank = rankReward.stopRank;
								if (rank >= startRank && rank <= endRank) {
									rankExt = rankReward;
									break;
								}
							}
							if (rankExt == null) {
								rankExt = maxExt;
							}
							Map<String, Integer> rankRewards = rankExt.rankRewards;
							MailSysData mailData = new MailSysData(SysMailConst.FIVE_2_FIVE);
							Map<String, String> replace = new HashMap<>();
							replace.put("rank", String.valueOf(rank));
							mailData.replace = replace;
							mailData.attachments = new ArrayList<>();

							for (Entry<String, Integer> attach : rankRewards.entrySet()) {
								Attachment item = new Attachment();
								item.itemCode = attach.getKey();
								item.itemNum = attach.getValue();
								mailData.attachments.add(item);
							}
							MailUtil.getInstance().sendMailToOnePlayer(playerId, mailData, Const.GOODS_CHANGE_TYPE.five2five);
						} else {// 玩家不在线时,记录玩家上线时发送邮件
							Map<String, Integer> hasNoReciveRankRewardPlayer = five2FiveSystemPo.hasNoReciveRankRewardPlayer;
							if (hasNoReciveRankRewardPlayer == null) {
								hasNoReciveRankRewardPlayer = new HashMap<>();
							}
							hasNoReciveRankRewardPlayer.put(playerId, rank);
							five2FiveSystemPo.hasNoReciveRankRewardPlayer = hasNoReciveRankRewardPlayer;
						}
						rank++;
					}
					RankType.PVP_5V5.getHandler().delRank(GWorld.__SERVER_ID);
					Out.info("发放5v5周奖励结束");
					five2FiveSystemPo.seasonRefreshTime = calcSoloSeasonTime();

					updateFive2FiveSystem(five2FiveSystemPo);
				} catch (Exception e) {
					Out.error(e);
				}
			}
		}, initialDelay, TimeUnit.DAYS.toMillis(7));

		// 开始和结束时间刷新红点
		for (String times : GlobalConfig.Group_Daily_OpenTime.split(";")) {
			String[] time = times.split(",");
			Date begin = DateUtil.format(time[0]);
			Date end = DateUtil.format(time[1]);
			JobFactory.addScheduleJob(new Runnable() {

				@Override
				public void run() {
					for (GPlayer p : PlayerUtil.getAllOnlinePlayer()) {
						WNPlayer wp = (WNPlayer) p;
						wp.updateSuperScriptList(wp.five2FiveManager.getSuperScript());
					}
				}
			}, DateUtil.getTaskDelay(begin), TimeUnit.DAYS.toMillis(1));

			JobFactory.addScheduleJob(new Runnable() {

				@Override
				public void run() {
					for (GPlayer p : PlayerUtil.getAllOnlinePlayer()) {
						WNPlayer wp = (WNPlayer) p;
						wp.updateSuperScriptList(wp.five2FiveManager.getSuperScript());
						wp.five2FiveManager.cancelFive2FiveMatch(false);
					}
					Five2FiveMatchPool.clearMatchPool();
				}
			}, DateUtil.getTaskDelay(end), TimeUnit.DAYS.toMillis(1));
		}
	}

	/**
	 * 计算赛季结束时间
	 * 
	 * @return
	 */
	private Date calcSoloSeasonTime() {
		Date endDate = calcSeasonEndTime(7, 0);
		if (endDate.getTime() - System.currentTimeMillis() < MIN_SEASON_MILLISEC) {// 小于最短赛季天数就直接再加一周时间
			endDate = calcSeasonEndTime(7 + 7, 0);
		}
		return endDate;
	}

	/**
	 * 返回赛季结束日期的5点钟时间
	 * 
	 * @param term 赛季周期天数，期望几日后结束
	 * @param endWeekDay 期望几日后的周几结束 1~6对应周一~周六 任何大于6都当0处理
	 * @return
	 */
	private static Date calcSeasonEndTime(int term, int endWeekDay) {
		if (endWeekDay >= 7) {
			endWeekDay = 0;
		}
		Date endDate = DateUtil.getFiveTimeOfDay(DateUtil.getDateAfter(term));
		Calendar endC = Calendar.getInstance();
		endC.setTime(endDate);
		endC.set(Calendar.DAY_OF_WEEK, endWeekDay + 1);

		return endC.getTime();
	}

	/**
	 * 获取平均等待时间
	 * 
	 * @param single 单人或者队伍
	 * @return
	 */
	public int getAvgWaitTime(boolean single) {
		long totalWaitTime = 0;
		int totalMatchSuccessCount = 0;
		if (single) {
			totalWaitTime = five2FiveSystemPo.singleTotalMatchSuccessCostTime;
			totalMatchSuccessCount = five2FiveSystemPo.totalMatchSuccessSingle;
		} else {
			totalWaitTime = five2FiveSystemPo.teamTotalMatchSuccessCostTime;
			totalMatchSuccessCount = five2FiveSystemPo.totalMatchSuccessTeam;
		}
		if (totalMatchSuccessCount == 0) {
			return GlobalConfig.Group_AverageTime;
		}
		return (int) (totalWaitTime / totalMatchSuccessCount / 1000);
	}

	/**
	 * 单个玩家请求匹配
	 * 
	 * @param player
	 */
	public void singleApplyMatch(WNPlayer player) {
		Five2FiveMatchPool.singlePutInApplyPool(player);
	}

	/**
	 * 队长请求匹配
	 * 
	 * @param teamData
	 */
	public void teamApplyMatch(TeamData teamData) {
		Five2FiveMatchPool.teamPutInApplyPool(teamData.teamMembers, teamData.id);
	}

	/**
	 * 匹配开始时间(是否在匹配5v5)
	 * 
	 * @param playerId
	 * @return
	 */
	public Date applyMatchTime(String playerId) {
		return Five2FiveMatchPool.applyMatchTime(playerId);
	}

	/**
	 * 是否可以进入匹配
	 * 
	 * @param player
	 * @param gameMapCO
	 * @return
	 */
	public String isCanEnter(WNPlayer player) {
		if (!isInOpenTime()) {
			return LangService.getValue("FIVE_2_FIVE_NOT_IN_TIME");
		}
		if (player.getLevel() < FunctionOpenUtil.findFunctionOpenPropsByFuncName("5v5").openLv) {
			return LangService.getValue("FIVE_2_FIVE_NOT_ENOUGH_LEVEL");
		}
		TeamManager tm = player.teamManager;
		boolean isInTeam = tm.isInTeam();
		GameMapCO gameMapCO = GameData.GameMaps.get(GlobalConfig.Group_Map);

		Date applyMatchTime = applyMatchTime(player.getId());
		if (applyMatchTime != null) {
			return LangService.getValue("FIVE_2_FIVE_MATCHING");
		}
		// 是否在问道大会中
		if (player.soloManager.isBusy() || player.soloManager.isInMatching()) {
			return LangService.getValue("FIVE_2_FIVE_BUSY");
		}
		if (isInTeam) {// 有队伍则组队匹配
			boolean isTeamLeader = tm.isTeamLeader();
			if (!isTeamLeader) {
				return LangService.getValue("TEAM_NO_AUTHORITY");
			}
			Map<String, TeamMemberData> teamMembers = tm.getTeamMembers();
			for (Entry<String, TeamMemberData> teamMember : teamMembers.entrySet()) {
				String teamPlayerId = teamMember.getKey();
				if (!PlayerUtil.isOnline(teamPlayerId)) {
					return LangService.getValue("FIVE_2_FIVE_NOT_ONLINE");
				}
				String validateResult = processValidate(PlayerUtil.getOnlinePlayer(teamPlayerId), gameMapCO);
				if (!validateResult.equals("")) {
					return validateResult;
				}
			}
		}
		return "";
	}

	/**
	 * 验证队员是否能匹配
	 * 
	 * @param player
	 * @param gameMapCO
	 * @return
	 */
	public String processValidate(WNPlayer player, GameMapCO gameMapCO) {
		if (!funIsOpen(player)) {
			return LangService.getValue("FIVE_2_FIVE_MEM_NOT_OPEN");
		}

		// 是否在问道大会和竞技场中
		if (player.soloManager.isBusy() || player.soloManager.isInMatching() || player.arenaManager.isInArenaMap(player.getAreaId())) {
			return LangService.getValue("FIVE_2_FIVE_TEAM_MEM_BUSY");
		}

		Date applyMatchTime = applyMatchTime(player.getId());
		if (applyMatchTime != null) {
			return LangService.getValue("FIVE_2_FIVE_TEAM_MEM_BUSY");
		}

		String isCan = AreaUtil.canEnterArea(gameMapCO, player);
		if (isCan != null) {
			return isCan;
		}
		return "";
	}

	/**
	 * 当前时间是否开启
	 * 
	 * @return
	 */
	public boolean isInOpenTime() {
		if(GWorld.DEBUG) {
			return true;
		}
		Calendar c = Calendar.getInstance();
		int weekDay = c.get(Calendar.DAY_OF_WEEK) - 1;
		if (!GlobalConfig.Group_Weekly_OpenTime.contains(String.valueOf(weekDay))) {
			return false;
		}
		Date nowTime = c.getTime();
		for (String times : GlobalConfig.Group_Daily_OpenTime.split(";")) {
			String[] time = times.split(",");
			Date begin = DateUtil.format(time[0]);
			Date end = DateUtil.format(time[1]);
			if (nowTime.after(begin) && nowTime.before(end)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 玩家5v5系统是否已经开启
	 * 
	 * @param player
	 * @return
	 */
	public boolean funIsOpen(WNPlayer player) {
		return player.functionOpenManager.isOpen(Const.FunctionType.FIVE_2_FIVE.getValue());
	}

	/**
	 * 获取5v5积分
	 * 
	 * @param playerId
	 * @return
	 */
	public int getFive2FiveScore(String playerId) {
		WNPlayer player = PlayerUtil.getOnlinePlayer(playerId);
		Five2FivePO five2FivePO = null;
		if (player == null) {
			five2FivePO = Five2FiveDao.getPlayerFive2FivePO(playerId);
		} else {
			five2FivePO = player.allBlobData.five2FivePo;
			if (five2FivePO == null) {
				five2FivePO = Five2FiveDao.getPlayerFive2FivePO(playerId);
				player.allBlobData.five2FivePo = five2FivePO;
			}
		}
		int score = five2FivePO.score;
		return score;
	}

	/**
	 * 队伍变化5v5处理
	 * 
	 * @param leaderId
	 */
	public void processTeamChangeOnFive2Five(String leaderId) {
		Date applyTime = applyMatchTime(leaderId);
		// 队伍是否在5v5请求匹配队列中,如果在则通知队长、队友队伍变化
		if (applyTime != null) {
			Five2FiveTeamChangePush.Builder teamChangePush = Five2FiveTeamChangePush.newBuilder();
			teamChangePush.setS2CCode(PomeloRequest.OK);
			WNPlayer leaderPlayer = PlayerUtil.getOnlinePlayer(leaderId);
			if (leaderPlayer == null) {
				return;
			}
			leaderPlayer.receive("five2five.five2FivePush.five2FiveTeamChangePush", teamChangePush.build());
		}
	}

	/**
	 * 5v5匹配成功后推送消息
	 * 
	 * @param matchTeamVoA
	 * @param matchTeamVoB
	 */
	public void five2FiveAfterMatchSucess(Five2FiveMatchTeamVo matchTeamVoA, Five2FiveMatchTeamVo matchTeamVoB) {
		// ==============================A队伍============================
		List<Five2FiveTempTeamMember> tempTeamMemsA = matchTeamVoA.tempTeamMember;
		int memNumberA = tempTeamMemsA.size();
		List<Five2FiveMatchMemberInfo> matchMemInfosA = new ArrayList<>();
		for (int i = 0; i < memNumberA; i++) {
			Five2FiveMatchMemberInfo.Builder matchMemInfo = Five2FiveMatchMemberInfo.newBuilder();
			Five2FiveTempTeamMember tempTeamMem = tempTeamMemsA.get(i);
			matchMemInfo.setPlayerId(tempTeamMem.playerId);
			matchMemInfo.setPlayerPro(tempTeamMem.playerPro);
			matchMemInfo.setPlayerLvl(tempTeamMem.playerLvl);
			matchMemInfo.setPlayerName(tempTeamMem.playerName);
			matchMemInfo.setReadyStatus(0);
			matchMemInfosA.add(matchMemInfo.build());
		}

		// ===============================B队伍===============================
		List<Five2FiveTempTeamMember> tempTeamMemsB = matchTeamVoB.tempTeamMember;
		int memNumberB = tempTeamMemsB.size();
		List<Five2FiveMatchMemberInfo> matchMemInfosB = new ArrayList<>();
		for (int i = 0; i < memNumberB; i++) {
			Five2FiveMatchMemberInfo.Builder matchMemInfo = Five2FiveMatchMemberInfo.newBuilder();
			Five2FiveTempTeamMember tempTeamMem = tempTeamMemsB.get(i);
			matchMemInfo.setPlayerId(tempTeamMem.playerId);
			matchMemInfo.setPlayerPro(tempTeamMem.playerPro);
			matchMemInfo.setPlayerLvl(tempTeamMem.playerLvl);
			matchMemInfo.setPlayerName(tempTeamMem.playerName);
			matchMemInfo.setReadyStatus(0);
			matchMemInfosB.add(matchMemInfo.build());
		}

		// ===========================向两边队伍推送10个人======================
		Five2FiveMatchMemberInfoPush.Builder matchMemPushA = Five2FiveMatchMemberInfoPush.newBuilder();
		matchMemPushA.setS2CCode(PomeloRequest.OK);
		matchMemPushA.setTempTeamId(matchTeamVoA.tempTeamId);
		matchMemPushA.addAllMatchTeamInfoA(matchMemInfosA);
		matchMemPushA.addAllMatchTeamInfoB(matchMemInfosB);
		int waitResonseTimeSec = GlobalConfig.Group_ReadyTime;
		matchMemPushA.setWaitResponseTimeSec(waitResonseTimeSec);
		for (int i = 0; i < memNumberA; i++) {
			Five2FiveTempTeamMember tempTeamMem = tempTeamMemsA.get(i);
			String playerId = tempTeamMem.playerId;
			WNPlayer player = PlayerUtil.findPlayer(playerId);
			if (player == null) {
				continue;
			}
			player.receive("five2five.five2FivePush.five2FiveMatchMemberInfoPush", matchMemPushA.build());
		}

		Five2FiveMatchMemberInfoPush.Builder matchMemPushB = Five2FiveMatchMemberInfoPush.newBuilder();
		matchMemPushB.setS2CCode(PomeloRequest.OK);
		matchMemPushB.setTempTeamId(matchTeamVoB.tempTeamId);
		matchMemPushB.addAllMatchTeamInfoA(matchMemInfosA);
		matchMemPushB.addAllMatchTeamInfoB(matchMemInfosB);
		matchMemPushB.setWaitResponseTimeSec(waitResonseTimeSec);
		for (int i = 0; i < memNumberB; i++) {
			Five2FiveTempTeamMember tempTeamMem = tempTeamMemsB.get(i);
			String playerId = tempTeamMem.playerId;
			WNPlayer player = PlayerUtil.findPlayer(playerId);
			if (player == null) {
				continue;
			}
			player.receive("five2five.five2FivePush.five2FiveMatchMemberInfoPush", matchMemPushB.build());
		}

		// ======================统计平均等待时间===================================
		List<Five2FiveTeamApplyVo> teamMatchVosA = matchTeamVoA.teamMatchVos;
		if (teamMatchVosA != null) {
			long teamCostTime = five2FiveSystemPo.teamTotalMatchSuccessCostTime;
			for (int i = 0; i < teamMatchVosA.size(); i++) {
				Five2FiveTeamApplyVo teamMatchVoA = teamMatchVosA.get(i);
				long thisTeamCostTime = System.currentTimeMillis() - teamMatchVoA.joinTime.getTime();
				teamCostTime += thisTeamCostTime;
			}
			five2FiveSystemPo.teamTotalMatchSuccessCostTime = teamCostTime;
			five2FiveSystemPo.totalMatchSuccessTeam += 1;
		}

		List<Five2FiveSingleApplyVo> singleMatchVosA = matchTeamVoA.singleMatchVos;
		if (singleMatchVosA != null) {
			long singleCostTime = five2FiveSystemPo.singleTotalMatchSuccessCostTime;
			for (Five2FiveSingleApplyVo single : singleMatchVosA) {
				long thisPlayerCostTime = System.currentTimeMillis() - single.joinTime.getTime();
				singleCostTime += thisPlayerCostTime;
			}
			five2FiveSystemPo.singleTotalMatchSuccessCostTime = singleCostTime;
			five2FiveSystemPo.totalMatchSuccessSingle += singleMatchVosA.size();
		}

		List<Five2FiveTeamApplyVo> teamMatchVosB = matchTeamVoB.teamMatchVos;
		if (teamMatchVosB != null) {
			long teamCostTime = five2FiveSystemPo.teamTotalMatchSuccessCostTime;
			for (int i = 0; i < teamMatchVosB.size(); i++) {
				Five2FiveTeamApplyVo teamMatchVoB = teamMatchVosB.get(i);
				long thisTeamCostTime = System.currentTimeMillis() - teamMatchVoB.joinTime.getTime();
				teamCostTime += thisTeamCostTime;

			}
			five2FiveSystemPo.teamTotalMatchSuccessCostTime = teamCostTime;
			five2FiveSystemPo.totalMatchSuccessTeam += 1;
		}

		List<Five2FiveSingleApplyVo> singleMatchVosB = matchTeamVoB.singleMatchVos;
		if (singleMatchVosB != null) {
			long singleCostTime = five2FiveSystemPo.singleTotalMatchSuccessCostTime;
			for (Five2FiveSingleApplyVo single : singleMatchVosB) {
				long thisPlayerCostTime = System.currentTimeMillis() - single.joinTime.getTime();
				singleCostTime += thisPlayerCostTime;
			}
			five2FiveSystemPo.singleTotalMatchSuccessCostTime = singleCostTime;
			five2FiveSystemPo.totalMatchSuccessSingle += singleMatchVosB.size();
		}

		pushMatchPool();

		waitPlayerReady(matchTeamVoA, matchTeamVoB);
		updateFive2FiveSystem(five2FiveSystemPo);
	}

	/**
	 * 单人退出5v5匹配
	 * 
	 * @param playerId
	 */
	public boolean singleQuitFive2FiveMatch(String playerId, boolean isSelf) {
		if(!isInOpenTime()) {
			return false;
		}
		boolean re = Five2FiveMatchPool.singleRemoveApplyPool(playerId);
		if (re && !isSelf) {
			pushCancelMatch(playerId);
		}
		return re;
	}

	/**
	 * 队伍退出5v5匹配
	 * 
	 * @param teamId
	 */
	// public boolean teamQuitFive2FiveMatch(String teamId) {
	// return Five2FiveMatchPool.teamRemoveApplyPool(teamId);
	// }

	/**
	 * 根据临时队伍ID获取临时队伍
	 * 
	 * @param tempTeamId
	 * @return
	 */
	public Five2FiveMatchTeamVo getMatchingTeam(String tempTeamId) {
		return Five2FiveMatchPool.getMatchingTeam(tempTeamId);
	}

	/**
	 * 更新玩家战报信息
	 * 
	 * @param btlPo
	 */
	public void updatePlayerFive2FiveBtlReportPO(Five2FivePlayerBtlReportPO btlPo) {
		Five2FiveDao.updatePlayerFive2FiveBtlReportPO(btlPo);
	}

	/**
	 * 更新5v5系統信息
	 * 
	 * @param systemPo
	 */
	public void updateFive2FiveSystem(Five2FiveSystemPO systemPo) {
		Five2FiveDao.updateFive2FiveSystem(systemPo);
	}

	/**
	 * 删除玩家战报
	 * 
	 * @param btlPo
	 */
	public void delPlayerFive2FiveBtlReportPO(Five2FivePlayerBtlReportPO btlPo) {
		Five2FiveDao.delPlayerFive2FiveBtlReportPO(btlPo);
	}

	/**
	 * 分享战报
	 * 
	 * @param instanceId
	 * @param resultInfos
	 */
	public void shardMatchResult(String instanceId, List<Five2FivePlayerResultInfoVo> resultInfos) {
		Five2FiveDao.updateShardBtlReport(instanceId, resultInfos);
	}

	/**
	 * 获取战报
	 * 
	 * @param instanceId
	 * @return
	 */
	public List<Five2FivePlayerResultInfoVo> queryResultInfos(String instanceId) {
		return Five2FiveDao.getShardBtlReport(instanceId);
	}

	/**
	 * 玩家全部做过操作后处理
	 * 
	 * @param matchTeamVoA
	 * @param matchTeamVoB
	 */
	public synchronized void afterAllChoiced(Five2FiveMatchTeamVo matchTeamVoA, Five2FiveMatchTeamVo matchTeamVoB) {
		if(Five2FiveMatchPool.removeMatchedTeam(matchTeamVoA.tempTeamId)==null ||
				Five2FiveMatchPool.removeMatchedTeam(matchTeamVoB.tempTeamId)==null) {
			Out.warn("5v5 removeMatchedTeam null occured");
			return;
		}
		Out.info("five2five readyInScene:TeamA:", matchTeamVoA.tempTeamId, ",teamB:", matchTeamVoB.tempTeamId);
		if (!matchTeamVoA.isAllChoice.compareAndSet(false, true)) {
			Out.warn("five2fivelog:err1:", matchTeamVoA.tempTeamId);
			return;
		}

		matchTeamVoB.isAllChoice.set(true);

		CopyOnWriteArrayList<Five2FiveTempTeamMember> tempTeamMemsA = matchTeamVoA.tempTeamMember;
		CopyOnWriteArrayList<Five2FiveTempTeamMember> tempTeamMemsB = matchTeamVoB.tempTeamMember;

		Area area = null;
		Map<String, Object> userData = Utils.ofMap("tempTeamMemsA", tempTeamMemsA, "tempTeamMemsB", tempTeamMemsB);
		for (Five2FiveTempTeamMember tempTeamMem : tempTeamMemsA) {
			WNPlayer memPlayer = PlayerUtil.getOnlinePlayer(tempTeamMem.playerId);
			if (memPlayer == null) {
				Out.warn("player offlined in 5v5, playerId=" + tempTeamMem.playerId);
				continue;
			}
			if (area == null) {
				area = enterFive2FiveArea(memPlayer, userData, matchTeamVoA.tempTeamId);
			} else {
				enterFive2FiveArea(memPlayer, area, userData, matchTeamVoA.tempTeamId);
			}
		}
		for (Five2FiveTempTeamMember tempTeamMem : tempTeamMemsB) {
			WNPlayer memPlayer = PlayerUtil.getOnlinePlayer(tempTeamMem.playerId);
			if (memPlayer == null) {
				Out.warn("player offlined in 5v5, playerId=" + tempTeamMem.playerId);
				continue;
			}
			if (area == null) {
				area = enterFive2FiveArea(memPlayer, userData, matchTeamVoB.tempTeamId);
			} else {
				enterFive2FiveArea(memPlayer, area, userData, matchTeamVoB.tempTeamId);
			}
		}
	}

	/**
	 * 先到的玩家创建一个试炼场景
	 * 
	 * @param player
	 * @return 返回新场景的AreaData 包含instanceId
	 */
	private Area enterFive2FiveArea(WNPlayer player, Map<String, Object> userData, String templateId) {
		cancelFollow(player);
		Area area = AreaUtil.createAreaAndDispatch(player, Arrays.asList(player.getId()), player.getLogicServerId(), GlobalConfig.Group_Map, userData);
		if (!PlayerUtil.isOnline(player.getId())) {
			area.addPlayer(player);
			area.playerEnterRequest(player);
			player.getXmdsManager().playerReady(player.getId());
			area.onPlayerAutoBattle(player, true);
		}
		Out.info("five2fivelog:in scene:playerId:", player.getId(), ",templateId=", templateId, ",sceneid:", area.areaId, ",sceneInstanceId:", area.instanceId);
		return area;
	}

	/**
	 * 进入指定创建成功的试炼场景
	 * 
	 * @param player
	 * @param areaData
	 */
	private void enterFive2FiveArea(WNPlayer player, Area area, Map<String, Object> userData, String templateId) {
		cancelFollow(player);
		if (area != null && !area.isClose()) {
			if (!PlayerUtil.isOnline(player.getId())) {
				area.addPlayer(player);
				area.playerEnterRequest(player);
				player.getXmdsManager().playerReady(player.getId());
				area.onPlayerAutoBattle(player, true);
			} else {
				AreaData areaData = new AreaData(area.areaId, area.instanceId);
				AreaUtil.dispatchByInstanceId(player, areaData);
			}
		}
		Out.info("five2fivelog:in scene:playerId:", player.getId(), ",templateId=", templateId, ",sceneid:", area.areaId, ",sceneInstanceId:", area.instanceId);
	}

	/**
	 * 取消跟随
	 * 
	 * @param player
	 */
	private void cancelFollow(WNPlayer player) {
		if (player.getTeamManager().isTeamLeader()) {// 队长进入队员全部取消
			for (String id : player.getTeamManager().getTeamMembers().keySet()) {
				WNPlayer p = PlayerUtil.getOnlinePlayer(id);
				if (p == null) {
					continue;
				}
				TeamService.followLeader(p, false);
			}
		} else {
			TeamService.followLeader(player, false);
		}
	}

	/**
	 * 获取排名第一信息
	 * 
	 * @return
	 */
	public String getFirstRankInfo() {
		return RankType.PVP_5V5.getHandler().getFirstRankMemberId(five2FiveSystemPo.logicServerId);
	}

	/**
	 * 获取玩家的排名
	 * 
	 * @param playerId
	 * @return
	 */
	public int getPlayerRank(String playerId) {
		return (int) RankType.PVP_5V5.getHandler().getRank(five2FiveSystemPo.logicServerId, playerId);
	}

	/**
	 * 根据奖励生产物品
	 * 
	 * @param rewards
	 * @return
	 */
	public List<NormalItem> createItem(Map<String, Integer> rewards) {
		List<NormalItem> returnItems = new ArrayList<>();
		for (String itemCode : rewards.keySet()) {
			List<NormalItem> items = ItemUtil.createItemsByItemCode(itemCode, rewards.get(itemCode));
			returnItems.addAll(items);
		}
		return returnItems;
	}

	/**
	 * 根据队伍ID获取队伍是否已经匹配成功
	 * 
	 * @param teamId
	 * @return
	 */
	@Deprecated
	public boolean teamIsMatchedSuccess(String teamId) {
		return Five2FiveMatchPool.teamIsMakeWithOthers(teamId);
	}

	/**
	 * 创建玩家5v5信息
	 * 
	 * @param playerId
	 * @return
	 */
	public Five2FivePO createFive2FivePO(String playerId) {
		Five2FivePO five2FivePO = new Five2FivePO();
		five2FivePO.createTime = new Date();
		five2FivePO.updateTime = five2FiveSystemPo.seasonRefreshTime;
		return five2FivePO;
	}

	/**
	 * 获取5v5系统信息
	 * 
	 * @return
	 */
	public Five2FiveSystemPO getFive2FiveSystemPO() {
		return five2FiveSystemPo;
	}

	/**
	 * 获取当前匹配池中玩家数量
	 * 
	 * @return
	 */
	public final static int getApplyPlayerCount() {
		return Five2FiveMatchPool.getApplyPlayer().size();
	}

	/**
	 * 等待玩家准备超过等待时间默认玩家同意
	 * 
	 * @param matchTeamVoA
	 * @param matchTeamVoB
	 */
	private void waitPlayerReady(Five2FiveMatchTeamVo matchTeamVoA, Five2FiveMatchTeamVo matchTeamVoB) {
		JobFactory.addDelayJob(new Runnable() {
			@Override
			public void run() {
				afterAllChoiced(matchTeamVoA, matchTeamVoB);
			}
		}, GlobalConfig.Group_ReadyTime + 1, TimeUnit.SECONDS);
	}

	/**
	 * 推送匹配失败
	 * 
	 * @param playerId
	 * @param single
	 * @param isReMatch
	 */
	private void pushMatchFailed(String playerId, boolean single, boolean isReMatch) {
		Five2FiveMatchFailedPush.Builder matchFailedPush = Five2FiveMatchFailedPush.newBuilder();
		matchFailedPush.setS2CCode(PomeloRequest.OK);
		if (isReMatch) {
			Five2FiveMatchTime.Builder matchTime = Five2FiveMatchTime.newBuilder();
			matchTime.setAvgWaitTime(getAvgWaitTime(single));
			matchTime.setMatchTime((int) (System.currentTimeMillis() / 1000));
			matchFailedPush.setFive2FiveMatchTime(matchTime);
		}
		WNPlayer player = PlayerUtil.getOnlinePlayer(playerId);
		if (player != null) {
			player.receive("five2five.five2FivePush.five2FiveMatchFailedPush", matchFailedPush.build());
		}
	}

	/**
	 * 匹配失败处理
	 * 
	 * @param matchTeamVo
	 * @param tempTeamMemsA
	 */
	@Deprecated
	void afterMatchedFailedProcess(Five2FiveMatchTeamVo matchTeamVo) {
		CopyOnWriteArrayList<Five2FiveTempTeamMember> tempTeamMems = matchTeamVo.tempTeamMember;
		List<Five2FiveTeamApplyVo> teamMatchVos = matchTeamVo.teamMatchVos;
		List<Five2FiveSingleApplyVo> singleMatchVos = matchTeamVo.singleMatchVos;
		if (singleMatchVos != null) {
			for (int i = 0; i < singleMatchVos.size(); i++) {
				Five2FiveSingleApplyVo single = singleMatchVos.get(i);
				WNPlayer player = single.player;
				if (player != null) {
					for (int j = 0; j < tempTeamMems.size(); j++) {
						Five2FiveTempTeamMember tempTeamMem = tempTeamMems.get(j);
						if (tempTeamMem.isReadyOrCancel.get() == Const.Five2Five.five2five_choice_ready.value && player.getId().equals(tempTeamMem.playerId)) {// 单个玩家重新加入报名队列
							Five2FiveMatchPool.singlePutInApplyPool(player);
							pushMatchFailed(tempTeamMem.playerId, true, true);
						} else if ((tempTeamMem.isReadyOrCancel.get() == Const.Five2Five.five2five_choice_giveup.value || tempTeamMem.isReadyOrCancel.get() == 0) && player.getId().equals(tempTeamMem.playerId)) {
							pushMatchFailed(tempTeamMem.playerId, true, false);
						}
					}
				}
			}
		}

		if (teamMatchVos != null) {
			for (int i = 0; i < teamMatchVos.size(); i++) {
				Five2FiveTeamApplyVo teamMatchVo = teamMatchVos.get(i);
				boolean isTeamAllReady = true;
				for (String teamMemPlayerId : teamMatchVo.teamMembers.keySet()) {
					WNPlayer player = PlayerUtil.getOnlinePlayer(teamMemPlayerId);
					if (player != null) {
						for (Five2FiveTempTeamMember tempTeamMem : tempTeamMems) {
							if (tempTeamMem.isReadyOrCancel.get() != Const.Five2Five.five2five_choice_ready.value && player.getId().equals(tempTeamMem.playerId)) {
								isTeamAllReady = false;
							}
						}
					}
				}
				if (isTeamAllReady) {// 队伍放入报名队列
					for (String teamMemPlayerId : teamMatchVo.teamMembers.keySet()) {
						pushMatchFailed(teamMemPlayerId, false, true);
					}
					Five2FiveMatchPool.teamPutInApplyPool(teamMatchVo.teamMembers, teamMatchVo.teamId);
				} else {
					for (String teamMemPlayerId : teamMatchVo.teamMembers.keySet()) {
						pushMatchFailed(teamMemPlayerId, false, false);
					}
				}
			}
		}
	}

	/**
	 * 向玩家推送取消匹配消息
	 * 
	 * @param playerId
	 */
	public void pushCancelMatch(String playerId) {
		WNPlayer memPlayer = PlayerUtil.getOnlinePlayer(playerId);
		if (memPlayer != null) {
			Five2FiveLeaderCancelMatchPush.Builder leaderCancelMatch = Five2FiveLeaderCancelMatchPush.newBuilder();
			leaderCancelMatch.setS2CCode(PomeloRequest.OK);
			memPlayer.receive("five2five.five2FivePush.five2FiveLeaderCancelMatchPush", leaderCancelMatch.build());
		}
	}

	/**
	 * 玩家准备就绪推送
	 * 
	 * @param readyId
	 * @param pushPlayer
	 */
	public void pushReady(String readyId, String pushPlayer) {
		Five2FiveMemberChoicePush.Builder memChoice = Five2FiveMemberChoicePush.newBuilder();
		memChoice.setS2CCode(PomeloRequest.OK);
		memChoice.setAgreeOrReady(Const.Five2Five.five2five_choice_type_ready.value);
		memChoice.setPlayerId(readyId);
		memChoice.setChoice(Const.Five2Five.five2five_choice_ready.value);
		WNPlayer player = PlayerUtil.getOnlinePlayer(pushPlayer);
		if (player != null) {
			player.receive("five2five.five2FivePush.five2FiveMemberChoicePush", memChoice.build());
		}
	}

	/**
	 * 向队列中玩家推送队列变化
	 */
	public void pushMatchPool() {
		Five2FiveMatchPoolChangePush.Builder poolChange = Five2FiveMatchPoolChangePush.newBuilder();
		poolChange.setS2CCode(PomeloRequest.OK);
		List<String> poolPlayers = Five2FiveMatchPool.getApplyPlayer();
		poolChange.setPreNumber(poolPlayers.size());
		for (int i = 0; i < poolPlayers.size(); i++) {
			String tempPlayerId = poolPlayers.get(i);
			WNPlayer tempPlayer = PlayerUtil.getOnlinePlayer(tempPlayerId);
			if (tempPlayer != null) {
				tempPlayer.receive("five2five.five2FivePush.five2FiveMatchPoolChangePush", poolChange.build());
			}
		}
	}

}
