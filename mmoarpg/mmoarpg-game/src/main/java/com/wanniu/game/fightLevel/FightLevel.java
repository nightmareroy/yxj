package com.wanniu.game.fightLevel;

import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

import com.alibaba.fastjson.JSONObject;
import com.wanniu.core.game.JobFactory;
import com.wanniu.core.game.LangService;
import com.wanniu.core.logfs.Out;
import com.wanniu.game.GWorld;
import com.wanniu.game.area.Area;
import com.wanniu.game.area.AreaEvent.MonsterData;
import com.wanniu.game.area.DamageHealVO;
import com.wanniu.game.common.Const;
import com.wanniu.game.common.Const.SCENE_TYPE;
import com.wanniu.game.common.Const.TaskType;
import com.wanniu.game.common.msg.MessagePush;
import com.wanniu.game.fightLevel.po.OutputStatistics;
import com.wanniu.game.player.BILogService;
import com.wanniu.game.player.GlobalConfig;
import com.wanniu.game.player.PlayerUtil;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.task.TaskEvent;
import com.wanniu.game.task.TaskUtils;
import com.wanniu.game.team.TeamData;
import com.wanniu.game.team.TeamData.TeamMemberData;

import pomelo.area.FightLevelHandler.OnFubenClosePush;
import pomelo.area.TeamHandler.MumberHurtInfo;
import pomelo.area.TeamHandler.OnTeamMumberHurtPush;

/**
 * @author agui
 */
public class FightLevel extends Area {
	/**
	 * 收益等级:相关30级没有收益.
	 */
	public static final int NEED_PRODUCE_LEVEL = 30;
	
	public int hard;
	private int needProduce;
	private SCENE_TYPE sceneType;
	private ScheduledFuture<?> jobFuture;
	private final ConcurrentHashMap<String, OutputStatistics> output = new ConcurrentHashMap<>();

	public FightLevel(JSONObject opts, SCENE_TYPE sceneType) {
		super(opts);
		this.hard = prop.hardModel;
		this.sceneType = sceneType;

		// 启动一个定时输出的任务
		jobFuture = JobFactory.addFixedRateJob(() -> GWorld.getInstance().ansycExec(() -> output()), 5000, 3000);
	}

	public void output() {
		if (output.isEmpty()) {
			return;
		}

		if (actors.isEmpty()) {
			return;
		}

		OnTeamMumberHurtPush.Builder push = OnTeamMumberHurtPush.newBuilder();
		for (OutputStatistics out : output.values()) {
			MumberHurtInfo.Builder m = MumberHurtInfo.newBuilder();
			m.setPlayerName(out.getName());
			m.setHurt(out.getHurt());
			m.setCure(out.getCure());
			push.addPlayers(m);
		}

		OnTeamMumberHurtPush result = push.build();
		for (Entry<String, Actor> actorEntry : this.actors.entrySet()) {
			String playerId = actorEntry.getKey();
			WNPlayer player = this.getPlayer(playerId);
			if (player == null) {
				continue;
			}
			player.receive("area.teamPush.onTeamMumberHurtPush", result);
		}
		Out.debug("副本输出.... areaId=", areaId);
	}

	@Override
	public void bindBattleServer(WNPlayer player) {
		super.bindBattleServer(player);
		TeamData team = player.getTeamManager().getTeam();
		if (team != null) {
			WNPlayer member = null;
			for (TeamMemberData teamMember : team.teamMembers.values()) {
				member = teamMember.getPlayer();
				if (member == null) {
					continue;
				}
				if (needProduce(member)) {
					needProduce += 1;
				}
			}
		} else {
			if (needProduce(player)) {
				needProduce = 1;
			}
		}
	}

	protected void onDailyActivity(WNPlayer player) {
		super.onDailyActivity(player);

		// if (prop.allowedPlayersMix > 1) { // 多人副本活跃
		// player.dailyActivityMgr.onEvent(Const.DailyType.DUNGEON, "0", 1);
		// }
		if (sceneType == SCENE_TYPE.FIGHT_LEVEL) {
			player.dailyActivityMgr.onEvent(Const.DailyType.DUNGEON, "0", 1);
		}
	}

	public int reliveNum(String playerid) {
		Out.debug(getClass().getName(), "fightLevel reviveNum:", this.actors);
		Actor actor = this.actors.get(playerid);
		if (actor == null) {
			// 此角色不存在
			Out.error(getClass().getName(), "fightLevel revive playerId not exist:", playerid);
			return 0;
		}

		int configNum = this.prop.revival;
		if (configNum <= 0) {
			return configNum;
		} else {
			if (configNum <= actor.rebornNum) {
				return 0;
			} else {
				return configNum - actor.rebornNum;
			}
		}
	}

	public void onGameOver(JSONObject event) {
		if (jobFuture != null) {
			jobFuture.cancel(false);
		}

		int winForce = event.getIntValue("winForce");
		boolean isWin = winForce == 2; // 通关
		MessagePush push = new MessagePush("area.fightLevelPush.onFubenClosePush", OnFubenClosePush.newBuilder().setS2CMsg(LangService.getValue("DUNGEON_OVER_MESSAGE")).setS2COverTime(prop.timeCount).build());

		// TeamData team = null;
		WNPlayer player = null;
		for (Entry<String, Actor> actorEntry : this.actors.entrySet()) {
			String playerId = actorEntry.getKey();
			Actor actor = actorEntry.getValue();
			player = this.getPlayer(playerId);
			if (player == null)
				continue;
			if (isWin) {
				player.finishFightLevel(this.hard, prop.templateID);
				player.onEvent(new TaskEvent(TaskType.FINISH_CLONESCENE, String.valueOf(this.areaId), 1));

				// 增加仙缘值
				if (this.prop.dungeonTab == 1 && player != null) {// 仅限这些副本会有仙缘产出
					Collection<String> teamMembers = player.getTeamMembers();
					if (teamMembers != null) {
						WNPlayer member = PlayerUtil.getOnlinePlayer(playerId);
						int lvl = member.getLevel();
						for (String teamMemPlayerId : teamMembers) {
							if (this.actors.containsKey(teamMemPlayerId)) {
								WNPlayer teamMember = PlayerUtil.getOnlinePlayer(teamMemPlayerId);
								if (teamMember == null)
									continue;
								int tempLvl = teamMember.getLevel();
								// 大于其他人10级或者自己没收益其他人有收益
								if (lvl >= tempLvl + 5 || (!actor.profitable && getActor(teamMemPlayerId).profitable)) {
									player.processXianYuanGet(GlobalConfig.Fate_Dungeon);
									break;
								}
							}
						}
					}
				}

				if (prop.type == SCENE_TYPE.FIGHT_LEVEL.getValue()) {
					player.onEvent(new TaskEvent(TaskType.FINISH_DUNGEONS_COUNT, String.valueOf(this.areaId), 1));
				}

			}
			// 成就
			player.achievementManager.onPassedDungeon(this.areaId);

			player.receive(push);

			BILogService.getInstance().ansycReportFightLevel(player.getPlayer(), isWin ? 1 : 0, this.areaId, this.instanceId);
		}
		if (isWin) {
			// ===========扣除副本次数
			TeamData team = player.getTeamManager().getTeam();
			if (team != null) {
				WNPlayer member = null;
				for (TeamMemberData teamMember : team.teamMembers.values()) {
					member = teamMember.getPlayer();
					if (member == null) {
						continue;
					}
					member.fightLevelManager.useProduce(prop.templateID);
					member.fightLevelManager.onDungeonReset(this.areaId);// 副本通关要重置boss掉落计数
				}
			} else {
				player.fightLevelManager.useProduce(prop.templateID);
				player.fightLevelManager.onDungeonReset(this.areaId);// 副本通关要重置boss掉落计数
			}
		}
	}

	@Override
	protected void onDisponseLeave(WNPlayer player) {
		Out.debug("-------------------dispose----", player.getName());
		TeamMemberData teamMember = player.getTeamManager().getTeamMember();
		if (teamMember == null || teamMember.isLeader || !teamMember.follow) {
			Out.debug(player.getName(), " leave fight level!!!!!!!!!!!!!!!!!!");
			player.fightLevelManager.leaveDungeon(player, this);
		}
	}

	@Override
	public boolean isUseTC() {
		return needProduce > 0;
	}

	public void onPlayerDeadByMonster(WNPlayer player, MonsterData monsterData) {
		String playerId = player.getId();

		Actor actor = this.actors.get(playerId);
		if (actor == null && !player.isProxy()) {
			Out.error("onPlayerDead not exist! :", playerId);
			return;
		}

		pushRelive(player);

	}

	private boolean needProduce(WNPlayer player) {
		if (prop.type == SCENE_TYPE.LOOP.getValue()) {
			// return true;
			return TaskUtils.profitableLoop(player);
		}
		return player.fightLevelManager.needProduce(prop.templateID) && (player.getLevel() < prop.reqLevel + NEED_PRODUCE_LEVEL);
	}

	@Override
	public void onPlayerDeadByPlayer(WNPlayer deadPlayer, WNPlayer hitPlayer, float x, float y) {
		Out.warn("副本中出现玩家击杀玩家！");
	}

	public void addPlayer(WNPlayer player) {
		super.addPlayer(player);
		Actor actor = getActor(player.getId());
		if (actor != null) {
			actor.profitable = needProduce(player);
		}

	}

	public AreaItem onPickItem(String playerId, String itemId, boolean isGuard) {
		return isUseTC() ? super.onPickItem(playerId, itemId, isGuard) : null;
	}

	public ReliveType getReliveType() {
		return this.prop.type == SCENE_TYPE.LOOP.getValue() ? ReliveType.NOW : ReliveType.BORN;
	}

	@Override
	public void dispose(boolean processExit) {
		super.dispose(processExit);
		if (jobFuture != null) {
			jobFuture.cancel(false);
		}
	}

	@Override
	public void onBattleReport(List<DamageHealVO> datas) {
		// [{"PlayerUUID":"f9d3662d-7a95-4793-9fea-1131bdd9c366","TotalDamage":3617348,"TotalHealing":70902}]
		for (DamageHealVO damage : datas) {
			OutputStatistics out = output.get(damage.PlayerUUID);
			if (out == null) {
				WNPlayer player = PlayerUtil.getOnlinePlayer(damage.PlayerUUID);
				if (player != null) {
					out = new OutputStatistics();
					out.setId(damage.PlayerUUID);
					out.setName(player.getName());
					output.put(damage.PlayerUUID, out);
				}
			}

			if (out != null) {
				out.setHurt(damage.TotalDamage);
				out.setCure(damage.TotalHealing);
			}
		}
	}
}
