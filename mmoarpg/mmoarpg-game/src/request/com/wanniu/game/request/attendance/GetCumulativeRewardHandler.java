package com.wanniu.game.request.attendance;

import java.io.IOException;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.entity.GPlayer;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.core.logfs.Out;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.AttendanceHandler.GetCumulativeRewardRequest;
import pomelo.area.AttendanceHandler.GetCumulativeRewardResponse;

/**
 * 获取累计签到信息
 * @author Tanglt
 *
 */
@GClientEvent("area.attendanceHandler.getCumulativeRewardRequest")
public class GetCumulativeRewardHandler extends PomeloRequest {

	@Override
	public PomeloResponse request() throws Exception {
		GetCumulativeRewardRequest req = GetCumulativeRewardRequest.parseFrom(pak.getRemaingBytes());
		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				GPlayer player = pak.getPlayer();
				if(player == null){
					GetCumulativeRewardResponse.Builder res = GetCumulativeRewardResponse.newBuilder();
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("SOMETHING_ERR"));
					body.writeBytes(res.build().toByteArray());
					return;
				}
				int id = req.getId();
				if(id == 0){
					GetCumulativeRewardResponse.Builder res = GetCumulativeRewardResponse.newBuilder();
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("PARAM_ERROR"));
					body.writeBytes(res.build().toByteArray());
					return;
				}
				WNPlayer wPlayer = (WNPlayer)player;
				GetCumulativeRewardResponse.Builder res = null;
				int resultCode = wPlayer.playerAttendance.getCumulativeReward(id);
				switch(resultCode){
				case 0:
					res = wPlayer.playerAttendance.createGetCumulativeRewardResponse();
					res.setS2CCode(OK);
					body.writeBytes(res.build().toByteArray());
					Out.info(player.getId(),":累计签到成功");
					break;
				case -1:
					res = GetCumulativeRewardResponse.newBuilder();
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("SIGN_HAVE_RECEIVED"));
					body.writeBytes(res.build().toByteArray());
					break;
				case -2:
					res = GetCumulativeRewardResponse.newBuilder();
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("SIGN_NOT_EXIST"));
					body.writeBytes(res.build().toByteArray());
					break;
				case -3:
					res = GetCumulativeRewardResponse.newBuilder();
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("SIGN_NOT_ENOUGH_COUNT"));
					body.writeBytes(res.build().toByteArray());
					break;
				case -4:
					res = GetCumulativeRewardResponse.newBuilder();
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("BAG_NOT_ENOUGH_POS"));
					body.writeBytes(res.build().toByteArray());
					break;
				default:
					res = GetCumulativeRewardResponse.newBuilder();
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("SOMETHING_ERR"));
					body.writeBytes(res.build().toByteArray());
					break;
				}
			}
		};
	}

}
