package com.wanniu.gm.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wanniu.game.GWorld;
import com.wanniu.game.arena.ArenaService;
import com.wanniu.game.guild.GuildUtil;
import com.wanniu.game.poes.GuildPO;
import com.wanniu.game.rank.RankType;
import com.wanniu.game.solo.SoloService;
import com.wanniu.gm.GMEvent;
import com.wanniu.gm.GMJsonResponse;
import com.wanniu.gm.GMResponse;

import cn.qeng.common.gm.RpcOpcode;
import cn.qeng.common.gm.vo.GmPlayerRankVO;

/**
 * 个人排行榜
 * 
 * @author lxm
 */
@GMEvent
public class PlayerRankHandler extends GMBaseHandler {

	public GMResponse execute(JSONArray arr) {
		String id = arr.getString(0);
		List<GmPlayerRankVO> list = new ArrayList<>();
		long rank = 0;
		rank = RankType.FIGHTPOWER.getHandler().getRank(GWorld.__SERVER_ID, id);
		list.add(new GmPlayerRankVO("综合", "人物战力榜", rank == 0 ? "未上榜" : String.valueOf(rank)));

		rank = RankType.LEVEL.getHandler().getRank(GWorld.__SERVER_ID, id);
		list.add(new GmPlayerRankVO("综合", "人物等级榜", rank == 0 ? "未上榜" : String.valueOf(rank)));

		rank = RankType.Mount.getHandler().getRank(GWorld.__SERVER_ID, id);
		list.add(new GmPlayerRankVO("综合", "坐骑榜", rank == 0 ? "未上榜" : String.valueOf(rank)));

		rank = RankType.PET.getHandler().getRank(GWorld.__SERVER_ID, id);
		list.add(new GmPlayerRankVO("综合", "宠物榜", rank == 0 ? "未上榜" : String.valueOf(rank)));

		GuildPO guild = GuildUtil.getPlayerGuild(id);
		rank = RankType.GUILD_LEVEL.getHandler().getRank(GWorld.__SERVER_ID, guild == null ? "" : guild.id);
		list.add(new GmPlayerRankVO("综合", "仙盟等级榜", rank == 0 ? "未上榜" : String.valueOf(rank)));

		rank = RankType.SOLO_SCORE.getHandler().getSeasonRank(GWorld.__SERVER_ID, SoloService.getInstance().getTerm(), id);
		list.add(new GmPlayerRankVO("竞技榜", "问道大会", rank == 0 ? "未上榜" : String.valueOf(rank)));

		rank = RankType.PVP_5V5.getHandler().getRank(GWorld.__SERVER_ID, id);
		list.add(new GmPlayerRankVO("竞技榜", "试炼大赛", rank == 0 ? "未上榜" : String.valueOf(rank)));

		rank = RankType.ARENA_SCOREALL.getHandler().getSeasonRank(GWorld.__SERVER_ID, ArenaService.getInstance().getTerm(), id);
		list.add(new GmPlayerRankVO("竞技榜", "五岳一战", rank == 0 ? "未上榜" : String.valueOf(rank)));

		Map<String, Object> data = new HashMap<String, Object>();
		data.put("total", list.size());
		data.put("rows", list);
		JSONObject jo = new JSONObject(data);

		return new GMJsonResponse(jo);
	}

	public short getType() {
		return RpcOpcode.OPCODE_QUERY_PLAYER_RANK;
	}
}