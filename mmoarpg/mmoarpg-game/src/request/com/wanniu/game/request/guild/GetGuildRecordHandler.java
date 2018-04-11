package com.wanniu.game.request.guild;

import java.io.IOException;
import java.util.List;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.GuildHandler.GetGuildRecordRequest;
import pomelo.area.GuildHandler.GetGuildRecordResponse;
import pomelo.area.GuildHandler.RecordInfo;

@GClientEvent("area.guildHandler.getGuildRecordRequest")
public class GetGuildRecordHandler extends PomeloRequest {

	public PomeloResponse request() throws Exception {
		WNPlayer player = (WNPlayer) pak.getPlayer();
		GetGuildRecordRequest req = GetGuildRecordRequest.parseFrom(pak.getRemaingBytes());
		int page = req.getPage();
		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				GetGuildRecordResponse.Builder res = GetGuildRecordResponse.newBuilder();
				List<RecordInfo> recordList = player.guildManager.getGuildRecordList(page);
				res.setS2CCode(OK);
				res.setS2CPage(page);
				res.addAllS2CRecordList(recordList);
				body.writeBytes(res.build().toByteArray());
			}
		};
	}
}
