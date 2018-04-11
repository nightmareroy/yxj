package com.wanniu.core.db.connet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.wanniu.core.GConfig;
import com.wanniu.core.GGlobal;
import com.wanniu.core.GSystem;
import com.wanniu.core.db.DBType;
import com.wanniu.core.db.QueryVo;
import com.wanniu.core.db.message.DBJoinMessage;
import com.wanniu.core.db.message.DBPingMessage;
import com.wanniu.core.db.message.DBQueryMessage;
import com.wanniu.core.logfs.Out;
import com.wanniu.core.tcp.client.ClientWorker;
import com.wanniu.core.tcp.protocol.Message;
import com.wanniu.core.tcp.protocol.Packet;
import com.wanniu.core.tcp.protocol.Prefix;

/**
 * @author agui
 *
 */
public class DBClient extends ClientWorker {

	private static DBClient instance = new DBClient();

	public static DBClient getInstance() {
		return instance;
	}

	private DBClient() {
	}

	public boolean isEnable() {
		return GConfig.getInstance().isEnableDB();
	}

	/**
	 * 初始化和启动
	 * 
	 */
	public void start() {
		if (isEnable()) {
			this.serverHost = GConfig.getInstance().get("server.db.host");
			this.serverPort = GConfig.getInstance().getInt("server.db.port");
			this.bootstrap = new DBClientBootstrap(new DBClientHandler(this));
			super.start();
		}
	}
	
	@Override
	public void add(Message message) {
		if (!isEnable()) {
			return;
		}
		super.add(message);
	}

	@Override
	public void doStart() {
		// 向DB服务器注册游戏服务器
		Out.info("开始重新注册DB服务...");
		Packet pc = bootstrap.request(new DBJoinMessage());
		if (pc == null) {
			if (session == null) {
				return;
			}
		} else {
			int status = pc.getInt();
			if (status == 200) {
				registed();
				return;
			}
		}
		Out.info(String.format("无法注册DB服 -> %s:%d", serverHost, serverPort));
		GSystem.waitMills(5000);
		doStart();
	}

	public void registed() {
		Out.info("成功注册到DB服务，开始等待信息发送...");
	}

	@Override
	public void handlePacket(Packet pak) {
		long reqId = pak.getLong();
		response(reqId, pak);
	}

	@Override
	public void ping() {
		add(new DBPingMessage());
	}

	public <T> T get(QueryVo vo, Class<T> cl) {
		List<T> ts = query(vo, cl);
		if (ts != null && !ts.isEmpty()) {
			return ts.get(0);
		}
		return null;
	}

	public <T> List<T> query(QueryVo vo, Class<T> cl) {
		if (!isEnable()) {
			return new ArrayList<>();
		}
		DBQueryMessage dbQuery = new DBQueryMessage(vo);
		Packet response = request(dbQuery);
		List<T> result = new ArrayList<T>();
		if (response != null) {
			byte[] buf = response.getBytes(Prefix.INT);
			if (buf != null) {
				String res = new String(buf, GGlobal.UTF_8);
				result = JSONObject.parseArray(res, cl);
			}
		}
		return result;
	}

	public void onPlayerleave(String playerId) {
		if (isEnable()) {
			DBClient.getInstance().add(new Message() {
				@Override
				protected void write() throws IOException {
					body.writeString(playerId);
				}

				@Override
				public short getType() {
					return DBType.NOTIFY;
				}
			});
		}
	}

}
