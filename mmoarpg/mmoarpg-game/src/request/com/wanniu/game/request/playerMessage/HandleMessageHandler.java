package com.wanniu.game.request.playerMessage;

import java.io.IOException;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.common.Const;
import com.wanniu.game.common.msg.ErrorResponse;
import com.wanniu.game.daoyou.DaoYouService;
import com.wanniu.game.message.MessageData;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.MessageHandler.HandleMessageRequest;
import pomelo.area.MessageHandler.HandleMessageResponse;

@GClientEvent("area.messageHandler.handleMessageRequest")
public class HandleMessageHandler extends PomeloRequest {

	@Override
	public PomeloResponse request() throws Exception {
		WNPlayer player = (WNPlayer) pak.getPlayer();
		HandleMessageRequest req = HandleMessageRequest.parseFrom(pak.getRemaingBytes());
		String id = req.getC2SId();
		int operate = req.getC2SOperate();
		if (id == null || operate != Const.MESSAGE_OPERATE.TYPE_ACCEPT.getValue() && operate != Const.MESSAGE_OPERATE.TYPE_REFUSE.getValue()) {
			return new ErrorResponse(LangService.getValue("DATA_ERR"));
		}

		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				HandleMessageResponse.Builder res = HandleMessageResponse.newBuilder();
				res.setS2CCode(OK);
				int type = req.getC2SType();
				if (type == Const.MESSAGE_TYPE.team_invite.getValue() || type == Const.MESSAGE_TYPE.team_apply.getValue()) {
					boolean isValid = player.getTeamManager().onMessage(type, operate, id);
					res.setS2CIsClear(isValid ? 0 : 1);
				} else if (type == Const.MESSAGE_TYPE.daoyou_invite.getValue()) {
					boolean isValid = DaoYouService.getInstance().onMessage(player, operate, id);
					res.setS2CIsClear(isValid ? 0 : 1);
				} else {
					MessageData message = player.messageManager.getReceivedMessage(type, id);
					if (message != null) {
						boolean isValid = true;
						if (message.isPastDue()) {
							player.messageManager.deleteReceivedMessage(type, id);
						} else {
							isValid = player.onMessage(operate, message);
						}
						res.setS2CIsClear(isValid ? 0 : 1);
					} else {
						res.setS2CCode(FAIL);
						res.setS2CMsg(LangService.getValue("HAVE_THIS_MSG"));
					}
				}
				body.writeBytes(res.build().toByteArray());
			}
		};
	}

}
