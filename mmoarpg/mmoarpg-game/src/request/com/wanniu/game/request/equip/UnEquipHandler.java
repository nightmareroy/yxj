package com.wanniu.game.request.equip;

import java.io.IOException;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.bag.WNBag;
import com.wanniu.game.common.msg.ErrorResponse;
import com.wanniu.game.equip.EquipManager;
import com.wanniu.game.item.NormalItem;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.EquipHandler.UnEquipRequest;
import pomelo.area.EquipHandler.UnEquipResponse;

/**
 * 卸下装备
 * 
 * @author Yangzz
 */
@GClientEvent("area.equipHandler.unEquipRequest")
public class UnEquipHandler extends PomeloRequest {

	public PomeloResponse request() throws Exception {

		WNPlayer player = (WNPlayer) pak.getPlayer();
		WNBag wnBag = player.getWnBag();
		EquipManager wnEquip = player.equipManager;

		UnEquipRequest req = UnEquipRequest.parseFrom(pak.getRemaingBytes());
		final int gridIndex = req.getC2SGridIndex();

		NormalItem equip = wnEquip.getEquipment(gridIndex);
		if (equip == null) {
			return new ErrorResponse(LangService.getValue("EQUIP_NOT_EQUIP"));
		}

		equip.itemDb.isNew = 0;
		if (!wnBag.testAddEntityItem(equip, true)) {
			return new ErrorResponse(LangService.getValue("BAG_NOT_ENOUGH_POS"));
		}

		wnEquip.unEquip(gridIndex);
		wnBag.addEntityItem(equip, null, null, true, false);

		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				UnEquipResponse.Builder res = UnEquipResponse.newBuilder();
				res.setS2CCode(OK);
				body.writeBytes(res.build().toByteArray());
			}
		};
	}
}