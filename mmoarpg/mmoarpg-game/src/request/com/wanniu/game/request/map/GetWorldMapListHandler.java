package com.wanniu.game.request.map;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.area.AreaDataConfig;
import com.wanniu.game.data.base.MapBase;

import pomelo.area.MapHandler.GetWorldMapListResponse;
import pomelo.area.MapHandler.WorldMap;

/**
 * 
 * @author Yangzz
 *
 */
@GClientEvent("area.mapHandler.getWorldMapListRequest")
public class GetWorldMapListHandler extends PomeloRequest {

	public PomeloResponse request() throws Exception {

		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				GetWorldMapListResponse.Builder res = GetWorldMapListResponse.newBuilder();
				List<WorldMap> data = new ArrayList<>();
				List<MapBase> props = AreaDataConfig.getInstance().find(t -> {
					return t.type >= 1 && t.type <= 2;
				});
			    props.forEach((prop) ->{
					WorldMap.Builder map = WorldMap.newBuilder();
					map.setId(prop.mapID);
					map.setIsOpen(1);
			        data.add(map.build());
			    });
				res.addAllData(data);
				res.setS2CCode(OK);
				body.writeBytes(res.build().toByteArray());
			}
		};
	}
}