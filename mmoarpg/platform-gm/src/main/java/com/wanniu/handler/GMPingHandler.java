package com.wanniu.handler;

import com.wanniu.GGlobal;
import com.wanniu.gm.request.GClientEvent;
import com.wanniu.tcp.protocol.NetHandler;
import com.wanniu.tcp.protocol.Packet;
import com.wanniu.util.Out;

import cn.qeng.gm.module.maintain.domain.Server;
import cn.qeng.gm.module.maintain.service.ServerService;

@GClientEvent(0xf2)
public class GMPingHandler extends NetHandler {

	@Override
	public void execute(Packet pak) {
		short onlieCount = pak.getShort();
		Integer sid = pak.getAttr(GGlobal._KEY_SERVER_ID);
		Server server = ServerService.getServer(sid);
		if (server != null) {
			server.setOlCount(onlieCount);
			if (onlieCount > 100) {
				Out.info(server.getServerName(), " - ", sid, " onlie count:", onlieCount);
			}
		}
	}
}