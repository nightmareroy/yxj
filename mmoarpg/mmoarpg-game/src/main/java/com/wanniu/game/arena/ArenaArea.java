package com.wanniu.game.arena;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import com.alibaba.fastjson.JSONObject;
import com.wanniu.core.game.JobFactory;
import com.wanniu.core.game.LangService;
import com.wanniu.core.logfs.Out;
import com.wanniu.game.area.Area;
import com.wanniu.game.area.AreaEvent;
import com.wanniu.game.arena.vo.ArenaBattleVO;
import com.wanniu.game.common.Const;
import com.wanniu.game.common.msg.MessageUtil;
import com.wanniu.game.common.msg.WNNotifyManager;
import com.wanniu.game.data.GameData;
import com.wanniu.game.player.GlobalConfig;
import com.wanniu.game.player.PlayerUtil;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.sevengoal.SevenGoalManager.SevenGoalTaskType;

import pomelo.area.ArenaHandler.ArenaBattleScore;
import pomelo.area.ArenaHandler.OnArenaBattleEndPush;
import pomelo.area.ArenaHandler.OnArenaBattleInfoPush;
import pomelo.area.PlayerHandler.PlayerRelivePush;

public class ArenaArea extends Area {
	/** 第一名玩家ID */
	private String firstPlayerId;

	/** 排行榜 */
	private ConcurrentHashMap<String, ArenaBattleVO> rank = new ConcurrentHashMap<>();

	public ArenaArea(JSONObject opts) {
		super(opts);
	}

	/**
	 * 从积分排名中移除
	 * 
	 * @param playerId
	 */
	// public void removeFromScoreList(String playerId) {
	// rank.remove(playerId);
	// }

	/**
	 * 刷新玩家排名
	 */
	private void refreshPlayerRank(WNPlayer player) {
		ArenaBattleVO vo = rank.get(player.getId());
		int oldScore = vo.getScore();
		vo.setScore(player.arenaManager.getScore());
		vo.setKillCount(player.arenaManager.getCombo());
		if (oldScore != vo.getScore()) {
			vo.setUpdateTime(System.currentTimeMillis());
		}
	}

	/**
	 * @return 返回杀人个数单场排行
	 */
	private List<ArenaBattleScore> getAllKillCount() {
		List<ArenaBattleVO> listRank = new ArrayList<>(rank.values());
		Collections.sort(listRank, new Comparator<ArenaBattleVO>() {
			@Override
			public int compare(ArenaBattleVO left, ArenaBattleVO right) {
				return left.getKillCount() >= right.getKillCount() ? -1 : 1;
			}

		});

		List<ArenaBattleScore> top3 = new ArrayList<>();
		for (ArenaBattleVO bf : listRank) {
			top3.add(bf.toBuilder(false));
		}

		return top3;
	}

	/**
	 * 获取积分单场排行
	 * 
	 * @return
	 */
	private List<ArenaBattleScore> getAllScores() {
		List<ArenaBattleVO> listRank = new ArrayList<>(rank.values());
		Collections.sort(listRank, new Comparator<ArenaBattleVO>() {
			@Override
			public int compare(ArenaBattleVO left, ArenaBattleVO right) {
				int i = right.getScore() - left.getScore();
				if (i == 0) {
					i = (int) (left.getUpdateTime() - right.getUpdateTime());
				}
				return i;
			}

		});

		List<ArenaBattleScore> top3 = new ArrayList<>();
		for (ArenaBattleVO bf : listRank) {
			top3.add(bf.toBuilder(true));
		}

		return top3;
	}

	/**
	 * 向竞技场内其他玩家的信息都push指定的targetPlayer信息
	 * 
	 * @param targetPlayer
	 */
	private void pushArenaBattleInfo(WNPlayer targetPlayer) {
		List<ArenaBattleScore> killRank = this.getAllKillCount();
		List<ArenaBattleScore> scoreRank = this.getAllScores();
		int i = 1;
		for (ArenaBattleScore ab : scoreRank) {
			WNPlayer player = null;
			if (targetPlayer != null && targetPlayer.getId().equals(ab.getId())) {
				player = targetPlayer;
			} else {
				player = this.getPlayer(ab.getId());
			}

			if (player != null) {
				OnArenaBattleInfoPush.Builder builder = OnArenaBattleInfoPush.newBuilder();
				builder.setS2CCode(Const.CODE.OK);
				builder.setS2CKillCount(player.arenaManager.getCombo());
				builder.setS2CIndex(i);
				builder.setS2CScore(player.arenaManager.getScore());
				builder.addAllS2CScores(scoreRank);
				builder.setS2CPlayerCount(this.actors.size());
				builder.addAllS2CKillCountList(killRank);

				player.receive("area.arenaPush.onArenaBattleInfoPush", builder.build());
			} else {
				Out.error(getClass(), "_pushBattleMessage can't find player id:", ab.getId());
			}
			i++;
		}
		if (!scoreRank.isEmpty()) {
			String newFirst = scoreRank.get(0).getId();
			// 某玩家从不是第一名跃居到第一名广播
			if (!newFirst.equals(firstPlayerId)) {
				String tips = LangService.format("ARENA_TO_FIRST", rank.get(newFirst).getName());
				for (String tempPlayerId : actors.keySet()) {
					WNPlayer tempPlayer = PlayerUtil.getOnlinePlayer(tempPlayerId);
					if (tempPlayer != null) {
						MessageUtil.sendSysTip(tempPlayer, tips, Const.TipsType.ROLL);
					}
				}
				firstPlayerId = newFirst;
			}
		} else {
			firstPlayerId = null;
		}
	}

	@Override
	public void setForce(WNPlayer player) {
		ArenaBattleVO obj = rank.get(player.getId());
		if (obj == null) {
			player.setForce(rank.size() + 2);
		} else {
			player.setForce(obj.getForce());
		}
	}

	public void onPlayerEntered(WNPlayer player) {
		ArenaBattleVO obj = this.rank.get(player.getId());
		if (obj == null) {// 添加积分排名
			obj = new ArenaBattleVO();
			obj.setId(player.getId());
			obj.setName(player.getName());
			obj.setPro(player.getPro());
			obj.setForce(player.getForce());
			this.rank.put(obj.getId(), obj);
		}

		player.getXmdsManager().refreshPlayerPKMode(player.getId(), Const.PkModel.All.value);// pk模式刷新为全体
		player.achievementManager.onArenaScore(0); // 成就清零
		if (firstPlayerId == null) {
			firstPlayerId = player.getId();
		}
		player.arenaManager.setArenaInstanceId(instanceId);

		player.sevenGoalManager.processGoal(SevenGoalTaskType.FIVE_MOUNTAIN_ANTICIPATE);
	}
	
	/**
	 * 怪物击杀玩家
	 */
	@Override
	public void onPlayerDeadByMonster(WNPlayer deadplayer, AreaEvent.MonsterData monsterData,float playerX,float playerY) {
		if (deadplayer == null ) {
			return;
		}
		// 死亡者如果是第一名则广播
		if (firstPlayerId.equals(deadplayer.getId()) && deadplayer.arenaManager.getScore() > 0) {
			String tips = LangService.format("ARENA_FIRST_KILLED", deadplayer.getName());
			for (String tempPlayerId : actors.keySet()) {
				WNPlayer tempPlayer = PlayerUtil.getOnlinePlayer(tempPlayerId);
				if (tempPlayer != null) {
					MessageUtil.sendSysTip(tempPlayer, tips, Const.TipsType.ROLL);
				}
			}
		}

		int score = deadplayer.arenaManager.getScore();
		score = (int) Math.ceil((double) score * 60 / 100);// 只掉60%，并且向上取整
		deadplayer.arenaManager.dropPlayerScore(null, playerX, playerY, score);// 玩家死亡分数掉落
		refreshPlayerRank(deadplayer);

		int overTime = GlobalConfig.JJC_RebirthTime;
		if (deadplayer != null) {
			// 向被击杀玩家推送复活框消息
			PlayerRelivePush.Builder data = newPlayerRelivePush(deadplayer, false);
			data.setCountDown(overTime);

			WNNotifyManager.getInstance().pushRelive(deadplayer, data.build());
		}

		Actor playerData = this.actors.get(deadplayer.getId());
		if (playerData == null) {
			Out.error(getClass(), "onPlayerDeadByPlayer not exist! :", deadplayer.getId());
			return;
		}

		this.pushArenaBattleInfo(null);
	}

	@Override
	public void onPlayerDeadByPlayer(WNPlayer deadplayer, WNPlayer hitPlayer, float x, float y) {
		if (deadplayer == null || hitPlayer == null) {
			return;
		}

		// 死亡者如果是第一名则广播
		if (firstPlayerId.equals(deadplayer.getId()) && deadplayer.arenaManager.getScore() > 0) {
			String tips = LangService.format("ARENA_FIRST_KILLED", deadplayer.getName());
			for (String tempPlayerId : actors.keySet()) {
				WNPlayer tempPlayer = PlayerUtil.getOnlinePlayer(tempPlayerId);
				if (tempPlayer != null) {
					MessageUtil.sendSysTip(tempPlayer, tips, Const.TipsType.ROLL);
				}
			}
		}

		int score = deadplayer.arenaManager.getScore();
		score = (int) Math.ceil((double) score * 60 / 100);// 只掉60%，并且向上取整
		deadplayer.arenaManager.dropPlayerScore(hitPlayer, x, y, score);// 玩家死亡分数掉落

		Actor deadActor = actors.get(deadplayer.getId());
		hitPlayer.arenaManager.killPlayer(deadplayer, deadActor, rank.size());// 计算积分、连杀等信息

		refreshPlayerRank(deadplayer);
		refreshPlayerRank(hitPlayer);

		int overTime = GlobalConfig.JJC_RebirthTime;
		if (deadplayer != null) {
			// 向被击杀玩家推送复活框消息
			PlayerRelivePush.Builder data = newPlayerRelivePush(deadplayer, false);
			data.setCountDown(overTime);

			WNNotifyManager.getInstance().pushRelive(deadplayer, data.build());
		}

		Actor playerData = this.actors.get(deadplayer.getId());
		if (playerData == null) {
			Out.error(getClass(), "onPlayerDeadByPlayer not exist! :", deadplayer.getId());
			return;
		}

		this.pushArenaBattleInfo(null);
	};

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.wanniu.game.area.Area#onCleanItem(com.wanniu.game.area.Area.AreaItem)
	 */
	@Override
	public boolean onCleanItem(AreaItem areaItem) {// 不清理玩家掉落的积分
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.wanniu.game.area.Area#onPickItem(java.lang.String, java.lang.String,
	 * boolean)
	 */
	@Override
	public AreaItem onPickItem(String playerId, String itemId, boolean isGuard) {
		WNPlayer player = this.getPlayer(playerId);
		if (player == null) {
			return null;
		}
		Out.debug(getClass(), player.getName(), " fightLevel onPickItem itemId:", itemId, ",isGuard:", isGuard);
		AreaItem itemInfo = this.items.get(itemId);
		if (itemInfo != null) {
			int groupCount = itemInfo.item.itemDb.groupCount;
			player.arenaManager.addScore(groupCount);
			refreshPlayerRank(player);
			this.items.remove(itemId);
		}
		this.pushArenaBattleInfo(null);
		return itemInfo;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.wanniu.game.area.Area#isFull()
	 */
	public boolean isFull() {
		if (super.isFull()) {
			return true;
		}
		return this.rank.size() >= this.fullCount;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.wanniu.game.area.Area#isFull(int)
	 */
	public boolean isFull(int addCount) {
		if (super.isFull(addCount)) {
			return true;
		}
		return this.rank.size() + addCount > this.fullCount;
	}

	/**
	 * 向指定的player推送竞技场结束通知
	 * 
	 * @param player
	 */
	private void pushArenaBattleEnd(WNPlayer player) {
		if (player == null)
			return;
		OnArenaBattleEndPush.Builder res = OnArenaBattleEndPush.newBuilder();
		res.setS2CCode(Const.CODE.OK);
		res.setOuttime(GameData.GameMaps.get(ArenaService.ARENA_MAP_ID).timeCount);
		player.receive("area.arenaPush.onArenaBattleEndPush", res.build());
	}

	@Override
	public void onGameOver(JSONObject msg) {
		List<ArenaBattleScore> listRank = getAllScores();
		// 通知结束
		for (int i = 0; i < listRank.size(); i++) {
			ArenaBattleScore info = listRank.get(i);
			WNPlayer player = this.getPlayer(info.getId());
			if (player != null) {
				player.arenaManager.onAreaClose(i + 1);// 更新单场积分排名
				if (player.getInstanceId().equals(this.instanceId)) {
					pushArenaBattleEnd(player);
					// 不管死活都让他复活
					getXmdsManager().revivePlayer(player.getId(), reliveData(ReliveType.PLACE));
				}
				// 退出时刷新原来的pk模式到战斗服
				player.getXmdsManager().refreshPlayerPKMode(player.getId(), player.pkRuleManager.pkData.pkModel);
			} else {
				Out.error("can't find player error in arenaArea!!!");
			}

			// 成就
			player.achievementManager.onArenaBattle();
		}
		Out.debug("ArenaArea onGameOver-----");
	}

	public ReliveType getReliveType() {
		return ReliveType.RANDOM;
	}

	public void onEndEnterScene(WNPlayer player) {
		super.onEndEnterScene(player);
		this.pushArenaBattleInfo(player);
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
		// 死亡状态强退增加自动复活
		Actor actor = getActor(player.getId());
		if (actor != null && !actor.alive && !PlayerUtil.isOnline(player.getId())) {
			JobFactory.addDelayJob(() -> {
				if (!actor.alive) {
					relive(player.getId(), getReliveType());
				}
			}, GlobalConfig.JJC_RebirthTime * 1000);
		}
	}

	// 竞技场无角色不能销毁
	@Override
	public void onPlayerLeaved(WNPlayer player) {
		refreshPlayerRank(player);
		pushArenaBattleInfo(player);
	}

}
