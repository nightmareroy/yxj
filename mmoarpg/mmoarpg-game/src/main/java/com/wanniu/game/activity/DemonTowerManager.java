package com.wanniu.game.activity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;
import java.util.concurrent.ScheduledFuture;

import com.wanniu.core.game.JobFactory;
import com.wanniu.core.logfs.Out;
import com.wanniu.game.common.Const;
import com.wanniu.game.common.Const.GOODS_CHANGE_TYPE;
import com.wanniu.game.common.ConstsTR;
import com.wanniu.game.data.GameData;
import com.wanniu.game.data.ext.DropListExt;
import com.wanniu.game.item.NormalItem;
import com.wanniu.game.mail.MailUtil;
import com.wanniu.game.mail.SysMailConst;
import com.wanniu.game.mail.data.MailData.Attachment;
import com.wanniu.game.mail.data.MailSysData;
import com.wanniu.game.player.GlobalConfig;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.poes.DemonTowerPO;
import com.wanniu.game.poes.PlayerPO;
import com.wanniu.redis.PlayerPOManager;

import pomelo.Common.DemonTowerFloorInfo;

public class DemonTowerManager {

	private static Map<String, ScheduledFuture<?>> scheduleMap = new HashMap<>();

	public DemonTowerPO po;

	private WNPlayer player;

	public DemonTowerManager(WNPlayer player) {
		this.po = PlayerPOManager.findPO(ConstsTR.PLAYER_DEMON_TOWER, player.getId(), DemonTowerPO.class);
		if (this.po == null) {
			this.po = new DemonTowerPO(player.getId(), 1, 0, GlobalConfig.ResetNum);
			PlayerPOManager.put(ConstsTR.PLAYER_DEMON_TOWER, player.getId(), this.po);
		}
		this.player = player;

		
	}

	public void init() {
		if (po.sweepEndTime != null) {
			Date now = new Date();
			long timeOffset = po.sweepEndTime.getTime() - now.getTime();
			if (timeOffset <= 0) {
				GetSweepRewards();
			} else if (!DemonTowerManager.scheduleMap.containsKey(player.getId())) {
				ScheduledFuture<?> scheduledFuture = JobFactory.addDelayJob(timerTask, timeOffset);
				DemonTowerManager.scheduleMap.put(player.getId(), scheduledFuture);
			}

		}
	}

	public int GetSecondToEndTime() {
		if (po.sweepEndTime != null) {
			Date now = new Date();
			return (int) ((po.sweepEndTime.getTime() - now.getTime()) / 1000);
		} else {
			return 0;
		}
	}

	// 开始扫荡
	public void StartToSweep(int sweepTime) {
		if (po.sweepCountLeft <= 0) {
			Out.error("没有扫荡次数！");
			return;
		}
		po.sweepCountLeft--;
		Date now = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(now);
		calendar.add(Calendar.SECOND, sweepTime);
		po.sweepEndTime = calendar.getTime();

		ScheduledFuture<?> scheduledFuture = JobFactory.addDelayJob(timerTask, sweepTime * 1000);
		DemonTowerManager.scheduleMap.put(player.getId(), scheduledFuture);

		player.dailyActivityMgr.onEvent(Const.DailyType.DEMON_TOWER, "0", 1);

	}

	TimerTask timerTask = new TimerTask() {

		@Override
		public void run() {
			String callbackPlayerId = player.getId();
			DemonTowerPO tempPo = PlayerPOManager.findPO(ConstsTR.PLAYER_DEMON_TOWER, player.getId(), DemonTowerPO.class);
			tempPo.sweepEndTime = null;

			MailSysData mailData = new MailSysData(SysMailConst.DemonTower_sweep);

			mailData.attachments = new ArrayList<>();
			HashMap<String, Integer> rewardMap = ComputeSweepRewards();
			for (String code : rewardMap.keySet()) {
				Attachment item = new Attachment();
				item.itemCode = code;
				item.itemNum = rewardMap.get(code);
				mailData.attachments.add(item);
			}
			MailUtil.getInstance().sendMailToOnePlayer(callbackPlayerId, mailData, Const.GOODS_CHANGE_TYPE.DemonTower);

			DemonTowerManager.scheduleMap.remove(player.getId());
		}
	};

	// 正在扫荡时，立刻完成扫荡
	public void FinishSweepWhenSpeeping() {
		if (po.sweepEndTime == null) {
			Out.debug("不在扫荡中");
		}
		GetSweepRewards();
		DemonTowerManager.scheduleMap.get(player.getId()).cancel(false);
		DemonTowerManager.scheduleMap.remove(player.getId());

	}

	// 不在扫荡时，立刻完成扫荡
	public void FinishSweep() {
		if (po.sweepEndTime != null) {
			Out.debug("在扫荡中");
		}
		GetSweepRewards();

		player.dailyActivityMgr.onEvent(Const.DailyType.DEMON_TOWER, "0", 1);
	}

	// 计算扫荡奖励
	public HashMap<String, Integer> ComputeSweepRewards() {
		HashMap<String, Integer> rewardMap = new HashMap<String, Integer>();

		if (po.maxFloor == 1)
			return rewardMap;

		for (int i = 1; i < po.maxFloor; i++) {
			DropListExt dropListExt = GameData.DropLists.get(i);

			for (Map.Entry<String, Integer> entry : dropListExt.rewardPreview.entrySet()) {
				if (!rewardMap.containsKey(entry.getKey()))
					rewardMap.put(entry.getKey(), 0);
				rewardMap.put(entry.getKey(), rewardMap.get(entry.getKey())+entry.getValue());
			}


		}
		return rewardMap;
	}

	// 扫荡完成获取扫荡奖励
	private void GetSweepRewards() {

		HashMap<String, Integer> rewardMap = ComputeSweepRewards();

		for (String code : rewardMap.keySet()) {
			player.bag.addCodeItem(code, rewardMap.get(code), Const.ForceType.DEFAULT, GOODS_CHANGE_TYPE.DemonTower);

		}

		po.sweepEndTime = null;
	}

	// 重置扫荡次数
	public void UpdateSweepCount() {
		po.sweepCountLeft = 1;
	}
	
	public void refreshNewDay() {
		Calendar calendar=Calendar.getInstance();
		calendar.setTime(new Date());
		if(calendar.get(Calendar.DAY_OF_WEEK)==Calendar.MONDAY) {
			DemonTowerService.getInstance().dispatchWeekReward(player);
			
			
		}
	}
	
	public DemonTowerFloorInfo.Builder getFloorInfoBuilder(int floorId){
		DemonTowerFloorInfo.Builder floorInfoBuilder=DemonTowerFloorInfo.newBuilder();
		floorInfoBuilder.setFloorId(floorId);
		
		String firstPlayerId=null;
		Date firstPlayerDate=null;
		String fastPlayerId=null;
		Long fastPlayerTime=null;
		synchronized (DemonTowerService.getInstance().demonTowerCenterPO) {
			firstPlayerId=DemonTowerService.getInstance().demonTowerCenterPO.firstPlayerIds.get(floorId-1);
			firstPlayerDate=DemonTowerService.getInstance().demonTowerCenterPO.firstPlayerDates.get(floorId-1);
			fastPlayerId=DemonTowerService.getInstance().demonTowerCenterPO.fastPlayerIds.get(floorId-1);
			fastPlayerTime=DemonTowerService.getInstance().demonTowerCenterPO.fastPlayerTimes.get(floorId-1);
		}
		
		
		if(firstPlayerId!=null) {
			PlayerPO playerPO=PlayerPOManager.findPO(ConstsTR.playerTR, firstPlayerId, PlayerPO.class);
			if(playerPO!=null) {
				floorInfoBuilder.setFirstPlayerName(playerPO.name);
				floorInfoBuilder.setFirstPlayerDate(firstPlayerDate.getTime());
			}
			else {
				//该玩家已删号
				DemonTowerService.getInstance().clearFirstData(floorId);
			}
			
		}
		
		
		if(fastPlayerId!=null) {
			PlayerPO playerPO=PlayerPOManager.findPO(ConstsTR.playerTR, fastPlayerId, PlayerPO.class);
			if(playerPO!=null) {
				floorInfoBuilder.setFastPlayerName(playerPO.name);
				floorInfoBuilder.setFastPlayerTime(fastPlayerTime.intValue()/1000);
			}
			else {
				//该玩家已删号
				DemonTowerService.getInstance().clearFastData(floorId);
			}
			
		}
		if(po.leastTimeList.size()>=floorId) {
			floorInfoBuilder.setMyFastTime(po.leastTimeList.get(floorId-1).intValue()/1000);
		}
		
		return floorInfoBuilder;
	}

	/**
	 * 获取当前通关层数.
	 */
	public int getMaxFloor() {
		return po.maxFloor;
	}

	/**
	 * @return 获取当前可以扫荡的次数
	 */
	public int getSweepCountLeft() {
		return po.sweepCountLeft;
	}
}