package com.wanniu.gm.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wanniu.core.common.StringInt;
import com.wanniu.game.common.Const.PlayerBtlData;
import com.wanniu.game.data.GameData;
import com.wanniu.game.petNew.PetNew;
import com.wanniu.game.petNew.PetSkill;
import com.wanniu.game.player.PlayerDao;
import com.wanniu.game.player.PlayerUtil;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.player.po.AllBlobPO;
import com.wanniu.gm.GMEvent;
import com.wanniu.gm.GMJsonResponse;
import com.wanniu.gm.GMResponse;

/**
 * 宠物详细信息查询
 * 
 * @author lxm
 *
 */
@GMEvent
public class PetInfoHandler extends GMBaseHandler {

	public GMResponse execute(JSONArray arr) {
		String id = arr.getString(0);
		int petId = arr.getIntValue(1);
		WNPlayer wnPlayer = PlayerUtil.getOnlinePlayer(id);
		if (wnPlayer == null) {
			AllBlobPO allBlobData = PlayerDao.getAllBlobData(id);
			wnPlayer = new WNPlayer(allBlobData);
		}
		PetNew pw = wnPlayer.petNewManager.playerPets.get(petId);

		List<StringInt> skills = new ArrayList<>();
		for (PetSkill s : pw.po.skills.values()) {
			skills.add(new StringInt(s.level, GameData.PetSkills.get(s.id).skillName));
		}

		// 宠物属性
		List<StringInt> attr = new ArrayList<>();
		for (Entry<PlayerBtlData, Integer> entry : pw.attr_final_pet.entrySet()) {
			attr.add(new StringInt(entry.getValue(), entry.getKey().chName));
		}

		// 主人属性
		List<StringInt> master = new ArrayList<>();
		for (Entry<PlayerBtlData, Integer> entry : pw.attr_master.entrySet()) {
			master.add(new StringInt(entry.getValue(), entry.getKey().chName));
		}

		Map<String, Object> all = new HashMap<String, Object>();

		Map<String, Object> data = new HashMap<String, Object>();
		data.put("total", skills.size());
		data.put("rows", skills);
		all.put("skills", data);

		data = new HashMap<String, Object>();
		data.put("total", attr.size());
		data.put("rows", attr);
		all.put("attr", data);

		data = new HashMap<String, Object>();
		data.put("total", master.size());
		data.put("rows", master);
		all.put("master", data);

		JSONObject jo = new JSONObject(all);

		return new GMJsonResponse(jo);
	}

	public short getType() {
		return 0x3026;
	}

}
