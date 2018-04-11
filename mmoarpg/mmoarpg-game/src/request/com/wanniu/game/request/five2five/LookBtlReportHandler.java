package com.wanniu.game.request.five2five;

import java.io.IOException;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.request.solo.SoloRequestFilter;

import pomelo.five2five.Five2FiveHandler.Five2FiveLookBtlReportResponse;

/**
 * @author wanghaitao
 *
 */
@GClientEvent("five2five.five2FiveHandler.five2FiveLookBtlReportRequest")
public class LookBtlReportHandler extends SoloRequestFilter {

	public PomeloResponse request(WNPlayer player) throws Exception {
		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				Five2FiveLookBtlReportResponse.Builder res = Five2FiveLookBtlReportResponse.newBuilder();
				player.five2FiveManager.lookBtlReport(res);
				body.writeBytes(res.build().toByteArray());
			}
		};
	}

}
