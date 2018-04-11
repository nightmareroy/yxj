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
package cn.qeng.gm.module.monitor.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 道具监控实体类
 *
 * @author 小流氓(mingkai.zhou@qeng.net)
 */
@Entity
@Table(name = "item_monitor")
public class ItemMonitor {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	// 事件唯一ID
	@Column(name = "eventID", nullable = false, length = 128)
	private String eventID;
	// 事件时间, 格式 yyyy-MM-dd HH:mm:ss"
	@Column(name = "eventTime", nullable = false)
	private Date eventTime;
	// 用户OPENID
	@Column(name = "uID", nullable = false, length = 128)
	private String uID;
	// 角色ID
	@Column(name = "userId", nullable = false)
	private long userId;
	// 角色名
	@Column(name = "name", nullable = false, length = 128)
	private String name;
	// 道具ID
	@Column(name = "itemId", nullable = false)
	private int itemId;
	// 道具数量
	@Column(name = "itemNum", nullable = false)
	private int itemNum;
	// 阀值
	@Column(name = "threshold", nullable = false)
	private long threshold;
	// 角色等级
	@Column(name = "level", nullable = false)
	private int level;
	// VIP等级
	@Column(name = "vIPLevel", nullable = false)
	private int vIPLevel;
	// 状态
	@Column(name = "state", nullable = false)
	private int state;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getEventID() {
		return eventID;
	}

	public void setEventID(String eventID) {
		this.eventID = eventID;
	}

	public Date getEventTime() {
		return eventTime;
	}

	public void setEventTime(Date eventTime) {
		this.eventTime = eventTime;
	}

	public String getuID() {
		return uID;
	}

	public void setuID(String uID) {
		this.uID = uID;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getItemId() {
		return itemId;
	}

	public void setItemId(int itemId) {
		this.itemId = itemId;
	}

	public int getItemNum() {
		return itemNum;
	}

	public void setItemNum(int itemNum) {
		this.itemNum = itemNum;
	}

	public long getThreshold() {
		return threshold;
	}

	public void setThreshold(long threshold) {
		this.threshold = threshold;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public int getvIPLevel() {
		return vIPLevel;
	}

	public void setvIPLevel(int vIPLevel) {
		this.vIPLevel = vIPLevel;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

}
