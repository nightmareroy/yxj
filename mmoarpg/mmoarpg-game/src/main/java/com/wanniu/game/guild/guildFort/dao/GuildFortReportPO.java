package com.wanniu.game.guild.guildFort.dao;

import java.util.HashMap;
import java.util.Map;

public class GuildFortReportPO {
	public String date = "";
	public Map<Integer,GuildFortBattleReportPO> battleReports = new HashMap<>();
}
