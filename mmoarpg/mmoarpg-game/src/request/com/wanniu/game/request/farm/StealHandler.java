package com.wanniu.game.request.farm;

import java.io.IOException;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.farm.FarmMgr;
import com.wanniu.game.player.WNPlayer;

import pomelo.farm.FarmHandler.StealRequest;
import pomelo.farm.FarmHandler.StealResponse;


@GClientEvent("farm.farmHandler.stealRequest")
public class StealHandler extends PomeloRequest {
	@Override
	public PomeloResponse request() throws Exception {
		WNPlayer player = (WNPlayer) pak.getPlayer();
		StealRequest msg = StealRequest.parseFrom(pak.getRemaingBytes());
		int blockId=msg.getBlockId();
		String friendId=msg.getFriendId();
		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				FarmMgr farmMgr = player.getFarmMgr();
				StealResponse.Builder res=farmMgr.steal(blockId, friendId);
				
				
				body.writeBytes(res.build().toByteArray());
			}
		};
	}

}