package com.wanniu.game.data; 

public class RankRewardCO { 

	/** 编号ID */
	public int iD;
	/** 排名类型 */
	public int rankType;
	/** 起始名次 */
	public int startRank;
	/** 结束名次 */
	public int stopRank;
	/** 奖励物品 */
	public String rankReward;
	/** 奖励来源 */
	public String rewardSource;
	/** 仙盟资金 */
	public int guildFund;
	/** 随机奖励掉落概率 */
	public int dropProb;
	/** 排名概率加成 */
	public int rankProb;
	/** 随机奖励1 */
	public String randomReward1;
	/** 权重1 */
	public int prob1;
	/** 随机奖励2 */
	public String randomReward2;
	/** 权重2 */
	public int prob2;
	/** 随机奖励3 */
	public String randomReward3;
	/** 权重3 */
	public int prob3;
	/** 随机奖励4 */
	public String randomReward4;
	/** 权重4 */
	public int prob4;
	/** 随机奖励5 */
	public String randomReward5;
	/** 权重5 */
	public int prob5;
	/** 是否拍卖 */
	public int isAuction;

	/** 主键 */
	public int getKey() {
		return this.iD; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}