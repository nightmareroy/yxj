package cn.qeng.gm.api;

import com.alibaba.fastjson.JSON;

import cn.qeng.common.gm.RpcOpcode;

/**
 * 查询玩家信息.
 *
 * @author 小流氓(176543888@qq.com)
 */
public class RollNoticeAPI extends GmAPI {
	private String content;

	public RollNoticeAPI(String content) {
		this.content = content;
	}

	@Override
	protected short getOp() {
		return RpcOpcode.OPCODE_ROLL_NOTICE;
	}

	@Override
	protected String getArgs() {
		return JSON.toJSONString(new Object[] { content });
	}
}