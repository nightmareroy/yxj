package cn.qeng.gm.api;

import com.alibaba.fastjson.JSON;

import cn.qeng.common.gm.RpcOpcode;

/**
 * 查询玩家处罚信息.
 *
 * @author 小流氓(176543888@qq.com)
 */
public class QueryPublishAPI extends GmAPI {
	private String playername;

	public QueryPublishAPI(String playername) {
		this.playername = playername;
	}

	@Override
	protected short getOp() {
		return RpcOpcode.OPCODE_QUERY_PUBLISH;
	}

	@Override
	protected String getArgs() {
		return JSON.toJSONString(new Object[] { playername });
	}
}