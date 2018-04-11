package com.wanniu.game.flee;

/**
 * 大逃杀玩家
 * 
 * @author lxm
 *
 */
public class FleePlayer {
	public String playerId;
	public String playerName;
	public int playerLevel;
	public int playerPro;
	public int force;
	public int rank;
	public int killCount;
	public int scoreChange;
	public boolean isDeath;

	public FleePlayer() {

	}

	public FleePlayer(String playerId, String playerName, int playerLevel, int playerPro, int force, int rank) {
		this.playerId = playerId;
		this.playerName = playerName;
		this.playerLevel = playerLevel;
		this.playerPro = playerPro;
		this.force = force;
		this.rank = rank;
	}

}
