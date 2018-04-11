package com.wanniu.game.request.equip;

import java.io.IOException;
import java.util.List;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.bag.WNBag;
import com.wanniu.game.bag.WNBag.SimpleItemInfo;
import com.wanniu.game.equip.EquipManager;
import com.wanniu.game.equip.EquipUtil;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.poes.PlayerBasePO.EquipStrengthPos;

import pomelo.area.EquipHandler.UnFillGemRequest;
import pomelo.area.EquipHandler.UnFillGemResponse;

/**
 * 卸下指定孔的宝石
 * 
 * @author Yangzz
 *
 */
@GClientEvent("area.equipHandler.unFillGemRequest")
public class UnFillGemHandler extends PomeloRequest {

	public PomeloResponse request() throws Exception {

		WNPlayer player = (WNPlayer) pak.getPlayer();
		WNBag wnBag = player.getWnBag();
		EquipManager wnEquip = player.equipManager;

		UnFillGemRequest req = UnFillGemRequest.parseFrom(pak.getRemaingBytes());
		int pos = req.getC2SPos();
		int index = req.getC2SIndex();

		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				UnFillGemResponse.Builder res = UnFillGemResponse.newBuilder();
				EquipStrengthPos posInfo = wnEquip.strengthPos.get(pos);
				if(posInfo == null){
			    	res.setS2CCode(FAIL);
			    	res.setS2CMsg(LangService.getValue("EQUIP_NOT_EXIST"));
					body.writeBytes(res.build().toByteArray());
			        return;
			    }

				List<SimpleItemInfo> gemList = EquipUtil.getGemList(posInfo, index);
			    if(gemList.size() == 0) {
			    	res.setS2CCode(FAIL);
			    	res.setS2CMsg(LangService.getValue("EQUIP_UNFILL_ERROR"));
					body.writeBytes(res.build().toByteArray());
			        return;
			    }

			    if(!wnBag.testAddCodeItems(gemList, null, false)){
			    	res.setS2CCode(FAIL);
			    	res.setS2CMsg(LangService.getValue("BAG_NOT_ENOUGH_POS"));
					body.writeBytes(res.build().toByteArray());
			        return;
			    }

			    if(wnEquip.unfillGem(pos, index)){
			    	res.setS2CCode(OK);
			    	body.writeBytes(res.build().toByteArray());
			        return;
			    }
			    else{
			    	res.setS2CCode(FAIL);
			    	res.setS2CMsg(LangService.getValue("EQUIP_UNFILL_ERROR"));
					body.writeBytes(res.build().toByteArray());
			        return;
			    }
			    
			}
		};
	}
}