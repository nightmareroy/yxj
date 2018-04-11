package com.wanniu.game.request.daoyou;

import java.io.IOException;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.daoyou.DaoYouService;
import com.wanniu.game.player.WNPlayer;

import pomelo.daoyou.DaoYouHandler.DaoYouRebateResponse;

/**
 * @author wanghaitao
 *
 */
@GClientEvent("daoyou.daoYouHandler.daoYouRebateRequest")
public class RebateHandler extends PomeloRequest {

	@Override
	public PomeloResponse request() throws Exception {
		WNPlayer player = (WNPlayer) pak.getPlayer();

		return new PomeloResponse() {

			@Override
			protected void write() throws IOException {
				DaoYouRebateResponse.Builder res = DaoYouRebateResponse.newBuilder();

				DaoYouService.getInstance().getAllRebate(player, res);
				body.writeBytes(res.build().toByteArray());
			}
		};
	}
}
