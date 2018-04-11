package cn.qeng.gm.api;

import com.alibaba.fastjson.JSON;

import cn.qeng.common.gm.RpcOpcode;

/**
 * 处罚玩家.
 *
 * @author 小流氓(176543888@qq.com)
 */
public class ForbidAPI extends GmAPI {
	private String id;
	private int type;
	private String time;
	private String reason;

	public ForbidAPI(String playerId, int type, String time, String reason) {
		this.id = playerId;
		this.type = type;
		this.time = time;
		this.reason = reason;
	}

	@Override
	protected short getOp() {
		return RpcOpcode.OPCODE_PUBLISH;
	}

	@Override
	protected String getArgs() {
		return JSON.toJSONString(new Object[] { id, type, time, reason });
	}
}