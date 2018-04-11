package com.wanniu.game.request.guild.guildFort;

import java.io.IOException;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.common.Const;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.GuildFortHandler.ApplyReportDetailRequest;
import pomelo.area.GuildFortHandler.ApplyReportDetailResponse;


/**
 * 据点战请求战报详情的协议
 * @author fangyue
 *
 */
@GClientEvent("area.guildFortHandler.applyReportDetailRequest")
public class ApplyReportDetailHandler extends PomeloRequest {
	public PomeloResponse request() throws Exception {
		WNPlayer player = (WNPlayer) pak.getPlayer();
		ApplyReportDetailRequest req = ApplyReportDetailRequest.parseFrom(pak.getRemaingBytes());
		String date = req.getDate();
		int fortId = req.getAreaId();//据点编号
		
		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				ApplyReportDetailResponse.Builder res = ApplyReportDetailResponse.newBuilder();
				String msg = player.guildFortManager.handleApplyReportDetail(res, date, fortId);
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
