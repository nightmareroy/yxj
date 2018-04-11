package com.wanniu.game.request.guild;

import java.io.IOException;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.guild.GuildResult;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.GuildHandler.ChangeGuildNoticeRequest;
import pomelo.area.GuildHandler.ChangeGuildNoticeResponse;

@GClientEvent("area.guildHandler.changeGuildNoticeRequest")
public class ChangeGuildNoticeHandler extends PomeloRequest {

	public PomeloResponse request() throws Exception {
		WNPlayer player = (WNPlayer) pak.getPlayer();
		ChangeGuildNoticeRequest req = ChangeGuildNoticeRequest.parseFrom(pak.getRemaingBytes());
		String notice = req.getNotice();

		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				ChangeGuildNoticeResponse.Builder res = ChangeGuildNoticeResponse.newBuilder();
				if (null == player) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("SOMETHING_ERR"));
					body.writeBytes(res.build().toByteArray());
					return;
				}

				GuildResult resData = player.guildManager.changeGuildNotice(notice);
				int result = resData.result;
				if (result == 0) {
					res.setS2CCode(OK);
					res.setS2CNotice(resData.newNotice);
					body.writeBytes(res.build().toByteArray());
					return;
				} else if (result == -1) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("GUILD_NOT_JOIN"));
					body.writeBytes(res.build().toByteArray());
					return;
				} else if (result == -2) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("GUILD_NO_POWER"));
					body.writeBytes(res.build().toByteArray());
					return;
				} else if (result == -3) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("PARAM_ERROR"));
					body.writeBytes(res.build().toByteArray());
					return;
				} else if (result == -4) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("GUILD_NOTICE_TOO_LONG"));
					body.writeBytes(res.build().toByteArray());
					return;
				} else if (result == -9) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("GUILD_NOTICE_BLACK_STRING"));
					body.writeBytes(res.build().toByteArray());
					return;
				}
				res.setS2CCode(FAIL);
				res.setS2CMsg(LangService.getValue("SOMETHING_ERR"));
				body.writeBytes(res.build().toByteArray());
			}
		};
	}
}
