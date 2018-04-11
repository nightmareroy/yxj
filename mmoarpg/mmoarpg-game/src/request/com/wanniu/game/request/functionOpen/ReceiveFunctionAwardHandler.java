package com.wanniu.game.request.functionOpen;

import java.io.IOException;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.FunctionOpenHandler.ReceiveFunctionAwardRequest;
import pomelo.area.FunctionOpenHandler.ReceiveFunctionAwardResponse;

/**
 * 领取功能奖励
 * @author Yangzz
 *
 */
@GClientEvent("area.functionOpenHandler.receiveFunctionAwardRequest")
public class ReceiveFunctionAwardHandler extends PomeloRequest {

	public PomeloResponse request() throws Exception {
		
		WNPlayer player = (WNPlayer) pak.getPlayer();
		ReceiveFunctionAwardRequest req = ReceiveFunctionAwardRequest.parseFrom(pak.getRemaingBytes());
		final int guideId = req.getGuideId();
		
		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				ReceiveFunctionAwardResponse.Builder res = ReceiveFunctionAwardResponse.newBuilder();

				String msg = player.functionOpenManager.receiveFunctionAward(guideId);
				
				if(msg != null) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(msg);
				} else {
					res.setS2CCode(OK);
				}
				
				body.writeBytes(res.build().toByteArray());
			}
		};
	}
}
