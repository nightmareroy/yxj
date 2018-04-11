package com.wanniu.game.request.player;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wanniu.core.GConfig;
import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.core.proxy.ProxyClient;
import com.wanniu.core.proxy.ProxyType.ProxyMethod;
import com.wanniu.game.area.Area;
import com.wanniu.game.area.AreaManager;
import com.wanniu.game.common.Const.SCENE_TYPE;
import com.wanniu.game.common.Utils;
import com.wanniu.game.common.msg.ErrorResponse;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.PlayerHandler.AreaLineData;
import pomelo.area.PlayerHandler.GetAreaLinesResponse;

/**
 * 获取分线
 * @author agui
 *
 */
@GClientEvent("area.playerHandler.getAreaLinesRequest")
public class GetAreaLinesHandler extends PomeloRequest {

	public PomeloResponse request() throws Exception {

		WNPlayer player = (WNPlayer) pak.getPlayer();

		Area area = player.getArea();
		if (!area.isNormal() 
				&& area.sceneType != SCENE_TYPE.CROSS_SERVER.getValue()
				&& area.sceneType != SCENE_TYPE.WORLD_BOSS.getValue()) {
			return new ErrorResponse(LangService.getValue("LINE_CHANGE_FAILED"));
		}
		
		if (area.sceneType == SCENE_TYPE.CROSS_SERVER.getValue() && GConfig.getInstance().isEnableProxy()) {
			JSONObject json = ProxyClient.getInstance().request(ProxyMethod.M_LINES,
					Utils.toJSON("areaId", area.areaId, "logicServerId", area.logicServerId));
			return new PomeloResponse() {
				@Override
				protected void write() throws IOException {
					GetAreaLinesResponse.Builder res = GetAreaLinesResponse.newBuilder();
					res.setS2CCode(OK);
					JSONArray arr = json.getJSONArray("lines");
					for (int i = 0; i < arr.size(); i++) {
						JSONObject json = arr.getJSONObject(i);
						AreaLineData.Builder line = AreaLineData.newBuilder();
						line.setIndex(json.getIntValue("lineIndex"));
						line.setInstanceId(json.getString("instanceId"));
						int curCount = json.getIntValue("curCount");
						if (curCount < area.prop.boundary) {
							line.setState(0);
						} else if (curCount >= area.prop.boundary && curCount < area.fullCount) {
							line.setState(1);
						} else {
							line.setState(2);
						}
						res.addS2CData(line);
					}
					body.writeBytes(res.build().toByteArray());
				}
			};
		}
		

		List<Area> areas = AreaManager.getInstance().getAreaMap().getAreas(area.areaId, area.logicServerId);
		Collections.sort(areas, new Comparator<Area>() {
			@Override
			public int compare(Area o1, Area o2) {
				return o1.lineIndex > o2.lineIndex ? 1 : o1.lineIndex == o2.lineIndex ? 0 : -1;
			}
		});
		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				GetAreaLinesResponse.Builder res = GetAreaLinesResponse.newBuilder();
				res.setS2CCode(OK);
				for (Area area : areas) {
					AreaLineData.Builder line = AreaLineData.newBuilder();
					line.setIndex(area.lineIndex);
					line.setInstanceId(area.instanceId);
					int curCount = area.getPlayerNum();
					if (area.getPlayerNum() < area.prop.boundary) {
						line.setState(0);
					} else if (curCount >= area.prop.boundary && curCount < area.fullCount) {
						line.setState(1);
					} else {
						line.setState(2);
					}
					res.addS2CData(line);
				}
				body.writeBytes(res.build().toByteArray());
			}
		};
	}
}