package com.wanniu.gm.handler;

import com.alibaba.fastjson.JSONArray;
import com.wanniu.core.groovy.GameGroovyManager;
import com.wanniu.gm.GMEvent;
import com.wanniu.gm.GMJsonResponse;
import com.wanniu.gm.GMResponse;

import cn.qeng.common.gm.RpcOpcode;

@GMEvent
public class JavaScriptHandler extends GMBaseHandler {

	public GMResponse execute(JSONArray arr) {
		String script = arr.getString(0);
		return new GMJsonResponse(GameGroovyManager.getInstance().sendGroovyCodeText(script));
	}

	public short getType() {
		return RpcOpcode.OPCODE_GROOVY_SCRIPT;
	}
}