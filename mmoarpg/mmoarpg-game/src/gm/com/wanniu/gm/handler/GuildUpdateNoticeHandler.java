package com.wanniu.gm.handler;

import com.alibaba.fastjson.JSONArray;
import com.wanniu.game.guild.GuildServiceCenter;
import com.wanniu.game.guild.GuildUtil;
import com.wanniu.game.poes.GuildPO;
import com.wanniu.gm.GMEvent;
import com.wanniu.gm.GMResponse;
import com.wanniu.gm.GMStateResponse;

import cn.qeng.common.gm.RpcOpcode;

/**
 * 修改仙盟公告
 * 
 * @author lxm
 */
@GMEvent
public class GuildUpdateNoticeHandler extends GMBaseHandler {

	public GMResponse execute(JSONArray arr) {
		String guildId = arr.getString(0);
		String notice = arr.getString(1);
		if (notice != null && notice.length() > 100) {
			notice = notice.substring(0, 100);
		}
		GuildPO guild = GuildUtil.getGuild(guildId);
		guild.notice = notice;

		GuildServiceCenter.getInstance().saveGuild(guild);
		return new GMStateResponse(1);
	}

	public short getType() {
		return RpcOpcode.OPCODE_GUILD_UPDATE_NOTICE;
	}
}