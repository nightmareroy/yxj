package com.wanniu.game.request.daoyou;

import java.io.IOException;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.core.util.StringUtil;
import com.wanniu.game.daoyou.DaoYouService;
import com.wanniu.game.player.WNPlayer;

import pomelo.daoyou.DaoYouHandler.DaoYouInviteDaoYouRequest;
import pomelo.daoyou.DaoYouHandler.DaoYouInviteDaoYouResponse;

/**
 * @author wanghaitao
 *
 */
@GClientEvent("daoyou.daoYouHandler.daoYouInviteDaoYouRequest")
public class InviteDaoYouHandler extends PomeloRequest {

	@Override
	public PomeloResponse request() throws Exception {
		WNPlayer player = (WNPlayer) pak.getPlayer();

		DaoYouInviteDaoYouRequest req = DaoYouInviteDaoYouRequest.parseFrom(pak.getRemaingBytes());

		String toPlayerId = req.getPlayerId();

		return new PomeloResponse() {

			@Override
			protected void write() throws IOException {
				DaoYouInviteDaoYouResponse.Builder res = DaoYouInviteDaoYouResponse.newBuilder();
				String msg = DaoYouService.getInstance().inviteDaoYou(player, toPlayerId, false);
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
