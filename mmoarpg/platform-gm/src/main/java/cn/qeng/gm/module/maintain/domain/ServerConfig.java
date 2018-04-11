/*
 * Copyright © 2017 qeng.cn All Rights Reserved.
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
package cn.qeng.gm.module.maintain.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 区服配置
 *
 * @author 周明凯(zhoumingkai@qeng.cn)
 */
@Entity
@Table(name = "server_config")
public class ServerConfig {
	/** 服务器ID */
	@Id
	@Column(name = "id", nullable = false)
	private int id;
	/** 服务器名称 */
	@Column(name = "name", nullable = false, length = 256)
	private String serverName;
	/** 入口id */
	@Column(name = "app_id", nullable = false)
	private int appId;
	/** 绑定的节点（区） */
	@Column(name = "area_id", nullable = false)
	private int areaId;

	@Column(name = "template", nullable = false, length = 64)
	private String template;

	// 游戏区所在机器IP
	@Column(name = "game_host", nullable = false, length = 32)
	private String gameHost;

	// 游戏服公网IP地址
	@Column(name = "pubhost", nullable = false, length = 256)
	private String pubhost;
	// 游戏服端口号
	@Column(name = "port", nullable = false)
	private int port;
	// 是否开启测试服功能
	@Column(name = "debug", nullable = false)
	private boolean debug;

	// 本地Redis
	@Column(name = "redis_host", nullable = false, length = 32)
	private String redisHost;
	@Column(name = "redis_port", nullable = false)
	private int redisPort;
	@Column(name = "redis_password")
	private String redisPassword;
	@Column(name = "redis_index", nullable = false)
	private int redisIndex;

	// 战斗服地址
	@Column(name = "battle_host", nullable = false, length = 32)
	private String battleHost;
	@Column(name = "battle_fast_port", nullable = false)
	private int battleFastPort;
	@Column(name = "battle_ice_port", nullable = false)
	private int battleIcePort;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getServerName() {
		return serverName;
	}

	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

	public int getAppId() {
		return appId;
	}

	public void setAppId(int appId) {
		this.appId = appId;
	}

	public int getAreaId() {
		return areaId;
	}

	public void setAreaId(int areaId) {
		this.areaId = areaId;
	}

	public String getPubhost() {
		return pubhost;
	}

	public void setPubhost(String pubhost) {
		this.pubhost = pubhost;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public boolean isDebug() {
		return debug;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	public String getRedisHost() {
		return redisHost;
	}

	public void setRedisHost(String redisHost) {
		this.redisHost = redisHost;
	}

	public int getRedisPort() {
		return redisPort;
	}

	public void setRedisPort(int redisPort) {
		this.redisPort = redisPort;
	}

	public String getRedisPassword() {
		return redisPassword;
	}

	public void setRedisPassword(String redisPassword) {
		this.redisPassword = redisPassword;
	}

	public int getRedisIndex() {
		return redisIndex;
	}

	public String getTemplate() {
		return template;
	}

	public void setTemplate(String template) {
		this.template = template;
	}

	public void setRedisIndex(int redisIndex) {
		this.redisIndex = redisIndex;
	}

	public String getBattleHost() {
		return battleHost;
	}

	public void setBattleHost(String battleHost) {
		this.battleHost = battleHost;
	}

	public int getBattleFastPort() {
		return battleFastPort;
	}

	public void setBattleFastPort(int battleFastPort) {
		this.battleFastPort = battleFastPort;
	}

	public int getBattleIcePort() {
		return battleIcePort;
	}

	public void setBattleIcePort(int battleIcePort) {
		this.battleIcePort = battleIcePort;
	}

	public String getGameHost() {
		return gameHost;
	}

	public void setGameHost(String gameHost) {
		this.gameHost = gameHost;
	}
}