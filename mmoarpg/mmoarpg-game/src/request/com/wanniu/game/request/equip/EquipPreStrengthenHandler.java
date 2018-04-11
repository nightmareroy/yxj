package com.wanniu.game.request.equip;

import java.io.IOException;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.equip.EquipUtil;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.poes.PlayerBasePO.EquipStrengthPos;

import pomelo.area.EquipHandler.EquipPreStrengthenRequest;
import pomelo.area.EquipHandler.EquipPreStrengthenResponse;

/**
 * 获取强化信息
 * 
 * @author Yangzz
 *
 */
@GClientEvent("area.equipHandler.equipPreStrengthenRequest")
public class EquipPreStrengthenHandler extends PomeloRequest {

	public PomeloResponse request() throws Exception {

		WNPlayer player = (WNPlayer) pak.getPlayer();

		EquipPreStrengthenRequest req = EquipPreStrengthenRequest.parseFrom(pak.getRemaingBytes());
		int pos = req.getC2SPos();

		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				EquipPreStrengthenResponse.Builder res = EquipPreStrengthenResponse.newBuilder();

				EquipStrengthPos strengthPos = player.equipManager.strengthPos.get(pos);
			    if(strengthPos == null){
			    	res.setS2CCode(FAIL);
			    	res.setS2CMsg(LangService.getValue("EQUIP_POS_NOT_EXIST"));
					body.writeBytes(res.build().toByteArray());
			        return;
			    }
			    res.setS2CStrengthenData(EquipUtil.getStrengthInfo(player, pos));

				res.setS2CCode(OK);
				body.writeBytes(res.build().toByteArray());
			}
		};
	}
}