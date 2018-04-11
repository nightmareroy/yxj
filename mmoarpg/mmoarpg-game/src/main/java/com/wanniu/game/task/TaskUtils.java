package com.wanniu.game.task;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.wanniu.core.logfs.Out;
import com.wanniu.core.util.RandomUtil;
import com.wanniu.core.util.StringUtil;
import com.wanniu.game.common.Const;
import com.wanniu.game.common.Const.TaskKind;
import com.wanniu.game.common.Const.TaskOpenWay;
import com.wanniu.game.common.Const.TaskState;
import com.wanniu.game.common.Const.TaskType;
import com.wanniu.game.common.ConstsTR;
import com.wanniu.game.common.Utils;
import com.wanniu.game.data.CircleCO;
import com.wanniu.game.data.DailyCO;
import com.wanniu.game.data.DailyReduceCO;
import com.wanniu.game.data.GameData;
import com.wanniu.game.data.TaskChestCO;
import com.wanniu.game.data.base.TaskBase;
import com.wanniu.game.data.base.TaskBase.ItemNode;
import com.wanniu.game.data.ext.WayTreasureExt;
import com.wanniu.game.functionOpen.FunctionOpenUtil;
import com.wanniu.game.player.GlobalConfig;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.poes.PlayerBasePO.EquipStrengthPos;
import com.wanniu.game.poes.PlayerPO;
import com.wanniu.game.team.TeamData;
import com.wanniu.game.team.TeamData.TeamMemberData;
import com.wanniu.redis.PlayerPOManager;

import pomelo.area.TaskHandler.TreasureScenePointPush;

public class TaskUtils {

	public static Map<Integer, TaskBase> AllTasks = new HashMap<Integer, TaskBase>();
	/** 一条龙跑环次数对应的奖励系数 */
	public static List<CircleCO> AllCircleRates = null;
	/** 师门任务次数对应的奖励系数 */
	public static List<DailyReduceCO> AllDailyRates = null;
	/** 师门一条龙对应的进度宝箱 */
	private static Map<Integer, List<TaskChestCO>> progressBox = null;
	/** 师门任务所有场景 */
	private static List<Integer> dailySceneIds = null;
	/** 按照场景ID 分组的师门 */
	private static Map<Integer, List<TaskBase>> sceneDailyTask = null;
	/** 师门 等级段集合 */
	private static List<Integer> dailyLevels = null;

	public static void init() {
		AllTasks.putAll(GameData.MainLines);
		AllTasks.putAll(GameData.BranchLines);
		AllTasks.putAll(GameData.Dailys);
		AllTasks.putAll(GameData.CircleScenes);

		// 一条龙 次数对应的奖励系数
		AllCircleRates = new ArrayList<>(GameData.Circles.values());
		AllCircleRates.sort(new Comparator<CircleCO>() {

			@Override
			public int compare(CircleCO arg0, CircleCO arg1) {
				int result = 0;
				if (arg0.num > arg1.num) {
					result = 1;
				} else if (arg0.num < arg1.num) {
					result = -1;
				}
				return result;
			}
		});
		// 师门任务次数对应的奖励系数
		AllDailyRates = new ArrayList<>(GameData.DailyReduces.values());
		AllDailyRates.sort(new Comparator<DailyReduceCO>() {

			@Override
			public int compare(DailyReduceCO arg0, DailyReduceCO arg1) {
				int result = 0;
				if (arg0.num > arg1.num) {
					result = 1;
				} else if (arg0.num < arg1.num) {
					result = -1;
				}
				return result;
			}
		});

		// 一条龙任务按照任务ID排序
		GameData.CircleScenes = new TreeMap<>(GameData.CircleScenes);

		// 师门一条龙的 进度宝箱
		progressBox = new HashMap<>();
		progressBox.put(TaskKind.DAILY, new ArrayList<>());
		progressBox.put(TaskKind.LOOP, new ArrayList<>());

		for (TaskChestCO taskChest : GameData.TaskChests.values()) {
			progressBox.get(taskChest.kind).add(taskChest);
		}
		// 进度宝箱排序(倒序)
		for (List<TaskChestCO> taskChestList : progressBox.values()) {
			taskChestList.sort(new Comparator<TaskChestCO>() {
				@Override
				public int compare(TaskChestCO o1, TaskChestCO o2) {
					if (o1.charLevel < o2.charLevel) {
						return 1;
					} else if (o1.charLevel > o2.charLevel) {
						return -1;
					}
					return 0;
				}
			});
		}

		sceneDailyTask = new HashMap<>();
		dailySceneIds = new ArrayList<>();
		dailyLevels = new ArrayList<>();
		for (TaskBase daily : GameData.Dailys.values()) {
			List<TaskBase> list = sceneDailyTask.get(daily.startScene);
			if (list == null) {
				list = new ArrayList<>();
				sceneDailyTask.put(daily.startScene, list);
			}
			list.add(daily);

			if (!dailySceneIds.contains(daily.startScene)) {
				dailySceneIds.add(daily.startScene);
			}

			if (!dailyLevels.contains(daily.level)) {
				dailyLevels.add(daily.level);
			}
		}
		for (List<TaskBase> list_daily : sceneDailyTask.values()) {
			list_daily.sort(new Comparator<TaskBase>() {

				@Override
				public int compare(TaskBase o1, TaskBase o2) {
					if (o1.iD < o2.iD) {
						return -1;
					} else if (o1.iD > o2.iD) {
						return 1;
					} else {
						return 0;
					}
				}
			});
		}

		dailyLevels.sort(new Comparator<Integer>() {
			@Override
			public int compare(Integer o1, Integer o2) {
				if (o1 < o2) {
					return -1;
				} else if (o1 > o2) {
					return 1;
				} else {
					return 0;
				}
			}
		});
	}

	public static TaskData createTask(TaskBase prop) {
		return createTask(prop, TaskState.NOT_START);
	}

	public static TaskData createTask(TaskBase prop, TaskState state) {
		TaskData data = new TaskData(prop);
		data.db.state = state.getValue();
		return data;
	}

	public static List<ItemNode> getReward(TaskBase prop, int pro) {
		List<ItemNode> rewards = new ArrayList<>();
		if (prop.rewards.containsKey(pro)) {
			List<ItemNode> temp = prop.rewards.get(pro);
			for (ItemNode node : temp) {
				rewards.add(new ItemNode(node.itemCode, node.itemNum, node.isBind));
			}
		}
		if (prop.rewards.containsKey(0)) {
			List<ItemNode> temp = prop.rewards.get(0);
			for (ItemNode node : temp) {
				rewards.add(new ItemNode(node.itemCode, node.itemNum, node.isBind));
			}
		}
		return rewards;
	};

	/**
	 * 创建每日任务
	 */
	public static List<TaskBase> getDailyTasks(int level, int rank, int pro, int cycleType) {
		// if(dailyTaskDate === 0 || !utils.isToday(dailyTaskDate)){
		List<TaskBase> dailyTasks = new ArrayList<>();

		// 是否有支线可以接
		for (DailyCO daily : GameData.Dailys.values()) {
			if (daily.before.equals("0") && (cycleType == -1 || daily.taskCycle == cycleType) && (daily.pro == 0 || daily.pro == pro)) {
				if (checkOpenWay(daily.openWay, daily.openDay) && checkTaskValidTime(daily.openTime, daily.endTime)) {
					dailyTasks.add(daily);
				}
			}
		}

		return dailyTasks;
	}

	public final static TaskBase getTaskProp(int id) {
		TaskBase task = GameData.MainLines.get(id);
		if (task == null) {
			task = GameData.BranchLines.get(id);
			if (task == null) {
				task = GameData.Dailys.get(id);
			}
			if (task == null) {
				task = GameData.CircleScenes.get(id);
			}
			if (task == null) {
				task = GameData.Treasures.get(id);
			}
		}
		return task;
	}

	/**
	 * 根据给定的giveNpc、type查找所有主线、支线、日常任务中符合的任务
	 * 
	 * @return 找不到返回null
	 */
	public static TaskBase getStealTaskProp(int giveNpc) {
		int type = TaskType.STEAL.getValue();
		for (TaskBase task : AllTasks.values()) {
			if (task.type == type && task.giveNpc == giveNpc) {
				return task;
			}
		}
		for (DailyCO daily : GameData.Dailys.values()) {
			if (daily.giveNpc == giveNpc) {
				return daily;
			}
		}
		return null;
	}

	public static boolean checkOpenWay(int openWay, String[] openDay) {
		if (openWay == TaskOpenWay.None.getValue()) {
			return false;
		}
		if (openWay == TaskOpenWay.DailyOpenInTime.getValue()) {
			return true;
		}
		Date now = new Date();
		Calendar c = Calendar.getInstance();
		c.setTime(now);
		int indexDay = c.get(Calendar.DAY_OF_WEEK);
		if (indexDay == 0) {
			indexDay = 7;
		}
		for (String day : openDay) {
			int dayInt = Integer.parseInt(day);
			if (dayInt == indexDay) {
				if (openWay == TaskOpenWay.WeekOpenInTime.getValue()) {
					return true;
				}
			}
		}
		return false;
	}

	public static boolean checkTaskValidTime(String startTime, String endTime) {
		if (StringUtil.isEmpty(startTime) || StringUtil.isEmpty(endTime)) {
			return true;
		}

		Date start = Utils.getTodayTimeFromString(startTime, null);
		Date end = Utils.getTodayTimeFromString(endTime, null);
		Date now = new Date();
		if (now.getTime() >= start.getTime() && now.getTime() <= end.getTime()) {
			return true;
		}
		return false;
	}

	/**
	 * 根据一条龙次数获取奖励 百分比系数
	 */
	public static int getLoopAwardRate(int times) {
		int rate = 0;
		for (CircleCO co : AllCircleRates) {
			if (co.num <= times) {
				rate = co.rate;
			}
		}
		return rate;
	}

	/**
	 * 判断指定player在一条龙副本里是否还有收益
	 * 
	 * @param player
	 * @return 没有收益返回false
	 */
	public static boolean profitableLoop(WNPlayer player) {
		int times = player.taskManager.taskListPO.todayLoopTaskCount;
		CircleCO co = AllCircleRates.get(AllCircleRates.size() - 1);

		return co.num > times;
	}

	/**
	 * 获取一条龙队员平均等级
	 */
	public static int getAvgLevel(TeamData team) {
		int openLevel = FunctionOpenUtil.getPropByName(Const.FunctionType.LoopTask.getValue()).openLv;
		if (team == null) {
			return openLevel;
		}
		float levelRate = GameData.CircleChoices.get(team.memberCount()).rate;
		int totalLevel = 0;
		for (TeamMemberData member : team.teamMembers.values()) {
			WNPlayer mPlayer = member.getPlayer();
			int lv = 0;
			if (mPlayer != null) {
				lv = mPlayer.getLevel();
			} else {
				PlayerPO playerPO = PlayerPOManager.findPO(ConstsTR.playerTR, member.id, PlayerPO.class);
				if (playerPO != null) {
					lv = playerPO.level;
				}
			}

			lv *= levelRate;
			totalLevel += (lv > openLevel ? lv : openLevel);
		}
		int avgLevel = totalLevel / team.memberCount();
		return avgLevel;
	}

	private static int times = 0; // 获取师门任务 递归次数

	/**
	 * 随机获取一条师门任务
	 */
	public static TaskBase getRDDailyTask(int level, int beforeTaskId, int pro) {
		List<TaskBase> list_tasks = null;
		TaskBase prop = null;
		int taskSceneId = 0;
		if (beforeTaskId == 0) {
			while (prop == null) {
				taskSceneId = dailySceneIds.get(RandomUtil.getIndex(dailySceneIds.size()));
				list_tasks = sceneDailyTask.get(taskSceneId);
				prop = getDailyTask(level, pro, list_tasks, 0);
				times++;
				if (times > 50) {
					times = 0;
					Out.error("1getRDDailyTask死锁level=", level, ",beforeTaskId=", beforeTaskId, ",pro=", pro);
					break;
				}
			}
			if (prop != null) {
				times = 0;
			}
			return prop;
		}

		TaskBase beforeProp = GameData.Dailys.get(beforeTaskId);
		taskSceneId = beforeProp.startScene;
		list_tasks = sceneDailyTask.get(taskSceneId);
		int index = -1;
		for (int i = 0; i < list_tasks.size(); i++) {
			if (list_tasks.get(i).iD == beforeTaskId) {
				index = i;
				break;
			}
		}

		while (prop == null) {
			list_tasks = sceneDailyTask.get(taskSceneId);
			prop = getDailyTask(level, pro, list_tasks, index + 1);
			taskSceneId = getRDTaskSceneId(taskSceneId); // dailySceneIds.get(RandomUtil.getIndex(dailySceneIds.size()));
			index = -1;
			times++;
			if (times > 50) {
				times = 0;
				Out.error("2getRDDailyTask死锁level=" + level + ",beforeTaskId=" + beforeTaskId + ",pro=" + pro);
				break;
			}
		}
		if (prop != null) {
			times = 0;
		}
		// Out.error("level=" + level +",prop.level=" + prop.level + ",index=" + index +
		// ",taskId=" + prop.iD + ",sceneId=" + prop.startScene);
		return prop;
	}

	private static int getRDTaskSceneId(int beforeSceneId) {
		List<Integer> list = new ArrayList<>();
		for (int sceneId : dailySceneIds) {
			if (beforeSceneId != sceneId) {
				list.add(sceneId);
			}
		}
		return list.get(RandomUtil.getIndex(list.size()));
	}

	private static TaskBase getDailyTask(int level, int pro, List<TaskBase> list_tasks, int index) {
		if (index >= list_tasks.size() - 1) {
			return null;
		}

		for (int i = index; i < list_tasks.size(); i++) {
			TaskBase prop = list_tasks.get(i);
			// 判断上一个任务和当前任务分类相同
			// if(beforeProp != null && beforeProp.taskCycle == prop.taskCycle) {
			// continue;
			// }
			// 等级限制
			// if (level < prop.level) {
			// continue;
			// }
			if (level < prop.level || prop.level != getNeerLevel(level)) {
				continue;
			}
			// 职业判断
			if (prop.pro != 0 && prop.pro != pro) {
				continue;
			}
			return prop;
		}
		// Out.error("getDailyTask:::"+list_tasks.size() +" ::" + index +":::"+
		// list_tasks.toString());
		return null;
	}

	/**
	 * 获取任务最接近的任务等级段
	 * 
	 * @return
	 */
	private static int getNeerLevel(int myLevel) {
		int lv = 0;
		for (int level : dailyLevels) {
			if (level <= myLevel) {
				lv = level;
			}
		}
		// Out.error("mylevel= " + myLevel + ", lv = " + lv);
		return lv;
	}

	/**
	 * 根据师门任务 次数获取奖励 百分比系数
	 */
	public static int getDailyAwardRate(int times) {
		int rate = 0;
		for (DailyReduceCO co : AllDailyRates) {
			if (co.num <= times) {
				rate = co.rate;
			}
		}
		return rate;
	}

	/**
	 * 触发强化所有部位强化任务
	 * 
	 * @param player
	 */
	public static void dealTrainEquipAllTask(WNPlayer player) {
		int equip_count = 0;
		Map<Integer, EquipStrengthPos> strengthPos = player.equipManager.strengthPos;
		for (int pos : strengthPos.keySet()) {
			EquipStrengthPos equip = strengthPos.get(pos);
			int equipLevel = equip.enSection * (GlobalConfig.EquipmentCraft_Enchant_MaxenLevel + 1) + equip.enLevel;
			if (equipLevel < 10) {
				continue;
			}
			equip_count++;
		}
		player.taskManager.dealTaskEvent(TaskType.TRAIN_EQUIP_ALL, "-1", equip_count);
	}

	/**
	 * 获取 师门和一条龙 进度宝箱
	 * 
	 * @return
	 */
	public static TaskChestCO getProgressBox(int taskKind, int level) {
		List<TaskChestCO> list = progressBox.get(taskKind);
		for (TaskChestCO taskChest : list) {
			if (level >= taskChest.charLevel) {
				return taskChest;
			}
		}
		Out.error("getProgressBox error, taskKind=", taskKind, ", level=", level);
		return null;
	}

	/**
	 * 获取第一个一条龙任务
	 */
	public static TaskBase getFirstLoopTask() {
		for (TaskBase _prop : GameData.CircleScenes.values()) {
			return _prop;
		}
		return null;
	}

	/**
	 * 判断是否可以和npc交谈
	 * 
	 * @param templateId 任务模板ID
	 * @param npcId
	 * @return
	 */
	public static boolean canTalkWithNpc(int templateId, int npcId) {
		TaskBase prop = TaskUtils.getTaskProp(templateId);
		if (prop != null && prop.needNpcs.contains(npcId)) {
			return true;
		}
		return false;
	}

	public static void treasurePush(WNPlayer player, int wayId, int taskId) {
		TreasureScenePointPush.Builder scenePointPush = TreasureScenePointPush.newBuilder();
		WayTreasureExt way = GameData.WayTreasures.get(wayId);
		int sceneIndex = RandomUtil.getIndex(way.doScenes.size());
		scenePointPush.setSceneId(way.doScenes.get(sceneIndex));
		List<Integer> points = way.doPoints.get(sceneIndex);
		scenePointPush.setPoint(points.get(RandomUtil.getIndex(points.size())));
		scenePointPush.setTaskId(taskId);
		player.receive("area.taskPush.treasureScenePointPush", scenePointPush.build());
		// 设置战斗服路点
		player.getXmdsManager().addUnit("", 0, null);
	}
}
