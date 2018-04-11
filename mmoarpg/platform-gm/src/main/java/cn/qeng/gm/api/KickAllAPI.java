package cn.qeng.gm.api;

import com.alibaba.fastjson.JSON;

import cn.qeng.common.gm.RpcOpcode;

/**
 * T人操作.
 *
 * @author 小流氓(176543888@qq.com)
 */
public class KickAllAPI extends GmAPI {

	public KickAllAPI() {}

	@Override
	protected short getOp() {
		return RpcOpcode.OPCODE_KICKALL;
	}

	@Override
	protected String getArgs() {
		return JSON.toJSONString(new Object[] {});
	}
}