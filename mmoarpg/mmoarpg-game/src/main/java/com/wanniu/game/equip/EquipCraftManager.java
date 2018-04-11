package com.wanniu.game.equip;

import com.wanniu.game.player.WNPlayer;

/**
 * 装备强化相关
 * 
 * @author Yangzz
 *
 */
public class EquipCraftManager {

	public WNPlayer player;

	public EquipCraftManager(WNPlayer player) {
		this.player = player;
	};

	// public void getSuperScript (){
	// var data = [];
	// // 各个加工模块相应的接口
	// data = data.concat(this.getLevelUpScript());
	// return data;
	// };

	// public void getLevelUpScript (){
	// Map<String, Integer> numberMap = Utils.ofMap("levelUp", 0, "colorUp", 0);
	// Map<String, Integer> levelUpEquipMap = new HashMap<>();
	// Map<String, Integer> colorUpEquipMap = new HashMap<>();
	// Map<String, Integer> mateCountMap = new HashMap<>();
	// Map<String, Integer> bagOriginMap = new HashMap<>();
	//
	//
	// int minLevelUpColor = GlobalConfig.Equipment_LvUp_QColor;
	// int minColorUpColor = GlobalConfig.Equipment_Upgrade_QColor;
	// for(int pos : this.player.equipManager.equips.keySet()){
	// NormalEquip item = this.player.equipManager.equips.get(pos);
	// if(!this.checkEquip(item)){
	// continue;
	// }
	// if(item.getQColor() >= minLevelUpColor){
	// levelUpEquipMap.put(item.itemDb.code, 1);
	// }
	// if(item.getQColor() >= minColorUpColor){
	// colorUpEquipMap.put(item.itemDb.code, 1);
	// }
	// }
	// for (int i = 1; i <= this.player.bag.bagPO.bagGridCount; ++i) {
	// NormalItem item = this.player.bag.bagGrids.get(i);
	// if (item == null) {
	// continue;
	// }
	// String code = item.itemDb.code;
	// NormalEquip equip = null;
	// if (item.isEquip()) {
	// if (!this.checkEquip(item)) {
	// continue;
	// }
	// equip = (NormalEquip) item;
	// if(equip.getQColor() >= minLevelUpColor){ //升级装备统计
	// if(!equip.isProcessed()){ //未加工过的装备统计，可以用作升级材料
	// if (bagOriginMap.containsKey(code)) {
	// bagOriginMap.put(code, bagOriginMap.get(code) + item.itemDb.groupCount);
	// } else {
	// bagOriginMap.put(code, item.itemDb.groupCount);
	// }
	// }
	// }
	// }
	// else {
	// if (mateCountMap.containsKey(code)) {//非装备统计
	// mateCountMap.put(code, mateCountMap.get(code) + item.itemDb.groupCount);
	// } else {
	// mateCountMap.put(code, item.itemDb.groupCount);
	// }
	// }
	// }
	//
	//// for(String code : levelUpEquipMap.keySet()){ //升级红点
	//// var levelUpProp = EquipCraftUtil.getEquipLevelUpPropByCode(code);
	//// if(!levelUpProp){
	//// continue;
	//// }
	//// var prop = itemUtil.getPropByCode(levelUpProp.TagetCode);
	//// if ((prop.UpReq && this.player.upLevel < prop.UpReq) || (!prop.UpReq &&
	// this.player.level < prop.LevelReq)) {
	//// continue;
	//// }
	//// int meetNum = 0; //满足条件数量
	//// if(levelUpEquipMap[code] >= 1){
	//// if(bagOriginMap[code] && bagOriginMap[code] >= 1){
	//// meetNum += 1; //当作材料的原始装备数量条件
	//// }
	//// }
	//// var itemCode = levelUpProp.ReqMateCode;
	//// var itemNum = levelUpProp.ReqMateCount;
	//// if(mateCountMap[itemCode] && mateCountMap[itemCode] >= itemNum){
	//// meetNum += 1; //满足材料数量条件
	//// }
	////
	//// if(meetNum >= 2 || (levelUpProp.Relation === 0 && meetNum >= 1)){
	//// numberMap.levelUp = 1;
	//// break;
	//// }
	//// }
	//// for(var code in colorUpEquipMap){//升品红点
	//// var colorUpProp = EquipCraftUtil.getEquipColorUpPropByCode(code);
	//// if(!colorUpProp){
	//// continue;
	//// }
	//// var prop = itemUtil.getPropByCode(colorUpProp.TagetCode);
	//// if ((prop.UpReq && this.player.upLevel < prop.UpReq) || (!prop.UpReq &&
	// this.player.level < prop.LevelReq)) {
	//// continue;
	//// }
	//// var canColorUp = true;
	//// var mateList = colorUpProp.mateList;
	//// for(var i = 0; i < mateList.length; ++i){
	//// var itemInfo = mateList[i];
	//// if(!mateCountMap[itemInfo.itemCode] || mateCountMap[itemInfo.itemCode] <
	// itemInfo.itemNum){
	//// canColorUp = false;
	//// break;
	//// }
	//// }
	//// if(canColorUp){
	//// numberMap.colorUp = 1;
	//// break;
	//// }
	//// }
	//
	// var data = [];
	// data.push({type: Const.SUPERSCRIPT_TYPE.EQUIP_LEVEL_UP, number:
	// numberMap.levelUp});
	// data.push({type: Const.SUPERSCRIPT_TYPE.EQUIP_COLOR_UP, number:
	// numberMap.colorUp});
	// return data;
	// };

	// public boolean checkEquip (NormalItem equip){
	// if(equip == null || !equip.isEquip()){
	// return false;
	// }
	// if(equip.prop.itemType != Const.ItemType.Weapon.getValue() &&
	// equip.prop.itemType != Const.ItemType.Armor.getValue()){
	// return false;
	// }
	// return true;
	// };

}
