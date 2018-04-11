package com.wanniu.game.request.attendance;

import java.io.IOException;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.entity.GPlayer;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.core.logfs.Out;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.AttendanceHandler.GetDailyRewardResponse;

/**
 * 获取每日签到信息
 * 
 * @author Tanglt
 *
 */
@GClientEvent("area.attendanceHandler.getDailyRewardRequest")
public class GetDailyRewardHandler extends PomeloRequest {

	@Override
	public PomeloResponse request() throws Exception {

		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				GPlayer player = pak.getPlayer();
				if (player == null) {
					GetDailyRewardResponse.Builder res = GetDailyRewardResponse.newBuilder();
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("SOMETHING_ERR"));
					body.writeBytes(res.build().toByteArray());
				} else {
					WNPlayer wplayer = (WNPlayer) player;
					int code = wplayer.playerAttendance.getDailyReward();
					GetDailyRewardResponse.Builder res = null;
					switch (code) {
					case 0:
						res = wplayer.playerAttendance.createGetDailyRewardResponse();
						res.setS2CCode(OK);
						body.writeBytes(res.build().toByteArray());
						wplayer.activityManager.updateDeskRedPoint();
						Out.info(player.getId(),":普通签到成功");
						break;
					case -1:
						res = GetDailyRewardResponse.newBuilder();
						res.setS2CCode(FAIL);
						res.setS2CMsg(LangService.getValue("SIGN_HAVE_SIGNED"));
						body.writeBytes(res.build().toByteArray());
						break;
					case -2:
						res = GetDailyRewardResponse.newBuilder();
						res.setS2CCode(FAIL);
						res.setS2CMsg(LangService.getValue("SIGN_NOT_EXIST"));
						body.writeBytes(res.build().toByteArray());
						break;
					case -3:
						res = GetDailyRewardResponse.newBuilder();
						res.setS2CCode(FAIL);
						res.setS2CMsg(LangService.getValue("BAG_NOT_ENOUGH_POS"));
						body.writeBytes(res.build().toByteArray());
						break;
					default:
						res = GetDailyRewardResponse.newBuilder();
						res.setS2CCode(FAIL);
						res.setS2CMsg(LangService.getValue("SOMETHING_ERR"));
						body.writeBytes(res.build().toByteArray());
						break;
					}
				}
			}
		};
	}
}
