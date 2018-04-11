package com.wanniu.core.game.entity;

import java.util.Date;

import com.wanniu.core.GGame;
import com.wanniu.core.GGlobal;
import com.wanniu.core.tcp.protocol.Message;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;

/**
 * 功能描述：玩家的基类
 * 
 * @author agui
 */
public abstract class GPlayer {

	public static volatile long Buf = 0;
	public static volatile long Count = 0;
	
	protected Channel session;
	protected byte state = 0;//0首次进入场景 1非首次进入场景

	private Date logoutDate;
	public long modifyTime;
	
	public GPlayer() {
		modifyTime = System.currentTimeMillis();
	}

	public void bind(Channel session) {
		this.session = session;
		session.attr(GGlobal.__KEY_PLAYER).set(this);
		GGame.getInstance().addPlayer(this);
	}

	/** 功能描述：获取玩家编号(应对应于角色唯一标识KEY) */
	public abstract String getId();

	/** 获取玩家状态 */
	public byte getState() {
		return this.state;
	}

	public void setState(byte state) {
		this.state = state;
	}

	/** 功能描述：绑定的客户端会话 */
	public Channel getSession() {
		return this.session;
	}

	public static class Watcher {
		public int count;
		public long buf;
		public void inrc(ByteBuf buf) {
			if(!GGame.MONITOR) return;
			this.buf += buf.readableBytes();
			this.count++;
		}
	}
	
	public final Watcher watcher = new Watcher();

	public void endWatch() {
		if (GGame.MONITOR) {
			Buf += watcher.buf;
			Count += watcher.count;
		}
	}
	
	/** 功能描述：玩家接收广播消息 */
	public void receive(Message msg) {
		if (session != null) {
			msg.getContent();
			watcher.inrc(msg.getContent());
			session.writeAndFlush(msg);
		}
	}

	/** 功能描述：单个玩家接收消息 */
	public void write(Message msg) {
		if (session != null) {
			watcher.inrc(msg.getContent());
			session.writeAndFlush(msg.getContent());
		}
	}

	/** 登出时间 */
	public Date getLogoutTime() {
		return logoutDate;
	}

	public void setLogoutTime(Date date) {
		this.logoutDate = date;
	}

	/**
	 * 功能描述：处理退出游戏世界
	 * 
	 * @param self
	 *            是否主动退出
	 */
	public void doLogout(boolean self) {
		setState((byte)0);//重置首次状态
		Date date = new Date();
		this.logoutDate = date;
		setLogoutTime(date);
		GGame.getInstance().removePlayer(this);
		if (!self) {
			GGame.getInstance().addWaitPlayer(this);
		}
		onLogout(self);
		sync();
	}
	
	public void bindBattleServer(String serverId) {
		
	}
	
	/**
	 * 功能描述：退出游戏世界的逻辑处理
	 * 
	 * @param self
	 *            是否主动退出
	 */
	public abstract void onLogout(boolean self);

	/** 功能描述：用户ID，用户中心的唯一标识ID */
	public abstract String getUid();

	/** 功能描述：获取所在战斗服ID */
	public abstract String getBattleServerId();

	/** 获取名称 */
	public abstract String getName();

	/** 数据安全持久化 */
	public abstract void sync();

	/** 释放玩家的相关引用（停服时也会触发） */
	public abstract void free();

}
