package com.wanniu.game.five2Five;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author wanghaitao
 *
 */
public class Five2FiveTempTeamMember {
	public String playerId;

	public int playerPro;

	public int playerLvl;

	public String playerName;

	public int force;

	public int index;

	/** 准备还是取消了 1、放弃2、准备就绪 */
	public AtomicInteger isReadyOrCancel = new AtomicInteger(0);
}
