package com.wanniu.game.solo.po;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.wanniu.game.DBField;

public class SoloSystemPO {
	@DBField(isPKey = true, fieldType = "varchar", size = 50)
	public String logicServerId;
	public Date seasonEndTime; // 新增 赛季结束时间
	public int term;// 当前是第几赛季
	public int rounds;// 本赛季已开展多少轮

	public List<String> soloNewses;// 传闻消息

	public SoloSystemPO() {

	}

	public SoloSystemPO(String logicServerId) {
		this.logicServerId = logicServerId;
		this.seasonEndTime = null;
		this.term = 0;
		this.rounds = 1;// 默认第一轮

		this.soloNewses = new ArrayList<>();
	}
}
