package com.wanniu.game.five2Five;

/**
 * @author wanghaitao
 *
 */
public class Five2FivePlayerResultInfoVo {
	public String playerId;

	public String playerName;

	public int playerLevel;

	public int playerPro;

	public int killCount;

	public int hurt;// 输出伤害

	public int treatMent;// 治疗

	public int isMvp;

	public int deadCount;

	public int resultA;// A队的胜负结果

	public Five2FivePlayerResultInfoVo() {

	}

	public Five2FivePlayerResultInfoVo(String playerId, String playerName, int playerLevel, int playerPro, int killCount, int hurt, int treatMent, int isMvp, int deadCount) {
		this.playerId = playerId;
		this.playerName = playerName;
		this.playerLevel = playerLevel;
		this.playerPro = playerPro;
		this.killCount = killCount;
		this.hurt = hurt;
		this.treatMent = treatMent;
		this.isMvp = isMvp;
		this.deadCount = deadCount;
	}

}
