package com.wanniu.game.request.mail;

import java.io.IOException;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.entity.GPlayer;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.common.Const;
import com.wanniu.game.common.Const.GOODS_CHANGE_TYPE;
import com.wanniu.game.mail.MailUtil;
import com.wanniu.game.mail.data.MailPlayerData;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.util.BlackWordUtil;

import pomelo.area.MailHandler.MailSendMailRequest;
import pomelo.area.MailHandler.MailSendMailResponse;

/**
 * 发送邮件
 * 
 * @author Tanglt
 *
 */
@GClientEvent("area.mailHandler.mailSendMailRequest")
public class MailSendMailHandler extends PomeloRequest {

	public PomeloResponse request() throws Exception {
		MailSendMailRequest req = MailSendMailRequest.parseFrom(pak.getRemaingBytes());
		// TODO request
		GPlayer player = pak.getPlayer();
		WNPlayer wPlayer = (WNPlayer) player;
		MailPlayerData mailData = new MailPlayerData();
		mailData.mailSender = wPlayer.getName();
		mailData.mailSenderId = wPlayer.getId();
		mailData.mailTitle = req.getMailTitle();
		mailData.mailText = req.getMailText();
		mailData.mailRead = req.getMailRead();
		mailData.mailIcon = wPlayer.getPlayer().pro;
		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				MailSendMailResponse.Builder res = MailSendMailResponse.newBuilder();
				// TODO response
				if (BlackWordUtil.isIncludeBlackString(mailData.mailTitle)) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("MAIL_TITLE_BLACK_STRING"));
					body.writeBytes(res.build().toByteArray());
					return;
				} else if (BlackWordUtil.isIncludeBlackString(mailData.mailText)) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("MAIL_TEXT_BLACK_STRING"));
					body.writeBytes(res.build().toByteArray());
					return;
				}
				if (mailData.mailText.length() > Const.MailSysParam.MAIL_MAX_WORD.getValue() + 10) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("MAIL_TEXT_TOO_LONG"));
					body.writeBytes(res.build().toByteArray());
					return;
				} else if (wPlayer.friendManager.isInBlackList(req.getToPlayerId())) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("MAIL_PLAYER_IN_BLACK_LIST"));
					body.writeBytes(res.build().toByteArray());
					return;
				} else {
					boolean result = MailUtil.getInstance().sendMailToOnePlayer(req.getToPlayerId(), mailData, GOODS_CHANGE_TYPE.mail);
					if (result) {
						res.setS2CCode(OK);
					} else {
						res.setS2CCode(FAIL);
						res.setS2CMsg(LangService.getValue("SOMETHING_ERR"));
					}
				}
				body.writeBytes(res.build().toByteArray());
			}
		};
	}
}
