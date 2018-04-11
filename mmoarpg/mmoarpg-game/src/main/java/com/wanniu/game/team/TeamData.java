package com.wanniu.game.team;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

import com.google.protobuf.GeneratedMessage;
import com.wanniu.core.game.JobFactory;
import com.wanniu.core.game.LangService;
import com.wanniu.core.game.protocol.PomeloPush;
import com.wanniu.core.logfs.Out;
import com.wanniu.core.util.RandomUtil;
import com.wanniu.game.GWorld;
import com.wanniu.game.area.Area;
import com.wanniu.game.common.Const;
import com.wanniu.game.common.Utils;
import com.wanniu.game.common.msg.MessagePush;
import com.wanniu.game.common.msg.MessageUtil;
import com.wanniu.game.data.GameData;
import com.wanniu.game.data.TeamTargetCO;
import com.wanniu.game.guild.GuildUtil;
import com.wanniu.game.message.MessageData;
import com.wanniu.game.message.MessageData.MessageData_Team_InviteId;
import com.wanniu.game.player.GlobalConfig;
import com.wanniu.game.player.PlayerUtil;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.poes.PlayerPO;
import com.wanniu.game.task.po.TaskPO;

import pomelo.area.TeamHandler.OnTeamTargetPush;
import pomelo.area.TeamHandler.Team;
import pomelo.area.TeamHandler.TeamMemberBasic;

/**
 * @author agui
 */
public class TeamData {

	public String id;
	public int logicServerId;
	public Map<String, TeamMemberData> teamMembers;
	public String leaderId;
	public int sceneType;

	public int targetId;
	public int difficulty;

	public int curTargetId;
	public int curDifficulty;

	// 目标属性相关设定
	public int minLevel = 0;
	public int maxLevel = 0;

	public boolean isAutoTeam;
	public boolean isAutoStart;
	public boolean isRobotJoin;
	public boolean isRobotLeader;

	public boolean local = true; // 是否是本地

	public boolean confirm = true; // 是否需要队伍确认

	public ScheduledFuture<?> lockfuture;

	public Map<String, Long> invites = new ConcurrentHashMap<>();
	public Map<String, Long> applies = new ConcurrentHashMap<>();
	//////////////////////////////////////////////////////////////////////
	/** 正在进行的一条龙任务 */
	public Map<Integer, TaskPO> loopTasks;
	/** 已完成的一条龙任务 */
	public Map<Integer, Integer> finishedLoopTasks;
	/** 是否有一条龙任务 */
	private boolean hasLoopTask;
	/** 一轮一条龙任务结束的时间 */
	private long rebotCanLeaveTime;

	//////////////////////////////////////////////////////////////////////

	public long robotFreeTime;
	public long robotJoinTime;

	public void onFirstAcceptLoopTask() {
		hasLoopTask = true;
	}

	/**
	 * 一条龙全部结束才算真的结束
	 */
	public void onAllOverLoopTask() {
		hasLoopTask = false;
		rebotCanLeaveTime = System.currentTimeMillis() + (Const.LOOP_TASK_ROBOT_WAIT_LEAVE_TIME);
	}

	/**
	 * 机器人是否能离开
	 * 
	 * @return
	 */
	public boolean canRobotLeave() {
		if (isInLoopTask()) {
			return false;
		}
		return System.currentTimeMillis() >= rebotCanLeaveTime;
	}

	public boolean isInLoopTask() {
		return hasLoopTask;
	}

	public int memberCount() {
		return teamMembers.size();
	}

	public void setAutoTeam(boolean auto) {
		this.isAutoTeam = auto;
		if (auto) {
			setRobotTime();
		}
	}

	public void addApply(String playerId, long validate) {
		applies.put(playerId, validate);
		if (applies.size() == 1) {
			onTeamChange();
		}
	}

	public void removeApply(String playerId) {
		if (applies.containsKey(playerId)) {
			applies.remove(playerId);
			if (applies.isEmpty()) {
				onTeamChange();
			}
		}
	}

	public void setRobotTime() {
		if (GWorld.APP_TIME > this.robotFreeTime && GWorld.APP_TIME < this.robotFreeTime + 300000) {
			this.robotJoinTime = GWorld.APP_TIME + Utils.getSecMills(60, 300);
		} else {
			this.robotJoinTime = GWorld.APP_TIME + Utils.getSecMills(6, 10);
		}
		setFreeRobotTime();
	}

	public void setFreeRobotTime() {
		if (isRobotJoin) {
			this.robotFreeTime = Math.max(GWorld.APP_TIME, this.robotJoinTime) + Utils.getSecMills(60, 200);
		}
	}

	public boolean isAllOnline() {
		for (TeamMemberData teamMember : teamMembers.values()) {
			if (!teamMember.isOnline()) {
				return false;
			}
		}
		return true;
	}

	public boolean isAllRobotOnline() {
		for (TeamMemberData teamMember : teamMembers.values()) {
			if (!teamMember.robot && teamMember.isOnline()) {
				return false;
			}
		}
		return true;
	}

	public int followCount() {
		int count = 0;
		for (TeamMemberData member : teamMembers.values()) {
			if (member.isFollow()) {
				count++;
			}
		}
		return count;
	}

	public boolean isFull() {
		return memberCount() >= GlobalConfig.NormalTeamMaxNum;
	}

	public Team.Builder createTeamProto() {
		TeamMemberData leader = getLeader();
		if (leader == null) {
			Out.warn("组队队长异常，自动转让队长...");
			if (memberCount() > 0) {
				changeLeader();
				leader = getLeader();
			} else {
				TeamService.destroyTeam(this);
				return null;
			}
		}
		if (leader == null || !leader.isOnline()) {
			return null;
		}
		Team.Builder team = Team.newBuilder();
		team.setId(this.id);
		team.setTargetId(this.targetId);
		team.setLeaderId(this.leaderId);
		team.setMinLevel(this.minLevel);
		team.setMaxLevel(this.maxLevel);
		for (TeamMemberData teamMember : teamMembers.values()) {
			TeamMemberBasic.Builder basic = TeamMemberBasic.newBuilder();
			PlayerPO member = teamMember.getPlayerData();
			basic.setId(teamMember.id);
			basic.setPro(member.pro);
			basic.setName(member.name);
			basic.setLevel(member.level);
			basic.setUpLevel(0);
			basic.setGuildName(GuildUtil.getGuildName(teamMember.id));
			team.addTeamMembers(basic);
		}
		return team;
	}

	public int getCurTargetId() {
		return curTargetId != 0 ? curTargetId : this.targetId;
	}

	public int getCurDifficulty() {
		return curDifficulty != 0 ? curDifficulty : this.difficulty;
	}

	public int getTargetMap() {
		int targetId = getCurTargetId();
		int difficulty = getCurDifficulty();
		if (targetId == 0)
			return 0;
		return getTargetMap(targetId, difficulty);
	}

	public static int getTargetMap(int targetId, int difficulty) {
		int mapId = 0;
		TeamTargetCO teamTargetProp = GameData.TeamTargets.get(targetId);
		if (teamTargetProp != null) {
			if (difficulty == 1) {
				mapId = teamTargetProp.normalMapID;
			}
			// 目前无难度2和3
			// else if (difficulty == 2) {
			// mapId = teamTargetProp.eliteMapID;
			// } else if (difficulty == 3) {
			// mapId = teamTargetProp.heroMapID;
			// }
		}
		if (mapId == 0 && targetId == 1020) {// 镇妖塔
			mapId = GlobalConfig.DemonTowerMapIds[RandomUtil.getIndex(GlobalConfig.DemonTowerMapIds.length)];
		}
		return mapId;
	}

	public void invite(WNPlayer fromPlayer, String toPlayerId) {
		Map<String, String> strMsg = new HashMap<>(2);
		strMsg.put("playerName", PlayerUtil.getFullColorName(fromPlayer));
		TeamTargetCO target = GameData.TeamTargets.get(this.targetId);
		strMsg.put("target", target != null ? LangService.format("TEAM_TARGET_DESC", target.targetName) : "");
		MessageData_Team_InviteId msgData = new MessageData_Team_InviteId();
		msgData.inviteId = toPlayerId;
		MessageData message = MessageUtil.createMessage(Const.MESSAGE_TYPE.team_invite.getValue(), fromPlayer.getId(), msgData, strMsg);
		message.id = id;
		MessageUtil.sendMessageToPlayer(message, toPlayerId);
		invites.put(toPlayerId, message.validTime);
	}

	public TeamMemberData getLeader() {
		return teamMembers.get(leaderId);
	}

	public TeamMemberData getMember(String memberId) {
		return teamMembers.get(memberId);
	}

	public boolean islock() {
		return lockfuture != null;
	}

	public void lock(int time) {
		if (lockfuture != null) {
			lockfuture.cancel(true);
			Out.warn("team lock more...");
		}
		lockfuture = JobFactory.addDelayJob(() -> {
			lockfuture = null;
			Out.debug("lock timeout...");
		}, time * 1000);
	}

	public void unlock() {
		if (lockfuture != null) {
			lockfuture.cancel(true);
			lockfuture = null;
		}
	}

	public WNPlayer getPlayer(String rid) {
		return PlayerUtil.getOnlinePlayer(rid);
	}

	public boolean isOpenFollow() {
		return isOpenJoin();
	}

	public boolean isOpenJoin() {
		boolean lock = islock();
		if (lock) {
			return false;
		}
		WNPlayer player = getPlayer(this.leaderId);
		Area area = player == null ? null : player.getArea();
		return area != null && area.isOpenJoinTeam();
	}

	public void changeLeader() {
		synchronized (TeamService.lock) {
			TeamMemberData newLeader = null;
			Map<String, TeamMemberData> teamMembers = this.teamMembers;
			for (TeamMemberData member : teamMembers.values()) {
				if (newLeader != null && member.isOnline() && ((member.joinTime < newLeader.joinTime && !member.robot) || !newLeader.isOnline() || (newLeader.robot && !member.robot))) {
					newLeader = member;
				} else if (newLeader == null) {
					newLeader = member;
				}
			}
			if (newLeader != null) {
				TeamMemberData oldLeader = getLeader();
				this.leaderId = newLeader.id;
				newLeader.isLeader = true;
				isRobotLeader = newLeader.robot;
				if (oldLeader != null && oldLeader != newLeader) {
					oldLeader.isLeader = false;
				}
			} else {
				Out.warn("队长无法转让, 队员：", teamMembers.size());
			}
		}
	}

	public void receive(String route, GeneratedMessage msg) {
		receive(new MessagePush(route, msg));
	}

	public void receive(PomeloPush push) {
		for (String rid : teamMembers.keySet()) {
			WNPlayer player = getPlayer(rid);
			if (player != null) {
				player.receive(push);
			}
		}
	}

	public void onTeamChange() {
		OnTeamTargetPush targetPush = OnTeamTargetPush.newBuilder().setS2CTeamTarget(TeamUtil.generateTeamTargetData(this)).setHaveApply(applies.size()).setS2CIsAcceptAutoTeam(isAutoTeam ? 1 : 0).build();
		for (String rid : teamMembers.keySet()) {
			WNPlayer member = PlayerUtil.getOnlinePlayer(rid);
			if (member != null) {
				member.receive("area.teamPush.onTeamTargetPush", targetPush);
			}
		}
	}

	public static class TeamMemberData {

		public String teamId;
		public String id;

		public long joinTime; // 加入时间

		public boolean robot; // 是否机器人

		public boolean isLeader;// 是否是队长
		public boolean follow; // 是否跟随
		public int handup; // 是否举手

		public TeamMemberData(String teamId, String playerId) {
			this.teamId = teamId;
			this.id = playerId;
			this.joinTime = GWorld.APP_TIME;
			this.follow = true;
		}

		public boolean isOnline() {
			return PlayerUtil.isOnline(id);
		}

		public WNPlayer getPlayer() {
			return PlayerUtil.getOnlinePlayer(id);
		}

		public PlayerPO getPlayerData() {
			return PlayerUtil.getPlayerBaseData(id);
		}

		public TeamData getTeam() {
			return TeamService.getTeam(teamId);
		}

		public boolean isFollow() {
			return follow || isLeader;
		}

		public int getFollow() {
			return follow && !isLeader ? 1 : 0;
		}

		public int getLeader() {
			return isLeader ? 1 : 0;
		}

		public boolean isBusy() {
			if (!isOnline()) {
				return true;
			}
			WNPlayer player = getPlayer();
			if (!isFollow()) {
				Area area = player.getArea();
				if (area == null || !area.isNormal()) {
					return true;
				}
			}
			return false;
		}

		public void receive(String route, GeneratedMessage msg) {
			receive(new MessagePush(route, msg));
		}

		public void receive(PomeloPush push) {
			WNPlayer player = getPlayer();
			if (player != null) {
				player.receive(push);
			}
		}

	}

}
