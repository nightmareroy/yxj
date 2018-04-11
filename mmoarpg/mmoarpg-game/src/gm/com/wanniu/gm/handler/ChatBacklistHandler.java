package com.wanniu.gm.handler;

import com.alibaba.fastjson.JSONArray;
import com.wanniu.game.request.chat.ChatBacklistManager;
import com.wanniu.gm.GMEvent;
import com.wanniu.gm.GMResponse;
import com.wanniu.gm.GMStateResponse;

import cn.qeng.common.gm.RpcOpcode;

@GMEvent
public class ChatBacklistHandler extends GMBaseHandler {
	public GMResponse execute(JSONArray arr) {
		ChatBacklistManager.getInstance().addIp(arr.getString(0));
		return new GMStateResponse(1);
	}

	public short getType() {
		return RpcOpcode.OPCODE_CHAT_BACKLIST;
	}
}