package com.wanniu.game.rank;

/**
 * 简单的排行数据.
 *
 * @author 小流氓(176543888@qq.com)
 */
public class SimpleRankData {
	private int rank;// 排名
	private String id;// 排行中ID
	private int score;// 排行的分数值

	public int getRank() {
		return rank;
	}

	public void setRank(int rank) {
		this.rank = rank;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}
}