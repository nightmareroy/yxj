package com.wanniu.game.request.daoyou;

import java.io.IOException;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.core.util.StringUtil;
import com.wanniu.game.daoyou.DaoYouService;
import com.wanniu.game.player.WNPlayer;

import pomelo.daoyou.DaoYouHandler.DaoYouNoticeRequest;
import pomelo.daoyou.DaoYouHandler.DaoYouNoticeResponse;

/**
 * @author wanghaitao
 *
 */
@GClientEvent("daoyou.daoYouHandler.daoYouNoticeRequest")
public class NoticeHandler extends PomeloRequest {

	@Override
	public PomeloResponse request() throws Exception {
		WNPlayer player = (WNPlayer) pak.getPlayer();

		DaoYouNoticeRequest req = DaoYouNoticeRequest.parseFrom(pak.getRemaingBytes());

		String notice = req.getNotice();
		return new PomeloResponse() {

			@Override
			protected void write() throws IOException {
				DaoYouNoticeResponse.Builder res = DaoYouNoticeResponse.newBuilder();

				String trimNotice = notice.trim();
				String playerId = player.getId();
				String msg = DaoYouService.getInstance().editNotice(playerId, trimNotice);
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
