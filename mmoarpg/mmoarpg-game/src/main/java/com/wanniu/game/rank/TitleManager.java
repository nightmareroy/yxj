package com.wanniu.game.rank;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import com.wanniu.core.logfs.Out;
import com.wanniu.game.common.msg.WNNotifyManager;
import com.wanniu.game.data.ext.RankListExt;
import com.wanniu.game.player.AttributeUtil;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.poes.TitlePO;

import pomelo.area.RankHandler.AwardRank;

public class TitleManager {

	public static class AwardRankData {
		public int id;
		public long gotTime;

		public AwardRankData() {}

		public AwardRankData(int id, long gotTime) {
			this.id = id;
			this.gotTime = gotTime;
		}
	}

	public static class RankBiData {
		public TreeMap<String, Integer> all;
		public TreeMap<String, Integer> single;
	}

	public WNPlayer player;
	public TitlePO titlePO;

	public TitleManager(WNPlayer player, TitlePO titlePO) {
		this.player = player;
		this.titlePO = titlePO;
	}

	public int getSelectedRankId() {
		return this.titlePO.selectedRankId;
	}

	public final int getTitleId() {
		return this.titlePO.selectedRankId;
	}

	public final void showRank() {
		// 更新玩家战斗服数据
		this.player.refreshBattlerServerBasic();
	}

	public final ArrayList<AwardRank.Builder> getRankInfo() {
		this.checkInvalidRanks(true);
		ArrayList<AwardRank.Builder> list = new ArrayList<>();
		for (Map.Entry<Integer, AwardRankData> node : this.titlePO.awardRanks.entrySet()) {
			AwardRankData each = node.getValue();
			RankListExt prop = RankConfig.getInstance().findListRankPropByRankID(each.id);
			long validTime = prop != null ? 1L * prop.validTime * 60 * 60 * 1000 : 0L;

			int invalidTime = 0; // 秒
			if (validTime > 0) {
				invalidTime = (int) Math.floor((each.gotTime + validTime) / 1000);
			}
			AwardRank.Builder rank = AwardRank.newBuilder();
			rank.setId(each.id);
			rank.setInvalidTime(invalidTime);
			list.add(rank);
		}
		return list;
	}

	public final void saveRank(int selectedRankId) {
		if (this.titlePO.awardRanks.containsKey(selectedRankId)) {
			this.titlePO.selectedRankId = selectedRankId;
			this.showRank();
		}
	}

	/**
	 * 获取称号（使用称号道具）
	 * 
	 * @param rankId
	 */
	public final void onAwardRank(int rankId) {
		RankListExt prop = RankConfig.getInstance().findListRankPropByRankID(rankId);
		if (prop == null) {
			Out.error("there is no data of RankID: ", rankId, " in rankListProps ");
			return;
		}
		long gotTime = System.currentTimeMillis();
		this.titlePO.awardRanks.put(rankId, new AwardRankData(rankId, gotTime));
		if (this.titlePO.selectedRankId == 0) {
			this.titlePO.selectedRankId = rankId;
			// 更新玩家战斗服数据
			this.player.refreshBattlerServerBasic();
		}
		// TODO bi统计
		RankBiData biData = this.biGetInfluence(rankId);
		// this.player.biServerManager.getRankId(rankId, prop.RankName,
		// biData.single, biData.all);
		this.pushAndRefreshRankInflu(true);
		WNNotifyManager.getInstance().pushAwardRank(this.player, rankId);
	}

	/**
	 * 由称号得失引起的属性变化以及处理
	 */
	private final void pushAndRefreshRankInflu(boolean refresh) {
		this.player.updateTitleAttrs();
		this.player.initAndCalAllInflu(null);
		if (refresh) {
			this.player.refreshBattlerServerEffect(false);
		}
		this.player.pushEffectData();
	}

	public final void refreshNewDay() {
		this.checkInvalidRanks(true);
	}

	public final void checkInvalidRanks(boolean refresh) {
		boolean needUpdate = false;
		if (null != this.titlePO.awardRanks) {
			Iterator<Map.Entry<Integer, AwardRankData>> iter = this.titlePO.awardRanks.entrySet().iterator();
			while (iter.hasNext()) {
				Map.Entry<Integer, AwardRankData> entry = iter.next();
				AwardRankData each = entry.getValue();
				if (null == each) {
					continue;
				}
				RankListExt prop = RankConfig.getInstance().findListRankPropByRankID(each.id);
				if (prop != null && prop.validTime > 0) {
					long validTime = 1L * prop.validTime * 60 * 60 * 1000; // 毫秒
					long invalidTime = each.gotTime + validTime;
					long currTime = System.currentTimeMillis();
					if (currTime >= invalidTime) {
						iter.remove();
						needUpdate = true;
					}
				}
			}
		}

		if (this.titlePO.selectedRankId != 0 && !(this.titlePO.awardRanks.containsKey(this.titlePO.selectedRankId))) {
			this.titlePO.selectedRankId = 0;
			if (refresh) {
				// 更新玩家战斗服数据
				this.player.refreshBattlerServerBasic();
			}
		}
		if (needUpdate) {
			this.pushAndRefreshRankInflu(refresh);
		}
	}

	/**
	 * 计算属性
	 */
	public final TreeMap<String, Integer> calAllInfluence() {
		TreeMap<String, Integer> data = new TreeMap<>();
		for (Map.Entry<Integer, AwardRankData> node : this.titlePO.awardRanks.entrySet()) {
			AwardRankData each = node.getValue();
			RankListExt prop = RankConfig.getInstance().findListRankPropByRankID(each.id);
			if (prop != null) {
				Map<String, Integer> rankAttrs = prop.rankAttrs;
				for (Map.Entry<String, Integer> atts : rankAttrs.entrySet()) {
					if (data.containsKey(atts.getKey())) {
						int value = atts.getValue() + data.get(atts.getKey());
						data.put(atts.getKey(), value);
					} else {
						data.put(atts.getKey(), atts.getValue());
					}
				}
			}
		}
		return data;
	}

	private final RankBiData biGetInfluence(int rankId) {
		RankBiData data = new RankBiData();
		data.all = new TreeMap<>();
		data.single = new TreeMap<>();
		for (Map.Entry<Integer, AwardRankData> node : this.titlePO.awardRanks.entrySet()) {
			AwardRankData each = node.getValue();
			RankListExt prop = RankConfig.getInstance().findListRankPropByRankID(each.id);
			if (prop != null) {
				Map<String, Integer> rankAttrs = prop.rankAttrs;
				for (Map.Entry<String, Integer> atts : rankAttrs.entrySet()) {
					String name = AttributeUtil.getNameByKey(atts.getKey());
					if (data.all.containsKey(name)) {
						int value = data.all.get(name);
						value += atts.getValue();
						data.all.put(name, value);
					} else {
						data.all.put(name, atts.getValue());
					}
					if (rankId == each.id) {
						if (data.single.containsKey(name)) {
							int value = data.single.get(name);
							value += atts.getValue();
							data.single.put(name, value);
						} else {
							data.single.put(name, atts.getValue());
						}
					}
				}
			}
		}
		return data;
	}

	// 计算战力
	public final int calFightPower() {
		TreeMap<String, Integer> allInflus = this.calAllInfluence();
		// 计算战力
		// int fightPower = CommonUtil.calFightPower(allInflus);
		int fightPower = 0;
		return fightPower;
	}

}
