package com.wanniu.game.request.player;

import java.io.IOException;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.data.ext.UpLevelExpExt;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.PlayerHandler.GetClassEventConditionResponse;

@GClientEvent("area.playerHandler.getClassEventConditionRequest")
public class GetClassEventConditionHandler extends PomeloRequest {

	@Override
	public PomeloResponse request() throws Exception {
		return new PomeloResponse() {

			@Override
			protected void write() throws IOException {
				WNPlayer player = (WNPlayer) pak.getPlayer();
				
				GetClassEventConditionResponse.Builder res = GetClassEventConditionResponse.newBuilder();
				res.setS2CCode(OK);
				UpLevelExpExt prop_next = player.baseDataManager.getNextUpLevelExp();
				String flag = player.baseDataManager.checkClassEvent(prop_next);
				if(flag==null)
					res.setS2CFlag(1);
				else{
					res.setS2CFlag(0);
				}
				body.writeBytes(res.build().toByteArray());
			}
			
		};
	}

}
