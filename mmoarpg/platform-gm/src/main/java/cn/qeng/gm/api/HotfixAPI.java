package cn.qeng.gm.api;

import com.alibaba.fastjson.JSON;

import cn.qeng.common.gm.RpcOpcode;

/**
 * 热修复.
 *
 * @author 小流氓(176543888@qq.com)
 */
public class HotfixAPI extends GmAPI {
	private String className;// 类全名 例：cn.qeng.gm.api.HotfixAPI
	private byte[] bodys;

	public HotfixAPI(String className, byte[] bodys) {
		this.className = className;
		this.bodys = bodys;
	}

	@Override
	protected short getOp() {
		return RpcOpcode.OPCODE_HOTFIX;
	}

	@Override
	protected String getArgs() {
		return JSON.toJSONString(new Object[] { className });
	}

	@Override
	protected byte[] getBytes() {
		return bodys;
	}
}