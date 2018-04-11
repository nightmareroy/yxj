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
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.wanniu.core.game.LangService;
import com.wanniu.game.common.Const;
import com.wanniu.game.common.msg.MessagePush;
import com.wanniu.game.guild.GuildServiceCenter;
import com.wanniu.game.player.GlobalConfig;
import com.wanniu.game.player.PlayerUtil;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.poes.GuildPO;
import com.wanniu.game.poes.PlayerPO;

import pomelo.area.GuildBossHandler.HurtRankInfo;
import pomelo.area.GuildBossHandler.OnEndGuildBossPush;
import pomelo.area.GuildBossHandler.OnHurtRankChangePush;

/**
 * 
 * 工会BOSS排名奖励
 * 
 * @author Feiling(feiling@qeng.cn)
 */
public class GuildBossAreaHurtRankCenter {

	public static GuildBossAreaHurtRankCenter instance = new GuildBossAreaHurtRankCenter();
	// 价值从高到低
	public static final Comparator<RankBean> SORT_HURT = new Comparator<RankBean>() {
		public int compare(RankBean o1, RankBean o2) {
			if (o1.getHurt() > o2.getHurt()) {
				return -1;
			} else if (o1.getHurt() < o2.getHurt()) {
				return 1;
			} else {
				return 0;
			}
		}
	};

	// 从低到高
	public static final Comparator<GuildRankBean> SORT_GUILD = new Comparator<GuildRankBean>() {
		public int compare(GuildRankBean o1, GuildRankBean o2) {
			if (o1.getBossKillTime() > o2.getBossKillTime()) {
				return 1;
			} else if (o1.getBossKillTime() < o2.getBossKillTime()) {
				return -1;
			} else {
				return 0;
			}
		}
	};

	// (血量+击杀时间综合排序)
	public static final Comparator<GuildRankBean> SORT_GUILD_HURT = new Comparator<GuildRankBean>() {
		public int compare(GuildRankBean o1, GuildRankBean o2) {
			if (o1.getTotalHurt() > o2.getTotalHurt()) {
				return -1;
			} else if (o1.getTotalHurt() < o2.getTotalHurt()) {
				return 1;
			} else {
				if (o1.getBossKillTime() > o2.getBossKillTime()) {
					return 1;
				} else if (o1.getBossKillTime() < o2.getBossKillTime()) {
					return -1;
				} else {
					return 0;
				}
			}
		}
	};

	// (只从血量排序)
	public static final Comparator<GuildRankBean> SORT_GUILD_ONLY_HURT = new Comparator<GuildRankBean>() {
		public int compare(GuildRankBean o1, GuildRankBean o2) {
			if (o1.getTotalHurt() > o2.getTotalHurt()) {
				return -1;
			} else if (o1.getTotalHurt() < o2.getTotalHurt()) {
				return 1;
			} else {
				return 0;
			}
		}
	};

	private Map<String, GuildRankBean> dataMap = new ConcurrentHashMap<>();// 用来实时显示数据的排行榜
	private Map<String, List<RankBean>> oldData = new ConcurrentHashMap<>();// 用来显示进入工会BOSS那个界面的伤害排行(这个不能删)
	private List<GuildRankBean> guildList = new ArrayList<>();
	private Map<String, RankBean> totalHurtMap = new HashMap<>();
	private List<RankBean> totalHurtList = new ArrayList<>();
	private boolean isChanged = false;

	public void processSortHurtRank(String guildId, Collection<String> pushRoleIds, boolean isForcePush, long bossKillTime, boolean hasKilled) {
		GuildRankBean bean = getGuildRankBean(guildId);
		processSortHurtRank(guildId, pushRoleIds, isForcePush);
		if (bossKillTime > 0 && !bean.hasKilled() && !hasKilled) {// 在活动结束的时候会发个大于0的事件,这里也要加上做排序
			bean.setBossKillTime(bossKillTime);
		}
		if (!bean.hasKilled() && hasKilled) {// 真正杀死怪物的记录
			bean.setHasKilled(true);
			bean.setBossKillTime(bossKillTime);
		}
	}

	/**
	 * 结束的处理
	 */
	public void overStaticsRanks(String guildId, Collection<String> roleIds) {
		GuildRankBean bean = getGuildRankBean(guildId);
		bean.sort();
		processSortHurtRank(guildId, roleIds, true);
		pushOverRanks(roleIds, bean);
	}

	public void pushOverRanks(Collection<String> roleIds, GuildRankBean bean) {
		for (String pId : roleIds) {
			WNPlayer py = PlayerUtil.getOnlinePlayer(pId);
			if (py != null) {
				RankBean myBean = bean.onlyGetRankBean(pId);// 没有上榜
				int myRank = myBean == null ? 0 : myBean.getGuildRank();
				long hurt = myBean == null ? 0 : myBean.getHurt();
				OnEndGuildBossPush.Builder msg = OnEndGuildBossPush.newBuilder();
				msg.setInfo(String.format(LangService.getValue("GUILD_BOSS_END_DETAIL_INFO"), convertNumToStr(hurt), String.valueOf(myRank)));
				msg.setSec(GlobalConfig.GuildBoss_LeaveTime);
				MessagePush push = new MessagePush("area.guildBossPush.onEndGuildBossPush", msg.build());
				py.receive(push);
			}
		}
	}

	/**
	 * 排序处理
	 * 
	 * @param roleIds
	 */
	public void processSortHurtRank(String guildId, Collection<String> pushRoleIds, boolean isForcePush) {
		if (isForcePush) {// 要强制推送
			pushRankData(pushRoleIds, guildId);
		} else {
			//if (!GuildBossAreaHurtRankCenter.getInstance().isChanged() || (pushRoleIds == null || pushRoleIds.isEmpty())) {// 没有数据改变就不需要排序了！
			//	return;
			//}
			//GuildBossAreaHurtRankCenter.getInstance().setChanged(false);
			if(pushRoleIds == null || pushRoleIds.isEmpty()) {
				return;
			}
			for (RankBean bean : totalHurtList) {
				bean.reset();// 整合伤害
			}
			sort(totalHurtList);
			Collections.sort(guildList, GuildBossAreaHurtRankCenter.SORT_GUILD_ONLY_HURT);
			pushRankData(pushRoleIds, guildId);
		}
	}

	private HurtRankInfo.Builder getHurtRankInfo(int rank, String playerId, long hurt) {
		PlayerPO playerBase = PlayerUtil.getPlayerBaseData(playerId);
		if (playerBase != null) {
			HurtRankInfo.Builder bd = HurtRankInfo.newBuilder();
			bd.setHurt(hurt);
			bd.setRank(rank);
			bd.setId(playerId);
			bd.setName(playerBase.name);
			return bd;
		}
		return null;
	}
	
	private HurtRankInfo.Builder getGuildHurtRankInfo(int rank, String guildId, long hurt) {
		GuildPO guildPO =GuildServiceCenter.getInstance().getGuild(guildId);
		if (guildPO != null) {
			HurtRankInfo.Builder bd = HurtRankInfo.newBuilder();
			bd.setHurt(hurt);
			bd.setRank(rank);
			bd.setId(guildId);
			bd.setName(guildPO.name);
			return bd;
		}
		return null;
	}

	private void sort(List<RankBean> list) {
		Collections.sort(list, GuildBossAreaHurtRankCenter.SORT_HURT);
	}

	/**
	 * 推送伤害消息
	 * 
	 * @param roleIds
	 */
	private void pushRankData(Collection<String> pushRoleIds, String guildId) {
		if (pushRoleIds == null || pushRoleIds.isEmpty()) {
			return;
		}
		OnHurtRankChangePush.Builder msg = OnHurtRankChangePush.newBuilder();
		msg.setS2CCode(Const.CODE.OK);
		if(totalHurtList != null && !totalHurtList.isEmpty()) {
			//个人伤害排行榜
			for (int i = 0; i < totalHurtList.size(); i++) {
				RankBean bean = totalHurtList.get(i);
				int rank = i + 1;
				bean.setRank(rank);
				if (i <= 9) {// 只显示前10名
					HurtRankInfo.Builder bd = getHurtRankInfo(rank, bean.getId(), bean.getHurt());
					if (bd != null) {
						msg.addOtherInfo(bd);
					}
				}
			}
		}
		
		if(guildList != null && !guildList.isEmpty()) {
			//工会伤害排行榜
			int rank = 0;	
			for (int i = 0; i < guildList.size(); i++) {
				GuildRankBean bean = guildList.get(i);
				long totalHurt = bean.getTotalHurt();
				if (rank <= 10 && totalHurt> 0) {// 只显示前10名
					HurtRankInfo.Builder bd = getGuildHurtRankInfo(++rank, bean.getGuildId(), totalHurt);
					if (bd != null) {
						msg.addOtherGuildInfo(bd);
					}
				}
				//自己工会所在的排名
				if (guildId.equals(bean.getGuildId())) {
					int myRank = totalHurt<= 0 ? 0 : rank;
					HurtRankInfo.Builder myGuildInfo = getGuildHurtRankInfo(myRank, bean.getGuildId(), totalHurt);
					if (myGuildInfo != null) {
						msg.setMyGuildInfo(myGuildInfo);
					}
				}
			}
		}
		
		msg.setJoinCount(pushRoleIds.size());
		//自己在世界上的个人伤害总排名
		for (String pId : pushRoleIds) {
			WNPlayer py = PlayerUtil.getOnlinePlayer(pId);
			if (py != null) {
				RankBean myBean = totalHurtMap.get(pId);
				int myRank = myBean == null ? 0 : myBean.getRank();
				long hurt = myBean == null ? 0 : myBean.getHurt();
				HurtRankInfo.Builder myInfo = getHurtRankInfo(myRank, pId, hurt);
				if (myInfo != null) {
					msg.setMyInfo(myInfo);
				}
				py.receive("area.guildBossPush.onHurtRankChangePush", msg.build());
			}
		}
	}

	public void clearRankData() {
		totalHurtMap.clear();
		totalHurtList.clear();
	}

	public static String convertNumToStr(long hurt) {
		long T1 = (long) Math.pow(10, 8);
		long T2 = (long) Math.pow(10, 5);
		long T3 = (long) Math.pow(10, 2);
		long T4 = (long) Math.pow(10, 6);
		if ((hurt / T1) >= 1) {
			hurt = (long) Math.floor(hurt * 1.0f / T4);
			return (String.format("%.2f", hurt * 1.0f / T3) + "亿");
		} else if ((hurt / T2) >= 1) {
			hurt = (long) Math.floor(hurt * 1.0f / T3);
			return (String.format("%.2f", hurt * 1.0f / T3) + "万");
		}
		return String.valueOf(hurt);
	}

	/**
	 * 只有活动结束才会进榜单
	 * 
	 * @param guildId
	 */
	public void processOver(String guildId) {
		GuildRankBean bean = getGuildRankBean(guildId);
		if (bean.getHurtList() == null || bean.getHurtList().isEmpty()) {
			return;
		}
		List<RankBean> newList = new ArrayList<>(bean.getHurtList());
		sort(newList);
		replaceShowData(guildId, newList);
		GuildBossAreaHurtRankService.getInstance().saveAfterOver(guildId, newList);
	}

	public void clearOldData() {
		dataMap.clear();
		guildList.clear();
		clearRankData();
	}

	public void replaceShowData(String guildId, List<RankBean> newList) {
		synchronized (oldData) {
			oldData.put(guildId, newList);
		}
	}

	/**
	 * 要以实时排行数据为准
	 * 
	 * @param guildId
	 * @param newList
	 */
	public void replaceIfnullShowData(String guildId, List<RankBean> newList) {
		synchronized (oldData) {
			List<RankBean> currentList = oldData.get(guildId);
			if (currentList == null) {
				oldData.put(guildId, newList);
			}
		}
	}

	public boolean isChanged() {
		return isChanged;
	}

	public void setChanged(boolean isChanged) {
		this.isChanged = isChanged;
	}

	public void setNewData(String guildId, String playerId, long hurt, int enterCount) {
		RankBean bean = getAndSetRankBean(playerId);
		bean.setNewData(enterCount, hurt);
		if (!isChanged) {
			isChanged = true;
		}
		GuildRankBean guildBean = getGuildRankBean(guildId);
		guildBean.setRankBeanIfNull(bean);
	}

	public GuildRankBean getGuildRankBean(String guildId) {
		GuildRankBean bean = dataMap.get(guildId);
		if (bean == null) {
			bean = new GuildRankBean(guildId);
			dataMap.put(guildId, bean);
			guildList.add(bean);
		}
		return bean;
	}

	public RankBean getAndSetRankBean(String playerId) {
		RankBean bean = totalHurtMap.get(playerId);
		if (bean == null) {
			bean = new RankBean();
			bean.setId(playerId);
			totalHurtMap.put(playerId, bean);
			totalHurtList.add(bean);
		}
		return bean;
	}

	public GuildRankBean onlyGetGuildRankBean(String guildId) {
		return dataMap.get(guildId);
	}

	public Map<String, GuildRankBean> getDataMap() {
		return dataMap;
	}

	public List<RankBean> getRankList(String guildId) {
		return oldData.get(guildId);
	}

	public static class GuildStaticRankBean {
		private String guildId;
		private long totalHurt;
		private int seconds;

		public String getGuildId() {
			return guildId;
		}

		public void setGuildId(String guildId) {
			this.guildId = guildId;
		}

		public long getTotalHurt() {
			return totalHurt;
		}

		public void setTotalHurt(long totalHurt) {
			this.totalHurt = totalHurt;
		}

		public int getSeconds() {
			return seconds;
		}

		public void setSeconds(int seconds) {
			this.seconds = seconds;
		}

		public static GuildStaticRankBean getGuildStaticRankBean(GuildRankBean bean) {
			GuildStaticRankBean rb = new GuildStaticRankBean();
			rb.setGuildId(bean.getGuildId());
			rb.setTotalHurt(bean.getTotalHurt());
			if (bean.hasKilled()) {
				rb.setSeconds((int) ((bean.getBossKillTime() - GuildBossCenter.getInstance().getBeginTime()) / 1000));
			}
			return rb;
		}
	}

	private GuildBossAreaHurtRankCenter() {}

	public static GuildBossAreaHurtRankCenter getInstance() {
		return instance;
	}
}
