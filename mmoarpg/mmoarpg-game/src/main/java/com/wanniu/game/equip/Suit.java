package com.wanniu.game.equip;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.wanniu.core.logfs.Out;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.EquipHandler.SuitAttrSort;
import pomelo.area.EquipHandler.SuitTypeAttr;

public class Suit {

	/**
	 * 根据玩家当前身上套装信息
	 * 
	 * @param career
	 * @returns {Array}
	 */
	public static List<SuitTypeAttr> getPlayerSuitInfo(WNPlayer player) {
		List<SuitTypeAttr> data = new ArrayList<>();
		// int career = player.getPro();
		//
		// for(int i = 1; i <= 3; ++i){
		// SuitTypeAttr.Builder suitData = SuitTypeAttr.newBuilder();
		// suitData.setType(i);
		// // 先判断装备阶数
		// // result{minAdvance, minQuality, minLevel}
		// int[] result = player.equipManager.getSuitAdvancedAndQColor(i);
		// Out.debug(Suit.class, "getSuitAdvancedAndQColor result " + result);
		// if(result != null && result[0] > 0){
		// List<SuiteConfigExt> props =
		// EquipCraftConfig.getInstance().getSuiteConfig(career, i, result[1],
		// result[0]);
		// if(props.size() > 0) {
		// SuiteConfigExt prop = props.get(0);
		// SuitAttr.Builder attr = SuitAttr.newBuilder();
		// attr.setAdvanced(prop.upReq);
		// attr.setQColor(prop.equipQColor);
		// attr.addAllAttrs(AttributeUtil.getAttributeBase(prop.attrs));
		// suitData.setAttr(attr);
		// }
		// }
		// else {
		// // 再判断等级
		// result = player.equipManager.getSuitLevelAndQColor(i);
		// Out.debug(Suit.class, "getSuitLevelAndQColor result " + result);
		// if(result != null){
		// List<SuiteConfigExt> props =
		// EquipCraftConfig.getInstance().getSuiteConfig(career, i, result[1],
		// result[2], 0, -1);
		// if(props.size() > 0) {
		// SuiteConfigExt prop = props.get(0);
		// SuitAttr.Builder attr = SuitAttr.newBuilder();
		// attr.setLevel(prop.equipLevel);
		// attr.setQColor(prop.equipQColor);
		// attr.addAllAttrs(AttributeUtil.getAttributeBase(prop.attrs));
		// suitData.setAttr(attr);
		// }
		//
		// }
		// }
		// data.add(suitData.build());
		// }
		// Out.debug(Suit.class, "getPlayerSuitInfo data " + data);
		return data;
	};

	public static Map<String, Integer> getSuitAttr(int pro, EquipManager equipManager, boolean sendMsg, String[] uid, int type) {
		WNPlayer player = equipManager.player;
		Map<String, Integer> data = new HashMap<>();
		// for(int i = 1; i <= 3; i ++){
		// // result{minAdvance, minQuality, minLevel}
		// int[] result = null;//equipManager.getSuitAdvancedAndQColor(i);
		// if(result != null && result[0] > 0) {
		// List<SuitConfigExt> props =
		// EquipCraftConfig.getInstance().getSuiteConfig(pro, i, result[1], result[0]);
		//
		// if (props.size() > 0) {
		// SuitConfigExt prop = props.get(0);
		// data.putAll(prop.attrs);
		// if(sendMsg && i == type){
		// Map<Integer, Integer> suitCurLevel =
		// player.getPlayerAttach().miscData.suitCurLevel;
		// if(suitCurLevel.get(type) == null || suitCurLevel.get(type) == 0){
		// suitCurLevel.put(type, result[2]);
		//// equipManager.player.miscData.suitCurLevel[type] = result.minLevel;
		// MessageUtil.sendSysSuitMessage(player.getId(), player.getUid(),
		// player.getServerId(), 0, result[0], result[1],i, prop.attrs);
		// }else if(result[2] > suitCurLevel.get(type)){
		// suitCurLevel.put(type, result[2]);
		//// equipManager.player.miscData.suitCurLevel[type] = result.minLevel;
		// MessageUtil.sendSysSuitMessage(player.getId(), player.getUid(),
		// player.getServerId(), 0, result[0], result[1],i, prop.attrs);
		// }
		// }
		// }
		//
		// }
		// else{
		// result = null;// equipManager.getSuitLevelAndQColor(i);
		// if(result != null && result.length > 0){
		//// int pro, int suiteType, int equipQColor, int equipLevel_lte, int
		// equipLevel_gt, int equipLevel_OrderBy
		// List<SuitConfigExt> props =
		// EquipCraftConfig.getInstance().getSuiteConfig(pro, i, result[0], result[1],
		// 0, -1);
		//// var props = dataAccessor.suitProps.find({
		//// pro: pro,
		//// SuiteType: i,
		//// EquipQColor: result.qColor,
		//// EquipLevel:{
		//// "$lte": result.level,
		//// "$gt" : 0
		//// }
		//// },
		//// {
		//// $orderBy: {
		//// EquipLevel: -1
		//// }
		//// });
		// if(props.size() > 0){
		// SuitConfigExt prop = props.get(0);
		// data.putAll(prop.attrs);
		// if(sendMsg && i == type){
		//
		//// Map<Integer, Integer> suitCurLevel =
		// player.getPlayerAttach().miscData.suitCurLevel;
		//// if(suitCurLevel.get(type) == null || suitCurLevel.get(type) == 0){
		//// suitCurLevel.put(type, result[2]);
		////// player.miscData.suitCurLevel[type] = result.level;
		//// MessageUtil.sendSysSuitMessage(player.getId(), player.getUid(),
		// player.getServerId(), prop.equipLevel, 0, result[1],i, prop.attrs);
		//// }else if(result[2] > suitCurLevel.get(type)){
		//// suitCurLevel.put(type, result[2]);
		////// player.miscData.suitCurLevel[type] = result.level;
		//// MessageUtil.sendSysSuitMessage(player.getId(), player.getUid(),
		// player.getServerId(), prop.equipLevel, 0, result[1],i, prop.attrs);
		//// }
		// }
		// }
		// }
		// }
		// }

		return data;
	}

	/**
	 * 根据职业和套装类型获取所有等级下所有品质的套装属性
	 * 
	 * @param pro 职业
	 * @param type 套装类型
	 * @returns {Array}
	 */
	public static List<SuitAttrSort> getSuitTypeDetail(int pro, int type) {
		List<SuitAttrSort> data = new ArrayList<>();
		// List<SuiteLevelCO> levelAttr =
		// EquipCraftConfig.getInstance().getSuitLevelProps();
		// if(levelAttr.size() == 0){
		// return data;
		// }
		//
		// for(int i = 0; i < levelAttr.size(); ++i){
		// List<SuiteConfigExt> props =
		// EquipCraftConfig.getInstance().getSuiteConfig(pro, type,
		// levelAttr.get(i).suiteLevel, -1);
		// props.sort(new Comparator<SuiteConfigExt>() {
		//
		// @Override
		// public int compare(SuiteConfigExt a, SuiteConfigExt b) {
		// return a.equipQColor > b.equipQColor ? 1 : -1;
		// }
		//
		// });
		//
		//
		// if(props.size() == 0){
		// continue;
		// }
		// SuitAttrSort.Builder suitData = SuitAttrSort.newBuilder();
		// suitData.setLevel(levelAttr.get(i).suiteLevel);
		// List<SuitAttr> attrs = new ArrayList<>();
		//
		// for(int j = 0; j < props.size(); ++j){
		// SuiteConfigExt prop = props.get(j);
		// SuitAttr.Builder attr = SuitAttr.newBuilder();
		// attr.setLevel(prop.equipLevel);
		// attr.setQColor(prop.equipQColor);
		// attr.addAllAttrs(AttributeUtil.getAttributeBase(prop.attrs));
		// attrs.add(attr.build());
		// }
		// suitData.addAllAttr(attrs);
		//
		// data.add(suitData.build());
		// }
		//
		// int maxAdvanced = levelAttr.get(0).suiteReqMax;
		// for(int i = 1; i <= maxAdvanced; ++i){
		// List<SuiteConfigExt> props =
		// EquipCraftConfig.getInstance().getSuiteConfig(pro, type, -1, i);
		//
		// if(props.size() == 0){
		// continue;
		// }
		// SuitAttrSort.Builder suitData = SuitAttrSort.newBuilder();
		// suitData.setAdvanced(i);
		// List<SuitAttr> attrs = new ArrayList<>();
		// for(int j = 0; j < props.size(); ++j){
		// SuiteConfigExt prop = props.get(j);
		// SuitAttr.Builder attr = SuitAttr.newBuilder();
		// attr.setAdvanced(prop.upReq);
		// attr.setQColor(prop.equipQColor);
		// attr.addAllAttrs(AttributeUtil.getAttributeBase(prop.attrs));
		// attrs.add(attr.build());
		// }
		// suitData.addAllAttr(attrs);
		// data.add(suitData.build());
		// }

		Out.debug(Suit.class, data);
		return data;
	};
}
