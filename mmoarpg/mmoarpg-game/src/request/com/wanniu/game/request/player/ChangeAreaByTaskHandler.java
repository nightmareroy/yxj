package com.wanniu.game.request.player;

import java.io.IOException;
import java.util.Map;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.core.logfs.Out;
import com.wanniu.game.area.AreaUtil;
import com.wanniu.game.common.Const;
import com.wanniu.game.common.Const.FunctionType;
import com.wanniu.game.common.Const.TaskState;
import com.wanniu.game.common.Const.TipsType;
import com.wanniu.game.common.msg.ErrorResponse;
import com.wanniu.game.common.msg.MessageUtil;
import com.wanniu.game.data.OpenLvCO;
import com.wanniu.game.functionOpen.FunctionOpenUtil;
import com.wanniu.game.player.PlayerUtil;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.task.TaskUtils;
import com.wanniu.game.task.po.TaskPO;
import com.wanniu.game.team.TeamData;
import com.wanniu.game.team.TeamData.TeamMemberData;

import pomelo.area.PlayerHandler.ChangeAreaByTaskRequest;
import pomelo.area.PlayerHandler.ChangeAreaByTaskResponse;

/**
 * 当玩家已经在目标任务路点时，点击任务寻路时触发
 * 
 * @author wfy
 *
 */
@GClientEvent("area.playerHandler.changeAreaByTaskRequest")
public class ChangeAreaByTaskHandler extends ChangeAreaFilter {

	public PomeloResponse request(WNPlayer player) throws Exception {

		ChangeAreaByTaskRequest req = ChangeAreaByTaskRequest.parseFrom(pak.getRemaingBytes());
		int mapId = req.getMapId();
		int taskId = req.getTaskId();
		int x = req.getPosx();
		int y = req.getPosy();
		int pointId = req.getPoint();
		
		if(mapId<=0 || taskId<=0){
			return new ErrorResponse(player.getAreaId() + " - " + pointId + " : " + LangService.getValue("AREA_ID_NULL"));
		}

		boolean done = false;
		///////目前只针对皓月镜在路点卡死情况的营救措施
//		如果是队长+队伍人数>3+一条龙场景+队员为跟随,把队伍中的其它队员也带进来
//		如果是队员操作，提示权限不足
		TeamData team = player.getTeamManager().getTeam();
		if(team!=null){
			TaskPO taskData = null;
			int areaId = 0;
			// 一条龙场景ID根据玩家等级，从任务配置表 动态获取
			Map<Integer, TaskPO> loopTasks = player.teamManager.getLoopTasks();
			if (loopTasks != null) {
				for (TaskPO task : loopTasks.values()) {
					if(taskId == task.templateId){
						areaId = TaskUtils.getTaskProp(task.templateId).circleDungeonID;
						taskData = task;
						break;
					}
				}
			}	
			if (areaId>0 && taskData != null && taskData.state == TaskState.NOT_COMPLETED.getValue()) {
				if (team.leaderId.equals(player.getId())) {
					boolean success = true;
					for (TeamMemberData teamMember : team.teamMembers.values()) {
						WNPlayer mPlayer = PlayerUtil.getOnlinePlayer(teamMember.id);
						if (mPlayer == null) {
							success = false;
							MessageUtil.sendSysTip(player, LangService.getValue("TEAM_PLAYER_OFF_LINE"), TipsType.BLACK);
							break;
						}
						if (!teamMember.isOnline()) {
							success = false;
							MessageUtil.sendSysTip(player, LangService.getValue("TEAM_PLAYER_OFF_LINE"), TipsType.BLACK);
							break;
						}
						
						OpenLvCO openConfig = FunctionOpenUtil.findFunctionOpenPropsByFuncName(FunctionType.LoopTask.getValue());
						if (mPlayer.getLevel() < openConfig.openLv) {
							success = false;
							MessageUtil.sendSysTip(player, 
									LangService.getValue("TEAM_MEMBER_LEVEL_LIMIT").replace("{level}", String.valueOf(openConfig.openLv)), 
									TipsType.BLACK);
							break;
						}
					}
					
					if (success) {
						if (team.memberCount() >= Const.LOOP_TASK_TEAM_MEMBER_COUNT) {
							if(team.followCount() == team.memberCount()) {
								team.confirm = false; // 队伍进入副本设置为无需确认
							}
							if(player.getAreaId() != areaId){
								AreaUtil.dispatchByAreaId(player, areaId,null);
								done = true;
							}
						} else {
							MessageUtil.sendSysTip(player, LangService.getValue("TASK_NEED_TEAM"), TipsType.BLACK);
						}
					}
				} else {
					MessageUtil.sendSysTip(player, LangService.getValue("TEAM_NO_AUTHORITY"), TipsType.BLACK);
				}
			}
			
			if(!done){
				Out.warn("changeAreaByTaskRequest handle failed: " + req.toString());
			}
		}
		Out.debug("changeAreaByTaskRequest: " + req.toString());
		
		ChangeAreaByTaskResponse.Builder res = ChangeAreaByTaskResponse.newBuilder();
		res.setS2CCode(OK);
		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				body.writeBytes(res.build().toByteArray());
			}
		};
	}
}