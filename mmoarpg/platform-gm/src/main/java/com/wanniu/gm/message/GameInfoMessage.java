package com.wanniu.gm.message;

import java.io.IOException;

import com.wanniu.tcp.protocol.Message;
import com.wanniu.util.DateUtil;

import cn.qeng.common.gm.RpcOpcode;
import cn.qeng.gm.module.maintain.domain.Server;

/**
 * 游戏基础信息消息
 * 
 * @author agui
 */
public class GameInfoMessage extends Message {

	private Server server;

	public GameInfoMessage(Server server) {
		this.server = server;
	}

	@Override
	protected void write() throws IOException {
		body.writeInt(server.getAreaId());
		body.writeInt(server.getId());
		body.writeString(server.getServerName());
		body.writeInt(server.getOlLimit());
		body.writeString(server.getOpenDate() == null ? null : DateUtil.format(server.getOpenDate()));
		body.writeBoolean(server.getIsNew());
		body.writeBoolean(server.getIsHot());
		body.writeBoolean(server.getIsRecommend());
		body.writeByte(server.getShowState());
		// 补一条对外时间
		body.writeString(server.getExternalTime() == null ? null : DateUtil.format(server.getExternalTime()));
	}

	@Override
	public short getType() {
		return RpcOpcode.OPCODE_SYNC_GAME_INFO;
	}
}