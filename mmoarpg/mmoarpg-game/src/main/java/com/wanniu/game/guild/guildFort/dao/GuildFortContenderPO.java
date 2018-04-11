package com.wanniu.game.guild.guildFort.dao;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.annotation.JSONField;
import com.wanniu.core.util.StringUtil;
import com.wanniu.game.guild.GuildServiceCenter;
import com.wanniu.game.guild.guildFort.GuildFortStatPush;
import com.wanniu.game.player.GlobalConfig;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.poes.GuildPO;

public class GuildFortContenderPO {
	public String guildId = "";
	public String guildName = "";
	public String guildIcon = "";
	public int guildLevel = 0;
	public int attendTimes = 0;
	public int winTimes = 0;
	
	/** key=playerId,value=MemberPO */
	public Map<String,GuildFortMemberPO> members = new HashMap<>();
	public int memberNumber = 0;
	
	public int pickItemNum = 0;
	public int defBuffScore = 0;
	public int defBuff = 0;
	
	public int killMonsterNum = 0;
	public int attBuffScore = 0;
	public int attBuff = 0;
	
	public int killPlayerNum = 0;
	public int killPlayerScore = 0;
	public int killFlagNum = 0;
	public int killFlagScore = 0;
	public int score = 0;
	
	public boolean isWinner = false;
	
	public GuildFortContenderPO() {
		
	}
	
	public GuildFortContenderPO(String guildId) {
		this.guildId = guildId;
		build();
	}
	
	@JSONField(deserialize=false,serialize=false)
	public synchronized  GuildFortContenderPO getContenderPO() {

			build();
			GuildFortContenderPO po = new GuildFortContenderPO();
			
			po.guildId = guildId;
			po.guildName = guildName;
			po.guildIcon = guildIcon;
			po.guildLevel = guildLevel;
			po.attendTimes = attendTimes;
			po.winTimes = winTimes;
			
			po.memberNumber = memberNumber;

			
			po.pickItemNum = pickItemNum;
			po.defBuffScore = defBuffScore;
			po.defBuff = defBuff;
			
			po.killMonsterNum = killMonsterNum;
			po.attBuffScore = attBuffScore;
			po.attBuff = attBuff;
			
			
			po.killPlayerNum = killPlayerNum;
			po.killPlayerScore = killPlayerScore;
			po.killFlagNum = killFlagNum;
			po.killFlagScore = killFlagScore;
			po.score = score;
			po.isWinner = isWinner;
			return po;
	}
	
	@JSONField(deserialize=false,serialize=false)
	public synchronized GuildFortStatPush getPush() {
		GuildFortStatPush po = new GuildFortStatPush();		
		po.memberNumber = members.size();			
		for(GuildFortMemberPO member: members.values()) {
			po.killPlayerNum += member.getKilledPlayerNum();
			po.killFlagNum += member.getKilledFlagNum();
			po.defBuffScore += member.getDefBuffScore();
			po.attBuffScore += member.getAttBuffScore();
			po.score += member.getScore();
		}
		po.defBuff = po.defBuffScore/GlobalConfig.GuildFort_PickAddDefense;
		po.attBuff = po.attBuffScore/GlobalConfig.GuildFort_KillMonAddAttack;
		
		return po;
	}
	
	@JSONField(deserialize=false,serialize=false)
	public synchronized GuildFortContenderPO build() {
		if(StringUtil.isNotEmpty(guildId)) {
			GuildPO guild = GuildServiceCenter.getInstance().getGuild(guildId);
			if(guild!=null) {
				guildName = guild.name;
				guildIcon = guild.icon;
				guildLevel = guild.level;
				attendTimes = guild.getFortInfo().attendTimes;
				winTimes = guild.getFortInfo().winTimes;
				
				memberNumber = members.size();
				pickItemNum = 0;
				killMonsterNum = 0;
				killPlayerNum = 0;
				killFlagNum = 0;
//
//				defBuffScore = 0;
//				attBuffScore = 0;
//				score = 0;
				for(GuildFortMemberPO member: members.values()) {
					pickItemNum += member.getPickItemNum();
					killMonsterNum += member.getKilledMonsterNum();
					killPlayerNum += member.getKilledPlayerNum();
					killFlagNum += member.getKilledFlagNum();
//					defBuffScore += member.getDefBuffScore();
//					attBuffScore += member.getAttBuffScore(); 
//					score += member.getScore();
				}

				killPlayerScore = killPlayerNum * GlobalConfig.GuildFort_KillPoint;
				killFlagScore = killFlagNum * GlobalConfig.GuildFort_DestroyPoint;
				
//				defBuff = defBuffScore/GlobalConfig.GuildFort_PickAddDefense;
//				attBuff = attBuffScore/GlobalConfig.GuildFort_KillMonAddAttack;
			}
		}
		
		return this;
	}
	
	
	/**
	 * if not contains the player,then create a new MemberPO and put it in
	 * @param player
	 * @return
	 */
	@JSONField(deserialize=false,serialize=false)
	public synchronized GuildFortMemberPO getMemberAndPut(WNPlayer player) {
		String playerId = player.getId();
		if(this.members.containsKey(playerId)) {
			return this.members.get(playerId);
		}else {
			GuildFortMemberPO member = new GuildFortMemberPO(playerId,player.getName(),player.getLevel(),player.guildManager.getJob(),player.guildManager.getGuildName());
			this.members.put(playerId, member);
			return member;
		}
	}
	
	@JSONField(deserialize=false,serialize=false)
	public synchronized Collection<GuildFortMemberPO> getMembers(){
		return this.members.values();
	}
	
	@JSONField(deserialize=false,serialize=false)
	public boolean isMember(WNPlayer player) {
		return isMember(player.guildManager.getGuildId());
	}
	
	@JSONField(deserialize=false,serialize=false)
	public boolean isMember(String guildId) {
		if(guildId==null) {
			return false;
		}
		return guildId.equals(this.guildId);
	}
	
	@JSONField(deserialize=false,serialize=false)
	public String getGuildId() {
		return this.guildId;
	}
	
	
	@JSONField(deserialize=false,serialize=false)
	public boolean isWinner() {
		return this.isWinner;
	}

	@JSONField(deserialize=false,serialize=false)
	public void setWinner(boolean isWinner) {
		this.isWinner = isWinner;
	}

	@JSONField(deserialize=false,serialize=false)
	public int getScore() {
		return this.score;
	}
}
