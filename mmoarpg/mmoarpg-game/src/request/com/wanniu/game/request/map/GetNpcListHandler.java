package com.wanniu.game.request.map;

import java.io.IOException;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.MapHandler.GetNpcListResponse;
import pomelo.area.MapHandler.MapUnit;

/**
 * 获取附近的NPC
 * @author agui
 */
@GClientEvent("area.mapHandler.getNpcListRequest")
public class GetNpcListHandler extends PomeloRequest {

	public PomeloResponse request() throws Exception {

		WNPlayer player = (WNPlayer) pak.getPlayer();
		
		JSONArray arr = JSON.parseArray(player.getXmdsManager().getAllNpcInfo(player.getInstanceId()));

		return new PomeloResponse() {
			@Override
			protected void write() throws IOException { 
				GetNpcListResponse.Builder res = GetNpcListResponse.newBuilder();
				res.setS2CCode(OK);

				for (int i = 0; i > arr.size(); i++) {
					JSONObject json = arr.getJSONObject(i);
					MapUnit.Builder unit = MapUnit.newBuilder();
					unit.setTemplateId(json.getIntValue("templateId"));
					int x = Math.round(json.getFloatValue("x"));
					int y = Math.round(json.getFloatValue("y"));
					unit.setX(x);
					unit.setY(y);
//					unit.setName(json.getString("name"));
					res.addData(unit);
				}

				body.writeBytes(res.build().toByteArray());
				return;

			}
		};
	}
}