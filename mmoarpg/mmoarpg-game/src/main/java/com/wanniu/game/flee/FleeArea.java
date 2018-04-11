package com.wanniu.game.flee;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import com.alibaba.fastjson.JSONObject;
import com.wanniu.game.area.Area;
import com.wanniu.game.common.Const;
import com.wanniu.game.data.GameData;
import com.wanniu.game.player.GlobalConfig;
import com.wanniu.game.player.PlayerUtil;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.sevengoal.SevenGoalManager.SevenGoalTaskType;

import pomelo.area.FleeHandler.FleeMatchMemberInfo;
import pomelo.area.FleeHandler.OnFleeDeathPush;
import pomelo.area.FleeHandler.OnFleeEndPush;

/**
 * 大逃杀场景
 * 
 * @author lxm
 *
 */
public class FleeArea extends Area {

	private ConcurrentHashMap<String, FleePlayer> playerMap = new ConcurrentHashMap<>();

	public FleeArea(JSONObject opts) {
		super(opts);
	}

	@Override
	public void setForce(WNPlayer player) {
		FleePlayer fleePlayer = playerMap.get(player.getId());
		if (fleePlayer != null) {
			player.setForce(fleePlayer.force);
		} else {
			player.setForce(playerMap.size() + 1);
		}
	}

	@Override
	public void onPlayerEntered(WNPlayer player) {
		FleePlayer fleePlayer = playerMap.get(player.getId());
		if (fleePlayer == null) {
			fleePlayer = new FleePlayer(player.getId(), player.getName(), player.getLevel(), player.getPro(), player.getForce(), playerMap.size() + 1);
			playerMap.put(fleePlayer.playerId, fleePlayer);
		}
		player.getXmdsManager().refreshPlayerPKMode(player.getId(), Const.PkModel.All.value);// pk模式刷新为全体
		
	}

	@Override
	public void onPlayerDeadByPlayer(WNPlayer deadplayer, WNPlayer hitPlayer, float x, float y) {
		if (deadplayer == null || hitPlayer == null) {
			return;
		}
		playerMap.get(hitPlayer.getId()).killCount++;
		FleePlayer deathPlayer = playerMap.get(deadplayer.getId());
		deathPlayer.rank = getAliveNum() + 1;
		deathPlayer.isDeath = true;

		int scoreChange = deadplayer.fleeManager.onGameOver(deathPlayer.rank);

		pushFleeDeath(deadplayer, deathPlayer.rank, scoreChange);
	}

	/**
	 * 获取场景活着的人数
	 * 
	 * @return
	 */
	private int getAliveNum() {
		int num = 0;
		for (Actor a : actors.values()) {
			if (a.alive) {
				num++;
			}
		}
		return num;
	}

	/**
	 * 玩家死亡推送
	 * 
	 * @param player
	 */
	private void pushFleeDeath(WNPlayer player, int rank, int scoreChange) {
		OnFleeDeathPush.Builder res = OnFleeDeathPush.newBuilder();
		res.setS2CCode(Const.CODE.OK);
		res.setOuttime(GameData.GameMaps.get(GlobalConfig.Flee_MapID).timeCount);
		res.setRank(rank);
		res.setScoreChange(scoreChange);
		player.receive("area.fleePush.onFleeDeathPush", res.build());
	}

	@Override
	public void onGameOver(JSONObject msg) {
		OnFleeEndPush.Builder push = OnFleeEndPush.newBuilder();
		push.setS2CCode(Const.CODE.OK);
		push.setOuttime(GameData.GameMaps.get(GlobalConfig.Flee_MapID).timeCount);
		List<FleePlayer> list = new ArrayList<>(playerMap.values());
		List<FleePlayer> notDeathList = new ArrayList<>();// 没有死亡的人
		for (FleePlayer p : list) {
			if (!p.isDeath) {
				notDeathList.add(p);
			}
		}
		list.removeAll(notDeathList);
		Collections.sort(notDeathList, (o1, o2) -> {
			return o1.force - o2.force;
		});
		for (int i = 0; i < notDeathList.size(); i++) {// 按排名发奖
			WNPlayer player = PlayerUtil.getOnlinePlayer(notDeathList.get(i).playerId);
			player.fleeManager.onGameOver(i + 1);
		}
		// 死亡的人加入到未死亡人后面
		notDeathList.addAll(list);

		for (FleePlayer p : notDeathList) {
			FleeMatchMemberInfo.Builder mem = FleeMatchMemberInfo.newBuilder();
			mem.setPlayerId(p.playerId);
			mem.setPlayerName(p.playerName);
			mem.setPlayerLvl(p.playerLevel);
			mem.setPlayerPro(p.playerPro);
			mem.setKillCount(p.killCount);
			mem.setScoreChange(p.scoreChange);
			push.addRanks(mem);
		}
		for (String id : actors.keySet()) {
			WNPlayer player = PlayerUtil.getOnlinePlayer(id);
			player.receive("area.fleePush.onFleeEndPush", push.build());
		}
	}

	public ReliveType getReliveType() {
		return ReliveType.RANDOM;
	}

	@Override
	public void onPlayerLogout(WNPlayer player) {
		// 重写父类方法去掉移除代码，考虑到玩家身上有buff所以不能移除。
		boolean keepObject = isKeepObject();
		if (keepObject && !isClose()) {
			// 防止进入场景过程中掉线
			player.getXmdsManager().playerReady(player.getId());
			// 自动托管
			onPlayerAutoBattle(player, true);
		}
	}

}
