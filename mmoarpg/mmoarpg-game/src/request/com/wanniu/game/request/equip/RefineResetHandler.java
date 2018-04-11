package com.wanniu.game.request.equip;

import java.io.IOException;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.equip.EquipManager;
import com.wanniu.game.equip.NormalEquip;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.EquipHandler.RefineResetRequest;
import pomelo.area.EquipHandler.RefineResetResponse;

/**
 * 精炼属性重置
 * 
 * @author Yangzz
 *
 */
@GClientEvent("area.equipHandler.refineResetRequest")
public class RefineResetHandler extends PomeloRequest {

	public PomeloResponse request() throws Exception {

		WNPlayer player = (WNPlayer) pak.getPlayer();
		EquipManager wnEquip = player.equipManager;

		RefineResetRequest req = RefineResetRequest.parseFrom(pak.getRemaingBytes());
		int equipPos = req.getC2SPos();
		int propIndex = req.getC2SPropIndex() - 1;
		
		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				RefineResetResponse.Builder res = RefineResetResponse.newBuilder();

			    if(propIndex < 0){
			    	res.setS2CCode(FAIL);
			    	res.setS2CMsg(LangService.getValue("PARAM_ERROR"));
					body.writeBytes(res.build().toByteArray());
			        return;
			    }
			    NormalEquip equip = wnEquip.getEquipment(equipPos);
			    if(equip == null){
			    	res.setS2CCode(FAIL);
			    	res.setS2CMsg(LangService.getValue("BAG_STACKINDEX_ILLEGALITY"));
					body.writeBytes(res.build().toByteArray());
			        return;
			    }
			    int result = -1;//equip.refineReset(propIndex, player);
			    if(result == 0){
			        res.setS2CCode(OK);
					body.writeBytes(res.build().toByteArray());
					return;
			    } else if (result == -1) {
			    	res.setS2CCode(FAIL);
			    	res.setS2CMsg(LangService.getValue("REFINE_RESET_PROP_NOT_EXIST"));
					body.writeBytes(res.build().toByteArray());
			        return;
			    } else if (result == -2) {
			    	res.setS2CCode(FAIL);
			    	res.setS2CMsg(LangService.getValue("REFINE_RESET_MATE_NOT_ENOUGH"));
					body.writeBytes(res.build().toByteArray());
			        return;
			    }
			    res.setS2CCode(FAIL);
		    	res.setS2CMsg(LangService.getValue("SOMETHING_ERR"));
				body.writeBytes(res.build().toByteArray());
		        return;
			}
		};
	}
}