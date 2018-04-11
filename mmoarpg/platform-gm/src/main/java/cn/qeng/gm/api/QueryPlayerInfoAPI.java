package cn.qeng.gm.api;

import com.alibaba.fastjson.JSON;

import cn.qeng.common.gm.RpcOpcode;

/**
 * 查询玩家信息.
 *
 * @author 小流氓(176543888@qq.com)
 */
public class QueryPlayerInfoAPI extends GmAPI {
	private String playername;
	private int type;

	public QueryPlayerInfoAPI(String playername, int type) {
		this.playername = playername;
		this.type = type;
	}

	@Override
	protected short getOp() {
		return RpcOpcode.OPCODE_QUERY_PLAYER_INFO;
	}

	@Override
	protected String getArgs() {
		return JSON.toJSONString(new Object[] { playername, type });
	}
}