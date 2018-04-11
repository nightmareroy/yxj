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
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.wanniu.core.db.GCache;
import com.wanniu.core.util.StringUtil;
import com.wanniu.game.common.ConstsTR;
import com.wanniu.game.player.WNPlayer;

/**
 * 
 * 场景外的伤害排行榜
 * 
 * @author Feiling(feiling@qeng.cn)
 */
public class GuildBossAreaHurtRankManager {
	private WNPlayer player;

	public GuildBossAreaHurtRankManager(WNPlayer player) {
		this.player = player;
	}

	/**
	 * 
	 * 获取上一次的伤害排行榜
	 * @param guildId
	 * @return
	 */
	public List<RankBean> getAndSetRankBeanList(String guildId) {
		List<RankBean> list = GuildBossAreaHurtRankCenter.getInstance().getRankList(guildId);
		if (list == null) {
			String vl = GCache.hget(ConstsTR.guildBossHurtTR.value, guildId);
			if (!StringUtil.isEmpty(vl)) {
				list = JSON.parseArray(vl, RankBean.class);
			} else {
				list = new ArrayList<RankBean>();
			}
			GuildBossAreaHurtRankCenter.getInstance().replaceIfnullShowData(guildId, list);
		}
		return list;
	}
	
	
	/**
	 * 获取实时的数据排行榜
	 * @param guildId
	 * @return
	 */
	public List<RankBean> getRankBeanListOnBegin(String guildId){
		GuildRankBean guildBean = GuildBossAreaHurtRankCenter.getInstance().onlyGetGuildRankBean(guildId);
		if(guildBean == null) {
			return null;
		}
		List<RankBean> list = guildBean.getHurtListWithLock();
		if(!list.isEmpty()) {
			Collections.sort(list, GuildBossAreaHurtRankCenter.SORT_HURT);
		}
		return list;
	}
	

	public WNPlayer getPlayer() {
		return player;
	}

	public void setPlayer(WNPlayer player) {
		this.player = player;
	}

}
