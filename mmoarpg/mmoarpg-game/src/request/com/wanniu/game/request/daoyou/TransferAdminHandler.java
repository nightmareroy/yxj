package com.wanniu.game.request.daoyou;

import java.io.IOException;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.core.logfs.Out;
import com.wanniu.game.daoyou.DaoYouService;
import com.wanniu.game.player.WNPlayer;

import pomelo.daoyou.DaoYouHandler.DaoYouKickTeamResponse;
import pomelo.daoyou.DaoYouHandler.DaoYouTransferAdminRequest;

/**
 * @author wanghaitao
 *
 */
@GClientEvent("daoyou.daoYouHandler.daoYouTransferAdminRequest")
public class TransferAdminHandler extends PomeloRequest {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.wanniu.core.game.protocol.PomeloRequest#request()
	 */
	@Override
	public PomeloResponse request() throws Exception {
		WNPlayer player = (WNPlayer) pak.getPlayer();

		DaoYouTransferAdminRequest req = DaoYouTransferAdminRequest.parseFrom(pak.getRemaingBytes());

		String newAdminPlayerId = req.getPlayerId();

		return new PomeloResponse() {

			@Override
			protected void write() throws IOException {
				DaoYouKickTeamResponse.Builder res = DaoYouKickTeamResponse.newBuilder();
				try {
					if (null == player) {
						res.setS2CCode(FAIL);
						res.setS2CMsg(LangService.getValue("SOMETHING_ERR"));
						body.writeBytes(res.build().toByteArray());
						return;
					}

					String msg = DaoYouService.getInstance().transferAdmin(player, newAdminPlayerId);
					if (!msg.equals("")) {
						res.setS2CCode(FAIL);
						res.setS2CMsg(msg);
						body.writeBytes(res.build().toByteArray());
						return;
					} else {
						res.setS2CCode(OK);
						body.writeBytes(res.build().toByteArray());
					}
				} catch (Exception err) {
					Out.error(err);
					res.setS2CCode(FAIL);
					body.writeBytes(res.build().toByteArray());
				}
			}
		};
	}
}
