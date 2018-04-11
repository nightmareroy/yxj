package com.wanniu.game.team;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.wanniu.core.game.JobFactory;
import com.wanniu.core.game.LangService;
import com.wanniu.core.logfs.Out;
import com.wanniu.game.GWorld;
import com.wanniu.game.area.Area;
import com.wanniu.game.area.AreaData;
import com.wanniu.game.area.AreaUtil;
import com.wanniu.game.common.Const.SCENE_TYPE;
import com.wanniu.game.common.Utils;
import com.wanniu.game.guild.GuildUtil;
import com.wanniu.game.player.GlobalConfig;
import com.wanniu.game.player.PlayerUtil;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.player.WNRobot;
import com.wanniu.game.poes.PlayerPO;
import com.wanniu.game.team.TeamData.TeamMemberData;

import pomelo.area.TeamHandler.Team;
import pomelo.area.TeamHandler.TeamMemberBasic;

/**
 * @author agui
 */
public class TeamService extends BaseTeamService {

	private static AtomicLong UUID = new AtomicLong(System.nanoTime());

	/**
	 * 创建队伍 params : logicServerId是否为跨服队伍由此字段决定
	 */
	public static String createTeam(WNPlayer leader, List<String> memberIds, int logicServerId) {
		Out.debug("teamService createTeam");
		synchronized (lock) {
			boolean isInTeam = leader.getTeamManager().isInTeam();
			if (isInTeam) {
				return LangService.getValue("TEAM_ENTER");
			}

			if (memberIds != null)
				for (String playerId : memberIds) {
					boolean isIn = isInTeam(playerId);
					if (isIn) {
						return LangService.getValue("TEAM_ENTER");
					}
				}

			String teamId = String.valueOf(UUID.incrementAndGet());
			TeamMemberData teamLeader = new TeamMemberData(teamId, leader.getId());
			teamLeader.isLeader = true;
			TeamData team = new TeamData();
			team.id = teamId;
			team.leaderId = teamLeader.id;
			team.logicServerId = logicServerId;
			team.teamMembers = new ConcurrentHashMap<>();

			team.sceneType = leader.getSceneType();

			// 目标属性相关设定
			team.difficulty = 1;
			team.isAutoTeam = GlobalConfig.TeamAutoInvite == 1;

			addTeam(team);

			team.teamMembers.put(teamLeader.id, teamLeader);
			addTeamMember(teamLeader);
			if (memberIds != null) {
				for (String playerId : memberIds) {
					TeamMemberData member = new TeamMemberData(teamId, playerId);
					team.teamMembers.put(member.id, member);
					addTeamMember(member);
				}
			}
			refreshTeam(team);
		}
		return null;
	};

	public static void changeTeamArea(WNPlayer player) {
		TeamData team = player.getTeamManager().getTeam();
		if (team != null) {
			if (player.getId().equals(team.leaderId)) {
				team.sceneType = player.getSceneType();
			}
			player.teamManager.checkLogin();
			Area area = player.getArea();
			if (area.sceneType == SCENE_TYPE.FIGHT_LEVEL.getValue() || area.sceneType == SCENE_TYPE.LOOP.getValue() || area.sceneType == SCENE_TYPE.GUILD_BOSS.getValue()) {
				if (player.getTeamManager().getTeamMember() != null) {
					player.getTeamManager().getTeamMember().follow = false;
				}
				refreshTeam(team);
				area.onPlayerAutoBattle(player, true);
				Out.debug("自动战斗请求:", player.getName(), " - " + player.getId(), " - ", area.prop.name);
			}else {
				refreshTeam(team);
			}
			if (area.isNormal()) {
				team.setFreeRobotTime();
			}
			
		} else {
			player.teamManager.pushTeamData();
		}
	}

	/**
	 * 加入队伍
	 */
	public static String joinTeam(String teamId, WNPlayer wPlayer) {
		if (wPlayer == null) {
			return LangService.getValue("PLAYER_NOT_ONLINE");
		}
		TeamData team = getTeam(teamId);
		synchronized (lock) {
			if (team == null) {
				return LangService.getValue("TEAM_BREAK_UP");
			}
			if (!team.isOpenJoin()) {
				return LangService.getValue("TEAM_LOCKED");
			}
			String res = TeamUtil.canJoinTeam(wPlayer, team);
			if (res != null) {
				return res;
			}
			TeamMemberData member = wPlayer.getTeamManager().getTeamMember();
			if (member != null) {
				return LangService.getValue("TEAM_ALREADY_IN_TEAM");
			}

			member = new TeamMemberData(teamId, wPlayer.getId());
			WNPlayer leader = team.getPlayer(team.leaderId);
			member.follow = leader != null && leader.getInstanceId().equals(wPlayer.getInstanceId());
			member.robot = wPlayer.isRobot();
			team.isRobotJoin |= member.robot;

			team.teamMembers.put(member.id, member);
			addTeamMember(member);

		}

		kickRobotMember(team);

		refreshTeam(team);
		// 队伍成员已满情况处理
		TeamUtil.handleFullOfTeam(team, team.getPlayer(team.leaderId));
		return null;
	}

	public static void kickRobotMember(TeamData team) {
		if (team.memberCount() > 3) {
			for (Map.Entry<String, TeamMemberData> entry : team.teamMembers.entrySet()) {
				if (entry.getValue().robot) {
					WNRobot robot = GWorld.getRobot(entry.getKey());
					if (robot == null)
						continue;
					if (robot.quitTeamFuture != null)
						continue;
					robot.quitTeamFuture = JobFactory.addDelayJob(() -> {
						robot.quitTeamFuture = null;
						if (team.isOpenJoin()) {
							TeamService.leaveTeam(robot);
						}
					}, Utils.getSecMills(1, 15));
					break;
				}
			}
		}
	}

	/**
	 * 离开队伍
	 */
	public static String leaveTeam(WNPlayer player) {
		String playerId = player.getId();
		TeamData team = player.getTeamManager().getTeam();
		synchronized (lock) {
			TeamMemberData teamMember = player.getTeamManager().getTeamMember();
			if (teamMember != null) {
				team.teamMembers.remove(playerId);
				removeTeamMember(playerId);
				if (teamMember.isLeader && team.memberCount() > 0) {
					// 如果是队长退出，则自动将队长职位转让给最早加入队伍的人
					team.changeLeader();
					team.invites.clear();
					team.applies.clear();
				}
			}
		}

		if (team != null) {
			if (team.memberCount() == 0) {
				destroyTeam(team);
			} else if (!destoryRobotTeam(team)) {
				refreshTeam(team);
			}
		}

		clearTeamData(playerId);

		return null;
	}

	public static boolean destoryRobotTeam(TeamData team) {
		for (TeamMemberData member : team.teamMembers.values()) {
			if (!member.robot) {
				return false;
			}
		}
		destroyTeam(team);
		return true;
	}

	/**
	 * 踢出队伍
	 */
	public static boolean kickOutTeam(TeamData team, TeamMemberData teamMember) {
		if (teamMember != null && team != null) {
			String playerId = teamMember.id;
			WNPlayer player = PlayerUtil.getOnlinePlayer(playerId);
			synchronized (lock) {
				team.teamMembers.remove(playerId);
				if (player != null && player.getTeamManager().getTeamMember() != teamMember) {
					return true;
				}
				removeTeamMember(playerId);
			}
			clearTeamData(playerId);
			refreshTeam(team);
			return true;
		}
		return false;
	}

	/**
	 * 生成队伍数据
	 */
	public static Team.Builder generateTeamData(TeamData team) {
		Team.Builder data = Team.newBuilder();
		data.setId(team.id);
		data.setLeaderId(team.leaderId);

		List<TeamMemberBasic> teamMemberBasics = new ArrayList<>();
		Map<String, TeamMemberData> teamMembers = team.teamMembers;
		for (TeamMemberData teamMember : teamMembers.values()) {
			TeamMemberBasic.Builder memberBasic = TeamMemberBasic.newBuilder();
			memberBasic.setId(teamMember.id);
			PlayerPO member = teamMember.getPlayerData();
			memberBasic.setPro(member.pro);
			memberBasic.setName(member.name);
			memberBasic.setLevel(member.level);
			memberBasic.setUpLevel(0);
			memberBasic.setGuildName(GuildUtil.getGuildName(teamMember.id));
			teamMemberBasics.add(memberBasic.build());
		}
		data.addAllTeamMembers(teamMemberBasics);
		data.setMinLevel(team.minLevel);
		data.setMaxLevel(team.maxLevel);
		return data;
	}

	/**
	 * 通过过滤器筛选队伍
	 */
	public static List<TeamData> queryTeamByTarget(TeamFilter filter) {
		List<TeamData> result = new ArrayList<>();
		Collection<TeamData> teams = TeamMap.values();
		for (TeamData team : teams) {
			if (filter.filter(team)) {
				result.add(team);
			}
		}

		int maxViewCount = GlobalConfig.TeamViewMAX;
		if (result.size() > maxViewCount) {
			Collections.shuffle(result);
			return result.subList(0, maxViewCount);
		}

		return result;
	}

	/**
	 * 解散队伍
	 */
	public static String dissolveTeam(String playerId) {
		TeamMemberData teamMember = getTeamMember(playerId);
		// 只有队长才有权进行相关操作
		if (teamMember != null && teamMember.isLeader) {
			destroyTeam(teamMember.getTeam());
			return LangService.getValue("TEAM_NO_AUTHORITY");
		}
		return null;
	}

	public static void refreshTeam(TeamData team) {
		refreshTeam(team, true);
	}

	public static void refreshTeam(TeamData team, boolean notify) {
		if (team != null) {
			Set<String> teamMemberIds = team.teamMembers.keySet();
			for (String playerId : teamMemberIds) {
				updateTeamData(team, playerId, notify);
			}
		}
	}

	public static void clearTeamData(String playerId) {
		updateTeamData(null, playerId, true);
	}

	public static void updateTeamData(TeamData team, String playerId, boolean notify) {
		Out.debug("teamService sendTeam2TeamMembers:", playerId);
		WNPlayer player = PlayerUtil.getOnlinePlayer(playerId);
		if (player != null) {
			Map<String, TeamMemberData> teamMembers = null;
			if (team == null) {
				player.pkRuleManager.onExitTeam();
			} else {
				teamMembers = team.teamMembers;
			}
			player.getTeamManager().setTeamData(team);
			List<String> teamMemberIds = new ArrayList<>();
			JSONArray members = new JSONArray();
			if (teamMembers != null) {
				team.isRobotJoin = false;
				for (TeamMemberData teamMember : teamMembers.values()) {
					team.isRobotJoin |= teamMember.robot;
					if (!teamMember.id.equals(player.getId())) {
						teamMemberIds.add(teamMember.id);
					}
					members.add(Utils.toJSON("followLeader", teamMember.getFollow(), "uuid", teamMember.id));
				}
			}
			player.getTeamManager().pushTeamData();
			if (notify) {
				player.getTeamManager().onMemberChange(teamMemberIds);
				player.getXmdsManager().refreshPlayerTeamData(player.getId(), JSON.toJSONString(teamMemberIds));
				player.getXmdsManager().refreshTeamData(player.getId(), Utils.toJSONString("leaderId", team == null ? "" : team.leaderId, "members", members));
			}
		}
	}

	public static String followLeader(WNPlayer player, boolean follow) {
		if (player == null)
			return null;
		TeamMemberData member = player.getTeamManager().getTeamMember();
		if (member != null && member.follow != follow) {
			TeamData team = player.getTeamManager().getTeam();
			if (follow) {
				WNPlayer leader = PlayerUtil.getOnlinePlayer(team.leaderId);
				if (leader != null && !leader.getInstanceId().equals(player.getInstanceId())) {
					Area area = leader.getArea();
					if (area.prop.reqLevel > player.getLevel()) {
						return LangService.format("TEAM_AREA_FOLLOW_LV", area.prop.reqLevel);
					}
					AreaUtil.changeArea(member.getPlayer(), new AreaData(area.areaId, area.instanceId));
				}
			}
			member.follow = follow;
			TeamService.refreshTeam(team);
		}
		return null;
	}

	public static void addAutoMatch(WNPlayer player, int targetId, int difficulty) {
		String playerId = player.getId();
		MatchData match = AutoMathes.get(playerId);
		if (match != null) {
			removeAutoMatch(playerId);
			if (match.targetId == targetId && match.difficulty == difficulty) {
				return;
			}
			match.targetId = targetId;
			match.difficulty = difficulty;
		} else {
			int logicServerId = player.getLogicServerId();
			match = AutoMathes.getTargetMatch(GWorld.__SERVER_ID, targetId, difficulty);
			if (match != null) {
				if (!matchTeam(match)) {
					WNPlayer leader = match.player;
					createTeam(leader, Arrays.asList(player.getId()), logicServerId);
					TeamData team = leader.getTeamManager().getTeam();
					if (team != null) {
						Out.debug("auto match player : ", leader.getId());
						team.isAutoTeam = true;
					} else {
						Out.error("auto match player : ", leader.getId());
					}
				}
				return;
			}
			match = new MatchData(player, targetId, difficulty);
		}
		AutoMathes.put(playerId, match);
	}

	public static void removeAutoMatch(String playerId) {
		AutoMathes.remove(playerId);
	}

	public static boolean isAutoMatch(String playerId) {
		return AutoMathes.containsKey(playerId);
	}

	public static void onLogout(WNPlayer player) {
		TeamUtil.removeAcrossMatch(player);
		TeamService.removeAutoMatch(player.getId());
		TeamData team = player.getTeamManager().getTeam();
		if (team != null) {
			player.getPlayerTempData().teamId = team.id;
			if (player.getTeamManager().isTeamLeader() && team.memberCount() > 1) {
				team.changeLeader();
			}
			TeamService.refreshTeam(team);
		} else {
			for (TeamData teamData : TeamMap.values()) {
				teamData.removeApply(player.getId());
			}
			player.getPlayerTempData().teamId = null;
		}
	}

}
