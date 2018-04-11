package com.wanniu.game.guild.guildFort.dao;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.annotation.JSONField;
import com.wanniu.core.logfs.Out;
import com.wanniu.game.player.GlobalConfig;

public class GuildFortMemberPO{
	/** key=itemId value=num */
	public Map<Integer,Integer> pickedItems = new HashMap<>();
	public int defBuffScore = 0;
	/**	key=monsterId value=num */
	public Map<Integer,Integer> killedMonsters = new HashMap<>();
	public int attBuffScore = 0;
	
	public int killedPlayer = 0;
	public int killedFlag = 0;
	public int score = 0;

	/** key=count,value=fightHurt	 */
	public Map<Integer,Long> fightHurts = new HashMap<>();	
	/** key=count,value=fightCure	 */
	public Map<Integer,Long> fightCures = new HashMap<>();
	
	public String playerName;
	public String playerId;
	public int playerLevel;
	public int guildJob;
	public String guildJobName;
	
	public GuildFortMemberPO() {
		
	}
	
	public GuildFortMemberPO(String playerId,String playerName,int playerLevel,int guildJob,String guildJobName) {
		this.playerId = playerId;
		this.playerName = playerName;
		this.playerLevel = playerLevel;
		this.guildJob = guildJob;
		this.guildJobName = guildJobName;
	}
	
	@JSONField(deserialize=false,serialize=false)
	public int getPickItemNum() {
		int pickItemNum = 0;
		for(Integer num : pickedItems.values()) {
			pickItemNum += num;
		}
		
		return pickItemNum;
	}
	
	@JSONField(deserialize=false,serialize=false)
	public int getKilledMonsterNum() {
		int killedMonsterNum = 0;
		for(Integer num : killedMonsters.values()) {
			killedMonsterNum += num;
		}
		
		return killedMonsterNum;
	}
	
	@JSONField(deserialize=false,serialize=false)
	public int getKilledPlayerNum() {
		return this.killedPlayer;
	}
	
	@JSONField(deserialize=false,serialize=false)
	public int getKilledFlagNum() {
		return this.killedFlag;
	}
	
	@JSONField(deserialize=false,serialize=false)
	public long getFightHurt() {
		long sumHurt = 0;
		for(Long hurt: fightHurts.values()) {
			sumHurt += hurt;
		}
		
		return sumHurt;
	}
	
	@JSONField(deserialize=false,serialize=false)
	public int getDefBuffScore() {
		return defBuffScore;
	}

	@JSONField(deserialize=false,serialize=false)
	public int getAttBuffScore() {
		return attBuffScore;
	}

	@JSONField(deserialize=false,serialize=false)
	public int getScore() {
		return score;
	}

	@JSONField(deserialize=false,serialize=false)
	public long getFightCure() {
		long sumHurt = 0;
		for(Long hurt: fightCures.values()) {
			sumHurt += hurt;
		}
		
		return sumHurt;
	}
	
	@JSONField(deserialize=false,serialize=false)
	public void onPickedItem(int itemId,int score) {
		addBeforePut(pickedItems,itemId,1);
		defBuffScore += score;
	}
	
	@JSONField(deserialize=false,serialize=false)
	public void onKilledMonster(int monsterId,int score) {
		addBeforePut(killedMonsters,monsterId,1);
		attBuffScore += score;
	}
	
	@JSONField(deserialize=false,serialize=false)
	public void onKilledPlayer(int score) {
		killedPlayer++;
		this.score += score;
	}
	
	@JSONField(deserialize=false,serialize=false)
	public void onKilledFlag(int score) {
		killedFlag++;
		this.score += score;
	}
	
	@JSONField(deserialize=false,serialize=false)
	public void onFightHurt(long newHurt,int count) {
		fightHurts.put(count, newHurt);
	}
	
	@JSONField(deserialize=false,serialize=false)
	public void onFightCure(long newHurt,int count) {
		fightCures.put(count, newHurt);
	}
	
	@JSONField(deserialize=false,serialize=false)
	public void addBeforePut(Map<Integer,Integer> data,int key,int value) {
		if(data.containsKey(key)) {
			data.put(key, data.get(key)+value);
		}else {
			data.put(key,value);
		}
	}
}
