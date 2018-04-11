package com.wanniu.game.solo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.alibaba.fastjson.JSONObject;
import com.wanniu.core.game.JobFactory;
import com.wanniu.core.logfs.Out;
import com.wanniu.game.area.Area;
import com.wanniu.game.area.AreaManager;
import com.wanniu.game.area.MonsterUnit;
import com.wanniu.game.common.Const;
import com.wanniu.game.data.GameData;
import com.wanniu.game.data.SoloMonsterCO;
import com.wanniu.game.player.GlobalConfig;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.sevengoal.SevenGoalManager.SevenGoalTaskType;

/**
 * 单挑场景
 */
public class SoloArea extends Area {
	public static int clearSeconds = 400;

	private boolean isGameOver = false;

	public SoloArea(JSONObject opts) {
		super(opts);
		// 计算出自动关闭的时间。
		JobFactory.addDelayJob(() -> {
			Out.info("问道玩法清场.", this.instanceId);
			AreaManager.getInstance().closeArea(instanceId);
		}, clearSeconds, TimeUnit.SECONDS);
	}

	public void addRobot(SoloMonsterCO soloProp) {
		String startPoint = String.valueOf(soloProp.startPoint);

		MonsterUnit monsterData = new MonsterUnit();
		monsterData.id = soloProp.monID;
		monsterData.force = Const.AreaForce.FORCEB.value;
		monsterData.flag = startPoint;
		monsterData.autoGuard = true;
		List<MonsterUnit> monsters = new ArrayList<>();
		monsters.add(monsterData);
		addUnitsToArea(monsters);
	};

	public String toJSON4EnterScene(WNPlayer player) {
		JSONObject json = player.toJSON4EnterScene(this);
		@SuppressWarnings("unchecked")
		Map<String, Number> tempData = (Map<String, Number>) json.get("tempData");
		tempData.put("x", 0);
		tempData.put("y", 0);
		return json.toJSONString();
	}

	/**
	 * 分配阵营
	 */
	public void setForce(WNPlayer player) {

	}

	private static class SoloResult {
		public String playerId;
		public int result;// result 1-胜 2-负 3-平

		public SoloResult(String playerId, int result) {
			this.playerId = playerId;
			this.result = result;
		}
	}

	/**
	 * 角色成功进入场景
	 */
	public void onPlayerEntered(WNPlayer player) {
		super.onPlayerEntered(player);
		player.sevenGoalManager.processGoal(SevenGoalTaskType.SOLO_ANTICIPATE);
	}

	/**
	 * 场景关闭事件
	 */
	public void onGameOver(JSONObject msg) {
		if (this.isGameOver) {
			return;
		}
		Out.info("soloArea gameover,instanceId=",this.instanceId);
		int winForce = msg.getIntValue("winForce");
		List<SoloResult> playerDatas = new ArrayList<>();

		int winScore = 0, failScore = 0;
		for (String playerId : this.actors.keySet()) {
			WNPlayer player = this.getPlayer(playerId);
			if (winForce == 0) {// 平局
				playerDatas.add(new SoloResult(playerId, 3));
			} else if (player.getForce() == winForce) {// 胜利
				playerDatas.add(new SoloResult(playerId, 1));
				winScore = player.soloManager.getScore();
			} else {// 负
				playerDatas.add(new SoloResult(playerId, 2));
				failScore = player.soloManager.getScore();
			}
		}

		int addScore = 0;// 胜利者得分
		int subScore = 0;// 失败者得分
		if (winForce != 0) {
			addScore = calcWinnerScore(winScore, failScore);
			subScore = calcLoserScore(winScore, failScore);
		}

		for (String playerId : this.actors.keySet()) {
			WNPlayer player = this.getPlayer(playerId);
			if (player != null) {
				// player.soloManager.setBusy(false);
				if (playerDatas.size() == 2) {
					if (playerId == playerDatas.get(0).playerId) {
						player.soloManager.onGameEnd(playerDatas.get(0).result, getPlayer(playerDatas.get(1).playerId), addScore,subScore);
					} else {
						player.soloManager.onGameEnd(playerDatas.get(1).result, getPlayer(playerDatas.get(0).playerId), addScore,subScore);
					}
				} else {
					player.soloManager.onGameEnd(playerDatas.get(0).result, null, addScore,subScore);
				}

			}
		}

		this.isGameOver = true;
	}

	/**
	 * 根据胜者资历和负者资历计算获得的积分
	 * 
	 * @param winnerScore
	 * @param loserScore
	 * @return
	 */
	private int calcScoreVariable(int winnerScore, int loserScore) {
		int base = GlobalConfig.Solo_WinGetPoint;
		int diff = winnerScore - loserScore;
		int floatValue = (int) (diff / 20);
		// -30到30
		floatValue = Math.min(Math.max(-base, floatValue), base);
		return base - floatValue;
	}
	
	/**
	 * Calculate the score of winner
	 * @param winnerScore
	 * @param loserScore
	 * @return winner's score
	 */
	private int calcWinnerScore(int winnerScore, int loserScore) {
		return calcScoreVariable(winnerScore,loserScore) + 1;
	}

	/**	top rank score in SoloRankCO.RankScore  */
	private static int TOP_RANKSCORE = 1000;
	/**
	 * Calculate the score of loser
	 * @param winnerScore
	 * @param loserScore
	 * @return loser's score, the result maybe negative
	 */
	private int calcLoserScore(int winnerScore, int loserScore) {
		if(loserScore<TOP_RANKSCORE) {
			return 1;
		}else {
			return -calcScoreVariable(winnerScore,loserScore);
		}
	}
	
	@Override
	protected void onDisponseLeave(WNPlayer player) {
		player.soloManager.handleLeaveSoloArea();
	}

	@Override
	public void onPlayerDeadByPlayer(WNPlayer deadplayer, WNPlayer hitPlayer, float x, float y) {

	}

	@Override
	public void onPlayerLogout(WNPlayer player) {
		boolean keepObject = isKeepObject();
		if (keepObject && !isClose()) {
			// 防止进入场景过程中掉线
			player.getXmdsManager().playerReady(player.getId());
			// 自动托管
			onPlayerAutoBattle(player, true);
		}
	}

}
