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
package cn.qeng.gm.module.game.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 批量发送.
 *
 * @author 小流氓(mingkai.zhou@qeng.net)
 */
@Entity
@Table(name = "batch_apply")
public class BatchApply {
	@Id
	@GeneratedValue
	@Column(name = "id")
	private int id;

	@Column(name = "server_id", nullable = false)
	private int serverId;
	@Column(name = "player_id", nullable = false, length = 64)
	private String playerId;
	@Column(name = "player_name", nullable = false, length = 128)
	private String playerName;

	@Column(name = "`title`", nullable = false, length = 128)
	private String title;
	@Column(name = "`content`", nullable = false, length = 512)
	private String content;

	@Column(name = "item_info", nullable = false, length = 2048)
	private String itemInfo;

	@Column(name = "create_time", nullable = false)
	private Date createTime;

	@Column(name = "modify_time", nullable = false)
	private Date modifyTime;

	@Column(name = "state", nullable = false)
	private int state;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getServerId() {
		return serverId;
	}

	public void setServerId(int serverId) {
		this.serverId = serverId;
	}

	public String getPlayerId() {
		return playerId;
	}

	public void setPlayerId(String playerId) {
		this.playerId = playerId;
	}

	public String getPlayerName() {
		return playerName;
	}

	public void setPlayerName(String playerName) {
		this.playerName = playerName;
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

	public String getItemInfo() {
		return itemInfo;
	}

	public void setItemInfo(String itemInfo) {
		this.itemInfo = itemInfo;
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

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}
}