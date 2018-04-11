package cn.qeng.gm.api;

import com.alibaba.fastjson.JSON;

import cn.qeng.common.gm.RpcOpcode;

/**
 * 聊天黑名单...
 *
 * @author 小流氓(176543888@qq.com)
 */
public class ChatBacklistAPI extends GmAPI {
	private String ip;

	public ChatBacklistAPI(String ip) {
		this.ip = ip;
	}

	@Override
	protected short getOp() {
		return RpcOpcode.OPCODE_CHAT_BACKLIST;
	}

	@Override
	protected String getArgs() {
		return JSON.toJSONString(new Object[] { ip });
	}
}