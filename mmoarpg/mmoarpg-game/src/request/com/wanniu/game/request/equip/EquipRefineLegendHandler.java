package com.wanniu.game.request.equip;

import java.io.IOException;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.common.Const;
import com.wanniu.game.common.Const.GOODS_CHANGE_TYPE;
import com.wanniu.game.common.msg.ErrorResponse;
import com.wanniu.game.data.GameData;
import com.wanniu.game.data.ext.KaiGuangExt;
import com.wanniu.game.equip.EquipManager;
import com.wanniu.game.equip.NormalEquip;
import com.wanniu.game.item.NormalItem;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.sevengoal.SevenGoalManager.SevenGoalTaskType;

import pomelo.area.EquipHandler.EquipRefineLegendRequest;
import pomelo.area.EquipHandler.EquipRefineLegendResponse;

/**
 * 精炼装备传奇属性-开光
 * 
 * @author Yangzz
 *
 */
@GClientEvent("area.equipHandler.equipRefineLegendRequest")
public class EquipRefineLegendHandler extends PomeloRequest {

	public PomeloResponse request() throws Exception {

		WNPlayer player = (WNPlayer) pak.getPlayer();

		// 功能未开启...
		if (!player.functionOpenManager.isOpen(Const.FunctionType.REFINE.getValue())) {
			return new ErrorResponse(LangService.getValue("FUNC_SET_PLAYED_NOT_OPEN"));
		}

		EquipRefineLegendRequest req = EquipRefineLegendRequest.parseFrom(pak.getRemaingBytes());
		final String equipId = req.getEquipId();
		final EquipManager equipManager = player.equipManager;

		boolean isDressed = true;
		// 装备不存在
		NormalEquip equip = null;
		int gridIndex = equipManager.getEquipmentById(equipId);
		if (gridIndex > 0) {
			equip = equipManager.getEquipment(gridIndex);
		}

		if (equip == null) {
			isDressed = false;
			NormalItem item = player.bag.findItemById(equipId);
			if (item != null) {
				equip = (NormalEquip) item;
				gridIndex = player.bag.findPosById(equipId);
			}
		}

		if (equip == null) {
			return new ErrorResponse(LangService.getValue("BAG_STACKINDEX_ILLEGALITY"));
		}

		// 只有橙色以上才可以开光...
		if (equip.prop.qcolor <= Const.ItemQuality.PURPLE.getValue()) {
			return new ErrorResponse(LangService.getValue("EQUIP_REFINE_LEGEND_PURPLE"));
		}

		// 验证消耗
		KaiGuangExt rebornCO = GameData.KaiGuangs.get(equip.prop.levelReq);
		// 金币
		if (player.player.gold < rebornCO.costGold) {
			return new ErrorResponse(LangService.getValue("NOT_ENOUGH_GOLD_LEARN"));
		}
		// 材料1/材料2
		for (String mateCode : rebornCO.materials.keySet()) {
			int mateCount = rebornCO.materials.get(mateCode);
			if (player.bag.findItemNumByCode(mateCode) < mateCount) {
				return new ErrorResponse(LangService.getValue("ITEM_NOT_ENOUGH"));
			}
		}

		// 扣除金币
		player.moneyManager.costGold(rebornCO.costGold, GOODS_CHANGE_TYPE.Refine);
		// 扣除材料
		for (String mateCode : rebornCO.materials.keySet()) {
			int mateCount = rebornCO.materials.get(mateCode);
			player.bag.discardItem(mateCode, mateCount, GOODS_CHANGE_TYPE.Refine);
		}

		// 精炼
		equipManager.refineLegend(equip, gridIndex, isDressed);

		player.sevenGoalManager.processGoal(SevenGoalTaskType.EQUIP_REFINE_COUNT);
		
		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				EquipRefineLegendResponse.Builder res = EquipRefineLegendResponse.newBuilder();
				res.setS2CCode(OK);
				body.writeBytes(res.build().toByteArray());
			}
		};
	}
}