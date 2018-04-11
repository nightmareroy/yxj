/*
 * Copyright © 2017 qeng.cn All Rights Reserved.
 * 
 * 感谢您加入清源科技，不用多久，您就会升职加薪、当上总经理、出任CEO、迎娶白富美、从此走上人生巅峰
 * 除非符合本公司的商业许可协议，否则不得使用或传播此源码，您可以下载许可协议文件：
 * 
 * 		http://www.noark.xyz/qeng/LICENSE
 *
 * 1、未经许可，任何公司及个人不得以任何方式或理由来修改、使用或传播此源码;
 * 2、禁止在本源码或其他相关源码的基础上发展任何派生版本、修改版本或第三方版本;
 * 3、无论你对源代码做出任何修改和优化，版权都归清源科技所有，我们将保留所有权利;
 * 4、凡侵犯清源科技相关版权或著作权等知识产权者，必依法追究其法律责任，特此郑重法律声明！
 */
package com.wanniu.game.data.ext;

import java.util.ArrayList;
import java.util.List;

import com.wanniu.core.util.RandomUtil;
import com.wanniu.core.util.StringUtil;
import com.wanniu.game.common.Const;
import com.wanniu.game.data.RankRewardCO;
import com.wanniu.game.item.ItemUtil;
import com.wanniu.game.item.NormalItem;

/**
 * 
 *
 * @author Feiling(feiling@qeng.cn)
 */
public class RankRewardExt extends RankRewardCO {

	private List<GuildBossRandItem> list;
	private int totalPercent;

	@Override
	public void initProperty() {
		int t1 = this.prob1 + this.prob2 + this.prob3;
		totalPercent = t1;

		List<GuildBossRandItem> tplist = new ArrayList<>();
		tplist.add(new GuildBossRandItem(this.prob1, this.randomReward1));
		tplist.add(new GuildBossRandItem(this.prob2, this.randomReward2));
		tplist.add(new GuildBossRandItem(this.prob3, this.randomReward3));
		tplist.add(new GuildBossRandItem(this.prob4, this.randomReward4));
		tplist.add(new GuildBossRandItem(this.prob5, this.randomReward5));
		list = tplist;
	}

	public List<NormalItem> getRandomReward() {
		boolean hasRate = RandomUtil.hasHitRate(10000, this.rankProb + this.dropProb);
		if (hasRate) {
			String itemStr = getRandomItem();
			return parseItems(itemStr);
		}
		return null;
	}

	private String getRandomItem() {
		if (totalPercent <= 0 || list == null) {
			return null;
		}
		int pct = RandomUtil.randomValue(totalPercent);
		int totalCount = 0;
		for (GuildBossRandItem bean : list) {
			totalCount += bean.getRandomValue();
			if (totalCount > pct) {
				return bean.getReward();
			}
		}
		return null;
	}

	public List<NormalItem> getList() {
		return parseItems(this.rankReward);
	}

	private List<NormalItem> parseItems(String itemStr) {
		if (!StringUtil.isEmpty(itemStr)) {
			List<NormalItem> tpList = new ArrayList<>();
			String[] items = itemStr.split(";");
			for (String oneItem : items) {
				String[] randomCounts = oneItem.split("[|]");
				String oneItemStr = randomCounts[0];
				String[] countStr = randomCounts[1].split("-");
				int begin = Integer.parseInt(countStr[0]);
				int end = Integer.parseInt(countStr[1]);
				int count = RandomUtil.getInt(begin, end);
				for (int i = 0; i < count; i++) {
					String[] item = oneItemStr.split(":");
					List<NormalItem> list = ItemUtil.createItemsByItemCode(item[0], Integer.parseInt(item[1]));
					if (list != null && !list.isEmpty()) {
						for (NormalItem ni : list) {
							ni.itemDb.isBind = Const.ForceType.BIND.getValue();
							tpList.add(ni);
						}
					}
				}

			}
			return tpList;
		}
		return null;
	}

	public static class GuildBossRandItem {
		public GuildBossRandItem(int randomValue, String reward) {
			this.randomValue = randomValue;
			this.reward = reward;
		}

		private int randomValue;
		private String reward;

		public int getRandomValue() {
			return randomValue;
		}

		public void setRandomValue(int randomValue) {
			this.randomValue = randomValue;
		}

		public String getReward() {
			return reward;
		}

		public void setReward(String reward) {
			this.reward = reward;
		}

	}
}
