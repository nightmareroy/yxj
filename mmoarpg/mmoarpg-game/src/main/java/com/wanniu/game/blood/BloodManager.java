package com.wanniu.game.blood;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.wanniu.core.logfs.Out;
import com.wanniu.game.common.Const;
import com.wanniu.game.common.Const.ManagerType;
import com.wanniu.game.common.Const.PlayerBtlData;
import com.wanniu.game.common.Const.PlayerEventType;
import com.wanniu.game.common.ConstsTR;
import com.wanniu.game.common.ModuleManager;
import com.wanniu.game.data.BloodListCO;
import com.wanniu.game.data.BloodSuitConfigCO;
import com.wanniu.game.data.GameData;
import com.wanniu.game.data.ext.BloodSuitListExt;
import com.wanniu.game.item.ItemUtil;
import com.wanniu.game.item.NormalItem;
import com.wanniu.game.mail.SysMailConst;
import com.wanniu.game.player.BILogService;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.poes.BloodPO;
import com.wanniu.redis.PlayerPOManager;

public class BloodManager extends ModuleManager {

	private WNPlayer player;
	public BloodPO bloodPO;

	// Quest表遗留了之前的任务道具，已经上线不能删，所以这里只能屏蔽
	public static List<String> itemFilter = Arrays.asList("vq01", "vq02", "vq03", "vq04", "vq05", "vq06", "vq07", "vq08", "vq09", "vq10");

	public BloodManager(WNPlayer player) {
		this.player = player;
		BloodPO bloodPO = PlayerPOManager.findPO(ConstsTR.player_blood, player.getId(), BloodPO.class);
		if (bloodPO == null) {
			bloodPO = new BloodPO(player.getId());
			PlayerPOManager.put(ConstsTR.player_blood, player.getId(), bloodPO);
		}
		this.bloodPO = bloodPO;
	}

	private void _init() {

	}

	public List<NormalItem> getBloodItems() {
		List<NormalItem> bloods = player.bag.findItemBySecondType(Const.ItemSecondType.virtQuest);

		return bloods;
	}

	/**
	 * 获取激活的套装
	 */
	public Map<Integer, List<Integer>> getActivedSuits() {
		Map<Integer, List<Integer>> activedSuits = new HashMap<>();
		for (Map.Entry<Integer, Integer> entry : bloodPO.equipedMap.entrySet()) {
			for (BloodSuitListExt bloodSuitListExt : GameData.BloodSuitLists.values()) {
				if (bloodSuitListExt.partIdList.contains(entry.getValue())) {
					if (!activedSuits.containsKey(bloodSuitListExt.suitID2)) {
						activedSuits.put(bloodSuitListExt.suitID2, new LinkedList<>());
					}
					List<Integer> suitList = activedSuits.get(bloodSuitListExt.suitID2);
					suitList.add(entry.getValue());
				}
			}
		}

		// for (BloodSuitListExt bloodSuitListExt : GameData.BloodSuitLists.values()) {
		// if(bloodSuitListExt.partIdList.contains(player.getPro()))
		// {
		// Map<Integer, PlayerItemPO> suit=new HashMap<>();
		// activedSuits.put(bloodSuitListExt.suitID2, suit);
		//
		// for (Integer partId : bloodSuitListExt.partIdList) {
		// for (PlayerItemPO playerItemPO : bloodPO.equipedMap.values()) {
		// BloodListCO
		// bloodListCO=GameData.BloodLists.get(GameData.Quests.get(playerItemPO.code).min);
		// if(bloodListCO.bloodID==partId)
		// {
		// suit.put(partId, playerItemPO);
		// break;
		// }
		// }
		//
		//
		// }
		//
		// }
		// }

		return activedSuits;
	}

	/**
	 * 装备血脉，相同位置已有血脉，则替换
	 */
	public int equipBlood(String itemId) {
		NormalItem item = player.bag.findItemById(itemId);
		int pos = player.bag.findPosById(itemId);
		if (pos == -1) {
			return 1;
		}
		if (item.prop.itemSecondType != Const.ItemSecondType.virtQuest.getValue()) {
			return 2;
		}
		if (itemFilter.contains(item.itemCode())) {
			return 3;
		}
		BloodListCO bloodListCO = GameData.BloodLists.get(GameData.Quests.get(item.itemCode()).min);

		// 背包中先移除血脉
		player.bag.discardItemByPos(pos, 0, true, Const.GOODS_CHANGE_TYPE.blood);

		// 如果已装备血脉，则卸载
		if (bloodPO.equipedMap.containsKey(bloodListCO.sortID3)) {
			int oldBloodId = bloodPO.equipedMap.get(bloodListCO.sortID3);
			String oldBloodCode = "blood" + oldBloodId;
			NormalItem entityItem = ItemUtil.createItemsByItemCode(oldBloodCode, 1).get(0);
			player.bag.addEntityItem(entityItem, Const.GOODS_CHANGE_TYPE.blood, null, false, false);

		}

		bloodPO.equipedMap.put(bloodListCO.sortID3, bloodListCO.bloodID);

		blooodSync();

		this.ansycReportBloodChange(1, bloodListCO.sortID3, bloodListCO);

		return 0;
	}

	// 1=穿, 2=脱
	private void ansycReportBloodChange(int type, int position, BloodListCO bloodListCO) {
		try {
			Map<String, Object> data = new HashMap<>();
			data.put("code", bloodListCO.code);
			data.put("name", bloodListCO.bloodName);
			for (BloodSuitListExt bloodSuitListExt : GameData.BloodSuitLists.values()) {
				if (bloodSuitListExt.partIdList.contains(bloodListCO.bloodID)) {
					data.put("suitID", bloodSuitListExt.suitID2);
					data.put("suitName", bloodSuitListExt.suitName);
					break;
				}
			}
			BILogService.getInstance().ansycReportBloodChange(player.getPlayer(), type, position, data);
		} catch (Exception e) {
			Out.warn("ansycReportBloodChange", e);
		}
	}

	/**
	 * 脱血脉
	 */
	public boolean unequipBlood(int sortId) {
		if (!bloodPO.equipedMap.containsKey(sortId)) {
			return false;
		}
		int bloodId = bloodPO.equipedMap.get(sortId);

		String code = "blood" + bloodId;

		// NormalItem normalItem = ItemUtil.createItemByDbOpts(playerItemPO);
		
		player.bag.addCodeItemMail(code, 1, Const.ForceType.DEFAULT, Const.GOODS_CHANGE_TYPE.blood,SysMailConst.BAG_FULL_COMMON);

		bloodPO.equipedMap.remove(sortId);


		blooodSync();

		List<BloodListCO> cs = GameData.findBloodLists(v -> code.equals(v.code));
		if (!cs.isEmpty()) {
			BloodListCO bloodListCO = cs.get(0);
			this.ansycReportBloodChange(2, bloodListCO.sortID3, bloodListCO);
		}

		return true;
	}

	/**
	 * 获取血脉所有可提供的经验
	 */
	// public int getBloodAllExp(int pos)
	// {
	// int result=0;
	//
	// NormalItem item = player.bag.getItem(pos);
	// if(item.prop.itemSecondType!=Const.ItemSecondType.virtQuest.getValue())
	// {
	// return result;
	// }
	// BloodListCO
	// bloodListCO=GameData.BloodLists.get(GameData.Quests.get(item.itemCode()).min);
	//
	//
	//
	//
	// for(int i=1;i<bloodData.lv;i++)
	// {
	// BloodLVUpCO bloodLVUpCO=GameData.BloodLVUps.get(i);
	// switch (bloodListCO.bloodQColor) {
	// case 1:
	// result+=bloodLVUpCO.experience1;
	// break;
	// case 2:
	// result+=bloodLVUpCO.experience2;
	// break;
	// case 3:
	// result+=bloodLVUpCO.experience3;
	// break;
	// case 4:
	// result+=bloodLVUpCO.experience4;
	// break;
	// default:
	// return -1;
	// }
	//
	// }
	//
	// result+=bloodListCO.exp;
	// result+=bloodData.exp;
	//
	// return result;
	// }

	/**
	 * 强化
	 */
	// public boolean strengthen(int sortId,List<String> itemIds)
	// {
	// PlayerItemPO playerItemPO_target = bloodPO.equipedMap.get(sortId);
	// if(playerItemPO_target==null)
	// {
	// return false;
	// }
	//
	// List<PlayerItemPO> playerItemPO_sourcies=new LinkedList<>();//
	// for (String itemId : itemIds) {
	// PlayerItemPO playerItemPO_source = player.bag.findItemById(itemId).itemDb;
	// if(playerItemPO_source==null)
	// {
	// return false;
	// }
	// playerItemPO_sourcies.add(playerItemPO_source);
	// }
	//
	//
	//
	//
	// BloodListCO
	// bloodListCO_target=GameData.BloodLists.get(GameData.Quests.get(playerItemPO_target.code).min);
	//// BloodListCO
	// bloodListCO_source=GameData.BloodLists.get(GameData.Quests.get(normalItem_source.itemCode()).min);
	//
	// for (PlayerItemPO playerItemPO_source : playerItemPO_sourcies) {
	// playerItemPO_target.bloodData.exp+=BloodManager.getBloodAllExp(playerItemPO_source.bloodData);
	// }
	//
	//
	// while (true)
	// {
	// if(playerItemPO_target.bloodData.lv>=GameData.BloodLVLimits.get(bloodListCO_target.bloodQColor).expLimit)
	// {
	// break;
	// }
	//
	// BloodLVUpCO
	// bloodLVUpCO=GameData.BloodLVUps.get(bloodListCO_target.bloodQColor);
	// int experience=0;
	// switch (bloodListCO_target.bloodQColor) {
	// case 1:
	// experience=bloodLVUpCO.experience1;
	// break;
	// case 2:
	// experience=bloodLVUpCO.experience2;
	// break;
	// case 3:
	// experience=bloodLVUpCO.experience3;
	// break;
	// case 4:
	// experience=bloodLVUpCO.experience4;
	// break;
	// default:
	// Out.error("参数错误");
	// return false;
	// }
	//
	// if(playerItemPO_target.bloodData.exp<experience)
	// {
	// break;
	// }
	//
	// playerItemPO_target.bloodData.exp-=experience;
	// playerItemPO_target.bloodData.lv++;
	//
	// }
	//
	// blooodSync();
	// return true;
	// }

	// public BloodInfo getBloodInfo()
	// {
	// BloodInfo.Builder bloodInfoBuilder = BloodInfo.newBuilder();
	// player.bag.addEntityItem(item, fromDes);
	// }

	/**
	 * 计算单个血脉属性
	 */
	public Map<PlayerBtlData, Integer> calSingleInfluence(int sortId) {
		Map<PlayerBtlData, Integer> data = new ConcurrentHashMap<>();
		if (!bloodPO.equipedMap.containsKey(sortId)) {
			return data;
		}

		BloodListCO bloodListCO = GameData.BloodLists.get(bloodPO.equipedMap.get(sortId));

		// BloodProCO bloodProCO=GameData.BloodPros.get(bloodData.attrId);
		// BloodLVLimitCO
		// bloodLVLimitCO=GameData.BloodLVLimits.get(bloodListCO.bloodQColor);

		// float scale = 1F+(float)bloodLVLimitCO.ratio/100F*(bloodData.lv-1);
		//
		// data.put(Const.PlayerBtlData.getE(bloodProCO.prop1),
		// (int)(bloodProCO.num1*scale));
		// data.put(Const.PlayerBtlData.getE(bloodProCO.prop2),
		// (int)(bloodProCO.num2*scale));
		// data.put(Const.PlayerBtlData.getE(bloodProCO.prop3),
		// (int)(bloodProCO.num3*scale));
		// data.put(Const.PlayerBtlData.getE(bloodProCO.prop4),
		// (int)(bloodProCO.num4*scale));

		if (bloodListCO.proNum >= 1) {
			data.put(Const.PlayerBtlData.getE(bloodListCO.prop1), bloodListCO.num1);
		}
		if (bloodListCO.proNum >= 2) {
			data.put(Const.PlayerBtlData.getE(bloodListCO.prop2), bloodListCO.num2);
		}
		if (bloodListCO.proNum >= 3) {
			data.put(Const.PlayerBtlData.getE(bloodListCO.prop3), bloodListCO.num3);
		}
		if (bloodListCO.proNum >= 4) {
			data.put(Const.PlayerBtlData.getE(bloodListCO.prop4), bloodListCO.num4);
		}

		return data;
	}

	/**
	 * 计算血脉所有属性
	 */
	public Map<PlayerBtlData, Integer> calAllInfluence() {
		Map<PlayerBtlData, Integer> data = new ConcurrentHashMap<>();

		// 单独属性
		for (Map.Entry<Integer, Integer> entry1 : bloodPO.equipedMap.entrySet()) {
			Map<PlayerBtlData, Integer> singleData = calSingleInfluence(entry1.getKey());
			for (Map.Entry<PlayerBtlData, Integer> entry2 : singleData.entrySet()) {
				PlayerBtlData key = entry2.getKey();
				Integer value = entry2.getValue();
				if (data.containsKey(key)) {
					data.put(key, data.get(key) + value);
				} else {
					data.put(key, value);
				}
			}

		}

		// 套装属性
		Map<Integer, List<Integer>> activedSuits = getActivedSuits();
		for (Map.Entry<Integer, List<Integer>> entry : activedSuits.entrySet()) {
			List<BloodSuitConfigCO> bloodSuitConfigCOs = GameData.findBloodSuitConfigs((t) -> t.suitID == entry.getKey() && t.partReqCount <= entry.getValue().size());

			for (BloodSuitConfigCO bloodSuitConfigCO : bloodSuitConfigCOs) {
				PlayerBtlData key = Const.PlayerBtlData.getE(bloodSuitConfigCO.prop);
				Integer value = bloodSuitConfigCO.num;
				if (data.containsKey(key)) {
					data.put(key, data.get(key) + value);
				} else {
					data.put(key, value);
				}
			}

		}
		return data;
	}

	/**
	 * 属性同步
	 */
	public void blooodSync() {
		this.player.btlDataManager.data_blood = calAllInfluence();
		this.player.btlDataManager.calFinalData();
		this.player.onBloodChange();
		// this.player.refreshBattlerServerAvatar();
	}

	@Override
	public void onPlayerEvent(PlayerEventType eventType) {
		switch (eventType) {

		default:
			break;
		}
	}

	@Override
	public ManagerType getManagerType() {
		// TODO Auto-generated method stub
		return ManagerType.BLOOD;
	}

}