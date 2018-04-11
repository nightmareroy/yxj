package com.wanniu.game.five2Five;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wanniu.core.game.JobFactory;
import com.wanniu.core.logfs.Out;
import com.wanniu.game.GWorld;
import com.wanniu.game.area.Area;
import com.wanniu.game.area.Area.Actor;
import com.wanniu.game.common.Utils;
import com.wanniu.game.common.msg.WNNotifyManager;
import com.wanniu.game.five2Five.Five2FiveService.Five2FiveResult;
import com.wanniu.game.player.GlobalConfig;
import com.wanniu.game.player.PlayerUtil;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.sevengoal.SevenGoalManager.SevenGoalTaskType;

import pomelo.area.PlayerHandler.PlayerRelivePush;

/**
 * @author wanghaitao
 *
 */
public class Five2FiveArea extends Area {
	private CopyOnWriteArrayList<Five2FiveTempTeamMember> tempTeamMemsA;

	private CopyOnWriteArrayList<Five2FiveTempTeamMember> tempTeamMemsB;

	/**
	 * @param opts
	 */
	@SuppressWarnings("unchecked")
	public Five2FiveArea(JSONObject opts) {
		super(opts);
		this.tempTeamMemsA = (CopyOnWriteArrayList<Five2FiveTempTeamMember>) opts.get("tempTeamMemsA");
		this.tempTeamMemsB = (CopyOnWriteArrayList<Five2FiveTempTeamMember>) opts.get("tempTeamMemsB");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.wanniu.game.area.Area#setForce(com.wanniu.game.player.WNPlayer)
	 */
	@Override
	public void setForce(WNPlayer player) {
		for (Five2FiveTempTeamMember tempTeamMem : tempTeamMemsA) {
			if (player.getId().equals(tempTeamMem.playerId)) {
				player.setForce(tempTeamMem.force);
				return;
			}
		}
		for (Five2FiveTempTeamMember tempTeamMem : tempTeamMemsB) {
			if (player.getId().equals(tempTeamMem.playerId)) {
				player.setForce(tempTeamMem.force);
				return;
			}
		}
	}

	

	@Override
	public void addPlayer(WNPlayer player) {
		setForce(player);
		player.setArea(this);
		String enterSceneData = toJSON4EnterScene(player);
		JSONObject jsonObject = JSONObject.parseObject(enterSceneData);
		@SuppressWarnings("unchecked")
		Map<String, Number> effects = (Map<String, Number>) jsonObject.get("effects");
		int index = 1;
		boolean isFinded = false;
		for (Five2FiveTempTeamMember member : tempTeamMemsA) {
			if (member.playerId.equals(player.getId())) {
				index = member.index;
				isFinded = true;
				break;
			}
		}
		if (!isFinded) {
			for (Five2FiveTempTeamMember member : tempTeamMemsB) {
				if (member.playerId.equals(player.getId())) {
					index = member.index;
				}
			}
		}
		effects.put("index", index);
		jsonObject.put("indexOut", index);
		enterSceneData = JSONObject.toJSONString(jsonObject);
		Out.debug("begin enter scene id:", this.instanceId);
		try {
			String playerId = player.getId();

			if (!actors.containsKey(playerId)) {
				this.hasPlayerEntered = true;
				actors.put(playerId, new Actor());
			}
			this.removeCloseFuture();
			Out.debug("enter scene id: ", this.instanceId, " ok", "index:", this.lineIndex);
		} catch (Exception error) {
			Out.error("c# enter scene id: ", this.instanceId, " error : ", error, " enterSceneData:", enterSceneData);
			throw error;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.wanniu.game.area.Area#onPlayerDeadByPlayer(com.wanniu.game.player.
	 * WNPlayer, com.wanniu.game.player.WNPlayer, float, float)
	 */
	@Override
	public void onPlayerDeadByPlayer(WNPlayer deadPlayer, WNPlayer hitPlayer, float x, float y) {
		// hitPlayer.arenaManager.killPlayer(deadplayer);// 计算积分、连杀等信息

		int overTime = GlobalConfig.Group_Resurrection;
		if (deadPlayer != null) {
			// 向被击杀玩家推送复活框消息
			PlayerRelivePush.Builder data = newPlayerRelivePush(deadPlayer, false);
			// String clientMsg =
			// LangService.getValue("REVIVE_JJC_KILL").replace("{playerName}",hitPlayer.getName());
			// data.setClientMsg(clientMsg);

			// ServerMsg serverMsg = new ServerMsg(1, hitPlayer.getId(),
			// hitPlayer.getName(), hitPlayer.getLevel(), hitPlayer.getPro(),
			// 0);
			// data.setServerMsg(JSON.toJSONString(serverMsg));
			data.setCountDown(overTime);
			data.setCbType(ReliveCB.RELIVE.value);

			WNNotifyManager.getInstance().pushRelive(deadPlayer, data.build());
		}

		Actor playerData = this.actors.get(deadPlayer.getId());
		if (playerData == null) {
			Out.error(getClass(), "onPlayerDeadByPlayer not exist! :", deadPlayer.getId());
			return;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.wanniu.game.area.Area#onGameOver(com.alibaba.fastjson.JSONObject)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void onGameOver(JSONObject event) {
		Out.debug("5v5 gameover");
		int winForce = event.getIntValue("winForce");// result 1-胜 2-负 3-平
		List<Five2FiveTempTeamMember> tempTeamMems = new ArrayList<>();
		tempTeamMems.addAll(tempTeamMemsA);
		tempTeamMems.addAll(tempTeamMemsB);

		String statistice = super.getXmdsManager().getInstanceStatistic(instanceId);
		JSONObject statisticeJson = JSON.parseObject(statistice);
		Map<String, int[]> everyStatistieces = new HashMap<>();
		for (Five2FiveTempTeamMember tempTeamMem : tempTeamMems) {
			String tempTeamMemPlayerId = tempTeamMem.playerId;
			Map<String, Integer> everyStatice = (Map<String, Integer>) statisticeJson.get(tempTeamMemPlayerId);
			int[] everyStatistiece = new int[4];
			if (everyStatice != null) {
				everyStatistiece[0] = everyStatice.get("killPlayerCount");
				everyStatistiece[1] = everyStatice.get("totalDamage");
				everyStatistiece[2] = everyStatice.get("totalHealing");
				Integer deadCount = everyStatice.get("deadCount");
				everyStatistiece[3] = deadCount == null ? 0 : deadCount;
			}
			everyStatistieces.put(tempTeamMemPlayerId, everyStatistiece);
		}

		int maxMvpValue = 0;
		String mvpPlayerId = "";
		Map<String, Integer> killCounts = new HashMap<>();
		Map<String, Integer> hurts = new HashMap<>();
		Map<String, Integer> treatMents = new HashMap<>();
		Map<String, Integer> deadCounts = new HashMap<>();
		for (Five2FiveTempTeamMember tempTeamMem : tempTeamMems) {// 计算MVP
			String tempTeamPlayerId = tempTeamMem.playerId;
			int[] everyStatistiece = everyStatistieces.get(tempTeamPlayerId);
			int killCount = everyStatistiece[0];
			int hurt = everyStatistiece[1];
			int treatMent = everyStatistiece[2];
			int deadCount = everyStatistiece[3];

			if (tempTeamMem.force == winForce) {
				int mvpValue = killCount * GlobalConfig.Group_Kill + hurt * GlobalConfig.Group_Dps + treatMent * GlobalConfig.Group_Treatment;
				if (mvpValue > maxMvpValue) {
					maxMvpValue = mvpValue;
					mvpPlayerId = tempTeamPlayerId;
				}
			}
			killCounts.put(tempTeamPlayerId, killCount);
			hurts.put(tempTeamPlayerId, hurt);
			treatMents.put(tempTeamPlayerId, treatMent);
			deadCounts.put(tempTeamPlayerId, deadCount);
		}

		Map<String, Five2FivePlayerResultInfoVo> playerResultInfosA = new HashMap<>();
		for (Five2FiveTempTeamMember tempTeamMem : tempTeamMemsA) {
			String tempTeamPlayerId = tempTeamMem.playerId;
			int killCount = killCounts.get(tempTeamPlayerId);
			int hurt = hurts.get(tempTeamPlayerId);
			int treatMent = treatMents.get(tempTeamPlayerId);
			boolean isMvp = false;
			if (mvpPlayerId.equals(tempTeamPlayerId)) {
				isMvp = true;
			}
			processPlayerResultInfo(tempTeamPlayerId, killCount, hurt, treatMent, isMvp, deadCounts.get(tempTeamPlayerId), playerResultInfosA);
		}

		Map<String, Five2FivePlayerResultInfoVo> playerResultInfosB = new HashMap<>();
		for (Five2FiveTempTeamMember tempTeamMem : tempTeamMemsB) {
			String tempTeamPlayerId = tempTeamMem.playerId;
			int killCount = killCounts.get(tempTeamPlayerId);
			int hurt = hurts.get(tempTeamPlayerId);
			int treatMent = treatMents.get(tempTeamPlayerId);
			boolean isMvp = false;
			if (mvpPlayerId.equals(tempTeamPlayerId)) {
				isMvp = true;
			}
			processPlayerResultInfo(tempTeamPlayerId, killCount, hurt, treatMent, isMvp, deadCounts.get(tempTeamPlayerId), playerResultInfosB);
			if(playerResultInfosA.containsKey(tempTeamPlayerId)) {
				Out.warn("5v5 Area:"+ this.instanceId+" player [" + tempTeamPlayerId +"] duplicate");
			}
		}

		String resultInstanceId = UUID.randomUUID().toString();
		onBothSidesGameOver(everyStatistieces, resultInstanceId, mvpPlayerId, winForce, tempTeamMemsA, playerResultInfosA, playerResultInfosB);
		onBothSidesGameOver(everyStatistieces, resultInstanceId, mvpPlayerId, winForce, tempTeamMemsB, playerResultInfosA, playerResultInfosB);

	}

	@Override
	protected void onDisponseLeave(WNPlayer player) {
		player.five2FiveManager.leaveFive2FiveArea();
	}

	/**
	 * 两边分别结算
	 * 
	 * @param everyStatistieces
	 * @param resultInstanceId
	 * @param mvpPlayerId
	 * @param winForce
	 * @param tempTeamMems
	 * @param resultInfosA
	 * @param resultInfosB
	 */
	private void onBothSidesGameOver(Map<String, int[]> everyStatistieces, String resultInstanceId, String mvpPlayerId, int winForce, CopyOnWriteArrayList<Five2FiveTempTeamMember> tempTeamMems, Map<String, Five2FivePlayerResultInfoVo> resultInfosA,
			Map<String, Five2FivePlayerResultInfoVo> resultInfosB) {
		for (Five2FiveTempTeamMember tempTeamMem : tempTeamMems) {
			String tempTeamPlayerId = tempTeamMem.playerId;
			WNPlayer tempTeamPlayer = this.getPlayer(tempTeamPlayerId);
			if (tempTeamPlayer == null) {
				tempTeamPlayer = (WNPlayer) GWorld.getInstance().getWaitPlayers().get(tempTeamPlayerId);
			}
			int[] everyStatistiece = everyStatistieces.get(tempTeamPlayerId);
			int killCount = 0;
			int hurt = 0;
			int treatMent = 0;
			if (everyStatistiece != null) {
				killCount = everyStatistiece[0];
				hurt = everyStatistiece[1];
				treatMent = everyStatistiece[2];
			}
			int result = 0;
			int scoreChange = 0;
			if (winForce == 0) {// 平局
				result = Five2FiveResult.TIE.ordinal();
				scoreChange = 0;
			} else if (tempTeamMem.force == winForce) {// 胜利
				result = Five2FiveResult.WIN.ordinal();
				scoreChange = GlobalConfig.Group_Integral;
			} else {// 负
				result = Five2FiveResult.FAIL.ordinal();
				scoreChange = -GlobalConfig.Group_Lose;
			}
			boolean isMvp = false;
			if (tempTeamPlayerId.equals(mvpPlayerId)) {
				isMvp = true;
			}
			tempTeamPlayer.five2FiveManager.onGameEnd(resultInstanceId, result, killCount, hurt, treatMent, scoreChange, isMvp, resultInfosA, resultInfosB);
		}
	}

	/**
	 * 处理结算信息
	 * 
	 * @param tempTeamPlayerId
	 * @param killCount
	 * @param hurt
	 * @param treatMent
	 * @param isMvp
	 * @param resultInfos
	 */
	private void processPlayerResultInfo(String tempTeamPlayerId, int killCount, int hurt, int treatMent, boolean isMvp, int deadCount, Map<String, Five2FivePlayerResultInfoVo> resultInfos) {
		Five2FivePlayerResultInfoVo resultInfoVo = new Five2FivePlayerResultInfoVo();
		WNPlayer tempPlayer = PlayerUtil.getOnlinePlayer(tempTeamPlayerId);
		if (tempPlayer == null) {
			tempPlayer = (WNPlayer) GWorld.getInstance().getWaitPlayers().get(tempTeamPlayerId);
		}
		resultInfoVo = new Five2FivePlayerResultInfoVo(tempPlayer.getId(), tempPlayer.getName(), tempPlayer.getLevel(), tempPlayer.getPro(), killCount, hurt, treatMent, isMvp ? 1 : 0, deadCount);
		resultInfos.put(tempTeamPlayerId, resultInfoVo);
		// 成就
		tempPlayer.achievementManager.onPassFiveVsFive();
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
		// 死亡状态强退增加自动复活
		Actor actor = getActor(player.getId());
		if (actor != null && !actor.alive && !PlayerUtil.isOnline(player.getId())) {
			JobFactory.addDelayJob(() -> {
				if (!actor.alive) {
					relive(player.getId(), getReliveType());
				}
			}, GlobalConfig.Group_Resurrection * 1000);
		}
	}
	
	/**
	 * 角色成功进入场景
	 */
	@Override
	public void onPlayerEntered(WNPlayer player) {
		super.onPlayerEntered(player);
		player.sevenGoalManager.processGoal(SevenGoalTaskType.TRIAL_ANTICIPATE);
	}
}
