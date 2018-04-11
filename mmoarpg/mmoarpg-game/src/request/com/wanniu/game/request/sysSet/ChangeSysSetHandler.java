package com.wanniu.game.request.sysSet;

import java.io.IOException;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.SysSetHandler.ChangeSysSetRequest;
import pomelo.area.SysSetHandler.ChangeSysSetResponse;
import pomelo.area.SysSetHandler.SetData;

@GClientEvent("area.sysSetHandler.changeSysSetRequest")
public class ChangeSysSetHandler extends PomeloRequest {

	@Override
	public PomeloResponse request() throws Exception {
		WNPlayer player = (WNPlayer) pak.getPlayer();
		if (player == null) {
			return new PomeloResponse() {
				@Override
				protected void write() throws IOException {
					ChangeSysSetResponse.Builder res = ChangeSysSetResponse.newBuilder();
					res.setS2CCode(FAIL);
					body.writeBytes(res.build().toByteArray());
				}
			};
		}
		ChangeSysSetRequest req = ChangeSysSetRequest.parseFrom(pak.getRemaingBytes());
		SetData setData = req.getC2SSetData();
		player.sysSetManager.changeSet(setData);
		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				ChangeSysSetResponse.Builder res = ChangeSysSetResponse.newBuilder();
				res.setS2CCode(OK);
				body.writeBytes(res.build().toByteArray());
			}
		};
	}

}
