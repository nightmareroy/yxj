package com.wanniu.game.request.five2five;

import java.io.IOException;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.request.solo.SoloRequestFilter;

import pomelo.five2five.Five2FiveHandler.Five2FiveLookMatchResultResponse;
import pomelo.five2five.Five2FiveHandler.Five2FiveShardMatchResultRequest;

/**
 * @author wanghaitao
 *
 */
@GClientEvent("five2five.five2FiveHandler.five2FiveLookMatchResultRequest")
public class LookMatchResultHandler extends SoloRequestFilter {

	public PomeloResponse request(WNPlayer player) throws Exception {
		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				Five2FiveShardMatchResultRequest req = Five2FiveShardMatchResultRequest.parseFrom(pak.getRemaingBytes());
				String instanceId = req.getInstanceId();
				Five2FiveLookMatchResultResponse.Builder res = Five2FiveLookMatchResultResponse.newBuilder();
				player.five2FiveManager.lookMatchResult(instanceId, res);
				body.writeBytes(res.build().toByteArray());
			}
		};
	}
}
