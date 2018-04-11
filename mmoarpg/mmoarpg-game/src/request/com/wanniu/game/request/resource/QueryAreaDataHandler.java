package com.wanniu.game.request.resource;

import java.io.IOException;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.area.Area;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.ResourceHandler.Npc;
import pomelo.area.ResourceHandler.QueryAreaDataResponse;

/**
 * 查询场景npc数据
 * @author agui
 */
@GClientEvent("area.resourceHandler.queryAreaDataRequest")
public class QueryAreaDataHandler extends PomeloRequest {

	public PomeloResponse request() throws Exception {

		WNPlayer player = (WNPlayer) pak.getPlayer();

		Area area = player.getArea();
		if (area.npcDatas == null && area.isNormal()) {
			String units_str = player.getXmdsManager().getAllUnitInfo(player.getInstanceId());
			JSONArray npcs_json = JSON.parseArray(units_str);
			QueryAreaDataResponse.Builder res = QueryAreaDataResponse.newBuilder();
			res.setS2CCode(OK);
			for (int i = 0; i < npcs_json.size(); i++) {
				JSONObject unit = npcs_json.getJSONObject(i);
				if ("XmdsInstanceNPC".equals(unit.get("type"))) {
					Npc.Builder npc = Npc.newBuilder();
					npc.setTemplateId(unit.getIntValue("templateId"));
					npc.setId(unit.getIntValue("ObjectId"));
					res.addS2CNpcs(npc);
				}
			}
			area.npcDatas = res.build().toByteArray();
		}

		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				body.writeBytes(area.npcDatas == null ? QueryAreaDataResponse.newBuilder().setS2CCode(OK).build().toByteArray() : area.npcDatas);
			}
		};
	}

	public short getType() {
		return 0x503;
	}

}
