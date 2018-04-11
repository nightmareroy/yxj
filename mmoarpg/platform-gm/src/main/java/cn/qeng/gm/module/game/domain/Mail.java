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
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import org.springframework.data.annotation.Transient;

import com.alibaba.fastjson.JSON;

/**
 * 补偿相关道具物品实体类
 *
 * @author 小流氓(mingkai.zhou@qeng.net)
 */
@Entity
@Table(name = "mail")
public class Mail {
	// id
	@Id
	@GeneratedValue
	@Column(name = "id")
	private int id;
	// 区服
	@Lob
	@Column(name = "sid_list", nullable = false)
	private String sidList;

	@Column(name = "sid_list_size", nullable = false)
	private int sidListSize;

	// 最小等级
	@Column(name = "min_level", nullable = false)
	private int minLevel;
	// 最晚创角时间
	@Column(name = "createRoleTime", nullable = false)
	private Date createRoleTime;

	// 邮件发送类型
	@Column(name = "mail_type", nullable = false)
	private int mailType;
	// 邮件发送的对象
	@Column(name = "player_id", nullable = false, length = 512)
	private String playerId;
	@Column(name = "`title`", nullable = false, length = 128)
	private String title;
	// 正文
	@Column(name = "`content`", nullable = false, length = 512)
	private String content;
	// 申请原因
	@Column(name = "reason", nullable = false, length = 256)
	private String reason;
	// 物品
	@Column(name = "item_list", nullable = false, length = 2048)
	private String itemList;
	// 申请人
	@Column(name = "apply_username", nullable = false, length = 128)
	private String applyUsername;
	@Column(name = "apply_name", nullable = false, length = 256)
	private String applyName;
	// 时间
	@Column(name = "create_time", nullable = false)
	private Date createTime;
	// 审批人
	@Column(name = "audit_username", length = 128)
	private String auditUsername;
	@Column(name = "audit_name", length = 256)
	private String auditName;
	// 申请批发状态
	@Column(name = "state", nullable = false)
	private int state;
	// 审批后的结果
	@Lob
	@Column(name = "result")
	private String result;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getSidList() {
		return sidList;
	}

	public void setSidList(String sidList) {
		this.sidList = sidList;
	}

	public int getSidListSize() {
		return sidListSize;
	}

	public void setSidListSize(int sidListSize) {
		this.sidListSize = sidListSize;
	}

	public int getMinLevel() {
		return minLevel;
	}

	public void setMinLevel(int minLevel) {
		this.minLevel = minLevel;
	}

	public Date getCreateRoleTime() {
		return createRoleTime;
	}

	public void setCreateRoleTime(Date createRoleTime) {
		this.createRoleTime = createRoleTime;
	}

	public int getMailType() {
		return mailType;
	}

	public void setMailType(int mailType) {
		this.mailType = mailType;
	}

	public String getPlayerId() {
		return playerId;
	}

	public void setPlayerId(String playerId) {
		this.playerId = playerId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getItemList() {
		return itemList;
	}

	public void setItemList(String itemList) {
		this.itemList = itemList;
	}

	public String getApplyUsername() {
		return applyUsername;
	}

	public void setApplyUsername(String applyUsername) {
		this.applyUsername = applyUsername;
	}

	public String getApplyName() {
		return applyName;
	}

	public void setApplyName(String applyName) {
		this.applyName = applyName;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getAuditUsername() {
		return auditUsername;
	}

	public void setAuditUsername(String auditUsername) {
		this.auditUsername = auditUsername;
	}

	public String getAuditName() {
		return auditName;
	}

	public void setAuditName(String auditName) {
		this.auditName = auditName;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	@Transient
	public List<ItemInfo> getItemListx() {
		return JSON.parseArray(itemList, ItemInfo.class);
	}
}