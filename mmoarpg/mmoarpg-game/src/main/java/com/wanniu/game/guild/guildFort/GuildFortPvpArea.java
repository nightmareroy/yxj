package com.wanniu.game.guild.guildFort;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wanniu.core.logfs.Out;
import com.wanniu.game.area.Area;
import com.wanniu.game.area.DamageHealVO;
import com.wanniu.game.common.Const;
import com.wanniu.game.common.Const.PlayerBtlData;
import com.wanniu.game.common.msg.MessagePush;
import com.wanniu.game.guild.guildFort.dao.GuildFortContenderPO;
import com.wanniu.game.player.PlayerUtil;
import com.wanniu.game.player.WNPlayer;

public class GuildFortPvpArea extends Area {
	private GuildFort fort;
	private Map<String, Integer> playerEnterCount = new ConcurrentHashMap<>();
	
	
	public GuildFortPvpArea(JSONObject opts) {
		super(opts);
		int fortId = opts.getIntValue("fortId");
		this.fort = GuildFortCenter.getInstance().getFort(fortId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.wanniu.game.area.Area#onBattleReport(java.util.List)
	 */
	public void onBattleReport(List<DamageHealVO> datas) {
		if(fort.isBattleOver()) {
			return;
		}
		if (datas != null && !datas.isEmpty()) {
			for (DamageHealVO heal : datas) {
				if (heal.TotalDamage <= 0) {
					Out.error("Damage onBattleReport value exception, val=",heal.TotalDamage);
				}
				
				if(heal.TotalHealing<=0) {
					Out.error("Healing onBattleReport value exception, val=",heal.TotalHealing);
				}
				
				WNPlayer player = this.getPlayer(heal.PlayerUUID);
				if(player==null) {
					Out.warn("player null onBattleReport");
					continue;
				}
				int count = getEnterCount(player.getId());
				fort.onBattleReport(player, heal,count);
			}
		}
	}

	
	/* (non-Javadoc)
	 * @see com.wanniu.game.area.Area#onPlayerEntered(com.wanniu.game.player.WNPlayer)
	 */
	public void onPlayerEntered(WNPlayer player) {
		super.onPlayerEntered(player);
		fort.onPlayerEntered(player);
		this.addEnterCount(player);
	}
	
	private void addEnterCount(WNPlayer player) {
		Integer count = playerEnterCount.get(player.player.id);
		if (count == null) {
			playerEnterCount.put(player.player.id, 1);
		} else {
			playerEnterCount.put(player.player.id, count + 1);
		}
	}
	
	private int getEnterCount(String playerId) {
		Integer count = playerEnterCount.get(playerId);
		if (count == null) {
			return 0;
		} else {
			return count;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.wanniu.game.area.Area#setForce(com.wanniu.game.player.WNPlayer)
	 */
	@Override
	public void setForce(WNPlayer player) {
		if(fort.isDefenserMember(player.guildManager.getGuildId())) {
			player.setForce(Const.AreaForce.FORCEA.value);
		}else {
			player.setForce(Const.AreaForce.FORCEB.value);
		}
	}
	
	/* (non-Javadoc)
	 * @see com.wanniu.game.area.Area#noCloseIfNoPlayer()
	 */
	protected boolean noCloseIfNoPlayer() {
		return true;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.wanniu.game.area.Area#onPlayerDeadByPlayer(com.wanniu.game.player.
	 * WNPlayer, com.wanniu.game.player.WNPlayer, float, float)
	 */
	public void onPlayerDeadByPlayer(WNPlayer deadPlayer, WNPlayer hitPlayer, float x, float y) {
		super.onPlayerDeadByPlayer(deadPlayer, hitPlayer, x, y);
		fort.onKilledPlayer(hitPlayer);
	}

	@Override
	public void onMonsterDead(int monsterId, int level, float x, float y, int attackType, String refreshPoint,
			WNPlayer player, JSONArray teamSharedIdList, JSONArray atkAssistantList) {
		super.onMonsterDead(monsterId, level, x, y, attackType, refreshPoint, player, teamSharedIdList,
				atkAssistantList);
		fort.onKilledFlag(player, monsterId);
	}

	/**
	 * Add attributes to the designated contender's members
	 * @param contender
	 */
	public void onAddBuff(GuildFortContenderPO contender) {
		for(String pid : this.actors.keySet()) {
			WNPlayer player = PlayerUtil.getOnlinePlayer(pid);
			if(player!=null && player.guildManager.guild!=null) {
				if(player.guildManager.getGuildId().equals(contender.guildId)) {//Only same guild member need update attribute
					Map<String, Integer> map = new HashMap<>();
					map.put(PlayerBtlData.Attack.toString(), contender.attBuff);
					map.put(PlayerBtlData.Def.toString(), contender.defBuff);
					player.btlDataManager.calFinalData(map,true);
					player.refreshBattlerServerEffect(false);
				}
			}
		}
	}
	
	/**
	 *  Push report message to all players those in this area
	 * @param defenserMessage
	 * @param attackerMessage
	 */
	public void pushReport(MessagePush defenserMessage,MessagePush attackerMessage) {
		for(String pid : this.actors.keySet()) {
			WNPlayer player = PlayerUtil.getOnlinePlayer(pid);
			if(player!=null) {
				if(fort.isDefenserMember(player.guildManager.getGuildId())) {
					player.receive(defenserMessage);
				}else {
					player.receive(attackerMessage);
				}
			}
		}
	}
}
