package com.wanniu.game.team;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.wanniu.core.GGlobal;
import com.wanniu.core.game.JobFactory;
import com.wanniu.core.logfs.Out;
import com.wanniu.game.GWorld;
import com.wanniu.game.area.Area;
import com.wanniu.game.common.Const;
import com.wanniu.game.common.Const.TaskKind;
import com.wanniu.game.common.Const.TaskState;
import com.wanniu.game.common.Const.TaskType;
import com.wanniu.game.common.Utils;
import com.wanniu.game.common.msg.MessageUtil;
import com.wanniu.game.data.GameData;
import com.wanniu.game.data.ext.TeamTargetExt;
import com.wanniu.game.five2Five.Five2FiveService;
import com.wanniu.game.message.MessageData;
import com.wanniu.game.message.MessageData.MessageData_Data;
import com.wanniu.game.player.GlobalConfig;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.player.WNRobot;
import com.wanniu.game.player.po.AllBlobPO;
import com.wanniu.game.task.TaskData;
import com.wanniu.game.task.po.TaskPO;
import com.wanniu.game.team.TeamData.TeamMemberData;
import com.wanniu.game.util.RobotUtil;

/**
 * @author agui
 */
public class BaseTeamService {

	protected static final MatchMap AutoMathes = new MatchMap();

	protected static final Map<String, TeamData> TeamMap = new ConcurrentHashMap<>();

	protected static final Map<String, TeamMemberData> TeamMemberMap = new ConcurrentHashMap<>();

	static class MatchMap extends ConcurrentHashMap<String, MatchData> {
		private static final long serialVersionUID = 1L;

		private Map<String, MatchData> targets = new ConcurrentHashMap<>();

		@Override
		public MatchData put(String key, MatchData value) {
			targets.put(value.getKey(), value);
			return super.put(key, value);
		}

		@Override
		public MatchData remove(Object key) {
			MatchData match = super.remove(key);
			if (match != null) {
				targets.remove(match.getKey());
			}
			return match;
		}

		public MatchData getTargetMatch(int logicServerId, int targetId, int difficulty) {
			return targets.get(getTargetKey(logicServerId, targetId, difficulty));
		}

		public void clear() {
			super.clear();
			targets.clear();
		}

	}

	static class MatchData {
		WNPlayer player;

		int targetId;

		int difficulty;

		MatchData(WNPlayer player, int targetId, int difficulty) {
			this.player = player;
			this.targetId = targetId;
			this.difficulty = difficulty;
		}

		String getKey() {
			return getTargetKey(GWorld.__SERVER_ID, targetId, difficulty);
		}
	}

	private static String getTargetKey(int logicServerId, int targetId, int difficulty) {
		return logicServerId + ":" + targetId + ":" + difficulty;
	}

	static {
		long minute = 5 * GGlobal.TIME_MINUTE;
		JobFactory.addScheduleJob(new Runnable() {
			@Override
			public void run() {
				clearExpiredTeam();
			}
		}, minute, minute);
		long second = GGlobal.TIME_SECOND * 3;
		JobFactory.addScheduleJob(new Runnable() {
			@Override
			public void run() {
				if (AutoMathes.isEmpty())
					return;
				Out.debug("auto match team...");
				for (Map.Entry<String, MatchData> entry : AutoMathes.entrySet()) {
					matchTeam(entry.getValue());
				}
			}

		}, second, second);
		second = GGlobal.TIME_SECOND * 1;
		JobFactory.addScheduleJob(new Runnable() {
			@Override
			public void run() {
				GWorld.APP_TIME = System.currentTimeMillis();
				for (TeamData team : TeamMap.values()) {
					if (!team.isOpenJoin())
						continue;
					if (team.isRobotJoin) {
						if (team.isRobotLeader) {
							team.changeLeader();
						}
						if (GWorld.APP_TIME > team.robotFreeTime && team.canRobotLeave()) {
							try {
								for (TeamMemberData member : team.teamMembers.values()) {
									if (!member.robot)
										continue;
									WNRobot robot = (WNRobot) member.getPlayer();
									if (robot != null && robot.quitTeamFuture == null) {
										robot.quitTeamFuture = JobFactory.addDelayJob(() -> {
											robot.quitTeamFuture = null;
											TeamService.leaveTeam(robot);
										}, Utils.getSecMills(1, 10));
									}
								}
							} catch (Exception e) {
								Out.error(e);
							}
							team.setFreeRobotTime();
						}
					}

					TeamTargetExt teamTargetProp = GameData.TeamTargets.get(team.targetId);
					if (teamTargetProp == null) {
						continue;
					}
					// 不需要机器人
					if (teamTargetProp.isRobot == 0) {
						continue;
					}

					if (team.isAutoTeam && GWorld.APP_TIME > team.robotJoinTime && team.memberCount() < 3) {
						try {
							WNPlayer leader = team.getPlayer(team.leaderId);
							if (GWorld.ROBOTS && leader != null) {
								Area area = leader.getArea();
								if (area.isFull())
									continue;
								AllBlobPO allBlob = RobotUtil.matchRobot(leader, Math.max(area.prop.reqLevel, team.minLevel));
								if (allBlob == null)
									continue;

								// 计算额外添加的属性...
								allBlob.robotAttr = teamTargetProp.randomAttrs();
								WNRobot robot = new WNRobot(allBlob);
								try {
									RobotUtil.newRobot(robot);
									leader.getArea().addPlayer(robot);

									String result = TeamService.joinTeam(team.id, robot);
									if (result != null) {// 不正常加入队伍...
										Out.warn("机器人入队，不正常。", result);
										// 主动回收
										robot.free();
									}
								} catch (Exception e) {
									Out.error("创建机器人，进入场景失败", e);
									robot.free();
								}
							}
						} catch (Exception e) {
							Out.error(e);
						}
					}
				}
			}

		}, second, second);

		clearExpiredMessage();
	}

	protected static Object lock = new Object();

	protected static boolean matchTeam(MatchData match) {
		WNPlayer player = match.player;
		for (TeamData team : TeamMap.values()) {
			if (team.isAutoTeam && team.local && team.targetId == match.targetId && team.difficulty == match.difficulty && team.memberCount() < GlobalConfig.NormalTeamMaxNum) {
				if (team.minLevel <= player.getLevel() && (team.maxLevel == 0 || player.getLevel() <= team.maxLevel)) {
					TeamService.joinTeam(team.id, player);
					return true;
				}
			}
		}
		return false;
	}

	public static void reload() {
		AutoMathes.clear();
		TeamMap.clear();
		TeamMemberMap.clear();
	}

	public static void clearExpiredMessage() {
		long now = GWorld.APP_TIME;
		long minValidtime = now + GGlobal.TIME_MINUTE;
		for (TeamData team : TeamMap.values()) {
			for (Map.Entry<String, Long> entry : team.invites.entrySet()) {
				if (now > entry.getValue()) {
					team.invites.remove(entry.getKey());
					Out.debug("remove team invite key : ", entry.getKey());
				} else {
					minValidtime = Math.min(minValidtime, entry.getValue());
				}
			}
			for (Map.Entry<String, Long> entry : team.applies.entrySet()) {
				if (now > entry.getValue()) {
					team.removeApply(entry.getKey());
					Out.debug("remove team applies key : ", entry.getKey());
				} else {
					minValidtime = Math.min(minValidtime, entry.getValue());
				}
			}
		}
		minValidtime = Math.min(minValidtime - now, GGlobal.TIME_MINUTE);
		JobFactory.addDelayJob(() -> {
			clearExpiredMessage();
		}, minValidtime);
	}

	/**
	 * 定时清除过期组队信息
	 */
	public static void clearExpiredTeam() {
		long now = GWorld.APP_TIME;
		long validTime = Math.max(GGlobal.TIME_HOUR / 2, GlobalConfig.TeamOfflineMaxTime * GGlobal.TIME_HOUR);
		for (TeamData team : TeamMap.values()) {
			boolean flag = true;
			for (TeamMemberData teamMember : team.teamMembers.values()) {
				if (teamMember.isOnline() || now - teamMember.joinTime < validTime) {
					flag = false;
					break;
				}
			}
			if (flag || team.isAllRobotOnline()) {
				Out.warn("clearPastTeam : ", team.id);
				destroyTeam(team);
			}
			// else {
			// if (team.getMember(team.leaderId) == null) {
			// Out.warn("队长数据错误自动修正...");
			// team.changeLeader();
			// }
			// }
		}
	}

	public static boolean isInTeam(String playerId) {
		return TeamMemberMap.containsKey(playerId);
	}

	public static boolean isInTeam(String teamId, String playerId) {
		TeamMemberData tmData = TeamMemberMap.get(playerId);
		return tmData != null && tmData.teamId.equals(teamId);
	}

	public static TeamMemberData getTeamMember(String playerId) {
		return TeamMemberMap.get(playerId);
	}

	public static void addTeamMember(TeamMemberData teamMember) {
		AutoMathes.remove(teamMember.id);
		TeamMemberMap.put(teamMember.id, teamMember);
		WNPlayer player = teamMember.getPlayer();
		// player.soloManager.quitMatching(false);
		// 队长身上有一条龙任务, 队员加入，共享一条龙任务
		TeamData team = teamMember.getTeam();
		// 处理5v5匹配队列变化
		if (!team.leaderId.equals(teamMember.id)) {
			Five2FiveService.getInstance().processTeamChangeOnFive2Five(teamMember.getTeam().leaderId);
		}
		if (team.isInLoopTask()) {
			if (player != null) {
				for (TaskPO task : team.loopTasks.values()) {
					TaskData taskData = new TaskData(task);
					player.taskManager.pushTaskUpdate(taskData, team);
					player.taskManager.questStatusChangeR2B(player, taskData, task.templateId);

					// 完成接取皓月镜任务
					player.taskManager.dealTaskEvent(TaskType.ACCEPT_DAILY_LOOP, String.valueOf(TaskKind.LOOP), 1);
				}
			}
		}

		team.setRobotTime();

		// 玩家加入队伍则自动从单人匹配中退出
		Five2FiveService.getInstance().singleQuitFive2FiveMatch(teamMember.id, false);

		if (!teamMember.robot) {
			for (TeamData teamData : TeamMap.values()) {
				teamData.invites.remove(teamMember.id);
				teamData.removeApply(teamMember.id);
			}
		}
	}

	public static void removeTeamMember(String playerId) {
		TeamMemberData teamMember = TeamMemberMap.remove(playerId);
		if (teamMember != null) {
			TeamData team = teamMember.getTeam();
			WNPlayer player = teamMember.getPlayer();
			if (player != null && player instanceof WNRobot) {
				player.free();
			}
			if (player != null && team != null) {
				if (team.loopTasks != null) {
					// 通知玩家 移除一条龙
					for (TaskPO db : team.loopTasks.values()) {
						TaskData taskData = new TaskData(db);
						TaskData task = taskData.clone();
						task.db.state = TaskState.DELETE.getValue();
						player.taskManager.pushTaskUpdate(task);
					}

					// 一条龙任务中,告诉队长 队员不足
					if (team.memberCount() < Const.LOOP_TASK_TEAM_MEMBER_COUNT && team.memberCount() > 0 && !team.isAutoTeam) {
						for (TeamMemberData member : team.teamMembers.values()) {
							if (member.isLeader) {
								MessageData_Data data = new MessageData_Data();
								MessageData message = MessageUtil.createMessage(Const.MESSAGE_TYPE.loop_task_member_leave.getValue(), member.id, data);
								message.id = member.id;
								MessageUtil.sendMessageToPlayer(message, member.id);
							}
						}
					}
				}
				team.setRobotTime();
			}
			TeamUtil.removeAcrossMatch(player);
			// 队长退出时所有人都退出
			if (playerId.equals(teamMember.getTeam().leaderId)) {
				WNPlayer leader = teamMember.getPlayer();
				if (leader != null) {
					leader.five2FiveManager.cancelFive2FiveMatch(false);
				}
			} else {
				// 处理5v5匹配队列变化
				Five2FiveService.getInstance().processTeamChangeOnFive2Five(teamMember.getTeam().leaderId);
			}
		}
	}

	public static Map<String, TeamMemberData> getTeamMembers(String teamId) {
		TeamData team = getTeam(teamId);
		if (team != null) {
			return team.teamMembers;
		}
		return null;
	}

	public static TeamData getTeam(String teamId) {
		return TeamMap.get(teamId);
	}

	public static void addTeam(TeamData team) {
		TeamMap.put(team.id, team);
	}

	public static void destroyTeam(TeamData team) {
		synchronized (lock) {
			String teamId = team.id;
			Out.debug("destory team - ", teamId);
			for (String memberId : team.teamMembers.keySet()) {
				removeTeamMember(memberId);
				TeamService.clearTeamData(memberId);
			}

			TeamMap.remove(teamId);
			team.teamMembers.clear();
		}
	}

}
