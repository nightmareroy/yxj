package com.wanniu.game.team;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.wanniu.core.game.LangService;
import com.wanniu.core.logfs.Out;
import com.wanniu.game.common.Const;
import com.wanniu.game.player.PlayerUtil;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.poes.PlayerPO;
import com.wanniu.game.task.po.TaskPO;
import com.wanniu.game.team.TeamData.TeamMemberData;

import pomelo.area.TeamHandler.OnAcrossTeamInfoPush;
import pomelo.area.TeamHandler.OnTeamTargetPush;
import pomelo.area.TeamHandler.OnTeamUpdatePush;
import pomelo.area.TeamHandler.TeamMember;

/**
 * @author agui
 */
public class TeamManager {

	private WNPlayer player;

	private TeamData team;

	private TeamMemberData teamMember;

	public boolean loginFlag;

	public int acrossTargetId;
	public int acrossDifficulty;

	public TeamManager() {

	}

	public TeamManager(WNPlayer player) {
		this.player = player;
		String teamId = player.getPlayerTempData().teamId;
		if (teamId != null) {
			TeamData team = TeamService.getTeam(teamId);
			setTeamData(team);
			checkLeader();
		}
	}

	public void quitAcrossMatch(boolean quit) {
		acrossTargetId = 0;
		player.teamMembers = null;
		if (team != null) {
			team.local = quit || team.memberCount() <= 1;
		}
		OnAcrossTeamInfoPush push = OnAcrossTeamInfoPush.newBuilder().build();
		player.receive("area.teamPush.onAcrossTeamInfoPush", push);
	}

	/**
	 * 角色是否接受自动组队
	 */
	public boolean isAcceptAutoTeam() {
		return this.player.getPlayer().isAcceptAutoTeam == 1;
	}

	/**
	 * 队伍是否接受自动组队
	 */
	public boolean isAutoTeam() {
		return this.team != null && this.team.isAutoTeam;
	}

	/**
	 * 获取是否接受组队总判断
	 */
	public int getIsAutoTeam() {
		if (this.team != null) {
			if (this.team.isAutoTeam) {
				return 1;
			} else {
				return 0;
			}
		}
		return this.isAcceptAutoTeam() ? 1 : 0;
	}

	/**
	 * 设置自动匹配
	 */
	public void setIsAutoTeam(boolean auto) {
		if (this.team != null) {
			this.team.isAutoTeam = auto;
		}
	}

	/**
	 * 事件处理
	 */
	public boolean onMessage(int msgType, int operate, String id) {
		Out.debug(msgType, " team onEvent, ", id);
		String result = null;

		if (msgType == Const.MESSAGE_TYPE.team_invite.getValue()) {
			TeamData team = TeamService.getTeam(id);
			if (team == null) {
				player.sendSysTip(LangService.getValue("TEAM_NULL"));
				return true;
			}
			if (!team.invites.containsKey(player.getId())) {
				player.sendSysTip(LangService.getValue("EXPIRED_MSG"));
				return false;
			}
			team.invites.remove(player.getId());
			if (!team.isOpenJoin()) {
				player.sendSysTip(LangService.getValue("TEAM_BATTLE_ERR"));
				return false;
			}
			if (team.isFull()) {
				player.sendSysTip(LangService.getValue("TEAM_IS_FULL"));
				return false;
			}
			if (operate == Const.MESSAGE_OPERATE.TYPE_ACCEPT.getValue()) {
				boolean isInTeam = this.isInTeam();
				if (isInTeam) {
					result = LangService.getValue("TEAM_EXISTS");
				} else {
					String res = TeamUtil.canJoinTeam(this.player, team);
					if (res == null) {
						result = TeamService.joinTeam(team.id, this.player);
					}
				}
				if (result == null) {
					TeamMemberData inviter = team.getLeader();
					TeamUtil.sendSysMessageByPlayerEnter(inviter.teamId, this.player.getId());
					return false;
				} else {
					player.sendSysTip(result);
					return true;
				}
			} else {
				String s = LangService.format("TEAM_REFUSE", this.player.getName());
				PlayerUtil.sendSysMessageToPlayer(s, team.leaderId, null);
			}
		} else if (msgType == Const.MESSAGE_TYPE.team_apply.getValue()) {
			String applyId = id;
			if (team != null) {
				if (!team.applies.containsKey(applyId)) {
					player.sendSysTip(LangService.getValue("EXPIRED_MSG"));
					return false;
				}
				team.removeApply(applyId);
			}
			if (operate == Const.MESSAGE_OPERATE.TYPE_ACCEPT.getValue()) {
				if (TeamUtil.isInTeam(applyId)) {
					player.sendSysTip(LangService.getValue("TEAM_TARGET_IN_TEAM"));
					return false;
				}
				TeamMemberData teamMember = team.getMember(player.getId());
				if (teamMember != null && teamMember.isLeader) {
					result = TeamService.joinTeam(teamMember.teamId, PlayerUtil.getOnlinePlayer(applyId));
				}
				Out.debug("join team result:", result);
				if (result == null) {
					TeamUtil.sendSysMessageByPlayerEnter(teamMember.teamId, applyId);
					return false;
				} else {
					String s = LangService.format("TEAM_REFUSE", this.player.getName());
					PlayerUtil.sendSysMessageToPlayer(s, applyId, null);
					return true;
				}
			} else {
				String res = LangService.getValue("TEAM_TARGET_REFUSE").replace("{playerName}", this.player.getName());
				PlayerUtil.sendSysMessageToPlayer(res, applyId, null);
			}
		}
		return true;
	}

	public void pushTeamData() {
		String teamId = "";
		List<TeamMember> teamMembers = new ArrayList<>();
		OnTeamUpdatePush.Builder data = OnTeamUpdatePush.newBuilder();
		TeamData team = this.team;
		if (team != null) {
			teamId = team.id;
			Map<String, TeamMemberData> teamMembersData = getTeamMembers();
			for (TeamMemberData teamMember : teamMembersData.values()) {
				if (!teamMember.id.equals(player.getId())) {
					PlayerPO member = teamMember.getPlayerData();
					TeamMember.Builder tm = TeamMember.newBuilder();
					tm.setId(teamMember.id);
					tm.setName(member.name);
					tm.setPro(member.pro);
					tm.setLevel(member.level);
					tm.setIsLeader(teamMember.getLeader());
					tm.setFollow(teamMember.getFollow());
					if (teamMember.isOnline()) {
						WNPlayer pm = teamMember.getPlayer();
						tm.setAreaId(pm.getAreaId());
						if (player.getInstanceId().equals(pm.getInstanceId())) {
							tm.setStatus(Const.PlayerStatus.online.getValue());
						} else {
							tm.setStatus(Const.PlayerStatus.faraway.getValue());
						}
					} else {
						tm.setStatus(Const.PlayerStatus.offline.getValue());
					}

					teamMembers.add(tm.build());
				} else {
					data.setFollow(teamMember.getFollow());
				}
			}
			data.setS2CIsAcceptAutoTeam(team.isAutoTeam ? 1 : 0);
		}
		data.setS2CTeamId(teamId);
		data.addAllS2CData(teamMembers);
		player.receive("area.teamPush.onTeamUpdatePush", data.build());
	}

	private void checkLeader() {
		TeamMemberData leader = team == null ? null : team.getLeader();
		if (teamMember != null && !teamMember.isLeader && (leader == null || !leader.isOnline())) {
			team.changeLeader();
		}
	}

	public void checkLogin() {
		if (this.loginFlag) {
			this.loginFlag = false;
			checkLeader();
			if (this.team != null) {
				OnTeamTargetPush targetPush = OnTeamTargetPush.newBuilder().setS2CTeamTarget(TeamUtil.generateTeamTargetData(team)).setHaveApply(team.applies.size()).setS2CIsAcceptAutoTeam(team.isAutoTeam ? 1 : 0).build();
				player.receive("area.teamPush.onTeamTargetPush", targetPush);
			}
		}
	}

	public void onPlayerUpgrade() {
		TeamService.refreshTeam(this.team, false);
	}

	/**
	 * 队员变化
	 * 
	 * @param members
	 */
	public void onMemberChange(Collection<String> members) {
		// this.player.initAndCalAllInflu(members);
		// this.player.pushAndRefreshEffect(false);
	}

	/**
	 * 设置队伍信息
	 */
	public void setTeamData(TeamData team) {
		if (this.team != team) {
			this.team = team;
			if (team == null) {
				this.teamMember = null;
			} else {
				this.teamMember = team.getMember(player.getId());
				if (this.teamMember == null) {
					this.team = null;
				}
			}
		}
	}

	/**
	 * 判断是否在队伍中
	 */
	public boolean isInTeam() {
		return this.teamMember != null;
	}

	/**
	 * 获取队伍成员信息
	 */
	public TeamMemberData getTeamMember() {
		return this.teamMember;
	}

	/**
	 * 获取队伍成员信息
	 */
	public Map<String, TeamMemberData> getTeamMembers() {
		if (this.team == null)
			return null;
		return this.team.teamMembers;
	}

	/**
	 * 获取队伍信息
	 */
	public TeamData getTeam() {
		return this.team;
	}

	/**
	 * 获取一条龙任务
	 */
	public Map<Integer, TaskPO> getLoopTasks() {
		return team != null ? team.loopTasks : null;
	}

	/**
	 * 判断是否是队长
	 */
	public boolean isTeamLeader() {
		return teamMember != null ? teamMember.isLeader : false;
	}

	/**
	 * 判断是否跟随队长
	 */
	public boolean isFollowLeader() {
		return teamMember != null ? teamMember.follow : false;
	}

	/**
	 * 获取团队成员数量包含队长
	 */
	public int followCount() {
		if (this.team != null) {
			return this.team.followCount();
		}
		return 1;
	}

}
