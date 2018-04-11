package com.wanniu.game.request.daily;

import java.io.IOException;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.core.logfs.Out;
import com.wanniu.game.common.CommonUtil;
import com.wanniu.game.daily.DailyActivityMgr;
import com.wanniu.game.player.PlayerUtil;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.DailyActivityHandler.DailyActivityResponse;

/**
 * 日常活跃
 * 
 * @author jjr
 *
 */
@GClientEvent("area.dailyActivityHandler.dailyActivityRequest")
public class DailyActivityHandler extends PomeloRequest {
	@Override
	public PomeloResponse request() throws Exception {
		return new PomeloResponse() {
			WNPlayer player = (WNPlayer) pak.getPlayer();

			@Override
			protected void write() throws IOException {
				DailyActivityResponse.Builder res = DailyActivityResponse.newBuilder();
				try {
					if (null == player) {
						res.setS2CCode(FAIL);
						res.setS2CMsg(LangService.getValue("SOMETHING_ERR"));
						body.writeBytes(res.build().toByteArray());
						PlayerUtil.logWarnIfPlayerNull(pak);
						return;
					}
					
					DailyActivityMgr mgr = player.dailyActivityMgr;
					res.setS2CCode(OK);
					res.addAllS2CDailyLs(mgr.getDailyLs());
					res.setS2CTotalDegree(mgr.po.totalDegree);
					res.addAllS2CDegreeLs(mgr.getDegreeLs());
					res.setS2CWeekIndex(CommonUtil.getWeek());
					body.writeBytes(res.build().toByteArray());
				} catch (Exception err) {
					Out.error(err);
					res.setS2CCode(FAIL);
					body.writeBytes(res.build().toByteArray());
				}
			}
		};
	}

}
