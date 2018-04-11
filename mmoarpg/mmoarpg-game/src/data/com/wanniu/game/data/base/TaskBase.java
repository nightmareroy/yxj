package com.wanniu.game.data.base;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.wanniu.core.logfs.Out;
import com.wanniu.core.util.StringUtil;
import com.wanniu.game.common.Const.PlayerPro;

public abstract class TaskBase {

	public Integer getKey() {
		return this.iD;
	}

	public int getId() {
		return iD;
	}

	public int getKind() {
		return kind;
	}

	public int getType() {
		return type;
	}

	/** 任务编号 */
	public int iD;
	/** 任务名称 */
	public String name;
	/** 任务种类 */
	public int kind;
	/** 显示分类 */
	public int show;
	/** 任务类型 */
	public int type;
	/** 会失败 */
	public int isFail;
	/** 失败条件关系 */
	public int failRelation;
	/** 失败条件 */
	public int failCondition;
	/** 需要进阶等级 */
	public int upOrder;
	/** 需要等级 */
	public int level;
	/** 进阶等级上限 */
	public int upLimit;
	/** 等级上限 */
	public int levelLimit;
	/** 任务难度 */
	public String difficulty;
	/** 职业限制 */
	public String job;
	/** 环日常分类 */
	public int taskCycle;
	/** 前置任务关系 */
	public int beforeRelations;
	/** 前置任务 */
	public String before;
	/** 后置任务 */
	public String next;
	/** 任务限时 */
	public int limitTime;
	/** 任务目标ID */
	public String targetID;
	/** 目标名字 */
	public String targetName;
	/** 任务目标需求数量 */
	public int quantity;
	/** 目标来源 */
	public String targetFromID;
	/** 目标来源备注 */
	public String sourceRemark;
	/** 任务TC */
	public String questTC;
	/** 最小等级 */
	public int minLevel;
	/** 最大等级 */
	public int maxLevel;
	/** 难度档 */
	public int diff;
	/** 开放策略 */
	public int openWay;
	/** 开放日 */
	public String open;
	/** 节日开始日期 */
	public String festivalStart;
	/** 节日结束日期 */
	public String festivalEnd;
	/** 开始时间 */
	public String openTime;
	/** 结束时间 */
	public String endTime;
	/** 次数策略 */
	public int count;
	/** 完成次数 */
	public int finishTimes;
	/** 快速完成 */
	public int isFastComplete;
	/** 快速完成花费钻石 */
	public int fastCompleteCost;
	/** 双倍领取 */
	public int isDouble;
	/** 双倍领取花费钻石 */
	public int doubleCost;
	/** 接受任务道具 */
	public String acceptItem;
	/** 接任务前提示 */
	public String beforePrompt;
	/** 接任务前“前往”文字 */
	public String goToBefore;
	/** 任务描述 */
	public String des;
	/** 任务目标描述 */
	public String prompt;
	/** 接任务NPC */
	public int giveNpc;
	/** 接任务NPC名字 */
	public String giveNpcName;
	/** 接任务时对白 */
	public String acceptDialogue;
	/** 经验奖励 */
	public int exp;
	/** 修为奖励 */
	public int upExp;
	/** 经验万分比 */
	public int expRatio;
	/** 金币奖励 */
	public int gold;
	/** 奖励物品TC */
	public String tcReward;
	/** 狂战士获取的奖励 */
	public String warriorReward;
	/** 刺客获得的奖励 */
	public String assassinReward;
	/** 魔法师获得的奖励 */
	public String magicianReward;
	/** 猎人获得的奖励 */
	public String hunterReward;
	/** 牧师获得的奖励 */
	public String ministerReward;
	/** 奖励物品展示 */
	public String rewardName;
	/** 炼魂道具 */
	public String soulItem;
	/** 接任务场景ID */
	public int startScene;
	/** 接任务路点ID */
	public int startPoint;
	/** 完成任务场景ID */
	public int doScene;
	/** 完成任务路点ID */
	public int doPoint;
	/** 是否可传送前往 */
	public int isTransfer;
	/** 完成任务界面 */
	public String funID;
	/** 任务场景打开界面 */
	public String taskFunID;
	/** 特殊情况提示 */
	public String specialTips;
	/** 交任务场景ID */
	public int submitScene;
	/** 交任务路点ID */
	public int submitPoint;
	/** 秘境传送 */
	public String secretTransfer;
	/** 接受按钮文字 */
	public String acceptBtn;
	/** 拒绝按钮文字 */
	public String rejectBtn;
	/** 接受任务后对白 */
	public String acceptContent;
	/** 接受任务后“前往”文字 */
	public String goToAccept;
	/** 交任务“前往”文字 */
	public String goToComplete;
	/** 交任务对白 */
	public String afterPrompt;
	/** 交任务对白按钮文字 */
	public String afterPromptBtn;
	/** 领奖励对白 */
	public String reward;
	/** 领奖按钮文字 */
	public String rewardBtn;
	/** 
	 * 交任务的NPCid<br/>
		不填时为达成任务条件自动完成。<br/>
		-1：完成时自动接取后一个任务
	 */
	public int completeNpc;
	/** 交任务NPC名字 */
	public String completeNpcName;
	/** 计数变化提示 */
	public String changePrompt;
	/** 接任务系统 */
	public String acceptSys;
	/** 交任务系统 */
	public String rewardSys;
	/** 对话框自动弹出 */
	public int nPCchat;
	/** 任务栏展示 */
	public int showItem;
	/** 特效框展示 */
	public int notEffected;
	public String needNpcID;
	public String needNpcName;
	public int needState;
	public String createPos;
	public int overState;
	public String overPos;
	/** 任务自动进行 */
	public int isAuto;
	//一条龙专用字段//////////////////////////////////////////////
	public String circleOutPOS;
	public int circleDungeonID;
	public int[] loopOutPos;
	//////////////////////////////////////////////
	/** 变身的avatar ID */
	public String modID;
	/** 清除变身的avatar */
	public int overState2;
	
	public int pro;
	public List<Integer> needNpcs = new ArrayList<>();

	public Map<Integer, List<ItemNode>> rewards = new HashMap<>(0);
	public List<ItemNode> accepTaskRewards = new ArrayList<>(0);
	public String[] beforeTask = new String[0];
	public String[] nextTask = new String[0];
	public List<String> targets = new ArrayList<>();
	public String[] targetFromIds = new String[0];
	public String[] openDay = new String[0];

	public static class ItemNode {
		public String itemCode;
		public int itemNum;
		public int isBind;

		public ItemNode(String itemCode, int itemNum) {
			this.itemCode = itemCode;
			this.itemNum = itemNum;
		}

		public ItemNode(String itemCode, int itemNum, int isBind) {
			this(itemCode, itemNum);
			this.isBind = isBind;
		}
	}

	public final boolean isTarget(String id) {
		if (this.targets != null) {
			for (int i = 0; i < this.targets.size(); i++) {
				if (this.targets.get(i).equals(id)) {
					return true;
				}
			}
		}
		return false;
	}

	public final boolean isTargetFormId(String id) {
		if (this.targetFromIds != null) {
			for (int i = 0; i < this.targetFromIds.length; i++) {
				if (id.equals(this.targetFromIds[i])) {
					return true;
				}
			}
		}
		return false;
	}

	public void beforeProperty() {
	}

	public void initProperty() {
		this.pro = PlayerPro.Value(this.job);
		// 物品奖励
		this.rewards = new HashMap<>();
		List<ItemNode> nodes = new ArrayList<>();
		nodes.add(new ItemNode("gold", this.gold));
		nodes.add(new ItemNode("exp", this.exp));
		nodes.add(new ItemNode("upexp", this.upExp));
		rewards.put(PlayerPro.COMMON.value, nodes);
		rewards.put(PlayerPro.CANG_LANG.value, getReward(this.warriorReward));
		rewards.put(PlayerPro.YU_JIAN.value, getReward(this.assassinReward));
		rewards.put(PlayerPro.YI_XIAN.value, getReward(this.magicianReward));
		rewards.put(PlayerPro.SHEN_JIAN.value, getReward(this.hunterReward));
		rewards.put(PlayerPro.LI_NHU.value, getReward(this.ministerReward));

		// 接受任务奖励
		this.accepTaskRewards = getReward(this.acceptItem.split(","));

		if(StringUtil.isNotEmpty(this.before)) {
			this.beforeTask = this.before.split(":");
		}
		if(StringUtil.isNotEmpty(this.next)) {
			this.nextTask = this.next.split(":");
		}
		this.targets = Arrays.asList(this.targetID.split(":"));
		this.targetFromIds = this.targetFromID.split(":");

		if (this.quantity <= 0) {
			Out.warn("策划数据有问题!!! 任务表, id:" , this.iD , ",任务目标需求数量:" , this.quantity , ",这样会导致任务直接完成!!!!!!");
		}
		this.openDay = this.open.split(",");
		
		// 所需Npc
		if(StringUtil.isNotEmpty(this.needNpcID)) {
			String[] npcs = this.needNpcID.split("\\|");
			for (int i = 0; i < npcs.length; i++) {
				this.needNpcs.add(Integer.parseInt(npcs[i]));
			}
		}
		
		if (StringUtil.isNotEmpty(circleOutPOS)) {
			loopOutPos = new int[2];
			String[] circlePOSes = circleOutPOS.split(",");
			int index = 0;
			for (String pos : circlePOSes) {
				loopOutPos[index] = (int) Float.parseFloat(pos);
				index ++;
			}
		}
	}

	private List<ItemNode> getReward(String reward) {
		return getReward(reward.split("\\|"));
	}

	private List<ItemNode> getReward(String[] rewards) {
		List<ItemNode> data = new ArrayList<>();
		for (String v : rewards) {
			if (v.trim().length() > 0) {
				String[] str = v.split(":");
				String itemCode = str[0];
				int itemNum = Integer.parseInt(str[1]);
				int isBind = 1;
				if (str.length >= 3) {
					isBind = Integer.parseInt(str[2]);
				}
				data.add(new ItemNode(itemCode, itemNum, isBind));
			}
		}
		return data;
	};

}
