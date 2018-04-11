package com.wanniu.game.request.equip;

import java.io.IOException;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.bag.WNBag;
import com.wanniu.game.equip.EquipManager;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.EquipHandler.UnFillAllGemRequest;
import pomelo.area.EquipHandler.UnFillAllGemResponse;

/**
 * 卸下所有宝石
 * 
 * @author Yangzz
 *
 */
@Deprecated
@GClientEvent("area.equipHandler.unFillAllGemRequest")
public class UnFillAllGemHandler extends PomeloRequest {

	public PomeloResponse request() throws Exception {

		WNPlayer player = (WNPlayer) pak.getPlayer();
		WNBag wnBag = player.getWnBag();
		EquipManager wnEquip = player.equipManager;

		UnFillAllGemRequest req = UnFillAllGemRequest.parseFrom(pak.getRemaingBytes());
		int pos = req.getC2SPos();

		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				UnFillAllGemResponse.Builder res = UnFillAllGemResponse.newBuilder();
	
			}
		};
	}
}