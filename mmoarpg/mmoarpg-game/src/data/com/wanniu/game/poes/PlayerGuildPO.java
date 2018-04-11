package com.wanniu.game.poes;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.wanniu.core.game.entity.GEntity;
import com.wanniu.game.DBTable;
import com.wanniu.game.common.Const;
import com.wanniu.game.common.Table;
import com.wanniu.game.guild.guildTech.GuildTechSkill;

@DBTable(Table.player_guild)
public class PlayerGuildPO extends GEntity {

	public int contribution;
	public Map<String, Integer> totalContributionMap;
	public Map<Integer, Integer> contributeTimesMap;
	public Date lastSelfExitTime;
	public Date refreshTime;
	public int depositCount;
	public int blessCount;
	public int[] blessRecState;
	public String joinDungeonGuildId;
	public Date joinDungeonTime;
	public int buffTime; // buff有效时间
	public List<Integer> buffIds;  // 个人祈福buff
	
	public Map<Integer, GuildTechSkill> skills;
	public List<Integer> boughtList;

	public PlayerGuildPO() {
		totalContributionMap = new HashMap<String, Integer>();
		contributeTimesMap = new HashMap<Integer, Integer>();
		lastSelfExitTime = new Date(0);
		joinDungeonTime = new Date(0);
		refreshTime = new Date(0);
		buffIds = new ArrayList<Integer>();
		blessRecState = new int[Const.NUMBER_MAX.GUILD_FINISHED_MAX];
		for (int i = 0; i < blessRecState.length; i++) {
			blessRecState[i] = Const.EVENT_GIFT_STATE.NOT_RECEIVE.getValue();
		}
		skills = new HashMap<Integer, GuildTechSkill>(); 
		boughtList = new ArrayList<Integer>();
	}

}
