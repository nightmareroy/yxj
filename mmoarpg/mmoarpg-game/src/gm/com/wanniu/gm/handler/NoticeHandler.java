package com.wanniu.gm.handler;

import com.alibaba.fastjson.JSONArray;
import com.wanniu.core.game.entity.GPlayer;
import com.wanniu.game.GWorld;
import com.wanniu.game.common.Const;
import com.wanniu.game.common.Const.TipsType;
import com.wanniu.game.common.msg.MessageUtil;
import com.wanniu.game.player.PlayerUtil;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.gm.GMEvent;
import com.wanniu.gm.GMResponse;
import com.wanniu.gm.GMStateResponse;

import cn.qeng.common.gm.RpcOpcode;

@GMEvent
public class NoticeHandler extends GMBaseHandler {

	public GMResponse execute(JSONArray arr) {
		String content = arr.getString(0);
		for (GPlayer p : PlayerUtil.getAllOnlinePlayer()) {
			WNPlayer wp = (WNPlayer) p;
			wp.sendSysTip(content, TipsType.ROLL);
		}

		MessageUtil.sendRollChat(GWorld.__SERVER_ID, content, Const.CHAT_SCOPE.SYSTEM);
		return new GMStateResponse(1);
	}

	public short getType() {
		return RpcOpcode.OPCODE_ROLL_NOTICE;
	}
}