package com.wanniu.gm.handler;

import com.alibaba.fastjson.JSONArray;
import com.wanniu.core.util.DateUtil;
import com.wanniu.game.common.Const.KickReason;
import com.wanniu.game.player.PlayerUtil;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.poes.PlayerPO;
import com.wanniu.gm.GMEvent;
import com.wanniu.gm.GMResponse;
import com.wanniu.gm.GMStateResponse;

import cn.qeng.common.gm.RpcOpcode;

@GMEvent
public class RoleForbidHandler extends GMBaseHandler {
	public enum ForbidType {
		// 冻结、解冻、禁言、解禁、T下线
		LOCK, UNLOCK, SHUTUP, UNSHUTUP, KICK
	}

	public GMResponse execute(JSONArray arr) {
		String id = arr.getString(0);
		int type = arr.getIntValue(1);
		String time = arr.getString(2);
		String reason = arr.getString(3);
		PlayerPO po = PlayerUtil.getPlayerBaseData(id);
		if (type == ForbidType.LOCK.ordinal()) {
			WNPlayer player = PlayerUtil.getOnlinePlayer(id);
			if (player != null) {
				player.kick(KickReason.GM_KICK);
			}
			po.freezeTime = DateUtil.format(time);
			po.freezeReason = reason;
		} else if (type == ForbidType.UNLOCK.ordinal()) {
			po.freezeTime = null;
			po.freezeReason = null;
		} else if (type == ForbidType.SHUTUP.ordinal()) {
			po.forbidTalkTime = DateUtil.format(time);
			po.forbidTalkReason = reason;
		} else if (type == ForbidType.UNSHUTUP.ordinal()) {
			po.forbidTalkTime = null;
			po.forbidTalkReason = null;
		} else if (type == ForbidType.KICK.ordinal()) {
			WNPlayer player = PlayerUtil.getOnlinePlayer(id);
			if (player != null) {
				player.kick(KickReason.GM_KICK);
			}
		}
		return new GMStateResponse(1);
	}

	public short getType() {
		return RpcOpcode.OPCODE_PUBLISH;
	}
}
