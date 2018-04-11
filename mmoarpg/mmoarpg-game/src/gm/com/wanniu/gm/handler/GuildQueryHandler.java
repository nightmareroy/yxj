package com.wanniu.gm.handler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wanniu.game.data.GameData;
import com.wanniu.game.guild.GuildUtil;
import com.wanniu.game.guild.dao.GuildDao;
import com.wanniu.game.player.PlayerUtil;
import com.wanniu.game.poes.GuildMemberPO;
import com.wanniu.game.poes.GuildPO;
import com.wanniu.game.poes.PlayerPO;
import com.wanniu.gm.GMErrorResponse;
import com.wanniu.gm.GMEvent;
import com.wanniu.gm.GMJsonResponse;
import com.wanniu.gm.GMResponse;

import cn.qeng.common.gm.RpcOpcode;
import cn.qeng.common.gm.vo.GmGuildMemberVO;
import cn.qeng.common.gm.vo.GmGuildVO;

/**
 * 仙盟查询
 * 
 * @author lxm
 */
@GMEvent
public class GuildQueryHandler extends GMBaseHandler {

	public GMResponse execute(JSONArray arr) {
		String guildName = arr.getString(0);
		int type = arr.getIntValue(1);
		if (type == 0) {// 精确查找
			String guildId = GuildUtil.getGuildByFullName(guildName);
			if (guildId == null) {
				return new GMErrorResponse();
			}
			List<GmGuildMemberVO> list = new ArrayList<>();
			List<GuildMemberPO> members = GuildUtil.getGuildMemberList(guildId);
			for (GuildMemberPO m : members) {
				PlayerPO playerPo = PlayerUtil.getPlayerBaseData(m.playerId);
				String state = PlayerUtil.isOnline(m.playerId) ? "在线" : "";
				if (state.isEmpty()) {
					long minute = (System.currentTimeMillis() - playerPo.logoutTime.getTime()) / 60000;
					if (minute < 60) {
						state = "离线" + minute + "分钟";
					} else {
						state = "离线" + minute / 60 + "小时";
					}
				}
				list.add(new GmGuildMemberVO(playerPo.name, playerPo.level, playerPo.fightPower, GuildUtil.getGuildJobPropByJobId(m.job).position, m.lastContributeValue, state, GameData.Characters.get(playerPo.pro).proName, m.job));
			}
			Collections.sort(list, new Comparator<GmGuildMemberVO>() {

				@Override
				public int compare(GmGuildMemberVO o1, GmGuildMemberVO o2) {
					int i = o1.getJob() - o2.getJob();
					if (i == 0) {
						i = o2.getLevel() - o1.getLevel();
					} else if (i == 0) {
						i = o2.getPower() - o1.getPower();
					}
					return i;
				}
			});
			GuildPO guild = GuildUtil.getGuild(guildId);
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("total", list.size());
			data.put("rows", list);
			data.put("id", guild.id);
			data.put("level", guild.level);
			data.put("notice", guild.notice);
			JSONObject jo = new JSONObject(data);

			return new GMJsonResponse(jo);
		} else {// 模糊查询
			List<GmGuildVO> list = new ArrayList<>();
			for (GuildPO guild : GuildDao.GuildMap.values()) {
				if (guild.name.indexOf(guildName) >= 0) {
					list.add(new GmGuildVO(guild.name, guild.level, guild.presidentName));
				}
			}

			Map<String, Object> data = new HashMap<String, Object>();
			data.put("total", list.size());
			data.put("rows", list);
			JSONObject jo = new JSONObject(data);
			return new GMJsonResponse(jo);
		}
	}

	public short getType() {
		return RpcOpcode.OPCODE_QUERY_GUILD_INFO;
	}
}