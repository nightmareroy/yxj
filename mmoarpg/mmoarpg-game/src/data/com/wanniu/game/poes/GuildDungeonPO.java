package com.wanniu.game.poes;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.wanniu.core.game.entity.GEntity;
import com.wanniu.game.DBField;
import com.wanniu.game.guild.guildDungeon.GuildDungeonRecord;
import com.wanniu.game.guild.guildDungeon.GuildDungeonThrowInfo;

import pomelo.guild.GuildManagerHandler.RankInfo;

/**
 * guildDungeon 实体类 Thu Feb 23 19:58:10 CST 2017 jjr
 */
public class GuildDungeonPO extends GEntity {
	@DBField(isPKey = true, fieldType = "varchar", size = 50)
	public String id;
	public int openTimesToday;
	public int openState;
	public int bReward;
	public int currPassedCount;
	public int totalPassedCount;
	public Date dungeonPassedTime;
	public Date openTime;
	public Map<Integer, GuildDungeonRecord> dungeonRecord;
	public String instanceId;
	public String serverId;
	public ArrayList<GuildDungeonThrowInfo> throwInfo;
	public Map<Integer, ArrayList<String>> damagePlayer;
	public ArrayList<RankInfo> damageRankInfo;
	public ArrayList<RankInfo> healRankInfo;
	public int enterState;

	public GuildDungeonPO() {
		this.id = "";
		dungeonPassedTime = new Date(0);
		openTime = new Date(0);
		dungeonRecord = new HashMap<Integer, GuildDungeonRecord>();
		instanceId = "";
		serverId = "";
		throwInfo = new ArrayList<GuildDungeonThrowInfo>();
		damagePlayer = new HashMap<Integer, ArrayList<String>>();
		damageRankInfo = new ArrayList<RankInfo>();
		healRankInfo = new ArrayList<RankInfo>();
	}

}
