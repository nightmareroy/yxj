package com.wanniu.game.request.daoyou;

import java.io.IOException;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.core.util.StringUtil;
import com.wanniu.game.daoyou.DaoYouService;
import com.wanniu.game.player.WNPlayer;

import pomelo.daoyou.DaoYouHandler.DaoYouKickTeamRequest;
import pomelo.daoyou.DaoYouHandler.DaoYouKickTeamResponse;

/**
 * @author wanghaitao
 *
 */
@GClientEvent("daoyou.daoYouHandler.daoYouKickTeamRequest")
public class KickTeamHandler extends PomeloRequest {

	@Override
	public PomeloResponse request() throws Exception {
		WNPlayer player = (WNPlayer) pak.getPlayer();

		DaoYouKickTeamRequest req = DaoYouKickTeamRequest.parseFrom(pak.getRemaingBytes());

		String kickPlayerId = req.getPlayerId();

		return new PomeloResponse() {

			@Override
			protected void write() throws IOException {
				DaoYouKickTeamResponse.Builder res = DaoYouKickTeamResponse.newBuilder();

				String playerId = player.getId();
				String msg = DaoYouService.getInstance().kickDaoYou(playerId, player.getName(), kickPlayerId);
				if (StringUtil.isNotEmpty(msg)) {
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
