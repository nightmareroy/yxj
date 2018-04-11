package com.wanniu.game.request.leaderboard;

import java.io.IOException;
import java.util.List;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.LeaderBoardHandler.WorShipRequest;
import pomelo.area.LeaderBoardHandler.WorShipResponse;

/**
 * 世界等级膜拜
 */
@GClientEvent("area.leaderBoardHandler.worShipRequest")
public class WorShipHandler extends PomeloRequest {

	public PomeloResponse request() throws Exception {

		WNPlayer player = (WNPlayer) pak.getPlayer();

		WorShipRequest req = WorShipRequest.parseFrom(pak.getRemaingBytes());
		int _type = req.getC2SType();

		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				WorShipResponse.Builder res = WorShipResponse.newBuilder();

				WorshipRes result = player.leaderBoardManager.worShip(player,_type);
				if (result.result) {
					res.setS2CCode(OK);
					res.addAllS2CAwards(result.awards);
				} else {
					res.setS2CCode(FAIL);
					res.setS2CMsg(result.info);
				}
				
				body.writeBytes(res.build().toByteArray());

			}
		};
	}
	
	public static final class WorshipRes {
		public boolean result;
		public String info;
		public List<String> awards;
		
		public WorshipRes(boolean result, String info, List<String> awards) {
			this.result = result;
			this.info = info;
			this.awards = awards;
		}
	}
}