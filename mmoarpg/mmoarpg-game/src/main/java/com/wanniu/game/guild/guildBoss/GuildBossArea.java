package com.wanniu.game.guild.guildBoss;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wanniu.core.game.JobFactory;
import com.wanniu.core.logfs.Out;
import com.wanniu.game.area.Area;
import com.wanniu.game.area.DamageHealVO;
import com.wanniu.game.common.Const;
import com.wanniu.game.common.Const.EventType;
import com.wanniu.game.common.msg.MessagePush;
import com.wanniu.game.data.ext.InspireLevelExt;
import com.wanniu.game.guild.GuildService;
import com.wanniu.game.monster.GuildBossRatioConfig;
import com.wanniu.game.player.GlobalConfig;
import com.wanniu.game.player.PlayerUtil;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.poes.GuildBossPo;
import com.wanniu.game.poes.GuildPO;
import com.wanniu.game.sevengoal.SevenGoalManager.SevenGoalTaskType;
import com.wanniu.game.task.TaskEvent;
import com.wanniu.game.task.TaskQueue;

import pomelo.area.GuildBossHandler.OnInspireChangePush;
import pomelo.area.GuildBossHandler.OnQuitGuildBossPush;

public class GuildBossArea extends Area {
	private GuildPO guildPo;
	private ScheduledFuture<?> future;
	private boolean isKilled = false;
	private boolean stopHurtAdd = false;// 这个值是因为BOSS在死后还会推送伤害过来的问题,在BOSS死后会延迟两秒最后再统计下伤害，要做弹框，两秒已经极限了
	private long killTime;
	private long outTime = 0;
	private Map<String, Integer> playerEnterCount = new ConcurrentHashMap<>();
	private Set<String> onLines = new HashSet<>();// 在该场景角色ID,

	public GuildBossArea(JSONObject opts, GuildPO guildPo) {
		super(opts);
		initStatisticsJob();
		if (this.guildPo == null && guildPo != null) {
			this.guildPo = guildPo;
		}
		outTime = calEndTime();
	}

	/**
	 * 获取截止日期
	 * 
	 * @return
	 */
	private long calEndTime() {
		return System.currentTimeMillis() + GuildBossService.getInstance().getEndSeconds() * 1000;
	}

	public void preOverProcess() {
		pushDetailInfo();
		removeBoss();
	}

	public void removeBoss() {
		this.getXmdsManager().removeUnit(instanceId, GuildBossService.GUILDBOSS_BOSS_ID);
	}

	/**
	 * 推送伤害和排名(这里因为伤害会延迟推送,所以加个延迟定时器)
	 */
	public void pushDetailInfo() {
		JobFactory.addDelayJob(() -> {
			processPushDetailInfo();
		}, 2000);
	}

	public void processPushDetailInfo() {
		stopHurtAdd = true;// 停止伤害推送
		this.cancelSchdule();// 这时候即使在排序也没什么意义了
		Collection<String> roleIds = (Collection<String>) this.actors.keySet();
		TaskQueue.put(new TaskEvent(EventType.over_statics_ranks, guildPo.id, roleIds));
	}

	/**
	 * 游戏结束处理(场景销毁前会延迟5秒)
	 */
	public void onGameOver() {
		killTime = System.currentTimeMillis() + 5000;
		statistics(killTime, false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.wanniu.game.area.Area#onBattleReport(java.util.List)
	 */
	public void onBattleReport(List<DamageHealVO> datas) {
		if (datas != null && !datas.isEmpty()) {
			for (DamageHealVO heal : datas) {
				if (heal.TotalDamage <= 0) {
					continue;
				}
				setNewData(heal.PlayerUUID, heal.TotalDamage);
			}
		}
	}

	@Override
	protected void addCloseFuture(int second) {
		super.addCloseFuture(second);
	}

	@Override
	public void dispose() {
		super.dispose();
		cancelSchdule();
	}

	@Override
	public void onMonsterDead(int monsterId, int level, float x, float y, int attackType, String refreshPoint, WNPlayer player, JSONArray teamSharedIdList, JSONArray atkAssistantList) {
		super.onMonsterDead(monsterId, level, x, y, attackType, refreshPoint, player, teamSharedIdList, atkAssistantList);
		if (monsterId == GuildBossService.GUILDBOSS_BOSS_ID) {
			onBossDead();
		}
	}

	/**
	 * 收到个人鼓舞请求
	 * 
	 * @param index
	 * @param guildBossPO
	 */
	public int receiveSinlgInspire(WNPlayer player, GuildBossPo guildBossPO, boolean isAttributeChanged) {
		int totalAdd = getInpireAdd(GuildBossConstant.SINGLE_INPIRE, guildBossPO.inspire.count);
		OnInspireChangePush.Builder msg = getOnInspireChangePush(guildBossPO.inspire.count, totalAdd / 100, GuildBossConstant.SINGLE_INPIRE);
		player.receive("area.guildBossPush.onInspireChangePush", msg.build());
		if (totalAdd > 0 && isAttributeChanged) {
			player.btlDataManager.onGuildBossInpire();
			player.refreshBattlerServerEffect(false);
		}
		return totalAdd;
	}

	public int getInpireAdd(int index, int count) {
		InspireLevelExt co = GuildBossRatioConfig.getInspireLevelCO(index, count);
		return co != null ? co.totalInspirePlus : 0;
	}

	/**
	 * 收到工会鼓舞请求
	 * 
	 * @param index
	 */
	public int receiveGuildInspire(int index, int count) {
		int totalAdd = getInpireAdd(index, count);
		OnInspireChangePush.Builder msg = getOnInspireChangePush(count, totalAdd / 100, index);
		Collection<String> roleIds = (Collection<String>) this.actors.keySet();
		for (String playerId : roleIds) {
			WNPlayer player = PlayerUtil.getOnlinePlayer(playerId);
			player.receive("area.guildBossPush.onInspireChangePush", msg.build());
			if (totalAdd > 0) {
				player.btlDataManager.onGuildBossInpire();
				player.refreshBattlerServerEffect(false);
			}
		}
		return totalAdd;
	}

	/**
	 * 收到工会鼓舞攻击请求
	 * 
	 * @param index
	 */
	public int receiveGuildInspireAtk(WNPlayer player) {
		int totalAdd = getInpireAdd(GuildBossConstant.GUILD_INPIRE, guildPo.inspire.count);
		OnInspireChangePush.Builder msg = getOnInspireChangePush(guildPo.inspire.count, totalAdd / 100, GuildBossConstant.GUILD_INPIRE);
		player.receive("area.guildBossPush.onInspireChangePush", msg.build());
		return totalAdd;
	}

	/**
	 * 收到工会鼓舞防御请求
	 * 
	 * @param index
	 */
	public int receiveGuildInspireDef(WNPlayer player) {
		int totalAdd = getInpireAdd(GuildBossConstant.GUILD_DEF_INPIRE, guildPo.defInspire.count);
		OnInspireChangePush.Builder msg = getOnInspireChangePush(guildPo.defInspire.count, totalAdd / 100, GuildBossConstant.GUILD_DEF_INPIRE);
		player.receive("area.guildBossPush.onInspireChangePush", msg.build());
		return totalAdd;
	}

	/**
	 * 从场景里获取总的鼓舞加成
	 * 
	 * @param player
	 * @return
	 */
	public int getTotalAtkAdd(WNPlayer player) {
		int totalAdd = 0;
		GuildBossPo guildBossPO = player.guildBossManager.getAndCheckUpdateGuildBossPo(player.player.id);
		totalAdd += getInpireAdd(GuildBossConstant.SINGLE_INPIRE, guildBossPO.inspire.count);
		if (guildPo != null && guildPo.inspire != null) {
			totalAdd += getInpireAdd(GuildBossConstant.GUILD_INPIRE, guildPo.inspire.count);
		}
		return totalAdd;
	}

	/**
	 * 从场景里获取总的防御鼓舞加成
	 * 
	 * @param player
	 * @return
	 */
	public int getTotalDefAdd(WNPlayer player) {
		int totalAdd = 0;
		if (guildPo != null && guildPo.defInspire != null) {
			totalAdd += getInpireAdd(GuildBossConstant.GUILD_DEF_INPIRE, guildPo.defInspire.count);
		}
		return totalAdd;
	}

	public void receiveInspireAfterEnter(WNPlayer player) {
		GuildBossPo singleSprie = player.guildBossManager.getAndCheckUpdateGuildBossPo(player.player.id);
		int add = 0;
		add += receiveSinlgInspire(player, singleSprie, false);
		GuildPO guildPO = player.guildManager.guild;
		player.guildBossManager.getAndCheckGuildBossAtkPoForGuild(guildPO);// 攻击加成
		player.guildBossManager.getAndCheckGuildBossDefPoForGuild(guildPO);// 防御加成
		add += receiveGuildInspireAtk(player);
		add += receiveGuildInspireDef(player);
		if (add > 0) {
			player.btlDataManager.onGuildBossInpire();
			player.refreshBattlerServerEffect(false);
		}
	}

	public void onPlayerLeave(WNPlayer player) {
		int totalAdd = 0;
		GuildBossPo guildBossPO = player.guildBossManager.getAndCheckUpdateGuildBossPo(player.player.id);
		totalAdd += getInpireAdd(GuildBossConstant.SINGLE_INPIRE, guildBossPO.inspire.count);
		if (guildPo != null && guildPo.inspire != null) {
			totalAdd += getInpireAdd(GuildBossConstant.GUILD_INPIRE, guildPo.inspire.count);
		}
		if (totalAdd > 0) {// 没有加成的话都不用管了
			player.btlDataManager.onGuildBossInpire();
			player.refreshBattlerServerEffect(false);
		}
	}

	private OnInspireChangePush.Builder getOnInspireChangePush(int personalTimes, int personalValue, int index) {
		OnInspireChangePush.Builder bd = OnInspireChangePush.newBuilder();
		bd.setTotalTimes(personalTimes);
		bd.setTotalValue(personalValue);
		bd.setIndex(index);
		bd.setS2CCode(Const.CODE.OK);
		return bd;
	}

	public void onBossDead() {
		Out.info("工会BOSS活动。。怪被杀了,guildId=", guildPo.id);
		isKilled = true;
		killTime = System.currentTimeMillis();
		GuildBossService.getInstance().upgradeGuildBoss();
		statistics(killTime, true);// 死的时候再统计下最后的伤害
		pushDetailInfo();// 给场景内玩家推送伤害和排名
		this.addCloseFuture(GlobalConfig.GuildBoss_LeaveTime - 3);
	}

	/**
	 * 取消排行榜定时器
	 */
	private void cancelSchdule() {
		if (this.future != null) {
			this.future.cancel(false);
			this.future = null;
		}
	}

	public boolean isKilled() {
		return isKilled;
	}

	/**
	 * 推送退出时间
	 */
	private void pushKickTime(WNPlayer player) {
		if (outTime == 0) {
			return;
		}
		long now = System.currentTimeMillis();
		int seconds = 1;
		if (now < outTime) {
			seconds = (int) ((outTime - now) / 1000);
		}
		seconds = seconds <= 0 ? 1 : seconds;
		MessagePush push = new MessagePush("area.guildBossPush.onQuitGuildBossPush", OnQuitGuildBossPush.newBuilder().setEndSeconds(seconds).build());
		player.receive(push);
	}

	@Override
	public void playerLeaveRequest(WNPlayer player, boolean keepObject) {
		super.playerLeaveRequest(player, keepObject);
		if (!keepObject) {// 下线还在打。。。
			synchronized (this) {
				onLines.remove(player.getId());
			}
		}

	}

	@Override
	public void onPlayerLeaved(WNPlayer player) {
		super.onPlayerLeaved(player);
		onPlayerLeave(player);
	}

	@Override
	public void onPlayerEntered(WNPlayer player) {
		super.onPlayerEntered(player);
		synchronized (this) {
			if (onLines.add(player.getId())) {
				Integer count = playerEnterCount.get(player.player.id);
				if (count == null) {
					playerEnterCount.put(player.player.id, 1);
				} else {
					playerEnterCount.put(player.player.id, count + 1);
				}
			}
		}
		player.sevenGoalManager.processGoal(SevenGoalTaskType.GUILD_BOSS_COUNT);
	}

	public int getEnterCount(String playerId) {
		Integer count = playerEnterCount.get(playerId);
		if (count == null) {
			return 0;
		} else {
			return count;
		}
	}

	/**
	 * 伤害统计定时器
	 */
	private void initStatisticsJob() {
		future = JobFactory.addScheduleJob(new Runnable() {
			@Override
			public void run() {
				statistics(0, false);
			}
		}, 1000, 1000);
	}

	@Override
	public void onReady(WNPlayer player) {
		String guildId = GuildService.getGuildId(player.player.id);
		if (guildId != null) {
			List<String> pushRoleIds = new ArrayList<>();
			pushRoleIds.add(player.getId());
			Collection<String> roleIds = (Collection<String>) this.actors.keySet();
			player.onEvent(new TaskEvent(EventType.hurtRank_sort, guildId, roleIds, true, 0l, false));
			receiveInspireAfterEnter(player);
			pushKickTime(player);
		} else {
			Out.warn("发现有个玩家在工会BOSS场景没有工会ID,playerId=", player.player.id);
		}
	}

	/**
	 * 统计伤害
	 */
	private void statistics(long bossKillTime, boolean hasKilled) {
		if (guildPo == null) {
			Out.warn("发现有个玩家在工会BOSS场景没有工会ID,instanceId=", this.instanceId);
			return;
		}
		Collection<String> roleIds = (Collection<String>) this.actors.keySet();
		TaskQueue.put(new TaskEvent(EventType.hurtRank_sort, guildPo.id, roleIds, false, bossKillTime, hasKilled));
	}

	public void setNewData(String playerId, long hurt) {
		if (guildPo == null) {
			Out.warn("发现有个玩家在工会BOSS场景没有工会ID,instanceId=", this.instanceId);
			return;
		}
		if (stopHurtAdd) {
			// Out.warn("发现有个玩家在工会BOSS场景停止伤害计数的时候还会有伤害推送？,instanceId=", this.instanceId);
			return;
		}
		int enterCount = this.getEnterCount(playerId);
		TaskQueue.put(new TaskEvent(EventType.hurtRank, guildPo.id, playerId, hurt, enterCount));
	}

}
