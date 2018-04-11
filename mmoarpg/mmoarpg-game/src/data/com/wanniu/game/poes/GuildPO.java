package com.wanniu.game.poes;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.annotation.JSONField;
import com.wanniu.core.game.entity.GEntity;
import com.wanniu.game.DBField;
import com.wanniu.game.guild.GuildAllBlob;

/**
 * guild 实体类 Thu Feb 23 14:25:42 CST 2017 jjr
 */
public class GuildPO extends GEntity {
	@DBField(isPKey = true, fieldType = "varchar", size = 50)
	public String id;
	public int logicServerId;
	public String icon;
	public String name;
	public int level;
	public String presidentId;
	public int presidentPro;
	public String presidentName;
	public String qqGroup;
	public long fund; // 公会基金
	public long sumFund;
	public long exp; // 公会声望
	public int entryLevel;
	public int entryUpLevel;
	public int guildMode;
	public String notice;
	public String logicName;
	public String jobName;
	public int job;

	public Date createTime;
	public Date changeNameTime;

	public Date kickTime;
	public int kickCount;

	public Map<Integer, String> officeNames;
	public GuildAllBlob allBlobData;
	public InspirePO inspire;// 攻击鼓舞信息
	public InspirePO defInspire;// 防御鼓舞信息
	
	public GuildFortInfoPO fortInfo;//据点战相关的信息

	@JSONField(serialize = false)
	@DBField(include = false)
	public boolean modify;
	
	// 仙盟竞拍收益总值
	public int auctionBonus;

	public GuildPO() {
		officeNames = new HashMap<Integer, String>();
		allBlobData = new GuildAllBlob();
		changeNameTime = new Date(0);
		kickTime = new Date(0);
	}
	
	public GuildFortInfoPO getFortInfo() {
		synchronized (this) {
			if(fortInfo == null) {
				fortInfo = new GuildFortInfoPO();
			}
		}
		
		return this.fortInfo;
	}
	
}
