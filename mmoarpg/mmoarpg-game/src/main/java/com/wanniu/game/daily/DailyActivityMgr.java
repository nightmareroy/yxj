package com.wanniu.game.daily;

import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.wanniu.core.game.LangService;
import com.wanniu.core.logfs.Out;
import com.wanniu.core.util.DateUtil;
import com.wanniu.game.attendance.PlayerAttendance.GiftState;
import com.wanniu.game.common.CommonUtil;
import com.wanniu.game.common.Const;
import com.wanniu.game.common.Const.TaskType;
import com.wanniu.game.data.GameData;
import com.wanniu.game.data.ext.ScheduleExt;
import com.wanniu.game.data.ext.ScheduleExt.TimeCond;
import com.wanniu.game.data.ext.VitBonusExt;
import com.wanniu.game.player.PlayerUtil;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.poes.DailyActivityPO;
import com.wanniu.game.poes.DailyActivityPO.DailyInfo;
import com.wanniu.game.poes.DailyActivityPO.DailyRewardInfo;
import com.wanniu.game.rich.RichManager;
import com.wanniu.game.sevengoal.SevenGoalManager;
import com.wanniu.game.poes.PlayerPO;
import com.wanniu.game.poes.SevenGoalPO;

import io.netty.util.internal.StringUtil;
import pomelo.area.DailyActivityHandler.UpdateActivityPush;
import pomelo.area.PlayerHandler.SuperScriptType;
import pomelo.dailyActivity.DailyActivity;

/**
 * 日常活动（活跃度）
 * 
 * @author jjr
 *
 */
public class DailyActivityMgr {
	private String playerId;
	public DailyActivityPO po;

	// 这几类活动需要做特殊处理，所以列出枚举
	public static enum ScheduleType {
		DEMON_INVADE(13), FIVE_MOUNTAIN(7), SOLO(6), TRIAL(8), ILLUSION2(15),GUILD_BOSS(16);

		private int value;

		ScheduleType(int value) {
			this.value = value;
		}
		
		public int getValue()
		{
			return value;
		}
	}
	
	private RichManager richManager;
	private SevenGoalManager sevenGoalManager;

	protected DailyActivityMgr() {

	}

	public DailyActivityMgr(String playerId, DailyActivityPO po) {
		this.setPlayerId(playerId);
		this.po = po;
		
	}

	public void init(RichManager richManager,SevenGoalManager sevenGoalManager) {
		for (Integer id : GameData.Schedules.keySet()) {
			if (!this.po.activeMap.containsKey(id)) {
				DailyInfo dailyInfo = new DailyInfo();
				dailyInfo.id = id;
				dailyInfo.process = 0;
				this.po.activeMap.put(id, dailyInfo);
			}
		}

		for (Integer id : GameData.VitBonuss.keySet()) {
			if (!this.po.rewards.containsKey(id)) {
				DailyRewardInfo reward = new DailyRewardInfo();
				reward.id = id;
				reward.state = 0;
				this.po.rewards.put(id, reward);
			}
		}

		updateRewardState();
		
		this.richManager=richManager;
		this.sevenGoalManager=sevenGoalManager;
	}

	public void reset() {
		for (Integer id : this.po.activeMap.keySet()) {
			DailyInfo info = this.po.activeMap.get(id);
			if (null == info) {
				continue;
			}
			info.process = 0;
		}

		for (Integer id : this.po.rewards.keySet()) {
			DailyRewardInfo reward = this.po.rewards.get(id);
			if (null == reward) {
				continue;
			}
			reward.state = 0;
		}

		this.po.totalDegree = 0;
	}

	/**
	 * 每天重置
	 */
	public void refreshNewDay() {
		reset();
	}

	/**
	 * 判断时间状态
	 * 
	 * @param taskId
	 * @return
	 */
	public int getTimeState(int taskId) {
		int size = getConfig(taskId).timeArray.size();
		if (size > 0) {
			// 有时间限制
			for (int j = 0; j < size; j++) {
				TimeCond timeCond = getConfig(taskId).timeArray.get(j);
				int timeState = DateUtil.isInTime(timeCond.beginTime, timeCond.endTime);
				if (Const.TimeState.TIME_NOT_UP.getValue() == timeState) {
					return Const.TimeState.TIME_NOT_UP.getValue();
				} else if (Const.TimeState.TIME_UP.getValue() == timeState) {
					return Const.TimeState.TIME_UP.getValue();
				}
			}
			return Const.TimeState.TIME_OVER.getValue();
		} else {
			// 无时间限制
			return Const.TimeState.TIME_UP.getValue();
		}
	}

	/**
	 * 判断时间状态 将开始时间提前10分钟进行判断
	 * 
	 * @param taskId
	 * @return 0:不在活动时间 1:即将开始 2:开放中
	 */
	public static int getTimeState2(int taskId) {
		int size = getConfig(taskId).periodInCalendarArray.size();
		if (size > 0) {
			// 有时间限制
			for (int j = 0; j < size; j++) {
				TimeCond timeCond = getConfig(taskId).periodInCalendarArray.get(j);
				int timeState = DateUtil.isInTime2(timeCond.beginTime, timeCond.endTime);
				if (timeState == 1 || timeState == 2) {
					return timeState;
				}
			}
			return 0;
		} else {
			// 无时间限制
			return 2;
		}
	}

	public DailyActivity.DailyInfo.Builder dailyToProto(int taskId) {
		DailyInfo elem = this.po.activeMap.get(taskId);
		if (!isWeekSatisfy(taskId)) {
			return null;
		}

		DailyActivity.DailyInfo.Builder info = DailyActivity.DailyInfo.newBuilder();
		info.setId(taskId);
		info.setCurNum(elem.process);
		info.setMaxNum(getConfig(taskId).maxCount);
		info.setPerDegree(getConfig(taskId).vitBonus);
		info.setLvLimit(getConfig(taskId).lvLimit);

		for (int j = 0; j < getConfig(taskId).timeArray.size(); j++) {
			TimeCond timeCond = getConfig(taskId).timeArray.get(j);
			int timeState = DateUtil.isInTime(timeCond.beginTime, timeCond.endTime);
			if (Const.TimeState.TIME_NOT_UP.getValue() == timeState) {
				String timeStr = timeCond.beginTime + "-" + timeCond.endTime;
				info.setOpenPeriod(timeStr);
				break;
			}
		}

		info.setIsOver(getTimeState(taskId));
		return info;
	}

	/**
	 * 获取活动列表
	 * 
	 * @return
	 */
	public List<DailyActivity.DailyInfo> getDailyLs() {
		List<DailyActivity.DailyInfo> dailyLs = new ArrayList<DailyActivity.DailyInfo>();
		for (Integer taskId : this.po.activeMap.keySet()) {
			if (!isValid(taskId)) {
				continue;
			}
			DailyActivity.DailyInfo.Builder info = dailyToProto(taskId);
			if (null != info)
				dailyLs.add(info.build());
		}
		return dailyLs;
	}

	/**
	 * 获取奖励列表
	 * 
	 * @return
	 */
	public List<DailyActivity.DegreeInfo> getDegreeLs() {
		updateRewardState();
		List<DailyActivity.DegreeInfo> degreeLs = new ArrayList<DailyActivity.DegreeInfo>();
		for (Integer id : this.po.rewards.keySet()) {
			DailyRewardInfo elem = this.po.rewards.get(id);

			DailyActivity.DegreeInfo.Builder info = DailyActivity.DegreeInfo.newBuilder();
			info.setId(id);
			info.setNeedDegree(getVitBounsConfig(id).reqVit);
			info.setState(elem.state);
			degreeLs.add(info.build());
		}
		return degreeLs;
	}

	/**
	 * 获取奖励
	 * 
	 * @param id
	 * @return
	 */
	public JSONObject getReward(int id) {
		JSONObject ret = new JSONObject();
		if (!this.po.rewards.containsKey(id)) {
			ret.put("result", -1);
			ret.put("des", LangService.getValue("DAILY_ACTIVITY_NOT_EXIST"));
			return ret;
		}

		DailyRewardInfo reward = this.po.rewards.get(id);
		if (null == reward) {
			ret.put("result", -1);
			ret.put("des", LangService.getValue("DAILY_ACTIVITY_NOT_EXIST"));
			return ret;
		}

		if (GiftState.NO_RECEIVE.getValue() == reward.state) {
			ret.put("result", -2);
			ret.put("des", LangService.getValue("DAILY_ACTIVITY_NOT_RECEIVE"));
			return ret;
		}

		if (GiftState.RECEIVED.getValue() == reward.state) {
			ret.put("result", -3);
			ret.put("des", LangService.getValue("DAILY_ACTIVITY_RECEIVED"));
			return ret;
		}

		if (GiftState.CAN_RECEIVE.getValue() == reward.state) {
			reward.state = GiftState.RECEIVED.getValue();
			ret.put("result", 0);
			ret.put("des", LangService.getValue("DAILY_ACTIVITY_SUCESS"));
			return ret;
		}

		ret.put("result", -2);
		ret.put("des", LangService.getValue("DAILY_ACTIVITY_NOT_RECEIVE"));
		return ret;
	}

	/**
	 * 判断任务是否存在
	 * 
	 * @param taskId
	 * @return
	 */
	public boolean isTaskExist(int taskId) {
		return this.po.activeMap.containsKey(taskId);
	}

	/**
	 * 获取任务单元
	 * 
	 * @param taskId
	 * @return
	 */
	public DailyInfo getTaskInfo(int taskId) {
		return this.po.activeMap.get(taskId);
	}

	/**
	 * 是否满足星期配置
	 * 
	 * @param taskId
	 * @return
	 */
	public boolean isWeekSatisfy(int taskId) {
		int w = CommonUtil.getWeek();
		ScheduleExt config = getConfig(taskId);
		if (null == config) {
			Out.error("dailyActivityMgr config is error in Schedules.json--->>>", taskId);
			return false;
		}
		List<Integer> weekArray = config.weekArray;
		if (weekArray.size() > 0 && -1 == weekArray.indexOf(w)) {
			return false;
		}
		return true;
	}

	/**
	 * 时间是否满足
	 * 
	 * @param taskId
	 * @return
	 */
	public boolean isTimeSatisfy(int taskId) {
		if(getConfig(taskId).timeArray.isEmpty()) {
			return true;
		}
		for (int j = 0; j < getConfig(taskId).timeArray.size(); j++) {
			TimeCond timeCond = getConfig(taskId).timeArray.get(j);
			if (Const.TimeState.TIME_UP.getValue() == DateUtil.isInTime(timeCond.beginTime, timeCond.endTime)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 是否满足所有条件
	 * 
	 * @param taskId
	 * @return
	 */
	public boolean isLvSatisfy(int taskId) {
		PlayerPO playerPO = PlayerUtil.getPlayerBaseData(this.getPlayerId());

		if (null == playerPO) {
			Out.error("playerPO is null");
			return false;
		}

		// 是否满足等级条件
		if (playerPO.level < getConfig(taskId).lvLimit) {
			Out.debug("playerPO.level limit ->>>>", playerPO.level, "config lv ->>>", getConfig(taskId).lvLimit);
			return false;
		}
		return true;
	}

	public boolean isValid(int taskId) {
		ScheduleExt config = getConfig(taskId);
		if (null == config) {
			Out.error("config is null-->>", taskId);
			return false;
		}

		return 1 == getConfig(taskId).isValid;
	}

	/**
	 * 是否满足条件
	 * 
	 * @return
	 */
	public boolean isSatisfyCond(int taskId) {
		if (!isWeekSatisfy(taskId)) {
			return false;
		}

		if (!isLvSatisfy(taskId)) {
			return false;
		}

		if (!isTimeSatisfy(taskId)) {
			return false;
		}
		return true;
	}

	/**
	 * 获取配置
	 * 
	 * @param taskId
	 * @return
	 */
	public static ScheduleExt getConfig(int taskId) {
		return GameData.Schedules.get(taskId);
	}

	public VitBonusExt getVitBounsConfig(int id) {
		return GameData.VitBonuss.get(id);
	}

	/**
	 * 获取还可完成次数
	 * 
	 * @param taskId 任务id
	 * @param num 增加数量
	 */
	public int getCanAddNum(int taskId, int num) {
		DailyInfo info = getTaskInfo(taskId);
		if (null == info) {
			return 0;
		}

		ScheduleExt config = getConfig(taskId);
		if (null == config) {
			return 0;
		}

		return Math.min(num, config.maxCount - info.process);
	}

	/**
	 * 活动更新推送
	 * 
	 * @param taskId
	 */
	public void updatePush(int taskId) {
		WNPlayer player = PlayerUtil.getOnlinePlayer(playerId);
		if (null != player) {
			List<DailyActivity.DailyInfo> dailyLs = new ArrayList<DailyActivity.DailyInfo>();
			dailyLs.add(dailyToProto(taskId).build());
			UpdateActivityPush.Builder dailyPush = UpdateActivityPush.newBuilder();
			dailyPush.addAllS2CDailyLs(dailyLs);
			dailyPush.setS2CTotalDegree(this.po.totalDegree);
			dailyPush.addAllS2CDegreeLs(getDegreeLs());
			player.receive("area.dailyActivityPush.updateActivityPush", dailyPush.build());
		}
	}

	public void updateRewardState() {
		for (Integer key : this.po.rewards.keySet()) {
			DailyRewardInfo reward = this.po.rewards.get(key);
			if (null == reward || GiftState.RECEIVED.getValue() == reward.state) {
				continue;
			}

			if (this.po.totalDegree >= getVitBounsConfig(reward.id).reqVit) {
				reward.state = GiftState.CAN_RECEIVE.getValue();
				continue;
			}

			reward.state = GiftState.NO_RECEIVE.getValue();
		}
	}

	/**
	 * 活动进度刷新
	 * 
	 * 活动任务id
	 * 
	 * @param num
	 */
	public void doProcess(int taskId, int num) {
		if (isTaskExist(taskId)) {
			DailyInfo info = getTaskInfo(taskId);
			if (null != info && isSatisfyCond(taskId)) {
				int canAddNum = getCanAddNum(taskId, num);
				info.process += canAddNum;

				ScheduleExt config = getConfig(taskId);
				if (null == config) {
					Out.error("config is null-->>", taskId);
					return;
				}
				this.po.totalDegree += canAddNum * config.vitBonus;
				if (canAddNum > 0) {
					// 刷新界面
					updatePush(taskId);
					updateRewardState();
					updateSuperScript();
					
					if(isComplete(taskId)) {
						richManager.AddFreeCount(taskId);
					}
				}

				// 更新活跃度任务
				WNPlayer player = PlayerUtil.getOnlinePlayer(getPlayerId());
				if (player != null) {
					player.taskManager.dealTaskEvent(TaskType.ACTIVITY_NUM, canAddNum * config.vitBonus);
				}

			}
		}
	}

	/**
	 * 日常任务是否完成
	 * 
	 * @param taskId
	 * @return
	 */
	public boolean isComplete(int taskId) {
		DailyInfo info = getTaskInfo(taskId);
		if (null == info) {
			Out.error("DailyInfo is null", info);
			return false;
		}

		if (getConfig(taskId).maxCount <= 0) {
			return false; // 次数不限制
		}
		return info.process >= getConfig(taskId).maxCount;
	}

	public boolean dailyAPI(Const.DailyType type, String target, ScheduleExt task) {
		if (StringUtil.isNullOrEmpty(target)) {
			return task.type == type.value;
		}

		return task.type == type.value && -1 != task.targetArray.indexOf(target);
	}

	/**
	 * 查询任务ID
	 * 
	 * @param type
	 * @param id
	 * @return
	 */
	public int[] findTaskId(Const.DailyType type, String id) {
		List<ScheduleExt> ls = GameData.findSchedules((o) -> {
			return dailyAPI(type, id, o);
		});

		int[] ids = new int[ls.size()];
		for (int i = 0; i < ls.size(); i++) {
			ids[i] = ls.get(i).schID;
		}

		return ids;
	}

	/**
	 * 事件触发
	 * 
	 * @param type
	 * @param targetId
	 * @param num
	 */
	public void onEvent(Const.DailyType type, String target, int num) {
		int[] taskIds = findTaskId(type, target);
		for (int i = 0; i < taskIds.length; i++) {
			int taskId = taskIds[i];
			if (!isTaskExist(taskId)) {
				return;
			}
			if (isSatisfyCond(taskId) && !isComplete(taskId)) {
				doProcess(taskId, num);
			}
		}
	}

	public void onLogin() {
		// 推送主界面红点
		updateSuperScript();
	}

	public void updateSuperScript() {
		List<SuperScriptType> data = this.getSuperScript();
		PlayerUtil.getOnlinePlayer(this.getPlayerId()).updateSuperScriptList(data);
	}

	/**
	 * 推送红点
	 * 
	 * @return
	 */
	public List<SuperScriptType> getSuperScript() {
		List<SuperScriptType> ret = new ArrayList<>();

		int sum = 0;
		for (Integer id : this.po.rewards.keySet()) {
			DailyRewardInfo elem = this.po.rewards.get(id);
			if (GiftState.CAN_RECEIVE.getValue() == elem.state) {
				sum += 1;
			}
		}

		SuperScriptType.Builder t = SuperScriptType.newBuilder();
		t.setType(Const.SUPERSCRIPT_TYPE.ACTIVITY_CENTER.getValue());
		t.setNumber(sum);
		ret.add(t.build());

		SuperScriptType.Builder sst = SuperScriptType.newBuilder();
		sst.setType(Const.SUPERSCRIPT_TYPE.FLAG_ACTIVITY_ACIVITY.getValue());
		sst.setNumber(sum);
		ret.add(sst.build());

		ScheduleExt scheduleExt = getConfig(ScheduleType.DEMON_INVADE.value);
		int timeState = getTimeState2(scheduleExt.schID);
		SuperScriptType.Builder yzrq = SuperScriptType.newBuilder();
		yzrq.setType(Const.SUPERSCRIPT_TYPE.DEMON_INVADE_ACTIVED.getValue());
		yzrq.setNumber(timeState);
		ret.add(yzrq.build());

		scheduleExt = getConfig(ScheduleType.FIVE_MOUNTAIN.value);
		timeState = getTimeState2(scheduleExt.schID);
		SuperScriptType.Builder f2f = SuperScriptType.newBuilder();
		f2f.setType(Const.SUPERSCRIPT_TYPE.FIVE_MOUNTAIN_ACTIVED.getValue());
		f2f.setNumber(timeState);
		ret.add(f2f.build());

		scheduleExt = getConfig(ScheduleType.SOLO.value);
		timeState = getTimeState2(scheduleExt.schID);
		SuperScriptType.Builder solo = SuperScriptType.newBuilder();
		solo.setType(Const.SUPERSCRIPT_TYPE.SOLO_ACTIVED.getValue());
		solo.setNumber(timeState);
		ret.add(solo.build());

		scheduleExt = getConfig(ScheduleType.TRIAL.value);
		timeState = getTimeState2(scheduleExt.schID);
		SuperScriptType.Builder trial = SuperScriptType.newBuilder();
		trial.setType(Const.SUPERSCRIPT_TYPE.TRIAL_ACTIVIED.getValue());
		trial.setNumber(timeState);
		ret.add(trial.build());

		{
			SuperScriptType.Builder illusion2 = SuperScriptType.newBuilder();
			illusion2.setType(Const.SUPERSCRIPT_TYPE.ILLUSION2.getValue());
			illusion2.setNumber(calIllusion2ScriptNum());
			ret.add(illusion2.build());
		}
		
		scheduleExt = getConfig(ScheduleType.GUILD_BOSS.value);
		timeState = getTimeState2(scheduleExt.schID);
		SuperScriptType.Builder guild_boss = SuperScriptType.newBuilder();
		guild_boss.setType(Const.SUPERSCRIPT_TYPE.GUILD_BOSS2.getValue());
		guild_boss.setNumber(timeState);
		ret.add(guild_boss.build());

		return ret;
	}

	/**
	 * 计算幻境2的小红点.
	 */
	public int calIllusion2ScriptNum() {
		return getTimeState2(ScheduleType.ILLUSION2.value);
	}

	/**
	 * 获取关闭幻境2场景还要多少秒.
	 */
	public static int getCloseIllusion2Second() {
		List<TimeCond> times = getConfig(ScheduleType.ILLUSION2.value).periodInCalendarArray;
		if (times.isEmpty()) {
			return 1;
		}
		// 有时间限制
		for (TimeCond timeCond : times) {
			if (DateUtil.isInTime2(timeCond.beginTime, timeCond.endTime) == 2) {
				String[] ts = timeCond.endTime.split(":");
				int hour = Integer.parseInt(ts[0]);
				int minute = Integer.parseInt(ts[1]);
				int second = ts.length == 3 ? Integer.parseInt(ts[2]) : 0;
				LocalTime now = LocalTime.now();
				LocalTime end = LocalTime.of(hour, minute, second);
				return Math.max(1, (int) Duration.between(now, end).getSeconds());
			}
		}
		return 1;
	}

	public String getPlayerId() {
		return this.playerId;
	}

	public void setPlayerId(String playerId) {
		this.playerId = playerId;
	}
}
