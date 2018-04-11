package cn.qeng.gm.api;

import com.alibaba.fastjson.JSON;

import cn.qeng.common.gm.RpcOpcode;

/**
 * 查询个人排行信息.
 *
 * @author 小流氓(176543888@qq.com)
 */
public class QueryPlayerRankAPI extends GmAPI {
	private String playerId;

	public QueryPlayerRankAPI(String playerId) {
		this.playerId = playerId;
	}

	@Override
	protected short getOp() {
		return RpcOpcode.OPCODE_QUERY_PLAYER_RANK;
	}

	@Override
	protected String getArgs() {
		return JSON.toJSONString(new Object[] { playerId });
	}
}