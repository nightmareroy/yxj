package com.wanniu.game.area;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.wanniu.game.common.Const;
import com.wanniu.game.common.Const.ForceType;
import com.wanniu.game.common.Const.GOODS_CHANGE_TYPE;
import com.wanniu.game.common.ConstsTR;
import com.wanniu.game.data.GameData;
import com.wanniu.game.data.base.MonsterBase;
import com.wanniu.game.data.ext.MonsterRefreshExt;
import com.wanniu.game.item.ItemUtil;
import com.wanniu.game.mail.MailUtil;
import com.wanniu.game.mail.SysMailConst;
import com.wanniu.game.mail.data.MailData.Attachment;
import com.wanniu.game.mail.data.MailSysData;
import com.wanniu.game.monster.MonsterConfig;
import com.wanniu.game.player.GlobalConfig;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.poes.MonsterDropPO;
import com.wanniu.redis.PlayerPOManager;

import pomelo.area.ItemHandler.RewardItemPush;
import pomelo.item.ItemOuterClass.MiniItem;

public class DropManager {
	public WNPlayer player;

	private MonsterDropPO po;

	public DropManager(WNPlayer player, MonsterDropPO po) {
		this.player = player;
		if (po == null) {
			po = new MonsterDropPO();
		}
		this.po = po;
		PlayerPOManager.put(ConstsTR.monster_drop_infoTR, player.getId(), po);
	}

	/**
	 * 给定的bossId是否可以掉落装备
	 * 
	 * @param bossId
	 * @return
	 */
	public boolean canDrop(int bossId, Area area) {
		if (bossId == 0 || area.sceneType == Const.SCENE_TYPE.FIGHT_LEVEL.getValue()) {// 为了避免和副本冲突
			return true;
		}

		MonsterBase prop = MonsterConfig.getInstance().get(bossId);
		if (prop == null || prop.type < 4) {// 普通小怪
			return true;
		}
		List<MonsterRefreshExt> refreshProps = GameData.findMonsterRefreshs(t -> {
			return t.monsterID == bossId && t.mapID == area.areaId;
		});
		if (refreshProps == null || refreshProps.isEmpty()) {
			return true;
		}
		MonsterRefreshExt ext = refreshProps.get(0);
		if (ext.useType == 1) {// 说明是每日有击杀上限的怪
			int count = po.getTodayCount();
			return count < GlobalConfig.Lords_MaxNumEveryday;
		}
		return true;
	}

	public void sendJoinReward(int bossId, String bossName, List<MonsterRefreshExt> refreshProps) {
		if (refreshProps == null || refreshProps.isEmpty()) {
			return;
		}
		MonsterRefreshExt ext = refreshProps.get(0);
		sendJoinRward(bossName, ext.joinReward);
	}

	private void sendJoinRward(String name, Map<String, Integer> rankRewards) {
		if (rankRewards == null) {
			return;
		}
		List<MiniItem> miniItem = new ArrayList<>();
		for (Entry<String, Integer> attach : rankRewards.entrySet()) {
			miniItem.add(ItemUtil.getMiniItemData(attach.getKey(), attach.getValue(), ForceType.BIND).build());
		}

		if (!miniItem.isEmpty()) {
			RewardItemPush.Builder data = RewardItemPush.newBuilder();
			data.setS2CCode(Const.CODE.OK);
			data.addAllS2CItem(miniItem);
			this.player.receive("area.itemPush.rewardItemPush", data.build());

			// 弹完界面，发个邮件...
			MailSysData mailData = new MailSysData(SysMailConst.Lords_Reward_ForPartake);
			Map<String, String> replace = new HashMap<>();
			replace.put("name", name);
			mailData.replace = replace;
			mailData.attachments = new ArrayList<>();
			for (Entry<String, Integer> attach : rankRewards.entrySet()) {
				Attachment item = new Attachment();
				item.itemCode = attach.getKey();
				item.itemNum = attach.getValue();
				mailData.attachments.add(item);
			}
			MailUtil.getInstance().sendMailToOnePlayer(this.player.getId(), mailData, GOODS_CHANGE_TYPE.BOSS_ASSISTS);
		}
	}

	/**
	 * 当boss死亡获得收益时
	 * 
	 * @param bossId
	 */
	public void onBossDead(int bossId) {
		if (bossId == 0) {
			return;
		}
		MonsterBase prop = MonsterConfig.getInstance().get(bossId);
		if (prop == null || prop.type < 4) {// 普通小怪不记录
			return;
		}
		this.po.addBossCount();
	}

	public void refreshNewDay() {
		if (po != null) {
			this.po.clear();
		}
	}
}
