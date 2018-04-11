package com.wanniu.gm;

import java.time.LocalDate;

import com.wanniu.core.gm.request.GMHandler;
import com.wanniu.core.logfs.Out;
import com.wanniu.core.tcp.protocol.Packet;
import com.wanniu.core.util.DateUtils;
import com.wanniu.core.util.StringUtil;
import com.wanniu.game.GWorld;

import cn.qeng.common.gm.RpcOpcode;

/**
 * 游戏服基本信息
 * 
 * @author agui
 */
public class GameInfoHandler extends GMHandler {

	public void execute(Packet pak) {
		GWorld.__AREA_ID = pak.getInt();
		GWorld.__ACROSS_SERVER_ID = pak.getInt();
		GWorld.__SERVER_NAME = pak.getString();
		GWorld.__PLAYER_LIMIT = pak.getInt();
		String openTime = pak.getString();

		if (StringUtil.isNotEmpty(openTime)) {
			// 兼容老的后台.
			if (openTime.length() > 10) {
				openTime = openTime.substring(0, 10);
			}
			GWorld.resetOpenServerDate(LocalDate.parse(openTime, DateUtils.F_YYYYMMDD));
		}
		// 不后台不设计开服时间，那就用今天
		else {
			GWorld.resetOpenServerDate(LocalDate.now());
		}

		GWorld.__NEW = pak.getBoolean();
		GWorld.__HOT = pak.getBoolean();
		GWorld.__RECOMMEND = pak.getBoolean();
		GWorld.__SHOW = pak.getByte();

		if (pak.remaing() > 0) {
			String externalTime = pak.getString();
			if (StringUtil.isNotEmpty(externalTime)) {
				GWorld.resetExternalTime(externalTime);
			}
		}

		GWorld.getInstance().syncServerInfo();
		Out.info("gm-server sync game info ok!");
	}

	public short getType() {
		return RpcOpcode.OPCODE_SYNC_GAME_INFO;
	}
}