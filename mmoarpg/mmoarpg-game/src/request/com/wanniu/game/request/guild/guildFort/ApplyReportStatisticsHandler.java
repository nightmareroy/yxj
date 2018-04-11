package com.wanniu.game.request.guild.guildFort;

import java.io.IOException;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.common.Const;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.GuildFortHandler.ApplyReportStatisticsRequest;
import pomelo.area.GuildFortHandler.ApplyReportStatisticsResponse;


/**
 * 据点战请求战报成员列表信息的协议
 * @author fangyue
 *
 */
@GClientEvent("area.guildFortHandler.applyReportStatisticsRequest")
public class ApplyReportStatisticsHandler extends PomeloRequest {
	public PomeloResponse request() throws Exception {
		WNPlayer player = (WNPlayer) pak.getPlayer();
		ApplyReportStatisticsRequest req = ApplyReportStatisticsRequest.parseFrom(pak.getRemaingBytes());
		String date = req.getDate();
		int fortId = req.getAreaId();//据点编号
		String guildId = req.getGuildId();
		
		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				ApplyReportStatisticsResponse.Builder res = ApplyReportStatisticsResponse.newBuilder();
				String msg = player.guildFortManager.handleApplyReportStatistics(res, date, fortId, guildId);
				if (msg != null) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(msg);
				} else {
					res.setS2CCode(Const.CODE.OK);
				}
				body.writeBytes(res.build().toByteArray());
			}
		};
	}
}
