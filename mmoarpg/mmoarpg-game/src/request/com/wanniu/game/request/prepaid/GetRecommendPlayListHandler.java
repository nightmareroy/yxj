package com.wanniu.game.request.prepaid;

import java.io.IOException;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;

import pomelo.area.VitalityHandler.GetRecommendPlayListResponse;
@GClientEvent("area.vitalityHandler.getRecommendPlayListRequest")
public class GetRecommendPlayListHandler extends PomeloRequest {

	@Override
	public PomeloResponse request() throws Exception {
//		GetRecommendPlayListRequest req = GetRecommendPlayListRequest.parseFrom(pak.getRemaingBytes());
		return new PomeloResponse() {
			protected void write() throws IOException {
				GetRecommendPlayListResponse.Builder res = GetRecommendPlayListResponse.newBuilder();
				
//				ArrayList<RecommendPlayInfo> data = player.vitalityManager.getRecommendPlayList();
//				res.addAllS2CRecommendPlayList(data);
				res.setS2CCode(OK);
				body.writeBytes(res.build().toByteArray());
			}
		};
	}

}
