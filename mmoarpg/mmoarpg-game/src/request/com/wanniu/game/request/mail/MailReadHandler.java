package com.wanniu.game.request.mail;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.entity.GPlayer;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.MailHandler.MailReadNotify;

/**
 * 同步邮件读取状态
 * @author Tanglt
 *
 */
@GClientEvent("area.mailHandler.mailReadNotify")
public class MailReadHandler extends PomeloRequest {

	public PomeloResponse request() throws Exception {
		MailReadNotify req = MailReadNotify.parseFrom(pak.getRemaingBytes());
		//request
		GPlayer player = pak.getPlayer();
		WNPlayer wPlayer = (WNPlayer)player;
		if(req.getC2SIdCount() > 0 ){
			String[] ids = new String[1];
			ids[0] = req.getC2SId(0);
			wPlayer.mailManager.readMail(ids);
		}
		return null;
	}

}
