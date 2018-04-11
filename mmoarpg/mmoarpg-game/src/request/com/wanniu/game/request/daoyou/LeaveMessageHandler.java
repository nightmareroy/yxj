package com.wanniu.game.request.daoyou;

import java.io.IOException;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.core.util.StringUtil;
import com.wanniu.game.daoyou.DaoYouService;
import com.wanniu.game.player.WNPlayer;

import pomelo.daoyou.DaoYouHandler.DaoYouLeaveMessageRequest;
import pomelo.daoyou.DaoYouHandler.DaoYouLeaveMessageResponse;

/**
 * @author wanghaitao
 *
 */
@GClientEvent("daoyou.daoYouHandler.daoYouLeaveMessageRequest")
public class LeaveMessageHandler extends PomeloRequest {

	@Override
	public PomeloResponse request() throws Exception {
		WNPlayer player = (WNPlayer) pak.getPlayer();

		DaoYouLeaveMessageRequest req = DaoYouLeaveMessageRequest.parseFrom(pak.getRemaingBytes());

		String messsage = req.getMessage();
		return new PomeloResponse() {

			@Override
			protected void write() throws IOException {
				DaoYouLeaveMessageResponse.Builder res = DaoYouLeaveMessageResponse.newBuilder();
				String trimMessage = messsage.trim();
				String msg = DaoYouService.getInstance().leaveMessage(player, trimMessage);
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
