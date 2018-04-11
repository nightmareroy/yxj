package com.wanniu.game.request.equip;

import java.io.IOException;
import java.util.Map;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.core.util.StringUtil;
import com.wanniu.game.common.Const;
import com.wanniu.game.equip.EquipManager;
import com.wanniu.game.equip.NormalEquip;
import com.wanniu.game.player.PlayerUtil;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.EquipHandler.RefineOneKeyRequest;
import pomelo.area.EquipHandler.RefineOneKeyResponse;

/**
 * 一键精炼
 * 
 * @author Yangzz
 *
 */
// FIXME 这再使用的功能，考虑是否需要删除掉...
@GClientEvent("area.equipHandler.refineOneKeyRequest")
public class RefineOneKeyHandler extends PomeloRequest {

	public PomeloResponse request() throws Exception {

		WNPlayer player = (WNPlayer) pak.getPlayer();
		EquipManager wnEquip = player.equipManager;

		RefineOneKeyRequest req = RefineOneKeyRequest.parseFrom(pak.getRemaingBytes());
		int equipPos = req.getC2SPos();
		String itemCode = req.getC2SItemCode();

		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				RefineOneKeyResponse.Builder res = RefineOneKeyResponse.newBuilder();

			    if(StringUtil.isEmpty(itemCode)) {
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
			    Map<String, Integer> resData = null;//equip.refineOneKey(itemCode, player);
			    int result = resData.get("result");
			    if(result == 0){
			        if(resData.get("successNum") > 0){
			        	res.setS2CCode(OK);
						body.writeBytes(res.build().toByteArray());
			            return;
			        }else{
			            PlayerUtil.sendSysMessageToPlayer(LangService.getValue("REFINE_FAILED_TIPS"), player.getId(), Const.TipsType.NORMAL);
			            res.setS2CCode(FAIL);
				    	res.setS2CMsg("");
						body.writeBytes(res.build().toByteArray());
				        return;
			        }
			    } else if (result == -1) {
			    	res.setS2CCode(FAIL);
			    	res.setS2CMsg(LangService.getValue("EQUIP_CAN_NOT_REFINE"));
					body.writeBytes(res.build().toByteArray());
			        return;
			    } else if (result == -2) {
			    	res.setS2CCode(FAIL);
			    	res.setS2CMsg(LangService.getValue("REFINE_MATE_ERROR"));
					body.writeBytes(res.build().toByteArray());
			        return;
			    } else if (result == 1) {
			    	res.setS2CCode(FAIL);
			    	res.setS2CMsg(LangService.getValue("EQUIPMENT_REFINE_HIGHEST"));
					body.writeBytes(res.build().toByteArray());
			        return;
			    } else if (result == 2) {
			    	res.setS2CCode(FAIL);
			    	res.setS2CMsg(LangService.getValue("REFINE_MATE_NOT_ENOUGH"));
					body.writeBytes(res.build().toByteArray());
			        return;
			    } else if (result == 3) {
			    	res.setS2CCode(FAIL);
			    	res.setS2CMsg(LangService.getValue("CONFIG_ERR"));
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