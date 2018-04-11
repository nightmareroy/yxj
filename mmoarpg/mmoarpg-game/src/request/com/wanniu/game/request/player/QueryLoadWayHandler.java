package com.wanniu.game.request.player;

import java.io.IOException;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.core.util.StringUtil;
import com.wanniu.game.player.PathService;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.PlayerHandler.QueryLoadWayRequest;
import pomelo.area.PlayerHandler.QueryLoadWayResponse;

/**
 * 获取角色坐标
 * 
 * @author Yangzz
 *
 */
@GClientEvent("area.playerHandler.queryLoadWayRequest")
public class QueryLoadWayHandler extends PomeloRequest {

	public PomeloResponse request() throws Exception {

		WNPlayer player = (WNPlayer) pak.getPlayer();

		QueryLoadWayRequest req = QueryLoadWayRequest.parseFrom(pak.getRemaingBytes());
		int toAreaId = req.getC2SAreaId();
		String pointId = req.getC2SPointId();
	    int areaId = player.getAreaId();
		
		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				QueryLoadWayResponse.Builder res = QueryLoadWayResponse.newBuilder();
				
				
				if(toAreaId == 0 || StringUtil.isEmpty(pointId) || areaId == toAreaId){
			    	res.setS2CCode(FAIL);
			    	res.setS2CMsg(LangService.getValue("SOMETHING_ERR"));
					body.writeBytes(res.build().toByteArray());
			        return;
			    }else{
			        int pointId = PathService.getInstance().findPath(areaId, toAreaId);
			        if(pointId != 0){
			        	res.setS2CPointId(String.valueOf(pointId));
			        	res.setS2CCode(OK);
						body.writeBytes(res.build().toByteArray());
			        }else{
				    	res.setS2CCode(FAIL);
				    	res.setS2CMsg(LangService.getValue("TARGET_PATH_NOT_AVAILABLE"));
						body.writeBytes(res.build().toByteArray());
				        return;
			        }
			    }
			}
		};
	}
}