package com.wanniu.game.sevengoal;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.logfs.Out;
import com.wanniu.core.util.DateUtil;
import com.wanniu.game.GWorld;
import com.wanniu.game.common.Const;
import com.wanniu.game.common.Const.ManagerType;
import com.wanniu.game.common.Const.PlayerEventType;
import com.wanniu.game.common.ConstsTR;
import com.wanniu.game.common.DateUtils;
import com.wanniu.game.common.ModuleManager;
import com.wanniu.game.data.GameData;
import com.wanniu.game.data.SevDayTaskCO;
import com.wanniu.game.data.SevTaskInsCO;
import com.wanniu.game.data.ext.SevTaskRewardExt;
import com.wanniu.game.item.ItemUtil;
import com.wanniu.game.item.NormalItem;
import com.wanniu.game.mail.SysMailConst;
import com.wanniu.game.player.GlobalConfig;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.poes.SevenGoalPO;
import com.wanniu.redis.PlayerPOManager;

import pomelo.area.PlayerHandler.SuperScriptType;
import pomelo.sevengoal.SevenGoalHandler.FetchAwardResponse;
import pomelo.sevengoal.SevenGoalHandler.GetSevenGoalResponse;
import pomelo.sevengoal.SevenGoalHandler.TaskInfo;

public class SevenGoalManager extends ModuleManager  {

	public WNPlayer player;
	
	public SevenGoalPO sevenGoalPO;
	
	public static enum SevenGoalTaskType{
		MOUNT_UPGRADE_LV(1),//坐骑升阶
		SOLO_ANTICIPATE(2),//问道大会参与次数
		ADD_FRIEND(3),//添加好友
		PAY_COUNT(4),//充值次数
		PET_UPGRADE_UPLV(5),//宠物突破
		EQUIP_STRENTHEN_COUNT(6),//全身强化次数
		FIVE_MOUNTAIN_ANTICIPATE(7),//五岳一战参与次数
		COST_DIAMOND_COUNT(8),//消费任意量元宝一定次数
		FIGHTPOWER_TO(9),//战力达到
		GEM_COMBINE_COUNT(10),//宝石合成次数
		TRIAL_ANTICIPATE(11),//试炼大赛参与次数
		COST_DIAMOND_OR_BINDDIAMOND_COUNT(12),//消费一定量的元宝或绑元一定次数
		EQUIP_REFINE_COUNT(13),//开光次数
		EQUIP_REBORN_COUNT(14),//洗练次数
		EQUIP_REBUILD_COUNT(15),//重铸或高级重铸次数
		DEMON_TOWER_COUNT(16),//镇妖塔挑战层数
		AREA_BOSS_KILL_COUNT(17),//击杀野外boss
		LEVEL_TO(18),//等级达到
		XIANYUAN_TO(19),//仙缘值达到
		GUILD_BOSS_COUNT(20),//公会boss参与次数
		ILLUSION2_COUNT(21);//秘境夺宝参与次数
		
	
		final int value;
		SevenGoalTaskType(int value) {
			this.value=value;
		}
		
		public static SevenGoalTaskType getType(int value) {
			for (SevenGoalTaskType sevenGoalTaskType : values()) {
				if(sevenGoalTaskType.value==value) {
					return sevenGoalTaskType;
				}
			}
			return null;
		}
	}
	
	public SevenGoalManager(WNPlayer player) {
		this.player=player;
		
		sevenGoalPO=PlayerPOManager.findPO(ConstsTR.SevenGoal, player.getId(), SevenGoalPO.class);
		if(sevenGoalPO==null) {
			sevenGoalPO=new SevenGoalPO();
			PlayerPOManager.put(ConstsTR.SevenGoal, player.getId(), sevenGoalPO);
		}
		
	}
	
	public LocalDateTime getStartDateTime() {
		Instant startInstant = null;
		try {
			startInstant = (DateUtils.parse(GlobalConfig.SevenGoal_Begin, DateUtil.F_yyyyMMddHHmmss)).toInstant();
		} catch (Exception e) {}
		LocalDateTime startDateTime = LocalDateTime.ofInstant(startInstant, ZoneId.systemDefault());
        startDateTime.toLocalDate().atTime(5, 0);
        if(GWorld.OPEN_SERVER_DATE.atTime(5, 0).isAfter(startDateTime)){
            startDateTime=GWorld.OPEN_SERVER_DATE.atTime(5, 0);
		}
		return startDateTime;
	}
	
	public LocalDateTime getEndDateTime() {
		return getStartDateTime().plus(GlobalConfig.SevenGoal_Continued, ChronoUnit.HOURS);
	}
	
	public boolean isActive() {
        Instant startInstant = null;
        try {
            startInstant = (DateUtils.parse(GlobalConfig.SevenGoal_Begin, DateUtil.F_yyyyMMddHHmmss)).toInstant();
        } catch (Exception e) {
            e.printStackTrace();
        }
        LocalDateTime startDateTime = LocalDateTime.ofInstant(startInstant, ZoneId.systemDefault());
        startDateTime.toLocalDate().atTime(5, 0);
        
        if(GWorld.OPEN_SERVER_DATE.atTime(5, 0).isAfter(startDateTime)){
            startDateTime=GWorld.OPEN_SERVER_DATE.atTime(5, 0);
        }

        int durationHour=GlobalConfig.SevenGoal_Continued;
        LocalDateTime endDateTime = startDateTime.plus(durationHour,ChronoUnit.HOURS);
        
        
        if (LocalDateTime.now().isBefore(startDateTime)) {
			return false;
		}
        if (LocalDateTime.now().isAfter(endDateTime)) {
			return false;
		}
		return true;
	}
	
	private int getDayId() {
        Instant startInstant = null;
        try {
            startInstant = (DateUtils.parse(GlobalConfig.SevenGoal_Begin, DateUtil.F_yyyyMMddHHmmss)).toInstant();
        } catch (Exception e) {
            e.printStackTrace();
        }
        LocalDate startDate = LocalDateTime.ofInstant(startInstant, ZoneId.systemDefault()).toLocalDate();

        
        if(GWorld.OPEN_SERVER_DATE.isAfter(startDate)){
            startDate=GWorld.OPEN_SERVER_DATE;
        }

        
        return (int)ChronoUnit.DAYS.between(startDate, LocalDate.now()) + 1;

	}
	


	public void checkData() {
		if(GlobalConfig.SevenGoal_CurrentTurn!=sevenGoalPO.currentTurn) {
			sevenGoalPO.reset(GlobalConfig.SevenGoal_CurrentTurn);
			initData();
		}
	}
	
	
	
	public GetSevenGoalResponse.Builder getSevenGoal(){
		checkData();
		
		GetSevenGoalResponse.Builder builder=GetSevenGoalResponse.newBuilder();
		
		
		
//		Date startDate;
//		Date endDate;
//		try {
//			startDate=DateUtils.parse(GlobalConfig.SevenGoal_Begin, DateUtil.F_yyyyMMddHHmmss);
//			endDate=DateUtils.parse(GlobalConfig.SevenGoal_End, DateUtil.F_yyyyMMddHHmmss);
//		} catch (Exception e) {
//			e.printStackTrace();
//			return builder;
//		}
		
		
		for (DayInfo dayInfo : sevenGoalPO.dayInfoMap.values()) {
			pomelo.sevengoal.SevenGoalHandler.DayInfo.Builder dayinfoBuilder=pomelo.sevengoal.SevenGoalHandler.DayInfo.newBuilder();
			dayinfoBuilder.setDayId(dayInfo.dayId);
			
			
			
			for (com.wanniu.game.sevengoal.TaskInfo taskInfo : dayInfo.taskMap.values()) {
				TaskInfo.Builder taskInfoBuilder=TaskInfo.newBuilder();
				
				taskInfoBuilder.setTaskId(taskInfo.taskId);
				taskInfoBuilder.setFinishedNum(taskInfo.finishedNum);
				
				
				dayinfoBuilder.addTaskInfo(taskInfoBuilder);
			}
			dayinfoBuilder.setFetched(dayInfo.fetched);
			builder.addDayInfo(dayinfoBuilder);
		}
		builder.setCurrentDayId(getDayId());
		builder.setStartTimestamp(getStartDateTime().format(com.wanniu.core.util.DateUtils.F_YYYYMMDDHHMMSS));
		builder.setEndTimestamp(getEndDateTime().format(com.wanniu.core.util.DateUtils.F_YYYYMMDDHHMMSS));
		builder.setS2CCode(PomeloRequest.OK);
		Out.error(builder);
		return builder;
	}
	
	public FetchAwardResponse.Builder fetchAward(int dayId){
		checkData();
		
		FetchAwardResponse.Builder builder = FetchAwardResponse.newBuilder();
		
		if(!isActive()) {
			builder.setS2CCode(PomeloRequest.FAIL);
			builder.setS2CMsg(LangService.getValue("SEVEN_GOAL_INACTIVED"));
			return builder;
		}
		
		if(!sevenGoalPO.dayInfoMap.containsKey(dayId)) {
			builder.setS2CCode(PomeloRequest.FAIL);
			builder.setS2CMsg(LangService.getValue("SEVEN_GOAL_PARAM_ERROR"));
			return builder;
		}
		
		if(dayId>getDayId()) {
			builder.setS2CCode(PomeloRequest.FAIL);
			builder.setS2CMsg(LangService.getValue("SEVEN_GOAL_PARAM_ERROR"));
			return builder;
		}
		
		DayInfo dayInfo = sevenGoalPO.dayInfoMap.get(dayId);
		
		
		if(dayInfo.fetched==true) {
			builder.setS2CCode(PomeloRequest.FAIL);
			builder.setS2CMsg(LangService.getValue("SEVEN_GOAL_FETCHED"));
			return builder;
		}
		
		boolean allFinished=true;
		for (com.wanniu.game.sevengoal.TaskInfo taskInfo : dayInfo.taskMap.values()) {
			SevDayTaskCO sevDayTaskCO = GameData.SevDayTasks.get(taskInfo.taskId);
			if(taskInfo.finishedNum<sevDayTaskCO.targetNum) {
				allFinished=false;
				break;
			}
		}
		if(!allFinished) {
			builder.setS2CCode(PomeloRequest.FAIL);
			builder.setS2CMsg(LangService.getValue("SEVEN_GOAL_NOT_FINISHED"));
			return builder;
		}
		dayInfo.fetched=true;
		Out.info("玩家：",player.getId(),"  领取了七日目标奖励，dayId:",dayId);
		
		{// 神坑....
			SevTaskRewardExt sevTaskRewardExt = GameData.SevTaskRewards.get(dayInfo.dayId);

			List<NormalItem> rewards = new ArrayList<>();
			String[] rewardStrs = sevTaskRewardExt.reward.split(";");
			for (String rewardSubStr : rewardStrs) {
				String[] rewardSubStrs = rewardSubStr.split(":");
				rewards.addAll(ItemUtil.createItemsByItemCode(rewardSubStrs[0], Integer.parseInt(rewardSubStrs[1])));
			}
			player.bag.addCodeItemMail(rewards, Const.ForceType.BIND, Const.GOODS_CHANGE_TYPE.SevenGoal, SysMailConst.BAG_FULL_COMMON);
		}
		
		builder.setS2CCode(PomeloRequest.OK);
		
		// 刷新红点
		updateSuperScript();
		
		return builder;
	}
	
	public void processGoal(SevenGoalTaskType taskType, Object...params) {
		if(!isActive()) {
			return;
		}
		checkData();
		int dayId=getDayId();
//		List<SevDayTaskCO> sevDayTaskCOs = GameData.findSevDayTasks(t->t.style==taskType.value);
		
		boolean done=false;

		for (DayInfo dayInfo : sevenGoalPO.dayInfoMap.values()) {
			for (com.wanniu.game.sevengoal.TaskInfo taskInfo : dayInfo.taskMap.values()) {
				SevDayTaskCO sevDayTaskCO = GameData.SevDayTasks.get(taskInfo.taskId);
				if(sevDayTaskCO.style!=taskType.value) {
					continue;
				}
				if(sevDayTaskCO.date>dayId&&sevDayTaskCO.advancedDown==0) {
					continue;
				}
				if(taskInfo.finishedNum>=sevDayTaskCO.targetNum) {
					continue;
				}
				
				
//				SevTaskInsCO sevTaskInsCO = GameData.SevTaskInss.get(sevDayTaskCO.style);
				switch (taskType) {
				case MOUNT_UPGRADE_LV://0坐骑阶数
					taskInfo.finishedNum=Math.min((int)params[0], sevDayTaskCO.targetNum) ;
					done=true;
					break;
				case SOLO_ANTICIPATE:
					taskInfo.finishedNum=Math.min(taskInfo.finishedNum+1, sevDayTaskCO.targetNum);
					done=true;
					break;
				case ADD_FRIEND:
					taskInfo.finishedNum=Math.min(taskInfo.finishedNum+1, sevDayTaskCO.targetNum);
					done=true;
					break;
				case PAY_COUNT:
					taskInfo.finishedNum=Math.min(taskInfo.finishedNum+1, sevDayTaskCO.targetNum);
					done=true;
					break;
				case PET_UPGRADE_UPLV://0所有宠物中，最大宠物阶数
					taskInfo.finishedNum=Math.min((int)params[0], sevDayTaskCO.targetNum) ;
					done=true;
					break;
				case EQUIP_STRENTHEN_COUNT://0全身装备总等级
					taskInfo.finishedNum=Math.min((int)params[0], sevDayTaskCO.targetNum) ;
					done=true;
					break;	
				case FIVE_MOUNTAIN_ANTICIPATE:
					taskInfo.finishedNum=Math.min(taskInfo.finishedNum+1, sevDayTaskCO.targetNum);
					done=true;
					break;
				case COST_DIAMOND_COUNT:
					taskInfo.finishedNum=Math.min(taskInfo.finishedNum+1, sevDayTaskCO.targetNum);
					done=true;
					break;
				case FIGHTPOWER_TO://0当前战力
					taskInfo.finishedNum=Math.min((int)params[0], sevDayTaskCO.targetNum) ;
					done=true;
					break;	

				case GEM_COMBINE_COUNT://0宝石等级
					if((int)params[0]==sevDayTaskCO.numParameter1) {
						taskInfo.finishedNum=Math.min(taskInfo.finishedNum+(int)params[1], sevDayTaskCO.targetNum);
						done=true;
					}
					break;
				case TRIAL_ANTICIPATE:
					taskInfo.finishedNum=Math.min(taskInfo.finishedNum+1, sevDayTaskCO.targetNum);
					done=true;
					break;
				case COST_DIAMOND_OR_BINDDIAMOND_COUNT://0消费数额
					taskInfo.finishedNum=Math.min((int)params[0], sevDayTaskCO.targetNum);
					done=true;
					break;
				case EQUIP_REFINE_COUNT:
					taskInfo.finishedNum=Math.min(taskInfo.finishedNum+1, sevDayTaskCO.targetNum);
					done=true;
					break;
				case EQUIP_REBORN_COUNT:
					taskInfo.finishedNum=Math.min(taskInfo.finishedNum+1, sevDayTaskCO.targetNum);
					done=true;
					break;
				case EQUIP_REBUILD_COUNT:
					taskInfo.finishedNum=Math.min(taskInfo.finishedNum+1, sevDayTaskCO.targetNum);
					done=true;
					break;
				case DEMON_TOWER_COUNT:
					taskInfo.finishedNum=Math.min(player.demonTowerManager.getMaxFloor()-1, sevDayTaskCO.targetNum);
					done=true;
					break;
				case AREA_BOSS_KILL_COUNT:
					taskInfo.finishedNum=Math.min(taskInfo.finishedNum+1, sevDayTaskCO.targetNum);
					done=true;
					break;
				case LEVEL_TO://0当前等级
					taskInfo.finishedNum=Math.min((int)params[0], sevDayTaskCO.targetNum);
					done=true;
					break;
				case XIANYUAN_TO://0当前仙缘值
					taskInfo.finishedNum=Math.min(taskInfo.finishedNum+(int)params[0], sevDayTaskCO.targetNum);
					done=true;
					break;
				case GUILD_BOSS_COUNT:
					taskInfo.finishedNum=Math.min(taskInfo.finishedNum+1, sevDayTaskCO.targetNum);
					done=true;
					break;
				case ILLUSION2_COUNT:
					taskInfo.finishedNum=Math.min(taskInfo.finishedNum+1, sevDayTaskCO.targetNum);
					done=true;
					break;
				default:
					break;
				}
			}
		}
		if(done) {
			updateSuperScript();
		}
	}
	
	/**
	 * 初始化任务数据。
	 * 重要：所有状态类的任务（Tip==0），都应该是可以提前完成的任务（AdvancedDown==1），策划配表的时候要注意。
	 * 在初始化数据的时候，对于状态类任务，要将玩家当前人物相关的数据写入到任务数据里。
	 * @param taskType
	 * @param params
	 */
	public void initData() {
		if(!isActive()) {
			return;
		}

		for (DayInfo dayInfo : sevenGoalPO.dayInfoMap.values()) {
			for (com.wanniu.game.sevengoal.TaskInfo taskInfo : dayInfo.taskMap.values()) {
				SevDayTaskCO sevDayTaskCO = GameData.SevDayTasks.get(taskInfo.taskId);
				SevTaskInsCO sevTaskInsCO = GameData.SevTaskInss.get(sevDayTaskCO.style);
				if(sevTaskInsCO.tip!=0) {
					continue;
				}

				SevenGoalTaskType sevenGoalTaskType=SevenGoalTaskType.getType(sevDayTaskCO.style);
				switch (sevenGoalTaskType) {
				case MOUNT_UPGRADE_LV:
					taskInfo.finishedNum=Math.min(player.mountManager.getMountLevel(), sevDayTaskCO.targetNum) ;
					break;
				case PET_UPGRADE_UPLV:
					int maxPetUpLv=player.petNewManager.getMaxPetUpLv();
					taskInfo.finishedNum=Math.min(maxPetUpLv, sevDayTaskCO.targetNum) ;
					break;
				case EQUIP_STRENTHEN_COUNT:
					taskInfo.finishedNum=Math.min(player.equipManager.getTotalStrenthenLv(), sevDayTaskCO.targetNum) ;
					break;	
				case FIGHTPOWER_TO://0当前战力
					taskInfo.finishedNum=Math.min(player.getFightPower(), sevDayTaskCO.targetNum) ;
					break;	
				case DEMON_TOWER_COUNT://0当前层数
					taskInfo.finishedNum=Math.min(player.demonTowerManager.getMaxFloor()-1, sevDayTaskCO.targetNum);
					break;
				case LEVEL_TO://0当前等级
					taskInfo.finishedNum=Math.min(player.getLevel(), sevDayTaskCO.targetNum);
					break;
				default:
					break;
				}
			}
		}
		updateSuperScript();
	}
	
	
	
	public void updateSuperScript() {
		List<SuperScriptType> data = this.getSuperScript();
		player.updateSuperScriptList(data);
	}
	
	@Override
	public List<SuperScriptType> getSuperScript() {
		List<SuperScriptType> ret = new ArrayList<>();
		
		int day = getDayId();
		
		int totalCount=0;
		for (DayInfo dayInfo : sevenGoalPO.dayInfoMap.values()) {
			if (dayInfo.dayId > day || dayInfo.fetched) {
				continue;
			}
			boolean allFinished=true;
			for (com.wanniu.game.sevengoal.TaskInfo taskInfo : dayInfo.taskMap.values()) {
				SevDayTaskCO sevDayTaskCO = GameData.SevDayTasks.get(taskInfo.taskId);
				if(taskInfo.finishedNum<sevDayTaskCO.targetNum) {
					allFinished=false;
					break;
				}
			}
			if(allFinished) {
				totalCount++;
			}
		}
		SuperScriptType.Builder t = SuperScriptType.newBuilder();
		t.setType(Const.SUPERSCRIPT_TYPE.SEVEN_GOAL.getValue());
		int count=0;
		if(isActive()) {
			count=1;
			if(totalCount>0) {
				count=2;
			}
		}
		t.setNumber(count);
		ret.add(t.build());
		
		return ret;
	}
	
	@Override
	public void onPlayerEvent(PlayerEventType eventType) {
		// TODO Auto-generated method stub
	}

	@Override
	public ManagerType getManagerType() {
		// TODO Auto-generated method stub
		return ManagerType.SEVEN_GOAL;
	}

}
