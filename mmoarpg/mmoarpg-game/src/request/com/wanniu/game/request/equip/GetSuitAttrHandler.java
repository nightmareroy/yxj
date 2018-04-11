package com.wanniu.game.request.equip;

import java.io.IOException;
import java.util.List;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.equip.Suit;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.EquipHandler.GetSuitAttrResponse;
import pomelo.area.EquipHandler.SuitTypeAttr;

/**
 * 获取套装属性
 * @author Yangzz
 *
 */
@GClientEvent("area.equipHandler.getSuitAttrRequest")
public class GetSuitAttrHandler extends PomeloRequest {

	public PomeloResponse request() throws Exception {
		
		WNPlayer player = (WNPlayer) pak.getPlayer();
		
//		GetSuitAttrRequest req = GetSuitAttrRequest.parseFrom(pak.getRemaingBytes());
		
		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				GetSuitAttrResponse.Builder res = GetSuitAttrResponse.newBuilder();
				
			    List<SuitTypeAttr> list = Suit.getPlayerSuitInfo(player);
			    
			    res.setS2CCode(OK);
			    res.addAllS2CData(list);
			    
				body.writeBytes(res.build().toByteArray());
			}
		};
	}
}
