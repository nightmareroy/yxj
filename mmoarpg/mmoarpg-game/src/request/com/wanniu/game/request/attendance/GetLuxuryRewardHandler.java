package com.wanniu.game.request.attendance;

import java.io.IOException;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.entity.GPlayer;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.core.logfs.Out;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.AttendanceHandler.GetLuxuryRewardResponse;

/**
 * 获取豪华签到信息
 * @author Tanglt
 *
 */
@GClientEvent("area.attendanceHandler.getLuxuryRewardRequest")
public class GetLuxuryRewardHandler extends PomeloRequest {

	@Override
	public PomeloResponse request() throws Exception {
		
		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				GPlayer player = pak.getPlayer();
				if(player == null){
					GetLuxuryRewardResponse.Builder res = GetLuxuryRewardResponse.newBuilder();
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("SOMETHING_ERR"));
					body.writeBytes(res.build().toByteArray());
					return;
				}
				WNPlayer wPlayer = (WNPlayer)player;
				int resultCode = wPlayer.playerAttendance.getLuxuryReward();
				GetLuxuryRewardResponse.Builder res = null;
				switch(resultCode){
				case 0:
					res = wPlayer.playerAttendance.createGetLuxuryRewardResponse();
					res.setS2CCode(OK);
					body.writeBytes(res.build().toByteArray());
					Out.info(player.getId(),":豪华签到成功");
					break;
				case -1:
					res = GetLuxuryRewardResponse.newBuilder();
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("SIGN_HAVE_RECEIVED"));
					body.writeBytes(res.build().toByteArray());
					break;
				case -2:
					res = GetLuxuryRewardResponse.newBuilder();
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("SIGN_NOT_RECHARGE"));
					body.writeBytes(res.build().toByteArray());
					break;
				case -3:
					res = GetLuxuryRewardResponse.newBuilder();
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("BAG_NOT_ENOUGH_POS"));
					body.writeBytes(res.build().toByteArray());
					break;
				default:
					res = GetLuxuryRewardResponse.newBuilder();
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("SOMETHING_ERR"));
					body.writeBytes(res.build().toByteArray());
					break;
				}
			}
		};
	}

}
