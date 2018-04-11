package com.wanniu.game.request.equip;

import java.io.IOException;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.equip.Suit;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.EquipHandler.GetSuitDetailRequest;
import pomelo.area.EquipHandler.GetSuitDetailResponse;

/**
 * 某种套装的详情
 * @author Yangzz
 *
 */
@GClientEvent("area.equipHandler.getSuitDetailRequest")
public class GetSuitDetailHandler extends PomeloRequest {

	public PomeloResponse request() throws Exception {
		
		WNPlayer player = (WNPlayer) pak.getPlayer();
		
		GetSuitDetailRequest req = GetSuitDetailRequest.parseFrom(pak.getRemaingBytes());
		int suitType = req.getC2SSuitType();
		
		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				GetSuitDetailResponse.Builder res = GetSuitDetailResponse.newBuilder();
				
			    
			    res.setS2CCode(OK);
			    res.addAllS2CData(Suit.getSuitTypeDetail(player.getPro(), suitType));
			    
				body.writeBytes(res.build().toByteArray());
			}
		};
	}
}
