package com.wanniu.game.request.farm;

import java.io.IOException;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.farm.FarmMgr;
import com.wanniu.game.player.WNPlayer;

import pomelo.farm.FarmHandler.CultivateFriendRequest;
import pomelo.farm.FarmHandler.CultivateFriendResponse;


@GClientEvent("farm.farmHandler.cultivateFriendRequest")
public class CultivateFriendHandler extends PomeloRequest {
	@Override
	public PomeloResponse request() throws Exception {
		WNPlayer player = (WNPlayer) pak.getPlayer();
		CultivateFriendRequest msg = CultivateFriendRequest.parseFrom(pak.getRemaingBytes());
		String friendId=msg.getFriendId();
		int blockId=msg.getBlockId();
		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				FarmMgr farmMgr = player.getFarmMgr();
				CultivateFriendResponse.Builder res = farmMgr.cultivateFriend(blockId, friendId);
				

				body.writeBytes(res.build().toByteArray());
			}
		};
	}

}
