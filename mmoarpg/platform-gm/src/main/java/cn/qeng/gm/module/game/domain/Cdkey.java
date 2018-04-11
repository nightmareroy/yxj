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
package cn.qeng.gm.module.game.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * CDKEY礼包
 */
@Entity
@Table(name = "cdkey")
public class Cdkey {
	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	// 礼包名称
	@Column(name = "name", nullable = false, length = 128)
	private String name;
	// 兑换的开始时间和结束时间
	@Column(name = "start_time", nullable = false)
	private Date startTime;
	@Column(name = "end_time", nullable = false)
	private Date endTime;

	@Column(name = "type", nullable = false)
	private int type;
	@Column(name = "min_level", nullable = false)
	private int minLevel;
	@Column(name = "use_max", nullable = false)
	private int useMax;
	@Column(name = "code_num", nullable = false)
	private int codeNum;
	// 物品
	@Column(name = "item_list", nullable = false, length = 2048)
	private String itemList;
	@Column(name = "create_time", nullable = false)
	private Date createTime;
	@Column(name = "modify_time", nullable = false)
	private Date modifyTime;

	@Column(name = "remaining", nullable = false)
	private int remaining;// 剩余的数量

	@Column(name = "sync_time", nullable = false)
	private Date syncTime;// 剩余的数量同步时间

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getMinLevel() {
		return minLevel;
	}

	public void setMinLevel(int minLevel) {
		this.minLevel = minLevel;
	}

	public int getUseMax() {
		return useMax;
	}

	public void setUseMax(int useMax) {
		this.useMax = useMax;
	}

	public int getCodeNum() {
		return codeNum;
	}

	public void setCodeNum(int codeNum) {
		this.codeNum = codeNum;
	}

	public String getItemList() {
		return itemList;
	}

	public void setItemList(String itemList) {
		this.itemList = itemList;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getModifyTime() {
		return modifyTime;
	}

	public void setModifyTime(Date modifyTime) {
		this.modifyTime = modifyTime;
	}

	public int getRemaining() {
		return remaining;
	}

	public void setRemaining(int remaining) {
		this.remaining = remaining;
	}

	public Date getSyncTime() {
		return syncTime;
	}

	public void setSyncTime(Date syncTime) {
		this.syncTime = syncTime;
	}
}