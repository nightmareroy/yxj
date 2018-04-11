package com.wanniu.gm.handler;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wanniu.game.GWorld;
import com.wanniu.game.data.GameData;
import com.wanniu.game.data.ext.CharacterExt;
import com.wanniu.game.data.ext.SkinListExt;
import com.wanniu.game.leaderBoard.LeaderBoardProto;
import com.wanniu.game.rank.RankType;
import com.wanniu.gm.GMErrorResponse;
import com.wanniu.gm.GMEvent;
import com.wanniu.gm.GMJsonResponse;
import com.wanniu.gm.GMResponse;

import pomelo.area.LeaderBoardHandler.LeaderBoardData;

/**
 * 排行榜
 * 
 * @author lxm
 *
 */
@GMEvent
public class RankHandler extends GMBaseHandler {

	public GMResponse execute(JSONArray arr) {
		int type = arr.getInteger(0);
		int subType = arr.getInteger(1);

		RankType kind = RankType.FIGHTPOWER;
		if (type == 0) {// 综合
			switch (subType) {
			case 0:
				kind = RankType.FIGHTPOWER;
				break;
			case 1:
				kind = RankType.LEVEL;
				break;
			case 2:
				kind = RankType.Mount;
				break;
			case 3:
				kind = RankType.PET;
				break;
			case 4:
				kind = RankType.XIANYUAN;
				break;
			case 5:
				kind = RankType.DEMON_TOWER;
				break;
			case 6:
				kind = RankType.HP;
				break;
			case 7:
				kind = RankType.PHY;
				break;
			case 8:
				kind = RankType.MAGIC;
				break;
			default:
				break;
			}
		} else if (type == 1) {// 仙盟
			if (subType == 0) {
				kind = RankType.GUILD_LEVEL;
			} else if (subType == 1) {
				kind = RankType.GUILD_FORT;
			}
		} else if (type == 2) {// 竞技
			if (subType == 0) {
				kind = RankType.SOLO_SCORE;
			} else if (subType == 1) {
				kind = RankType.PVP_5V5;
			} else if (subType == 2) {
				kind = RankType.ARENA_SCOREALL;
			}
		}
		LeaderBoardProto result = kind.getHandler().getRankData(GWorld.__SERVER_ID, -1, "");
		if (result == null) {
			return new GMErrorResponse();
		}

		JSONArray jaData = new JSONArray();
		for (LeaderBoardData data : result.s2c_lists) {
			JSONArray ja = new JSONArray();
			for (int i = 0; i < data.getContentsCount(); i++) {
				if (i == 2) {
					CharacterExt p = GameData.Characters.get(Integer.parseInt(data.getContents(i)));
					ja.add(p == null ? "" : p.proName);
					continue;
				}
				if (kind.equals(RankType.Mount) && i == 6) {
					SkinListExt skin = GameData.SkinLists.get(Integer.parseInt(data.getContents(i)));
					ja.add(skin == null ? "" : skin.skinName);
					continue;
				}
				ja.add(data.getContents(i));
			}
			jaData.add(ja);
		}
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("data", jaData);
		JSONObject jo = new JSONObject(data);
		return new GMJsonResponse(jo);
	}

	public short getType() {
		return 0x5010;
	}

}
