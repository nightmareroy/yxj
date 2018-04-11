package com.wanniu.game.request.guild.guildBless;

import java.io.IOException;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.guild.GuildResult;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.GuildBlessHandler.ReceiveBlessGiftRequest;
import pomelo.area.GuildBlessHandler.ReceiveBlessGiftResponse;

@GClientEvent("area.guildBlessHandler.receiveBlessGiftRequest")
public class ReceiveBlessGiftHandler extends PomeloRequest {

	public PomeloResponse request() throws Exception {
		WNPlayer player = (WNPlayer) pak.getPlayer();
		ReceiveBlessGiftRequest req = ReceiveBlessGiftRequest.parseFrom(pak.getRemaingBytes());
		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				ReceiveBlessGiftResponse.Builder res = ReceiveBlessGiftResponse.newBuilder();

				GuildResult resData = player.guildManager.receiveBlessGift(req.getIndex());
				int result = resData.result;
				if (0 == result) {
					res.setS2CCode(OK);
					body.writeBytes(res.build().toByteArray());
					return;
				} else if (-1 == result) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("BLESS_GIFT_HAVE_RECEIVED"));
					body.writeBytes(res.build().toByteArray());
					return;
				} else if (-2 == result) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("BAG_NOT_ENOUGH_POS"));
					body.writeBytes(res.build().toByteArray());
					return;
				} else if (1 == result) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("GUILD_NOT_JOIN"));
					body.writeBytes(res.build().toByteArray());
					return;
				} else if (2 == result) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("BLESS_NOT_FINISH"));
					body.writeBytes(res.build().toByteArray());
					return;
				} else if (3 == result) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("GUILD_PARAMS"));
					body.writeBytes(res.build().toByteArray());
					return;
				} else if (4 == result) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("GUILD_GETTED"));
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