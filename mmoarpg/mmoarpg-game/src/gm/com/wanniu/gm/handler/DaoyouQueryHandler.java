package com.wanniu.gm.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wanniu.game.daoyou.DaoYouCenter;
import com.wanniu.game.data.GameData;
import com.wanniu.game.player.PlayerUtil;
import com.wanniu.game.poes.DaoYouMemberPO;
import com.wanniu.game.poes.DaoYouPO;
import com.wanniu.game.poes.PlayerPO;
import com.wanniu.gm.GMErrorResponse;
import com.wanniu.gm.GMEvent;
import com.wanniu.gm.GMJsonResponse;
import com.wanniu.gm.GMResponse;

import cn.qeng.common.gm.RpcOpcode;
import cn.qeng.common.gm.vo.GmDaoYouVO;

/**
 * 道友查询
 * 
 * @author lxm
 *
 */
@GMEvent
public class DaoyouQueryHandler extends GMBaseHandler {

	public GMResponse execute(JSONArray arr) {
		String daoyouName = arr.getString(0);
		String daoyouId = DaoYouCenter.getInstance().getDaoYouId(daoyouName);
		if (daoyouId == null) {
			return new GMErrorResponse();
		}
		DaoYouPO daoyouPo = DaoYouCenter.getInstance().getDaoYou(daoyouId);
		List<GmDaoYouVO> list = new ArrayList<>();
		List<String> members = DaoYouCenter.getInstance().getAllDaoYouMember(daoyouId);
		for (String pid : members) {
			PlayerPO playerPo = PlayerUtil.getPlayerBaseData(pid);
			if (playerPo == null) {
				continue;
			}
			DaoYouMemberPO memberPo = DaoYouCenter.getInstance().getDaoYouMember(pid);
			list.add(new GmDaoYouVO(playerPo.name, playerPo.level, playerPo.fightPower, GameData.Characters.get(playerPo.pro).proName, daoyouPo.adminPlayerId.equals(pid) ? "是" : "否", String.valueOf(memberPo.todayReciveRebate), String.valueOf(memberPo.totalReciveRebate)));
		}
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("total", list.size());
		data.put("rows", list);
		data.put("power", daoyouPo.fightPower);
		JSONObject jo = new JSONObject(data);
		return new GMJsonResponse(jo);
	}

	public short getType() {
		return RpcOpcode.OPCODE_QUERY_DAOYOU_INFO;
	}
}