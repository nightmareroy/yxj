package com.wanniu.game.request.equip;

import java.io.IOException;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.common.Const;
import com.wanniu.game.common.Const.GOODS_CHANGE_TYPE;
import com.wanniu.game.data.GameData;
import com.wanniu.game.data.ext.SeniorReBuildExt;
import com.wanniu.game.equip.EquipManager;
import com.wanniu.game.equip.NormalEquip;
import com.wanniu.game.item.NormalItem;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.sevengoal.SevenGoalManager.SevenGoalTaskType;

import pomelo.area.EquipHandler.EquipSeniorRebuildRequest;
import pomelo.area.EquipHandler.EquipSeniorRebuildResponse;

/**
 * 高级重铸装备
 * 
 * @author Liyue
 *
 */
@GClientEvent("area.equipHandler.equipSeniorRebuildRequest")
public class EquipSeniorRebuildHandler extends PomeloRequest {

	public PomeloResponse request() throws Exception {

		WNPlayer player = (WNPlayer) pak.getPlayer();
		EquipManager equipManager = player.equipManager;

		EquipSeniorRebuildRequest req = EquipSeniorRebuildRequest.parseFrom(pak.getRemaingBytes());
		String equipId = req.getEquipId();

		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				EquipSeniorRebuildResponse.Builder res = EquipSeniorRebuildResponse.newBuilder();
				if (!player.functionOpenManager.isOpen(Const.FunctionType.Rebuild.getValue())) {
					res.setS2CCode(Const.CODE.FAIL);
					res.setS2CMsg(LangService.getValue("FUNC_SET_PLAYED_NOT_OPEN"));
					body.writeBytes(res.build().toByteArray());
					return;
				}
				
				
				boolean isDressed = true;
				NormalEquip equip = null;
				int gridIndex = equipManager.getEquipmentById(equipId);
				if(gridIndex > 0) {
					equip = equipManager.getEquipment(gridIndex);
				}
				
				if(equip == null) {
					isDressed = false;
					NormalItem item = player.bag.findItemById(equipId);
					if(item != null) {
						equip = (NormalEquip) item;
						gridIndex = player.bag.findPosById(equipId);
					}
				}
				
				if (equip == null) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("BAG_STACKINDEX_ILLEGALITY"));
					body.writeBytes(res.build().toByteArray());
					return;
				}
				// 验证消耗
				SeniorReBuildExt seniorRebuildExt = GameData.SeniorReBuilds.get(equip.prop.levelReq);
			
				
				// 金币
				if (player.player.gold < seniorRebuildExt.costGold) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("NOT_ENOUGH_GOLD_LEARN"));
					body.writeBytes(res.build().toByteArray());
					return;
				}
				// 材料1/材料2
				for (String mateCode : seniorRebuildExt.materials.keySet()) {
					int mateCount = seniorRebuildExt.materials.get(mateCode);
					
					if (player.bag.findItemNumByCode(mateCode) < mateCount) {
						res.setS2CCode(FAIL);
						res.setS2CMsg(LangService.getValue("ITEM_NOT_ENOUGH"));
						body.writeBytes(res.build().toByteArray());
						return;
					}
				}
				
				
				
				// 扣除金币
				player.moneyManager.costGold(seniorRebuildExt.costGold, GOODS_CHANGE_TYPE.SeniorRebuild);
				
				// 扣除材料
				for (String mateCode : seniorRebuildExt.materials.keySet()) {
					int mateCount = seniorRebuildExt.materials.get(mateCode);
					
					player.bag.discardItem(mateCode, mateCount, GOODS_CHANGE_TYPE.SeniorRebuild);
				}

				// 重铸
				equipManager.seniorRebuild(equip, gridIndex, isDressed);
				player.sevenGoalManager.processGoal(SevenGoalTaskType.EQUIP_REBUILD_COUNT);
				
				res.setS2CCode(OK);
				body.writeBytes(res.build().toByteArray());
				return;
			}
		};
	}
}