package cn.qeng.gm.api;

import com.alibaba.fastjson.JSON;

import cn.qeng.common.gm.RpcOpcode;

/**
 * 查询个人宠物信息.
 *
 * @author 小流氓(176543888@qq.com)
 */
public class QueryPlayerPetAPI extends GmAPI {
	private String playerId;

	public QueryPlayerPetAPI(String playerId) {
		this.playerId = playerId;
	}

	@Override
	protected short getOp() {
		return RpcOpcode.OPCODE_QUERY_PLAYER_PET;
	}

	@Override
	protected String getArgs() {
		return JSON.toJSONString(new Object[] { playerId });
	}
}