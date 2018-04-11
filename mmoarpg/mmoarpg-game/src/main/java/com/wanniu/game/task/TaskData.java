package com.wanniu.game.task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.wanniu.core.common.StringString;
import com.wanniu.core.logfs.Out;
import com.wanniu.game.common.Const;
import com.wanniu.game.common.Const.TaskKind;
import com.wanniu.game.common.Const.TaskState;
import com.wanniu.game.common.Const.TaskType;
import com.wanniu.game.data.base.TaskBase;
import com.wanniu.game.player.BILogService;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.task.po.TaskPO;

import pomelo.task.TaskOuterClass.Task;

public class TaskData {

	public TaskBase prop;
	public TaskPO db;

	private int secProgress = -1; // 师门任务进度 / 主线第二个怪物目标数量

	public TaskData(TaskBase prop) {
		this.db = new TaskPO();
		this.db.templateId = prop.getId();
		this.db.progress = 0;
		this.db.finishCount = 0;
		this.db.extendData = new HashMap<>();
		this.prop = prop;
	}

	public TaskData(TaskPO po) {
		this.db = po;

		TaskBase prop = TaskUtils.getTaskProp(this.db.templateId);
		if (prop != null) {
			this.prop = prop;
			this.setProgress(this.db.progress);
		} else {
			Out.error("there is no data of id: ", this.db.templateId, " in taskProps ");
		}
	}

	protected Map<String, String> _getBattlerAttribute() {
		if (this.db.battle_attributes == null) {
			this.db.battle_attributes = new HashMap<>();
		}
		return this.db.battle_attributes;
	}

	/**
	 * 获取任务类型
	 */
	public final int getKind() {
		return this.prop.getKind();
	}

	/**
	 * 获取任务目标类型
	 */
	public final int getType() {
		return this.prop.getType();
	}

	/**
	 * 任务需求目标数量
	 */
	public final int getTargetNum() {
		return this.prop.quantity;
	};

	/***/
	public final boolean isTargetID(TaskEvent msg) {
		if (this.prop.getType() == TaskType.FINISH_CLONESCENE.getValue()) {
			if (Integer.valueOf(msg.params[0].toString()) == this.prop.doScene) {
				return true;
			}
			return false;
		}
		if (this.prop.getType() == TaskType.TAKE_EQUIP_Qt.getValue()) {
			String[] conditionArr = this.prop.targets.get(0).split("\\|");
			String[] conditionArr1 = ((String) msg.params[0]).split("\\|");
			if (conditionArr.length > 1 && conditionArr1.length > 2) {
				int arr0 = Integer.parseInt(conditionArr[0]);
				int arr10 = Integer.parseInt(conditionArr1[0]);
				int arr1 = Integer.parseInt(conditionArr[1]);
				int arr11 = Integer.parseInt(conditionArr1[1]);
				int arr12 = Integer.parseInt(conditionArr1[2]);
				if (arr10 >= arr0 && arr11 >= arr1) {
					if (this.db.extendData == null) {
						this.db.extendData = new HashMap<>();
					}
					if (this.db.extendData.containsKey(arr12)) {
						return false;
					}
					this.db.extendData.put(arr12, 1);
					return true;
				}
				if (arr10 >= arr0 && arr11 >= arr1) {

				}
				return false;
			} else {
				return false;
			}
		}
		if (this.prop.isTarget(String.valueOf(msg.params[0]))) {
			return true;
		}
		if (this.prop.isTarget("") && prop.getType() != TaskType.reachPos.getValue()) {
			return true;
		}
		if ((this.prop.getType() == TaskType.TRAIN_EQUIP.getValue()) && (this.prop.isTarget("0"))) {
			return true;
		}
		return false;
	}

	public final boolean isTargetFromID(String id) {
		return this.prop.isTargetFormId(id);
	}

	public final String questTc() {
		return this.prop.questTC;
	}

	/**
	 * 提交任务
	 * 
	 * @return
	 */
	public final boolean submit() {
		if (this.db.state == TaskState.COMPLETED_NOT_DELIVERY.getValue()) {
			this.db.state = TaskState.COMPLETED.getValue();
			return true;
		}
		return false;
	}

	/**
	 * 判断任务是否完成(完成不一定提交)
	 */
	public final boolean isCompleted() {
		return this.db.state == TaskState.COMPLETED_NOT_DELIVERY.getValue();
	}

	/**
	 * 完成任务
	 */
	public final void complete() {
		if (this.db.state == TaskState.NOT_COMPLETED.getValue()) {
			this.db.progress = this.getTargetNum();
			this.db.state = TaskState.COMPLETED_NOT_DELIVERY.getValue();
		}
	}

	/**
	 * 判断是否是日常任务
	 */
	public final boolean isDaily() {
		return TaskKind.DAILY == this.getKind();
	}

	/**
	 * 判断是否是一条龙任务
	 */
	public final boolean isLoop() {
		return TaskKind.LOOP == this.getKind();
	}

	/**
	 * 判断是否是挖宝任务
	 */
	public final boolean isTreasure() {
		return TaskKind.TREASURE == this.getKind();
	}

	/**
	 * 自动接任务
	 * 
	 * @returns {boolean}
	 */
	public final boolean autoAccept() {
		if (this.prop.giveNpc == 0) {
			return true;
		}
		return false;
	}

	/**
	 * 获取任务状态
	 * 
	 * @returns {exports.TaskState.COMPLETED|*|Task.state}
	 */
	public final int getState() {
		return this.db.state;
	}

	public final void pushProgressTips(WNPlayer player) {
		if (this.db.progress > 0) {
			String tips = this.prop.changePrompt;
			if (tips.length() > 0) {
				String replaceStr = "(" + this.db.progress + "/" + this.getTargetNum() + ")";
				tips = tips.replace("{0}", replaceStr);
				player.sendSysTip(tips, Const.TipsType.NO_BG);
			}
		}
	}

	public final boolean onEvent(WNPlayer player, TaskEvent event) {
		boolean flag = false;
		if (this.db.state != TaskState.NOT_COMPLETED.getValue()) {
			return flag;
		}
		int name = event.type;
		if (name == this.getType()) {
			int targetNum = this.getTargetNum();
			if (this.isTargetID(event)) {
				Out.debug("enter  task onEvent");
				if (name == TaskType.LEVEL_UP.getValue() || name == TaskType.USERUP_LEVEL.getValue() || name == TaskType.TRAIN_EQUIP.getValue() || name == TaskType.MOUNT_UPLEVEL.getValue() || name == TaskType.WING_UPLEVEL.getValue() || name == TaskType.TRAIN_EQUIP_ALL.getValue()) {
					this.db.progress = Math.max((int) event.params[1], this.db.progress);
				} else {
					this.db.progress = this.db.progress + (int) event.params[1];
				}

				if (this.db.progress >= targetNum) {
					this.db.progress = targetNum;
					this.complete();
					if (this.getKind() == TaskKind.DAILY) {
						player.getPlayerTasks().dailyTaskFinEvent();
					} else if (this.getKind() == TaskKind.LOOP) {
						player.getPlayerTasks().loopTaskFinEvent();
					}

					// 任务完成
					BILogService.getInstance().ansycReportMission(player.getPlayer(), "任务完成", prop.kind, prop.iD, prop.name);
				}
				this.pushProgressTips(player);
				flag = true;
				// this.update();
			}
		}
		return flag;
	}

	public int getProgress() {
		return this.db.progress;
	}

	/**
	 * 设置任务进度
	 */
	public final boolean setProgress(int prgs) {
		this.db.progress = prgs;
		if (this.db.progress >= this.getTargetNum()) {
			this.complete();
		}
		return this.db.progress >= this.getTargetNum();
	}

	public int getSecProgress() {
		return secProgress;
	}

	public void setSecProgress(int secProgress) {
		this.secProgress = secProgress;
	}

	public final List<StringString> initBattleServerAttribute() {
		List<StringString> attr = new ArrayList<>();
		attr.add(new StringString("type", String.valueOf(this.getType())));
		attr.add(new StringString("areaId", String.valueOf(this.prop.doScene)));
		attr.add(new StringString("pointId", String.valueOf(this.prop.doPoint)));
		attr.add(new StringString("targetId", String.valueOf(this.prop.targetID)));

		// 子状态为1时表示完成任务目标未提交
		if (this.db.state == TaskState.COMPLETED_NOT_DELIVERY.getValue()) {
			attr.add(new StringString("state", "1"));
		} else {
			attr.add(new StringString("state", "0"));
		}
		// if (this.prop.secretTransfer != null && this.prop.secretTransfer.length() >
		// 0) {
		// attr.add(new StringString("SecretTransfer", this.prop.secretTransfer));
		// }
		Map<String, String> saved_attrs = this._getBattlerAttribute();
		for (Map.Entry<String, String> node : saved_attrs.entrySet()) {
			attr.add(new StringString(node.getKey(), node.getValue()));
		}
		return attr;
	};

	public final void setBattleAttribute(String key, String value) {
		Map<String, String> saved_attrs = this._getBattlerAttribute();
		if (value != null) {
			saved_attrs.put(key, value);
		} else {
			saved_attrs.remove(key);
		}
	};

	/**
	 * 游戏服提交给战斗服的数据
	 */
	public final JSONObject toJson4BattleServer() {
		JSONObject data = new JSONObject();
		data.put("QuestID", this.db.templateId);
		data.put("State", 1); // 已接受任务状态才有任务数据
		data.put("Attributes", this.initBattleServerAttribute());
		return data;
	}

	/**
	 * 获取序列化数据
	 */
	public TaskPO toJson4Serialize() {
		return this.db;
	};

	public final Task buildTask() {
		Task.Builder builder = Task.newBuilder();
		builder.setTemplateId(this.db.templateId);
		builder.setState(this.db.state);
		if (this.secProgress > -1) { // 一条龙/师门 任务进度, 或者 多目标任务
			builder.addProgress(this.secProgress);
		}
		builder.addProgress(this.db.progress);
		builder.setLeftTime(this.prop.limitTime);
		return builder.build();
	}

	public final TaskData clone() {
		TaskData task = new TaskData(prop);
		task.db.state = this.db.state;
		task.db.progress = this.db.progress;
		task.db.finishCount = this.db.finishCount;
		return task;
	}

}
