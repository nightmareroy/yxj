package com.wanniu.game.request.functionOpen;

import java.io.IOException;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.FunctionOpenHandler.SetFunctionPlayedRequest;
import pomelo.area.FunctionOpenHandler.SetFunctionPlayedResponse;

/**
 * 设置某个功能为玩过状态
 * @author haog
 *
 */
@GClientEvent("area.functionOpenHandler.setFunctionPlayedRequest")
public class SetFunctionPlayedHandler extends PomeloRequest {

	public PomeloResponse request() throws Exception {
		SetFunctionPlayedRequest req = SetFunctionPlayedRequest.parseFrom(pak.getRemaingBytes());
		WNPlayer player = (WNPlayer) pak.getPlayer();
		
		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				SetFunctionPlayedResponse.Builder res = SetFunctionPlayedResponse.newBuilder();
				// logic
				
				int result = player.functionOpenManager.setFunctionPlayed(req.getFunctionName());
			    if(result == 0){
			    	res.setS2CCode(OK);
			    	body.writeBytes(res.build().toByteArray());
			        return;
			    }else if(result == -1){
			    	res.setS2CCode(FAIL);
				    res.setS2CMsg(LangService.getValue("FUNC_SET_PLAYED_NOT_OPEN"));
			    	body.writeBytes(res.build().toByteArray());
			        return;
			    }
			    res.setS2CCode(FAIL);
			    res.setS2CMsg(LangService.getValue("SOMETHING_ERR"));
		    	body.writeBytes(res.build().toByteArray());
		        return;
			}
		};
	}
}
