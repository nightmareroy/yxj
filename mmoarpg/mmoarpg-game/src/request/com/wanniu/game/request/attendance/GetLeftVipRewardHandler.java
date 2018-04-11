package com.wanniu.game.request.attendance;

import java.io.IOException;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.entity.GPlayer;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.core.logfs.Out;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.AttendanceHandler.GetLeftVipRewardRequest;
import pomelo.area.AttendanceHandler.GetLeftVipRewardResponse;

/**
 * 获取VIP奖励
 * @author Tanglt
 *
 */
@GClientEvent("area.attendanceHandler.getLeftVipRewardRequest")
public class GetLeftVipRewardHandler extends PomeloRequest {

	@Override
	public PomeloResponse request() throws Exception {
		return new PomeloResponse() {
			GetLeftVipRewardRequest req = GetLeftVipRewardRequest.parseFrom(pak.getRemaingBytes());
			@Override
			protected void write() throws IOException {
				int id = req.getId();
				GPlayer player = pak.getPlayer();
				if(player == null){
					GetLeftVipRewardResponse.Builder res = GetLeftVipRewardResponse.newBuilder();
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("SOMETHING_ERR"));
					body.writeBytes(res.build().toByteArray());
					return;
				}
				if(id == 0){
					GetLeftVipRewardResponse.Builder res = GetLeftVipRewardResponse.newBuilder();
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("PARAM_ERROR"));
					body.writeBytes(res.build().toByteArray());
					return;
				}
				WNPlayer wPlayer = (WNPlayer)player;
				int resultCode = wPlayer.playerAttendance.getLeftVipReward(id);
				GetLeftVipRewardResponse.Builder res = null;
				switch(resultCode){
				case 0:
					res = wPlayer.playerAttendance.createGetLeftVipRewardResponse();
					res.setS2CCode(OK);
					body.writeBytes(res.build().toByteArray());
					Out.info(player.getId(),":vip签到成功");
					break;
				case -1:
					res = GetLeftVipRewardResponse.newBuilder();
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("SIGN_HAVE_RECEIVED"));
					body.writeBytes(res.build().toByteArray());
					break;
				case -2:
					res = GetLeftVipRewardResponse.newBuilder();
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("SIGN_HAVE_NOT_SIGN"));
					body.writeBytes(res.build().toByteArray());
					break;
				case -3:
					res = GetLeftVipRewardResponse.newBuilder();
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("SIGN_NO_VIP_DOUBLE"));
					body.writeBytes(res.build().toByteArray());
					break;
				case -4:
					res = GetLeftVipRewardResponse.newBuilder();
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("SIGN_VIP_LEVEL_NOT_ENOUGH"));
					body.writeBytes(res.build().toByteArray());
					break;
				case -5:
					res = GetLeftVipRewardResponse.newBuilder();
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("BAG_NOT_ENOUGH_POS"));
					body.writeBytes(res.build().toByteArray());
					break;
				default:
					res = GetLeftVipRewardResponse.newBuilder();
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("SOMETHING_ERR"));
					body.writeBytes(res.build().toByteArray());
					break;
				}
			}
		};
	}

}
