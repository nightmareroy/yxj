package com.wanniu.game.task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.wanniu.core.common.StringString;
import com.wanniu.core.game.LangService;
import com.wanniu.core.logfs.Out;
import com.wanniu.core.util.StringUtil;
import com.wanniu.game.GWorld;
import com.wanniu.game.bag.WNBag;
import com.wanniu.game.bag.WNBag.SimpleItemInfo;
import com.wanniu.game.common.Const;
import com.wanniu.game.common.Const.ForceType;
import com.wanniu.game.common.Const.GOODS_CHANGE_TYPE;
import com.wanniu.game.common.Const.Position;
import com.wanniu.game.common.Const.TaskKind;
import com.wanniu.game.common.Const.TaskState;
import com.wanniu.game.common.Const.TaskType;
import com.wanniu.game.common.msg.MessageUtil;
import com.wanniu.game.daoyou.DaoYouService;
import com.wanniu.game.data.BranchLineCO;
import com.wanniu.game.data.CharacterLevelCO;
import com.wanniu.game.data.CircleRewardCO;
import com.wanniu.game.data.DailyReduceCO;
import com.wanniu.game.data.DailyRewardCO;
import com.wanniu.game.data.GameData;
import com.wanniu.game.data.TaskChestCO;
import com.wanniu.game.data.base.DItemBase;
import com.wanniu.game.data.base.TaskBase;
import com.wanniu.game.data.base.TaskBase.ItemNode;
import com.wanniu.game.equip.EquipManager.EquipAndLevelData;
import com.wanniu.game.equip.NormalEquip;
import com.wanniu.game.item.ItemUtil;
import com.wanniu.game.item.NormalItem;
import com.wanniu.game.item.data.ItemToBtlServerData;
import com.wanniu.game.mail.SysMailConst;
import com.wanniu.game.message.MessageData;
import com.wanniu.game.message.MessageData.MessageData_Quest;
import com.wanniu.game.player.BILogService;
import com.wanniu.game.player.GlobalConfig;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.player.po.MiscData;
import com.wanniu.game.poes.PlayerBasePO.EquipStrengthPos;
import com.wanniu.game.poes.TaskListPO;
import com.wanniu.game.request.task.AcceptTaskHandler.AcceptTaskResult;
import com.wanniu.game.task.po.TaskPO;
import com.wanniu.game.team.TeamData;
import com.wanniu.game.team.TeamData.TeamMemberData;

import pomelo.area.TaskHandler.TaskAutoPush;
import pomelo.area.TaskHandler.TaskUpdatePush;
import pomelo.task.TaskOuterClass.Task;
import pomelo.task.TaskOuterClass.Tasks;

public class PlayerTasks {

	public static class NotifyToBattleServerParam {
		public String playerUUID;
		public String questID;
		public boolean initStatus;
		public List<StringString> status;
	}

	public static class DropItemServerData {
		public Position pos;
		public List<ItemToBtlServerData> items;
	}

	private WNPlayer player;
	public TaskListPO taskListPO;
	/** 皓月镜每5轮结算参数 */
	public LoopResult loopResult;

	private int playerPro;
	public Map<Integer, TaskPO> normalTasks;
	public Map<Integer, Integer> finishedNormalTasks;
	public Map<Integer, TaskPO> dailyTasks;
	public Map<Integer, Integer> finishedDailyTasks;
	public Map<Integer, TaskPO> treasureTasks;
	public Map<Integer, Integer> finishedTreasureTasks;
	private Map<Integer, List<TaskData>> eventWatchers;

	public PlayerTasks(WNPlayer player, TaskListPO tasks) {
		this.player = player;
		playerPro = player.getPro();
		this.taskListPO = tasks;
		this.init(tasks);
	}

	private final void init(TaskListPO data) {
		// 接取的普通任务
		this.normalTasks = data.normalTasks;
		// 完成的普通任务
		this.finishedNormalTasks = data.finishedNormalTasks;

		if (GWorld.DEBUG) {
			if (data.treasureTasks == null) {
				data.treasureTasks = new HashMap<>();
			}
			if (data.finishedTreasureTasks == null) {
				data.finishedTreasureTasks = new HashMap<>();
			}
			if (data.loopResult == null) {
				data.loopResult = new LoopResult();
			}
		}

		this.dailyTasks = data.dailyTasks;
		this.finishedDailyTasks = data.finishedDailyTasks;

		this.treasureTasks = data.treasureTasks;
		this.finishedTreasureTasks = data.finishedTreasureTasks;

		this.loopResult = data.loopResult;

		this.refreshWatchEvents();
	}

	/**
	 * 凌晨刷新
	 */
	public final void refreshNewDay() {
		// 日常任务改全部重置
		// 公会相关
		MiscData miscData = player.playerAttachPO.miscData;
		if (miscData != null) {
			miscData.guildBlessToday = 0;
			miscData.guildSkillUpToday = 0;
			miscData.guildDonateToday = 0;
		}

		this.finishedDailyTasks = new HashMap<>();
		// * 清除师门，一条龙当日任务次数(在线玩家0点重置，离线玩家上线重置)
		if (this.taskListPO != null) {
			this.taskListPO.todayLoopTaskCount = 0;
			this.taskListPO.todayDailyTaskCount = 0;
		}

		// 每日推送 接师门，接皓月镜，完成活跃度任务
		for (TaskBase prop : GameData.BranchLines.values()) {
			if (this.player.getLevel() < prop.level) {
				continue;
			}
			if (this.normalTasks.containsKey(prop.iD)) {
				// 活跃度任务，每日重置任务
				if (prop.type == TaskType.ACTIVITY_NUM.getValue() || prop.type == TaskType.FINISH_DUNGEONS_COUNT.getValue() || prop.type == TaskType.KILL_BOSS_COUNT.getValue()) {
					TaskData task = new TaskData(this.normalTasks.get(prop.iD));
					task.setProgress(0);
					pushTaskUpdate(task);
				}
				continue;
			}
			if (prop.type == TaskType.ACTIVITY_NUM.getValue() || prop.type == TaskType.FINISH_DUNGEONS_COUNT.getValue() || prop.type == TaskType.KILL_BOSS_COUNT.getValue()) {
				AcceptTaskResult result = acceptTask(prop.iD, TaskKind.BRANCH);
				if (result.task != null) {
					result.task.setSecProgress(getSecProgress(result.task.getKind()));
					pushTaskUpdate(result.task);
				}
			} else if (prop.type == TaskType.ACCEPT_DAILY_LOOP.getValue() && StringUtil.isNotEmpty(prop.targetID)) {
				int targetId = Integer.parseInt(prop.targetID);
				if (targetId == TaskKind.DAILY && dailyTasks.size() == 0) {
					AcceptTaskResult result = acceptTask(prop.iD, TaskKind.BRANCH);
					if (result.task != null) {
						result.task.setSecProgress(getSecProgress(result.task.getKind()));
						pushTaskUpdate(result.task);
					}
				} else if (targetId == TaskKind.LOOP) {
					TeamData team = player.getTeamManager().getTeam();
					if (team == null || (team != null && (team.loopTasks == null || team.loopTasks.size() == 0))) {
						AcceptTaskResult result = acceptTask(prop.iD, TaskKind.BRANCH);
						if (result.task != null) {
							result.task.setSecProgress(getSecProgress(result.task.getKind()));
							pushTaskUpdate(result.task);
						}
						// TaskData task = new TaskData(prop);
						// task.setSecProgress(task.getKind());
						// this.pushTaskUpdate(task);
					}
				}
			}
		}

		this.refreshWatchEvents();
	}

	public final void refreshWatchEvents() {
		this.eventWatchers = new ConcurrentHashMap<>();

		for (TaskPO db : normalTasks.values()) {
			TaskData task = new TaskData(db);
			int type = task.getType();
			List<TaskData> tasks = this.eventWatchers.get(type);
			if (tasks == null) {
				tasks = new ArrayList<>();
				this.eventWatchers.put(type, tasks);
			}
			tasks.add(task);

		}
		for (TaskPO db : dailyTasks.values()) {
			TaskData task = new TaskData(db);
			int type = task.getType();
			List<TaskData> tasks = this.eventWatchers.get(type);
			if (tasks == null) {
				tasks = new ArrayList<>();
				this.eventWatchers.put(type, tasks);
			}
			tasks.add(task);
		}
		// // 一条龙任务
		// Map<Integer, TaskData> loopTasks = player.teamManager.getLoopTasks();
		// if (loopTasks != null) {
		// for (TaskData task : loopTasks.values()) {
		// int type = task.getType();
		// List<TaskData> tasks = this.eventWatchers.get(type);
		// if (tasks == null) {
		// tasks = new ArrayList<>();
		// this.eventWatchers.put(type, tasks);
		// }
		// tasks.add(task);
		// }
		// }
		// 挖宝任务
		for (TaskPO db : this.treasureTasks.values()) {
			TaskData task = new TaskData(db);
			int type = task.getType();
			List<TaskData> tasks = this.eventWatchers.get(type);
			if (tasks == null) {
				tasks = new ArrayList<>();
				this.eventWatchers.put(type, tasks);
			}
			tasks.add(task);
		}
	}

	/**
	 * 同步战斗服数据
	 */
	public final JSONArray toJson4BattleServer() {
		JSONArray data = new JSONArray();
		for (Map.Entry<Integer, TaskPO> node : this.normalTasks.entrySet()) {
			TaskData task = new TaskData(node.getValue());
			if (task.getState() > TaskState.NOT_START.getValue()) {
				data.add(task.toJson4BattleServer());
			}
		}
		for (Map.Entry<Integer, TaskPO> node : this.dailyTasks.entrySet()) {
			TaskData task = new TaskData(node.getValue());
			if (task.getState() > TaskState.NOT_START.getValue()) {
				data.add(task.toJson4BattleServer());
			}
		}
		// 一条龙
		Map<Integer, TaskPO> loopTasks = player.teamManager.getLoopTasks();
		if (loopTasks != null) {
			for (Map.Entry<Integer, TaskPO> node : loopTasks.entrySet()) {
				TaskData task = new TaskData(node.getValue());
				if (task.getState() > TaskState.NOT_START.getValue()) {
					data.add(task.toJson4BattleServer());
				}
			}
		}
		// 挖宝任务
		for (Map.Entry<Integer, TaskPO> node : this.treasureTasks.entrySet()) {
			TaskData task = new TaskData(node.getValue());
			if (task.getState() > TaskState.NOT_START.getValue()) {
				data.add(task.toJson4BattleServer());
			}
		}
		return data;
	}

	/** 重载方法 */
	public final void dealTaskEvent(TaskType type, int num) {
		this.onEvent(new TaskEvent(type, null, num));
	}

	public final void dealTaskEvent(TaskType type, String target, int num) {
		this.onEvent(new TaskEvent(type, target, num));
	}

	public final TaskData getTask(int templateId, int kind) {
		if (kind == TaskKind.MAIN || kind == TaskKind.BRANCH) {
			if (this.normalTasks.containsKey(templateId)) {
				return new TaskData(this.normalTasks.get(templateId));
			}
		} else if (kind == TaskKind.DAILY) {
			if (this.dailyTasks.containsKey(templateId)) {
				return new TaskData(this.dailyTasks.get(templateId));
			}
		} else if (kind == TaskKind.LOOP) {
			// 一条龙任务从队伍中获取
			TeamData team = player.getTeamManager().getTeam();
			if (team != null && team.loopTasks != null && team.loopTasks.containsKey(templateId)) {
				return new TaskData(team.loopTasks.get(templateId));
			}
		} else if (kind == TaskKind.TREASURE) {
			if (this.treasureTasks.containsKey(templateId)) {
				return new TaskData(this.treasureTasks.get(templateId));
			}
		} else {
			Out.error("getTask is null:templateId=", templateId, ",kind=", kind);
		}
		return null;
	}

	public final TaskData getTaskByID(int templateId) {
		TaskPO task = this.normalTasks.get(templateId);
		if (task == null) {
			task = this.dailyTasks.get(templateId);
		}
		if (task == null) {
			TeamData team = player.getTeamManager().getTeam();
			if (team != null && team.loopTasks != null) {
				task = team.loopTasks.get(templateId);
			}
		}
		if (task == null) {
			task = this.treasureTasks.get(templateId);
		}
		if (task != null) {
			return new TaskData(task);
		}
		return null;
	}

	public final boolean isTaskDoingOrFinish(int templateId) {
		TaskData task = this.getTaskByID(templateId);
		if (task != null) {
			if (task.getState() >= TaskState.NOT_COMPLETED.getValue()) {
				return true;
			}
		}
		if (this.isFinish(templateId, TaskKind.MAIN)) {
			return true;
		}
		return false;
	}

	public final boolean isTaskDoing(int templateId) {
		TaskData task = this.getTaskByID(templateId);
		if (task != null) {
			if (task.getState() == TaskState.NOT_COMPLETED.getValue()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 正在进行中的任务是否有某个类型的任务
	 * 
	 * @param opts StringInt stringValue:id intValue:type
	 * @returns {*}
	 */
	public final TaskData doingTaskHaveType(int taskType) {
		Out.debug("doingTaskHaveType:", taskType);
		List<TaskData> all = eventWatchers.get(taskType);
		if (all != null && all.size() > 0)
			for (TaskData task : all) {
				if (task != null && task.getState() == TaskState.NOT_COMPLETED.getValue()) {
					return task;
				}
			}
		return null;
	}

	public final boolean hasMainLineTask() {
		for (Map.Entry<Integer, TaskPO> node : this.normalTasks.entrySet()) {
			TaskData task = new TaskData(node.getValue());
			if (task != null && task.getKind() == TaskKind.MAIN) {
				return true;
			}
		}
		return false;
	}

	private final boolean isDailyFinish(int templateId) {
		return this.finishedDailyTasks.containsKey(templateId);
	}

	private final boolean isTreasureFinish(int templateId) {
		return this.finishedTreasureTasks.containsKey(templateId);
	}

	private final boolean isLoopFinish(int templateId) {
		TeamData team = player.getTeamManager().getTeam();
		if (team != null && team.finishedLoopTasks != null) {
			return team.finishedLoopTasks.containsKey(templateId);
		}
		return false;
	}

	private final boolean isNormalFinish(int templateId) {
		return this.finishedNormalTasks.containsKey(templateId);
	}

	private final boolean isFinish(int templateId, int kind) {
		switch (kind) {
		case TaskKind.DAILY:
			return isDailyFinish(templateId);
		case TaskKind.LOOP:
			return isLoopFinish(templateId);
		case TaskKind.TREASURE:
			return isTreasureFinish(templateId);
		default:
			return isNormalFinish(templateId);
		}
	}

	public final boolean isFinishTask(int templateId) {
		return isDailyFinish(templateId) || isNormalFinish(templateId) || isTreasureFinish(templateId);
	}

	/**
	 * 能否接取任务
	 */
	private final String _canAcceptTaskByProp(TaskBase prop) {
		if (prop == null) {
			return LangService.getValue("TASK_TEMPLATE_NOT_EXIST");
		}
		int templateId = prop.getId();
		int kind = prop.getKind();

		// 检查是否在时间段之内
		if (TaskUtils.checkOpenWay(prop.openWay, prop.openDay)) {
			if (!TaskUtils.checkTaskValidTime(prop.openTime, prop.endTime)) {
				return LangService.getValue("TASK_TIME_IS_NOT_OPEN");
			}
		}
		// 职业限制
		if (StringUtil.isNotEmpty(prop.job)) {
			if (prop.pro != player.getPro()) {
				// 职业不符合
				return LangService.getValue("TASK_JOB_ERROR");
			}
		}

		// 如果已完成过...
		if (kind == TaskKind.MAIN || kind == TaskKind.BRANCH) {
			if (isFinish(templateId, prop.getKind())) {
				return LangService.getValue("TASK_IS_ACCPETED");
			}
		}

		// 是否已经接取
		TaskData task = this.getTask(templateId, kind);
		if (task != null && task.getState() != TaskState.NOT_START.getValue()) {
			Out.debug("task not has get ");
			// 已经接取,不能重复接取
			return LangService.getValue("TASK_IS_ACCPETED");
		}

		if (prop.kind != TaskKind.LOOP) {
			// 等级限制
			if (player.getPlayer().upLevel < prop.upOrder) {
				return LangService.getValue("TASK_UPLEVEL_ERROR");
			} else if (player.getPlayer().level < prop.level) {
				return LangService.format("TASK_LEVEL_ERROR", prop.level);
			}

			// 阶级上限限制
			if (player.getPlayer().upLevel > prop.upLimit) {
				return LangService.getValue("TASK_UPLEVEL_ERROR");
			} else if (prop.levelLimit > 0 && player.getPlayer().upLevel == prop.upLimit && player.getPlayer().level > prop.levelLimit) {
				return LangService.getValue("TASK_UPLEVEL_ERROR");
			}
		}
		// 前置任务是否完成(只有主线，支线任务才有前置任务要求, 66,67接取师门一条龙，完成活跃度是每日定时推送的，不需要前置检测)
		if (prop.kind != TaskKind.DAILY && prop.type != TaskType.ACCEPT_DAILY_LOOP.getValue() && prop.type != TaskType.ACTIVITY_NUM.getValue() && prop.type != TaskType.FINISH_DUNGEONS_COUNT.getValue() && prop.type != TaskType.KILL_BOSS_COUNT.getValue()) {
			if (prop.beforeRelations > 0) {
				// 前置条件或的关系
				for (int i = 0; i < prop.beforeTask.length; i++) {
					int beforeTask = Integer.parseInt(prop.beforeTask[i]);
					if (beforeTask != 0) {
						if (isFinish(beforeTask, prop.getKind())) {
							return null;
						}
					}
				}
				return LangService.getValue("TASK_BEFORE_ERROR");
			} else {
				// 前置条件和的关系
				for (int i = 0; i < prop.beforeTask.length; i++) {
					int beforeTask = Integer.parseInt(prop.beforeTask[i]);
					if (beforeTask != 0) {
						if (!isFinish(beforeTask, prop.getKind())) {
							Out.debug("beforeTask not finished beforeTask:" + beforeTask);
							return LangService.getValue("TASK_BEFORE_ERROR");
						}
					}
				}
				return null;
			}
		}
		return null;
	}

	private final boolean _isDiscardItemTask(TaskData task) {
		if (task.getType() == TaskType.DISCARD_ITEM.getValue()) {
			task.db.state = TaskState.NOT_COMPLETED.getValue();
			String itemId = task.prop.targets.get(0);
			if (itemId.length() > 0) {
				if (player.getWnBag().findItemNumByCode(itemId) >= task.getTargetNum()) {
					this.dealTaskEvent(TaskType.DISCARD_ITEM, itemId, task.getTargetNum());
					task.setSecProgress(getSecProgress(task.getKind()));
					this.pushTaskUpdate(task);
				}
			}
		} else {
			if (task.getTargetNum() != 0) {
				task.db.state = TaskState.NOT_COMPLETED.getValue();
			}
		}
		return true;
	}

	/**
	 * 提交道具任务
	 * 
	 * @param task
	 * @return
	 */
	private final boolean _discardItem(TaskData task) {
		if (task.getType() == TaskType.DISCARD_ITEM.getValue()) {
			String itemId = task.prop.targets.get(0);
			if (itemId.length() > 0) {
				Out.debug("accept code:", itemId);
				boolean flag = player.getWnBag().discardItem(itemId, task.getTargetNum(), Const.GOODS_CHANGE_TYPE.task_submit, null, false, false);
				// 把任务状态设置为已完成
				task.db.state = TaskState.COMPLETED_NOT_DELIVERY.getValue();
				return flag;
			}
		}
		return true;
	}

	public final void gmNewTask(int templateId) {
		TaskData task = this.getTaskByID(templateId);
		if (task != null) {
			task.db.state = TaskState.NOT_START.getValue();
		} else {
			TaskBase prop = TaskUtils.getTaskProp(templateId);
			task = TaskUtils.createTask(prop);
			this._addTask(task);
		}
		task.setSecProgress(getSecProgress(task.getKind()));
		this.pushTaskUpdate(task);
	}

	public final void gmfinishTaskTarget(int templateId) {
		TaskData task = this.getTaskByID(templateId);
		if (task != null) {
			this.complete(task);
		}
	}

	public final void gmDiscardTaskByID(int templateId) {
		if (templateId == 0) {
			Out.debug("------------------deleteTask------all------------");
			Map<Integer, TaskPO> all = new HashMap<>();
			all.putAll(this.normalTasks);
			all.putAll(this.dailyTasks);
			TeamData team = player.getTeamManager().getTeam();
			if (team != null && team.loopTasks != null) {
				all.putAll(team.loopTasks);
			}
			all.putAll(this.treasureTasks);
			for (Map.Entry<Integer, TaskPO> node : all.entrySet()) {
				TaskData task = new TaskData(node.getValue());
				task.db.state = TaskState.DELETE.getValue();
				this._deleteTask(task.db.templateId, task.prop.getKind(), true);
				task.setSecProgress(getSecProgress(task.getKind()));
				this.pushTaskUpdate(task);
			}
		} else {
			TaskData task = this.getTaskByID(templateId);
			if (task != null) {
				Out.debug("------------------deleteTask------------------", templateId);
				task.db.state = TaskState.DELETE.getValue();
				this._deleteTask(task.db.templateId, task.prop.getKind(), true);
				task.setSecProgress(getSecProgress(task.getKind()));
				this.pushTaskUpdate(task);
			}
		}
	}

	public final void gmAcceptTask(int templateId) {
		TaskBase prop = TaskUtils.getTaskProp(templateId);
		TaskData task = TaskUtils.createTask(prop, TaskState.NOT_COMPLETED);
		this._isDiscardItemTask(task);
		this._addTask(task);

		// 接受任务的奖励
		List<ItemNode> rewards = task.prop.accepTaskRewards;
		Out.debug("AccepTaskReward", rewards);
		List<SimpleItemInfo> items = new ArrayList<>();
		for (ItemNode node : rewards) {
			SimpleItemInfo map = new SimpleItemInfo();
			map.itemCode = node.itemCode;
			map.itemNum = node.itemNum;
			map.forceType = Const.ForceType.BIND; // 任务奖励物品都给绑定的
													// Const.ForceType.getE(node.isBind);
			items.add(map);
		}
		if (!player.getWnBag().testAddCodeItems(items, Const.ForceType.BIND, false)) {
			return;
		}
		// 任务奖励
		Out.debug("AccepTask reward ", rewards);
		player.getWnBag().addCodeItems(items, Const.GOODS_CHANGE_TYPE.task, null, false, false);

		// 接任务触发功能开放
		player.functionOpenManager.onAcceptTask(String.valueOf(templateId));

		// 通知战斗服任务已接取, 合并到initStatus为true的QuestStatusChangedR2B消息中
		List<StringString> attr = task.initBattleServerAttribute();
		NotifyToBattleServerParam param1 = new NotifyToBattleServerParam();
		param1.playerUUID = player.getId();
		param1.questID = String.valueOf(templateId);
		param1.initStatus = true;
		param1.status = attr;
		this.notifyToBattleServer("QuestStatusChangedR2B", param1);

		this._checkProCondition(task);
		task.setSecProgress(getSecProgress(task.getKind()));
		this.pushTaskUpdate(task);
	};

	/**
	 * 接受任务
	 * 
	 * @param templateId
	 * @param kind
	 */
	public final AcceptTaskResult acceptTask(int templateId, int kind) {
		Out.debug("acceptTask templateId: ", templateId, " kind: ", kind);
		AcceptTaskResult result = new AcceptTaskResult();
		TaskBase prop = TaskUtils.getTaskProp(templateId);
		String canAcceptRes = this._canAcceptTaskByProp(prop);
		if (canAcceptRes != null) {
			result.msg = canAcceptRes;
			return result;
		}
		result.task = this.getTask(templateId, kind);
		if (result.task == null) {
			result.task = TaskUtils.createTask(prop);
			if (result.task.getTargetNum() == 0) {
				// 任务目标数为0,则修改任务状态
				result.task.db.state = TaskState.NOT_COMPLETED.getValue();
			}
			this._addTask(result.task);
		}

		if (!this._isDiscardItemTask(result.task)) {
			result.msg = LangService.getValue("TASK_BEFORE_ERROR");
			return result;
		}

		// 判断是否需要变身
		if (StringUtil.isNotEmpty(result.task.prop.modID)) {
			String[] data = result.task.prop.modID.split(":");
			player.playerBasePO.model = data[0];
			player.playerBasePO.speed = Float.parseFloat(data[1]);
			player.refreshBattlerServerAvatar();
			player.refreshBattlerServerEffect(false);
		}

		this._checkProCondition(result.task);

		// 接受任务的奖励
		List<ItemNode> rewards = result.task.prop.accepTaskRewards;
		if (rewards != null) {
			WNBag bag = player.getWnBag();
			List<SimpleItemInfo> rItems = new ArrayList<>(rewards.size());
			for (ItemNode node : rewards) {
				SimpleItemInfo map = new SimpleItemInfo();
				map.itemCode = node.itemCode;
				map.itemNum = node.itemNum;
				map.forceType = Const.ForceType.BIND; // node.isBind;
				rItems.add(map);
			}
			if (!bag.testAddCodeItems(rItems, Const.ForceType.BIND, false)) {
				return result;
			}
			List<NormalItem> finalRewards = new ArrayList<>();
			for (int i = 0; i < rewards.size(); ++i) {
				ItemNode node = rewards.get(i);
				List<NormalItem> items = ItemUtil.createItemsByItemCode(node.itemCode, node.itemNum);
				for (NormalItem item : items) {
					item.setBind(1);
					finalRewards.add(item);
				}
			}
			bag.addEntityItems(finalRewards, Const.GOODS_CHANGE_TYPE.task);
		}

		// 接任务触发功能开放
		player.functionOpenManager.onAcceptTask(String.valueOf(templateId));

		// 完成接取师门任务
		if (kind == TaskKind.DAILY || kind == TaskKind.LOOP) {
			player.taskManager.dealTaskEvent(TaskType.ACCEPT_DAILY_LOOP, String.valueOf(kind), 1);
		}

		TeamData team = player.getTeamManager().getTeam();
		if (team != null && team.leaderId.equals(player.getId()) && kind == TaskKind.LOOP) {
			for (TeamMemberData member : team.teamMembers.values()) {
				WNPlayer mPlayer = member.getPlayer();
				if (mPlayer == null) {
					continue;
				}
				questStatusChangeR2B(mPlayer, result.task, templateId);
			}
		} else {
			questStatusChangeR2B(this.player, result.task, templateId);
		}

		// 接受任务
		BILogService.getInstance().ansycReportMission(player.getPlayer(), "任务接取", kind, prop.iD, prop.name);
		return result;
	}

	public void questStatusChangeR2B(WNPlayer player, TaskData task, int templateId) {
		// 通知战斗服任务已接取, 合并到initStatus为true的QuestStatusChangedR2B消息中
		List<StringString> attr = task.initBattleServerAttribute();
		NotifyToBattleServerParam param1 = new NotifyToBattleServerParam();
		param1.playerUUID = player.getId();
		param1.questID = String.valueOf(templateId);
		param1.initStatus = true;
		param1.status = attr;
		player.taskManager.notifyToBattleServer("QuestStatusChangedR2B", param1);
	}

	private final void _checkProCondition(TaskData task) {
		if (task.prop.getType() == TaskType.LEVEL_UP.getValue()) {
			// 升到某个等级，角色达到指定等级时任务完成
			this.dealTaskEvent(TaskType.LEVEL_UP, player.getPlayer().level);
		} else if (task.prop.getType() == TaskType.USERUP_LEVEL.getValue()) {
			// 人物进阶
			this.dealTaskEvent(TaskType.USERUP_LEVEL, player.getPlayer().upLevel);
		} else if (task.prop.getType() == TaskType.TRAIN_EQUIP.getValue()) {
			// 装备强化
			EquipAndLevelData equips = player.equipManager.getAllEquipAndLevel();
			Out.debug("_checkProCondition:", equips);
			// for (String s : task.prop.targets) {
			// -1表示所有部位强化到 n 级, 0:任意部位强化到n级,
			if (task.prop.targetID.equals("0")) {
				this.dealTaskEvent(TaskType.TRAIN_EQUIP, task.prop.targetID, equips.maxLevel);
			} else {
				for (int pos : equips.equips.keySet()) {
					if (!String.valueOf(pos).equals(task.prop.targetID)) {
						continue;
					}
					EquipStrengthPos equip = equips.equips.get(pos);
					int equipLevel = equip.enSection * (GlobalConfig.EquipmentCraft_Enchant_MaxenLevel + 1) + equip.enLevel;
					if (equipLevel > 0) {
						this.dealTaskEvent(TaskType.TRAIN_EQUIP, String.valueOf(pos), equipLevel);
					}
				}
			}
			// }
		} else if (task.prop.getType() == TaskType.TRAIN_EQUIP_ALL.getValue()) {
			TaskUtils.dealTrainEquipAllTask(player);
		} else if (task.prop.getType() == TaskType.FILL_GEM.getValue()) {
			// 镶嵌宝石
			for (EquipStrengthPos posInfo : player.equipManager.strengthPos.values()) {
				for (String code : posInfo.gems.values()) {
					if (StringUtil.isEmpty(code))
						continue;
					DItemBase prop = ItemUtil.getUnEquipPropByCode(code);
					this.dealTaskEvent(TaskType.FILL_GEM, String.valueOf(prop.levelReq), 1);
				}
			}
		} else if (task.prop.getType() == TaskType.FRIEND_NUM.getValue()) {
			// 好友相关
			int frinedNum = player.getFriendManager().getFriendsNum();
			if (frinedNum > 0) {
				this.dealTaskEvent(TaskType.FRIEND_NUM, frinedNum);
			}
		} else if (task.prop.getType() == TaskType.JOIN_LEAGUE.getValue()) {
			// 加入盟
			if (DaoYouService.getInstance().getDaoYou(player.getId()) != null) {
				this.dealTaskEvent(TaskType.JOIN_LEAGUE, 1);
			}
		} else if (task.prop.getType() == TaskType.EARN_NECK.getValue()) {
			// 获得项链
			if (player.playerAttachPO.miscData.hasGetNeck > 0) {
				this.dealTaskEvent(TaskType.EARN_NECK, 1);
			}
		} else if (task.prop.getType() == TaskType.ADD_GUILD.getValue()) {
			// 加入工会
			if (player.guildManager.isInGuild()) {
				this.dealTaskEvent(TaskType.ADD_GUILD, 1);
			}
		} else if (task.prop.getType() == TaskType.JOIN_SOLO.getValue()) {
			// 参加单挑王
			if (player.soloManager.soloData.havePlayed) {
				this.dealTaskEvent(TaskType.JOIN_SOLO, 1);
			}
		} else if (task.prop.getType() == TaskType.GUILD_PRAY.getValue()) {
			// 公会祈福
			if (player.playerAttachPO.miscData.guildBlessToday > 0) {
				this.dealTaskEvent(TaskType.GUILD_PRAY, player.playerAttachPO.miscData.guildBlessToday);
			}
		} else if (task.prop.getType() == TaskType.GUILD_DONATE.getValue()) {
			// 公会捐献
			if (player.playerAttachPO.miscData.guildDonateToday > 0) {
				this.dealTaskEvent(TaskType.GUILD_DONATE, player.playerAttachPO.miscData.guildDonateToday);
			}
		} else if (task.prop.getType() == TaskType.GUILD_TECH_UP.getValue()) {
			// 升级公会修行
			if (player.playerAttachPO.miscData.guildSkillUpToday > 0) {
				this.dealTaskEvent(TaskType.GUILD_TECH_UP, player.playerAttachPO.miscData.guildSkillUpToday);
			}
		} else if (task.prop.getType() == TaskType.TAKE_EQUIP_Qt.getValue()) {
			// 穿装备要求：品质等级|品质色
			Map<Integer, NormalEquip> equips = player.equipManager.equips;
			for (Map.Entry<Integer, NormalEquip> node : equips.entrySet()) {
				NormalEquip equip = node.getValue();
				String[] conditionArr = task.prop.targets.get(0).split("\\|");
				if (conditionArr.length > 1) {
					if (equip.getQLevel() >= Integer.parseInt(conditionArr[0]) && equip.getQColor() >= Integer.parseInt(conditionArr[1])) {
						String tarSz = equip.getQLevel() + "|" + equip.getQColor() + "|" + equip.getPosition();
						this.dealTaskEvent(TaskType.TAKE_EQUIP_Qt, tarSz, 1);
					}
				}
			}
		} else if (task.prop.getType() == TaskType.MOUNT_UPLEVEL.getValue()) {
			// 坐骑进阶等级
			if (player.mountManager != null && player.mountManager.mount != null) {
				int mountLevel = player.mountManager.mount.rideLevel * 11 + player.mountManager.mount.starLv;
				if (mountLevel > 0) {
					this.dealTaskEvent(TaskType.MOUNT_UPLEVEL, mountLevel);
				}
			}
		} else if (task.prop.getType() == TaskType.TRAIN_RIDE.getValue()) {
			// if (player.mountManager != null && player.mountManager.mount != null) {
			// int mountLevel = player.mountManager.mount.rideLevel * 11 +
			// player.mountManager.mount.starLv;
			// if (mountLevel > 0) {
			// this.dealTaskEvent(TaskType.TRAIN_RIDE, mountLevel);
			// }
			// }
		} else if (task.prop.getType() == TaskType.PET_TRAIN.getValue()) {
			// 会导致 战斗中宠物升级了，也能直接完成任务
			// if (player.petNewManager != null && player.petNewManager.petsPO
			// != null) {
			// for (PetNewPO pet : player.petNewManager.petsPO.pets.values()) {
			// if (pet.exp > 0 || pet.level > 0) {
			// this.dealTaskEvent(TaskType.PET_TRAIN, String.valueOf(pet.id),
			// 1);
			// break;
			// }
			// }
			// }
		} else if (task.prop.getType() == TaskType.ACCEPT_DAILY_LOOP.getValue()) {
			// 完成接取师门任务
			int kind = task.prop.getKind();
			boolean completed = false;
			if (kind == TaskKind.DAILY) {
				if (this.dailyTasks.size() > 0 || this.taskListPO.todayDailyTaskCount > 0) {
					completed = true;
				}
			} else if (kind == TaskKind.LOOP) {
				TeamData team = player.teamManager.getTeam();
				if (team != null && team.loopTasks != null && team.loopTasks.size() > 0) {
					completed = true;
				} else if (this.taskListPO.todayLoopTaskCount > 0) {
					completed = true;
				}
			}
			if (completed) {
				player.taskManager.dealTaskEvent(TaskType.ACCEPT_DAILY_LOOP, String.valueOf(kind), 1);
			}
		} else if (task.prop.getType() == TaskType.ROLE_UPGRADE.getValue()) {
			if (player.player.upLevel > 0) {
				this.dealTaskEvent(TaskType.ROLE_UPGRADE, player.player.upLevel);
			}
		}
	}

	/**
	 * 丢弃任务
	 * 
	 * @param templateId
	 * @param kind
	 * @returns {boolean}
	 */
	public final boolean discardTask(int templateId, int kind) {
		Out.debug("discardTask templateId: ", templateId, " kind: ", kind);
		TaskData task = this.getTask(templateId, kind);
		if (task != null) { // && task.getState() ==
							// TaskState.NOT_COMPLETED.getValue() TODO
							// 这里js为什么要NOT_COMPLETED才能放弃？
			Out.debug("------------------deleteTask------------------", templateId);
			task.db.state = TaskState.DELETE.getValue();
			task.setSecProgress(getSecProgress(task.getKind()));
			this._deleteTask(templateId, kind, true);

			// 一条龙任务，通知所有队员删除任务
			TeamData team = player.getTeamManager().getTeam();
			if (kind == TaskKind.LOOP && team != null) {
				for (TeamMemberData member : team.teamMembers.values()) {
					WNPlayer mPlayer = member.getPlayer();
					if (mPlayer != null) {
						mPlayer.taskManager.pushTaskUpdate(task);
					}
				}
			} else {
				this.pushTaskUpdate(task);
			}

			// TODO 丢弃任务之后再接当前任务
			// TaskBase prop = TaskUtils.getTaskProp(templateId);
			// if (this._canAcceptTaskByProp(prop).result) {
			// TaskData newTask = TaskUtils.createTask(prop);
			// this._addTask(newTask);
			// task.setSecProgress(getSecProgress(task.getKind()));
			// List<Task> temp = new ArrayList<>();
			// temp.add(newTask.buildTask());
			// this._pushTaskUpdate(temp);
			// }
			return true;
		}
		return false;
	}

	public final boolean discardTaskByID(int templateId) {
		TaskData task = this.getTaskByID(templateId);
		if (task != null && task.getState() == TaskState.NOT_COMPLETED.getValue()) {
			Out.debug("------------------deleteTask------------------", templateId);
			task.db.state = TaskState.DELETE.getValue();
			task.setSecProgress(getSecProgress(task.getKind()));
			this._deleteTask(templateId, task.getKind(), true);
			this.pushTaskUpdate(task);
			return true;
		}
		return false;
	}

	public final boolean complete(TaskData task) {
		if (task.getState() == TaskState.NOT_COMPLETED.getValue()) {
			task.complete();
			// 任务完成
			BILogService.getInstance().ansycReportMission(player.getPlayer(), "任务完成", task.getKind(), task.prop.iD, task.prop.name);

			// 战斗服相关
			NotifyToBattleServerParam param = new NotifyToBattleServerParam();
			param.playerUUID = player.getId();
			param.questID = String.valueOf(task.db.templateId);
			param.initStatus = false;
			param.status = new ArrayList<>();
			param.status.add(new StringString("state", String.valueOf(task.db.templateId)));
			this.notifyToBattleServer("QuestStatusChangedR2B", param);

			task.setSecProgress(getSecProgress(task.getKind()));
			this.pushTaskUpdate(task);
			if (task.getKind() == TaskKind.DAILY) {
				this.dailyTaskFinEvent();
			} else if (task.getKind() == TaskKind.LOOP) {
				this.loopTaskFinEvent();
			}
			return true;
		}
		return false;
	}

	/**
	 * 完成任务
	 * 
	 * @param templateId
	 * @param kind
	 * @returns {boolean}
	 */
	public final boolean completeTask(int templateId, int kind) {
		TaskData task = this.getTask(templateId, kind);
		if (task != null) {
			return this.complete(task);
		}
		return false;
	}

	public final void dailyTaskFinEvent() {
		this.dealTaskEvent(TaskType.FINISH_DAILY_TASK, 1);
		Out.info(player.getId(), ":完成了一次师门任务进度。");
		// 更新活跃度
		this.player.dailyActivityMgr.onEvent(Const.DailyType.DAILY_TASK, "0", 1);
	}

	public final void loopTaskFinEvent() {
		TeamData team = player.teamManager.getTeam();
		if (team == null) {
			return;
		}
		for (TeamMemberData member : team.teamMembers.values()) {
			WNPlayer player = member.getPlayer();
			if (player == null) {
				continue;
			}
			// 更新任务
			player.taskManager.dealTaskEvent(TaskType.FINISH_LOOP_TASK, 1);
			// 更新活跃度
			player.dailyActivityMgr.onEvent(Const.DailyType.LOOP_TASK, "0", 1);
		}
	}

	public final boolean completeTaskByID(int templateId) {
		TaskData task = this.getTaskByID(templateId);
		if (task != null) {
			return this.complete(task);
		}
		return false;
	}

	public final boolean isCompleteTaskByID(int templateId) {
		if (this.isFinishTask(templateId)) {
			return true;
		}
		TaskData task = this.getTaskByID(templateId);
		if (task != null) {
			return task.isCompleted();
		}
		return false;
	}

	/**
	 * gm 快速完成任务
	 * 
	 * @param templateId
	 * @returns {boolean}
	 */
	public final boolean gmFinishTask(int templateId) {
		if (templateId == TaskKind.MAIN) {
			for (TaskPO task : normalTasks.values()) {
				TaskBase prop = TaskUtils.getTaskProp(task.templateId);
				if (prop.getKind() == TaskKind.MAIN) {
					templateId = task.templateId;
					break;
				}
			}
		} else if (templateId == TaskKind.BRANCH) {
			for (TaskPO task : normalTasks.values()) {
				TaskBase prop = TaskUtils.getTaskProp(task.templateId);
				if (prop.getKind() == TaskKind.BRANCH) {
					templateId = task.templateId;
					break;
				}
			}
		} else if (templateId == TaskKind.DAILY) {
			for (TaskPO task : dailyTasks.values()) {
				templateId = task.templateId;
				break;
			}
		} else if (templateId == TaskKind.TREASURE) {
			for (TaskPO task : treasureTasks.values()) {
				templateId = task.templateId;
				break;
			}
		} else if (templateId == TaskKind.LOOP) {
			TeamData team = player.teamManager.getTeam();
			if (team != null) {
				for (TaskPO task : team.loopTasks.values()) {
					templateId = task.templateId;
					break;
				}
			}
		}

		TaskData task = this.getTaskByID(templateId);
		if (task != null) {
			int kind = task.getKind();
			if (task.getState() >= TaskState.NOT_START.getValue() && task.getState() <= TaskState.COMPLETED_NOT_DELIVERY.getValue()) {
				// 是否需要清除 变身状态
				if (task.prop.overState2 == 1) {
					player.playerBasePO.model = null;
					player.playerBasePO.speed = 0;
					player.refreshBattlerServerAvatar();
					player.refreshBattlerServerEffect(false);
				}

				List<ItemNode> rewards = TaskUtils.getReward(task.prop, playerPro);
				List<NormalItem> tcItems = ItemUtil.createItemsByTcCode(task.prop.tcReward);
				for (NormalItem v : tcItems) {
					if (v.prop.itemSecondType == Const.ItemSecondType.virtual.getValue()) {
						rewards.add(new ItemNode(v.getTemplate().code, v.getWorth(), 0));
					} else {
						rewards.add(new ItemNode(v.getTemplate().code, v.getTemplate().groupCount, 0));
					}
				}
				Out.debug("gmFinishTask task prop:", rewards);

				WNBag bag = player.getWnBag();
				Out.debug("gmFinishTask testAddCodeItems ");
				List<SimpleItemInfo> rItems = new ArrayList<>();
				for (ItemNode node : rewards) {
					SimpleItemInfo map = new SimpleItemInfo();
					map.itemCode = node.itemCode;
					map.itemNum = node.itemNum;
					map.forceType = Const.ForceType.BIND; // node.isBind;
					rItems.add(map);
				}

				if (!bag.testAddCodeItems(rItems, Const.ForceType.BIND, false)) {
					return false;
				}

				task.db.state = TaskState.COMPLETED.getValue();

				// 通知游戏服任务已完成
				NotifyToBattleServerParam param = new NotifyToBattleServerParam();
				param.playerUUID = player.getId();
				param.questID = String.valueOf(templateId);
				this.notifyToBattleServer("QuestCommittedR2B", param);

				task.setSecProgress(getSecProgress(task.getKind()));
				this.pushTaskUpdate(task);

				String[] nextTask = task.prop.nextTask;

				this._finishTask(templateId, kind);
				this._deleteTask(templateId, kind, false);

				TeamData team = player.teamManager.getTeam();
				if (kind == TaskKind.DAILY) {// 师门
					// 随机获取下一个师门任务
					TaskBase nextProp = TaskUtils.getRDDailyTask(player.getLevel(), templateId, player.getPro());
					nextTask = new String[] { String.valueOf(nextProp.iD) };
					this._getNextAcceptTask(kind, nextTask, task.db.finishCount);
				} else if (kind == TaskKind.LOOP) {// 一条龙
					// 给所有队员推送下一个一条龙任务
					for (TeamMemberData member : team.teamMembers.values()) {
						WNPlayer mPlayer = member.getPlayer();
						if (mPlayer == null) {
							continue;
						}
						nextTask = task.prop.nextTask;
						mPlayer.taskManager._getNextAcceptTask(kind, nextTask, task.db.finishCount);
					}
				} else {
					this._getNextAcceptTask(kind, nextTask, task.db.finishCount);
				}

				// 任务奖励
				Out.debug("gmFinishTask submit reward ", rewards);
				bag.addCodeItems(rItems, Const.GOODS_CHANGE_TYPE.task, null, false, false);
			}
		}
		if (!player.getArea().isNormal()) {
			player.fightLevelManager.leaveDungeon(player, player.getArea());
		}
		return true;
	}

	//
	public final int getTaskExp(int exp, int expRatio) {
		if (expRatio <= 0) {
			return exp;
		}
		int maxLevel = GlobalConfig.Role_LevelLimit;
		if (player.getPlayer().level >= maxLevel) {
			return exp;
		}
		// 角色等级管理
		CharacterLevelCO prop = GameData.CharacterLevels.get(player.getLevel());
		if (prop != null) {
			return exp + (expRatio * prop.experience / 10000);
		}
		return exp;
	}

	//
	/**
	 * 提交任务
	 * 
	 * @param templateId
	 * @param kind 任务种类
	 * @double 双倍领取
	 * @returns {boolean}
	 */
	public final int submitTask(int templateId, int kind, int isDouble) {
		TaskData task = this.getTask(templateId, kind);
		if (task == null || (task.getState() != TaskState.COMPLETED_NOT_DELIVERY.getValue() && task.getType() != TaskType.DISCARD_ITEM.getValue())) {
			Out.error("submitTask: task is null", player.getPlayer().name, ",kind=", kind, ",id:", templateId, "-", (task != null ? task.getState() : null));
			return -1;
		}
		List<ItemNode> rewards = TaskUtils.getReward(task.prop, playerPro);
		List<NormalItem> tcItems = ItemUtil.createItemsByTcCode(task.prop.tcReward);

		for (NormalItem v : tcItems) {
			if (v.prop.itemSecondType == Const.ItemSecondType.virtual.getValue()) {
				rewards.add(new ItemNode(v.getTemplate().code, v.getWorth(), 0));
			} else {
				rewards.add(new ItemNode(v.getTemplate().code, v.getTemplate().groupCount, 0));
			}
		}

		// 经验万分比, VIP任务获得的银两加成
		for (int i = 0; i < rewards.size(); ++i) {
			ItemNode reward = rewards.get(i);
			if (task.prop.expRatio > 0) {
				// 经验万分比
				if (reward.itemCode.equals("exp")) {
					reward.itemNum = this.getTaskExp(reward.itemNum, task.prop.expRatio);
				}
			}
			// VIP 银两收益加成
			if (reward.itemCode.equals("gold")) {
				int vip = player.baseDataManager.getVip();
				int add = 0;
				if (vip > 0) {
					add = GameData.Cards.get(vip).prv3;
				}
				reward.itemNum += reward.itemNum * add / 10000;
			}
		}

		if (isDouble > 0) {
			// 检查任务能否双倍领取
			if (task.prop.isDouble == 0) {
				return -1;
			}
			if (!player.moneyManager.enoughDiamond(task.prop.doubleCost)) {
				return -2;
			}
			for (ItemNode v : rewards) {
				v.itemNum = v.itemNum * 2;
			}
		}

		// 师门任务, 奖励根据次数折扣
		if (kind == TaskKind.DAILY) {
			for (int i = 0; i < rewards.size(); i++) {
				ItemNode reward = rewards.get(i);
				reward.itemNum = reward.itemNum * TaskUtils.getDailyAwardRate(this.taskListPO.todayDailyTaskCount) / 100;
			}
		}

		Out.debug("submit task prop:", rewards);
		WNBag bag = player.getWnBag();
		List<Map<String, Object>> rItems = new ArrayList<>();
		for (ItemNode node : rewards) {
			Map<String, Object> map = new HashMap<>();
			map.put("itemCode", node.itemCode);
			map.put("itemNum", node.itemNum);
			map.put("isBind", node.isBind);
			rItems.add(map);
		}
		// 背包满了，就自动发邮件
		// if (!bag.testAddCodeItems(rItems, Const.ForceType.BIND, false)) {
		// return -2;
		// }

		if (!this._discardItem(task)) {
			Out.error("submitTask:", player.getPlayer().name, "id:", templateId, "_discardItem");
			return -1;
		}

		TeamData team = player.getTeamManager().getTeam();
		if (task.submit()) {
			// 是否需要清除 变身状态
			if (task.prop.overState2 == 1) {
				player.playerBasePO.model = null;
				player.playerBasePO.speed = 0;
				player.refreshBattlerServerAvatar();
				player.refreshBattlerServerEffect(false);
			}

			// 通知游戏服任务已完成
			NotifyToBattleServerParam param = new NotifyToBattleServerParam();
			param.playerUUID = player.getId();
			param.questID = String.valueOf(templateId);
			this.notifyToBattleServer("QuestCommittedR2B", param);
			task.setSecProgress(getSecProgress(task.getKind()));
			// 给所有队员推送下一个一条龙任务
			if (kind == TaskKind.LOOP && team != null) {
				for (TeamMemberData member : team.teamMembers.values()) {
					WNPlayer mPlayer = member.getPlayer();
					if (mPlayer == null) {
						continue;
					}
					mPlayer.taskManager.pushTaskUpdate(task);
				}
			} else {
				this.pushTaskUpdate(task);
			}

			if (isDouble > 0) {
				player.moneyManager.costDiamond(task.prop.doubleCost, Const.GOODS_CHANGE_TYPE.task);
			}

			this._finishTask(templateId, kind);
			this._deleteTask(templateId, kind, false);

			// 一条龙任务额外奖励
			if (kind == TaskKind.LOOP && team != null && team.leaderId.equals(player.getId())) {
				// 给队伍中的每个人发奖励
				for (TeamMemberData member : team.teamMembers.values()) {
					WNPlayer player = member.getPlayer();
					if (player != null) {
						CircleRewardCO circleReward = GameData.CircleRewards.get(player.getLevel());
						if (circleReward != null) {
							Out.debug("----------------" + circleReward.gold);
							int rate = TaskUtils.getLoopAwardRate(player.taskManager.taskListPO.todayLoopTaskCount);
							if (rate > 0) {
								int award_exp = circleReward.exp * rate / 100;
								int award_upexp = circleReward.cul * rate / 100;
								// 队长前10轮加成5%
								if (team.leaderId.equals(player.getId()) && player.taskManager.taskListPO.todayLoopTaskCount <= Const.LOOP_LEADER_ADD) {
									award_exp += award_exp * 5 / 100;
									award_upexp += award_upexp * 5 / 1000;
								}
								player.addExp(award_exp, GOODS_CHANGE_TYPE.task);
								player.addUpExp(award_upexp, GOODS_CHANGE_TYPE.task);
								player.moneyManager.addGold(circleReward.gold * rate / 100, GOODS_CHANGE_TYPE.task);
							}
							for (TeamMemberData tempMember : team.teamMembers.values()) {
								WNPlayer teamMemPlayer = tempMember.getPlayer();
								if (teamMemPlayer == null) {
									continue;
								}
								int teamMemLvl = teamMemPlayer.getLevel();
								if (player.getLevel() >= teamMemLvl + 5) {
									int xianyuan = GlobalConfig.Fate_Loop;
									// if (player.getId().equals(team.leaderId) &&
									// player.taskManager.taskListPO.todayLoopTaskCount <= Const.LOOP_LEADER_ADD) {
									// xianyuan += xianyuan * 5 / 100;
									// }
									player.processXianYuanGet(xianyuan);
									break;
								}
							}
						} else {
							Out.error("一条龙等级对应的奖励未配置 level=:" + player.getLevel());
						}

						// 一条龙任务每5个任务奖励一个宝箱
						int taskCircle = player.taskManager.taskListPO.todayLoopTaskCount / Const.LOOP_TASK_ROUND_COUNT;
						if (player.taskManager.taskListPO.todayLoopTaskCount > 0 && player.taskManager.taskListPO.todayLoopTaskCount % Const.LOOP_TASK_ROUND_COUNT == 0 && taskCircle <= GlobalConfig.Quest_rewardTimes2) {
							TaskChestCO taskChest = TaskUtils.getProgressBox(TaskKind.LOOP, player.getLevel());
							player.bag.addCodeItemMail(taskChest.chest, 1, ForceType.DEFAULT, GOODS_CHANGE_TYPE.task, SysMailConst.BAG_FULL_COMMON);
						}
					}
				}

				// 当前一轮已满
				if (team.finishedLoopTasks != null && team.finishedLoopTasks.size() >= Const.LOOP_TASK_ROUND_COUNT) {
					team.onAllOverLoopTask();
					team.finishedLoopTasks.clear();
				}
				// TODO 策划王杨说 这里写死10(活跃度10)
				if (this.taskListPO.todayLoopTaskCount != 10 || this.taskListPO.loopBreaked) {

					if (this.taskListPO.todayLoopTaskCount > 0 && this.taskListPO.todayLoopTaskCount % GlobalConfig.Loop_completeTips == 0) {
						MessageData_Quest data = new MessageData_Quest();
						data.finishCount = task.db.finishCount;
						Map<String, String> strMsg = new HashMap<>();
						strMsg.put("times", String.valueOf(this.taskListPO.todayLoopTaskCount));
						MessageData message = MessageUtil.createMessage(Const.MESSAGE_TYPE.loop_task_times.getValue(), player.getId(), data, strMsg);
						message.id = player.getId();
						MessageUtil.sendMessageToPlayer(message, player.getId());
					} else {
						String[] nextTask = task.prop.nextTask;
						List<TaskData> tasks = _getNextAcceptTask(kind, nextTask, task.db.finishCount);
						if (tasks.size() > 0) {
							// 给所有队员推送下一个一条龙任务
							for (TeamMemberData member : team.teamMembers.values()) {
								WNPlayer mPlayer = member.getPlayer();
								if (mPlayer == null || mPlayer == player) {
									continue;
								}
								for (TaskData t : tasks) {
									mPlayer.taskManager.pushTaskUpdate(t);
								}
							}
						}
					}
				}
				if (this.taskListPO.todayLoopTaskCount == 10 && !this.taskListPO.loopBreaked) {
					this.taskListPO.loopBreaked = true;
				}
			} else if (kind == TaskKind.DAILY) {
				// 师门任务额外奖励
				DailyRewardCO dailyReward = GameData.DailyRewards.get(player.getLevel());
				if (dailyReward != null) {
					int rate = TaskUtils.getDailyAwardRate(this.taskListPO.todayDailyTaskCount);
					if (rate > 0) {
						player.addExp(dailyReward.exp * rate / 100, GOODS_CHANGE_TYPE.task);
						player.addUpExp(dailyReward.cul * rate / 100, GOODS_CHANGE_TYPE.task);
						player.moneyManager.addGold(dailyReward.gold * rate / 100, GOODS_CHANGE_TYPE.task);
					}
				}
				// 当日师门任务已达上限
				if (player.taskManager.taskListPO != null && player.taskManager.taskListPO.todayDailyTaskCount < GlobalConfig.Daily_completeMax) {
					// 师门10轮之后打断自动流程
					if (player.taskManager.taskListPO.todayDailyTaskCount > 0 && player.taskManager.taskListPO.todayDailyTaskCount % Const.DAILY_TASK_ROUND_COUNT == 0) {
						TaskAutoPush.Builder autoPush = TaskAutoPush.newBuilder();
						autoPush.setAuto(0);
						player.receive("area.taskPush.taskAutoPush", autoPush.build());
					}

					// 当日师门当日总数完成
					DailyReduceCO dr = GameData.DailyReduces.get(this.taskListPO.todayDailyTaskCount);
					if (this.taskListPO.todayDailyTaskCount > 0 && dr != null) {
						MessageData_Quest data = new MessageData_Quest();
						data.finishCount = task.db.finishCount;
						Map<String, String> strMsg = new HashMap<>(2);
						strMsg.put("times", String.valueOf(this.taskListPO.todayDailyTaskCount));
						strMsg.put("rate", String.valueOf(dr.rate));

						MessageData message = MessageUtil.createMessage(Const.MESSAGE_TYPE.daily_task_times.getValue(), player.getId(), data, strMsg);
						message.id = player.getId();
						MessageUtil.sendMessageToPlayer(message, player.getId());
					} else {
						// 随机获取下一个师门任务
						TaskBase nextProp = TaskUtils.getRDDailyTask(player.getLevel(), templateId, player.getPro());
						String[] nextTask = new String[] { String.valueOf(nextProp.iD) };
						this._getNextAcceptTask(kind, nextTask, task.db.finishCount);
					}
				}
				// 师门任务每10轮奖励一个宝箱
				int taskCircle = this.taskListPO.todayDailyTaskCount / Const.DAILY_TASK_ROUND_COUNT;
				if (this.taskListPO.todayDailyTaskCount > 0 && this.taskListPO.todayDailyTaskCount % Const.DAILY_TASK_ROUND_COUNT == 0 && taskCircle <= GlobalConfig.Quest_rewardTimes1) {
					TaskChestCO taskChest = TaskUtils.getProgressBox(TaskKind.DAILY, this.player.getLevel());
					this.player.bag.addCodeItemMail(taskChest.chest, 1, ForceType.DEFAULT, GOODS_CHANGE_TYPE.task, SysMailConst.BAG_FULL_COMMON);
				}

				// 扣除道具任务 是直接请求submitTask的，手动执行 dailyTaskFinEvent
				if (task.getType() == TaskType.DISCARD_ITEM.getValue()) {
					this.dailyTaskFinEvent();
				}
			} else {
				String[] nextTask = task.prop.nextTask;
				this._getNextAcceptTask(kind, nextTask, task.db.finishCount);
			}

			// 任务奖励
			Out.debug("task submit rewards ", rewards);

			List<NormalItem> finalRewards = new ArrayList<>();
			for (int i = 0; i < rewards.size(); ++i) {
				ItemNode reward = rewards.get(i);
				if (reward.itemCode.equals("exp")) {
					// 宠物相关
					if (player.petNewManager.getFightingPetId() != null) {
						int petProb = GlobalConfig.Pet_GetExp_Prob;
						if (petProb > 100) {
							petProb = 100;
						}
						int petExp = (int) Math.floor(rewards.get(i).itemNum * (petProb / 100));
						player.petNewManager.addExp(player.petNewManager.getFightingPetId(), petExp);
					}
				}
				List<NormalItem> items = ItemUtil.createItemsByItemCode(reward.itemCode, reward.itemNum);
				for (NormalItem item : items) {
					item.setBind(1);
					finalRewards.add(item);
				}
			}
			bag.addCodeItemMail(finalRewards, ForceType.DEFAULT, GOODS_CHANGE_TYPE.task, SysMailConst.BAG_FULL_COMMON);

			if (kind == TaskKind.LOOP) {
				for (TeamMemberData member : team.teamMembers.values()) {
					WNPlayer mPlayer = member.getPlayer();
					if (mPlayer == null) {
						continue;
					}
					// 完成任务触发功能开放
					mPlayer.functionOpenManager.onFinishTask(String.valueOf(templateId));
					// 成就相关
					mPlayer.achievementManager.onFinishTask(templateId);
					mPlayer.achievementManager.onFinishTaskNum(kind);
				}
			} else {
				// 完成任务触发功能开放
				player.functionOpenManager.onFinishTask(String.valueOf(templateId));
				// 成就相关
				player.achievementManager.onFinishTask(templateId);
				player.achievementManager.onFinishTaskNum(kind);
			}
			return 1;
		}
		Out.error("submitTask:", player.getPlayer().name, "id:", templateId, " submit error");
		return -1;
	};

	public final boolean updateStatus(int templateId, int kind) {
		TaskData task = this.getTask(templateId, kind);
		if (task == null) {
			return false;
		}

		if (task.getType() != TaskType.INTERACT_NPC.getValue()) {
			return false;
		}

		if (task.getState() != TaskState.NOT_COMPLETED.getValue()) {
			return false;
		}

		// 后面统一做npc位置校验
		task.complete();
		task.setSecProgress(getSecProgress(task.getKind()));
		this.pushTaskUpdate(task);
		if (kind == TaskKind.DAILY) {
			this.dailyTaskFinEvent();
		} else if (kind == TaskKind.LOOP) {
			this.loopTaskFinEvent();
		}
		// 任务完成
		BILogService.getInstance().ansycReportMission(player.getPlayer(), "任务完成", task.getKind(), task.prop.iD, task.prop.name);
		return true;
	}

	/**
	 * 事件处理
	 */
	public final void onEvent(TaskEvent event) {
		int eventType = event.type;
		Out.debug("task event handle", eventType);
		List<TaskData> tasks = this.eventWatchers.get(eventType);
		if (tasks != null && tasks.size() > 0) {
			for (TaskData task : tasks) {
				if (task.onEvent(player, event)) {
					task.setSecProgress(getSecProgress(task.getKind()));
					onComplteTask(player, event, task);
				}
			}
		}

		// 一条龙任务通知队员更新 任务目标数
		TeamData team = player.teamManager.getTeam();
		if (team != null && team.isInLoopTask()) {
			Map<Integer, TaskPO> loopTasks = team.loopTasks;
			for (TaskPO db : loopTasks.values()) {
				TaskData taskData = new TaskData(db);
				if (taskData.getType() != eventType) {
					continue;
				}
				if (taskData.onEvent(player, event)) {
					taskData.setSecProgress(getSecProgress(taskData.getKind()));
				}
				if (taskData.isCompleted()) {
					for (TeamMemberData member : team.teamMembers.values()) {
						WNPlayer mPlayer = member.getPlayer();
						if (mPlayer == null) {
							continue;
						}
						onComplteTask(mPlayer, event, taskData);
					}
				}
			}
		}
	}

	private void onComplteTask(WNPlayer player, TaskEvent event, TaskData task) {
		player.taskManager.pushTaskUpdate(task);

		if (task.db.state == TaskState.COMPLETED_NOT_DELIVERY.getValue()) {
			// 更新战斗服数据
			NotifyToBattleServerParam param = new NotifyToBattleServerParam();
			param.playerUUID = player.getId();
			param.questID = String.valueOf(task.db.templateId);
			param.initStatus = false;
			param.status = new ArrayList<>();
			param.status.add(new StringString("state", String.valueOf(TaskState.COMPLETED_NOT_DELIVERY.getValue())));
			player.taskManager.notifyToBattleServer("QuestStatusChangedR2B", param);

			// 交任务的NPCid 不填时为达成任务条件自动完成。
			if (task.prop.completeNpc == -1) {
				player.taskManager.submitTask(task.db.templateId, task.getKind(), 0);
			}
		}
	};

	//
	// 同步战斗服数据
	private final void notifyToBattleServer(String name, NotifyToBattleServerParam param) {
		player.getXmdsManager().notifyBattleServer(player.getInstanceId(), name, JSON.toJSONString(param));
	}

	//
	//
	/**
	 * 获取主线/支线后置可接任务列表 要大改
	 */
	public final List<TaskData> _getNextAcceptTask(int kind, String[] nextTasks, int finishCount) {
		List<TaskData> tasks = new ArrayList<>(2);
		TaskData task = null;
		boolean hasMain = false;
		for (String taskId : nextTasks) {
			int id = Integer.parseInt(taskId);
			TaskBase prop = TaskUtils.getTaskProp(id);
			// 如果后置任务是炼魂类型任务，则不给再接取了（炼魂任务在玩家上线就给初始化随机给玩家了）
			if (prop != null && this._canAcceptTaskByProp(prop) == null) {
				if (prop.getKind() == TaskKind.MAIN) {
					hasMain = true;
				}
				if (prop.giveNpc == 0) { // 自动接
					task = this.acceptTask(prop.getId(), prop.getKind()).task;
				} else {
					task = TaskUtils.createTask(prop);
					if (prop.getKind() == TaskKind.MAIN) {
						if (!this.hasMainLineTask() && !this.isFinishTask(id)) {
							this.normalTasks.put(task.db.templateId, task.db);
						}
					} else if (prop.getKind() == TaskKind.BRANCH) {
						this.normalTasks.put(task.db.templateId, task.db);
					} else if (prop.getKind() == TaskKind.TREASURE) {
						this.treasureTasks.put(task.db.templateId, task.db);
					} else if (prop.getKind() == TaskKind.LOOP) {
						TeamData team = player.teamManager.getTeam();
						if (team != null) {
							if (team.loopTasks == null) {
								team.loopTasks = new HashMap<>();
							}
							team.loopTasks.put(task.db.templateId, task.db);
						}
					} else {
						this.dailyTasks.put(task.db.templateId, task.db);
					}
				}
				task.db.finishCount = finishCount;
				task.setSecProgress(getSecProgress(task.getKind()));
				tasks.add(task);
				this.pushTaskUpdate(task);
			}
		}
		// 下一级才能接受的主线任务
		if (kind == TaskKind.MAIN && !hasMain) {
			for (String taskId : nextTasks) {
				int id = Integer.parseInt(taskId);
				TaskBase prop = TaskUtils.getTaskProp(id);
				if (prop != null && prop.getKind() == TaskKind.MAIN) {
					task = TaskUtils.createTask(prop);
					this.normalTasks.put(task.db.templateId, task.db);
					task.db.finishCount = finishCount;
					task.setSecProgress(getSecProgress(task.getKind()));
					tasks.add(task);
					this.pushTaskUpdate(task);
				}
			}
		}
		this.refreshWatchEvents();
		return tasks;
	};

	//
	/**
	 * 添加任务
	 * 
	 * @param task
	 * @private
	 */
	private final void _addTask(TaskData task) {
		if (task.isDaily()) {
			this.dailyTasks.put(task.db.templateId, task.db);
		} else if (task.isLoop()) {
			TeamData team = player.teamManager.getTeam();
			if (team != null) {
				team.onFirstAcceptLoopTask();
				if (team.loopTasks == null) {
					team.loopTasks = new HashMap<>();
				}
				team.loopTasks.put(task.db.templateId, task.db);
			}
		} else if (task.isTreasure()) {
			this.treasureTasks.put(task.db.templateId, task.db);
		} else {
			this.normalTasks.put(task.db.templateId, task.db);
		}
		this.refreshWatchEvents();
	};

	//
	/**
	 * 删除任务
	 * 
	 * @param templateId
	 * @param kind
	 * @private
	 */
	private final void _deleteTask(int templateId, int kind, boolean sendtoBattle) {
		Out.debug("_deleteTask templateId: ", templateId, " kind: ", kind);
		if (kind == TaskKind.DAILY) {
			this.dailyTasks.remove(templateId);
		} else if (kind == TaskKind.LOOP) {
			Map<Integer, TaskPO> loopTasks = player.teamManager.getLoopTasks();
			if (loopTasks != null) {
				loopTasks.remove(templateId);
			}
		} else if (kind == TaskKind.TREASURE) {
			this.treasureTasks.remove(templateId);
		} else {
			this.normalTasks.remove(templateId);
		}

		if (sendtoBattle) {
			// 通知游戏服任务已放弃
			NotifyToBattleServerParam param = new NotifyToBattleServerParam();
			param.playerUUID = player.getId();
			param.questID = String.valueOf(templateId);
			this.notifyToBattleServer("QuestDroppedR2B", param);
		}
		this.refreshWatchEvents();
	};

	//
	/**
	 * 完成任务
	 * 
	 * @param templateId
	 * @param kind
	 * @private
	 */
	private final void _finishTask(int templateId, int kind) {
		Out.debug("_finishTask templateId: ", templateId, " kind: ", kind);
		if (kind == TaskKind.DAILY) {
			this.finishedDailyTasks.put(templateId, templateId);
			this.taskListPO.todayDailyTaskCount += 1;
		} else if (kind == TaskKind.LOOP) {
			// 给队伍每个人当日任务数+=1
			TeamData team = player.teamManager.getTeam();
			if (team != null) {
				if (team.finishedLoopTasks == null) {
					team.finishedLoopTasks = new HashMap<>();
				}
				team.finishedLoopTasks.put(templateId, templateId);

				for (TeamMemberData member : team.teamMembers.values()) {
					WNPlayer mPlayer = member.getPlayer();
					if (mPlayer != null) {
						mPlayer.taskManager.taskListPO.todayLoopTaskCount += 1;
					}
				}
			}
		} else if (kind == TaskKind.TREASURE) {
			this.finishedTreasureTasks.put(templateId, templateId);
		} else {
			// serial(templateId, this.finishedNormalTasks);
			this.finishedNormalTasks.put(templateId, templateId);
		}
	};

	public Tasks toJson4Payload() {
		Tasks.Builder data = Tasks.newBuilder();
		List<Task> taskList = new ArrayList<>();

		for (int templateId : this.normalTasks.keySet()) {
			TaskData task = new TaskData(this.normalTasks.get(templateId));
			task.setSecProgress(getSecProgress(task.getKind()));
			if (task.getState() != TaskState.DELETE.getValue()) {
				Task payLoad = task.buildTask();
				if (payLoad != null) {
					taskList.add(payLoad);
				}
			}
		}

		// 师门 每日任务
		for (int templateId : this.dailyTasks.keySet()) {
			TaskData task = new TaskData(this.dailyTasks.get(templateId));
			task.setSecProgress(getSecProgress(task.getKind()));
			if (task.getState() != TaskState.DELETE.getValue()) {
				Task payLoad = task.buildTask();
				if (payLoad != null) {
					taskList.add(payLoad);
				}
			}
		}

		// 一条龙任务
		Map<Integer, TaskPO> loopTasks = player.teamManager.getLoopTasks();
		if (loopTasks != null) {
			for (int templateId : loopTasks.keySet()) {
				TaskData task = new TaskData(loopTasks.get(templateId));
				task.setSecProgress(getSecProgress(task.getKind()));
				if (task.getState() != TaskState.DELETE.getValue()) {
					Task payLoad = task.buildTask();
					if (payLoad != null) {
						taskList.add(payLoad);
					}
				}
			}
		}

		// 挖宝任务
		for (int templateId : this.treasureTasks.keySet()) {
			TaskData task = new TaskData(this.treasureTasks.get(templateId));
			task.setSecProgress(getSecProgress(task.getKind()));
			if (task.getState() != TaskState.DELETE.getValue()) {
				Task payLoad = task.buildTask();
				if (payLoad != null) {
					taskList.add(payLoad);
				}
			}
		}

		data.addAllTaskList(taskList);
		// 判断如果当天之前有未完成的任务，则接着之前的任务做，不重新给玩家发任务
		this.getDailyTask(taskList, -1, false);
		return data.build();
	};

	//
	/**
	 * 获取玩家可做的日常任务(玩家等级变化，阶级变化都有可能有新的日常任务触发)
	 * 
	 * @param dataArray
	 * @param props
	 */
	public final void getDailyTask(List<Task> dataArray, int cycleType, boolean isNewDay) {
		return;
		// List<TaskBase> props = TaskUtils.getDailyTasks(player.getLevel(),
		// player.getPlayer().upLevel, playerPro, cycleType);
		// int souleTaskCycleType = TaskUtils.getSoulTaskCycleType();
		// for (TaskBase prop : props) {
		// TaskData task = this.getTask(prop.getId(), prop.getKind());
		// if (task == null && (cycleType != -1 || !this.isFinish(prop.getId(),
		// prop.getKind()))
		// && ((souleTaskCycleType != 0 && prop.taskCycle == souleTaskCycleType)
		// || (!this.isHaveCyleTypeDailyTask(prop.taskCycle) &&
		// !this.hasDidCycleTypeToday(prop.taskCycle)))) {
		// if (prop.getType() == TaskType.CHAIN_SOUL.getValue()) {
		// if (!isNewDay &&
		// !this.isHaveDailyTaskByType(TaskType.CHAIN_SOUL.getValue())) {
		// // 如果是炼魂任务的日常，不能直接领取(去掉)
		// int dayCount = GlobalConfig.Quest_Soul_DailyLimit;
		// if (this.getDailySoulTaskCount < dayCount &&
		// this._canAcceptTaskByProp(prop).result) {
		// // task = taskUtil.createTask(prop,
		// // TaskState.NOT_START);
		// // dataArray.push(task.toJson4Payload());
		// // self._addTask(task);
		// // 检查背包格子数
		// if (player.getWnBag().testEmptyGridLarge(1, false)) {// 检查法器背包格子
		// AcceptTaskResult result = this.acceptTask(prop.getId(),
		// prop.getKind());
		// if (result.task != null) {
		// task = this.getTask(prop.getId(), prop.getKind());
		// if (task != null) {
		// task.setSecProgress(getSecProgress(task.getKind()));
		// dataArray.add(task.buildTask());
		// }
		// }
		// this.getDailySoulTaskCount++;
		// }
		// }
		// }
		// } else {
		// if (this._canAcceptTaskByProp(prop).result) {
		// AcceptTaskResult result = this.acceptTask(prop.getId(),
		// prop.getKind());
		// if (result.task != null) {
		// task = this.getTask(prop.getId(), prop.getKind());
		// if (task != null) {
		// task.setSecProgress(getSecProgress(task.getKind()));
		// dataArray.add(task.buildTask());
		// }
		// }
		// }
		// }
		// }
		// }
	}

	public void pushTaskUpdate(TaskData data) {
		pushTaskUpdate(data, player.teamManager.getTeam());
	}

	public final void pushTaskUpdate(TaskData data, TeamData team) {
		if (data != null) {
			data.setSecProgress(getSecProgress(data.getKind(), team));
			TaskUpdatePush.Builder builder = TaskUpdatePush.newBuilder();
			Tasks.Builder task = Tasks.newBuilder();
			task.addTaskList(data.buildTask());
			builder.setS2CData(task.build());
			player.receive("area.taskPush.taskUpdatePush", builder.build());
		}
	}

	public final void pushTaskUpdate(List<Task> data) {
		if (data.size() > 0) {
			TaskUpdatePush.Builder builder = TaskUpdatePush.newBuilder();
			Tasks.Builder task = Tasks.newBuilder();
			task.addAllTaskList(data);
			builder.setS2CData(task.build());
			player.receive("area.taskPush.taskUpdatePush", builder.build());
		}
	}

	/**
	 * 接收战斗服的任务请求
	 * 
	 * @param eventName
	 */
	public final void onTaskRequestEvent(String eventName, int templateId, String key, int value) {
		Out.debug("onTaskRequestEvent recived name:", eventName, " id: ", templateId);
		if (eventName.equals("AcceptQuest")) {
			// 接收任务
		} else if (eventName.equals("CommitQuest")) {
			// 提交任务
		} else if (eventName.equals("DropQuest")) {
			this.discardTaskByID(templateId);
			// 丢弃任务
		} else if (eventName.equals("UpdateQuestStatus")) {
			// 像游戏服更新任务状态
			TaskData task = this.getTaskByID(templateId);
			if (task == null) {
				Out.error("UpdateQuestStatus can not find task: ", templateId);
				return;
			}
			if (key.equals("state") && value == TaskState.COMPLETED_NOT_DELIVERY.getValue()) {
				// 更新任务状态到已完成但是未提交
				this.completeTaskByID(templateId);
				if (task != null && task.prop.completeNpc == -1) {
					// 交任务的NPCid 不填时为达成任务条件自动完成。
					this.submitTask(task.db.templateId, task.getKind(), 0);
				}
			} else if (key.equals("add_progress")) {
				Out.debug("onTaskRequestEvent add_progress", task);
				if (task != null) {
					int progress = task.getProgress();

					progress = progress + value;
					if (progress >= task.getTargetNum()) {
						progress = task.getTargetNum();
					}
					boolean killed = task.setProgress(progress);
					if (task.db.state == TaskState.COMPLETED_NOT_DELIVERY.getValue()) {
						NotifyToBattleServerParam param = new NotifyToBattleServerParam();
						param.playerUUID = player.getId();
						param.questID = String.valueOf(templateId);
						param.initStatus = false;
						param.status = new ArrayList<>();
						param.status.add(new StringString("state", String.valueOf(TaskState.COMPLETED_NOT_DELIVERY.getValue())));
						this.notifyToBattleServer("QuestStatusChangedR2B", param);
					} else if (killed) {
						// 目标达成缺没有完成任务, 委托战斗服进行处理
						NotifyToBattleServerParam param = new NotifyToBattleServerParam();
						param.playerUUID = player.getId();
						param.questID = String.valueOf(templateId);
						param.initStatus = false;
						param.status = new ArrayList<>();
						param.status.add(new StringString("refine_killed", "true"));
						this.notifyToBattleServer("QuestStatusChangedR2B", param);
						task.setBattleAttribute("refine_killed", "true");
					}
					task.pushProgressTips(player);

					task.setSecProgress(getSecProgress(task.getKind()));
					this.pushTaskUpdate(task);
				}
			} else {
				TaskData task1 = this.getTaskByID(templateId);
				if (task1 != null) {
					task1.setBattleAttribute(key, String.valueOf(value));
				}
			}
		} else if (eventName.equals("accessable")) {
			// 进去区域可以接受改任务
			TaskBase prop = TaskUtils.getTaskProp(templateId);
			if (prop == null) {
				Out.warn("accessable task not found: ", templateId);
			} else {
				if (this._canAcceptTaskByProp(prop) == null) {
					TaskData newTask = null;
					if (prop.giveNpc == 0) { // 自动接
						AcceptTaskResult result = this.acceptTask(prop.getId(), prop.getKind());
						newTask = result.task;
					}
					if (newTask != null) {
						this._addTask(newTask);
						newTask.setSecProgress(getSecProgress(newTask.getKind()));
						this.pushTaskUpdate(newTask);
					}
				}
			}

		} else if (eventName.equals("unaccessable")) {
			// 退出该区域,如果还没有接受改任务则不能接了
			TaskData task = this.getTaskByID(templateId);
			if (task != null && task.getState() == TaskState.NOT_START.getValue()) {
				task.db.state = TaskState.DELETE.getValue();
				task.setSecProgress(getSecProgress(task.getKind()));
				this._deleteTask(templateId, task.getKind(), true);
				this.pushTaskUpdate(task);
			}
		}
	}

	/**
	 * 获取一条龙/师门任务进度 或者多目标任务目标数
	 */
	public int getSecProgress(int kind) {
		return getSecProgress(kind, player.teamManager.getTeam());
	}

	/**
	 * 获取一条龙/师门任务进度 或者多目标任务目标数
	 */
	public int getSecProgress(int kind, TeamData team) {
		if (kind == TaskKind.DAILY) {
			int all = (this.taskListPO.todayDailyTaskCount + this.dailyTasks.size()) % Const.DAILY_TASK_ROUND_COUNT;
			if (all == 0) {
				all = Const.DAILY_TASK_ROUND_COUNT;
			}
			return all;
		} else if (kind == TaskKind.LOOP) {
			int all = 0;
			// TeamData team = player.teamManager.getTeam();
			if (team == null) {
				return -1;
			}
			if ((team.finishedLoopTasks == null || team.finishedLoopTasks.size() == 0) && (team.loopTasks == null || team.loopTasks.size() == 0)) {
				return -1;
			}
			if (team.finishedLoopTasks != null) {
				all += team.finishedLoopTasks.size();
			}
			if (team.loopTasks != null) {
				all += team.loopTasks.size();
			}
			return all;
		} else {
			return -1;
		}
	}

	/**
	 * 等级的变化会造成支线任务可接
	 * 
	 * @param level
	 */
	public final void onLevelChange(int level) {
		List<Task> data = new ArrayList<>();
		for (BranchLineCO prop : GameData.BranchLines.values()) {
			if (prop.level <= level) {
				if (!this.isFinish(prop.iD, prop.kind) && this._canAcceptTaskByProp(prop) == null) {
					if (prop.giveNpc == 0) {
						this.acceptTask(prop.iD, prop.kind);
						TaskData task = this.getTask(prop.iD, prop.kind);
						if (task != null) {
							task.setSecProgress(getSecProgress(task.getKind()));
							data.add(task.buildTask());
						}
					} else {
						TaskData task = TaskUtils.createTask(prop);
						this.normalTasks.put(task.db.templateId, task.db);
						task.setSecProgress(getSecProgress(task.getKind()));
						data.add(task.buildTask());
					}
				}
			}
		}

		// 等级改变，日常任务触发
		this.getDailyTask(data, -1, false);
		Out.debug("task onLevelChange: ", data);
		if (data.size() > 0) {
			this.refreshWatchEvents();
			this.pushTaskUpdate(data);
		}

		dealTaskEvent(TaskType.LEVEL_UP, level);
	}

	public final void onUpLevelChange(int upLevel) {
		List<Task> data = new ArrayList<>();
		for (BranchLineCO p : GameData.BranchLines.values()) {
			if (p.level <= upLevel && p.level > 0) {
				if (!this.isFinish(p.iD, p.kind) && this._canAcceptTaskByProp(p) == null) {
					if (p.giveNpc == 0) {
						this.acceptTask(p.iD, p.kind);
						TaskData task = this.getTask(p.iD, p.kind);
						if (task != null) {
							task.setSecProgress(getSecProgress(task.getKind()));
							data.add(task.buildTask());
						}
					} else {
						TaskData task = TaskUtils.createTask(p);
						this.normalTasks.put(task.db.templateId, task.db);
						task.setSecProgress(getSecProgress(task.getKind()));
						data.add(task.buildTask());
					}
				}
			}
		}
		// 等级改变，任务触发
		this.getDailyTask(data, -1, false);
		Out.debug("task onUpLevelChange: ", data);
		if (data.size() > 0) {
			this.refreshWatchEvents();
			this.pushTaskUpdate(data);
		}
		// 角色等级变化
		this.dealTaskEvent(TaskType.USERUP_LEVEL, player.getPlayer().upLevel);
	}

	public void onLogin() {
		// 队伍中，有一条龙任务
		Map<Integer, TaskPO> loopTasks = player.teamManager.getLoopTasks();
		if (loopTasks != null) {
			for (TaskPO db : loopTasks.values()) {
				this.pushTaskUpdate(new TaskData(db));
			}
		}
	}
}
