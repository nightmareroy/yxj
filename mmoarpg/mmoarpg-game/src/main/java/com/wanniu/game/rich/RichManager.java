package com.wanniu.game.rich;

import java.security.Timestamp;
import java.text.DateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.logfs.Out;
import com.wanniu.core.util.DateUtil;
import com.wanniu.core.util.RandomUtil;
import com.wanniu.game.common.Const.ForceType;
import com.wanniu.game.common.Const.GOODS_CHANGE_TYPE;
import com.wanniu.game.common.Const.ManagerType;
import com.wanniu.game.common.Const.PlayerEventType;
import com.wanniu.game.data.FunctionsCO;
import com.wanniu.game.data.GameData;
import com.wanniu.game.data.TurnRewardCO;
import com.wanniu.game.data.ZillionaireCageCO;
import com.wanniu.game.data.ZillionaireFreeCO;
import com.wanniu.game.data.ext.ScheduleExt;
import com.wanniu.game.data.ext.TurnRewardExt;
import com.wanniu.game.item.ItemUtil;
import com.wanniu.game.item.NormalItem;
import com.wanniu.game.mail.SysMailConst;
import com.wanniu.game.GWorld;
import com.wanniu.game.common.Const;
import com.wanniu.game.common.ConstsTR;
import com.wanniu.game.common.DateUtils;
import com.wanniu.game.common.ModuleManager;
import com.wanniu.game.common.msg.ErrorResponse;
import com.wanniu.game.daily.DailyActivityMgr;
import com.wanniu.game.player.GlobalConfig;
import com.wanniu.game.player.PlayerUtil;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.poes.RichPO;
import com.wanniu.game.poes.DailyActivityPO.DailyInfo;
import com.wanniu.redis.PlayerPOManager;

import groovy.time.Duration;
import pomelo.area.PlayerHandler.SuperScriptType;
import pomelo.rich.RichHandler.DiceResponse;
import pomelo.rich.RichHandler.FetchTurnAwardResponse;
import pomelo.rich.RichHandler.GetRichInfoResponse;
import pomelo.rich.RichHandler.Reward;
import pomelo.rich.RichHandler.TaskInfo;
import pomelo.rich.RichHandler.TurnReward;

public class RichManager extends ModuleManager {

	WNPlayer player;
	RichPO richPO;
	DailyActivityMgr dailyActivityMgr;

	public RichManager(WNPlayer player) {
		this.player = player;
	}

	public void init() {
		richPO = PlayerPOManager.findPO(ConstsTR.Rich, player.getId(), RichPO.class);
		if (richPO == null) {
			richPO = new RichPO();
			PlayerPOManager.put(ConstsTR.Rich, player.getId(), richPO);
		}

		dailyActivityMgr = player.dailyActivityMgr;
	}
	
	LocalDateTime getStartLocalDateTime() {
		Instant startInstant = null;
		try {
			startInstant = (DateUtils.parse(GlobalConfig.Zillionaire_Open_Time, DateUtil.F_yyyyMMddHHmmss)).toInstant();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		LocalDateTime startLocalDateTime = LocalDateTime.ofInstant(startInstant, ZoneId.systemDefault());
		
		if(GWorld.OPEN_SERVER_DATE.atTime(0, 0).isAfter(startLocalDateTime)){
			startLocalDateTime=GWorld.OPEN_SERVER_DATE.atTime(0, 0);
		}
		
		return startLocalDateTime;
		
	}
	
	LocalDateTime getEndLocalDateTime() {
		LocalDateTime startDateTime=getStartLocalDateTime();
		int durationHour=GlobalConfig.Zillionaire_Continued_Time;
		return startDateTime.plus(durationHour,ChronoUnit.HOURS);
	}

	public boolean isActive() {

		Instant startInstant = null;
		try {
			startInstant = (DateUtils.parse(GlobalConfig.Zillionaire_Open_Time, DateUtil.F_yyyyMMddHHmmss)).toInstant();
		} catch (Exception e) {
			e.printStackTrace();
		}
		LocalDateTime startDateTime = LocalDateTime.ofInstant(startInstant, ZoneId.systemDefault());

		if(GWorld.OPEN_SERVER_DATE.atTime(0, 0).isAfter(startDateTime)){
			startDateTime=GWorld.OPEN_SERVER_DATE.atTime(0, 0);
		}

		int durationHour=GlobalConfig.Zillionaire_Continued_Time;
		LocalDateTime endDateTime = startDateTime.plus(durationHour,ChronoUnit.HOURS);

		if (LocalDateTime.now().isBefore(startDateTime)) {
			return false;
		}
		if (LocalDateTime.now().isAfter(endDateTime)) {
			return false;
		}
		return true;
	}
	


	public void checkData() {
		if(richPO.currentTurn!=GlobalConfig.Zillionaire_CurrentTurn) {
			richPO.reset(GlobalConfig.Zillionaire_CurrentTurn);
		}

	}

	public GetRichInfoResponse.Builder getRichInfo(String playerId) {
		GetRichInfoResponse.Builder builder = GetRichInfoResponse.newBuilder();

		checkData();

		if (!isActive()) {
			builder.setS2CCode(PomeloRequest.FAIL);
			builder.setS2CMsg(LangService.getValue("RICH_INACTIVED"));
			return builder;
		}

		for (ZillionaireFreeCO zillionaireFreeCO : GameData.ZillionaireFrees.values()) {
			TaskInfo.Builder taskInfoBuilder = TaskInfo.newBuilder();
			
			DailyInfo info = player.dailyActivityMgr.getTaskInfo(zillionaireFreeCO.taskID);
			ScheduleExt scheduleExt=GameData.Schedules.get(zillionaireFreeCO.taskID);
			taskInfoBuilder.setSchName(scheduleExt.schName);
			taskInfoBuilder.setFinishedCount(info.process);
			taskInfoBuilder.setMaxCount(scheduleExt.maxCount);

			builder.addTaskInfo(taskInfoBuilder.build());
		}
		for (TurnRewardExt turnRewardExt : GameData.TurnRewards.values()) {
			TurnReward.Builder turnRewardBuilder = TurnReward.newBuilder();
			turnRewardBuilder.setTurnId(turnRewardExt.sort);

			for (Map.Entry<String, Integer> entry : turnRewardExt.getRewardMap.entrySet()) {
				Reward.Builder rewardBuilder = Reward.newBuilder();
				rewardBuilder.setCode(entry.getKey());
				rewardBuilder.setGroupCount(entry.getValue());
				
				turnRewardBuilder.addReward(rewardBuilder);
			}
			
			turnRewardBuilder.setState(richPO.turnStatesMap.get(turnRewardExt.sort));
//			turnRewardBuilder.setReward(rewardBuilder);
			builder.addTurnReward(turnRewardBuilder);
		}

		builder.setCurrentStep(richPO.currentStep);
		builder.setFreeCount(richPO.freeCount);

//		Calendar startDate = Calendar.getInstance();
//		Calendar endDate = Calendar.getInstance();
//		try {
//			startDate.setTime(DateUtils.parse(GlobalConfig.Zillionaire_Open_Time, DateUtil.F_yyyyMMddHHmmss));
//			endDate.setTime(DateUtils.parse(GlobalConfig.Zillionaire_Close_Time, DateUtil.F_yyyyMMddHHmmss));
//
//		} catch (Exception e) {
//			e.printStackTrace();
//			return builder;
//		}

		
		builder.setStartTimestamp(getStartLocalDateTime().format(com.wanniu.core.util.DateUtils.F_YYYYMMDDHHMMSS));
		builder.setEndTimestamp(getEndLocalDateTime().format(com.wanniu.core.util.DateUtils.F_YYYYMMDDHHMMSS));
		builder.setS2CCode(PomeloRequest.OK);

		return builder;
	}
	
	private int getRound() {
		int finishedRound = 0;
		for (int state : richPO.turnStatesMap.values()) {
			if (state != 0) {
				finishedRound++;
			}
		}

		return Math.min(GameData.TurnRewards.size(), finishedRound+1);
	}

	public DiceResponse.Builder dice(String playerId) {
		DiceResponse.Builder builder = DiceResponse.newBuilder();

		checkData();

		if (!isActive()) {
			builder.setS2CCode(PomeloRequest.FAIL);
			builder.setS2CMsg(LangService.getValue("RICH_INACTIVED"));
			return builder;
		}

		if (richPO.freeCount > 0) {
			richPO.freeCount--;
			updateSuperScript();
		} else if (!player.moneyManager.enoughDiamond(GlobalConfig.Zillionaire_Yuanbao_One)) {
			builder.setS2CCode(PomeloRequest.FAIL);
			builder.setS2CMsg(LangService.getValue("RICH_NOT_ENOUGH_DIAMOND"));
			Out.error(builder);
			return builder;
		} else {
			player.moneyManager.costDiamond(GlobalConfig.Zillionaire_Yuanbao_One, GOODS_CHANGE_TYPE.Rich);
		}

		int stepCount = GameData.ZillionaireCages.size();
		

		int randomStep = RandomUtil.getInt(1, 6);

		int newStep = (richPO.currentStep - 1 + randomStep) % stepCount + 1;
		
		if((richPO.currentStep  + randomStep) > stepCount) {
			int currentRound=getRound();
			
			if (richPO.turnStatesMap.get(currentRound) == 0) {
				richPO.turnStatesMap.put(currentRound, 1);
				updateSuperScript();
			}
			
		}
	
		richPO.currentStep = newStep;

		builder.setStep(randomStep);

		Reward.Builder rewardBuilder = Reward.newBuilder();
		ZillionaireCageCO zillionaireCageCO = GameData.ZillionaireCages.get(richPO.currentStep);
		rewardBuilder.setCode(zillionaireCageCO.itemCode);
		rewardBuilder.setGroupCount(zillionaireCageCO.nUM);
		
		player.bag.addCodeItemMail(zillionaireCageCO.itemCode, zillionaireCageCO.nUM, 
				zillionaireCageCO.isBind==0?ForceType.UN_BIND:ForceType.BIND, GOODS_CHANGE_TYPE.Rich, SysMailConst.BAG_FULL_COMMON);

		builder.setS2CCode(PomeloRequest.OK);
		builder.setReward(rewardBuilder);

		return builder;
	}

	public FetchTurnAwardResponse.Builder fetchTurnAward(String playerId, int turnId) {
		FetchTurnAwardResponse.Builder builder = FetchTurnAwardResponse.newBuilder();

		checkData();

		if (!isActive()) {
			builder.setS2CCode(PomeloRequest.FAIL);
			builder.setS2CMsg(LangService.getValue("RICH_INACTIVED"));
			return builder;
		}

		if (!richPO.turnStatesMap.containsKey(turnId)) {
			builder.setS2CCode(PomeloRequest.FAIL);
			builder.setS2CMsg(LangService.getValue("RICH_TURN_ID_ERROR"));
			return builder;
		}

		int state = richPO.turnStatesMap.get(turnId);
		if (state == 0) {
			builder.setS2CCode(PomeloRequest.FAIL);
			builder.setS2CMsg(LangService.getValue("RICH_TURN_NOT_FINISHED"));
			return builder;
		}
		if (state == 2) {
			builder.setS2CCode(PomeloRequest.FAIL);
			builder.setS2CMsg(LangService.getValue("RICH_TURN_FETCHED"));
			return builder;
		}

		TurnRewardExt turnRewardExt = GameData.TurnRewards.get(turnId);

		richPO.turnStatesMap.put(turnId, 2);
		List<NormalItem> rewards=ItemUtil.createItemsByItemCode(turnRewardExt.getRewardMap);
		player.bag.addCodeItemMail(rewards,turnRewardExt.isBind == 0 ? ForceType.UN_BIND : ForceType.BIND, 
				GOODS_CHANGE_TYPE.Rich,SysMailConst.BAG_FULL_COMMON);

		for (Map.Entry<String, Integer> entry : turnRewardExt.getRewardMap.entrySet()) {
			Reward.Builder rewardBuilder = Reward.newBuilder();
			rewardBuilder.setCode(entry.getKey());
			rewardBuilder.setGroupCount(entry.getValue());
			
			builder.addReward(rewardBuilder);
		}

		builder.setS2CCode(PomeloRequest.OK);
		
		updateSuperScript();
		
		return builder;
	}

	public void AddFreeCount(int taskId) {

		if (!isActive()) {
			return;
		}

		for (ZillionaireFreeCO zillionaireFreeCO : GameData.ZillionaireFrees.values()) {
			if (zillionaireFreeCO.taskID == taskId) {
				if (richPO.freeCount < GlobalConfig.Zillionaire_FreeUp) {
					richPO.freeCount++;
					
					updateSuperScript();
				}
				return;
			}
		}

	}
	
	private int getFechableCount() {
		int count=0;
		for (int value : richPO.turnStatesMap.values()) {
			if(value==1) {
				count++;
			}
		}
		return count;
	}
	
	public void updateSuperScript() {
		List<SuperScriptType> data = this.getSuperScript();
		player.updateSuperScriptList(data);
	}
	
	@Override
	public List<SuperScriptType> getSuperScript() {
		List<SuperScriptType> ret = new ArrayList<>();
		SuperScriptType.Builder t = SuperScriptType.newBuilder();
		t.setType(Const.SUPERSCRIPT_TYPE.RICH.getValue());
		int count=0;
		if(isActive()) {
			count=1;
			if(richPO.freeCount>0) {
				count=2;
			}
			if(getFechableCount()>0) {
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
		return ManagerType.RICH;
	}
}
