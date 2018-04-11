package com.wanniu.vo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import com.wanniu.GServer;
import com.wanniu.tcp.protocol.Message;

import io.netty.channel.Channel;

public class ServerVo {
	
	public static enum Icon {
		DEFAULT, OPEN, CLOSE
	}

	public static enum State {
		MAINTAIN(0, "维护"), 
		SMOOTH(1, "流畅"), 
		BUSY(2, "繁忙"), 
		FULL(3, "爆满");
		
		public final int value;
		
		State(int state, String desc) {
			this.value = state;
		}
	}

	/** 入口id */
	private int appId = GServer.__APP_ID;
	
	/** 入口id */
	private int logicServerId;
	/** 服务id */
	private int serverId;

	/** 文本 */
	private String serverName;

	/** 在线数量 */
	private int olCount;
	/** 在线上限 */
	private int olLimit = 1000;

	/** 绑定的节点（区） */
	private int areaId;
	
	/** 服务器状态 */
	private int state;
	/** 服务器显示 */
	private int show = 2; // 0：隐藏；1：对内；2：对外

	/** 新服 */
	private boolean isNew;
	/** 热服 */
	private boolean isHot;
	/** 推荐服 */
	private boolean isRecommend;
	
	/** 子节点 */
	public List<ServerVo> children = new ArrayList<ServerVo>(2);

	/** 状态 */
	private Icon iconCls = Icon.DEFAULT;
	
	/** 服务器IP */
	private String ip;

	/** 服务器端口 */
	private int port;
	
	/** 开服时间 */
	private Date openDate;

	/** 描述 */
	private String desc;
	
	public ServerVo(String name) {
		this.serverName = name;
	}
	
	public ServerVo(int id, String name) {
		this.logicServerId = id;
		this.serverName = name;
	}

	public ServerVo(int id, String name, int areaId) {
		this.logicServerId = id;
		this.serverName = name;
		this.areaId = areaId;
	}
	
	@JSONField(name="id")
	public int getLogicServerId() {
		return logicServerId;
	}

	public void setLogicServerId(int serverId) {
		this.logicServerId = serverId;
	}

	@JSONField(name="text")
	public String getServerName() {
		return serverName;
	}

	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

	public String getIconCls() {
		switch(iconCls) {
			case OPEN: {
				return "icon-ok";
			}
			case CLOSE: {
				return "icon-no";
			}
			default: {
				return null;
			}
		}
	}

	@JSONField(serialize=false)
	public int getOlCount() {
		return olCount;
	}

	public void setOlCount(int olCount) {
		this.olCount = olCount;
		if (olCount < 0) {
			state = 0;
		} else if (olCount >= olLimit / 2) {
			state = 3;
		} else if (olCount > olLimit / 3) {
			state = 3;
		} else if (olCount > olLimit / 8) {
			state = 2;
		} else {
			state = 1;
		}
		setIconCls(state == 0 ? Icon.CLOSE : Icon.OPEN);
	}

	@JSONField(serialize=false)
	public int getAppId() {
		return appId;
	}

	public void setAppId(int appId) {
		this.appId = appId;
	}

	@JSONField(serialize=false)
	public int getAreaId() {
		return areaId;
	}

	public void setAreaId(int areaId) {
		this.areaId = areaId;
	}

	@JSONField(serialize=false)
	public int getServerId() {
		return serverId;
	}

	public void setServerId(int serverId) {
		this.serverId = serverId;
	}

	public void setIconCls(Icon icon) {
		this.iconCls = icon;
	}

	@JSONField(serialize=false)
	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	@JSONField(serialize=false)
	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	@JSONField(serialize=false)
	public Date getOpenDate() {
		return openDate;
	}

	public void setOpenDate(Date openDate) {
		this.openDate = openDate;
	}

	@JSONField(serialize=false)
	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	@JSONField(serialize=false)
	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	@JSONField(serialize=false)
	public boolean isNew() {
		return isNew;
	}

	public void setNew(boolean isNew) {
		this.isNew = isNew;
	}

	@JSONField(serialize=false)
	public boolean isHot() {
		return isHot;
	}

	public void setHot(boolean isHot) {
		this.isHot = isHot;
	}

	@JSONField(serialize=false)
	public boolean isRecommend() {
		return isRecommend;
	}

	public void setRecommend(boolean isRecommend) {
		this.isRecommend = isRecommend;
	}

	@JSONField(serialize=false)
	public int getOlLimit() {
		return olLimit;
	}

	public void setOlLimit(int olLimit) {
		this.olLimit = olLimit;
	}

	@JSONField(serialize=false)
	public int getShow() {
		return show;
	}

	public void setShow(int show) {
		this.show = show;
	}

	public boolean request(long key, short op, String json) {
		Channel channel = GServer.getInstance().getChannel(this.logicServerId);
		if (channel != null) {
			channel.writeAndFlush(new Message() {
				@Override
				protected void write() throws IOException {
					body.writeLong(key);
					body.writeShort(op);
					body.writeString(json);
				}

				@Override
				public short getType() {
					return 0xABC;
				}
			}.getContent());
			return true;
		}
		return false;
	}
	
	public boolean write(Message message) {
		Channel channel = GServer.getInstance().getChannel(this.logicServerId);
		if (channel != null) {
			channel.writeAndFlush(message.getContent());
			return true;
		}
		return false;
	}
	
	public JSONObject toList() {
		JSONObject json = new JSONObject();
		json.put("id", this.logicServerId);
		json.put("serverId", this.serverId);
		json.put("name", this.serverName);
		json.put("areaId", this.areaId);
		json.put("host", this.port > 0 ? this.ip + ":" + this.port : "");
		json.put("openDate", this.openDate);
		json.put("desc", this.desc);
		json.put("olCount", this.olCount);
		json.put("state", this.state);
		json.put("isNew", this.isNew ? 1 : 0);
		json.put("isHot", this.isHot ? 1 : 0);
		json.put("isRecommend", this.isRecommend ? 1 : 0);
		json.put("olLimit", this.olLimit);
		json.put("show", this.show);
		return json;
	}
	
}
