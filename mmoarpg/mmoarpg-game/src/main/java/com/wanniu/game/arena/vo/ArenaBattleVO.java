package com.wanniu.game.arena.vo;

import pomelo.area.ArenaHandler.ArenaBattleScore;

public class ArenaBattleVO {
	private String name; // 玩家名称
	private int score; // 分数
	private int pro; // 玩家职业
	private String id; // 玩家id
	private int killCount;
	private long updateTime = System.currentTimeMillis();
	private int force;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public int getPro() {
		return pro;
	}

	public void setPro(int pro) {
		this.pro = pro;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getKillCount() {
		return killCount;
	}

	public void setKillCount(int killCount) {
		this.killCount = killCount;
	}

	public long getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(long updateTime) {
		this.updateTime = updateTime;
	}

	public int getForce() {
		return force;
	}

	public void setForce(int force) {
		this.force = force;
	}

	public ArenaBattleScore toBuilder(boolean isScore) {
		ArenaBattleScore.Builder builder = ArenaBattleScore.newBuilder();
		builder.setId(id);
		builder.setName(name);
		builder.setPro(pro);
		if (isScore) {
			builder.setScore(score);
		} else {
			builder.setScore(killCount);
		}

		return builder.build();
	}

}
