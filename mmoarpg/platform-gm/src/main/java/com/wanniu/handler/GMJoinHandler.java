package com.wanniu.handler;

import java.util.Date;

import com.wanniu.GGlobal;
import com.wanniu.GServer;
import com.wanniu.gm.message.GameInfoMessage;
import com.wanniu.gm.request.GClientEvent;
import com.wanniu.tcp.protocol.NetHandler;
import com.wanniu.tcp.protocol.Packet;
import com.wanniu.util.Out;
import com.wanniu.util.StringUtil;

import cn.qeng.gm.module.maintain.domain.Server;
import cn.qeng.gm.module.maintain.service.ServerService;

@GClientEvent(0xff)
public class GMJoinHandler extends NetHandler {

	@Override
	public void execute(Packet pak) {
		String secrt = pak.getString();
		int sid = pak.getInt();
		short onlieCount = pak.getShort();
		pak.setAttr(GGlobal._KEY_SERVER_ID, sid);

		boolean isNew = false;
		Server server = ServerService.getServer(sid);
		if (server == null) {
			isNew = true;
			server = new Server();
			server.setId(sid);
			server.setDescribe("");
			server.setOpenDate(new Date());
		}

		// 在线人数...
		server.setOlCount(onlieCount);

		String oip = server.getIp();
		int oport = server.getPort();

		// IP与端口...
		String ip = pak.getString();
		int port = pak.getInt();
		server.setIp(ip);
		server.setPort(port);

		// 游戏服的名称
		String name = pak.getString();
		if (StringUtil.isNotEmpty(name) && (StringUtil.isEmpty(server.getServerName()) || String.valueOf(sid).equals(server.getServerName()))) {
			server.setServerName(name);
		}

		// 大区编号
		int areaId = pak.getInt();
		if (areaId == 0) {
			areaId = 2;
		}
		if (server.getAreaId() == 0) {
			server.setAreaId(areaId);
		}

		// 应用编号.
		server.setAppId(pak.getInt());
		ServerService.saveServer(server);

		// 合服情况...
		ServerService.getAllServer().forEach(s -> {
			if (oport == s.getPort() && StringUtil.isNotEmpty(oip) && oip.equals(s.getIp())) {
				s.setIp(ip);
				s.setPort(port);
				ServerService.saveServer(s);
			}
		});

		send(new GameInfoMessage(server));
		if (isNew) {
			ServerService.addServer(sid, server);
		}
		GServer.getInstance().addChannel(pak.getSession());

		ServerService.resetCalServerByGroupCaches();
		Out.info("secrt:", secrt, ", sid:", sid, ", onlineCount:", onlieCount);
	}
}