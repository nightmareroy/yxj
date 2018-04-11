package com.wanniu.game.redpacket;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.wanniu.core.GGame;
import com.wanniu.core.db.GCache;
import com.wanniu.core.game.JobFactory;
import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GPlayer;
import com.wanniu.core.game.protocol.PomeloPush;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.logfs.Out;
import com.wanniu.core.util.DateUtil;
import com.wanniu.core.util.RandomUtil;
import com.wanniu.core.util.StringUtil;
import com.wanniu.game.common.Const.CHAT_SCOPE;
import com.wanniu.game.common.Const.GOODS_CHANGE_TYPE;
import com.wanniu.game.common.Const.TipsType;
import com.wanniu.game.common.msg.MessagePush;
import com.wanniu.game.common.msg.MessageUtil;
import com.wanniu.game.data.GameData;
import com.wanniu.game.data.ext.ScheduleExt;
import com.wanniu.game.guild.GuildServiceCenter;
import com.wanniu.game.item.NormalItem;
import com.wanniu.game.item.VirtualItemType;
import com.wanniu.game.mail.MailUtil;
import com.wanniu.game.mail.SysMailConst;
import com.wanniu.game.mail.data.MailSysData;
import com.wanniu.game.mail.data.MailData.Attachment;
import com.wanniu.game.message.MessageData;
import com.wanniu.game.GWorld;
import com.wanniu.game.common.Const;
import com.wanniu.game.common.ConstsTR;
import com.wanniu.game.player.GlobalConfig;
import com.wanniu.game.player.PlayerUtil;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.poes.GuildPO;
import com.wanniu.game.poes.MessagePO;
import com.wanniu.game.poes.RedPacketCenterPO;
import com.wanniu.game.util.BlackWordUtil;
import com.wanniu.redis.PlayerPOManager;

import pomelo.area.MessageHandler.OnMessageAddPush;
import pomelo.chat.ChatHandler.OnChatPush;
import pomelo.redpacket.RedPacketHandler.FetcherInfo;
import pomelo.redpacket.RedPacketHandler.GetRedPacketListResponse;
import pomelo.redpacket.RedPacketHandler.OnRedPacketDispatchPush;
import pomelo.redpacket.RedPacketHandler.RedPacketInfo;


public class RedPacketService {
	
	private static RedPacketService redPacketService;
	
	public RedPacketCenterPO redPacketCenterPO;
	
	private ScheduleExt scheduleExt;
	private int taskId = 18;//红包活动id
	
	public static RedPacketService getInstance() {
		if (redPacketService == null) {
			redPacketService = new RedPacketService();
		}
		return redPacketService;
	}
	
	public void init() {
		scheduleExt=GameData.Schedules.get(taskId);
		
		String data = GCache.hget(Integer.toString(GWorld.__SERVER_ID), ConstsTR.RedPacket.value);
		if(data==null) {
			redPacketCenterPO=new RedPacketCenterPO();
			GCache.hset(Integer.toString(GWorld.__SERVER_ID), ConstsTR.RedPacket.value, JSON.toJSONString(redPacketCenterPO));
		}
		else {
			redPacketCenterPO=JSON.parseObject(data, RedPacketCenterPO.class);
		}
		
		
		//服务器重启之后要清理过期红包
		List<RedPacket> rightToRemove=new LinkedList<>();
		
		synchronized (redPacketCenterPO) {
			for (RedPacket redPacket : redPacketCenterPO.redPackets.values()) {
				long leftMili=redPacket.dispatchDate.getTime()+GlobalConfig.Red_HongbaoTime*1000*60-System.currentTimeMillis();
				if(leftMili<=0) {
					rightToRemove.add(redPacket);
					
					int fetchedNum=0;
					for (RedPacketFetchInfo redPacketFetchInfo : redPacket.redPacketFetchInfoList) {
						if(redPacketFetchInfo.fetcherId==null) {
							fetchedNum+=redPacketFetchInfo.num;
						}
					}
					if(redPacket.benifitType==1) {
						fetchedNum=fetchedNum/GlobalConfig.Red_HongbaoRatio;
					}
					if(fetchedNum>0) {
						MailSysData mailData = new MailSysData(SysMailConst.RedPackBack);
						List<Attachment> list_attach = new ArrayList<>();
						Attachment attachment = new Attachment();
						attachment.itemCode = VirtualItemType.DIAMOND.getItemcode();
						attachment.itemNum = fetchedNum;
						list_attach.add(attachment);
						mailData.attachments = list_attach;
						MailUtil.getInstance().sendMailToOnePlayer(redPacket.providerId, mailData, GOODS_CHANGE_TYPE.RedPacket);
					}
					
				}
				else {
					final String packetId=redPacket.id;
					JobFactory.addDelayJob(()->{
						synchronized (redPacketCenterPO) {
							redPacketCenterPO.redPackets.remove(packetId);
						}
						OnRedPacketDispatchPush.Builder pushBuilder=OnRedPacketDispatchPush.newBuilder();
						pushBuilder.setS2CCode(PomeloRequest.OK);
						pushBuilder.setId(packetId);
						GWorld.getInstance().broadcast(new MessagePush("redpacket.redPacketPush.onRedPacketDispatchPush", pushBuilder.build()), GWorld.__SERVER_ID);
					
						
						if(redPacket.providerId!=null) {
							int fetchedNum=0;
							for (RedPacketFetchInfo redPacketFetchInfo : redPacket.redPacketFetchInfoList) {
								fetchedNum+=redPacketFetchInfo.num;
							}
							if(redPacket.benifitType==1) {
								fetchedNum=fetchedNum/GlobalConfig.Red_HongbaoRatio;
							}
							MailSysData mailData = new MailSysData(SysMailConst.RedPackBack);
							List<Attachment> list_attach = new ArrayList<>();
							Attachment attachment = new Attachment();
							attachment.itemCode = VirtualItemType.DIAMOND.getItemcode();
							attachment.itemNum = fetchedNum;
							list_attach.add(attachment);
							mailData.attachments = list_attach;
							MailUtil.getInstance().sendMailToOnePlayer(redPacket.providerId, mailData, GOODS_CHANGE_TYPE.RedPacket);
						}
						
					}, leftMili);
				}
			}
			
			
			for (RedPacket redPacket : rightToRemove) {
//				Out.error("clear:",redPacket.id,",",redPacket.providerName,",",redPacket.dispatchDate);
				redPacketCenterPO.redPackets.remove(redPacket.id);
			}
			
			GCache.hset(Integer.toString(GWorld.__SERVER_ID), ConstsTR.RedPacket.value, JSON.toJSONString(redPacketCenterPO));
		}
		
		//定时发红包
		Map<LocalTime, Integer> slot = new HashMap<>();
		String[] slotStrs=GlobalConfig.Red_Time_Slot.split(";");
		for (String slotSubStr : slotStrs) {
			String[] slotSubStrs=slotSubStr.split(",");
			LocalTime localTime = LocalTime.parse(slotSubStrs[0]);
			int count = Integer.parseInt(slotSubStrs[1]);
			slot.put(localTime, count);
		}
		
		for (Map.Entry<LocalTime, Integer> entry : slot.entrySet()) {
			final LocalTime startTime=entry.getKey();
			final LocalDate startDate=LocalDate.now();
			LocalDateTime baseStartDateTime=LocalDateTime.of(startDate, startTime);
			final int repeatCount = entry.getValue();
			for(long i=0;i<repeatCount;i++) {
				LocalDateTime startDateTime=baseStartDateTime.plusSeconds(i*GlobalConfig.Red_Time_Space);

				if(LocalDateTime.now().isAfter(startDateTime)) {
					startDateTime=startDateTime.plusDays(1);
				}
				Duration duration=Duration.between(LocalDateTime.now(),startDateTime);
				final long timeOffset=duration.toMillis();
				JobFactory.addFixedRateJob(()->{
					String msg="";
					int msgCount=GameData.RedPackages.size();
					if(msgCount>=1) {
						int randomMsgId=RandomUtil.getInt(1, msgCount);
						msg=GameData.RedPackages.get(randomMsgId).words;
					}
					dispatchRedPacket(null, GlobalConfig.God_Red_Money, GlobalConfig.Red_Time_Number, 0, 1, 1, msg);
				},timeOffset,Const.Time.Day.getValue());
			}
		}
		
	}
		
	
	/**
	 * 获取当前所有红包
	 * @param playerId
	 * @return
	 */
	public GetRedPacketListResponse.Builder getAllRedPackets(String playerId) {
		GetRedPacketListResponse.Builder builder = GetRedPacketListResponse.newBuilder();

		GuildPO guildPO=GuildServiceCenter.getInstance().getGuildByMemberId(playerId);
		
		synchronized (redPacketCenterPO) {
			for (RedPacket redPacket : redPacketCenterPO.redPackets.values()) {
//				List<RedPacketTypeInfo.Builder> list=builder.getRedPacketTypeInfoBuilderList();
//				RedPacketTypeInfo.Builder selectedRedPacketTypeInfoBuilder=null;
//				for (RedPacketTypeInfo.Builder redPacketTypeInfoBuilder : list) {
//					if(redPacketTypeInfoBuilder.getChannelType()==redPacket.channelType) {
//						selectedRedPacketTypeInfoBuilder=redPacketTypeInfoBuilder;
//					}
//				}
//				if(selectedRedPacketTypeInfoBuilder==null) {
//					RedPacketTypeInfo.Builder redPacketTypeInfoBuilder=RedPacketTypeInfo.newBuilder();
//					redPacketTypeInfoBuilder.setChannelType(redPacket.channelType);
//					builder.addRedPacketTypeInfo(redPacketTypeInfoBuilder);
//					
//					selectedRedPacketTypeInfoBuilder=redPacketTypeInfoBuilder;
//				}
				if(redPacket.providerGuildId!=null) {
					if(guildPO==null) {
						continue;
					}
					if(!redPacket.providerGuildId.equals(guildPO.id)) {
						continue;
					}
				}
				
				RedPacketInfo.Builder redPacketInfoBuilder=getRedPacketInfo(redPacket,playerId);
				
				if(redPacketInfoBuilder!=null) {
					builder.addRedPacketInfo(redPacketInfoBuilder);
				}
			}
		}
		
		
		builder.setS2CCode(PomeloRequest.OK);
		return builder;
	}
	
	//获取红包领取状态  0我未领取 1我领取过 2已被领光 
//	private int getState(RedPacket redPacket,String playerId) {
//		
//		int unpackedCount=0;
//		for (RedPacketFetchInfo redPacketFetchInfo : redPacket.redPacketFetchInfoList) {
//			if(redPacketFetchInfo.fetcherId==null) {
//				unpackedCount++;
//			}
//			else if(playerId.equals(redPacketFetchInfo.fetcherId)) {
//				return 1;
//			}
//		}
//		if(unpackedCount==0) {
//			return 2;
//		}
//		return 0;
//		
//	}
	
	/**
	 * 获取红包摘要
	 * @param redPacket
	 * @return
	 */
	private RedPacketInfo.Builder getRedPacketInfo(RedPacket redPacket,String playerId){
		//如果是针对于某个玩家来获取红包信息，要验证一下该玩家是否有资格获取信息
		if(playerId!=null) {
			GuildPO guildPO=GuildServiceCenter.getInstance().getGuildByMemberId(playerId);
			
			if(redPacket.providerGuildId!=null) {
				if(guildPO==null) {
					return null;
				}
				if(!redPacket.providerGuildId.equals(guildPO.id)) {
					return null;
				}
			}
		}
		
		
		RedPacketInfo.Builder builder=RedPacketInfo.newBuilder();
		builder.setId(redPacket.id);
		builder.setCount(redPacket.count);
		builder.setTotalNum(redPacket.totalNum);
		builder.setChannelType(redPacket.providerGuildId==null?0:1);
		builder.setBenifitType(redPacket.benifitType);
		builder.setFetchType(redPacket.fetchType);
//		builder.setState(getState(redPacket,playerId));
		if(redPacket.providerId!=null) {
			builder.setProviderId(redPacket.providerId);
		}
		
		builder.setProviderName(redPacket.providerName);
		builder.setMessage(redPacket.msg);
		builder.setDispatchTimestamp(redPacket.dispatchDate.getTime());
		
//		List<FetcherInfo.Builder> fetcherInfoBuilderList=builder.getFetcherListBuilderList();
		for (RedPacketFetchInfo redPacketFetchInfo : redPacket.redPacketFetchInfoList) {
			if(redPacketFetchInfo.fetcherId!=null) {
				FetcherInfo.Builder fetcherInfoBuilder=FetcherInfo.newBuilder();
				fetcherInfoBuilder.setFetcherId(redPacketFetchInfo.fetcherId);
				fetcherInfoBuilder.setFetcherName(redPacketFetchInfo.fetcherName);
				fetcherInfoBuilder.setFetcherValue(redPacketFetchInfo.num);
				
				builder.addFetcherList(fetcherInfoBuilder);
			}
		}
		
		
		
		return builder;
	}
	
	/**
	 * 获取红包详情
	 * @param redPacketId
	 * @param playerId
	 * @return
	 */
//	public GetRedPacketDetailResponse.Builder getRedPacketDetail(String redPacketId,String playerId){
//		GetRedPacketDetailResponse.Builder builder=GetRedPacketDetailResponse.newBuilder();
//		
//		
//		
//		synchronized (redPacketCenterPO) {
//			RedPacket redPacket=redPacketCenterPO.redPackets.get(redPacketId);
//			
//			if(redPacket==null) {
//				return null;
//			}
//
//			
//			builder.setRedPacketInfo(getRedPacketInfo(redPacket,playerId));
//			List<FetcherInfo.Builder> fetcherInfoBuilderList=builder.getFetcherListBuilderList();
//			for (RedPacketFetchInfo redPacketFetchInfo : redPacket.redPacketFetchInfoList) {
//				if(redPacketFetchInfo.fetcherId!=null) {
//					FetcherInfo.Builder fetcherInfoBuilder=FetcherInfo.newBuilder();
//					fetcherInfoBuilder.setFetcherId(redPacketFetchInfo.fetcherId);
//					fetcherInfoBuilder.setFetcherName(redPacketFetchInfo.fetcherName);
//					fetcherInfoBuilder.setFetcherValue(redPacketFetchInfo.num);
//					
//					fetcherInfoBuilderList.add(fetcherInfoBuilder);
//				}
//			}
//			
//			//按金额排序
//			if(fetcherInfoBuilderList.size()>0) {
//				fetcherInfoBuilderList.sort((FetcherInfo.Builder a,FetcherInfo.Builder b)->{
//					return a.getFetcherValue()-b.getFetcherValue();
//				});
//				builder.setMaxPlayerId(fetcherInfoBuilderList.get(fetcherInfoBuilderList.size()-1).getFetcherId());
//				builder.setMinPlayerId(fetcherInfoBuilderList.get(0).getFetcherId());
//				
//			}
//			
//			//按时间排序
////			fetcherInfoBuilderList.sort((FetcherInfo.Builder a,FetcherInfo.Builder b)->{
////				RedPacketFetchInfo redPacketFetchInfo_a = null;
////				RedPacketFetchInfo redPacketFetchInfo_b = null;
////				for (RedPacketFetchInfo redPacketFetchInfo : redPacket.redPacketFetchInfoList) {
////					if(redPacketFetchInfo_a.fetcherId.equals(redPacketFetchInfo.fetcherId)) {
////						redPacketFetchInfo_a=redPacketFetchInfo;
////					}
////					if(redPacketFetchInfo_b.fetcherId.equals(redPacketFetchInfo.fetcherId)) {
////						redPacketFetchInfo_b=redPacketFetchInfo;
////					}
////				}
////				if(redPacketFetchInfo_a==null||redPacketFetchInfo_b==null) {
////					Out.error("红包获得者列表中找不到待比较的对象！");
////					return 0;
////				}
////				return redPacketFetchInfo_a.fetchDate.compareTo(redPacketFetchInfo_b.fetchDate);
////			});
//			
//			
//		}
//		
//		
//		
//		
//		
//		
//		return builder;
//	}
	
	/**
	 * 发红包
	 * @param playerId
	 * @param totalNum
	 * @param count
	 * @param channelType
	 * @param fetchType
	 * @param benifitType
	 * @param msg
	 * @return
	 */
	public int dispatchRedPacket(String playerId, int totalNum,int count,int channelType,
			int fetchType,int benifitType,String msg) {
		WNPlayer player=null;
		GuildPO guildPO=null;
		RedPacket redPacket=null;
		
		//玩家发的红包
		if(playerId!=null) {
			player=PlayerUtil.getOnlinePlayer(playerId);
			guildPO=GuildServiceCenter.getInstance().getGuildByMemberId(playerId);
			
			if(totalNum<GlobalConfig.Red_DiamondNumber_Down||totalNum>GlobalConfig.Red_DiamondNumber_Up) {
				//红包总额过大或过小
				return 1;
			}
//			if(count<GlobalConfig.Red_HongbaoNumber_Down||count>GlobalConfig.Red_HongbaoNumber_Up) {
//				//红包数量过大或过小
//				return 2;
//			}
			if(player.getLevel()<GlobalConfig.Red_SendLevel) {
				//等级不够
				return 3;
			}
			if(!player.moneyManager.enoughDiamond(totalNum)) {
				//元宝不足
				return 4;
			}
			if(channelType==1&&guildPO==null) {
				//是工会红包，但是发送者没加入公会
				return 5;
			}
			
			player.moneyManager.costDiamond(totalNum, GOODS_CHANGE_TYPE.RedPacket);
			
			
			msg=BlackWordUtil.replaceBlackString(msg);
			
			
			if(channelType==0) {
				redPacket=new RedPacket(playerId,player.getName(),null,
						totalNum,count,fetchType,benifitType,msg);
			}
			else {
				redPacket=new RedPacket(playerId,player.getName(),guildPO.id,
						totalNum,count,fetchType,benifitType,msg);
			}
		}
		
		//系统发的红包
		else {
			redPacket=new RedPacket(null,LangService.getValue("RED_PACKET_SYS_NAME"),null,totalNum,count,fetchType,benifitType,msg);
		}
		

		synchronized (redPacketCenterPO) {
			redPacketCenterPO.redPackets.put(redPacket.id, redPacket);
			GCache.hset(Integer.toString(GWorld.__SERVER_ID), ConstsTR.RedPacket.value, JSON.toJSONString(redPacketCenterPO));
		}
		
		
		final String packetId=redPacket.id;
		final RedPacket tempRedpacket=redPacket;
		JobFactory.addDelayJob(()->{
			synchronized (redPacketCenterPO) {
				redPacketCenterPO.redPackets.remove(packetId);
			}
			OnRedPacketDispatchPush.Builder pushBuilder=OnRedPacketDispatchPush.newBuilder();
			pushBuilder.setS2CCode(PomeloRequest.OK);
			pushBuilder.setId(packetId);
			GWorld.getInstance().broadcast(new MessagePush("redpacket.redPacketPush.onRedPacketDispatchPush", pushBuilder.build()), GWorld.__SERVER_ID);
		
			
			if(tempRedpacket.providerId!=null) {
				int fetchedNum=0;
				for (RedPacketFetchInfo redPacketFetchInfo : tempRedpacket.redPacketFetchInfoList) {
					if(redPacketFetchInfo.fetcherId==null) {
						fetchedNum+=redPacketFetchInfo.num;
					}
					
				}
				if(tempRedpacket.benifitType==1) {
					fetchedNum=fetchedNum/GlobalConfig.Red_HongbaoRatio;
				}
				if(fetchedNum>0) {
					MailSysData mailData = new MailSysData(SysMailConst.RedPackBack);
					List<Attachment> list_attach = new ArrayList<>();
					Attachment attachment = new Attachment();
					attachment.itemCode = VirtualItemType.DIAMOND.getItemcode();
					attachment.itemNum = fetchedNum;
					list_attach.add(attachment);
					mailData.attachments = list_attach;
					MailUtil.getInstance().sendMailToOnePlayer(tempRedpacket.providerId, mailData, GOODS_CHANGE_TYPE.RedPacket);
				}
			}
			
			
			
		}, GlobalConfig.Red_HongbaoTime*1000*60);
		
		
		//红包builder
		OnRedPacketDispatchPush.Builder pushBuilder=OnRedPacketDispatchPush.newBuilder();
		pushBuilder.setS2CCode(PomeloRequest.OK);
		RedPacketInfo.Builder redPacketInfoBuilder=getRedPacketInfo(redPacket,playerId);
		pushBuilder.setRedPacketInfo(redPacketInfoBuilder);
//		GWorld.getInstance().broadcast(new MessagePush("redpacket.redPacketPush.onRedPacketDispatchPush", pushBuilder.build()), GWorld.__SERVER_ID);
		
		
		//闪烁提示builder
		MessagePO opts = new MessagePO();
		opts.createPlayerId = "";
		opts.id = "";
		opts.strMsg = null;
		opts.createTime = new Date();
		opts.messageType = Const.MESSAGE_TYPE.redpacket.getValue();
		opts.data = null;
		MessageData message = new MessageData(opts);
		OnMessageAddPush.Builder onMessageAddPushBuilder = OnMessageAddPush.newBuilder();
		onMessageAddPushBuilder.setS2CCode(PomeloRequest.OK);
		onMessageAddPushBuilder.setS2CData(message.toJson4PayLoad());
//		GWorld.getInstance().broadcast(new MessagePush("area.messagePush.onMessageAddPush", onMessageAddPushBuilder.build()), GWorld.__SERVER_ID);

		//聊天builder
		String chatMsg=LangService.format("RED_PACKET_DISPATCH_MSG",redPacket.providerName);
		if(redPacket.providerId==null) {
			chatMsg=LangService.getValue("RED_PACKET_SYS_MSG");
		}
		OnChatPush.Builder chatPushBuilder = OnChatPush.newBuilder();
		chatPushBuilder.setS2CPlayerId("");
		chatPushBuilder.setS2CUid("");
		chatPushBuilder.setS2CContent(chatMsg);
		chatPushBuilder.setS2CSys(1);
		chatPushBuilder.setS2CTime(DateUtil.getDateTime());
		chatPushBuilder.setS2CServerData("{}");
		if(redPacket.providerGuildId==null) {
			chatPushBuilder.setS2CScope(CHAT_SCOPE.WORLD.getValue());
		}
		else {
			chatPushBuilder.setS2CScope(CHAT_SCOPE.GUILD.getValue());
		}
		
		
		for (GPlayer gplayer : GGame.getInstance().getOnlinePlayers().values()) {
			GuildPO gguildPO=GuildServiceCenter.getInstance().getGuildByMemberId(gplayer.getId());
			if(redPacket.providerGuildId!=null) {
				if(gguildPO==null||!gguildPO.id.equals(redPacket.providerGuildId)) {
					continue;
				}
			}
			gplayer.receive(new MessagePush("redpacket.redPacketPush.onRedPacketDispatchPush", pushBuilder.build()));
			gplayer.receive(new MessagePush("area.messagePush.onMessageAddPush", onMessageAddPushBuilder.build()));
			gplayer.receive(new MessagePush("chat.chatPush.onChatPush", chatPushBuilder.build()));
//			GWorld.getInstance().broadcast(new MessagePush("chat.chatPush.onChatPush", chatMsg.build()), GGame.__SERVER_ID);
			
			
		}
		
		
		
//		OnChatPush.Builder push = OnChatPush.newBuilder();
//		push.setS2CContent(text);
//		push.setS2CPlayerId(player.getId());
//		push.setS2CServerData(serverData);
//		push.setS2CScope(scope);
//		push.setS2CUid(pak.getUid());
//		push.setS2CTime(chatTime);
//		push.setS2CIndex(0);
//		push.setS2CSys(sys);
//		PomeloPush chatPush = new PomeloPush() {
//			@Override
//			protected void write() throws IOException {
//				body.writeBytes(pushBuilder.build().toByteArray());
//			}
//
//			@Override
//			public String getRoute() {
//				return "chat.chatPush.onChatPush";
//			}
//		};
//		GWorld.getInstance().broadcast(chatPush, GWorld.__SERVER_ID);
		
		Out.info("玩家id:",playerId,",发布了红包id:",redPacket.id,",totalNum:",redPacket.totalNum
				,",count:",redPacket.count,",channelType:",channelType
				,",fetchType:",redPacket.fetchType,",benifitType:",redPacket.benifitType);
		
		return 0;
	}
	
	/**
	 * 抢红包
	 * @param playerId
	 * @param redPacketId
	 * @return
	 */
	public int fetchRedPacket(String playerId,String redPacketId) {
		GuildPO guildPO=GuildServiceCenter.getInstance().getGuildByMemberId(playerId);
		WNPlayer player=PlayerUtil.getOnlinePlayer(playerId);
		RedPacket redPacket=null;
		RedPacketFetchInfo selectedFetchInfo=null;
		int selectedPos=0;
		synchronized (redPacketCenterPO) {
			redPacket=redPacketCenterPO.redPackets.get(redPacketId);
			if(redPacket==null) {
//				for (RedPacket redPacket2 : redPacketCenterPO.redPackets.values()) {
//					Out.error(redPacket2.id,",,",redPacket2.providerName);
//				}
//				Out.error("---------");
//				Out.error(redPacketCenterPO.redPackets);
				//红包不存在
				return -1;
			}
			List<RedPacketFetchInfo> canFetchList=new LinkedList<>();
			for (RedPacketFetchInfo redPacketFetchInfo : redPacket.redPacketFetchInfoList) {
				if(redPacketFetchInfo.fetcherId!=null) {
					if(redPacketFetchInfo.fetcherId.equals(playerId)) {
						//已经抢过了
						return -2;
					}
				}
				else {
					canFetchList.add(redPacketFetchInfo);
				}
			}
			if(canFetchList.size()==0) {
				//已经被抢光了
				return -3;
			}
			selectedPos=RandomUtil.random(canFetchList.size());
			selectedFetchInfo=canFetchList.get(selectedPos);
			
			
			if(player.dailyActivityMgr.getTaskInfo(taskId).process>=scheduleExt.maxAttend) {
				//已达到今日抢红包上限
				return -4;
			}
			if(player.getLevel()<GlobalConfig.Red_LootLevel) {
				//等级不够
				return -5;
			}
			if(redPacket.providerGuildId!=null) {
				if(guildPO==null) {
					//未加入公会
					return -6;
				}
				if(!redPacket.providerGuildId.equals(guildPO.id)) {
					//不属于该公会
					return -7;
				}
			}
			
			selectedFetchInfo.SetFetcher(playerId, player.getName());
			
			GCache.hset(Integer.toString(GWorld.__SERVER_ID), ConstsTR.RedPacket.value, JSON.toJSONString(redPacketCenterPO));
			
			switch (redPacket.benifitType) {
			case 0://元宝
				player.moneyManager.addDiamond(selectedFetchInfo.num, GOODS_CHANGE_TYPE.RedPacket);
				break;
			case 1://银币
				player.moneyManager.addGold(selectedFetchInfo.num, GOODS_CHANGE_TYPE.RedPacket);
				break;

			default:
				Out.error("参数错误");
				return -8;
			}
		}
		
		player.dailyActivityMgr.onEvent(Const.DailyType.RED_PACKET, "0", 1);
		
		OnRedPacketDispatchPush.Builder pushBuilder=OnRedPacketDispatchPush.newBuilder();
		pushBuilder.setS2CCode(PomeloRequest.OK);
		RedPacketInfo.Builder redPacketInfoBuilder=getRedPacketInfo(redPacket,playerId);
		pushBuilder.setRedPacketInfo(redPacketInfoBuilder);
//		GWorld.getInstance().broadcast(new MessagePush("redpacket.redPacketPush.onRedPacketDispatchPush", pushBuilder.build()), GWorld.__SERVER_ID);
		
		
				
				
		
		for (GPlayer gplayer : GGame.getInstance().getOnlinePlayers().values()) {
			GuildPO gguildPO=GuildServiceCenter.getInstance().getGuildByMemberId(gplayer.getId());
			if(redPacket.providerGuildId!=null) {
				if(gguildPO==null||!gguildPO.id.equals(redPacket.providerGuildId)) {
					continue;
				}
			}
			
			gplayer.receive(new MessagePush("redpacket.redPacketPush.onRedPacketDispatchPush", pushBuilder.build()));

			
			

			if(redPacket.fetchType==0) {
				OnChatPush.Builder chatPushBuilder = OnChatPush.newBuilder();
				if(redPacket.providerGuildId==null) {
					chatPushBuilder=MessageUtil.createChatMsg(player, redPacket.msg, CHAT_SCOPE.WORLD, TipsType.ROLL);
				}
				else {
					chatPushBuilder=MessageUtil.createChatMsg(player, redPacket.msg, CHAT_SCOPE.GUILD, TipsType.ROLL);
				}
				
				gplayer.receive(new MessagePush("chat.chatPush.onChatPush", chatPushBuilder.build()));
			}
	
			
		}
		
//		player.playerAttachPO.fetchRedPacketCount++;
		
		Out.info("玩家id:",playerId,",获得了红包id:",redPacket.id,",totalNum:",redPacket.totalNum,",count:"
				,redPacket.count,",红包位置id:",selectedPos
				,",fetchType:",redPacket.fetchType,",benifitType:",redPacket.benifitType);
		
//		Out.info("玩家id:",playerId,"获得了红包,红包id:",redPacket.id,",红包位置id:",selectedFetchInfo.id,",红包收益种类:",redPacket.benifitType);
		
		return selectedFetchInfo.num;
	}
	
	
}
