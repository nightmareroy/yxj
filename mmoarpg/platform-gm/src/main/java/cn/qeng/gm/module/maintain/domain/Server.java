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
package cn.qeng.gm.module.maintain.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * 服务器信息
 *
 * @author 小流氓(mingkai.zhou@qeng.net)
 */
@Entity
@Table(name = "server")
public class Server {

	// 大区最基本的配置-----------------------------------------
	/** 服务器ID */
	@Id
	@Column(name = "id", nullable = false)
	private int id;
	/** 服务器名称 */
	@Column(name = "server_name", nullable = false, length = 256)
	private String serverName;
	/** 入口id */
	@Column(name = "app_id", nullable = false)
	private int appId;

	// 区服其他的配置-----------------------------------------
	/** 绑定的节点（区） */
	@Column(name = "area_id", nullable = false)
	private int areaId;
	/** 在线上限 */
	@Column(name = "online_limit", nullable = false)
	private int olLimit = 1000;
	/** 服务器显示 */
	@Column(name = "show_state", nullable = false)
	private int showState = 0; // 0：隐藏；1：对内；2：对外
	/** 新服 */
	@Column(name = "is_new", nullable = false)
	private boolean isNew;
	/** 热服 */
	@Column(name = "is_hot", nullable = false)
	private boolean isHot;
	/** 推荐服 */
	@Column(name = "is_recommend", nullable = false)
	private boolean isRecommend;

	/** 服务器IP */
	@Column(name = "ip", length = 32)
	private String ip;

	/** 服务器端口 */
	@Column(name = "port", nullable = false)
	private int port;

	/** 开服日期 */
	@Column(name = "open_date")
	private Date openDate;

	/** 描述 */
	@Column(name = "server_describe", length = 256)
	private String describe;

	/** 对外时间 */
	@Column(name = "external_time")
	private Date externalTime;

	/** 服务器状态 */
	@Transient
	private int state;
	/** 在线数量 */
	@Transient
	private int olCount;

	// 合并后的主服编号，0表示自己就是主服
	@Column(name = "master", nullable = false)
	private int master;

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

	public int getOlLimit() {
		return olLimit;
	}

	public void setOlLimit(int olLimit) {
		this.olLimit = olLimit;
	}

	public int getShowState() {
		return showState;
	}

	public void setShowState(int showState) {
		this.showState = showState;
	}

	public boolean getIsNew() {
		return isNew;
	}

	public void setNew(boolean isNew) {
		this.isNew = isNew;
	}

	public boolean getIsHot() {
		return isHot;
	}

	public void setHot(boolean isHot) {
		this.isHot = isHot;
	}

	public boolean getIsRecommend() {
		return isRecommend;
	}

	public void setRecommend(boolean isRecommend) {
		this.isRecommend = isRecommend;
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

	public Date getOpenDate() {
		return openDate;
	}

	public void setOpenDate(Date openDate) {
		this.openDate = openDate;
	}

	public String getDescribe() {
		return describe;
	}

	public void setDescribe(String describe) {
		this.describe = describe;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public Date getExternalTime() {
		return externalTime;
	}

	public void setExternalTime(Date externalTime) {
		this.externalTime = externalTime;
	}

	public int getOlCount() {
		return olCount;
	}

	public void setOlCount(int olCount) {
		this.olCount = olCount;
	}

	public int getMaster() {
		return master;
	}

	public void setMaster(int master) {
		this.master = master;
	}
}