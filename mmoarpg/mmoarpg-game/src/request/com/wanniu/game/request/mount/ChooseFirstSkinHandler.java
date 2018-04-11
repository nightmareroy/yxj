package com.wanniu.game.request.mount;

import java.io.IOException;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.MountHandler.ChooseFirstSkinRequest;
import pomelo.area.MountHandler.ChooseFirstSkinResponse;

@GClientEvent("area.mountHandler.chooseFirstSkinRequest")
public class ChooseFirstSkinHandler  extends PomeloRequest {
	public PomeloResponse request() throws Exception {
		ChooseFirstSkinRequest req = ChooseFirstSkinRequest.parseFrom(pak.getRemaingBytes());
		
		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				WNPlayer player = (WNPlayer) pak.getPlayer();
				int skinId = req.getC2SSkinId();
				ChooseFirstSkinResponse.Builder res = ChooseFirstSkinResponse.newBuilder();
				// logic
				
				boolean result = player.mountManager.chooseFirstSkin(skinId);
			    if(!result){
			    	res.setS2CCode(FAIL);
			    	res.setS2CMsg(LangService.getValue("SOMETHING_ERR"));
			    }
			    else{
			    	res.setS2CCode(OK);
			    }
				body.writeBytes(res.build().toByteArray());
			}
		};
	}
}
