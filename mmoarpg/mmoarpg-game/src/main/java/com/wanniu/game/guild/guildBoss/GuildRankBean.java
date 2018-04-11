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
package com.wanniu.game.guild.guildBoss;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 *
 * @author Feiling(feiling@qeng.cn)
 */
public class GuildRankBean {
	private String guildId;
	private long bossKillTime;
	private Map<String, RankBean> hurtMap = new HashMap<>();
	private List<RankBean> hurtList = new ArrayList<>();
	private boolean hasKilled = false;

	public boolean hasKilled() {
		return hasKilled;
	}

	public void setHasKilled(boolean hasKilled) {
		this.hasKilled = hasKilled;
	}

	public long getTotalHurt() {
		long total = 0l;
		if (hurtList != null && !hurtList.isEmpty()) {
			for (RankBean bean : hurtList) {
				total += bean.getHurt();
			}
		}
		return total;
	}

	public long getBossKillTime() {
		return bossKillTime;
	}

	public void setBossKillTime(long bossKillTime) {
		this.bossKillTime = bossKillTime;
	}

	public GuildRankBean(String guildId) {
		this.guildId = guildId;
		this.bossKillTime = -1l;
	}

	public String getGuildId() {
		return guildId;
	}

	public void setRankBeanIfNull(RankBean bean) {
		RankBean tmBean = hurtMap.get(bean.getId());
		if (tmBean == null) {
			hurtMap.put(bean.getId(), bean);
			synchronized (hurtList) {
				hurtList.add(bean);
			}
		}
	}

	public List<RankBean> getHurtListWithLock() {
		synchronized (hurtList) {
			return new ArrayList<>(hurtList);
		}
	}

	public RankBean onlyGetRankBean(String playerId) {
		return hurtMap.get(playerId);
	}

	public List<RankBean> getHurtList() {
		return hurtList;
	}

	public void sort() {
		Collections.sort(hurtList, GuildBossAreaHurtRankCenter.SORT_HURT);
		for (int i = 0; i < hurtList.size(); i++) {
			RankBean bean = hurtList.get(i);
			bean.setGuildRank(i+1);
		}
	}
}
