package com.wanniu.login.vo;

import java.io.IOException;
import java.util.Collection;
import java.util.Set;

import com.alibaba.fastjson.JSONArray;
import com.wanniu.core.GGlobal;
import com.wanniu.core.game.protocol.PomeloPush;
import com.wanniu.core.logfs.Out;
import com.wanniu.login.LoginServer;
import com.wanniu.login.proto.LoginHandler.ServerStatePush;
import com.wanniu.login.whitelist.WhitelistManager;

import io.netty.channel.Channel;

public class ServerVO {

	public int appId; // appId
	public int areaId; // 区Id
	public int logicServerId; // 逻辑服ID
	public String name; // 服务器IP
	public String host; // 服务器IP
	public int port; // 服务器端口
	public int show = ServerShow.OUTER.value; // 显示状态(0：隐藏，1：对内；2，对外)
	public int load = ServerLoad.SMOOTH.value; // 负载状态
	public boolean isNew; // 是否新服
	public boolean isHot; // 是否热服
	public boolean isRecommend; // 是否推荐

	public int olCount; // 在线数
	public int olLimit = 1000; // 上限数

	public String extObj; // 自定义参数
	public Set<Integer> sidList;// 合服列表.
	public long pingtime; // ping时间

	public JSONArray toShowJSON() {
		JSONArray arr = new JSONArray();
		arr.add(logicServerId);
		arr.add(name);
		arr.add(host);
		arr.add(port);
		arr.add(load);
		arr.add(isNew ? 1 : 0);
		arr.add(isHot ? 1 : 0);
		arr.add(isRecommend ? 1 : 0);
		arr.add(extObj);
		return arr;
	}

	public void setLoad(int load) {
		if (this.load != load) {
			this.load = load;
			syncClientState();
		}
	}

	public void setOlCount(int onlineCount) {
		if (onlineCount < 0) {
			this.olCount = 0;
			this.setLoad(ServerLoad.MAINTAIN.value);
			Out.info(this.name, "维护了。。。");
		} else {
			int load = ServerLoad.SMOOTH.value;
			this.olCount = onlineCount;
			if (onlineCount >= olLimit / 2) {
				load = ServerLoad.FULL.value;
			} else if (onlineCount > olLimit / 3) {
				load = ServerLoad.FULL.value;
			} else if (onlineCount > olLimit / 8) {
				load = ServerLoad.BUSY.value;
			}
			if (this.load == ServerLoad.MAINTAIN.value) {
				Out.info(this.name, "连接了。。。");
			}
			this.setLoad(load);
			this.pingtime = System.currentTimeMillis();
		}
	}

	public void setShow(int show) {
		if (show != this.show) {
			this.show = show;
			syncClientState();
		}
	}

	public void syncClientState() {
		Collection<Channel> channels = LoginServer.getInstance().getLoginSessions().values();
		for (Channel channel : channels) {
			channel.writeAndFlush(new PomeloPush() {
				@Override
				protected void write() throws IOException {
					ServerStatePush.Builder push = ServerStatePush.newBuilder();
					String ip = channel.remoteAddress().toString();
					ip = ip.substring(1, ip.indexOf(":"));
					String uid = channel.attr(GGlobal._KEY_USER_ID).get();
					push.setSid(logicServerId);
					if (show == ServerShow.OUTER.value || (show == ServerShow.INNER.value && WhitelistManager.getInstance().isWhiteList(ip, uid))) {
						push.setState(load);
					} else {
						push.setState(ServerLoad.MAINTAIN.value);
					}
					body.writeBytes(push.build().toByteArray());
				}

				@Override
				public String getRoute() {
					return "login.loginPush.serverStatePush";
				}
			});
		}
	}

	public String toString() {
		return this.appId + " : " + this.logicServerId + " - " + this.name + " ->　" + this.host + ":" + this.port;
	}

}
