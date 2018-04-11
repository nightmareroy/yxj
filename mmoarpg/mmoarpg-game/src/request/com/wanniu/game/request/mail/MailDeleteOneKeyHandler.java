package com.wanniu.game.request.mail;

import java.io.IOException;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.entity.GPlayer;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.MailHandler.MailDeleteOneKeyResponse;

/**
 * 删除邮件ByKey
 * @author Tanglt
 *
 */
@GClientEvent("area.mailHandler.mailDeleteOneKeyRequest")
public class MailDeleteOneKeyHandler extends PomeloRequest {

	public PomeloResponse request() throws Exception {
		//MailDeleteOneKeyRequest req = MailDeleteOneKeyRequest.parseFrom(pak.getRemaingBytes());
		// request
		GPlayer player = pak.getPlayer();
		WNPlayer wPlayer = (WNPlayer)player;
		String[] ids = wPlayer.mailManager.mailDeleteOneKey();
		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				MailDeleteOneKeyResponse.Builder res = MailDeleteOneKeyResponse.newBuilder();
				// response
				res.setS2CCode(OK);
				for(int i = 0;i<ids.length;i++){
					res.addS2CIds(ids[i]);
				}
				body.writeBytes(res.build().toByteArray());
			}
		};
	}
}
