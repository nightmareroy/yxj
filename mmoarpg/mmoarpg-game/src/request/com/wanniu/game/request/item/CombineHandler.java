package com.wanniu.game.request.item;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.bag.WNBag;
import com.wanniu.game.bag.WNBag.SimpleItemInfo;
import com.wanniu.game.common.Const;
import com.wanniu.game.data.CombineTypeCO;
import com.wanniu.game.data.GameData;
import com.wanniu.game.data.base.DItemBase;
import com.wanniu.game.data.ext.CombineExt;
import com.wanniu.game.equip.EquipCraftConfig;
import com.wanniu.game.equip.EquipCraftUtil;
import com.wanniu.game.item.ItemUtil;
import com.wanniu.game.item.po.DetailItemNum;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.sevengoal.SevenGoalManager.SevenGoalTaskType;

import pomelo.area.ItemHandler.CombineRequest;
import pomelo.area.ItemHandler.CombineResponse;

/**
 * 请求道具合成
 * 
 * @author Yangzz
 *
 */
@GClientEvent("area.itemHandler.combineRequest")
public class CombineHandler extends PomeloRequest {

	public PomeloResponse request() throws Exception {

		WNPlayer player = (WNPlayer) pak.getPlayer();
		WNBag bag = player.getWnBag();

		CombineRequest req = CombineRequest.parseFrom(pak.getRemaingBytes());
		int destId = req.getC2SDestID();
		int num = req.getC2SNum();
//		int index = req.getC2SGridIndex();

		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				CombineResponse.Builder res = CombineResponse.newBuilder();

				CombineExt prop = EquipCraftConfig.getInstance().getCombineProp(destId);
				if (prop == null) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("ITEM_NOT_COMBINE"));
					body.writeBytes(res.build().toByteArray());
					return;
				}

				// var biItems = {earItems: {}, costItems: {}};
				DItemBase productProp = ItemUtil.getUnEquipPropByCode(prop.destCode);
				if (productProp == null) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("ITEM_NOT_COMBINE"));
					body.writeBytes(res.build().toByteArray());
					return;
				}

				Map<String, List<Integer>> destCounts = new HashMap<>();
				destCounts.put("bind", new ArrayList<>());
				destCounts.put("unBind", new ArrayList<>());

				for (int i = 0; i < prop.material.size(); ++i) {
					SimpleItemInfo itemInfo = prop.material.get(i);
					DetailItemNum countInfo = bag.findDetailItemNumByCode(itemInfo.itemCode);
					double maxNum = Math.floor(countInfo.totalNum / itemInfo.itemNum);
					if (maxNum < num) {
						res.setS2CCode(FAIL);
						res.setS2CMsg(LangService.getValue("ITEM_COMBINE_NOT_ENOUGH"));
						body.writeBytes(res.build().toByteArray());
						return;
					}
					int unBindNum = 0;
					int bindNum = 0;
//					if (!isBindFirst) {
//						int unBindMax = (int) Math.floor(countInfo.unBindNum / itemInfo.itemNum);
//
//						unBindNum = (int) Math.min(unBindMax, num);
//						bindNum = Math.max(num - unBindNum, 0); // 不能小于0
//					} else {
						// 优先合成绑定的
						int bindMax = (int) Math.floor(countInfo.bindNum / itemInfo.itemNum);
						int leftCount = countInfo.bindNum % itemInfo.itemNum;
						if (leftCount > 0 && (leftCount + countInfo.unBindNum > itemInfo.itemNum)) {
							bindMax += 1;// 可以消耗部分未绑定材料
						}
						bindNum = (int) Math.min(bindMax, num);
						unBindNum = Math.max(num - bindNum, 0);// 不能小于0
//					}
					if (bindNum > 0) {
						destCounts.get("bind").add(bindNum);
					}
					if (unBindNum > 0) {
						destCounts.get("unBind").add(unBindNum);
					}
				}

				List<SimpleItemInfo> destItems = new ArrayList<>();
				for (String key : destCounts.keySet()) {
					int destCount = 0;
					List<Integer> list_binds = destCounts.get(key);
					if (list_binds.size() > 0) {
						destCount = Collections.min(list_binds);// Math.min.apply(null,
																// array)获得数组里面最小的一个值
					}
					if (destCount > 0) {
						SimpleItemInfo itemInfo = new SimpleItemInfo();
						itemInfo.itemCode = prop.destCode;
						itemInfo.itemNum = destCount;
						itemInfo.forceType = key.equals("bind") ? Const.ForceType.BIND : Const.ForceType.UN_BIND;
						destItems.add(itemInfo);
					}
				}
				if (!bag.testAddCodeItems(destItems)) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("BAG_NOT_ENOUGH_POS"));
					body.writeBytes(res.build().toByteArray());
					return;
				}

				for (int i = 0; i < prop.material.size(); ++i) {
					SimpleItemInfo v = prop.material.get(i);
					int totalNeed = v.itemNum * num;

					int leftCost = totalNeed;
//					if (isPointIndex && v.itemCode.equals(item.itemDb.code)) {
//						int costPosNum = Math.min(item.itemDb.groupCount, totalNeed);
//						bag.discardItemByPos(index, costPosNum, false, Const.GOODS_CHANGE_TYPE.compound);
//						leftCost -= costPosNum;
//					}
					if (leftCost > 0) {
						bag.discardItem(v.itemCode, leftCost, Const.GOODS_CHANGE_TYPE.compound, null, false, false);
					}
				}
				bag.addCodeItems(destItems, Const.GOODS_CHANGE_TYPE.compound);

				player.taskManager.dealTaskEvent(Const.TaskType.COMBINE_GEM, prop.destCode, num);

				// 成就
				if (productProp.itemSecondType == Const.ItemSecondType.gem.getValue()) {
					player.achievementManager.onGemCombine(prop.destCode, num);
				}
				List<CombineTypeCO> combineTypeProps = GameData.findCombineTypes(t -> {
					return t.tagetCode.equals(prop.destCode);
				});
				CombineTypeCO combineTypeProp = null;
				if (combineTypeProps.size() > 0) {
					combineTypeProp = combineTypeProps.get(0);
					CombineTypeCO combineIdProp = GameData.CombineTypes.get(combineTypeProp.parentID);
					if (combineIdProp != null) {
						// TODO BI
						// if(combineTypeProp.parentID > 10000){
						// CombineTypeCO tagNameProp =
						// GameData.CombineTypes.get(combineIdProp.parentID);
						// if(tagNameProp != null){
						// player.biServerManager.equipCompose(tagNameProp.ItemName,
						// biItems.earItems, biItems.costItems);
						// }
						// if(tagNameProp.iD == 101){
						// player.biServerManager.gemOperation(1, prop.ItemName,
						// productProp.Name, num);
						// }
						// }else{
						// player.biServerManager.equipCompose(combineIdProp.ItemName,
						// biItems.earItems, biItems.costItems);
						// }
					}

				}
				if (prop.isNotice != 0) {
					EquipCraftUtil.sendEquipCombineMsg(player, productProp);
				}

				player.sevenGoalManager.processGoal(SevenGoalTaskType.GEM_COMBINE_COUNT, productProp.levelReq,num);
				res.setS2CCode(OK);
				body.writeBytes(res.build().toByteArray());
			}
		};
	}
}
