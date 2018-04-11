package com.wanniu.game.activity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;
import java.util.concurrent.ScheduledFuture;

import com.alibaba.fastjson.JSON;
import com.wanniu.core.db.GCache;
import com.wanniu.core.game.JobFactory;
import com.wanniu.core.logfs.Out;
import com.wanniu.game.GWorld;
import com.wanniu.game.common.Const;
import com.wanniu.game.common.Const.ForceType;
import com.wanniu.game.common.Const.GOODS_CHANGE_TYPE;
import com.wanniu.game.common.ConstsTR;
import com.wanniu.game.data.GameData;
import com.wanniu.game.data.ext.DropListExt;
import com.wanniu.game.item.ItemUtil;
import com.wanniu.game.item.NormalItem;
import com.wanniu.game.mail.MailUtil;
import com.wanniu.game.mail.SysMailConst;
import com.wanniu.game.mail.data.MailData.Attachment;
import com.wanniu.game.mail.data.MailSysData;
import com.wanniu.game.player.GlobalConfig;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.poes.DemonTowerCenterPO;
import com.wanniu.game.poes.DemonTowerPO;
import com.wanniu.game.poes.RedPacketCenterPO;
import com.wanniu.redis.PlayerPOManager;

public class DemonTowerService {

	static DemonTowerService demonTowerService;
	public DemonTowerCenterPO demonTowerCenterPO;
	
	private DemonTowerService() {
		
	}
	
	public static DemonTowerService getInstance() {
		if(demonTowerService==null) {
			demonTowerService=new DemonTowerService();
		}
		return demonTowerService;
	}
	
	public void init() {
		String data = GCache.hget(Integer.toString(GWorld.__SERVER_ID), ConstsTR.DemonTower.value);
		if(data==null) {
			demonTowerCenterPO=new DemonTowerCenterPO();
			GCache.hset(Integer.toString(GWorld.__SERVER_ID), ConstsTR.DemonTower.value, JSON.toJSONString(demonTowerCenterPO));
		}
		else {
			demonTowerCenterPO=JSON.parseObject(data, DemonTowerCenterPO.class);
		}
		
		
	}
	
	public boolean finishFloor(int floorId,String playerId,long time) {
		boolean refreshRechord=false;
		synchronized (demonTowerCenterPO) {
			if(demonTowerCenterPO.firstPlayerIds.get(floorId-1)==null) {
				demonTowerCenterPO.firstPlayerIds.set(floorId-1,playerId);
				demonTowerCenterPO.firstPlayerDates.set(floorId-1, new Date());
			}
			
//			Out.error(demonTowerCenterPO.fastPlayerTimes.get(floorId-1),",,",time);
			if(demonTowerCenterPO.fastPlayerTimes.get(floorId-1)==null||
					demonTowerCenterPO.fastPlayerTimes.get(floorId-1)>time) {
				demonTowerCenterPO.fastPlayerIds.set(floorId-1,playerId);
				demonTowerCenterPO.fastPlayerTimes.set(floorId-1, time);
				refreshRechord=true;
			}
			
			GCache.hset(Integer.toString(GWorld.__SERVER_ID), ConstsTR.DemonTower.value, JSON.toJSONString(demonTowerCenterPO));
		}
		return refreshRechord;
	}
	
	public void clearFirstData(int floorId) {
		synchronized (demonTowerCenterPO) {
			if(demonTowerCenterPO.firstPlayerIds.get(floorId-1)==null) {
				demonTowerCenterPO.firstPlayerIds.set(floorId-1,null);
				demonTowerCenterPO.firstPlayerDates.set(floorId-1, null);
				
				GCache.hset(Integer.toString(GWorld.__SERVER_ID), ConstsTR.DemonTower.value, JSON.toJSONString(demonTowerCenterPO));
			}
		}
	}
	
	public void clearFastData(int floorId) {
		synchronized (demonTowerCenterPO) {
			if(demonTowerCenterPO.fastPlayerTimes.get(floorId-1)==null) {
				demonTowerCenterPO.fastPlayerIds.set(floorId-1,null);
				demonTowerCenterPO.fastPlayerTimes.set(floorId-1, null);
				GCache.hset(Integer.toString(GWorld.__SERVER_ID), ConstsTR.DemonTower.value, JSON.toJSONString(demonTowerCenterPO));
			}
		}
	}
	
	public void clearAllData() {
		demonTowerCenterPO.firstPlayerIds=new LinkedList<>();
		demonTowerCenterPO.firstPlayerDates=new LinkedList<>();
		demonTowerCenterPO.fastPlayerIds=new LinkedList<>();
		demonTowerCenterPO.fastPlayerTimes=new LinkedList<>();
		for (DropListExt dropListExt : GameData.DropLists.values()) {
			demonTowerCenterPO.firstPlayerIds.add(null);
			demonTowerCenterPO.firstPlayerDates.add(null);
			demonTowerCenterPO.fastPlayerIds.add(null);
			demonTowerCenterPO.fastPlayerTimes.add(null);
//			leastPlayerRewardGot.add(false);
		}
		GCache.hset(Integer.toString(GWorld.__SERVER_ID), ConstsTR.DemonTower.value, JSON.toJSONString(demonTowerCenterPO));
	}
	
//	public boolean getGotable(int floorId,String playerId) {
//		synchronized (demonTowerCenterPO) {
//			String fastPlayerId=demonTowerCenterPO.fastPlayerIds.get(floorId-1);
//			if(fastPlayerId!=null) {
//				if(playerId.equals(fastPlayerId)) {
//					return demonTowerCenterPO.leastPlayerRewardGot.get(floorId-1);
//				}
//				
//			}
//		}
//		return false;
//	}
	
//	public void fetchFastReward(int floorId,WNPlayer player) {
//		synchronized (demonTowerCenterPO) {
//			String fastPlayerId=demonTowerCenterPO.fastPlayerIds.get(floorId-1);
//			if(fastPlayerId!=null) {
//				if(player.getId().equals(fastPlayerId)) {
//					if(demonTowerCenterPO.leastPlayerRewardGot.get(floorId-1)==false) {
//						
//						demonTowerCenterPO.leastPlayerRewardGot.set(floorId-1, true);
//						DropListExt dropListExt=GameData.DropLists.get(floorId);
//						List<NormalItem> items = ItemUtil.createItemsByItemCode(dropListExt.weekRewardPreview);
//						player.bag.addCodeItemMail(items, ForceType.BIND, GOODS_CHANGE_TYPE.DemonTower, SysMailConst.BAG_FULL_COMMON);
//					}
//				}
//				
//			}
//			
//		}
//	}
	
	public void dispatchWeekReward(WNPlayer player) {
		List<NormalItem> items=new LinkedList<>();
		
		synchronized (demonTowerCenterPO) {
			for (int i=0;i<demonTowerCenterPO.fastPlayerIds.size();i++) {
				String pid = demonTowerCenterPO.fastPlayerIds.get(i);
				if(pid!=null) {
					if(pid.equals(player.getId())) {
						DropListExt dropListExt=GameData.DropLists.get(i+1);
						List<NormalItem> item=ItemUtil.createItemsByItemCode(dropListExt.weekRewardPreview);
						items.addAll(item);
					}
				}
			}
			
		}
		
		MailSysData mailData = new MailSysData(SysMailConst.DemonTowerFastRecordReward);
		List<Attachment> list_attach = new ArrayList<>();
		for (NormalItem item : items) {
			// 虚拟道具不用走邮件
			if (item.isVirtual()) {
				player.bag.addEntityItem(item,GOODS_CHANGE_TYPE.DemonTower);
			} else {
				Attachment attachment = new Attachment();
				attachment.itemCode = item.itemCode();
				attachment.itemNum = item.getNum();
				list_attach.add(attachment);
			}
		}
		if (list_attach.size() > 0) {
			mailData.attachments = list_attach;
			MailUtil.getInstance().sendMailToOnePlayer(player.getId(), mailData, GOODS_CHANGE_TYPE.DemonTower);
		}
	}
}