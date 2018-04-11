package com.wanniu.game.solo.vo;

import com.wanniu.game.player.GlobalConfig;
import com.wanniu.game.player.WNPlayer;

public class MatcherVO {
	private WNPlayer player;
	private int score;
	private boolean isOnline;
	private long beginTime;
	private int scoreRange;
	private long offlineTime;
	private boolean markedDel;

	public boolean isMarkedDel() {
		return markedDel;
	}

	public void setMarkedDel(boolean markedDel) {
		this.markedDel = markedDel;
	}

	public long getBeginTime() {
		return beginTime;
	}

	public void setBeginTime(long beginTimeMills) {
		this.beginTime = beginTimeMills;
	}

	public MatcherVO(int score, WNPlayer player) {
		this.player = player;
		this.score = score;
		this.isOnline = true;
		this.beginTime = System.currentTimeMillis();
		this.scoreRange = GlobalConfig.Solo_MatchRangeIncrease;
		this.offlineTime = 0;
	}

	public WNPlayer getPlayer() {
		return this.player;
	}

	/**
	 * 扩大匹配分值范围
	 */
	public void increaseScoreRange() {
		this.scoreRange += GlobalConfig.Solo_MatchRangeIncrease;
	}

	/**
	 * @return 匹配分值的下限
	 */
	public int getMinScore() {
		return score - scoreRange < 0 ? 0 : score - scoreRange;
	}

	/**
	 * @return 匹配分值的上限
	 */
	public int getMaxScore() {
		return score + scoreRange;
	}

	public int getScoreRange() {
		return this.scoreRange;
	}

	public String getPlayerId() {
		return this.player.getId();
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	/**
	 * @return 是否在线
	 */
	public boolean isOnline() {
		return isOnline;
	}

	/**
	 * 设置为已上线
	 */
	public void setOnline() {
		this.isOnline = true;
		this.offlineTime = 0;
	}

	/**
	 * 设置为已下线
	 */
	public void setOffline() {
		this.isOnline = false;
		this.offlineTime = System.currentTimeMillis();
	}

	/**
	 * @return 返回已经掉线的时间秒数
	 */
	public int getOfflinedTime() {
		if (this.offlineTime == 0) {
			return 0;
		}
		return (int) (System.currentTimeMillis() - this.offlineTime) / 1000;
	}

	public int getWaitTime() {
		return (int) (System.currentTimeMillis() - this.beginTime) / 1000;
	}
}
