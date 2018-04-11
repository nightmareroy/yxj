package com.wanniu.gm.handler;

import com.alibaba.fastjson.JSONArray;
import com.wanniu.core.logfs.Out;
import com.wanniu.game.GWorld;
import com.wanniu.game.common.Const.KickReason;
import com.wanniu.game.player.PlayerUtil;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.gm.GMEvent;
import com.wanniu.gm.GMResponse;
import com.wanniu.gm.GMStateResponse;

import cn.qeng.common.gm.RpcOpcode;

@GMEvent
public class KickAllHandler extends GMBaseHandler {
	public GMResponse execute(JSONArray arr) {
		GWorld.getInstance().ansycExec(() -> {
			Out.info("接到后台发出的踢人命令.");
			for (String id : GWorld.getInstance().getOnlinePlayers().keySet()) {
				try {
					WNPlayer player = PlayerUtil.getOnlinePlayer(id);
					if (player != null) {
						player.kick(KickReason.SERVER_SHUT_DOWN);
					}
				} catch (Exception e) {
					Out.warn("踢人时发生异常.", e);
				}
			}
		});
		return new GMStateResponse(1);
	}

	public short getType() {
		return RpcOpcode.OPCODE_KICKALL;
	}
}