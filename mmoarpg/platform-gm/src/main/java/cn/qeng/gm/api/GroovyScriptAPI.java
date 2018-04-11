package cn.qeng.gm.api;

import com.alibaba.fastjson.JSON;

import cn.qeng.common.gm.RpcOpcode;

/**
 * Groovy脚本执行.
 *
 * @author 小流氓(176543888@qq.com)
 */
public class GroovyScriptAPI extends GmAPI {
	private String script;

	public GroovyScriptAPI(String script) {
		this.script = script;
	}

	@Override
	protected short getOp() {
		return RpcOpcode.OPCODE_GROOVY_SCRIPT;
	}

	@Override
	protected String getArgs() {
		return JSON.toJSONString(new Object[] { script });
	}
}