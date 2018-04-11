package cn.qeng.gm.api;

import com.alibaba.fastjson.JSON;

import cn.qeng.common.gm.RpcOpcode;

/**
 * 查询仙盟信息.
 *
 * @author 小流氓(176543888@qq.com)
 */
public class QueryGuildInfoAPI extends GmAPI {
	private String guildname;
	private int type;

	public QueryGuildInfoAPI(String guildname, int type) {
		this.guildname = guildname;
		this.type = type;
	}

	@Override
	protected short getOp() {
		return RpcOpcode.OPCODE_QUERY_GUILD_INFO;
	}

	@Override
	protected String getArgs() {
		return JSON.toJSONString(new Object[] { guildname, type });
	}
}