package com.wanniu.gm.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.wanniu.game.data.base.DEquipBase;
import com.wanniu.game.data.base.DItemBase;
import com.wanniu.game.item.ItemConfig;
import com.wanniu.gm.GMEvent;
import com.wanniu.gm.GMJsonResponse;
import com.wanniu.gm.GMResponse;

/**
 * 获取所有物品数据
 * 
 * @author lxm
 *
 */
@GMEvent
public class ItemDataHandler extends GMBaseHandler {

	public GMResponse execute(JSONArray arr) {
		List<Object> list = new ArrayList<Object>();
		for (DItemBase it : ItemConfig.getInstance().getItemTemplates().values()) {
			Map<String, String> map = new HashMap<String, String>();
			map.put("id", it.code);
			map.put("text", it.name);
			list.add(map);
		}
		for (DEquipBase it : ItemConfig.getInstance().getEquipTemplates().values()) {
			Map<String, String> map = new HashMap<String, String>();
			map.put("id", it.code);
			map.put("text", it.name);
			list.add(map);
		}
		return new GMJsonResponse(JSON.toJSONString(list));
	}

	public short getType() {
		return 0x3030;
	}

}
