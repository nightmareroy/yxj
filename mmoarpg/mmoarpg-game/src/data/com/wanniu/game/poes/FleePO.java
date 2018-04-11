package com.wanniu.game.poes;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.wanniu.core.game.entity.GEntity;
import com.wanniu.game.DBTable;
import com.wanniu.game.common.Table;
import com.wanniu.game.flee.FleeReportCO;

/**
 * 玩家大逃杀信息
 * 
 * @author lxm
 *
 */
@DBTable(Table.player_flee)
public class FleePO extends GEntity {

	/** 积分 */
	public int score;

	/** 赛季结束时间 */
	public Date seasonEndTime = new Date();
	
	/** 段位 */
	public int grade = 1;
	
	/** 历史最高段位 */
	public int maxGrade = 1;
	
	/** 历史最高排名 */
	public int maxRank;
	
	/** 段位领取记录 */
	public List<Integer> receiveGrades = new ArrayList<>();
	
	/** 战斗记录 */
	public List<FleeReportCO> reports = new ArrayList<>();
	
	/** 是否玩过 */
	public boolean isPlayed;

	public FleePO() {

	}
}
