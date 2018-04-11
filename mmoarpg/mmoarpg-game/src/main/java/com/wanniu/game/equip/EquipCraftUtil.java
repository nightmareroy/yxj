package com.wanniu.game.equip;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.wanniu.core.game.LangService;
import com.wanniu.core.logfs.Out;
import com.wanniu.core.util.RandomUtil;
import com.wanniu.game.chat.ChannelUtil;
import com.wanniu.game.common.Const;
import com.wanniu.game.common.Utils;
import com.wanniu.game.common.msg.MessageUtil;
import com.wanniu.game.data.base.DItemEquipBase;
import com.wanniu.game.item.ItemUtil;
import com.wanniu.game.item.NormalItem;
import com.wanniu.game.player.GlobalConfig;
import com.wanniu.game.player.PlayerUtil;
import com.wanniu.game.player.WNPlayer;

/**
 * 装备加工辅助工具
 * 
 * @author Yangzz
 *
 */
public class EquipCraftUtil {
	// 精炼
	// public static RefineCO getRefinePropByColor(int color){
	// return EquipCraftConfig.getInstance().getRefinePropByColor(color);
	// };
	//
	// public static RefineMagicCO getRefineMagicPropById(int propID){
	// return EquipCraftConfig.getInstance().getRefineMagicPropById(propID);
	// };
	//
	//// var getRefineMagicPropList(){
	//// return dataAccessor.equipRefineMagicProps.data();
	//// };
	//
	// public static List<RefineMagicCO> getRefineMagicPropListByGroup(int groupId){
	// return EquipCraftConfig.getInstance().getRefineMagicPropListByGroup(groupId);
	// };

	// //读取所有分组id
	public static List<Integer> getRefineMagicGroupIds() {
		return EquipCraftConfig.getInstance().getRefineMagicGroupIds();
	};

	// //升级
	// var getEquipLevelUpProp(id){
	// var list = dataAccessor.equipLevelUpProps.find({ID: id});
	// if (list.length > 0) {
	// return list[0];
	// }
	// return null;
	// };
	//
	// public static EquipLevelUpCO getEquipLevelUpPropByCode(String code){
	// return EquipCraftConfig.getInstance().getEquipLevelUpPropByCode(code);
	// };

	// //品质进阶
	// var getEquipColorUpProp(id){
	// var list = dataAccessor.equipColorUpProps.find({ID: id});
	// if (list.length > 0) {
	// return list[0];
	// }
	// return null;
	// };
	//
	// //品质进阶
	// var getEquipColorUpPropByCode(id){
	// var list = dataAccessor.equipColorUpProps.find({Code: id});
	// if (list.length > 0) {
	// return list[0];
	// }
	// return null;
	// };
	//
	// var getEquipMakeProp(id){
	// var list = dataAccessor.equipMakeProps.find({ID: id});
	// if(list.length > 0){
	// return list[0];
	// }
	// return null;
	// };
	//
	// public static EquipMakeExt getEquipMakePropByCode(String paperCode){
	// return EquipCraftConfig.getInstance().getEquipMakePropByCode(paperCode);
	// };

	// var levelUpCodes = [];
	// var getEquipLevelUpMateCodeList(){
	// if(levelUpCodes.length > 0){
	// return levelUpCodes;
	// }
	// var list = dataAccessor.equipLevelUpProps.data();
	// for(var i = 0; i < list.length; ++i){
	// var mateCode = list[i].ReqMateCode;
	// if(mateCode && levelUpCodes.indexOf(mateCode) === -1){
	// levelUpCodes.push(mateCode);
	// }
	// }
	// return levelUpCodes;
	// };

	/**
	 * 从所有分组中 随机获得需要增加的分组，直到分组id（最多重复2次）用完或增加满
	 * 
	 * @param oldGroupIds 已有的分组id数组
	 * @param allGroupIds 所有组的id
	 * @param maxCount 最大数量
	 * @param maxRepeatGroupNum 最大分组重复数量
	 * @returns {Array} 随机添加的分组id
	 */
	public static List<Integer> getAddGroupIds(List<Integer> oldGroupIds, List<Integer> allGroupIds, int maxCount, int maxRepeatGroupNum) {
		List<Integer> result = new ArrayList<>();
		int maxRepeatNum = maxRepeatGroupNum;
		if (maxRepeatNum == 0) {
			maxRepeatNum = 2;
		}
		List<Integer> addIds = new ArrayList<>();
		if (oldGroupIds.size() >= maxCount) {
			return result; // 分组已满
		}
		Map<Integer, Integer> groupCount = new HashMap<>();
		List<Integer> oldList = new ArrayList<>();
		List<Integer> allList = new ArrayList<>();
		allGroupIds.forEach(groupId -> {
			allList.add(groupId);
		});

		Iterator<Integer> iter = oldGroupIds.iterator();
		while (iter.hasNext()) {
			int groupId = iter.next();

			oldList.add(groupId);
			if (groupCount.get(groupId) == null || groupCount.get(groupId) == 0) {// 重复了，后面不在随机
				groupCount.put(groupId, 1);
			} else {
				groupCount.put(groupId, groupCount.get(groupId) + 1);
			}
			if (groupCount.get(groupId) >= maxRepeatNum) {
				int index = allList.indexOf(groupId);
				allList.remove(index);
			}
		}

		for (int i = oldList.size(); i < maxCount; ++i) {
			int index = Utils.random(1, allList.size());
			if (index <= 0) {
				break;
			}
			index = index - 1; // 下标从0开始
			int groupId = allList.get(index); // 重复的前面已经去除了
			addIds.add(groupId);
			if (groupCount.get(groupId) != null && groupCount.get(groupId) != 0) {
				groupCount.put(groupId, groupCount.get(groupId) + 1);
			} else {
				groupCount.put(groupId, 1);
			}
			if (groupCount.get(groupId) >= maxRepeatNum) {
				allList.remove(index); // allList.splice(index, 1);
			}
		}

		return addIds;
	};

	/**
	 * 从所有分组中 随机获得需要一个分组id
	 * 
	 * @param oldGroupIds 已有的分组id数组
	 * @param allGroupIds 所有组的id
	 * @param refreshIndex 需要重置的序号
	 * @param maxRepeatGroupNum 最大分组重复数量
	 * @returns {number}
	 */
	public static int getRefreshGroupId(List<Integer> oldGroupIds, List<Integer> allGroupIds, int refreshIndex, int maxRepeatGroupNum) {
		if (refreshIndex >= oldGroupIds.size()) {
			return 0; // 下标未找到
		}
		List<Integer> oldList = new ArrayList<>();
		oldGroupIds.forEach(groupId -> {
			oldList.add(groupId);
		});
		// 先删掉一个，再生成一个
		oldList.remove(refreshIndex);
		List<Integer> addIds = getAddGroupIds(oldList, allGroupIds, oldGroupIds.size(), maxRepeatGroupNum);
		if (addIds.size() > 0) {
			return addIds.get(0);
		}
		return 0;
	}

	// var getEquipMakeMsg(playerName, targetName){
	// var msgIndex = utils.random(1,3);
	// var msgKey = "EQUIPMENT_MAKE_SPEAKER" + msgIndex;
	// var resStr = strList[msgKey];
	// resStr = resStr.replace("{playerName}",playerName);
	// resStr = resStr.replace("{ItemName}", targetName);
	// return resStr;
	// };
	//
	//
	public static void sendEquipCombineMsg(WNPlayer player, DItemEquipBase prop) {
		int minColor = GlobalConfig.Item_Qcolor_Combine_Notice;
		Out.debug("sendEquipCombineMsg minColor", minColor, ",itemColor:", prop.qcolor, ", item:", prop.code);
		if (prop.qcolor < minColor) {
			return;
		}

		String playerName = PlayerUtil.getColorPlayerNameByPro(player.player.pro, player.getName());
		String targetName = ItemUtil.getColorItemNameByQcolor(prop.qcolor, prop.name);
		int msgIndex = RandomUtil.getInt(1, 3);
		String msgKey = "ITEM_COMBINE_SPEAKER" + msgIndex;
		String msgStr = LangService.getValue(msgKey);
		msgStr = msgStr.replace("{playerName}", playerName);
		msgStr = msgStr.replace("{ItemName}", targetName);
		MessageUtil.sendRollChat(player.getLogicServerId(), msgStr, Const.CHAT_SCOPE.SYSTEM);
	};

	public static void sendEquipMakeMsg(WNPlayer player, NormalItem item) {
		int minColor = GlobalConfig.Equipment_Qcolor_Make_Notice;
		if (item.prop.qcolor < minColor) {
			return;
		}

		String playerName = PlayerUtil.getColorPlayerNameByPro(player.getPro(), player.getName());
		// String targetName =
		// ItemUtil.getColorItemNameByQcolor(item.prop.qcolor,item.prop.name);
		String targetName = LangService.getValue(MessageUtil.getColorLink(item.prop.qcolor));
		String itemLink = ChannelUtil.setItemInfo(item);
		targetName = targetName.replace("{a}", item.prop.name).replace("{b}", itemLink);

		int msgIndex = Utils.random(1, 3);
		String msgKey = "EQUIPMENT_MAKE_SPEAKER" + msgIndex;
		String msgStr = LangService.getValue(msgKey);
		msgStr = msgStr.replace("{playerName}", playerName);
		msgStr = msgStr.replace("{ItemName}", targetName);
		MessageUtil.sendRollChat(player.getLogicServerId(), msgStr, Const.CHAT_SCOPE.SYSTEM);
	};

	public static void sendEquipLevelUpMsg(WNPlayer player, NormalItem item) {
		int minUpLevel = GlobalConfig.Equipment_UpLevel_LevelUp_Notice;
		if (item.getUpLevel() < minUpLevel) {
			return;
		}

		String playerName = PlayerUtil.getColorPlayerNameByPro(player.getPro(), player.getName());
		String targetName = ItemUtil.getColorItemNameByQcolor(item.prop.qcolor, item.prop.name);
		String levelKey = "upnmb" + item.getUpLevel();
		String levelName = LangService.getValue(levelKey);

		int msgIndex = Utils.random(1, 3);
		String msgKey = "EQUIPMENT_LEVELUP_SPEAKER" + msgIndex;
		String msgStr = LangService.getValue(msgKey);
		msgStr = msgStr.replace("{playerName}", playerName);
		msgStr = msgStr.replace("{EquipmentName}", targetName);
		msgStr = msgStr.replace("{EquipmentWearLv}", levelName);
		MessageUtil.sendRollChat(player.getLogicServerId(), msgStr, Const.CHAT_SCOPE.SYSTEM);
	};

	// var sendEquipColorUpMsg(player, item){
	// var minColor = configUtil.getGlobalConfig("Equipment.Qcolor.Upgrade.Notice");
	// if(item.prop.Qcolor < minColor){
	// return;
	// }
	// var playerUtil = require("../util/playerUtil");
	// var itemUtil = require("../util/itemUtil");
	//
	// var playerName = playerUtil.getColorPlayerNameByPro(player.pro,
	// player.getName());
	// var targetName = itemUtil.getColorItemNameByQcolor(item.prop.Qcolor,
	// item.prop.Name);
	// var msgIndex = utils.random(1,3);
	// var msgKey = "EQUIPMENT_UPGRADE_SPEAKER" + msgIndex;
	// var msgStr = strList[msgKey];
	// msgStr = msgStr.replace("{playerName}",playerName);
	// msgStr = msgStr.replace("{EquipmentName}", targetName);
	// MessageUtil.sendWorldContent(player.getLogicServerId(), msgStr,
	// Const.CHAT_SCOPE.WORLD);
	// };
	//
	// var test(){
	// var res = getAddGroupIds([], [1001,1002,1003,1004], 8, 2);//每个组最多2个，刚好用完
	// console.log(res);
	//
	// dataAccessor.initArea();
	// var groupIds = getRefineMagicGroupIds();
	// var addGroupIds = getAddGroupIds([], groupIds, 8, 2);
	// console.log("addGroupIds:" + addGroupIds + ", groupIds:" + groupIds);
	// for(var i = 0; i < addGroupIds.length; ++i) {
	// var groupId = addGroupIds[i];
	// var propList = getRefineMagicPropListByGroup(groupId);
	// console.log("propList.length:" + propList.length + ", groupId:" + groupId);
	// var randIndex = utils.random(0, propList.length - 1);
	// var magicProp = propList[randIndex];
	// console.log("groupId:" +groupId + ", prop:" + JSON.stringify(magicProp));
	// }
	// };
	//
	// var testResetProb(){//品质概率测试
	// dataAccessor.initArea();
	//
	// var newGroupId = 1;
	//
	// var totalWeight = 0;
	// var list = getRefineMagicPropListByGroup(newGroupId);
	// list.forEach(function(v){
	// totalWeight += v.Rare;
	// });
	//
	// var execNum = totalWeight * 10;
	// var data = {colorCount:{},colorProb:{}, total: 0};
	// for(var i = 0; i < execNum; ++i){
	// var propList = getRefineMagicPropListByGroup(newGroupId);
	// var randIndex = utils.randIndexByWeight(propList, "Rare"); //"Rare"为权值字段
	// var newProp = propList[randIndex];
	// if(data.colorCount[newProp.Qcolor]){
	// data.colorCount[newProp.Qcolor] += 1;
	// }else{
	// data.colorCount[newProp.Qcolor] = 1;
	// }
	// data.total += 1;
	// }
	// for(var key in data.colorCount){
	// data.colorProb[key] = "" + (100 * data.colorCount[key] / data.total) + "%";
	// }
	//
	// console.log(util.inspect(data, false, 10));
	//
	// };
}
