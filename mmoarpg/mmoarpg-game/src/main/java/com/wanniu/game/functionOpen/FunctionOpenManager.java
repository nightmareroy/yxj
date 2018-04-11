package com.wanniu.game.functionOpen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.wanniu.core.game.LangService;
import com.wanniu.core.util.StringUtil;
import com.wanniu.game.common.Const;
import com.wanniu.game.common.Const.EventType;
import com.wanniu.game.common.Const.GOODS_CHANGE_TYPE;
import com.wanniu.game.common.ConstsTR;
import com.wanniu.game.data.GameData;
import com.wanniu.game.data.GuideCO;
import com.wanniu.game.data.OpenLvCO;
import com.wanniu.game.mail.SysMailConst;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.poes.FunctionOpenPO;
import com.wanniu.game.task.TaskEvent;
import com.wanniu.redis.PlayerPOManager;

import pomelo.area.FunctionOpenHandler.FunctionAwardListPush;
import pomelo.area.FunctionOpenHandler.FunctionOpenListPush;
import pomelo.player.PlayerOuterClass.FunctionInfo;

enum ReqType {
	LEVEL(0), // 无事件
	ACCEPT_TASK(1), // 接任务触发
	FINISH_TASK(2), // 完成任务触发
	INTERACT_ITEM(3); // 交互道具触发

	private int value;

	private ReqType(int value) {
		this.value = value;
	}

	public int getValue() {
		return this.value;
	}
};

public class FunctionOpenManager {

	public WNPlayer player;
	public FunctionOpenPO data;

	public FunctionOpenManager(WNPlayer player, FunctionOpenPO data) {
		this.player = player;

		if (data == null) {
			data = new FunctionOpenPO();
		}
		this.data = data;
		if (this.data.openMap == null) {
			this.data.openMap = new HashMap<>();
		}
		if (this.data.playMap == null) {
			this.data.playMap = new HashMap<>();
		}
		if (this.data.functionAwards == null) {
			this.data.functionAwards = new ArrayList<>();
		}
		this.init();
	}

	public void init() {
		List<OpenLvCO> propList = FunctionOpenUtil.getPropList();
		for (OpenLvCO prop : propList) {
			if (this.data.openMap.containsKey(prop.fun)) {
				continue;
			}
			if (prop.isReq == 0) {// 不需要事件
				if (this.checkLevelReq(prop)) {
					this.data.openMap.put(prop.fun, 1);
				}
			}
		}
	};

	/**
	 * 根据配置判断是否满足指定功能开放等级要求
	 * 
	 * @param prop 配置
	 * @returns {boolean} 等级，vip等级是否满足
	 */
	public boolean checkLevelReq(OpenLvCO prop) {
		int level = Math.max(prop.openLv, 1);
		int upLevel = prop.openUpLv;
		int vipLevel = prop.openVIPLv;

		if (upLevel > 0) {
			if (this.player.getPlayer().upLevel < upLevel)
				return false;
		} else if (this.player.getPlayer().level < level) {
			return false;
		}

		// if (prop.lvVIPRelations == 0) { // 等级和vip同时满足
		// if (upLevel > 0) {
		// if (this.player.getPlayer().upLevel < upLevel ||
		// this.player.baseDataManager.getVip() < vipLevel) {
		// return false;
		// }
		// } else if (this.player.getPlayer().level < level ||
		// this.player.baseDataManager.getVip() < vipLevel) {
		// return false;
		// }
		// } else { // 等级和vip只需满足一个
		// if (upLevel > 0) {
		// if (this.player.getPlayer().upLevel < upLevel &&
		// this.player.baseDataManager.getVip() < vipLevel) {
		// return false;
		// }
		// } else if (this.player.getPlayer().level < level &&
		// this.player.baseDataManager.getVip() < vipLevel) {
		// return false;
		// }
		// }
		return true;
	};

	public List<FunctionInfo> toJson4PayLoad() {
		List<FunctionInfo> data = new ArrayList<FunctionInfo>();

		List<OpenLvCO> propList = FunctionOpenUtil.getPropList();
		for (OpenLvCO prop : propList) {
			if (prop.isOpen > 0 && prop.type == 0) {
				continue; // 默认开启的不需要推送
			}
			FunctionInfo.Builder tempInfo = FunctionInfo.newBuilder();
			tempInfo.setFuncName(prop.fun);
			tempInfo.setOpenFlag(0);
			tempInfo.setPlayFlag(0);

			if (this.data.playMap.containsKey(prop.fun)) {
				tempInfo.setPlayFlag(1);
			}
			if (prop.isOpen > 0 && this.data.openMap.containsKey(prop.fun)) {
				tempInfo.setOpenFlag(1);
			}
			data.add(tempInfo.build());
		}
		// Out.debug("functionOpen all data: ", data);
		return data;
	};

	/**
	 * 根据功能名称列表，帅选开启对功能以供推送
	 * 
	 * @param nameArray
	 * @returns {Array}
	 */
	public List<FunctionInfo> getFunctionOpenPushList(List<String> nameArray) {
		List<FunctionInfo> data = new ArrayList<>();
		List<OpenLvCO> propList = FunctionOpenUtil.getPropListByFunctionNameArray(nameArray);
		for (OpenLvCO prop : propList) {
			if (prop.isOpen > 0 && prop.type == 0) {
				continue; // 默认开启的功能均不需要推送
			}
			FunctionInfo.Builder tempInfo = FunctionInfo.newBuilder();
			tempInfo.setFuncName(prop.fun);
			tempInfo.setOpenFlag(0);
			tempInfo.setPlayFlag(0);
			if (this.data.playMap.containsKey(prop.fun)) {
				tempInfo.setPlayFlag(1);
			}
			if (prop.isOpen > 0 && this.data.openMap.containsKey(prop.fun)) {
				tempInfo.setOpenFlag(1);
				data.add(tempInfo.build());
			}
		}
		// logger.debug('functionOpen push data:',data);
		return data;
	};

	/**
	 * 领取功能奖励
	 */
	public String receiveFunctionAward(int guideId) {
		if (this.data.functionAwards.contains(guideId)) {
			return LangService.getValue("SOLO_REWARD_HAS_DRAWED");
		}
		GuideCO guideCO = GameData.Guides.get(guideId);
		if (guideCO == null) {
			return LangService.getValue("SOLO_REWARD_NOT_EXIST");
		}

		// 更新状态
		this.data.functionAwards.add(guideId);
		// 发放奖励
		if (StringUtil.isNotEmpty(guideCO.reward)) {
			String[] codeNum = guideCO.reward.split(":");
			player.bag.addCodeItemMail(codeNum[0], Integer.parseInt(codeNum[1]), null, GOODS_CHANGE_TYPE.FUNCTION_OPEN, SysMailConst.BAG_FULL_COMMON);
		}
		return null;
	}

	/**
	 * 检测并推送功能奖励
	 */
	public void checkFunctoinAward() {
		FunctionAwardListPush.Builder functionAwardPush = FunctionAwardListPush.newBuilder();
		for (GuideCO guideCO : GameData.Guides.values()) {
			// 未开启
			if (this.player.getLevel() < guideCO.closeLv) {
				continue;
			}
			// 已领取
			if (this.data.functionAwards.contains(guideCO.iD)) {
				continue;
			}

			functionAwardPush.addGuideIds(guideCO.iD);
		}
		if (functionAwardPush.getGuideIdsCount() > 0) {
			player.receive("area.functionOpenPush.functionAwardListPush", functionAwardPush.build());
		}
	}

	/**
	 * 升级或进阶触发
	 */
	public void onUpgradeLevelOrVip() {
		List<Integer> reqTypeArray = new ArrayList<>();
		reqTypeArray.add(ReqType.LEVEL.getValue());
		reqTypeArray.add(ReqType.FINISH_TASK.getValue());// 等级会触发的功能类型
		List<OpenLvCO> propList = FunctionOpenUtil.getPropListByReqTypeArray(reqTypeArray);
		this.checkOpenFunctions(propList, null);

		// 检测功能奖励
		checkFunctoinAward();
	};

	public void checkAll() {
		List<OpenLvCO> propList = FunctionOpenUtil.getPropList();
		this.checkOpenFunctions(propList, null);
	};

	public void onFinishTask(String taskId) {
		// 任务触发的功能
		List<OpenLvCO> propList = FunctionOpenUtil.getPropListByReqEvent(ReqType.FINISH_TASK.getValue(), taskId);
		this.checkOpenFunctions(propList, taskId);
	};

	public void onAcceptTask(String taskId) {
		List<OpenLvCO> propList = FunctionOpenUtil.getPropListByReqEvent(ReqType.ACCEPT_TASK.getValue(), taskId);
		this.checkOpenFunctions(propList, taskId);
	};

	public void onInteract(String objId) {
		List<OpenLvCO> propList = FunctionOpenUtil.getPropListByReqEvent(ReqType.INTERACT_ITEM.getValue(), objId);
		this.checkOpenFunctions(propList, objId);
	};

	/**
	 * 任务触发
	 * 
	 * @param param
	 * @param chekcPropList 需要检查的功能配置列表
	 */
	public void checkOpenFunctions(List<OpenLvCO> propList, String param) {
		List<String> openIdArray = new ArrayList<>();
		for (OpenLvCO prop : propList) {
			if (this.data.openMap.containsKey(prop.fun)) {
				continue;
			}
			if (prop.isReq == 0) {
				if (!this.checkLevelReq(prop)) {
					continue;
				}
			} else if (prop.isReq == 1) { // 接任务
				if (!prop.openReq.equals(param)) {
					continue; // 任务不匹配
				}
			} else if (prop.isReq == 2) { // 完成任务
				if (!this.checkLevelReq(prop)) {
					continue; // 等级不够
				}
				if (!prop.openReq.equals(param)) {
					continue; // 任务不匹配
				}
				if (!player.getPlayerTasks().isFinishTask(Integer.parseInt(param))) {
					continue;// 任务未完成
				}
			} else if (prop.isReq == 3) { // 交互道具
				if (!prop.openReq.equals(param)) {
					continue; // 交互道具不匹配
				}
			} else {
				continue; // 未处理事件
			}
			// 条件满足
			this.data.openMap.put(prop.fun, 1);
			openIdArray.add(prop.fun);
		}
		this.onOpenNewFunctions(openIdArray);
	};

	public void pushAllFunctionOpenInfoToClient() {
		FunctionOpenListPush.Builder data = FunctionOpenListPush.newBuilder();
		data.setS2CCode(Const.CODE.OK);
		data.getS2CListBuilderList().clear();
		data.addAllS2CList(this.toJson4PayLoad());
		data.setS2CIsAll(1); // 所有

		player.receive("area.functionOpenPush.functionOpenListPush", data.build());
	};

	public void pushNewFunctionOpenInfoToClient(List<String> pushIdArray) {
		if (pushIdArray == null || pushIdArray.size() == 0) {
			return;
		}
		FunctionOpenListPush.Builder data = FunctionOpenListPush.newBuilder();
		data.setS2CCode(Const.CODE.OK);

		List<FunctionInfo> list = this.getFunctionOpenPushList(pushIdArray);
		if (list.size() == 0) {
			return;
		}

		data.addAllS2CList(list);
		data.setS2CIsAll(0); // 部分刷新
		player.receive("area.functionOpenPush.functionOpenListPush", data.build());
	};

	public void onOpenNewFunctions(List<String> openArray) {
		if (openArray.size() == 0) {
			return;
		}
		this.pushNewFunctionOpenInfoToClient(openArray);
		// // 检查翅膀和坐骑开启
		if (openArray.indexOf(Const.FunctionType.RIDING.getValue()) != -1 || openArray.indexOf(Const.FunctionType.MOUNT.getValue()) != -1) {
			this.player.mountManager.openMount();
		}
	};

	/**
	 * 事件处理
	 */
	public void onEvent(TaskEvent event) {
		if (event.type == EventType.interActiveItem.getValue()) {
			this.onInteract(event.params[0].toString());
		}
	};

	public void gmOpenFunction(int id) {
		if (id > 0) {
			OpenLvCO prop = FunctionOpenUtil.getPropById(id);
			if (prop == null) {
				return;
			}
			if (this.data.openMap.containsKey(prop.fun)) {
				return;
			}
			this.data.openMap.put(prop.fun, 1);
			List<String> idArray = new ArrayList<>();
			idArray.add(prop.fun);
			this.onOpenNewFunctions(idArray);
		} else {
			List<String> idArray = new ArrayList<>();
			List<OpenLvCO> propList = FunctionOpenUtil.getPropList();
			for (OpenLvCO prop : propList) {
				if (this.data.openMap.containsKey(prop.fun)) {
					continue;
				}
				this.data.openMap.put(prop.fun, 1);
				idArray.add(prop.fun);
			}
			this.onOpenNewFunctions(idArray);
		}
	};

	/**
	 * 判断功能是否开启
	 * 
	 * @param functionName 功能名称， 参考配置OpenLv.json的Fun字段和consts.FunctionType枚举
	 * @returns {boolean}
	 */
	public boolean isOpen(String functionName) {
		OpenLvCO prop = FunctionOpenUtil.getPropByName(functionName);
		// logger.info('isOpen:', prop);
		if (prop == null) { // 没有配置，默认开放
			return true;
		}
		if (prop.isOpen == 0) { // 策划配置中关闭
			return false;
		}
		if (prop.type == 0) { // 创建角色即开启
			return true;
		}
		if (!this.data.openMap.containsKey(functionName)) {
			return false;
		}
		return true;
	};

	// 离线玩家判断是否开启
	public static boolean IsOpen(String functionName, String playerId) {
		OpenLvCO prop = FunctionOpenUtil.getPropByName(functionName);
		// logger.info('isOpen:', prop);
		if (prop == null) { // 没有配置，默认开放
			return true;
		}
		if (prop.isOpen == 0) { // 策划配置中关闭
			return false;
		}
		if (prop.type == 0) { // 创建角色即开启
			return true;
		}
		FunctionOpenPO functionOpenPO = PlayerPOManager.findPO(ConstsTR.player_func_openTR, playerId, FunctionOpenPO.class);
		if (!functionOpenPO.openMap.containsKey(functionName)) {
			return false;
		}
		return true;
	};

	public int setFunctionPlayed(String funcName) {
		if (!this.isOpen(funcName)) {
			return -1; // 未开启，无法设置
		}
		if (this.data.playMap.containsKey(funcName)) {
			return 0; // 已设置
		}
		this.data.playMap.put(funcName, 1);
		return 0;
	};

}
