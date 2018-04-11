package com.wanniu.game.flee;

import java.util.Date;
import java.util.List;

import com.wanniu.core.game.LangService;
import com.wanniu.core.util.DateUtil;
import com.wanniu.game.GWorld;
import com.wanniu.game.area.AreaUtil;
import com.wanniu.game.common.Const;
import com.wanniu.game.common.ConstsTR;
import com.wanniu.game.data.GameData;
import com.wanniu.game.data.ext.BattleRoyaleRankExt;
import com.wanniu.game.item.ItemUtil;
import com.wanniu.game.item.NormalItem;
import com.wanniu.game.player.GlobalConfig;
import com.wanniu.game.player.PlayerUtil;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.poes.FleePO;
import com.wanniu.game.rank.RankType;
import com.wanniu.redis.PlayerPOManager;

import pomelo.area.FleeHandler.CancelMatchResponse;
import pomelo.area.FleeHandler.EnterFleeResponse;
import pomelo.area.FleeHandler.FleeBtlReport;
import pomelo.area.FleeHandler.FleeInfoResponse;
import pomelo.area.FleeHandler.FleeLookBtlReportResponse;
import pomelo.area.FleeHandler.GetRewardResponse;
import pomelo.area.FleeHandler.GradeReward;

/**
 * 大逃杀
 * 
 * @author lxm
 *
 */
public class FleeManager {
	private WNPlayer player;
	private FleePO fleePo;

	/** 开始匹配时间 */
	private long joinTime;

	public FleeManager(WNPlayer player) {
		this.player = player;
		fleePo = loadFleePO(player.getId());
	}

	private FleePO loadFleePO(String playerId) {
		FleePO fleePO = PlayerPOManager.findPO(ConstsTR.player_fleeTR, playerId, FleePO.class);
		if (fleePO == null) {
			fleePO = new FleePO();
			PlayerPOManager.put(ConstsTR.player_fleeTR, playerId, fleePO);
		}
		return fleePO;
	}

	/**
	 * 获取大逃杀主界面信息
	 * 
	 * @return
	 */
	public FleeInfoResponse getFleeInfoResponse() {
		refreshFlee();

		FleeInfoResponse.Builder res = FleeInfoResponse.newBuilder();
		res.setS2CCode(Const.CODE.OK);
		res.setCurrentRank(FleeService.getInstance().getPlayerRank(player.getId()));
		res.setMaxRank(fleePo.maxRank);
		res.setGrade(fleePo.grade);
		res.setMaxGrade(fleePo.maxGrade);
		res.setScore(fleePo.score);
		res.setSeasonEndTime(fleePo.seasonEndTime.getTime());
		res.setJoinTime(0);
		if (FleeService.getInstance().getMatchPlayer().contains(player.getId())) {
			res.setJoinTime((int) Math.floor(this.joinTime / 1000));
			res.setAvgMatchTime(FleeService.getInstance().getAvgMatchingTime());
		}
		for (BattleRoyaleRankExt r : GameData.BattleRoyaleRanks.values()) {
			GradeReward.Builder re = GradeReward.newBuilder();
			re.setGradeId(r.iD);
			if (fleePo.receiveGrades.contains(r.iD)) {
				re.setStatus(2);// 已领取
			} else {
				re.setStatus(fleePo.grade >= r.iD && fleePo.isPlayed ? 1 : 0);
			}
			res.addGradeRewards(re);
		}

		return res.build();
	}

	private void refreshFlee() {
		Date seasonEndDate = FleeService.getInstance().getSeasonEndDate();
		if (!DateUtil.isSameDay(fleePo.seasonEndTime, seasonEndDate)) {
			fleePo.seasonEndTime = seasonEndDate;
			fleePo.receiveGrades.clear();
			resetGrade();
		}
	}

	/**
	 * 查看战报
	 * 
	 * @return
	 */
	public FleeLookBtlReportResponse getFleeLookBtlReportResponse() {
		FleeLookBtlReportResponse.Builder res = FleeLookBtlReportResponse.newBuilder();
		res.setS2CCode(Const.CODE.OK);

		for (int i = fleePo.reports.size() - 1; i >= 0; i--) {
			FleeReportCO co = fleePo.reports.get(i);
			FleeBtlReport.Builder rep = FleeBtlReport.newBuilder();
			rep.setCreateTime(co.datetime);
			rep.setRank(co.rank);
			rep.setScoreChange(co.scoreChange);
			res.addBr(rep);
		}

		return res.build();
	}

	/**
	 * 领取段位奖励
	 * 
	 * @param gradeId
	 * @return
	 */
	public GetRewardResponse getRewardResponse(int gradeId) {
		GetRewardResponse.Builder res = GetRewardResponse.newBuilder();
		res.setS2CCode(Const.CODE.OK);
		if (fleePo.receiveGrades.contains(gradeId)) {
			res.setS2CCode(Const.CODE.FAIL);
			res.setS2CMsg(LangService.getValue("FLEE_GOT"));
			return res.build();
		}
		if (fleePo.grade < gradeId) {
			res.setS2CCode(Const.CODE.FAIL);
			res.setS2CMsg(LangService.getValue("FLEE_CANNOT_GOT_THIS_RANK"));
			return res.build();
		}
		BattleRoyaleRankExt re = GameData.BattleRoyaleRanks.get(gradeId);
		// 发奖
		List<NormalItem> gradeReward = ItemUtil.createItemsByItemCode(re.gradeRewards);
		if (!player.bag.testAddEntityItems(gradeReward, true)) {
			res.setS2CCode(Const.CODE.FAIL);
			res.setS2CMsg(LangService.getValue("BAG_NOT_ENOUGH_POS"));
			return res.build();
		}
		fleePo.receiveGrades.add(gradeId);
		player.bag.addEntityItems(gradeReward, Const.GOODS_CHANGE_TYPE.flee);
		return res.build();
	}

	/**
	 * 重置段位信息
	 */
	private void resetGrade() {
		int newGrade = GameData.BattleRoyaleRanks.get(fleePo.grade).rankInherit;
		if (newGrade == 0) {
			newGrade = 1;
		}
		int newScore = GameData.BattleRoyaleRanks.get(newGrade).rankScore;
		fleePo.grade = newGrade;
		fleePo.score = newScore;
	}

	/**
	 * 匹配进入大逃杀场景
	 * 
	 * @return
	 */
	public EnterFleeResponse enterFlee() {
		EnterFleeResponse.Builder res = EnterFleeResponse.newBuilder();
		res.setS2CCode(Const.CODE.OK);
		if (!FleeService.getInstance().isInOpenTime()) {
			res.setS2CCode(Const.CODE.FAIL);
			res.setS2CMsg(LangService.getValue("FLEE_NOT_OPEN"));
			return res.build();
		}

		List<String> matchPlayer = FleeService.getInstance().getMatchPlayer();
		if (!matchPlayer.contains(player.getId())) {
			synchronized (matchPlayer) {
				matchPlayer.add(player.getId());
				this.joinTime = System.currentTimeMillis();
				if (matchPlayer.size() == 10) {// 人满了开车
					for (String id : matchPlayer) {
						WNPlayer wp = PlayerUtil.getOnlinePlayer(id);
						FleeService.getInstance().updateMatchingTime(wp.fleeManager.getMatchedTime());
						AreaUtil.dispatchByAreaId(wp, GlobalConfig.Flee_MapID,null);
					}
					matchPlayer.clear();
				}
			}
		}
		res.setJoinTime((int) Math.floor(this.joinTime / 1000));
		res.setAvgMatchTime(FleeService.getInstance().getAvgMatchingTime());
		return res.build();
	}

	/**
	 * 玩家离线处理
	 */
	public void onPlayerOffline() {
		cancelMatchFlee();
	}

	/**
	 * 取消匹配
	 * 
	 * @return
	 */
	public CancelMatchResponse cancelMatchFlee() {
		CancelMatchResponse.Builder res = CancelMatchResponse.newBuilder();
		res.setS2CCode(Const.CODE.OK);

		List<String> matchPlayer = FleeService.getInstance().getMatchPlayer();
		if (matchPlayer.contains(player.getId())) {
			synchronized (matchPlayer) {
				matchPlayer.remove(player.getId());
			}
		}
		return res.build();
	}

	/**
	 * 增加或减少积分，返回积分变化
	 * 
	 * @param score
	 */
	private int addScore(int score) {
		int oldScore = fleePo.score;

		fleePo.score += score;
		if (fleePo.score < 0) {
			fleePo.score = 0;
		}
		fleePo.grade = FleeService.getInstance().getGradeIdByScore(fleePo.score);
		if (fleePo.maxGrade < fleePo.grade) {
			fleePo.maxGrade = fleePo.grade;
		}

		// 更新排行榜
		RankType.FLEE.getHandler().asyncUpdateRank(GWorld.__SERVER_ID, player.getId(), fleePo.score);

		int currentRank = FleeService.getInstance().getPlayerRank(player.getId());
		if (fleePo.maxRank > currentRank) {
			fleePo.maxRank = currentRank;
		}

		return fleePo.score - oldScore;
	}

	/**
	 * 一场结束
	 * 
	 * @param rank
	 * @return
	 */
	public int onGameOver(int rank) {
		fleePo.isPlayed = true;
		int addScore = 0;
		if (rank <= 5) {
			addScore = GlobalConfig.Flee_OneToFive_GetPoint + (5 - rank) * 100;
		} else {
			if (fleePo.score < GlobalConfig.Flee_RankBase_GetPoint) {// 只加不减
				addScore = GlobalConfig.Flee_RankBase_BaseGetPoint + (10 - rank) * GlobalConfig.Flee_RankBase_BaseGetPoint;
			} else {
				addScore = GlobalConfig.Flee_SixToTen_GetPoint + (rank - 6) * 100;
				addScore = -addScore;
			}
		}
		int scoreChange = addScore(addScore);
		fleePo.reports.add(new FleeReportCO(DateUtil.getDateTime(), rank, scoreChange));
		if (fleePo.reports.size() > GlobalConfig.Flee_ReportCount) {
			fleePo.reports.remove(0);
		}
		return scoreChange;
	}

	/**
	 * 匹配成功所耗费的时间
	 */
	public long getMatchedTime() {
		return System.currentTimeMillis() - this.joinTime;
	}

}
