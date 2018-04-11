package com.wanniu.gm.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wanniu.game.common.ConstsTR;
import com.wanniu.game.data.GameData;
import com.wanniu.game.player.PlayerUtil;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.playerSkill.po.SkillDB;
import com.wanniu.game.poes.SkillsPO;
import com.wanniu.gm.GMEvent;
import com.wanniu.gm.GMJsonResponse;
import com.wanniu.gm.GMResponse;
import com.wanniu.redis.PlayerPOManager;

import cn.qeng.common.gm.RpcOpcode;
import cn.qeng.common.gm.vo.GmPlayerSkillVO;

/**
 * 玩家技能查询
 * 
 * @author lxm
 */
@GMEvent
public class PlayerSkillHandler extends GMBaseHandler {

	public GMResponse execute(JSONArray arr) {
		String id = arr.getString(0);
		WNPlayer wnPlayer = PlayerUtil.getOnlinePlayer(id);
		SkillsPO skillDb = null;
		if (wnPlayer == null) {
			skillDb = PlayerPOManager.findPO(ConstsTR.skillTR, id, SkillsPO.class);
		} else {
			skillDb = wnPlayer.skillKeyManager.player_skills;
		}

		List<GmPlayerSkillVO> list = new ArrayList<>();
		for (Entry<Integer, SkillDB> entry : skillDb.skills.entrySet()) {
			String skillName = GameData.SkillDatas.get(entry.getKey()).skillName;
			list.add(new GmPlayerSkillVO(skillName, entry.getValue().lv));
		}
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("total", list.size());
		data.put("rows", list);
		JSONObject jo = new JSONObject(data);

		return new GMJsonResponse(jo);
	}

	public short getType() {
		return RpcOpcode.OPCODE_QUERY_PLAYER_SKILL;
	}
}