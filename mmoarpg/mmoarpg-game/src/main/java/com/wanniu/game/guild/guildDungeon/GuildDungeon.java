package com.wanniu.game.guild.guildDungeon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.wanniu.core.game.JobFactory;
import com.wanniu.core.game.LangService;
import com.wanniu.core.logfs.Out;
import com.wanniu.game.area.Area;
import com.wanniu.game.area.AreaData;
import com.wanniu.game.area.AreaEvent.MonsterData;
import com.wanniu.game.area.AreaUtil;
import com.wanniu.game.area.DamageHealVO;
import com.wanniu.game.common.Const;
import com.wanniu.game.common.Const.GOODS_CHANGE_TYPE;
import com.wanniu.game.data.GDungeonMapCO;
import com.wanniu.game.data.GDungeonRankCO;
import com.wanniu.game.data.GameData;
import com.wanniu.game.data.base.MapBase;
import com.wanniu.game.guild.GuildCommonUtil;
import com.wanniu.game.guild.GuildService;
import com.wanniu.game.item.po.PlayerItemPO;
import com.wanniu.game.mail.MailUtil;
import com.wanniu.game.mail.SysMailConst;
import com.wanniu.game.mail.data.MailData.Attachment;
import com.wanniu.game.mail.data.MailSysData;
import com.wanniu.game.player.GlobalConfig;
import com.wanniu.game.player.PlayerUtil;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.poes.GuildDungeonPO;

import pomelo.area.GuildHandler.OnDungeonEndPush;
import pomelo.area.PlayerHandler.PlayerRelivePush;
import pomelo.area.PlayerHandler.ReliveResponse;
import pomelo.guild.GuildManagerHandler.RankInfo;
import pomelo.item.ItemOuterClass.ItemDetail;

public class GuildDungeon extends Area {
	public String guildId;
	public int maxCountDungeonId;
	public int dungeonCount;
	public Map<String, GuildDungeonPlayerInfo> playerInfo;
	public boolean isClose;
	public String killplayerId;
	public Map<String, Actor> playerIds;
	private GuildDungeon self = this;

	public GuildDungeon(JSONObject opts) {
		super(opts);
		playerIds = super.actors;

		this.guildId = opts.getString("guildId");
		this.maxCountDungeonId = opts.getIntValue("maxCountDungeonId");
		this.dungeonCount = opts.getIntValue("dungeonCount");
		this.playerInfo = new HashMap<String, GuildDungeonPlayerInfo>();
		this.isClose = false;
		this.killplayerId = "";

		// pomelo.app.curServer.id 第三个参数是否还有意义，调试的时候看看
		GuildService.dungeonInit(this.guildId, instanceId, "");

	}

	public void onClose(String msg) {
		if (this.isClose) {
			return;
		}

		this.isClose = true;
		List<PlayerItemPO> itemsInfo = new ArrayList<PlayerItemPO>();
		for (AreaItem dropItem : this.items.values()) {
			itemsInfo.add(dropItem.item.cloneItemDB());
		}

		GuildService.dungeonPass(guildId, dungeonCount, killplayerId);

		for (String id : super.actors.keySet()) {
			WNPlayer player = this.getPlayer(id);
			if (null == player)
				continue;

			List<ItemDetail> awardItem = new ArrayList<ItemDetail>();
			for (AreaItem dropItem : this.items.values()) {
				awardItem.add(dropItem.item.getItemDetail(player.playerBasePO).build());
			}

			int state = 0;
			if (super.areaId == this.maxCountDungeonId) {
				state = 1;
			}

			OnDungeonEndPush.Builder msgPush = OnDungeonEndPush.newBuilder();
			msgPush.setS2CCode(Const.CODE.OK);
			msgPush.addAllAwardItem(awardItem);
			msgPush.setState(state);
			msgPush.setLeftTime(60);
			player.receive("area.guildPush.onDungeonEndPush", msgPush.build());
		}

		GuildService.updateDropItem(this.guildId, this.dungeonCount, itemsInfo);

		GuildDungeonPO data = GuildService.updateDamageAndHeal(this.guildId, this.playerInfo, this.dungeonCount, this.killplayerId);

		if (this.areaId == this.maxCountDungeonId) {
			this.rankReward(data);
		}

		// 定时器
		closeFuture = JobFactory.addDelayJob(new Runnable() {
			@Override
			public void run() {
				for (String id : playerIds.keySet()) {
					Actor playerData = playerIds.get(id);
					if (playerData.leave)
						continue;
					WNPlayer player = getPlayer(id);
					if (null != player) {
						if (areaId == maxCountDungeonId) {
							player.guildManager.leaveDungeon(player);
						} else {
							player.guildManager.joinGuildDungeon(self, 0);
						}
					}
				}
				AreaUtil.closeArea(instanceId);
			}
		}, 60000);
	}

	/**
	 * 角色成功进入场景
	 */
	public void onPlayerEntered(WNPlayer player) {
		try {
			GuildService.updatePlayerNum(this.guildId, this.dungeonCount, this.getPlayerNum());
		} catch (Exception err) {
			Out.error("GuildService updatePlayerNum error:", err);
			throw new Error("call GuildService updatePlayerNum error");
		}
	}

	/**
	 * 角色成功离开场景
	 */
	public void onPlayerLeaved(WNPlayer player) {
		super.onPlayerLeaved(player);
		try {
			GuildService.updatePlayerNum(this.guildId, this.dungeonCount, this.getPlayerNum());
		} catch (Exception err) {
			Out.error("GuildService updatePlayerNum error:", err);
			throw new Error("call GuildService updatePlayerNum error");
		}
	}

	public void closeGuildDungeon() {
		if (this.isClose) {
			return;
		}

		List<PlayerItemPO> itemsInfo = new ArrayList<PlayerItemPO>();
		for (AreaItem dropItem : this.items.values()) {
			itemsInfo.add(dropItem.item.cloneItemDB());
		}

		try {
			GuildService.updateDropItem(this.guildId, this.dungeonCount, itemsInfo);
			GuildDungeonPO data = GuildService.updateDamageAndHeal(this.guildId, this.playerInfo, this.dungeonCount, this.killplayerId);

			this.rankReward(data);

			for (String id : playerIds.keySet()) {
				Actor playerData = playerIds.get(id);
				if (playerData.leave)
					continue;

				WNPlayer player = this.getPlayer(id);

				if (null != player) {
					player.guildManager.leaveDungeon(player);
				}
			}

			addCloseFuture();
		} catch (Exception err) {
			Out.error("closeGuildDungeon error:", err);
			throw new Error("closeGuildDungeon error");
		}

	}

	public int getRank(ArrayList<RankInfo> rankInfo, String playerId) {
		for (int i = 0; i < rankInfo.size(); i++) {
			if (rankInfo.get(i).getPlayerId() == playerId) {
				return i + 1;
			}
		}
		return 0;
	}

	public void rankReward(GuildDungeonPO data) {
		Map<String, Integer> playerScores = new HashMap<String, Integer>();
		for (Integer dungeonCount : data.damagePlayer.keySet()) {
			ArrayList<String> damagePlayers = data.damagePlayer.get(dungeonCount);
			for (String id : damagePlayers) {
				List<GDungeonMapCO> props = GameData.findGDungeonMaps((t) -> {
					return t.type == Const.SCENE_TYPE.GUILD_DUNGEON.getValue();
				});

				props.sort((o1, o2) -> {
					return o1.layer - o2.layer;
				});

				if (playerScores.containsKey(id)) {
					int scores = playerScores.get(id);
					playerScores.put(id, scores + props.get(dungeonCount - 1).gpoints);
				} else {
					playerScores.put(id, props.get(dungeonCount - 1).gpoints);
				}
			}
		}

		for (String id : playerScores.keySet()) {
			int dungeonScore = playerScores.get(id);
			int rank = this.getRank(data.damageRankInfo, id);
			GDungeonRankCO rankProp = GuildCommonUtil.findGDungeonRanks(1, rank);
			if (null != rankProp) {
				dungeonScore += rankProp.gpoints;
			}

			rank = this.getRank(data.healRankInfo, id);
			rankProp = GuildCommonUtil.findGDungeonRanks(2, rank);
			if (null != rankProp) {
				dungeonScore += rankProp.gpoints;
			}

			WNPlayer player = PlayerUtil.getOnlinePlayer(id);
			if (null != player) {
				player.addGuildPoint(dungeonScore);
				player.pushDynamicData("guildpoint", player.player.guildpoint);
			} else {
				MailSysData mailData = new MailSysData(SysMailConst.GUILD_DUNGEON_INTEGRAL);
				mailData.attachments = new ArrayList<Attachment>();
				Attachment attach = new Attachment();
				attach.itemCode = "guildpoint";
				attach.itemNum = dungeonScore;
				mailData.attachments.add(attach);
				MailUtil.getInstance().sendMailToOnePlayer(id, mailData, GOODS_CHANGE_TYPE.GUILD_BOSS);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.wanniu.game.area.Area#onBattleReport(java.util.List)
	 */
	public void onBattleReport(List<DamageHealVO> datas) {
		for (DamageHealVO data : datas) {
			GuildDungeonPlayerInfo info = new GuildDungeonPlayerInfo();
			info.damage = data.TotalDamage;
			info.heal = data.TotalHealing;
			this.playerInfo.put(data.PlayerUUID, info);
			WNPlayer player = this.getPlayer(data.PlayerUUID);
			if (null != player) {
				player.guildManager.setJoinDungeonGuildId(this.guildId);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.wanniu.game.area.Area#onKillBoss(java.lang.String)
	 */
	public void onKillBoss(String playerId) {
		this.killplayerId = playerId;
	}

	public boolean canRebirth(String playerId) {
		Actor playerData = super.actors.get(playerId);
		if (null != playerData) {
			MapBase mapBase = prop;
			if (mapBase.revival == 0) {
				return false;
			} else if (mapBase.revival < 0) {
				return true;
			} else if (playerData.rebornNum >= mapBase.revival) {
				return false;
			}
			playerData.rebornNum++;
		}

		return true;
	}

	public boolean isDamagePlayer(Map<Integer, ArrayList<String>> damagePlayer, int dungeonCount, String playerId) {
		ArrayList<String> eachCountDamage = damagePlayer.get(dungeonCount);
		if (null != eachCountDamage && eachCountDamage.size() > 0) {
			int index = eachCountDamage.indexOf(playerId);
			if (index != -1) {
				return true;
			}
		}
		return false;
	}

	@Override
	public ReliveResponse.Builder relive(String playerId, ReliveType reliveType) {
		ReliveResponse.Builder res = ReliveResponse.newBuilder();
		Actor actor = playerIds.get(playerId);
		WNPlayer player = this.getPlayer(playerId);
		if (null == actor || null == player) {
			res.setS2CCode(Const.CODE.FAIL);
			res.setS2CMsg(LangService.getValue("REVIVE_NOT_FIND_PLAYER"));
			return res;
		}

		if (prop.revival > 0 && actor.rebornNum >= prop.revival) {
			res.setS2CCode(Const.CODE.FAIL);
			res.setS2CMsg(LangService.getValue("REVIVE_MAX"));
			return res;
		}

		return super.relive(playerId, reliveType);
	}

	public int reviveNum(String playerId) {
		MapBase mapBase = prop;
		Actor playerData = playerIds.get(playerId);
		if (null == playerData) {
			// 此角色不存在
			return 0;
		}

		int configNum = mapBase.revival;
		if (configNum <= 0) {
			return configNum;
		} else {
			if (configNum <= playerData.rebornNum) {
				return 0;
			} else {
				return configNum - playerData.rebornNum;
			}
		}
	}

	public void onPlayerDeadByMonster(String playerId, MonsterData monsterData) {
		Actor playerData = playerIds.get(playerId);
		if (null == playerData) {
			Out.error("onPlayerDead not exist! :", playerId);
			return;
		}
		WNPlayer player = this.getPlayer(playerId);
		PlayerRelivePush.Builder data = newPlayerRelivePush(player);
		int overTime = GlobalConfig.Dungeon_DeadBack_Time;
		data.setCountDown(overTime);
	}

	/**
	 * 添加角色
	 */
	public void addPlayer(WNPlayer player) {
		setForce(player);
		String enterSceneData = toJSON4EnterScene(player);
		try {
			playerEnterRequest(player, enterSceneData);
			this.hasPlayerEntered = true;

			if (null == super.actors.get(player.getId())) {
				playerIds.put(player.getId(), new Actor());
			}

		} catch (Exception error) {
			Out.error("c# enter scene id: ", this.instanceId, "enterSceneData:", enterSceneData);
			Out.error("c# enter scene id: ", this.instanceId, " error : ", error);
			throw error;
		}
	}

	/**
	 * 移除角色
	 */
	@Override
	public void removePlayer(WNPlayer player, boolean keepObject) {
		String playerId = player.getId();
		if (this.hasPlayer(playerId)) {
			playerLeaveRequest(player, keepObject);
			this.onPlayerLeaved(player);

			resetEmptyTime();

			// 刷新场景信息
			// AreaManager.getInstance().refreshAreaStatu(super.instanceId);
			playerIds.get(playerId).leave = true;
		}
	}

	/**
	 * 场景销毁
	 */
	public void dispose() {
		for (String playerId : playerIds.keySet()) {
			if (playerIds.get(playerId).leave)
				continue;
			WNPlayer player = this.getPlayer(playerId);

			AreaUtil.dispatchByAreaId(player, new AreaData(player.playerTempData.historyAreaId, player.playerTempData.historyX, player.playerTempData.historyY), null);
		}
		// 调用战斗服删除场景信息
		getZoneManager().destroyZoneRequest(super.instanceId);
	}

	public void cleanDropItems() {

	}

}
