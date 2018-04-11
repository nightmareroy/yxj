package com.wanniu.game.request.equip;

import java.io.IOException;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.equip.EquipManager;
import com.wanniu.game.equip.NormalEquip;
import com.wanniu.game.item.NormalItem;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.EquipHandler.SaveSeniorRebuildRequest;
import pomelo.area.EquipHandler.SaveSeniorRebuildResponse;

/**
 * 保存重铸装备
 * 
 * @author Yangzz
 *
 */
@GClientEvent("area.equipHandler.saveSeniorRebuildRequest")
public class SaveSeniorRebuildHandler extends PomeloRequest {

	public PomeloResponse request() throws Exception {

		WNPlayer player = (WNPlayer) pak.getPlayer();
		EquipManager equipManager = player.equipManager;

		SaveSeniorRebuildRequest req = SaveSeniorRebuildRequest.parseFrom(pak.getRemaingBytes());
		String equipId = req.getEquipId();

		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				SaveSeniorRebuildResponse.Builder res = SaveSeniorRebuildResponse.newBuilder();
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

				equipManager.saveSeniorRebuild(equip, gridIndex, isDressed);
				
				res.setS2CCode(OK);
				body.writeBytes(res.build().toByteArray());
				return;
			}
		};
	}
}