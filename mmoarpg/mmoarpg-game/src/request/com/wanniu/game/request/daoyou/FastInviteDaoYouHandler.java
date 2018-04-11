package com.wanniu.game.request.daoyou;

import java.io.IOException;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.core.util.StringUtil;
import com.wanniu.game.daoyou.DaoYouService;
import com.wanniu.game.player.WNPlayer;

import pomelo.daoyou.DaoYouHandler.DaoYouFastInviteDaoYouResponse;

/**
 * @author wanghaitao
 *
 */
@GClientEvent("daoyou.daoYouHandler.daoYouFastInviteDaoYouRequest")
public class FastInviteDaoYouHandler extends PomeloRequest {

	@Override
	public PomeloResponse request() throws Exception {
		WNPlayer player = (WNPlayer) pak.getPlayer();

		return new PomeloResponse() {

			@Override
			protected void write() throws IOException {
				DaoYouFastInviteDaoYouResponse.Builder res = DaoYouFastInviteDaoYouResponse.newBuilder();

				String msg = DaoYouService.getInstance().fastInviteDaoYouCreateTeam(player);
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
