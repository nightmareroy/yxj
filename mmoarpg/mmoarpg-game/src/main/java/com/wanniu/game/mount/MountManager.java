package com.wanniu.game.mount;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.wanniu.game.common.CommonUtil;
import com.wanniu.game.common.Const;
import com.wanniu.game.common.Const.EventType;
import com.wanniu.game.common.Const.FunctionType;
import com.wanniu.game.common.Const.GOODS_CHANGE_TYPE;
import com.wanniu.game.common.Const.ManagerType;
import com.wanniu.game.common.Const.PlayerBtlData;
import com.wanniu.game.common.Const.PlayerEventType;
import com.wanniu.game.common.Const.TaskType;
import com.wanniu.game.common.ConstsTR;
import com.wanniu.game.common.ModuleManager;
import com.wanniu.game.data.GameData;
import com.wanniu.game.data.ext.RideListExt;
import com.wanniu.game.data.ext.SkinListExt;
import com.wanniu.game.player.AttributeUtil;
import com.wanniu.game.player.BILogService;
import com.wanniu.game.player.GlobalConfig;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.player.bi.LogReportService;
import com.wanniu.game.poes.MountPO;
import com.wanniu.game.rank.RankType;
import com.wanniu.game.sevengoal.SevenGoalManager.SevenGoalTaskType;
import com.wanniu.game.task.TaskEvent;
import com.wanniu.redis.PlayerPOManager;

import pomelo.Common.AttributeBase;
import pomelo.area.MountHandler.MountData;
import pomelo.area.MountHandler.MountFlagPush;
import pomelo.area.MountHandler.MountNewSkinPush;
import pomelo.area.PlayerHandler.SuperScriptType;

public class MountManager extends ModuleManager {
	private WNPlayer player;
	public MountPO mount;
	public Map<PlayerBtlData, Integer> data_mount_show = new HashMap<>();
	public Map<PlayerBtlData, Integer> data_mount_show_next = new HashMap<>();
	public Map<PlayerBtlData, Integer> data_mount_final = new HashMap<>();

	public MountManager(WNPlayer player, MountPO mount) {
		this.player = player;
		this.mount = mount;
		player.player.openMount = mount != null;
		calMountData();
	}

	public static MountPO createMount(String playerId) {
		MountPO mount = new MountPO();
		mount.rideLevel = GlobalConfig.Ride_InitUpLevel;
		mount.starLv = GlobalConfig.Ride_InitStar;
		mount.mountSkins = new ArrayList<>();
		mount.usingSkinId = GlobalConfig.Ride_DefaultSkinID;
		mount.mountSkins.add(mount.usingSkinId);

		PlayerPOManager.put(ConstsTR.mountTR, playerId, mount);
		return mount;
	}

	/**
	 * 计算坐骑的属性加成
	 */
	private void calMountData() {
		if (mount == null || !player.functionOpenManager.isOpen(FunctionType.MOUNT.getValue())) {
			return;
		}

		data_mount_show.clear();
		data_mount_final.clear();
		data_mount_show = MountUtil.getMountBaseProp(mount.rideLevel, mount.starLv);
		int next_lvl = mount.rideLevel;
		int next_star = mount.starLv + 1;
		if (next_star > GlobalConfig.mountMaxStar) {
			next_lvl++;
			next_star = 0;
		}
		if (next_lvl > GlobalConfig.Ride_MaxUpLevel)
			data_mount_show_next.clear();
		else
			data_mount_show_next = MountUtil.getMountBaseProp(next_lvl, next_star);

		AttributeUtil.addData2AllData(data_mount_show, data_mount_final);
		int maxSpeed = 0;
		for (int skinId : mount.mountSkins) {
			SkinListExt skin = GameData.SkinLists.get(skinId);
			if (skin != null) {
				AttributeUtil.addData2AllData(skin.skinAttrs, data_mount_final);
				if (skin.skinAttrs.containsKey(PlayerBtlData.RunSpeed)) {
					if (skin.skinAttrs.get(PlayerBtlData.RunSpeed) > maxSpeed)
						maxSpeed = skin.skinAttrs.get(PlayerBtlData.RunSpeed);
				}
			}
		}
		// SkinListExt skin = GameData.findSkinLists((t)->t.skinID ==
		// mount.usingSkinId).get(0);
		// if(skin!=null){
		// AttributeUtil.addData2AllData(skin.skinAttrs, data_mount);
		// }
		data_mount_final.put(PlayerBtlData.RunSpeed, maxSpeed);
		if (mount.rideFlag == Const.MOUNT_RIDING_STATE.off.getValue()) {
			data_mount_final.remove(PlayerBtlData.RunSpeed);
		}

		this.refreshFightPower();
		if (player.rankManager != null) {
			player.rankManager.onEvent(RankType.Mount, mount.fightPower, mount.usingSkinId);
		}
	}

	/**
	 * 升星
	 * 
	 * @return
	 */
	public int upgradeStar() {
		if (mount.starLv >= GlobalConfig.mountMaxStar)
			return -1;
		RideListExt prop = GameData.findRideLists((t) -> t.rideLevel == mount.rideLevel).get(0);
		// int flag = player.bag.findItemNumByCode(prop.upStarItemCode);
		// System.out.println(flag);
		if (player.bag.discardItem(prop.upStarItemCode, prop.upStarItemCount, GOODS_CHANGE_TYPE.equipColorUp)) {
			mount.starLv++;
			calMountData();
			player.onMountPropChange();
		} else
			return -2;

		// 坐骑培养
		this.player.getPlayerTasks().dealTaskEvent(TaskType.TRAIN_RIDE, "0", 1);
		// 坐骑升阶
		this.player.getPlayerTasks().dealTaskEvent(TaskType.MOUNT_UPLEVEL, "0", mount.rideLevel * 11 + mount.starLv);

		LogReportService.getInstance().ansycReportMountUpgrade(player, mount.rideLevel, mount.starLv);
		BILogService.getInstance().ansycReportRideTrainBI(player.getPlayer(), 1, mount.rideLevel, mount.starLv, prop.upStarItemCode, prop.upStarItemCount);
		return 1;
	}

	/**
	 * 升阶
	 * 
	 * @return
	 */
	public int upgradeLv() {
		if (mount.rideLevel >= GlobalConfig.Ride_MaxUpLevel)
			return -1;
		if (mount.starLv != GlobalConfig.mountMaxStar)
			return -2;
		RideListExt prop = GameData.findRideLists((t) -> t.rideLevel == mount.rideLevel).get(0);
		if (player.bag.discardItem(prop.upLevelItemCode, prop.upLevelItemCount, GOODS_CHANGE_TYPE.equipLevelUp)) {
			mount.rideLevel++;
			mount.starLv = 0;
			calMountData();
			player.onMountPropChange();
			// addTestSKin();
		} else
			return -3;

		player.achievementManager.onHorseLevelChange(mount.rideLevel);
		// 坐骑培养
		this.player.getPlayerTasks().dealTaskEvent(TaskType.TRAIN_RIDE, "0", 1);
		// 坐骑升阶
		this.player.getPlayerTasks().dealTaskEvent(TaskType.MOUNT_UPLEVEL, "0", mount.rideLevel * 11 + mount.starLv);

		LogReportService.getInstance().ansycReportMountUpgrade(player, mount.rideLevel, mount.starLv);
		BILogService.getInstance().ansycReportRideTrainBI(player.getPlayer(), 2, mount.rideLevel, mount.starLv, prop.upLevelItemCode, prop.upLevelItemCount);

		player.sevenGoalManager.processGoal(SevenGoalTaskType.MOUNT_UPGRADE_LV, getMountLevel());
		return 1;
	}

	/**
	 * 换皮肤，玩家骑的其实是变色龙
	 * 
	 * @param skinId
	 * @return
	 */
	public int changeSkin(int _skinId) {
		if (mount == null) {
			return -1;
		}
		if (!mount.mountSkins.contains(_skinId)) {
			return -1;
		}
		if (mount.usingSkinId == _skinId) {
			return -2;
		}
		mount.usingSkinId = _skinId;
		calMountData();
		player.onMountPropChange();
		player.refreshBattlerServerAvatar();
		return 1;
	}

	/**
	 * 获得新皮肤
	 * 
	 * @param _skinId
	 * @return
	 */
	public int addNewSkin(int _skinId) {
		if (!player.player.openMount || mount == null)
			return -3;
		if (mount.mountSkins.contains(_skinId))
			return -1;
		if (!GameData.SkinLists.containsKey(_skinId))
			return -2;
		mount.mountSkins.add(_skinId);

		// 上报坐骑皮肤
		LogReportService.getInstance().ansycReportMountSkin(player, _skinId);
		SkinListExt prop = GameData.SkinLists.get(_skinId);
		if (prop != null) {
			BILogService.getInstance().ansycReportMountActivate(player.getPlayer(), _skinId, prop.skinName);
		}

		MountNewSkinPush.Builder data = MountNewSkinPush.newBuilder();
		data.setS2CSkinId(_skinId);
		player.receive("area.mountPush.mountNewSkinPush", data.build());

		// 成就
		player.achievementManager.onMountGot();
		return 1;
	}

	public int getMountLevel() {
		if (mount != null)
			return mount.rideLevel;
		return 0;
	}

	/**
	 * 刷新战斗力
	 */
	public void refreshFightPower() {
		mount.fightPower = CommonUtil.calFightPower(data_mount_final);
	}

	/**
	 * 开放功能
	 */
	public void openMount() {
		// this._pushAndRefreshMountChange();
		// this.updateDB();
		if (this.mount == null) {
			this.mount = createMount(player.getId());
			player.player.openMount = true;
			calMountData();
			player.onMountPropChange();
		}
		// MountNewSkinPush.Builder data = MountNewSkinPush.newBuilder();
		// data.setS2CSkinId(mount.usingSkinId);
		// player.receive("area.mountPush.mountNewSkinPush", data.build());

		// 成就
		// player.achievementManager.onMountGot();
	}

	public boolean isOpenMount() {
		if (mount != null)
			return true;
		return false;
	}

	public MountData.Builder getMountData() {
		MountData.Builder data = MountData.newBuilder();
		data.setRideLevel(mount.rideLevel);
		data.setUsingSkinID(mount.usingSkinId);
		data.addAllMountSkins(mount.mountSkins);
		List<AttributeBase> list = new ArrayList<>();
		for (PlayerBtlData pbd : data_mount_show.keySet()) {
			if (pbd == PlayerBtlData.RunSpeed)
				continue;
			AttributeBase.Builder ab = AttributeBase.newBuilder();
			ab.setId(pbd.id);
			ab.setMaxValue(data_mount_show.get(pbd));
			list.add(ab.build());
		}
		data.setStarLv(mount.starLv);
		data.setFightPowerValue(0);
		data.addAllMountAttrs(list);
		List<AttributeBase> list_next = new ArrayList<>();
		for (PlayerBtlData pbd : data_mount_show_next.keySet()) {
			if (pbd == PlayerBtlData.RunSpeed)
				continue;
			AttributeBase.Builder ab = AttributeBase.newBuilder();
			ab.setId(pbd.id);
			ab.setMaxValue(data_mount_show_next.get(pbd));
			list_next.add(ab.build());
		}
		data.addAllMountAttrsNext(list_next);
		return data;
	}

	public final int ridingMount(int isUp) {
		// 只要获取上马时间
		if (this.mount == null) {
			return 0;
		}
		int times = 0;
		this.player.getXmdsManager().refreshSummonMount(this.player.getId(), times, isUp);
		// if(isUp == Const.MOUNT_RIDING_STATE.on.getValue() ){
		// Out.info("请求上马");
		// }else{
		// Out.info("请求下马");
		// }
		// mount.rideFlag = isUp;
		// pushToClientMountsFlag();
		// player.refreshBattlerServerAvatar();
		// calMountData();
		// player.onMountPropChange();
		return 0;
	}

	public void onEvent(TaskEvent event) {
		if (event.type == EventType.summonMount.getValue()) {
			boolean flag = (boolean) event.params[0];
			// if (flag) {// 判断当前场景是否允许上马
			// if (!AreaUtil.canRideMount(player.getAreaId())) {
			// player.getXmdsManager().refreshSummonMount(this.player.getId(), 0, 0);
			// return;
			// }
			// }
			int rideFlag = flag ? Const.MOUNT_RIDING_STATE.on.getValue() : Const.MOUNT_RIDING_STATE.off.getValue();
			if (mount == null) {
				// Out.red("相同的状态" + rideFlag);
				return;
			}

			mount.rideFlag = rideFlag;
			// if(rideFlag == Const.MOUNT_RIDING_STATE.on.getValue())
			// Out.info("上马成功");
			// else
			// Out.info("下马成功");
			pushToClientMountsFlag();
			player.refreshBattlerServerAvatar();
			calMountData();
			player.onMountPropChange();
			// 只要上过马，就不能在挑选皮肤
			mount.firstChoose = true;
		}

	}

	public void pushToClientMountsFlag() {
		MountFlagPush.Builder data = MountFlagPush.newBuilder();
		if (mount == null) {
			data.setS2CFlag(Const.MOUNT_RIDING_STATE.off.getValue());
		} else {
			data.setS2CFlag(mount.rideFlag);
			data.setS2CUsingSkinId(mount.usingSkinId);
		}

		player.receive("area.mountPush.mountFlagPush", data.build());
	}

	public void unMountData() {
		if (mount != null) {
			if (mount.rideFlag == 0) {
				return;
			}
			mount.rideFlag = Const.MOUNT_RIDING_STATE.off.getValue();
			calMountData();
			player.onMountPropChange();
		}

		// this.flag = Const.MOUNT_RIDING_STATE.off.getValue();
		// this._pushAndRefreshMountChange();
		this.pushToClientMountsFlag();
	}

	public List<SuperScriptType> getSuperScript() {
		List<SuperScriptType> list = new ArrayList<>();
		int number = 0;
		SuperScriptType.Builder data = SuperScriptType.newBuilder();
		data.setType(Const.SUPERSCRIPT_TYPE.MOUNT.getValue());
		data.setNumber(number);

		list.add(data.build());
		return list;
	}

	public void addTestSKin() {
		List<Integer> mountSkins = mount.mountSkins;
		if (mountSkins.size() < 4) {
			int maxId = mountSkins.get(mountSkins.size() - 1);
			// mountSkins.add(maxId+1);
			addNewSkin(maxId + 1);
		}
	}

	@Override
	public void onPlayerEvent(PlayerEventType eventType) {
		switch (eventType) {
		case UPGRADE:

			break;

		default:
			break;
		}

	}

	@Override
	public ManagerType getManagerType() {
		return ManagerType.MOUNT;
	}

	/**
	 * 让角色拥有所有坐骑皮肤
	 */
	public void addAllSkin() {
		if (mount == null)
			return;
		mount.mountSkins = new ArrayList<>();
		for (SkinListExt skin : GameData.SkinLists.values()) {
			mount.mountSkins.add(skin.skinID);
		}
	}

	public boolean chooseFirstSkin(int skinId) {
		if (mount == null)
			return false;
		if (mount.firstChoose)
			return false;
		SkinListExt prop = GameData.SkinLists.get(skinId);
		if (prop == null || prop.skinQColor != 1) // 只有品质为1的可以挑选
			return false;
		mount.mountSkins.clear();
		mount.usingSkinId = skinId;
		mount.firstChoose = true;
		addNewSkin(skinId);
		return true;
	}
}
