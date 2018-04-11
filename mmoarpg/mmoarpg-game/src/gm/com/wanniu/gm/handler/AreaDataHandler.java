package com.wanniu.gm.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wanniu.game.data.GameData;
import com.wanniu.game.data.NormalMapCO;
import com.wanniu.gm.GMEvent;
import com.wanniu.gm.GMJsonResponse;
import com.wanniu.gm.GMResponse;

/**
 * 获取普通场景数据
 * 
 * @author lxm
 *
 */
@GMEvent
public class AreaDataHandler extends GMBaseHandler {

	public GMResponse execute(JSONArray arr) {
		List<Object> list = new ArrayList<Object>();
		for (NormalMapCO m : GameData.NormalMaps.values()) {
			Map<String, String> map = new HashMap<String, String>();
			map.put("id", String.valueOf(m.mapID));
			map.put("text", m.name + " " + m.reqLevel + "级");
			list.add(map);
		}
		JSONObject jo = new JSONObject();
		jo.put("data", list);
		return new GMJsonResponse(jo.toJSONString());
	}

	public short getType() {
		return 0x3003;
	}

}
