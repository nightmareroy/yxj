/*
 * Copyright © 2016 qeng.cn All Rights Reserved.
 * 
 * 感谢您加入清源科技，不用多久，您就会升职加薪、当上总经理、出任CEO、迎娶白富美、从此走上人生巅峰
 * 除非符合本公司的商业许可协议，否则不得使用或传播此源码，您可以下载许可协议文件：
 * 
 * 		http://www.noark.xyz/qeng/LICENSE
 *
 * 1、未经许可，任何公司及个人不得以任何方式或理由来修改、使用或传播此源码;
 * 2、禁止在本源码或其他相关源码的基础上发展任何派生版本、修改版本或第三方版本;
 * 3、无论你对源代码做出任何修改和优化，版权都归清源科技所有，我们将保留所有权利;
 * 4、凡侵犯清源科技相关版权或著作权等知识产权者，必依法追究其法律责任，特此郑重法律声明！
 */
package cn.qeng.gm.module.maintain.service.result;

import java.util.ArrayList;
import java.util.List;

/**
 * 区服数据类
 *
 * @author 小流氓(mingkai.zhou@qeng.net)
 */
public class ServerListInfo {
	private List<ServerInfo> list = new ArrayList<>();

	public List<ServerInfo> getList() {
		return list;
	}

	public void setList(List<ServerInfo> list) {
		this.list = list;
	}

	@Override
	public String toString() {
		return "ServerListInfo [list=" + list + "]";
	}

	// [{\"id\":1,\"areaName\":\"游戏服1\",\"ip\":\"192.168.23.11\",\"port\":5000,\"gsPort\":0,\"httpPort\":9088,\"onlineMax\":5000,\"op\":2}]
	public static class ServerInfo {
		private int id;
		private String areaName;
		private String ip;
		private int port;
		private int gsPort;
		private int httpPort;
		private int onlineMax;
		private int op;

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public String getAreaName() {
			return areaName;
		}

		public void setAreaName(String areaName) {
			this.areaName = areaName;
		}

		public String getIp() {
			return ip;
		}

		public void setIp(String ip) {
			this.ip = ip;
		}

		public int getPort() {
			return port;
		}

		public void setPort(int port) {
			this.port = port;
		}

		public int getGsPort() {
			return gsPort;
		}

		public void setGsPort(int gsPort) {
			this.gsPort = gsPort;
		}

		public int getHttpPort() {
			return httpPort;
		}

		public void setHttpPort(int httpPort) {
			this.httpPort = httpPort;
		}

		public int getOnlineMax() {
			return onlineMax;
		}

		public void setOnlineMax(int onlineMax) {
			this.onlineMax = onlineMax;
		}

		public int getOp() {
			return op;
		}

		public void setOp(int op) {
			this.op = op;
		}

		@Override
		public String toString() {
			return "ServerInfo [id=" + id + ",areaName=" + areaName + ",ip=" + ip + ",port=" + port + ",gsPort=" + gsPort + ",httpPort=" + httpPort + ",onlineMax=" + onlineMax + ",op=" + op + "]";
		}
	}
}
