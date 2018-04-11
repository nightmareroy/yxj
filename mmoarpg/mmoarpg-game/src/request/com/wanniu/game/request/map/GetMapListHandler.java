package com.wanniu.game.request.map;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.data.GameData;
import com.wanniu.game.data.WorldZoneCO;

import pomelo.area.MapHandler.GetMapListRequest;
import pomelo.area.MapHandler.GetMapListResponse;

/**
 * @author Yangzz
 *
 */
@GClientEvent("area.mapHandler.getMapListRequest")
public class GetMapListHandler extends PomeloRequest {

	public PomeloResponse request() throws Exception {

//		WNPlayer player = (WNPlayer) pak.getPlayer();

		GetMapListRequest req = GetMapListRequest.parseFrom(pak.getRemaingBytes());
		int mapId = req.getC2SMapId();
		
		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				GetMapListResponse.Builder res = GetMapListResponse.newBuilder();
			    
			    List<Integer> result = new ArrayList<>();
			    List<WorldZoneCO> maps = GameData.findWorldZones(t -> {return t.followMapID == mapId;});
			    maps.forEach((map) -> {
//			        if(player.mapManager.isEnteredMap(map.mapID)){
//			            result.add(map.mapID);
//			        }
			    });
			    
				
				res.setS2CCode(OK);
				res.addAllS2CMapIds(result);
				body.writeBytes(res.build().toByteArray());
				return;

			}
		};
	}
}