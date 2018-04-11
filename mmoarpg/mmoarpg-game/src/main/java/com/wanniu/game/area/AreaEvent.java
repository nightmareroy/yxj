package com.wanniu.game.area;

import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wanniu.core.game.JobFactory;
import com.wanniu.core.game.LangService;
import com.wanniu.core.logfs.Out;
import com.wanniu.core.util.DateUtil;
import com.wanniu.core.util.StringUtil;
import com.wanniu.game.area.Area.Actor;
import com.wanniu.game.chat.ChannelUtil;
import com.wanniu.game.common.Const.EventType;
import com.wanniu.game.common.Const.GOODS_CHANGE_TYPE;
import com.wanniu.game.common.Const.SCENE_TYPE;
import com.wanniu.game.common.Const.TaskType;
import com.wanniu.game.common.msg.MessageUtil;
import com.wanniu.game.data.GameData;
import com.wanniu.game.data.Normal_WorldCO;
import com.wanniu.game.data.ResurrectionCO;
import com.wanniu.game.data.base.DItemEquipBase;
import com.wanniu.game.data.base.MonsterBase;
import com.wanniu.game.data.base.TaskBase;
import com.wanniu.game.data.ext.MonsterRefreshExt;
import com.wanniu.game.data.ext.WayTreasureExt;
import com.wanniu.game.guild.guildBoss.GuildBossArea;
import com.wanniu.game.guild.guildDungeon.GuildDungeon;
import com.wanniu.game.item.ItemConfig;
import com.wanniu.game.item.NormalItem;
import com.wanniu.game.item.data.ItemToBtlServerData;
import com.wanniu.game.mail.MailUtil;
import com.wanniu.game.mail.SysMailConst;
import com.wanniu.game.mail.data.MailSysData;
import com.wanniu.game.monster.MonsterConfig;
import com.wanniu.game.player.GlobalConfig;
import com.wanniu.game.player.PlayerUtil;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.task.TaskEvent;
import com.wanniu.game.task.TaskUtils;
import com.wanniu.game.task.po.TaskPO;

/**
 * 为场景添加相应事件
 * 
 * @author agui
 */
public class AreaEvent {

	/** monsterData */
	public static class MonsterData {
		public int templateId;
		public int level;
		public int qColor;
		public String id;
		public String name;
		public String sceneType;
		public int killType;
		public int pro;
		public int drop;
	}

	public static void onTaskEvent(String taskPlayerId, int unitTemplateId) {
		if (StringUtil.isNotEmpty(taskPlayerId)) {
			WNPlayer taskPlayer = PlayerUtil.getOnlinePlayer(taskPlayerId);
			if (taskPlayer != null) {
				if (taskPlayer.isProxy()) {
					taskPlayer.onProxyEvent(3, body -> {
						body.writeInt(unitTemplateId);
					});
					return;
				}
				// 是否是挖宝任务怪
				for (TaskPO taskpo : taskPlayer.taskManager.treasureTasks.values()) {
					TaskBase prop = TaskUtils.getTaskProp(taskpo.templateId);
					for (String target : prop.targets) {
						WayTreasureExt way = GameData.WayTreasures.get(Integer.parseInt(target));
						if (way == null) {
							continue;
						}
						if (way.monsterIds.contains(unitTemplateId)) {
							taskPlayer.taskManager.dealTaskEvent(TaskType.FIND_TREASURE, target, 1);
							// 推送下一个挖宝场景
							int lastIndex = prop.targets.indexOf(target);
							if (lastIndex < prop.targets.size() - 1) {
								TaskUtils.treasurePush(taskPlayer, Integer.parseInt(prop.targets.get(lastIndex + 1)), taskpo.templateId);
							}
							return;
						}
					}
				}

				// 是否是击杀boss数量任务怪
				List<Normal_WorldCO> normal_WorldCOs = GameData.findNormal_Worlds((t) -> t.type >= 4);
				for (Normal_WorldCO normal_WorldCO : normal_WorldCOs) {
					if (normal_WorldCO.iD == unitTemplateId) {
						taskPlayer.onEvent(new TaskEvent(EventType.killBossCount, unitTemplateId, 1));
					}
				}

				taskPlayer.onEvent(new TaskEvent(EventType.killMonster, unitTemplateId, 1));
				taskPlayer.taskManager.dealTaskEvent(TaskType.GOT, String.valueOf(unitTemplateId), 1);
			}
		}
	}

	public static void unitDead(Area area, JSONObject msg) {
		int unitType = msg.getIntValue("unitType");
		String hitFinalPlayerId = msg.getString("hitFinal");
		String belongPlayerId = msg.getString("belongPlayerId"); // 默认使用第一个摸怪玩家
		JSONArray atkAssistantList = msg.getJSONArray("atkAssistantList");
		WNPlayer hitFinalPlayer = null;
		if (!StringUtil.isEmpty(belongPlayerId)) {
			hitFinalPlayer = area.getPlayer(belongPlayerId);
		}
		if (hitFinalPlayer == null && !StringUtil.isEmpty(hitFinalPlayerId)) {
			hitFinalPlayer = area.getPlayer(hitFinalPlayerId);
		}

		if (unitType == 0) { // 怪物死亡
			int unitTemplateId = msg.getIntValue("unitTemplateId");

			// 不管他咋死的，有没有掉落判定，都要移除他还活着的状态
			String refreshPoint = msg.getString("refreshPoint");
			area.removeAliveBoss(unitTemplateId, refreshPoint);

			if (hitFinalPlayer == null) {
				Out.debug("怪物死亡时，受益人为空. belongPlayerId=", belongPlayerId, ",hitFinalPlayerId=", hitFinalPlayerId);
				return;
			}

			MonsterBase monsterProp = MonsterConfig.getInstance().get(unitTemplateId);
			if (monsterProp == null) {
				if (Arrays.binarySearch(GlobalConfig.Monster_NoDrop_IDLists, unitTemplateId) < 0) {
					Out.error("can not get prop from monsterProps by unitDead msg.unitTemplateId:", unitTemplateId);
				}
			} else {
				List<MonsterRefreshExt> refreshProps = GameData.findMonsterRefreshs(t -> {
					return t.monsterID == unitTemplateId && t.mapID == area.areaId;
				});
				if (refreshProps != null && !refreshProps.isEmpty()) {
					MonsterRefreshExt bossExt = refreshProps.get(0);
					Out.info("has boss dead:bossId = ", bossExt.monsterID);
				}

				JSONArray teamSharedIdList = msg.getJSONArray("awardPlayer");

				area.onMonsterDead(unitTemplateId, msg.getIntValue("unitLevel"), msg.getIntValue("posX"), msg.getIntValue("posY"), msg.getIntValue("attackType"), refreshPoint, hitFinalPlayer, teamSharedIdList, atkAssistantList);

				onTaskEvent(hitFinalPlayer.getId(), unitTemplateId);// onTaskEvent(hitFinalPlayerId, unitTemplateId);
				int shareType = monsterProp.shareType;
				if (shareType == 1) {
					if (atkAssistantList != null && !atkAssistantList.isEmpty()) {
						atkAssistantList.forEach((atkAssistant) -> {
							String _playerId = (String) atkAssistant;
							WNPlayer bindPlayer = area.getPlayer(_playerId);
							if (_playerId != null && !_playerId.equals(belongPlayerId) && bindPlayer != null && bindPlayer.area.areaId == area.areaId) {// 玩家和BOSS必须再同一个场景下) {// hitFinalPlayerId
								onTaskEvent(_playerId, unitTemplateId);
							}
						});
					}
				}

				if (teamSharedIdList != null && !teamSharedIdList.isEmpty()) {
					teamSharedIdList.forEach((teamSharedId) -> {
						onTaskEvent((String) teamSharedId, unitTemplateId);
					});
				}
			}
		} else if (unitType == 1) { // 玩家死亡
			String unitPlayerId = msg.getString("unitPlayerId");
			WNPlayer deadPlayer = area.getPlayer(unitPlayerId);
			// 被玩家击杀
			Actor actor = area.getActor(unitPlayerId);
			if (actor != null) {
				actor.alive = false;
				ResurrectionCO resurrection = GameData.Resurrections.get(area.areaId);
				if (resurrection != null && resurrection.resurrectCD > 0) {
					actor.reliveCoolTime = System.currentTimeMillis() + resurrection.resurrectCD * 1000;
				}
				if (!PlayerUtil.isOnline(unitPlayerId)) {
					JobFactory.addDelayJob(() -> {
						area.relive(unitPlayerId, area.getReliveType());
					}, GlobalConfig.JJC_RebirthTime * 1000);
				}
			} else if (!area.isNormal()) {
				area.recordDie(unitPlayerId);
			}
			if (hitFinalPlayerId != null) {
				if (deadPlayer != null) {
					area.onPlayerDeadByPlayer(deadPlayer, hitFinalPlayer, msg.getIntValue("posX"), msg.getIntValue("posY"));

					if (area.needSendKillMail()) {
						deadPlayer.chouRenManager.beKilledOnce(hitFinalPlayerId);
						deadPlayer.friendManager.beKilledOnce(hitFinalPlayerId);
					}
				}
				// 添加进仇人列表或者增加仇恨值
				if (hitFinalPlayer != null) {
					if (area.needSendKillMail()) {
						hitFinalPlayer.chouRenManager.killOtherOnce(unitPlayerId);
						hitFinalPlayer.friendManager.killOtherOnce(unitPlayerId);
					}
				}
			} else if (deadPlayer != null) {
				// 被怪物击杀
				// MonsterData monsterData = new MonsterData();
				// monsterData.templateId =
				// msg.getIntValue("attackerTemplateId");
				// monsterData.level = msg.getIntValue("attackerLevel");
				// monsterData.qColor = msg.getIntValue("attackerQColor");
				// monsterData.name = msg.getString("attackerName");
				// monsterData.sceneType = msg.getString("attackerSceneType");
				// monsterData.killType = 0;
				area.onPlayerDeadByMonster(deadPlayer, null, msg.getIntValue("posX"), msg.getIntValue("posY"));
			}
			List<ItemToBtlServerData> itemsPayLoad = null;
			List<NormalItem> normalItemsPayLoad = null;
			if (area.isPKDrop()) {
				long pkTime = msg.getLongValue("pkTime");
				if (pkTime < GlobalConfig.PK_Killed_LostEquip_HurtDmgTimeOut) {
					Object[] objects = area.onPKPlayerDeadDrop(deadPlayer, hitFinalPlayer, msg.getIntValue("pkValue"), msg.getIntValue("posX"), msg.getIntValue("posY"));
					itemsPayLoad = (List<ItemToBtlServerData>) objects[0];
					normalItemsPayLoad = (List<NormalItem>) objects[1];
				}
			}

			// 不是普通场景不发送邮件提示...
			if (hitFinalPlayer != null && deadPlayer != null && area.needSendKillMail()) {
				MailSysData mailData = null;
				if (itemsPayLoad != null && itemsPayLoad.size() > 0) {
					mailData = new MailSysData(SysMailConst.BeKilledBySomneone1);
				} else {
					mailData = new MailSysData(SysMailConst.BeKilledBySomneone2);
				}
				mailData.replace = new HashMap<>();
				mailData.replace.put("time", DateUtil.format(Calendar.getInstance().getTime()));
				mailData.replace.put("location", area.getSceneName());
				mailData.replace.put("name", hitFinalPlayer.getName());
				if (itemsPayLoad != null && itemsPayLoad.size() > 0) {
					StringBuffer stringBuffer = new StringBuffer();
					for (NormalItem nItem : normalItemsPayLoad) {

						String itemText = LangService.getValue(MessageUtil.getColorLink(nItem.prop.qcolor));
						String itemLink = ChannelUtil.setItemInfo(nItem);
						DItemEquipBase base = ItemConfig.getInstance().getItemProp(nItem.prop.code);
						itemText = itemText.replace("{a}", base.name).replace("{b}", itemLink);

						String numText = LangService.getValue("DEFAULT");
						numText = numText.replace("{a}", "*" + nItem.getNum());

						String targetItemText = itemText + numText;

						stringBuffer.append(targetItemText);
						stringBuffer.append("、");

					}
					stringBuffer.deleteCharAt(stringBuffer.length() - 1);

					mailData.replace.put("item", stringBuffer.toString());

				}

				MailUtil.getInstance().sendMailToOnePlayer(deadPlayer.getId(), mailData, GOODS_CHANGE_TYPE.KILL_PLAYER);
			}
		}
		// else if (unitType == 2) { // 宠物
		// WNPlayer master = area.getPlayer(unitPlayerId);
		//
		// if (master != null) {
		// master.petManager.petDead();
		// }
		// }
	}

	public static void unitDeadEventB2R(Area area,JSONObject event) {
		area.onUnitDead(event);
	}
	
	public static void gameOverEventB2R(Area area, JSONObject event) {
		// 处理场景关闭事件
		if (!area.isClose()) {
			Out.info("scene game over:areaId=" + area.areaId + ",instanceId=", area.instanceId);
			area.isClose = true;
			area.onGameOver(event);
			area.addCloseFuture();
		}
	}

	public static void pickItemEventB2R(Area area, JSONObject msg) {
		area.onPickItem(msg.getString("playerId"), msg.getString("itemId"), msg.getBooleanValue("isGuard"));
	}

	public static void messageEventB2R(Area area, JSONObject msg) {
		String[] msgArray = msg.getString("msg").split(",");
		String type = msgArray[0];
		String playerId = msgArray[1];
		String param = msgArray[2];

		WNPlayer player = area.getPlayer(playerId);
		if (player == null) {
			Out.warn("AreaEvent player is offline:", playerId, "::", type, "::", param);
			return;
		}
		switch (type) {
		case "accessable":
		case "unaccessable": {
			player.taskManager.onTaskRequestEvent(type, Integer.valueOf(param), null, 0);
			return;
		}
		case "achievement": {
			player.achievementManager.onPlaceArrived(Integer.parseInt(param));
			return;
		}
		case "addUnit": { // 刷怪出来
			player.onEvent(new TaskEvent(EventType.addUnit, area, param));
			return;
		}
		case "loopTransform": {
			JobFactory.addDelayJob(() -> {
				player.onEvent(new TaskEvent(EventType.loopTransform, area, param));
			}, 0);
			return;
		}
		case "typeArena": {
			String paramData = msgArray[3];
			area.eatOrLostBuffer(player, param, paramData);
			return;
		}
		case "挖宝事件": { // FIXME
			player.onEvent(new TaskEvent(EventType.addUnit, area, param));
			return;
		}
		}
	}

	/**
	 * 需要策划在战斗服里添加伤害统计事件，战斗服才会推送这个消息
	 * @param area
	 * @param msg
	 */
	public static void battleReportEventB2R(Area area, JSONObject msg) {
		area.onBattleReport(msg.getJSONArray("data").toJavaList(DamageHealVO.class));
	};

	public static void killBossEventB2R(Area area, JSONObject msg) {
		area.onKillBoss(msg.getString("playerId"));
	}
}
