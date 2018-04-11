package com.wanniu.game.poes;

import java.util.Date;

import com.wanniu.core.game.entity.GEntity;
import com.wanniu.game.DBField;

/**
 * guildmember 实体类 Thu Feb 23 16:50:01 CST 2017 jjr
 */
public class GuildMemberPO extends GEntity {
	
	@DBField(isPKey = true, fieldType = "char", size = 36)
	public String playerId;
	
	public String guildId;

	public String name;
	public int pro;

	public int job;
	public long lastContributeValue;
	public Date createTime;
	public Date lastContributeTime;

	public GuildMemberPO() {
	}
	
}
