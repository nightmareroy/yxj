package com.wanniu.game.guild.guildFort.dao;

import java.util.HashMap;
import java.util.Map;

public class GuildFortAwardPO {
	public static enum AwardFlag {
		NO_AWARD(0), // 不可领取
		HAS_AWARD(1), // 可领取
		AWARDED(2); // 已领取
		public int value;

		private AwardFlag(int value) {
			this.value = value;
		}
	}
	/** key=FortId,value=dailyAwardFlag 0: 不可领取 1: 可领取 2：已领取 */
	public Map<Integer, AwardFlag> awardStatus = new HashMap<>();
	public long updateDate = 0;
	
	public GuildFortAwardPO() {

	}
}
