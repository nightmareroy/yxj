package com.wanniu.game.request.mail;

import java.io.IOException;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.entity.GPlayer;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.mail.MailManager.ERR_CODE;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.MailHandler.MailGetAttachmentRequest;
import pomelo.area.MailHandler.MailGetAttachmentResponse;

/**
 * 获取邮件附件
 * @author Tanglt
 *
 */
@GClientEvent("area.mailHandler.mailGetAttachmentRequest")
public class MailGetAttachmentHandler extends PomeloRequest {

	public PomeloResponse request() throws Exception {
		MailGetAttachmentRequest req = MailGetAttachmentRequest.parseFrom(pak.getRemaingBytes());
		//  request
		GPlayer player = pak.getPlayer();
		WNPlayer wPlayer = (WNPlayer)player;
		int code = wPlayer.mailManager.mailGetAttachment(req.getC2SId(),true);
		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				MailGetAttachmentResponse.Builder res = MailGetAttachmentResponse.newBuilder();
				// response
				if(code == ERR_CODE.ERR_CODE_OK.getValue()){
					res.setS2CCode(OK);
				}
				else if(code == ERR_CODE.ERR_CODE_BAG_FULL.getValue()){
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("BAG_NOT_ENOUGH_POS"));
				}
				else if(code == ERR_CODE.ERR_CODE_NO_ATTACH.getValue()){
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("MAIL_NO_ATTACHMENT"));
				}
				else if(code == ERR_CODE.ERR_CODE_NO_SUCH_MAIL.getValue()){
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("MAIL_NO_SUCH_MAIL"));
				}
				else{
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("SOMETHING_ERR"));
				}
				body.writeBytes(res.build().toByteArray());
			}
		};
	}
}
