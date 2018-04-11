package com.wanniu.game.request.equip;

import java.io.IOException;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.bag.WNBag;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.EquipHandler.FillAllGemRequest;
import pomelo.area.EquipHandler.FillAllGemResponse;

/**
 * 一键镶嵌宝石
 * 
 * @author Yangzz
 *
 */
@Deprecated
@GClientEvent("area.equipHandler.fillAllGemRequest")
public class FillAllGemHandler extends PomeloRequest {

	public PomeloResponse request() throws Exception {

		WNPlayer player = (WNPlayer) pak.getPlayer();
		WNBag wnBag = player.getWnBag();

		FillAllGemRequest req = FillAllGemRequest.parseFrom(pak.getRemaingBytes());
		int pos = req.getC2SPos();

		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				FillAllGemResponse.Builder res = FillAllGemResponse.newBuilder();


			}
		};
	}
}