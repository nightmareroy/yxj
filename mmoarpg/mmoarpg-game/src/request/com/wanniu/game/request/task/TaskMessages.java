package com.wanniu.game.request.task;

import com.wanniu.core.game.LangService;
import com.wanniu.core.logfs.Out;
import com.wanniu.game.common.Const;
import com.wanniu.game.common.Const.MESSAGE_TYPE;
import com.wanniu.game.common.Const.TaskKind;
import com.wanniu.game.common.Const.TipsType;
import com.wanniu.game.common.msg.MessageUtil;
import com.wanniu.game.data.base.TaskBase;
import com.wanniu.game.message.MessageData;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.request.task.AcceptTaskHandler.AcceptTaskResult;
import com.wanniu.game.task.TaskUtils;
import com.wanniu.game.team.TeamData;
import com.wanniu.game.team.TeamData.TeamMemberData;

/**
 * 任务二次确认事件处理
 * 
 * @author Yangzz
 *
 */
public class TaskMessages {
	/**
	 * 处理 客户端二次确认事件
	 */
	public final static boolean onMessage(WNPlayer player, MESSAGE_TYPE msgType, int operate, MessageData message) {
		if (operate == Const.MESSAGE_OPERATE.TYPE_ACCEPT.getValue()) {
			if (message.messageType == Const.MESSAGE_TYPE.daily_task_times.getValue()) {// 当日师门当日总数完成, 通知再开始一轮
				if (player.taskManager.dailyTasks.size() > 0) {
					MessageUtil.sendSysTip(player, LangService.getValue("DAILY_HAS_ACCEPTED"), TipsType.BLACK);
					return false;
				}
				
				TaskBase prop = TaskUtils.getRDDailyTask(player.getLevel(), 0, player.getPro());
				AcceptTaskResult result = player.getPlayerTasks().acceptTask(prop.iD, TaskKind.DAILY);
				if (result.task != null) {
					result.task.setSecProgress(player.taskManager.getSecProgress(result.task.getKind()));
					player.getPlayerTasks().pushTaskUpdate(result.task);
					return true;
				} else {
					MessageUtil.sendSysTip(player, result.msg, TipsType.BLACK);
					return false;
				}
				
			} else if (message.messageType == Const.MESSAGE_TYPE.loop_task_addfriend.getValue()) {// 一条龙队员加队长好友
				TeamData team = player.teamManager.getTeam();
				if (team != null && !player.getId().equals(team.leaderId)) {
					player.friendManager.friendApply(team.leaderId, player);
				}
			} else if (message.messageType == Const.MESSAGE_TYPE.loop_task_member_leave.getValue()) {// 一条龙任务队员离开
				// 设置队伍为 自动匹配模式
				TeamData team = player.teamManager.getTeam();
				if (team != null && team.leaderId.equals(player.getId())) {
					player.teamManager.setIsAutoTeam(true);
					MessageUtil.sendSysTip(player, LangService.getValue("TEAM_SET_AUTO_SUCC"), TipsType.BLACK);	
					player.teamManager.pushTeamData();
				}
			} else if (message.messageType == Const.MESSAGE_TYPE.loop_task_times.getValue()) {// 一条龙本轮跑环已完成
				// 再次接取一条龙
				TeamData team = player.teamManager.getTeam();
				if (team == null) {
					MessageUtil.sendSysTip(player, LangService.getValue("TASK_NEED_TEAM"), TipsType.BLACK);
					return false;
				}
				// 只有队长才能接任务
				if (!team.leaderId.equals(player.getId())) {
					MessageUtil.sendSysTip(player, LangService.getValue("TEAM_NO_AUTHORITY"), TipsType.BLACK);
					return false;
				}

				// 判断是否在三人以上队伍中
				if (team.memberCount() < Const.LOOP_TASK_TEAM_MEMBER_COUNT) {
					MessageUtil.sendSysTip(player, String.format(LangService.getValue("TEAM_NEED_MEMBER"), Const.LOOP_TASK_TEAM_MEMBER_COUNT), TipsType.BLACK);
					return false;
				}

				// 默认给第一个一条龙任务
				TaskBase prop = TaskUtils.getFirstLoopTask();
				int templateId = prop.iD;
				
				AcceptTaskResult result = player.getPlayerTasks().acceptTask(templateId, prop.kind);
				if (result.task != null) {
					result.task.setSecProgress(player.taskManager.getSecProgress(result.task.getKind()));
					// 给队员推送消息
					for (TeamMemberData member : team.teamMembers.values()) {
						WNPlayer mPlayer = member.getPlayer();
						if (mPlayer == null)
							continue;
						mPlayer.getPlayerTasks().pushTaskUpdate(result.task);
					}
				} else {
					Out.error("accept loop task failed");
					return false;
				}

			}
		}
		return true;
	}
}
