package com.wanniu.game.task;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.wanniu.core.game.LangService;
import com.wanniu.core.logfs.Out;
import com.wanniu.core.util.StringUtil;
import com.wanniu.game.area.Area;
import com.wanniu.game.area.AreaUtil;
import com.wanniu.game.area.MonsterUnit;
import com.wanniu.game.common.Const;
import com.wanniu.game.common.Const.EventType;
import com.wanniu.game.common.Const.FunctionType;
import com.wanniu.game.common.Const.TaskState;
import com.wanniu.game.common.Const.TaskType;
import com.wanniu.game.common.Const.TipsType;
import com.wanniu.game.common.msg.MessageUtil;
import com.wanniu.game.data.GameData;
import com.wanniu.game.data.OpenLvCO;
import com.wanniu.game.data.PickItemCO;
import com.wanniu.game.data.base.MonsterBase;
import com.wanniu.game.functionOpen.FunctionOpenUtil;
import com.wanniu.game.guild.guildBoss.GuildBossAreaHurtRankCenter;
import com.wanniu.game.guild.guildBoss.GuildBossService;
import com.wanniu.game.monster.MonsterConfig;
import com.wanniu.game.player.GlobalConfig;
import com.wanniu.game.player.PlayerUtil;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.task.po.TaskPO;
import com.wanniu.game.team.TeamData;
import com.wanniu.game.team.TeamData.TeamMemberData;

public class TaskEvent implements Runnable {

	public int type;
	public Object[] params;
	public WNPlayer player;

	public TaskEvent(EventType name, Object... param) {
		this.type = name.getValue();
		this.params = param;
	}

	public TaskEvent(TaskType name, Object... param) {
		this.type = name.getValue();
		this.params = param;
	}

	public TaskEvent(int type, String param) {
		this.type = type;
		if (StringUtil.isNotEmpty(param)) {
			this.params = JSONArray.parseArray(param).toArray();
		}
	}

	/**
	 * 工会BOSS的处理
	 * 
	 * @return
	 */
	private boolean processGuildBoss() {
		if (this.type == EventType.hurtRank.getValue()) {// 统计输出排行
			String guildId = (String) this.params[0];
			String playerId = (String) this.params[1];
			long hurt = (long) this.params[2];
			int enterCount = (int) this.params[3];
			GuildBossAreaHurtRankCenter.getInstance().setNewData(guildId, playerId, hurt, enterCount);
			return true;
		} else if (this.type == EventType.hurtRank_sort.getValue()) {// 输出排行榜排序计算
			String guildId = (String) this.params[0];			
			Collection<String> pushRoleIds = (Collection<String>) this.params[1];// 要推送数据的玩家
			boolean isForcePush = (boolean) this.params[2];
			long killTime = (long) this.params[3];
			boolean hasKilled = (boolean) this.params[4];
			GuildBossAreaHurtRankCenter.getInstance().processSortHurtRank(guildId, pushRoleIds, isForcePush, killTime, hasKilled);
			return true;
		} else if (this.type == EventType.statics_ranks.getValue()) {// BOSS结束以后统计个排行榜
			GuildBossService.getInstance().processStaticsAllRanks();
			return true;
		} else if (this.type == EventType.over_statics_ranks.getValue()) {// BOSS结束以后统计个排行榜
			String guildId = (String) this.params[0];
			Collection<String> roleIds = (Collection<String>) this.params[1];
			GuildBossAreaHurtRankCenter.getInstance().overStaticsRanks(guildId, roleIds);
			return true;
		}
		return false;
	}

	private void onInterActiveItemEvent() {
		int templateID = Integer.parseInt(this.params[0].toString());
		PickItemCO pickItemProp = GameData.PickItems.get(templateID);
		if (pickItemProp != null) {
			String tcCode = pickItemProp.tC;
			if (player.bag.findFirstItemByCode("fishgear1") == null) {
				// 是否有鱼杆
				return;
			}
			// 是否有钓鱼任务
			TaskData task = player.taskManager.doingTaskHaveType(TaskType.FISH.getValue());
			if (task != null) {
				String extendTc = task.questTc();
				PlayerUtil.onFishItem(player, null, extendTc);
			} else {
				PlayerUtil.onFishItem(player, tcCode, null);
			}
			// 成就
			player.achievementManager.onFishing();
		}
	}
	
	private void onAddUnitEvent() {
		// 不在召唤boss任务中
		String[] taskIds = GlobalConfig.Call_Boss_TaskID.split(",");
		TaskData task = null;
		for (String taskId : taskIds) {
			if (StringUtil.isEmpty(taskId)) {
				continue;
			}
			task = player.taskManager.getTaskByID(Integer.parseInt(taskId));
			if (task != null) {
				break;
			}
		}
		if (task == null) {
			Out.error(player.getId() + " don't have CALL_BOSS QUEST");
			return;
		}

		Area area = (Area) this.params[0];
		String[] location = ((String) this.params[1]).split(":");
		int x = Integer.parseInt(location[0]);
		int y = Integer.parseInt(location[1]);
		int monsterId = Integer.parseInt(task.prop.targetID);

		MonsterBase prop = MonsterConfig.getInstance().get(monsterId);
		List<MonsterUnit> data = new ArrayList<>();
		MonsterUnit unit = new MonsterUnit();
		unit.id = monsterId;
		unit.x = x;
		unit.y = y;
		unit.force = Const.AreaForce.MONSTER.value;
		unit.autoGuard = true;
		unit.unique = true;
		unit.shareType = prop.shareType;
		data.add(unit);
		area.addUnitsToArea(data);

		return;				
	}
	
	
	private void onChangeAreaEvent() {
		int areaId = (int) this.params[0];
		int targetX = (int) this.params[1];
		int targetY = (int) this.params[2];
		AreaUtil.enterArea(player, areaId, targetX, targetY);
	}
	
	private void onLoopTransformEvent() {
		String[] location = String.valueOf(this.params[1]).split(":");
		int targetX = Integer.parseInt(location[0]);
		int targetY = Integer.parseInt(location[1]);
		int areaId = 0;
		TaskPO taskData = null;
		// if (location.length == 3) {
		// taskIndex = Integer.parseInt(location[2]); // 当前是第几个一条龙任务()
		// }

		// 一条龙场景ID根据玩家等级，从任务配置表 动态获取
		Map<Integer, TaskPO> loopTasks = player.teamManager.getLoopTasks();
		if (loopTasks != null) {
			for (TaskPO task : loopTasks.values()) {
				areaId = TaskUtils.getTaskProp(task.templateId).circleDungeonID;
				taskData = task;
				break;
			}
		}
		// 切换场景，如果是队长+队伍人数>3+一条龙场景+队员为跟随,把队伍中的其它队员也带进来
		// 如果是队员操作，提示权限不足
		TeamData team = player.getTeamManager().getTeam();
		if (team != null && areaId != 0 && taskData != null && taskData.state == TaskState.NOT_COMPLETED.getValue()) {
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
						MessageUtil.sendSysTip(player, LangService.getValue("TEAM_MEMBER_LEVEL_LIMIT").replace("{level}", String.valueOf(openConfig.openLv)), TipsType.BLACK);
						break;
					}
				}

				if (success) {
					if (team.memberCount() >= Const.LOOP_TASK_TEAM_MEMBER_COUNT) {
						if (team.followCount() == team.memberCount()) {
							team.confirm = false; // 队伍进入副本设置为无需确认
						}
						AreaUtil.enterArea(player, areaId, targetX, targetY);
					} else {
						MessageUtil.sendSysTip(player, LangService.getValue("TASK_NEED_TEAM"), TipsType.BLACK);
					}
				}
			} else {
				MessageUtil.sendSysTip(player, LangService.getValue("TEAM_NO_AUTHORITY"), TipsType.BLACK);
			}
		}
	
	}
	
	
	@Override
	public void run() {
		if (processGuildBoss()) {
			return;
		}
		// Out.debug("onevent:::", this.type);
		if (player.onSaveRebirth(this)) {
			return;
		}

		player.taskManager.onEvent(this);
		player.achievementManager.onTaskEvent(this);
		player.bag.onEvent(this);
		player.sceneProgressManager.onEvent(this);
		player.mountManager.onEvent(this);		
		player.functionOpenManager.onEvent(this);

		if (this.type == EventType.interActiveItem.getValue()) {
			onInterActiveItemEvent();			
		} else if (this.type == EventType.addUnit.getValue()) {// 召唤boss
			onAddUnitEvent();
		}else if (this.type == EventType.changeArea.getValue()) {
			onChangeAreaEvent();
		} else if (this.type == EventType.loopTransform.getValue()) {
			onLoopTransformEvent();
		}
		player = null;
		params = null;
	}

}
