package com.wanniu.game.request.rich;

import java.io.IOException;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.common.msg.ErrorResponse;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.rich.RichManager;

import pomelo.rich.RichHandler.DiceResponse;

/**
 * 大富翁投掷骰子
 * 
 * @author liyue
 */
@GClientEvent("rich.richHandler.diceRequest")
public class DiceHandler extends PomeloRequest {

	@Override
	public PomeloResponse request() throws Exception {
		WNPlayer player = (WNPlayer) pak.getPlayer();
//		DiceRequest req=DiceRequest.parseFrom(pak.getRemaingBytes());

		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				DiceResponse.Builder res=player.richManager.dice(player.getId());
				
				body.writeBytes(res.build().toByteArray());

			}
		};
	}

}
