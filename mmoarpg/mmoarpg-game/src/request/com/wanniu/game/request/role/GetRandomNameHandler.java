package com.wanniu.game.request.role;

import java.io.IOException;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.core.logfs.Out;
import com.wanniu.game.area.PlayerRemote;

import pomelo.connector.RoleHandler.GetRandomNameRequest;
import pomelo.connector.RoleHandler.GetRandomNameResponse;

/**
 * 获取随机名字
 * @author Yangzz
 *
 */
@GClientEvent("connector.roleHandler.getRandomNameRequest")
public class GetRandomNameHandler extends PomeloRequest {

	public PomeloResponse request() throws Exception {
		GetRandomNameRequest req = GetRandomNameRequest.parseFrom(pak.getRemaingBytes());
		final int pro =  req.getC2SPro();
		
		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				GetRandomNameResponse.Builder res = GetRandomNameResponse.newBuilder();
				
				if(pro == 0){
			        Out.warn("getRandomNameRequest pro is null!");
			        res.setS2CCode(KICK);
			        res.setS2CMsg(LangService.getValue("PARAM_ERROR"));
			        return;
			    }
				
		        String lastName = PlayerRemote.getRandomName(pro, 0); // logicServerId TODO 从session获取
				
				res.setS2CCode(OK);
				res.setS2CName(lastName);
				
				body.writeBytes(res.build().toByteArray());
			}
		};
	}

}
