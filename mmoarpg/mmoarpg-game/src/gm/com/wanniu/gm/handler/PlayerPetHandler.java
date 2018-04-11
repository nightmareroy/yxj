package com.wanniu.gm.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wanniu.game.data.GameData;
import com.wanniu.game.petNew.PetCenter;
import com.wanniu.game.player.PlayerUtil;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.poes.PetNewPO;
import com.wanniu.game.poes.PlayerPetsNewPO;
import com.wanniu.gm.GMEvent;
import com.wanniu.gm.GMJsonResponse;
import com.wanniu.gm.GMResponse;

import cn.qeng.common.gm.RpcOpcode;
import cn.qeng.common.gm.vo.GmPetVO;

/**
 * 玩家宠物查询
 * 
 * @author lxm
 *
 */
@GMEvent
public class PlayerPetHandler extends GMBaseHandler {

	public GMResponse execute(JSONArray arr) {
		String id = arr.getString(0);
		WNPlayer wnPlayer = PlayerUtil.getOnlinePlayer(id);
		PlayerPetsNewPO petPo = null;
		if (wnPlayer == null) {
			petPo = PetCenter.getInstance().findPet(id);
		} else {
			petPo = wnPlayer.petNewManager.petsPO;
		}

		List<GmPetVO> list = new ArrayList<>();
		for (PetNewPO p : petPo.pets.values()) {
			list.add(new GmPetVO(p.id, p.name, p.level, p.upLevel, p.fightPower, GameData.BaseDatas.get(p.id).type, p.id == petPo.fightPetId ? "是" : "否", p.skills.size()));
		}
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("total", list.size());
		data.put("rows", list);
		JSONObject jo = new JSONObject(data);

		return new GMJsonResponse(jo);
	}

	public short getType() {
		return RpcOpcode.OPCODE_QUERY_PLAYER_PET;
	}
}