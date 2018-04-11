package com.wanniu.game.solo;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

import com.wanniu.core.game.JobFactory;
import com.wanniu.core.logfs.Out;
import com.wanniu.game.GWorld;
import com.wanniu.game.area.Area;
import com.wanniu.game.area.AreaData;
import com.wanniu.game.area.AreaUtil;
import com.wanniu.game.common.Const;
import com.wanniu.game.common.Utils;
import com.wanniu.game.player.GlobalConfig;
import com.wanniu.game.player.PlayerUtil;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.solo.vo.MatcherVO;

import pomelo.Common.Avatar;
import pomelo.area.SoloHandler.OnSoloMatchedPush;

/**
 * @author WFY 管理所有参战玩家的匹配
 */
public class SoloMatcher {
	public long sumMatchingTime;// 匹配对手总等待时间
	public int matchedNumber;// 匹配成功的人数
	private Map<String, MatcherVO> matchers = new ConcurrentHashMap<>();
	private static List<MatchedTeam> matchedQueue = new CopyOnWriteArrayList<>();
	private static SoloMatcher soloService;

	public static SoloMatcher getInstance() {
		if (soloService == null) {
			soloService = new SoloMatcher();
		}
		return soloService;
	}

	private SoloMatcher() {
		init();
	}

	private void init() {
		this.sumMatchingTime = 0;
		this.matchedNumber = 0;

		JobFactory.addScheduleJob(new Runnable() {
			@Override
			public void run() {
				if (SoloService.getInstance().isInOpenTime()) {
					checkMatchers();
				}
			}
		}, GlobalConfig.Solo_MatchWaitTime, GlobalConfig.Solo_MatchWaitTime, TimeUnit.SECONDS);

	}

	/**
	 * 定时检查玩家等待时间状态和离线时间，清理状态为可删除的用户
	 */
	private void checkMatchers() {
		synchronized (matchers) {
			Iterator<MatcherVO> iter = this.matchers.values().iterator();
			while (iter.hasNext()) {
				MatcherVO mpo = iter.next();
				if (mpo.isOnline()) {
					if (mpo.getBeginTime() >= GlobalConfig.Solo_MatchWaitTime) {// 扩大匹配分值范围
						mpo.increaseScoreRange();
					}
				} else {
					if (mpo.getOfflinedTime() >= GlobalConfig.Solo_MatchOfflineTime) {// 检查离线时间，超过配置值就从匹配列表删除
						iter.remove();
						Out.debug("matcher list removed player:", mpo.getPlayer().getName());
					}
				}
			}

			for (MatcherVO mpo : matchers.values()) {// 遍历每个队列中的玩家此时是否有合适的对手
				if (mpo.isMarkedDel()) {
					Out.warn("匹配异常，命中匹配过了，playerId=", mpo.getPlayerId());
					continue;
				}
				if (!mpo.isOnline()) {
					Out.warn("匹配异常，命中不在线了，playerId=", mpo.getPlayerId());
					continue;
				}
				markMatched(mpo);
			}

			iter = this.matchers.values().iterator();
			while (iter.hasNext()) {// 清除匹配成功、过期的玩家
				MatcherVO mpo = iter.next();
				if (mpo.isMarkedDel()) {
					iter.remove();
					Out.debug("matcher list removed player:" + mpo.getPlayer().getName());
				}
			}
		}
	}

	/**
	 * 查找指定player是否有可以匹配的对手，匹配成功后把双方标记为可删除状态，并加入匹配成功队列
	 * 
	 * @param player
	 */
	private void markMatched(MatcherVO player) {
		int minScore = player.getMinScore();
		int maxScore = player.getMaxScore();

		for (MatcherVO mpo : matchers.values()) {
			if (!mpo.isMarkedDel() && mpo.isOnline() && mpo.getScore() >= minScore && mpo.getScore() <= maxScore && !player.getPlayerId().equals(mpo.getPlayerId())) {
				mpo.setMarkedDel(true);
				player.setMarkedDel(true);
				SoloMatcher.addWaiter(mpo.getPlayer(), player.getPlayer());
				return;
			}
		}
	}

	/**
	 * 重置匹配列表
	 */
	public void resetMatchingList() {
		this.sumMatchingTime = 0;
		this.matchedNumber = 0;
		this.matchers.clear();
	}

	/**
	 * 收到匹配成功消息的玩家确认进入战斗
	 * 
	 * @param wnPlayer
	 * @return 不能进入返回false
	 */
	public boolean joinBattle(WNPlayer wnPlayer) {
		MatchedTeam team = this.getTeam(wnPlayer.getId());
		if (team == null) {// 没有匹配成功的对手
			return false;
		}
		GWorld.getInstance().ansycExec(() -> {
			synchronized (team) {
				if (team.getAllPlayers().contains(wnPlayer)) {
					// 成就
					wnPlayer.achievementManager.onSoloBattle();

					if (team.getBattleArea() != null) {// 已经有战斗场景了
						enterSoloArea(wnPlayer, team.getBattleArea());
					} else {
						team.setBattleArea(enterNewSoloArea(wnPlayer));
					}
					Out.info("solo enter scene: instanceId=", team.getBattleArea().instanceId);
					team.remove(wnPlayer.getId());
				}
			}
		});
		return true;
	}

	/**
	 * 获取平均匹配等待时间秒数
	 * 
	 * @return
	 */
	public int getAvgMatchingTime() {
		if (this.matchedNumber <= 0) {
			return GlobalConfig.Solo_MATCH_TIME;
		}
		return (int) (this.sumMatchingTime / this.matchedNumber) / 1000;
	}

	/**
	 * 加入新的匹配时间
	 * 
	 * @param matchedTime
	 * @return
	 */
	public long updateMatchingTime(long matchedTime) {
		this.matchedNumber += 1;
		this.sumMatchingTime += matchedTime;
		return this.sumMatchingTime / this.matchedNumber;
	}

	/**
	 * 根据我的资质查找匹配对手
	 * 
	 * @param myScore
	 * @param myId
	 * @return
	 */
	public WNPlayer findMatchedPlayer(int myScore, String myId) {
		WNPlayer dest = null;
		synchronized (matchers) {
			int matchScoreRange = GlobalConfig.Solo_MatchRangeIncrease;
			int minScore = myScore - matchScoreRange;
			int maxScore = myScore + matchScoreRange;

			MatcherVO player = this.matchers.get(myId);
			if (player != null) {
				minScore = player.getMinScore();
				maxScore = player.getMaxScore();
			}
			for (MatcherVO mpo : matchers.values()) {
				// 在线、没有标记删除、分值范围匹配
				if (mpo.isOnline() && !mpo.isMarkedDel() && mpo.getScore() >= minScore && mpo.getScore() <= maxScore) {
					dest = mpo.getPlayer();
					break;
				}
			}
			if (dest != null) {// 匹配成功，从匹配列表里移除
				this.matchers.remove(dest.getId());
				if (player != null) {
					this.matchers.remove(player.getPlayerId());
				}
			}
		}
		return dest;
	}

	public void playerOffline(String playerId) {
		MatcherVO player = this.matchers.get(playerId);
		if (player != null) {
			player.setOffline();
		}
	}

	/**
	 * 加入匹配队列
	 * 
	 * @param myScore
	 * @param myId
	 */
	public void addToMatchingList(int myScore, WNPlayer me) {
		MatcherVO player = this.matchers.get(me.getId());

		if (player != null) {
			player.setOnline();
		} else {
			this.matchers.put(me.getId(), new MatcherVO(myScore, me));
		}
		Out.debug(me.getName(), " added -------mathers:", matchers.size());
	}

	/**
	 * 是否在匹配队列中
	 * 
	 * @param playerId
	 * @return
	 */
	public boolean isInMatchingList(String playerId) {
		return this.matchers.get(playerId) != null;
	}

	/**
	 * 从匹配列表里删除指定对象
	 * 
	 * @param playerId
	 */
	public void delToMatchingList(String playerId) {
		// synchronized (matchers) {
		this.matchers.remove(playerId);
		// }
	}

	/**
	 * 匹配成功后，通知用户并等待双方确认
	 * 
	 * @param a
	 * @param b
	 */
	public static void addWaiter(WNPlayer a, WNPlayer b) {
		Out.info("solo matched:", "a=", a.getId(), ",b=", b.getId());
		MatchedTeam team = new MatchedTeam(a, b);
		// synchronized (SoloMatcher.matchedQueue) {
		matchedQueue.add(team);
		pushSoloMatched(a, b, false);
		pushSoloMatched(b, a, false);
		// }

		JobFactory.addDelayJob(new Runnable() {
			@Override
			public void run() {
				Collection<WNPlayer> members = team.getAllPlayers();
				for (WNPlayer member : members) {
					SoloMatcher.getInstance().joinBattle(member);
					// Area areaData = team.getBattleArea();
					// if (areaData != null) {
					// enterSoloArea(member, areaData);
					// } else {
					// team.setBattleArea(enterNewSoloArea(member));
					// }
				}
				// team.clear();
				// synchronized (SoloMatcher.matchedQueue) {
				matchedQueue.remove(team);
				// }
			}
		}, GlobalConfig.Solo_EnterTime, TimeUnit.SECONDS);

	}

	/**
	 * 先到的玩家创建一个单挑场景
	 * 
	 * @param player
	 * @return 返回新场景的AreaData 包含instanceId
	 */
	private static Area enterNewSoloArea(WNPlayer player) {
		player.setForce(Const.AreaForce.FORCEA.value);
		player.soloManager.onEnterSoloArea();
		Map<String, Object> userData = Utils.ofMap("isAddRobot", false, "pro", player.getPro());
		Area area = AreaUtil.createAreaAndDispatch(player, Arrays.asList(player.getId()), player.getLogicServerId(), GlobalConfig.Solo_MapID, userData);
		if (!PlayerUtil.isOnline(player.getId())) {
			area.addPlayer(player);
			area.playerEnterRequest(player);
			player.getXmdsManager().playerReady(player.getId());
			area.onPlayerAutoBattle(player, true);
		}
		Out.debug(player.getName(), "-------------- enter1 --------------", area.instanceId);
		return area;// new AreaData(area.areaId, area.instanceId);
	}

	/**
	 * 进入指定创建成功的单挑场景
	 * 
	 * @param player
	 * @param areaData
	 */
	private static void enterSoloArea(WNPlayer player, Area area) {
		player.soloManager.onEnterSoloArea();
		if (area != null && !area.isClose()) {
			player.setForce(Const.AreaForce.FORCEB.value);
			Out.debug(player.getName(), "-------------- enter2 --------------", area.instanceId);
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
	}

	private MatchedTeam getTeam(String playerId) {
		for (MatchedTeam team : matchedQueue) {
			if (team.getPlayer(playerId) != null) {
				return team;
			}
		}
		return null;
	}

	/**
	 * 把对手的信息push给匹配成功的玩家
	 * 
	 * @param me
	 * @param vsPlayer
	 */
	public static void pushSoloMatched(WNPlayer me, WNPlayer vsPlayer, boolean isReconnect) {
		if (me == null || vsPlayer == null) {
			Out.error("Player null occoured in pushSoloMatched...");
			return;
		}
		me.soloManager.setBusy(true);// 一旦匹配成功就算设置为忙态
		me.dailyActivityMgr.onEvent(Const.DailyType.SOLO, "0", 1);
		SoloMatcher.getInstance().updateMatchingTime(me.soloManager.getMatchedTime());
		OnSoloMatchedPush.Builder msg = OnSoloMatchedPush.newBuilder();
		msg.setS2CCode(Const.CODE.OK);
		msg.setS2CVsPlayerName(vsPlayer.getName());
		msg.setS2CVsPlayerPro(vsPlayer.getPro());
		msg.setS2CVsPlayerLevel(vsPlayer.getLevel());
		List<Avatar> equipAvatars = PlayerUtil.getBattlerServerAvatar(vsPlayer, false);
		msg.addAllS2CVsPlayerAvatars(equipAvatars);
		msg.setS2CWaitResponseTimeSec(GlobalConfig.Solo_EnterTime);
		me.receive("area.soloPush.onSoloMatchedPush", msg.build());
	}
}
