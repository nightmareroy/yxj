package com.wanniu.game.five2Five;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CopyOnWriteArrayList;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.logfs.Out;
import com.wanniu.core.util.DateUtil;
import com.wanniu.game.GWorld;
import com.wanniu.game.area.AreaUtil;
import com.wanniu.game.common.Const;
import com.wanniu.game.common.Const.ManagerType;
import com.wanniu.game.common.Const.PlayerEventType;
import com.wanniu.game.common.ModuleManager;
import com.wanniu.game.data.GameData;
import com.wanniu.game.data.ext.DayRewardExt;
import com.wanniu.game.data.ext.PersonalRankExt;
import com.wanniu.game.five2Five.Five2FiveService.Five2FiveResult;
import com.wanniu.game.five2Five.dao.Five2FiveDao;
import com.wanniu.game.item.NormalItem;
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
import com.wanniu.game.poes.PlayerPO;
import com.wanniu.game.rank.RankType;
import com.wanniu.game.team.TeamData;
import com.wanniu.game.team.TeamData.TeamMemberData;
import com.wanniu.game.team.TeamManager;

import pomelo.area.PlayerHandler.SuperScriptType;
import pomelo.five2five.Five2FiveHandler.Five2FiveApplyMatchPush;
import pomelo.five2five.Five2FiveHandler.Five2FiveApplyMatchResultPush;
import pomelo.five2five.Five2FiveHandler.Five2FiveBtlReport;
import pomelo.five2five.Five2FiveHandler.Five2FiveGameResult;
import pomelo.five2five.Five2FiveHandler.Five2FiveLookBtlReportResponse;
import pomelo.five2five.Five2FiveHandler.Five2FiveLookMatchResultResponse;
import pomelo.five2five.Five2FiveHandler.Five2FiveMatchMemberInfo;
import pomelo.five2five.Five2FiveHandler.Five2FiveMatchResponse;
import pomelo.five2five.Five2FiveHandler.Five2FiveMatchTime;
import pomelo.five2five.Five2FiveHandler.Five2FiveMemberChoicePush;
import pomelo.five2five.Five2FiveHandler.Five2FiveOnGameEndPush;
import pomelo.five2five.Five2FiveHandler.Five2FivePlayerResultInfo;
import pomelo.five2five.Five2FiveHandler.Five2FiveRankInfo;
import pomelo.five2five.Five2FiveHandler.Five2FiveResponse;

/**
 * @author wanghaitao
 *
 */
public class Five2FiveManager extends ModuleManager {
	private WNPlayer player;

	private int isAgreeOrRefuse;// 1拒绝2同意

	public Five2FiveManager(WNPlayer player) {
		this.player = player;
	}

	/**
	 * 请求5v5
	 * 
	 * @param playerId
	 * @param res
	 */
	public void applyFive2Five(String playerId, Five2FiveResponse.Builder res) {
		Five2FivePO five2FivePO = player.allBlobData.five2FivePo;
		if (five2FivePO == null) {
			five2FivePO = Five2FiveDao.getPlayerFive2FivePO(player.getId());
			if (five2FivePO == null) {
				res.setS2CCode(PomeloRequest.FAIL);
				res.setS2CMsg(LangService.getValue("SOMETHING_ERR"));
				return;
			}
			player.allBlobData.five2FivePo = five2FivePO;
		}
		refreshFive2FiveInfo(five2FivePO);
		res.setS2CCode(PomeloRequest.OK);
		Five2FiveRankInfo.Builder firstRankInfo = Five2FiveRankInfo.newBuilder();
		String firstPlayerId = Five2FiveService.getInstance().getFirstRankInfo();
		if (!firstPlayerId.equals("")) {
			firstRankInfo.setPlayerId(firstPlayerId);
			WNPlayer firstPlayer = PlayerUtil.getOnlinePlayer(firstPlayerId);
			PlayerPO playerPo = null;
			if (firstPlayer == null) {
				playerPo = PlayerUtil.getPlayerBaseData(firstPlayerId);
			} else {
				playerPo = firstPlayer.player;
			}
			firstRankInfo.setPlayerName(playerPo.name);
			firstRankInfo.setPlayerLvl(playerPo.level);
			firstRankInfo.setPro(playerPo.pro);
			firstRankInfo.setPlayerUpLvl(playerPo.upLevel);
			firstRankInfo.setPlayerFightPower(playerPo.fightPower);
			int firstScore = Five2FiveService.getInstance().getFive2FiveScore(firstPlayerId);
			firstRankInfo.setScore(firstScore);
			res.setFirstRankInfo(firstRankInfo);
		}

		res.setScore(five2FivePO.score);
		long selfRank = Five2FiveService.getInstance().getPlayerRank(playerId);
		res.setRank((int) selfRank);
		res.setWin(five2FivePO.winCount);
		res.setTie(five2FivePO.tieCount);
		res.setFail(five2FivePO.failCount);
		res.setMvp(five2FivePO.mvpCount);
		res.setTotalCanReciveCount(five2FivePO.canReciveRewardCount);
		res.setHasRecivedCount(five2FivePO.hasReciveRewardCount);

		TeamManager tm = player.teamManager;
		boolean isInTeam = tm.isInTeam();
		int avgWaitTime = 0;
		if (isInTeam) {
			avgWaitTime = Five2FiveService.getInstance().getAvgWaitTime(false);
		} else {// 单人
			avgWaitTime = Five2FiveService.getInstance().getAvgWaitTime(true);
		}

		Five2FiveMatchTime.Builder matchTime = Five2FiveMatchTime.newBuilder();
		matchTime.setAvgWaitTime(avgWaitTime);
		Date applyMatchTime = Five2FiveService.getInstance().applyMatchTime(playerId);
		if (applyMatchTime != null) {
			matchTime.setMatchTime((int) Math.floor(applyMatchTime.getTime() / 1000));
		} else {
			matchTime.setMatchTime(0);
		}
		res.setMatchPeople(Five2FiveMatchPool.getApplyPlayer().size());
		res.setFive2FiveMatchTime(matchTime);
		res.setSeasonEndTime(String.valueOf(Five2FiveService.getInstance().getFive2FiveSystemPO().seasonRefreshTime.getTime()));
	}

	/**
	 * 取消5v5匹配
	 */
	public void cancelFive2FiveMatch(boolean isSelf) {
		String playerId = player.getId();
		if (applyMatchTime(playerId) == null) {
			return;
		}

		Five2FiveService.getInstance().singleQuitFive2FiveMatch(playerId, isSelf);

		Five2FiveService.getInstance().pushMatchPool();
	}

	/**
	 * 请求5v5匹配
	 * 
	 * @param res
	 * @param matchOrReMatch
	 * @return
	 */
	public void applyFive2FiveMatch(Five2FiveMatchResponse.Builder res, int matchOrReMatch) {
		TeamManager tm = player.teamManager;
		if (matchOrReMatch == 2) {
			TeamData teamData = tm.getTeam();
			Map<String, TeamMemberData> teamMembers = teamData.teamMembers;
			for (TeamMemberData memData : teamMembers.values()) {
				String memPlayerId = memData.id;
				Five2FiveService.getInstance().singleQuitFive2FiveMatch(memPlayerId, true);
			}
		}

		String validateResult = Five2FiveService.getInstance().isCanEnter(player);
		if (!validateResult.equals("")) {
			res.setS2CCode(PomeloRequest.FAIL);
			res.setS2CMsg(validateResult);
			return;
		}

		res.setS2CCode(PomeloRequest.OK);
		boolean isInTeam = tm.isInTeam();

		Date now = new Date();
		if (isInTeam) {
			TeamData teamData = tm.getTeam();
			Map<String, TeamMemberData> teamMembers = teamData.teamMembers;
			List<Five2FiveMatchMemberInfo> five2FiveMatchMemberInfos = new ArrayList<>();
			for (TeamMemberData memData : teamMembers.values()) {
				String memPlayerId = memData.id;
				Five2FiveService.getInstance().singleQuitFive2FiveMatch(memPlayerId, true);

				Five2FiveMatchMemberInfo.Builder memInfo = Five2FiveMatchMemberInfo.newBuilder();
				WNPlayer player = memData.getPlayer();
				if (player == null) {
					continue;
				}
				player.five2FiveManager.isAgreeOrRefuse = 0;
				memInfo.setPlayerId(player.getId());
				memInfo.setPlayerPro(player.getPro());
				memInfo.setPlayerLvl(player.getLevel());
				memInfo.setPlayerName(player.getName());
				int readyStatus = 0;
				if (memPlayerId.equals(teamData.leaderId)) {
					readyStatus = Const.Five2Five.five2five_choice_ready.value;
				}
				memInfo.setReadyStatus(readyStatus);
				five2FiveMatchMemberInfos.add(memInfo.build());
			}

			if (teamMembers.size() > 1) {
				for (TeamMemberData memData : teamMembers.values()) {
					WNPlayer player = memData.getPlayer();
					if (player == null) {
						continue;
					}
					Five2FiveApplyMatchPush.Builder applyMatch = Five2FiveApplyMatchPush.newBuilder();
					applyMatch.addAllFive2FiveMatchMemberInfo(five2FiveMatchMemberInfos);
					applyMatch.setWaitResponseTimeSec(GlobalConfig.Group_ReadyTime);
					player.receive("five2five.five2FivePush.five2FiveApplyMatchPush", applyMatch.build());
				}
			} else {// 队伍只有一个人时直加入匹配队列
				Five2FiveService.getInstance().teamApplyMatch(teamData);// 加入匹配队列

				int avgWaitTime = Five2FiveService.getInstance().getAvgWaitTime(false);
				Five2FiveMatchTime.Builder matchTime = Five2FiveMatchTime.newBuilder();
				matchTime.setAvgWaitTime(avgWaitTime);
				matchTime.setMatchTime((int) (now.getTime() / 1000));
				res.setFive2FiveMatchTime(matchTime);
			}
			// 队长自动准备
			player.five2FiveManager.isAgreeOrRefuse = Const.Five2Five.five2five_choice_ready.value;
		} else {// 单人
			int avgWaitTime = Five2FiveService.getInstance().getAvgWaitTime(true);
			Five2FiveMatchTime.Builder matchTime = Five2FiveMatchTime.newBuilder();
			matchTime.setAvgWaitTime(avgWaitTime);
			matchTime.setMatchTime((int) (now.getTime() / 1000));
			res.setFive2FiveMatchTime(matchTime);
			Five2FiveService.getInstance().singleApplyMatch(player);
			isAgreeOrRefuse = 0;
		}
		// 向队列中玩家推送队列变化
		Five2FiveService.getInstance().pushMatchPool();
	}

	/**
	 * 
	 * 同意5V5匹配
	 * 
	 * @param player
	 * @return
	 */
	public String agreeMatch(WNPlayer player) {
		TeamManager tm = player.teamManager;
		if (tm == null) {
			return LangService.getValue("SOMETHING_ERR");
		}

		TeamData teamData = tm.getTeam();
		if (teamData == null) {
			return LangService.getValue("SOMETHING_ERR");
		}

		player.five2FiveManager.isAgreeOrRefuse = Const.Five2Five.five2five_choice_ready.value;
		Five2FiveMemberChoicePush.Builder choice = Five2FiveMemberChoicePush.newBuilder();
		choice.setS2CCode(PomeloRequest.OK);
		choice.setAgreeOrReady(Const.Five2Five.five2five_choice_type_agree.value);
		choice.setPlayerId(player.getId());
		choice.setChoice(Const.Five2Five.five2five_choice_ready.value);

		int agreeNumber = 0;
		int refuseNumber = 0;
		Map<String, TeamMemberData> teamMembers = teamData.teamMembers;
		for (TeamMemberData memData : teamMembers.values()) {
			WNPlayer playerMem = memData.getPlayer();
			if (playerMem == null) {
				continue;
			}

			int teamMemAgreeOrRefuse = playerMem.five2FiveManager.isAgreeOrRefuse;
			if (teamMemAgreeOrRefuse == Const.Five2Five.five2five_choice_giveup.value) {
				refuseNumber++;
			}
			if (teamMemAgreeOrRefuse == Const.Five2Five.five2five_choice_ready.value) {
				agreeNumber++;
			}
			playerMem.receive("five2five.five2FivePush.five2FiveMemberChoicePush", choice.build());
		}

		Five2FiveApplyMatchResultPush.Builder applyMatchResultPush = Five2FiveApplyMatchResultPush.newBuilder();
		boolean isPush = false;
		if (refuseNumber != 0 && agreeNumber + refuseNumber == teamMembers.size()) {
			applyMatchResultPush.setS2CCode(PomeloRequest.FAIL);
			isPush = true;
		} else if (agreeNumber == teamMembers.size()) {
			Five2FiveService.getInstance().teamApplyMatch(teamData);// 加入匹配队列

			applyMatchResultPush.setS2CCode(PomeloRequest.OK);
			// 推送匹配结果
			Five2FiveMatchTime.Builder matchTime = Five2FiveMatchTime.newBuilder();
			int avgWaitTime = Five2FiveService.getInstance().getAvgWaitTime(false);
			matchTime.setAvgWaitTime(avgWaitTime);
			matchTime.setMatchTime((int) (System.currentTimeMillis() / 1000));
			applyMatchResultPush.setFive2FiveMatchTime(matchTime);
			isPush = true;
		}
		if (isPush) {
			for (TeamMemberData teamMemberData : teamMembers.values()) {
				WNPlayer teamPlayer = teamMemberData.getPlayer();
				if (teamPlayer == null) {
					continue;
				}
				teamPlayer.receive("five2five.five2FivePush.five2FiveApplyMatchResultPush", applyMatchResultPush.build());
			}

			// 向队列中玩家推送队列变化
			Five2FiveService.getInstance().pushMatchPool();
		}
		return "";
	}

	/**
	 * 
	 * 拒绝5V5匹配
	 * 
	 * @param player
	 * @return
	 */
	public String refuseMatch(WNPlayer player) {
		TeamManager tm = player.teamManager;
		if (tm == null) {
			return LangService.getValue("SOMETHING_ERR");
		}

		TeamData teamData = tm.getTeam();
		if (teamData == null) {
			return LangService.getValue("SOMETHING_ERR");
		}

		player.five2FiveManager.isAgreeOrRefuse = Const.Five2Five.five2five_choice_giveup.value;
		Five2FiveMemberChoicePush.Builder choice = Five2FiveMemberChoicePush.newBuilder();
		choice.setS2CCode(PomeloRequest.OK);
		choice.setAgreeOrReady(Const.Five2Five.five2five_choice_type_agree.value);
		choice.setPlayerId(player.getId());
		choice.setChoice(Const.Five2Five.five2five_choice_giveup.value);

		Map<String, TeamMemberData> teamMembers = teamData.teamMembers;
		for (TeamMemberData memData : teamMembers.values()) {
			WNPlayer playerMem = memData.getPlayer();
			if (playerMem == null) {
				continue;
			}
			playerMem.receive("five2five.five2FivePush.five2FiveMemberChoicePush", choice.build());
		}
		return "";
	}

	/**
	 * 准备就绪
	 * 
	 * @param tempTeamId
	 * @return
	 */
	public String matchReady(String tempTeamId) {
		Five2FiveMatchTeamVo tempTeamVo = Five2FiveService.getInstance().getMatchingTeam(tempTeamId);
		if (tempTeamVo == null) {
			return LangService.getValue("SOMETHING_ERR");
		}

		CopyOnWriteArrayList<Five2FiveTempTeamMember> tempTeamMembers = tempTeamVo.tempTeamMember;
		int totalReadyNumber = 0;
		for (Five2FiveTempTeamMember tempTeamMember : tempTeamMembers) {
			String playerId = tempTeamMember.playerId;
			WNPlayer tempTeamPlayer = PlayerUtil.findPlayer(playerId);
			if (tempTeamPlayer == null) {
				continue;
			}
			if (playerId.equals(player.getId())) {
				tempTeamMember.isReadyOrCancel.set(Const.Five2Five.five2five_choice_ready.value);
			}
			if (tempTeamMember.isReadyOrCancel.get() == Const.Five2Five.five2five_choice_ready.value) {
				totalReadyNumber += 1;
			}

			Five2FiveService.getInstance().pushReady(player.getId(), playerId);
		}

		CopyOnWriteArrayList<Five2FiveTempTeamMember> oppoTempTeamMembers = Five2FiveService.getInstance().getMatchingTeam(tempTeamVo.oppoTempTeamId).tempTeamMember;
		for (Five2FiveTempTeamMember tempTeamMember : oppoTempTeamMembers) {
			if (tempTeamMember.isReadyOrCancel.get() == Const.Five2Five.five2five_choice_ready.value) {
				totalReadyNumber += 1;
			}
			// 推送准备给对方队伍
			Five2FiveService.getInstance().pushReady(player.getId(), tempTeamMember.playerId);
		}
		if (totalReadyNumber == tempTeamMembers.size() + oppoTempTeamMembers.size()) {// 全部准备好则直接进入场景
			if (!tempTeamVo.isAllChoice.get()) {
				Five2FiveMatchTeamVo tempTeamVoB = Five2FiveService.getInstance().getMatchingTeam(tempTeamVo.oppoTempTeamId);
				Five2FiveService.getInstance().afterAllChoiced(tempTeamVo, tempTeamVoB);
				tempTeamVo.isAllChoice.set(true);
			} else {
				return "";
			}
		}
		return "";
	}

	/**
	 * 领取奖励响应
	 * 
	 * @return
	 */
	public String reciveReward() {
		Five2FivePO five2FivePo = player.allBlobData.five2FivePo;
		if (five2FivePo == null) {
			return LangService.getValue("FIVE_2_FIVE_NO_REWERD_CAN_RECIVE");
		}
		int canReciveRewardCount = canReciveRewardCount();
		if (canReciveRewardCount == 0) {
			return LangService.getValue("FIVE_2_FIVE_NO_REWERD_CAN_RECIVE");
		}

		DayRewardExt dayReward = GameData.DayRewards.get(five2FivePo.hasReciveRewardCount + 1);
		if (dayReward == null) {
			return LangService.getValue("FIVE_2_FIVE_NO_REWERD_CAN_RECIVE");
		}

		List<NormalItem> rankReward = Five2FiveService.getInstance().createItem(dayReward.dayRewards);

		if (!this.player.getWnBag().testAddEntityItems(rankReward, true)) {
			return LangService.getValue("BAG_NOT_ENOUGH_POS");
		}

		this.player.getWnBag().addEntityItems(rankReward, Const.GOODS_CHANGE_TYPE.five2five);

		// five2FivePo.canReciveRewardCount = five2FivePo.canReciveRewardCount -
		// 1 < 0 ? 0
		// : five2FivePo.canReciveRewardCount - 1;

		five2FivePo.lastReciveRewardTime = new Date();
		five2FivePo.hasReciveRewardCount++;

		this.updateSuperScript();
		return "";
	}

	/**
	 * 分享战报
	 * 
	 * @param instanceId
	 * @return
	 */
	public String shardMatchResult(String instanceId) {
		List<Five2FivePlayerBtlReportPO> five2FiveBtlReportPO = player.allBlobData.five2FiveBtlReportPO;
		if (five2FiveBtlReportPO == null) {
			return LangService.getValue("FIVE_2_FIVE_NO_REPORT");
		}
		int resultA = Five2FiveResult.TIE.ordinal();// A队的胜负结果
		List<Five2FivePlayerResultInfoVo> resultInfos = new ArrayList<>();
		for (int i = 0; i < five2FiveBtlReportPO.size(); i++) {
			Five2FivePlayerBtlReportPO reportPo = five2FiveBtlReportPO.get(i);
			if (reportPo != null) {
				if (instanceId.equals(reportPo.id)) {
					if (reportPo.resultInfoA != null && reportPo.resultInfoB != null) {
						for (Five2FivePlayerResultInfoVo everyResultInfo : reportPo.resultInfoA.values()) {
							resultInfos.add(everyResultInfo);
							// 在A队里面胜负结果和自己一样
							if (everyResultInfo.playerId.equals(reportPo.playerId)) {
								resultA = reportPo.status;
							}
						}
						for (Five2FivePlayerResultInfoVo everyResultInfo : reportPo.resultInfoB.values()) {
							resultInfos.add(everyResultInfo);
							// 在B队里面取反
							if (everyResultInfo.playerId.equals(reportPo.playerId)) {
								if (reportPo.status == Five2FiveResult.WIN.ordinal()) {
									resultA = Five2FiveResult.FAIL.ordinal();
								} else if (reportPo.status == Five2FiveResult.FAIL.ordinal()) {
									resultA = Five2FiveResult.WIN.ordinal();
								}
							}
						}
					}
					break;
				}
			}
		}
		if (resultInfos.size() == 0) {
			return LangService.getValue("FIVE_2_FIVE_NO_REPORT");
		}
		resultInfos.get(0).resultA = resultA;
		Five2FiveService.getInstance().shardMatchResult(instanceId, resultInfos);
		Out.info("试炼战报,插入分享后的战报成功!角色id=", player.getId(), ",战报id=", instanceId);
		String shareContent = LangService.getValue("5V5_SHARE");
		shareContent = shareContent.replace("playerName", player.getName());
		shareContent = shareContent.replace("Battlefield", instanceId);
		return "";
	}

	/**
	 * 查看战报
	 * 
	 * @param res
	 */
	public void lookBtlReport(Five2FiveLookBtlReportResponse.Builder res) {
		String playerId = player.getId();
		List<Five2FivePlayerBtlReportPO> five2FiveBtlReportPO = player.allBlobData.five2FiveBtlReportPO;
		List<Five2FiveBtlReport> playerResultInfo = new ArrayList<>();
		if (five2FiveBtlReportPO == null) {
			five2FiveBtlReportPO = Five2FiveDao.getPlayerFive2FiveBtlReportPO(playerId);
			if (five2FiveBtlReportPO == null) {
				res.setS2CCode(PomeloRequest.OK);
				res.addAllBr(playerResultInfo);
				return;
			}
			player.allBlobData.five2FiveBtlReportPO = five2FiveBtlReportPO;
		}
		res.setS2CCode(PomeloRequest.OK);
		for (Five2FivePlayerBtlReportPO btlReport : five2FiveBtlReportPO) {
			Map<String, Five2FivePlayerResultInfoVo> resultInfosA = btlReport.resultInfoA;
			Map<String, Five2FivePlayerResultInfoVo> resultInfosB = btlReport.resultInfoB;
			Map<String, Five2FivePlayerResultInfoVo> resultInfos = new HashMap<>();
			resultInfos.putAll(resultInfosA);
			resultInfos.putAll(resultInfosB);
			Five2FivePlayerResultInfoVo resultInfo = resultInfos.get(playerId);
			Five2FiveBtlReport.Builder five2FiveBtlReport = Five2FiveBtlReport.newBuilder();
			five2FiveBtlReport.setStatus(btlReport.status);
			five2FiveBtlReport.setScoreChange(btlReport.scoreChange);
			five2FiveBtlReport.setKillCount(resultInfo.killCount);
			five2FiveBtlReport.setHurt(resultInfo.hurt);
			five2FiveBtlReport.setTreatMent(resultInfo.treatMent);
			Date createTime = btlReport.createTime;
			String createTimeStr = DateUtil.format(createTime, DateUtil.F_yyyyMMdd_new);
			five2FiveBtlReport.setCreateTime(createTimeStr);
			playerResultInfo.add(five2FiveBtlReport.build());
		}
		res.addAllBr(playerResultInfo);
	}

	/**
	 * 查看比赛结果
	 * 
	 * @param matchResultId
	 * @param res
	 */
	public void lookMatchResult(String matchResultId, Five2FiveLookMatchResultResponse.Builder res) {
		List<Five2FivePlayerResultInfoVo> resultInfos = Five2FiveService.getInstance().queryResultInfos(matchResultId);
		if (resultInfos.size() == 0) {
			res.setS2CCode(PomeloRequest.FAIL);
			res.setS2CMsg(LangService.getValue("FIVE_2_FIVE_NO_REPORT"));
			return;
		}
		res.setS2CCode(PomeloRequest.OK);
		List<Five2FivePlayerResultInfoVo> playerResultInfos = Five2FiveService.getInstance().queryResultInfos(matchResultId);
		List<Five2FivePlayerResultInfo> playerResultInfosA = new ArrayList<>();
		List<Five2FivePlayerResultInfo> playerResultInfosB = new ArrayList<>();
		int number = 0;
		for (Five2FivePlayerResultInfoVo everyResultInfo : playerResultInfos) {
			Five2FivePlayerResultInfo.Builder tempResult = Five2FivePlayerResultInfo.newBuilder();
			tempResult.setHurt(everyResultInfo.hurt);
			tempResult.setIsMvp(everyResultInfo.isMvp);
			tempResult.setKillCount(everyResultInfo.killCount);
			tempResult.setPlayerId(everyResultInfo.playerId);
			tempResult.setTreatMent(everyResultInfo.treatMent);
			tempResult.setPlayerName(everyResultInfo.playerName);
			tempResult.setPlayerLevel(everyResultInfo.playerLevel);
			tempResult.setPlayerPro(everyResultInfo.playerPro);
			tempResult.setDeadCount(everyResultInfo.deadCount);
			if (number < Five2FiveMatchPool.getBeginNeedCount()) {
				playerResultInfosA.add(tempResult.build());
			} else {
				playerResultInfosB.add(tempResult.build());
			}
			number++;
		}
		res.setS2CCode(PomeloRequest.OK);
		res.addAllResultInfoA(playerResultInfosA);
		res.addAllResultInfoB(playerResultInfosB);
		res.setResultA(resultInfos.get(0).resultA);
	}

	/**
	 * 刷新每日战斗和领奖次数
	 */
	public void refreshRewardCount() {
		Five2FivePO five2FivePo = player.allBlobData.five2FivePo;
		Date lastChallengeTime = five2FivePo.lastChallengeTime;
		if (lastChallengeTime == null || DateUtil.canRefreshData(5, lastChallengeTime)) {
			five2FivePo.canReciveRewardCount = 0;
			five2FivePo.hasReciveRewardCount = 0;
		}
	}

	/**
	 * 可以领取参与奖励的次数
	 * 
	 * @param five2FivePO
	 * @return
	 */
	public int canReciveRewardCount() {
		Five2FivePO five2FivePo = player.allBlobData.five2FivePo;
		if (five2FivePo == null) {
			return 0;
		}
		refreshRewardCount();
		return five2FivePo.canReciveRewardCount - five2FivePo.hasReciveRewardCount;
	}

	/**
	 * 请求5v5的时间(是否在匹配5v5)
	 * 
	 * @param playerId
	 * @return
	 */
	public Date applyMatchTime(String playerId) {
		return Five2FiveService.getInstance().applyMatchTime(playerId);
	}

	/**
	 * 新的一天刷新
	 */
	public void refreshNewDay() {
		updateSuperScript();
		Out.debug("==========>> Five2FiveManager.refreshNewDay() on ", new Date());
	};

	/**
	 * 请求离开5v5场景
	 * 
	 * @param res
	 * @return
	 */
	public String leaveFive2FiveArea() {
		int historyAreaId = this.player.getPlayerTempData().historyAreaId;
		if (historyAreaId <= 0) {
			return LangService.getValue("AREA_ID_NULL");
		}

		Object finalResult = AreaUtil.dispatchByAreaId(this.player, historyAreaId, this.player.getPlayerTempData().historyX, this.player.getPlayerTempData().historyY);
		if (finalResult != null) {
			return LangService.getValue("AREA_ID_NULL");
		} else {
			return LangService.getValue("SOMETHING_ERR");
		}
	};

	/**
	 * 结算处理
	 * 
	 * @param resultInstanceId
	 * @param result
	 * @param killCount
	 * @param hurt
	 * @param treatMent
	 * @param scoreChange
	 * @param isMvp
	 * @param resultInfos
	 */
	public void onGameEnd(String resultInstanceId, int result, int killCount, int hurt, int treatMent, int scoreChange, boolean isMvp, Map<String, Five2FivePlayerResultInfoVo> resultInfosA, Map<String, Five2FivePlayerResultInfoVo> resultInfosB) {
		// 更新5v5信息
		Five2FivePO five2FivePO = player.allBlobData.five2FivePo;
		refreshFive2FiveInfo(five2FivePO);
		int oldScore = five2FivePO.score;
		five2FivePO.score = five2FivePO.score + scoreChange < 0 ? 0 : five2FivePO.score + scoreChange;
		if (result == Five2FiveResult.WIN.ordinal()) {// 胜利
			five2FivePO.winCount = five2FivePO.winCount + 1;
		} else if (result == Five2FiveResult.TIE.ordinal()) {// 平局
			five2FivePO.tieCount = five2FivePO.tieCount + 1;
		} else {// 失败
			five2FivePO.failCount = five2FivePO.failCount + 1;
		}
		if (isMvp) {
			five2FivePO.mvpCount = five2FivePO.mvpCount + 1;
		}
		five2FivePO.canReciveRewardCount = five2FivePO.canReciveRewardCount + 1 >= GlobalConfig.Group_RewardTimes ? GlobalConfig.Group_RewardTimes : five2FivePO.canReciveRewardCount + 1;
		five2FivePO.lastChallengeTime = new Date();

		// 增加战报
		Five2FivePlayerBtlReportPO reportPo = new Five2FivePlayerBtlReportPO();
		reportPo.id = resultInstanceId;
		reportPo.playerId = player.getId();
		reportPo.status = result;
		reportPo.scoreChange = five2FivePO.score - oldScore;
		reportPo.resultInfoA = resultInfosA;
		reportPo.resultInfoB = resultInfosB;
		reportPo.createTime = new Date();
		List<Five2FivePlayerBtlReportPO> reportPos = player.allBlobData.five2FiveBtlReportPO;
		if (reportPos == null) {
			reportPos = Five2FiveDao.getPlayerFive2FiveBtlReportPO(player.getId());
		}
		if (reportPos.size() >= GlobalConfig.Group_Record) {
			Five2FivePlayerBtlReportPO firstBtlReport = reportPos.get(reportPos.size() - 1);
			Five2FiveService.getInstance().delPlayerFive2FiveBtlReportPO(firstBtlReport);
			reportPos.remove(reportPos.size() - 1);
			Out.info("试炼战报,删除多余的战报:角色id=", player.getId(), ",战报id=", firstBtlReport.id);
		}
		reportPos.add(0, reportPo);
		player.allBlobData.five2FiveBtlReportPO = reportPos;

		Five2FiveService.getInstance().updatePlayerFive2FiveBtlReportPO(reportPo);
		Out.info("试炼战报,写入战报:角色id=", player.getId(), ",战报id=", reportPo.id);
		// 之前排名
		int oldRank = Five2FiveService.getInstance().getPlayerRank(player.getId());

		// 刷新排行榜
		RankType.PVP_5V5.getHandler().asyncUpdateRank(GWorld.__SERVER_ID, player.getId(), five2FivePO.score);

		Five2FiveOnGameEndPush.Builder gameEndPush = Five2FiveOnGameEndPush.newBuilder();
		gameEndPush.setS2CCode(PomeloRequest.OK);
		Five2FiveGameResult.Builder gameResult = Five2FiveGameResult.newBuilder();
		gameResult.setResult(result);
		gameResult.setNewScore(five2FivePO.score - oldScore);
		gameResult.setCurrScore(five2FivePO.score);
		gameResult.setMvpCount(five2FivePO.mvpCount);
		int currentRank = Five2FiveService.getInstance().getPlayerRank(player.getId());
		gameResult.setRankChange(oldRank == 0 ? currentRank : oldRank - currentRank);
		gameResult.setCurrRank((int) currentRank);
		gameResult.setInstanceId(resultInstanceId);
		gameEndPush.setS2CGameResult(gameResult);
		gameEndPush.setS2CGameOverTime(GameData.GameMaps.get(GlobalConfig.Group_Map).timeCount);
		List<Five2FivePlayerResultInfo> resultsA = new ArrayList<>();
		for (Five2FivePlayerResultInfoVo resultInfoVo : resultInfosA.values()) {
			Five2FivePlayerResultInfo.Builder temp = Five2FivePlayerResultInfo.newBuilder();
			temp.setPlayerId(resultInfoVo.playerId);
			temp.setKillCount(resultInfoVo.killCount);
			temp.setHurt(resultInfoVo.hurt);
			temp.setTreatMent(resultInfoVo.treatMent);
			temp.setIsMvp(resultInfoVo.isMvp);
			temp.setPlayerName(resultInfoVo.playerName);
			temp.setPlayerLevel(resultInfoVo.playerLevel);
			temp.setPlayerPro(resultInfoVo.playerPro);
			temp.setDeadCount(resultInfoVo.deadCount);
			resultsA.add(temp.build());
		}
		List<Five2FivePlayerResultInfo> resultsB = new ArrayList<>();
		for (Five2FivePlayerResultInfoVo resultInfoVo : resultInfosB.values()) {
			Five2FivePlayerResultInfo.Builder temp = Five2FivePlayerResultInfo.newBuilder();
			temp.setPlayerId(resultInfoVo.playerId);
			temp.setKillCount(resultInfoVo.killCount);
			temp.setHurt(resultInfoVo.hurt);
			temp.setTreatMent(resultInfoVo.treatMent);
			temp.setIsMvp(resultInfoVo.isMvp);
			temp.setPlayerName(resultInfoVo.playerName);
			temp.setPlayerLevel(resultInfoVo.playerLevel);
			temp.setPlayerPro(resultInfoVo.playerPro);
			temp.setDeadCount(resultInfoVo.deadCount);
			resultsB.add(temp.build());
		}
		gameEndPush.addAllResultInfoA(resultsA);
		gameEndPush.addAllResultInfoB(resultsB);
		player.receive("five2five.five2FivePush.five2FiveOnGameEndPush", gameEndPush.build());
		player.dailyActivityMgr.onEvent(Const.DailyType.PVP_5V5, "0", 1);
	}

	/**
	 * 玩家离线处理
	 */
	public void onPlayerOffline() {
		if(Five2FiveService.getInstance().isInOpenTime()) {
			Five2FiveMatchPool.singleRemoveApplyPool(this.player.getId());
		}
	}

	/**
	 * 玩家登陆处理上赛季未领取的奖励
	 */
	public void onPlayerOnline() {
		Five2FivePO five2FivePO = player.allBlobData.five2FivePo;
		if (five2FivePO == null) {
			five2FivePO = Five2FiveDao.getPlayerFive2FivePO(player.getId());
			if (five2FivePO == null) {
				return;
			}
			player.allBlobData.five2FivePo = five2FivePO;
		}
		Five2FiveSystemPO five2FiveSystemPo = Five2FiveService.getInstance().getFive2FiveSystemPO();
		if (five2FiveSystemPo != null) {
			Map<String, Integer> hasNoReciveRankRewardPlayer = five2FiveSystemPo.hasNoReciveRankRewardPlayer;
			if (hasNoReciveRankRewardPlayer != null && hasNoReciveRankRewardPlayer.containsKey(player.getId())) {
				Map<Integer, PersonalRankExt> personalRanks = GameData.PersonalRanks;
				PersonalRankExt maxExt = null;
				for (PersonalRankExt temp : personalRanks.values()) {
					if (maxExt == null || (temp.startRank > maxExt.startRank && temp.stopRank > maxExt.stopRank)) {
						maxExt = temp;
					}
				}
				PersonalRankExt rankExt = null;
				int rank = hasNoReciveRankRewardPlayer.get(player.getId());
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
				MailUtil.getInstance().sendMailToOnePlayer(this.player.getId(), mailData, Const.GOODS_CHANGE_TYPE.five2five);
				hasNoReciveRankRewardPlayer.remove(player.getId());
				Five2FiveService.getInstance().updateFive2FiveSystem(five2FiveSystemPo);
			}
		}
	}

	/**
	 * 刷新5v5信息
	 * 
	 * @param five2FivePO
	 */
	private void refreshFive2FiveInfo(Five2FivePO five2FivePO) {
		Five2FiveSystemPO five2FiveSystemPo = Five2FiveService.getInstance().getFive2FiveSystemPO();
		Date seasonRefreshTime = five2FiveSystemPo.seasonRefreshTime;
		// 赛季更新
		if (five2FivePO.updateTime == null || !DateUtil.isSameDay(five2FivePO.updateTime, seasonRefreshTime)) {
			five2FivePO.score = 0;
			five2FivePO.winCount = 0;
			five2FivePO.tieCount = 0;
			five2FivePO.failCount = 0;
			five2FivePO.mvpCount = 0;
			five2FivePO.updateTime = seasonRefreshTime;
		}
		refreshRewardCount();
	}

	/**
	 * 更新红点
	 */
	private void updateSuperScript() {
		List<SuperScriptType> list = new ArrayList<>();
		list.addAll(getSuperScript());
		this.player.updateSuperScriptList(list);
	};

	/**
	 * 获取红点信息
	 * 
	 * @return
	 */
	@Override
	public List<SuperScriptType> getSuperScript() {
		List<SuperScriptType> list = new ArrayList<>();
		if (!this.player.functionOpenManager.isOpen(Const.FunctionType.FIVE_2_FIVE.getValue())) {
			return list;
		}
		SuperScriptType.Builder data = SuperScriptType.newBuilder();
		data.setType(Const.SUPERSCRIPT_TYPE.FIVE_2_FIVE_REWARD.getValue());
		data.setNumber(canReciveRewardCount());

		SuperScriptType.Builder data2 = SuperScriptType.newBuilder();
		data2.setType(Const.SUPERSCRIPT_TYPE.FIVE_2_FIVE.getValue());
		data2.setNumber(Five2FiveService.getInstance().isInOpenTime() ? 1 : 0);

		list.add(data.build());
		list.add(data2.build());
		return list;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.wanniu.game.common.ModuleManager#onPlayerEvent(com.wanniu.game.common
	 * .Const.PlayerEventType)
	 */
	@Override
	public void onPlayerEvent(PlayerEventType eventType) {
		switch (eventType) {
		case REFRESH_NEWDAY:
			refreshNewDay();
			break;
		case AFTER_LOGIN:
			onPlayerOnline();
			break;
		default:
			break;
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.wanniu.game.common.ModuleManager#getManagerType()
	 */
	@Override
	public ManagerType getManagerType() {
		return ManagerType.FIVE_2_FIVE;
	}

}
