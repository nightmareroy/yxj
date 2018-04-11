package com.wanniu.game.request.mail;

import java.io.IOException;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.entity.GPlayer;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.MailHandler.MailDeleteRequest;
import pomelo.area.MailHandler.MailDeleteResponse;

/**
 * 删除邮件
 * @author Tanglt
 *
 */
@GClientEvent("area.mailHandler.mailDeleteRequest")
public class MailDeleteHandler extends PomeloRequest {

	public PomeloResponse request() throws Exception {
		MailDeleteRequest req = MailDeleteRequest.parseFrom(pak.getRemaingBytes());
		// request
		GPlayer gPlayer = pak.getPlayer();
		String id = req.getC2SId();
		WNPlayer wPlayer = (WNPlayer)gPlayer;		
		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				MailDeleteResponse.Builder res = MailDeleteResponse.newBuilder();
				if(wPlayer.mailManager.mailDelete(id, false)){
					res.setS2CCode(OK);
				}
				else{
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("MAIL_DEL_NOT_READ_OR_NOT_GET_ATTACH"));
				}		
				body.writeBytes(res.build().toByteArray());
			}
		};
	}
}
