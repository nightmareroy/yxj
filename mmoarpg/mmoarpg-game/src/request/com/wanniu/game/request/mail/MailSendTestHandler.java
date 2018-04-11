package com.wanniu.game.request.mail;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.entity.GPlayer;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.common.Const.GOODS_CHANGE_TYPE;
import com.wanniu.game.mail.MailUtil;
import com.wanniu.game.mail.data.MailPlayerData;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.MailHandler.MailSendTestNotify;

/**
 * 发送邮件同步
 * 
 * @author Tanglt
 *
 */
@GClientEvent("area.mailHandler.mailSendTestNotify")
public class MailSendTestHandler extends PomeloRequest {

	public PomeloResponse request() throws Exception {
		MailSendTestNotify req = MailSendTestNotify.parseFrom(pak.getRemaingBytes());
		// TODO request
		GPlayer player = pak.getPlayer();
		WNPlayer wPlayer = (WNPlayer) player;
		MailPlayerData mailData = new MailPlayerData();
		mailData.mailId = req.getC2SMailId();
		mailData.mailSender = wPlayer.getName();
		mailData.mailSenderId = wPlayer.getId();
		mailData.tcCode = req.getC2STcCode();
		mailData.mailIcon = wPlayer.getPlayer().pro;
		MailUtil.getInstance().sendMailToOnePlayer(player.getId(), mailData, GOODS_CHANGE_TYPE.mail);
		return null;
	}
}
