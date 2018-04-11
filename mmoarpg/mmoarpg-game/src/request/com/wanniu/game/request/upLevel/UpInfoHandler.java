package com.wanniu.game.request.upLevel;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;

/**
 * 请求进阶界面
 * @author Yangzz
 *
 */
@GClientEvent("area.upLevelHandler.upInfoRequest")
public class UpInfoHandler extends PomeloRequest {

	public PomeloResponse request() throws Exception {
		
//		WNPlayer player = (WNPlayer) pak.getPlayer();
//		
//		UpInfoRequest req = UpInfoRequest.parseFrom(pak.getRemaingBytes());
//		
//		return new PomeloResponse() {
//			@Override
//			protected void write() throws IOException {
//				UpInfoResponse.Builder result = player.upLevelManager.handleUpInfo();
//
//			    if (result.getS2CCode() == UpLevelManager.ERR_CODE.ERR_CODE_OK.value) {
//			        result.setS2CCode(OK);
//			    } else if (result.getS2CCode() == UpLevelManager.ERR_CODE.ERR_CODE_NOT_REACH_REQ_LEVEL.value) {
//			        String msg = LangService.getValue("UPLEVEL_NOT_REACH_REQ_LEVEL").replace("{playerLevel}", String.valueOf(0));// result.ReqLevel
//			        result.setS2CCode(FAIL);
//			        result.setS2CMsg(msg);
//			    } else if (result.getS2CCode() == UpLevelManager.ERR_CODE.ERR_CODE_ALREADY_MAX_UPLEVEL.value) {
//			        result.setS2CCode(FAIL);
//			        result.setS2CMsg(LangService.getValue("UPLEVEL_ALREADY_MAX_UPLEVEL"));
//			    } else {
//			        result.setS2CCode(FAIL);
//			        result.setS2CMsg(LangService.getValue("SOMETHING_ERR"));
//			    }
//
//				body.writeBytes(result.build().toByteArray());
//			}
//		};
		
		return null;
	}
}
