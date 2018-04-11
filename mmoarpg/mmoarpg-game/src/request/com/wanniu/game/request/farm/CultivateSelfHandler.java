package com.wanniu.game.request.farm;

import java.io.IOException;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.farm.FarmMgr;
import com.wanniu.game.player.WNPlayer;

import pomelo.farm.FarmHandler.CultivateSelfRequest;
import pomelo.farm.FarmHandler.CultivateSelfResponse;


@GClientEvent("farm.farmHandler.cultivateSelfRequest")
public class CultivateSelfHandler extends PomeloRequest {
	@Override
	public PomeloResponse request() throws Exception {
		WNPlayer player = (WNPlayer) pak.getPlayer();
		CultivateSelfRequest msg = CultivateSelfRequest.parseFrom(pak.getRemaingBytes());
		int blockId=msg.getBlockId();
		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				CultivateSelfResponse.Builder res = CultivateSelfResponse.newBuilder();
				FarmMgr farmMgr = player.getFarmMgr();
				
				boolean cultivateRes=farmMgr.cultivateSelf(blockId);
				if(!cultivateRes)
				{
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("FARM_CULTIVATE_FAIL"));
					body.writeBytes(res.build().toByteArray());
					return;
				}
				res.setS2CCode(OK);
				body.writeBytes(res.build().toByteArray());
			}
		};
	}

}
