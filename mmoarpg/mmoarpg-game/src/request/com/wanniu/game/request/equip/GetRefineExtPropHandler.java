package com.wanniu.game.request.equip;

import java.io.IOException;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.equip.EquipManager;
import com.wanniu.game.equip.NormalEquip;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.EquipHandler.GetRefineExtPropRequest;
import pomelo.area.EquipHandler.GetRefineExtPropResponse;

/**
 * 获取装备可能会精炼出来的随机属性集合
 * @author Yangzz
 *
 */
@GClientEvent("area.equipHandler.getRefineExtPropRequest")
public class GetRefineExtPropHandler extends PomeloRequest {

	public PomeloResponse request() throws Exception {
		
		WNPlayer player = (WNPlayer) pak.getPlayer();
		EquipManager equipManager = player.equipManager;
		
		GetRefineExtPropRequest req = GetRefineExtPropRequest.parseFrom(pak.getRemaingBytes());
		String equipId = req.getEquipId();
		
		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				GetRefineExtPropResponse.Builder res = GetRefineExtPropResponse.newBuilder();

				int pos = equipManager.getEquipmentById(equipId);
				NormalEquip equip = equipManager.getEquipment(pos);
				if (equip == null) {
					equip = (NormalEquip) player.bag.findItemById(equipId);
				}

				res.addAllExtAtts(equipManager.getRefineExtProp(equip));
				
				res.setS2CCode(OK);
				body.writeBytes(res.build().toByteArray());
			}
		};
	}
}
