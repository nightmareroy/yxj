package com.wanniu.game.area;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.protobuf.GeneratedMessage;
import com.wanniu.core.GGlobal;
import com.wanniu.core.game.JobFactory;
import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GPlayer;
import com.wanniu.core.game.protocol.PomeloPush;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.logfs.Out;
import com.wanniu.core.util.RandomUtil;
import com.wanniu.core.util.StringUtil;
import com.wanniu.csharp.CSharpClient;
import com.wanniu.game.GWorld;
import com.wanniu.game.chat.ChannelUtil;
import com.wanniu.game.common.Const;
import com.wanniu.game.common.Const.CHAT_SCOPE;
import com.wanniu.game.common.Const.EventType;
import com.wanniu.game.common.Const.GOODS_CHANGE_TYPE;
import com.wanniu.game.common.Const.SCENE_TYPE;
import com.wanniu.game.common.Const.TaskType;
import com.wanniu.game.common.Const.TipsType;
import com.wanniu.game.common.Utils;
import com.wanniu.game.common.msg.MessagePush;
import com.wanniu.game.common.msg.MessageUtil;
import com.wanniu.game.common.msg.WNNotifyManager;
import com.wanniu.game.daoyou.DaoYouService;
import com.wanniu.game.data.CharacterLevelCO;
import com.wanniu.game.data.GameData;
import com.wanniu.game.data.MeltConfigCO;
import com.wanniu.game.data.MonsterRefreshCO;
import com.wanniu.game.data.ResurrectionCO;
import com.wanniu.game.data.base.DItemEquipBase;
import com.wanniu.game.data.base.MapBase;
import com.wanniu.game.data.base.MonsterBase;
import com.wanniu.game.data.ext.MonsterRefreshExt;
import com.wanniu.game.data.ext.RandomBoxExt;
import com.wanniu.game.data.ext.RandomBoxExt.Point;
import com.wanniu.game.equip.NormalEquip;
import com.wanniu.game.item.ItemConfig;
import com.wanniu.game.item.ItemUtil;
import com.wanniu.game.item.NormalItem;
import com.wanniu.game.item.VirtualItem;
import com.wanniu.game.item.data.ItemToBtlServerData;
import com.wanniu.game.item.po.ItemSpeData;
import com.wanniu.game.mail.MailUtil;
import com.wanniu.game.mail.SysMailConst;
import com.wanniu.game.mail.data.MailData.Attachment;
import com.wanniu.game.mail.data.MailSysData;
import com.wanniu.game.monster.MonsterConfig;
import com.wanniu.game.petNew.PetNew;
import com.wanniu.game.player.BILogService;
import com.wanniu.game.player.GlobalConfig;
import com.wanniu.game.player.PlayerUtil;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.poes.DaoYouPO;
import com.wanniu.game.poes.PlayerPO;
import com.wanniu.game.poes.PlayerTempPO;
import com.wanniu.game.sevengoal.SevenGoalManager.SevenGoalTaskType;
import com.wanniu.game.task.TaskEvent;
import com.wanniu.game.team.TeamData.TeamMemberData;
import com.wanniu.game.team.TeamService;
import com.wanniu.game.team.TeamUtil;
import com.wanniu.game.util.RobotUtil;

import Pomelo.ZoneManagerPrx;
import Xmds.FinishPickItem;
import Xmds.GetPlayerData;
import Xmds.XmdsManagerPrx;
import pomelo.area.BattleHandler.ItemDrop;
import pomelo.area.BattleHandler.ItemDropPush;
import pomelo.area.BattleHandler.PointItemView;
import pomelo.area.BattleHandler.ThrowPointItemListPush;
import pomelo.area.BattleHandler.ThrowPointResultPush;
import pomelo.area.PlayerHandler.PlayerRelivePush;
import pomelo.area.PlayerHandler.ReliveResponse;
import pomelo.chat.ChatHandler.OnChatPush;

/**
 * @author agui
 */
public class Area {

	public boolean bsClose; // 战斗服是否关闭
	protected String serverId; // 战斗服ID
	public byte[] npcDatas;

	public static enum ReliveBtn {
		UN_SHOW(0), SHOW_CLICK(1), SHOW_UN_CLICK(2);
		public int value;

		ReliveBtn(int value) {
			this.value = value;
		}
	}

	public static enum ReliveUP {
		OK(1), // 可直接强化
		NO(0); // 不可直接强化
		public int value;

		ReliveUP(int value) {
			this.value = value;
		}
	}

	public static enum ReliveOP {
		FIELD(0), // 野外复活
		RELIVE(1), // 副本复活
		LEAVE(2); // 回城复活
		public int value;

		ReliveOP(int value) {
			this.value = value;
		}
	}

	public static enum ReliveCB {
		ICON(0), RELIVE(1), LEAVE(2);
		public int value;

		ReliveCB(int value) {
			this.value = value;
		}
	}

	/**
	 * 1:原地复活；2:出生点复活；3:复活点复活. 4:技能复活.
	 */
	public static enum ReliveType {
		/** 回城复活 */
		CITY(0),
		/** 原地复活 */
		NOW(1),
		/** 出生点复活 */
		BORN(2),
		/** 复活点复活 */
		PLACE(3),
		/** 技能复活 */
		SKILL(4),
		/** 随机点复活 */
		RANDOM(5);
		public final int value;

		private ReliveType(int value) {
			this.value = value;
		}
	}

	/** 场景中的角色 */
	public static class Actor {
		public int rebornNum;
		public boolean leave, ready;
		public boolean alive = true;
		public long reliveCoolTime;
		/** 是否可收益 */
		public boolean profitable = true;
		/** 获得的虚拟物品 */
		public Map<String, Integer> historyVirtualItems;
		/** 获得的非 虚拟物品 */
		public List<NormalItem> historyItems;
		/** 吃到的buffer(目前仅针对【天神buffer】) */
		public List<String> buffers = new ArrayList<>();

		public void relive() {
			this.rebornNum++;
			alive = true;
		}
	}

	public static class AreaItem {
		public NormalItem item;
		public Map<String, WNPlayer> bindPlayers;
		public long createTime;
		public int monsterId;

		/** doprItem */
		public WNPlayer dropPlayer;
		public float dropX;
		public float dropY;

		public AreaItem(NormalItem item) {
			this.item = item;
			this.createTime = System.currentTimeMillis();
		}
	}

	public boolean isFull() {
		return isClose || bsClose || actors.size() >= this.fullCount;
	}

	public boolean isFull(int addCount) {
		return isClose || bsClose || actors.size() + addCount > this.fullCount;
	}

	public int logicServerId;
	public int areaId;
	public String instanceId;
	public int lineIndex = 1;
	public boolean hasPlayerEntered;
	/**
	 * {playerId, {rebornNum : 0, helpRevive : false,
	 * helpTime:System.currentMillions}}
	 */
	public Map<String, Actor> actors;
	public long emptyTime;
	public MapBase prop;
	public int sceneType;
	public int lifeTime;
	public Map<String, AreaItem> items;
	public Set<Integer> aliveBoss;
	public Set<String> diePlayers;// 离线阵亡的玩家

	public int fullCount;
	public int maxCount;

	protected boolean isClose;

	public boolean isClose() {
		return this.isClose;
	}

	public boolean isPlayerClose(WNPlayer player) {
		return this.isClose;
	}

	public String getSceneName() {
		return prop.name;
	}

	protected Future<?> timer_SceneEndTime = null;

	public final Map<Integer, RandomBoxExt.Point> tcPoints = new ConcurrentHashMap<>();

	/**
	 * @param whitePlayers : 场景角色白名单 map key:playerIds value:camp
	 */
	public Area(JSONObject opts) {
		Out.debug("create area opts:", opts);
		this.logicServerId = opts.containsKey("logicServerId") ? opts.getIntValue("logicServerId") : 0;
		this.areaId = opts.getIntValue("areaId");
		this.instanceId = opts.getString("instanceId");
		// this.resident = opts.getIntValue("resident");

		this.prop = AreaDataConfig.getInstance().get(this.areaId);

		this.init();
	}

	public boolean isNewPoint(RandomBoxExt.Point point) {
		for (RandomBoxExt.Point p : tcPoints.values()) {
			if (point.distance(p) < 1) {// modify distance less 5 to less 1,account for boxes are stacked together when
										// the distance of boxes are very tight
				return false;
			}
		}

		return true;
	}

	/**
	 * 创建新宝箱
	 */
	public void newRandomBox(RandomBoxExt box) {
		GWorld.getInstance().ansycExec(() -> {
			RandomBoxExt.Point point = box.randomPoint();
			int flag = 0;
			while (flag++ < 50 && !isNewPoint(point)) {
				point = box.randomPoint();
			}
			float direction = (float) (RandomUtil.getFloat() * Math.PI * 2);
			int objId = getXmdsManager().addUnit(instanceId, box.iD, Utils.toJSONString("force", Const.AreaForce.FORCEA.value, "name", box.name, "x", point.x, "y", point.y, "direction", direction));
			tcPoints.put(objId, point);
		});
	}

	private ScheduledFuture<?> robotDisponseJob;

	public void bindBattleServer(WNPlayer player) {
		this.serverId = player.getBattleServerId();
		Out.debug("bindBattleServer ", this.serverId);

		if (!isNormal()) {
			Map<String, TeamMemberData> members = player.getTeamManager().getTeamMembers();
			if (members != null) {
				for (TeamMemberData member : members.values()) {
					WNPlayer mPlayer = member.getPlayer();
					if (mPlayer != null) {
						onDailyActivity(mPlayer);
					}
				}
			} else {
				onDailyActivity(player);
			}
		}

		{
			// 幻境 种怪
			List<MonsterRefreshExt> props = GameData.findMonsterRefreshs(t -> {
				return t.mapID == this.areaId;
			});
			List<Integer> data = new ArrayList<>(props.size());
			if (props.size() > 0) {
				for (MonsterRefreshExt refreshExt : props) {
					Date bornBeginTime = AreaUtil.formatToday(refreshExt.rebornBeginTime);
					Date bornEndTime = AreaUtil.formatToday(refreshExt.rebornEndTime);
					long now = System.currentTimeMillis();
					if (bornBeginTime.getTime() <= now && bornEndTime.getTime() > now) {
						data.add(refreshExt.iD);
					}
				}
			}
			this.createMonster(data, true);
		}

		GameData.RandomBoxs.forEach((k, v) -> {
			if (v.startScene == areaId) {
				for (int i = 0; i < v.quantity; i++) {
					newRandomBox(v);
				}
			}
		});

		if (PlayerUtil.isRobot(player.player)) {
			if (robotDisponseJob == null) {
				robotDisponseJob = JobFactory.addDelayJob(() -> {
					dispose();
				}, 2 * GGlobal.TIME_MINUTE);
			}
		}
	}

	public ZoneManagerPrx getZoneManager() {
		return CSharpClient.getZoneManager(serverId);
	}

	public XmdsManagerPrx getXmdsManager() {
		return CSharpClient.getXmdsManager(serverId);
	}

	public String getServerId() {
		return this.serverId;
	}

	protected void init() {
		this.hasPlayerEntered = false;
		this.actors = new ConcurrentHashMap<>();
		this.emptyTime = GWorld.APP_TIME;
		this.sceneType = this.prop.type;
		this.lifeTime = this.prop.lifeTime * 1000;

		this.items = new ConcurrentHashMap<>(); // 场景道具
		this.aliveBoss = new ConcurrentSkipListSet<>();
	}

	/**
	 * 获取场景类型
	 */
	public int getSceneType() {
		return this.sceneType;
	}

	/**
	 * 场景角色人数是否为0
	 */
	public boolean isEmpty() {
		return this.getPlayerNum() <= 0;
	}

	/**
	 * 是否是普通场景
	 */
	public boolean isNormal() {
		return this.sceneType == SCENE_TYPE.NORMAL.getValue() || this.sceneType == SCENE_TYPE.ILLUSION.getValue() || this.sceneType == SCENE_TYPE.CROSS_SERVER.getValue();
	}

	/**
	 * 杀人是否掉落的场景
	 */
	public boolean isPKDrop() {
		return this.sceneType == SCENE_TYPE.NORMAL.getValue() || this.sceneType == SCENE_TYPE.ILLUSION.getValue() || this.sceneType == SCENE_TYPE.CROSS_SERVER.getValue() || this.sceneType == SCENE_TYPE.ILLUSION_2.getValue();
	}

	/**
	 * 是否需要队伍确认
	 * 
	 * @return
	 */
	public boolean needTeamConfirm(int type) {
		return type == Const.SCENE_TYPE.FIGHT_LEVEL.getValue() || type == Const.SCENE_TYPE.LOOP.getValue() || type == Const.SCENE_TYPE.DEMON_TOWER.getValue() || type == Const.SCENE_TYPE.RESOURCE_DUNGEON.getValue() || type == Const.SCENE_TYPE.ILLUSION_2.getValue();
	}

	/**
	 * 开放组队的场景
	 * 
	 * @return
	 */
	public boolean isOpenJoinTeamArea() {
		return isNormal() || this.sceneType == SCENE_TYPE.ILLUSION_2.getValue();
	}

	/**
	 * 当玩家没有以后不要回收的除了普通场景以外的场景
	 * 
	 * @return
	 */
	protected boolean noCloseIfNoPlayer() {
		return this.sceneType == SCENE_TYPE.GUILD_BOSS.getValue() || this.sceneType == SCENE_TYPE.ILLUSION_2.getValue();
	}

	/**
	 * 重置emptyTime
	 */
	public void resetEmptyTime() {
		if (this.isEmpty()) {
			this.emptyTime = GWorld.APP_TIME;
		}
	}

	/**
	 * 判断该场景是否有效
	 */
	public boolean isValid() {
		return !this.isEmpty() || (GWorld.APP_TIME - this.emptyTime) <= this.lifeTime;
	}

	/**
	 * 死亡后需要发送邮件的场景
	 */
	public boolean needSendKillMail() {
		return (this.sceneType == Const.SCENE_TYPE.NORMAL.getValue() || this.sceneType == Const.SCENE_TYPE.ILLUSION_2.getValue());
	}

	/**
	 * 场景没人情况下是否可以删除
	 */
	public boolean canCloseNoPlayer() {
		if (this.isEmpty()) {
			return true;
		}
		return false;
	}

	/*
	 * 副本到达开放时间的EndTime
	 */
	public void onSceneEndTime() {

	}

	/**
	 * 销毁场景强制退出场景内玩家
	 */
	protected void onDisponseLeave(WNPlayer player) {
		Out.info(player.getName(), " leave disponse area ", prop.name);
		PlayerTempPO temp = player.getPlayerTempData();
		AreaUtil.dispatchByAreaId(player, new AreaData(temp.historyAreaId, temp.historyX, temp.historyY), null);
	}

	protected Boolean isDispose = false;

	/**
	 * 场景销毁
	 */
	public void dispose() {
		dispose(false);
	}

	public void dispose(boolean processExit) {
		synchronized (isDispose) {
			if (isDispose)
				return;
			isDispose = true;
		}
		this.isClose = true;
		try {

			if (timer_SceneEndTime != null) {
				timer_SceneEndTime.cancel(true);
				timer_SceneEndTime = null;
			}

			if (!processExit) {
				for (Map.Entry<String, Actor> entry : this.actors.entrySet()) {
					String playerId = entry.getKey();
					WNPlayer player = this.getPlayer(playerId);
					if (player != null && player.getArea() == this && !player.isProxy()) {
						this.onDisponseLeave(player);
					}
				}
			}

			this.actors.clear();
		} catch (Exception e) {
			Out.error(e);
		} finally {
			Out.info("destroyZone areaid:", prop.name, " - ", this.areaId, "  instanceId:", this.instanceId);
			// 调用战斗服删除场景信息
			getZoneManager().destroyZoneRequest(this.instanceId);
		}
	}

	/**
	 * 分配阵营
	 */
	public void setForce(WNPlayer player) {
		player.setForce(Const.AreaForce.FORCEA.value);
	}

	/**
	 * 自动熔炼
	 */
	public boolean autoMelt(WNPlayer player, NormalEquip equip) {
		// 只有绑定的可熔炼装备才可自动熔炼
		if (!equip.isEquip() || equip.prop.noMelt == 1) {
			return false;
		}
		MeltConfigCO prop = ItemUtil.getMeltProp(equip.prop.meltLevel, equip.getQColor());
		if (prop == null) {
			return false;
		}
		if (player.isProxy()) {
			//
			return false;
		}

		List<Integer> meltColor = player.hookSetManager.getMeltQcolor();
		if (!meltColor.contains(equip.prop.qcolor)) {
			return false;
		}
		List<NormalItem> tcItems = ItemUtil.createItemsByTcCode(prop.tcCode);
		List<NormalItem> addItems = ItemUtil.getPackUpItems(tcItems);

		// 检查背包格子数
		if (!player.getWnBag().testEmptyGridLarge(ItemUtil.getPackUpItemsNum(addItems))) {
			return false;
		}

		if (prop.costGold > 0 && !player.moneyManager.costGold(prop.costGold, Const.GOODS_CHANGE_TYPE.melt)) {
			player.sendSysTip(LangService.getValue("GOLD_NOT_ENOUGH"));
			return false;
		}

		Map<Integer, Object> currencyList = new HashMap<>();
		currencyList.put(Const.CurrencyType.COIN.getValue(), prop.costGold);

		// 幻境 经验/银币/修为上限配置
		// if (this.sceneType == Const.SCENE_TYPE.ILLUSION.getValue()) {
		// for (NormalItem normalItem : addItems) {
		// if (normalItem.isVirtual()) {
		// VirtualItem vItem = (VirtualItem) normalItem;
		// if (normalItem.itemDb.code.equals("gold")) {
		// vItem.setWorth(player.illusionManager.addAward("gold", vItem.getWorth()));
		// } else if (normalItem.itemDb.code.equals("exp")) {
		// vItem.setWorth(player.illusionManager.addAward("exp", vItem.getWorth()));
		// } else if (normalItem.itemDb.code.equals("upexp")) {
		// vItem.setWorth(player.illusionManager.addAward("upexp", vItem.getWorth()));
		// }
		// }
		// }
		// }

		player.getWnBag().addEntityItems(addItems, Const.GOODS_CHANGE_TYPE.AUTO_MELT, currencyList);

		// 上报自动
		BILogService.getInstance().ansycReportMeltCultivate(player.getPlayer(), addItems);

		return true;
	}

	/**
	 * 是否可以拾取交互物品
	 * 
	 * @param itemCode
	 * @param itemNum
	 * @return
	 */
	protected boolean canPickInterActiveItem(WNPlayer player, String itemCode, int itemNum) {
		return true;
	}

	/**
	 * 采集场景中物品和福袋时触发此事件
	 * 
	 * @param player
	 * @param objId 场景编辑器里的物品id
	 * @param itemId 游戏服的物品id，策划配置
	 */
	public void onInterActiveItem(WNPlayer player, int objId, int itemId) {
		Point point = tcPoints.remove(objId);
		if (point != null) {
			Out.debug("interActiveItem : ", point);
		}
		RandomBoxExt box = GameData.RandomBoxs.get(itemId);
		if (StringUtil.isNotEmpty(box.tc)) {
			boxNormalTC(player, box.tc, (int) point.x, (int) point.y);
		}

		if (StringUtil.isNotEmpty(box.teamTc)) {
			boxTeamTC(player, box.teamTc);
		}
		if (StringUtil.isNotEmpty(box.personTc)) {
			List<NormalItem> dropItems = ItemUtil.createItemsByTcCode(box.personTc);
			if (dropItems != null && !dropItems.isEmpty()) {
				List<NormalItem> trueAdd = new ArrayList<>();
				for (NormalItem item : dropItems) {
					if (canPickInterActiveItem(player, item.itemCode(), item.getNum())) {
						trueAdd.add(item);
					}
				}
				if (!trueAdd.isEmpty()) {
					player.bag.addCodeItemMail(trueAdd, Const.ForceType.DEFAULT, GOODS_CHANGE_TYPE.random_box, SysMailConst.BAG_FULL_COMMON);
				}
			}
		}
		CharacterLevelCO prop = GameData.CharacterLevels.get(player.getLevel());
		int playerLevelUpExp = prop.experience;
		int exp = Math.round(playerLevelUpExp * box.expRatio / 10000F);
		if (exp > 0)
			player.addExp(exp, GOODS_CHANGE_TYPE.random_box);
		int upexp = Math.round((player.getLevel() - 1) * box.upExpRatio / 10000F);
		if (upexp > 0)
			player.addUpExp(upexp, GOODS_CHANGE_TYPE.random_box);
		int gold = Math.round((player.getLevel() - 1) * box.goldPerMonLv / 10000F);
		if (gold > 0)
			player.moneyManager.addGold(gold, GOODS_CHANGE_TYPE.random_box);

		if (box.startScene == areaId && StringUtil.isNotEmpty(box.startPoint)) {
			JobFactory.addDelayJob(() -> {
				newRandomBox(box);
			}, box.refreshTime * 1000);
		}

	}

	/**
	 * on fighting boss
	 * 
	 * @param playerId
	 */
	public void onKillBoss(String playerId) {
		
	}

	/**
	 * battling report from battle server
	 * 
	 * @param datas
	 */
	public void onBattleReport(List<DamageHealVO> datas) {

	}

	/**
	 * 拾取道具
	 * 
	 * @param playerId
	 * @param itemId
	 * @param isGuard
	 * @return
	 */
	public AreaItem onPickItem(String playerId, String itemId, boolean isGuard) {
		AreaItem areaItem = this.items.get(itemId);
		if (areaItem != null && (areaItem.bindPlayers == null || areaItem.bindPlayers.containsKey(playerId) || System.currentTimeMillis() - areaItem.createTime > GlobalConfig.itemdrop_lock_lifeTime)) {
			Actor actor = getActor(playerId);
			if (actor == null || !actor.profitable) {
				Out.debug(playerId, "当前不可拾取", itemId);
				return null;
			}
			WNPlayer player = getPlayer(playerId);

			this.items.remove(itemId);
			Out.debug(getClass(), player.getName(), " onPickItem itemId:", itemId, ",isGuard:", isGuard);
			int groupCount = areaItem.item.itemDb.groupCount;
			this.onFreedomPickItem(player, areaItem.item, isGuard);
			areaItem.item.setGroup(groupCount);
			if (areaItem.dropPlayer != null) {
				this.onPickPlayerDropItem(player, areaItem);
			} else {
				this.onPickMonsterDropItem(player, areaItem);
			}
			return areaItem;
		}
		return null;
	}

	/**
	 * 自由拾取道具
	 */
	public void onFreedomPickItem(WNPlayer player, NormalItem item, boolean isGuard) {
		if (player.isProxy()) {
			player.onProxyEvent(13, body -> {
				body.writeString(Utils.serialize(item.itemDb));
				body.writeBoolean(isGuard);
			});
			return;
		}
		Out.debug("onFreedomPickItem playerId:", player.getName(), "itemId:", item.itemDb.id, " isbind:", item.isBinding());
		boolean canAutoMelt = isGuard && item.isEquip() ? autoMelt(player, (NormalEquip) item) : false;
		if (!canAutoMelt) {
			// 没有熔炼,加道具
			if (!player.getWnBag().testEmptyGridLarge(1)) {
				return;
			}
			// isSpecialItem(player, item);
			boolean isSilient = isGuard ? true : false;
			player.getWnBag().addEntityItem(item, Const.GOODS_CHANGE_TYPE.monsterdrop, null, false, isSilient);
			FinishPickItem data = item.toJSON4PickItemBatterServer();

			if (isGuard) { // 自动战斗背包满了弹框
				player.getWnBag().testEmptyGridLarge(1);
			}
			Out.debug(getClass(), "onFinishPickItem:", data);
			getXmdsManager().onFinishPickItem(player.getId(), JSON.toJSONString(data));
		}
	}

	/**
	 * 角色掉落道具
	 */
	protected void onPickPlayerDropItem(WNPlayer player, AreaItem itemInfo) {
		// 判断 掉落者
		WNPlayer dropPlayer = itemInfo.dropPlayer;
		if (dropPlayer != null && itemInfo.item.prop.qcolor >= Const.ItemQuality.PURPLE.getValue()) {
			NormalItem item = itemInfo.item;
			float dropX = itemInfo.dropX;
			float dropY = itemInfo.dropY;
			Out.debug(getClass(), "玩家 ", player.getName(), "拾起了物品", item.itemDb.id);
			Map<String, Object> datatmp = new HashMap<>(6);
			Map<String, Object> data = new HashMap<>(6);

			datatmp.put("MsgType", 3);
			datatmp.put("s2c_playerId", player.getId());
			datatmp.put("s2c_name", player.getName());
			datatmp.put("s2c_level", player.getLevel());
			datatmp.put("s2c_pro", player.getPro());

			String str1 = LangService.getValue("GREEN_LINK") + "在";
			str1 = str1.replace("{b}", JSON.toJSONString(datatmp));
			str1 = str1.replace("{a}", player.getName());
			datatmp.clear();
			datatmp.put("MsgType", 6);
			data.put("areaId", this.areaId);
			data.put("targetX", dropX);
			data.put("targetY", dropY);
			datatmp.put("data", data);

			String str2 = LangService.getValue("GREEN_LINK") + "拾起了";
			str2 = str2.replace("{b}", JSON.toJSONString(datatmp));
			str2 = str2.replace("{a}", this.prop.name + "(" + (dropX) + "," + (dropY) + ")");

			datatmp.clear();
			datatmp.put("MsgType", 3);
			datatmp.put("s2c_playerId", dropPlayer.getId());
			datatmp.put("s2c_name", dropPlayer.getName());
			datatmp.put("s2c_level", dropPlayer.getLevel());
			datatmp.put("s2c_pro", dropPlayer.getPro());

			String str3 = LangService.getValue("GREEN_LINK") + "的";
			str3 = str3.replace("{b}", JSON.toJSONString(datatmp));
			str3 = str3.replace("{a}", dropPlayer.getName());

			datatmp.clear();
			datatmp.put("MsgType", 1);
			datatmp.put("Id", item.itemDb.id);
			datatmp.put("PlayerId", dropPlayer.getId());
			datatmp.put("Name", item.prop.name);
			datatmp.put("Quality", item.itemDb.groupCount);
			datatmp.put("TemplateId", item.itemDb.code);

			String str4 = LangService.getValue(MessageUtil.getColorLink(item.prop.qcolor));
			str4 = str4.replace("{b}", JSON.toJSONString(datatmp));
			str4 = str4.replace("{a}", item.prop.name + "×" + item.itemDb.groupCount);

			String content = str1 + str2 + str3 + str4;
			OnChatPush.Builder msg = MessageUtil.createChatMsg(player, content, Const.CHAT_SCOPE.SYSTEM, item.prop.qcolor >= Const.ItemQuality.ORANGE.getValue() ? TipsType.BLACK : TipsType.NORMAL);
			GWorld.getInstance().broadcast(new MessagePush("chat.chatPush.onChatPush", msg.build()), logicServerId);
		}
	}

	/**
	 * 怪物掉落道具
	 */
	protected void onPickMonsterDropItem(WNPlayer player, AreaItem itemInfo) {
		int pickItemQcolor = GlobalConfig.World_Boss_Pick_ItemQcolor;
		if (itemInfo.monsterId != 0 && itemInfo.item.prop.qcolor >= pickItemQcolor) {
			MonsterBase monsterProp = MonsterConfig.getInstance().get(itemInfo.monsterId);
			if (monsterProp != null) {
				NormalItem item = itemInfo.item;
				int index = Utils.random(1, 3);
				String content = LangService.getValue("MONSTER_BOSS_PICK_ITEM_MESSAGE" + index);
				content = content.replace("{mapName}", this.getSceneName());
				String strData = MessageUtil.getPlayerNameColor(player.getName(), player.getPro());
				content = content.replace("{playerName}", strData);
				String monsterName = MessageUtil.getMonsterName(monsterProp.name, monsterProp.qcolor);
				content = content.replace("{monsterName}", monsterName);
				Map<String, Object> datatmp = new HashMap<>();
				datatmp.put("MsgType", 1);
				datatmp.put("Id", item.itemDb.id);
				datatmp.put("Name", item.prop.name);
				datatmp.put("Quality", item.itemDb.groupCount);
				datatmp.put("TemplateId", item.itemDb.code);
				String strItem = LangService.getValue(MessageUtil.getColorLink(item.getQLevel()));
				strItem = strItem.replace("{b}", JSON.toJSONString(datatmp));
				strItem = strItem.replace("{a}", item.prop.name + "×" + item.itemDb.groupCount);

				content = content.replace("{item}", strItem);

				String finalcontent = content;
				GWorld.getInstance().ansycExec(() -> {
					if (item.prop.qcolor >= Const.ItemQuality.GREEN.getValue()) {
						for (GPlayer p : PlayerUtil.getAllOnlinePlayer()) {
							((WNPlayer) p).sendSysTip(finalcontent, TipsType.ROLL);
						}
						MessageUtil.sendRollChat(GWorld.__SERVER_ID, finalcontent, Const.CHAT_SCOPE.SYSTEM);
					}
				});
			}
		}
	}

	/**
	 * 场景结算事件
	 */
	public void onGameOver(JSONObject event) {}

	/**
	 * 往场景中添加单位 传入参数 instanceId :场景id data : 单位数组，{id：模版id, force：阵营, flag:
	 * 在场景中的路点或region， x:直接指定x坐标,y:直接指定y坐标｝ flag和x，y任选其一 eg: [
	 * {id:108403,force:Const.AreaForce.FORCEB,flag:"Start6"},
	 * {id:108403,force:Const.AreaForce.FORCEB,x:30,y:80},
	 * {id:108403,force:Const.AreaForce.FORCEB,flag:"Start8"}, ]
	 */
	public void addUnitsToArea(List<MonsterUnit> data) {
		if (data.size() > 0) {
			GWorld.getInstance().ansycExec(() -> {
				getXmdsManager().addUnits(instanceId, JSON.toJSONString(data));
			});
		}
	}

	/**
	 * 创建怪物
	 */
	public void createMonster(List<Integer> data, boolean bInit) {
		if (data.size() == 0) {
			return;
		}
		List<MonsterUnit> monsterData = new ArrayList<>();
		synchronized (aliveBoss) {
			for (int id : data) {
				GameData.MonsterRefreshs.values().forEach(co -> {
					if (co.iD == id && co.mapID == areaId) {
						MonsterUnit md = new MonsterUnit();
						md.id = co.monsterID;
						md.force = Const.AreaForce.MONSTER.value;
						md.flag = co.getRefreshPoint();
						md.autoGuard = true;
						md.unique = true;
						// md.level = 50;//TODO 50 only for test guild boss
						monsterData.add(md);
						if (!aliveBoss.contains(id)) {
							this.aliveBoss.add(id);
						}

						Out.info("addUnitsToArea:", prop.name, ",", areaId, ",", co.monsterID, ",", co.getRefreshPoint());
					}
				});
			}
		}

		if (monsterData.size() > 0) {
			addUnitsToArea(monsterData);
		}

	}

	public void isSpecialItem(WNPlayer player, NormalItem item) {
		if (item.prop.qcolor >= Const.ItemQuality.ORANGE.getValue()) {
			String link = ChannelUtil.setItemInfo(item);
			String key = MessageUtil.getColorLink(item.prop.qcolor);
			String str = LangService.getValue(key);
			str = str.replace("{b}", link);
			str = str.replace("{a}", item.prop.name);

			String playerName = MessageUtil.getPlayerNameColor(player.getName(), player.player.pro);
			String final_str = playerName + "获得" + str;

			DaoYouPO daoYou = DaoYouService.getInstance().getDaoYou(player.getId());
			if (daoYou != null) {
				DaoYouService.getInstance().addDaoYouMessage(Const.DaoYou.DaoYouMessageTypeSystem.getValue(), "", daoYou, final_str);
			}
		}
	}

	/**
	 * 是否可触发TC
	 */
	public boolean isUseTC() {
		return true;
	}

	/**
	 * 移除活着的BOSS
	 */
	public void removeAliveBoss(Integer monsterId, String refreshPoint) {
		List<MonsterRefreshExt> refreshProps = GameData.findMonsterRefreshs(t -> {
			return t.monsterID == monsterId && t.mapID == this.areaId && t.containsRefreshPoint(refreshPoint);
		});

		if (!refreshProps.isEmpty()) {
			MonsterRefreshCO refresh = refreshProps.get(0);
			if (aliveBoss.remove(refresh.iD)) {
				Out.debug("成功移除还活着的BOSS, monsterId=", monsterId);
			}
		}
	}

	/**
	 * 怪物死亡处理接口
	 * 
	 * @params monsterId:怪物id level：怪物等级 x:x y:y player:归属者 attackType 1:宠物：
	 *         refreshPoint：刷新点 teamSharedIdList:
	 *         队伍成员(距离以内的)atkAssistantList:所有参与打伤害的玩家
	 * @return 掉落集合
	 */
	public void onMonsterDead(int monsterId, int level, float x, float y, int attackType, String refreshPoint, WNPlayer player, JSONArray teamSharedIdList, JSONArray atkAssistantList) {
		Out.debug(player.getName(), " kill ", monsterId);
		MonsterBase monsterProp = MonsterConfig.getInstance().get(monsterId);
		if (monsterProp == null) {
			Out.warn("onMonsterDead can not get prop from monsterProps:", monsterId);
			return;
		}

		CharacterLevelCO charactorProp = GameData.CharacterLevels.get(player.getLevel());
		int playerLevelUpExp = charactorProp.experience;

		List<MonsterRefreshExt> refreshProps = GameData.findMonsterRefreshs(t -> {
			return t.monsterID == monsterId && t.mapID == this.areaId && t.containsRefreshPoint(refreshPoint);
		});
		// 首领 boss掉落集合
		List<NormalItem> list_announce_item = new ArrayList<>();

		// 距离范围内的队伍成员
		Map<String, WNPlayer> sharedDropPlayers = new HashMap<>(5);
		Map<String, WNPlayer> sharedExpPlayers = new HashMap<>(5);
		boolean canFightLevelDrop = player.fightLevelManager.canDrop(monsterId, this);
		boolean canNormapDrop = player.dropManager.canDrop(monsterId, this);
		if (canFightLevelDrop && canNormapDrop) {
			sharedDropPlayers.put(player.getId(), player);
			sharedExpPlayers.put(player.getId(), player);
		} else if (canFightLevelDrop) {
			sharedExpPlayers.put(player.getId(), player);
		}

		int teamExpAdd = 0; // 策划要求预留，后期实现
		if (teamSharedIdList != null) {
			teamExpAdd = TeamUtil.getTeamExpAdd(teamSharedIdList.size());
			teamSharedIdList.forEach(teamSharedId -> {
				Actor actor = getActor((String) teamSharedId);
				if (actor != null && actor.profitable) {
					WNPlayer bindPlayer = this.getPlayer((String) teamSharedId);
					if (bindPlayer != null) {
						boolean canFightLevelDropOther = bindPlayer.fightLevelManager.canDrop(monsterId, this);
						boolean canNormapDropOther = bindPlayer.dropManager.canDrop(monsterId, this);
						if (canFightLevelDropOther && canNormapDropOther) {
							sharedDropPlayers.put(bindPlayer.getId(), bindPlayer);
							sharedExpPlayers.put(bindPlayer.getId(), bindPlayer);
						} else if (canFightLevelDropOther) {
							sharedExpPlayers.put(bindPlayer.getId(), bindPlayer);
						}
					}
				}
			});
		}
		if (sharedExpPlayers.size() > 0) {
			this.addExp(player, sharedExpPlayers.values(), attackType, monsterProp, level, teamExpAdd);
		}

		if (!isUseTC()) {
			return;
		}

		// 队伍内所有玩家(不在距离内的)
		Map<String, WNPlayer> bindPlayers = new HashMap<>(5);
		if (player.fightLevelManager.canDrop(monsterId, this) && player.dropManager.canDrop(monsterId, this)) {
			bindPlayers.put(player.getId(), player);
		}

		if (monsterProp.type >= 4 && isNormal()) {
			BILogService.getInstance().ansycReportKillBoss(player.getPlayer(), this.sceneType, monsterId);
			player.sevenGoalManager.processGoal(SevenGoalTaskType.AREA_BOSS_KILL_COUNT);
		}

		Collection<String> teamMembers = player.getTeamMembers();
		if (teamMembers != null) {
			teamExpAdd = TeamUtil.getTeamExpAdd(teamMembers.size());
			for (String playerId : teamMembers) {
				Actor actor = getActor(playerId);

				if (actor == null || !actor.profitable)
					continue;
				WNPlayer bindPlayer = this.getPlayer(playerId);
				if (bindPlayer != null && bindPlayer != player && bindPlayer.fightLevelManager.canDrop(monsterId, this) && bindPlayer.dropManager.canDrop(monsterId, this)) {
					bindPlayers.put(bindPlayer.getId(), bindPlayer);

					if (monsterProp.type >= 4 && isNormal()) {
						BILogService.getInstance().ansycReportKillBoss(bindPlayer.getPlayer(), this.sceneType, monsterId);
						bindPlayer.sevenGoalManager.processGoal(SevenGoalTaskType.AREA_BOSS_KILL_COUNT);
					}
				}
			}
		}

		// 参与奖（发邮件）
		if (atkAssistantList != null) {
			for (Object o : atkAssistantList) {
				String playerId = (String) o;
				if ((teamMembers != null && teamMembers.contains(playerId)) || playerId.equals(player.player.id)) {// 击杀的人如果有队友那么就不该领取了
					continue;
				}
				WNPlayer bindPlayer = this.getPlayer(playerId);
				if (bindPlayer == null || bindPlayer.area == null) {
					continue;
				}
				if (bindPlayer.area.areaId == this.areaId) {// 玩家和BOSS必须再同一个场景下
					bindPlayer.dropManager.sendJoinReward(monsterId, monsterProp.name, refreshProps);

					if (monsterProp.type >= 4 && isNormal()) {
						BILogService.getInstance().ansycReportKillBoss(bindPlayer.getPlayer(), this.sceneType, monsterId);
					}
				}
			}
		}

		if (bindPlayers.size() == 0 && sharedDropPlayers.size() == 0) {
			return;
		}

		List<ItemToBtlServerData> itemsPayLoad = new ArrayList<>();

		// 首杀TC 通用TC处理
		Map<Integer, String> tcMap = monsterProp.firstTcMap;
		String tcCode = tcMap.get(player.getPro());
		if (StringUtil.isNotEmpty(tcCode)) {
			List<Integer> firstKillMonsterIds = player.playerAttachPO.firstKillMonsterIds;
			if (firstKillMonsterIds.contains(monsterId)) {
				tcCode = monsterProp.tc;
			} else {
				player.playerAttachPO.addFirstMonsterId(monsterId);
			}
		} else {
			tcCode = monsterProp.tc;
		}

		boolean over = false;
		boolean isTeamTc = false;
		do {
			if (StringUtil.isNotEmpty(tcCode)) {
				List<NormalItem> dropItems = ItemUtil.createItemsByRealTC(tcCode, player.getLevel());
				if (refreshProps.size() > 0) {
					list_announce_item.addAll(dropItems);
				}
				if (pointItems == null) {
					pointItems = new HashMap<>();
				}
				for (NormalItem dropItem : dropItems) {
					if (dropItem.isVirtQuest()) {
						for (WNPlayer belongPlayer : bindPlayers.values()) {
							Actor actor = getActor(belongPlayer.getId());
							if (actor == null || !actor.profitable) {
								continue;
							}
							belongPlayer.onEvent(new TaskEvent(EventType.collect, dropItem.itemDb.code, dropItem.itemDb.groupCount));
						}
					} else if (dropItem.isVirtual()) {
						VirtualItem virItem = (VirtualItem) dropItem;
						if ("gold".equals(dropItem.itemDb.code)) {
							virItem.dropResetWorth(level, monsterProp.goldPerMonLv, bindPlayers.size());
						} else if ("exp".equals(dropItem.itemDb.code)) {
							virItem.dropResetWorth(level, monsterProp.expRatio, playerLevelUpExp);
						}
						int originWorth = virItem.getWorth();
						for (WNPlayer belongPlayer : bindPlayers.values()) {
							Actor actor = getActor(belongPlayer.getId());
							if (actor == null || !actor.profitable) {
								continue;
							}
							if ("gold".equals(dropItem.itemDb.code)) {
								int extGold = belongPlayer.getBtlExdGold();
								extGold += belongPlayer.getGuildExdGold();
								virItem.addWorth(originWorth * extGold / 10000);
								virItem.addWorth((int) (virItem.getWorth() * belongPlayer.vipManager.getExtGoldRatio()));
							} else if ("exp".equals(dropItem.itemDb.code)) {
								// 经验卡加成
								int exdExp = belongPlayer.bufferManager.getBuffAttrValue("ExdExp");
								// 工会祈福加成
								exdExp += belongPlayer.getGuildExdExp();

								virItem.addWorth(originWorth * exdExp / 10000); // 经验卡加成
							}
							// 幻境金币/经验/修为限制
							if (this.sceneType == Const.SCENE_TYPE.ILLUSION.getValue()) {
								virItem.setWorth(belongPlayer.illusionManager.addAward(dropItem.itemDb.code, virItem.getWorth()));
							}
							addVirtureItem(belongPlayer, dropItem, GOODS_CHANGE_TYPE.monsterdrop);
							virItem.setWorth(originWorth); // 针对每个人修改后，要改回原值
						}
					} else {
						Set<String> team = player.getTeamMembers();
						if (isTeamTc && team != null && getCurAreaMember(player) > 1 && dropItem.getQLevel() >= GlobalConfig.Dis_Roll_quality) {

							PointItem pointItem = new PointItem(dropItem.itemCode(), dropItem.getNum(), getSceneType());
							pointItem.bindTeam(player);
							pointItems.put(pointItem.id, pointItem);
							Out.debug("======================pointItem add ", pointItem.id, " :", pointItem.itemCode);
						} else {
							AreaItem areaItem = new AreaItem(dropItem);
							areaItem.bindPlayers = bindPlayers;
							areaItem.monsterId = monsterId;

							// 这里本来说是没有队伍掉场景，后来和刚子商量后还是直接扔背包吧！
							this.items.put(dropItem.itemDb.id, areaItem);
							itemsPayLoad.add(dropItem.toJSON4BatterServer(bindPlayers.keySet(), Const.TEAM_DISTRIBUTE_TYPE.FREEDOM, false));
							// ItemDropPush.Builder push = ItemDropPush.newBuilder();
							// ItemDrop.Builder item = dropItem.toProto4Client();
							// item.setLifeTime(1000);
							// push.addItems(item);
							// boolean canAutoMelt = dropItem.isEquip() ? autoMelt(player, (NormalEquip)
							// dropItem) : false;
							// if (!canAutoMelt) {
							// if (!player.bag.testAddCodeItem(dropItem.itemCode(), dropItem.getNum())) {
							// if (player.playerTempData.sendMailItemNum < Const.AUTO_PICKUP_LIMIT) {
							// player.playerTempData.sendMailItemNum += 1;
							// boolean canDrop = player.illusionManager.addItemNum(this,
							// dropItem.itemCode(), dropItem.getNum());
							// if (canDrop) {
							// player.bag.addCodeItemMail(dropItem.itemCode(), dropItem.getNum(), null,
							// GOODS_CHANGE_TYPE.fight_level, SysMailConst.BAG_FULL_COMMON);
							// }
							// }
							// } else {
							// boolean canDrop = player.illusionManager.addItemNum(this,
							// dropItem.itemCode(), dropItem.getNum());
							// if (canDrop) {
							// player.bag.addCodeItemMail(dropItem.itemCode(), dropItem.getNum(), null,
							// GOODS_CHANGE_TYPE.fight_level, SysMailConst.BAG_FULL_COMMON);
							// }
							// }
							// }
							// if (push.getItemsCount() > 0) {
							// push.setX(Math.round(x * 100));
							// push.setY(Math.round(y * 100));
							// player.receive("area.battlePush.itemDropPush", push.build());
							// }
						}
					}
				}

				sendThrowPointItemListPush(player, monsterId);
			}
			if (over)
				break;
			tcCode = monsterProp.teamTc;
			isTeamTc = true;
			over = true;
		} while (true);

		tcCode = monsterProp.personTcMap.get(player.getPro());
		if (StringUtil.isNotEmpty(tcCode)) {
			Set<String> team = player.getTeamMembers();
			for (Map.Entry<String, Actor> entry : actors.entrySet()) {
				if (!entry.getValue().profitable) {
					continue;
				}
				WNPlayer member = getPlayer(entry.getKey());
				if ((team != null && member != null && team.contains(member.getId())) || member == player) {
					if (!(member.fightLevelManager.canDrop(monsterId, this) && member.dropManager.canDrop(monsterId, this))) {
						continue;
					}
					List<NormalItem> dropItems = ItemUtil.createItemsByRealTC(tcCode, player.getLevel());
					if (refreshProps.size() > 0) {
						list_announce_item.addAll(dropItems);
					}
					ItemDropPush.Builder push = ItemDropPush.newBuilder();
					for (NormalItem dropItem : dropItems) {
						if (dropItem.isVirtQuest()) {
							member.onEvent(new TaskEvent(EventType.collect, dropItem.itemDb.code, dropItem.itemDb.groupCount));
						} else if (dropItem.isVirtual()) {
							VirtualItem virItem = (VirtualItem) dropItem;
							if ("gold".equals(dropItem.itemDb.code)) {
								virItem.dropResetWorth(level, monsterProp.goldPerMonLv, bindPlayers.size());
								int extGold = member.getBtlExdGold();
								extGold += member.getGuildExdGold();
								virItem.addWorth((int) Math.floor(dropItem.getWorth() * extGold / 10000));
								virItem.addWorth((int) (virItem.getWorth() * member.vipManager.getExtGoldRatio()));
							} else if ("exp".equals(dropItem.itemDb.code)) {
								virItem.dropResetWorth(level, monsterProp.expRatio, playerLevelUpExp);
								int exdExp = member.bufferManager.getBuffAttrValue("ExdExp");// 经验加成万分比
								exdExp += member.getGuildExdExp();
								virItem.addWorth((int) Math.floor(dropItem.getWorth() * exdExp / 10000));
							}
							// 幻境金币/经验/修为限制
							if (this.sceneType == Const.SCENE_TYPE.ILLUSION.getValue()) {
								virItem.setWorth(member.illusionManager.addAward(dropItem.itemDb.code, virItem.getWorth()));
							}
							addVirtureItem(member, dropItem, GOODS_CHANGE_TYPE.monsterdrop);
						} else {
							ItemDrop.Builder item = dropItem.toProto4Client();
							item.setLifeTime(1000);
							push.addItems(item);
							boolean canAutoMelt = dropItem.isEquip() ? autoMelt(member, (NormalEquip) dropItem) : false;
							if (!canAutoMelt) {
								if (!member.bag.testAddCodeItem(dropItem.itemCode(), dropItem.getNum())) {
									if (member.playerTempData.sendMailItemNum < Const.AUTO_PICKUP_LIMIT) {
										member.playerTempData.sendMailItemNum += 1;
										boolean canDrop = member.illusionManager.addItemNum(this, dropItem.itemCode(), dropItem.getNum());
										if (canDrop) {
											member.bag.addEntityItemMail(dropItem, GOODS_CHANGE_TYPE.fight_level, SysMailConst.BAG_FULL_COMMON);
											// member.bag.addCodeItemMail(dropItem.itemCode(), dropItem.getNum(), null,
											// GOODS_CHANGE_TYPE.fight_level, SysMailConst.BAG_FULL_COMMON);
										}
									}
								} else {
									boolean canDrop = member.illusionManager.addItemNum(this, dropItem.itemCode(), dropItem.getNum());
									if (canDrop) {
										member.bag.addEntityItemMail(dropItem, GOODS_CHANGE_TYPE.fight_level, SysMailConst.BAG_FULL_COMMON);
										// member.bag.addCodeItemMail(dropItem.itemCode(), dropItem.getNum(), null,
										// GOODS_CHANGE_TYPE.fight_level, SysMailConst.BAG_FULL_COMMON);
									}
								}
							}
						}
					}
					if (push.getItemsCount() > 0) {
						push.setX(Math.round(x * 100));
						push.setY(Math.round(y * 100));
						member.receive("area.battlePush.itemDropPush", push.build());
					}
				}
			}
		}

		// 记录已经获取该boss怪物收益的用户
		for (WNPlayer p : sharedDropPlayers.values()) {
			p.fightLevelManager.onBossDead(monsterId, this);
		}
		if (this.sceneType == Const.SCENE_TYPE.NORMAL.getValue()) {
			for (WNPlayer p : sharedDropPlayers.values()) {
				p.dropManager.onBossDead(monsterId);
			}
		}

		if (refreshProps.size() > 0) {
			MonsterRefreshCO refresh = refreshProps.get(0);

			for (String playerId : this.actors.keySet()) {
				WNPlayer _player = this.getPlayer(playerId);
				// useType==1为boss，2为攻城小怪
				if (_player != null && refresh.useType == 1) {
					// 更新活跃度
					_player.dailyActivityMgr.onEvent(Const.DailyType.ILLSION_BOSS, "0", 1);
					// 成就
					_player.achievementManager.onKillBoss(1);
				}
			}
			List<NormalItem> list_announce_temp = new ArrayList<>();
			for (NormalItem item : list_announce_item) {
				if (item.isVirtQuest()) {
					continue;
				}
				if (item.getQLevel() < Const.ItemQuality.PURPLE.getValue()) {
					continue;
				}
				list_announce_temp.add(item);
			}
			if (refresh.msgSend > 0 && list_announce_temp.size() > 0) {
				String msg = null;
				StringBuffer str_item = new StringBuffer();
				int count = 0;
				for (NormalItem item : list_announce_temp) {
					count++;
					str_item.append(ChannelUtil.getChatLinkItem(item));
					str_item.append("*" + item.getNum());
					if (count < list_announce_temp.size()) {
						str_item.append("、 ");
					}
				}
				if (bindPlayers.size() > 1) {
					msg = LangService.getValue("MONSTER_BOSS_KILLED_TEAM");
					StringBuffer playerName = new StringBuffer();
					count = 0;
					for (WNPlayer p : bindPlayers.values()) {
						count++;
						if (p == null)
							continue;
						playerName.append(MessageUtil.getPlayerNameColor(p.getName(), p.getPro()));
						if (count < bindPlayers.size()) {
							playerName.append("、 ");
						}
					}
					msg = msg.replace("{0}", playerName.toString());
					MapBase areaProp = AreaUtil.getAreaProp(this.areaId);
					msg = msg.replace("{1}", areaProp.name);
					String monsterName = MessageUtil.getMonsterName(monsterProp.name, monsterProp.qcolor);
					msg = msg.replace("{2}", monsterName);
					msg = msg.replace("{3}", str_item.toString());
				} else {
					msg = LangService.getValue("MONSTER_BOSS_KILLED_ONE");
					String strData = MessageUtil.getPlayerNameColor(player.getName(), player.getPro());
					msg = msg.replace("{0}", strData);
					MapBase areaProp = AreaUtil.getAreaProp(this.areaId);
					msg = msg.replace("{1}", areaProp.name);
					String monsterName = MessageUtil.getMonsterName(monsterProp.name, monsterProp.qcolor);
					msg = msg.replace("{2}", monsterName);
					msg = msg.replace("{3}", str_item.toString());
				}

				OnChatPush.Builder chat = MessageUtil.createChatMsg(player, msg, CHAT_SCOPE.SYSTEM, TipsType.NORMAL);
				GWorld.getInstance().broadcast(new MessagePush("chat.chatPush.onChatPush", chat.build()), logicServerId);
			}
		}

		if (itemsPayLoad.size() > 0) {
			// 向战斗服发送怪物死亡物品掉落数据 这里map封装转json待验证
			String data = Utils.toJSON("pos", Utils.ofMap("x", x, "y", y), "items", itemsPayLoad).toJSONString();
			Out.debug("onMonsterDead drops :", data);
			getXmdsManager().onMonsterDiedDrops(instanceId, data);
		}
	}

	/**
	 * 记录已消灭的怪物id
	 */
	public void benifitTreasure(String playerId, int monsterId) {

	}

	/**
	 * 判断是否可获取某怪的收益
	 */
	public boolean benifitable(String playerId, int monsterId) {
		return true;
	}

	/**
	 * 随机宝箱 队伍TC
	 */
	public void boxTeamTC(WNPlayer player, String tc) {
		if (pointItems == null) {
			pointItems = new HashMap<>();
		}
		List<NormalItem> dropItems = ItemUtil.createItemsByTcCode(tc);
		for (NormalItem dropItem : dropItems) {
			if (getCurAreaMember(player) > 1 && dropItem.getQLevel() >= GlobalConfig.Dis_Roll_quality) {
				PointItem pointItem = new PointItem(dropItem.itemCode(), dropItem.getNum(), getSceneType());
				pointItem.bindTeam(player);
				pointItems.put(pointItem.id, pointItem);
			} else {
				player.bag.addCodeItemMail(dropItem.itemCode(), dropItem.getNum(), null, GOODS_CHANGE_TYPE.random_box, SysMailConst.BAG_FULL_COMMON);
			}
		}

		sendThrowPointItemListPush(player, 0);
	}

	/**
	 * 随机宝箱 常规TC
	 */
	public void boxNormalTC(WNPlayer player, String tc, int x, int y) {
		List<ItemToBtlServerData> itemsPayLoad = new ArrayList<>();

		Map<String, WNPlayer> bindPlayers = new HashMap<>(5);
		// bindPlayers.put(player.getId(), player);
		for (String playerId : this.actors.keySet()) {
			WNPlayer actor = this.getPlayer(playerId);
			if (actor != null) {
				bindPlayers.put(playerId, actor);
			}
		}

		List<NormalItem> dropItems = ItemUtil.createItemsByTcCode(tc);
		for (NormalItem dropItem : dropItems) {
			AreaItem areaItem = new AreaItem(dropItem);
			areaItem.bindPlayers = bindPlayers;
			areaItem.monsterId = 0;
			this.items.put(dropItem.itemDb.id, areaItem);
			itemsPayLoad.add(dropItem.toJSON4BatterServer(bindPlayers.keySet(), Const.TEAM_DISTRIBUTE_TYPE.FREEDOM, false));
		}
		if (itemsPayLoad.size() > 0) {
			// 向战斗服发送怪物死亡物品掉落数据 这里map封装转json待验证
			String data = Utils.toJSON("pos", Utils.ofMap("x", x, "y", y), "items", itemsPayLoad).toJSONString();
			Out.debug("onMonsterDead drops :", data);
			getXmdsManager().onMonsterDiedDrops(instanceId, data);
		}
	}

	/**
	 * 获取队伍在当前场景的人数
	 */
	private int getCurAreaMember(WNPlayer player) {
		if (player == null)
			return 0;
		Set<String> team = player.getTeamMembers();
		if (team == null)
			return 0;
		int currentMember = 0;
		for (String playerId : team) {
			WNPlayer mPlayer = getPlayer(playerId);
			if (mPlayer != null && mPlayer.getArea() == this) {
				currentMember++;
			}
		}
		return currentMember;
	}

	protected void addVirtureItem(WNPlayer player, NormalItem dropItem, GOODS_CHANGE_TYPE type) {
		player.getWnBag().addEntityItem(dropItem, type);
	}

	/**
	 * @param type 1:原地复活；2:出生点复活；3:复活点复活. 4:技能复活.
	 */
	public String reliveData(ReliveType type) {
		return Utils.toJSONString("type", type.value, "qty", 0, "itemType", "diamond", "hp", 0, "mp", 0);
	}

	public void recordDie(String playerId) {
		if (isNormal())
			return;
		if (diePlayers == null) {
			diePlayers = new ConcurrentSkipListSet<>();
		}
		diePlayers.add(playerId);
	}

	/**
	 * 复活操作
	 */
	public ReliveResponse.Builder relive(String playerId, ReliveType reliveType) {
		ReliveResponse.Builder res = ReliveResponse.newBuilder();
		Actor actor = getActor(playerId);
		WNPlayer player = this.getPlayer(playerId);
		if (actor == null || player == null) {
			// 此角色不存在
			res.setS2CCode(PomeloRequest.FAIL);
			res.setS2CMsg(LangService.getValue("REVIVE_NOT_FIND_PLAYER"));
			return res;
		}

		if (this.prop.revivedMapID != 0) {
			AreaUtil.dispatchByAreaId(player, new AreaData(this.prop.revivedMapID, 0, 0), null);
		} else {
			if (reliveType == ReliveType.CITY) {
				player.fightLevelManager.leaveDungeon(player, this);
				if (isEmpty()) {
					addCloseFuture();
				}
			} else {
				Out.debug("复活：", reliveType);
				// 出生点复活
				getXmdsManager().revivePlayer(playerId, reliveData(reliveType));
			}
		}
		actor.relive();
		if (diePlayers != null && diePlayers.contains(playerId)) {
			diePlayers.remove(playerId);
		}
		res.setS2CCode(PomeloRequest.OK);
		return res;
	}

	/**
	 * 获取场景总人数
	 */
	public int getPlayerNum() {
		return actors.size();
	}

	/**
	 * 获取角色
	 */
	public WNPlayer getPlayer(String playerId) {
		return PlayerUtil.getOnlinePlayer(playerId);
	}

	/**
	 * 获取场景角色
	 */
	public Actor getActor(String playerId) {
		return actors.get(playerId);
	}

	/**
	 * 获取场景所有角色是否全部死亡
	 */
	public boolean isAllActorDie() {
		for (Actor actor : actors.values()) {
			if (actor.alive) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 角色成功进入场景
	 */
	public void onPlayerEntered(WNPlayer player) {

		// 检测角色称号时效性
		player.titleManager.checkInvalidRanks(true);

		// 更新任务状态
		player.taskManager.dealTaskEvent(TaskType.reachPos, String.valueOf(this.areaId), 1);
		if (this.sceneType == SCENE_TYPE.FIGHT_LEVEL.getValue() || this.sceneType == SCENE_TYPE.LOOP.getValue() || this.sceneType == SCENE_TYPE.GUILD_BOSS.getValue()) {
			if (player.isRobot()) {
				JobFactory.addDelayJob(() -> {
					this.onPlayerAutoBattle(player, true);
				}, 2000);
			}
		}

		player.activityManager.CheckLimitTimeGiftList();

		// BI上报...
		if (!isNormal()) {
			BILogService.getInstance().ansycReportEnteredArea(player.getPlayer(), sceneType, areaId);
		}
	}

	/**
	 * 角色成功离开场景
	 */
	public void onPlayerLeaved(WNPlayer player) {
		Out.debug(player.getName(), " onPlayerLeaved: ", prop.name);
		if (!isNormal() && !noCloseIfNoPlayer() && isEmpty() && closeFuture == null) {
			addCloseFuture(66);
		}
	}

	/**
	 * 场景中角色需求数据
	 */
	public String toJSON4EnterScene(WNPlayer player) {
		return player.toJSON4EnterScene(this).toJSONString();
	}

	public void putActor(String playerId) {
		this.actors.put(playerId, new Actor());
	}

	public void removeActor(String playerId) {
		this.actors.remove(playerId);
		if (!isNormal() && isAllActorDie()) {
			AreaEvent.gameOverEventB2R(this, Utils.toJSON("force", -1));
		}
	}

	/**
	 * 添加角色
	 */
	public void addPlayer(WNPlayer player) {
		String playerId = player.getId();
		setForce(player);
		player.setArea(this);
		Out.debug(player.getName(), "addPlayer begin enter scene id:", instanceId, " - ", prop.name, this.lineIndex);
		try {
			if (!actors.containsKey(playerId)) {
				this.hasPlayerEntered = true;
				actors.put(playerId, new Actor());
			}
			this.removeCloseFuture();
			if (player.isRobot()) {
				playerEnterRequest(player);
				JobFactory.addDelayJob(() -> {
					player.onEndEnterScene();
				}, Utils.getSecMills(3, 5));
			}
		} catch (Exception error) {
			Out.error("c# enter scene id: ", instanceId, " - ", prop.name, " error : ", error);
			throw error;
		}
	}

	/**
	 * 获取场景出生点
	 */
	public JSONObject getBornPlace(int templateID) {
		JSONObject data = Utils.toJSON("x", 0, "y", 0);
		String result = getXmdsManager().getBornPlace(this.instanceId, templateID);
		if (StringUtil.isNotEmpty(result)) {
			data = JSON.parseObject(result);
		}
		return data;
	}

	/**
	 * 获取场景玩家的基础信心
	 */
	public GetPlayerData getPlayerData(String playerId) {
		// if(getXmdsManager() == null) return null;
		String result = getXmdsManager().getPlayerData(playerId, true);
		return StringUtil.isEmpty(result) ? null : JSON.parseObject(result, GetPlayerData.class);
	}

	/**
	 * 从战斗服同步角色数据
	 */
	public void syncPlayerHistoryData(WNPlayer player) {
		if (this.isNormal()) {
			GetPlayerData result = getPlayerData(player.getId());
			if (result != null) {
				Out.debug(instanceId, " syncPlayerHistoryData:", player.getName(), " x:", result.x, ", y:", result.y);
				player.syncNowData(this.areaId, this.instanceId, result);
				player.syncHistoryData(this.areaId, this.instanceId, result);
			}
		}
	}

	/**
	 * 移除角色
	 */
	public void removePlayer(WNPlayer player, boolean keepObject) {
		Out.debug(this.instanceId, " - ", prop.name, " removePlayer player :", player.getName());
		String playerId = player.getId();
		Actor actor = getActor(playerId);
		if (actor != null) {
			this.playerLeaveRequest(player, keepObject);
			this.actors.remove(playerId);
			this.onPlayerLeaved(player);
			this.resetEmptyTime();
			RobotUtil.onRobotLeaderQuit(this, player);
		}
		if (!isNormal() && isAllActorDie() && !noCloseIfNoPlayer()) {
			AreaEvent.gameOverEventB2R(this, Utils.toJSON("force", -1));
		}
	}

	/**
	 * 通知玩家复活
	 */
	public void pushRelive(WNPlayer player) {
		if (player != null) {
			PlayerRelivePush.Builder data = newPlayerRelivePush(player);
			WNNotifyManager.getInstance().pushRelive(player, data.build());
		}
	}

	/**
	 * pk爆装 return 长度为2的数组 1： List<ItemToBtlServerData> 2：List<Object>
	 */
	public Object[] onPKPlayerDeadDrop(WNPlayer deadPlayer, WNPlayer hitPlayer, int pkValue, float x, float y) {
		Object[] result = new Object[2];
		if (deadPlayer == null || hitPlayer == null || deadPlayer.isProxy())
			return result;

		List<ItemToBtlServerData> itemsPayLoad = new ArrayList<>();
		List<NormalItem> items = deadPlayer.pkRuleManager.dropItemByKilled(pkValue, this);
		Out.debug(getClass(), " onPlayerDeadByPlayer items:", items.size());
		for (NormalItem dropItem : items) {
			dropItem.itemDb.gotTime = new Date();
			AreaItem areaItem = new AreaItem(dropItem);
			areaItem.dropPlayer = deadPlayer;
			areaItem.dropX = x;
			areaItem.dropY = y;
			this.items.put(dropItem.itemDb.id, areaItem);
			List<String> list_pids = new ArrayList<>();
			if (hitPlayer != null) {
				list_pids.add(hitPlayer.getId());
			}
			list_pids.add(deadPlayer.getId());
			ItemToBtlServerData itemData = dropItem.toJSON4BatterServer(list_pids, Const.TEAM_DISTRIBUTE_TYPE.FREEDOM, true);
			itemsPayLoad.add(itemData);

			if (dropItem.getQLevel() >= Const.ItemQuality.ORANGE.getValue()) {
				StringBuffer sb = new StringBuffer();
				if (dropItem instanceof NormalEquip && dropItem.itemDb.speData != null) {
					ItemSpeData speData = dropItem.itemDb.speData;
					if (speData != null) {
						sb.append(speData.baseAtts.toString());
						if (speData.extAtts != null) {
							sb.append("|||").append(speData.extAtts.toString());
						}
						if (speData.legendAtts != null) {
							sb.append("|||").append(speData.legendAtts.toString());
						}
					}
				}
				PlayerUtil.bi(this.getClass(), Const.BiLogType.Pk, hitPlayer, deadPlayer.getId(), deadPlayer.getName(), dropItem.itemDb.id, dropItem.itemDb.code, sb.toString());
			}
		}
		if (itemsPayLoad.size() > 0) {
			// 向战斗服发送死亡物品掉落数据
			String data = Utils.toJSON("pos", Utils.ofMap("x", x, "y", y), "items", itemsPayLoad).toJSONString();
			Out.debug(getClass(), " onPlayerDeadByPlayer:", data);
			getXmdsManager().onMonsterDiedDrops(instanceId, data);
		}

		result[0] = itemsPayLoad;
		result[1] = items;

		return result;
	}

	/**
	 * 怪物击杀玩家
	 */
	public void onPlayerDeadByMonster(WNPlayer player, AreaEvent.MonsterData monsterData, float playerX, float playerY) {
		pushRelive(player);
	}

	/**
	 * 玩家击杀玩家
	 */
	public void onPlayerDeadByPlayer(WNPlayer deadPlayer, WNPlayer hitPlayer, float x, float y) {
		if (deadPlayer == null || hitPlayer == null) {
			return;
		}
		Out.info("玩家击杀玩家 Attack=", hitPlayer.getId(), "(", hitPlayer.getName(), "),Dead=", deadPlayer.getId(), "(", deadPlayer.getName(), ")");
		pushRelive(deadPlayer);
		hitPlayer.onEvent(new TaskEvent(TaskType.KILL_PLAYER, String.valueOf(deadPlayer.getPro()), 1));
	}

	/**
	 * 杀怪获取经验
	 */
	protected void addExp(WNPlayer player, Collection<WNPlayer> belongPlayers, int attackType, MonsterBase monsterProp, int monsterLevel, int teamExpAdd) {
		int levelLimit = GlobalConfig.Exp_Monster_LevelLimit;
		PetNew pet = player.getFightingPet();
		if (pet != null && Math.abs(pet.po.level - monsterLevel) <= levelLimit) {
			if (attackType == 1) {// killed by pet
				int exp = monsterProp.baseExp * 2 * monsterLevel / (pet.po.level + monsterLevel);
				if (exp < 1)
					exp = 1; // csz说的，最少要给1点经验
				pet.addExp(exp, true);
			} else {// killed by master
				int ratioMaster = Integer.valueOf(GameData.PetConfigs.get("PetExp.Percent.KillByMaster").paramValue);
				int exp = monsterProp.baseExp * ratioMaster / 100 * 2 * monsterLevel / (pet.po.level + monsterLevel);
				if (exp < 1)
					exp = 1; // csz说的，最少要给1点经验
				pet.addExp(exp, true);
			}
		}
		if (Math.abs(player.getLevel() - monsterLevel) > levelLimit) {
			int exp = 1;
			// 经验卡加成
			int exdExp = player.bufferManager.getBuffAttrValue("ExdExp");
			// 工会祈福加成
			exdExp += player.getGuildExdExp();
			exp += (exp * exdExp / 10000);

			// 幻境金币/经验/修为限制
			if (this.sceneType == Const.SCENE_TYPE.ILLUSION.getValue()) {
				exp = player.illusionManager.addAward("exp", exp);
			}

			List<NormalItem> expItems = ItemUtil.createItemsByItemCode("exp", exp);
			for (NormalItem item : expItems) {
				addVirtureItem(player, item, GOODS_CHANGE_TYPE.monsterdrop);
			}
		}
		for (WNPlayer belongPlayer : belongPlayers) {
			if (belongPlayer == null)
				continue;
			int levelDiff = Math.abs(belongPlayer.getLevel() - monsterLevel);
			if (levelDiff > levelLimit)
				continue;
			int exp = monsterProp.baseExp * 2 * monsterLevel / (belongPlayer.getLevel() + monsterLevel);

			int teamExp = 0;
			if (teamExpAdd > 0) {
				teamExp = exp * (10000 + teamExpAdd) / 10000;
				if (teamExp < 0) {
					teamExp = 0;
				}
			}
			exp = (int) (exp * (1 + belongPlayer.vipManager.getVipExpRatio()));

			exp += teamExp;
			// 幻境金币/经验/修为限制
			if (this.sceneType == Const.SCENE_TYPE.ILLUSION.getValue()) {
				exp = belongPlayer.illusionManager.addAward("exp", exp);
			}
			List<NormalItem> expItems = ItemUtil.createItemsByItemCode("exp", exp);
			for (NormalItem item : expItems) {
				addVirtureItem(belongPlayer, item, GOODS_CHANGE_TYPE.monsterdrop);
			}
		}

		return;
	}

	public boolean hasPlayer(String playerId) {
		return this.actors.containsKey(playerId);
	}

	public boolean isAllRobot() {
		for (String playerId : this.actors.keySet()) {
			TeamMemberData teamMember = TeamService.getTeamMember(playerId);
			if (teamMember == null || !teamMember.robot) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 场景中是否掉落高品质道具
	 */
	public boolean hasHighQualityItem() {
		for (AreaItem itemInfo : this.items.values()) {
			if (itemInfo.item.prop.qcolor >= GlobalConfig.Leave_Notice_Quality) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 是否可复活
	 */
	public boolean canRebirth(String playerId) {
		return true;
	}

	/**
	 * 场景接收广播
	 */
	public void receive(String route, GeneratedMessage msg) {
		receive(new MessagePush(route, msg));
	}

	public void receive(PomeloPush push) {
		for (String rid : actors.keySet()) {
			WNPlayer player = getPlayer(rid);
			if (player != null) {
				player.receive(push);
			}
		}
	}

	/**
	 * 是否是一个保持玩家场景模型的场景
	 */
	public boolean isKeepObject() {
		return !isOpenJoinTeam();
	}

	/**
	 * 是否开放加入队伍
	 */
	public boolean isOpenJoinTeam() {
		return isOpenJoinTeamArea();
	}

	/**
	 * 玩家登录时触发
	 */
	public void onPlayerLogin(WNPlayer player) {
		player.nofitySuperScript();
		if (!isNormal() && (diePlayers == null || !diePlayers.contains(player.getId()))) {
			onPlayerAutoBattle(player, true);
		}
	}

	/**
	 * 玩家离线时触发
	 */
	public void onPlayerLogout(WNPlayer player) {
		if (player.getTeamManager().getTeamMember() != null) {
			player.getTeamManager().getTeamMember().follow = false;
		}

		boolean keepObject = isKeepObject();
		if (keepObject && !isClose()) {
			if (!player.getTeamManager().isFollowLeader()) {
				// 自动托管
				onPlayerAutoBattle(player, true);
			}
		}
		Actor actor = getActor(player.getId());
		if (actor != null) {
			if (!actor.alive) {
				recordDie(player.getId());
			}
			removePlayer(player, keepObject);
		}
	}

	public void onPlayerAutoBattle(WNPlayer player, boolean enable) {
		if (!isClose()) {
			String playerId = player.getId();
			// getZoneManager().playerNetStateChanged(playerId, "disconnected");
			getXmdsManager().autoBattle(instanceId, playerId, enable);
		}
	}

	protected PlayerRelivePush.Builder newPlayerRelivePush(WNPlayer player) {
		return newPlayerRelivePush(player, true);
	}

	/**
	 * 复活消息
	 */
	protected PlayerRelivePush.Builder newPlayerRelivePush(WNPlayer player, boolean showBtn) {
		ResurrectionCO resurrection = GameData.Resurrections.get(areaId);

		PlayerRelivePush.Builder data = PlayerRelivePush.newBuilder();
		data.setBtn(showBtn ? ReliveBtn.SHOW_CLICK.value : ReliveBtn.UN_SHOW.value);

		data.setCountDown(GlobalConfig.Group_Resurrection);
		data.setCbType(ReliveCB.RELIVE.value);
		data.setType(isNormal() ? ReliveOP.FIELD.value : ReliveOP.RELIVE.value);
		data.setOp(isNormal() ? ReliveUP.OK.value : ReliveUP.NO.value);
		int totalReliveNum = Const.RELIVE_NUM + player.vipManager.getReliveNum();
		int nowReliveNum = totalReliveNum - player.getReliveManager().nowReliveNum;
		int cost = Const.RELIVE_DIAMOND;
		if (resurrection != null) {
			if (resurrection.backResurrect != 1) {
				data.setCbType(ReliveCB.RELIVE.value);
				data.setType(ReliveOP.FIELD.value);
				data.setOp(ReliveUP.NO.value);
			} else if (!isNormal()) {
				data.setCbType(ReliveCB.LEAVE.value);
				data.setOp(ReliveUP.OK.value);
				data.setType(ReliveOP.LEAVE.value);
			}
			cost = resurrection.cost;
			if (!isNormal() && resurrection.resurrectNum > 0) {
				int currCount = resurrection.resurrectNum - player.getReliveManager().getReliveCount(areaId);
				if (currCount > 0) {
					data.setType(ReliveOP.RELIVE.value);
					data.setCbType(ReliveCB.RELIVE.value);
					data.setOp(ReliveUP.NO.value);
				}
				data.setCurrCount(currCount);
				data.setTotalCount(resurrection.resurrectNum);
			}
			data.setCountDown(resurrection.autoResurrectTime);
			data.setCooltime(resurrection.resurrectCD);
			if (resurrection.lieDown == 1) {
				data.setCbType(ReliveCB.ICON.value);
			}
			if (resurrection.freeResurrect > 0) {
				nowReliveNum = 0;
			}
		}
		if (nowReliveNum > 0) {
			player.getReliveManager().payCost = 0;
			data.setPayConfirm(0);
			data.setCostStr(LangService.format("RELIVE_FREE", nowReliveNum, totalReliveNum));
		} else {
			if (-nowReliveNum > 0) {
				cost = -nowReliveNum * cost;
				cost = Math.min(cost, 50);
			}
			player.getReliveManager().payCost = cost;
			data.setPayConfirm(player.getReliveManager().payConfirm);
			data.setCostStr(LangService.format("RELIVE_DIAMOND", cost));
		}
		if (showBtn) {
			data.setBtnSafe(1);
			data.setBtnCity(1);
			data.setBtnCurr(getSceneType() == SCENE_TYPE.LOOP.getValue() ? 0 : 1);
		}
		return data;
	}

	public boolean onCleanItem(AreaItem areaItem) {
		return true;
	}

	/**
	 * 1:原地复活；2:出生点复活；3:复活点复活. 4:技能复活.
	 */
	public ReliveType getReliveType() {
		return ReliveType.BORN;
	}

	public void onRobotQuit(int second) {
		for (String playerId : actors.keySet()) {
			WNPlayer player = PlayerUtil.getOnlinePlayer(playerId);
			if (player != null && player.isRobot()) {
				JobFactory.addDelayJob(() -> {
					player.fightLevelManager.leaveDungeon(player, this);
				}, Utils.getSecMills(3, second) / 2);
			}
		}
	}

	protected ScheduledFuture<?> closeFuture;

	/**
	 * 场景销毁事件
	 */
	public void addCloseFuture() {
		addCloseFuture(prop.timeCount);
	}

	protected void addCloseFuture(int second) {
		try {
			if (closeFuture != null && !closeFuture.isDone()) {
				Out.info(getClass(), " more addCloseFuture!!!");
				closeFuture.cancel(true);
			}
			onRobotQuit(second);
		} finally {
			closeFuture = JobFactory.addDelayJob(() -> {
				if (robotDisponseJob != null) {
					robotDisponseJob.cancel(true);
					robotDisponseJob = null;
				}
				AreaUtil.closeArea(instanceId);
			}, (second + 5) * GGlobal.TIME_SECOND);
		}
	}

	public void removeCloseFuture() {
		if (closeFuture != null) {
			closeFuture.cancel(true);
			closeFuture = null;
		}
	}

	/**
	 * 触发日常活动
	 */
	protected void onDailyActivity(WNPlayer player) {
		// if (player.soloManager != null) {
		// player.soloManager.quitMatching(false);
		// }
	}

	public void onEndEnterScene(WNPlayer player) {
		if (diePlayers != null && diePlayers.contains(player.getId())) {
			pushRelive(player);
		}
		Out.info("player true enter scene:sceneId=", this.areaId, "instanceId:", this.instanceId, ",playerId=", player.getId());
	}

	public void onUnitDead(JSONObject msg) {
		AreaEvent.unitDead(this, msg);
	}

	/**
	 * Buff飘字
	 ***************************************************************************/
	/**
	 * 玩家吃到或者失效buffer(目前只针对【天神】、【贪婪】buffer)
	 * 
	 * @param area
	 * @param paramType
	 * @param paramData
	 */
	public void eatOrLostBuffer(WNPlayer player, String paramType, String paramData) {
		String tips = "";
		Map<String, Actor> actors = this.actors;
		if (paramType.equals("GetBuff")) {// 吃到buffer
			Actor actor = actors.get(player.getId());
			if (actor != null) {
				actor.buffers.add(paramData);
			}
			if (Integer.parseInt(paramData) == Const.Arena.ARENA_TIANSHEN.value) {// 天神
				tips = LangService.format("ARENA_TIANSHEN_BUFF", player.getName());
				for (String tempPlayerId : actors.keySet()) {
					WNPlayer tempPlayer = PlayerUtil.getOnlinePlayer(tempPlayerId);
					if (tempPlayer != null) {
						MessageUtil.sendSysTip(tempPlayer, tips, Const.TipsType.ROLL);
					}
				}
			} else if (Integer.parseInt(paramData) == Const.Arena.ARENA_GREEDY.value) {// 贪婪
				tips = LangService.getValue("ARENA_GREEDY_BUFF");
				MessageUtil.sendSysTip(player, tips, Const.TipsType.NORMAL);
			} else if (Integer.parseInt(paramData) == Const.Arena.FIGHT_POWER_UP.value) {// 战斗力上升
				tips = LangService.getValue("BUFF_FIGHT_UP");
				MessageUtil.sendSysTip(player, tips, Const.TipsType.NORMAL);
			}
		} else if (paramType.equals("LoseBuff")) {// buffer失效
			Actor actor = actors.get(player.getId());
			if (actor != null) {
				actor.buffers.remove(paramData);
			}
		}
	}

	/**
	 * 掷点
	 *******************************************************************************/
	private Map<String, PointItem> pointItems;

	private class PointItem {
		/** 唯一标识 */
		String id;
		String itemCode;
		int num;
		boolean send2client;
		boolean get;
		Map<String, Integer> points;
		Set<String> bindPlayers;
		ScheduledFuture<?> schResult;
		int sceneTypeValue;

		PointItem(String itemCode, int num, int sceneTypeValue) {
			this.itemCode = itemCode;
			this.id = UUID.randomUUID().toString();
			this.num = num;
			this.sceneTypeValue = sceneTypeValue;
		}

		public void bindTeam(WNPlayer player) {
			Map<String, TeamMemberData> members = player.getTeamManager().getTeamMembers();
			bindPlayers = new HashSet<>(members == null ? 1 : members.size());
			points = new HashMap<>(members == null ? 1 : members.size());
			if (members != null) {
				bindPlayers.addAll(members.keySet());
			} else {
				bindPlayers.add(player.getId());
			}
		}
	}

	/**
	 * 发送物品掷点列表推送
	 */
	private void sendThrowPointItemListPush(WNPlayer player, int bossId) {
		if (pointItems.size() > 0) {
			int time = 30;// prop.timeCount * 2 / 3;
			ThrowPointItemListPush.Builder builder = ThrowPointItemListPush.newBuilder();
			builder.setS2CCode(Const.CODE.OK);
			builder.setTime(time);
			for (PointItem pointItem : pointItems.values()) {
				if (!pointItem.send2client) {
					PointItemView.Builder view = PointItemView.newBuilder();
					view.setId(pointItem.id);
					view.setItemCode(pointItem.itemCode);
					view.setNum(pointItem.num);
					builder.addItems(view);

					pointItem.send2client = true;
				}
			}
			MessagePush list = null;
			if (builder.getItemsCount() > 0) {
				list = new MessagePush("area.battlePush.throwPointItemListPush", builder.build());
			}
			Set<String> team = player.getTeamMembers();
			Set<String> toThrowPointResultTeam = new HashSet<>();

			for (Entry<String, Actor> actorEntry : this.actors.entrySet()) {
				String teamPlayerId = actorEntry.getKey();
				Actor actor = actorEntry.getValue();
				WNPlayer teamPlayer = this.getPlayer(teamPlayerId);
				if (teamPlayer != null && actor.profitable && teamPlayer.fightLevelManager.canDrop(bossId, this) && teamPlayer.dropManager.canDrop(bossId, this) && list != null && (team != null && team.contains(teamPlayerId))) {
					teamPlayer.receive(list);
					toThrowPointResultTeam.add(teamPlayerId);
				}
			}

			for (PointItem item : pointItems.values()) {
				if (team != null && toThrowPointResultTeam.size() > 0 && item.schResult == null) {
					item.schResult = JobFactory.addDelayJob(() -> {
						Out.debug("throw point timeout!!!");
						throwPointResult(item, item.id, toThrowPointResultTeam);
					}, (time) * 1000);
				}
			}
		}
	}

	public void onRobotThrowPoint(WNPlayer player) {
		for (PointItem pointItem : pointItems.values()) {
			if (pointItem.bindPlayers.contains(player.getId())) {
				JobFactory.addDelayJob(() -> {
					randomPoint(player, pointItem.id);
				}, Utils.getSecMills(1, 5));
			}
		}
	}

	public int randomPoint(WNPlayer player, String id) {
		if (pointItems == null || !pointItems.containsKey(id))
			return 0;
		PointItem item = pointItems.get(id);
		Map<String, Integer> points = item.points;
		if (item.get || points.containsKey(player.getId()))
			return 0;
		int point = RandomUtil.getInt(100);
		// 不能存在相同的点数
		for (int p : points.values()) {
			if (p == point) {
				if (point < 50) {
					point += RandomUtil.getInt(3);
				} else {
					point -= RandomUtil.getInt(3);
				}
				break;
			}
		}

		points.put(player.getId(), point);
		if (points.size() >= getPlayerNum()) {
			if (item.schResult != null) {
				item.schResult.cancel(true);
				item.schResult = null;
			}
			throwPointResult(item, id, player != null ? player.getTeamMembers() : null);
		}
		return point;
	}

	// 掷点结果
	private void throwPointResult(PointItem item, String pointId, Set<String> team) {
		synchronized (item) {
			if (item.get)
				return;
			item.get = true;
		}
		StringBuilder content = new StringBuilder();
		DItemEquipBase equip = ItemConfig.getInstance().getItemProp(item.itemCode);

		String equipNmae = ItemUtil.getColorItemNameByQcolor(equip.qcolor, equip.name);
		Map<String, Integer> points = item.points;
		String rid = null;
		int tmpPoint = 0;

		// 此装备在需求掷点的成员中是否有适合他本职业的
		boolean hasProFit = false;
		for (String playerId : item.bindPlayers) {
			PlayerPO member = PlayerUtil.getPlayerBaseData(playerId);
			if (member.pro == equip.Pro && points.containsKey(playerId)) {
				hasProFit = true;
			}
		}

		for (String playerId : item.bindPlayers) {
			PlayerPO member = PlayerUtil.getPlayerBaseData(playerId);
			if (points.containsKey(playerId)) {
				int point = points.get(playerId);
				content.append(LangService.format("TEAM_THROW_POINT", point, member.name)).append("<br/>");
				if (point > tmpPoint) {
					if (!hasProFit) {
						rid = playerId;
						tmpPoint = point;
					} else {
						if (member.pro == equip.Pro) {// 本职业优先，非本职业的不参与掷点比较
							rid = playerId;
							tmpPoint = point;
						}
					}
				}
			} else {
				content.append(LangService.format("TEAM_UNTHROW_POINT", member.name)).append("<br/>");
			}
		}

		WNPlayer member = rid == null ? null : getPlayer(rid);
		if (member == null) { // 无人拾取随机分配
			List<String> list_profitable = new ArrayList<>();

			for (String _pId : actors.keySet()) {
				WNPlayer _player = getPlayer(_pId);
				if (actors.get(_pId) != null && actors.get(_pId).profitable && _player != null && team.contains(_pId)) {

					list_profitable.add(_pId);
				}
			}
			if (list_profitable.size() > 0) {
				int idx = RandomUtil.getIndex(list_profitable.size());
				member = getPlayer(list_profitable.get(idx));
			}
		}
		if (member != null) {
			ThrowPointResultPush push = ThrowPointResultPush.newBuilder().setS2CCode(Const.CODE.OK).setId(item.id).setName(member == null ? "" : member.getName()).setItemCode(item.itemCode).setPoint(tmpPoint).setNum(item.num).build();

			WNPlayer player = member;
			JobFactory.addDelayJob(() -> {

				String itemLink = null;
				NormalItem newEquip = null;
				if (player != null) {
					player.illusionManager.addItemNum(this, item.itemCode, 1);
					if (player.getWnBag().testAddCodeItem(item.itemCode, 1, null, false)) {
						newEquip = ItemUtil.createItemsByItemCode(item.itemCode, 1).get(0);

						itemLink = ChannelUtil.getChatLinkItem(newEquip);
						// FIXME 来源需细分
						player.getWnBag().addEntityItem(newEquip, GOODS_CHANGE_TYPE.RollPoint, null, false, false);
					} else {// 背包满发邮件
						MailSysData mailData = new MailSysData(SysMailConst.BAG_FULL_COMMON);
						mailData.attachments = new ArrayList<>();
						Attachment attachment = new Attachment();
						attachment.itemCode = item.itemCode;
						attachment.itemNum = 1;
						mailData.attachments.add(attachment);
						// FIXME 来源需细分
						MailUtil.getInstance().sendMailToOnePlayer(player.getId(), mailData, GOODS_CHANGE_TYPE.RollPoint);
					}
				}

				if (itemLink != null) {// itemLink不能包含在<font></font>之内
					String title = LangService.format("TEAM_THROW_RESULT", equipNmae) + "<br/>";
					content.insert(0, title);
					if (item != null) {
						if (newEquip.isEquip()) {// 装备默认数量1件
							content.append(LangService.format("TEAM_THROW_WIN_EQUIP", player.getName(), 1, "")).append(itemLink);
						} else {
							content.append(LangService.format("TEAM_THROW_WIN", player.getName(), item.num, "")).append(itemLink);
						}
					}
				} else {
					String title = LangService.format("TEAM_THROW_RESULT", equipNmae) + "<br/>";
					content.insert(0, title);
					content.append(LangService.format("TEAM_THROW_WIN_EQUIP", player.getName(), 1, equipNmae));
				}

				for (String playerId : item.bindPlayers) {
					WNPlayer actor = this.getPlayer(playerId);
					if (actor != null) {
						actor.receive("area.battlePush.throwPointResultPush", push);
						MessageUtil.sendChatMsgAsyn(actor, content.toString(), Const.CHAT_SCOPE.TEAM, TipsType.NORMAL);
						Out.debug("==============", content);
					}
				}

			}, 0); // player == null ? 0 : 2000
		}

		this.pointItems.remove(pointId);
		Out.debug("===============pointId removed:", pointId);
	}

	public void playerEnterRequest(WNPlayer player) {
		playerEnterRequest(player, toJSON4EnterScene(player));
	}

	public void playerEnterRequest(WNPlayer player, String enterSceneData) {
		getZoneManager().playerEnterRequest(player.getId(), instanceId, enterSceneData);
		Out.debug("playerEnterRequest====================", player.getName(), "-", instanceId, "-", prop.name);
	}

	public void playerLeaveRequest(WNPlayer player, boolean keepObject) {
		getZoneManager().playerLeaveRequest(player.getId(), instanceId, keepObject);
		Out.debug("playerLeaveRequest--------------------", player.getName(), "-", instanceId, "-", player.getArea().prop.name);
	}

	/**
	 * 玩家进场景以后推的消息
	 */
	public void onReady(WNPlayer player) {

	}

	/**
	 * @return create new AreaData that contains areaId and instanceId
	 */
	public AreaData getAreaData() {
		System.err.println("instanceId=" + this.instanceId);
		return new AreaData(this.areaId, this.instanceId);
	}

	@Override
	public String toString() {
		return new StringBuilder().append(serverId).append(" :: ").append(prop.name).append(" : ").append(instanceId).append(" : ").append(actors.size()).toString();
	}
}