package cn.qeng.gm.api;

import com.alibaba.fastjson.JSON;

import cn.qeng.common.gm.RpcOpcode;

public class QueryRechargeAPI extends GmAPI {
	private String playername;

	public QueryRechargeAPI(String playername) {
		this.playername = playername;
	}

	@Override
	protected short getOp() {
		return RpcOpcode.OPCODE_QUERY_RECHARGE;
	}

	@Override
	protected String getArgs() {
		return JSON.toJSONString(new String[] { playername });
	}
}