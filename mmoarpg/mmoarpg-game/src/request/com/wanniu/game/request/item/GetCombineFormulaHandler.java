package com.wanniu.game.request.item;

import java.io.IOException;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.bag.WNBag;
import com.wanniu.game.data.ext.CombineExt;
import com.wanniu.game.equip.EquipCraftConfig;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.ItemHandler.GetCombineFormulaRequest;
import pomelo.area.ItemHandler.GetCombineFormulaResponse;
import pomelo.item.ItemOuterClass.Combine;

/**
 * 请求道具合成公式
 * @author Yangzz
 *
 */
@GClientEvent("area.itemHandler.getCombineFormulaRequest")
public class GetCombineFormulaHandler extends PomeloRequest {

	public PomeloResponse request() throws Exception {
		
		WNPlayer player = (WNPlayer) pak.getPlayer();
		WNBag wnBag = player.getWnBag();
		
		GetCombineFormulaRequest req = GetCombineFormulaRequest.parseFrom(pak.getRemaingBytes());
		int destId = req.getC2SDestID();
		
		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				GetCombineFormulaResponse.Builder res = GetCombineFormulaResponse.newBuilder();
				
				CombineExt prop = EquipCraftConfig.getInstance().getCombineProp(destId);
			    if(prop == null){
			    	res.setS2CCode(FAIL);
			    	res.setS2CMsg(LangService.getValue("ITEM_NOT_COMBINE"));
					body.writeBytes(res.build().toByteArray());
					return;
			    }
			    Combine data = EquipCraftConfig.getInstance().getCombineFormula(prop);
			    
			    res.setS2CCode(OK);
			    res.setS2CData(data);
				body.writeBytes(res.build().toByteArray());
			}
		};
	}
}
