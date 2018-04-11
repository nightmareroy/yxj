package cn.qeng.gm.api;

import com.alibaba.fastjson.JSON;

import cn.qeng.common.gm.RpcOpcode;

/**
 * 公会修改公告的功能.
 *
 * @author 小流氓(176543888@qq.com)
 */
public class GuildUpdateNoticeAPI extends GmAPI {
	private String guildId;
	private String notice;

	public GuildUpdateNoticeAPI(String guildId, String notice) {
		this.guildId = guildId;
		this.notice = notice;
	}

	@Override
	protected short getOp() {
		return RpcOpcode.OPCODE_GUILD_UPDATE_NOTICE;
	}

	@Override
	protected String getArgs() {
		return JSON.toJSONString(new Object[] { guildId, notice });
	}
}