package com.wanniu.game.guild.guildFort;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wanniu.core.logfs.Out;
import com.wanniu.game.area.Area;
import com.wanniu.game.common.msg.MessagePush;
import com.wanniu.game.player.PlayerUtil;
import com.wanniu.game.player.WNPlayer;

public class GuildFortPveArea extends Area{
	private GuildFort fort;
	
	
	public GuildFortPveArea(JSONObject opts) {
		super(opts);
		int fortId = opts.getIntValue("fortId");
		this.fort = GuildFortCenter.getInstance().getFort(fortId);
	}

	
	/* (non-Javadoc)
	 * @see com.wanniu.game.area.Area#onPlayerEntered(com.wanniu.game.player.WNPlayer)
	 */
	public void onPlayerEntered(WNPlayer player) {
		super.onPlayerEntered(player);
		fort.onPlayerEntered(player);
	}
	
	/* (non-Javadoc)
	 * @see com.wanniu.game.area.Area#noCloseIfNoPlayer()
	 */
	protected boolean noCloseIfNoPlayer() {
		return true;
	}
	
	/* (non-Javadoc)
	 * @see com.wanniu.game.area.Area#onInterActiveItem(com.wanniu.game.player.WNPlayer, int, int)
	 */
	public void onInterActiveItem(WNPlayer player, int objId, int itemId) {
		super.onInterActiveItem(player, objId, itemId);
		if (fort != null) {
			fort.onPickedItem(player,itemId);
		}else {
			Out.warn("contender null onInterActiveItem: " + player.getId() + " " + itemId);
		}
	}
	
	
	@Override
	public void onMonsterDead(int monsterId, int level, float x, float y, int attackType, String refreshPoint, WNPlayer player, JSONArray teamSharedIdList, JSONArray atkAssistantList) {
		super.onMonsterDead(monsterId, level, x, y, attackType, refreshPoint, player, teamSharedIdList, atkAssistantList);
		if (fort != null) {
			fort.onKilledMonster(player,monsterId);
		}else {
			Out.warn("contender null onMonsterDead: " + player.getId() + " " + monsterId);
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
