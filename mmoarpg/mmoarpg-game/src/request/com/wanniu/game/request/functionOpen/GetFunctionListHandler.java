package com.wanniu.game.request.functionOpen;

import java.io.IOException;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.FunctionOpenHandler.GetFunctionListRequest;
import pomelo.area.FunctionOpenHandler.GetFunctionListResponse;

/**
 * 请求功能列表
 * @author haog
 *
 */
@GClientEvent("area.functionOpenHandler.getFunctionListRequest")
public class GetFunctionListHandler extends PomeloRequest {

	public PomeloResponse request() throws Exception {
		GetFunctionListRequest req = GetFunctionListRequest.parseFrom(pak.getRemaingBytes());
		WNPlayer player = (WNPlayer) pak.getPlayer();
		
		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				GetFunctionListResponse.Builder res = GetFunctionListResponse.newBuilder();

				res.addAllS2CList(player.functionOpenManager.toJson4PayLoad());
				
				res.setS2CCode(OK);
				body.writeBytes(res.build().toByteArray());
			}
		};
	}
}
