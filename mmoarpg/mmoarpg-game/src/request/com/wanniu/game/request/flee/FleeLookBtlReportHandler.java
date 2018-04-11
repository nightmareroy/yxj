package com.wanniu.game.request.flee;

import java.io.IOException;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.FleeHandler.FleeLookBtlReportResponse;

/**
 * 大逃杀获取战报
 * 
 * @author lxm
 *
 */
@GClientEvent("area.fleeHandler.fleeLookBtlReportRequest")
public class FleeLookBtlReportHandler extends PomeloRequest {

	@Override
	public PomeloResponse request() throws Exception {
		WNPlayer player = (WNPlayer) pak.getPlayer();
		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				FleeLookBtlReportResponse res = player.fleeManager.getFleeLookBtlReportResponse();
				body.writeBytes(res.toByteArray());
			}
		};
	}
}