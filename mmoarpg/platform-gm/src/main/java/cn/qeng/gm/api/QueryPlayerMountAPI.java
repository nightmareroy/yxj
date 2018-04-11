package cn.qeng.gm.api;

import com.alibaba.fastjson.JSON;

import cn.qeng.common.gm.RpcOpcode;

/**
 * 查询个人坐骑信息.
 *
 * @author 小流氓(176543888@qq.com)
 */
public class QueryPlayerMountAPI extends GmAPI {
	private String playerId;

	public QueryPlayerMountAPI(String playerId) {
		this.playerId = playerId;
	}

	@Override
	protected short getOp() {
		return RpcOpcode.OPCODE_QUERY_PLAYER_MOUNT;
	}

	@Override
	protected String getArgs() {
		return JSON.toJSONString(new Object[] { playerId });
	}
}