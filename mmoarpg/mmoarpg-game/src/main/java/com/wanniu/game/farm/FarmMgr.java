package com.wanniu.game.farm;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.TimerTask;
import java.util.concurrent.ScheduledFuture;

import com.wanniu.core.game.JobFactory;
import com.wanniu.core.game.LangService;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.util.RandomUtil;
import com.wanniu.game.bag.WNBag.SimpleItemInfo;
import com.wanniu.game.common.Const;
import com.wanniu.game.common.Const.ManagerType;
import com.wanniu.game.common.Const.PlayerEventType;
import com.wanniu.game.common.Const.VipType;
import com.wanniu.game.common.ConstsTR;
import com.wanniu.game.common.ModuleManager;
import com.wanniu.game.data.GameData;
import com.wanniu.game.data.GetLandCO;
import com.wanniu.game.data.PlantLevelCO;
import com.wanniu.game.data.PlantShopCO;
import com.wanniu.game.data.PlantingCO;
import com.wanniu.game.item.ItemConfig;
import com.wanniu.game.item.ItemUtil;
import com.wanniu.game.mail.SysMailConst;
import com.wanniu.game.player.GlobalConfig;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.poes.FarmPO;
import com.wanniu.redis.PlayerPOManager;

import pomelo.area.PlayerHandler.SuperScriptType;
import pomelo.farm.Farm;
import pomelo.farm.FarmHandler.CultivateFriendResponse;
import pomelo.farm.FarmHandler.HarvestResponse;
import pomelo.farm.FarmHandler.StealResponse;

/**
 * 果园管理
 * 
 * @author liyue
 *
 */
public class FarmMgr extends ModuleManager {

	public FarmPO myPO;
	// private String myId;
	private WNPlayer player;

	public static enum BLOCK_STATE {
		CLOSED(0), // 未开垦
		OPENED(1), // 已开垦
		SOWED(2); // 已种植

		public final int value;

		private BLOCK_STATE(int value) {
			this.value = value;
		}
	}

	public static enum SEED_STATE {
		UNSOWED(0), // 0：未播种
		SEEDED(1), // 1：种子期
		GROWED(2), // 2:成长期
		MATURE(3), // 3：成熟期
		HARVESTABLE(4);// 4：可收获

		public final int value;

		private SEED_STATE(int value) {
			this.value = value;
		}
	}

	public static enum CULTIVATE_TYPE {
		NONE(0), // 0：无需操作
		WATER(1), // 1：浇水
		BUG(2), // 2:除虫
		GRASS(3); // 3：除草

		public final int value;

		private CULTIVATE_TYPE(int value) {
			this.value = value;
		}
	}

	public static enum SEED_TYPE {
		APPLE(1), // 0：苹果
		PEAR(2), // 1：梨子
		PEACH(3), // 2:桃子
		CHERRIES(4), // 3：樱桃
		GRAPE(5); // 葡萄

		public final int value;

		private SEED_TYPE(int value) {
			this.value = value;
		}
	}

	public static enum SEED_QUALITY {
		BLUE(1), PURPLE(2), ORANGE(3), GREE(4), RED(5);

		public final int value;

		private SEED_QUALITY(int value) {
			this.value = value;
		}
	}

	public static enum OPEN_BLOCK {
		LV(1), DIAMOND(2), MONTHCARD(3), FOREVERCARD(4);

		public final int value;

		private OPEN_BLOCK(int value) {
			this.value = value;
		}
	}

	public static enum RECORD_TYPE {
		FRIEND_WATER(1), FRIEND_BUG(2), FRIEND_GRASS(3), FRIEND_STEAL(4), SOW(5), WATER(6), BUG(7), GRASS(8),
		// FRIEND_WATER(9),
		FORGET_WATER(10), FORGET_BUG(11), FORGET_GRASS(12), HARVEST(13);

		public final int value;

		private RECORD_TYPE(int value) {
			this.value = value;
		}
	}

	/**
	 * 田块信息
	 * 
	 * @author liyue
	 *
	 */
	public static class Block {
		public int blockId; // 果园地块id
		public BLOCK_STATE blockState; // 地块状态 (0:未开垦 1:已开垦 3:已种植)
		public String seedCode; // 种子代码
		// public SEED_QUALITY seedQuality;//种子品质
		public SEED_STATE seedState; // 种子成长状态 (0:未播种 1:种子期 2:成长期 3:成熟期 4:可收获)
		public CULTIVATE_TYPE cultivateType; // 成长周期中的培育操作种类 (0:无需操作 1:浇水 2:除虫 3:除草 )
		// public boolean cultivateState; // 成长周期中的培育操作状态 (true:完成 false:未完成 )

		public int friendCultivateNum; // 好友帮助次数
		public List<String> stolerList; // 偷取者列表

		// public Date startTime; // 本阶段开始时间
		public Date nextTime; // 进入下一个阶段的时间戳,如果可收获，则表示进入此状态的时间
		// public Date protectEndTime; // 保护期结束的时间
		public int missTime;// 错过的培育次数

		public Block() {
			this.blockState = BLOCK_STATE.CLOSED;
			this.seedCode = null;
			this.seedState = SEED_STATE.UNSOWED;
			this.cultivateType = CULTIVATE_TYPE.NONE;
			this.friendCultivateNum = 0;
			this.stolerList = new LinkedList<>();
			this.nextTime = null;
			this.missTime = 0;
		}

		public Block(int blockId) {
			this();
			this.blockId = blockId;

		}
	}

	/**
	 * 种子信息
	 * 
	 * @author liyue
	 *
	 */
	public static class Seed {
		public String seedCode;// 种子代码
		public int num; // 种子数量

		public Seed() {
			num = 0;
		}

		public Seed(String seedCode, int num) {
			this();
			this.seedCode = seedCode;
			this.num = num;
		}

	}

	/**
	 * 果实信息
	 * 
	 * @author liyue
	 *
	 */
	public static class Product {
		public String productCode;// 种子代码
		public int num; // 果实数量

		public Product() {
			num = 0;
		}

		public Product(String productCode, int num) {
			this();
			this.productCode = productCode;
			this.num = num;
		}

	}

	/**
	 * 日志信息
	 * 
	 * @author liyue
	 *
	 */
	public static class RecordInfo {
		public RECORD_TYPE recordType; // 记录类型
		public Date recordTime;// 记录时间
		public List<String> recordParams;// = new ArrayList<String>(); // 记录所需参数（其他参数根据recordType 去配置里面查找 ）

		public RecordInfo() {
			this.recordType = RECORD_TYPE.WATER;
			this.recordTime = new Date();
			this.recordParams = new LinkedList<>();
		}

		public RecordInfo(RECORD_TYPE recordType, List<String> recordParams) {
			// this();
			this.recordType = recordType;
			this.recordTime = new Date();
			this.recordParams = recordParams;
		}
	}

	public FarmMgr(WNPlayer player) {

		this.player = player;
		this.myPO = getPlayerPO(player.getId());

		UpdateBlocks();

		Date now = new Date();
		if (!FarmMgr.scheduleMap.containsKey(player.getId())) {
			Map<Integer, ScheduledFuture<?>> s_map = FarmMgr.scheduleMap.put(player.getId(), new HashMap<>());
			for (Block block : this.myPO.blockMap.values()) {
				PlantingCO plantingCO = ItemConfig.plantingMap.get(block.seedCode);
				// 过期处理
				// 跳过多余的生长阶段
				if (block.seedState == SEED_STATE.SEEDED || block.seedState == SEED_STATE.GROWED || block.seedState == SEED_STATE.MATURE) {
					while (now.getTime() >= block.nextTime.getTime()) {
						if (block.seedState == SEED_STATE.SEEDED) {
							block.seedState = SEED_STATE.GROWED;
							int nextStageProb = getNextStageProb(block);
							long nextnextTimeStamp = block.nextTime.getTime() + plantingCO.growTime * 60 * 1000 * nextStageProb / 100;

							block.nextTime = new Date(nextnextTimeStamp);
							if (block.cultivateType != CULTIVATE_TYPE.NONE)
								block.missTime++;
							block.cultivateType = getRandomCultivateType();
						} else if (block.seedState == SEED_STATE.GROWED) {
							block.seedState = SEED_STATE.MATURE;
							int nextStageProb = getNextStageProb(block);
							long nextnextTimeStamp = block.nextTime.getTime() + plantingCO.growTime * 60 * 1000 * nextStageProb / 100;

							block.nextTime = new Date(nextnextTimeStamp);
							if (block.cultivateType != CULTIVATE_TYPE.NONE)
								block.missTime++;
							block.cultivateType = getRandomCultivateType();
						} else if (block.seedState == SEED_STATE.MATURE) {
							block.seedState = SEED_STATE.HARVESTABLE;

							// block.nextTime=null;
							if (block.cultivateType != CULTIVATE_TYPE.NONE)
								block.missTime++;
							block.cultivateType = CULTIVATE_TYPE.NONE;
							break;
						}
					}

					if (block.seedState != SEED_STATE.HARVESTABLE) {
						ScheduledFuture<?> scheduledFuture = JobFactory.addDelayJob(new BlockTimerTask(myPO.playerId, block.blockId), block.nextTime.getTime() - now.getTime());
						s_map.put(block.blockId, scheduledFuture);
					}

				}

			}
		}

	}

	// 获取下一个成长阶段，成长时间占总时间的百分比，非成长期返回-1
	public static int getNextStageProb(Block block) {
		int stageProb = -1;
		if (block.seedState == SEED_STATE.SEEDED) {
			stageProb = GlobalConfig.Planting_GrowStageProp;
		} else if (block.seedState == SEED_STATE.GROWED) {
			stageProb = GlobalConfig.Planting_MatureStageProp;
		}
		return stageProb;
	}

	// 随机获取一个可培育的状态
	public static CULTIVATE_TYPE getRandomCultivateType() {
		CULTIVATE_TYPE[] cultivateTypes = { CULTIVATE_TYPE.WATER, CULTIVATE_TYPE.BUG, CULTIVATE_TYPE.GRASS };
		return cultivateTypes[RandomUtil.getInt(0, cultivateTypes.length - 1)];
	}

	// 预估可收获的时间,非成长状态返回null
	public static Date evaluateHarvestTime(String playerId, int blockId) {
		FarmPO tempPO = PlayerPOManager.findPO(ConstsTR.player_farmTR, playerId, FarmPO.class);
		Block block = tempPO.blockMap.get(blockId);
		PlantingCO plantingCO = ItemConfig.plantingMap.get(block.seedCode);

		if (block.seedState == SEED_STATE.UNSOWED || block.seedState == SEED_STATE.HARVESTABLE) {
			return null;
		} else {
			long endTimeStamp = block.nextTime.getTime();// +plantingCO.growTime*60*1000*stageProb/100;
			if (block.seedState == SEED_STATE.GROWED || block.seedState == SEED_STATE.MATURE) {
				endTimeStamp += plantingCO.growTime * 60 * 1000 * GlobalConfig.Planting_SeedStageProp / 100;

				if (block.seedState == SEED_STATE.MATURE) {
					endTimeStamp += plantingCO.growTime * 60 * 1000 * GlobalConfig.Planting_MatureStageProp / 100;

				}
			}
			return new Date(endTimeStamp);
		}
	}

	// 预估保护时间结束的时间，不处于保护时间内时，返回null
	public static Date getProtectEndTime(String playerId, int blockId) {
		FarmPO tempPO = PlayerPOManager.findPO(ConstsTR.player_farmTR, playerId, FarmPO.class);
		Block block = tempPO.blockMap.get(blockId);
		if (block.seedState != SEED_STATE.HARVESTABLE) {
			return null;
		}
		Date now = new Date();
		long protectEndTimeStamp = block.nextTime.getTime() + GlobalConfig.Planting_ProtectionTime * 60 * 1000;
		if (now.getTime() < protectEndTimeStamp)
			return new Date(protectEndTimeStamp);
		else {
			return null;
		}

	}

	// 日志操作
	public static void AddRecord(Queue<RecordInfo> recordQueue, RecordInfo recordInfo) {
		if (recordQueue.size() >= GlobalConfig.Planting_MaxRecord) {
			recordQueue.poll();
		}
		recordQueue.add(recordInfo);
	}

	///////////////////////////////////////////////////////////////////////////////////////

	private static Map<String, Map<Integer, ScheduledFuture<?>>> scheduleMap = new HashMap<>();

	public static class BlockTimerTask extends TimerTask {
		String playerId;
		int blockId;

		public BlockTimerTask(String playerId, int blockId) {
			this.playerId = playerId;
			this.blockId = blockId;
		}

		@Override
		public void run() {
			// ScheduledFuture<?>
			// scheduledFuture=FarmMgr.scheduleMap.get(playerId).get(blockId);
			FarmPO tempPO = PlayerPOManager.findPO(ConstsTR.player_farmTR, playerId, FarmPO.class);
			Block block = tempPO.blockMap.get(blockId);
			PlantingCO plantingCO = ItemConfig.plantingMap.get(block.seedCode);
			if (block.seedState == SEED_STATE.SEEDED) {
				block.seedState = SEED_STATE.GROWED;
				int nextStageProb = getNextStageProb(block);
				long nextnextTimeStamp = block.nextTime.getTime() + plantingCO.growTime * 60 * 1000 * nextStageProb / 100;

				block.nextTime = new Date(nextnextTimeStamp);
				if (block.cultivateType != CULTIVATE_TYPE.NONE) {
					block.missTime++;
					// 日志
					RECORD_TYPE recordType = null;
					if (block.cultivateType == CULTIVATE_TYPE.WATER)
						recordType = RECORD_TYPE.FORGET_WATER;
					else if (block.cultivateType == CULTIVATE_TYPE.BUG)
						recordType = RECORD_TYPE.FORGET_BUG;
					else if (block.cultivateType == CULTIVATE_TYPE.GRASS)
						recordType = RECORD_TYPE.FORGET_GRASS;
					List<String> recordParams = new LinkedList<>();
					recordParams.add(GameData.Miscs.get(plantingCO.product).name);
					recordParams.add(GameData.Miscs.get(plantingCO.product).name);
					RecordInfo recordInfo = new RecordInfo(recordType, recordParams);
					AddRecord(tempPO.recordLs, recordInfo);
				}
				block.cultivateType = getRandomCultivateType();
			} else if (block.seedState == SEED_STATE.GROWED) {
				block.seedState = SEED_STATE.MATURE;
				int nextStageProb = getNextStageProb(block);
				long nextnextTimeStamp = block.nextTime.getTime() + plantingCO.growTime * 60 * 1000 * nextStageProb / 100;

				block.nextTime = new Date(nextnextTimeStamp);
				if (block.cultivateType != CULTIVATE_TYPE.NONE) {
					block.missTime++;
					// 日志
					RECORD_TYPE recordType = null;
					if (block.cultivateType == CULTIVATE_TYPE.WATER)
						recordType = RECORD_TYPE.FORGET_WATER;
					else if (block.cultivateType == CULTIVATE_TYPE.BUG)
						recordType = RECORD_TYPE.FORGET_BUG;
					else if (block.cultivateType == CULTIVATE_TYPE.GRASS)
						recordType = RECORD_TYPE.FORGET_GRASS;
					List<String> recordParams = new LinkedList<>();
					recordParams.add(GameData.Miscs.get(plantingCO.product).name);
					recordParams.add(GameData.Miscs.get(plantingCO.product).name);
					RecordInfo recordInfo = new RecordInfo(recordType, recordParams);
					AddRecord(tempPO.recordLs, recordInfo);
				}
				block.cultivateType = getRandomCultivateType();
			} else if (block.seedState == SEED_STATE.MATURE) {
				block.seedState = SEED_STATE.HARVESTABLE;

				// block.nextTime=null;
				if (block.cultivateType != CULTIVATE_TYPE.NONE) {
					block.missTime++;
					// 日志
					RECORD_TYPE recordType = null;
					if (block.cultivateType == CULTIVATE_TYPE.WATER)
						recordType = RECORD_TYPE.FORGET_WATER;
					else if (block.cultivateType == CULTIVATE_TYPE.BUG)
						recordType = RECORD_TYPE.FORGET_BUG;
					else if (block.cultivateType == CULTIVATE_TYPE.GRASS)
						recordType = RECORD_TYPE.FORGET_GRASS;
					List<String> recordParams = new LinkedList<>();
					recordParams.add(GameData.Miscs.get(plantingCO.product).name);
					recordParams.add(GameData.Miscs.get(plantingCO.product).name);
					RecordInfo recordInfo = new RecordInfo(recordType, recordParams);
					AddRecord(tempPO.recordLs, recordInfo);
				}
				block.cultivateType = getRandomCultivateType();

			}

			if (block.seedState != SEED_STATE.HARVESTABLE) {
				ScheduledFuture<?> scheduledFuture2 = JobFactory.addDelayJob(new BlockTimerTask(tempPO.playerId, block.blockId), block.nextTime.getTime() - (new Date()).getTime());
				scheduleMap.get(playerId).put(block.blockId, scheduledFuture2);
			}
		}

	}

	// 获取任意玩家po,如果没有则创建
	public FarmPO getPlayerPO(String playerId) {
		FarmPO po = PlayerPOManager.findPO(ConstsTR.player_farmTR, playerId, FarmPO.class);
		if (po == null) {
			po = new FarmPO(playerId);
			PlayerPOManager.put(ConstsTR.player_farmTR, playerId, po);
		}
		// UpdateBlocksOfOnlinePlayer(po);
		return po;
	}

	// 获取玩家果园是否可播种
	public boolean getPlayerSowable(String playerId) {
		FarmPO playerPO = PlayerPOManager.findPO(ConstsTR.player_farmTR, playerId, FarmPO.class);
		if(playerPO==null)
		{
			return false;
		}
		boolean sowable = false;
		for (Block block : playerPO.blockMap.values()) {
			if (block.blockState == BLOCK_STATE.OPENED) {
				sowable = true;
				break;

			}
		}
		return sowable;
	}

	// 获取玩家果园是否可培育
	public boolean getPlayerCultivatable(String playerId) {
//		FarmPO playerPO = PlayerPOManager.findPO(ConstsTR.player_farmTR, playerId, FarmPO.class);
//		if(playerPO==null)
//		{
//			return false;
//		}

		for (Block block : myPO.blockMap.values()) {
			if (block.blockState == BLOCK_STATE.SOWED) {
				if (block.seedState == SEED_STATE.SEEDED || block.seedState == SEED_STATE.GROWED || block.seedState == SEED_STATE.MATURE) {
					if (block.cultivateType != CULTIVATE_TYPE.NONE) {
						return true;
					}
				}

			}
		}
		return false;
	}
	
	// 获取好友果园是否可培育
	public boolean getFriendCultivatable(String friendId) {
		FarmPO friendPO = PlayerPOManager.findPO(ConstsTR.player_farmTR, friendId, FarmPO.class);
		if(friendPO==null)
		{
			return false;
		}
		if(friendPO.blockMap==null)
		{
			return false;
		}
		Set<String> friendIdSet = player.friendManager.getAllFriendId();
		if (!friendIdSet.contains(friendId)) {

			return false;
		}
		for (Block block : friendPO.blockMap.values()) {
			if (block.blockState == BLOCK_STATE.SOWED) {
				if (block.seedState == SEED_STATE.SEEDED || block.seedState == SEED_STATE.GROWED || block.seedState == SEED_STATE.MATURE) {
					if (block.cultivateType != CULTIVATE_TYPE.NONE) {
						return true;
					}
				}

			}
		}
		return false;
	}

	// 获取玩家果园是否可收获
	public boolean getPlayerHarvestable(String playerId) {
//		FarmPO playerPO = PlayerPOManager.findPO(ConstsTR.player_farmTR, playerId, FarmPO.class);
//		if(playerPO==null)
//		{
//			return false;
//		}

		for (Block block : myPO.blockMap.values()) {
			if (block.blockState == BLOCK_STATE.SOWED) {
				if (block.seedState == SEED_STATE.HARVESTABLE) {
					return true;
				}

			}
		}
		return false;
	}

	// 获取玩家果园是否可偷取
	public boolean getPlayerStealable(String playerId) {
		FarmPO playerPO = PlayerPOManager.findPO(ConstsTR.player_farmTR, playerId, FarmPO.class);
		if(playerPO==null)
		{
			return false;
		}
		if(playerPO.blockMap==null)
		{
			return false;
		}

		for (Block block : playerPO.blockMap.values()) {
			if (block.blockState == BLOCK_STATE.SOWED) {
				if (block.seedState == SEED_STATE.MATURE) {
					Date now=new Date();
					if(now.getTime() >= block.nextTime.getTime() + GlobalConfig.Planting_ProtectionTime * 60 * 1000)
					{
						if (!block.stolerList.contains(myPO.playerId)) {
							return true;
						}
						
					}
				}
			}
		}
		
		return false;
	}

	// 获取某块田是否可培育
//	public boolean getBlockCultivatable(String playerId, int blockId) {
//		FarmPO playerPO = PlayerPOManager.findPO(ConstsTR.player_farmTR, playerId, FarmPO.class);
//		if(playerPO==null)
//		{
//			return false;
//		}
//		Block block = playerPO.blockMap.get(blockId);
//
//		if (block.blockState == BLOCK_STATE.SOWED) {
//			if (block.seedState == SEED_STATE.SEEDED || block.seedState == SEED_STATE.GROWED || block.seedState == SEED_STATE.MATURE) {
//				if (block.cultivateType != CULTIVATE_TYPE.NONE) {
//					return true;
//				}
//			}
//
//		}
//
//		return false;
//	}

	// 获取某块田是否可偷取
//	public boolean getBlockStealable(String playerId, int blockId) {
//		FarmPO playerPO = PlayerPOManager.findPO(ConstsTR.player_farmTR, playerId, FarmPO.class);
//		if(playerPO==null)
//		{
//			return false;
//		}
//		Block block = playerPO.blockMap.get(blockId);
//
//		if (block.blockState == BLOCK_STATE.SOWED) {
//			if (block.seedState == SEED_STATE.MATURE) {
//				Date now = new Date();
//				if (now.getTime() >= block.nextTime.getTime() + GlobalConfig.Planting_ProtectionTime * 60 * 1000) {
////					Set<String> friendIdSet = player.friendManager.getAllFriendId();
////					if (friendIdSet.contains(playerId)) {
////
////						return true;
////					}
//					return true;
//				}
//			}
//		}
//
//		return false;
//	}

	// 更新在线玩家的田块，开启其中符合条件的
	public void UpdateBlocks() {
		// WNPlayer player = PlayerUtil.getOnlinePlayer(myPO.playerId);
		// if (null == player) {
		// return;
		// }

		for (Block block : myPO.blockMap.values()) {
			GetLandCO getLandCO = GameData.GetLands.get(block.blockId);

			boolean open = false;

			if (getLandCO.getType == OPEN_BLOCK.LV.value) {
				if (player.getLevel() >= getLandCO.value) {
					open = true;
				}
			} else if (getLandCO.getType == OPEN_BLOCK.DIAMOND.value) {
				if (VipType.month.value == player.baseDataManager.getVip() || VipType.sb_double.value == player.baseDataManager.getVip()) {
					open = true;
				}
			} else if (getLandCO.getType == OPEN_BLOCK.FOREVERCARD.value) {
				if (VipType.forever.value == player.baseDataManager.getVip() || VipType.sb_double.value == player.baseDataManager.getVip()) {
					open = true;
				}
			}

			if (open) {
				block.blockState = BLOCK_STATE.OPENED;
				block.seedState = SEED_STATE.UNSOWED;
			}

		}

		updateSuperScriptList();
	}

	// 开垦
	public boolean open(int blockId) {
		GetLandCO getLandCO = GameData.GetLands.get(blockId);

		// 玩家不在线
		// WNPlayer player = PlayerUtil.getOnlinePlayer(myPO.playerId);
		// if (null == player) {
		// return false;
		// }

		// 只有元宝开启需要手动开启
		if (getLandCO.getType != OPEN_BLOCK.DIAMOND.value) {
			return false;
		}

		// 元宝不足
		if (!player.moneyManager.enoughDiamond(getLandCO.value)) {
			return false;
		}

		Block block = myPO.blockMap.get(blockId);

		if (block.blockState != BLOCK_STATE.CLOSED) {
			return false;
		}

		player.moneyManager.costDiamond(getLandCO.value, Const.GOODS_CHANGE_TYPE.FarmOpen);

		block.blockState = BLOCK_STATE.OPENED;
		block.seedState = SEED_STATE.UNSOWED;

		updateSuperScriptList();

		return true;
	}

	// 播种
	public boolean sow(int blockId, String seedCode) {
		Block block = myPO.blockMap.get(blockId);
		PlantingCO plantingCO = ItemConfig.plantingMap.get(block.seedCode);

		// 玩家不在线
		// WNPlayer player = PlayerUtil.getOnlinePlayer(myPO.playerId);
		// if (null == player) {
		// return false;
		// }

		// 田块不可播种
		if (block.blockState != BLOCK_STATE.OPENED) {
			return false;
		}

		// 种植等级不足
		if (myPO.lv < plantingCO.plantLevel) {
			return false;
		}

		int seedNum = player.bag.findItemNumByCode(seedCode);

		// 种子不足
		if (seedNum < 1) {
			return false;
		}

		player.bag.discardItem(seedCode, 1, Const.GOODS_CHANGE_TYPE.FarmSow);

		block.seedCode = seedCode;
		block.blockState = BLOCK_STATE.SOWED;
		block.seedState = SEED_STATE.SEEDED;
		// CULTIVATE_TYPE[] cultivateTypeArray=
		// {CULTIVATE_TYPE.WATER,CULTIVATE_TYPE.BUG,CULTIVATE_TYPE.GRASS};
		block.cultivateType = getRandomCultivateType();// cultivateTypeArray[RandomUtil.getInt(0, cultivateTypeArray.length-1)];

		int nextStageProb = getNextStageProb(block);
		long nextnextTimeStamp = block.nextTime.getTime() + plantingCO.growTime * 60 * 1000 * nextStageProb / 100;
		block.nextTime = new Date(nextnextTimeStamp);

		// 日志
		RECORD_TYPE recordType = RECORD_TYPE.SOW;

		List<String> recordParams = new LinkedList<>();
		recordParams.add(plantingCO.name);
		RecordInfo recordInfo = new RecordInfo(recordType, recordParams);
		AddRecord(myPO.recordLs, recordInfo);

		ScheduledFuture<?> scheduledFuture = JobFactory.addDelayJob(new BlockTimerTask(myPO.playerId, block.blockId), nextnextTimeStamp);
		scheduleMap.get(myPO.playerId).put(block.blockId, scheduledFuture);

		updateSuperScriptList();

		return true;

	}

	// 帮助好友
	public CultivateFriendResponse.Builder cultivateFriend(int blockId, String playerId) {
		CultivateFriendResponse.Builder builder = CultivateFriendResponse.newBuilder();

		// 不是好友
		Set<String> friendIdSet = player.friendManager.getAllFriendId();
		if (!friendIdSet.contains(playerId)) {
			builder.setS2CCode(PomeloRequest.FAIL);
			builder.setS2CMsg(LangService.getValue("FARM_NOT_MY_FRIEND"));

			return builder;
		}

		FarmPO friendPO = PlayerPOManager.findPO(ConstsTR.player_farmTR, playerId, FarmPO.class);
		if (friendPO==null) {
			builder.setS2CCode(PomeloRequest.FAIL);
			builder.setS2CMsg(LangService.getValue("FARM_PLAYER_PO_NOT_FOUND"));

			return builder;
		}
		Block block = friendPO.blockMap.get(blockId);

		// 未播种
		if (block.blockState != BLOCK_STATE.SOWED) {
			builder.setS2CCode(PomeloRequest.FAIL);
			builder.setS2CMsg(LangService.getValue("FARM_UNSOWED"));

			return builder;
		}

		// 无需操作
		if (block.cultivateType == CULTIVATE_TYPE.NONE) {
			builder.setS2CCode(PomeloRequest.FAIL);
			builder.setS2CMsg(LangService.getValue("FARM_CULTIVATE_FAIL"));

			return builder;
		}

		// 日志
		RECORD_TYPE recordType = null;
		if (block.cultivateType == CULTIVATE_TYPE.WATER)
			recordType = RECORD_TYPE.FRIEND_WATER;
		else if (block.cultivateType == CULTIVATE_TYPE.BUG)
			recordType = RECORD_TYPE.FRIEND_BUG;
		else if (block.cultivateType == CULTIVATE_TYPE.GRASS)
			recordType = RECORD_TYPE.FRIEND_GRASS;
		List<String> recordParams = new LinkedList<>();
		recordParams.add(player.getName());
		RecordInfo recordInfo = new RecordInfo(recordType, recordParams);
		AddRecord(friendPO.recordLs, recordInfo);

		// 个人数据
		block.cultivateType = CULTIVATE_TYPE.NONE;
		int fate = player.processXianYuanGet(GlobalConfig.Fate_Plant);

		builder.setFate(fate);
		builder.setS2CCode(PomeloRequest.OK);

		updateSuperScriptList();

		return builder;
	}

	// 培育自己
	public boolean cultivateSelf(int blockId) {
		// FarmPO tempPO=PlayerPOManager.findPO(ConstsTR.player_farmTR, playerId,
		// FarmPO.class);

		Block block = myPO.blockMap.get(blockId);

		// 未播种
		if (block.blockState != BLOCK_STATE.SOWED) {
			return false;
		}

		// 无需操作
		if (block.cultivateType == CULTIVATE_TYPE.NONE) {
			return false;
		}

		// 日志
		RECORD_TYPE recordType = null;
		if (block.cultivateType == CULTIVATE_TYPE.WATER)
			recordType = RECORD_TYPE.WATER;
		else if (block.cultivateType == CULTIVATE_TYPE.BUG)
			recordType = RECORD_TYPE.BUG;
		else if (block.cultivateType == CULTIVATE_TYPE.GRASS)
			recordType = RECORD_TYPE.GRASS;
		List<String> recordParams = new LinkedList<>();
		PlantingCO plantingCO = ItemConfig.plantingMap.get(block.seedCode);
		recordParams.add(GameData.Miscs.get(plantingCO.product).name);
		RecordInfo recordInfo = new RecordInfo(recordType, recordParams);
		AddRecord(myPO.recordLs, recordInfo);

		block.cultivateType = CULTIVATE_TYPE.NONE;

		updateSuperScriptList();

		return true;
	}

	// 收获
	public HarvestResponse.Builder harvest(int blockId) {
		HarvestResponse.Builder builder = HarvestResponse.newBuilder();

		Block block = myPO.blockMap.get(blockId);

		// 未播种
		if (block.blockState != BLOCK_STATE.SOWED) {
			builder.setS2CCode(PomeloRequest.FAIL);
			builder.setS2CMsg(LangService.getValue("FARM_UNSOWED"));
			return builder;
		}

		// 未成熟
		if (block.seedState != SEED_STATE.HARVESTABLE) {
			builder.setS2CCode(PomeloRequest.FAIL);
			builder.setS2CMsg(LangService.getValue("FARM_UNMATURE"));
			return builder;
		}

		// 技能升级
		PlantingCO plantingCO = ItemConfig.plantingMap.get(block.seedCode);
		PlantLevelCO plantLevelCO = GameData.PlantLevels.get(myPO.lv);
		PlantLevelCO plantLevelCOMax = GameData.PlantLevels.get(GameData.PlantLevels.size());
		if (myPO.lv < plantLevelCOMax.level) {
			myPO.exp += plantingCO.getExp;
			if (myPO.exp >= plantLevelCO.exp) {
				myPO.lv++;
				myPO.exp -= plantingCO.getExp;
				if (myPO.lv == plantLevelCOMax.level)
					myPO.exp = 0;
			}
		}

		// 结算收益
		// player.bag.addCodeItem(plantingCO.product, , forceType, fromDes);
		int rewardNum = plantingCO.harvest * (100 - block.missTime * GlobalConfig.Planting_DeductionProp) * (100 + plantLevelCO.harvestAdd) * (100 - block.stolerList.size() * (100 - GlobalConfig.Planting_StealProp)) / 1000000;
		player.bag.addCodeItemMail(plantingCO.product, rewardNum, Const.ForceType.DEFAULT, Const.GOODS_CHANGE_TYPE.FarmHarvest, SysMailConst.BAG_FULL_COMMON);

		// 修改地块
		block.blockState = BLOCK_STATE.OPENED;
		block.seedCode = null;
		block.seedState = SEED_STATE.UNSOWED;
		block.cultivateType = CULTIVATE_TYPE.NONE;
		block.friendCultivateNum = 0;
		block.stolerList.clear();
		block.nextTime = null;
		block.missTime = 0;

		// 日志
		RECORD_TYPE recordType = RECORD_TYPE.HARVEST;

		List<String> recordParams = new LinkedList<>();
		recordParams.add(String.valueOf(rewardNum));
		recordParams.add(GameData.Miscs.get(plantingCO.product).name);
		RecordInfo recordInfo = new RecordInfo(recordType, recordParams);
		AddRecord(myPO.recordLs, recordInfo);

		Farm.Block.Builder farmBlockBuilder = Farm.Block.newBuilder();
		farmBlockBuilder.setBlockId(blockId);
		farmBlockBuilder.setBlockState(block.blockState.value);
		farmBlockBuilder.setSeedCode(block.seedCode);
		farmBlockBuilder.setSeedState(block.seedState.value);
		farmBlockBuilder.setCultivateType(block.cultivateType.value);

		builder.setBlock(farmBlockBuilder.build());

		builder.setS2CCode(PomeloRequest.OK);

		// block.cultivateType=CULTIVATE_TYPE.NONE;
		updateSuperScriptList();

		return builder;
	}

	// 偷取
	public StealResponse.Builder steal(int blockId, String playerId) {
		StealResponse.Builder builder = StealResponse.newBuilder();

		// 不是好友
		Set<String> friendIdSet = player.friendManager.getAllFriendId();
		if (!friendIdSet.contains(playerId)) {
			builder.setS2CCode(PomeloRequest.FAIL);
			builder.setS2CMsg(LangService.getValue("FARM_NOT_MY_FRIEND"));

			return builder;
		}

		FarmPO friendPO = PlayerPOManager.findPO(ConstsTR.player_farmTR, playerId, FarmPO.class);
		if (friendPO==null) {
			builder.setS2CCode(PomeloRequest.FAIL);
			builder.setS2CMsg(LangService.getValue("FARM_PLAYER_PO_NOT_FOUND"));

			return builder;
		}
		Block block = friendPO.blockMap.get(blockId);

		// 未播种
		if (block.blockState != BLOCK_STATE.SOWED) {
			builder.setS2CCode(PomeloRequest.FAIL);
			builder.setS2CMsg(LangService.getValue("FARM_UNSOWED"));
			return builder;
		}

		// 未成熟
		if (block.seedState != SEED_STATE.HARVESTABLE) {
			builder.setS2CCode(PomeloRequest.FAIL);
			builder.setS2CMsg(LangService.getValue("FARM_UNMATURE"));
			return builder;
		}

		// 处于保护期
		Date now = new Date();
		if (now.getTime() < block.nextTime.getTime() + GlobalConfig.Planting_ProtectionTime * 60 * 1000) {
			builder.setS2CCode(PomeloRequest.FAIL);
			builder.setS2CMsg(LangService.getValue("FARM_IN_PROTECTION"));
			return builder;
		}

		// 自己已经偷取过了
		if (block.stolerList.contains(myPO.playerId)) {
			builder.setS2CCode(PomeloRequest.FAIL);
			builder.setS2CMsg(LangService.getValue("FARM_CANNOT_STEAL"));
			return builder;
		}

		// 修改数据
		PlantingCO plantingCO = ItemConfig.plantingMap.get(block.seedCode);
		PlantLevelCO plantLevelCO = GameData.PlantLevels.get(friendPO.lv);
		int awardNum = plantingCO.harvest * (100 - block.missTime * GlobalConfig.Planting_DeductionProp) * (100 + plantLevelCO.harvestAdd) * GlobalConfig.Planting_StealProp / 1000000;
		player.bag.addCodeItemMail(plantingCO.product, awardNum, Const.ForceType.DEFAULT, Const.GOODS_CHANGE_TYPE.FarmSteal, SysMailConst.BAG_FULL_COMMON);
		block.stolerList.add(myPO.playerId);

		// 日志
		RECORD_TYPE recordType = RECORD_TYPE.FRIEND_STEAL;

		List<String> recordParams = new LinkedList<>();
		recordParams.add(player.getName());
		recordParams.add(String.valueOf(awardNum));
		recordParams.add(GameData.Miscs.get(plantingCO.product).name);
		RecordInfo recordInfo = new RecordInfo(recordType, recordParams);
		AddRecord(myPO.recordLs, recordInfo);

		builder.setProductCode(plantingCO.product);
		builder.setNum(awardNum);
		builder.setS2CCode(PomeloRequest.OK);

		updateSuperScriptList();

		return builder;
	}

	// 兑换商品
	public boolean ChangeShopItem(int itemId) {
		PlantShopCO plantShopCO = GameData.PlantShops.get(itemId);
		if (myPO.shopToday.containsKey(itemId)) {
			if (myPO.shopToday.get(itemId) >= plantShopCO.changeNum)
				return false;
		}
		if (!player.bag.discardItemsByCode(plantShopCO.parameter, Const.GOODS_CHANGE_TYPE.FarmChange))
			return false;
		List<SimpleItemInfo> simpleItemInfos = ItemUtil.parseString(plantShopCO.parameter);
		for (SimpleItemInfo simpleItemInfo : simpleItemInfos) {
			player.bag.addCodeItemMail(simpleItemInfo.itemCode, simpleItemInfo.itemNum, Const.ForceType.DEFAULT, Const.GOODS_CHANGE_TYPE.FarmChange, SysMailConst.BAG_FULL_COMMON);
		}
		return true;
	}

	public void refreshNewDay() {
		myPO.shopToday.clear();

		updateSuperScriptList();
	}

	public List<SuperScriptType> getSuperScriptList() {
		List<SuperScriptType> ls = new ArrayList<SuperScriptType>();

		int meSowable = getPlayerSowable(player.getId()) ? 1 : 0;
		int meCultivatable = getPlayerCultivatable(player.getId()) ? 1 : 0;
		int meHarvestable = getPlayerHarvestable(player.getId()) ? 1 : 0;

		ls.add(createSuperScriptType(Const.SUPERSCRIPT_TYPE.FARM_CULTIVATE.getValue(), meSowable + meCultivatable + meHarvestable));

		for (String friendId : player.friendManager.getAllFriendId()) {
			int friendStealable = getPlayerStealable(friendId) ? 1 : 0;
			int friendCultivatable = getFriendCultivatable(friendId) ? 1 : 0;
			ls.add(createSuperScriptType(Const.SUPERSCRIPT_TYPE.FARM_FRIEND.getValue(), friendStealable + friendCultivatable));
		}

		return ls;
	}

	private SuperScriptType createSuperScriptType(int type, int num) {
		SuperScriptType.Builder data = SuperScriptType.newBuilder();
		data.setType(type);
		data.setNumber(num);
		return data.build();
	}

	public void updateSuperScriptList() {
		List<SuperScriptType> ls = getSuperScriptList();
		this.player.updateSuperScriptList(ls); // 红点推送给玩家
	}

	@Override
	public void onPlayerEvent(PlayerEventType eventType) {
		// TODO Auto-generated method stub
		switch (eventType) {
		case UPGRADE:
		case PAY:
			UpdateBlocks();
			break;

		default:
			break;
		}
	}

	@Override
	public ManagerType getManagerType() {
		// TODO Auto-generated method stub
		return ManagerType.FARM;
	}

	//
	// public JSONObject water(int id,String code,String friendId){
	// JSONObject ret = new JSONObject();
	// FarmInfo farm = this.po.farmMap.get(id);
	// farm.openState = OPEN_STATE.OPENED.value;
	// farm.seedState = SEED_STATE.CAN_SEED.value;
	// ret.put("result", 0);
	// return ret;
	//
	// }
	//
	// public JSONObject steal(int id,String code,String friendId){
	// JSONObject ret = new JSONObject();
	// FarmInfo farm = this.po.farmMap.get(id);
	// farm.openState = OPEN_STATE.OPENED.value;
	// farm.seedState = SEED_STATE.CAN_SEED.value;
	// ret.put("result", 0);
	// return ret;
	// }
	//
	// public JSONObject harvest(int id,String code){
	// JSONObject ret = new JSONObject();
	// FarmInfo farm = this.po.farmMap.get(id);
	// farm.openState = OPEN_STATE.OPENED.value;
	// farm.seedState = SEED_STATE.CAN_SEED.value;
	// ret.put("result", 0);
	// return ret;
	// }
}
