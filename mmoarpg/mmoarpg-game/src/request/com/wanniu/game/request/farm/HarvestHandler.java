package com.wanniu.game.request.farm;

import java.io.IOException;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.farm.FarmMgr;
import com.wanniu.game.player.WNPlayer;

import pomelo.farm.FarmHandler.HarvestRequest;
import pomelo.farm.FarmHandler.HarvestResponse;

@GClientEvent("farm.farmHandler.harvestRequest")
public class HarvestHandler extends PomeloRequest {
	@Override
	public PomeloResponse request() throws Exception {
		WNPlayer player = (WNPlayer) pak.getPlayer();
		HarvestRequest msg = HarvestRequest.parseFrom(pak.getRemaingBytes());
		int blockId=msg.getBlockId();
		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				FarmMgr farmMgr = player.getFarmMgr();
				HarvestResponse.Builder res = farmMgr.harvest(blockId);
				
				body.writeBytes(res.build().toByteArray());
			}
		};
	}

}