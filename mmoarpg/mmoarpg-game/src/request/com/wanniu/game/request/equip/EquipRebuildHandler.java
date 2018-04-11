package com.wanniu.game.request.equip;

import java.io.IOException;
import java.util.List;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.common.Const;
import com.wanniu.game.common.Const.GOODS_CHANGE_TYPE;
import com.wanniu.game.data.ext.ReBuildExt;
import com.wanniu.game.equip.EquipManager;
import com.wanniu.game.equip.NormalEquip;
import com.wanniu.game.item.ItemConfig;
import com.wanniu.game.item.NormalItem;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.sevengoal.SevenGoalManager.SevenGoalTaskType;

import pomelo.area.EquipHandler.EquipRebuildRequest;
import pomelo.area.EquipHandler.EquipRebuildResponse;

/**
 * 重铸装备
 * 
 * @author Yangzz
 *
 */
@GClientEvent("area.equipHandler.equipRebuildRequest")
public class EquipRebuildHandler extends PomeloRequest {

	public PomeloResponse request() throws Exception {

		WNPlayer player = (WNPlayer) pak.getPlayer();
		EquipManager equipManager = player.equipManager;

		EquipRebuildRequest req = EquipRebuildRequest.parseFrom(pak.getRemaingBytes());
		String equipId = req.getEquipId();
		List<Integer> lockedAttIdList=req.getLockedAttIdList();

		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				EquipRebuildResponse.Builder res = EquipRebuildResponse.newBuilder();
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
				
				//check lockids isn't leagal
				if(!equipManager.checkRebuildLocks(lockedAttIdList, equip.speData.extAtts.size())){
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("EQUIP_LOCKED_ID_ERR"));
					body.writeBytes(res.build().toByteArray());
					return;
				}
				
				
				
				// 验证消耗
				ReBuildExt rebornCO = ItemConfig.getInstance().rebuildMap.get(equip.prop.levelReq).get(lockedAttIdList.size());
				
				// 金币
				if (player.player.gold < rebornCO.costGold) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("NOT_ENOUGH_GOLD_LEARN"));
					body.writeBytes(res.build().toByteArray());
					return;
				}
				// 材料1/材料2/材料3
				for (String mateCode : rebornCO.materials.keySet()) {
					int mateCount = rebornCO.materials.get(mateCode);
					//增加锁定消耗			
					if (player.bag.findItemNumByCode(mateCode) < mateCount) {
						res.setS2CCode(FAIL);
						res.setS2CMsg(LangService.getValue("ITEM_NOT_ENOUGH"));
						body.writeBytes(res.build().toByteArray());
						return;
					}
				}
				
				// 扣除金币
				player.moneyManager.costGold(rebornCO.costGold, GOODS_CHANGE_TYPE.Rebuild);
				
				// 扣除材料
				for (String mateCode : rebornCO.materials.keySet()) {
					int mateCount = rebornCO.materials.get(mateCode);
					//增加锁定消耗				
					if(!player.bag.discardItem(mateCode, mateCount, GOODS_CHANGE_TYPE.Rebuild)){
						res.setS2CCode(FAIL);
						res.setS2CMsg(LangService.getValue("ITEM_NOT_ENOUGH"));
						body.writeBytes(res.build().toByteArray());
						return;
					}
				}

				// 重铸
				equipManager.rebuild(equip, gridIndex, isDressed,lockedAttIdList);
				
				player.sevenGoalManager.processGoal(SevenGoalTaskType.EQUIP_REBUILD_COUNT);
				
				res.setS2CCode(OK);
				body.writeBytes(res.build().toByteArray());
				return;
			}
		};
	}
}