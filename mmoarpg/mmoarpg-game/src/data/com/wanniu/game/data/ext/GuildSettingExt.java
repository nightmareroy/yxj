package com.wanniu.game.data.ext;

import com.wanniu.core.util.StringUtil;
import com.wanniu.game.data.GuildSettingCO;

public class GuildSettingExt extends GuildSettingCO {
	public long selfOutMs;
	public long kickOutMs;
	public long fireOutMs;
	public long impeachMs;
	public long impeachTimeMs;

	public int guildBossBeginHours;
	public int guildBossBeginMinutes;

	public int guildBossEndHours;
	public int guildBossEndMinutes;

	public void initProperty() {
		// 时间统一转成毫秒
		this.selfOutMs = this.selfOut * 60 * 1000; // minute -> ms
		this.kickOutMs = this.kickOut * 60 * 1000; // minute -> ms
		this.fireOutMs = this.fireOut * 60 * 1000; // minute -> ms
		this.impeachMs = this.impeach * 24 * 60 * 60 * 1000; // day -> ms
		this.impeachTimeMs = this.impeachTime * 60 * 1000; // minute -> ms

		if (!StringUtil.isEmpty(this.gBossOpenTime)) {
			String[] beginStrs = this.gBossOpenTime.split(":");
			guildBossBeginHours = Integer.parseInt(beginStrs[0]);
			guildBossBeginMinutes = Integer.parseInt(beginStrs[1]);

		}
		if (!StringUtil.isEmpty(this.gBossCloseTime)) {
			String[] endStrs = this.gBossCloseTime.split(":");
			guildBossEndHours = Integer.parseInt(endStrs[0]);
			guildBossEndMinutes = Integer.parseInt(endStrs[1]);
		}
	}

}
