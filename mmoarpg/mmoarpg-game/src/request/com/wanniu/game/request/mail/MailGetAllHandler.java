package com.wanniu.game.request.mail;

import java.io.IOException;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.entity.GPlayer;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.common.Const;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.MailHandler.Mail;
import pomelo.area.MailHandler.MailGetAllResponse;

/**
 * 获取邮件
 * @author Tanglt
 *
 */
@GClientEvent("area.mailHandler.mailGetAllRequest")
public class MailGetAllHandler extends PomeloRequest {

	public PomeloResponse request() throws Exception {
		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				MailGetAllResponse.Builder res = MailGetAllResponse.newBuilder();
				// response
				res.setS2CCode(OK);
				GPlayer gPlayer = pak.getPlayer();
				WNPlayer wPlayer = (WNPlayer)gPlayer;
				Mail[] mails = wPlayer.mailManager.getAllMails(wPlayer);
				for(int i = 0;i<mails.length;i++){
					res.addMails(mails[i]);
				}
				res.setS2CMaxMailNum(Const.MailSysParam.MAIL_MAX_NUM.getValue());
				res.setS2CMaxWordNum(Const.MailSysParam.MAIL_MAX_WORD.getValue());
				body.writeBytes(res.build().toByteArray());
			}
		};
	}

}
