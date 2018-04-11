package com.wanniu.game.guild.guildDungeon;

import java.util.Map;

import com.wanniu.game.item.po.PlayerItemPO;

import pomelo.guild.GuildManagerHandler.DiceInfo;

//工会副本掉落数据
public class GuildDungeonThrowInfo {
	public PlayerItemPO dropItem;
	public int dungeonCount;
	public Map<String, DiceInfo> diceInfo;
	public String mostPointPlayerId;
	public String mostPointPlayerName;
	public int mostPointPlayerPro;
	public int mostPoint;
}
