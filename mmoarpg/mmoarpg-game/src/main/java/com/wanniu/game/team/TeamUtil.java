package com.wanniu.game.team;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.wanniu.core.game.LangService;
import com.wanniu.core.logfs.Out;
import com.wanniu.core.proxy.ProxyClient;
import com.wanniu.core.proxy.ProxyType;
import com.wanniu.core.tcp.protocol.Message;
import com.wanniu.game.GWorld;
import com.wanniu.game.area.Area;
import com.wanniu.game.area.AreaUtil;
import com.wanniu.game.common.Const;
import com.wanniu.game.guild.GuildUtil;
import com.wanniu.game.player.GlobalConfig;
import com.wanniu.game.player.PlayerUtil;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.poes.PlayerPO;
import com.wanniu.game.team.TeamData.TeamMemberData;

import pomelo.area.TeamHandler;
import pomelo.area.TeamHandler.TeamMemberDetail;

/**
 * @author agui
 */
public class TeamUtil {

	public static int getTeamExpAdd(int teamMemberNum) {
		int expAdd = 0;
		if (teamMemberNum == 2) {
			expAdd = GlobalConfig.TeamExperience2;
		} else if (teamMemberNum == 3) {
			expAdd = GlobalConfig.TeamExperience3;
		} else if (teamMemberNum == 4) {
			expAdd = GlobalConfig.TeamExperience4;
		} else if (teamMemberNum == 5) {
			expAdd = GlobalConfig.TeamExperience5;
		}
		return expAdd;
	}

	/**
	 * 生成队伍成员详情数据
	 */
	public static List<TeamMemberDetail> generateTeamMemberDetailData(WNPlayer player, TeamData team) {
		Map<String, TeamMemberData> teamMembers = team.teamMembers;
		List<TeamMemberDetail> data = new ArrayList<>();
		for (TeamMemberData teamMember : teamMembers.values()) {
			PlayerPO po = teamMember.getPlayerData();
			TeamMemberDetail.Builder detail = TeamMemberDetail.newBuilder();
			detail.setId(po.id);
			detail.setName(po.name);
			detail.setPro(po.pro);
			detail.setLevel(po.level);
			detail.setUpLevel(po.upOrder);
			String guildName = GuildUtil.getGuildName(teamMember.id);
			detail.setGuildName(guildName);
			detail.setIsLeader(teamMember.getLeader());
			detail.setFightPower(po.fightPower);

			WNPlayer member = teamMember.getPlayer();
			detail.setAreaDes("");
			if (teamMember.isOnline()) {
				if (player.getInstanceId().equals(member.area.instanceId)) {
					detail.setStatus(Const.PlayerStatus.online.getValue());
				} else {
					detail.setStatus(Const.PlayerStatus.faraway.getValue());
				}
				Area area = member.area;
				if (player.getAreaId() == area.areaId) {
					detail.setAreaDes(area.lineIndex + "线");
				} else {
					detail.setAreaDes(area.getSceneName());
				}
			} else {
				detail.setStatus(Const.PlayerStatus.offline.getValue());
			}
			detail.addAllAvatars(PlayerUtil.getBattlerServerAvatar(po));

			detail.setFollow(teamMember.getFollow());
			if (teamMember.isLeader) {
				data.add(0, detail.build());
			} else {
				data.add(detail.build());
			}
		}
		return data;
	}

	/**
	 * 判断是否在队伍中 params : playerId
	 */
	public static boolean isInTeam(String playerId) {
		return TeamService.isInTeam(playerId);
	}

	/**
	 * 判断是否在队伍中
	 */
	public static boolean isInTeam(String teamId, String playerId) {
		return TeamService.isInTeam(teamId, playerId);
	}

	/**
	 * 跨服创建队伍 param : leader, members[]}
	 */
	public static String createAcrossTeam(String leaderId, List<String> memberIds) {
		Out.debug("sendCenterServerToCreateTeamAcrossServer");
		WNPlayer leader = PlayerUtil.findPlayer(leaderId);
		return TeamService.createTeam(leader, memberIds, leader.getAcrossServerId());
	}

	/**
	 * 本服创建队伍 param : leader, members[]}
	 */
	public static String createLocaleTeam(String leaderId, List<String> memberIds) {
		Out.debug("sendCenterServerToCreateTeamSelfServer");
		WNPlayer leader = PlayerUtil.findPlayer(leaderId);
		return TeamService.createTeam(leader, memberIds, GWorld.__SERVER_ID);
	}

	/**
	 * 判断某人是否已经被自己邀请过
	 */
	public static boolean isHasInvitedPlayer(WNPlayer player, String invitedId) {
		TeamData team = player.getTeamManager().getTeam();
		return team != null && team.invites.containsKey(invitedId);
	}

	/**
	 * 判断是否已经申请过加入某支队伍
	 */
	public static boolean isHasAppliedTeam(WNPlayer player, TeamData team) {
		return team.applies.containsKey(player.getId());
	}

	public static boolean isFullOfTeam(String teamId) {
		TeamData team = TeamService.getTeam(teamId);
		return team == null || team.isFull();
	}

	public static void handleFullOfTeam(TeamData team, WNPlayer player) {
		if (GWorld.ROBOT) {
			team.confirm = false;
			team.isAutoStart = true;
			if (team != null && team.isAutoStart && team.isFull() && team.isAllOnline()) {
				team.curTargetId = team.targetId;
				team.curDifficulty = team.difficulty;
				int mapId = team.getTargetMap();
				if (mapId > 0) {
					goToTeamTarget(team, player);
				}
			}
		}
	}

	public static Area goToTeamTarget(TeamData team, WNPlayer player) {
		int mapId = team.getTargetMap();
		if (mapId != 0) {
			// MapBase sceneProp = AreaUtil.getAreaProp(mapId);
			{
				Area area = AreaUtil.enterArea(player, mapId, 0, 0);
				// if (area != null) {
				// AreaData areaData = new AreaData(area.areaId, area.instanceId);
				// for (TeamMemberData teamMember : team.teamMembers.values()) {
				// if (!teamMember.id.equals(player.getId())) {
				// AreaUtil.disCardItemByTransArea(sceneProp, player);
				// AreaUtil.dispatchByInstanceId(player, areaData);
				// }
				// }
				// }
				return area;
			}
		} else {
			Out.warn("there is no prop in teamTargetProp ID:", team.getCurTargetId());
		}
		return null;
	}

	public static void sendSysMessageByPlayerEnter(String teamId, String playerId) {
		WNPlayer player = PlayerUtil.getOnlinePlayer(playerId);
		for (TeamMemberData teamMember : TeamService.getTeamMembers(teamId).values()) {
			if (teamMember.id.equals(playerId)) {
				PlayerUtil.sendSysMessageToPlayer(LangService.getValue("TEAM_ENTER"), teamMember.id, null);
			} else {
				PlayerUtil.sendSysMessageToPlayer(LangService.getValue("TEAM_TARGET_ENTER").replace("{playerName}", player.getName()), teamMember.id);
			}
		}
	}

	public static void sendSysMessageByLeaderChanged(String teamId, String oldLeaderName) {
		TeamData team = TeamService.getTeam(teamId);
		WNPlayer leader = PlayerUtil.getOnlinePlayer(team.leaderId);
		for (TeamMemberData teamMember : team.teamMembers.values()) {
			if (teamMember.isLeader) {
				PlayerUtil.sendSysMessageToPlayer(LangService.getValue("TEAM_BE_CAPTAIN").replace("{playerName}", oldLeaderName), teamMember.id);
			} else {
				PlayerUtil.sendSysMessageToPlayer(LangService.getValue("TEAM_CHANGE_CAPTAIN").replace("{playerName}", leader.getName()), teamMember.id);
			}
		}
	}

	/**
	 * 生成队伍目标数据
	 */
	public static TeamHandler.TeamTarget generateTeamTargetData(TeamData team) {
		TeamHandler.TeamTarget.Builder target = TeamHandler.TeamTarget.newBuilder();
		if (team != null) {
			target.setTargetId(team.targetId);
			target.setDifficulty(team.difficulty);
			target.setMinLevel(team.minLevel);
			target.setMaxLevel(team.maxLevel);
			target.setIsAutoStart(team.isAutoStart ? 1 : 0);
			target.setIsAutoTeam(team.isAutoTeam ? 1 : 0);
		} else {
			target.setTargetId(0);
			target.setDifficulty(1);
			target.setMinLevel(0);
			target.setMaxLevel(0);
			target.setIsAutoStart(0);
			target.setIsAutoTeam(0);
		}
		return target.build();
	}

	public static String canJoinTeam(WNPlayer player, TeamData team) {
		if (team == null) {
			return LangService.getValue("TEAM_BREAK_UP");
		}
		if (!team.isOpenJoin()) {
			return LangService.getValue("TEAM_LOCKED");
		}
		if (team.minLevel > player.getLevel()) {
			return LangService.getValue("TEAM_TARGET_NO_LV");
		}
		if (team.maxLevel != 0 && team.maxLevel < player.getLevel()) {
			return LangService.getValue("TEAM_TARGET_MORE_LV");
		}
		if (team.isFull()) {
			return LangService.getValue("TEAM_IS_FULL");
		}
		if (!isValidOfMap(team.sceneType) || !isValidOfMap(player.getSceneType())) {
			return LangService.getValue("TEAM_IN_RAID");
		}
		return null;
	}

	public static boolean isValidOfMap(int sceneType) {
		return Arrays.binarySearch(GlobalConfig.TeamMapTypeGOs, sceneType) >= 0;
	}

	public static void leaveTeamInAreaServer(WNPlayer player) {
		boolean isInTeam = player.getTeamManager().isInTeam();
		if (isInTeam) {
			Map<String, TeamMemberData> teamMembers = player.getTeamManager().getTeamMembers();
			TeamService.leaveTeam(player);
			PlayerUtil.sendSysMessageToPlayer(LangService.getValue("TEAM_QUIT"), player.getId());
			for (TeamMemberData teamMember : teamMembers.values()) {
				if (!teamMember.id.equals(player.getId())) {
					PlayerUtil.sendSysMessageToPlayer(LangService.format("TEAM_TARGET_LEAVE", player.getName()), teamMember.id, null);
				}
			}
		}
	}

	public static void pushAcrossMatch(WNPlayer player) {
		TeamData team = player.getTeamManager().getTeam();
		if (team != null) {
			team.local = false;
		} else {
			TeamService.removeAutoMatch(player.getId());
		}
		ProxyClient.getInstance().add(new Message() {
			@Override
			protected void write() throws IOException {
				body.writeByte(1); // 加入匹配
				int targetId = player.teamManager.acrossTargetId;
				int difficult = player.teamManager.acrossDifficulty;
				if (team != null) {
					targetId = team.curTargetId;
					difficult = team.curDifficulty;
				}
				body.writeInt(targetId);
				body.writeByte(difficult);
				if (team != null) {
					int count = team.memberCount();
					body.writeByte(count);
					for (String playerId : team.teamMembers.keySet()) {
						concatPlayer(PlayerUtil.getOnlinePlayer(playerId));
					}
				} else {
					body.writeByte(1);
					concatPlayer(player);
				}
			}

			private void concatPlayer(WNPlayer player) {
				body.writeString(player.getId());
				body.writeString(player.getName());
				body.writeByte(player.getPro());
				body.writeShort(player.getLevel());
			}

			@Override
			public short getType() {
				return ProxyType.TEAM;
			}
		});
	}

	public static void removeAcrossMatch(WNPlayer player) {
		if (player != null && player.isRomote()) {
			player.teamManager.quitAcrossMatch(false);
			ProxyClient.getInstance().add(new Message() {
				@Override
				protected void write() throws IOException {
					body.writeByte(2); // 退出匹配
					body.writeString(player.getId());
				}

				@Override
				public short getType() {
					return ProxyType.TEAM;
				}
			});
		}
	}

}
