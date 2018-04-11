package com.wanniu.game.achievement;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

import com.wanniu.core.game.LangService;
import com.wanniu.core.logfs.Out;
import com.wanniu.core.util.StringUtil;
import com.wanniu.game.common.Const;
import com.wanniu.game.common.ConstsTR;
import com.wanniu.game.common.Const.ACHIEVEMENT_CONDITION_TYPE;
import com.wanniu.game.common.Const.PlayerBtlData;
import com.wanniu.game.common.msg.MessageUtil;
import com.wanniu.game.common.msg.WNNotifyManager;
import com.wanniu.game.data.ArmourAttributeCO;
import com.wanniu.game.data.GameData;
import com.wanniu.game.data.ext.AchievementConfigExt;
import com.wanniu.game.data.ext.AchievementExt;
import com.wanniu.game.data.ext.FashSuitConfigExt;
import com.wanniu.game.data.ext.FashionExt;
import com.wanniu.game.functionOpen.FunctionOpenManager;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.poes.AchievementDataPO;
import com.wanniu.game.poes.AchievementDataPO.AchievePO;
import com.wanniu.game.poes.AchievementDataPO.HolyArmour;
import com.wanniu.game.poes.PlayerPO;
import com.wanniu.game.poes.TaskListPO;
import com.wanniu.redis.PlayerPOManager;

import pomelo.area.AchievementHandler;

public class AchievementServiceNew {

	enum ERR_CODE {
		ERR_CODE_OK(0), ERR_CODE_BAG_FULL(1), ERR_CODE_NO_SUCH_AWARD(2), ERR_CODE_CONFIG_ERROR(3), ERR_CODE_PLAYER_LEVEL(4), ERR_CODE_ACHIEVEMENT_SCORE(5), ERR_CODE_ACHIEVEMENT_NOT_FINISH(6), ERR_CODE_DATA_ERROR(7), ERR_CODE_NOT_ENOUGH_GLOD(8), ERE_CODE_FAIL(9), ERR_CODE_NEED_GOTO(10);
		private int value;

		private ERR_CODE(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}
	}

	public List<AchievementExt> achievementLevelArray;

	public List<AchievementExt> achievementUpLevelArray;

	public List<AchievementExt> achievementPower;

	private AchievementServiceNew() {
		this.init();
	}

	private static class Holder {
		private static AchievementServiceNew Instance = new AchievementServiceNew();
	}

	public static AchievementServiceNew getInstance() {
		return Holder.Instance;
	}

	/**
	 * 加载数据并初始化
	 */
	public void init() {
		this.initAchievementType();
	};

	public void initAchievementType() {
		this.achievementLevelArray = getAchievementsByConditionType(Const.ACHIEVEMENT_CONDITION_TYPE.PLAYER_LEVEL);

		this.achievementUpLevelArray = getAchievementsByConditionType(Const.ACHIEVEMENT_CONDITION_TYPE.PLAYER_RANK);

		this.achievementPower = getAchievementsByConditionType(Const.ACHIEVEMENT_CONDITION_TYPE.PLAYER_POWER_POINT);
	}

	/**
	 * 筛选等于type的所有Achievement配置
	 * 
	 * @param type
	 * @return
	 */
	private List<AchievementExt> getAchievementsByConditionType(Const.ACHIEVEMENT_CONDITION_TYPE type) {
		Collection<AchievementExt> all = GameData.Achievements.values();
		List<AchievementExt> result = new ArrayList<>();
		for (AchievementExt achExt : all) {
			if (type.value == achExt.conditionType) {
				result.add(achExt);
			}
		}

		return result;
	}

	/**
	 * 判断对应的成就id是否已经放入add中
	 * 
	 * @param id
	 * @param add
	 * @returns {number}
	 */
	public int isAdd(int id, List<Integer> add) {
		int tem = 0;

		for (Integer key : add) {
			if (key == id) {
				tem = 1;
				return tem;
			}
		}
		return tem;
	}

	public AchievementHandler.Achievement achievementToJson4Payload(AchievementDataPO.AchievePO achievementRecord) {
		AchievementHandler.Achievement.Builder achievementData = AchievementHandler.Achievement.newBuilder();
		if (achievementRecord != null) {
			achievementData.setStatus(0);
			achievementData.setId(achievementRecord.id);
			achievementData.setScheduleCurr(achievementRecord.scheduleCurr);
		}
		return achievementData.build();
	};

	/**
	 * 单个成就是否完成
	 */
	public boolean isComplete(AchievePO po, AchievementExt ext) {
		if (po.scheduleCurr >= ext.targetNum) {
			return true;
		}
		return false;
	}

	/****************************************************************************************************************
	 * 序列化相关 其他模块调用接口 end
	 *****************************************************************************************************************/

	public String getColorContent(int pro, String playerName, String achievementName) {
		int quality = Const.ACHIEVEMENT_QUALITY_TYPE.ORANGE.value;
		String playerNameTxt = LangService.format("NAME_COLOR", playerName);
		if (quality == Const.ACHIEVEMENT_QUALITY_TYPE.WHITE.value) {
			return LangService.getValue("ACHIEVEMENT_FINISH_WHITE").replace("{playerName}", playerNameTxt).replace("{achievementName}", achievementName);
		} else if (quality == Const.ACHIEVEMENT_QUALITY_TYPE.GREEN.value) {
			return LangService.getValue("ACHIEVEMENT_FINISH_GREEN").replace("{playerName}", playerNameTxt).replace("{achievementName}", achievementName);
		} else if (quality == Const.ACHIEVEMENT_QUALITY_TYPE.BLUE.value) {
			return LangService.getValue("ACHIEVEMENT_FINISH_BLUE").replace("{playerName}", playerNameTxt).replace("{achievementName}", achievementName);
		} else if (quality == Const.ACHIEVEMENT_QUALITY_TYPE.PURPLE.value) {
			return LangService.getValue("ACHIEVEMENT_FINISH_PRUPLE").replace("{playerName}", playerNameTxt).replace("{achievementName}", achievementName);
		} else if (quality == Const.ACHIEVEMENT_QUALITY_TYPE.ORANGE.value) {
			return LangService.getValue("ACHIEVEMENT_FINISH_ORANGE").replace("{playerName}", playerNameTxt).replace("{achievementName}", achievementName);
		} else if (quality == Const.ACHIEVEMENT_QUALITY_TYPE.RED.value) {
			return LangService.getValue("ACHIEVEMENT_FINISH_RED").replace("{playerName}", playerNameTxt).replace("{achievementName}", achievementName);
		}
		return playerNameTxt;
	};

	public List<AchievementExt> findByConditionType(Const.ACHIEVEMENT_CONDITION_TYPE conditionType) {
		return GameData.findAchievements((t) -> {
			return t.conditionType == conditionType.value;
		});
	}

	public List<AchievementExt> findAchievementsByIdAndConditionType(int id, Const.ACHIEVEMENT_CONDITION_TYPE conditionType) {
		return GameData.findAchievements((t) -> {
			return t.conditionType == conditionType.value && t.id == id;
		});
	}

	/**
	 * @param targetID int类型
	 */
	public List<AchievementExt> findByConditionTypeAndTargetIdInt(Const.ACHIEVEMENT_CONDITION_TYPE conditionType, int targetID) {
		return GameData.findAchievements((ext) -> {
			return ext.conditionType == conditionType.value && Integer.parseInt(ext.targetID) == targetID;
		});
	}

	/**
	 * @param targetID String类型
	 */
	public List<AchievementExt> findByConditionTypeAndTargetIdString(Const.ACHIEVEMENT_CONDITION_TYPE conditionType, String targetID) {
		List<AchievementExt> achievementArray = GameData.findAchievements(new Predicate<AchievementExt>() {
			@Override
			public boolean test(AchievementExt ext) {
				return ext.conditionType == conditionType.value && (StringUtil.isEmpty(targetID) || (StringUtil.isNotEmpty(targetID) && ext.targetID.equals(targetID)));
			}
		});

		return achievementArray;
	}

	/**
	 * 成就改变
	 * 
	 * @param newSchedule 当前成就值
	 * @param achievementRecords 玩家成就数据
	 * @param typeRecords 玩家成就类型数据
	 * @param achievementArray 策划成就信息
	 * @param player
	 * @param isAdd 是否是累加
	 * @returns {Array}
	 */
	public List<AchievementHandler.Achievement> _onConditionChange(int newSchedule, List<AchievementExt> achievementArray, WNPlayer player, boolean isAdd) {
		Map<Integer, AchievementDataPO.AchievePO> achievementRecords = player.achievementManager.getAchievementRecords();
		if (achievementRecords == null)
			return null;

		List<AchievementHandler.Achievement> achievementDatas = new ArrayList<>();

		if (!player.functionOpenManager.isOpen(Const.FunctionType.ACHIEVEMENT.getValue())) {
			return achievementDatas;
		}

		for (AchievementExt achievement : achievementArray) {
			if (achievement == null)
				continue;
			AchievementConfigExt config = GameData.AchievementConfigs.get(achievement.chapterID);
			// if (config.quest != 0 &&
			// !player.taskManager.finishedNormalTasks.containsKey(config.quest)) {
			// continue;
			// }
			if (!GetChapterOpened(player.getId(), achievement.chapterID)) {
				continue;
			}

			AchievementDataPO.AchievePO achievementRecord = achievementRecords.get(achievement.id);
			if (achievementRecord == null) {
				achievementRecord = new AchievePO();
				achievementRecord.id = achievement.id;
				achievementRecord.scheduleCurr = 0;
				achievementRecords.put(achievement.id, achievementRecord);
			}
			if (achievementRecord.scheduleCurr >= achievement.targetNum) {
				// 已完成
				continue;
			}
			// 指定数量的部位强化达到xx级
			if (achievement.conditionType == Const.ACHIEVEMENT_CONDITION_TYPE.EQUIP_POS_LEVEL.value || achievement.conditionType == Const.ACHIEVEMENT_CONDITION_TYPE.PET_LEVEL.value) {
				int schedule = 0;
				if (achievementRecord.data == null) {
					schedule = 1;
					achievementRecord.data = new ArrayList<>();
					achievementRecord.data.add(String.valueOf(newSchedule));
				} else {
					boolean newpos = true;
					for (String pos : achievementRecord.data) {
						if (Integer.parseInt(pos) == newSchedule) {
							newpos = false;
							break;
						}
					}
					if (newpos) {
						achievementRecord.data.add(String.valueOf(newSchedule));
					}
					schedule = achievementRecord.data.size();
				}
				achievementRecord.scheduleCurr = schedule;
			} else {
				if (isAdd) {
					achievementRecord.scheduleCurr += newSchedule;
				} else {
					achievementRecord.scheduleCurr = newSchedule;
				}
			}
			if (achievementRecord.scheduleCurr >= achievement.targetNum) {
				// 当前成就项已完成
				achievementRecord.scheduleCurr = achievement.targetNum;

				// 整章已完成，可激活元始圣甲
				if (GetChapterFinished(player.getId(), achievement.chapterID)) {
					int holyArmourId = -1;
					for (ArmourAttributeCO armourAttributeCO : GameData.ArmourAttributes.values()) {
						if (armourAttributeCO.typeId == achievement.chapterID)
							holyArmourId = armourAttributeCO.iD;
					}
					if (holyArmourId == -1) {
						Out.error("脚本错误");
						return null;
					}

					HolyArmour holyArmour = player.achievementManager.achievementDataPO.holyArmourMap.get(holyArmourId);
					if(holyArmour.states == 1) {
						holyArmour.states = 2;
					}
					
				}

				achievementDatas.add(achievementToJson4Payload(achievementRecord));

				if (achievement.broadCast == 1) {
					// 广播
					String content = this.getColorContent(player.getPro(), player.getName(), achievement.name);
					MessageUtil.sendRollChat(player.getLogicServerId(), content, Const.CHAT_SCOPE.SYSTEM);
				}
			}
		}

		if (achievementDatas != null && achievementDatas.size() > 0) {
			WNNotifyManager.getInstance().pushAchievements(player, achievementDatas);
			player.achievementManager.updateSuperScript();
		}
		return achievementDatas;
	};

	/**
	 * 离线玩家好友成就获得
	 */
	public static void OnFriendAchieveOfOfflinePlayer(int newSchedule, List<AchievementExt> achievementArray, String playerId) {
		AchievementDataPO achievementDataPO = PlayerPOManager.findPO(ConstsTR.achievementTR, playerId, AchievementDataPO.class);

		Map<Integer, AchievementDataPO.AchievePO> achievementRecords = achievementDataPO.achievements;
		if (achievementRecords == null)
			return;

		// List<AchievementHandler.Achievement> achievementDatas = new ArrayList<>();

		if (!FunctionOpenManager.IsOpen(Const.FunctionType.ACHIEVEMENT.getValue(), playerId)) {
			return;
		}

		for (AchievementExt achievement : achievementArray) {
			if (achievement == null)
				continue;
			AchievementConfigExt config = GameData.AchievementConfigs.get(achievement.chapterID);
			// if (config.quest != 0 &&
			// !taskListPO.finishedNormalTasks.containsKey(config.quest)) {
			// continue;
			// }
			if (!GetChapterOpened(playerId, achievement.chapterID)) {
				continue;
			}

			AchievementDataPO.AchievePO achievementRecord = achievementRecords.get(achievement.id);
			if (achievementRecord == null) {
				// achievementRecord = new AchievePO();
				// achievementRecord.id = achievement.id;
				// achievementRecord.scheduleCurr = 0;
				// achievementRecords.put(achievement.id, achievementRecord);
				return;
			}
			if (achievementRecord.scheduleCurr >= achievement.targetNum) {
				// 已完成
				continue;
			}

			achievementRecord.scheduleCurr += newSchedule;
		}

		return;
	}

	/**
	 * 获取玩家所有章节完成度
	 */
	public static Map<Integer, int[]> GetChapterProgress(String playerId) {
		AchievementDataPO achievementDataPO = PlayerPOManager.findPO(ConstsTR.achievementTR, playerId, AchievementDataPO.class);

		Map<Integer, int[]> result = new HashMap<>();
		for (AchievementConfigExt achievementConfigExt : GameData.AchievementConfigs.values()) {

			List<AchievementExt> list_achieve = GameData.findAchievements(t -> {
				return t.chapterID == achievementConfigExt.id;
			});
			int[] ar = new int[2];
			ar[0] = 0;
			ar[1] = list_achieve.size();
			result.put(achievementConfigExt.id, ar);
		}

		for (AchievePO achievePO : achievementDataPO.achievements.values()) {
			AchievementExt prop = GameData.Achievements.get(achievePO.id);
			if (prop == null) {
				Out.error(AchievementManager.class, achievePO.id);
				continue;
			}
			// 章节未开启
			AchievementConfigExt config = GameData.AchievementConfigs.get(prop.chapterID);
			// if (config.quest != 0 &&
			// !TaskListPO.finishedNormalTasks.containsKey(config.quest)) {
			// continue;
			// }
			if (!GetChapterOpened(playerId, prop.chapterID)) {
				continue;
			}
			if (achievePO.awardState==1) {
				int[] ar = result.get(prop.chapterID);
				ar[0]++;
			}
			else if (achievePO.scheduleCurr >= prop.targetNum) {
				int[] ar = result.get(prop.chapterID);
				ar[0]++;
			}
		}

		for (Map.Entry<Integer, int[]> entry : result.entrySet()) {
			if (entry.getValue()[0] == entry.getValue()[1]) {
				int holyArmourId = -1;
				for (ArmourAttributeCO armourAttributeCO : GameData.ArmourAttributes.values()) {
					if (armourAttributeCO.typeId == entry.getKey())
						holyArmourId = armourAttributeCO.iD;
				}
				if (holyArmourId == -1) {
					Out.error("脚本错误");
					return null;
				}

				HolyArmour holyArmour = achievementDataPO.holyArmourMap.get(holyArmourId);
				if (holyArmour.states == 1) {
					holyArmour.states = 2;
				}
			}
		}

		return result;
	}

	/**
	 * 获取玩家某一章节是否开放
	 */
	public static boolean GetChapterOpened(String playerId, int chapterId) {
		AchievementConfigExt achievementConfigExt = GameData.AchievementConfigs.get(chapterId);
		TaskListPO taskListPO = PlayerPOManager.findPO(ConstsTR.taskTR, playerId, TaskListPO.class);
		PlayerPO playerPO = PlayerPOManager.findPO(ConstsTR.playerTR, playerId, PlayerPO.class);

		if (playerPO.level < achievementConfigExt.lv) {
			return false;
		}
		if (achievementConfigExt.quest != 0 && !taskListPO.finishedNormalTasks.containsKey(achievementConfigExt.quest)) {
			return false;
		}
		// if(achievementConfigExt.schieve!=0)
		// {
		// if(!GetChapterFinished(playerId,chapterId))
		// {
		// return false;
		// }
		// }
		return true;
	}

	/**
	 * 获取玩家某一章节是否完成
	 */
	public static boolean GetChapterFinished(String playerId, int chapterId) {
		Map<Integer, int[]> progress = GetChapterProgress(playerId);

		int[] ar = progress.get(chapterId);
		if (ar[0] == ar[1]) {
			return true;
		} else {
			return false;
		}
	}

}
