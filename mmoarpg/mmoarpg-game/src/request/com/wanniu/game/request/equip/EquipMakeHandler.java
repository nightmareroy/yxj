package com.wanniu.game.request.equip;

import java.io.IOException;
import java.util.List;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.core.util.StringUtil;
import com.wanniu.game.bag.WNBag.SimpleItemInfo;
import com.wanniu.game.common.Const;
import com.wanniu.game.common.Const.GOODS_CHANGE_TYPE;
import com.wanniu.game.common.Const.TaskType;
import com.wanniu.game.data.ext.EquipMakeExt;
import com.wanniu.game.equip.EquipCraftConfig;
import com.wanniu.game.equip.EquipCraftUtil;
import com.wanniu.game.item.ItemUtil;
import com.wanniu.game.item.NormalItem;
import com.wanniu.game.item.po.DetailItemNum;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.EquipHandler.EquipMakeRequest;
import pomelo.area.EquipHandler.EquipMakeResponse;

/**
 * 装备制作
 * 
 * @author Yangzz
 *
 */
@GClientEvent("area.equipHandler.equipMakeRequest")
public class EquipMakeHandler extends PomeloRequest {

	public PomeloResponse request() throws Exception {

		WNPlayer player = (WNPlayer) pak.getPlayer();

		EquipMakeRequest req = EquipMakeRequest.parseFrom(pak.getRemaingBytes());
		String targetCode = req.getC2STargetCode();

		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				EquipMakeResponse.Builder res = EquipMakeResponse.newBuilder();

				
				if (!player.functionOpenManager.isOpen(Const.FunctionType.Make.getValue())) {
					res.setS2CCode(Const.CODE.FAIL);
					res.setS2CMsg(LangService.getValue("FUNC_SET_PLAYED_NOT_OPEN"));
					body.writeBytes(res.build().toByteArray());
					return;
				}
				
				if (StringUtil.isEmpty(targetCode)) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("PARAM_ERROR"));
					body.writeBytes(res.build().toByteArray());
					return;
				}
				
				EquipMakeExt makeProp = EquipCraftConfig.getInstance().getEquipMakePropByCode(targetCode);
				if (makeProp == null) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("EQUIP_MAKE_NOT_MAKE_PAPER"));
					body.writeBytes(res.build().toByteArray());
					return;
				}


				// 检测金币
				if(player.player.gold < makeProp.costMoney) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("GOLD_NOT_ENOUGH"));
					body.writeBytes(res.build().toByteArray());
					return;
				}
				// 检查材料
				List<SimpleItemInfo> reqMate = makeProp.reqMate;
				for (int i = 0; i < reqMate.size(); ++i) {
					SimpleItemInfo itemInfo = reqMate.get(i);
					DetailItemNum mateInfo = player.getWnBag().findDetailItemNumByCode(itemInfo.itemCode);
					if (mateInfo.totalNum < itemInfo.itemNum) {
						res.setS2CCode(FAIL);
						res.setS2CMsg(LangService.getValue("EQUIP_MAKE_MATE_NOT_ENOUGH"));
						body.writeBytes(res.build().toByteArray());
						return;
					}
//					if (!resultBind) {
//						resultBind = mateInfo.unBindNum < itemInfo.itemNum;
//					}
				}

				// 检查背包空间
				Const.ForceType forceType = Const.ForceType.BIND;//resultBind ? Const.ForceType.BIND : Const.ForceType.UN_BIND;
				if (!player.getWnBag().testAddCodeItem(targetCode, 1, forceType, true)) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("BAG_NOT_ENOUGH_POS"));
					body.writeBytes(res.build().toByteArray());
					return;
				}

				// 扣除金币
				player.moneyManager.costGold(makeProp.costMoney, GOODS_CHANGE_TYPE.equipMake);
				
				// 扣除材料
//				boolean isUnBindFist = !resultBind;
//				player.getWnBag().discardItem(makeProp.paperCode, makeProp.paperCount, Const.GOODS_CHANGE_TYPE.equipMake, null, isUnBindFist, false);
//				Map<String, Integer> biItems = new HashMap<>();
//				biItems.put(makeProp.paperCode, makeProp.paperCount);
				for (int i = 0; i < reqMate.size(); ++i) {
					SimpleItemInfo mateInfo = reqMate.get(i);
					player.getWnBag().discardItem(mateInfo.itemCode, mateInfo.itemNum, Const.GOODS_CHANGE_TYPE.equipMake, null, false, false);
//					biItems.put(mateInfo.itemCode, mateInfo.itemNum);
				}
				// 给道具
				NormalItem newEquip = ItemUtil.createItemsByItemCode(targetCode, 1).get(0);
				newEquip.setBind(ItemUtil.getPropBindType(newEquip.prop, forceType));
				player.getWnBag().addEntityItem(newEquip, Const.GOODS_CHANGE_TYPE.equipMake, null, false, false);
				EquipCraftUtil.sendEquipMakeMsg(player, newEquip);
				
				// 更新任务状态
				player.taskManager.dealTaskEvent(TaskType.EQUIP_MAKE, "", 1);
				// 成就
				player.achievementManager.onEquipMake();
				// 打造红点
				player.equipManager.updateMakeScript(null);

				res.setS2CCode(OK);
				body.writeBytes(res.build().toByteArray());
			}
		};
	}
}