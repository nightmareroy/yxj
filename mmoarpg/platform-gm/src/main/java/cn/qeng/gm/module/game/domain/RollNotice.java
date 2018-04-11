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
import javax.persistence.Lob;
import javax.persistence.Table;

/**
 * 滚动公告实体类
 *
 * @author 小流氓(mingkai.zhou@qeng.net)
 */
@Entity
@Table(name = "roll_notice")
public class RollNotice {

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	// 区服信息.
	@Lob
	@Column(name = "sids", nullable = false)
	private String sids;
	@Column(name = "sids_length", nullable = false)
	private int sidsLength;

	// 开始时间
	@Column(name = "start_time", nullable = false)
	private Date startTime;
	// 结束时间
	@Column(name = "end_time", nullable = false)
	private Date endTime;
	// 公告滚动时间间隔
	@Column(name = "`interval`", nullable = false)
	private int interval;
	// 公告内容
	@Column(name = "content", nullable = false, length = 4096)
	private String content;

	@Column(name = "create_time", nullable = false)
	private Date createTime;
	@Column(name = "modify_time", nullable = false)
	private Date modifyTime;

	// 发布人的账号和名称
	@Column(name = "username", nullable = false, length = 128)
	private String username;
	@Column(name = "name", nullable = false, length = 256)
	private String name;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getSids() {
		return sids;
	}

	public void setSids(String sids) {
		this.sids = sids;
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

	public int getInterval() {
		return interval;
	}

	public void setInterval(int interval) {
		this.interval = interval;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
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

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getSidsLength() {
		return sidsLength;
	}

	public void setSidsLength(int sidsLength) {
		this.sidsLength = sidsLength;
	}
}