package com.wanniu.game.request.guild;

import java.io.IOException;

import com.alibaba.fastjson.JSONObject;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.GuildHandler.GetGuildMoneyResponse;

@GClientEvent("area.guildHandler.getGuildMoneyRequest")
public class GetGuildMoneyHandler extends PomeloRequest {

	public PomeloResponse request() throws Exception {
		WNPlayer player = (WNPlayer) pak.getPlayer();
		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				GetGuildMoneyResponse.Builder res = GetGuildMoneyResponse.newBuilder();

				JSONObject retData = player.guildManager.toJson4MoneyPayLoad();
				res.setS2CCode(OK);
				res.setDepositCount(retData.getIntValue("depositCount"));
				res.setDepositCountMax(retData.getIntValue("depositCountMax"));
				body.writeBytes(res.build().toByteArray());
			}
		};
	}
}
