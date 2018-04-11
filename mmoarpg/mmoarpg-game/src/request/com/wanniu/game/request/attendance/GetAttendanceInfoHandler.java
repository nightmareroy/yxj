package com.wanniu.game.request.attendance;

import java.io.IOException;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.entity.GPlayer;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.AttendanceHandler.GetAttendanceInfoResponse;

/**
 * 获取签到信息
 * @author Tanglt
 *
 */
@GClientEvent("area.attendanceHandler.getAttendanceInfoRequest")
public class GetAttendanceInfoHandler extends PomeloRequest {
	@Override
	public PomeloResponse request() throws Exception {
		return new PomeloResponse(){
			@Override
			protected void write() throws IOException {
				GPlayer player = pak.getPlayer();
				if(player != null){
					WNPlayer wPlayer = (WNPlayer)player;
					GetAttendanceInfoResponse.Builder res = wPlayer.playerAttendance.createGetAttendanceInfoResponse();
					res.setS2CCode(OK);
					body.writeBytes(res.build().toByteArray());
				}
				else{
					GetAttendanceInfoResponse.Builder res = GetAttendanceInfoResponse.newBuilder();
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("SOMETHING_ERR"));
					body.writeBytes(res.build().toByteArray());
				}
			}
		};
	}

}
