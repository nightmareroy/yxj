package com.wanniu.game.request.mail;

import java.io.IOException;
import java.util.List;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.entity.GPlayer;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.common.Const.MailAttachments;
import com.wanniu.game.mail.MailManager.ERR_CODE;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.MailHandler.MailGetAttachmentOneKeyResponse;

/**
 * 获取邮件附件ByKey
 * @author Tanglt
 *
 */
@GClientEvent("area.mailHandler.mailGetAttachmentOneKeyRequest")
public class MailGetAttachmentOneKeyHandler extends PomeloRequest {

	public PomeloResponse request() throws Exception {
		//MailGetAttachmentOneKeyRequest req = MailGetAttachmentOneKeyRequest.parseFrom(pak.getRemaingBytes());
		// request
		GPlayer player = pak.getPlayer();
		WNPlayer wPlayer = (WNPlayer)player;
		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				MailAttachments attachments = wPlayer.mailManager.mailGetAttachmentOneKey();
				MailGetAttachmentOneKeyResponse.Builder res = MailGetAttachmentOneKeyResponse.newBuilder();
				// response
				res.setS2CCode(OK);
				List<String> mailIds = attachments.mailIds;
				res.addAllS2CIds(mailIds);
//				for(int i = 0;i<mailIds.length;i++){
//					res.setS2CIds(i, mailIds[i]);
//				}
				if(attachments.code == ERR_CODE.ERR_CODE_BAG_FULL.getValue()){
					//这里如果设置fail，客户端的请求逻辑无法回调，所以商讨下来只设置一个msg
//					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("BAG_NOT_ENOUGH_POS"));
				}
				
				body.writeBytes(res.build().toByteArray());
			}
		};
	}
}
