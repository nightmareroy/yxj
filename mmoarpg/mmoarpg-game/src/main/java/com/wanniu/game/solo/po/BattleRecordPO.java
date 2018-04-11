package com.wanniu.game.solo.po;

import java.util.Date;

public class BattleRecordPO {
	public int result = 0; // 战斗结果 1-胜 2-负 3-平
	public Date battleTime; // 战斗结束时间
	public int score; // 当时资历
	public int scoreChange; // 资历变化 获得或者减少多少score
	public String vsName; // 对方名字
	public String vsGuildName; // 对方公会名称
	public int vsPro; // 对方职业
	public int vsScore; // 对方资历score
}
