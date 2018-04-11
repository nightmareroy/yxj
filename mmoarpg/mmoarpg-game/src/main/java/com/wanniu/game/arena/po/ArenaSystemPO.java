package com.wanniu.game.arena.po;

import java.util.Date;

import com.wanniu.game.DBField;

public class ArenaSystemPO {
	@DBField(isPKey = true, fieldType = "varchar", size = 50)
	public String logicServerId;
	public Date seasonResetTime; // 新增 赛季结束时间
	public int term;// 当前是第几赛季
	// public int dailyTerm;//每日总榜的期数

	public ArenaSystemPO() {

	}

	public ArenaSystemPO(String logicServerId) {
		this.logicServerId = logicServerId;
		this.seasonResetTime = new Date();
		this.term = 0;// 默认第1赛季
		// this.dailyTerm = 0;
	}
}
