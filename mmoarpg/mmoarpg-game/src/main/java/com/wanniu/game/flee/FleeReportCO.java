package com.wanniu.game.flee;

/**
 * 大逃杀战斗记录
 * 
 * @author lxm
 *
 */
public class FleeReportCO {
	public String datetime;
	public int rank;
	public int scoreChange;

	public FleeReportCO(String datetime, int rank, int scoreChange) {
		this.datetime = datetime;
		this.rank = rank;
		this.scoreChange = scoreChange;
	}

	public FleeReportCO() {

	}
}
