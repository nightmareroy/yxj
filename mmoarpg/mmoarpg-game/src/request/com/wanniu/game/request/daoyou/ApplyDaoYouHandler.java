package com.wanniu.game.request.daoyou;

import java.io.IOException;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.common.Const;
import com.wanniu.game.common.msg.ErrorResponse;
import com.wanniu.game.daoyou.DaoYouService;
import com.wanniu.game.player.WNPlayer;

import pomelo.daoyou.DaoYouHandler.DaoYouResponse;

/**
 * 请求道友
 * 
 * @author wanghaitao
 *
 */
@GClientEvent("daoyou.daoYouHandler.daoYouRequest")
public class ApplyDaoYouHandler extends PomeloRequest {

	@Override
	public PomeloResponse request() throws Exception {
		WNPlayer player = (WNPlayer) pak.getPlayer();
		if (!player.functionOpenManager.isOpen(Const.FunctionType.DaoYou.getValue())) {
			return new ErrorResponse(LangService.getValue("DAO_YOU_NOT_OPEN"));
		}

		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				DaoYouResponse.Builder res = DaoYouResponse.newBuilder();

				String playerId = player.getId();
				DaoYouService.getInstance().applyDaoYouList(playerId, res);
				body.writeBytes(res.build().toByteArray());
			}
		};
	}

}
